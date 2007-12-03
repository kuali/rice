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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.docsearch.xml.DocumentSearchXMLResultProcessor;
import edu.iu.uis.eden.docsearch.xml.DocumentSearchXMLResultProcessorImpl;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.lookupable.Column;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.ClassLoaderUtils;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class CustomSearchAttributesTest extends KEWTestCase {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomSearchAttributesTest.class);

    protected void loadTestData() throws Exception {
        loadXmlFile("SearchAttributeConfig.xml");
    }

    private SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,String value,DocumentType docType) {
    	SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(key,value,key);
    	Field field = getFieldByFormKey(docType, key);
    	if (field != null) {
        	sacc.setSearchableAttributeValue(DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType()));
        	sacc.setRangeSearch(field.isMemberOfRange());
        	sacc.setAllowWildcards(field.isAllowingWildcards());
        	sacc.setAutoWildcardBeginning(field.isAutoWildcardAtBeginning());
        	sacc.setAutoWildcardEnd(field.isAutoWildcardAtEnding());
        	sacc.setCaseSensitive(field.isCaseSensitive());
        	sacc.setSearchInclusive(field.isInclusive());
            sacc.setSearchable(field.isSearchable());
            sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
    	}
    	return sacc;
    }
    
    private Field getFieldByFormKey(DocumentType docType, String formKey) {
    	if (docType == null) {
    		return null;
    	}
		for (SearchableAttribute searchableAttribute : docType.getSearchableAttributes()) {
			for (Row row : searchableAttribute.getSearchingRows()) {
				for (Field field : row.getFields()) {
					if (field.getPropertyName().equals(formKey)) {
						return field;
					}
				}
			}
		}
		return null;
    }

    @Test public void testCustomDocumentSearchGeneratorUse() throws Exception {
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType");
    	assertTrue("The document search Generator class should be of type CustomDocumentSearchGenerator",(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchGenerator()) instanceof StandardDocumentSearchGenerator));
    	docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType_DefaultCustomProcessor");
    	LOG.error("testCustomDocumentSearchGeneratorUse() Class name is " + docType.getDocumentSearchGenerator().getClass().getName()); 
    	assertTrue("The document search Generator class should be of type CustomDocumentSearchGenerator",(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchGenerator()) instanceof CustomDocumentSearchGenerator));
    }

    @Test public void testCustomDocumentSearchResultProcessorOverrideUse() throws Exception {
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType");
    	assertTrue("The document search processor class should be of type StandardDocumentSearchResultProcessor",(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchResultProcessor()) instanceof StandardDocumentSearchResultProcessor));

    	docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType_DefaultCustomProcessor");
    	assertTrue("The document search processor class should be of type DocumentSearchXMLResultProcessorImpl",(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchResultProcessor()) instanceof DocumentSearchXMLResultProcessorImpl));

    	docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType2");
    	assertTrue("The document search processor class should be of type CustomSearchResultProcessor",(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchResultProcessor()) instanceof CustomSearchResultProcessor));
    }
    
    @Test public void testSearchXMLResultProcessorFunction() throws Exception {
        RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName("XMLResultProcessorDetails");
        DocumentSearchXMLResultProcessorImpl docSearchResult = new DocumentSearchXMLResultProcessorImpl();
        docSearchResult.setRuleAttribute(ruleAttribute);

        List<Column> columns = docSearchResult.getCustomDisplayColumns();
    	for (Iterator iter = columns.iterator(); iter.hasNext();) {
			Column column = (Column) iter.next();
			if (DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL.equals(column.getKey())) {
				assertEquals("Attribute xml is not populating column 'sortable' value correctly", "true", column.getSortable());
				assertEquals("Attribute xml is not populating column 'title' value correctly", "", column.getColumnTitle());
			} else if (DocumentSearchResult.PROPERTY_NAME_DOCUMENT_TITLE.equals(column.getKey())) {
				assertEquals("Attribute xml is not populating column 'sortable' value correctly", "false", column.getSortable());
				assertEquals("Attribute xml is not populating column 'title' value correctly", null, column.getColumnTitle());
			} else if (DocumentSearchResult.PROPERTY_NAME_INITIATOR.equals(column.getKey())) {
				assertEquals("Attribute xml is not populating column 'sortable' value correctly", null, column.getSortable());
				assertEquals("Attribute xml is not populating column 'title' value correctly", "Initiator Dude", column.getColumnTitle());
			} else if (DocumentSearchResult.PROPERTY_NAME_ROUTE_STATUS_DESC.equals(column.getKey())) {
				assertEquals("Attribute xml is not populating column 'sortable' value correctly", null, column.getSortable());
				assertEquals("Attribute xml is not populating column 'title' value correctly", null, column.getColumnTitle());
			} else if (TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY.equals(column.getKey())) {
				assertEquals("Attribute xml is not populating column 'sortable' value correctly", "false", column.getSortable());
				assertEquals("Attribute xml is not populating column 'title' value correctly", null, column.getColumnTitle());
			} else {
				fail("Key value of custom column should never be anything except already checked values but is '" + column.getKey() + "'");
			}
		}
    	
    	assertEquals("Value of 'show all standard fields' should be default",DocumentSearchXMLResultProcessor.DEFAULT_SHOW_ALL_STANDARD_FIELDS_VALUE,docSearchResult.getShowAllStandardFields());
    	assertEquals("Value of 'override searchable attributes' should be default",DocumentSearchXMLResultProcessor.DEFAULT_OVERRIDE_SEARCHABLE_ATTRIBUTES_VALUE,docSearchResult.getOverrideSearchableAttributes());
    }
    
    private DocumentSearchResultComponents performSearch(String documentTypeName,String userNetworkId) throws EdenUserNotFoundException {
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId(userNetworkId));

        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType));
        return docSearchService.getList(user, criteria);
    }
    
    private void parseList(List<Column> columns, List<String> columnsRequired, List<String> columnsNotAllowed) {
    	// check to see if column should be excluded but is not
		for (Iterator iterator = columnsNotAllowed.iterator(); iterator.hasNext();) {
			String disallowedColumnKey = (String) iterator.next();
	        for (Iterator iter = columns.iterator(); iter.hasNext();) {
				Column currentColumn = (Column) iter.next();
				if (disallowedColumnKey.equals(currentColumn.getKey())) {
					fail("The column with key '" + currentColumn.getKey() + "' should not be in the list of columns to be displayed but was");
				}
	        }    	
		}

		// check to see if column should be in list but is not
		for (int i = 0; i < columnsRequired.size(); i++) {
			String requiredColumnKey = columnsRequired.get(i);
			Column testColumn = columns.get(i);
			if (!(requiredColumnKey.equals(testColumn.getKey()))) {
				fail("The column with key '" + requiredColumnKey + "' should be in the list of columns to be displayed (at location " + i + ") but was not");
			}
		}
    }

    @Test public void testCustomDocumentSearchXMLResultProcessor() throws Exception {
    	String searchableAttributeKey_Shown = "givenname";
    	String searchableAttributeKey_Hidden = "givenname_hidden";
    	/*    - test showAllStandard = *blank* && overrideSearchAtt = *blank* (default way of doing search) - XMLResultProcessorDefault
    	 *        - search attr xml hidden && search proc attr shown = shown
    	 *        - search attr xml shown && search proc attr hidden = hidden
    	 *        - standard fields = hidden 
    	 */
        String documentTypeName = "SearchDocType_DefaultCustomProcessor";
        String userNetworkId = "rkirkend";
        DocumentSearchResultComponents result = performSearch(documentTypeName, userNetworkId);
        /*  columns that should be shown
         *    - routeHeaderId
         *    - doctypelabel
         *    - givenname_hidden
         *    - doc title
         *    - init
         *    - docroutestat
         *    - routeLog
         *    
         *  columns that should not be shown
         *    - givenname
         *    - create date
         */
        parseList(result.getColumns(), Arrays.asList(new String[]{
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID,
        		DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL,
        		searchableAttributeKey_Hidden,
        		DocumentSearchResult.PROPERTY_NAME_DOCUMENT_TITLE,
        		DocumentSearchResult.PROPERTY_NAME_INITIATOR,
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_STATUS_DESC,
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_LOG
        		}), Arrays.asList(new String[]{
        				DocumentSearchResult.PROPERTY_NAME_DATE_CREATED,
        				searchableAttributeKey_Shown
        		}));

        /*    - test showAllStandard = false && overrideSearchAtt = true - XMLResultProcessorAllCustom
    	 *        - search attr xml hidden && search proc attr shown = shown
    	 *        - search attr xml shown && search proc attr hidden = hidden
    	 *        - standard fields = hidden 
    	 */        
        documentTypeName = "SearchDocType_AllCustomProcessor";
        result = performSearch(documentTypeName, userNetworkId);
        /*  columns that should be shown
         *    - routeHeaderId
         *    - doctypelabel
         *    - doc title
         *    - init
         *    - givenname_hidden
         *    - docroutestat
         *    - routeLog
         *    
         *  columns that should not be shown
         *    - givenname
         *    - create date
         */
        parseList(result.getColumns(), Arrays.asList(new String[]{
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID,
        		DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL,
        		DocumentSearchResult.PROPERTY_NAME_DOCUMENT_TITLE,
        		DocumentSearchResult.PROPERTY_NAME_INITIATOR,
        		searchableAttributeKey_Hidden,
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_STATUS_DESC,
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_LOG
        		}), Arrays.asList(new String[]{
        				DocumentSearchResult.PROPERTY_NAME_DATE_CREATED,
        				searchableAttributeKey_Shown
        		}));

        /*    - test showAllStandard = true && overrideSearchAtt = true - XMLResultProcessorSearchAttributeCustom
    	 *        - search attr xml hidden && search proc attr shown = shown
    	 *        - search attr xml shown && search proc attr hidden = hidden
    	 *        - standard fields = shown 
    	 *        - check order with standard fields before search fields
         */
        documentTypeName = "SearchDocType_SearchAttCustomProcessor";
        result = performSearch(documentTypeName, userNetworkId);
        /*  columns that should be shown
         *    - routeHeaderId
         *    - docTypeLabel
         *    - documentTitle
         *    - docRouteStatusCodeDesc
         *    - initiator
         *    - dateCreated
         *    - givenname_hidden
         *    - routeLog
         *    
         *  columns that should not be shown
         *    - givenname
         */
        parseList(result.getColumns(), Arrays.asList(new String[]{
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID,
        		DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL,
        		DocumentSearchResult.PROPERTY_NAME_DOCUMENT_TITLE,
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_STATUS_DESC,
				DocumentSearchResult.PROPERTY_NAME_INITIATOR,
				DocumentSearchResult.PROPERTY_NAME_DATE_CREATED,
        		searchableAttributeKey_Hidden,
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_LOG
        		}), Arrays.asList(new String[]{
        				searchableAttributeKey_Shown
        		}));

        /*    - test showAllStandard = false && overrideSearchAtt = false - XMLResultProcessorStandardCustom
    	 *        - search attr xml hidden && search proc attr show = hidden
    	 *        - search attr xml shown && search proc attr hidden = shown
    	 *        - standard fields = hidden 
    	 *        - check order with search fields after custom standard fields
         */
        documentTypeName = "SearchDocType_StandardCustomProcessor";
        result = performSearch(documentTypeName, userNetworkId);
        /*  columns that should be shown
         *    - routeHeaderId
         *    - doc title
         *    - doctypelabel
         *    - docroutestat
         *    - init
         *    - givenname
         *    - routeLog
         *    
         *  columns that should not be shown
         *    - givenname_hidden
         *    - create date
         */
        parseList(result.getColumns(), Arrays.asList(new String[]{
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID,
        		DocumentSearchResult.PROPERTY_NAME_DOCUMENT_TITLE,
        		DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL,
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_STATUS_DESC,
        		DocumentSearchResult.PROPERTY_NAME_INITIATOR,
        		searchableAttributeKey_Shown,
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_LOG
        		}), Arrays.asList(new String[]{
        				DocumentSearchResult.PROPERTY_NAME_DATE_CREATED,
                		searchableAttributeKey_Hidden
        		}));

        /*    - test showAllStandard = true && overrideSearchAtt = false (default way of doing search) - XMLResultProcessorNormalCustom
    	 *        - search attr xml hidden && search proc attr show = hidden
    	 *        - search attr xml shown && search proc attr hidden = shown
    	 *        - standard fields = shown 
    	 *        - check order with standard fields before search fields
         */
        documentTypeName = "SearchDocType_NormalCustomProcessor";
        result = performSearch(documentTypeName, userNetworkId);
        /*  columns that should be shown
         *    - routeHeaderId
         *    - docTypeLabel
         *    - documentTitle
         *    - docRouteStatusCodeDesc
         *    - initiator
         *    - dateCreated
         *    - givenname
         *    - routeLog
         *    
         *  columns that should not be shown
         *    - givenname_hidden
         */
        parseList(result.getColumns(), Arrays.asList(new String[]{
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID,
        		DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL,
        		DocumentSearchResult.PROPERTY_NAME_DOCUMENT_TITLE,
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_STATUS_DESC,
				DocumentSearchResult.PROPERTY_NAME_INITIATOR,
				DocumentSearchResult.PROPERTY_NAME_DATE_CREATED,
        		searchableAttributeKey_Shown,
        		DocumentSearchResult.PROPERTY_NAME_ROUTE_LOG
        		}), Arrays.asList(new String[]{
                		searchableAttributeKey_Hidden
        		}));
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
        WorkflowDocument workDoc_Matching1 = new WorkflowDocument(new NetworkIdVO(userNetworkId), documentTypeName1);
    	DocumentType docType1 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName1);
        WorkflowAttributeDefinitionVO stringXMLDef1 = new WorkflowAttributeDefinitionVO("SearchableAttributeVisible");
        stringXMLDef1.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching1.addSearchableDefinition(stringXMLDef1);
        workDoc_Matching1.routeDocument("");

        String documentTypeName2 = "SearchDocType_DefaultCustomProcessor_2";
        WorkflowDocument workDoc_Matching2 = new WorkflowDocument(new NetworkIdVO(userNetworkId), documentTypeName2);
    	DocumentType docType2 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName2);
        WorkflowAttributeDefinitionVO stringXMLDef2 = new WorkflowAttributeDefinitionVO("SearchableAttributeVisible");
        stringXMLDef2.addProperty(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE);
        workDoc_Matching2.addSearchableDefinition(stringXMLDef2);
        workDoc_Matching2.routeDocument("");
        
        // do search with attribute using doc type 1... make sure both docs are returned
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName1);
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have 2 documents.", 2, result.getSearchResults().size());

        // do search with attribute using doc type 1... make sure both docs are returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName1);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType1));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have 2 documents.", 2, result.getSearchResults().size());

        // do search with attribute using doc type 2... make sure both docs are returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName2);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType2));
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have 2 documents.", 2, result.getSearchResults().size());

        // do search without attribute using doc type 1... make sure both docs are returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName1);
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have 2 documents.", 2, result.getSearchResults().size());

        // do search without attribute using doc type 2... make sure both docs are returned
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTypeFullName(documentTypeName2);
        result = docSearchService.getList(user, criteria);
        assertEquals("Search results should have 2 documents.", 2, result.getSearchResults().size());
        
        String documentTypeName3 = "SearchDocType_DefaultCustomProcessor_3";
        WorkflowDocument workDoc_Matching3 = new WorkflowDocument(new NetworkIdVO(userNetworkId), documentTypeName3);
    	DocumentType docType3 = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName3);
        WorkflowAttributeDefinitionVO stringXMLDef3 = new WorkflowAttributeDefinitionVO("SearchableAttributeVisible");
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

        WorkflowDocument workDoc_NonMatching2 = new WorkflowDocument(new NetworkIdVO(userNetworkId), documentTypeName2);
        WorkflowAttributeDefinitionVO stringXMLDef1a = new WorkflowAttributeDefinitionVO("SearchableAttributeVisible");
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
