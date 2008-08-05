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
package edu.iu.uis.eden.docsearch;

import org.junit.Test;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.util.ClassLoaderUtils;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.applicationconstants.ApplicationConstant;
import edu.iu.uis.eden.applicationconstants.ApplicationConstantsService;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class CustomDocumentSearchGeneratorTest extends DocumentSearchTestBase {
//	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomSearchAttributesTest.class);

    protected void loadTestData() throws Exception {
        loadXmlFile("SearchAttributeConfig.xml");
    }

    @Test public void testCustomDocumentSearchGeneratorUse() throws Exception {
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType");
    	assertEquals("The document search Generator class is incorrect.",StandardDocumentSearchGenerator.class,(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchGenerator())).getClass());
    	docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType_DefaultCustomProcessor");
    	assertEquals("The document search Generator class is incorrect.",CustomDocumentSearchGenerator.class,(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchGenerator())).getClass());
    }
    
	private DocumentType getValidDocumentType(String documentTypeFullName) {
		if (Utilities.isEmpty(documentTypeFullName)) {
			return null;
		}
		DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeFullName);
		if (docType == null) {
			throw new RuntimeException("No Valid Document Type Found for document type name '" + documentTypeFullName + "'");
		} else {
			return docType;
		}
	}

    @Test public void testCustomDocSearchGeneratorResultSetLimit() throws Exception {
        String documentTypeName = "SearchDocType_DefaultCustomProcessor";
        String userNetworkId = "rkirkend";
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(userNetworkId));

        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName)));

        // adjust the app constant to be greater than custom generator value
        adjustResultSetCapApplicationConstantValue(CustomDocumentSearchGenerator.RESULT_SET_LIMIT + 1);
        KEWServiceLocator.getDocumentSearchService().getList(user, criteria);
        assertEquals("Criteria threshold should equal custom generator class threshold", CustomDocumentSearchGenerator.RESULT_SET_LIMIT, criteria.getThreshold().intValue());

        // adjust the app constant to be less than custom generator value
        int newLimit = CustomDocumentSearchGenerator.RESULT_SET_LIMIT - 1;
        adjustResultSetCapApplicationConstantValue(newLimit);
        KEWServiceLocator.getDocumentSearchService().getList(user, criteria);
        assertEquals("Criteria threshold should equal system result set threshold", newLimit, criteria.getThreshold().intValue());

        // delete the app constant
        KEWServiceLocator.getApplicationConstantsService().delete(KEWServiceLocator.getApplicationConstantsService().findByName(KEWConstants.DOC_SEARCH_RESULT_CAP_KEY));
        KEWServiceLocator.getDocumentSearchService().getList(user, criteria);
        assertEquals("Criteria threshold should equal custom generator class threshold", CustomDocumentSearchGenerator.RESULT_SET_LIMIT, criteria.getThreshold().intValue());
    }
    
    private void adjustResultSetCapApplicationConstantValue(Integer newValue) {
        ApplicationConstantsService acs = KEWServiceLocator.getApplicationConstantsService();
        ApplicationConstant ac = acs.findByName(KEWConstants.DOC_SEARCH_RESULT_CAP_KEY);
        if (ac == null) {
        	ac = new ApplicationConstant();
        }
        ac.setApplicationConstantName(KEWConstants.DOC_SEARCH_RESULT_CAP_KEY);
        ac.setApplicationConstantValue(newValue.toString());
        acs.save(ac);
    }
    
    /**
     * Tests function of adding extra document type names to search including using searchable attributes
     * that may or may not exist on all the document type names being searched on.
     * 
     * @throws Exception
     */
    @Test public void testSearchOnExtraDocType() throws Exception {
        String userNetworkId = "rkirkend";
        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId(userNetworkId));

        String documentTypeName1 = "SearchDocType_DefaultCustomProcessor";
        WorkflowDocument workDoc_Matching1 = new WorkflowDocument(new NetworkIdDTO(userNetworkId), documentTypeName1);
    	DocumentType docType1 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName1);
        WorkflowAttributeDefinitionDTO stringXMLDef1 = new WorkflowAttributeDefinitionDTO("SearchableAttributeVisible");
        stringXMLDef1.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching1.addSearchableDefinition(stringXMLDef1);
        workDoc_Matching1.routeDocument("");

        String documentTypeName2 = "SearchDocType_DefaultCustomProcessor_2";
        WorkflowDocument workDoc_Matching2 = new WorkflowDocument(new NetworkIdDTO(userNetworkId), documentTypeName2);
    	DocumentType docType2 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName2);
        WorkflowAttributeDefinitionDTO stringXMLDef2 = new WorkflowAttributeDefinitionDTO("SearchableAttributeVisible");
        stringXMLDef2.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching2.addSearchableDefinition(stringXMLDef2);
        workDoc_Matching2.routeDocument("");
        
        // do search with attribute using doc type 1... make sure both docs are returned
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType1));
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 2, result.getSearchResults().size());

        // do search with attribute using doc type 2... make sure both docs are returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName2);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType2));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 2, result.getSearchResults().size());

        // do search without attribute using doc type 1... make sure both docs are returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName1);
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 2, result.getSearchResults().size());

        // do search without attribute using doc type 2... make sure both docs are returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName2);
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 2, result.getSearchResults().size());
        
        String documentTypeName3 = "SearchDocType_DefaultCustomProcessor_3";
        WorkflowDocument workDoc_Matching3 = new WorkflowDocument(new NetworkIdDTO(userNetworkId), documentTypeName3);
    	DocumentType docType3 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName3);
        WorkflowAttributeDefinitionDTO stringXMLDef3 = new WorkflowAttributeDefinitionDTO("SearchableAttributeVisible");
        stringXMLDef3.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching3.addSearchableDefinition(stringXMLDef3);
        workDoc_Matching3.routeDocument("");
        
        // do search with attribute using doc type 3... make sure 1 doc is returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName3);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType3));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());
        
        // do search without attribute using doc type 3... make sure 1 doc is returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName3);
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        WorkflowDocument workDoc_NonMatching2 = new WorkflowDocument(new NetworkIdDTO(userNetworkId), documentTypeName2);
        WorkflowAttributeDefinitionDTO stringXMLDef1a = new WorkflowAttributeDefinitionDTO("SearchableAttributeVisible");
        // TODO delyea - adding underscore below invalidates via REGEX but doesn't blow up on route or addSearchable?
        String searchAttributeValue = TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE + "nonMatching";
        stringXMLDef1a.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, searchAttributeValue);
        workDoc_NonMatching2.addSearchableDefinition(stringXMLDef1a);
        workDoc_NonMatching2.routeDocument("");
        
        // do search with attribute using doc type 1... make sure 1 doc is returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, searchAttributeValue, docType1));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        // do search without attribute using doc type 1... make sure all 3 docs are returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName1);
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have one document.", 3, result.getSearchResults().size());
    }
}