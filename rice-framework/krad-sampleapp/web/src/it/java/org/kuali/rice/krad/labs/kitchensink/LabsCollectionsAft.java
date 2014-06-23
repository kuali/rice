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
package org.kuali.rice.krad.labs.kitchensink;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

import static org.junit.Assert.assertNotSame;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsCollectionsAft extends LabsKitchenSinkBase {

    /**
     * /kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page7#UifCompView-Page7
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=UifCompView&pageId=UifCompView-Page7#UifCompView-Page7";

    /**
     * list4[0].subList[0].field1
     */
    public static final String FIELD_ELEMENT_NAME = "list4[0].subList[0].field1";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

	@Override
	protected void navigate() throws Exception {
		navigateToKitchenSink("Collections");
	}
	
	@Test
    public void testCollectionsBookmark() throws Exception {
        testCollections();
        testSubCollectionSize();
        testDeleteSubCollectionLine();
        passed();
    }

    @Test
    public void testCollectionsNav() throws Exception {
        testCollections();
        testSubCollectionSize();
        testDeleteSubCollectionLine();
        passed();
    }
    
    protected void testCollections() throws InterruptedException {
        // Wait for page to load
        waitForTextPresent("Collection Group rendered as a List", WebDriverUtils.configuredImplicityWait() * 3);

    	//Collection Group 1 - CollectionGroupTableLayout
    	waitForElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine dataTable']/tbody/tr[@class='uif-collectionAddItem odd']",
                "https://jira.kuali.org/browse/RICEQA-274 AFT Failure update LabsCollectionsAft");
    	assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine dataTable']/tbody/tr/td[@class='uif-collection-column-action ']");
    
    	//Collection Group 2 - CollectionGroupTableLayout with jQuery table features on
    	assertElementPresentByXpath("//div[@id='collection2_disclosureContent']/div/table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine dataTable']/tbody/tr[@class='uif-collectionAddItem odd']");
    	assertElementPresentByXpath("//div[@id='collection2_disclosureContent']/div/div[@class='dataTables_length']/label/select");
    	assertElementPresentByXpath("//div[@id='collection2_disclosureContent']/div/table[@class='table table-condensed table-bordered uif-tableCollectionLayout uif-hasAddLine dataTable']/tbody/tr/td[1]/div/fieldset/div/button");
    	
    	//Collection Group 6 - CollectionGroupTableLayout with jQuery table features on
    	assertElementPresentByXpath("//section[@class='uif-collectionItem uif-tableCollectionItem uif-collectionAddItem']/table[@class='table table-condensed table-bordered uif-gridLayout uif-table-fixed']");
    	assertElementPresentByXpath("//div[@class='dataTables_scrollBody']/table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr/td[@class='uif-collection-column-action ']");
    	
    	//Collection Group 3 - Stacked
    	assertElementPresentByXpath("//div[@id='collection3_disclosureContent']/section/table");
    	
    	//Collection Group 4 - Stacked Collection with a Table subcollection
    	assertElementPresentByXpath("//div[@id='collection4_disclosureContent']/section/table");
    	assertElementPresentByXpath("//div[@id='subCollection1_line0_disclosureContent']");
    	
    	//Collection Group 5 - Stacked Collection with a Stacked subcollection
    	assertElementPresentByXpath("//ul/li/div[@data-parent='UifCompView-CollectionList']");
    }

    protected void testSubCollectionSize() throws Exception {
        // wait for collections page to load by checking the presence of a sub collection line item
        for (int second = 0;; second++) {
            if (second >= waitSeconds)
                jiraAwareFail(TIMEOUT_MESSAGE
                        + " looking for "
                        + SUB_COLLECTION_UIF_DISCLOSURE_SPAN_UIF_HEADER_TEXT_SPAN_XPATH);
            try {
                if (isElementPresentByXpath("//span[@class='uif-headerText-span' and contains(text(),'SubCollection - (3 lines)')]")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        // verify that sub collection sizes are displayed as expected
        waitForElementPresentByXpath("//section[@id='subCollection1_line0']/header/div/label/a/span[contains(text(),'SubCollection - (3 lines)')]]");
        waitForElementPresentByXpath("//a[@id='subCollection1_line1_toggle']/span");
    }

    protected void testDeleteSubCollectionLine() throws Exception {
        // wait for collections page to load by checking the presence of a sub collection line item
        waitForElementPresentByName(FIELD_ELEMENT_NAME);

        // change a value in the line to be deleted
        waitAndTypeByName(FIELD_ELEMENT_NAME, "selenium");

        // click the delete button
        waitAndClickById("subCollection1_line0_del_line0_line0");
        Thread.sleep(2000);

        // confirm that the input box containing the modified value is not present
        for (int second = 0;; second++) {
            if (second >= waitSeconds)fail(TIMEOUT_MESSAGE);

            try {
                if (!"selenium".equals(waitAndGetAttributeByName(FIELD_ELEMENT_NAME, "value")))
                    break;
            } catch (Exception e) {}

            Thread.sleep(1000);
        }

        // verify that the value has changed for the input box in the line that has replaced the deleted one
        assertNotSame("selenium", waitAndGetAttributeByName(FIELD_ELEMENT_NAME, "value"));
    }
}
