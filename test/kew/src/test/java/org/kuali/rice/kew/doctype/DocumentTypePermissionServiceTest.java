/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.doctype;

import org.junit.Test;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypePermissionService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.KIMServiceLocator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentTypePermissionServiceTest extends KEWTestCase {

	private DocumentTypePermissionService service;
	
	@Override
	public void setUpInternal() throws Exception {
		super.setUpInternal();
		service = KEWServiceLocator.getDocumentTypePermissionService();
	}

	@Test
	public void canBlanketApprove() throws Exception {
		DocumentType testDocType = KEWServiceLocator.getDocumentTypeService().findByName("TestDocumentType");
		KimPrincipal ewestfalPrincipal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("ewestfal");
		assertNotNull(testDocType);
		assertTrue("ewestfal should be a blanket approver", service.canBlanketApprove(ewestfalPrincipal.getPrincipalId(), testDocType, KEWConstants.ROUTE_HEADER_INITIATED_CD, ewestfalPrincipal.getPrincipalId()));
		
		// TODO set up actual KIM permissions in DB and verify this permission works
	}
	
	@Test
	public void testCanInitiate() throws Exception {
		DocumentType testDocType = KEWServiceLocator.getDocumentTypeService().findByName("TestDocumentType");
		KimPrincipal ewestfalPrincipal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("ewestfal");
		assertNotNull(testDocType);
		assertTrue("ewestfal should be allowed to initiate", service.canInitiate(ewestfalPrincipal.getPrincipalId(), testDocType));
    }
	
}
