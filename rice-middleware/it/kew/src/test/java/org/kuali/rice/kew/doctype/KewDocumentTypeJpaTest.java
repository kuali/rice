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

package org.kuali.rice.kew.doctype;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.node.ProcessDefinitionBo;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.test.BaselineTestCase;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests to confirm JPA mapping for the Kew module Document type objects
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KewDocumentTypeJpaTest extends KEWTestCase {

    public static final String TEST_DOC_ID = "1234";

    @Test
    public void testDocumentTypePersistAndFetch() throws Exception{
        DocumentType dt = setupDocumentType(true);
        assertTrue("DocumentType Persisted correctly", dt != null && StringUtils.isNotBlank(dt.getDocumentTypeId()));
        DocumentTypePolicy dtp = setupDocumentTypePolicy(dt);
        assertTrue("DocumentTypePolicy persisted correctly", dtp != null && StringUtils.isNotBlank(
                dtp.getDocumentType().getDocumentTypeId()));
        dt.getDocumentTypePolicies().add(dtp);

        ApplicationDocumentStatusCategory appDocStatusCategory = setupApplicationDocumentStatusCategory(
                dt);
        assertTrue("ApplicationDocumentStatusCategory persisted correctly", appDocStatusCategory != null);
        dt.getApplicationStatusCategories().add(appDocStatusCategory);

        ApplicationDocumentStatus appDocStatus = setApplicationDocumentStatus(dt, appDocStatusCategory);
        assertTrue("Application Document Status persisted correctly", appDocStatus != null &&
                StringUtils.isNotBlank(appDocStatus.getDocumentTypeId()));
        dt.getValidApplicationStatuses().add(appDocStatus);


        DocumentTypeAttributeBo documentTypeAttributeBo = setupDocumentTypeAttributeBo(dt);
        assertTrue("DocumentTypeAttributeBo persisted correctly", documentTypeAttributeBo != null &&
                        StringUtils.isNotBlank(documentTypeAttributeBo.getId()));
        dt.getDocumentTypeAttributes().add(documentTypeAttributeBo);

        ProcessDefinitionBo processDefinitionBo = setupProcessDefinitionBo(dt);
        assertTrue("ProcessDefinitionBo persisted correctly", processDefinitionBo != null &&
                        StringUtils.isNotBlank(processDefinitionBo.getProcessId()));
        dt.addProcess(processDefinitionBo);

        dt = KRADServiceLocator.getDataObjectService().save(dt, PersistenceOption.FLUSH);

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
        DocumentType documentType = setupDocumentType(true);
        String documentTypeId = documentType.getDocumentTypeId();
        setupDocumentRouteHeaderValueWithRouteHeaderAssigned(documentTypeId);


        documentType = KEWServiceLocator.getDocumentTypeService().findByDocumentId(TEST_DOC_ID);

        assertTrue("DocumentType fetched by document id",documentType != null && StringUtils.equals(
                documentType.getDocumentTypeId(),documentTypeId));
    }

    @Test
    public void testDocumentTypeVersionAndSave() throws Exception{
        DocumentType parent = setupDocumentType(false);
        parent.setName("MyParentDocType");
        parent = KEWServiceLocator.getDocumentTypeService().versionAndSave(parent);

        assertNotNull(parent.getDocumentTypeId());
        assertEquals(Integer.valueOf(0), parent.getVersion());

        DocumentType documentType = setupDocumentType(false);
        documentType.setDocTypeParentId(parent.getDocumentTypeId());
        documentType = KEWServiceLocator.getDocumentTypeService().versionAndSave(documentType);

        assertNotNull(documentType);
        assertEquals(Integer.valueOf(0), documentType.getVersion());

        // now modify the doc type and re-save it, we have to be careful here, we can't just set the label on our
        // original doc because it's managed in jpa now and would be saved! Also, we are still in the same transaction
        // so out attempt to find old doc type internally will just return ourselves(!)
        //
        // set this modified version up without a parent, let's make sure that works!
        DocumentType modified = setupDocumentType(false);
        modified.setLabel("a custom label");
        modified = KEWServiceLocator.getDocumentTypeService().versionAndSave(modified);

        assertNotNull(modified.getDocumentTypeId());
        assertEquals(modified.getPreviousVersionId(), documentType.getDocumentTypeId());
        assertEquals(Integer.valueOf(1), modified.getVersion());
        assertTrue(modified.isCurrent());
        assertNull(modified.getParentId());

        // refetch the parent, it should have no children
        DocumentType newParent = KEWServiceLocator.getDocumentTypeService().findByName(parent.getName());
        assertEquals(0, newParent.getChildrenDocTypes().size());
        assertEquals(parent.getDocumentTypeId(), newParent.getDocumentTypeId());

        // get the old doc type, it should no longer be current
        DocumentType oldVersion = KEWServiceLocator.getDocumentTypeService().findById(modified.getPreviousVersionId());
        assertEquals(Integer.valueOf(0), oldVersion.getVersion());
        assertFalse(oldVersion.isCurrent());
    }

    @Test
    public void testDocumentTypeFindAllCurrentRootDocuments() throws Exception{
        setupDocumentType(true);

        List rootDocumentType = KEWServiceLocator.getDocumentTypeService().findAllCurrentRootDocuments();

        assertTrue("Found all root documents", rootDocumentType != null && rootDocumentType.size() == 3);
    }

    @Test
    public void testDocumentTypeFindAllCurrent() throws Exception{
        setupDocumentType(true);

        List currentDocTypes = KEWServiceLocator.getDocumentTypeService().findAllCurrent();
        assertTrue("Found all current documents", currentDocTypes != null && currentDocTypes.size() == 7);
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
        DocumentType documentType = setupDocumentType(true);
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
        DocumentType parentDocType = setupDocumentType(true);
        DocumentType childDocType = setupDocumentType(false);
        childDocType.setDocTypeParentId(parentDocType.getDocumentTypeId());
        childDocType.setName("CoolNewDocType");
        childDocType = KRADServiceLocator.getDataObjectService().save(childDocType, PersistenceOption.FLUSH);
        assertTrue("Child doc type now has a parent doc type",childDocType != null &&
                StringUtils.isNotBlank(childDocType.getDocTypeParentId()));

        List<DocumentType> documentTypes = (List<DocumentType>)
                KEWServiceLocator.getDocumentTypeService().find(childDocType,parentDocType.getName(),true);
        assertTrue("Fetched correct number of documentTypes", documentTypes != null && documentTypes.size() == 1);


    }


    private DocumentType setupDocumentType(boolean persist){
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
        if (persist) {
            return KRADServiceLocator.getDataObjectService().save(documentType, PersistenceOption.FLUSH);
        }
        return documentType;
    }

    private DocumentTypePolicy setupDocumentTypePolicy(DocumentType documentType) throws Exception{
        DocumentTypePolicy dtp = new DocumentTypePolicy();
        dtp.setDocumentType(documentType);
        dtp.setInheritedFlag(true);
        dtp.setPolicyName("DISAPPROVE");
        dtp.setPolicyStringValue("somevalue");
        dtp.setPolicyValue(true);
        return KRADServiceLocator.getDataObjectService().save(dtp, PersistenceOption.FLUSH);
    }

    private ApplicationDocumentStatus setApplicationDocumentStatus(DocumentType documentType, ApplicationDocumentStatusCategory category){
        ApplicationDocumentStatus applicationDocumentStatus = new ApplicationDocumentStatus();
        applicationDocumentStatus.setDocumentType(documentType);
        applicationDocumentStatus.setCategory(category);
        applicationDocumentStatus.setSequenceNumber(1);
        applicationDocumentStatus.setStatusName("someStatus");

        return KRADServiceLocator.getDataObjectService().save(applicationDocumentStatus, PersistenceOption.FLUSH);
    }

    private ApplicationDocumentStatusCategory setupApplicationDocumentStatusCategory(DocumentType documentType){
        ApplicationDocumentStatusCategory applicationDocumentStatusCategory = new ApplicationDocumentStatusCategory();
        applicationDocumentStatusCategory.setCategoryName("TestCategory");
        applicationDocumentStatusCategory.setDocumentType(documentType);

        return KRADServiceLocator.getDataObjectService().save(applicationDocumentStatusCategory, PersistenceOption.FLUSH);
    }

    private DocumentTypeAttributeBo setupDocumentTypeAttributeBo(DocumentType documentType){
        DocumentTypeAttributeBo documentTypeAttributeBo = new DocumentTypeAttributeBo();
        documentTypeAttributeBo.setDocumentType(documentType);
        documentTypeAttributeBo.setOrderIndex(1);
        documentTypeAttributeBo.setLockVerNbr(1);
        RuleAttribute ruleAttribute = setupRuleAttribute();
        documentTypeAttributeBo.setRuleAttribute(ruleAttribute);

        return KRADServiceLocator.getDataObjectService().save(documentTypeAttributeBo, PersistenceOption.FLUSH);
    }

    private ProcessDefinitionBo setupProcessDefinitionBo(DocumentType documentType){
        ProcessDefinitionBo processDefinitionBo = new ProcessDefinitionBo();
        processDefinitionBo.setDocumentType(documentType);
        processDefinitionBo.setInitial(true);
        processDefinitionBo.setName("testing");

        return KRADServiceLocator.getDataObjectService().save(processDefinitionBo, PersistenceOption.FLUSH);
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

        return KRADServiceLocator.getDataObjectService().save(routeHeader, PersistenceOption.FLUSH);
    }

    private RuleAttribute setupRuleAttribute(){
        RuleAttribute ruleAttribute = new RuleAttribute();
        ruleAttribute.setApplicationId("TST");
        ruleAttribute.setDescription("Testing");
        ruleAttribute.setLabel("New Label");
        ruleAttribute.setResourceDescriptor("ResourceDescriptor");
        ruleAttribute.setType("newType");
        ruleAttribute.setName("Attr");

        return KRADServiceLocator.getDataObjectService().save(ruleAttribute, PersistenceOption.FLUSH);
    }

}
