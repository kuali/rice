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

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoWidgetsTooltipAft extends DemoLibraryBase {

	 /**
     * /kr-krad/kradsampleapp?viewId=Demo-TooltipView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-TooltipView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "Tooltip");
    }

    protected void testWidgetsTooltipHover() throws Exception {
        selectByName("exampleShown","Tooltip On Hover");
        fireMouseOverEventByName("dataField1");
        assertElementPresentByXpath("//td[@class='jquerybubblepopup-innerHtml' and contains(text(),'This is a helpful tooltip about this field')]");
    }

    protected void testWidgetsTooltipFocus() throws Exception {
        selectByName("exampleShown","Tooltip On Focus");
        waitAndTypeByXpath("//div[@id='Demo-Tooltip-Example2']/div/input[@name='dataField1']","");
        assertElementPresentByXpath("//td[@class='jquerybubblepopup-innerHtml' and contains(text(),'This tooltip appears when the field receives focus')]");
    }

    @Test
    public void testWidgetsTooltipBookmark() throws Exception {
        testWidgetsTooltipHover();
        testWidgetsTooltipFocus();
        passed();
    }

    @Test
    public void testWidgetsTooltipNav() throws Exception {
        testWidgetsTooltipHover();
        testWidgetsTooltipFocus();
        passed();
    }
}
