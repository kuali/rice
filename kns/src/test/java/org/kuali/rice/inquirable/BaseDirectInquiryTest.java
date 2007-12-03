package org.kuali.rice.inquirable;

import java.net.URL;

import org.junit.Test;
import org.kuali.rice.testharness.HtmlUnitUtil;
import org.kuali.rice.testharness.KNSTestCase;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class BaseDirectInquiryTest extends KNSTestCase {
    
    @Test public void testDirectInquiry() throws Exception {
        final WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_6_0);
        HtmlPage travelDocPage = gotoPageAndLogin(webClient, HtmlUnitUtil.BASE_URL + "/travelDocument2.do?methodToCall=docHandler&command=initiate&docTypeName=TravelRequest");
        assertEquals("Kuali :: Travel Doc 2", travelDocPage.getTitleText());
        final HtmlForm form=(HtmlForm)travelDocPage.getForms().get(0);
        final HtmlTextInput travelAcctNumber = (HtmlTextInput) form.getInputByName("travelAccount.number");
        travelAcctNumber.setValueAttribute("a1");
        int idx1 = travelDocPage.asXml().indexOf("methodToCall.performInquiry.(!!edu.sampleu.travel.bo.TravelAccount!!).((#travelAccount.number:number#))");
        int idx2 = travelDocPage.asXml().indexOf("\"", idx1);
        // no popup for now
        webClient.setJavaScriptEnabled(false);
        final HtmlImageInput button = (HtmlImageInput) form.getInputByName(travelDocPage.asXml().substring(idx1, idx2).replace("&amp;", "&").replace("((&lt;&gt;))", "((<>))"));

        final HtmlPage inquiryPage=(HtmlPage) button.click();
        final HtmlForm form1=(HtmlForm)inquiryPage.getForms().get(0);
        assertTrue("Inquiry page should have 'Travel Account Inquiry' in title bar", HtmlUnitUtil.pageContainsText(inquiryPage, "Travel Account Inquiry"));
        //assertTrue(inquiryPage.asText().contains("Type: CAT - Clearing Account "));
        HtmlAnchor anchor = (HtmlAnchor)inquiryPage.getHtmlElementById("closeDirectInquiry");
        assertNotNull(anchor);
        final HtmlPage page=(HtmlPage)anchor.click();
        final HtmlForm form2=(HtmlForm)page.getForms().get(0);
        assertEquals("Kuali :: Travel Doc 2", page.getTitleText());
        final HtmlTextInput travelAcctNumber1 = (HtmlTextInput) form2.getInputByName("travelAccount.number");
        assertEquals(travelAcctNumber1.getValueAttribute(), "a1");

        // test account number can't be found
        travelAcctNumber1.setValueAttribute("");
        idx1 = page.asXml().indexOf("methodToCall.performInquiry.(!!edu.sampleu.travel.bo.TravelAccount!!).((#travelAccount.number:number#))");
        idx2 = page.asXml().indexOf("\"", idx1);
        final HtmlImageInput button1 = (HtmlImageInput) form2.getInputByName(page.asXml().substring(idx1, idx2).replace("&amp;", "&").replace("((&lt;&gt;))", "((<>))"));
        final HtmlPage errorPage=(HtmlPage) button1.click();
        assertTrue(errorPage.asText().contains("Kuali :: Errors in Request "));
        // there's no close button on error page, KualiError.jsp, which is what the inquiry will forward to if no input is specified
        //final HtmlForm form3=(HtmlForm)errorPage.getForms().get(0);
        //HtmlAnchor anchor1 = (HtmlAnchor)errorPage.getHtmlElementById("closeDirectInquiry");
        //assertNotNull(anchor1);
        //final HtmlPage page1=(HtmlPage)anchor1.click();
        //final HtmlForm form4=(HtmlForm)page1.getForms().get(0);
        assertEquals("Kuali :: Travel Doc 2", page.getTitleText());
        final HtmlForm form4=(HtmlForm)page.getForms().get(0);
        final HtmlTextInput travelAcctNumber2 = (HtmlTextInput) form4.getInputByName("travelAccount.number");
        assertEquals(travelAcctNumber2.getValueAttribute(), "");

    }
    private HtmlPage gotoPageAndLogin(WebClient webClient, String url) throws Exception {
// need webclient in the main test program, so create login here.  not using from htmlutil
    	HtmlPage loginPage = (HtmlPage)webClient.getPage(new URL(url));
        HtmlForm htmlForm = (HtmlForm)loginPage.getForms().get(0);
        HtmlSubmitInput button = (HtmlSubmitInput)htmlForm.getInputByValue("Login");
        return (HtmlPage)button.click();
    }

}
