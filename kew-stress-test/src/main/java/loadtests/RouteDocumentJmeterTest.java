/*
 * Copyright 2007 The Kuali Foundation
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
package loadtests;

import javax.xml.namespace.QName;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log4j.Logger;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.kuali.rice.resourceloader.SpringResourceLoader;
import org.springframework.util.Log4jConfigurer;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;

/**
 * Routes a document.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class RouteDocumentJmeterTest extends AbstractJavaSamplerClient {

    private static final Logger LOG = Logger.getLogger(RouteDocumentJmeterTest.class);
    private static boolean initialized = false;
    public static SpringResourceLoader SPRING_LOADER = new SpringResourceLoader(new QName("StressTest"),
	    "classpath:WebServiceSpringBeans.xml");

    public SampleResult runTest(JavaSamplerContext context) {
	initializeWorkflow();
	long start = System.currentTimeMillis();
	try {
	    WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("KULUSER"), "LoadTestMassDocument");
	    document.routeDocument("MassDocumentRouted for Load test");
	    long stop = System.currentTimeMillis();
	    SampleResult result = new SampleResult(start, stop);
	    result.setSuccessful(true);
	    result.setSamplerData("Good to go");
	    return result;
	} catch (Exception e) {
	    LOG.error("Caught Exception running JMeter test");
	    long stop = System.currentTimeMillis();
	    SampleResult result = new SampleResult(start, stop);
	    result.setSuccessful(false);
	    result.setSamplerData("FAILURE!!!");
	    return result;
	}
    }

    private static synchronized void initializeWorkflow() {
	try {
	    if (!initialized) {
		Log4jConfigurer.initLogging("classpath:stress-test-log4j.properties");
		SPRING_LOADER.start();
		initialized = true;
	    }
	} catch (Exception e) {
	    LOG.error("Caught exception initializing KEW");
	    throw new RiceRuntimeException("Caught exception initializing KEW", e);
	}
    }

}
