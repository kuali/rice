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
package org.kuali.rice.krad.demo.uif.library.widgets;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoWidgetsSyntaxHighlighterAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-SyntaxHighlighterView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-SyntaxHighlighterView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "SyntaxHighlighter");
    }

    protected void testWidgetsSyntaxHighlighter() throws Exception {
        fireMouseOverEventByXpath("//section[@id='Demo-SyntaxHighlighter-Example1']/div/div");
        waitForElementPresentByXpath("//section[@id='Demo-SyntaxHighlighter-Example1']/div/div[@class='uif-syntaxHighlighter']/a[@class='uif-copyPaste']");
        
    }
    
    protected void testWidgetSyntaxHighlighterWithNoCopyButton() throws Exception {
       selectByName("exampleShown","Syntax Highlighter that does not display copying button");
       fireMouseOverEventByXpath("//section[@id='Demo-SyntaxHighlighter-Example2']/div/div[@class='uif-syntaxHighlighter']");
       waitForElementNotPresent(By.xpath("//section[@id='Demo-SyntaxHighlighter-Example2']/div/div[@class='uif-syntaxHighlighter']/a[@class='uif-copyPaste']"));
    }
    
    private void testAllSyntaxHighlighter() throws Exception {
    	testWidgetsSyntaxHighlighter();
    	testWidgetSyntaxHighlighterWithNoCopyButton();
	    passed();
    }

    @Test
    public void testWidgetsHelpBookmark() throws Exception {
    	testAllSyntaxHighlighter();
    }

    @Test
    public void testWidgetsHelpNav() throws Exception {
    	testAllSyntaxHighlighter();
    }
}
