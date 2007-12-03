/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.lookupable.DocumentTypeLookupableImpl;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;

public class DocumentSearchTest extends KEWTestCase {

    DocumentSearchService docSearchService;
    UserService userService;

    protected void setUpTransaction() throws Exception {
        docSearchService = (DocumentSearchService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_SEARCH_SERVICE);
        userService = (UserService)KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
    }

    @Test public void testDocSearch() throws Exception {
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("bmcgough"));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        List searchResults = null;
        DocumentSearchResultComponents result = null;
        criteria.setDocTitle("*IN");
        criteria.setNamedSearch("bytitle");
        result = docSearchService.getList(user, criteria);
        criteria = new DocSearchCriteriaVO();
        criteria.setDocTitle("*IN-CFSG");
        criteria.setNamedSearch("for in accounts");
        result = docSearchService.getList(user, criteria);
        criteria = new DocSearchCriteriaVO();
        criteria.setFromDateApproved("09/16/2004");
        result = docSearchService.getList(user, criteria);
        criteria = new DocSearchCriteriaVO();
        criteria.setDocRouteNodeId("3");
        criteria.setDocRouteNodeLogic("equal");
        result = docSearchService.getList(user, criteria);
        user = userService.getWorkflowUser(new AuthenticationUserId("bmcgough"));
        SavedSearchResult savedSearchResults = docSearchService.getSavedSearchResults(user, "DocSearch.NamedSearch.bytitle");
        assertNotNull(savedSearchResults);
        savedSearchResults = docSearchService.getSavedSearchResults(user, "DocSearch.NamedSearch.for in accounts");
        assertNotNull(savedSearchResults);
    }

    @Test public void testGetNamedDocSearches() throws Exception {
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("bmcgough"));
        List namedSearches = docSearchService.getNamedSearches(user);
        assertNotNull(namedSearches);
    }

    /**
     * Test for https://test.kuali.org/jira/browse/KULWF-703
     */
    @Test public void testSearchEDENSERVICE_DOCS() throws Exception {
        loadXmlStream(new FileInputStream(getBaseDir()+"/src/main/config/bootstrap/BootstrapData.xml"));
//        loadXmlStream(new FileInputStream("conf/bootstrap/BootstrapRuleTemplateContent.xml"));
//        loadXmlStream(new FileInputStream("conf/bootstrap/BootstrapDocumentTypesContent.xml"));
//        loadXmlStream(new FileInputStream("conf/bootstrap/BootstrapRuleContent.xml"));

        Collection c = KEWServiceLocator.getDocumentTypeService().find(new DocumentType(), "EDENSERVICE-DOCS", true);
        assertNotNull(c);
        assertTrue(c.size() > 0);

        DocumentTypeLookupableImpl lookupable = new DocumentTypeLookupableImpl();
        Map fieldValues = new HashMap();
        fieldValues.put(DocumentTypeLookupableImpl.ACTIVE_IND_PROPERTY_NAME, "ALL");
        fieldValues.put(DocumentTypeLookupableImpl.DOC_TYP_PROPERTY_NAME, "");
        fieldValues.put(DocumentTypeLookupableImpl.DOC_TYP_FULL_NAME, "");
        fieldValues.put(DocumentTypeLookupableImpl.DOCUMENT_TYPE_ID_PROPERTY_NAME, "");
        fieldValues.put(DocumentTypeLookupableImpl.DOC_TYP_NAME_PROPERTY_NAME, "EDENSERVICE-DOCS");
        fieldValues.put(DocumentTypeLookupableImpl.BACK_LOCATION_KEY_NAME, "http://localhost:8080/en-dev/DocumentSearch.do");

        fieldValues.put(DocumentTypeLookupableImpl.DOC_FORM_KEY_NAME, "0");

        //String docTypReturn = (String) fieldConversions.get(DOC_TYP_FULL_NAME);

        List list = lookupable.getSearchResults(fieldValues, new HashMap());
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test public void testDefaultCreateDateSearchCriteria() throws Exception {
        WorkflowUser user = userService.getWorkflowUser(new AuthenticationUserId("bmcgough"));
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        DocumentSearchResultComponents result = docSearchService.getList(user, criteria);
        assertNotNull("Should have a date created value",criteria.getFromDateCreated());
        Calendar today = Calendar.getInstance();
        Calendar criteriaDate = Calendar.getInstance();
        criteriaDate.setTime(DocSearchUtils.convertStringDateToTimestamp(criteria.getFromDateCreated()));
        assertEquals("Criteria date minus today's date should equal the constant value", EdenConstants.DOCUMENT_SEARCH_NO_CRITERIA_CREATE_DATE_DAYS_AGO.doubleValue(), getDifferenceInDays(today, criteriaDate), 0);

        criteria = new DocSearchCriteriaVO();
        criteria.setDocTitle("testing");
        result = docSearchService.getList(user, criteria);
        assertNotNull("Should have a date created value",criteria.getFromDateCreated());
        today = Calendar.getInstance();
        criteriaDate = Calendar.getInstance();
        criteriaDate.setTime(DocSearchUtils.convertStringDateToTimestamp(criteria.getFromDateCreated()));
        assertEquals("Criteria date minus today's date should equal the constant value", EdenConstants.DOCUMENT_SEARCH_DOC_TITLE_CREATE_DATE_DAYS_AGO.doubleValue(), getDifferenceInDays(today, criteriaDate), 0);
    }

    private static double getDifferenceInDays(Calendar startCalendar, Calendar endCalendar) {
        // First, get difference in whole days
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);

        return (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000);
    }

}
