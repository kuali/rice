/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.messaging;

import org.kuali.rice.test.TestUtilities;

import edu.iu.uis.eden.messaging.exceptionhandling.DefaultExceptionServiceImpl;

public class TestExceptionRoutingServiceImpl extends DefaultExceptionServiceImpl {

	public void placeInExceptionRouting(Throwable throwable, PersistedMessage message, Object service) {
		ExceptionThreader exceptionThreader = new ExceptionThreader(throwable, message, service, this);
		exceptionThreader.start();
	}
	
	private static class ExceptionThreader extends Thread {

		private Throwable throwable;
		private PersistedMessage message;
		private Object service;
		private TestExceptionRoutingServiceImpl testExceptionService;
		
		public ExceptionThreader(Throwable throwable, PersistedMessage message, Object service, TestExceptionRoutingServiceImpl testExceptionService) {
			this.throwable = throwable;
			this.message = message;
			this.service = service;
			this.testExceptionService = testExceptionService;
			TestUtilities.setExceptionThreader(this);
		}

		public void run() {
		    this.testExceptionService.callRealPlaceInExceptionRouting(this.throwable, this.message, this.service);
		}
	}
	
	public void callRealPlaceInExceptionRouting(Throwable throwable, PersistedMessage message, Object service) {
		super.placeInExceptionRouting(throwable, message, service);
	}
	
}
