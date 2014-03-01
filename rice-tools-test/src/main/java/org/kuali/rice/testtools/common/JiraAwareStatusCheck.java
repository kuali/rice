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
package org.kuali.rice.testtools.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * Prints out status of JiraAware Jiras from JiraAwareContainsFailures.properties and JiraAwareRegexFailures.properties
 * files.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JiraAwareStatusCheck {

    public static void main(String[] args ) {
        String baseUrl = "https://jira.kuali.org/browse/";
        String jira = "";
        String statusSpan = "<span id=\"status-val\" class=\"value\">";

        checkJiraMatchesContains(baseUrl, jira, statusSpan);
        checkJiraMatchesRegex(baseUrl, jira, statusSpan);
    }

    private static void checkJiraMatchesContains(String baseUrl, String jira, String statusSpan) {
        String key;
        String status;
        Iterator iter = JiraAwareFailureUtils.jiraMatches.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            jira = JiraAwareFailureUtils.jiraMatches.getProperty(key);
            jira = jira.substring(0, jira.indexOf(" "));
            try {
                status = getStatus(baseUrl, jira, statusSpan);
                System.out.println(jira + " Status: " + status + " for key: " + key);
            } catch (Exception e) {
                System.out.println("Exception reading " + baseUrl + jira + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void checkJiraMatchesRegex(String baseUrl, String jira, String statusSpan) {
        String key;
        String status;
        Iterator iter = JiraAwareFailureUtils.regexJiraMatches.keySet().iterator();
        while (iter.hasNext()) {
            key = (String)iter.next();
            jira = JiraAwareFailureUtils.regexJiraMatches.getProperty(key);
            jira = jira.substring(0, jira.indexOf(" "));
            try {
                status = getStatus(baseUrl, jira, statusSpan);
                System.out.println(jira + " Status: " + status + " for key: " + key);
            } catch (Exception e) {
                System.out.println("Exception reading " + baseUrl + jira + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static String getStatus(String baseUrl, String jira, String statusSpan) throws Exception {
        String contents = JiraAwareStatusCheck.getText(baseUrl + jira);
//        System.out.println("\n" + contents + "\n");
        contents = contents.substring(contents.indexOf(statusSpan) + statusSpan.length(), contents.length());
        contents = contents.substring(0, contents.indexOf("</span>"));
        String status = contents.substring(contents.indexOf("alt=\"") + 5, contents.length());
        status = status.substring(0, status.indexOf("\""));
        return status;
    }

    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)website.openConnection();
        connection.setInstanceFollowRedirects(true);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }
}
