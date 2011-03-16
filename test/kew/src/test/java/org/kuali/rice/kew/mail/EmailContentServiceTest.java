/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.rice.kew.mail;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.core.mail.EmailContent;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionlist.ActionListFilter;

import org.kuali.rice.kew.mail.service.EmailContentService;
import org.kuali.rice.kew.mail.service.impl.StyleableEmailContentServiceImpl;
import org.kuali.rice.kew.rule.RuleTestUtils;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * Unit test for the EmailContentService
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class EmailContentServiceTest extends KEWTestCase {
	
	private static final Logger LOG = Logger.getLogger(EmailContentServiceTest.class);

	/**
	 * @see org.kuali.rice.test.RiceTestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		loadXmlFile("EmailContentServiceTestConfig.xml");
	}
	
	/**
	 * This method specifically exercises a group responsibility to assure that the 
	 * {@link StyleableEmailContentServiceImpl} can handle that case.
	 * See KULRICE-3659.
	 */
	@Test
	public void testGroup() throws Exception {
		String ewestfalPrincipalId = getPrincipalIdForName("ewestfal");
		
		// this document type has a group responsibility
		WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), "EmailTestWorkgroupDocType");
		doc.routeDocument("");
		
		ActionListFilter actionListFilter = new ActionListFilter();
		actionListFilter.setDocumentType(doc.getDocumentType());
		Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().getActionList(ewestfalPrincipalId, actionListFilter);

		EmailContentService emailContentService = KEWServiceLocator.getEmailContentService();
		
		Person person = KIMServiceLocator.getPersonService().getPerson(ewestfalPrincipalId);
		// this would blow up before the fix
		EmailContent emailContent = emailContentService.generateDailyReminder(person, actionItems);
	}
	
	/**
	 * This method tests that delegation doesn't break the email
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUserDelegator() throws Exception {
		
		RuleTestUtils.createDelegationToUser("EmailTestUserDocType", "WorkflowDocumentTemplate", "user1");
		
		String user1PrincipalId = getPrincipalIdForName("user1");
		
		// this document type has a group responsibility
		WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), "EmailTestUserDocType");
		doc.routeDocument("");
		
		ActionListFilter actionListFilter = new ActionListFilter();
		actionListFilter.setDocumentType(doc.getDocumentType());
		Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().getActionList(user1PrincipalId, actionListFilter);

		EmailContentService emailContentService = KEWServiceLocator.getEmailContentService();
		
		Person person = KIMServiceLocator.getPersonService().getPerson(user1PrincipalId);
		EmailContent emailContent = emailContentService.generateDailyReminder(person, actionItems);
	}
	
	/**
	 * This method tests that 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGroupDelegator() throws Exception {
		
		RuleTestUtils.createDelegationToGroup("EmailTestWorkgroupDocType", "WorkflowDocumentTemplate", "EmailTestDelegateWorkgroup");
		
		String user1PrincipalId = getPrincipalIdForName("user1");
		
		// this document type has a group responsibility
		WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), "EmailTestWorkgroupDocType");
		doc.routeDocument("");
		
		ActionListFilter actionListFilter = new ActionListFilter();
		actionListFilter.setDocumentType(doc.getDocumentType());
		Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().getActionList(user1PrincipalId, actionListFilter);

		EmailContentService emailContentService = KEWServiceLocator.getEmailContentService();
		
		Person person = KIMServiceLocator.getPersonService().getPerson(user1PrincipalId);
		EmailContent emailContent = emailContentService.generateDailyReminder(person, actionItems);
	}
}
