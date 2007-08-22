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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class RemotableTestMethodRunner extends TestMethodRunner {

	private String remotingProtocol;

	public RemotableTestMethodRunner(Object test, Method method, RunNotifier notifier, Description description, String remotingProtocol) {
		super(test, method, notifier, description);
		this.remotingProtocol = remotingProtocol;
	}

	@Override
	protected void executeMethodBody() throws IllegalAccessException, InvocationTargetException {
		throw new UnsupportedOperationException("setUpWebservices not currently supported");
//		try {
//			//duplicated in WorkflowTestCase
//			Core.getCurrentContextConfig().overrideProperty(Config.CLIENT_PROTOCOL, EdenConstants.WEBSERVICE_CLIENT_PROTOCOL);
//			Core.getCurrentContextConfig().overrideProperty("workflowutility.javaservice.endpoint", "http://localhost:9912/en-test/remoting/%7BKEW%7DWorkflowUtilityService");
//			Core.getCurrentContextConfig().overrideProperty("workflowdocument.javaservice.endpoint", "http://localhost:9912/en-test/remoting/%7BKEW%7DWorkflowDocumentActionsService");
//			Core.getCurrentContextConfig().overrideProperty("secure.workflowdocument.javaservice.endpoint", "true");
//			Core.getCurrentContextConfig().overrideProperty("secure.workflowutility.javaservice.endpoint", "true");
//			KSBConfigurer ksbConfigurer  = new KSBConfigurer();
//			ksbConfigurer.start();
//		} catch (Exception e) {
//			if (e instanceof RuntimeException) {
//				throw (RuntimeException)e;
//			}
//			throw new RuntimeException("Failed to start the ksb configurer to run the remotable test.", e);
//		}
//    	super.executeMethodBody();
	}





}
