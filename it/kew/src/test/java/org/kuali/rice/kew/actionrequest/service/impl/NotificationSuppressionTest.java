/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.actionrequest.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import mocks.MockDocumentRequeuerImpl;
import mocks.MockEmailNotificationService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.core.api.config.CoreConfigHelper;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.api.action.DelegationType;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;

/**
 * This is a description of what this class does - gilesp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class NotificationSuppressionTest extends KEWTestCase {

	private static final String TEST_RULE_TEMPLATE = "WorkflowDocumentTemplate";

	protected void loadTestData() throws Exception {
		loadXmlFile("NotificationSuppressionTestConfig.xml");
	}
	
	private static final String TEST_DOC_TYPE = "NotificationSuppressionTestDocType";

	/**
	 * Tests that the notification suppression keys work equivalently for ActionRequestDTO and ActionRequestValue
	 * 
	 * @throws Exception
	 */
	@Test public void testNotificationSuppressionKeys() throws Exception {
		WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), TEST_DOC_TYPE);
		document.routeDocument("");
		ActionRequestDTO[] requests = document.getActionRequests();
		assertTrue("there must be ActionRequestDTOs to test!", requests != null && requests.length > 0);

		NotificationSuppression notificationSuppression = new NotificationSuppression();

		boolean atLeastOne = false;
		for (ActionRequestDTO reqDTO : requests) if (reqDTO.getParentActionRequestId() == null) {
			atLeastOne = true;
			ActionRequestValue reqVal = DTOConverter.convertActionRequestDTO(reqDTO);
			assertTrue(CollectionUtils.isEqualCollection(notificationSuppression.getSuppressNotifyNodeStateKeys(reqVal),
					notificationSuppression.getSuppressNotifyNodeStateKeys(reqDTO)));

			// test that changing the responsible party changes the key

			reqDTO.setPrincipalId("asdf");
			assertFalse(CollectionUtils.isEqualCollection(notificationSuppression.getSuppressNotifyNodeStateKeys(reqVal),
					notificationSuppression.getSuppressNotifyNodeStateKeys(reqDTO)));
		}
		assertTrue(atLeastOne);

	}
	
	/**
	 * test that ActionItemS are filtered when a corresponding notification suppression NodeState is present
	 * in the related RouteNodeInstance
	 * 
	 * @throws Exception
	 */
	@Test public void testActionItemFiltering() throws Exception {
		WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), TEST_DOC_TYPE);
		document.routeDocument("");
		ActionRequestDTO[] requests = document.getActionRequests();
		assertTrue("there must be ActionRequestDTOs to test!", requests != null && requests.length > 0);
		
		NotificationSuppression notificationSuppression = new NotificationSuppression();

		boolean atLeastOne = false;
		for (ActionRequestDTO reqDTO : requests) if (reqDTO.getParentActionRequestId() == null) {
			atLeastOne = true;
			
			ActionRequestValue reqVal = DTOConverter.convertActionRequestDTO(reqDTO);

			List<ActionItem> actionItems = new ArrayList<ActionItem>();
			actionItems.add(KEWServiceLocator.getActionListService().createActionItemForActionRequest(reqVal));
			
			RouteNodeInstance routeNodeInstance = new RouteNodeInstance();
			
			// if there is no notification suppression state, nothing should be filtered
			int actionItemsCount = actionItems.size();
			notificationSuppression.filterNotificationSuppressedActionItems(actionItems, routeNodeInstance);
			assertTrue(actionItemsCount == actionItems.size());
			
			// if there is a suppression state for this ActionRequestValue, the ActionItem(s) should be filtered
			notificationSuppression.addNotificationSuppression(routeNodeInstance, reqVal);
			notificationSuppression.filterNotificationSuppressedActionItems(actionItems, routeNodeInstance);
			assertTrue(actionItems.size() == 0);
		}
		assertTrue(atLeastOne);
		
	}
	
	/**
	 * This method tests email suppression soup to nuts by routing / requeueing documents and meddling
	 * with the responsible parties.
	 * 
	 * @throws Exception
	 */
	@Test public void testSuppression() throws Exception {
		WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), TEST_DOC_TYPE);
		document.routeDocument("");

		assertTrue("the responsible party should have been notified",
				1 == getMockEmailService().immediateReminderEmailsSent("user1", document.getDocumentId(), 
				KEWConstants.ACTION_REQUEST_COMPLETE_REQ));
		
		getMockEmailService().resetReminderCounts();
		
		List<RuleBaseValues> existingRules = 
			KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination(TEST_RULE_TEMPLATE, TEST_DOC_TYPE);
		assertNotNull(existingRules);
		assertEquals(1, existingRules.size());

		RuleBaseValues originalRule = existingRules.get(0);
		assertTrue("Original rule should be current.", originalRule.getCurrentInd());

		List<RuleResponsibility> originalResps = originalRule.getResponsibilities();
		assertEquals(1, originalResps.size());

		RuleResponsibility originalResp = originalResps.get(0);

		RuleTemplate ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(TEST_RULE_TEMPLATE);
		assertNotNull(ruleTemplate);
		assertNotNull(ruleTemplate.getRuleTemplateId());
		assertFalse(StringUtils.isEmpty(ruleTemplate.getName()));

		// save a new rule delegation
		RuleDelegation ruleDelegation = new RuleDelegation();
		ruleDelegation.setResponsibilityId(originalResp.getResponsibilityId());
		ruleDelegation.setDelegationType(DelegationType.PRIMARY.getCode());
		RuleBaseValues rule = new RuleBaseValues();
		ruleDelegation.setDelegationRuleBaseValues(rule);
		rule.setDelegateRule(true);
		rule.setActiveInd(true);
		rule.setCurrentInd(true);
		rule.setDocTypeName(originalRule.getDocTypeName());
		rule.setRuleTemplateId(ruleTemplate.getDelegationTemplateId());
		rule.setRuleTemplate(ruleTemplate);
		rule.setDescription("Description of this delegate rule");
		rule.setForceAction(true);
		RuleResponsibility delegationResponsibility = new RuleResponsibility();
		rule.getResponsibilities().add(delegationResponsibility);
		delegationResponsibility.setRuleBaseValues(rule);
		delegationResponsibility.setRuleResponsibilityName("user2");
		delegationResponsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);

		// reset mock service test data
		getMockEmailService().resetReminderCounts();
		MockDocumentRequeuerImpl.clearRequeuedDocumentIds();
		
		// this *SHOULD* requeue
		KEWServiceLocator.getRuleService().saveRuleDelegation(ruleDelegation, true);

		assertTrue("document should have been requeued",
				MockDocumentRequeuerImpl.getRequeuedDocumentIds().contains(document.getDocumentId()));
		
		assertTrue("should have notified user2", 
				1 == getMockEmailService().immediateReminderEmailsSent("user2", document.getDocumentId(), 
				KEWConstants.ACTION_REQUEST_COMPLETE_REQ));
		assertTrue("the responsible party that is delegating should not be notified",
				0 == getMockEmailService().immediateReminderEmailsSent("user1", document.getDocumentId(), 
				KEWConstants.ACTION_REQUEST_COMPLETE_REQ));

		getMockEmailService().resetReminderCounts();

		// now if we requeue, nobody should get notified
		MockDocumentRequeuerImpl.clearRequeuedDocumentIds();
		String applicationId = KEWServiceLocator.getRouteHeaderService().getApplicationIdByDocumentId(document.getDocumentId());
		MessageServiceNames.getDocumentRequeuerService((applicationId != null) ? applicationId : CoreConfigHelper.getApplicationId(),
				document.getDocumentId(), 0).requeueDocument(document.getDocumentId());
		//new DocumentRequeuerImpl().requeueDocument(document.getDocumentId());
		
		assertTrue("nobody should have been notified", 
				0 == getMockEmailService().immediateReminderEmailsSent("user2", document.getDocumentId(), 
				KEWConstants.ACTION_REQUEST_COMPLETE_REQ));
		assertTrue("nobody should have been notified",
				0 == getMockEmailService().immediateReminderEmailsSent("user1", document.getDocumentId(), 
				KEWConstants.ACTION_REQUEST_COMPLETE_REQ));
		
		getMockEmailService().resetReminderCounts();
	}
	
    private MockEmailNotificationService getMockEmailService() {
        return (MockEmailNotificationService)KEWServiceLocator.getActionListEmailService();
    }
}
