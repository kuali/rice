/*
 * Copyright 2006-2013 The Kuali Foundation
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

package org.kuali.rice.kew.doctype;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.node.ProcessDefinitionBo;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * Tests to confirm JPA mapping for the Kew module Document type objects
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KewDocumentTypeJpaTest extends KEWTestCase{

    public static final String TEST_DOC_ID = "1234";

    @Test
    public void testDocumentTypePersistAndFetch() throws Exception{
        DocumentType dt = setupDocumentType();
        assertTrue("DocumentType Persisted correctly", dt != null && StringUtils.isNotBlank(dt.getDocumentTypeId()));
        DocumentTypePolicy dtp = setupDocumentTypePolicy(dt);
        assertTrue("DocumentTypePolicy persisted correctly", dtp != null && StringUtils.isNotBlank(
                dtp.getDocumentType().getDocumentTypeId()));

        ApplicationDocumentStatusCategory appDocStatusCategory = setupApplicationDocumentStatusCategory(
                dt.getDocumentTypeId());
        assertTrue("ApplicationDocumentStatusCategory persisted correctly", appDocStatusCategory != null);

        ApplicationDocumentStatus appDocStatus = setApplicationDocumentStatus(dt.getDocumentTypeId(), appDocStatusCategory);
        assertTrue("Application Document Status persisted correctly", appDocStatus != null &&
                StringUtils.isNotBlank(appDocStatus.getDocumentTypeId()));


        DocumentTypeAttributeBo documentTypeAttributeBo = setupDocumentTypeAttributeBo(dt);
        assertTrue("DocumentTypeAttributeBo persisted correctly", documentTypeAttributeBo != null &&
                        StringUtils.isNotBlank(documentTypeAttributeBo.getId()));

        ProcessDefinitionBo processDefinitionBo = setupProcessDefinitionBo(dt);
        assertTrue("ProcessDefinitionBo persisted correctly", processDefinitionBo != null &&
                        StringUtils.isNotBlank(processDefinitionBo.getProcessId()));

        dt = KRADServiceLocator.getDataObjectService().find(DocumentType.class,dt.getDocumentTypeId());
        assertTrue("Document Type fetched correctly", dt != null && StringUtils.isNotBlank(dt.getDocumentTypeId()));

        assertTrue("App doc status grabbed for doc type", dt.getValidApplicationStatuses() != null
                        && dt.getValidApplicationStatuses().size() == 1);
        assertTrue("Document type policy fetched correctly", dt.getDocumentTypePolicies() != null &&
                        dt.getDocumentTypePolicies().size() == 1);
        assertTrue("ApplicationDocStatusCategory fetched correctly", dt.getApplicationStatusCategories() != null &&
                        dt.getApplicationStatusCategories().size() == 1);
        assertTrue("DocumentTypeAttributeBo fetched correctly", dt.getDocumentTypeAttributes() != null &&
                        dt.getDocumentTypeAttributes().size() == 1);
        assertTrue("ProcessDefinitionBo fetched correctly", dt.getProcesses() != null && dt.getProcesses().size() == 1);
    }


    @Test
    public void testDocumentTypeFindByDocumentId() throws Exception{
        DocumentType documentType = setupDocumentType();
        String documentTypeId = documentType.getDocumentTypeId();
        setupDocumentRouteHeaderValueWithRouteHeaderAssigned(documentTypeId);


        documentType = KEWServiceLocator.getDocumentTypeService().findByDocumentId(TEST_DOC_ID);

        assertTrue("DocumentType fetched by document id",documentType != null && StringUtils.equals(
                documentType.getDocumentTypeId(),documentTypeId));
    }

    @Test
    public void testDocumentTypeVersionAndSave() throws Exception{
        setupDocumentType();
        DocumentType documentType = setupDocumentTypeNoPersist();

        KEWServiceLocator.getDocumentTypeService().versionAndSave(documentType);

        documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentType.getName());

        assertTrue("DocumentType has been versioned and resaved", documentType != null
                && documentType.getVersion() == 1);
    }

    @Test
    public void testDocumentTypeFindAllCurrentRootDocuments() throws Exception{
        setupDocumentType();

        List rootDocumentType = KEWServiceLocator.getDocumentTypeService().findAllCurrentRootDocuments();

        assertTrue("Found all root documents", rootDocumentType != null && rootDocumentType.size() == 1);
    }

    @Test
    public void testDocumentTypeFindAllCurrent() throws Exception{
        setupDocumentType();

        List currentDocTypes = KEWServiceLocator.getDocumentTypeService().findAllCurrent();
        assertTrue("Found all current documents", currentDocTypes != null && currentDocTypes.size() == 1);
    }

    @Test
    public void testDocumentTypeFindPreviousInstances() throws Exception{
        testDocumentTypeVersionAndSave();

        List<DocumentType> previousInstances = KEWServiceLocator.getDocumentTypeService().
                                findPreviousInstances("gooddoctype");
        assertTrue("Previous instances found correctly", previousInstances != null && previousInstances.size() == 1);
    }

    @Test
    public void testDocumentTypeFindByName() throws Exception{
        DocumentType documentType = setupDocumentType();
        String documentTypeName = documentType.getName();

        documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        assertTrue("DocumentType fetched by document id",documentType != null && StringUtils.equals(
                documentType.getName(),documentTypeName));

        String nameCaseInsensitive = "gooDdocType";
        documentType = KEWServiceLocator.getDocumentTypeService().findByNameCaseInsensitive(nameCaseInsensitive);
        assertTrue("DocumentType fetched by document id",documentType != null && StringUtils.equals(
                documentType.getName(),documentTypeName));
    }


    @Test
    public void testDocumentTypeServiceFind() throws Exception{
        DocumentType parentDocType = setupDocumentType();
        DocumentType childDocType = setupDocumentTypeNoPersist();
        childDocType.setDocTypeParentId(parentDocType.getDocumentTypeId());
        childDocType.setName("CoolNewDocType");
        KRADServiceLocator.getDataObjectService().save(childDocType);
        assertTrue("Child doc type now has a parent doc type",childDocType != null &&
                StringUtils.isNotBlank(childDocType.getDocTypeParentId()));

        List<DocumentType> documentTypes = (List<DocumentType>)
                KEWServiceLocator.getDocumentTypeService().find(childDocType,parentDocType.getName(),true);
        assertTrue("Fetched correct number of documentTypes", documentTypes != null && documentTypes.size() == 1);


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

    private DocumentType setupDocumentTypeNoPersist(){
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

        return documentType;
    }

    private DocumentTypePolicy setupDocumentTypePolicy(DocumentType documentType) throws Exception{
        DocumentTypePolicy dtp = new DocumentTypePolicy();
        dtp.setDocumentType(documentType);
        dtp.setInheritedFlag(true);
        dtp.setPolicyName("DISAPPROVE");
        dtp.setPolicyStringValue("somevalue");
        dtp.setPolicyValue(true);

        return KRADServiceLocator.getDataObjectService().save(dtp);
    }

    private ApplicationDocumentStatus setApplicationDocumentStatus(String documentTypeId, ApplicationDocumentStatusCategory category){
        ApplicationDocumentStatus applicationDocumentStatus = new ApplicationDocumentStatus();
        applicationDocumentStatus.setDocumentTypeId(documentTypeId);
        applicationDocumentStatus.setCategory(category);
        applicationDocumentStatus.setSequenceNumber(1);
        applicationDocumentStatus.setStatusName("someStatus");

        return KRADServiceLocator.getDataObjectService().save(applicationDocumentStatus);
    }

    private ApplicationDocumentStatusCategory setupApplicationDocumentStatusCategory(String documentTypeId){
        ApplicationDocumentStatusCategory applicationDocumentStatusCategory = new ApplicationDocumentStatusCategory();
        applicationDocumentStatusCategory.setCategoryName("TestCategory");
        applicationDocumentStatusCategory.setDocumentTypeId(documentTypeId);

        return KRADServiceLocator.getDataObjectService().save(applicationDocumentStatusCategory);
    }

    private DocumentTypeAttributeBo setupDocumentTypeAttributeBo(DocumentType documentType){
        DocumentTypeAttributeBo documentTypeAttributeBo = new DocumentTypeAttributeBo();
        documentTypeAttributeBo.setDocumentType(documentType);
        documentTypeAttributeBo.setOrderIndex(1);
        documentTypeAttributeBo.setLockVerNbr(1);
        documentTypeAttributeBo.setRuleAttributeId("1234");

        return KRADServiceLocator.getDataObjectService().save(documentTypeAttributeBo);
    }

    private ProcessDefinitionBo setupProcessDefinitionBo(DocumentType documentType){
        ProcessDefinitionBo processDefinitionBo = new ProcessDefinitionBo();
        processDefinitionBo.setDocumentType(documentType);
        processDefinitionBo.setInitial(true);
        processDefinitionBo.setName("testing");

        return KRADServiceLocator.getDataObjectService().save(processDefinitionBo);
    }

    private DocumentRouteHeaderValue setupDocumentRouteHeaderValueWithRouteHeaderAssigned(String documentTypeId) {
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setDocumentId(TEST_DOC_ID);
        routeHeader.setAppDocId("Test");
        routeHeader.setApprovedDate(null);
        routeHeader.setCreateDate(new Timestamp(new Date().getTime()));
        routeHeader.setDocContent("test");
        routeHeader.setDocRouteLevel(1);
        routeHeader.setDocRouteStatus(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);
        routeHeader.setDocTitle("Test");
        routeHeader.setDocumentTypeId(documentTypeId);
        routeHeader.setDocVersion(KewApiConstants.DocumentContentVersions.CURRENT);
        routeHeader.setRouteStatusDate(new Timestamp(new Date().getTime()));
        routeHeader.setDateModified(new Timestamp(new Date().getTime()));
        routeHeader.setInitiatorWorkflowId("someone");

        return KRADServiceLocator.getDataObjectService().save(routeHeader);
    }

}
