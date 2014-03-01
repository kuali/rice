/*
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.test.runners;

import org.apache.commons.beanutils.MethodUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.kuali.rice.core.api.util.ShadowingInstrumentableClassLoader;
import org.kuali.rice.test.MethodAware;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.junit.internal.runners.rules.RuleFieldValidator.*;

/**
 * A JUnit test {@link org.junit.runner.Runner} which uses a custom classloader with a copy of the classpath and allows
 * for transformers to be added to the ClassLoader for load-time weaving.
 *
 * <p>Useful when writing tests that use JPA with EclipseLink since it depends upon load-time weaving.</p>
 *
 * <p>In order to use this class, you must have a {@link BootstrapTest} annotation available somewhere in the hierarchy
 * of your test class (usually on the same class where the {@link RunWith} annotation is specified which references this
 * runner class). This informs the runner about a test that it can run to execute any one-time initialization for
 * the test suite. Ideally, this bootstrap test will execute code which loads JPA persistence units and any associated
 * ClassFileTransformers for load-time weaving. This is necessary because it is common for an integration test to have
 * references in the test class itself to JPA entities which need to be weaved. When this occurs, if the persistence
 * units and ClassFileTransformers are not properly loaded before the entity classes are loaded by the classloader, then
 * instrumentation will (silently!) fail to occur.</p>
 *
 * <p>Much of the code in this class was copied from the JUnit ParentRunner, BlockJUnit4ClassRunner, and
 * TomcatInstrumentableClassLoader.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LoadTimeWeavableTestRunner extends Runner implements Filterable, Sortable {

    private static final String[] JUNIT_CLASSLOADER_EXCLUDES = { "org.junit.", "junit.framework." };

    private final TestClass originalTestClass;
    private TestClass fTestClass;
    private Method currentMethod;

    // static because we only need one custom loader per JVM in which the tests are running, otherwise the memory
    // usage gets crazy!
    private static ClassLoader customLoader;

    private Sorter fSorter = Sorter.NULL;

    private List<FrameworkMethod> originalFilteredChildren = null;
    private List<FrameworkMethod> filteredChildren = null;

    private static final ThreadLocal<Boolean> runningBootstrapTest = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    private RunnerScheduler fScheduler = new RunnerScheduler() {
        public void schedule(Runnable childStatement) {
            childStatement.run();
        }
        public void finished() {
            // do nothing
        }
    };

    /**
     * Constructs a new {@code ParentRunner} that will run {@code @TestClass}
     */
    public LoadTimeWeavableTestRunner(Class<?> testClass) throws InitializationError {
        this.originalTestClass = new TestClass(testClass);
        if (LoadTimeWeavableTestRunner.customLoader == null) {
            LoadTimeWeavableTestRunner.customLoader =
                    new ShadowingInstrumentableClassLoader(testClass.getClassLoader(), JUNIT_CLASSLOADER_EXCLUDES);
        }
        validate();
    }

    private TestClass getCustomTestClass(Class<?> originalTestClass, ClassLoader customLoader) {
        try {
            Class<?> newTestClass = customLoader.loadClass(originalTestClass.getName());
            if (newTestClass == originalTestClass) {
                throw new IllegalStateException(newTestClass.getName() + " loaded from custom class loader should have been a different instance but was the same!");
            }
            return new TestClass(newTestClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to load test class from custom classloader: " + originalTestClass.getName());
        }
    }

    protected ClassLoader getCustomClassLoader() {
        return customLoader;
    }

    /**
     * Adds to {@code errors} if any method in this class is annotated with
     * {@code annotation}, but:
     * <ul>
     * <li>is not public, or
     * <li>takes parameters, or
     * <li>returns something other than void, or
     * <li>is static (given {@code isStatic is false}), or
     * <li>is not static (given {@code isStatic is true}).
     */
    protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation,
            boolean isStatic, List<Throwable> errors) {
        List<FrameworkMethod> methods = getOriginalTestClass().getAnnotatedMethods(annotation);

        for (FrameworkMethod eachTestMethod : methods) {
            eachTestMethod.validatePublicVoidNoArg(isStatic, errors);
        }
    }

    private void validateClassRules(List<Throwable> errors) {
        CLASS_RULE_VALIDATOR.validate(getOriginalTestClass(), errors);
        CLASS_RULE_METHOD_VALIDATOR.validate(getOriginalTestClass(), errors);
    }

    /**
     * Constructs a {@code Statement} to run all of the tests in the test class. Override to add pre-/post-processing.
     * Here is an outline of the implementation:
     * <ul>
     * <li>Call {@link #runChild(org.junit.runners.model.FrameworkMethod, org.junit.runner.notification.RunNotifier)} on each object returned by {@link #getChildren()} (subject to any imposed filter and sort).</li>
     * <li>ALWAYS run all non-overridden {@code @BeforeClass} methods on this class
     * and superclasses before the previous step; if any throws an
     * Exception, stop execution and pass the exception on.
     * <li>ALWAYS run all non-overridden {@code @AfterClass} methods on this class
     * and superclasses before any of the previous steps; all AfterClass methods are
     * always executed: exceptions thrown by previous steps are combined, if
     * necessary, with exceptions from AfterClass methods into a
     * {@link org.junit.runners.model.MultipleFailureException}.
     * </ul>
     *
     * @return {@code Statement}
     */
    protected Statement classBlock(final RunNotifier notifier) {
        Statement statement = childrenInvoker(notifier);
        statement = withBeforeClasses(statement);
        statement = withAfterClasses(statement);
        statement = withClassRules(statement);
        return statement;
    }

    /**
     * Returns a {@link org.junit.runners.model.Statement}: run all non-overridden {@code @BeforeClass} methods on this class
     * and superclasses before executing {@code statement}; if any throws an
     * Exception, stop execution and pass the exception on.
     */
    protected Statement withBeforeClasses(Statement statement) {
        List<FrameworkMethod> befores = getTestClass()
                .getAnnotatedMethods(BeforeClass.class);
        return befores.isEmpty() ? statement :
                new RunBefores(statement, befores, null);
    }

    /**
     * Returns a {@link org.junit.runners.model.Statement}: run all non-overridden {@code @AfterClass} methods on this class
     * and superclasses before executing {@code statement}; all AfterClass methods are
     * always executed: exceptions thrown by previous steps are combined, if
     * necessary, with exceptions from AfterClass methods into a
     * {@link org.junit.runners.model.MultipleFailureException}.
     */
    protected Statement withAfterClasses(Statement statement) {
        List<FrameworkMethod> afters = getTestClass()
                .getAnnotatedMethods(AfterClass.class);
        return afters.isEmpty() ? statement :
                new RunAfters(statement, afters, null);
    }

    /**
     * Returns a {@link org.junit.runners.model.Statement}: apply all
     * static fields assignable to {@link org.junit.rules.TestRule}
     * annotated with {@link org.junit.ClassRule}.
     *
     * @param statement the base statement
     * @return a RunRules statement if any class-level {@link org.junit.Rule}s are
     *         found, or the base statement
     */
    private Statement withClassRules(Statement statement) {
        List<TestRule> classRules = classRules();
        return classRules.isEmpty() ? statement :
                new RunRules(statement, classRules, getDescription());
    }

    /**
     * @return the {@code ClassRule}s that can transform the block that runs
     *         each method in the tested class.
     */
    protected List<TestRule> classRules() {
        List<TestRule> result = getTestClass().getAnnotatedMethodValues(null, ClassRule.class, TestRule.class);

        result.addAll(getTestClass().getAnnotatedFieldValues(null, ClassRule.class, TestRule.class));

        return result;
    }

    /**
     * Returns a {@link org.junit.runners.model.Statement}: Call {@link #runChild(org.junit.runners.model.FrameworkMethod, org.junit.runner.notification.RunNotifier)}
     * on each object returned by {@link #getChildren()} (subject to any imposed
     * filter and sort)
     */
    protected Statement childrenInvoker(final RunNotifier notifier) {
        return new Statement() {
            @Override
            public void evaluate() {
                runChildren(notifier);
            }
        };
    }

    private void runChildren(final RunNotifier notifier) {
        for (final FrameworkMethod each : getFilteredChildren()) {
            fScheduler.schedule(new Runnable() {
                public void run() {
                    LoadTimeWeavableTestRunner.this.runChild(each, notifier);
                }
            });
        }
        fScheduler.finished();
    }

    /**
     * Returns a name used to describe this Runner
     */
    protected String getName() {
        return getOriginalTestClass().getName();
    }

    /**
     * Returns a {@link org.junit.runners.model.TestClass} object wrapping the class to be executed.
     */
    public final TestClass getTestClass() {
        if (fTestClass == null) {
            throw new IllegalStateException("Attempted to access test class but it has not yet been initialized!");
        }
        return fTestClass;
    }

    /**
     * Returns the original test class that was passed to this test runner.
     */
    public final TestClass getOriginalTestClass() {
        return originalTestClass;
    }

    /**
     * Runs a {@link org.junit.runners.model.Statement} that represents a leaf (aka atomic) test.
     */
    protected final void runLeaf(Statement statement, Description description,
            RunNotifier notifier) {
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();
        try {
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            eachNotifier.addFailedAssumption(e);
        } catch (Throwable e) {
            eachNotifier.addFailure(e);
        } finally {
            eachNotifier.fireTestFinished();
        }
    }

    /**
     * @return the annotations that should be attached to this runner's
     *         description.
     */
    protected Annotation[] getRunnerAnnotations() {
        return getOriginalTestClass().getAnnotations();
    }

    //
    // Implementation of Runner
    //

    @Override
    public Description getDescription() {
        Description description = Description.createSuiteDescription(getName(),
                getRunnerAnnotations());
        for (FrameworkMethod child : getOriginalFilteredChildren()) {
            description.addChild(describeOriginalChild(child));
        }
        return description;
    }

    @Override
    public void run(final RunNotifier notifier) {
        ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(customLoader);
        try {
            if (runBootstrapTest(notifier, getOriginalTestClass())) {
                this.fTestClass = getCustomTestClass(getOriginalTestClass().getJavaClass(), customLoader);
                EachTestNotifier testNotifier = new EachTestNotifier(notifier, getDescription());
                try {
                    Statement statement = classBlock(notifier);
                    statement.evaluate();
                } catch (AssumptionViolatedException e) {
                    testNotifier.fireTestIgnored();
                } catch (StoppedByUserException e) {
                    throw e;
                } catch (Throwable e) {
                    testNotifier.addFailure(e);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(currentContextClassLoader);
        }
    }

    protected boolean runBootstrapTest(RunNotifier notifier, TestClass testClass) {
        if (!runningBootstrapTest.get().booleanValue()) {
            runningBootstrapTest.set(Boolean.TRUE);
            try {
                BootstrapTest bootstrapTest = getBootstrapTestAnnotation(testClass.getJavaClass());
                if (bootstrapTest != null) {
                    Result result = JUnitCore.runClasses(bootstrapTest.value());
                    List<Failure> failures = result.getFailures();
                    for (Failure failure : failures) {
                        notifier.fireTestFailure(failure);
                    }
                    return result.getFailureCount() == 0;
                } else {
                    throw new IllegalStateException("LoadTimeWeavableTestRunner, must be coupled with an @BootstrapTest annotation to define the bootstrap test to execute.");
                }
            } finally {
                runningBootstrapTest.set(Boolean.FALSE);
            }
        }
        return true;
    }

    private BootstrapTest getBootstrapTestAnnotation(Class<?> testClass) {
        BootstrapTest bootstrapTest = testClass.getAnnotation(BootstrapTest.class);
        if (bootstrapTest != null) {
            return bootstrapTest;
        } else if (testClass.getSuperclass() != null) {
            return getBootstrapTestAnnotation(testClass.getSuperclass());
        } else {
            return null;
        }
    }

    //
    // Implementation of Filterable and Sortable
    //

    public void filter(Filter filter) throws NoTestsRemainException {
        for (Iterator<FrameworkMethod> iter = getOriginalFilteredChildren().iterator(); iter.hasNext(); ) {
            FrameworkMethod each = iter.next();
            if (shouldRun(filter, each)) {
                try {
                    filter.apply(each);
                } catch (NoTestsRemainException e) {
                    iter.remove();
                }
            } else {
                iter.remove();
            }
        }
        if (getOriginalFilteredChildren().isEmpty()) {
            throw new NoTestsRemainException();
        }
    }

    public void sort(Sorter sorter) {
        fSorter = sorter;
        for (FrameworkMethod each : getOriginalFilteredChildren()) {
            sortChild(each);
        }
        Collections.sort(getOriginalFilteredChildren(), comparator());
    }

    //
    // Private implementation
    //

    private void validate() throws InitializationError {
        List<Throwable> errors = new ArrayList<Throwable>();
        collectInitializationErrors(errors);
        if (!errors.isEmpty()) {
            throw new InitializationError(errors);
        }
    }

    private List<FrameworkMethod> getOriginalFilteredChildren() {
        if (originalFilteredChildren == null) {
            originalFilteredChildren = new ArrayList<FrameworkMethod>(getOriginalChildren());
        }
        return originalFilteredChildren;
    }

    private List<FrameworkMethod> getFilteredChildren() {
        if (getOriginalFilteredChildren() == null) {
            throw new IllegalStateException("Attempted to get filtered children before original filtered children were initialized.");
        }
        if (filteredChildren == null) {
            filteredChildren = new ArrayList<FrameworkMethod>();
            List<FrameworkMethod> testMethods = computeTestMethods();
            for (FrameworkMethod originalMethod : getOriginalFilteredChildren()) {
                for (FrameworkMethod testMethod : testMethods) {
                    if (originalMethod.isShadowedBy(testMethod)) {
                        filteredChildren.add(testMethod);
                    }
                }
            }
        }
        return filteredChildren;
    }

    private void sortChild(FrameworkMethod child) {
        fSorter.apply(child);
    }

    private boolean shouldRun(Filter filter, FrameworkMethod each) {
        return filter.shouldRun(describeOriginalChild(each));
    }

    private Comparator<? super FrameworkMethod> comparator() {
        return new Comparator<FrameworkMethod>() {
            public int compare(FrameworkMethod o1, FrameworkMethod o2) {
                return fSorter.compare(describeChild(o1), describeChild(o2));
            }
        };
    }

    //
    // Implementation of ParentRunner
    //

    /**
     * Runs the test corresponding to {@code child}, which can be assumed to be
     * an element of the list returned by {@link #getChildren()}.
     * Subclasses are responsible for making sure that relevant test events are
     * reported through {@code notifier}
     */
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        this.currentMethod = method.getMethod();
        try {
            Description description = describeChild(method);
            if (method.getAnnotation(Ignore.class) != null) {
                notifier.fireTestIgnored(description);
            } else {
                runLeaf(methodBlock(method), description, notifier);
            }
        } finally {
            this.currentMethod = null;
        }
    }

    /**
     * Returns a {@link org.junit.runner.Description} for {@code child}, which can be assumed to
     * be an element of the list returned by {@link #getChildren()}
     */
    protected Description describeChild(FrameworkMethod method) {
        return Description.createTestDescription(getTestClass().getJavaClass(),
                testName(method), method.getAnnotations());
    }

    protected Description describeOriginalChild(FrameworkMethod method) {
        return Description.createTestDescription(getOriginalTestClass().getJavaClass(),
                testName(method), method.getAnnotations());
    }

    /**
     * Returns a list of objects that define the children of this Runner.
     */
    protected List<FrameworkMethod> getChildren() {
        return computeTestMethods();
    }

    protected List<FrameworkMethod> getOriginalChildren() {
        return computeOriginalTestMethods();
    }

    //
    // Override in subclasses
    //

    /**
     * Returns the methods that run tests. Default implementation returns all
     * methods annotated with {@code @Test} on this class and superclasses that
     * are not overridden.
     */
    protected List<FrameworkMethod> computeTestMethods() {
        return getTestClass().getAnnotatedMethods(Test.class);
    }

    protected List<FrameworkMethod> computeOriginalTestMethods() {
        return getOriginalTestClass().getAnnotatedMethods(Test.class);
    }

    /**
     * Adds to {@code errors} a throwable for each problem noted with the test class (available from {@link #getTestClass()}).
     * Default implementation adds an error for each method annotated with
     * {@code @BeforeClass} or {@code @AfterClass} that is not
     * {@code public static void} with no arguments.
     */
    protected void collectInitializationErrors(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(BeforeClass.class, true, errors);
        validatePublicVoidNoArgMethods(AfterClass.class, true, errors);
        validateClassRules(errors);
        validateNoNonStaticInnerClass(errors);
        validateConstructor(errors);
        validateInstanceMethods(errors);
        validateFields(errors);
        validateMethods(errors);
    }

    protected void validateNoNonStaticInnerClass(List<Throwable> errors) {
        if (getOriginalTestClass().isANonStaticInnerClass()) {
            String gripe = "The inner class " + getOriginalTestClass().getName()
                    + " is not static.";
            errors.add(new Exception(gripe));
        }
    }

    /**
     * Adds to {@code errors} if the test class has more than one constructor,
     * or if the constructor takes parameters. Override if a subclass requires
     * different validation rules.
     */
    protected void validateConstructor(List<Throwable> errors) {
        validateOnlyOneConstructor(errors);
        validateZeroArgConstructor(errors);
    }

    /**
     * Adds to {@code errors} if the test class has more than one constructor
     * (do not override)
     */
    protected void validateOnlyOneConstructor(List<Throwable> errors) {
        if (!hasOneConstructor()) {
            String gripe = "Test class should have exactly one public constructor";
            errors.add(new Exception(gripe));
        }
    }

    /**
     * Adds to {@code errors} if the test class's single constructor takes
     * parameters (do not override)
     */
    protected void validateZeroArgConstructor(List<Throwable> errors) {
        if (!getOriginalTestClass().isANonStaticInnerClass()
                && hasOneConstructor()
                && (getOriginalTestClass().getOnlyConstructor().getParameterTypes().length != 0)) {
            String gripe = "Test class should have exactly one public zero-argument constructor";
            errors.add(new Exception(gripe));
        }
    }

    private boolean hasOneConstructor() {
        return getOriginalTestClass().getJavaClass().getConstructors().length == 1;
    }

    /**
     * Adds to {@code errors} for each method annotated with {@code @Test},
     * {@code @Before}, or {@code @After} that is not a public, void instance
     * method with no arguments.
     *
     * @deprecated unused API, will go away in future version
     */
    @Deprecated
    protected void validateInstanceMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
        validateTestMethods(errors);

        if (computeOriginalTestMethods().size() == 0) {
            errors.add(new Exception("No runnable methods"));
        }
    }

    protected void validateFields(List<Throwable> errors) {
        RULE_VALIDATOR.validate(getOriginalTestClass(), errors);
    }

    private void validateMethods(List<Throwable> errors) {
        RULE_METHOD_VALIDATOR.validate(getOriginalTestClass(), errors);
    }

    /**
     * Adds to {@code errors} for each method annotated with {@code @Test}that
     * is not a public, void instance method with no arguments.
     */
    protected void validateTestMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(Test.class, false, errors);
    }

    /**
     * Returns a new fixture for running a test. Default implementation executes
     * the test class's no-argument constructor (validation should have ensured
     * one exists).
     */
    protected Object createTest() throws Exception {
        Object test = getTestClass().getOnlyConstructor().newInstance();
        setTestName(test, currentMethod);
        setTestMethod(test, currentMethod);
        return test;
    }

    /**
     * Sets the {@link java.lang.reflect.Method} on the test case if it is {@link org.kuali.rice.test.MethodAware}
     * @param method the current method to be run
     * @param test the test instance
     */
    protected void setTestMethod(Object test, Method method) throws Exception {
        Class<?> methodAwareClass = Class.forName(MethodAware.class.getName(), true, getCustomClassLoader());
        if (methodAwareClass.isInstance(test)) {
            Method setTestMethod = methodAwareClass.getMethod("setTestMethod", Method.class);
            setTestMethod.invoke(test, method);
        }
    }

    protected void setTestName(final Object test, final Method testMethod) throws Exception {
        String name = testMethod == null ? "" : testMethod.getName();
        final Method setNameMethod = MethodUtils.getAccessibleMethod(test.getClass(), "setName",
                new Class[]{String.class});
        if (setNameMethod != null) {
            setNameMethod.invoke(test, name);
        }
    }

    /**
     * Returns the name that describes {@code method} for {@link org.junit.runner.Description}s.
     * Default implementation is the method's name
     */
    protected String testName(FrameworkMethod method) {
        return method.getName();
    }

    /**
     * Returns a Statement that, when executed, either returns normally if
     * {@code method} passes, or throws an exception if {@code method} fails.
     *
     * Here is an outline of the default implementation:
     *
     * <ul>
     * <li>Invoke {@code method} on the result of {@code createTest()}, and
     * throw any exceptions thrown by either operation.
     * <li>HOWEVER, if {@code method}'s {@code @Test} annotation has the {@code
     * expecting} attribute, return normally only if the previous step threw an
     * exception of the correct type, and throw an exception otherwise.
     * <li>HOWEVER, if {@code method}'s {@code @Test} annotation has the {@code
     * timeout} attribute, throw an exception if the previous step takes more
     * than the specified number of milliseconds.
     * <li>ALWAYS run all non-overridden {@code @Before} methods on this class
     * and superclasses before any of the previous steps; if any throws an
     * Exception, stop execution and pass the exception on.
     * <li>ALWAYS run all non-overridden {@code @After} methods on this class
     * and superclasses after any of the previous steps; all After methods are
     * always executed: exceptions thrown by previous steps are combined, if
     * necessary, with exceptions from After methods into a
     * {@link org.junit.runners.model.MultipleFailureException}.
     * <li>ALWAYS allow {@code @Rule} fields to modify the execution of the
     * above steps. A {@code Rule} may prevent all execution of the above steps,
     * or add additional behavior before and after, or modify thrown exceptions.
     * For more information, see {@link org.junit.rules.TestRule}
     * </ul>
     *
     * This can be overridden in subclasses, either by overriding this method,
     * or the implementations creating each sub-statement.
     */
    protected Statement methodBlock(FrameworkMethod method) {
        Object test;
        try {
            test = new ReflectiveCallable() {
                @Override
                protected Object runReflectiveCall() throws Throwable {
                    return createTest();
                }
            }.run();
        } catch (Throwable e) {
            return new Fail(e);
        }

        Statement statement = methodInvoker(method, test);
        statement = possiblyExpectingExceptions(method, test, statement);
        statement = withPotentialTimeout(method, test, statement);
        statement = withBefores(method, test, statement);
        statement = withAfters(method, test, statement);
        statement = withRules(method, test, statement);
        return statement;
    }

    //
    // Statement builders
    //

    /**
     * Returns a {@link org.junit.runners.model.Statement} that invokes {@code method} on {@code test}
     */
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        return new InvokeMethod(method, test);
    }

    /**
     * Returns a {@link org.junit.runners.model.Statement}: if {@code method}'s {@code @Test} annotation
     * has the {@code expecting} attribute, return normally only if {@code next}
     * throws an exception of the correct type, and throw an exception
     * otherwise.
     *
     * @deprecated Will be private soon: use Rules instead
     */
    @Deprecated
    protected Statement possiblyExpectingExceptions(FrameworkMethod method,
            Object test, Statement next) {
        Test annotation = method.getAnnotation(Test.class);
        return expectsException(annotation) ? new ExpectException(next,
                getExpectedException(annotation)) : next;
    }

    /**
     * Returns a {@link org.junit.runners.model.Statement}: if {@code method}'s {@code @Test} annotation
     * has the {@code timeout} attribute, throw an exception if {@code next}
     * takes more than the specified number of milliseconds.
     *
     * @deprecated Will be private soon: use Rules instead
     */
    @Deprecated
    protected Statement withPotentialTimeout(FrameworkMethod method,
            Object test, Statement next) {
        long timeout = getTimeout(method.getAnnotation(Test.class));
        return timeout > 0 ? new FailOnTimeout(next, timeout) : next;
    }

    /**
     * Returns a {@link org.junit.runners.model.Statement}: run all non-overridden {@code @Before}
     * methods on this class and superclasses before running {@code next}; if
     * any throws an Exception, stop execution and pass the exception on.
     *
     * @deprecated Will be private soon: use Rules instead
     */
    @Deprecated
    protected Statement withBefores(FrameworkMethod method, Object target,
            Statement statement) {
        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(Before.class);
        return befores.isEmpty() ? statement : new RunBefores(statement,
                befores, target);
    }

    /**
     * Returns a {@link org.junit.runners.model.Statement}: run all non-overridden {@code @After}
     * methods on this class and superclasses before running {@code next}; all
     * After methods are always executed: exceptions thrown by previous steps
     * are combined, if necessary, with exceptions from After methods into a
     * {@link org.junit.runners.model.MultipleFailureException}.
     *
     * @deprecated Will be private soon: use Rules instead
     */
    @Deprecated
    protected Statement withAfters(FrameworkMethod method, Object target,
            Statement statement) {
        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(
                After.class);
        return afters.isEmpty() ? statement : new RunAfters(statement, afters,
                target);
    }

    private Statement withRules(FrameworkMethod method, Object target,
            Statement statement) {
        List<TestRule> testRules = getTestRules(target);
        Statement result = statement;
        result = withMethodRules(method, testRules, target, result);
        result = withTestRules(method, testRules, result);

        return result;
    }

    private Statement withMethodRules(FrameworkMethod method, List<TestRule> testRules,
            Object target, Statement result) {
        for (org.junit.rules.MethodRule each : getMethodRules(target)) {
            if (!testRules.contains(each)) {
                result = each.apply(result, method, target);
            }
        }
        return result;
    }

    private List<org.junit.rules.MethodRule> getMethodRules(Object target) {
        return rules(target);
    }

    /**
     * @param target the test case instance
     * @return a list of MethodRules that should be applied when executing this
     *         test
     */
    protected List<org.junit.rules.MethodRule> rules(Object target) {
        return getTestClass().getAnnotatedFieldValues(target, Rule.class, org.junit.rules.MethodRule.class);
    }

    /**
     * Returns a {@link org.junit.runners.model.Statement}: apply all non-static value fields
     * annotated with {@link org.junit.Rule}.
     *
     * @param statement The base statement
     * @return a RunRules statement if any class-level {@link org.junit.Rule}s are
     *         found, or the base statement
     */
    private Statement withTestRules(FrameworkMethod method, List<TestRule> testRules,
            Statement statement) {
        return testRules.isEmpty() ? statement :
                new RunRules(statement, testRules, describeChild(method));
    }

    /**
     * @param target the test case instance
     * @return a list of TestRules that should be applied when executing this
     *         test
     */
    protected List<TestRule> getTestRules(Object target) {
        List<TestRule> result = getTestClass().getAnnotatedMethodValues(target,
                Rule.class, TestRule.class);

        result.addAll(getTestClass().getAnnotatedFieldValues(target,
                Rule.class, TestRule.class));

        return result;
    }

    private Class<? extends Throwable> getExpectedException(Test annotation) {
        if (annotation == null || annotation.expected() == Test.None.class) {
            return null;
        } else {
            return annotation.expected();
        }
    }

    private boolean expectsException(Test annotation) {
        return getExpectedException(annotation) != null;
    }

    private long getTimeout(Test annotation) {
        if (annotation == null) {
            return 0;
        }
        return annotation.timeout();
    }

}
