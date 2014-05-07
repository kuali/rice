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
package org.kuali.rice.krad.demo.uif.library.layoutmanagers;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * This class tests the Table Layout Cases included in Demo Library
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLayoutManagersTableLayoutAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-TableLayoutManagerView&methodToCall=start
     */
    public static final String BOOKMARK_URL =
            "/kr-krad/kradsampleapp?viewId=Demo-TableLayoutManagerView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Layout Managers");
        waitAndClickByLinkText("Table Layout");
    }

    protected void testLayoutManagersBasicTableLayout() throws Exception {
        selectByName("exampleShown","Basic Table Layout");
        waitForElementPresentByXpath("//section[@id='Demo-TableLayoutManager-Example1']/div/div/div/table");
        waitForElementPresentByXpath("//button[@id='Demo-TableLayoutManager-Example1_add']");
    }
    
    protected void testLayoutManagersJqueryTableFeatures() throws Exception {
        selectByName("exampleShown","jQuery Table Features");
        waitForElementPresentByXpath("//section[@id='Demo-TableLayoutManager-Example2']/div/div/div/table");
    }
    
    protected void testLayoutManagersAddBlankLineTop() throws Exception {
        selectByName("exampleShown","Add Blank Line Top");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example3']/div/div/table/tbody/tr/td[2]/div/input[@value='A']");
        waitAndClickByXpath("//section[@data-parent='Demo-TableLayoutManager-Example3']/div/button[contains(text(),'Add Line')]");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example3']/div/div/table/tbody/tr/td[2]/div/input[@value='']");
    }
    
    protected void testLayoutManagersAddBlankLineBottom() throws Exception {
        selectByName("exampleShown","Add Blank Line Bottom");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example4']/div/div/table/tbody/tr/td[2]/div/input[@value='A']");
        waitAndClickByXpath("//section[@data-parent='Demo-TableLayoutManager-Example4']/div/button[contains(text(),'Add Line')]");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example4']/div/div/table/tbody/tr[3]/td[2]/div/input[@value='']");
    }
    
    protected void testLayoutManagersAddViaLightBoxTop() throws Exception {
        selectByName("exampleShown","Add Via Lightbox TOP");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example5']/div/div/table/tbody/tr/td[2]/div/input[@value='a']");
        waitAndClickByXpath("//section[@data-parent='Demo-TableLayoutManager-Example5']/div/button[contains(text(),'Add Line')]");
        waitForElementPresentByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']");
        waitAndTypeByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']/div/div/div[@class='modal-body']/div[2]/div/input","1");
        waitAndTypeByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']/div/div/div[@class='modal-body']/div[4]/div/input","1");
        waitAndTypeByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']/div/div/div[@class='modal-body']/div[6]/div/input","1");
        waitAndTypeByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']/div/div/div[@class='modal-body']/div[8]/div/input","1");
        waitAndClickByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']/div/div/div[@class='modal-footer']/button[2]");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example5']/div/div/table/tbody/tr/td[2]/div/input[@value='1']");
    }
    
    protected void testLayoutManagersAddViaLightBoxBottom() throws Exception {
        selectByName("exampleShown","Add Via Lightbox BOTTOM");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example6']/div/div/table/tbody/tr/td[2]/div/input[@value='a']");
        waitAndClickByXpath("//section[@data-parent='Demo-TableLayoutManager-Example6']/div/button[contains(text(),'Add Line')]");
        waitForElementPresentByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']");
        waitAndTypeByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']/div/div/div[@class='modal-body']/div[2]/div/input","1");
        waitAndTypeByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']/div/div/div[@class='modal-body']/div[4]/div/input","1");
        waitAndTypeByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']/div/div/div[@class='modal-body']/div[6]/div/input","1");
        waitAndTypeByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']/div/div/div[@class='modal-body']/div[8]/div/input","1");
        waitAndClickByXpath("//section[@class='modal fade uif-cssGridGroup in' and @style='display: block;']/div/div/div[@class='modal-footer']/button[2]");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example6']/div/div/table/tbody/tr[4]/td[2]/div/input[@value='1']");
    }
    
    protected void testLayoutManagersActionColumnLeft() throws Exception {
        selectByName("exampleShown","Action Column Left");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example7']/div/div/table/tbody/tr/td/div/fieldset/div/button");
    }
    
    protected void testLayoutManagersActionColumn3() throws Exception {
        selectByName("exampleShown","Action Column 3");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example8']/div/div/table/tbody/tr/td[3]/div/fieldset/div/button");
    }
    
    protected void testLayoutManagersSaveRow() throws Exception {
        selectByName("exampleShown","Save Row");
        waitForElementPresentByXpath("//section[@data-parent='Demo-TableLayoutManager-Example9']/div/div/table/tbody/tr/td[6]/div/fieldset/div/button[contains(text(),'Save')]");
    }
    
    protected void testLayoutManagersMultirow() throws Exception {
        selectByName("exampleShown","Multirow");
        waitForElementPresentByXpath("//section[@id='Demo-TableLayoutManager-Example10']/div/div/div/table/thead/tr/th[@rowspan='2']");
    }
    
    protected void testLayoutManagersSeparateAddLine() throws Exception {
        selectByName("exampleShown","Separate Add Line");
        waitForElementPresentByXpath("//section[@class='uif-collectionItem uif-tableCollectionItem uif-collectionAddItem']");
    }

    @Test
    public void testLayoutManagersBoxLayoutBookmark() throws Exception {
    	testLayoutManagersAll();
    }

    @Test
    public void testLayoutManagersBoxLayoutNav() throws Exception {
    	testLayoutManagersAll();
    }
    
    private void testLayoutManagersAll() throws Exception {
    	testLayoutManagersBasicTableLayout();
    	testLayoutManagersJqueryTableFeatures();
    	testLayoutManagersAddBlankLineTop();
    	testLayoutManagersAddBlankLineBottom();
    	testLayoutManagersAddViaLightBoxTop();
    	testLayoutManagersAddViaLightBoxBottom();
    	testLayoutManagersActionColumnLeft();
    	testLayoutManagersActionColumn3();
    	testLayoutManagersSaveRow();
    	testLayoutManagersMultirow();
    	testLayoutManagersSeparateAddLine();
    	passed();
    }
}
