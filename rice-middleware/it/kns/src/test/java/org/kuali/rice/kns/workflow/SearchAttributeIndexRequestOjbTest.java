/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.workflow;

import org.junit.Test;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.test.document.SearchAttributeIndexTestDocumentOjb;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * tests that a document, which goes through a regular or blanket approval process, is indexed correctly
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated KNS test class, convert to KRAD equivalent if applicable.
 */
@Deprecated
public class SearchAttributeIndexRequestOjbTest extends SearchAttributeIndexRequestTest {

    private static final String SEARCH_ATTRIBUTE_INDEX_DOCUMENT_TEST_DOC_TYPE = "SearchAttributeIndexTestDocumentOjb";
	
	private enum DOCUMENT_FIXTURE {
		NORMAL_DOCUMENT("hippo","routing");
		
		private String constantString;
		private String routingString;
		private DOCUMENT_FIXTURE(String constantString, String routingString) {
			this.constantString = constantString;
			this.routingString = routingString;
		}

        public Document getDocument() throws Exception {
            Document document = KRADServiceLocatorWeb.getDocumentService().getNewDocument(SEARCH_ATTRIBUTE_INDEX_DOCUMENT_TEST_DOC_TYPE);
            SearchAttributeIndexTestDocumentOjb searchAttributeIndexTestDocument = (SearchAttributeIndexTestDocumentOjb) document;
            searchAttributeIndexTestDocument.initialize(constantString, routingString);

            return searchAttributeIndexTestDocument;
        }
	}

    @Test
    public void regularApproveTest() throws Exception {
        final String principalName = "quickstart";
        final String principalId = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName).getPrincipalId();
        GlobalVariables.setUserSession(new UserSession(principalName));
        RouteContext.clearCurrentRouteContext();

        Document document = DOCUMENT_FIXTURE.NORMAL_DOCUMENT.getDocument();
        document.getDocumentHeader().setDocumentDescription("Routed SAIndexTestDoc");
        final DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(SEARCH_ATTRIBUTE_INDEX_DOCUMENT_TEST_DOC_TYPE);

        assertApproveAttributes(document, documentType, principalId);
    }

    @Test
    public void blanketApproveTest() throws Exception {
        final String principalName = "admin";
        final String principalId = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName).getPrincipalId();
        GlobalVariables.setUserSession(new UserSession(principalName));

        Document document = DOCUMENT_FIXTURE.NORMAL_DOCUMENT.getDocument();
        document.getDocumentHeader().setDocumentDescription("Blanket Approved SAIndexTestDoc");
        final DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(SEARCH_ATTRIBUTE_INDEX_DOCUMENT_TEST_DOC_TYPE);

        assertBlanketApproveAttributes(document, documentType, principalId);
    }


}
