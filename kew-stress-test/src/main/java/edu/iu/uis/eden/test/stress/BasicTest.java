/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.test.stress;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;

public class BasicTest extends AbstractTest {

    static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BasicTest.class);

    private String initiator;

    private String documentTypeName;

    private String applicationContent;

    private List attributeDefinitions = new ArrayList();

    private boolean createIt = true;

    private Long documentId;

    private int errorsThusFar = 0;

    private int maxErrors = 5;

    private List<UserIdVO> recipients = new ArrayList<UserIdVO>();
    
    public BasicTest() {
    }

    public BasicTest(String initiator, String documentTypeName) throws Exception {
        this(initiator, documentTypeName, null, new ArrayList());
    }

    public BasicTest(String initiator, String documentTypeName, String applicationContent, List attributeDefinitions) throws Exception {
        LOG.info("Initializing Basic Test with initiator=" + initiator + ", documentType=" + documentTypeName + ", applicationContent=" + applicationContent);
        this.initiator = initiator;
        this.documentTypeName = documentTypeName;
        if (attributeDefinitions == null)
            attributeDefinitions = new ArrayList();
        this.applicationContent = applicationContent;
        this.attributeDefinitions = attributeDefinitions;
    }

    public boolean doWork() throws Exception {
        long start = System.currentTimeMillis();
        boolean finalized = false;
        try {
            if (createIt) {
                long createStart = System.currentTimeMillis();
                WorkflowDocument document = createDocument();
                long createEnd = System.currentTimeMillis();
                LOG.info("Created test document: " + (createEnd - createStart) + " ms");
                document.routeDocument("Stress Test route.  initiator=" + initiator + ", documentType=" + documentTypeName);
                long routeEnd = System.currentTimeMillis();
                TestInfo.markCallToServer();
                LOG.info("Routed test document: " + document.getRouteHeaderId() + " in " + (routeEnd - createEnd) + " ms.");
                if (document.getRouteHeaderId() == null) {
                    throw new Exception("Document was not assigned a Document id.");
                }
            }
            createIt = false;
            long finalizeStart = System.currentTimeMillis();
            finalized = finalizeDocument();
            long finalizeEnd = System.currentTimeMillis();
            LOG.info("Time to run finalizeDocument(): " + (finalizeEnd - finalizeStart));
        } catch (Exception e) {
            errorsThusFar++;
            if (errorsThusFar >= maxErrors) {
                LOG.fatal("Test exceeded its maxium number of errors!", e);
                throw e;
            }
            LOG.error("An error occurred when attempting to do work on test, errorsThusFar=" + errorsThusFar, e);
        }
        long end = System.currentTimeMillis();
        LOG.info("Total time to doWork(): " + (end - start) + " ms");
        return finalized;
    }

    protected WorkflowDocument createDocument() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO(initiator), documentTypeName);
        documentId = document.getRouteHeaderId();
        TestInfo.markCallToServer();
        TestInfo.addRouteHeaderId(document.getRouteHeaderId());
        for (Iterator iterator = attributeDefinitions.iterator(); iterator.hasNext();) {
            WorkflowAttributeDefinitionVO definition = (WorkflowAttributeDefinitionVO) iterator.next();
            document.addAttributeDefinition(definition);
        }
        if (applicationContent != null && !applicationContent.equals("")) {
            document.setApplicationContent(applicationContent);
        }
        return document;
    }

    protected boolean finalizeDocument() throws Exception {
        LOG.info("Finalizing document " + documentId);
        NetworkIdVO initiatorId = new NetworkIdVO(initiator);
        long t1 = System.currentTimeMillis();
        WorkflowDocument document = new WorkflowDocument(initiatorId, documentId);
        long t2 = System.currentTimeMillis();
        TestInfo.markCallToServer();
        if (document.stateIsFinal()) {
            return true;
        }
        if (document.stateIsException() || document.stateIsCanceled() || document.stateIsDisapproved()) {
            String message = "Document ended up in an illegal state: " + document.getStatusDisplayValue();
            LOG.error(message);
            throw new Exception(message);
        }
        long t3 = System.currentTimeMillis();
        ActionRequestVO[] requests = new WorkflowInfo().getActionRequests(documentId);
        long t4 = System.currentTimeMillis();
        TestInfo.markCallToServer();
        this.recipients.addAll(StressTestUtils.handleRequests(documentId, requests));
        long t5 = System.currentTimeMillis();
        document = new WorkflowDocument(initiatorId, documentId);
        long t6 = System.currentTimeMillis();
        TestInfo.markCallToServer();
        LOG.info("Time to load initial document: " + (t2 - t1));
        LOG.info("Time to do some minor logic: " + (t3 - t2));
        LOG.info("Time to load Action Requests: " + (t4 - t3));
        LOG.info("Total time to Handle all Requests: " + (t5 - t4));
        LOG.info("Time to load document after handling requests: " + (t6 - t5));
        return document.stateIsFinal();
    }

    public String getApplicationContent() {
        return applicationContent;
    }

    public void setApplicationContent(String applicationContent) {
        this.applicationContent = applicationContent;
    }

    public List getAttributeDefinitions() {
        return attributeDefinitions;
    }

    public void setAttributeDefinitions(List attributeDefinitions) {
        this.attributeDefinitions = attributeDefinitions;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public List<UserIdVO> getRecipients() {
        return this.recipients;
    }

    public void setRecipients(List<UserIdVO> recipients) {
        this.recipients = recipients;
    }
}