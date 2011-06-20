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

package org.kuali.rice.kim.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.address.EntityAddressContract;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.email.EntityEmailContract;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.api.identity.phone.EntityPhoneContract;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.services.IdentityService;
import org.kuali.rice.kim.api.identity.type.EntityTypeData;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.api.type.KimTypeService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmploymentInformationInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityInfo;
import org.kuali.rice.kim.bo.entity.impl.KimEntityEmploymentInformationImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.bo.ui.GroupDocumentQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentPrivacy;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.group.GroupAttributeBo;
import org.kuali.rice.kim.impl.group.GroupBo;
import org.kuali.rice.kim.impl.group.GroupMemberBo;
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameBo;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneBo;
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo;
import org.kuali.rice.kim.impl.identity.privacy.EntityPrivacyPreferencesBo;
import org.kuali.rice.kim.impl.identity.type.EntityTypeDataBo;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityInternalService;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.kim.service.IdentityManagementNotificationService;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.KIMServiceLocatorWeb;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.KimAttributeDefinition;
import org.kuali.rice.krad.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.datadictionary.control.TextControlDefinition;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentHelperService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;

import javax.xml.namespace.QName;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a description of what this class does - shyu don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class UiDocumentServiceImpl implements UiDocumentService {
	private static final Logger LOG = Logger.getLogger(UiDocumentServiceImpl.class);
	private static final String SHOW_BLANK_QUALIFIERS = "kim.show.blank.qualifiers";
	
	private RoleService roleService;
	private RoleManagementService roleManagementService;
	private BusinessObjectService businessObjectService;
	private IdentityService identityService;
	private IdentityManagementService identityManagementService;
	private GroupService groupService;
	private ResponsibilityService responsibilityService;
    private ResponsibilityInternalService responsibilityInternalService;
	private KimTypeInfoService kimTypeInfoService;
    private DocumentHelperService documentHelperService;


	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	public void saveEntityPerson(
			IdentityManagementPersonDocument identityManagementPersonDocument) {
		KimEntityImpl kimEntity = new KimEntityImpl();
		KimEntityImpl origEntity = getEntityImpl(identityManagementPersonDocument.getEntityId());
		boolean creatingNew = true;
		if (origEntity == null) {
			origEntity = new KimEntityImpl();
			kimEntity.setActive(true);
		} else {
			// TODO : in order to resolve optimistic locking issue. has to get identity and set the version number if identity records matched
			// Need to look into this.
			//kimEntity = origEntity;
			kimEntity.setActive(origEntity.isActive());
			kimEntity.setVersionNumber(origEntity.getVersionNumber());
			creatingNew = false;
		}

		kimEntity.setEntityId(identityManagementPersonDocument.getEntityId());
		String initiatorPrincipalId = getInitiatorPrincipalId(identityManagementPersonDocument);
		boolean inactivatingPrincipal = false;
		if(canModifyEntity(initiatorPrincipalId, identityManagementPersonDocument.getPrincipalId())){
			inactivatingPrincipal = setupPrincipal(identityManagementPersonDocument, kimEntity, origEntity.getPrincipals());
			setupAffiliation(identityManagementPersonDocument, kimEntity, origEntity.getAffiliations(), origEntity.getEmploymentInformation());
			setupName(identityManagementPersonDocument, kimEntity, origEntity.getNames());
		// entitytype
			List<EntityTypeDataBo> entityTypes = new ArrayList<EntityTypeDataBo>();
			EntityTypeDataBo entityType = new EntityTypeDataBo();
			entityType.setEntityId(identityManagementPersonDocument.getEntityId());
			entityType.setEntityTypeCode(KimConstants.EntityTypes.PERSON);
			entityType.setActive(true);
			entityTypes.add(entityType);
			EntityTypeDataBo origEntityType = new EntityTypeDataBo();
			for (EntityTypeDataBo type : origEntity.getEntityTypes()) {
				// should check identity.entitytypeid, but it's not persist in persondoc yet
				if (type.getEntityTypeCode()!=null && StringUtils.equals(type.getEntityTypeCode(), entityType.getEntityTypeCode())) {
					origEntityType = type;
					entityType.setVersionNumber(type.getVersionNumber());
					entityType.setActive(type.isActive());
				}
			}
			setupPhone(identityManagementPersonDocument, entityType, origEntityType.getPhoneNumbers());
			setupEmail(identityManagementPersonDocument, entityType, origEntityType.getEmailAddresses());
			setupAddress(identityManagementPersonDocument, entityType, origEntityType.getAddresses());
            kimEntity.setEntityTypes(entityTypes);
		} else{
			if(ObjectUtils.isNotNull(origEntity.getExternalIdentifiers()))
				kimEntity.setExternalIdentifiers(origEntity.getExternalIdentifiers());
			if(ObjectUtils.isNotNull(origEntity.getEmploymentInformation()))
				kimEntity.setEmploymentInformation(origEntity.getEmploymentInformation());
			if(ObjectUtils.isNotNull(origEntity.getAffiliations()))
				kimEntity.setAffiliations(origEntity.getAffiliations());
			if(ObjectUtils.isNotNull(origEntity.getNames()))
				kimEntity.setNames(origEntity.getNames());
			if(ObjectUtils.isNotNull(origEntity.getEntityTypes()))
				kimEntity.setEntityTypes(origEntity.getEntityTypes());
		}
		if(creatingNew || canOverrideEntityPrivacyPreferences(getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId())) {
			setupPrivacy(identityManagementPersonDocument, kimEntity, origEntity.getPrivacyPreferences());
		} else {
			if(ObjectUtils.isNotNull(origEntity.getPrivacyPreferences())) {
				kimEntity.setPrivacyPreferences(origEntity.getPrivacyPreferences());
			}
		}
		List <GroupMemberBo>  groupPrincipals = populateGroupMembers(identityManagementPersonDocument);
		List <RoleMemberImpl>  rolePrincipals = populateRoleMembers(identityManagementPersonDocument);
		List <KimDelegationImpl> personDelegations = populateDelegations(identityManagementPersonDocument);
		List <PersistableBusinessObject> bos = new ArrayList<PersistableBusinessObject>();
		List <RoleResponsibilityActionImpl> roleRspActions = populateRoleRspActions(identityManagementPersonDocument);
		List <RoleMemberAttributeDataImpl> blankRoleMemberAttrs = getBlankRoleMemberAttrs(rolePrincipals);
		bos.add(kimEntity);
		//if(ObjectUtils.isNotNull(kimEntity.getPrivacyPreferences()))
		//	bos.add(kimEntity.getPrivacyPreferences());
		bos.addAll(groupPrincipals);
		bos.addAll(rolePrincipals);
		bos.addAll(roleRspActions);
		bos.addAll(personDelegations);
		// boservice.save(bos) does not handle deleteawarelist
		getBusinessObjectService().save(bos);

		//KimApiServiceLocator.getIdentityManagementService().flushEntityPrincipalCaches();
		IdentityManagementNotificationService service = (IdentityManagementNotificationService) KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(new QName("KIM", "kimIdentityManagementNotificationService"));
		service.principalUpdated();

		if (!blankRoleMemberAttrs.isEmpty()) {
			getBusinessObjectService().delete(blankRoleMemberAttrs);
		}
		if ( inactivatingPrincipal ) {
			//when a person is inactivated, inactivate their group, role, and delegation memberships
			KimApiServiceLocator.getRoleManagementService().principalInactivated(identityManagementPersonDocument.getPrincipalId());
		}
	}

	private String getInitiatorPrincipalId(Document document){
		try{
			return document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
		} catch(Exception ex){
			return null;
		}
	}
	/**
	 *
	 * @see org.kuali.rice.kim.service.UiDocumentService#getAttributeEntries(AttributeDefinitionMap)
	 */
	public Map<String,Object> getAttributeEntries( AttributeDefinitionMap definitions ) {
		Map<String,Object> attributeEntries = new HashMap<String,Object>();
		if(definitions!=null){
	        for (String key : definitions.keySet()) {
				AttributeDefinition definition = definitions.get(key);
				Map<String,Object> attribute = new HashMap<String,Object>();
				if (definition instanceof KimDataDictionaryAttributeDefinition) {
	//				AttributeDefinition definition = ((KimDataDictionaryAttributeDefinition) attrDefinition)
	//						.getDataDictionaryAttributeDefinition();
					ControlDefinition control = definition.getControl();
					if (control.isSelect() 
							|| control.isRadio()) {
						Map<String,Object> controlMap = new HashMap<String,Object>();
			            if (control.isSelect()) {
			            	controlMap.put("select", "true");
			            } else {
			            	controlMap.put("radio", "true");
			            }
			            controlMap.put("valuesFinder", control.getValuesFinderClass());
			            if (control.getBusinessObjectClass() != null) {
			                controlMap.put("businessObject", control.getBusinessObjectClass());
			            }
			            if (StringUtils.isNotEmpty(control.getKeyAttribute())) {
			                controlMap.put("keyAttribute", control.getKeyAttribute());
			            }
			            if (StringUtils.isNotEmpty(control.getLabelAttribute())) {
			                controlMap.put("labelAttribute", control.getLabelAttribute());
			            }
			            if (control.getIncludeKeyInLabel() != null) {
			                controlMap.put("includeKeyInLabel", control.getIncludeKeyInLabel().toString());
			            }
						attribute.put("control", controlMap);
					} else {
			        	// FIXME: Huh!?!?, control is a Map in the above code but a ControlDefinition here?!?!?
			        	// Maybe this should use the AttributesMapBuilder code to create this
			        	attribute.put("control", definition.getControl());
			        }
					attribute.put("name", definition.getName());
					attribute.put("label", definition.getLabel());
					attribute.put("shortLabel", definition.getShortLabel());
					attribute.put("maxLength", definition.getMaxLength());
					attribute.put("required", definition.isRequired());
					attributeEntries.put(definition.getName(),attribute);
				} else {
					TextControlDefinition control = new TextControlDefinition();
					control.setSize(10);
					attribute.put("name", definition.getName());
					attribute.put("control", control);
					attribute.put("label", definition.getLabel());
					attribute.put("maxLength", 20);
					attribute.put("required", true);
					attribute.put("shortLabel", definition.getLabel());
					attributeEntries.put(definition.getName(),attribute);
				}
			}
		}
        return attributeEntries;
	}


	/**
	 *
	 * @see org.kuali.rice.kim.service.UiDocumentService#loadEntityToPersonDoc(IdentityManagementPersonDocument, String)
	 */
	public void loadEntityToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId) {
		Principal principal = this.getIdentityService().getPrincipal(principalId);
        if(principal==null)
        	throw new RuntimeException("Principal does not exist for principal id:"+principalId);

        identityManagementPersonDocument.setPrincipalId(principal.getPrincipalId());
        identityManagementPersonDocument.setPrincipalName(principal.getPrincipalName());
        identityManagementPersonDocument.setPassword(principal.getPassword());
        identityManagementPersonDocument.setActive(principal.isActive());
        KimEntityInfo kimEntity = this.getIdentityService().getEntityInfo(principal.getEntityId());
		identityManagementPersonDocument.setEntityId(kimEntity.getEntityId());
		if ( ObjectUtils.isNotNull( kimEntity.getPrivacyPreferences() ) ) {
			identityManagementPersonDocument.setPrivacy(loadPrivacyReferences(kimEntity.getPrivacyPreferences()));
		}
		//identityManagementPersonDocument.setActive(kimEntity.isActive());
		identityManagementPersonDocument.setAffiliations(loadAffiliations(kimEntity.getAffiliations(),kimEntity.getEmploymentInformation()));
		identityManagementPersonDocument.setNames(loadNames( identityManagementPersonDocument, principalId, kimEntity.getNames(), identityManagementPersonDocument.getPrivacy().isSuppressName() ));
		EntityTypeData entityType = null;
		for (EntityTypeData type : kimEntity.getEntityTypes()) {
			if (KimConstants.EntityTypes.PERSON.equals(type.getEntityTypeCode())) {
				entityType = EntityTypeData.Builder.create(type).build();
			}
		}

		if(entityType!=null){
			identityManagementPersonDocument.setEmails(loadEmails(identityManagementPersonDocument, principalId, entityType.getEmailAddresses(), identityManagementPersonDocument.getPrivacy().isSuppressEmail()));
			identityManagementPersonDocument.setPhones(loadPhones(identityManagementPersonDocument, principalId, entityType.getPhoneNumbers(), identityManagementPersonDocument.getPrivacy().isSuppressPhone()));
			identityManagementPersonDocument.setAddrs(loadAddresses(identityManagementPersonDocument, principalId, entityType.getAddresses(), identityManagementPersonDocument.getPrivacy().isSuppressAddress()));
		}

		List<? extends Group> groups = getGroupsByIds(getGroupService().getDirectGroupIdsForPrincipal(identityManagementPersonDocument.getPrincipalId()));
		loadGroupToPersonDoc(identityManagementPersonDocument, groups);
		loadRoleToPersonDoc(identityManagementPersonDocument);
		loadDelegationsToPersonDoc(identityManagementPersonDocument);
	}

    @SuppressWarnings("unchecked")
	public List<KimDelegationImpl> getPersonDelegations(String principalId){
		if(principalId==null)
			return new ArrayList<KimDelegationImpl>();
		Map<String,String> criteria = new HashMap<String,String>(1);
		criteria.put(KimConstants.PrimaryKeyConstants.MEMBER_ID, principalId);
		criteria.put( KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, Role.PRINCIPAL_MEMBER_TYPE );
		List<KimDelegationMemberImpl> delegationMembers = (List<KimDelegationMemberImpl>)getBusinessObjectService().findMatching(KimDelegationMemberImpl.class, criteria);
		List<KimDelegationImpl> delegations = new ArrayList<KimDelegationImpl>();
		List<String> delegationIds = new ArrayList<String>();
		if(ObjectUtils.isNotNull(delegationMembers)){
			for(KimDelegationMemberImpl delegationMember: delegationMembers){
				if(!delegationIds.contains(delegationMember.getDelegationId())){
					delegationIds.add(delegationMember.getDelegationId());
					criteria = new HashMap<String,String>(1);
					criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationMember.getDelegationId());
					delegations.add((KimDelegationImpl)getBusinessObjectService().findByPrimaryKey(KimDelegationImpl.class, criteria));
				}
			}
		}
		return delegations;
	}


    protected void loadDelegationsToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument){
		List<RoleDocumentDelegation> delList = new ArrayList<RoleDocumentDelegation>();
		RoleDocumentDelegation documentDelegation;
		List<KimDelegationImpl> origDelegations = getPersonDelegations(identityManagementPersonDocument.getPrincipalId());
		if(ObjectUtils.isNotNull(origDelegations)){
			for(KimDelegationImpl del: origDelegations){
				if(del.isActive()){
					documentDelegation = new RoleDocumentDelegation();
					documentDelegation.setActive(del.isActive());
					documentDelegation.setDelegationId(del.getDelegationId());
					documentDelegation.setDelegationTypeCode(del.getDelegationTypeCode());
					documentDelegation.setKimTypeId(del.getKimTypeId());
					documentDelegation.setMembers(
							loadDelegationMembers(identityManagementPersonDocument,
									del.getMembers(), (RoleImpl)getMember(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, del.getRoleId())));
					documentDelegation.setRoleId(del.getRoleId());
					documentDelegation.setEdit(true);
					delList.add(documentDelegation);
				}
			}
		}
		identityManagementPersonDocument.setDelegations(delList);
		setDelegationMembersInDocument(identityManagementPersonDocument);
	}

	public void setDelegationMembersInDocument(IdentityManagementPersonDocument identityManagementPersonDocument){
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getDelegations())){
			for(RoleDocumentDelegation delegation: identityManagementPersonDocument.getDelegations()){
				if(CollectionUtils.isNotEmpty(delegation.getMembers())){
					for(RoleDocumentDelegationMember member: delegation.getMembers()){
						if (StringUtils.equals(member.getMemberId(), identityManagementPersonDocument.getPrincipalId()))
						{
							member.setDelegationTypeCode(delegation.getDelegationTypeCode());
							identityManagementPersonDocument.getDelegationMembers().add(member);
						}
					}
				}
			}
		}
	}

    protected List<RoleDocumentDelegationMember> loadDelegationMembers(
    		IdentityManagementPersonDocument identityManagementPersonDocument, List<KimDelegationMemberImpl> members, RoleImpl roleImpl){
		List<RoleDocumentDelegationMember> pndMembers = new ArrayList<RoleDocumentDelegationMember>();
		RoleDocumentDelegationMember pndMember = new RoleDocumentDelegationMember();
		RoleMemberImpl roleMember;
		if(ObjectUtils.isNotNull(members)){
			for(KimDelegationMemberImpl member: members){
				pndMember = new RoleDocumentDelegationMember();
				pndMember.setActiveFromDate(member.getActiveFromDate());
				pndMember.setActiveToDate(member.getActiveToDate());
				pndMember.setActive(member.isActive());
				pndMember.setRoleImpl(roleImpl);
				if(pndMember.isActive()){
					KimCommonUtilsInternal.copyProperties(pndMember, member);
					pndMember.setRoleMemberId(member.getRoleMemberId());
					roleMember = getRoleMemberForRoleMemberId(member.getRoleMemberId());
					if(roleMember!=null){
						pndMember.setRoleMemberName(getMemberName(roleMember.getMemberTypeCode(), roleMember.getMemberId()));
						pndMember.setRoleMemberNamespaceCode(getMemberNamespaceCode(roleMember.getMemberTypeCode(), roleMember.getMemberId()));
					}
					pndMember.setMemberNamespaceCode(getMemberNamespaceCode(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setMemberName(getMemberName(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setEdit(true);
					pndMember.setQualifiers(loadDelegationMemberQualifiers(identityManagementPersonDocument, pndMember.getAttributesHelper().getDefinitions(), member.getAttributes()));
					pndMembers.add(pndMember);
				}
			}
		}
		return pndMembers;
	}

    protected List<RoleDocumentDelegationMemberQualifier> loadDelegationMemberQualifiers(IdentityManagementPersonDocument identityManagementPersonDocument,
    		AttributeDefinitionMap origAttributeDefinitions, List<KimDelegationMemberAttributeDataImpl> attributeDataList){
		List<RoleDocumentDelegationMemberQualifier> pndMemberRoleQualifiers = new ArrayList<RoleDocumentDelegationMemberQualifier>();
		RoleDocumentDelegationMemberQualifier pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
		boolean attributePresent = false;
		String origAttributeId;
		if(origAttributeDefinitions!=null){
			for(String key: origAttributeDefinitions.keySet()) {
				origAttributeId = identityManagementPersonDocument.getKimAttributeDefnId(origAttributeDefinitions.get(key));
				if(ObjectUtils.isNotNull(attributeDataList)){
					for(KimDelegationMemberAttributeDataImpl memberRoleQualifier: attributeDataList){
						if(StringUtils.equals(origAttributeId, memberRoleQualifier.getKimAttribute().getId())){
							pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
							pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getId());
							pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
							pndMemberRoleQualifier.setDelegationMemberId(memberRoleQualifier.getAssignedToId());
							pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
							pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
							pndMemberRoleQualifier.setKimAttribute(memberRoleQualifier.getKimAttribute());
							pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
							attributePresent = true;
						}
					}
				}
				if(!attributePresent){
					pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
					pndMemberRoleQualifier.setKimAttrDefnId(origAttributeId);
					pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
				}
				attributePresent = false;
			}
		}
		return pndMemberRoleQualifiers;
	}

	/**
	 *
	 * This method load related group data to pending document when usert initiate the 'edit'.
	 *
	 * @param identityManagementPersonDocument
	 * @param groups
	 */
    protected void loadGroupToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, List<? extends Group> groups) {
        List <PersonDocumentGroup> docGroups = new ArrayList <PersonDocumentGroup>();
        if(ObjectUtils.isNotNull(groups)){
            List<String> directMemberPrincipalIds;
            Collection<GroupMember> groupMemberships;
            for (Group group: groups) {
                directMemberPrincipalIds = getGroupService().getDirectMemberPrincipalIds(group.getId());
                if(ObjectUtils.isNotNull(directMemberPrincipalIds)){
                    directMemberPrincipalIds = new ArrayList<String>(new HashSet<String>(directMemberPrincipalIds));
                    for (String memberId: directMemberPrincipalIds) {
                        // other more direct methods for this ?
                        // can't cast group to 'GroupImpl' because list is GroupInfo type
                        if (StringUtils.equals(memberId, identityManagementPersonDocument.getPrincipalId())) {
                            List<String> groupIds = new ArrayList<String>();
                            groupIds.add(group.getId());
                            groupMemberships = getGroupService().getMembers(groupIds);
                            if(ObjectUtils.isNotNull(groupMemberships)){
                                for (GroupMember groupMember: groupMemberships) {
                                    if (groupMember.isActive(new Timestamp(System.currentTimeMillis())) && StringUtils.equals(groupMember.getMemberId(), identityManagementPersonDocument.getPrincipalId()) &&
                                        StringUtils.equals(groupMember.getTypeCode(), KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE)) {
                                        // create one PersonDocumentGroup per GroupMembershipInfo **
                                        PersonDocumentGroup docGroup = new PersonDocumentGroup();
                                        docGroup.setGroupId(group.getId());
                                        docGroup.setGroupName(group.getName());
                                        docGroup.setNamespaceCode(group.getNamespaceCode());
                                        docGroup.setPrincipalId(memberId);
                                        docGroup.setGroupMemberId(groupMember.getId());
                                        if (groupMember.getActiveFromDate() != null) {
                                        	docGroup.setActiveFromDate(new Timestamp(groupMember.getActiveFromDate().getTime()));
                                        }
                                        if (groupMember.getActiveToDate() != null) {
                                        	docGroup.setActiveToDate(new Timestamp(groupMember.getActiveToDate().getTime()));
                                        }
                                        docGroup.setEdit(true);
                                        docGroups.add(docGroup);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        identityManagementPersonDocument.setGroups(docGroups);
    }

	protected void loadRoleToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List <PersonDocumentRole> docRoles = new ArrayList <PersonDocumentRole>();
		List<RoleImpl> roles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());
		List<String> roleIds = new ArrayList<String>();
		if(ObjectUtils.isNotNull(roles)){
	        for (RoleImpl role : roles) {
	        	if (!roleIds.contains(role.getRoleId())) {
		        	PersonDocumentRole docRole = new PersonDocumentRole();
		        	docRole.setKimTypeId(role.getKimTypeId());
		        	docRole.setActive(role.isActive());
		        	docRole.setNamespaceCode(role.getNamespaceCode());
		        	docRole.setEdit(true);
		        	docRole.setRoleId(role.getRoleId());
		        	docRole.setRoleName(role.getRoleName());
		        	docRole.setRolePrncpls(populateDocRolePrncpl(role.getMembers(), identityManagementPersonDocument.getPrincipalId(), getAttributeDefinitionsForRole(docRole)));
		        	docRole.refreshReferenceObject("assignedResponsibilities");
		        	if(docRole.getRolePrncpls()!=null && !docRole.getRolePrncpls().isEmpty()){
		        		docRoles.add(docRole);
		        		roleIds.add(role.getRoleId());
		        	}
	        	}
	        }
		}
		for (PersonDocumentRole role : docRoles) {
			role.setDefinitions(getAttributeDefinitionsForRole(role));
        	// when post again, it will need this during populate
            role.setNewRolePrncpl(new KimDocumentRoleMember());
            if(role.getDefinitions()!=null){
	            for (String key : role.getDefinitions().keySet()) {
	            	KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
	            	//qualifier.setQualifierKey(key);
	            	setAttrDefnIdForQualifier(qualifier,role.getDefinitions().get(key));
	            	role.getNewRolePrncpl().getQualifiers().add(qualifier);
	            }
            }
            loadRoleRstAction(role);
            role.setAttributeEntry( getAttributeEntries( role.getDefinitions() ) );
		}
        //

        identityManagementPersonDocument.setRoles(docRoles);
	}

	protected AttributeDefinitionMap getAttributeDefinitionsForRole(PersonDocumentRole role) {
    	KimTypeService kimTypeService = KIMServiceLocatorWeb.getKimTypeService(KimTypeBo.to(role.getKimRoleType()));
    	//it is possible that the the kimTypeService is coming from a remote application
        // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
        try {
        	if ( kimTypeService != null ) {
        		return kimTypeService.getAttributeDefinitions(role.getKimTypeId());
        	}
        } catch (Exception ex) {
            LOG.warn("Not able to retrieve KimTypeService from remote system for KIM Role Type: " + role.getKimRoleType(), ex);
        }
    	return new AttributeDefinitionMap();
	}

	protected void loadRoleRstAction(PersonDocumentRole role) {
		if(role!=null && CollectionUtils.isNotEmpty(role.getRolePrncpls())){
			for (KimDocumentRoleMember roleMbr : role.getRolePrncpls()) {
				List<RoleResponsibilityActionImpl> actions = getRoleRspActions( roleMbr.getRoleMemberId());
				if(ObjectUtils.isNotNull(actions)){
					for (RoleResponsibilityActionImpl entRoleRspAction :actions) {
						KimDocumentRoleResponsibilityAction roleRspAction = new KimDocumentRoleResponsibilityAction();
						roleRspAction.setRoleResponsibilityId(entRoleRspAction.getRoleResponsibilityId());
						roleRspAction.setActionTypeCode(entRoleRspAction.getActionTypeCode());
						roleRspAction.setActionPolicyCode(entRoleRspAction.getActionPolicyCode());
						roleRspAction.setPriorityNumber(entRoleRspAction.getPriorityNumber());
						roleRspAction.setRoleResponsibilityActionId(entRoleRspAction.getRoleResponsibilityActionId());
						roleRspAction.refreshReferenceObject("roleResponsibility");
						roleMbr.getRoleRspActions().add(roleRspAction);
					}
				}
			}
		}
	}

	protected void setAttrDefnIdForQualifier(KimDocumentRoleQualifier qualifier, AttributeDefinition definition) {
    	qualifier.setKimAttrDefnId(getAttributeDefnId(definition));
    	qualifier.refreshReferenceObject("kimAttribute");
    }

	protected String getAttributeDefnId(AttributeDefinition definition) {
    	return ((KimAttributeDefinition)definition).getKimAttrDefnId();
    }

	private PrincipalBo getPrincipalImpl(String principalId) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(KIMPropertyConstants.Principal.PRINCIPAL_ID, principalId);
		return (PrincipalBo)getBusinessObjectService().findByPrimaryKey(PrincipalBo.class, criteria);
	}

	public List<KimEntityEmploymentInformationInfo> getEntityEmploymentInformationInfo(String entityId) {
        KimEntityImpl entityImpl = getEntityImpl(entityId);
        List<KimEntityEmploymentInformationInfo> empInfos = new ArrayList<KimEntityEmploymentInformationInfo>();
        KimEntityEmploymentInformationInfo empInfo;
        if(ObjectUtils.isNotNull(entityImpl) && CollectionUtils.isNotEmpty(entityImpl.getEmploymentInformation())){
        	for(KimEntityEmploymentInformationImpl empImpl: entityImpl.getEmploymentInformation()){
            	empInfo = new KimEntityEmploymentInformationInfo(empImpl);
            	empInfos.add(empInfo);
        	}
        }
        return empInfos;
	}

	private KimEntityImpl getEntityImpl(String entityId) {
		KimEntityImpl entityImpl = (KimEntityImpl)getBusinessObjectService().findBySinglePrimaryKey(KimEntityImpl.class, entityId);
        //TODO - remove this hack... This is here because currently jpa only seems to be going 2 levels deep on the eager fetching.
		if(entityImpl!=null  && entityImpl.getEntityTypes() != null) {
        	for (EntityTypeDataBo et : entityImpl.getEntityTypes()) {
        		et.refresh();
        	}
        }
		return entityImpl;
	}

    @SuppressWarnings("unchecked")
	protected List<RoleImpl> getRolesForPrincipal(String principalId) {
		if ( principalId == null ) {
			return new ArrayList<RoleImpl>();
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put("members.memberId", principalId);
		criteria.put("members.memberTypeCode", RoleImpl.PRINCIPAL_MEMBER_TYPE);
		return (List<RoleImpl>)getBusinessObjectService().findMatching(RoleImpl.class, criteria);
	}

	@SuppressWarnings("unchecked")
	protected List<RoleMemberImpl> getRoleMembersForPrincipal(String principalId) {
		if ( principalId == null ) {
			return new ArrayList<RoleMemberImpl>();
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put("memberId", principalId);
		criteria.put("memberTypeCode", RoleImpl.PRINCIPAL_MEMBER_TYPE);
		return (List<RoleMemberImpl>)getBusinessObjectService().findMatching(RoleMemberImpl.class, criteria);
	}

	public RoleMemberImpl getRoleMember(String roleMemberId) {
		if ( roleMemberId == null ) {
			return null;
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put("roleMemberId", roleMemberId);
		return (RoleMemberImpl)getBusinessObjectService().findByPrimaryKey(RoleMemberImpl.class, criteria);
	}

    @SuppressWarnings("unchecked")
	protected List<RoleResponsibilityActionImpl> getRoleRspActions(String roleMemberId) {
		Map<String,String> criteria = new HashMap<String,String>( 1 );
		criteria.put(KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId);
		return (List<RoleResponsibilityActionImpl>)getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
	}

    protected List<KimDocumentRoleMember> populateDocRolePrncpl(List <RoleMemberImpl> roleMembers, String principalId, AttributeDefinitionMap definitions) {
		List <KimDocumentRoleMember> docRoleMembers = new ArrayList <KimDocumentRoleMember>();
		if(ObjectUtils.isNotNull(roleMembers)){
	    	for (RoleMemberImpl rolePrincipal : roleMembers) {
	    		if (rolePrincipal.isActive() && RoleImpl.PRINCIPAL_MEMBER_TYPE.equals(rolePrincipal.getMemberTypeCode()) &&
	    				StringUtils.equals(rolePrincipal.getMemberId(), principalId)) {
	        		KimDocumentRoleMember docRolePrncpl = new KimDocumentRoleMember();
	        		docRolePrncpl.setMemberId(rolePrincipal.getMemberId());
	        		docRolePrncpl.setRoleMemberId(rolePrincipal.getRoleMemberId());
	        		docRolePrncpl.setActive(rolePrincipal.isActive());
	        		docRolePrncpl.setRoleId(rolePrincipal.getRoleId());
	        		docRolePrncpl.setActiveFromDate(rolePrincipal.getActiveFromDate());
	        		docRolePrncpl.setActiveToDate(rolePrincipal.getActiveToDate());
	         		docRolePrncpl.setQualifiers(populateDocRoleQualifier(rolePrincipal.getAttributes(), definitions));
	         		docRolePrncpl.setEdit(true);
	        		docRoleMembers.add(docRolePrncpl);
	    		 }
	    	}
		}
    	return docRoleMembers;
    }

    // UI layout for rolequalifier is a little different from kimroleattribute set up.
    // each principal may have member with same role multiple times with different qualifier, but the role
    // only displayed once, and the qualifier displayed multiple times.
    protected List<KimDocumentRoleQualifier> populateDocRoleQualifier(List <RoleMemberAttributeDataImpl> qualifiers, AttributeDefinitionMap definitions) {
		List <KimDocumentRoleQualifier> docRoleQualifiers = new ArrayList <KimDocumentRoleQualifier>();
		if(definitions!=null){
			for (String key : definitions.keySet()) {
				AttributeDefinition definition = definitions.get(key);
				String attrDefId=((KimAttributeDefinition)definition).getKimAttrDefnId();
				boolean qualifierFound = false;
				if(ObjectUtils.isNotNull(qualifiers)){
					for (RoleMemberAttributeDataImpl qualifier : qualifiers) {
						if (attrDefId!=null && StringUtils.equals(attrDefId, qualifier.getKimAttributeId())) {
				    		KimDocumentRoleQualifier docRoleQualifier = new KimDocumentRoleQualifier();
				    		docRoleQualifier.setAttrDataId(qualifier.getId());
				    		docRoleQualifier.setAttrVal(qualifier.getAttributeValue());
				    		docRoleQualifier.setKimAttrDefnId(qualifier.getKimAttributeId());
				    		docRoleQualifier.setKimAttribute(qualifier.getKimAttribute());
				    		docRoleQualifier.setKimTypId(qualifier.getKimTypeId());
				    		docRoleQualifier.setRoleMemberId(qualifier.getAssignedToId());
				    		docRoleQualifier.setEdit(true);
				    		formatAttrValIfNecessary(docRoleQualifier);
				    		docRoleQualifiers.add(docRoleQualifier);
				    		qualifierFound = true;
				    		break;
						}
					}
				}
				if (!qualifierFound) {
		    		KimDocumentRoleQualifier docRoleQualifier = new KimDocumentRoleQualifier();
		    		docRoleQualifier.setAttrVal("");
		    		docRoleQualifier.setKimAttrDefnId(attrDefId);
		    		docRoleQualifier.refreshReferenceObject("kimAttribute");
		    		docRoleQualifiers.add(docRoleQualifier);
				}
			}
			// If all of the qualifiers are empty, return an empty list
			// This is to prevent dynamic qualifiers from appearing in the
			// person maintenance roles tab.  see KULRICE-3989 for more detail
			if (!Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty(SHOW_BLANK_QUALIFIERS))) {
				int qualCount = 0;
				for (KimDocumentRoleQualifier qual : docRoleQualifiers){
					if (StringUtils.isEmpty(qual.getAttrVal())){
						qualCount++;
					}
				}
				if (qualCount == docRoleQualifiers.size()){
					return new ArrayList <KimDocumentRoleQualifier>();
				}
			}
		}
    	return docRoleQualifiers;
    }

    protected List<PersonDocumentName> loadNames( IdentityManagementPersonDocument personDoc, String principalId, List <EntityName> names, boolean suppressDisplay ) {
		List<PersonDocumentName> docNames = new ArrayList<PersonDocumentName>();
		if(ObjectUtils.isNotNull(names)){
			for (EntityName name: names) {
				if(name.isActive()){
					PersonDocumentName docName = new PersonDocumentName();
                    if (name.getNameType() != null) {
					    docName.setNameTypeCode(name.getNameType().getCode());
                    }

					//We do not need to check the privacy setting here - The UI should care of it
					docName.setFirstName(name.getFirstNameUnmasked());
					docName.setLastName(name.getLastNameUnmasked());
					docName.setMiddleName(name.getMiddleNameUnmasked());
					docName.setTitle(name.getTitleUnmasked());
					docName.setSuffix(name.getSuffixUnmasked());

					docName.setActive(name.isActive());
					docName.setDflt(name.isDefaultValue());
					docName.setEdit(true);
					docName.setEntityNameId(name.getId());
					docNames.add(docName);
				}
			}
		}
		return docNames;
	}

	public boolean canModifyEntity( String currentUserPrincipalId, String toModifyPrincipalId ){
		return (StringUtils.isNotBlank(currentUserPrincipalId) && StringUtils.isNotBlank(toModifyPrincipalId) &&
				currentUserPrincipalId.equals(toModifyPrincipalId)) ||
				getIdentityManagementService().isAuthorized(
						currentUserPrincipalId,
						KimConstants.NAMESPACE_CODE,
						KimConstants.PermissionNames.MODIFY_ENTITY,
						null,
						new AttributeSet(KimConstants.AttributeConstants.PRINCIPAL_ID, currentUserPrincipalId));
	}

	public boolean canOverrideEntityPrivacyPreferences( String currentUserPrincipalId, String toModifyPrincipalId ){
		return (StringUtils.isNotBlank(currentUserPrincipalId) && StringUtils.isNotBlank(toModifyPrincipalId) &&
				currentUserPrincipalId.equals(toModifyPrincipalId)) ||
				getIdentityManagementService().isAuthorized(
						currentUserPrincipalId,
						KimConstants.NAMESPACE_CODE,
						KimConstants.PermissionNames.OVERRIDE_ENTITY_PRIVACY_PREFERENCES,
						null,
						new AttributeSet(KimConstants.AttributeConstants.PRINCIPAL_ID, currentUserPrincipalId) );
	}

	protected boolean canAssignToRole(IdentityManagementRoleDocument document, String initiatorPrincipalId){
        boolean rulePassed = true;
        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, document.getRoleNamespace());
        additionalPermissionDetails.put(KimConstants.AttributeConstants.ROLE_NAME, document.getRoleName());
		if(!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
				document, KimConstants.NAMESPACE_CODE, KimConstants.PermissionTemplateNames.ASSIGN_ROLE,
				initiatorPrincipalId, additionalPermissionDetails, null)){
            rulePassed = false;
		}
		return rulePassed;
	}

	protected List<PersonDocumentAffiliation> loadAffiliations(List <EntityAffiliation> affiliations, List<KimEntityEmploymentInformationInfo> empInfos) {
		List<PersonDocumentAffiliation> docAffiliations = new ArrayList<PersonDocumentAffiliation>();
		if(ObjectUtils.isNotNull(affiliations)){
			for (EntityAffiliation affiliation: affiliations) {
				if(affiliation.isActive()){
					PersonDocumentAffiliation docAffiliation = new PersonDocumentAffiliation();
					docAffiliation.setAffiliationTypeCode(affiliation.getAffiliationType().getCode());
					docAffiliation.setCampusCode(affiliation.getCampusCode());
					docAffiliation.setActive(affiliation.isActive());
					docAffiliation.setDflt(affiliation.isDefaultValue());
					docAffiliation.setEntityAffiliationId(affiliation.getId());
					docAffiliation.refreshReferenceObject("affiliationType");
					// EntityAffiliationImpl does not define empinfos as collection
					docAffiliations.add(docAffiliation);
					docAffiliation.setEdit(true);
					// employment informations
					List<PersonDocumentEmploymentInfo> docEmploymentInformations = new ArrayList<PersonDocumentEmploymentInfo>();
					if(ObjectUtils.isNotNull(empInfos)){
						for (KimEntityEmploymentInformationInfo empInfo: empInfos) {
							if (empInfo.isActive() && StringUtils.equals(docAffiliation.getEntityAffiliationId(), empInfo.getEntityAffiliationId())) {
								PersonDocumentEmploymentInfo docEmpInfo = new PersonDocumentEmploymentInfo();
								docEmpInfo.setEntityEmploymentId(empInfo.getEntityEmploymentId());
								docEmpInfo.setEmployeeId(empInfo.getEmployeeId());
								docEmpInfo.setEmploymentRecordId(empInfo.getEmploymentRecordId());
								docEmpInfo.setBaseSalaryAmount(empInfo.getBaseSalaryAmount());
								docEmpInfo.setPrimaryDepartmentCode(empInfo.getPrimaryDepartmentCode());
								docEmpInfo.setEmploymentStatusCode(empInfo.getEmployeeStatusCode());
								docEmpInfo.setEmploymentTypeCode(empInfo.getEmployeeTypeCode());
								docEmpInfo.setActive(empInfo.isActive());
								docEmpInfo.setPrimary(empInfo.isPrimary());
								docEmpInfo.setEntityAffiliationId(empInfo.getEntityAffiliationId());
								// there is no version number on KimEntityEmploymentInformationInfo
								//docEmpInfo.setVersionNumber(empInfo.getVersionNumber());
								docEmpInfo.setEdit(true);
								docEmpInfo.refreshReferenceObject("employmentType");
								docEmploymentInformations.add(docEmpInfo);
							}
						}
					}
					docAffiliation.setEmpInfos(docEmploymentInformations);
				}
			}
		}
		return docAffiliations;

	}

    protected boolean setupPrincipal(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<PrincipalBo> origPrincipals) {
    	boolean inactivatingPrincipal = false;
		List<PrincipalBo> principals = new ArrayList<PrincipalBo>();
		Principal.Builder principal = Principal.Builder.create(identityManagementPersonDocument.getPrincipalName());
		principal.setPrincipalId(identityManagementPersonDocument.getPrincipalId());
		principal.setPassword(identityManagementPersonDocument.getPassword());
		principal.setActive(identityManagementPersonDocument.isActive());
		principal.setEntityId(identityManagementPersonDocument.getEntityId());
		if(ObjectUtils.isNotNull(origPrincipals)){
			for (PrincipalBo prncpl : origPrincipals) {
				if (prncpl.getPrincipalId()!=null && StringUtils.equals(prncpl.getPrincipalId(), principal.getPrincipalId())) {
					principal.setVersionNumber(prncpl.getVersionNumber());
                    principal.setObjectId(prncpl.getObjectId());
					// check if inactivating the principal
					if ( prncpl.isActive() && !principal.isActive() ) {
						inactivatingPrincipal = true;
					}
				}
			}
		}
		principals.add(PrincipalBo.from(principal.build()));

		kimEntity.setPrincipals(principals);
		return inactivatingPrincipal;
	}

    protected void setupPrivacy(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, EntityPrivacyPreferencesBo origPrivacy) {
		EntityPrivacyPreferencesBo privacyPreferences = new EntityPrivacyPreferencesBo();
		privacyPreferences.setEntityId(identityManagementPersonDocument.getEntityId());
		privacyPreferences.setSuppressAddress(identityManagementPersonDocument.getPrivacy().isSuppressAddress());
		privacyPreferences.setSuppressEmail(identityManagementPersonDocument.getPrivacy().isSuppressEmail());
		privacyPreferences.setSuppressName(identityManagementPersonDocument.getPrivacy().isSuppressName());
		privacyPreferences.setSuppressPhone(identityManagementPersonDocument.getPrivacy().isSuppressPhone());
		privacyPreferences.setSuppressPersonal(identityManagementPersonDocument.getPrivacy().isSuppressPersonal());
		if (ObjectUtils.isNotNull(origPrivacy)) {
			privacyPreferences.setVersionNumber(origPrivacy.getVersionNumber());
            privacyPreferences.setObjectId(origPrivacy.getObjectId());
		}
		kimEntity.setPrivacyPreferences(privacyPreferences);
	}
    protected PersonDocumentPrivacy loadPrivacyReferences(EntityPrivacyPreferences privacyPreferences) {
		PersonDocumentPrivacy docPrivacy = new PersonDocumentPrivacy();
		docPrivacy.setSuppressAddress(privacyPreferences.isSuppressAddress());
		docPrivacy.setSuppressEmail(privacyPreferences.isSuppressEmail());
		docPrivacy.setSuppressName(privacyPreferences.isSuppressName());
		docPrivacy.setSuppressPhone(privacyPreferences.isSuppressPhone());
		docPrivacy.setSuppressPersonal(privacyPreferences.isSuppressPersonal());
		docPrivacy.setEdit(true);
		return docPrivacy;
	}

    protected void setupName(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<EntityNameBo> origNames) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressName() ||
    			canOverrideEntityPrivacyPreferences( getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId() ) ) {
	    	List<EntityNameBo> entityNames = new ArrayList<EntityNameBo>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getNames())){
				for (PersonDocumentName name : identityManagementPersonDocument.getNames()) {
				    EntityNameBo entityName = new EntityNameBo();
					entityName.setNameTypeCode(name.getNameTypeCode());
					entityName.setFirstName(name.getFirstName());
					entityName.setLastName(name.getLastName());
					entityName.setMiddleName(name.getMiddleName());
					entityName.setTitle(name.getTitle());
					entityName.setSuffix(name.getSuffix());
					entityName.setActive(name.isActive());
					entityName.setDefaultValue(name.isDflt());
					entityName.setId(name.getEntityNameId());
					entityName.setEntityId(identityManagementPersonDocument.getEntityId());
					if(ObjectUtils.isNotNull(origNames)){
						for (EntityNameBo origName : origNames) {
							if (origName.getId()!=null && StringUtils.equals(origName.getId(), entityName.getId())) {
								entityName.setVersionNumber(origName.getVersionNumber());
							}

						}
					}
					entityNames.add(entityName);
				}
			}
			kimEntity.setNames(entityNames);
    	}
	}

    protected void setupAffiliation(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity,List<EntityAffiliationBo> origAffiliations, List<KimEntityEmploymentInformationImpl> origEmpInfos) {
		List<EntityAffiliationBo> entityAffiliations = new ArrayList<EntityAffiliationBo>();
		// employment informations
		List<KimEntityEmploymentInformationImpl> entityEmploymentInformations = new ArrayList<KimEntityEmploymentInformationImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getAffiliations())){
			for (PersonDocumentAffiliation affiliation : identityManagementPersonDocument.getAffiliations()) {
				EntityAffiliationBo entityAffiliation = new EntityAffiliationBo();
				entityAffiliation.setAffiliationTypeCode(affiliation.getAffiliationTypeCode());
				entityAffiliation.setCampusCode(affiliation.getCampusCode());
				entityAffiliation.setActive(affiliation.isActive());
				entityAffiliation.setDefaultValue(affiliation.isDflt());
				entityAffiliation.setEntityId(identityManagementPersonDocument.getEntityId());
				entityAffiliation.setId(affiliation.getEntityAffiliationId());
				if(ObjectUtils.isNotNull(origAffiliations)){
				// EntityAffiliationImpl does not define empinfos as collection
					for (EntityAffiliationBo origAffiliation : origAffiliations) {
						if(isSameAffiliation(origAffiliation, entityAffiliation)){
							entityAffiliation.setId(origAffiliation.getId());
						}
						if (origAffiliation.getId()!=null && StringUtils.equals(origAffiliation.getId(), entityAffiliation.getId())) {
							entityAffiliation.setVersionNumber(origAffiliation.getVersionNumber());
						}
					}
				}
				entityAffiliations.add(entityAffiliation);
				int employeeRecordCounter = origEmpInfos==null?0:origEmpInfos.size();
				if(CollectionUtils.isNotEmpty(affiliation.getEmpInfos())){
					for (PersonDocumentEmploymentInfo empInfo : affiliation.getEmpInfos()) {
						KimEntityEmploymentInformationImpl entityEmpInfo = new KimEntityEmploymentInformationImpl();
						entityEmpInfo.setEntityEmploymentId(empInfo.getEntityEmploymentId());
						entityEmpInfo.setEmployeeId(empInfo.getEmployeeId());
						entityEmpInfo.setEmploymentRecordId(empInfo.getEmploymentRecordId());
						entityEmpInfo.setBaseSalaryAmount(empInfo.getBaseSalaryAmount());
						entityEmpInfo.setPrimaryDepartmentCode(empInfo.getPrimaryDepartmentCode());
						entityEmpInfo.setEmployeeStatusCode(empInfo.getEmploymentStatusCode());
						entityEmpInfo.setEmployeeTypeCode(empInfo.getEmploymentTypeCode());
						entityEmpInfo.setActive(empInfo.isActive());
						entityEmpInfo.setPrimary(empInfo.isPrimary());
						entityEmpInfo.setEntityId(identityManagementPersonDocument.getEntityId());
						entityEmpInfo.setEntityAffiliationId(empInfo.getEntityAffiliationId());
						if(ObjectUtils.isNotNull(origEmpInfos)){
							for (KimEntityEmploymentInformationImpl origEmpInfo : origEmpInfos) {
								if(isSameEmpInfo(origEmpInfo, entityEmpInfo)){
									entityEmpInfo.setEntityEmploymentId(entityEmpInfo.getEntityEmploymentId());
								}

								if (origEmpInfo.getEntityEmploymentId()!=null && StringUtils.equals(origEmpInfo.getEntityEmploymentId(), entityEmpInfo.getEntityEmploymentId())) {
									entityEmpInfo.setVersionNumber(origEmpInfo.getVersionNumber());
									entityEmpInfo.setEmploymentRecordId(empInfo.getEmploymentRecordId());
								}
							}
						}
						if(StringUtils.isEmpty(entityEmpInfo.getEmploymentRecordId())){
							employeeRecordCounter++;
							entityEmpInfo.setEmploymentRecordId(employeeRecordCounter+"");
						}
						entityEmploymentInformations.add(entityEmpInfo);
					}
				}
			}
		}
		kimEntity.setEmploymentInformation(entityEmploymentInformations);
		kimEntity.setAffiliations(entityAffiliations);
	}

   private boolean isSameAffiliation(EntityAffiliationBo origAffiliation, EntityAffiliationBo entityAffiliation){
    	//entityId
    	//affiliationTypeCode
    	//campusCode
    	return (origAffiliation!=null && entityAffiliation!=null) &&
    	(StringUtils.isNotEmpty(origAffiliation.getCampusCode()) && StringUtils.equals(origAffiliation.getCampusCode(), entityAffiliation.getCampusCode()))
    	&&
    	(StringUtils.isNotEmpty(origAffiliation.getAffiliationTypeCode()) && StringUtils.equals(origAffiliation.getAffiliationTypeCode(), entityAffiliation.getAffiliationTypeCode()))
 		&&
 		(StringUtils.isNotEmpty(origAffiliation.getEntityId()) && StringUtils.equals(origAffiliation.getEntityId(), entityAffiliation.getEntityId()));
    }

    private boolean isSameEmpInfo(KimEntityEmploymentInformationImpl origEmpInfo, KimEntityEmploymentInformationImpl entityEmpInfo){
    	//emp_info:
    		//employmentRecordId
    		//entityId
    		//These should be unique - add a business rule
    	return (origEmpInfo!=null && entityEmpInfo!=null)
    			&& (StringUtils.isNotEmpty(origEmpInfo.getEmploymentRecordId())
    					&& StringUtils.equals(origEmpInfo.getEmploymentRecordId(), entityEmpInfo.getEmploymentRecordId() )
    				)
    			&& StringUtils.equals( origEmpInfo.getEntityId(),entityEmpInfo.getEntityId());
    }

    protected void setupPhone(IdentityManagementPersonDocument identityManagementPersonDocument, EntityTypeDataBo entityType, List<EntityPhoneBo> origPhones) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressPhone() || canOverrideEntityPrivacyPreferences(getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId()) ) {
			List<EntityPhoneBo> entityPhones = new ArrayList<EntityPhoneBo>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getPhones())){
				for (PersonDocumentPhone phone : identityManagementPersonDocument.getPhones()) {
					EntityPhoneBo entityPhone = new EntityPhoneBo();
					entityPhone.setPhoneTypeCode(phone.getPhoneTypeCode());
					entityPhone.setEntityId(identityManagementPersonDocument.getEntityId());
					entityPhone.setId(phone.getEntityPhoneId());
					entityPhone.setEntityTypeCode(entityType.getEntityTypeCode());
					entityPhone.setPhoneNumber(phone.getPhoneNumber());
					entityPhone.setCountryCode(phone.getCountryCode());
					entityPhone.setExtension(phone.getExtension());
					entityPhone.setExtensionNumber(phone.getExtensionNumber());
					entityPhone.setActive(phone.isActive());
					entityPhone.setDefaultValue(phone.isDflt());
					if(ObjectUtils.isNotNull(origPhones)){
						for (EntityPhoneContract origPhone : origPhones) {
							if (origPhone.getId()!=null && StringUtils.equals(origPhone.getId(), entityPhone.getId())) {
								entityPhone.setVersionNumber(origPhone.getVersionNumber());
							}
						}
					}
					entityPhone.setId(phone.getEntityPhoneId());
					entityPhones.add(entityPhone);
				}
			}
			entityType.setPhoneNumbers(entityPhones);
    	}
	}

    protected List<PersonDocumentPhone> loadPhones(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId, List<EntityPhone> entityPhones, boolean suppressDisplay ) {
		List<PersonDocumentPhone> docPhones = new ArrayList<PersonDocumentPhone>();
		if(ObjectUtils.isNotNull(entityPhones)){
			for (EntityPhone phone: entityPhones) {
				if(phone.isActive()){
					PersonDocumentPhone docPhone = new PersonDocumentPhone();
                    if (phone.getPhoneType() != null) {
					    docPhone.setPhoneTypeCode(phone.getPhoneType().getCode());
                    }
					//docPhone.setPhoneType(((KimEntityPhoneImpl)phone).getPhoneType());
					docPhone.setEntityTypeCode(phone.getEntityTypeCode());
					//We do not need to check the privacy setting here - The UI should care of it
					docPhone.setPhoneNumber(phone.getPhoneNumberUnmasked());
					docPhone.setCountryCode(phone.getCountryCodeUnmasked());
					docPhone.setExtensionNumber(phone.getExtensionNumberUnmasked());

					docPhone.setActive(phone.isActive());
					docPhone.setDflt(phone.isDefaultValue());
					docPhone.setEntityPhoneId(phone.getId());
					docPhone.setEdit(true);
					docPhones.add(docPhone);
				}
			}
		}
		return docPhones;

	}

    protected void setupEmail(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			EntityTypeDataBo entityType, List<EntityEmailBo> origEmails) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressEmail() || canOverrideEntityPrivacyPreferences(getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId()) ) {
			List<EntityEmailBo> entityEmails = new ArrayList<EntityEmailBo>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getEmails())){
				for (PersonDocumentEmail email : identityManagementPersonDocument.getEmails()) {
					EntityEmailBo entityEmail = new EntityEmailBo();
					entityEmail.setEntityId(identityManagementPersonDocument.getEntityId());
					entityEmail.setEntityTypeCode(entityType.getEntityTypeCode());
					entityEmail.setEmailTypeCode(email.getEmailTypeCode());
					entityEmail.setEmailAddress(email.getEmailAddress());
					entityEmail.setActive(email.isActive());
					entityEmail.setDefaultValue(email.isDflt());
					entityEmail.setId(email.getEntityEmailId());
					if(ObjectUtils.isNotNull(origEmails)){
						for (EntityEmailContract origEmail : origEmails) {
							if (origEmail.getId()!=null && StringUtils.equals(origEmail.getId(), entityEmail.getId())) {
								entityEmail.setVersionNumber(origEmail.getVersionNumber());
							}
						}
					}
					entityEmails.add(entityEmail);
				}
			}
			entityType.setEmailAddresses(entityEmails);
    	}
	}
    protected List<PersonDocumentEmail> loadEmails(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId, List<EntityEmail> entityEmails, boolean suppressDisplay ) {
		List<PersonDocumentEmail> emails = new ArrayList<PersonDocumentEmail>();
		if(ObjectUtils.isNotNull(entityEmails)){
			for (EntityEmail email: entityEmails) {
				if(email.isActive()){
					PersonDocumentEmail docEmail = new PersonDocumentEmail();
					//docEmail.setEntityId(email.getEntityId());
					docEmail.setEntityTypeCode(email.getEntityTypeCode());
                    if (email.getEmailType() != null) {
					    docEmail.setEmailTypeCode(email.getEmailType().getCode());
                    }
					// EmailType not on info object.
					//docEmail.setEmailType(((KimEntityEmailImpl)email).getEmailType());
					//We do not need to check the privacy setting here - The UI should care of it
					docEmail.setEmailAddress(email.getEmailAddressUnmasked());

					docEmail.setActive(email.isActive());
					docEmail.setDflt(email.isDefaultValue());
					docEmail.setEntityEmailId(email.getId());
					docEmail.setEdit(true);
					emails.add(docEmail);
				}
			}
		}
		return emails;
	}

    protected void setupAddress(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			EntityTypeDataBo entityType, List<EntityAddressBo> origAddresses) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressAddress() || canOverrideEntityPrivacyPreferences(getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId()) ) {
			List<EntityAddressBo> entityAddresses = new ArrayList<EntityAddressBo>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getAddrs())){
				for (PersonDocumentAddress address : identityManagementPersonDocument.getAddrs()) {
					EntityAddressBo entityAddress = new EntityAddressBo();
					entityAddress.setEntityId(identityManagementPersonDocument.getEntityId());
					entityAddress.setEntityTypeCode(entityType.getEntityTypeCode());
					entityAddress.setAddressTypeCode(address.getAddressTypeCode());
					entityAddress.setLine1(address.getLine1());
					entityAddress.setLine2(address.getLine2());
					entityAddress.setLine3(address.getLine3());
					entityAddress.setStateCode(address.getStateCode());
					entityAddress.setPostalCode(address.getPostalCode());
					entityAddress.setCountryCode(address.getCountryCode());
					entityAddress.setCityName(address.getCityName());
					entityAddress.setActive(address.isActive());
					entityAddress.setDefaultValue(address.isDflt());
					entityAddress.setId(address.getEntityAddressId());
					if(ObjectUtils.isNotNull(origAddresses)){
						for (EntityAddressContract origAddress : origAddresses) {
							if (origAddress.getId()!=null && StringUtils.equals(origAddress.getId(), entityAddress.getId())) {
								entityAddress.setVersionNumber(origAddress.getVersionNumber());
							}
						}
					}
					entityAddresses.add(entityAddress);
				}
			}
			entityType.setAddresses(entityAddresses);
    	}
	}

    protected List<PersonDocumentAddress> loadAddresses(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId, List<EntityAddress> entityAddresses, boolean suppressDisplay ) {
		List<PersonDocumentAddress> docAddresses = new ArrayList<PersonDocumentAddress>();
		if(ObjectUtils.isNotNull(entityAddresses)){
			for (EntityAddress address: entityAddresses) {
				if(address.isActive()){
					PersonDocumentAddress docAddress = new PersonDocumentAddress();
					docAddress.setEntityTypeCode(address.getEntityTypeCode());
					docAddress.setAddressTypeCode(address.getAddressType().getCode());

					//We do not need to check the privacy setting here - The UI should care of it
					docAddress.setLine1(address.getLine1Unmasked());
					docAddress.setLine2(address.getLine2Unmasked());
					docAddress.setLine3(address.getLine3Unmasked());
					docAddress.setStateCode(address.getStateCodeUnmasked());
					docAddress.setPostalCode(address.getPostalCodeUnmasked());
					docAddress.setCountryCode(address.getCountryCodeUnmasked());
					docAddress.setCityName(address.getCityNameUnmasked());

					docAddress.setActive(address.isActive());
					docAddress.setDflt(address.isDefaultValue());
					docAddress.setEntityAddressId(address.getId());
					docAddress.setEdit(true);
					docAddresses.add(docAddress);
				}
			}
		}
		return docAddresses;
	}


    protected List <GroupMemberBo> populateGroupMembers(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List <GroupMemberBo>  groupPrincipals = new ArrayList<GroupMemberBo>();
//		List<? extends Group> origGroups = getGroupService().getGroupsForPrincipal(identityManagementPersonDocument.getPrincipalId());
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getGroups())){
			for (PersonDocumentGroup group : identityManagementPersonDocument.getGroups()) {
				GroupMember.Builder groupPrincipalImpl = GroupMember.Builder.create(group.getGroupId(), identityManagementPersonDocument.getPrincipalId(), KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
				if (group.getActiveFromDate() != null) {
					groupPrincipalImpl.setActiveFromDate(new java.sql.Timestamp(group.getActiveFromDate().getTime()));
				}
				if (group.getActiveToDate() != null) {
					groupPrincipalImpl.setActiveToDate(new java.sql.Timestamp(group.getActiveToDate().getTime()));
				}
				groupPrincipalImpl.setId(group.getGroupMemberId());


                //groupPrincipalImpl.setVersionNumber(group.getVersionNumber());
				// get the ORM-layer optimisic locking value
				// TODO: this should be replaced with the retrieval and storage of that value
				// in the document tables and not re-retrieved here
				Collection<GroupMember> currGroupMembers = getGroupService().getMembers(Collections.singletonList(group.getGroupId()));
				if(ObjectUtils.isNotNull(currGroupMembers)){
					for (GroupMember origGroupMember: currGroupMembers) {
                        if (origGroupMember.isActive(new Timestamp(System.currentTimeMillis()))
                            && origGroupMember.getTypeCode().equals(KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE)) {
                            if(origGroupMember.getId()!=null && StringUtils.equals(origGroupMember.getId(), group.getGroupMemberId())){
                                groupPrincipalImpl.setObjectId(origGroupMember.getObjectId());
                                groupPrincipalImpl.setVersionNumber(origGroupMember.getVersionNumber());
                            }
                        }
					}
				}

				groupPrincipals.add(GroupMemberBo.from(groupPrincipalImpl.build()));

			}
		}
		return groupPrincipals;
	}

    protected List<RoleMemberImpl> populateRoleMembers(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List<RoleImpl> origRoles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());

		List <RoleMemberImpl> roleMembers = new ArrayList<RoleMemberImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getRoles())){
			for (PersonDocumentRole role : identityManagementPersonDocument.getRoles()) {
				//if(role.isEditable()){
					List<RoleMemberImpl> origRoleMembers = new ArrayList<RoleMemberImpl>();
					if(ObjectUtils.isNotNull(origRoles)){
						for (RoleImpl origRole : origRoles) {
							if (origRole.getRoleId()!=null && StringUtils.equals(origRole.getRoleId(), role.getRoleId())) {
								origRoleMembers = origRole.getMembers();
								break;
							}
						}
					}
					if (role.getRolePrncpls().isEmpty()) {
						if (!role.getDefinitions().isEmpty()) {
							RoleMemberImpl roleMemberImpl = new RoleMemberImpl();
							roleMemberImpl.setRoleId(role.getRoleId());
							roleMemberImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
							roleMemberImpl.setMemberTypeCode(Role.PRINCIPAL_MEMBER_TYPE);
							roleMembers.add(roleMemberImpl);
						}
					} else {
						for (KimDocumentRoleMember roleMember : role.getRolePrncpls()) {
							RoleMemberImpl roleMemberImpl = new RoleMemberImpl();
							roleMemberImpl.setRoleId(role.getRoleId());
							// TODO : principalId is not ready here yet ?
							roleMemberImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
							roleMemberImpl.setMemberTypeCode(Role.PRINCIPAL_MEMBER_TYPE);
							roleMemberImpl.setRoleMemberId(roleMember.getRoleMemberId());
							if (roleMember.getActiveFromDate() != null) {
								roleMemberImpl.setActiveFromDate(new java.sql.Timestamp(roleMember.getActiveFromDate().getTime()));
							}
							if (roleMember.getActiveToDate() != null) {
								roleMemberImpl.setActiveToDate(new java.sql.Timestamp(roleMember.getActiveToDate().getTime()));
							}
							List<RoleMemberAttributeDataImpl> origAttributes = new ArrayList<RoleMemberAttributeDataImpl>();
							if(ObjectUtils.isNotNull(origRoleMembers)){
								for (RoleMemberImpl origMember : origRoleMembers) {
									if (origMember.getRoleMemberId()!=null && StringUtils.equals(origMember.getRoleMemberId(), roleMember.getRoleMemberId())) {
										origAttributes = origMember.getAttributes();
										roleMemberImpl.setVersionNumber(origMember.getVersionNumber());
									}
								}
							}
							List<RoleMemberAttributeDataImpl> attributes = new ArrayList<RoleMemberAttributeDataImpl>();
							if(CollectionUtils.isNotEmpty(roleMember.getQualifiers())){
								for (KimDocumentRoleQualifier qualifier : roleMember.getQualifiers()) {
									//if (StringUtils.isNotBlank(qualifier.getAttrVal())) {
										RoleMemberAttributeDataImpl attribute = new RoleMemberAttributeDataImpl();
										attribute.setId(qualifier.getAttrDataId());
										attribute.setAttributeValue(qualifier.getAttrVal());
										attribute.setKimAttributeId(qualifier.getKimAttrDefnId());
										attribute.setAssignedToId(qualifier.getRoleMemberId());
										attribute.setKimTypeId(qualifier.getKimTypId());

										updateAttrValIfNecessary(attribute);

										if(ObjectUtils.isNotNull(origAttributes)){
											for (RoleMemberAttributeDataImpl origAttribute : origAttributes) {
												if (origAttribute.getId()!=null && StringUtils.equals(origAttribute.getId(), qualifier.getAttrDataId())) {
													attribute.setVersionNumber(origAttribute.getVersionNumber());
												}
											}
										}
										if (attribute.getVersionNumber() != null || StringUtils.isNotBlank(qualifier.getAttrVal())) {
											attributes.add(attribute);
										}
									//}
								}
							}
							roleMemberImpl.setAttributes(attributes);
							roleMembers.add(roleMemberImpl);
						}
					}
				//}
			}
		}
		return roleMembers;
	}

	protected List<KimDelegationImpl> populateDelegations(IdentityManagementPersonDocument identityManagementPersonDocument){
		List<KimDelegationImpl> origDelegations = getPersonDelegations(identityManagementPersonDocument.getPrincipalId());
		List<KimDelegationImpl> kimDelegations = new ArrayList<KimDelegationImpl>();
		KimDelegationImpl newKimDelegation;
		KimDelegationImpl origDelegationImplTemp = null;
		List<KimDelegationMemberImpl> origMembers = new ArrayList<KimDelegationMemberImpl>();
		boolean activatingInactive = false;
		String newDelegationIdAssigned = "";
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getDelegations())){
			for(RoleDocumentDelegation roleDocumentDelegation: identityManagementPersonDocument.getDelegations()){
				newKimDelegation = new KimDelegationImpl();
				KimCommonUtilsInternal.copyProperties(newKimDelegation, roleDocumentDelegation);
				newKimDelegation.setRoleId(roleDocumentDelegation.getRoleId());
				if(ObjectUtils.isNotNull(origDelegations)){
					for(KimDelegationImpl origDelegationImpl: origDelegations){
						if((origDelegationImpl.getRoleId()!=null && StringUtils.equals(origDelegationImpl.getRoleId(), newKimDelegation.getRoleId())) &&
								(origDelegationImpl.getDelegationId()!=null && StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId()))){
							//TODO: verify if you want to add  && newRoleMember.isActive() condition to if...
							newDelegationIdAssigned = newKimDelegation.getDelegationId();
							newKimDelegation.setDelegationId(origDelegationImpl.getDelegationId());
							activatingInactive = true;
						}
						if(origDelegationImpl.getDelegationId()!=null && StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId())){
							newKimDelegation.setVersionNumber(origDelegationImpl.getVersionNumber());
							origDelegationImplTemp = origDelegationImpl;
						}
					}
				}
				origMembers = (origDelegationImplTemp==null || origDelegationImplTemp.getMembers()==null)?
									new ArrayList<KimDelegationMemberImpl>():origDelegationImplTemp.getMembers();
				newKimDelegation.setMembers(getDelegationMembers(roleDocumentDelegation.getMembers(), origMembers, activatingInactive, newDelegationIdAssigned));
				kimDelegations.add(newKimDelegation);
				activatingInactive = false;
			}
		}
		return kimDelegations;
	}

    protected List <RoleMemberAttributeDataImpl> getBlankRoleMemberAttrs(List <RoleMemberImpl> rolePrncpls) {

		List <RoleMemberAttributeDataImpl>  blankRoleMemberAttrs = new ArrayList<RoleMemberAttributeDataImpl>();
		if(ObjectUtils.isNotNull(rolePrncpls)){
			for (RoleMemberImpl roleMbr : rolePrncpls) {
				List <RoleMemberAttributeDataImpl>  roleMemberAttrs = new ArrayList<RoleMemberAttributeDataImpl>();
				if (CollectionUtils.isNotEmpty(roleMbr.getAttributes())) {
					for (RoleMemberAttributeDataImpl attr : roleMbr.getAttributes()) {
						if (StringUtils.isBlank(attr.getAttributeValue())) {
							roleMemberAttrs.add(attr);
						}
					}
					if (!roleMemberAttrs.isEmpty()) {
						roleMbr.getAttributes().removeAll(roleMemberAttrs);
						blankRoleMemberAttrs.addAll(roleMemberAttrs);
					}

				}
			}
		}

		return blankRoleMemberAttrs;

	}

    protected List <RoleResponsibilityActionImpl> populateRoleRspActions(IdentityManagementPersonDocument identityManagementPersonDocument) {
//		List<RoleImpl> origRoles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());

		List <RoleResponsibilityActionImpl>  roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getRoles())){
			for (PersonDocumentRole role : identityManagementPersonDocument.getRoles()) {
				if(CollectionUtils.isNotEmpty(role.getRolePrncpls())){
					for (KimDocumentRoleMember roleMbr : role.getRolePrncpls()) {
						if(CollectionUtils.isNotEmpty(roleMbr.getRoleRspActions())){
							for (KimDocumentRoleResponsibilityAction roleRspAction : roleMbr.getRoleRspActions()) {
								RoleResponsibilityActionImpl entRoleRspAction = new RoleResponsibilityActionImpl();
								entRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
								entRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
								entRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
								entRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
								entRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
								entRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
								entRoleRspAction.setRoleResponsibilityId(roleRspAction.getRoleResponsibilityId());
								List<RoleResponsibilityActionImpl> actions = getRoleRspActions( roleMbr.getRoleMemberId());
								if(ObjectUtils.isNotNull(actions)){
									for(RoleResponsibilityActionImpl orgRspAction : actions) {
										if (orgRspAction.getRoleResponsibilityActionId()!=null && StringUtils.equals(orgRspAction.getRoleResponsibilityActionId(), roleRspAction.getRoleResponsibilityActionId())) {
											entRoleRspAction.setVersionNumber(orgRspAction.getVersionNumber());
										}
									}
								}
								roleRspActions.add(entRoleRspAction);
							}
						}
					}
				}
			}
		}
		return roleRspActions;

	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KRADServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	protected IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KimApiServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}

	protected IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KimApiServiceLocator.getIdentityService();
		}
		return identityService;
	}

	protected GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KimApiServiceLocator.getGroupService();
		}
		return groupService;
	}

	protected DocumentHelperService getDocumentHelperService() {
	    if ( documentHelperService == null ) {
	        documentHelperService = KRADServiceLocatorWeb.getDocumentHelperService();
		}
	    return this.documentHelperService;
	}

	protected RoleService getRoleService() {
	   	if(roleService == null){
	   		roleService = KimApiServiceLocator.getRoleService();
    	}
		return roleService;
	}

	protected RoleManagementService getRoleManagementService() {
	   	if(roleManagementService == null){
	   		roleManagementService = KimApiServiceLocator.getRoleManagementService();
    	}
		return roleManagementService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	protected ResponsibilityService getResponsibilityService() {
	   	if ( responsibilityService == null ) {
    		responsibilityService = KimApiServiceLocator.getResponsibilityService();
    	}
		return responsibilityService;
	}

	public void setResponsibilityService(ResponsibilityService responsibilityService) {
		this.responsibilityService = responsibilityService;
	}


	/* Role document methods */
	@SuppressWarnings("unchecked")
	public void loadRoleDoc(IdentityManagementRoleDocument identityManagementRoleDocument, KimRoleInfo kimRole){
		KimRoleInfo kimRoleInfo = (KimRoleInfo)kimRole;
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KIMPropertyConstants.Role.ROLE_ID, kimRoleInfo.getRoleId());
		RoleImpl kimRoleImpl = (RoleImpl)
			getBusinessObjectService().findByPrimaryKey(RoleImpl.class, criteria);
		identityManagementRoleDocument.setRoleId(kimRoleImpl.getRoleId());
		identityManagementRoleDocument.setKimType(KimTypeBo.to(kimRoleImpl.getKimRoleType()));
		identityManagementRoleDocument.setRoleTypeName(kimRoleImpl.getKimRoleType().getName());
		identityManagementRoleDocument.setRoleTypeId(kimRoleImpl.getKimTypeId());
		identityManagementRoleDocument.setRoleName(kimRoleImpl.getRoleName());
		identityManagementRoleDocument.setRoleDescription(kimRoleImpl.getRoleDescription());
		identityManagementRoleDocument.setActive(kimRoleImpl.isActive());
		identityManagementRoleDocument.setRoleNamespace(kimRoleImpl.getNamespaceCode());
		identityManagementRoleDocument.setEditing(true);

		identityManagementRoleDocument.setPermissions(loadPermissions((List<RolePermissionImpl>)getBusinessObjectService().findMatching(RolePermissionImpl.class, criteria)));
		identityManagementRoleDocument.setResponsibilities(loadResponsibilities((List<RoleResponsibilityImpl>)getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria)));
		loadResponsibilityRoleRspActions(identityManagementRoleDocument);
		identityManagementRoleDocument.setMembers(loadRoleMembers(identityManagementRoleDocument, kimRoleImpl.getMembers()));
		loadMemberRoleRspActions(identityManagementRoleDocument);
		identityManagementRoleDocument.setDelegations(loadRoleDocumentDelegations(identityManagementRoleDocument, getRoleDelegations(kimRoleImpl.getRoleId())));
		//Since delegation members are flattened out on the UI...
		setDelegationMembersInDocument(identityManagementRoleDocument);
		identityManagementRoleDocument.setKimType(KimTypeBo.to(kimRoleImpl.getKimRoleType()));
	}

	public void setDelegationMembersInDocument(IdentityManagementRoleDocument identityManagementRoleDocument){
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getDelegations())){
			for(RoleDocumentDelegation delegation: identityManagementRoleDocument.getDelegations()){
				if(CollectionUtils.isNotEmpty(delegation.getMembers())){
					for(RoleDocumentDelegationMember member: delegation.getMembers()){
						member.setDelegationTypeCode(delegation.getDelegationTypeCode());
						identityManagementRoleDocument.getDelegationMembers().add(member);
					}
				}
			}
		}
	}

	protected List<KimDocumentRoleResponsibility> loadResponsibilities(List<RoleResponsibilityImpl> roleResponsibilities){
		List<KimDocumentRoleResponsibility> documentRoleResponsibilities = new ArrayList<KimDocumentRoleResponsibility>();
		if(ObjectUtils.isNotNull(roleResponsibilities)){
			for(RoleResponsibilityImpl roleResponsibility: roleResponsibilities){
				if(roleResponsibility.isActive()) {
					KimDocumentRoleResponsibility roleResponsibilityCopy = new KimDocumentRoleResponsibility();
					KimCommonUtilsInternal.copyProperties(roleResponsibilityCopy, roleResponsibility);
					roleResponsibilityCopy.setEdit(true);
					documentRoleResponsibilities.add(roleResponsibilityCopy);
				}
			}
		}
		return documentRoleResponsibilities;
	}

	protected List<KimDocumentRolePermission> loadPermissions(List<RolePermissionImpl> rolePermissions){
		List<KimDocumentRolePermission> documentRolePermissions = new ArrayList<KimDocumentRolePermission>();
		KimDocumentRolePermission rolePermissionCopy;
		if(ObjectUtils.isNotNull(rolePermissions)){
			for(RolePermissionImpl rolePermission: rolePermissions){
				if ( rolePermission.isActive() ) {
					rolePermissionCopy = new KimDocumentRolePermission();
					rolePermissionCopy.setRolePermissionId(rolePermission.getRolePermissionId());
					rolePermissionCopy.setRoleId(rolePermission.getRoleId());
					rolePermissionCopy.setPermissionId(rolePermission.getPermissionId());
					rolePermissionCopy.setKimPermission(rolePermission.getKimPermission().toSimpleInfo());
					rolePermissionCopy.setEdit(true);
					documentRolePermissions.add(rolePermissionCopy);
				}
			}
		}
		return documentRolePermissions;
	}

	protected List<KimDocumentRoleMember> loadRoleMembers(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleMemberImpl> members){
		List<KimDocumentRoleMember> pndMembers = new ArrayList<KimDocumentRoleMember>();
		KimDocumentRoleMember pndMember = new KimDocumentRoleMember();
		if(ObjectUtils.isNotNull(members)){
			for(RoleMemberImpl member: members){
				pndMember = new KimDocumentRoleMember();
				pndMember.setActiveFromDate(member.getActiveFromDate());
				pndMember.setActiveToDate(member.getActiveToDate());
				pndMember.setActive(member.isActive());
				if(pndMember.isActive()){
					pndMember.setRoleMemberId(member.getRoleMemberId());
					pndMember.setRoleId(member.getRoleId());
					pndMember.setMemberId(member.getMemberId());
					pndMember.setMemberNamespaceCode(getMemberNamespaceCode(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setMemberName(getMemberName(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setMemberFullName(getMemberFullName(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setMemberTypeCode(member.getMemberTypeCode());
					pndMember.setQualifiers(loadRoleMemberQualifiers(identityManagementRoleDocument, member.getAttributes()));
					pndMember.setEdit(true);
					pndMembers.add(pndMember);
				}
			}
		}
            Collections.sort(pndMembers, identityManagementRoleDocument.getMemberMetaDataType());
		return pndMembers;
	}

	protected void loadResponsibilityRoleRspActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
			for(KimDocumentRoleResponsibility responsibility: identityManagementRoleDocument.getResponsibilities()){
				responsibility.getRoleRspActions().addAll(loadKimDocumentRoleRespActions(
						getRoleResponsibilityActionImpls(responsibility.getRoleResponsibilityId())));
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected List<RoleResponsibilityActionImpl> getRoleResponsibilityActionImpls(String roleResponsibilityId){
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, "*");
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ID, roleResponsibilityId);
		return (List<RoleResponsibilityActionImpl>)
			getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
	}

	@SuppressWarnings("unchecked")
	public List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActionImpls(String roleMemberId){
		Map<String, String> criteria = new HashMap<String, String>(1);
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
		return (List<RoleResponsibilityActionImpl>)
			getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
	}

	protected void loadMemberRoleRspActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getMembers())){
			for(KimDocumentRoleMember member: identityManagementRoleDocument.getMembers()){
				member.getRoleRspActions().addAll(loadKimDocumentRoleRespActions(
						getRoleMemberResponsibilityActionImpls(member.getRoleMemberId()) ) );
			}
		}
	}

	protected List<KimDocumentRoleResponsibilityAction> loadKimDocumentRoleRespActions(
			List<RoleResponsibilityActionImpl> roleRespActionImpls){
		List<KimDocumentRoleResponsibilityAction> documentRoleRespActions = new ArrayList<KimDocumentRoleResponsibilityAction>();
		KimDocumentRoleResponsibilityAction documentRoleRespAction;
		if(ObjectUtils.isNotNull(roleRespActionImpls)){
			for(RoleResponsibilityActionImpl roleRespActionImpl: roleRespActionImpls){
				documentRoleRespAction = new KimDocumentRoleResponsibilityAction();
				KimCommonUtilsInternal.copyProperties(documentRoleRespAction, roleRespActionImpl);
				// handle the roleResponsibility object being null since not all may be defined when ID value is "*"
				if ( ObjectUtils.isNotNull(roleRespActionImpl.getRoleResponsibility()) ) {
					documentRoleRespAction.setKimResponsibility(roleRespActionImpl.getRoleResponsibility().getKimResponsibility());
				}
				documentRoleRespActions.add(documentRoleRespAction);
			}
		}
		return documentRoleRespActions;
	}

    public BusinessObject getMember(String memberTypeCode, String memberId){
        Class<? extends BusinessObject> roleMemberTypeClass = null;
        String roleMemberIdName = "";
    	if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberTypeClass = PrincipalBo.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.PRINCIPAL_ID;
	 	 	Principal principalInfo = getIdentityManagementService().getPrincipal(memberId);
	 	 	if (principalInfo != null) {
	 	 		
	 	 	}
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	roleMemberTypeClass = GroupBo.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.GROUP_ID;
        	Group groupInfo = null;
	 	 	groupInfo = getGroupService().getGroup(memberId);
	 	 	if (groupInfo != null) {
	 	 		
	 	 	}
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	roleMemberTypeClass = RoleImpl.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.ROLE_ID;
        	KimRoleInfo roleInfo = null;
	 	 	roleInfo = getRoleService().getRole(memberId);
	 	 	if (roleInfo != null) {
	 	 		
	 	 	}
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(roleMemberIdName, memberId);
        return getBusinessObjectService().findByPrimaryKey(roleMemberTypeClass, criteria);
    }

	public String getMemberName(String memberTypeCode, String memberId){
		if(StringUtils.isEmpty(memberTypeCode) || StringUtils.isEmpty(memberId)) return "";
		BusinessObject member = getMember(memberTypeCode, memberId);
		if (member == null) { //not a REAL principal, try to fake the name
			String fakeName = "";
			Principal kp = KimApiServiceLocator.getIdentityManagementService().getPrincipal(memberId);
			if(kp != null && kp.getPrincipalName() != null && !"".equals(kp.getPrincipalName())){
				fakeName = kp.getPrincipalName();
			}

			return fakeName;
		}
		return getMemberName(memberTypeCode, member);
	}

	public String getMemberFullName(String memberTypeCode, String memberId){
		if(StringUtils.isEmpty(memberTypeCode) || StringUtils.isEmpty(memberId)) return "";
	   	String memberFullName = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	Principal principalInfo = null;
        	principalInfo = getIdentityManagementService().getPrincipal(memberId);
        	if (principalInfo != null) {
        		String principalName = principalInfo.getPrincipalName();
        		Person psn = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName);
        		memberFullName = psn.getFirstName() + " " + psn.getLastName();
        	}        	        	
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	Group group = null;
        	group = getIdentityManagementService().getGroup(memberId);
        	if (group != null) {
        		memberFullName = group.getName();
        	}
        	
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	KimRoleInfo roleInfo = null;
        	roleInfo = getRoleService().getRole(memberId);        	
        	memberFullName = roleInfo.getRoleName();
        }
        return memberFullName;
	}

	public String getMemberNamespaceCode(String memberTypeCode, String memberId){
		if(StringUtils.isEmpty(memberTypeCode) || StringUtils.isEmpty(memberId)) return "";
    	String roleMemberNamespaceCode = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberNamespaceCode = "";
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	Group groupInfo = getIdentityManagementService().getGroup(memberId);
        	if (groupInfo!= null) {
        		roleMemberNamespaceCode = groupInfo.getNamespaceCode();
        	}
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	KimRoleInfo roleInfo = getRoleService().getRole(memberId);
        	if (roleInfo != null) {
        		roleMemberNamespaceCode = roleInfo.getNamespaceCode();
        	}        	
        }
        return roleMemberNamespaceCode;
	}

    public String getMemberIdByName(String memberTypeCode, String memberNamespaceCode, String memberName){
    	String memberId = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
            Principal principal = getIdentityManagementService().getPrincipalByPrincipalName(memberName);
            if(principal!=null)
            	memberId = principal.getPrincipalId();
       } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	Group groupInfo = getIdentityManagementService().getGroupByName(memberNamespaceCode, memberName);
        	if(groupInfo!=null)
            memberId = groupInfo.getId();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	memberId = getRoleManagementService().getRoleIdByName(memberNamespaceCode, memberName);
        }
        return memberId;
    }

    public String getMemberName(String memberTypeCode, BusinessObject member){
    	String roleMemberName = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberName = ((PrincipalBo)member).getPrincipalName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	roleMemberName = ((GroupBo)member).getName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	roleMemberName = ((RoleImpl)member).getRoleName();
        }
        return roleMemberName;
    }

    public String getMemberNamespaceCode(String memberTypeCode, BusinessObject member){
    	String roleMemberNamespaceCode = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberNamespaceCode = "";
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	roleMemberNamespaceCode = ((GroupBo)member).getNamespaceCode();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	roleMemberNamespaceCode = ((RoleImpl)member).getNamespaceCode();
        }
        return roleMemberNamespaceCode;
    }

    protected List<KimDocumentRoleQualifier> loadRoleMemberQualifiers(IdentityManagementRoleDocument identityManagementRoleDocument,
			List<RoleMemberAttributeDataImpl> attributeDataList){
		List<KimDocumentRoleQualifier> pndMemberRoleQualifiers = new ArrayList<KimDocumentRoleQualifier>();
		KimDocumentRoleQualifier pndMemberRoleQualifier = new KimDocumentRoleQualifier();

		// add all attributes from attributeDataList
		if(attributeDataList!=null){
			for(RoleMemberAttributeDataImpl memberRoleQualifier: attributeDataList){
				pndMemberRoleQualifier = new KimDocumentRoleQualifier();
				pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getId());
				pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
				pndMemberRoleQualifier.setRoleMemberId(memberRoleQualifier.getAssignedToId());
				pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
				pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
				pndMemberRoleQualifier.setKimAttribute(memberRoleQualifier.getKimAttribute());
				formatAttrValIfNecessary(pndMemberRoleQualifier);
				pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
			}
		}
		// also add any attributes already in the document that are not in the attributeDataList
		int countOfOriginalAttributesNotPresent = 0;
		List<KimDocumentRoleQualifier> fillerRoleQualifiers = new ArrayList<KimDocumentRoleQualifier>();

		AttributeDefinitionMap origAttributes = identityManagementRoleDocument.getDefinitions();
		if ( origAttributes != null ) {
			for(String key: origAttributes.keySet()) {
				boolean attributePresent = false;
				String origAttributeId = identityManagementRoleDocument.getKimAttributeDefnId(origAttributes.get(key));
				if(attributeDataList!=null){
					for(RoleMemberAttributeDataImpl memberRoleQualifier: attributeDataList){
						if(origAttributeId!=null && StringUtils.equals(origAttributeId, memberRoleQualifier.getKimAttribute().getId())){
							attributePresent = true;
							break;
						}
					}
				}
				if(!attributePresent){
					countOfOriginalAttributesNotPresent++;
					pndMemberRoleQualifier = new KimDocumentRoleQualifier();
					pndMemberRoleQualifier.setKimAttrDefnId(origAttributeId);
					pndMemberRoleQualifier.refreshReferenceObject("kimAttribute");
					fillerRoleQualifiers.add(pndMemberRoleQualifier);
				}
			}

			if(countOfOriginalAttributesNotPresent != origAttributes.size()) {
				pndMemberRoleQualifiers.addAll(fillerRoleQualifiers);
			}
		}
		return pndMemberRoleQualifiers;
	}

    @SuppressWarnings("unchecked")
	public List<KimDelegationImpl> getRoleDelegations(String roleId){
		if(roleId==null)
			return new ArrayList<KimDelegationImpl>();
		Map<String,String> criteria = new HashMap<String,String>(1);
		criteria.put("roleId", roleId);
		return (List<KimDelegationImpl>)getBusinessObjectService().findMatching(KimDelegationImpl.class, criteria);
	}

    protected List<RoleDocumentDelegation> loadRoleDocumentDelegations(IdentityManagementRoleDocument identityManagementRoleDocument, List<KimDelegationImpl> delegations){
		List<RoleDocumentDelegation> delList = new ArrayList<RoleDocumentDelegation>();
		RoleDocumentDelegation documentDelegation;
		if(ObjectUtils.isNotNull(delegations)){
			for(KimDelegationImpl del: delegations){
				documentDelegation = new RoleDocumentDelegation();
				documentDelegation.setActive(del.isActive());
				if(documentDelegation.isActive()){
					documentDelegation.setDelegationId(del.getDelegationId());
					documentDelegation.setDelegationTypeCode(del.getDelegationTypeCode());
					documentDelegation.setKimTypeId(del.getKimTypeId());
					documentDelegation.setMembers(loadDelegationMembers(identityManagementRoleDocument, del.getMembers()));
					documentDelegation.setRoleId(del.getRoleId());
					documentDelegation.setEdit(true);
					delList.add(documentDelegation);
				}
			}
		}
		return delList;
	}

    protected List<RoleDocumentDelegationMember> loadDelegationMembers(IdentityManagementRoleDocument identityManagementRoleDocument, List<KimDelegationMemberImpl> members){
		List<RoleDocumentDelegationMember> pndMembers = new ArrayList<RoleDocumentDelegationMember>();
		RoleDocumentDelegationMember pndMember = new RoleDocumentDelegationMember();
		RoleMemberImpl roleMember;
		if(ObjectUtils.isNotNull(members)){
			for(KimDelegationMemberImpl member: members){
				pndMember = new RoleDocumentDelegationMember();
				pndMember.setActiveFromDate(member.getActiveFromDate());
				pndMember.setActiveToDate(member.getActiveToDate());
				pndMember.setActive(member.isActive());
				if(pndMember.isActive()){
					KimCommonUtilsInternal.copyProperties(pndMember, member);
					pndMember.setRoleMemberId(member.getRoleMemberId());
					roleMember = getRoleMemberForRoleMemberId(member.getRoleMemberId());
					if(roleMember!=null){
						pndMember.setRoleMemberName(getMemberName(roleMember.getMemberTypeCode(), roleMember.getMemberId()));
						pndMember.setRoleMemberNamespaceCode(getMemberNamespaceCode(roleMember.getMemberTypeCode(), roleMember.getMemberId()));
					}
					pndMember.setMemberNamespaceCode(getMemberNamespaceCode(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setMemberName(getMemberName(member.getMemberTypeCode(), member.getMemberId()));
					pndMember.setEdit(true);
					pndMember.setQualifiers(loadDelegationMemberQualifiers(identityManagementRoleDocument, member.getAttributes()));
					pndMembers.add(pndMember);
				}
			}
		}
		return pndMembers;
	}

    protected RoleMemberImpl getRoleMemberForRoleMemberId(String roleMemberId){
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
		return (RoleMemberImpl)getBusinessObjectService().findByPrimaryKey(RoleMemberImpl.class, criteria);
    }

    protected List<RoleDocumentDelegationMemberQualifier> loadDelegationMemberQualifiers(IdentityManagementRoleDocument identityManagementRoleDocument,
			List<KimDelegationMemberAttributeDataImpl> attributeDataList){
		List<RoleDocumentDelegationMemberQualifier> pndMemberRoleQualifiers = new ArrayList<RoleDocumentDelegationMemberQualifier>();
		RoleDocumentDelegationMemberQualifier pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
		AttributeDefinitionMap origAttributes = identityManagementRoleDocument.getDefinitions();
		boolean attributePresent = false;
		String origAttributeId;
		if(origAttributes!=null){
			for(String key: origAttributes.keySet()) {
				origAttributeId = identityManagementRoleDocument.getKimAttributeDefnId(origAttributes.get(key));
				if(attributeDataList!=null){
					for(KimDelegationMemberAttributeDataImpl memberRoleQualifier: attributeDataList){
						if(origAttributeId!=null && StringUtils.equals(origAttributeId, memberRoleQualifier.getKimAttribute().getId())){
							pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
							pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getId());
							pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
							pndMemberRoleQualifier.setDelegationMemberId(memberRoleQualifier.getAssignedToId());
							pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
							pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
							pndMemberRoleQualifier.setKimAttribute(memberRoleQualifier.getKimAttribute());
							pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
							attributePresent = true;
						}
					}
				}
				if(!attributePresent){
					pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
					pndMemberRoleQualifier.setKimAttrDefnId(origAttributeId);
					pndMemberRoleQualifier.refreshReferenceObject("kimAttribute");
					pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
				}
				attributePresent = false;
			}
		}
		return pndMemberRoleQualifiers;
	}

	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	@SuppressWarnings("unchecked")
	public void saveRole(IdentityManagementRoleDocument identityManagementRoleDocument) {
		RoleImpl kimRole = new RoleImpl();
		Map<String, String> criteria = new HashMap<String, String>();
		String roleId = identityManagementRoleDocument.getRoleId();
		criteria.put(KIMPropertyConstants.Role.ROLE_ID, roleId);
		RoleImpl origRole = (RoleImpl)getBusinessObjectService().findByPrimaryKey(RoleImpl.class, criteria);

		List<RolePermissionImpl> origRolePermissions = new ArrayList<RolePermissionImpl>();
		List<RoleResponsibilityImpl> origRoleResponsibilities = new ArrayList<RoleResponsibilityImpl>();
		List<RoleMemberImpl> origRoleMembers = new ArrayList<RoleMemberImpl>();
		List<KimDelegationImpl> origRoleDelegations = new ArrayList<KimDelegationImpl>();

		kimRole.setRoleId(identityManagementRoleDocument.getRoleId());
		kimRole.setKimTypeId(identityManagementRoleDocument.getRoleTypeId());
		kimRole.setNamespaceCode(identityManagementRoleDocument.getRoleNamespace());
		kimRole.setRoleName(identityManagementRoleDocument.getRoleName());
		kimRole.setRoleDescription(identityManagementRoleDocument.getRoleDescription());

		if (origRole == null) {
			origRole = new RoleImpl();
			kimRole.setActive(true);
		} else {
			kimRole.setActive(identityManagementRoleDocument.isActive());
			kimRole.setVersionNumber(origRole.getVersionNumber());
			origRolePermissions = (List<RolePermissionImpl>)getBusinessObjectService().findMatching(RolePermissionImpl.class, criteria);
			origRoleResponsibilities = (List<RoleResponsibilityImpl>)getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria);
			origRoleMembers = (List<RoleMemberImpl>)getBusinessObjectService().findMatching(RoleMemberImpl.class, criteria);
			origRoleDelegations = (List<KimDelegationImpl>)getBusinessObjectService().findMatching(KimDelegationImpl.class, criteria);
		}

		if( getKimTypeInfoService().getKimType(identityManagementRoleDocument.getRoleTypeId()) == null ) {
			LOG.error( "Kim type not found for:"+identityManagementRoleDocument.getRoleTypeId(), new Throwable() );
		}

		List<PersistableBusinessObject> bos = new ArrayList<PersistableBusinessObject>();

		bos.add(kimRole);
		bos.addAll(getRolePermissions(identityManagementRoleDocument, origRolePermissions));
		bos.addAll(getRoleResponsibilities(identityManagementRoleDocument, origRoleResponsibilities));
		bos.addAll(getRoleResponsibilitiesActions(identityManagementRoleDocument));
		String initiatorPrincipalId = getInitiatorPrincipalId(identityManagementRoleDocument);
		if(canAssignToRole(identityManagementRoleDocument, initiatorPrincipalId)){
			List<RoleMemberImpl> newRoleMembersList = getRoleMembers(identityManagementRoleDocument, origRoleMembers);
			bos.addAll(newRoleMembersList);
			bos.addAll(getRoleMemberResponsibilityActions(newRoleMembersList));
			//bos.addAll(getRoleMemberResponsibilityActions(identityManagementRoleDocument));
			bos.addAll(getRoleDelegations(identityManagementRoleDocument, origRoleDelegations));
		}
		getBusinessObjectService().save(bos);
		IdentityManagementNotificationService service = (IdentityManagementNotificationService) KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(new QName("KIM", "kimIdentityManagementNotificationService"));
        service.roleUpdated();
		KIMServiceLocatorInternal.getResponsibilityInternalService().updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(identityManagementRoleDocument, origRoleResponsibilities));
		if(!kimRole.isActive()){
			// when a role is inactivated, inactivate the memberships of principals, groups, and roles in
			// that role, delegations, and delegation members, and that roles memberships in other roles
			KimApiServiceLocator.getRoleManagementService().roleInactivated(identityManagementRoleDocument.getRoleId());
		}
	}

	protected List<RolePermissionImpl> getRolePermissions(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RolePermissionImpl> origRolePermissions){
		List<RolePermissionImpl> rolePermissions = new ArrayList<RolePermissionImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getPermissions())){
			for(KimDocumentRolePermission documentRolePermission: identityManagementRoleDocument.getPermissions()){
				RolePermissionImpl newRolePermission = new RolePermissionImpl();
				newRolePermission.setRolePermissionId(documentRolePermission.getRolePermissionId());
				newRolePermission.setRoleId(identityManagementRoleDocument.getRoleId());
				newRolePermission.setPermissionId(documentRolePermission.getPermissionId());
				newRolePermission.setActive( documentRolePermission.isActive() );

				newRolePermission.setActive(documentRolePermission.isActive());
				if(ObjectUtils.isNotNull(origRolePermissions)){
					for(RolePermissionImpl origPermissionImpl: origRolePermissions){
						if(!StringUtils.equals(origPermissionImpl.getRoleId(), newRolePermission.getRoleId()) &&
								StringUtils.equals(origPermissionImpl.getPermissionId(), newRolePermission.getPermissionId()) &&
								!origPermissionImpl.isActive() && newRolePermission.isActive()){
							newRolePermission.setRolePermissionId(origPermissionImpl.getRolePermissionId());
						}
						if(origPermissionImpl.getRolePermissionId()!=null && StringUtils.equals(origPermissionImpl.getRolePermissionId(), newRolePermission.getRolePermissionId())){
							newRolePermission.setVersionNumber(origPermissionImpl.getVersionNumber());
						}
					}
				}
				rolePermissions.add(newRolePermission);
			}
		}
		return rolePermissions;
	}

	protected List<RoleResponsibilityImpl> getRoleResponsibilities(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleResponsibilityImpl> origRoleResponsibilities){
		List<RoleResponsibilityImpl> roleResponsibilities = new ArrayList<RoleResponsibilityImpl>();
		RoleResponsibilityImpl newRoleResponsibility;
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
			for(KimDocumentRoleResponsibility documentRoleResponsibility: identityManagementRoleDocument.getResponsibilities()){
				newRoleResponsibility = new RoleResponsibilityImpl();
				KimCommonUtilsInternal.copyProperties(newRoleResponsibility, documentRoleResponsibility);
				newRoleResponsibility.setActive(documentRoleResponsibility.isActive());
				newRoleResponsibility.setRoleId(identityManagementRoleDocument.getRoleId());
				if(ObjectUtils.isNotNull(origRoleResponsibilities)){
					for(RoleResponsibilityImpl origResponsibilityImpl: origRoleResponsibilities){
						if(!StringUtils.equals(origResponsibilityImpl.getRoleId(), newRoleResponsibility.getRoleId()) &&
								StringUtils.equals(origResponsibilityImpl.getResponsibilityId(), newRoleResponsibility.getResponsibilityId()) &&
								!origResponsibilityImpl.isActive() && newRoleResponsibility.isActive()){
							newRoleResponsibility.setRoleResponsibilityId(origResponsibilityImpl.getRoleResponsibilityId());
						}
						if(origResponsibilityImpl.getRoleResponsibilityId()!=null && StringUtils.equals(origResponsibilityImpl.getRoleResponsibilityId(), newRoleResponsibility.getRoleResponsibilityId()))
							newRoleResponsibility.setVersionNumber(origResponsibilityImpl.getVersionNumber());
					}
				}
				roleResponsibilities.add(newRoleResponsibility);
			}
		}
		return roleResponsibilities;
	}


	protected List <RoleResponsibilityActionImpl> getRoleResponsibilitiesActions(
			IdentityManagementRoleDocument identityManagementRoleDocument){
		List <RoleResponsibilityActionImpl>  roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
		// loop over the responsibilities assigned to the role
			for(KimDocumentRoleResponsibility roleResponsibility : identityManagementRoleDocument.getResponsibilities()){
				// only process if the actions are not assigned at the role member level
				if(!getResponsibilityInternalService().areActionsAtAssignmentLevelById(roleResponsibility.getResponsibilityId())){
					List<KimDocumentRoleResponsibilityAction> documentRoleResponsibilityActions = roleResponsibility.getRoleRspActions();
					if( ObjectUtils.isNotNull(documentRoleResponsibilityActions)
							&& !documentRoleResponsibilityActions.isEmpty()
							&& StringUtils.isNotBlank(documentRoleResponsibilityActions.get(0).getRoleResponsibilityActionId() ) ) {
						RoleResponsibilityActionImpl roleRspAction = new RoleResponsibilityActionImpl();
						roleRspAction.setRoleResponsibilityActionId(documentRoleResponsibilityActions.get(0).getRoleResponsibilityActionId());
						roleRspAction.setActionPolicyCode(documentRoleResponsibilityActions.get(0).getActionPolicyCode());
						roleRspAction.setActionTypeCode(documentRoleResponsibilityActions.get(0).getActionTypeCode());
						roleRspAction.setPriorityNumber(documentRoleResponsibilityActions.get(0).getPriorityNumber());
						roleRspAction.setForceAction(documentRoleResponsibilityActions.get(0).isForceAction());
						roleRspAction.setRoleMemberId("*");
						roleRspAction.setRoleResponsibilityId(documentRoleResponsibilityActions.get(0).getRoleResponsibilityId());
						updateResponsibilityActionVersionNumber(roleRspAction, getRoleResponsibilityActionImpls(roleResponsibility.getRoleResponsibilityId()));
						roleRspActions.add(roleRspAction);
					}
				}
			}
		}
		return roleRspActions;
	}

	// FIXME: This should be pulling by the PK, not using another method which pulls multiple records and then finds
	// the right one here!
	protected void updateResponsibilityActionVersionNumber(RoleResponsibilityActionImpl newRoleRspAction,
			List<RoleResponsibilityActionImpl> origRoleRespActionImpls){
		if(ObjectUtils.isNotNull(origRoleRespActionImpls)){
			for(RoleResponsibilityActionImpl origRoleResponsibilityActionImpl: origRoleRespActionImpls){
				if(origRoleResponsibilityActionImpl.getRoleResponsibilityActionId()!=null && StringUtils.equals(origRoleResponsibilityActionImpl.getRoleResponsibilityActionId(),
						newRoleRspAction.getRoleResponsibilityActionId())) {
					newRoleRspAction.setVersionNumber(origRoleResponsibilityActionImpl.getVersionNumber());
					break;
				}
			}
		}
	}

	protected List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActions(List<RoleMemberImpl> newRoleMembersList){
		List<RoleResponsibilityActionImpl> roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(ObjectUtils.isNotNull(newRoleMembersList)){
			for(RoleMemberImpl roleMember: newRoleMembersList){
				roleRspActions.addAll(roleMember.getRoleRspActions());
			}
		}
		return roleRspActions;
	}

	protected List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		List<RoleResponsibilityActionImpl> roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getMembers())){
			for(KimDocumentRoleMember roleMember: identityManagementRoleDocument.getMembers()){
				for(KimDocumentRoleResponsibilityAction roleRspAction : roleMember.getRoleRspActions()){
					RoleResponsibilityActionImpl entRoleRspAction = new RoleResponsibilityActionImpl();
					entRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
					entRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
					entRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
					entRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
					entRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
					entRoleRspAction.setForceAction(roleRspAction.isForceAction());
					entRoleRspAction.setRoleResponsibilityId(roleRspAction.getRoleResponsibilityId());
					List<RoleResponsibilityActionImpl> actions = getRoleRspActions(roleMember.getRoleMemberId());
					if(ObjectUtils.isNotNull(actions)){
						for(RoleResponsibilityActionImpl orgRspAction : actions) {
							if (orgRspAction.getRoleResponsibilityActionId()!=null && StringUtils.equals(orgRspAction.getRoleResponsibilityActionId(), roleRspAction.getRoleResponsibilityActionId())) {
								entRoleRspAction.setVersionNumber(orgRspAction.getVersionNumber());
							}
						}
					}
					roleRspActions.add(entRoleRspAction);
				}
			}
		}
		return roleRspActions;
	}

    protected List<RoleMemberImpl> getRoleMembers(IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleMemberImpl> origRoleMembers){
        List<RoleMemberImpl> roleMembers = new ArrayList<RoleMemberImpl>();
        RoleMemberImpl newRoleMember;
        RoleMemberImpl origRoleMemberImplTemp = null;
        List<RoleMemberAttributeDataImpl> origAttributes = new ArrayList<RoleMemberAttributeDataImpl>();
        boolean activatingInactive = false;
        String newRoleMemberIdAssigned = "";

        identityManagementRoleDocument.setKimType(KimApiServiceLocator.getKimTypeInfoService().getKimType(identityManagementRoleDocument.getRoleTypeId()));
        KimTypeService kimTypeService = KIMServiceLocatorWeb.getKimTypeService(identityManagementRoleDocument.getKimType());

        if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getMembers())){
            for(KimDocumentRoleMember documentRoleMember: identityManagementRoleDocument.getMembers()){
                origRoleMemberImplTemp = null;

                newRoleMember = new RoleMemberImpl();
                KimCommonUtilsInternal.copyProperties(newRoleMember, documentRoleMember);
                newRoleMember.setRoleId(identityManagementRoleDocument.getRoleId());
                if(ObjectUtils.isNotNull(origRoleMembers)){
                    for(RoleMemberImpl origRoleMemberImpl: origRoleMembers){
                        if((origRoleMemberImpl.getRoleId()!=null && StringUtils.equals(origRoleMemberImpl.getRoleId(), newRoleMember.getRoleId())) &&
                            (origRoleMemberImpl.getMemberId()!=null && StringUtils.equals(origRoleMemberImpl.getMemberId(), newRoleMember.getMemberId())) &&
                            (origRoleMemberImpl.getMemberTypeCode()!=null && StringUtils.equals(origRoleMemberImpl.getMemberTypeCode(), newRoleMember.getMemberTypeCode())) &&
                            !origRoleMemberImpl.isActive() &&
                            !kimTypeService.validateUniqueAttributes(identityManagementRoleDocument.getKimType().getId(),
                                    Attributes.fromMap(documentRoleMember.getQualifierAsAttributeSet()), Attributes.fromMap(origRoleMemberImpl.getQualifier()))){
                            //TODO: verify if you want to add  && newRoleMember.isActive() condition to if...

                            newRoleMemberIdAssigned = newRoleMember.getRoleMemberId();
                            newRoleMember.setRoleMemberId(origRoleMemberImpl.getRoleMemberId());
                            activatingInactive = true;
                        }
                        if(origRoleMemberImpl.getRoleMemberId()!=null && StringUtils.equals(origRoleMemberImpl.getRoleMemberId(), newRoleMember.getRoleMemberId())){
                            newRoleMember.setVersionNumber(origRoleMemberImpl.getVersionNumber());
                            origRoleMemberImplTemp = origRoleMemberImpl;
                        }
                    }
                }
                origAttributes = (origRoleMemberImplTemp==null || origRoleMemberImplTemp.getAttributes()==null)?
                                    new ArrayList<RoleMemberAttributeDataImpl>():origRoleMemberImplTemp.getAttributes();
                newRoleMember.setAttributes(getRoleMemberAttributeData(documentRoleMember.getQualifiers(), origAttributes, activatingInactive, newRoleMemberIdAssigned));
                newRoleMember.setRoleRspActions(getRoleMemberResponsibilityActions(documentRoleMember, origRoleMemberImplTemp, activatingInactive, newRoleMemberIdAssigned));
                roleMembers.add(newRoleMember);
                activatingInactive = false;
            }
        }
        return roleMembers;
    }

	protected List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActions(
			KimDocumentRoleMember documentRoleMember, RoleMemberImpl origRoleMemberImplTemp, boolean activatingInactive, String newRoleMemberIdAssigned){
		List<RoleResponsibilityActionImpl> roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		List<RoleResponsibilityActionImpl> origActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(origRoleMemberImplTemp!=null) {
			origActions = getRoleRspActions(origRoleMemberImplTemp.getRoleMemberId());
		}
		if(CollectionUtils.isNotEmpty(documentRoleMember.getRoleRspActions())){
			for(KimDocumentRoleResponsibilityAction roleRspAction : documentRoleMember.getRoleRspActions()){
				RoleResponsibilityActionImpl newRoleRspAction = new RoleResponsibilityActionImpl();
				newRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
				newRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
				newRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
				newRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
				newRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
				newRoleRspAction.setForceAction(roleRspAction.isForceAction());
				newRoleRspAction.setRoleResponsibilityId("*");
				if(ObjectUtils.isNotNull(origActions)){
					for(RoleResponsibilityActionImpl origRspAction: origActions) {
						if(activatingInactive && StringUtils.equals(origRspAction.getRoleResponsibilityId(), newRoleRspAction.getRoleResponsibilityId()) &&
								StringUtils.equals(newRoleRspAction.getRoleMemberId(), newRoleMemberIdAssigned)){
							newRoleRspAction.setRoleMemberId(origRspAction.getRoleMemberId());
							newRoleRspAction.setRoleResponsibilityActionId(origRspAction.getRoleResponsibilityActionId());
						}
						if (origRspAction.getRoleResponsibilityActionId()!=null && StringUtils.equals(origRspAction.getRoleResponsibilityActionId(), newRoleRspAction.getRoleResponsibilityActionId())) {
							newRoleRspAction.setVersionNumber(origRspAction.getVersionNumber());
						}
					}
				}
				roleRspActions.add(newRoleRspAction);
			}
		}
		return roleRspActions;
	}

	protected List<RoleMemberAttributeDataImpl> getRoleMemberAttributeData(List<KimDocumentRoleQualifier> qualifiers,
			List<RoleMemberAttributeDataImpl> origAttributes, boolean activatingInactive, String newRoleMemberIdAssigned){
		List<RoleMemberAttributeDataImpl> roleMemberAttributeDataList = new ArrayList<RoleMemberAttributeDataImpl>();
		RoleMemberAttributeDataImpl newRoleMemberAttributeData;
		if(CollectionUtils.isNotEmpty(qualifiers)){
			for(KimDocumentRoleQualifier memberRoleQualifier: qualifiers){
				if(StringUtils.isNotBlank(memberRoleQualifier.getAttrVal())){
					newRoleMemberAttributeData = new RoleMemberAttributeDataImpl();
					newRoleMemberAttributeData.setId(memberRoleQualifier.getAttrDataId());
					newRoleMemberAttributeData.setAttributeValue(memberRoleQualifier.getAttrVal());
					newRoleMemberAttributeData.setAssignedToId(memberRoleQualifier.getRoleMemberId());
					newRoleMemberAttributeData.setKimTypeId(memberRoleQualifier.getKimTypId());
					newRoleMemberAttributeData.setKimAttributeId(memberRoleQualifier.getKimAttrDefnId());

					updateAttrValIfNecessary(newRoleMemberAttributeData);

					if(ObjectUtils.isNotNull(origAttributes)){
						for(RoleMemberAttributeDataImpl origAttribute: origAttributes){
							if(activatingInactive && StringUtils.equals(origAttribute.getKimAttributeId(), newRoleMemberAttributeData.getKimAttributeId()) &&
									StringUtils.equals(newRoleMemberAttributeData.getAssignedToId(), newRoleMemberIdAssigned)){
								newRoleMemberAttributeData.setAssignedToId(origAttribute.getAssignedToId());
								newRoleMemberAttributeData.setId(origAttribute.getId());
							}
							if(origAttribute.getId()!=null && StringUtils.equals(origAttribute.getId(), newRoleMemberAttributeData.getId())){
								newRoleMemberAttributeData.setVersionNumber(origAttribute.getVersionNumber());
							}
						}
					}
					roleMemberAttributeDataList.add(newRoleMemberAttributeData);
				}
			}
		}
		return roleMemberAttributeDataList;
	}

	/**
	 * Determines if the attribute value on the attribute data should be updated; if so, it performs some attribute value formatting.
	 * In the default implementation, this method formats checkbox controls
	 *
	 * @param roleMemberAttributeData a role member qualifier attribute to update
	 */
	protected void updateAttrValIfNecessary(RoleMemberAttributeDataImpl roleMemberAttributeData) {
		final AttributeDefinition attributeDefinition = getAttributeDefinition(roleMemberAttributeData.getKimTypeId(),
                roleMemberAttributeData.getKimAttributeId());
		if (attributeDefinition != null) {
			if (attributeDefinition.getControl() != null && attributeDefinition.getControl().isCheckbox()) {
				convertCheckboxAttributeData(roleMemberAttributeData);
			}
		}
	}

	protected void formatAttrValIfNecessary(KimDocumentRoleQualifier roleQualifier) {
		final AttributeDefinition attributeDefinition = getAttributeDefinition(roleQualifier.getKimTypId(),
                roleQualifier.getKimAttrDefnId());
		if (attributeDefinition != null) {
			if (attributeDefinition.getControl() != null && attributeDefinition.getControl().isCheckbox()) {
				formatCheckboxAttributeData(roleQualifier);
			}
		}
	}

	protected void formatCheckboxAttributeData(KimDocumentRoleQualifier roleQualifier) {
		if (roleQualifier.getAttrVal().equals(KimConstants.KIM_ATTRIBUTE_BOOLEAN_TRUE_STR_VALUE)) {
			roleQualifier.setAttrVal(KimConstants.KIM_ATTRIBUTE_BOOLEAN_TRUE_STR_VALUE_DISPLAY);
		} else if (roleQualifier.getAttrVal().equals(KimConstants.KIM_ATTRIBUTE_BOOLEAN_FALSE_STR_VALUE)) {
			roleQualifier.setAttrVal(KimConstants.KIM_ATTRIBUTE_BOOLEAN_FALSE_STR_VALUE_DISPLAY);
		}
	}

	/**
	 * Finds the KNS attribute used to render the given KimAttributeData
	 *
     * @return the KNS attribute used to render that qualifier, or null if the AttributeDefinition cannot be determined
	 */
	protected AttributeDefinition getAttributeDefinition(String kimTypId, String attrDefnId) {
		final KimType type = getKimTypeInfoService().getKimType(kimTypId);
		if (type != null) {
			final KimTypeService typeService = (KimTypeService) KIMServiceLocatorInternal.getBean(type.getServiceName());
			if (typeService != null) {
				final KimTypeAttribute attributeInfo = type.getAttributeDefinitionById(attrDefnId);
				if (attributeInfo != null) {
					final AttributeDefinitionMap attributeMap = typeService.getAttributeDefinitions(type.getId());
					if (attributeMap != null) {
						return attributeMap.getByAttributeName(attributeInfo.getKimAttribute().getAttributeName());
					}
				}
			}
		}
		return null;
	}

	/**
	 * Formats the attribute value on this checkbox attribute, changing "on" to "Y" and "off" to "N"
	 *
	 * @param roleMemberAttributeData the attribute data to format the attribute value of
	 */
	protected void convertCheckboxAttributeData(RoleMemberAttributeDataImpl roleMemberAttributeData) {
		if (roleMemberAttributeData.getAttributeValue().equalsIgnoreCase(KimConstants.KIM_ATTRIBUTE_BOOLEAN_TRUE_STR_VALUE_DISPLAY)) {
			roleMemberAttributeData.setAttributeValue(KimConstants.KIM_ATTRIBUTE_BOOLEAN_TRUE_STR_VALUE);
		} else if (roleMemberAttributeData.getAttributeValue().equalsIgnoreCase(KimConstants.KIM_ATTRIBUTE_BOOLEAN_FALSE_STR_VALUE_DISPLAY)) {
			roleMemberAttributeData.setAttributeValue(KimConstants.KIM_ATTRIBUTE_BOOLEAN_FALSE_STR_VALUE);
		}
	}

	protected List<KimDelegationImpl> getRoleDelegations(IdentityManagementRoleDocument identityManagementRoleDocument, List<KimDelegationImpl> origDelegations){
		List<KimDelegationImpl> kimDelegations = new ArrayList<KimDelegationImpl>();
		KimDelegationImpl newKimDelegation;
		KimDelegationImpl origDelegationImplTemp = null;
		List<KimDelegationMemberImpl> origMembers = new ArrayList<KimDelegationMemberImpl>();
		boolean activatingInactive = false;
		String newDelegationIdAssigned = "";
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getDelegations())){
			for(RoleDocumentDelegation roleDocumentDelegation: identityManagementRoleDocument.getDelegations()){
				newKimDelegation = new KimDelegationImpl();
				KimCommonUtilsInternal.copyProperties(newKimDelegation, roleDocumentDelegation);
				newKimDelegation.setRoleId(identityManagementRoleDocument.getRoleId());
				if(ObjectUtils.isNotNull(origDelegations)){
					for(KimDelegationImpl origDelegationImpl: origDelegations){
						if(StringUtils.equals(origDelegationImpl.getRoleId(), newKimDelegation.getRoleId()) &&
								StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId())){
							//TODO: verify if you want to add  && newRoleMember.isActive() condition to if...
							newDelegationIdAssigned = newKimDelegation.getDelegationId();
							newKimDelegation.setDelegationId(origDelegationImpl.getDelegationId());
							activatingInactive = true;
						}
						if(origDelegationImpl.getDelegationId()!=null && StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId())){
							newKimDelegation.setVersionNumber(origDelegationImpl.getVersionNumber());
							origDelegationImplTemp = origDelegationImpl;
						}
					}
				}
				origMembers = (origDelegationImplTemp==null || origDelegationImplTemp.getMembers()==null)?
									new ArrayList<KimDelegationMemberImpl>():origDelegationImplTemp.getMembers();
				newKimDelegation.setMembers(getDelegationMembers(roleDocumentDelegation.getMembers(), origMembers, activatingInactive, newDelegationIdAssigned));
				kimDelegations.add(newKimDelegation);
				activatingInactive = false;
			}
		}
		return kimDelegations;
	}

	protected List<KimDelegationMemberImpl> getDelegationMembers(List<RoleDocumentDelegationMember> delegationMembers,
			List<KimDelegationMemberImpl> origDelegationMembers, boolean activatingInactive, String newDelegationIdAssigned){
		List<KimDelegationMemberImpl> delegationsMembersList = new ArrayList<KimDelegationMemberImpl>();
		KimDelegationMemberImpl newDelegationMemberImpl;
		KimDelegationMemberImpl origDelegationMemberImplTemp = null;
		List<KimDelegationMemberAttributeDataImpl> origAttributes;
		String delegationMemberId = "";
		if(CollectionUtils.isNotEmpty(delegationMembers)){
			for(RoleDocumentDelegationMember delegationMember: delegationMembers){
				newDelegationMemberImpl = new KimDelegationMemberImpl();
				KimCommonUtilsInternal.copyProperties(newDelegationMemberImpl, delegationMember);
				if(ObjectUtils.isNotNull(origDelegationMembers)){
					for(KimDelegationMemberImpl origDelegationMember: origDelegationMembers){
						if(activatingInactive && StringUtils.equals(origDelegationMember.getMemberId(), newDelegationMemberImpl.getMemberId()) &&
								StringUtils.equals(newDelegationMemberImpl.getDelegationId(), newDelegationIdAssigned) &&
								!origDelegationMember.isActive()){
							newDelegationMemberImpl.setDelegationId(origDelegationMember.getDelegationId());
							delegationMemberId = newDelegationMemberImpl.getDelegationMemberId();
							newDelegationMemberImpl.setDelegationMemberId(origDelegationMember.getDelegationMemberId());
						}
						if(origDelegationMember.getDelegationMemberId()!=null && StringUtils.equals(origDelegationMember.getDelegationMemberId(), newDelegationMemberImpl.getDelegationMemberId())){
							newDelegationMemberImpl.setVersionNumber(origDelegationMember.getVersionNumber());
							origDelegationMemberImplTemp = origDelegationMember;
						}
					}
				}
				origAttributes = (origDelegationMemberImplTemp==null || origDelegationMemberImplTemp.getAttributes()==null)?
						new ArrayList<KimDelegationMemberAttributeDataImpl>():origDelegationMemberImplTemp.getAttributes();
				newDelegationMemberImpl.setAttributes(getDelegationMemberAttributeData(delegationMember.getQualifiers(), origAttributes, activatingInactive, delegationMemberId));
				delegationsMembersList.add(newDelegationMemberImpl);
			}
		}
		return delegationsMembersList;
	}

	//TODO: implement logic same as role members - do not insert qualifiers with blank values
	protected List<KimDelegationMemberAttributeDataImpl> getDelegationMemberAttributeData(
			List<RoleDocumentDelegationMemberQualifier> qualifiers, List<KimDelegationMemberAttributeDataImpl> origAttributes,
			boolean activatingInactive, String delegationMemberId){
		List<KimDelegationMemberAttributeDataImpl> delegationMemberAttributeDataList = new ArrayList<KimDelegationMemberAttributeDataImpl>();
		KimDelegationMemberAttributeDataImpl newDelegationMemberAttributeData;
		if(CollectionUtils.isNotEmpty(qualifiers)){
			for(RoleDocumentDelegationMemberQualifier memberRoleQualifier: qualifiers){
				if(StringUtils.isNotBlank(memberRoleQualifier.getAttrVal())){
					newDelegationMemberAttributeData = new KimDelegationMemberAttributeDataImpl();
					newDelegationMemberAttributeData.setId(memberRoleQualifier.getAttrDataId());
					newDelegationMemberAttributeData.setAttributeValue(memberRoleQualifier.getAttrVal());
					newDelegationMemberAttributeData.setAssignedToId(memberRoleQualifier.getDelegationMemberId());
					newDelegationMemberAttributeData.setKimTypeId(memberRoleQualifier.getKimTypId());
					newDelegationMemberAttributeData.setKimAttributeId(memberRoleQualifier.getKimAttrDefnId());
					if(ObjectUtils.isNotNull(origAttributes)){
						for(KimDelegationMemberAttributeDataImpl origAttribute: origAttributes){
							if(activatingInactive && StringUtils.equals(origAttribute.getKimAttributeId(), newDelegationMemberAttributeData.getKimAttributeId()) &&
									StringUtils.equals(newDelegationMemberAttributeData.getAssignedToId(), delegationMemberId)){
								newDelegationMemberAttributeData.setAssignedToId(origAttribute.getAssignedToId());
								newDelegationMemberAttributeData.setId(origAttribute.getId());
							}
							if(StringUtils.equals(origAttribute.getId(), newDelegationMemberAttributeData.getId())){
								newDelegationMemberAttributeData.setVersionNumber(origAttribute.getVersionNumber());
							}
						}
					}
					delegationMemberAttributeDataList.add(newDelegationMemberAttributeData);
				}
			}
		}
		return delegationMemberAttributeDataList;
	}

	/* Group document methods */
	public void loadGroupDoc(IdentityManagementGroupDocument identityManagementGroupDocument, Group groupInfo){
		//Map<String, String> criteria = new HashMap<String, String>();
		//criteria.put(KimConstants.PrimaryKeyConstants.GROUP_ID, groupInfo.getId());
		//GroupImpl kimGroupImpl = (GroupImpl)
		//	getBusinessObjectService().findByPrimaryKey(GroupImpl.class, criteria);

		identityManagementGroupDocument.setGroupId(groupInfo.getId());
        KimType kimType = KimApiServiceLocator.getKimTypeInfoService().getKimType(groupInfo.getKimTypeId());
		identityManagementGroupDocument.setKimType(kimType);
		identityManagementGroupDocument.setGroupTypeName(kimType.getName());
		identityManagementGroupDocument.setGroupTypeId(kimType.getId());
		identityManagementGroupDocument.setGroupName(groupInfo.getName());
		identityManagementGroupDocument.setGroupDescription(groupInfo.getDescription());
		identityManagementGroupDocument.setActive(groupInfo.isActive());
		identityManagementGroupDocument.setGroupNamespace(groupInfo.getNamespaceCode());

        List<GroupMember> members = new ArrayList(KimApiServiceLocator.getGroupService().getMembersOfGroup(groupInfo.getId()));
        identityManagementGroupDocument.setMembers(loadGroupMembers(identityManagementGroupDocument, members));



        identityManagementGroupDocument.setQualifiers(loadGroupQualifiers(identityManagementGroupDocument, groupInfo.getAttributes()));
		identityManagementGroupDocument.setEditing(true);
	}

	protected static class GroupMemberNameComparator implements Comparator<GroupDocumentMember> {
		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(GroupDocumentMember m1, GroupDocumentMember m2) {
			return m1.getMemberName().compareToIgnoreCase(m2.getMemberName());
		}
	}

	protected GroupMemberNameComparator groupMemberNameComparator = new GroupMemberNameComparator();

	protected List<GroupDocumentMember> loadGroupMembers(
			IdentityManagementGroupDocument identityManagementGroupDocument, List<GroupMember> members){
		List<GroupDocumentMember> pndMembers = new ArrayList<GroupDocumentMember>();
		GroupDocumentMember pndMember = new GroupDocumentMember();
		if(ObjectUtils.isNotNull(members)){
			for(GroupMember member: members){
				pndMember = new GroupDocumentMember();
				pndMember.setActiveFromDate(member.getActiveFromDate());
				pndMember.setActiveToDate(member.getActiveToDate());
				//pndMember.setActive(member.isActive());
				if(pndMember.isActive()){
					pndMember.setGroupMemberId(member.getMemberId());
					pndMember.setGroupId(member.getGroupId());
					pndMember.setMemberId(member.getMemberId());
					pndMember.setMemberName(getMemberName(member.getTypeCode(), member.getMemberId()));
					pndMember.setMemberFullName(getMemberFullName(member.getTypeCode(), member.getMemberId()));
					pndMember.setMemberTypeCode(member.getTypeCode());
					pndMember.setEdit(true);
					pndMembers.add(pndMember);
				}
			}
		}
		Collections.sort(pndMembers, groupMemberNameComparator);
		return pndMembers;
	}

	protected List<GroupDocumentQualifier> loadGroupQualifiers(IdentityManagementGroupDocument IdentityManagementGroupDocument,
			Attributes attributes){
		List<GroupDocumentQualifier> pndGroupQualifiers = new ArrayList<GroupDocumentQualifier>();
		GroupDocumentQualifier pndGroupQualifier = new GroupDocumentQualifier();
		AttributeDefinitionMap origAttributes = IdentityManagementGroupDocument.getDefinitions();
		boolean attributePresent = false;
		String origAttributeId;
		if(origAttributes!=null){

			for(String key: origAttributes.keySet()) {
				origAttributeId = IdentityManagementGroupDocument.getKimAttributeDefnId(origAttributes.get(key));
				if(!attributes.isEmpty()){

					for(GroupAttributeBo groupQualifier: KimAttributeDataBo.createFrom(GroupAttributeBo.class, attributes, IdentityManagementGroupDocument.getGroupTypeId())){
						if(origAttributeId!=null && ObjectUtils.isNotNull(groupQualifier.getKimAttribute()) &&
								StringUtils.equals(origAttributeId, groupQualifier.getKimAttribute().getId())){
							pndGroupQualifier = new GroupDocumentQualifier();
							KimCommonUtilsInternal.copyProperties(pndGroupQualifier, groupQualifier);
							pndGroupQualifier.setAttrDataId(groupQualifier.getId());
							pndGroupQualifier.setAttrVal(groupQualifier.getAttributeValue());
							pndGroupQualifier.setKimAttrDefnId(groupQualifier.getKimAttribute().getId());
							pndGroupQualifier.setKimTypId(groupQualifier.getKimType().getId());
							pndGroupQualifier.setGroupId(groupQualifier.getAssignedToId());
							pndGroupQualifiers.add(pndGroupQualifier);
							attributePresent = true;
						}
					}
				}
				if(!attributePresent){
					pndGroupQualifier = new GroupDocumentQualifier();
					pndGroupQualifier.setKimAttrDefnId(origAttributeId);
					pndGroupQualifiers.add(pndGroupQualifier);
				}
				attributePresent = false;
			}
		}
		return pndGroupQualifiers;
	}

	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	@SuppressWarnings("unchecked")
	public void saveGroup(IdentityManagementGroupDocument identityManagementGroupDocument) {
		GroupBo kimGroup = new GroupBo();
		Map<String, String> criteria = new HashMap<String, String>();
		String groupId = identityManagementGroupDocument.getGroupId();
		criteria.put("groupId", groupId);
		GroupBo origGroup = (GroupBo)getBusinessObjectService().findBySinglePrimaryKey(GroupBo.class, groupId);
		List<GroupMemberBo> origGroupMembers = new ArrayList<GroupMemberBo>();
		if (ObjectUtils.isNull(origGroup)) {
			origGroup = new GroupBo();
			kimGroup.setActive(true);
		} else {
			kimGroup.setVersionNumber(origGroup.getVersionNumber());
			//TODO: when a group is inactivated, inactivate the memberships of principals in that group
			//and the memberships of that group in roles
			kimGroup.setActive(identityManagementGroupDocument.isActive());
			origGroupMembers = (List<GroupMemberBo>)getBusinessObjectService().findMatching(GroupMemberBo.class, criteria);
		}

		kimGroup.setId(identityManagementGroupDocument.getGroupId());
		KimType kimType = getKimTypeInfoService().getKimType(identityManagementGroupDocument.getGroupTypeId());
		if( kimType == null ) {
			throw new RuntimeException("Kim type not found for:"+identityManagementGroupDocument.getGroupTypeId());
		}

		kimGroup.setKimTypeId(kimType.getId());
		kimGroup.setNamespaceCode(identityManagementGroupDocument.getGroupNamespace());
		kimGroup.setName(identityManagementGroupDocument.getGroupName());
		kimGroup.setDescription(identityManagementGroupDocument.getGroupDescription());
		kimGroup.setAttributeDetails(getGroupAttributeData(identityManagementGroupDocument, origGroup.getAttributeDetails()));

		List<String> oldIds = null;
		List<String> newIds = null;
		//List<PersistableBusinessObject> bos = new ArrayList<PersistableBusinessObject>();
		oldIds = getGroupService().getMemberPrincipalIds(kimGroup.getId()); // for the actionList update


		List<GroupMemberBo> newGroupMembersList = getGroupMembers(identityManagementGroupDocument, origGroupMembers);
		kimGroup.setMembers(newGroupMembersList);  // add the new, complete list to the group

		kimGroup = (GroupBo)getBusinessObjectService().save(kimGroup);

		newIds = kimGroup.getMemberPrincipalIds();
		//newIds = getGroupService().getMemberPrincipalIds(kimGroup.getGroupId()); // for the action list update

		// Do an async update of the action list for the updated groups
		KIMServiceLocatorInternal.getGroupInternalService().updateForWorkgroupChange(kimGroup.getId(), oldIds, newIds);
		IdentityManagementNotificationService service = (IdentityManagementNotificationService) KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(new QName("KIM", "kimIdentityManagementNotificationService"));
        service.groupUpdated();
		if(!kimGroup.isActive()){
			// when a group is inactivated, inactivate the memberships of principals in that group
			// and the memberships of that group in roles
			KimApiServiceLocator.getRoleService().groupInactivated(identityManagementGroupDocument.getGroupId());
		}

	}

	/**
	* Looks up GroupInfo objects for each group id passed in
	* @param groupIds the List of group ids to look up GroupInfo records on
	* @return a List of GroupInfo records
	*/
	protected List<? extends Group> getGroupsByIds(List<String> groupIds) {
		List<Group> groups = new ArrayList<Group>();
		Map<String, Group> groupInfoMap = getGroupService().getGroups(groupIds);
		for (String groupId : groupInfoMap.keySet()) {
			groups.add(groupInfoMap.get(groupId));
		}
		return groups;
	}

	protected List<GroupMemberBo> getGroupMembers(IdentityManagementGroupDocument identityManagementGroupDocument, List<GroupMemberBo> origGroupMembers){
		List<GroupMemberBo> groupMembers = new ArrayList<GroupMemberBo>();
		GroupMemberBo newGroupMember;
		if(CollectionUtils.isNotEmpty(identityManagementGroupDocument.getMembers())){
			for(GroupDocumentMember documentGroupMember: identityManagementGroupDocument.getMembers()){
				newGroupMember = new GroupMemberBo();
				//KimCommonUtilsInternal.copyProperties(newGroupMember, documentGroupMember);
                //copy properties manually for now until new BO created for DocumentGroupMember

				newGroupMember.setGroupId(identityManagementGroupDocument.getGroupId());
                newGroupMember.setActiveFromDate(documentGroupMember.getActiveFromDate());
                newGroupMember.setActiveToDate(documentGroupMember.getActiveToDate());
                newGroupMember.setMemberId(documentGroupMember.getMemberId());
                newGroupMember.setTypeCode(documentGroupMember.getMemberTypeCode());
				if(ObjectUtils.isNotNull(origGroupMembers)){
					for(GroupMemberBo origGroupMemberImpl: origGroupMembers){
						if(StringUtils.equals(origGroupMemberImpl.getGroupId(), newGroupMember.getGroupId()) &&
								StringUtils.equals(origGroupMemberImpl.getMemberId(), newGroupMember.getMemberId()) &&
								!origGroupMemberImpl.isActive(new Timestamp(System.currentTimeMillis()))){
							//TODO: verify if you want to add  && newGroupMember.isActive() condition to if...
							newGroupMember.setMemberId(origGroupMemberImpl.getMemberId());
						}
                        if(StringUtils.equals(origGroupMemberImpl.getGroupId(), newGroupMember.getGroupId()) &&
								StringUtils.equals(origGroupMemberImpl.getMemberId(), newGroupMember.getMemberId()) &&
								origGroupMemberImpl.isActive(new Timestamp(System.currentTimeMillis()))){
							newGroupMember.setId(origGroupMemberImpl.getId());
                            newGroupMember.setVersionNumber(origGroupMemberImpl.getVersionNumber());
						}
					}
				}
				groupMembers.add(newGroupMember);
			}
		}
		return groupMembers;
	}

	protected List<GroupAttributeBo> getGroupAttributeData(IdentityManagementGroupDocument identityManagementGroupDocument,
			List<GroupAttributeBo> origAttributes){
		List<GroupAttributeBo> groupAttributeDataList = new ArrayList<GroupAttributeBo>();
		GroupAttributeBo newGroupAttributeData;
		if(CollectionUtils.isNotEmpty(identityManagementGroupDocument.getQualifiers())){
			for(GroupDocumentQualifier groupQualifier: identityManagementGroupDocument.getQualifiers()){
				if(StringUtils.isNotBlank(groupQualifier.getAttrVal())){
					newGroupAttributeData = new GroupAttributeBo();
					newGroupAttributeData.setId(groupQualifier.getAttrDataId());
					newGroupAttributeData.setAttributeValue(groupQualifier.getAttrVal());
					newGroupAttributeData.setAssignedToId(groupQualifier.getGroupId());
					newGroupAttributeData.setKimTypeId(groupQualifier.getKimTypId());
					newGroupAttributeData.setKimAttributeId(groupQualifier.getKimAttrDefnId());
					if(ObjectUtils.isNotNull(origAttributes)){
						for(GroupAttributeBo origAttribute: origAttributes){
							if(StringUtils.equals(origAttribute.getKimAttributeId(), newGroupAttributeData.getKimAttributeId()) &&
									StringUtils.equals(newGroupAttributeData.getAssignedToId(), origAttribute.getAssignedToId())){
							    newGroupAttributeData.setId(origAttribute.getId());
							}
							if(origAttribute.getId()!=null && StringUtils.equals(origAttribute.getId(), newGroupAttributeData.getId())){
							    newGroupAttributeData.setVersionNumber(origAttribute.getVersionNumber());
							}
						}
					}
					groupAttributeDataList.add(newGroupAttributeData);
				}
			}
		}
		return groupAttributeDataList;
	}

    @SuppressWarnings("unchecked")
	public KimDocumentRoleMember getKimDocumentRoleMember(String memberTypeCode, String memberId, String roleId){
    	if(StringUtils.isEmpty(memberTypeCode) || StringUtils.isEmpty(memberId) || StringUtils.isEmpty(roleId))
    		return null;
    	KimDocumentRoleMember documentRoleMember = new KimDocumentRoleMember();
    	documentRoleMember.setRoleId(roleId);
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
    	criteria.put("mbr_id", memberId);

    	List<RoleMemberImpl> matchingRoleMembers = (List<RoleMemberImpl>)getBusinessObjectService().findMatching(RoleMemberImpl.class, criteria);
    	if(matchingRoleMembers==null || matchingRoleMembers.size()<1) return null;

    	RoleMemberImpl roleMemberImpl = matchingRoleMembers.get(0);
    	documentRoleMember.setRoleMemberId(roleMemberImpl.getRoleMemberId());
    	if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
    		Principal principal = getIdentityManagementService().getPrincipal(memberId);
    		if (principal != null) {
    			documentRoleMember.setMemberId(principal.getPrincipalId());
        		documentRoleMember.setMemberName(principal.getPrincipalName());
        		documentRoleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
    		}    		
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	Group group = null;
        	group = getGroupService().getGroup(memberId);
        	if (group != null) {
        		documentRoleMember.setMemberNamespaceCode(group.getNamespaceCode());
        		documentRoleMember.setMemberId(group.getId());
        		documentRoleMember.setMemberName(group.getName());
        		documentRoleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);	
        	}
        	
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	KimRoleInfo role = null;
        	role = getRoleService().getRole(memberId);
        	if (role != null) {
        		documentRoleMember.setMemberNamespaceCode(role.getNamespaceCode());
        		documentRoleMember.setMemberId(role.getRoleId());
        		documentRoleMember.setMemberName(role.getRoleName());
        		documentRoleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
        	}        	
        }
    	return documentRoleMember;
    }

    protected Set<String> getChangedRoleResponsibilityIds(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleResponsibilityImpl> origRoleResponsibilities){
		Set<String> lRet = new HashSet<String>();
		List<String> newResp = new ArrayList<String>();
		List<String> oldResp = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
			for(KimDocumentRoleResponsibility documentRoleResponsibility: identityManagementRoleDocument.getResponsibilities()){
				newResp.add(documentRoleResponsibility.getResponsibilityId());
			}
		}
		if(ObjectUtils.isNotNull(origRoleResponsibilities)){
			for(RoleResponsibilityImpl roleResp: origRoleResponsibilities){
				oldResp.add(roleResp.getResponsibilityId());
			}
		}
		lRet.addAll(newResp);
		lRet.addAll(oldResp);

		return lRet;
	}

	public KimTypeInfoService getKimTypeInfoService() {
		if ( kimTypeInfoService == null ) {
			kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
		}
		return kimTypeInfoService;
	}

    public List<KimDocumentRoleMember> getRoleMembers(Map<String,String> fieldValues) {
		List<KimDocumentRoleMember> matchingRoleMembers = new ArrayList<KimDocumentRoleMember>();
		List<RoleMembershipInfo> matchingRoleMembersTemp = getRoleService().findRoleMembers(fieldValues);
		KimDocumentRoleMember matchingRoleMember;
		BusinessObject roleMemberObject;
		RoleMemberImpl roleMember;
		if(CollectionUtils.isNotEmpty(matchingRoleMembersTemp)){
			for(RoleMembershipInfo roleMembership: matchingRoleMembersTemp){
				roleMember = getRoleMember(roleMembership.getRoleMemberId());
				roleMemberObject = getMember(roleMember.getMemberTypeCode(), roleMember.getMemberId());
				matchingRoleMember = new KimDocumentRoleMember();
				KimCommonUtilsInternal.copyProperties(matchingRoleMember, roleMember);
				matchingRoleMember.setMemberName(getMemberName(roleMember.getMemberTypeCode(), roleMemberObject));
				matchingRoleMember.setMemberNamespaceCode(getMemberNamespaceCode(roleMember.getMemberTypeCode(), roleMemberObject));
				matchingRoleMember.setQualifiers(getQualifiers(roleMember.getAttributes()));
				matchingRoleMembers.add(matchingRoleMember);
			}
		}
		return matchingRoleMembers;
    }

    private List<KimDocumentRoleQualifier> getQualifiers(List<RoleMemberAttributeDataImpl> attributes){
    	if(attributes==null) return null;
    	List<KimDocumentRoleQualifier> qualifiers = new ArrayList<KimDocumentRoleQualifier>();
    	KimDocumentRoleQualifier qualifier;
    	if(ObjectUtils.isNotNull(attributes)){
	    	for(RoleMemberAttributeDataImpl attribute: attributes){
		    	qualifier = new KimDocumentRoleQualifier();
				qualifier.setAttrDataId(attribute.getId());
				qualifier.setAttrVal(attribute.getAttributeValue());
				qualifier.setRoleMemberId(attribute.getAssignedToId());
				qualifier.setKimTypId(attribute.getKimTypeId());
				qualifier.setKimAttrDefnId(attribute.getKimAttributeId());
				qualifier.setKimAttribute(attribute.getKimAttribute());
				qualifiers.add(qualifier);
	    	}
    	}
    	return qualifiers;
    }

   public ResponsibilityInternalService getResponsibilityInternalService() {
    	if ( responsibilityInternalService == null ) {
    		responsibilityInternalService = KIMServiceLocatorInternal.getResponsibilityInternalService();
    	}
		return responsibilityInternalService;
	}
}
