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
package org.kuali.rice.kew.doctype.bo;

import org.junit.Test;
import org.kuali.rice.kew.document.DocumentTypeMaintainable;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.rule.event.RouteDocumentEvent;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a unit test for the DocumentTypeBusinessRules class.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeBusinessRulesTest extends KEWTestCase {
	
	/**
	 * A convenience method for wrapping the given document type inside a MaintenanceDocument.
	 * 
	 * @param xmlFile The name of the XML file to load that contains the desired document type.
	 * @param docName The name of the document type to wrap.
	 * @return A new MaintenanceDocument containing Maintainable instances that use the given doc type as their business object.
	 */
	private MaintenanceDocument makeNewMaintDoc(String xmlFile, String docName) throws WorkflowException {
		// Load the document type and initialize the global settings.
		GlobalVariables.setErrorMap(new ErrorMap());
		GlobalVariables.setUserSession(new UserSession("admin"));
		loadXmlFile(xmlFile);
		// Initialize the documents.
		MaintenanceDocument maintDoc = (MaintenanceDocument) KNSServiceLocator.getDocumentService().getNewDocument("DocumentTypeDocument");
		DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(docName);
		// Setup the Maintainable instances.
		Maintainable oldDocTypeMaint = new DocumentTypeMaintainable();
		Maintainable newDocTypeMaint = new DocumentTypeMaintainable();
		oldDocTypeMaint.setBusinessObject(docType);
		oldDocTypeMaint.setBoClass(DocumentType.class);
		oldDocTypeMaint.setMaintenanceAction(KNSConstants.MAINTENANCE_EDIT_ACTION);
		newDocTypeMaint.setBusinessObject(docType);
		newDocTypeMaint.setBoClass(DocumentType.class);
		newDocTypeMaint.setMaintenanceAction(KNSConstants.MAINTENANCE_EDIT_ACTION);
		// Setup the MaintenanceDocument.
		maintDoc.setOldMaintainableObject(oldDocTypeMaint);
		maintDoc.setNewMaintainableObject(newDocTypeMaint);
		maintDoc.getDocumentHeader().setDocumentDescription("This is a doc handler URL test");
		return maintDoc;
	}
	
	/**
	 * Verifies that the doc handler URL validation succeeds if the docHandler field is included and is not blank.
     * 
     * @throws Exception
	 */
	@Test public void testDocumentWithValidDocHandlerUrl() throws Exception {
		MaintenanceDocument maintDoc = makeNewMaintDoc("ChildParentTestConfig1_Valid1.xml", "PCITestChild1");
		boolean isValid = KNSServiceLocator.getKualiRuleService().applyRules(new RouteDocumentEvent("", maintDoc));
		assertTrue("The document should have been valid", isValid);
		assertTrue("The error map should be empty", GlobalVariables.getErrorMap().isEmpty());
	}
	
	/**
	 * Verifies that the doc handler URL validation fails if the docHandler field is omitted; will also check parent before ending search.
     * 
     * @throws Exception
	 */
	@Test public void testDocumentWithMissingDocHandlerUrl() throws Exception {
		MaintenanceDocument maintDoc = makeNewMaintDoc("ChildParentTestConfig1_Invalid1.xml", "PCITestChild1");
		boolean isValid = KNSServiceLocator.getKualiRuleService().applyRules(new RouteDocumentEvent("", maintDoc));
		boolean isBadUrl = GlobalVariables.getErrorMap().containsMessageKey(
				KEWPropertyConstants.ERROR_DOCTYPEBUSINESSRULES_NO_DOC_HANDLER_URL);
		assertFalse("The document should have been invalid", isValid);
		assertTrue("'" + KEWPropertyConstants.ERROR_DOCTYPEBUSINESSRULES_NO_DOC_HANDLER_URL +
				"' should have been a message key in the error map", isBadUrl);
	}
	
	/**
	 * Verifies that the doc handler URL validation succeeds when it is defined on the parent.
     * 
     * @throws Exception
	 */
	@Test public void testDocumentWithValidDocHandlerUrlInParent() throws Exception {
		MaintenanceDocument maintDoc = makeNewMaintDoc("ChildParentTestConfig1_Valid2.xml", "PCITestChild1");
		boolean isValid = KNSServiceLocator.getKualiRuleService().applyRules(new RouteDocumentEvent("", maintDoc));
		assertTrue("The document should have been valid", isValid);
		assertTrue("The error map should be empty", GlobalVariables.getErrorMap().isEmpty());
	}
}