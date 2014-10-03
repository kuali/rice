/*
 * Copyright 2006-2014 The Kuali Foundation
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
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Test utilities (and common tests) for KEW module Document type object persistence
 *
 * Created by fraferna on 9/3/14.
 */
public abstract class KewDocumentTypeBaseTest extends KEWTestCase {

    protected DocumentType setupDocumentType(boolean persist) {
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
            return getDataObjectService().save(documentType, PersistenceOption.FLUSH);
        }
        return documentType;
    }

    protected DocumentTypePolicy setupDocumentTypePolicy(DocumentType documentType) throws Exception {
        DocumentTypePolicy dtp = new DocumentTypePolicy();
        dtp.setDocumentType(documentType);
        dtp.setInheritedFlag(true);
        dtp.setPolicyName("DISAPPROVE");
        dtp.setPolicyStringValue("somevalue");
        dtp.setPolicyValue(true);
        return getDataObjectService().save(dtp, PersistenceOption.FLUSH);
    }

    protected ApplicationDocumentStatusCategory setupApplicationDocumentStatusCategory(DocumentType documentType) {
        ApplicationDocumentStatusCategory applicationDocumentStatusCategory = new ApplicationDocumentStatusCategory();
        applicationDocumentStatusCategory.setCategoryName("TestCategory");
        applicationDocumentStatusCategory.setDocumentType(documentType);

        return getDataObjectService().save(applicationDocumentStatusCategory, PersistenceOption.FLUSH);
    }

    protected DocumentTypeAttributeBo setupDocumentTypeAttributeBo(DocumentType documentType) {
        DocumentTypeAttributeBo documentTypeAttributeBo = new DocumentTypeAttributeBo();
        documentTypeAttributeBo.setDocumentType(documentType);
        documentTypeAttributeBo.setOrderIndex(1);
        documentTypeAttributeBo.setLockVerNbr(1);
        RuleAttribute ruleAttribute = setupRuleAttribute();
        documentTypeAttributeBo.setRuleAttribute(ruleAttribute);

        return getDataObjectService().save(documentTypeAttributeBo, PersistenceOption.FLUSH);
    }

    protected ProcessDefinitionBo setupProcessDefinitionBo(DocumentType documentType) {
        ProcessDefinitionBo processDefinitionBo = new ProcessDefinitionBo();
        processDefinitionBo.setDocumentType(documentType);
        processDefinitionBo.setInitial(true);
        processDefinitionBo.setName("testing");

        return getDataObjectService().save(processDefinitionBo, PersistenceOption.FLUSH);
    }

    protected DocumentRouteHeaderValue setupDocumentRouteHeaderValueWithRouteHeaderAssigned(String documentTypeId) {
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setDocumentId(KewDocumentTypeJpaTest.TEST_DOC_ID);
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

        return getDataObjectService().save(routeHeader, PersistenceOption.FLUSH);
    }

    private RuleAttribute setupRuleAttribute() {
        RuleAttribute ruleAttribute = new RuleAttribute();
        ruleAttribute.setApplicationId("TST");
        ruleAttribute.setDescription("Testing");
        ruleAttribute.setLabel("New Label");
        ruleAttribute.setResourceDescriptor("ResourceDescriptor");
        ruleAttribute.setType("newType");
        ruleAttribute.setName("Attr");

        return getDataObjectService().save(ruleAttribute, PersistenceOption.FLUSH);
    }

    private ApplicationDocumentStatus setApplicationDocumentStatus(DocumentType documentType,
            ApplicationDocumentStatusCategory category) {
        ApplicationDocumentStatus applicationDocumentStatus = new ApplicationDocumentStatus();
        applicationDocumentStatus.setDocumentType(documentType);
        applicationDocumentStatus.setCategory(category);
        applicationDocumentStatus.setSequenceNumber(1);
        applicationDocumentStatus.setStatusName("someStatus");

        return getDataObjectService().save(applicationDocumentStatus, PersistenceOption.FLUSH);
    }

    @Test
    public void testDocumentTypePersistAndFetch() throws Exception {
        DocumentType dt = setupDocumentType(true);
        assertTrue("DocumentType Persisted correctly", dt != null && StringUtils.isNotBlank(dt.getDocumentTypeId()));
        DocumentTypePolicy dtp = setupDocumentTypePolicy(dt);
        assertTrue("DocumentTypePolicy persisted correctly", dtp != null && StringUtils.isNotBlank(
                dtp.getDocumentType().getDocumentTypeId()));
        dt.getDocumentTypePolicies().add(dtp);

        ApplicationDocumentStatusCategory appDocStatusCategory = setupApplicationDocumentStatusCategory(dt);
        assertTrue("ApplicationDocumentStatusCategory persisted correctly", appDocStatusCategory != null);
        dt.getApplicationStatusCategories().add(appDocStatusCategory);

        ApplicationDocumentStatus appDocStatus = setApplicationDocumentStatus(dt, appDocStatusCategory);
        assertTrue("Application Document Status persisted correctly", appDocStatus != null && StringUtils.isNotBlank(
                appDocStatus.getDocumentTypeId()));
        dt.getValidApplicationStatuses().add(appDocStatus);

        DocumentTypeAttributeBo documentTypeAttributeBo = setupDocumentTypeAttributeBo(dt);
        assertTrue("DocumentTypeAttributeBo persisted correctly",
                documentTypeAttributeBo != null && StringUtils.isNotBlank(documentTypeAttributeBo.getId()));
        dt.getDocumentTypeAttributes().add(documentTypeAttributeBo);

        ProcessDefinitionBo processDefinitionBo = setupProcessDefinitionBo(dt);
        assertTrue("ProcessDefinitionBo persisted correctly", processDefinitionBo != null && StringUtils.isNotBlank(
                processDefinitionBo.getProcessId()));
        dt.addProcess(processDefinitionBo);

        dt = KRADServiceLocator.getDataObjectService().save(dt, PersistenceOption.FLUSH);

        dt = fetchDocumentType(dt);
        assertTrue("Document Type fetched correctly", dt != null && StringUtils.isNotBlank(dt.getDocumentTypeId()));

        assertTrue("App doc status grabbed for doc type",
                dt.getValidApplicationStatuses() != null && dt.getValidApplicationStatuses().size() == 1);
        assertTrue("Document type policy fetched correctly",
                dt.getDocumentTypePolicies() != null && dt.getDocumentTypePolicies().size() == 1);
        assertTrue("ApplicationDocStatusCategory fetched correctly",
                dt.getApplicationStatusCategories() != null && dt.getApplicationStatusCategories().size() == 1);
        assertTrue("DocumentTypeAttributeBo fetched correctly",
                dt.getDocumentTypeAttributes() != null && dt.getDocumentTypeAttributes().size() == 1);
        assertTrue("ProcessDefinitionBo fetched correctly", dt.getProcesses() != null && dt.getProcesses().size() == 1);
    }

    protected abstract DataObjectService getDataObjectService();

    protected abstract DocumentType fetchDocumentType(DocumentType dt);
}
