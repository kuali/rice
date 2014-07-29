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
package org.kuali.rice.krad.demo.uif.library.containers;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoContainerCollectionGroupAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionGroupView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionGroupView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Containers");
        waitAndClickByLinkText("Collection Group");
    }

    protected void testLibraryContainerCollectionGroupTableLayout() throws Exception {
       waitForElementPresentByXpath("//div[@id='Demo-CollectionGroup-Example1']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-disclosureContent']/div[2]/table");
    }
    
    protected void testLibraryContainerCollectionGroupStackedLayout() throws Exception {
        selectByName("exampleShown","Stacked Layout");
        waitForElementPresentByXpath("//div[@id='Demo-CollectionGroup-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-disclosureContent']/div[2]/div/table");
        waitForElementPresentByXpath("//div[@id='Demo-CollectionGroup-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-disclosureContent']/div[2]/div[2]/table");
        waitForElementPresentByXpath("//div[@id='Demo-CollectionGroup-Example2']/div[@class='uif-verticalBoxLayout clearfix']/div/div[@class='uif-disclosureContent']/div[2]/div[3]/table");
        
     }
    
    @Test
    public void testContainerCollectionGroupBookmark() throws Exception {
        testLibraryContainerCollectionGroupTableLayout();
        testLibraryContainerCollectionGroupStackedLayout();
        passed();
    }

    @Test
    public void testContainerCollectionGroupNav() throws Exception {
        testLibraryContainerCollectionGroupTableLayout();
        testLibraryContainerCollectionGroupStackedLayout();
        passed();
    }  
}
