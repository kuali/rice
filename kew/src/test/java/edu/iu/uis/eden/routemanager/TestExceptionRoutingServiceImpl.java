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
package edu.iu.uis.eden.routemanager;

import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.messaging.exceptionhandling.ExceptionRoutingServiceImpl;
import edu.iu.uis.eden.test.TestUtilities;


public class TestExceptionRoutingServiceImpl extends ExceptionRoutingServiceImpl {

	@Override
	public void placeInExceptionRouting(Throwable throwable, PersistedMessage persistedMessage, Long routeHeaderId) {
		ExceptionThreader exceptionThreader = new ExceptionThreader(throwable, persistedMessage, routeHeaderId, this);
		exceptionThreader.start();
	}
	
	private static class ExceptionThreader extends Thread {

		private Throwable throwable;
		private PersistedMessage message;
		Long routeHeaderId;
		private TestExceptionRoutingServiceImpl testExceptionService;
		
		public ExceptionThreader(Throwable throwable, PersistedMessage message, Long routeHeaderId, TestExceptionRoutingServiceImpl testExceptionService) {
			this.throwable = throwable;
			this.message = message;
			this.routeHeaderId = routeHeaderId;
			this.testExceptionService = testExceptionService;
			TestUtilities.setExceptionThreader(this);
		}

		public void run() {
			testExceptionService.callRealPlaceInExceptionRouting(throwable, message, routeHeaderId);
		}
	}
	
	public void callRealPlaceInExceptionRouting(Throwable throwable, PersistedMessage message, Long routeHeaderId) {
		super.placeInExceptionRouting(throwable, message, routeHeaderId);
	}	
}