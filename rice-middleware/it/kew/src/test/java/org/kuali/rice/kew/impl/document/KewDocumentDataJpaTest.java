/*
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

package org.kuali.rice.kew.impl.document;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.docsearch.SearchableAttributeDateTimeValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeStringValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.documentlink.DocumentLink;
import org.kuali.rice.kew.engine.node.Branch;
import org.kuali.rice.kew.engine.node.BranchState;
import org.kuali.rice.kew.engine.node.NodeState;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.notes.Attachment;
import org.kuali.rice.kew.notes.Note;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueContent;
import org.kuali.rice.kew.routeheader.DocumentStatusTransition;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.test.TestHarnessServiceLocator;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests to confirm JPA mapping for the Kew module Document data objects
 */
public class KewDocumentDataJpaTest extends KEWTestCase {
    public static final String TEST_DOC_ID = "1234";
    public static final String SEARCHABLE_ATTR_ID = "1000";
    public static final String SEARCHABLE_ATTR_KEY = "testAttribute";
    public static final String SEARCHABLE_ATTR_VAL = "testing";
    public static final Timestamp SEARCHABLE_ATTR_VAL_DT = new Timestamp(System.currentTimeMillis());

    public static final String DOC_CNT = "<xml>some content</xml>";
    public static final String NOTE_TXT = "noteText";

    @Test
    public void testDocumentContentFetchAndSave() throws Exception{
        DocumentRouteHeaderValueContent dc = setupDocumentValueContent();
        assertTrue("DocumentRouteHeaderContent persisted", dc != null && StringUtils.equals(TEST_DOC_ID,
                dc.getDocumentId()) && StringUtils.equals(dc.getDocumentContent(),DOC_CNT));

        dc = KRADServiceLocator.getDataObjectService().find(DocumentRouteHeaderValueContent.class,TEST_DOC_ID);
        assertTrue("DocumentRouteHeaderContent refetched", dc != null && StringUtils.equals(TEST_DOC_ID,
                dc.getDocumentId()) && StringUtils.equals(dc.getDocumentContent(),DOC_CNT));
    }

    @Test
    public void testDocumentRouteHeaderValueFindByDocumentId() throws Exception{
        DocumentRouteHeaderValue savedVal = setupDocumentRouteHeaderValue();
        DocumentRouteHeaderValue fetchedVal = KEWServiceLocator.getRouteHeaderService().
                                            getRouteHeader(savedVal.getDocumentId(), true);
        assertTrue("getRouteHeader fetched correctly",fetchedVal != null &&
                StringUtils.equals(fetchedVal.getDocumentId(),savedVal.getDocumentId()));

    }

    @Test
    public void testDocumentRouteHeaderValueGetRouteHeaders() throws Exception{
        DocumentRouteHeaderValue savedRouteHeader = setupDocumentRouteHeaderValue();
        setupDocumentRouteHeaderValueWithRouteHeaderAssigned();
        List<String> documentIds = new ArrayList<String>();
        documentIds.add(TEST_DOC_ID);
        documentIds.add(savedRouteHeader.getDocumentId());

        Collection<DocumentRouteHeaderValue> routeHeaders =
                KEWServiceLocator.getRouteHeaderService().getRouteHeaders(documentIds,true);
        assertTrue("getRouteHeaders fetched",routeHeaders != null && routeHeaders.size() == 2);
    }

    @Test
    public void testDocumentRouteHeaderValueLockRouteHeader() throws Exception{
        DocumentRouteHeaderValue dv = setupDocumentRouteHeaderValue();
        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(dv.getDocumentId());
        dv = KEWServiceLocator.getRouteHeaderService().getRouteHeader(dv.getDocumentId());
        dv.setAppDocStatus("X");
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(dv);
    }

    @Test
    public void testDocumentRouteHeaderValueSaveRouteHeader() throws Exception{
        DocumentRouteHeaderValue dv = setupDocumentRouteHeaderValue();
        dv.setAppDocStatus("X");
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(dv);
        assertTrue("saveRouteHeader saved correctly",StringUtils.equals(dv.getAppDocStatus(),"X"));
    }

    @Test(expected = OptimisticLockingFailureException.class)
    public void testDocumentRouteHeaderValueSaveRouteHeaderThrowsOptimisticException() throws Exception{
        DocumentRouteHeaderValue dv = setupDocumentRouteHeaderValue();
        dv.setVersionNumber(null);
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(dv);
        fail();
    }

    @Test
    public void testDocumentRouteHeaderValueDelete() throws Exception{
        DocumentRouteHeaderValue dv = setupDocumentRouteHeaderValueWithRouteHeaderAssigned();

        KEWServiceLocator.getRouteHeaderService().deleteRouteHeader(dv);
        dv = KEWServiceLocator.getRouteHeaderService().getRouteHeader(dv.getDocumentId());
        assertTrue("Route Header deleted", dv == null);
    }

    @Test
    public void testDocumentRouteHeaderValueGetNextDocumentId() throws Exception{
        String val = KEWServiceLocator.getRouteHeaderService().getNextDocumentId();
        assertTrue("Next value found",StringUtils.isNotEmpty(val));
    }

    @Test
    public void testDocumentRouteHeaderValueClearRouteHeaderSearchValues() throws Exception{
        setupDocumentRouteHeaderValueWithRouteHeaderAssigned();
        setupSearchAttributeStringValue();
        KEWServiceLocator.getRouteHeaderService().clearRouteHeaderSearchValues(TEST_DOC_ID);
    }
    @Test
    public void testDocumentRouteHeaderValueHasSearchableAttributeValue() throws Exception{
        setupSearchAttributeStringValue();
        setupSearchAttributeDateTimeValue();

        boolean hasSAValue = KEWServiceLocator.getRouteHeaderService().hasSearchableAttributeValue(TEST_DOC_ID,
                SEARCHABLE_ATTR_KEY,SEARCHABLE_ATTR_VAL);
        assertTrue("hasSearchableAttributeValue found", hasSAValue == true);
    }


    @Test
    public void testDocumentStatusTransition() throws Exception{
        DocumentRouteHeaderValue documentRouteHeaderValue = setupDocumentRouteHeaderValueWithRouteHeaderAssigned();
        DocumentStatusTransition documentStatusTransition = setupDocumentStatusTransition();
        //Modify status
        documentRouteHeaderValue.getAppDocStatusHistory().add(documentStatusTransition);
        documentStatusTransition.setOldAppDocStatus("R");


        assertTrue("Document Status Transition saved and persisted",
                documentStatusTransition != null && StringUtils.isNotEmpty(
                        documentStatusTransition.getStatusTransitionId()));

        DocumentStatusTransition fetchedVal = KRADServiceLocator.getDataObjectService().
                find(DocumentStatusTransition.class, documentStatusTransition.getStatusTransitionId());
        assertTrue("DocumentStatusTransition fetched after save",fetchedVal != null &&
                StringUtils.equals(fetchedVal.getDocumentId(),fetchedVal.getDocumentId()));

        KRADServiceLocator.getDataObjectService().save(documentRouteHeaderValue);
        DocumentRouteHeaderValue newHeaderVal = KRADServiceLocator.getDataObjectService().find(
                DocumentRouteHeaderValue.class,documentRouteHeaderValue.getDocumentId());

                assertTrue("On fetch app doc history updated", newHeaderVal != null &&
                StringUtils.equals(newHeaderVal.getAppDocStatusHistory().get(0).getOldAppDocStatus(),"R"));
    }

    @Test
    public void testBranchState() throws Exception {
        Branch branch = setupRouteBranch();
        branch = setupRouteBranchState(branch);
        assertTrue("BranchState persisted", branch != null &&
               branch.getBranchState() != null &&
                    StringUtils.isNotBlank(branch.getBranchState().get(0).getBranchStateId()));
        branch= KradDataServiceLocator.getDataObjectService().find(
                Branch.class,branch.getBranchId());
        assertTrue("BranchState fetched",branch != null && branch.getBranchState().get(0) != null);

    }

    @Test
    public void testRouteHeaderInitJoinTable() throws Exception{
        DocumentRouteHeaderValue dv = setupDocumentRouteHeaderValueWithRouteHeaderAssigned();
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        RouteNodeInstance rni = setupRouteNodeInstance(rn);
        setupInitialRouteNodeInstancesJoinTable(rni.getRouteNodeInstanceId());
        dv = KradDataServiceLocator.getDataObjectService().find(DocumentRouteHeaderValue.class,
                TEST_DOC_ID);

        dv.getInitialRouteNodeInstances().add(rni);


        assertTrue("DocumentRouteHeaderValue fetched correctly with joined instance",dv!= null
                && dv.getInitialRouteNodeInstances() != null && dv.getInitialRouteNodeInstances().get(0)!= null
                && StringUtils.equals(dv.getInitialRouteNodeInstances().get(0).getRouteNodeInstanceId(),
                rni.getRouteNodeInstanceId()));

    }

    @Test
    public void testBranch() throws Exception {
        Branch b = setupRouteBranch();
        assertTrue("branch persisted correctly", b != null && StringUtils.isNotBlank(b.getBranchId())
                && b.getLockVerNbr() > 0);
    }

    @Test
    public void testNodeStates() throws Exception{
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        RouteNodeInstance savedVal = setupRouteNodeInstance(rn);
        NodeState ns = setupNodeState(savedVal);
        savedVal.getState().add(ns);
        KRADServiceLocator.getDataObjectService().save(savedVal);
        assertTrue("RouteNodeInstance saved and persisted", savedVal != null && StringUtils.isNotEmpty(
                savedVal.getRouteNodeInstanceId()));

        savedVal = KRADServiceLocator.getDataObjectService().find(RouteNodeInstance.class,
                savedVal.getRouteNodeInstanceId());
        assertTrue("NodeStates fetched correctly", savedVal.getState() != null && savedVal.getState().size() == 1);
    }

    @Test
    public void testRouteNodeInstance() throws Exception{
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        RouteNodeInstance savedVal = setupRouteNodeInstance(rn);
        assertTrue("DocumentRouteHeaderValue saved and persisted", savedVal != null && StringUtils.isNotEmpty(
                savedVal.getRouteNodeInstanceId()));

        RouteNodeInstance fetchedValue = KRADServiceLocator.getDataObjectService().
                find(RouteNodeInstance.class, savedVal.getRouteNodeInstanceId());
        assertTrue("DocumentRouteHeaderValue fetched after save",fetchedValue != null &&
                StringUtils.equals(savedVal.getDocumentId(),fetchedValue.getDocumentId()));

    }

    @Test
    public void testNote() throws Exception{
        Note note = setupNote();
        assertTrue("Note persisted correctly",note != null && StringUtils.isNotBlank(note.getNoteId()));
        note.setNoteText("ModifiedText");
        KRADServiceLocator.getDataObjectService().save(note);
        note = KRADServiceLocator.getDataObjectService().find(Note.class,note.getNoteId());
        assertTrue("Note persisted correctly",note != null && StringUtils.equals(note.getNoteText(), "ModifiedText"));
    }

    @Test
    public void testNoteService() throws Exception{
        Note note = setupNote();
        assertTrue("Note persisted correctly",note != null && StringUtils.isNotBlank(note.getNoteId()));
        List<Note> notes = KEWServiceLocator.getNoteService().getNotesByDocumentId(TEST_DOC_ID);
        assertTrue("getNotesByDocumentId fetched correctly",notes != null && notes.size() == 1);
    }

    @Test
    public void testAttachment() throws Exception {
        Note note = setupNote();
        Attachment attachment = setupAttachment(note);
        note.getAttachments().add(attachment);

        assertTrue("Attachment persisted correctly", attachment != null &&
                        StringUtils.isNotBlank(attachment.getAttachmentId()));
        note = KEWServiceLocator.getNoteService().getNoteByNoteId(note.getNoteId());
        assertTrue("Attachment fetched on note fetch",note != null && note.getAttachments() != null &&
                       note.getAttachments().size() == 1 &&
                StringUtils.equals(note.getAttachments().get(0).getAttachmentId(),attachment.getAttachmentId()));
    }

    @Test
    public void testDocumentLink() throws Exception{
        DocumentLink documentLink = setupDocumentLink(TEST_DOC_ID,"9999");
        assertTrue("documentlink persisted correctly", documentLink != null &&
                    StringUtils.isNotBlank(documentLink.getDocLinkId()));
        KEWServiceLocator.getDocumentLinkService().deleteDocumentLink(documentLink);
        documentLink = KRADServiceLocator.getDataObjectService().find(DocumentLink.class,documentLink.getDocLinkId());
        assertTrue("documentlink was deleted",documentLink == null);

    }

    @Test
    public void testRouteNodeServiceDeleteByRouteNodeInstance() throws Exception{
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        RouteNodeInstance routeNodeInstance = setupRouteNodeInstance(rn);
        routeNodeInstance.setRouteNode(rn);
        routeNodeInstance = KRADServiceLocator.getDataObjectService().save(routeNodeInstance);
        assertTrue("Route node instance persisted with route node",
                routeNodeInstance != null && StringUtils.isNotBlank(routeNodeInstance.getRouteNodeId()));
        String routeNodeInstanceId = routeNodeInstance.getRouteNodeInstanceId();
        KEWServiceLocator.getRouteNodeService().deleteByRouteNodeInstance(routeNodeInstance);
        routeNodeInstance = KradDataServiceLocator.getDataObjectService().find(RouteNodeInstance.class,
                routeNodeInstanceId);
        assertTrue("RouteNodeInstanceDeleted successfully", routeNodeInstance == null);

    }

    @Test
    public void testRouteNodeServiceGetActiveNodeInstances() throws Exception{
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        RouteNodeInstance routeNodeInstance = setupRouteNodeInstance(rn);
        routeNodeInstance.setRouteNode(rn);
        routeNodeInstance = KRADServiceLocator.getDataObjectService().save(routeNodeInstance);
        assertTrue("Route node instance persisted with route node",
                routeNodeInstance != null && StringUtils.isNotBlank(routeNodeInstance.getRouteNodeId()));
        List<RouteNodeInstance> routeNodeInstances = KEWServiceLocator.getRouteNodeService().
                                    getActiveNodeInstances(routeNodeInstance.getDocumentId());
        assertTrue("Route node instances found", routeNodeInstances != null && routeNodeInstances.size() == 1);
    }

    @Test
    public void testRouteNodeServiceGetCurrentRouteNodeNames() throws Exception{
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        RouteNodeInstance routeNodeInstance = setupRouteNodeInstance(rn);
        routeNodeInstance.setRouteNode(rn);
        routeNodeInstance = KRADServiceLocator.getDataObjectService().save(routeNodeInstance);
        assertTrue("Route node instance persisted with route node",
                routeNodeInstance != null && StringUtils.isNotBlank(routeNodeInstance.getRouteNodeId()));
        List<String> routeNodeNames = KEWServiceLocator.getRouteNodeService().
                getCurrentRouteNodeNames(routeNodeInstance.getDocumentId());
        assertTrue("Route node instances found", routeNodeNames != null && routeNodeNames.size() == 1);

    }

    @Test
    public void testRouteNodeServiceGetActiveRouteNodeNames() throws Exception{
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        RouteNodeInstance routeNodeInstance = setupRouteNodeInstance(rn);
        routeNodeInstance.setRouteNode(rn);
        routeNodeInstance = KRADServiceLocator.getDataObjectService().save(routeNodeInstance);
        assertTrue("Route node instance persisted with route node",
                routeNodeInstance != null && StringUtils.isNotBlank(routeNodeInstance.getRouteNodeId()));
        List<String> routeNodeNames = KEWServiceLocator.getRouteNodeService().
                getActiveRouteNodeNames(routeNodeInstance.getDocumentId());
        assertTrue("Route node instances found", routeNodeNames != null && routeNodeNames.size() == 1);
    }

    @Test
    public void testRouteNodeServiceGetTerminalNodeInstances() throws Exception{
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        RouteNodeInstance routeNodeInstance = setupRouteNodeInstance(rn);
        routeNodeInstance.setActive(false);
        routeNodeInstance.setComplete(true);
        routeNodeInstance.setRouteNode(rn);
        routeNodeInstance = KRADServiceLocator.getDataObjectService().save(routeNodeInstance);
        assertTrue("Route node instance persisted with route node",
                routeNodeInstance != null && StringUtils.isNotBlank(routeNodeInstance.getRouteNodeId()));
        List<RouteNodeInstance> routeNodeList = KEWServiceLocator.getRouteNodeService().
                getTerminalNodeInstances(routeNodeInstance.getDocumentId());
        assertTrue("Route node instances found", routeNodeList != null && routeNodeList.size() == 1);

        List<String> routeNodeNames = KEWServiceLocator.getRouteNodeService().getTerminalRouteNodeNames(
                                    routeNodeInstance.getDocumentId());
        assertTrue("Route node instances found", routeNodeNames != null && routeNodeNames.size() == 1);

    }

    @Test
    public void testRouteNodeServiceGetInitialNodeInstances() throws Exception{
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        RouteNodeInstance routeNodeInstance = setupRouteNodeInstance(rn);
        routeNodeInstance.setRouteNode(rn);
        routeNodeInstance = KRADServiceLocator.getDataObjectService().save(routeNodeInstance);

        assertTrue("Route node instance persisted with route node",
                routeNodeInstance != null && StringUtils.isNotBlank(routeNodeInstance.getRouteNodeId()));
        DocumentRouteHeaderValue drv = setupDocumentRouteHeaderValueWithRouteHeaderAssigned();
        drv.getInitialRouteNodeInstances().add(routeNodeInstance);
        KRADServiceLocator.getDataObjectService().save(drv);

        List<RouteNodeInstance> routeNodeInstances = KEWServiceLocator.getRouteNodeService().getInitialNodeInstances(
                                                    routeNodeInstance.getDocumentId());
        assertTrue("Route node instances found", routeNodeInstances != null && routeNodeInstances.size() == 1);
    }

    @Test
    public void testRouteNodeServiceFindNodeState() throws Exception{
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        RouteNodeInstance routeNodeInstance = setupRouteNodeInstance(rn);
        routeNodeInstance.setRouteNode(rn);
        routeNodeInstance = KRADServiceLocator.getDataObjectService().save(routeNodeInstance);
        NodeState nodeState = setupNodeState(routeNodeInstance);
        assertTrue("Node state is persisted", nodeState != null && StringUtils.isNotBlank(nodeState.getNodeStateId()));
        String nodeStateId = nodeState.getNodeStateId();
        nodeState = KEWServiceLocator.getRouteNodeService().findNodeState(
                Long.parseLong(routeNodeInstance.getRouteNodeInstanceId()),nodeState.getKey());
        assertTrue("Node state is found", nodeState != null && StringUtils.equals(
                    nodeStateId,nodeState.getNodeStateId()));
    }

    @Test
    public void testRouteNodeServiceFindRouteNodeByName() throws Exception{
        DocumentType documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        String routeNodeId = rn.getRouteNodeId();
        rn = KEWServiceLocator.getRouteNodeService().findRouteNodeByName(
                                        rn.getDocumentTypeId(),rn.getRouteNodeName());
        assertTrue("Route node fetched correctly", rn != null && StringUtils.equals(routeNodeId,rn.getRouteNodeId()));

    }

    @Test
    public void testRouteNodeServiceSaveRouteNodeInstance() throws Exception {
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName("gooddoctype");
        if(documentType != null){
            KRADServiceLocator.getDataObjectService().delete(documentType);
        }

        documentType = setupDocumentType();
        RouteNode rn = setupRouteNode(documentType);
        assertNotNull(rn.getRouteNodeId());
        RouteNodeInstance rni = new RouteNodeInstance();
        rni.setDocumentId(TEST_DOC_ID);
        rni.setRouteNode(rn);
        rni = KRADServiceLocator.getDataObjectService().save(rni);
        assertNotNull(rni.getRouteNodeId());
        assertEquals(rn.getName(), rni.getName());
    }

    private DocumentLink setupDocumentLink(String desDocumentId, String orgDocumentId){
        DocumentLink documentLink = new DocumentLink();
        documentLink.setDestDocId(desDocumentId);
        documentLink.setOrgnDocId(orgDocumentId);
        KRADServiceLocator.getDataObjectService().save(documentLink);

        documentLink = new DocumentLink();
        documentLink.setOrgnDocId(desDocumentId);
        documentLink.setDestDocId(orgDocumentId);

        return KRADServiceLocator.getDataObjectService().save(documentLink);
    }

    private NodeState setupNodeState(RouteNodeInstance routeNodeInstance){
        NodeState nodeState = new NodeState();
        nodeState.setKey("TST");
        nodeState.setValue("VAL");
        nodeState.setNodeInstance(routeNodeInstance);

        return KRADServiceLocator.getDataObjectService().save(nodeState, PersistenceOption.FLUSH);

    }

    private Note setupNote(){
        Note note = new Note();
        note.setNoteText(NOTE_TXT);
        note.setNoteAuthorWorkflowId("1");
        note.setNoteCreateDate(new Timestamp(System.currentTimeMillis()));
        note.setDocumentId(TEST_DOC_ID);

        return KradDataServiceLocator.getDataObjectService().save(note);
    }

    private Attachment setupAttachment(Note note){
        Attachment attachment = new Attachment();
        attachment.setFileLoc("/testfile");
        attachment.setMimeType("test");
        attachment.setFileName("test.txt");
        attachment.setNote(note);
        return KRADServiceLocator.getDataObjectService().save(attachment, PersistenceOption.FLUSH);
    }

    private Branch setupRouteBranchState(Branch branch){
        BranchState bs = new BranchState();
        bs.setKey("KEY");
        bs.setValue("VAL");
        bs.setBranch(branch);

        branch.getBranchState().add(bs);
        return KradDataServiceLocator.getDataObjectService().save(branch);
    }

    private Branch setupRouteBranch() {
        Branch branch = new Branch();
        branch.setName("PRIMARY");

        return KRADServiceLocator.getDataObjectService().save(branch, PersistenceOption.FLUSH);
    }

    private void setupInitialRouteNodeInstancesJoinTable(String routeNodeInstanceId) {
        new JdbcTemplate(TestHarnessServiceLocator.getDataSource()).execute("insert into KREW_INIT_RTE_NODE_INSTN_T "
                + "values('"+TEST_DOC_ID+"','"+routeNodeInstanceId+"')");
    }

    private DocumentRouteHeaderValueContent setupDocumentValueContent() {
        DocumentRouteHeaderValueContent dc = new DocumentRouteHeaderValueContent();
        dc.setDocumentId(TEST_DOC_ID);
        dc.setDocumentContent(DOC_CNT);

        return KRADServiceLocator.getDataObjectService().save(dc);
    }


    private SearchableAttributeStringValue setupSearchAttributeStringValue() {
        SearchableAttributeStringValue searchableAttributeValue = new SearchableAttributeStringValue();
        searchableAttributeValue.setDocumentId(TEST_DOC_ID);
        searchableAttributeValue.setSearchableAttributeValueId(SEARCHABLE_ATTR_ID);
        searchableAttributeValue.setSearchableAttributeKey(SEARCHABLE_ATTR_KEY);
        searchableAttributeValue.setSearchableAttributeValue(SEARCHABLE_ATTR_VAL);

        return KRADServiceLocator.getDataObjectService().save(searchableAttributeValue);
    }

    private SearchableAttributeDateTimeValue setupSearchAttributeDateTimeValue() {
        SearchableAttributeDateTimeValue searchableAttributeDateTimeValue = new SearchableAttributeDateTimeValue();
        searchableAttributeDateTimeValue.setDocumentId(TEST_DOC_ID);
        searchableAttributeDateTimeValue.setSearchableAttributeValueId(SEARCHABLE_ATTR_ID);
        searchableAttributeDateTimeValue.setSearchableAttributeKey(SEARCHABLE_ATTR_KEY);
        searchableAttributeDateTimeValue.setSearchableAttributeValue(SEARCHABLE_ATTR_VAL_DT);

        return KRADServiceLocator.getDataObjectService().save(searchableAttributeDateTimeValue);
    }


    private DocumentRouteHeaderValue setupDocumentRouteHeaderValue() {
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setAppDocId("Test");
        routeHeader.setApprovedDate(null);
        routeHeader.setCreateDate(new Timestamp(new Date().getTime()));
        routeHeader.setDocContent("test");
        routeHeader.setDocRouteLevel(1);
        routeHeader.setDocRouteStatus(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);
        routeHeader.setDocTitle("Test");
        routeHeader.setDocumentTypeId("1");
        routeHeader.setDocVersion(KewApiConstants.DocumentContentVersions.CURRENT);
        routeHeader.setRouteStatusDate(new Timestamp(new Date().getTime()));
        routeHeader.setDateModified(new Timestamp(new Date().getTime()));
        routeHeader.setInitiatorWorkflowId("someone");

        return KRADServiceLocator.getDataObjectService().save(routeHeader, PersistenceOption.FLUSH);
    }

    private DocumentRouteHeaderValue setupDocumentRouteHeaderValueWithRouteHeaderAssigned() {
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setDocumentId(TEST_DOC_ID);
        routeHeader.setAppDocId("Test");
        routeHeader.setApprovedDate(null);
        routeHeader.setCreateDate(new Timestamp(new Date().getTime()));
        routeHeader.setDocContent("test");
        routeHeader.setDocRouteLevel(1);
        routeHeader.setDocRouteStatus(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);
        routeHeader.setDocTitle("Test");
        routeHeader.setDocumentTypeId("1");
        routeHeader.setDocVersion(KewApiConstants.DocumentContentVersions.CURRENT);
        routeHeader.setRouteStatusDate(new Timestamp(new Date().getTime()));
        routeHeader.setDateModified(new Timestamp(new Date().getTime()));
        routeHeader.setInitiatorWorkflowId("someone");

        return KRADServiceLocator.getDataObjectService().save(routeHeader);
    }

    private DocumentStatusTransition setupDocumentStatusTransition(){
        DocumentStatusTransition documentStatusTransition = new DocumentStatusTransition();
        documentStatusTransition.setDocumentId(TEST_DOC_ID);
        documentStatusTransition.setNewAppDocStatus("F");
        documentStatusTransition.setOldAppDocStatus("A");
        documentStatusTransition.setStatusTransitionDate(new Timestamp(System.currentTimeMillis()));
        return KRADServiceLocator.getDataObjectService().save(documentStatusTransition);

    }

    private RouteNodeInstance setupRouteNodeInstance(RouteNode routeNode){
        RouteNodeInstance routeNodeInstance = new RouteNodeInstance();
        routeNodeInstance.setActive(true);
        routeNodeInstance.setComplete(true);
        routeNodeInstance.setDocumentId(TEST_DOC_ID);
        routeNodeInstance.setInitial(true);
        routeNodeInstance.setRouteNode(routeNode);
        return KRADServiceLocator.getDataObjectService().save(routeNodeInstance, PersistenceOption.FLUSH);
    }

    private RouteNode setupRouteNode(DocumentType documentType){
        RouteNode routeNode = new RouteNode();
        routeNode.setDocumentType(documentType);
        routeNode.setRouteNodeName("PreRoute");
        routeNode.setRouteMethodName("org.kuali.rice.kew.engine.node.InitialNode");
        routeNode.setRouteMethodCode("C");
        routeNode.setActivationType("P");

        return KRADServiceLocator.getDataObjectService().save(routeNode, PersistenceOption.FLUSH);
    }


    private DocumentType setupDocumentType(){
        DocumentType documentType = new DocumentType();
        documentType.setActionsUrl("/test");
        documentType.setActive(true);
        documentType.setActualApplicationId("tst");
        documentType.setActualNotificationFromAddress("blah@iu.edu");
        documentType.setApplyRetroactively(true);
        documentType.setAuthorizer("TestAuthorizer");
        documentType.setBlanketApprovePolicy("GoodPolicy");
        documentType.setBlanketApproveWorkgroupId("TestGroup");
        documentType.setCurrentInd(true);
        documentType.setDescription("testing descr");
        documentType.setCustomEmailStylesheet("blah@iu.edu");
        documentType.setDocumentId("1234");
        documentType.setLabel("doc type stuff");
        documentType.setName("gooddoctype");
        documentType.setReturnUrl("returnUrl");
        documentType.setPostProcessorName("PostProcessMe");
        documentType.setDocTypeParentId(null);

        return KRADServiceLocator.getDataObjectService().save(documentType);
    }
}