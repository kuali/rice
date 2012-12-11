/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.samplu.krad.compview;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;
import org.openqa.selenium.internal.seleniumemulation.GetValue;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

/**
 * tests that a line in a sub collection can be deleted
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DeleteSubCollectionLineLegacyIT extends WebDriverLegacyITBase{
    @Override
    public String getTestUrl() {
        return  "/kr-krad/uicomponents?viewId=UifCompView_KNS&methodToCall=start&readOnlyFields=field91";
    }

    @Test
    /**
     * tests that a line in a sub collection can be deleted
     */
    public void deleteSubCollectionLine() throws Exception {
        // click on collections page link
        waitAndClickByLinkText("Collections");
        Thread.sleep(5000);
        // wait for collections page to load by checking the presence of a sub collection line item

        waitForElementPresentByName("list4[0].subList[0].field1");
        // change a value in the line to be deleted
        waitAndTypeByName("list4[0].subList[0].field1", "selenium");
        // click the delete button
        waitAndClick("div[title='Line Summary \\'A\\' With Expression'].uif-group.uif-gridGroup.uif-collectionItem.uif-gridCollectionItem div.uif-group.uif-collectionGroup.uif-tableCollectionGroup.uif-tableSubCollection.uif-disclosure tr.odd button[data-loadingmessage='Deleting Line...'].uif-action.uif-secondaryActionButton.uif-smallActionButton");
//        waitAndClickByXpath("//div[@title='Line Summary \'A\' With Expression']/table/tbody/tr[1]/td[5]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button[@data-loadingmessage='Deleting Line...']");
        Thread.sleep(2000);
        // confirm that the input box containing the modified value is not present
        
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { 
                System.out.println("Loop ----- "+second);
                if (!"selenium".equals(getAttributeByName("list4[0].subList[0].field1","value")))
                    break;
                } catch (Exception e) {}
            Thread.sleep(1000);
        }
        // verify that the value has changed for the input box in the line that has replaced the deleted one
        assertNotSame("selenium", getAttributeByName("list4[0].subList[0].field1","value"));
        passed();
    }
}
