/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.lookup;

import org.junit.Test;
import org.kuali.rice.RiceTestCase;
import org.kuali.rice.test.htmlunit.HtmlUnitUtil;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class BaseLookupTest extends RiceTestCase {

    @Test public void testLookupable() throws Exception {
        
        HtmlPage lookupPage = HtmlUnitUtil.gotoPageAndLogin(HtmlUnitUtil.BASE_URL + "/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.sampleu.travel.bo.TravelAccount&returnLocation=portal.do&hideReturnLink=true&docFormKey=88888888");
        assertEquals("Kuali :: Lookup", lookupPage.getTitleText());
        
        HtmlForm lookupForm = (HtmlForm)lookupPage.getFormByName("KualiForm");
        HtmlInput searchButton = (HtmlInput)lookupForm.getInputByName("methodToCall.search");
        HtmlPage searchResultsPage = (HtmlPage)searchButton.click();
        HtmlTable resultsTable = (HtmlTable)searchResultsPage.getHtmlElementById("row");        
        HtmlTableRow row1 = (HtmlTableRow)resultsTable.getRow(0);
        HtmlTableCell cell1 = (HtmlTableCell)row1.getCell(0);
        
        assertNotNull(resultsTable);   
    }
    
}
