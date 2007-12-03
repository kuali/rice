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
package loadtests;

import org.apache.log4j.Logger;

import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.test.stress.BasicTest;
import edu.iu.uis.eden.test.stress.TestInfo;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MassDocumentRouter extends BasicTest {
    
    private static final int NUM_DOCS_TO_ROUTE = 99;
    
    public MassDocumentRouter() throws Exception {
	this.setDocumentId(new Long(-1));
    }

    @Override
    public boolean doWork() throws Exception {

	//List<Long> docIds = new ArrayList<Long>();
	ScheduledThreadPoolExecutor ses = new ScheduledThreadPoolExecutor(20);
	ses.prestartAllCoreThreads();
	
	new Router().run();
	
	int i = 0;
	while (i++ < NUM_DOCS_TO_ROUTE) {
	    ses.execute(new Router());
	}
	
	while (ses.getQueue().size() > 0) {
	    Thread.sleep(2000);
	}
	
	return true;
    }
    
    
    public static class Router implements Runnable {

	private static final Logger LOG = Logger.getLogger(Router.class);
	
	public void run() {
	    LOG.info("Preparing to route document");
	    try {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("KULUSER"), "LoadTestMassDocument");
		document.routeDocument("MassDocumentRouted for Load test");
		TestInfo.markCallToServer();
	        TestInfo.addRouteHeaderId(document.getRouteHeaderId());
		Thread.sleep(1500);
		
		int i = 0;
		while (new WorkflowDocument(new NetworkIdVO("KULUSER"), document.getRouteHeaderId()).stateIsInitiated() && i++ < 20) {
		    if (i >= 20) {
			throw new RuntimeException("Too many enroute check iterations performed.  assuming something went wrong in document routing.");
		    }
		    Thread.sleep(1000);
		}
	    } catch (Exception e) {
		LOG.error("Caught exception processing a LoadTestMassDocument", e);
	    }
	}
    }
}