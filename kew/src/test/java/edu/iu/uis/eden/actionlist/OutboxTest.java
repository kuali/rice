/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.actionlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.workflow.test.KEWTestCase;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.preferences.Preferences;
import edu.iu.uis.eden.preferences.PreferencesServiceImpl;
import edu.iu.uis.eden.routetemplate.TestRuleAttribute;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.useroptions.UserOptions;

/**
 * Tests Outbox functionality
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OutboxTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("OutboxTestConfig.xml");
    }

    @Test
    public void testOutboxItemNotSavedOnSavedDocumentStatus() throws Exception {
        final WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
        List<Id> recipients = new ArrayList<Id>();
        recipients.add(new AuthenticationUserId("rkirkend"));
        TestRuleAttribute.setRecipients("TestRole", "qualRole", recipients);

        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("quickstart"), "TestDocumentType");
        document.routeDocument("");

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());

        new TransactionTemplate(KEWServiceLocator.getPlatformTransactionManager()).execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                UserOptions option = KEWServiceLocator.getUserOptionsService().findByOptionId(PreferencesServiceImpl.USE_OUT_BOX, rkirkend);
                option.setOptionVal(EdenConstants.PREFERENCES_YES_VAL);
                KEWServiceLocator.getUserOptionsService().save(option);
                return null;
            }
        });

        document.saveDocument("");

        Collection outbox = KEWServiceLocator.getActionListService().getOutbox(rkirkend, new ActionListFilter());
        assertEquals("there should be an outbox item", 0, outbox.size());
    }

    @Test
    public void testTakeActionCreatesOutboxItem() throws Exception {

        final WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
        List<Id> recipients = new ArrayList<Id>();
        recipients.add(new AuthenticationUserId("rkirkend"));
        TestRuleAttribute.setRecipients("TestRole", "qualRole", recipients);

        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("quickstart"), "TestDocumentType");
        document.routeDocument("");

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());

        new TransactionTemplate(KEWServiceLocator.getPlatformTransactionManager()).execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                UserOptions option = KEWServiceLocator.getUserOptionsService().findByOptionId(PreferencesServiceImpl.USE_OUT_BOX, rkirkend);
                option.setOptionVal(EdenConstants.PREFERENCES_YES_VAL);
                KEWServiceLocator.getUserOptionsService().save(option);
                return null;
            }
        });

        document.approve("");

        Collection outbox = KEWServiceLocator.getActionListService().getOutbox(rkirkend, new ActionListFilter());
        assertEquals("there should be an outbox item", 1, outbox.size());
    }

    @Test
    public void testSingleOutboxItemPerDocument() throws Exception {
        final WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
        List<Id> recipients = new ArrayList<Id>();
        recipients.add(new AuthenticationUserId("rkirkend"));
        recipients.add(new AuthenticationUserId("user1"));
        TestRuleAttribute.setRecipients("TestRole", "qualRole", recipients);

        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("quickstart"), "TestDocumentType");
        document.routeDocument("");

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());

        new TransactionTemplate(KEWServiceLocator.getPlatformTransactionManager()).execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                UserOptions option = KEWServiceLocator.getUserOptionsService().findByOptionId(PreferencesServiceImpl.USE_OUT_BOX, rkirkend);
                option.setOptionVal(EdenConstants.PREFERENCES_YES_VAL);
                KEWServiceLocator.getUserOptionsService().save(option);
                return null;
            }
        });

        document.appSpecificRouteDocumentToUser("A", "", new NetworkIdVO("user1"), "", true);

        document.approve("");

        Collection outbox = KEWServiceLocator.getActionListService().getOutbox(rkirkend, new ActionListFilter());
        assertEquals("there should be an outbox item", 1, outbox.size());

        document = new WorkflowDocument(new NetworkIdVO("user1"), document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());

        document.appSpecificRouteDocumentToUser("A", "", new NetworkIdVO("rkirkend"), "", true);

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("approve should be requested", document.isApprovalRequested());
        document.approve("");

        outbox = KEWServiceLocator.getActionListService().getOutbox(rkirkend, new ActionListFilter());
        assertEquals("there should be an outbox item", 1, outbox.size());
    }

    @Test
    public void testOnlyPersonWhoTookActionReceivesOutboxItem_Route() throws Exception {
        final WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
        final WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
        List<Id> recipients = new ArrayList<Id>();
        recipients.add(new AuthenticationUserId("rkirkend"));
        recipients.add(new AuthenticationUserId("user1"));
        TestRuleAttribute.setRecipients("TestRole", "qualRole", recipients);

        new TransactionTemplate(KEWServiceLocator.getPlatformTransactionManager()).execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                Preferences prefs = KEWServiceLocator.getPreferencesService().getPreferences(rkirkend);
                if (!prefs.isUsingOutbox()) {
                    UserOptions option = KEWServiceLocator.getUserOptionsService().findByOptionId(PreferencesServiceImpl.USE_OUT_BOX, rkirkend);
                    option.setOptionVal(EdenConstants.PREFERENCES_YES_VAL);
                    KEWServiceLocator.getUserOptionsService().save(option);
                }

                prefs = KEWServiceLocator.getPreferencesService().getPreferences(user1);
                if (!prefs.isUsingOutbox()) {
                    UserOptions option = KEWServiceLocator.getUserOptionsService().findByOptionId(PreferencesServiceImpl.USE_OUT_BOX, user1);
                    option.setOptionVal(EdenConstants.PREFERENCES_YES_VAL);
                    KEWServiceLocator.getUserOptionsService().save(option);
                }
                return null;
            }
        });

        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("quickstart"), "TestDocumentType");
        document.routeDocument("");

        // verify test is sane and users have action items
        assertFalse(KEWServiceLocator.getActionListService().getActionList(rkirkend, new ActionListFilter()).isEmpty());
        assertFalse(KEWServiceLocator.getActionListService().getActionList(user1, new ActionListFilter()).isEmpty());

        document = new WorkflowDocument(new NetworkIdVO("user1"), document.getRouteHeaderId());
        document.approve("");
        // verify only user who took action has the outbox item
        assertTrue(KEWServiceLocator.getActionListService().getOutbox(rkirkend, new ActionListFilter()).isEmpty());
        assertEquals(1, KEWServiceLocator.getActionListService().getOutbox(user1, new ActionListFilter()).size());
    }

    @Test
    public void testOnlyPersonWhoTookActionReceivesOutboxItem_BlanketApprove() throws Exception {
        final WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
        final WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
        List<Id> recipients = new ArrayList<Id>();
        recipients.add(new AuthenticationUserId("rkirkend"));
        recipients.add(new AuthenticationUserId("user1"));
        TestRuleAttribute.setRecipients("TestRole", "qualRole", recipients);

        new TransactionTemplate(KEWServiceLocator.getPlatformTransactionManager()).execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                Preferences prefs = KEWServiceLocator.getPreferencesService().getPreferences(rkirkend);
                if (!prefs.isUsingOutbox()) {
                    UserOptions option = KEWServiceLocator.getUserOptionsService().findByOptionId(PreferencesServiceImpl.USE_OUT_BOX, rkirkend);
                    option.setOptionVal(EdenConstants.PREFERENCES_YES_VAL);
                    KEWServiceLocator.getUserOptionsService().save(option);
                }

                prefs = KEWServiceLocator.getPreferencesService().getPreferences(user1);
                if (!prefs.isUsingOutbox()) {
                    UserOptions option = KEWServiceLocator.getUserOptionsService().findByOptionId(PreferencesServiceImpl.USE_OUT_BOX, user1);
                    option.setOptionVal(EdenConstants.PREFERENCES_YES_VAL);
                    KEWServiceLocator.getUserOptionsService().save(option);
                }
                return null;
            }
        });

        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
        document.blanketApprove("");
        // verify only user who took action has the outbox item
        assertEquals("Wrong number of outbox items found for rkirkend", 0, KEWServiceLocator.getActionListService().getOutbox(rkirkend, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for user1", 0, KEWServiceLocator.getActionListService().getOutbox(user1, new ActionListFilter()).size());

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
        document.saveDocument("");
        // verify test is sane and users have action items
        assertEquals("Wrong number of action items found for rkirkend", 1, KEWServiceLocator.getActionListService().getActionList(rkirkend, new ActionListFilter()).size());
        // verify that outboxes of two users are clear
        assertEquals("Wrong number of outbox items found for rkirkend", 0, KEWServiceLocator.getActionListService().getOutbox(rkirkend, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for user1", 0, KEWServiceLocator.getActionListService().getOutbox(user1, new ActionListFilter()).size());

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        document.blanketApprove("");
        // verify only user who took action has the outbox item
        assertEquals("Wrong number of outbox items found for rkirkend", 1, KEWServiceLocator.getActionListService().getOutbox(rkirkend, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for user1", 0, KEWServiceLocator.getActionListService().getOutbox(user1, new ActionListFilter()).size());
    }

    @Test
    public void testOnlyPersonWhoTookActionReceivesOutboxItem_Workgroup() throws Exception {
        loadXmlFile("OutboxTestConfig.xml");
        final WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
        final WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
        final WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));

        new TransactionTemplate(KEWServiceLocator.getPlatformTransactionManager()).execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                Preferences prefs = KEWServiceLocator.getPreferencesService().getPreferences(rkirkend);
                if (!prefs.isUsingOutbox()) {
                    UserOptions option = KEWServiceLocator.getUserOptionsService().findByOptionId(PreferencesServiceImpl.USE_OUT_BOX, rkirkend);
                    option.setOptionVal(EdenConstants.PREFERENCES_YES_VAL);
                    KEWServiceLocator.getUserOptionsService().save(option);
                }

                prefs = KEWServiceLocator.getPreferencesService().getPreferences(user1);
                if (!prefs.isUsingOutbox()) {
                    UserOptions option = KEWServiceLocator.getUserOptionsService().findByOptionId(PreferencesServiceImpl.USE_OUT_BOX, user1);
                    option.setOptionVal(EdenConstants.PREFERENCES_YES_VAL);
                    KEWServiceLocator.getUserOptionsService().save(option);
                }
                return null;
            }
        });

        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user2"), "OutboxTestDocumentType");
        document.routeDocument("");
        // verify action items exist
        assertEquals("Wrong number of action items found for rkirkend", 1, KEWServiceLocator.getActionListService().getActionList(rkirkend, new ActionListFilter()).size());
        assertEquals("Wrong number of action items found for ewestfal", 1, KEWServiceLocator.getActionListService().getActionList(ewestfal, new ActionListFilter()).size());
        assertEquals("Wrong number of action items found for user1", 1, KEWServiceLocator.getActionListService().getActionList(user1, new ActionListFilter()).size());
        // verify outboxes are clear
        assertEquals("Wrong number of outbox items found for rkirkend", 0, KEWServiceLocator.getActionListService().getOutbox(rkirkend, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for ewestfal", 0, KEWServiceLocator.getActionListService().getOutbox(ewestfal, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for user1", 0, KEWServiceLocator.getActionListService().getOutbox(user1, new ActionListFilter()).size());

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        document.approve("");
        // verify only user who took action has the outbox item
        assertEquals("Wrong number of outbox items found for rkirkend", 1, KEWServiceLocator.getActionListService().getOutbox(rkirkend, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for ewestfal", 0, KEWServiceLocator.getActionListService().getOutbox(user1, new ActionListFilter()).size());
        assertEquals("Wrong number of outbox items found for user1", 0, KEWServiceLocator.getActionListService().getOutbox(user1, new ActionListFilter()).size());
    }

    @Test
    public void testOutBoxDefaultPreferenceOnConfigParam() throws Exception {
        WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
        Preferences prefs = KEWServiceLocator.getPreferencesService().getPreferences(user1);
        assertTrue("By default the user's pref should be outbox on", prefs.isUsingOutbox());

        Core.getCurrentContextConfig().overrideProperty(Config.OUT_BOX_DEFAULT_PREFERENCE_ON, "false");
        WorkflowUser natjohns = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("natjohns"));
        prefs = KEWServiceLocator.getPreferencesService().getPreferences(natjohns);
        assertFalse("The user's pref should be outbox off", prefs.isUsingOutbox());
    }

}