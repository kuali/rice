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
package org.kuali.rice.krad.service;

import org.junit.Test;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.AccountRequestDocument;
import org.kuali.rice.krad.test.document.RuleEventImpl;
import org.kuali.rice.krad.test.document.bo.Account;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import java.util.List;

import static org.junit.Assert.*;

/**
 * This class tests the DocumentService (currently only getNewDocument is tested).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentServiceTest extends KRADTestCase {

    public DocumentServiceTest() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setUserSession(new UserSession("quickstart"));
    }

    @Override
    public void tearDown() throws Exception {
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setUserSession(null);
        super.tearDown();
    }

    /**
     * This method tests getNewDocument
     *
     * @throws Exception
     */
    @Test public void testGetNewDocument() throws Exception {
        AccountRequestDocument travelDocument = (AccountRequestDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument("AccountRequest");
        WorkflowDocument wd =  travelDocument.getDocumentHeader().getWorkflowDocument();

        assertEquals("Initiator should be the current user", wd.getInitiatorPrincipalId(),
                GlobalVariables.getUserSession().getPerson().getPrincipalId());
    }

    /**
     * This method tests getNewDocument but the initiator is not the current user
     *
     * @throws Exception
     */
    @Test public void testGetNewDocumentDifferentInitiatorThanCurrentUser() throws Exception {
        AccountRequestDocument travelDocument = (AccountRequestDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument("AccountRequest", "testuser1");
        WorkflowDocument wd =  travelDocument.getDocumentHeader().getWorkflowDocument();

        assertEquals("Initiator should be testuser1", wd.getInitiatorPrincipalId(), "testuser1");
    }

    /**
     * This method tests getNewDocument but the initiator is invalid
     *
     * @throws Exception
     */
    @Test public void testGetNewDocumentInvalidInitiator() throws Exception {
        AccountRequestDocument travelDocument = (AccountRequestDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument("AccountRequest", "notValidUserAtAll");
        WorkflowDocument wd =  travelDocument.getDocumentHeader().getWorkflowDocument();

        assertEquals("Initiator should be the current user", wd.getInitiatorPrincipalId(), GlobalVariables.getUserSession().getPerson().getPrincipalId());
    }

    /**
     * tests saveDocument, in particular, the save document rule event and the custom rule method
     * invocation of the business rule associated with the document.
     *
     * @throws Exception
     */
    @Test
    public void testSaveDocument_DocumentEvent() throws Exception {
        MaintenanceDocument maintenanceDocument = ( MaintenanceDocument ) KRADServiceLocatorWeb
                .getDocumentService().getNewDocument( "AccountMaintenanceDocument" );

        Account account = ( Account ) maintenanceDocument.getNewMaintainableObject().getDataObject();

        SaveDocumentEvent documentEvent = new SaveDocumentEvent( maintenanceDocument );
        documentEvent.setName( "DocumentControllerBaseSaveDocumentRuleTest#testSave_SaveDocumentEvent()" );
        documentEvent.setRuleMethodName( "processEvent" );

        Document savedDocument = KRADServiceLocatorWeb.getDocumentService()
                .saveDocument(maintenanceDocument, documentEvent);

        assertNull( "New maintenance document should not have a version number yet.",
                maintenanceDocument.getDocumentHeader().getVersionNumber() );
        assertNotNull( "Saved maintenance document must have a version number.", savedDocument.getDocumentHeader().getVersionNumber() );

        List<ErrorMessage> msgs = GlobalVariables.getMessageMap().getInfoMessagesForProperty( documentEvent.getName() );

        assertEquals( "There must be one entry added by the business rule method.", 1, msgs.size() );
        assertEquals( "The message set by the business rule must match the test message.",
                documentEvent.getRuleMethodName() + "()", msgs.get(0).toString() );
    }

    /**
     * tests saveDocument, in particular, the save document rule event and the default rule method
     * invocation of the business rule associated with the document.
     *
     * @throws Exception
     */
    @Test
    public void testSaveDocument_Default() throws Exception {
        MaintenanceDocument maintenanceDocument = ( MaintenanceDocument ) KRADServiceLocatorWeb
                .getDocumentService().getNewDocument( "AccountMaintenanceDocument" );

        Account account = ( Account ) maintenanceDocument.getNewMaintainableObject().getDataObject();

        RuleEventImpl documentEvent = new RuleEventImpl( maintenanceDocument );
        documentEvent.setName("DocumentControllerBaseSaveDocumentRuleTest#testSave_Default()");

        Document savedDocument = KRADServiceLocatorWeb.getDocumentService()
                .saveDocument(maintenanceDocument, documentEvent);

        assertNull( "New maintenance document should not have a version number yet.",
                maintenanceDocument.getDocumentHeader().getVersionNumber() );
        assertNotNull( "Saved maintenance document must have a version number.", savedDocument.getDocumentHeader().getVersionNumber() );

        List<ErrorMessage> msgs = GlobalVariables.getMessageMap()
                .getInfoMessagesForProperty( documentEvent.getClass().getName() );

        assertEquals( "There must be one entry added by the business rule method.", 1, msgs.size() );
        assertEquals( "The message set by the business rule must match the test message.",
                "org.kuali.rice.krad.test.document.AccountRules" + "()", msgs.get( 0 ).toString() );
    }
}
