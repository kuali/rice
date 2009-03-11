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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * This is a validator for document types to ensure that the documents either contain or inherit the required properties.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeBusinessRules extends MaintenanceDocumentRuleBase {
	
	/**
	 * Constructs a new DocumentTypeRules instance and calls the parent class' constructor.
	 */
	public DocumentTypeBusinessRules() {
		super();
	}
	
	/**
	 * Verifies that a given document has the proper fields defined locally or in a parent document.
	 * 
	 * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
	 */
	@Override
	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		// Perform any business rules validation defined by the parent class.
		boolean docIsValid = super.processCustomRouteDocumentBusinessRules(document);
		// The document type to be validated.
		DocumentType docType = (DocumentType) document.getDocumentBusinessObject();
		// A flag indicating the test's success/failure.
		boolean testSucceeded = false;
		
		// Verify that the document handler URL has been defined locally or in a parent document.
		for (DocumentType currDoc = docType; !testSucceeded && currDoc != null; currDoc = currDoc.getParentDocType()) {
			String docUrl = currDoc.getUnresolvedDocHandlerUrl();
			if (StringUtils.isNotBlank(docUrl)) {
				testSucceeded = true;
			}
		}
		// Check if the above test failed.
		if (!testSucceeded) {
			docIsValid = false;
			String tempName = docType.getName();
			putGlobalError(KEWPropertyConstants.ERROR_DOCTYPEBUSINESSRULES_NO_DOC_HANDLER_URL,
					((StringUtils.isNotBlank(tempName)) ? "'" + tempName + "'" : "The document"));
		}
		
        return docIsValid;
    }
	
}