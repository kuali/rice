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

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Link test failures to existing Jiras as a html link in Jenkins.
 * </p><p>
 * The more failures the more useful it is to not have to keep tracking down the same Jiras.
 * </p><p>
 * Set -Djira.aware.regex.failures.location and -Djira.aware.contains.failures.location to define file locations, else
 * @{code JiraAwareRegexFailures.properties} and {@code JiraAwareContainsFailures.properties} will be read as a resource stream.  To
 * override the Jira browse url set -Djira.aware.browse.url
 * </p><p>
 * To make use of JiraAwareFailureUtils implement {@see JiraAwareFailable} and call {@code JiraAwareFailureUtils.fail(contents, message, failable);} instead of
 * asserts or Assert.fail().
 * </p><p>
 * TODO:
 * <ol>
 *   <li>Integration Test integration.  ITs often fail by the 10s tracking down existing Jiras is a huge time sink.</li>
 * </ol>
 * </p>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JiraAwareFailureUtils {

    /**
     * <p>
     * Set -Djira.aware.base.url to point your Jira url base (include trailing slash), defaults to
     * https://jira.kuali.org/browse/
     * </p>
     */
    public static final String JIRA_BROWSE_URL_PROPERTY = "jira.aware.browse.url";

    private static String JIRA_BROWSE_URL = System.getProperty(JIRA_BROWSE_URL_PROPERTY,"https://jira.kuali.org/browse/");

    private static String REGEX_DEFAULT_PROPS_LOCATION = "JiraAwareRegexFailures.properties";

    /**
     * <p>
     * Set -Djira.aware.regex.failures.location to point to the the regex failures properties, defaults to
     * JiraAwareRegexFailures.properties
     * </p>
     */
    public static final String REGEX_LOCATION_POPERTY = "jira.aware.regex.failures.location";

    private static String REGEX_PROPS_LOCATION = System.getProperty(REGEX_LOCATION_POPERTY, REGEX_DEFAULT_PROPS_LOCATION);

    private static String CONTAINS_DEFAULT_PROPS_LOCATION = "JiraAwareContainsFailures.properties";

    /**
     * <p>
     * Set -Djira.aware.contains.failures.location to point to the the regex failures properties, defaults to
     * JiraAwareContainsFailures.properties
     * </p>
     */
    public static final String CONTAINS_LOCATION_PROERTY = "jira.aware.contains.failures.location";

    private static String CONTAINS_PROPS_LOCATION = System.getProperty(CONTAINS_LOCATION_PROERTY, CONTAINS_DEFAULT_PROPS_LOCATION);

    static Properties regexJiraMatches; // for more powerful matching

    static Properties jiraMatches; // simple contains matching

    static {
        try {
            regexJiraMatches = new PropertiesUtils().loadProperties(REGEX_PROPS_LOCATION, REGEX_DEFAULT_PROPS_LOCATION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jiraMatches = new PropertiesUtils().loadProperties(CONTAINS_PROPS_LOCATION, CONTAINS_DEFAULT_PROPS_LOCATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JiraAwareFailureUtils() {}

    /**
     * <p>
     * Calls {@see #failOnMatchedJira(String, JiraAwareFailable)} and calls fail on the {@see JiraAwareFailable#fail} if no matched jira failures.
     * </p>
     *
     * @param message to pass to fail
     * @param failable {@see JiraAwareFailable#fail}
     */
    public static void fail(String message, JiraAwareFailable failable) {
        failOnMatchedJira(message, failable);
        failable.fail(message);
    }

    /**
     * <p>
     * Calls {@see #failOnMatchedJira(String, String, JiraAwareFailable)} and calls fail on the {@see JiraAwareFailable#fail} fails if no matched jira failures.
     * </p>
     *
     * @param contents to check for jira matches on
     * @param message to pass to fail, also checked for jira matches
     * @param failable {@see JiraAwareFailable#fail}
     */
    public static void fail(String contents, String message, JiraAwareFailable failable) {
        failOnMatchedJira(contents, message, failable);
        failable.fail(contents + " " + message);
    }

    /**
     * <p>
     * Calls {@see #failOnMatchedJira(String, String, JiraAwareFailable)} and calls fail on the {@see JiraAwareFailable#fail} fails if no matched jira failures.
     * </p>
     *
     * @param contents to check for jira matches on
     * @param message to pass to fail, also checked for jira matches
     * @param failable {@see JiraAwareFailable#fail}
     */
    public static void fail(String contents, String message, Throwable throwable, JiraAwareFailable failable) {
        failOnMatchedJira(contents, message, failable);
        if (throwable != null) {
            failOnMatchedJira(ExceptionUtils.getStackTrace(throwable), throwable.getMessage(), failable);
            failable.fail(contents + " " + message + " " + throwable.getMessage() + "\n\t" + ExceptionUtils.getStackTrace(throwable));
        }
        failable.fail(contents + " " + message + " " + contents);
    }

    /**
     * <p>
     * Calls {@see #failOnMatchedJira(String, JiraAwareFailable)} with the contents and if no match is detected then the message.
     * </p>
     *
     * @param contents to check for containing of the jiraMatches keys.
     * @param message to check for containing of the jiraMatches keys if contents doesn't
     * @param failable to fail with the jiraMatches value if the contents or message is detected
     */
    public static void failOnMatchedJira(String contents, String message, JiraAwareFailable failable) {
        if (message == null) {
            message = ""; // prevent NPEs
        }

        String match = findMatchedJiraContains(message);
        if (match != null && !match.equals("")) {
            failable.fail(match);
        }

        match = findMatchedJiraContains(contents);
        if (match != null && !match.equals("")) {
            failable.fail(match);
        }

        match = findMatchedJiraRegex(message);
        if (match != null && !match.equals("")) {
            failable.fail(match);
        }

        match = findMatchedJiraRegex(contents);
        if (match != null && !match.equals("")) {
            failable.fail(match);
        }
    }

    /**
     * <p>
     * If the contents contains the jiraMatches key, calls fail on the {@see JiraAwareFailable#fail} passing in the jiraMatches value for the matched key.
     * </p>
     *
     * @param contents to check for containing of the jiraMatches keys.
     * @param failable to fail with the jiraMatches value if the jiraMatches key is contained in the contents
     */
    public static void failOnMatchedJira(String contents, JiraAwareFailable failable) {
        String match = findMatchedJira(contents);
        if (match != null && !match.equals("")) {
            failable.fail(match);
        }
    }

    /**
     * <p>
     * Returns the value from the properties files for the key matching the contents or an empty string if no match is found.
     * </p>
     *
     * @param contents to search for key in from properties file
     * @return value for key which matches contents
     */
    public static String findMatchedJira(String contents) {
        String match = findMatchedJiraContains(contents);
        if (match != null && !"".equals(match)) {
            return match;
        }

        return findMatchedJiraRegex(contents);
    }

    protected static String findMatchedJiraContains(String contents) {
        if (jiraMatches == null || jiraMatches.keySet() == null) {
            System.out.println("WARNING JiraAwareFailureUtils contains properties empty, findMatchesJira contains not available.");
            return "";
        }
        String key = null;

        Iterator iter = jiraMatches.keySet().iterator();

        while (iter.hasNext()) {
            key = (String)iter.next();
            if (contents.contains(key)) {
                return("\n" + JIRA_BROWSE_URL + jiraMatches.get(key) + "\n\n" + contents);
            }
        }

        return "";
    }

    protected static String findMatchedJiraRegex(String contents) {
        if (regexJiraMatches == null || regexJiraMatches.keySet() == null) {
            System.out.println("WARNING JiraAwareFailureUtils Regex properties empty, findMatchesJiraRegex not available.");
            return "";
        }
        String key = null;
        Pattern pattern = null;
        Matcher matcher = null;

        Iterator iter = regexJiraMatches.keySet().iterator();

        while (iter.hasNext()) {
            key = (String)iter.next();
            pattern = Pattern.compile(key, Pattern.DOTALL); // match across line terminators
            matcher = pattern.matcher(contents);

            if (matcher.find()) {
                return("\n" + JIRA_BROWSE_URL + regexJiraMatches.get(key) + "\n\n" + contents);
            }
        }

        return "";
    }

}