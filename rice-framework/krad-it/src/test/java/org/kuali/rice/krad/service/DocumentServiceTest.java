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

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentBase;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentBase;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.AccountRequestDocument;
import org.kuali.rice.krad.test.document.AccountRequestDocument2;
import org.kuali.rice.krad.test.document.RuleEventImpl;
import org.kuali.rice.krad.test.document.bo.Account;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.NoteType;

import java.util.Arrays;
import java.util.Collections;
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

    /**
     * Tests getting documents by a list of document numbers.
     *
     * @throws Exception for any test issues
     */
    @Test
    public void testGetDocumentsByListOfDocumentHeaderIds_Default() throws Exception {
        Document document1 = KRADServiceLocatorWeb.getDocumentService().getNewDocument("AccountMaintenanceDocument");
        Document document2 = KRADServiceLocatorWeb.getDocumentService().getNewDocument("AccountMaintenanceDocument");

        RuleEventImpl documentEvent1 = new RuleEventImpl(document1);
        documentEvent1.setName("saving document 1");
        RuleEventImpl documentEvent2 = new RuleEventImpl(document2);
        documentEvent2.setName("saving document 2");

        Document savedDocument1 = KRADServiceLocatorWeb.getDocumentService().saveDocument(document1, documentEvent1);
        Document savedDocument2 = KRADServiceLocatorWeb.getDocumentService().saveDocument(document2, documentEvent2);

        String documentNumber1 = savedDocument1.getDocumentNumber();
        String documentNumber2 = savedDocument2.getDocumentNumber();

        List<Document> documents = KRADServiceLocatorWeb.getDocumentService().getDocumentsByListOfDocumentHeaderIds(
                MaintenanceDocumentBase.class, Arrays.asList(documentNumber1, documentNumber2));

        assertEquals("wrong number of documents found", 2, documents.size());

        for (Document document : documents) {
            assertNotNull("document was null", document);
        }
    }

    /**
     * Tests getting documents by a list of document numbers which don't exist.
     *
     * @throws Exception for any test issues
     */
    @Test
    public void testGetDocumentsByListOfDocumentHeaderIds_MissingDocuments() throws Exception {
        String fakeDocumentNumber1 = "1234";
        String fakeDocumentNumber2 = "5678";

        List<Document> documents = KRADServiceLocatorWeb.getDocumentService().getDocumentsByListOfDocumentHeaderIds(
                MaintenanceDocumentBase.class, Arrays.asList(fakeDocumentNumber1, fakeDocumentNumber2));

        assertTrue("documents found", documents.isEmpty());
    }

    /**
     * Test getting notes via getNotes, which will call getNoteService().getByRemoteObjectId
     *
     * @throws Exception for any test issues
     */
    @Test
    public void testGetNotes() throws Exception {
        AccountRequestDocument2 accountDoc2 = (AccountRequestDocument2) KRADServiceLocatorWeb.getDocumentService().getNewDocument("AccountRequest2");
        String remoteObjectId = "remoteObjectId" + RandomStringUtils.randomNumeric(5);
        String noteText = "i like notes";

        WorkflowDocument workflowDocument = WorkflowDocumentFactory.createDocument("admin","AccountRequest2");
        DocumentHeader documentHeader = new DocumentHeader();
        documentHeader.setWorkflowDocument(workflowDocument);
        documentHeader.setDocumentNumber(workflowDocument.getDocumentId());
        documentHeader.setObjectId(remoteObjectId);
        accountDoc2.setDocumentHeader(documentHeader);

        Note note = new Note();
        note.setAuthorUniversalIdentifier("superLongNameUsersFromWorkflow");
        note.setNotePostedTimestamp(CoreApiServiceLocator.getDateTimeService().getCurrentTimestamp());
        note.setNoteText(noteText);
        note.setRemoteObjectIdentifier(remoteObjectId);
        note.setNoteTypeCode(NoteType.BUSINESS_OBJECT.getCode());
        try {
            KRADServiceLocator.getNoteService().save(note);
        } catch (Exception e) {
            fail("Saving a note should not fail");
        }

        Note note2 = new Note();
        note2.setAuthorUniversalIdentifier("admin");
        note2.setNotePostedTimestamp(CoreApiServiceLocator.getDateTimeService().getCurrentTimestamp());
        note2.setNoteText(noteText);
        note2.setRemoteObjectIdentifier(remoteObjectId);
        note2.setNoteTypeCode(NoteType.BUSINESS_OBJECT.getCode());
        try {
            KRADServiceLocator.getNoteService().save(note2);
            KradDataServiceLocator.getDataObjectService().flush(Note.class);
        } catch (Exception e) {
            fail("Saving a note should not fail");
        }

        List<Note> notes = accountDoc2.getNotes();

        assertTrue("Size of the notes list should be 2", notes.size() == 2);
        assertFalse("List is immutable but it should be mutable.",
                Collections.unmodifiableList(notes).getClass().isInstance(notes));

        for (Note note1 : notes) {
            assertNotNull("document was null", note1);
            assertTrue(note1.getNoteText().equalsIgnoreCase(noteText));
        }
    }

}