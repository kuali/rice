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
package org.kuali.rice.kew.actions;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.webservice.DocumentResponse;
import org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService;
import org.kuali.rice.kew.webservice.StandardResponse;

/**
 * This is a description of what this class does - Daniel Epstein don't forget
 * to fill this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class SimpleDocumentActionsWebServiceTest extends KEWTestCase {
	
	@Override
	protected void loadTestData() throws Exception {
		loadXmlFile("ActionsConfig.xml");
	}
	
	@Test
	public void testCreateAndRoute() throws Exception{

		SimpleDocumentActionsWebService simpleService = (SimpleDocumentActionsWebService) GlobalResourceLoader.getService(new QName("KEW", "simpleDocumentActionsService"));
		DocumentResponse dr = simpleService.create("admin","doc1", "BlanketApproveSequentialTest", "Doc1Title");
		StandardResponse sr = simpleService.route(dr.getDocId(), "admin", "Doc1Title", "<foo>bar</foo>", "Annotation!");
		sr = simpleService.approve(dr.getDocId(), "admin", "Doc1Title", "<foo>b</foo>", "Annotation!!!");
		assertTrue(StringUtils.isEmpty(sr.getErrorMessage()));		
		
	}

}
