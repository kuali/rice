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
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.kuali.rice.core.api.util.collect.CollectionUtils;
import org.kuali.rice.test.MethodAware;
import org.springframework.instrument.classloading.WeavingTransformer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import static org.junit.internal.runners.rules.RuleFieldValidator.*;

/**
 * A JUnit test {@link org.junit.runner.Runner} which uses a custom classloader with a copy of the classpath and allows
 * for transformers to be added to the ClassLoader for load-time weaving. Useful when writing tests that use JPA with
 * EclipseLink since it depends upon load-time weaving.
 *
 * <p>Much of the code in this class was copied from the JUnit ParentRunner and BlockJUnit4ClassRunner.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LoadTimeWeavableTestRunner extends Runner implements Filterable, Sortable {

    private final TestClass fTestClass;
    private final URLClassLoader customLoader;
    private Method currentMethod;

    private Sorter fSorter = Sorter.NULL;

    private List<FrameworkMethod> fFilteredChildren = null;

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
        URL[] parentUrls = ((URLClassLoader)testClass.getClassLoader()).getURLs();
        this.customLoader = new JUnitCustomClassLoader(parentUrls, testClass.getClassLoader());
        this.fTestClass = getCustomTestClass(testClass, customLoader);
        validate();
    }

    private TestClass getCustomTestClass(Class<?> originalTestClass, ClassLoader customLoader) throws InitializationError {
        try {
            Class<?> newTestClass = customLoader.loadClass(originalTestClass.getName());
            if (newTestClass == originalTestClass) {
                throw new RuntimeException(newTestClass.getName() + " loaded from custom class loader should have been a different instance but was the same!");
            }
            return new TestClass(newTestClass);
        } catch (ClassNotFoundException e) {
            throw new InitializationError(e);
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
        List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(annotation);

        for (FrameworkMethod eachTestMethod : methods) {
            eachTestMethod.validatePublicVoidNoArg(isStatic, errors);
        }
    }

    private void validateClassRules(List<Throwable> errors) {
        CLASS_RULE_VALIDATOR.validate(getTestClass(), errors);
        CLASS_RULE_METHOD_VALIDATOR.validate(getTestClass(), errors);
    }

    /**
     * Constructs a {@code Statement} to run all of the tests in the test class. Override to add pre-/post-processing.
     * Here is an outline of the implementation:
     * <ul>
     * <li>Call {@link #runChild(FrameworkMethod, RunNotifier)} on each object returned by {@link #getChildren()} (subject to any imposed filter and sort).</li>
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
     * Returns a {@link Statement}: run all non-overridden {@code @BeforeClass} methods on this class
     * and superclasses before executing {@code statement}; if any throws an
     * Exception, stop execution and pass the exception on.
     */
    protected Statement withBeforeClasses(Statement statement) {
        List<FrameworkMethod> befores = fTestClass
                .getAnnotatedMethods(BeforeClass.class);
        return befores.isEmpty() ? statement :
                new RunBefores(statement, befores, null);
    }

    /**
     * Returns a {@link Statement}: run all non-overridden {@code @AfterClass} methods on this class
     * and superclasses before executing {@code statement}; all AfterClass methods are
     * always executed: exceptions thrown by previous steps are combined, if
     * necessary, with exceptions from AfterClass methods into a
     * {@link org.junit.runners.model.MultipleFailureException}.
     */
    protected Statement withAfterClasses(Statement statement) {
        List<FrameworkMethod> afters = fTestClass
                .getAnnotatedMethods(AfterClass.class);
        return afters.isEmpty() ? statement :
                new RunAfters(statement, afters, null);
    }

    /**
     * Returns a {@link Statement}: apply all
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
        List<TestRule> result = fTestClass.getAnnotatedMethodValues(null, ClassRule.class, TestRule.class);

        result.addAll(fTestClass.getAnnotatedFieldValues(null, ClassRule.class, TestRule.class));

        return result;
    }

    /**
     * Returns a {@link Statement}: Call {@link #runChild(FrameworkMethod, RunNotifier)}
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
        return fTestClass.getName();
    }

    /**
     * Returns a {@link TestClass} object wrapping the class to be executed.
     */
    public final TestClass getTestClass() {
        return fTestClass;
    }

    /**
     * Runs a {@link Statement} that represents a leaf (aka atomic) test.
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
        return fTestClass.getAnnotations();
    }

    //
    // Implementation of Runner
    //

    @Override
    public Description getDescription() {
        Description description = Description.createSuiteDescription(getName(),
                getRunnerAnnotations());
        for (FrameworkMethod child : getFilteredChildren()) {
            description.addChild(describeChild(child));
        }
        return description;
    }

    @Override
    public void run(final RunNotifier notifier) {
        Thread.currentThread().setContextClassLoader(customLoader);
        EachTestNotifier testNotifier = new EachTestNotifier(notifier,
                getDescription());
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

    //
    // Implementation of Filterable and Sortable
    //

    public void filter(Filter filter) throws NoTestsRemainException {
        for (Iterator<FrameworkMethod> iter = getFilteredChildren().iterator(); iter.hasNext(); ) {
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
        if (getFilteredChildren().isEmpty()) {
            throw new NoTestsRemainException();
        }
    }

    public void sort(Sorter sorter) {
        fSorter = sorter;
        for (FrameworkMethod each : getFilteredChildren()) {
            sortChild(each);
        }
        Collections.sort(getFilteredChildren(), comparator());
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

    private List<FrameworkMethod> getFilteredChildren() {
        if (fFilteredChildren == null) {
            fFilteredChildren = new ArrayList<FrameworkMethod>(getChildren());
        }
        return fFilteredChildren;
    }

    private void sortChild(FrameworkMethod child) {
        fSorter.apply(child);
    }

    private boolean shouldRun(Filter filter, FrameworkMethod each) {
        return filter.shouldRun(describeChild(each));
    }

    private Comparator<? super FrameworkMethod> comparator() {
        return new Comparator<FrameworkMethod>() {
            public int compare(FrameworkMethod o1, FrameworkMethod o2) {
                return fSorter.compare(describeChild(o1), describeChild(o2));
            }
        };
    }

    /**
     * Sets a scheduler that determines the order and parallelization
     * of children.  Highly experimental feature that may change.
     */
    public void setScheduler(RunnerScheduler scheduler) {
        this.fScheduler = scheduler;
    }

    //
    // Implementation of ParentRunner
    //

    /**
     * Runs the test corresponding to {@code child}, which can be assumed to be
     * an element of the list returned by {@link LoadTimeWeavableTestRunner#getChildren()}.
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
     * Returns a {@link Description} for {@code child}, which can be assumed to
     * be an element of the list returned by {@link LoadTimeWeavableTestRunner#getChildren()}
     */
    protected Description describeChild(FrameworkMethod method) {
        return Description.createTestDescription(getTestClass().getJavaClass(),
                testName(method), method.getAnnotations());
    }

    /**
     * Returns a list of objects that define the children of this Runner.
     */
    protected List<FrameworkMethod> getChildren() {
        return computeTestMethods();
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
        if (getTestClass().isANonStaticInnerClass()) {
            String gripe = "The inner class " + getTestClass().getName()
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
        if (!getTestClass().isANonStaticInnerClass()
                && hasOneConstructor()
                && (getTestClass().getOnlyConstructor().getParameterTypes().length != 0)) {
            String gripe = "Test class should have exactly one public zero-argument constructor";
            errors.add(new Exception(gripe));
        }
    }

    private boolean hasOneConstructor() {
        return getTestClass().getJavaClass().getConstructors().length == 1;
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

        if (computeTestMethods().size() == 0) {
            errors.add(new Exception("No runnable methods"));
        }
    }

    protected void validateFields(List<Throwable> errors) {
        RULE_VALIDATOR.validate(getTestClass(), errors);
    }

    private void validateMethods(List<Throwable> errors) {
        RULE_METHOD_VALIDATOR.validate(getTestClass(), errors);
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
     * Returns the name that describes {@code method} for {@link Description}s.
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
     * For more information, see {@link TestRule}
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
     * Returns a {@link Statement} that invokes {@code method} on {@code test}
     */
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        return new InvokeMethod(method, test);
    }

    /**
     * Returns a {@link Statement}: if {@code method}'s {@code @Test} annotation
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
     * Returns a {@link Statement}: if {@code method}'s {@code @Test} annotation
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
     * Returns a {@link Statement}: run all non-overridden {@code @Before}
     * methods on this class and superclasses before running {@code next}; if
     * any throws an Exception, stop execution and pass the exception on.
     *
     * @deprecated Will be private soon: use Rules instead
     */
    @Deprecated
    protected Statement withBefores(FrameworkMethod method, Object target,
            Statement statement) {
        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(
                Before.class);
        return befores.isEmpty() ? statement : new RunBefores(statement,
                befores, target);
    }

    /**
     * Returns a {@link Statement}: run all non-overridden {@code @After}
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
        return getTestClass().getAnnotatedFieldValues(target, Rule.class,
                org.junit.rules.MethodRule.class);
    }

    /**
     * Returns a {@link Statement}: apply all non-static value fields
     * annotated with {@link Rule}.
     *
     * @param statement The base statement
     * @return a RunRules statement if any class-level {@link Rule}s are
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

    public static class JUnitCustomClassLoader extends URLClassLoader {

        private static final String[] SYSTEM_CLASSES = new String[] { "java.", "javax.", "javax.", "org.xml.", "org.w3c.", "com.sun.", "sun." };
        private static final String[] DELEGATE_TO_PARENT =  new String[] { "org.junit." };

        private final WeavingTransformer weavingTransformer;

        public JUnitCustomClassLoader() {
            super(new URL[0]);
            this.weavingTransformer = new WeavingTransformer(this);
        }

        public JUnitCustomClassLoader(ClassLoader parent) {
            super(new URL[0], parent);
            this.weavingTransformer = new WeavingTransformer(this);
        }

        public JUnitCustomClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
            this.weavingTransformer = new WeavingTransformer(this);
        }

        @Override
        public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (isDelegateToParentClass(name)) {
                return loadParentClass(name, resolve);
            }
            Class<?> loadedClass = loadExistingClass(name, resolve);
            if (loadedClass != null) {
                return loadedClass;
            }
            loadedClass = loadSystemClass(name, resolve);
            if (loadedClass != null) {
                return loadedClass;
            }
            loadedClass = loadLocalClass(name, resolve);
            if (loadedClass != null) {
                return loadedClass;
            }
            loadedClass = loadParentClass(name, resolve);
            if (loadedClass != null) {
                return loadedClass;
            }
            throw new ClassNotFoundException(name);
        }

        @Override
        public URL getResource(String name) {
            URL resource = findResource(name);
            if (resource == null) {
                resource = getParent().getResource(name);
            }
            return resource;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            Enumeration<URL> localResources = findResources(name);
            Enumeration<URL> parentResources = getParent().getResources(name);
            return CollectionUtils.concat(localResources, parentResources);
        }



        private Class<?> loadExistingClass(String name, boolean resolve) {
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass != null && resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }

        private Class<?> loadSystemClass(String name, boolean resolve) {
            Class<?> loadedClass = null;
            if (isSystemClass(name)) {
                try {
                    loadedClass = getSystemClassLoader().loadClass(name);
                    if (loadedClass != null && resolve) {
                        resolveClass(loadedClass);
                    }
                } catch (ClassNotFoundException e) {
                    // not found in system class loader
                }
            }
            return loadedClass;
        }

        private Class<?> loadLocalClass(String name, boolean resolve) {
            Class<?> loadedClass = null;
            try {
                loadedClass = findClass(name);
                if (loadedClass != null && resolve) {
                    resolveClass(loadedClass);
                }
            } catch (ClassNotFoundException e) {
                // not found locally
            }
            return loadedClass;
        }

        private Class<?> loadParentClass(String name, boolean resolve) {
            Class<?> loadedClass = null;
            try {
                loadedClass = getParent().loadClass(name);
                if (loadedClass != null && resolve) {
                    resolveClass(loadedClass);
                }
            } catch (ClassNotFoundException e) {
                // not found in parent
            }
            return loadedClass;
        }

        /**
         * This method modeled on the isSystemPath method in Jetty's ContextLoader.
         *
         * When loading classes from the system classloader, we really only want to load certain classes
         * from there so this will tell us whether or not the class name given is one we want to load
         * from the system classloader.
         */
        private boolean isSystemClass(String name) {
            name = name.replace('/','.');
            while(name.startsWith(".")) {
                name=name.substring(1);
            }
            for (int index = 0; index < SYSTEM_CLASSES.length; index++) {
                String systemClass = SYSTEM_CLASSES[index];
                if (systemClass.endsWith(".")) {
                    if (name.startsWith(systemClass)) {
                        return true;
                    }
                }
                else if (name.equals(systemClass)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isDelegateToParentClass(String name) {
            name = name.replace('/','.');
            while(name.startsWith(".")) {
                name=name.substring(1);
            }
            for (int index = 0; index < DELEGATE_TO_PARENT.length; index++) {
                String parentClass = DELEGATE_TO_PARENT[index];
                if (parentClass.endsWith(".")) {
                    if (name.startsWith(parentClass)) {
                        return true;
                    }
                }
                else if (name.equals(parentClass)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Delegate for LoadTimeWeaver's {@code addTransformer} method.
         * Typically called through ReflectiveLoadTimeWeaver.
         * @see org.springframework.instrument.classloading.LoadTimeWeaver#addTransformer
         * @see org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver
         */
        public void addTransformer(ClassFileTransformer transformer) {
            this.weavingTransformer.addTransformer(transformer);
        }

        /**
         * Delegate for LoadTimeWeaver's {@code getThrowawayClassLoader} method.
         * Typically called through ReflectiveLoadTimeWeaver.
         * @see org.springframework.instrument.classloading.LoadTimeWeaver#getThrowawayClassLoader
         * @see org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver
         */
        public ClassLoader getThrowawayClassLoader() {
            JUnitCustomClassLoader tempLoader = new JUnitCustomClassLoader();
            // Use reflection to copy all the fields since most of them are private
            // on pre-5.5 Tomcat.
            shallowCopyFieldState(this, tempLoader);
            return tempLoader;
        }

        // The code below is originally taken from ReflectionUtils and optimized for
        // local usage. There is no dependency on ReflectionUtils to keep this class
        // self-contained (since it gets deployed into Tomcat's server class loader).

        /**
         * Given the source object and the destination, which must be the same class
         * or a subclass, copy all fields, including inherited fields. Designed to
         * work on objects with public no-arg constructors.
         * @throws IllegalArgumentException if arguments are incompatible or either
         * is {@code null}
         */
        private static void shallowCopyFieldState(final Object src, final Object dest) throws IllegalArgumentException {
            if (src == null) {
                throw new IllegalArgumentException("Source for field copy cannot be null");
            }
            if (dest == null) {
                throw new IllegalArgumentException("Destination for field copy cannot be null");
            }
            Class targetClass = findCommonAncestor(src.getClass(), dest.getClass());

            // Keep backing up the inheritance hierarchy.
            do {
                // Copy each field declared on this class unless it's static or
                // file.
                Field[] fields = targetClass.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    // Skip static and final fields (the old FieldFilter)
                    // do not copy resourceEntries - it's a cache that holds class entries.
                    if (!(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()) ||
                            field.getName().equals("resourceEntries"))) {
                        try {
                            // copy the field (the old FieldCallback)
                            field.setAccessible(true);
                            Object srcValue = field.get(src);
                            field.set(dest, srcValue);
                        }
                        catch (IllegalAccessException ex) {
                            throw new IllegalStateException(
                                    "Shouldn't be illegal to access field '" + fields[i].getName() + "': " + ex);
                        }
                    }
                }
                targetClass = targetClass.getSuperclass();
            }
            while (targetClass != null && targetClass != Object.class);
        }

        private static Class findCommonAncestor(Class one, Class two) throws IllegalArgumentException {
            Class ancestor = one;
            while (ancestor != Object.class || ancestor != null) {
                if (ancestor.isAssignableFrom(two)) {
                    return ancestor;
                }
                ancestor = ancestor.getSuperclass();
            }
            // try the other class hierarchy
            ancestor = two;
            while (ancestor != Object.class || ancestor != null) {
                if (ancestor.isAssignableFrom(one)) {
                    return ancestor;
                }
                ancestor = ancestor.getSuperclass();
            }
            return null;
        }


        public String toString() {
            StringBuilder sb = new StringBuilder("[urls=");
            URL[] urls = getURLs();
            if (urls == null) {
                sb.append("null");
            } else {
                for (int i = 0; i < urls.length; i++) {
                    sb.append(urls[i]);
                    sb.append(",");
                }
                // remove trailing comma
                if (urls.length > 1) {
                    sb.setLength(sb.length() - 1);
                }
            }
            sb.append("]");
            return sb.toString();
        }

    }


}
