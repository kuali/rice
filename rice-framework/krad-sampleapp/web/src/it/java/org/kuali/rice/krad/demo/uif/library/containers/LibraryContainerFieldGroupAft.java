/**
 * Copyright 2005-2018 The Kuali Foundation
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
public class LibraryContainerFieldGroupAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-FieldGroupView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-FieldGroupView";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Containers");
        waitAndClickByLinkText("Field Group");
    }

    protected void testLibraryContainerFieldGroupVertical() throws Exception {
       waitForElementPresentByXpath("//div[@class='uif-inputField uif-boxLayoutVerticalItem pull-left clearfix']/input[@name='inputField1']");
       waitForElementPresentByXpath("//div[@class='uif-inputField uif-boxLayoutVerticalItem pull-left clearfix']/input[@name='inputField2']");
    }
    
    protected void testLibraryContainerFieldGroupHorizontal() throws Exception {
    	waitAndClickByXpath("//a[contains(text(),'Field Group Horizontal')]");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-boxLayoutHorizontalItem']/input[@name='inputField1']");
        waitForElementPresentByXpath("//div[@class='uif-inputField uif-boxLayoutHorizontalItem']/input[@name='inputField2']");
     }
    
    @Test
    public void testContainerFieldGroupBookmark() throws Exception {
    	testLibraryContainerFieldGroupVertical();
    	testLibraryContainerFieldGroupHorizontal();
        passed();
    }

    @Test
    public void testContainerFieldGroupNav() throws Exception {
    	testLibraryContainerFieldGroupVertical();
    	testLibraryContainerFieldGroupHorizontal();
        passed();
    }  
}
