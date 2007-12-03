/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.test.stress;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.iu.uis.eden.clientapp.vo.UserIdVO;

public class StressTestServlet extends HttpServlet {

    private static final long serialVersionUID = 1588315166209073945L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StressTestServlet.class);

    private static final long DEFAULT_POLL_INTERVAL = 1000;
    public static final String POLL_INTERVAL = "pollInterval";

    public static final String RANDOM = "random";
    public static final String UTILITY = "utility";

    public void init() throws ServletException {
	super.init();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	try {
	    long start = System.currentTimeMillis();
	    String pollingIntervalValue = request.getParameter(POLL_INTERVAL);
	    long pollingInterval = (pollingIntervalValue == null ? DEFAULT_POLL_INTERVAL : Long
		    .parseLong(pollingIntervalValue));
	    Test test = determineTest(request);
	    Map parameters = new HashMap();
	    Enumeration enumeration = request.getParameterNames();
	    while (enumeration.hasMoreElements()) {
		String name = (String) enumeration.nextElement();
		parameters.put(name, request.getParameter(name));
	    }
	    test.setParameters(parameters);
	    while (!test.doWork()) {
		Thread.sleep(pollingInterval);
	    }
	    long stop = System.currentTimeMillis();
	    long durationSecs = (stop - start)/1000;
	    response.setContentType("text/html");
	    response.getWriter().write(
		    "<html><head><title>SUCCESS</title></head><body>" + "Successfully completed test. Calls to server "
			    + TestInfo.getServerCalls() + "<br>" + "Documents routed " + TestInfo.getRouteHeaderIds().size() + "<br>"
			    + "Duration in Secs: " + durationSecs + "<br>");
		  
	    if (test instanceof BasicTest) {
		response.getWriter().write("Document Id:" + ((BasicTest)test).getDocumentId() + "<br>");
		int i = 0;
		for (UserIdVO userId : ((BasicTest)test).getRecipients()) {
		    response.getWriter().write("User Id" + ++i + " :" + userId + "<br>");
		}
	    }
    	    response.getWriter().write("</body></html>");
	} catch (Exception e) {
	    LOG.error("Exception thrown from test!.", e);
	    throw new ServletException(e);
	}
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	doGet(request, response);
    }

    protected Test determineTest(HttpServletRequest request) throws Exception {
	String test = request.getParameter("test");
	if (test == null)
	    test = "";
	test = test.trim();
	if (test == null || "".equals(test)) {
	    throw new Exception("Must specifiy a 'test' parameter!");
	} else if (test.equals(UTILITY)) {
	    return new WorkflowUtilityTest();
	} else {
	    return (Test) Class.forName(test).newInstance();
	}
    }
}