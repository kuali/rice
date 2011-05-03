/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch;

import org.junit.Test;
import org.kuali.rice.core.api.parameter.Parameter;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;

import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.util.KNSConstants;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
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
		if (org.apache.commons.lang.StringUtils.isEmpty(documentTypeFullName)) {
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
        Person user = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(userNetworkId);


        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName)));

        Parameter orig = CoreFrameworkServiceLocator.getParameterService().getParameter(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KEWConstants.DOC_SEARCH_RESULT_CAP);

        String origValue = orig.getValue();
        // adjust the app constant to be greater than custom generator value
        adjustResultSetCapApplicationConstantValue(orig, CustomDocumentSearchGenerator.RESULT_SET_LIMIT + 1);
        KEWServiceLocator.getDocumentSearchService().getList(user.getPrincipalId(), criteria);
        assertEquals("Criteria threshold should equal custom generator class threshold", CustomDocumentSearchGenerator.RESULT_SET_LIMIT, criteria.getThreshold().intValue());

        orig = CoreFrameworkServiceLocator.getParameterService().getParameter(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KEWConstants.DOC_SEARCH_RESULT_CAP);
        // adjust the app constant to be less than custom generator value
        int newLimit = CustomDocumentSearchGenerator.RESULT_SET_LIMIT - 1;
        adjustResultSetCapApplicationConstantValue(orig, newLimit);
        KEWServiceLocator.getDocumentSearchService().getList(user.getPrincipalId(), criteria);
        assertEquals("Criteria threshold should equal system result set threshold", newLimit, criteria.getThreshold().intValue());

        orig = CoreFrameworkServiceLocator.getParameterService().getParameter(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KEWConstants.DOC_SEARCH_RESULT_CAP);
        // reset the parameter
        Parameter.Builder pb = Parameter.Builder.create(orig);
        pb.setValue(origValue);
        CoreFrameworkServiceLocator.getParameterService().updateParameter(pb.build());

        // old parameter value will still be cached, let's flush the cache
        //KNSServiceLocator.getParameterService().clearCache();
        
        KEWServiceLocator.getDocumentSearchService().getList(user.getPrincipalId(), criteria);
        assertEquals("Criteria threshold should equal custom generator class threshold", CustomDocumentSearchGenerator.RESULT_SET_LIMIT, criteria.getThreshold().intValue());
    }

    private void adjustResultSetCapApplicationConstantValue(Parameter p, Integer newValue) {
        Parameter.Builder ps = Parameter.Builder.create(p);

        //ps.setNamespaceCode(KEWConstants.KEW_NAMESPACE);
        //ps.setName(KEWConstants.DOC_SEARCH_RESULT_CAP);
        ps.setValue(newValue.toString());
        //ps.setParameterType(ParameterType.Builder.create("CONFG"));
        //ps.setVersionNumber(p.getVersionNumber());
        //ps.setComponentCode(KNSConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE);
        //ps.setEvaluationOperator(EvaluationOperator.ALLOW);
        CoreFrameworkServiceLocator.getParameterService().updateParameter(ps.build());
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
        Person user = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(userNetworkId);

        String documentTypeName1 = "SearchDocType_DefaultCustomProcessor";
        WorkflowDocument workDoc_Matching1 = WorkflowDocument.createDocument(getPrincipalIdForName(userNetworkId), documentTypeName1);
    	DocumentType docType1 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName1);
        WorkflowAttributeDefinitionDTO stringXMLDef1 = new WorkflowAttributeDefinitionDTO("SearchableAttributeVisible");
        stringXMLDef1.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching1.addSearchableDefinition(stringXMLDef1);
        workDoc_Matching1.routeDocument("");

        String documentTypeName2 = "SearchDocType_DefaultCustomProcessor_2";
        WorkflowDocument workDoc_Matching2 = WorkflowDocument.createDocument(getPrincipalIdForName(userNetworkId), documentTypeName2);
    	DocumentType docType2 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName2);
        WorkflowAttributeDefinitionDTO stringXMLDef2 = new WorkflowAttributeDefinitionDTO("SearchableAttributeVisible");
        stringXMLDef2.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching2.addSearchableDefinition(stringXMLDef2);
        workDoc_Matching2.routeDocument("");

        // do search with attribute using doc type 1... make sure both docs are returned
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType1));
        DocumentSearchResultComponents result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertEquals("Search results should have one document.", 2, result.getSearchResults().size());

        // do search with attribute using doc type 2... make sure both docs are returned
        criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName2);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType2));
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertEquals("Search results should have one document.", 2, result.getSearchResults().size());

        // do search without attribute using doc type 1... make sure both docs are returned
        criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName1);
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertEquals("Search results should have one document.", 2, result.getSearchResults().size());

        // do search without attribute using doc type 2... make sure both docs are returned
        criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName2);
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertEquals("Search results should have one document.", 2, result.getSearchResults().size());

        String documentTypeName3 = "SearchDocType_DefaultCustomProcessor_3";
        WorkflowDocument workDoc_Matching3 = WorkflowDocument.createDocument(getPrincipalIdForName(userNetworkId), documentTypeName3);
    	DocumentType docType3 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName3);
        WorkflowAttributeDefinitionDTO stringXMLDef3 = new WorkflowAttributeDefinitionDTO("SearchableAttributeVisible");
        stringXMLDef3.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching3.addSearchableDefinition(stringXMLDef3);
        workDoc_Matching3.routeDocument("");

        // do search with attribute using doc type 3... make sure 1 doc is returned
        criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName3);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType3));
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        // do search without attribute using doc type 3... make sure 1 doc is returned
        criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName3);
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        WorkflowDocument workDoc_NonMatching2 = WorkflowDocument.createDocument(getPrincipalIdForName(userNetworkId), documentTypeName2);
        WorkflowAttributeDefinitionDTO stringXMLDef1a = new WorkflowAttributeDefinitionDTO("SearchableAttributeVisible");
        // TODO delyea - adding underscore below invalidates via REGEX but doesn't blow up on route or addSearchable?
        String searchAttributeValue = TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE + "nonMatching";
        stringXMLDef1a.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, searchAttributeValue);
        workDoc_NonMatching2.addSearchableDefinition(stringXMLDef1a);
        workDoc_NonMatching2.routeDocument("");

        // do search with attribute using doc type 1... make sure 1 doc is returned
        criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, searchAttributeValue, docType1));
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertEquals("Search results should have one document.", 1, result.getSearchResults().size());

        // do search without attribute using doc type 1... make sure all 3 docs are returned
        criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName1);
        result = docSearchService.getList(user.getPrincipalId(), criteria);
        assertEquals("Search results should have one document.", 3, result.getSearchResults().size());
    }
}
