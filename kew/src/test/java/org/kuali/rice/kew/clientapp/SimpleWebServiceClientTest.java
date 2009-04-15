/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.kew.clientapp;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.kew.config.KEWConfigurer;
import org.kuali.rice.kew.config.ThinClientResourceLoader;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;


/**
 * Tests a simple web-service only client to verfiy backward compatability
 * with the 2.2.x version of workflow.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Ignore("once KULRICE-2792 is resolved, remove this ignore annotation so that this test executes again")
public class SimpleWebServiceClientTest extends KEWTestCase {

	@Override
	public void tearDown() throws Exception {
		QName thinRLName = new QName(ConfigContext.getCurrentContextConfig().getServiceNamespace(), "ThinClientResourceLoader");
		GlobalResourceLoader.getResourceLoader().removeResourceLoader(thinRLName);
		ResourceLoader tempThinRL = GlobalResourceLoader.getResourceLoader(thinRLName);
		if (tempThinRL != null) {
			throw new RuntimeException("Unable to remove ThinClientResourceLoader the remaining tests are probably messed up");
		}
		super.tearDown();
	}


	/**
	 * Verifies that we can aquire a reference to a WorkflowDocument without the need to
	 * wire up a KSBConfigurer.  This verifies backward compatability for EN-364.
	 */
	@Test public void testAquiringWorkflowDocument() throws Exception {
		this.setUpWebservices();

		//verify the ThinClientResourceLoader is in the GRL.
		ResourceLoader rl = GlobalResourceLoader.getResourceLoader();
		ResourceLoader tempThinRL = rl.getResourceLoaders().get(0);
		assertTrue("First resource loader should be thin", tempThinRL instanceof ThinClientResourceLoader);
		ThinClientResourceLoader thinRL = (ThinClientResourceLoader)tempThinRL;

		RouteHeaderDTO routeHeader = new RouteHeaderDTO();
        routeHeader.setDocTypeName("TestDocumentType");

		thinRL.getWorkflowDocument().createDocument(getPrincipalIdForName("rkirkend"), routeHeader);

	}

	protected void setUpWebservices() throws Exception {
		try {
		    String remoteUrlLocation = ConfigContext.getCurrentContextConfig().getProperty("serviceServletUrl");
			ConfigContext.getCurrentContextConfig().overrideProperty(Config.CLIENT_PROTOCOL, KEWConstants.WEBSERVICE_CLIENT_PROTOCOL);
            ConfigContext.getCurrentContextConfig().overrideProperty("workflowutility.javaservice.endpoint", remoteUrlLocation + "%7BKEW%7DWorkflowUtilityService");
//            ConfigContext.getCurrentContextConfig().overrideProperty("workflowutility.javaservice.endpoint", "http://localhost:9952/en-test/remoting/%7BKEW%7DWorkflowUtilityService");
            ConfigContext.getCurrentContextConfig().overrideProperty("workflowdocument.javaservice.endpoint", remoteUrlLocation + "%7BKEW%7DWorkflowDocumentActionsService");
//            ConfigContext.getCurrentContextConfig().overrideProperty("workflowdocument.javaservice.endpoint", "http://localhost:9952/en-test/remoting/%7BKEW%7DWorkflowDocumentActionsService");
			ConfigContext.getCurrentContextConfig().overrideProperty("secure.workflowdocument.javaservice.endpoint", "true");
			ConfigContext.getCurrentContextConfig().overrideProperty("secure.workflowutility.javaservice.endpoint", "true");
			KEWConfigurer kewConfigurer  = new KEWConfigurer();
			kewConfigurer.start();
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException)e;
	}
			throw new RuntimeException("Failed to start the ksb configurer to run the remotable test.", e);
		}
	}


}
