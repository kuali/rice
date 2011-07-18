/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.docsearch.xml.DocumentSearchXMLResultProcessor;
import org.kuali.rice.kew.docsearch.xml.DocumentSearchXMLResultProcessorImpl;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.web.ui.Column;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CustomDocumentSearchResultProcessorTest extends DocumentSearchTestBase {
//	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomSearchAttributesTest.class);

    protected void loadTestData() throws Exception {
        loadXmlFile("SearchAttributeConfig.xml");
    }

    @Test public void testCustomDocumentSearchResultProcessorOverrideUse() throws Exception {
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType");
    	assertEquals("The document search Processor class is incorrect.",StandardDocumentSearchResultProcessor.class,(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchResultProcessor())).getClass());

    	docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType_DefaultCustomProcessor");
    	assertEquals("The document search Processor class is incorrect.",DocumentSearchXMLResultProcessorImpl.class,(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchResultProcessor())).getClass());

    	docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName("SearchDocType2");
    	assertEquals("The document search Processor class is incorrect.",CustomSearchResultProcessor.class,(ClassLoaderUtils.unwrapFromProxy(docType.getDocumentSearchResultProcessor())).getClass());
    }

    @Test public void testSearchXMLResultProcessorFunction() throws Exception {
        RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName("XMLResultProcessorDetails");
        DocumentSearchXMLResultProcessorImpl docSearchResult = new DocumentSearchXMLResultProcessorImpl();
        docSearchResult.setRuleAttribute(ruleAttribute);

        List<Column> columns = docSearchResult.getCustomDisplayColumns();
    	for (Iterator iter = columns.iterator(); iter.hasNext();) {
    	    Column column = (Column) iter.next();
			if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL.equals(column.getPropertyName())) {
				assertEquals("Attribute xml is not populating column 'sortable' value correctly", "true", column.getSortable());
				assertEquals("Attribute xml is not populating column 'title' value correctly", "", column.getColumnTitle());
			} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE.equals(column.getPropertyName())) {
				assertEquals("Attribute xml is not populating column 'sortable' value correctly", "false", column.getSortable());
				assertEquals("Attribute xml is not populating column 'title' value correctly", null, column.getColumnTitle());
			} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR.equals(column.getPropertyName())) {
				assertEquals("Attribute xml is not populating column 'sortable' value correctly", null, column.getSortable());
				assertEquals("Attribute xml is not populating column 'title' value correctly", "Initiator Dude", column.getColumnTitle());
			} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC.equals(column.getPropertyName())) {
				assertEquals("Attribute xml is not populating column 'sortable' value correctly", null, column.getSortable());
				assertEquals("Attribute xml is not populating column 'title' value correctly", null, column.getColumnTitle());
			} else if (TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY.equals(column.getPropertyName())) {
				assertEquals("Attribute xml is not populating column 'sortable' value correctly", "false", column.getSortable());
				assertEquals("Attribute xml is not populating column 'title' value correctly", null, column.getColumnTitle());
			} else {
				fail("Key value of custom column should never be anything except already checked values but is '" + column.getPropertyName() + "'");
			}
		}

    	assertEquals("Value of 'show all standard fields' should be default",DocumentSearchXMLResultProcessor.DEFAULT_SHOW_ALL_STANDARD_FIELDS_VALUE,docSearchResult.getShowAllStandardFields());
    	assertEquals("Value of 'override searchable attributes' should be default",DocumentSearchXMLResultProcessor.DEFAULT_OVERRIDE_SEARCHABLE_ATTRIBUTES_VALUE,docSearchResult.getOverrideSearchableAttributes());
    }

    private DocumentSearchResultComponents performSearch(String documentTypeName,String userNetworkId) {
    	DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
        DocumentSearchService docSearchService = (DocumentSearchService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        Person person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(userNetworkId);

        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
        criteria.setDocTypeFullName(documentTypeName);
        criteria.addSearchableAttribute(createSearchAttributeCriteriaComponent(TestXMLSearchableAttributeString.SEARCH_STORAGE_KEY, TestXMLSearchableAttributeString.SEARCH_STORAGE_VALUE, docType));
        return docSearchService.getList(person.getPrincipalId(), criteria);
    }

    private void parseList(List<Column> columns, List<String> columnsRequired, List<String> columnsNotAllowed) {
    	// check to see if column should be excluded but is not
		for (Iterator iterator = columnsNotAllowed.iterator(); iterator.hasNext();) {
			String disallowedColumnKey = (String) iterator.next();
	        for (Iterator iter = columns.iterator(); iter.hasNext();) {
	            Column currentColumn = (Column) iter.next();
				if (disallowedColumnKey.equals(currentColumn.getPropertyName())) {
					fail("The column with key '" + currentColumn.getPropertyName() + "' should not be in the list of columns to be displayed but was");
				}
	        }
		}

		// check to see if column should be in list but is not
		for (int i = 0; i < columnsRequired.size(); i++) {
			String requiredColumnKey = columnsRequired.get(i);
			Column testColumn = columns.get(i);
			if (!(requiredColumnKey.equals(testColumn.getPropertyName()))) {
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
         *    - documentId
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
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL,
        		searchableAttributeKey_Hidden,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG
        		}), Arrays.asList(new String[]{
        				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED,
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
         *    - documentId
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
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR,
        		searchableAttributeKey_Hidden,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG
        		}), Arrays.asList(new String[]{
        				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED,
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
         *    - documentId
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
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC,
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR,
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED,
        		searchableAttributeKey_Hidden,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG
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
         *    - documentId
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
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR,
        		searchableAttributeKey_Shown,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG
        		}), Arrays.asList(new String[]{
        				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED,
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
         *    - documentId
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
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC,
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR,
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED,
        		searchableAttributeKey_Shown,
        		KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG
        		}), Arrays.asList(new String[]{
                		searchableAttributeKey_Hidden
        		}));
    }
}
