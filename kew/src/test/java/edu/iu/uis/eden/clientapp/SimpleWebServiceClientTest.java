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
package edu.iu.uis.eden.clientapp;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.workflow.config.KEWConfigurer;
import org.kuali.workflow.config.ThinClientResourceLoader;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;

/**
 * Tests a simple web-service only client to verfiy backward compatability
 * with the 2.2.x version of workflow.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Ignore
public class SimpleWebServiceClientTest extends KEWTestCase {

	@Override
	public void tearDown() throws Exception {
		QName thinRLName = new QName(Core.getCurrentContextConfig().getMessageEntity(), "ThinClientResourceLoader");
		GlobalResourceLoader.getResourceLoader().removeResourceLoader(thinRLName);
		ResourceLoader tempThinRL = GlobalResourceLoader.getResourceLoader(thinRLName);
		if (tempThinRL != null) {
			throw new RuntimeException("Unable to remove ThinClientResourceLoader the remaining tests are probably messed up");
		}
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

		thinRL.getWorkflowUtility().getWorkflowUser(new NetworkIdVO("rkirkend"));

		RouteHeaderVO routeHeader = new RouteHeaderVO();
        routeHeader.setDocTypeName("TestDocumentType");

		thinRL.getWorkflowDocument().createDocument(new NetworkIdVO("rkirkend"), routeHeader);

	}

	protected void setUpWebservices() throws Exception {
		try {
			Core.getCurrentContextConfig().overrideProperty(Config.CLIENT_PROTOCOL, EdenConstants.WEBSERVICE_CLIENT_PROTOCOL);
			Core.getCurrentContextConfig().overrideProperty("workflowutility.javaservice.endpoint", "http://localhost:9952/en-test/remoting/%7BKEW%7DWorkflowUtilityService");
			Core.getCurrentContextConfig().overrideProperty("workflowdocument.javaservice.endpoint", "http://localhost:9952/en-test/remoting/%7BKEW%7DWorkflowDocumentActionsService");
			Core.getCurrentContextConfig().overrideProperty("secure.workflowdocument.javaservice.endpoint", "true");
			Core.getCurrentContextConfig().overrideProperty("secure.workflowutility.javaservice.endpoint", "true");
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
