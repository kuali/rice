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
package org.kuali.rice.krad.demo.uif.library.general;

import org.junit.Ignore;
import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoGeneralFeaturesRichMessagesAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-RichMessagesView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-RichMessagesView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("General Features");
        waitAndClickByLinkText("Rich Messages");
    }

    protected void testGeneralFeaturesRichMessagesHtml() throws Exception {
        waitAndClickByLinkText("Html");
        assertElementPresentByXpath("//p[@data-parent='Demo-RichMessages-Example1']/b");
        assertElementPresentByXpath("//p[@data-parent='Demo-RichMessages-Example1']/br");
    }
    
    protected void testGeneralFeaturesRichMessagesCompByIndex() throws Exception {
        waitAndClickByLinkText("Comp. by Index");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example2']/div/input");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example2']/a[@class='uif-link inlineBlock']");
    }
    
    protected void testGeneralFeaturesRichMessagesCompById() throws Exception {
        waitAndClickByLinkText("Comp by id");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example3']/div/label");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example3']/div/input");
    }
    
    protected void testGeneralFeaturesRichMessagesColor() throws Exception {
        waitAndClickByLinkText("Color");
        assertElementPresentByXpath("//p[@data-parent='Demo-RichMessages-Example4']/span[@style='color: green;']");
        assertElementPresentByXpath("//p[@data-parent='Demo-RichMessages-Example4']/span[@style='color: blue;']");
    }
    
    protected void testGeneralFeaturesRichMessagesCss() throws Exception {
        waitAndClickByLinkText("CSS");
        assertElementPresentByXpath("//p[@data-parent='Demo-RichMessages-Example5']/span[@class='fl-text-underline fl-text-larger']");
    }
    
    protected void testGeneralFeaturesRichMessagesLink() throws Exception {
        waitAndClickByLinkText("Link");
        assertElementPresentByXpath("//p[@data-parent='Demo-RichMessages-Example13']/a");
    }
    
    protected void testGeneralFeaturesRichMessagesAction() throws Exception {
        waitAndClickByLinkText("Action");
        waitAndClick(By.xpath("//p[@data-parent='Demo-RichMessages-Example14'][1]/a"));
        assertJgrowlText("Sample Message Text. Data passed: none");
        waitAndClick(By.className("jGrowl-close"));

        waitAndClickByLinkText("Action"); // default is loaded after jGrowl display
        waitAndClick(By.xpath("//p[@data-parent='Demo-RichMessages-Example14'][2]/a"));
        assertJgrowlText("Sample Message Text. Data passed: none");
        waitAndClick(By.className("jGrowl-close"));

        waitAndClickByLinkText("Action"); // default is loaded after jGrowl display
        waitAndClick(By.xpath("//p[@data-parent='Demo-RichMessages-Example14'][3]/a"));
        assertJgrowlText("Sample Message Text. Data passed: You passed data");
        waitAndClick(By.className("jGrowl-close"));

        waitAndClickByLinkText("Action"); // default is loaded after jGrowl display
        waitAndClick(By.xpath("//p[@data-parent='Demo-RichMessages-Example14'][4]/a"));
        assertJgrowlText("Sample Message Text. Data passed: none");
        waitAndClick(By.className("jGrowl-close"));

        waitAndClickByLinkText("Action"); // default is loaded after jGrowl display
        waitAndClick(By.xpath("//p[@data-parent='Demo-RichMessages-Example14'][5]/a"));
        assertJgrowlText("Sample Message Text. Data passed: none");
        waitAndClick(By.className("jGrowl-close"));
    }
    
    protected void testGeneralFeaturesRichMessagesCombine() throws Exception {
        waitAndClickByLinkText("Combine");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example6']/button");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example6']/span/div/input");
    }
    
    protected void testGeneralFeaturesRichMessagesInLabels() throws Exception {
        waitAndClickByLinkText("In Labels");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example7']/label/span[@style='color: green;']");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example7']/input");
    }
    
    protected void testGeneralFeaturesRichMessagesWInputField() throws Exception {
        waitAndClickByLinkText("W/ InputField");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example8']/div/button");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example8']/input");
    }
    
    protected void testGeneralFeaturesRichMessagesWSpringEL() throws Exception {
        waitAndClickByLinkText("W/ SpringEL");
        assertElementPresentByXpath("//p[@data-parent='Demo-RichMessages-Example9']/span[@style='color: green;']");
    }
    
    protected void testGeneralFeaturesRichMessagesImages() throws Exception {
        waitAndClickByLinkText("Images");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example10']/ul/li/img");
    }
    
    protected void testGeneralFeaturesRichMessagesEscapeChar() throws Exception {
        waitAndClickByLinkText("Escape char");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example11']/span[@style='color: green;']/b");
    }
    
    protected void testGeneralFeaturesRichMessagesEscapeCheckboxRadio() throws Exception {
        waitAndClickByLinkText("Checkboxes/Radio");
        assertElementPresentByXpath("//div[@data-parent='Demo-RichMessages-Example12']/fieldset/span/label/span[@style='color: blue;']");
    }
    
    @Test
    public void testGeneralFeaturesRichMessagesBookmark() throws Exception {
        testRichMessages();
        passed();
    }

    @Test
    public void testGeneralFeaturesRichMessagesNav() throws Exception {
        testRichMessages();
        passed();
    }

    @Test
    @Ignore // convience method for page redesign
    public void testHtmlNav() throws Exception {
        testGeneralFeaturesRichMessagesHtml();
    }

    @Test
    @Ignore // convience method for page redesign
    public void testGeneralFeaturesRichMessagesCompByIndexNav() throws Exception {
        testGeneralFeaturesRichMessagesCompByIndex();
    }

    @Test
    @Ignore // convience method for page redesign
    public void testGeneralFeaturesRichMessagesCssNav() throws Exception {
        testGeneralFeaturesRichMessagesCss();
    }

    @Test
    @Ignore // convience method for page redesign
    public void testGeneralFeaturesRichMessagesCombineNav() throws Exception {
        testGeneralFeaturesRichMessagesCombine();
    }

    @Test
    @Ignore // convience method for page redesign
    public void testGeneralFeaturesRichMessagesWInputFieldNav() throws Exception {
        testGeneralFeaturesRichMessagesWInputField();
    }

    @Test
    @Ignore // convience method for page redesign
    public void testGeneralFeaturesRichMessagesWSpringELNav() throws Exception {
        testGeneralFeaturesRichMessagesWSpringEL();
    }


    @Test
    @Ignore // convience method for page redesign
    public void testGeneralFeaturesRichMessagesImagesNav() throws Exception {
        testGeneralFeaturesRichMessagesImages();
    }

    @Test
    @Ignore // convience method for page redesign
    public void testGeneralFeaturesRichMessagesEscapeCheckboxRadioNav() throws Exception {
        testGeneralFeaturesRichMessagesEscapeCheckboxRadio();
    }

    @Test
    @Ignore // convience method for page redesign
    public void testGeneralFeaturesRichMessagesInLabelsNav() throws Exception {
        testGeneralFeaturesRichMessagesInLabels();
    }

    @Test
    @Ignore // convience method for page redesign
    public void testGeneralFeaturesRichMessagesCompByIdNav() throws Exception {
        testGeneralFeaturesRichMessagesCompById();
    }

    @Test
    @Ignore // convience method for page redesign
    public void testGeneralFeaturesRichMessagesActionNav() throws Exception {
        testGeneralFeaturesRichMessagesAction();
    }

    private void testRichMessages() throws Exception {
        testGeneralFeaturesRichMessagesHtml();
        testGeneralFeaturesRichMessagesCompByIndex();
        testGeneralFeaturesRichMessagesColor();
        testGeneralFeaturesRichMessagesCss();
        testGeneralFeaturesRichMessagesLink();
        testGeneralFeaturesRichMessagesCombine();
        testGeneralFeaturesRichMessagesWInputField();
        testGeneralFeaturesRichMessagesWSpringEL();
        testGeneralFeaturesRichMessagesImages();
        testGeneralFeaturesRichMessagesEscapeChar();
        testGeneralFeaturesRichMessagesEscapeCheckboxRadio();
        testGeneralFeaturesRichMessagesInLabels();
        testGeneralFeaturesRichMessagesCompById();
        testGeneralFeaturesRichMessagesAction(); // flakiest last
    }
}
