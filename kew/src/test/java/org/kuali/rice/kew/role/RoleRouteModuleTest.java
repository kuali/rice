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
package org.kuali.rice.kew.role;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityTemplateImpl;
import org.kuali.rice.kim.bo.role.impl.ResponsibilityAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * Tests Role-based routing integration between KEW and KIM.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */

public class RoleRouteModuleTest extends KEWTestCase {

	private String namespace = KEWConstants.KEW_NAMESPACE;
	
	private static boolean suiteDataInitialized = false;
	private static boolean suiteCreateDelegateInitialized = false;

	protected void loadTestData() throws Exception {
        loadXmlFile("RoleRouteModuleTestConfig.xml");

        // only create this data once per suite!
        
        if (suiteDataInitialized) {
        	return;
        }
        
        /**
         * First we need to set up:
         *
         * 1) KimAttributes for both chart and org
         * 2) The KimType for "chart/org"
         * 3) The KimTypeAttributes for chart and org to define relationship between KimType and it's KimAttributes
         */

        // create "chart" KimAttribute
        Long chartAttributeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ATTR_DEFN_ID_S");
        KimAttributeImpl chartAttribute = new KimAttributeImpl();
        chartAttribute.setKimAttributeId("" + chartAttributeId);
        chartAttribute.setAttributeName("chart");
        chartAttribute.setNamespaceCode(namespace);
        chartAttribute.setAttributeLabel("chart");
        chartAttribute.setActive(true);
        KNSServiceLocator.getBusinessObjectService().save(chartAttribute);
        
        // create "org" KimAttribute
        Long orgAttributeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ATTR_DEFN_ID_S");
        KimAttributeImpl orgAttribute = new KimAttributeImpl();
        orgAttribute.setKimAttributeId("" + orgAttributeId);
        orgAttribute.setAttributeName("org");
        orgAttribute.setNamespaceCode(namespace);
        orgAttribute.setAttributeLabel("org");
        orgAttribute.setActive(true);
        KNSServiceLocator.getBusinessObjectService().save(orgAttribute);

        // create KimType
        Long kimTypeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_TYP_ID_S");
        KimTypeImpl kimType = new KimTypeImpl();
        kimType.setKimTypeId("" + kimTypeId);
        kimType.setName("ChartOrg");
        kimType.setNamespaceCode(namespace);
        kimType.setKimTypeServiceName("testBaseRoleTypeService"); // do we need to set the kim type service yet? we shall see...
        kimType.setActive(true);
        KNSServiceLocator.getBusinessObjectService().save(kimType);

        // create chart KimTypeAttribute
        Long chartTypeAttributeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_TYP_ATTR_ID_S");
        KimTypeAttributeImpl chartTypeAttribute = new KimTypeAttributeImpl();
        chartTypeAttribute.setKimTypeAttributeId("" + chartTypeAttributeId);
        chartTypeAttribute.setActive(true);
        chartTypeAttribute.setKimAttributeId(chartAttribute.getKimAttributeId());
        chartTypeAttribute.setKimTypeId(kimType.getKimTypeId());
        KNSServiceLocator.getBusinessObjectService().save(chartTypeAttribute);

        // create org KimTypeAttribute
        Long orgTypeAttributeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_TYP_ATTR_ID_S");
        KimTypeAttributeImpl orgTypeAttribute = new KimTypeAttributeImpl();
        orgTypeAttribute.setKimTypeAttributeId("" + orgTypeAttributeId);
        orgTypeAttribute.setActive(true);
        orgTypeAttribute.setKimAttributeId(orgAttribute.getKimAttributeId());
        orgTypeAttribute.setKimTypeId(kimType.getKimTypeId());
        KNSServiceLocator.getBusinessObjectService().save(orgTypeAttribute);

        /**
         * New let's create the Role
         */

        String roleId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_ID_S");
        RoleImpl role = new RoleImpl();
        role.setRoleId(roleId);
        role.setNamespaceCode(namespace);
        role.setRoleDescription("");
        role.setRoleName("RoleRouteModuleTestRole");
        role.setActive(true);
        role.setKimRoleType(kimType);
        role.setKimTypeId(kimType.getKimTypeId());

        String roleMemberId1 = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_ID_S");
        RoleMemberImpl adminRolePrincipal = new RoleMemberImpl();
        adminRolePrincipal.setRoleMemberId(roleMemberId1);
        KimPrincipal adminPrincipal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("admin");
        assertNotNull(adminPrincipal);
        adminRolePrincipal.setMemberId(adminPrincipal.getPrincipalId());
        adminRolePrincipal.setMemberTypeCode( Role.PRINCIPAL_MEMBER_TYPE );

        String roleMemberId2 = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_ID_S");
        RoleMemberImpl user2RolePrincipal = new RoleMemberImpl();
        user2RolePrincipal.setRoleMemberId(roleMemberId2);
        KimPrincipal user2Principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("user2");
        assertNotNull(user2Principal);
        user2RolePrincipal.setMemberId(user2Principal.getPrincipalId());
        user2RolePrincipal.setMemberTypeCode( Role.PRINCIPAL_MEMBER_TYPE );

        String roleMemberId3 = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_ID_S");
        RoleMemberImpl user1RolePrincipal = new RoleMemberImpl();
        user1RolePrincipal.setRoleMemberId(roleMemberId3);
        KimPrincipal user1Principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("user1");
        assertNotNull(user1Principal);
        user1RolePrincipal.setMemberId(user1Principal.getPrincipalId());
        user1RolePrincipal.setMemberTypeCode( Role.PRINCIPAL_MEMBER_TYPE );

        List<RoleMemberImpl> memberPrincipals = new ArrayList<RoleMemberImpl>();
        memberPrincipals.add(adminRolePrincipal);
        memberPrincipals.add(user2RolePrincipal);
        memberPrincipals.add(user1RolePrincipal);

        role.setMembers(memberPrincipals);

        /**
         * Let's create qualifiers for chart and org for our role members
         */

        String dataId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_GRP_ATTR_DATA_ID_S");
        RoleMemberAttributeDataImpl chartDataBL = new RoleMemberAttributeDataImpl();
        chartDataBL.setAttributeDataId(dataId);
        chartDataBL.setAttributeValue("BL");
        chartDataBL.setKimAttribute(chartAttribute);
        chartDataBL.setKimAttributeId(chartAttribute.getKimAttributeId());
        chartDataBL.setKimType(kimType);
        chartDataBL.setKimTypeId(kimType.getKimTypeId());
        chartDataBL.setRoleMemberId(adminRolePrincipal.getRoleMemberId());

        dataId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_GRP_ATTR_DATA_ID_S");
        RoleMemberAttributeDataImpl chartDataBL2 = new RoleMemberAttributeDataImpl();
        chartDataBL2.setAttributeDataId(dataId);
        chartDataBL2.setAttributeValue("BL");
        chartDataBL2.setKimAttribute(chartAttribute);
        chartDataBL2.setKimAttributeId(chartAttribute.getKimAttributeId());
        chartDataBL2.setKimType(kimType);
        chartDataBL2.setKimTypeId(kimType.getKimTypeId());
        chartDataBL2.setRoleMemberId(user2RolePrincipal.getRoleMemberId());

        dataId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_GRP_ATTR_DATA_ID_S");
        RoleMemberAttributeDataImpl orgDataBUS = new RoleMemberAttributeDataImpl();
        orgDataBUS.setAttributeDataId(dataId);
        orgDataBUS.setAttributeValue("BUS");
        orgDataBUS.setKimAttribute(orgAttribute);
        orgDataBUS.setKimAttributeId(orgAttribute.getKimAttributeId());
        orgDataBUS.setKimType(kimType);
        orgDataBUS.setKimTypeId(kimType.getKimTypeId());
        orgDataBUS.setRoleMemberId(adminRolePrincipal.getRoleMemberId());

        dataId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_GRP_ATTR_DATA_ID_S");
        RoleMemberAttributeDataImpl orgDataBUS2 = new RoleMemberAttributeDataImpl();
        orgDataBUS2.setAttributeDataId(dataId);
        orgDataBUS2.setAttributeValue("BUS");
        orgDataBUS2.setKimAttribute(orgAttribute);
        orgDataBUS2.setKimAttributeId(orgAttribute.getKimAttributeId());
        orgDataBUS2.setKimType(kimType);
        orgDataBUS2.setKimTypeId(kimType.getKimTypeId());
        orgDataBUS2.setRoleMemberId(user2RolePrincipal.getRoleMemberId());


        dataId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_GRP_ATTR_DATA_ID_S");
        RoleMemberAttributeDataImpl chartDataIN = new RoleMemberAttributeDataImpl();
        chartDataIN.setAttributeDataId(dataId);
        chartDataIN.setAttributeValue("IN");
        chartDataIN.setKimAttribute(chartAttribute);
        chartDataIN.setKimAttributeId(chartAttribute.getKimAttributeId());
        chartDataIN.setKimType(kimType);
        chartDataIN.setKimTypeId(kimType.getKimTypeId());
        chartDataIN.setRoleMemberId(user1RolePrincipal.getRoleMemberId());

        dataId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_GRP_ATTR_DATA_ID_S");
        RoleMemberAttributeDataImpl orgDataMED = new RoleMemberAttributeDataImpl();
        orgDataMED.setAttributeDataId(dataId);
        orgDataMED.setAttributeValue("MED");
        orgDataMED.setKimAttribute(orgAttribute);
        orgDataMED.setKimAttributeId(orgAttribute.getKimAttributeId());
        orgDataMED.setKimType(kimType);
        orgDataMED.setKimTypeId(kimType.getKimTypeId());
        orgDataMED.setRoleMemberId(user1RolePrincipal.getRoleMemberId());

        List<RoleMemberAttributeDataImpl> user1Attributes = new ArrayList<RoleMemberAttributeDataImpl>();
        user1Attributes.add(chartDataIN);
        user1Attributes.add(orgDataMED);
        user1RolePrincipal.setAttributes(user1Attributes);

        List<RoleMemberAttributeDataImpl> user2Attributes = new ArrayList<RoleMemberAttributeDataImpl>();
        user2Attributes.add(chartDataBL2);
        user2Attributes.add(orgDataBUS2);
        user2RolePrincipal.setAttributes(user2Attributes);

        List<RoleMemberAttributeDataImpl> adminAttributes = new ArrayList<RoleMemberAttributeDataImpl>();
        adminAttributes.add(chartDataBL);
        adminAttributes.add(orgDataBUS);
        adminRolePrincipal.setAttributes(adminAttributes);


        /**
         * Now we can save the role!
         */

        KNSServiceLocator.getBusinessObjectService().save(role);


        /**
         * Let's set up attributes for responsibility details
         */

        // create "documentType" KimAttribute
        Long documentTypeAttributeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ATTR_DEFN_ID_S");
        KimAttributeImpl documentTypeAttribute = new KimAttributeImpl();
        documentTypeAttribute.setKimAttributeId("" + documentTypeAttributeId);
        documentTypeAttribute.setAttributeName(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL);
        documentTypeAttribute.setNamespaceCode(namespace);
        documentTypeAttribute.setAttributeLabel("documentType");
        documentTypeAttribute.setActive(true);
        KNSServiceLocator.getBusinessObjectService().save(documentTypeAttribute);

        // create "node name" KimAttribute
        Long nodeNameAttributeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ATTR_DEFN_ID_S");
        KimAttributeImpl nodeNameAttribute = new KimAttributeImpl();
        nodeNameAttribute.setKimAttributeId("" + nodeNameAttributeId);
        nodeNameAttribute.setAttributeName(KEWConstants.ROUTE_NODE_NAME_DETAIL);
        nodeNameAttribute.setNamespaceCode(namespace);
        nodeNameAttribute.setAttributeLabel("nodeName");
        nodeNameAttribute.setActive(true);
        KNSServiceLocator.getBusinessObjectService().save(nodeNameAttribute);

        // create KimType for responsibility details
        Long kimRespTypeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_TYP_ID_S");
        KimTypeImpl kimRespType = new KimTypeImpl();
        kimRespType.setKimTypeId("" + kimRespTypeId);
        kimRespType.setName("RespDetails");
        kimRespType.setNamespaceCode(namespace);
        kimRespType.setKimTypeServiceName("testBaseResponsibilityTypeService");
        kimRespType.setActive(true);
        KNSServiceLocator.getBusinessObjectService().save(kimRespType);

        // create document type KimTypeAttribute
        Long documentTypeTypeAttributeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_TYP_ATTR_ID_S");
        KimTypeAttributeImpl documentTypeTypeAttribute = new KimTypeAttributeImpl();
        documentTypeTypeAttribute.setKimTypeAttributeId("" + documentTypeTypeAttributeId);
        documentTypeTypeAttribute.setActive(true);
        documentTypeTypeAttribute.setKimAttributeId(chartAttribute.getKimAttributeId());
        documentTypeTypeAttribute.setKimTypeId(kimType.getKimTypeId());
        KNSServiceLocator.getBusinessObjectService().save(documentTypeTypeAttribute);

        // create nodeNameType KimTypeAttribute
        Long nodeNameTypeAttributeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_TYP_ATTR_ID_S");
        KimTypeAttributeImpl nodeNameTypeAttribute = new KimTypeAttributeImpl();
        nodeNameTypeAttribute.setKimTypeAttributeId("" + nodeNameTypeAttributeId);
        nodeNameTypeAttribute.setActive(true);
        nodeNameTypeAttribute.setKimAttributeId(orgAttribute.getKimAttributeId());
        nodeNameTypeAttribute.setKimTypeId(kimType.getKimTypeId());
        KNSServiceLocator.getBusinessObjectService().save(nodeNameTypeAttribute);

        createResponsibilityForRoleRouteModuleTest1(role, documentTypeAttribute, nodeNameAttribute, kimRespType, user1RolePrincipal, user2RolePrincipal, adminRolePrincipal);
        createResponsibilityForRoleRouteModuleTest2(role, documentTypeAttribute, nodeNameAttribute, kimRespType, user1RolePrincipal, user2RolePrincipal, adminRolePrincipal);
                
        suiteDataInitialized = true;
    }

	private void createResponsibilityForRoleRouteModuleTest1(RoleImpl role, KimAttributeImpl documentTypeAttribute, KimAttributeImpl nodeNameAttribute, KimTypeImpl kimRespType, RoleMemberImpl user1RolePrincipal, RoleMemberImpl user2RolePrincipal, RoleMemberImpl adminRolePrincipal) {

		/**
         * Create the responsibility template
         */

        String templateId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_RSP_TMPL_ID_S");
    	KimResponsibilityTemplateImpl template = new KimResponsibilityTemplateImpl();
        template.setResponsibilityTemplateId(templateId);
        template.setNamespaceCode(namespace);
        template.setName("Review");
        template.setKimTypeId(kimRespType.getKimTypeId());
        template.setActive(true);
        template.setDescription("description");

        KNSServiceLocator.getBusinessObjectService().save(template);


        /**
         * Create the responsibility details for RoleRouteModuleTest1
         */

        String responsibilityId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_RSP_ID_S");

        String dataId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_GRP_ATTR_DATA_ID_S");
        ResponsibilityAttributeDataImpl documentTypeDetail = new ResponsibilityAttributeDataImpl();
        documentTypeDetail.setAttributeDataId(dataId);
        documentTypeDetail.setAttributeValue("RoleRouteModuleTest1");
        documentTypeDetail.setKimAttribute(documentTypeAttribute);
        documentTypeDetail.setKimAttributeId(documentTypeAttribute.getKimAttributeId());
        documentTypeDetail.setKimType(kimRespType);
        documentTypeDetail.setKimTypeId(kimRespType.getKimTypeId());
        documentTypeDetail.setResponsibilityId(responsibilityId);

        dataId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_GRP_ATTR_DATA_ID_S");
        ResponsibilityAttributeDataImpl nodeNameDetail = new ResponsibilityAttributeDataImpl();
        nodeNameDetail.setAttributeDataId(dataId);
        nodeNameDetail.setAttributeValue("Role1");
        nodeNameDetail.setKimAttribute(nodeNameAttribute);
        nodeNameDetail.setKimAttributeId(nodeNameAttribute.getKimAttributeId());
        nodeNameDetail.setKimType(kimRespType);
        nodeNameDetail.setKimTypeId(kimRespType.getKimTypeId());
        nodeNameDetail.setResponsibilityId(responsibilityId);



        /**
         * Create the responsibility
         */

        List<ResponsibilityAttributeDataImpl> detailObjects = new ArrayList<ResponsibilityAttributeDataImpl>();
        detailObjects.add(documentTypeDetail);
        detailObjects.add(nodeNameDetail);

        KimResponsibilityImpl responsibility = new KimResponsibilityImpl();
        responsibility.setActive(true);
        responsibility.setDescription("resp1");
        responsibility.setDetailObjects(detailObjects);
        responsibility.setName("VoluntaryReview");
        responsibility.setNamespaceCode(namespace);
        responsibility.setResponsibilityId(responsibilityId);
        responsibility.setTemplate(template);
        responsibility.setTemplateId(template.getResponsibilityTemplateId());

        KNSServiceLocator.getBusinessObjectService().save(responsibility);

        /**
         * Create the RoleResponsibility
         */

        String roleResponsibilityId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_RSP_ID_S");
        RoleResponsibilityImpl roleResponsibility = new RoleResponsibilityImpl();
        roleResponsibility.setRoleResponsibilityId(roleResponsibilityId);
        roleResponsibility.setActive(true);
        roleResponsibility.setResponsibilityId(responsibilityId);
        roleResponsibility.setRoleId(role.getRoleId());

        KNSServiceLocator.getBusinessObjectService().save(roleResponsibility);

        /**
         * Create the various responsibility actions
         */
        String roleResponsibilityActionId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_RSP_ACTN_ID_S");
        RoleResponsibilityActionImpl roleResponsibilityAction1 = new RoleResponsibilityActionImpl();
        roleResponsibilityAction1.setRoleResponsibilityActionId(roleResponsibilityActionId);
        roleResponsibilityAction1.setRoleResponsibilityId(roleResponsibilityId);
        roleResponsibilityAction1.setRoleMemberId(user1RolePrincipal.getRoleMemberId());
        roleResponsibilityAction1.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        roleResponsibilityAction1.setActionPolicyCode(KEWConstants.APPROVE_POLICY_FIRST_APPROVE);
        roleResponsibilityAction1.setPriorityNumber(1);
        KNSServiceLocator.getBusinessObjectService().save(roleResponsibilityAction1);

        roleResponsibilityActionId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_RSP_ACTN_ID_S");
        RoleResponsibilityActionImpl roleResponsibilityAction2 = new RoleResponsibilityActionImpl();
        roleResponsibilityAction2.setRoleResponsibilityActionId(roleResponsibilityActionId);
        roleResponsibilityAction2.setRoleResponsibilityId(roleResponsibilityId);
        roleResponsibilityAction2.setRoleMemberId(user2RolePrincipal.getRoleMemberId());
        roleResponsibilityAction2.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        roleResponsibilityAction2.setActionPolicyCode(KEWConstants.APPROVE_POLICY_FIRST_APPROVE);
        roleResponsibilityAction2.setPriorityNumber(1);
        KNSServiceLocator.getBusinessObjectService().save(roleResponsibilityAction2);

        roleResponsibilityActionId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_RSP_ACTN_ID_S");
        RoleResponsibilityActionImpl roleResponsibilityAction3 = new RoleResponsibilityActionImpl();
        roleResponsibilityAction3.setRoleResponsibilityActionId(roleResponsibilityActionId);
        roleResponsibilityAction3.setRoleResponsibilityId(roleResponsibilityId);
        roleResponsibilityAction3.setRoleMemberId(adminRolePrincipal.getRoleMemberId());
        roleResponsibilityAction3.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        roleResponsibilityAction3.setActionPolicyCode(KEWConstants.APPROVE_POLICY_FIRST_APPROVE);
        roleResponsibilityAction3.setPriorityNumber(1);
        KNSServiceLocator.getBusinessObjectService().save(roleResponsibilityAction3);

	}

	private void createResponsibilityForRoleRouteModuleTest2(RoleImpl role, KimAttributeImpl documentTypeAttribute, KimAttributeImpl nodeNameAttribute, KimTypeImpl kimRespType, RoleMemberImpl user1RolePrincipal, RoleMemberImpl user2RolePrincipal, RoleMemberImpl adminRolePrincipal) {

		/**
         * Create the responsibility template
         */

        String templateId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_RSP_TMPL_ID_S");
    	KimResponsibilityTemplateImpl template = new KimResponsibilityTemplateImpl();
        template.setResponsibilityTemplateId(templateId);
        template.setNamespaceCode(namespace);
        template.setName("AllApproveReview");
        template.setKimTypeId(kimRespType.getKimTypeId());
        template.setActive(true);
        template.setDescription("description");

        KNSServiceLocator.getBusinessObjectService().save(template);

        /**
         * Create the responsibility details for RoleRouteModuleTest2
         */

        String responsibilityId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_RSP_ID_S");

        String dataId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_GRP_ATTR_DATA_ID_S");
        ResponsibilityAttributeDataImpl documentTypeDetail = new ResponsibilityAttributeDataImpl();
        documentTypeDetail.setAttributeDataId(dataId);
        documentTypeDetail.setAttributeValue("RoleRouteModuleTest2");
        documentTypeDetail.setKimAttribute(documentTypeAttribute);
        documentTypeDetail.setKimAttributeId(documentTypeAttribute.getKimAttributeId());
        documentTypeDetail.setKimType(kimRespType);
        documentTypeDetail.setKimTypeId(kimRespType.getKimTypeId());
        documentTypeDetail.setResponsibilityId(responsibilityId);

        dataId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_GRP_ATTR_DATA_ID_S");
        ResponsibilityAttributeDataImpl nodeNameDetail = new ResponsibilityAttributeDataImpl();
        nodeNameDetail.setAttributeDataId(dataId);
        nodeNameDetail.setAttributeValue("Role1");
        nodeNameDetail.setKimAttribute(nodeNameAttribute);
        nodeNameDetail.setKimAttributeId(nodeNameAttribute.getKimAttributeId());
        nodeNameDetail.setKimType(kimRespType);
        nodeNameDetail.setKimTypeId(kimRespType.getKimTypeId());
        nodeNameDetail.setResponsibilityId(responsibilityId);



        /**
         * Create the responsibility
         */

        List<ResponsibilityAttributeDataImpl> detailObjects = new ArrayList<ResponsibilityAttributeDataImpl>();
        detailObjects.add(documentTypeDetail);
        detailObjects.add(nodeNameDetail);

        KimResponsibilityImpl responsibility = new KimResponsibilityImpl();
        responsibility.setActive(true);
        responsibility.setDescription("resp2");
        responsibility.setDetailObjects(detailObjects);
        responsibility.setName("VoluntaryReview");
        responsibility.setNamespaceCode(namespace);
        responsibility.setResponsibilityId(responsibilityId);
        responsibility.setTemplate(template);
        responsibility.setTemplateId(template.getResponsibilityTemplateId());

        KNSServiceLocator.getBusinessObjectService().save(responsibility);

        /**
         * Create the RoleResponsibility
         */

        String roleResponsibilityId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_RSP_ID_S");
        RoleResponsibilityImpl roleResponsibility = new RoleResponsibilityImpl();
        roleResponsibility.setRoleResponsibilityId(roleResponsibilityId);
        roleResponsibility.setActive(true);
        roleResponsibility.setResponsibilityId(responsibilityId);
        roleResponsibility.setRoleId(role.getRoleId());

        KNSServiceLocator.getBusinessObjectService().save(roleResponsibility);

        /**
         * Create the various responsibility actions
         */
        String roleResponsibilityActionId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_RSP_ACTN_ID_S");
        RoleResponsibilityActionImpl roleResponsibilityAction1 = new RoleResponsibilityActionImpl();
        roleResponsibilityAction1.setRoleResponsibilityActionId(roleResponsibilityActionId);
        roleResponsibilityAction1.setRoleResponsibilityId(roleResponsibilityId);
        roleResponsibilityAction1.setRoleMemberId(user1RolePrincipal.getRoleMemberId());
        roleResponsibilityAction1.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        roleResponsibilityAction1.setActionPolicyCode(KEWConstants.APPROVE_POLICY_ALL_APPROVE);
        roleResponsibilityAction1.setPriorityNumber(1);
        KNSServiceLocator.getBusinessObjectService().save(roleResponsibilityAction1);

        roleResponsibilityActionId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_RSP_ACTN_ID_S");
        RoleResponsibilityActionImpl roleResponsibilityAction2 = new RoleResponsibilityActionImpl();
        roleResponsibilityAction2.setRoleResponsibilityActionId(roleResponsibilityActionId);
        roleResponsibilityAction2.setRoleResponsibilityId(roleResponsibilityId);
        roleResponsibilityAction2.setRoleMemberId(user2RolePrincipal.getRoleMemberId());
        roleResponsibilityAction2.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        roleResponsibilityAction2.setActionPolicyCode(KEWConstants.APPROVE_POLICY_ALL_APPROVE);
        roleResponsibilityAction2.setPriorityNumber(1);
        KNSServiceLocator.getBusinessObjectService().save(roleResponsibilityAction2);

        roleResponsibilityActionId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_RSP_ACTN_ID_S");
        RoleResponsibilityActionImpl roleResponsibilityAction3 = new RoleResponsibilityActionImpl();
        roleResponsibilityAction3.setRoleResponsibilityActionId(roleResponsibilityActionId);
        roleResponsibilityAction3.setRoleResponsibilityId(roleResponsibilityId);
        roleResponsibilityAction3.setRoleMemberId(adminRolePrincipal.getRoleMemberId());
        roleResponsibilityAction3.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        roleResponsibilityAction3.setActionPolicyCode(KEWConstants.APPROVE_POLICY_ALL_APPROVE);
        roleResponsibilityAction3.setPriorityNumber(1);
        KNSServiceLocator.getBusinessObjectService().save(roleResponsibilityAction3);
	}
	
	private void createDelegate(){
		
		if (suiteCreateDelegateInitialized) {
			return;
		}

		// create delegation KimType
        Long kimDlgnTypeId = KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_TYP_ID_S");
        KimTypeImpl kimDlgnType = new KimTypeImpl();
        kimDlgnType.setKimTypeId("" + kimDlgnTypeId);
        kimDlgnType.setName("TestBaseDelegationType");
        kimDlgnType.setNamespaceCode(namespace);
        kimDlgnType.setKimTypeServiceName("testBaseDelegationTypeService");
        kimDlgnType.setActive(true);
        KNSServiceLocator.getBusinessObjectService().save(kimDlgnType);

		/*
		 * Manually create a delegate
		 */
		String id = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_DLGN_MBR_ID_S");
		KimDelegationImpl delegate = new KimDelegationImpl();

		delegate.setDelegationId(id);
		delegate.setDelegationTypeCode(KEWConstants.DELEGATION_PRIMARY);
		delegate.setActive(true);
		delegate.setKimType(kimDlgnType);
		delegate.setKimTypeId("" + kimDlgnTypeId);
		/*
		 * Assign it a role that was created above.  This should mean that every
		 * principle in the role can have the delegate added below as a delegate
		 */
		KimRoleInfo role = KIMServiceLocator.getRoleService().getRoleByName(namespace, "RoleRouteModuleTestRole");
		assertNotNull("Role should exist.", role);
		delegate.setRoleId(role.getRoleId());
		KNSServiceLocator.getBusinessObjectService().save(delegate);

		// BC of the way the jpa is handled we have to create the delagate, then the members
		String delgMemberId = "" + KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_DLGN_MBR_ID_S");
	    KimDelegationMemberImpl user1RoleDelegate = new KimDelegationMemberImpl();
	    user1RoleDelegate.setDelegationMemberId(delgMemberId);
	    // This is the user the delegation requests should be sent to.
	    KimPrincipal kPrincipal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("ewestfal");
	    assertNotNull(kPrincipal);
	    user1RoleDelegate.setMemberId(kPrincipal.getPrincipalId());
	    /*
	     * this is checked when adding delegates in both the ActionRequestFactory
	     * and RoleServiceImpl
	     */
	    user1RoleDelegate.setMemberTypeCode( Role.PRINCIPAL_MEMBER_TYPE );

	    // attach it to the delegate we created above
	    user1RoleDelegate.setDelegationId(delegate.getDelegationId());

	    KNSServiceLocator.getBusinessObjectService().save(user1RoleDelegate);

	    suiteCreateDelegateInitialized = true;
	    
	}


	@Test
	public void testRoleRouteModule_FirstApprove() throws Exception {
		
		WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "RoleRouteModuleTest1");
		document.routeDocument("");

		// in this case we should have a first approve role that contains admin and user2, we
		// should also have a first approve role that contains just user1

		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user2"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());

		// examine the action requests
		ActionRequestDTO[] actionRequests = new WorkflowInfo().getActionRequests(document.getRouteHeaderId());
		// there should be 2 root action requests returned here, 1 containing the 2 requests for "BL", and one containing the request for "IN"
		assertEquals("Should have 3 action requests.", 3, actionRequests.length);
		int numRoots = 0;
		for (ActionRequestDTO actionRequest : actionRequests) {
			// each of these should be "first approve"
			if (actionRequest.getApprovePolicy() != null) {
				assertEquals(KEWConstants.APPROVE_POLICY_FIRST_APPROVE, actionRequest.getApprovePolicy());
			}
			if (actionRequest.getParentActionRequestId() == null) {
				numRoots++;
			}
		}
		assertEquals("There should have been 3 root requests.", 3, numRoots);

		// let's approve as "user1" and verify the document is still ENROUTE
		document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
		document.approve("");
		assertTrue("Document should be ENROUTE.", document.stateIsEnroute());

		// verify that admin and user2 still have requests
		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user2"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());

		// let's approve as "user2" and verify the document is still ENROUTE
		document.approve("");
		assertTrue("Document should be ENROUTE.", document.stateIsEnroute());

		// let's approve as "admin" and verify the document has gone FINAL
		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		document.approve("");
		assertTrue("Document should be FINAL.", document.stateIsFinal());
	}

	@Test
	public void testRoleRouteModule_AllApprove() throws Exception {

		WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "RoleRouteModuleTest2");
		document.routeDocument("");

		// in this case we should have all approve roles for admin, user1 and user2

		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user2"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());

		// examine the action requests
		ActionRequestDTO[] actionRequests = new WorkflowInfo().getActionRequests(document.getRouteHeaderId());
		assertEquals("Should have 3 action requests.", 3, actionRequests.length);
		int numRoots = 0;
		for (ActionRequestDTO actionRequest : actionRequests) {
			if (actionRequest.getApprovePolicy() != null) {
				assertEquals(KEWConstants.APPROVE_POLICY_ALL_APPROVE, actionRequest.getApprovePolicy());
			}
			if (actionRequest.getParentActionRequestId() == null) {
				numRoots++;
			}
		}
		assertEquals("There should have been 3 root requests.", 3, numRoots);

		// let's approve as "user1" and verify the document does NOT go FINAL
		document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
		document.approve("");
		assertTrue("Document should still be enroute.", document.stateIsEnroute());

		// verify that admin and user2 still have requests
		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user2"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());

		// approve as "user2" and verify document is still ENROUTE
		document.approve("");
		assertTrue("Document should be ENROUTE.", document.stateIsEnroute());

		// now approve as "admin", coument should be FINAL
		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		document.approve("");
		assertTrue("Document should be FINAL.", document.stateIsFinal());
	}

	@Test
    public void testDelegate() throws Exception{
		this.createDelegate();
		
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "RoleRouteModuleTest2");
        document.routeDocument("");

        String ewestfalPrincipalId = getPrincipalIdForName("ewestfal");

        // now our fancy new delegate should have an action request
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertTrue("ewestfal should be able to approve", document.isApprovalRequested());

        // let's look at the action requests
        ActionRequestDTO[] actionRequests = document.getActionRequests();

        boolean ewestfalHasRequest = false;
        boolean ewestfalHasDelegateRequest = false;
        for (ActionRequestDTO actionRequest : actionRequests) {
        	if (ewestfalPrincipalId.equals(actionRequest.getPrincipalId())) {
        		ewestfalHasRequest = true;
        		if (actionRequest.getParentActionRequestId() != null) {
        			ewestfalHasDelegateRequest = true;
        			assertEquals("Delegation type should been PRIMARY", KEWConstants.DELEGATION_PRIMARY, actionRequest.getDelegationType());
        		}
        	}
        }
        assertTrue("ewestfal should have had a request", ewestfalHasRequest);
        assertTrue("ewestfal should have had a delegate request", ewestfalHasDelegateRequest);
    }

	@Test
	public void testDelegateApproval() throws Exception{
		this.createDelegate();

		WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), "RoleRouteModuleTest2");
		document.routeDocument("");

		// See if the delegate can approve the document
		document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
		assertTrue("ewestfal should have an approval request", document.isApprovalRequested());
		document.approve("");

		assertTrue("Document should have been approved by the delegate.", document.stateIsFinal());
	}

}
