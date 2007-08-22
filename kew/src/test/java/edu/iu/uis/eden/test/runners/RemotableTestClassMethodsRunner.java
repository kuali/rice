/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.test.runners;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.junit.Test;
import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.internal.runners.TestIntrospector;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import edu.iu.uis.eden.EdenConstants;

public class RemotableTestClassMethodsRunner extends TestClassMethodsRunner {

	public RemotableTestClassMethodsRunner(Class<?> klass) {
		super(klass);
	}

	@Override
	public Description getDescription() {
		List<Method> testMethods = new TestIntrospector(getTestClass()).getTestMethods(Test.class);
		Description spec= Description.createSuiteDescription(getName());
		for (Method method : testMethods) {
			spec.addChild(methodDescription(method));
			spec.addChild(webserviceMethodDescription(method));
		}
		return spec;
	}

	@Override
	protected void invokeTestMethod(Method method, RunNotifier notifier) {
		Object test;
		try {
			test = createTest();
		} catch (Exception e) {
			testAborted(notifier, methodDescription(method));
			return;
		}
		setTestName(test, method.getName());
		TestMethodRunner runner1 = createMethodRunner(test, method, notifier);
		
		// add a runner to run the test in webservice mode
		try {
			test = createTest();
		} catch (Exception e) {
			testAborted(notifier, webserviceMethodDescription(method));
			return;
		}
		setTestName(test, method.getName()+"OverWebservices");
		TestMethodRunner runner2 = createWebserviceMethodRunner(test, method, notifier);
		
		// run the tests
		runner1.run();
		runner2.run();
	}
	
	protected RemotableTestMethodRunner createWebserviceMethodRunner(Object test, Method method, RunNotifier notifier) {
		return new RemotableTestMethodRunner(test, method, notifier, webserviceMethodDescription(method), EdenConstants.WEBSERVICE_CLIENT_PROTOCOL);
	}
	
	protected Description webserviceMethodDescription(Method method) {
		return Description.createTestDescription(getTestClass(), method.getName()+"OverWebservices");
	}
	
	private void setTestName(Object test, String name) {
		try {
			Method setNameMethod = MethodUtils.getAccessibleMethod(test.getClass(), "setName", new Class[] { String.class });
			setNameMethod.invoke(test, new Object[] { name });
		} catch (Exception e) {
			// no setName method, or we failed to invoke it, so we can't set the name
		}
	}
	
	private void testAborted(RunNotifier notifier, Description description) {
		notifier.fireTestStarted(description);
		notifier.fireTestFailure(new Failure(description, new Exception("No runnable methods")));
		notifier.fireTestFinished(description);
	}

}
