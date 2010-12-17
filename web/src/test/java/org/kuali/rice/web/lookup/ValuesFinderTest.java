/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.web.lookup;

import java.util.HashMap;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.web.test.WebTestBase;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

//import edu.sampleu.travel.document.keyvalue.TravelRequestType;

/**
 * This class tests various ValuesFinders classes.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Ignore("KULRICE-3011")
public class ValuesFinderTest extends WebTestBase {

    private HashMap<String, String> accountTypes = new HashMap<String, String>();
    private HashMap<String, String> requestTypes = new HashMap<String, String>();

    public ValuesFinderTest() {
	accountTypes.put("", "");
	accountTypes.put("CAT", "CAT - Clearing Account Type");
	accountTypes.put("EAT", "EAT - Expense Account Type");
	accountTypes.put("IAT", "IAT - Income Account Type");

	requestTypes.put("", "");
	requestTypes.put("TRT1", "Travel Request Type 1");
	requestTypes.put("TRT2", "Travel Request Type 2");
	requestTypes.put("TRT3", "Travel Request Type 3");
    }


    /**
     * This method tests to make sure the AccountType select list is using
     * it's ValuesFinder correctly.
     *
     * @throws Exception
     */
    @Test public void testTravelAccountSelect() throws Exception {
        final HtmlPage page = getPortalPage();

        HtmlPage page2 = clickOn(page, "travelAccountLookup");

        assertEquals("Kuali :: Lookup", page2.getTitleText());

        final HtmlForm kualiForm = (HtmlForm) page2.getForms().get(0);
        final HtmlSelect selected = (HtmlSelect) kualiForm.getSelectByName("extension.accountTypeCode");
        selected.getOptionSize();
        List options = selected.getOptions();
        assertEquals(accountTypes.size(), options.size());
        for (Object option: options) {
            String key = ((HtmlOption)option).getValueAttribute();
            assertEquals(accountTypes.get(key), ((HtmlOption)option).asText());
        }
    }

    /**
     * This method tests to make sure the TravelRequestType select list is using
     * it's ValuesFinder correctly.
     *
     * @throws Exception
     */
    @Test public void testTravelRequestTypesSelect() throws Exception {
        final HtmlPage page = getPortalPage();

        HtmlPage page2 = clickOn(page, "createTravelRequest");

        assertEquals("Kuali :: Travel Doc 2", page2.getTitleText());

        final HtmlForm kualiForm = (HtmlForm) page2.getForms().get(0);
        final HtmlSelect selected = (HtmlSelect) kualiForm.getSelectByName("document.requestType");
        selected.getOptionSize();
        List options = selected.getOptions();
        assertEquals(requestTypes.size(), options.size());
        for (Object option: options) {
            String key = ((HtmlOption)option).getValueAttribute();
            assertEquals(requestTypes.get(key), ((HtmlOption)option).asText());
        }
    }

    /**
     * This method tests to make sure the TravelRequestType ValuesFinder
     * behaves correctly out of the web environment - no Form in GlobalVariables
     *
     * @throws Exception
     */
//    @Test public void testTravelRequestTypeNonWeb() {
//	TravelRequestType travelRequestType = new TravelRequestType();
//	List<KeyLabelPair> keyValues = travelRequestType.getKeyValues();
//        assertEquals(requestTypes.size() - 1, keyValues.size());
//        for (KeyLabelPair keyValue: keyValues) {
//            assertEquals(requestTypes.get(keyValue.getKey()), keyValue.getLabel());
//        }
//    }
}
