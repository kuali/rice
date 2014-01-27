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
package org.kuali.rice.edl.impl;

import org.junit.Test;
import org.kuali.rice.kew.test.KEWTestCase;

/**
 * Tests the web GUI for the ActionList.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class OfficeOfAffirmativeActionTest extends KEWTestCase {

	private final String OAA_DIR = "org/kuali/rice/edl/OfficeOfAffirmativeAction/";

	protected void loadTestData() throws Exception {
		// workgroups
		loadXmlFile("OAATestWorkgroups.xml");

		// attributes
		loadXmlFile(OAA_DIR + "EdocliteDepartmentSearchAttribute.xml");
        loadXmlFile(OAA_DIR + "EdocliteExpectedStartDateSearchAttribute.xml");
        loadXmlFile(OAA_DIR + "EdocliteInitiatorAttribute.xml");
        loadXmlFile(OAA_DIR + "EdocliteOAASearchAttribute.xml");
        loadXmlFile(OAA_DIR + "EdocliteSalaryGradeSearchAttribute.xml");
        loadXmlFile(OAA_DIR + "EdocliteSchoolAttribute.xml");
        loadXmlFile(OAA_DIR + "EdocliteSchoolSearchAttribute.xml");
        loadXmlFile(OAA_DIR + "EdocliteTotalAMFSearchAttribute.xml");
        loadXmlFile(OAA_DIR + "EdocliteTotalApplicantsSearchAttribute.xml");

        // templates
        loadXmlFile(OAA_DIR + "OfficeOfAffirmativeActionInitiatorAcknowledgmentRuleTemplate.xml");
        loadXmlFile(OAA_DIR + "OfficeOfAffirmativeActionOfficerRuleTemplate.xml");
        loadXmlFile(OAA_DIR + "OfficeOfAffirmativeActionSchoolAcknowledgementRuleTemplate.xml");
        loadXmlFile(OAA_DIR + "OfficeOfAffirmativeActionSchoolRuleTemplate.xml");
        loadXmlFile(OAA_DIR + "OfficeOfAffirmativeActionViceChancellorRuleTemplate.xml");

        // document types
        loadXmlFile(OAA_DIR + "OfficeOfAffirmativeActionDocType.xml");
        loadXmlFile(OAA_DIR + "InterviewRequestDocType.xml");
        loadXmlFile(OAA_DIR + "OfferRequestDocType.xml");
        loadXmlFile(OAA_DIR + "SearchStatusDocType.xml");
        loadXmlFile(OAA_DIR + "VacancyNoticeDocType.xml");
        loadXmlFile(OAA_DIR + "WaiverRequestDocType.xml");

        // forms
        loadXmlFile(OAA_DIR + "InterviewRequestForm.xml");
        loadXmlFile(OAA_DIR + "OfferRequestForm.xml");
        loadXmlFile(OAA_DIR + "SearchStatusForm.xml");
        loadXmlFile(OAA_DIR + "VacancyNoticeForm.xml");
        loadXmlFile(OAA_DIR + "WaiverRequestForm.xml");

        // rules
        loadXmlFile(OAA_DIR + "OfficeOfAffirmativeActionPilotRules.xml");

        // widgets
        loadXmlFile(EDLXmlUtils.class, "default-widgets.xml");
    }

	@Test public void testOAAEdocLiteLoad() {
	    // just a test to allow the setup method above to run and verify the xml import
	    // of these files
	}

	/**
	 * Tests the Office of Affirmative Action interview request.
	 */
//	@Test public void testInterviewRequest() throws Exception {
//		WebClient webClient = new WebClient();
//
//		URL url = new URL (URL_PREFIX + "EDocLite?userAction=initiate&edlName=InterviewRequest");
//		HtmlPage page = (HtmlPage)webClient.getPage(url);
//
//		// On the first access, we should end up on the backdoor and login as quickstart
//		HtmlForm form = (HtmlForm) page.getForms().get(0);
//		HtmlTextInput textInput = (HtmlTextInput)form.getInputByName("__login_user");
//		assertEquals("quickstart", textInput.getDefaultValue());
//		page = (HtmlPage)form.submit();
//
//		// we should be on the EDL form now, check that theres a form here
//		assertEquals("Should be one form.", 1, page.getForms().size());
//
//		// TOOD, fill out the form, route it, verify it goes where it needs to go
//		// do a few different permutations of data on a few different EDL's
//	}
//
//
//	@Test public void testOfferRequest() throws Exception {
//		// TODO
//	}
//
//	@Test public void testSearchStatus() throws Exception {
//		// TODO
//	}
//
//	@Test public void testVacancyNotice() throws Exception {
//		// TODO
//	}
//
//	@Test public void testWaiverRequest() throws Exception {
//		// TODO
//	}

}
