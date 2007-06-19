/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.rice.testharness.KNSTestCase;
import org.kuali.rice.testharness.TransactionalLifecycle;


/**
 * This class is the superclass for all test cases which may require the use of
 * services, or datasources, or any of the other expensive/time-consuming
 * infrastructure.
 * <p>
 * For test methods or classes with the {@link RelatesTo} annotation, this class
 * also wraps any test errors or failures with a notice that the listed JIRA
 * issues are related. This is to help developers see on test reports what work
 * may be in progress or recently done for this problem. It will help
 * distinguish tests which have already been investigated from ones which still
 * need to be. Test errors before the setUp method, e.g., in connecting to the
 * database to start a test transaction or to Workflow for a user session, are
 * not wrapped with these notices. Tests not extending KualiTestBase also do not
 * get these notices. For the sake of speed, the current JIRA status of the
 * related issues are not checked. The original Throwable is the cause of the
 * wrapper, so it appears next in the stacktrace on the test report.
 * <p>
 * If the {@value #SKIP_OPEN_OR_IN_PROGRESS_OR_REOPENED_JIRA_ISSUES} system
 * property is set, then this class passes (without running its contents) any
 * test that {@link RelatesTo} a JIRA issue that is currently open or
 * in-progress or reopened. This is an alternative to
 * {@link org.kuali.test.suite.OpenOrInProgressOrReopenedSuite} for Anthill to
 * retain the same format of its test report while not revealing any failures of
 * such tests. When using this system property, keep in mind that it takes well
 * over a minute to get the list of open issues from JIRA. The list is cached
 * statically, so it's insignificant to add a minute or two to the time it takes
 * for the whole Anthill build. But, developers will probably not want to add
 * this system property to their own environments, because of this delay and so
 * that they can still work on those tests.
 * 
 * @see WithTestSpringContext
 * @see TestsWorkflowViaDatabase
 * @see RelatesTo
 * 
 * 
 */

public abstract class KualiTestBase extends KNSTestCase implements KualiTestConstants {

	public static final String SKIP_OPEN_OR_IN_PROGRESS_OR_REOPENED_JIRA_ISSUES = "org.kuali.test.KualiTestBase.skipOpenOrInProgressOrReopenedJiraIssues";

	private static final Map<String, Level> changedLogLevels = new HashMap<String, Level>();

	private TransactionalLifecycle transactionalLifecycle;


	/**
	 * Changes the logging-level associated with the given loggerName to the
	 * given level. The original logging-level is saved, and will be
	 * automatically restored at the end of each test.
	 * 
	 * @param loggerName
	 *            name of the logger whose level to change
	 * @param newLevel
	 *            the level to change to
	 */
	protected void setLogLevel(String loggerName, Level newLevel) {
		Logger logger = Logger.getLogger(loggerName);

		if (!changedLogLevels.containsKey(loggerName)) {
			Level originalLevel = logger.getLevel();
			changedLogLevels.put(loggerName, originalLevel);
		}

		logger.setLevel(newLevel);
	}

	/**
	 * Restores the logging-levels changed through calls to setLogLevel to their
	 * original values.
	 */
	protected void resetLogLevels() {
		for (Iterator i = changedLogLevels.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();

			String loggerName = (String) e.getKey();
			Level originalLevel = (Level) e.getValue();

			Logger.getLogger(loggerName).setLevel(originalLevel);
		}
		changedLogLevels.clear();
	}

	@Before 
	public void setUp() throws Exception {
		super.setUp();
		final boolean needsSpring = getClass().isAnnotationPresent(WithTestSpringContext.class);
		GlobalVariables.setErrorMap(new ErrorMap());
		if (needsSpring) {
			transactionalLifecycle = new TransactionalLifecycle();
			transactionalLifecycle.start();
		}
	}

	@After 
	public void tearDown() throws Exception {
		final boolean needsSpring = getClass().isAnnotationPresent(WithTestSpringContext.class);
		resetLogLevels();
		if (needsSpring) {
			transactionalLifecycle.stop();
		}
		GlobalVariables.setErrorMap(new ErrorMap());
		super.tearDown();
	}
}
