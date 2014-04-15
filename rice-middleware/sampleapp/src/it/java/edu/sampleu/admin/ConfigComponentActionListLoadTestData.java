package edu.sampleu.admin;

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

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.openqa.selenium.By;

/**
 * Test name does not end in Aft so this test isn't normally run, created to provide load test data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigComponentActionListLoadTestData extends ConfigComponentActionListAftBase {

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Test
    public void testCreate1000ApprovalComponentBookmark() throws InterruptedException {
//        String[] users = {"eric", "fran", "frank", "erin", "fred", "earl", "doug", "edna"};
        String[] users = {"eric"};
        for (String user: users) {
            for (int i = 0; i < 1000; i++) {
                System.out.println(user + " " + i);
                testCreateActionRequestPerson(new String[][]{{user, "A"}});
                uniqueString = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
                open(getBaseUrlString() + getTestUrl());
                waitForElementPresent(By.xpath("//iframe"));
            }
        }
    }
}
