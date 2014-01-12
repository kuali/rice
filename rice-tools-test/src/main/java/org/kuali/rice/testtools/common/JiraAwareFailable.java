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

/**
 * <p>
 * Used by {@see JiraAwareFailureUtils} to fail tests in a Jira aware way.
 * </p>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface JiraAwareFailable {

    /**
     * <p>
     * Fail in a framework appropriate way, should not call jiraAwareFail.
     * </p>
     *
     * @param message to display for the failure.
     */
    void fail(String message);

    /**
     * jiraAwareFail is a hook to do things like checking for incident reports, 404s, 503s, etc, the last statement
     * typically would be a call to {@see JiraAwareUtil#fail(String, String, JiraAwareFailable} to check for Jira matches.
     *
     * @param message to check for a Jira match and fail with.
     */
    void jiraAwareFail(String message);

    /**
     * jiraAwareFail is a hook to do things like checking for incident reports, 404s, 503s, etc, the last statement
     * typically would be a call to {@see JiraAwareUtil#fail(String, String, JiraAwareFailable} to check for Jira matches.
     *
     * @param contents to check for a Jira match
     * @param message to check for a Jira match and fail with.
     */
    void jiraAwareFail(String contents, String message);

    /**
     * jiraAwareFail is a hook to do things like checking for incident reports, 404s, 503s, etc, the last statement
     * typically would be a call to {@see JiraAwareUtil#fail(String, String, Throwable, JiraAwareFailable} to check for Jira matches.
     *
     * @param contents to check for a Jira match
     * @param message to check for a Jira match and fail with.
     * @param throwable to check for a Jira match
     */
    void jiraAwareFail(String contents, String message, Throwable throwable);
}
