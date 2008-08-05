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
package edu.iu.uis.eden.docsearch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaVO;
import org.kuali.rice.kew.docsearch.web.DocumentSearchForm;
import org.kuali.rice.kew.user.AuthenticationUserId;
import org.kuali.rice.kew.user.UserUtils;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.workflow.test.KEWHtmlUnitTestCase;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Tests the web GUI for the Document Search.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentSearchActionTest extends KEWHtmlUnitTestCase {
    
    private static final String DOCUMENT_SEARCH_FORM_NAME = "DocumentSearchForm";
    private static final String DOCUMENT_TYPE_NAME_HIDE_SPECIFIC = "DocumentSearchActionTest_HideSpecific";
    private static final String DOCUMENT_TYPE_NAME_HIDE_ALL = "DocumentSearchActionTest_HideAll";
    private static final String DOCUMENT_TYPE_NAME_NO_PROCESSOR = "DocumentSearchActionTest_NoProcessor";
    private static final String SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME = "propertyField[0].value";

    protected void loadTestData() throws Exception {
        loadXmlFile("DocumentSearchWebConfig.xml");
    }       
    
    @Test public void testCustomSearchCriteriaProcessor_HideSpecificFields() throws Exception {
        Map<String,String> expectedValues = new HashMap<String,String>();
        expectedValues.put("criteria.docTypeFullName", DOCUMENT_TYPE_NAME_HIDE_SPECIFIC);

        HtmlPage basicSearchPage = performLogin(QUICKSTART_USER_NETWORK_ID, "DocumentSearch.do?criteria.docTypeFullName=" + DOCUMENT_TYPE_NAME_HIDE_SPECIFIC);
        checkStandardCriteriaFields(basicSearchPage, expectedValues, Arrays.asList(new String[]{"criteria.docTypeFullName","criteria.initiator","fromDateCreated"}), Arrays.asList(new String[]{}));
        verifySearchableAttribute(true,basicSearchPage, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), true);
        HtmlPage basicSearchResultPage = (HtmlPage) basicSearchPage.getFormByName(DOCUMENT_SEARCH_FORM_NAME).getInputByName("methodToCall.doDocSearch").click();
        checkStandardCriteriaFields(basicSearchResultPage, expectedValues, Arrays.asList(new String[]{"criteria.docTypeFullName","criteria.initiator","fromDateCreated"}), Arrays.asList(new String[]{}));
        verifySearchableAttribute(true,basicSearchResultPage, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), true);

        // move to advanced search
        HtmlPage advancedSearchPage = (HtmlPage)((HtmlAnchor) basicSearchResultPage.getHtmlElementById("searchType")).click();
        checkStandardCriteriaFields(advancedSearchPage, expectedValues, Arrays.asList(new String[]{"criteria.docTypeFullName","criteria.initiator","criteria.docTitle"}), Arrays.asList(new String[]{}));
        verifySearchableAttribute(true,advancedSearchPage, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), false);
        HtmlPage advancedSearchResultPage = (HtmlPage) advancedSearchPage.getFormByName(DOCUMENT_SEARCH_FORM_NAME).getInputByName("methodToCall.doDocSearch").click();
        checkStandardCriteriaFields(advancedSearchResultPage, expectedValues, Arrays.asList(new String[]{"criteria.docTypeFullName","criteria.initiator","criteria.docTitle"}), Arrays.asList(new String[]{}));
        verifySearchableAttribute(true,advancedSearchResultPage, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), false);

        // move back to basic search
        HtmlPage basicSearchPage2 = (HtmlPage)((HtmlAnchor) advancedSearchResultPage.getHtmlElementById("searchType")).click();
        checkStandardCriteriaFields(basicSearchPage2, expectedValues, Arrays.asList(new String[]{"criteria.docTypeFullName","criteria.initiator","fromDateCreated"}), Arrays.asList(new String[]{}));
        verifySearchableAttribute(true,basicSearchPage2, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), true);
        HtmlPage basicSearchResultPage2 = (HtmlPage) basicSearchPage2.getFormByName(DOCUMENT_SEARCH_FORM_NAME).getInputByName("methodToCall.doDocSearch").click();
        checkStandardCriteriaFields(basicSearchResultPage2, expectedValues, Arrays.asList(new String[]{"criteria.docTypeFullName","criteria.initiator","fromDateCreated"}), Arrays.asList(new String[]{}));
        verifySearchableAttribute(true,basicSearchResultPage2, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), true);
    }
    
    @Test public void testCustomSearchCriteriaProcessor_HideSpecificFields_Clear() throws Exception {
        Map<String,String> expectedValues = new HashMap<String,String>();
        expectedValues.put("criteria.docTypeFullName", DOCUMENT_TYPE_NAME_HIDE_SPECIFIC);
        Map<String,String> expectedValuesAfterClear = new HashMap<String,String>();
        expectedValuesAfterClear.put("criteria.docTypeFullName", "");

        // check basic search clear function
        HtmlPage basicSearchPage = performLogin(QUICKSTART_USER_NETWORK_ID, "DocumentSearch.do?criteria.docTypeFullName=" + DOCUMENT_TYPE_NAME_HIDE_SPECIFIC);
        checkStandardCriteriaFields(basicSearchPage, expectedValues, Arrays.asList(new String[]{"criteria.docTypeFullName","criteria.initiator","fromDateCreated"}), Arrays.asList(new String[]{}));
        verifySearchableAttribute(true,basicSearchPage, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), true);

        HtmlPage basicSearchClearedPage = (HtmlPage) basicSearchPage.getFormByName(DOCUMENT_SEARCH_FORM_NAME).getInputByName("methodToCall.clear").click();
        checkStandardCriteriaFields(basicSearchClearedPage, expectedValuesAfterClear, Arrays.asList(new String[]{"criteria.docTypeFullName"}), Arrays.asList(new String[]{"criteria.initiator","fromDateCreated"}));
        verifySearchableAttribute(false,basicSearchClearedPage, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), true);
        
        // check advanced search clear function
        HtmlPage advancedSearchPage = getPage("DocumentSearch.do?isAdvancedSearch=" + DocSearchCriteriaVO.ADVANCED_SEARCH_INDICATOR_STRING + "&criteria.docTypeFullName=" + DOCUMENT_TYPE_NAME_HIDE_SPECIFIC);
        checkStandardCriteriaFields(advancedSearchPage, expectedValues, Arrays.asList(new String[]{"criteria.docTypeFullName","criteria.initiator","criteria.docTitle"}), Arrays.asList(new String[]{}));
        verifySearchableAttribute(true,advancedSearchPage, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), false);

        HtmlPage advancedSearchClearedPage = (HtmlPage) advancedSearchPage.getFormByName(DOCUMENT_SEARCH_FORM_NAME).getInputByName("methodToCall.clear").click();
        checkStandardCriteriaFields(advancedSearchClearedPage, expectedValuesAfterClear, Arrays.asList(new String[]{"criteria.docTypeFullName"}), Arrays.asList(new String[]{"criteria.initiator","fromDateCreated"}));
        verifySearchableAttribute(false,advancedSearchClearedPage, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), true);
    }

    @Test public void testCustomSearchCriteriaProcessor_HideAllFieldsByProcessor() throws Exception {
        verifyHideAllFieldsSearch(DOCUMENT_TYPE_NAME_HIDE_ALL,"");
    }
    
    @Test public void testCustomSearchCriteriaProcessor_HideAllFieldsByUrlParams() throws Exception {
        verifyHideAllFieldsSearch(DOCUMENT_TYPE_NAME_NO_PROCESSOR,"searchCriteriaEnabled=false&headerBarEnabled=false");
    }
    
    private void verifyHideAllFieldsSearch(String documentTypeName, String urlParameters) throws Exception {
        Map<String,String> expectedValues = new HashMap<String,String>();
        expectedValues.put("criteria.docTypeFullName", documentTypeName);
        expectedValues.put("criteria.initiator", "delyea");
        String idType = "EMPLID";
        String searchAttributeUrlValue = "CURRENT_USER." + idType;
        expectedValues.put(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, UserUtils.getIdValue(idType, KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(QUICKSTART_USER_NETWORK_ID))));

        // check basic search
        HtmlPage basicSearchPage = performLogin(QUICKSTART_USER_NETWORK_ID, "DocumentSearch.do?criteria.initiator=delyea&criteria.docTypeFullName=" + documentTypeName + "&searchableAttributes=givenname:" + searchAttributeUrlValue + ((Utilities.isEmpty(urlParameters)) ? "" : "&" + urlParameters));
        checkStandardCriteriaFields(basicSearchPage, expectedValues, Arrays.asList(new String[]{"criteria.docTypeFullName","criteria.initiator","fromDateCreated"}), Arrays.asList(new String[]{}));
        verifySearchableAttribute(true,basicSearchPage, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), false);
        // test that header bar is hidden
        try {
            basicSearchPage.getHtmlElementById("headerTable");
            fail("Header Table should not exist on basic search page if criteria processor is working properly");
        } catch (ElementNotFoundException e) {
            // element should not be found if criteria processor is working properly
        }

        // check advanced search
        HtmlPage advancedSearchPage = getPage("DocumentSearch.do?criteria.initiator=delyea&isAdvancedSearch=" + DocSearchCriteriaVO.ADVANCED_SEARCH_INDICATOR_STRING + "&criteria.docTypeFullName=" + documentTypeName + "&searchableAttributes=givenname:" + searchAttributeUrlValue + ((Utilities.isEmpty(urlParameters)) ? "" : "&" + urlParameters));
        checkStandardCriteriaFields(advancedSearchPage, expectedValues, Arrays.asList(new String[]{"criteria.docTypeFullName","criteria.initiator","fromDateCreated","criteria.appDocId","criteria.docTitle"}), Arrays.asList(new String[]{}));
        verifySearchableAttribute(true,advancedSearchPage, SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME, expectedValues.get(SEARCH_ATTRIBUTE_FORM_FIELD_PROPERTY_NAME), false);
        // test that header bar is hidden
        try {
            advancedSearchPage.getHtmlElementById("headerTable");
            fail("Header Table should not exist on advanced search page if criteria processor is working properly");
        } catch (ElementNotFoundException e) {
            // element should not be found if criteria processor is working properly
        }
    }
    
    /**
     * This method is a helper to check generic standard criteria fields common to search
     */
    private void checkStandardCriteriaFields(HtmlPage searchPage, Map<String,String> expectedFieldValuesByKey, List<String> hiddenFieldAttributeNames, List<String> shownFieldAttributeNames) {
        assertEquals("Should be one form.", 1, searchPage.getForms().size());
        for (Iterator iterator = shownFieldAttributeNames.iterator(); iterator.hasNext();) {
            String fieldAttributeName = (String) iterator.next();
            HtmlInput input = searchPage.getFormByName(DOCUMENT_SEARCH_FORM_NAME).getInputByName(fieldAttributeName);
            assertFalse("Field with attribute name '" + fieldAttributeName + "' should not be hidden","hidden".equals(input.getTypeAttribute()));
            if (expectedFieldValuesByKey.containsKey(fieldAttributeName)) {
                assertEquals("Field with attribute name '" + fieldAttributeName + "' has incorrect value", expectedFieldValuesByKey.get(fieldAttributeName), input.getValueAttribute());
            }
        }
        for (Iterator iter = hiddenFieldAttributeNames.iterator(); iter.hasNext();) {
            String fieldAttributeName = (String) iter.next();
            HtmlInput input = searchPage.getFormByName(DOCUMENT_SEARCH_FORM_NAME).getInputByName(fieldAttributeName);
            assertTrue("Field with attribute name '" + fieldAttributeName + "' should be hidden","hidden".equals(input.getTypeAttribute()));
            if (expectedFieldValuesByKey.containsKey(fieldAttributeName)) {
                assertEquals("Field with attribute name '" + fieldAttributeName + "' has incorrect value", expectedFieldValuesByKey.get(fieldAttributeName), input.getValueAttribute());
            }
        }
    }
    
    private void verifySearchableAttribute(boolean elementShouldExist, HtmlPage searchPage, String propertyName, String expectedValue, boolean shouldHaveTextField) {
        List searchAttributeInputs = searchPage.getFormByName(DOCUMENT_SEARCH_FORM_NAME).getInputsByName(propertyName);
        if (!elementShouldExist) {
            // element should not exist on form
            if (!searchAttributeInputs.isEmpty()) {
                fail("Element with propertyName '" + propertyName + "' should not exist on form but does");
            }
            return;
        } else if (elementShouldExist && searchAttributeInputs.isEmpty()) {
            fail("Element with propertyName '" + propertyName + "' should exist on form but does not");
        }
        boolean foundDisplayedTextField = false;
        for (Iterator iterator = searchAttributeInputs.iterator(); iterator.hasNext();) {
            HtmlInput searchAttribute = (HtmlInput) iterator.next();
            if (expectedValue != null) {
                assertEquals("Searchable Attribute with propertyName '" + propertyName + "' should have been on search page",expectedValue,searchAttribute.getValueAttribute());
            }
            if (!"hidden".equals(searchAttribute.getTypeAttribute())) {
                foundDisplayedTextField = true;
            }
        }
        if (shouldHaveTextField) {
            assertTrue("Searchable Attribute with propertyName '" + propertyName + "' should have had at least one field on the form that was displayed and not hidden",foundDisplayedTextField);
        } else {
            assertFalse("Searchable Attribute with propertyName '" + propertyName + "' should not have had any non-hidden field types on form",foundDisplayedTextField);
        }
    }
   
}
