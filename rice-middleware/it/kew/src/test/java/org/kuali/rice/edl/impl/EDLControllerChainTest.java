package org.kuali.rice.edl.impl;

import org.junit.Test;
import org.kuali.rice.edl.impl.service.EdlServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.test.BaselineTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class EDLControllerChainTest extends KEWTestCase {

    private static final String EXPECTED_OUTPUT = "<html xmlns:wf=\"http://xml.apaches.org/xalan/java/org.kuali.rice.edl.framework.util.EDLFunctions\">\n" +
                    "<body>admin</body>\n" +
                    "</html>";

    protected void loadTestData() throws Exception {
        super.loadXmlFile("EDLControllerChainTest.xml");
    }

    @Test
    public void testRenderEdl_WithEdlFunctions() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("User-Agent", "JUnit");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestParser requestParser = new RequestParser(request);
        requestParser.setParameterValue("command", "initiate");
        requestParser.setParameterValue("userAction", "initiate");

        UserSession userSession = new UserSession("admin");
        GlobalVariables.setUserSession(userSession);

        EDLController edlController = EdlServiceLocator.getEDocLiteService().getEDLControllerUsingEdlName("TestDocumentType");
        EDLControllerChain chain = new EDLControllerChain();
        chain.addEdlController(edlController);

        // render the EDL
        chain.renderEDL(requestParser, response);

        // the output should conform to our template as well as including the user's name per our use of EDLFunctions
        assertEquals(EXPECTED_OUTPUT, response.getContentAsString().trim());
    }

}
