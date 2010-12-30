/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.routemanager;

import org.kuali.rice.kew.messaging.exceptionhandling.ExceptionRoutingServiceImpl;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.ksb.messaging.PersistedMessageBO;
import org.kuali.rice.test.ThreadMonitor;



public class TestExceptionRoutingServiceImpl extends ExceptionRoutingServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	    .getLogger(TestExceptionRoutingServiceImpl.class);
    
	@Override
	public void placeInExceptionRouting(Throwable throwable, PersistedMessageBO persistedMessage, Long routeHeaderId) {
		ExceptionThreader exceptionThreader = new ExceptionThreader(throwable, persistedMessage, routeHeaderId, this);
		ThreadMonitor.addThread(exceptionThreader);
		exceptionThreader.start();
	}
	
	private static class ExceptionThreader extends Thread {

		private Throwable throwable;
		private PersistedMessageBO message;
		Long routeHeaderId;
		private TestExceptionRoutingServiceImpl testExceptionService;
		
		public ExceptionThreader(Throwable throwable, PersistedMessageBO message, Long routeHeaderId, TestExceptionRoutingServiceImpl testExceptionService) {
			this.throwable = throwable;
			this.message = message;
			this.routeHeaderId = routeHeaderId;
			this.testExceptionService = testExceptionService;
			TestUtilities.setExceptionThreader(this);
		}

		public void run() {
		    try {
			testExceptionService.callRealPlaceInExceptionRouting(throwable, message, routeHeaderId);
		    } catch (Exception e) {
			LOG.error(e, e);
		    }
		}
	}
	
	public void callRealPlaceInExceptionRouting(Throwable throwable, PersistedMessageBO message, Long routeHeaderId) throws Exception {
		super.placeInExceptionRouting(throwable, message, routeHeaderId);
	}	
}
