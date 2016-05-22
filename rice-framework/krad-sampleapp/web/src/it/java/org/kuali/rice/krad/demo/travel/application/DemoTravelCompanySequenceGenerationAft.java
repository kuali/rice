/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krad.demo.travel.application;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelCompanySequenceGenerationAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelCompany
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelCompany";

    public static final String DESCRIPTION_FIELD = "document.documentHeader.documentDescription";
    public static final String COMPANY_NAME_FIELD = "document.newMaintainableObject.dataObject.travelCompanyName";
    public static final String TRAVEL_COMPANY_ID = "lookupCriteria[travelCompanyId]";
    public static final String TRAVEL_CO_ID_XPATH = "//div[@data-label=\"Id\"]";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Travel Company Lookup");
    }

    protected void testTravelCompanyCreateNewDocumentSequenceGeneration() throws Exception {
        waitAndClickByLinkText("Create New");

        createTravelCompanyDoc();
        int travelCompanyIdDoc1 = Integer.parseInt(findElement(By.xpath(TRAVEL_CO_ID_XPATH)).getText());

        navigate();
        waitAndClickByLinkText("Create New");

        createTravelCompanyDoc();
        int travelCompanyIdDoc2 = Integer.parseInt(findElement(By.xpath(TRAVEL_CO_ID_XPATH)).getText());

        assertTrue("The Travel Company Id on the second document should be one higher than the first document.  "
                + "travelCompanyIdDoc1: " + travelCompanyIdDoc1 + ", travelCompanyIdDoc2: " + travelCompanyIdDoc2 ,
                travelCompanyIdDoc2 == travelCompanyIdDoc1 + 1);
    }

    protected void testTravelCompanyCopyDocumentSequenceGeneration() throws Exception {
        waitAndClickButtonByText(SEARCH);
        waitAndClickCopy();
        createTravelCompanyDoc();

        int travelCompanyIdDoc1 = determineNewTravelCompanyId();

        navigate();

        waitAndTypeByName(TRAVEL_COMPANY_ID, Integer.toString(travelCompanyIdDoc1));
        waitAndClickButtonByText(SEARCH);
        waitAndClickCopy();
        createTravelCompanyDoc();
        int travelCompanyIdDoc2 = determineNewTravelCompanyId();

        assertTrue("The Travel Company Id on the second document should be one higher than the first document.  "
                + "travelCompanyIdDoc1: " + travelCompanyIdDoc1 + ", travelCompanyIdDoc2: " + travelCompanyIdDoc2 ,
                travelCompanyIdDoc2 == travelCompanyIdDoc1 + 1);
    }

    private void createTravelCompanyDoc() throws Exception {
        waitAndTypeByName(DESCRIPTION_FIELD,"Travel Company Sequence Generation Test");
        String randomCode = RandomStringUtils.randomAlphabetic(5).toUpperCase();
        clearTextByName(COMPANY_NAME_FIELD);
        waitAndTypeByName(COMPANY_NAME_FIELD, "Company Name " + randomCode);

        waitAndClickSubmitByText();
        waitAndClickConfirmSubmitOk();
        waitForProgress("Loading...", WebDriverUtils.configuredImplicityWait() * 8);
        waitForTextPresent("Document was successfully submitted.", WebDriverUtils.configuredImplicityWait() * 2);
    }

    private int determineNewTravelCompanyId() throws Exception {
        int highestTravelCompanyId = 0;
        List<WebElement> travelCompanyIds = findElements(By.xpath(TRAVEL_CO_ID_XPATH));

        for (WebElement travelCompanyId: travelCompanyIds) {
            int potentialNewId = Integer.parseInt(travelCompanyId.getText());
            if (potentialNewId > highestTravelCompanyId) {
                highestTravelCompanyId =  potentialNewId;
            }
        }

        return highestTravelCompanyId;
    }

    @Test
    public void testTravelCompanyCreateNewDocumentSequenceGenerationBookmark() throws Exception {
        testTravelCompanyCreateNewDocumentSequenceGeneration();
        passed();
    }

    @Test
    public void testTravelCompanyCreateNewDocumentSequenceGenerationNav() throws Exception {
        testTravelCompanyCreateNewDocumentSequenceGeneration();
        passed();
    }

    @Test
    public void testTravelCompanyCopyDocumentSequenceGenerationBookmark() throws Exception {
        testTravelCompanyCopyDocumentSequenceGeneration();
        passed();
    }

    @Test
    public void testTravelCompanyCopyDocumentSequenceGenerationNav() throws Exception {
        testTravelCompanyCopyDocumentSequenceGeneration();
        passed();
    }
}
