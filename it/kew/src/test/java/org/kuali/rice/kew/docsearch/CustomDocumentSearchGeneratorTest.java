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
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.impl.document.search.DocumentSearchGeneratorImpl;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

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
    	assertEquals("The document search Generator class is incorrect.",DocumentSearchGeneratorImpl.class,(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchGenerator())).getClass());
    	docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType_DefaultCustomProcessor");
    	assertEquals("The document search Generator class is incorrect.",CustomDocumentSearchGeneratorImpl.class,(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchGenerator())).getClass());
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

    private void adjustResultSetCapApplicationConstantValue(Parameter p, Integer newValue) {
        Parameter.Builder ps = Parameter.Builder.create(p);

        //ps.setNamespaceCode(KEWConstants.KEW_NAMESPACE);
        //ps.setName(KEWConstants.DOC_SEARCH_RESULT_CAP);
        ps.setValue(newValue.toString());
        //ps.setParameterType(ParameterType.Builder.create("CONFG"));
        //ps.setVersionNumber(p.getVersionNumber());
        //ps.setComponentCode(KRADConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE);
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
        WorkflowDocument workDoc_Matching1 = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(userNetworkId), documentTypeName1);
    	DocumentType docType1 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName1);
        WorkflowAttributeDefinition.Builder stringXMLDef1 = WorkflowAttributeDefinition.Builder.create("SearchableAttributeVisible");
        stringXMLDef1.addPropertyDefinition(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching1.addSearchableDefinition(stringXMLDef1.build());
        workDoc_Matching1.route("");
        
        String documentTypeName2 = "SearchDocType_DefaultCustomProcessor_2";
        WorkflowDocument workDoc_Matching2 = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(userNetworkId), documentTypeName2);
    	DocumentType docType2 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName2);
        WorkflowAttributeDefinition.Builder stringXMLDef2 = WorkflowAttributeDefinition.Builder.create("SearchableAttributeVisible");
        stringXMLDef2.addPropertyDefinition(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching2.addSearchableDefinition(stringXMLDef2.build());
        workDoc_Matching2.route("");

        // do search with attribute using doc type 1... make sure both docs are returned
        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName1);
        addSearchableAttribute(criteria, TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY,
                TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        DocumentSearchResults results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 2, results.getSearchResults().size());

        // do search with attribute using doc type 2... make sure both docs are returned
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName2);
        addSearchableAttribute(criteria, TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 2, results.getSearchResults().size());

        // do search without attribute using doc type 1... make sure both docs are returned
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName1);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 2, results.getSearchResults().size());

        // do search without attribute using doc type 2... make sure both docs are returned
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName2);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 2, results.getSearchResults().size());

        String documentTypeName3 = "SearchDocType_DefaultCustomProcessor_3";
        WorkflowDocument workDoc_Matching3 = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(userNetworkId), documentTypeName3);
    	DocumentType docType3 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName3);
        WorkflowAttributeDefinition.Builder stringXMLDef3 = WorkflowAttributeDefinition.Builder.create("SearchableAttributeVisible");
        stringXMLDef3.addPropertyDefinition(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching3.addSearchableDefinition(stringXMLDef3.build());
        workDoc_Matching3.route("");

        // do search with attribute using doc type 3... make sure 1 doc is returned
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName3);
        addSearchableAttribute(criteria, TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        // do search without attribute using doc type 3... make sure 1 doc is returned
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName3);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        WorkflowDocument workDoc_NonMatching2 = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(userNetworkId), documentTypeName2);
        WorkflowAttributeDefinition.Builder stringXMLDef1a = WorkflowAttributeDefinition.Builder.create("SearchableAttributeVisible");
        // TODO delyea - adding underscore below invalidates via REGEX but doesn't blow up on route or addSearchable?
        String searchAttributeValue = TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE + "nonMatching";
        stringXMLDef1a.addPropertyDefinition(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, searchAttributeValue);
        workDoc_NonMatching2.addSearchableDefinition(stringXMLDef1a.build());
        workDoc_NonMatching2.route("");

        // do search with attribute using doc type 1... make sure 1 doc is returned
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName1);
        addSearchableAttribute(criteria, TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, searchAttributeValue);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 1, results.getSearchResults().size());

        // do search without attribute using doc type 1... make sure all 3 docs are returned
        criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(documentTypeName1);
        results = docSearchService.lookupDocuments(user.getPrincipalId(), criteria.build());
        assertEquals("Search results should have one document.", 3, results.getSearchResults().size());
    }
}
