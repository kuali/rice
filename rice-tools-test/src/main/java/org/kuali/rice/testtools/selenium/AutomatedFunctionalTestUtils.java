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
package org.kuali.rice.testtools.selenium;

import org.apache.commons.lang.RandomStringUtils;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.common.JiraAwareFailureUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

/**
 * For Rice specific sampleapp testing code.
 * <ol>
 *   <li>Keep test framework methods (WebDriver, Assert) dependencies out of this class, those should be in
 *   {@link WebDriverUtils}</li>
 *   <li>Move JiraAware calls out of this class, those should be in {@see JiraAwareFailureUtils} or modified to be failed
 *   in another class</li>
 * </ol>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AutomatedFunctionalTestUtils {

    /**
     * //div[@class='error']"
     */
    public static final String DIV_ERROR_LOCATOR = "//div[@class='error']";

    /**
     * //div[@class='msg-excol']
     */
    public static final String DIV_EXCOL_LOCATOR = "//div[@class='msg-excol']";

    /**
     * Calendar.getInstance().getTimeInMillis() + ""
     */
    public static final String DTS = Calendar.getInstance().getTimeInMillis() + "";

    /**
     * Calendar.getInstance().getTimeInMillis() + "" + RandomStringUtils.randomAlphabetic(2).toLowerCase()
     * @Deprecated {@link AutomatedFunctionalTestUtils#createUniqueDtsPlusTwoRandomChars()}
     */
    public static final String DTS_TWO = Calendar.getInstance().getTimeInMillis() + "" + RandomStringUtils.randomAlphabetic(2).toLowerCase();

    /**
     *  &hideReturnLink=true
     */
    public static final String HIDE_RETURN_LINK =  "&hideReturnLink=true";

    /**
     *  &hideReturnLink=false
     */
    public static final String HIDE_RETURN_LINK_FALSE =  "&hideReturnLink=false";

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=
     */
    public static final String KRAD_LOOKUP_METHOD =  "/kr-krad/lookup?methodToCall=start&dataObjectClassName=";

    /**
     * /kr/lookup.do?methodToCall=start&businessObjectClassName=
     */
    public static final String KNS_LOOKUP_METHOD =  "/kr/lookup.do?methodToCall=start&businessObjectClassName=";

    /**
     * /kr-krad/kradsampleapp?viewId=KradSampleAppHome
     */
    public static final String KRAD_PORTAL = "/kr-krad/kradsampleapp?viewId=KradSampleAppHome";

    /**
     * /kr-krad/kradsampleapp?viewId=KradSampleAppHome
     */
    public static final String KRAD_PORTAL_URL = WebDriverUtils.getBaseUrlString() + KRAD_PORTAL;

    /**
     * /kr-krad/labs?viewId=LabsMenuView
     */
    public static final  String LABS = "/kr-krad/labs?viewId=LabsMenuView";

    /**
     * WebDriverUtils.getBaseUrlString() + LABS
     */
    public static final String LABS_URL = WebDriverUtils.getBaseUrlString() + LABS;

    /**
     * /portal.do
     */
    public static final String PORTAL = "/portal.do";

    /**
     * WebDriverUtils.getBaseUrlString() + ITUtil.PORTAL
     */
    public static final String PORTAL_URL =  WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.PORTAL;

    /**
     * URLEncoder.encode(PORTAL_URL)
     */
    public static final String PORTAL_URL_ENCODED = URLEncoder.encode(PORTAL_URL);

    /**
     *  &showMaintenanceLinks=true
     */
    public static final String SHOW_MAINTENANCE_LINKS =  "&showMaintenanceLinks=true";

    /**
     * KRAD
     */
    public static final String REMOTE_UIF_KRAD = "KRAD";

    /**
     * KNS
     */
    public static final String REMOTE_UIF_KNS  = "KNS";

    /**
     * &docFormKey=
     */
    public static final String DOC_FORM_KEY = "&docFormKey=";
    

    public static String blanketApprovalCleanUpErrorText(String errorText) {
        errorText = errorText.replace("* required field", "").replace("\n", " ").trim(); // bit of extra ui text we don't care about
        return errorText;
    }

    /**
     * Creates a 13 digit time stamp with two random characters appended for use with fields that require unique values.
     *
     * Some fields have built in validation to not allow 9 continuous digits for those cases {@see #createUniqueDtsPlusTwoRandomCharsNot9Digits}
     * @return
     */
    public static String createUniqueDtsPlusTwoRandomChars() {
        return Calendar.getInstance().getTimeInMillis() + "" + RandomStringUtils.randomAlphabetic(2).toLowerCase();
    }

    /**
     * Creates a 13 digit time stamp with two random characters inserted into it to avoid the 9 continous digit varification some fields use.
     *
     * @return
     */
    public static String createUniqueDtsPlusTwoRandomCharsNot9Digits() {
        String dtsTwo = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        dtsTwo = dtsTwo.substring(0, 5) + dtsTwo.substring(13, 14) + dtsTwo.substring(6, 12);
        return dtsTwo;
    }

    protected static void checkForIncidentReport(String contents, String linkLocator, String message, JiraAwareFailable failable) {
        if (contents == null) { //guard clause
            return;
        }

        String errorMessage = incidentReportMessage(contents, linkLocator, message);

        if (errorMessage != null) {
            if (message != null && !message.isEmpty()) {
                failable.jiraAwareFail(errorMessage, message);
            } else {
                failable.jiraAwareFail(errorMessage, contents);
            }
        }
    }

    protected static String incidentReportMessage(String contents, String linkLocator, String message) {
        if (incidentReported(contents)) {
            try {
                return processIncidentReport(contents, linkLocator, message);
            } catch (IndexOutOfBoundsException e) {
                return "\nIncident report detected "
                                + message
                                + " but there was an exception during processing: "
                                + e.getMessage()
                                + "\nStack Trace from processing exception"
                                + stackTrace(e)
                                + "\nContents that triggered exception: "
                                + deLinespace(contents);
            }
        }

        if (contents.contains("HTTP Status 404")) {
            return "HTTP Status 404 contents: " + contents;
        }

        if (contents.contains("HTTP Status 500")) {
            return "\nHTTP Status 500 stacktrace: " + extract500Exception(contents);
        }

        // freemarker exception
        if (contents.contains("Java backtrace for programmers:")
                || contents.contains("Java stack trace (for programmers):")
                || contents.contains("FreeMarker template error:")) {
            try {
                return freemarkerExceptionMessage(contents, linkLocator, message);
            } catch (IndexOutOfBoundsException e) {
                return "\nFreemarker exception detected "
                        + message
                        + " but there was an exception during processing: "
                        + e.getMessage()
                        + "\nStack Trace from processing exception"
                        + stackTrace(e)
                        + "\nContents that triggered exception: "
                        + deLinespace(contents);
            }
        }

        if (contents.contains("Document Expired")) { // maybe Firefox specific
            return "Document Expired message.";
        }

        return null;
    }

    public static String deLinespace(String contents) {
        while (contents.contains("\n\n")) {
            contents = contents.replaceAll("\n\n", "\n");
        }
        return contents;
    }

    private static String extractIncidentReportInfo(String contents, String linkLocator, String message) {
        String chunk =  contents.substring(contents.indexOf("Incident Feedback"), contents.lastIndexOf("</div>") );
        String docId = chunk.substring(chunk.lastIndexOf("Document Id"), chunk.indexOf("View Id"));
        docId = docId.substring(0, docId.indexOf("</div>"));
        docId = docId.substring(docId.lastIndexOf(">") + 2, docId.length()).trim();

        String viewId = chunk.substring(chunk.lastIndexOf("View Id"), chunk.indexOf("Error Message"));
        viewId = viewId.substring(0, viewId.indexOf("</div>"));
        viewId = viewId.substring(viewId.lastIndexOf(">") + 2, viewId.length()).trim();

        String stackTrace = chunk.substring(chunk.lastIndexOf("(only in dev mode)"), chunk.length());
        stackTrace = stackTrace.substring(stackTrace.indexOf("<pre") + 4, stackTrace.length());
        stackTrace = stackTrace.substring(stackTrace.indexOf(">") + 1, stackTrace.indexOf("</"));

        return "\nIncident report "
                + message
                + " navigating to "
                + linkLocator
                + " : View Id: "
                + viewId.trim()
                + " Doc Id: "
                + docId.trim()
                + "\nStackTrace: "
                + stackTrace.trim();
    }

    private static String extractIncidentReportKim(String contents, String linkLocator, String message) {
        if (contents.indexOf("id=\"headerarea\"") > -1) {
            String chunk =  contents.substring(contents.indexOf("id=\"headerarea\""), contents.lastIndexOf("</div>") );
            String docIdPre = "type=\"hidden\" value=\"";
            String docId = chunk.substring(chunk.indexOf(docIdPre) + docIdPre.length(), chunk.indexOf("\" name=\"documentId\""));

            String stackTrace = chunk.substring(chunk.lastIndexOf("name=\"displayMessage\""), chunk.length());
            String stackTracePre = "value=\"";
            stackTrace = stackTrace.substring(stackTrace.indexOf(stackTracePre) + stackTracePre.length(), stackTrace.indexOf("name=\"stackTrace\"") - 2);

            return "\nIncident report "
                    + message
                    + " navigating to "
                    + linkLocator
                    + " Doc Id: "
                    + docId.trim()
                    + "\nStackTrace: "
                    + stackTrace.trim();
        }
        return "\nIncident report detected for " + linkLocator + " but not able to parse.  " + message;
    }

    public static void failOnInvalidUserName(String userName, String contents, JiraAwareFailable failable) {
        if (contents.indexOf("Invalid") > -1) {
            failable.fail("Invalid Login " + userName);
        }
    }
/*
    public static void failOnMatchedJira(String contents) {
        Iterator<String> iter = jiraMatches.keySet().iterator();
        String key = null;

        while (iter.hasNext()) {
            key = iter.next();
            if (contents.contains(key)) {
                SeleneseTestBase.fail(JIRA_BROWSE_URL + jiraMatches.get(key));
            }
        }
    }
*/

    private static void failWithInfo(String contents, String linkLocator, JiraAwareFailable failable, String message) {
        JiraAwareFailureUtils.failOnMatchedJira(contents, linkLocator, failable);
        failable.fail(contents);
    }

    private static void failWithReportInfo(String contents, String linkLocator, JiraAwareFailable failable, String message) {
        final String incidentReportInformation = extractIncidentReportInfo(contents, linkLocator, message);
        JiraAwareFailureUtils.failOnMatchedJira(incidentReportInformation, failable);
        failWithInfo(incidentReportInformation, linkLocator, failable, message);
    }

    /*
    private static void failWithReportInfoForKim(String contents, String linkLocator, String message) {
        final String kimIncidentReport = extractIncidentReportKim(contents, linkLocator, message);
        SeleneseTestBase.fail(kimIncidentReport);
    }
*/
    private static void failWithReportInfoForKim(String contents, String linkLocator, JiraAwareFailable failable, String message) {
        final String kimIncidentReport = extractIncidentReportKim(contents, linkLocator, message);
        JiraAwareFailureUtils.failOnMatchedJira(kimIncidentReport, failable);
        failable.fail(kimIncidentReport);
    }

    public static String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";

        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static boolean incidentReported(String contents) {
        return contents != null &&
                contents.contains("Incident Report") &&
                !contents.contains("portal.do?channelTitle=Incident%20Report") && // Incident Report link on sampleapp KRAD tab
                !contents.contains("portal.do?channelTitle=Incident Report") &&   // Incident Report link on sampleapp KRAD tab IE8
                !contents.contains("uitest?viewId=Travel-testView2") &&
                !contents.contains("SeleniumException"); // selenium timeouts have Incident Report in them
    }

    /**
     * Append http:// if not present, remove trailing /.
     *
     * @param baseUrl
     * @return
     */
    public static String prettyHttp(String baseUrl) {

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        if (!baseUrl.startsWith("http")) {
            baseUrl = "http://" + baseUrl;
        }

        return baseUrl;
    }

    private static String extract500Exception(String contents) {
        return contents.substring(contents.indexOf("<b>exception</b> </p><pre>") +26,
                                  contents.indexOf("</pre><p></p><p><b>note</b>"));
    }

    private static void processFreemarkerException(String contents, String linkLocator, JiraAwareFailable failable, String message) {
        JiraAwareFailureUtils.failOnMatchedJira(contents, failable);
        String errorMessage = freemarkerExceptionMessage(contents, linkLocator, message);

        JiraAwareFailureUtils.failOnMatchedJira(errorMessage, linkLocator, failable);
        failable.fail(errorMessage);
    }

    protected static String freemarkerExceptionMessage(String contents, String linkLocator, String message) {
        String ftlStackTrace = null;
        if (contents.contains("more<")) {
            ftlStackTrace = contents.substring(contents.indexOf("----------"), contents.indexOf("more<") - 1);
        } else if (contents.contains("at java.lang.Thread.run(Thread.java:")) {
            if (contents.indexOf("Error: on line") > -1) {
                ftlStackTrace = contents.substring(contents.indexOf("Error: on line"), contents.indexOf("at java.lang.Thread.run(Thread.java:") + 39 );
            } else {
                ftlStackTrace = contents.substring(contents.indexOf("FreeMarker template error:"), contents.indexOf("at java.lang.Thread.run(Thread.java:") + 39 );
            }
        }
        return "\nFreemarker Exception " + message + " navigating to " + linkLocator + "\nStackTrace: " + ftlStackTrace.trim();
    }

/*
    private static void processIncidentReport(String contents, String linkLocator, String message) {
        failOnMatchedJira(contents);

        if (contents.indexOf("Incident Feedback") > -1) {
            failWithReportInfo(contents, linkLocator, message);
        }

        if (contents.indexOf("Incident Report") > -1) { // KIM incident report
            failWithReportInfoForKim(contents, linkLocator, message);
        }

        SeleneseTestBase.fail("\nIncident report detected " + message + "\n Unable to parse out details for the contents that triggered exception: " + deLinespace(
                contents));
    }

    private static void failWithReportInfo(String contents, String linkLocator, String message) {
        final String incidentReportInformation = extractIncidentReportInfo(contents, linkLocator, message);
        SeleneseTestBase.fail(incidentReportInformation);
    }
*/

    protected static void processIncidentReport(String contents, String linkLocator, JiraAwareFailable failable, String message) {

        if (contents.indexOf("Incident Feedback") > -1) {
            failWithReportInfo(contents, linkLocator, failable, message);
        }

        if (contents.indexOf("Incident Report") > -1) { // KIM incident report
            failWithReportInfoForKim(contents, linkLocator, failable, message);
        }

        JiraAwareFailureUtils.failOnMatchedJira(contents, failable);
        failable.fail("\nIncident report detected "
                + message
                + "\n Unable to parse out details for the contents that triggered exception: "
                + deLinespace(contents));
    }

    protected static String processIncidentReport(String contents, String linkLocator, String message) {

        if (contents.indexOf("Incident Feedback") > -1) {
            return extractIncidentReportInfo(contents, linkLocator, message);
        }

        if (contents.indexOf("Incident Report") > -1) { // KIM incident report
            return extractIncidentReportKim(contents, linkLocator, message);
        }

        return "\nIncident report detected "
                + message
                + "\n Unable to parse out details for the contents that triggered exception: "
                + deLinespace(contents);
    }

    /**
     * Write the given stack trace into a String remove the ats in an attempt to not cause Jenkins problems.
     * @param throwable whose stack trace to return
     * @return String of the given throwable's stack trace.
     */
    public static String stackTrace(Throwable throwable) {
        StringWriter wrt = new StringWriter();
        PrintWriter pw = new PrintWriter(wrt);
        throwable.printStackTrace(pw);
        pw.flush();
        return wrt.toString();
    }

    /**
     * <p>
     * Use the KRAD Login Screen or the old KNS Login Screen
     * </p>
     *
     * @return true if Krad login
     */
    public static boolean isKradLogin(){
        // check system property, default to KRAD
        String loginUif = System.getProperty(WebDriverUtils.REMOTE_LOGIN_UIF);
        if (loginUif == null) {
            loginUif = REMOTE_UIF_KRAD;
        }

        return (REMOTE_UIF_KRAD.equalsIgnoreCase(loginUif));
    }
}
