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
package org.kuali.rice.transactiondocument;

import org.junit.Test;
import org.kuali.rice.KNSTestCase;
import org.kuali.rice.test.htmlunit.HtmlUnitUtil;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

public class BaseTransactionDocumentTest extends KNSTestCase {

    @Test public void testTransactionDocumentRoute() throws Exception {
        HtmlPage transDocPage = HtmlUnitUtil.gotoPageAndLogin(HtmlUnitUtil.BASE_URL + "/travelDocument2.do?methodToCall=docHandler&command=initiate&docTypeName=TravelRequest");
        HtmlForm form = transDocPage.getFormByName("KualiForm");
        form.getInputByName("document.documentHeader.financialDocumentDescription").setValueAttribute("description");
        ((HtmlTextArea)form.getTextAreasByName("document.documentHeader.explanation").get(0)).setText("justification");
        form.getInputByName("document.traveler").setValueAttribute("traveler");
        form.getInputByName("document.origin").setValueAttribute("origin");
        form.getInputByName("document.destination").setValueAttribute("destination");
        form.getSelectByName("document.requestType").setSelectedAttribute("TRT1", true);
        form.getInputByName("travelAccount.number").setValueAttribute("a1");
        transDocPage = (HtmlPage)((HtmlInput)form.getInputByName("methodToCall.insertAccount")).click();
        HtmlPage resultsPage = (HtmlPage)((HtmlInput)transDocPage.getFormByName("KualiForm").getInputByName("methodToCall.route")).click();
        
        assertTrue("Document was not successfully Routed", HtmlUnitUtil.pageContainsText(resultsPage, "Document was successfully submitted"));
        
    }
    
}
