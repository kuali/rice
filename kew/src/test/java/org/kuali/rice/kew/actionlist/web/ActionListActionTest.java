/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package org.kuali.rice.kew.actionlist.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWHtmlUnitTestCase;
import org.kuali.rice.kew.util.KEWConstants;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;


/**
 * Tests the web GUI for the ActionList.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListActionTest extends KEWHtmlUnitTestCase {

    private static final String ACTION_LIST_URL_SUFFIX = "ActionList.do";

	protected void loadTestData() throws Exception {
        loadXmlFile("ActionListWebConfig.xml");
    }

	/**
	 * Tests the mass action list.
	 */
	@Test public void testMassActionList() throws Exception {
        HtmlPage page = performLogin(QUICKSTART_USER_NETWORK_ID, ACTION_LIST_URL_SUFFIX);

		// we should be on the Action List now, check that theres a form here
		assertEquals("Should be one form.", 1, page.getForms().size());

		// route 10 documents to quickstart and add FYIs which should result in Mass Actions
		int numDocs = 10;
		for (int i = 0; i < numDocs; i++) {
			WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "MassActionListTest");
			document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, "", getPrincipalIdForName(QUICKSTART_USER_NETWORK_ID), "", true);
			document.routeDocument("");
			assertTrue("Document should be FINAL.", document.stateIsFinal());
		}

		// check that the quickstart user has 10 action items
		Collection<ActionItem> actionList = KEWServiceLocator.getActionListService().findByPrincipalId(getQuickstartPrincipal().getPrincipalId());
		assertEquals("Should have 10 items.", 10, actionList.size());

		// now refresh the Action List
		page = getPage(ACTION_LIST_URL_SUFFIX);

		HtmlForm form = (HtmlForm) page.getForms().get(0);
		for (int i = 0; i < numDocs; i++) {
			String actionTakenCodeFieldName = "actions[" + i + "].actionTakenCd";
			HtmlSelect select = form.getSelectByName(actionTakenCodeFieldName);
			List options = select.getOptions();
			assertEquals("Should have two options", 2, select.getOptionSize());
			boolean hasNone = false;
			boolean hasFYI = false;
			for (Iterator iter = options.iterator(); iter.hasNext();) {
				HtmlOption option = (HtmlOption) iter.next();
				if (option.getValueAttribute().equals("NONE")) {
					hasNone = true;
				}
				else if (option.getValueAttribute().equals(KEWConstants.ACTION_REQUEST_FYI_REQ)){
					hasFYI = true;
				}
			}
			assertTrue("Should have had NONE option", hasNone);
			assertTrue("Should have had FYI option", hasFYI);
			select.setSelectedAttribute(KEWConstants.ACTION_REQUEST_FYI_REQ, true);
		}

		// get a reference to the "takeMassActions" link and click it
		HtmlAnchor anchor = (HtmlAnchor)page.getHtmlElementById("takeMassActions");
		anchor.click();

		// after taking mass actions, user should have no requests left in their action list
		actionList = KEWServiceLocator.getActionListService().findByPrincipalId(getQuickstartPrincipal().getPrincipalId());
		assertEquals("Should have 0 items.", 0, actionList.size());

		// now let's route some and intersperse them with documents that won't have the Mass Action option,
		// the odd index docs are approvals, the even index ones are fyis which will have mass action available
		List<Doc> massActionable = new ArrayList<Doc>();
		List<Doc> nonMassActionable = new ArrayList<Doc>();
		numDocs = 10;
		for (int i = 0; i < numDocs; i++) {
			boolean isMassActionable = (i % 2 == 0);
			String actionRequested = (isMassActionable ? KEWConstants.ACTION_REQUEST_FYI_REQ : KEWConstants.ACTION_REQUEST_APPROVE_REQ);
			WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "MassActionListTest");
			document.adHocRouteDocumentToPrincipal(actionRequested, "", getPrincipalIdForName(QUICKSTART_USER_NETWORK_ID), "", true);
			document.routeDocument("");
			if (isMassActionable) {
				massActionable.add(new Doc(i, document.getRouteHeaderId()));
			} else {
				nonMassActionable.add(new Doc(i, document.getRouteHeaderId()));
			}
		}

		// check that the quickstart user has 10 action items
		actionList = KEWServiceLocator.getActionListService().findByPrincipalId(getQuickstartPrincipal().getPrincipalId());
		assertEquals("Should have 10 items.", 10, actionList.size());

		// refresh the Action List
        page = getPage(ACTION_LIST_URL_SUFFIX);
		assertEquals("Should be one form on the page.", 1, page.getForms().size());
		form = (HtmlForm) page.getForms().get(0);

		for (int i = 0; i < numDocs; i++) {
			boolean isMassActionable = (i % 2 == 0);
			String actionTakenCodeFieldName = "actions[" + i + "].actionTakenCd";
			if (isMassActionable) {
				HtmlSelect select = form.getSelectByName(actionTakenCodeFieldName);
				List options = select.getOptions();
				assertEquals("Should have two options", 2, select.getOptionSize());
				boolean hasNone = false;
				boolean hasFYI = false;
				for (Iterator iter = options.iterator(); iter.hasNext();) {
					HtmlOption option = (HtmlOption) iter.next();
					if (option.getValueAttribute().equals("NONE")) {
						hasNone = true;
					}
					else if (option.getValueAttribute().equals("F")){
						hasFYI = true;
					}
				}
				assertTrue("Should have had NONE option", hasNone);
				assertTrue("Should have had FYI option", hasFYI);
				select.setSelectedAttribute("F", true);

			} else {
				try {
					form.getSelectByName(actionTakenCodeFieldName);
					fail("ElementNotFoundException should have been thrown");
				} catch (ElementNotFoundException e) {}
			}
		}

		// take the actions
		anchor = (HtmlAnchor)page.getHtmlElementById("takeMassActions");
		anchor.click();

		// since only half the actions were "mass actionable", we should have 5 now
		actionList = KEWServiceLocator.getActionListService().findByPrincipalId(getQuickstartPrincipal().getPrincipalId());
		assertEquals("Should have 5 items.", 5, actionList.size());
		// check that the documents remaining are the ones which are approve requests and not the mass actionable fyi requests
		for (Iterator iterator = actionList.iterator(); iterator.hasNext();) {
			ActionItem actionItem = (ActionItem) iterator.next();
			assertEquals(KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
			boolean foundDoc = false;
			for (Doc doc : nonMassActionable) {
				if (doc.docId.equals(actionItem.getRouteHeaderId())) {
					foundDoc = true;
					break;
				}
			}
			if (!foundDoc) {
				fail("The given remaining document should have been non-Mass Actionable: " + actionItem.getRouteHeaderId());
			}
		}

	}

	private static class Doc {
		public int index;
		public Long docId;
		public Doc(int index, Long docId) {
			this.index = index;
			this.docId = docId;
		}

	}


}
