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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.KimEntityAddress;
import org.kuali.rice.kim.bo.entity.KimEntityEmail;
import org.kuali.rice.kim.bo.entity.KimEntityPhone;
import org.kuali.rice.kim.bo.entity.impl.KimEntityAddressImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityAffiliationImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityEmailImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityEmploymentInformationImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityEntityTypeImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityExternalIdentifierImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityPhoneImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityPrivacyPreferencesImpl;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
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
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.control.TextControlDefinition;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This is a description of what this class does - shyu don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class UiDocumentServiceImpl implements UiDocumentService {
	protected PermissionService permissionService;
	protected RoleService roleService;
	protected BusinessObjectService businessObjectService;
	protected IdentityService identityService;
	protected GroupService groupService;
	protected ResponsibilityService responsibilityService;

	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	public void saveEntityPerson(
			IdentityManagementPersonDocument identityManagementPersonDocument) {
		KimEntityImpl kimEntity = new KimEntityImpl();
		KimEntityImpl origEntity = ((IdentityServiceImpl)getIdentityService()).getEntityImpl(identityManagementPersonDocument.getEntityId());
		if (origEntity == null) {
			origEntity = new KimEntityImpl();
			kimEntity.setActive(true);
		} else {
			// TODO : in order to resolve optimistic locking issue. has to get entity and set the version number if entity records matched
			// Need to look into this.
			kimEntity.setActive(origEntity.isActive());
			kimEntity.setVersionNumber(origEntity.getVersionNumber());
		}

		kimEntity.setEntityId(identityManagementPersonDocument.getEntityId());

		boolean inactivatingPrincipal = setupPrincipal(identityManagementPersonDocument, kimEntity, origEntity.getPrincipals());
		setupExtId(identityManagementPersonDocument, kimEntity, origEntity.getExternalIdentifiers());
		setupPrivacy(identityManagementPersonDocument, kimEntity, origEntity.getPrivacyPreferences());
		setupAffiliation(identityManagementPersonDocument, kimEntity, origEntity.getAffiliations(), origEntity.getEmploymentInformation());
		setupName(identityManagementPersonDocument, kimEntity, origEntity.getNames());
		// entitytype
		List<KimEntityEntityTypeImpl> entityTypes = new ArrayList<KimEntityEntityTypeImpl>();
		KimEntityEntityTypeImpl entityType = new KimEntityEntityTypeImpl();
		entityType.setEntityId(identityManagementPersonDocument.getEntityId());
		entityType.setEntityTypeCode(KimConstants.EntityTypes.PERSON);
		entityType.setActive(true);
		entityTypes.add(entityType);
		KimEntityEntityTypeImpl origEntityType = new KimEntityEntityTypeImpl();
		for (KimEntityEntityTypeImpl type : origEntity.getEntityTypes()) {
			// should check entity.entitytypeid, but it's not persist in persondoc yet
			if (type.getEntityTypeCode().equals(entityType.getEntityTypeCode())) {
				origEntityType = type;
				entityType.setVersionNumber(type.getVersionNumber());
				entityType.setActive(type.isActive());
			}
		}
		kimEntity.setEntityTypes(entityTypes);
		setupPhone(identityManagementPersonDocument, entityType, origEntityType.getPhoneNumbers());
		setupEmail(identityManagementPersonDocument, entityType, origEntityType.getEmailAddresses());
		setupAddress(identityManagementPersonDocument, entityType, origEntityType.getAddresses());
		List <GroupMemberImpl>  groupPrincipals = populateGroups(identityManagementPersonDocument);
		List <RoleMemberImpl>  rolePrincipals = populateRoles(identityManagementPersonDocument);
		List <BusinessObject> bos = new ArrayList<BusinessObject>();
		List <RoleResponsibilityActionImpl> roleRspActions = populateRoleRspActions(identityManagementPersonDocument);
		List <RoleMemberAttributeDataImpl> blankRoleMemberAttrs = getBlankRoleMemberAttrs(rolePrincipals);
		bos.add(kimEntity);
		bos.add(kimEntity.getPrivacyPreferences());
		bos.addAll(groupPrincipals);
		bos.addAll(rolePrincipals);
		bos.addAll(roleRspActions);
		// boservice.save(bos) does not handle deleteawarelist
		getBusinessObjectService().save(bos);
		if (!blankRoleMemberAttrs.isEmpty()) {
			getBusinessObjectService().delete(blankRoleMemberAttrs);
		}
		if ( inactivatingPrincipal ) {
			KIMServiceLocator.getRoleManagementService().principalInactivated(identityManagementPersonDocument.getPrincipalId());
		}
	}

	/**
	 *
	 * @see org.kuali.rice.kim.service.UiDocumentService#getAttributeEntries(AttributeDefinitionMap)
	 */
	public Map<String,Object> getAttributeEntries( AttributeDefinitionMap definitions ) {
		Map<String,Object> attributeEntries = new HashMap<String,Object>();
        for (String key : definitions.keySet()) {
			AttributeDefinition definition = definitions.get(key);
			Map<String,Object> attribute = new HashMap<String,Object>();
			if (definition instanceof KimDataDictionaryAttributeDefinition) {
//				AttributeDefinition definition = ((KimDataDictionaryAttributeDefinition) attrDefinition)
//						.getDataDictionaryAttributeDefinition();
				ControlDefinition control = definition.getControl();
				if (control.isSelect()) {
					Map<String,Object> controlMap = new HashMap<String,Object>();
		            controlMap.put("select", "true");
		            controlMap.put("valuesFinder", control.getValuesFinderClass().getName());
		            if (control.getBusinessObjectClass() != null) {
		                controlMap.put("businessObject", control.getBusinessObjectClass().getName());
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
				attribute.put("label", definition.getLabel());
				attribute.put("shortLabel", definition.getShortLabel());
				attribute.put("maxLength", definition.getMaxLength());
				attribute.put("required", definition.isRequired());
				attributeEntries.put(definition.getName(),attribute);
			} else {
				TextControlDefinition control = new TextControlDefinition();
				control.setSize(10);
				attribute.put("control", control);
				attribute.put("label", definition.getLabel());
				attribute.put("maxLength", 20);
				attribute.put("required", true);
				attribute.put("shortLabel", definition.getLabel());
				attributeEntries.put(definition.getName(),attribute);
			}
		}
        return attributeEntries;
	}


	/**
	 *
	 * @see org.kuali.rice.kim.service.UiDocumentService#loadEntityToPersonDoc(IdentityManagementPersonDocument, String)
	 */
	public void loadEntityToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId) {
        KimPrincipalImpl principal = ((IdentityServiceImpl)getIdentityService()).getPrincipalImpl(principalId);
        identityManagementPersonDocument.setPrincipalId(principal.getPrincipalId());
        identityManagementPersonDocument.setPrincipalName(principal.getPrincipalName());
        identityManagementPersonDocument.setPassword(principal.getPassword());
        identityManagementPersonDocument.setActive(principal.isActive());
		KimEntityImpl kimEntity = ((IdentityServiceImpl)getIdentityService()).getEntityImpl(principal.getEntityId());
		identityManagementPersonDocument.setEntityId(kimEntity.getEntityId());
		//identityManagementPersonDocument.setActive(kimEntity.isActive());
		identityManagementPersonDocument.setAffiliations(loadAffiliations(kimEntity.getAffiliations(),kimEntity.getEmploymentInformation()));
		identityManagementPersonDocument.setNames(loadNames(kimEntity.getNames()));
		KimEntityEntityTypeImpl entityType = null;
		for (KimEntityEntityTypeImpl type : kimEntity.getEntityTypes()) {
			if (type.getEntityTypeCode().equals(KimConstants.EntityTypes.PERSON)) {
				entityType = type;
			}
		}

		for (KimEntityExternalIdentifierImpl extId : kimEntity.getExternalIdentifiers()){
			if (extId.getExternalIdentifierTypeCode().equals(KimConstants.PersonExternalIdentifierTypes.TAX)) {
				identityManagementPersonDocument.setTaxId(extId.getExternalId());
			}
		}
		identityManagementPersonDocument.setEmails(loadEmails(entityType.getEmailAddresses()));
		identityManagementPersonDocument.setPhones(loadPhones(entityType.getPhoneNumbers()));
		identityManagementPersonDocument.setAddrs(loadAddresses(entityType.getAddresses()));
		if ( ObjectUtils.isNotNull( kimEntity.getPrivacyPreferences() ) ) {
			identityManagementPersonDocument.setPrivacy(loadPrivacyReferences(kimEntity.getPrivacyPreferences()));
		}

		List<? extends KimGroup> groups = getGroupService().getGroupsForPrincipal(identityManagementPersonDocument.getPrincipalId());
		loadGroupToPersonDoc(identityManagementPersonDocument, groups);
		loadRoleToPersonDoc(identityManagementPersonDocument);


	}


	/**
	 *
	 * This method load related group data to pending document when usert initiate the 'edit'.
	 *
	 * @param identityManagementPersonDocument
	 * @param groups
	 */
	protected void loadGroupToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, List<? extends KimGroup> groups) {
		List <PersonDocumentGroup> docGroups = new ArrayList <PersonDocumentGroup>();
		for (KimGroup group : groups) {
			for (String memberId : getGroupService().getDirectMemberPrincipalIds(group.getGroupId())) {
				// other more direct methods for this ?
				// can't cast group to 'KimGroupImpl' because list is GroupInfo type
				if (memberId.equals(identityManagementPersonDocument.getPrincipalId())) {
					PersonDocumentGroup docGroup = new PersonDocumentGroup();
					docGroup.setGroupId(group.getGroupId());
					docGroup.setGroupName(group.getGroupName());
					docGroup.setPrincipalId(memberId);
					List<String> groupIds = new ArrayList<String>();
					groupIds.add(group.getGroupId());
					for (GroupMembershipInfo groupMember : getGroupService().getGroupMembers(groupIds)) {
						if (groupMember.getMemberId().equals(identityManagementPersonDocument.getPrincipalId()) && groupMember.getMemberTypeCode().equals(KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE)) {
							docGroup.setGroupMemberId(groupMember.getGroupMemberId());
							docGroup.setActiveFromDate(groupMember.getActiveFromDate());
							docGroup.setActiveToDate(groupMember.getActiveToDate());
						}
					}
					docGroup.setEdit(true);
					//docGroup.setGroupMemberId(((KimGroupImpl));
					docGroups.add(docGroup);
				}
			}
		}
		identityManagementPersonDocument.setGroups(docGroups);
	}

	protected void loadRoleToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List <PersonDocumentRole> docRoles = new ArrayList <PersonDocumentRole>();
		List<KimRoleImpl> roles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());
		List<String> roleIds = new ArrayList<String>();
        for (KimRoleImpl role : roles) {
        	if (!roleIds.contains(role.getRoleId())) {
	        	PersonDocumentRole docRole = new PersonDocumentRole();
	        	docRole.setKimTypeId(role.getKimTypeId());
	        	docRole.setActive(role.isActive());
	        	docRole.setNamespaceCode(role.getNamespaceCode());
	        	docRole.setEdit(true);
	        	docRole.setRoleId(role.getRoleId());
	        	docRole.setKimRoleType(role.getKimRoleType());
	        	docRole.setRoleName(role.getRoleName());
	        	docRole.setRolePrncpls(populateDocRolePrncpl(role.getMembers(), identityManagementPersonDocument.getPrincipalId(), getAttributeDefinitionsForRole(docRole)));
	        	docRole.refreshReferenceObject("assignedResponsibilities");
	        	docRoles.add(docRole);
	        	roleIds.add(role.getRoleId());
        	}
        }

		for (PersonDocumentRole role : docRoles) {
			role.setDefinitions(getAttributeDefinitionsForRole(role));
        	// when post again, it will need this during populate
            role.setNewRolePrncpl(new KimDocumentRoleMember());
            for (String key : role.getDefinitions().keySet()) {
            	KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
            	//qualifier.setQualifierKey(key);
            	setAttrDefnIdForQualifier(qualifier,role.getDefinitions().get(key));
            	role.getNewRolePrncpl().getQualifiers().add(qualifier);
            }
            loadRoleRstAction(role);
            role.setAttributeEntry( getAttributeEntries( role.getDefinitions() ) );
		}
        //

        identityManagementPersonDocument.setRoles(docRoles);
	}

	protected AttributeDefinitionMap getAttributeDefinitionsForRole(PersonDocumentRole role) {
    	KimTypeService kimTypeService = KimCommonUtils.getKimTypeService( role.getKimRoleType() );
    	if ( kimTypeService != null ) {
    		return kimTypeService.getAttributeDefinitions(role.getKimTypeId());
    	}
    	return new AttributeDefinitionMap();
	}

	protected void loadRoleRstAction(PersonDocumentRole role) {
		for (KimDocumentRoleMember roleMbr : role.getRolePrncpls()) {
			List<RoleResponsibilityActionImpl> actions = getRoleRspActions(roleMbr.getRoleId(), roleMbr.getRoleMemberId());
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

	protected void setAttrDefnIdForQualifier(KimDocumentRoleQualifier qualifier, AttributeDefinition definition) {
    	qualifier.setKimAttrDefnId(getAttributeDefnId(definition));
    	qualifier.refreshReferenceObject("kimAttribute");
    }

	protected String getAttributeDefnId(AttributeDefinition definition) {
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		return ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	} else {
    		return ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	}
    }

    @SuppressWarnings("unchecked")
	protected List<KimRoleImpl> getRolesForPrincipal(String principalId) {
		if ( principalId == null ) {
			return new ArrayList<KimRoleImpl>();
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put("members.memberId", principalId);
		criteria.put("members.memberTypeCode", KimRoleImpl.PRINCIPAL_MEMBER_TYPE);
		return (List<KimRoleImpl>)getBusinessObjectService().findMatching(KimRoleImpl.class, criteria);
	}

	@SuppressWarnings("unchecked")
	protected List<RoleMemberImpl> getRoleMembersForPrincipal(String principalId) {
		if ( principalId == null ) {
			return new ArrayList<RoleMemberImpl>();
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put("memberId", principalId);
		criteria.put("memberTypeCode", KimRoleImpl.PRINCIPAL_MEMBER_TYPE);
		return (List<RoleMemberImpl>)getBusinessObjectService().findMatching(RoleMemberImpl.class, criteria);
	}

    @SuppressWarnings("unchecked")
	protected List<RoleResponsibilityActionImpl> getRoleRspActions(String roleId, String roleMemberId) {
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put("roleResponsibility.roleId", roleId);
		criteria.put("roleMemberId", roleMemberId);
		return (List<RoleResponsibilityActionImpl>)getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
	}

    protected List<KimDocumentRoleMember> populateDocRolePrncpl(List <RoleMemberImpl> roleMembers, String principalId, AttributeDefinitionMap definitions) {
		List <KimDocumentRoleMember> docRoleMembers = new ArrayList <KimDocumentRoleMember>();
    	for (RoleMemberImpl rolePrincipal : roleMembers) {
    		if (rolePrincipal.getMemberTypeCode().equals(KimRoleImpl.PRINCIPAL_MEMBER_TYPE) && rolePrincipal.getMemberId().equals(principalId)) {
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
    	return docRoleMembers;
    }

    // UI layout for rolequalifier is a little different from kimroleattribute set up.
    // each principal may have member with same role multiple times with different qualifier, but the role
    // only displayed once, and the qualifier displayed multiple times.
    protected List<KimDocumentRoleQualifier> populateDocRoleQualifier(List <RoleMemberAttributeDataImpl> qualifiers, AttributeDefinitionMap definitions) {
		List <KimDocumentRoleQualifier> docRoleQualifiers = new ArrayList <KimDocumentRoleQualifier>();
		for (String key : definitions.keySet()) {
			AttributeDefinition definition = definitions.get(key);
			String attrDefId=null;
			if (definition instanceof KimDataDictionaryAttributeDefinition) {
				attrDefId = ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
			} else {
				attrDefId = ((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
			}
			boolean qualifierFound = false;
			for (RoleMemberAttributeDataImpl qualifier : qualifiers) {
				if (attrDefId.equals(qualifier.getKimAttributeId())) {
		    		KimDocumentRoleQualifier docRoleQualifier = new KimDocumentRoleQualifier();
		    		docRoleQualifier.setAttrDataId(qualifier.getAttributeDataId());
		    		docRoleQualifier.setAttrVal(qualifier.getAttributeValue());
		    		docRoleQualifier.setKimAttrDefnId(qualifier.getKimAttributeId());
		    		docRoleQualifier.setKimAttribute(qualifier.getKimAttribute());
		    		docRoleQualifier.setKimTypId(qualifier.getKimTypeId());
		    		docRoleQualifier.setTargetPrimaryKey(qualifier.getTargetPrimaryKey());
		    		docRoleQualifier.setEdit(true);
		    		docRoleQualifiers.add(docRoleQualifier);
		    		qualifierFound = true;
		    		break;
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
    	return docRoleQualifiers;
    }

    protected List<PersonDocumentName> loadNames(List <KimEntityNameImpl> names) {
		List<PersonDocumentName> docNames = new ArrayList<PersonDocumentName>();
		for (KimEntityNameImpl name : names) {
			PersonDocumentName docName = new PersonDocumentName();
			docName.setNameTypeCode(name.getNameTypeCode());
			docName.setEntityNameType(name.getEntityNameType());
			docName.setFirstName(name.getFirstName());
			docName.setLastName(name.getLastName());
			docName.setMiddleName(name.getMiddleName());
			docName.setTitle(name.getTitle());
			docName.setSuffix(name.getSuffix());
			docName.setActive(name.isActive());
			docName.setDflt(name.isDefault());
			docName.setEdit(true);
			docName.setEntityNameId(name.getEntityNameId());
			docNames.add(docName);
		}
		return docNames;
	}

    protected List<PersonDocumentAffiliation> loadAffiliations(List <KimEntityAffiliationImpl> affiliations, List<KimEntityEmploymentInformationImpl> empInfos) {
		List<PersonDocumentAffiliation> docAffiliations = new ArrayList<PersonDocumentAffiliation>();
		for (KimEntityAffiliationImpl affiliation : affiliations) {
			PersonDocumentAffiliation docAffiliation = new PersonDocumentAffiliation();
			docAffiliation.setAffiliationTypeCode(affiliation.getAffiliationTypeCode());
			docAffiliation.setCampusCode(affiliation.getCampusCode());
			docAffiliation.setActive(affiliation.isActive());
			docAffiliation.setDflt(affiliation.isDefault());
			docAffiliation.setEntityAffiliationId(affiliation
					.getEntityAffiliationId());
			docAffiliation.refreshReferenceObject("affiliationType");
			// EntityAffiliationImpl does not define empinfos as collection
			docAffiliations.add(docAffiliation);
			docAffiliation.setEdit(true);
			// employment informations
			List<PersonDocumentEmploymentInfo> docEmploymentInformations = new ArrayList<PersonDocumentEmploymentInfo>();
			for (KimEntityEmploymentInformationImpl empInfo : empInfos) {
				if (docAffiliation.getEntityAffiliationId().equals(empInfo.getEntityAffiliationId())) {
				PersonDocumentEmploymentInfo docEmpInfo = new PersonDocumentEmploymentInfo();
				docEmpInfo.setEntityEmploymentId(empInfo
						.getEntityEmploymentId());
				docEmpInfo.setEmployeeId(empInfo.getEmployeeId());
				docEmpInfo.setEmploymentRecordId(empInfo
						.getEmploymentRecordId());
				docEmpInfo.setBaseSalaryAmount(empInfo
						.getBaseSalaryAmount());
				docEmpInfo.setPrimaryDepartmentCode(empInfo.getPrimaryDepartmentCode());
				docEmpInfo.setEmployeeStatusCode(empInfo
						.getEmployeeStatusCode());
				docEmpInfo.setEmployeeTypeCode(empInfo
						.getEmployeeTypeCode());
				docEmpInfo.setActive(empInfo.isActive());
				docEmpInfo.setPrimary(empInfo.isPrimary());
				docEmpInfo.setEntityAffiliationId(empInfo
						.getEntityAffiliationId());
				docEmpInfo.setVersionNumber(empInfo.getVersionNumber());
				docEmpInfo.setEdit(true);
				docEmpInfo.refreshReferenceObject("employmentType");
				docEmploymentInformations.add(docEmpInfo);
				}
			}
			docAffiliation.setEmpInfos(docEmploymentInformations);
		}
		return docAffiliations;

	}

    protected boolean setupPrincipal(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<KimPrincipalImpl> origPrincipals) {
    	boolean inactivatingPrincipal = false;
		List<KimPrincipalImpl> principals = new ArrayList<KimPrincipalImpl>();
		KimPrincipalImpl principal = new KimPrincipalImpl();
		principal.setPrincipalId(identityManagementPersonDocument.getPrincipalId());
		principal.setPrincipalName(identityManagementPersonDocument.getPrincipalName());
		principal.setPassword(identityManagementPersonDocument.getPassword());
		principal.setActive(identityManagementPersonDocument.isActive());
		for (KimPrincipalImpl prncpl : origPrincipals) {
			if (prncpl.getPrincipalId().equals(principal.getPrincipalId())) {
				principal.setVersionNumber(prncpl.getVersionNumber());
				// check if inactivating the principal
				if ( prncpl.isActive() && !principal.isActive() ) {
					inactivatingPrincipal = true;
				}
			}
		}
		principals.add(principal);

		kimEntity.setPrincipals(principals);
		return inactivatingPrincipal;
	}

    protected void setupExtId(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<KimEntityExternalIdentifierImpl> origExtIds) {
		List<KimEntityExternalIdentifierImpl> extIds = new ArrayList<KimEntityExternalIdentifierImpl>();
		KimEntityExternalIdentifierImpl extId = new KimEntityExternalIdentifierImpl();
		extId.setEntityId(identityManagementPersonDocument.getEntityId());
		extId.setExternalId(identityManagementPersonDocument.getTaxId());
		extId.setExternalIdentifierTypeCode(KimConstants.PersonExternalIdentifierTypes.TAX);
		for (KimEntityExternalIdentifierImpl origExtId : origExtIds) {
			if (origExtId.getExternalIdentifierTypeCode().equals(extId.getExternalIdentifierTypeCode())) {
				extId.setVersionNumber(origExtId.getVersionNumber());
			}
		}
		extIds.add(extId);
		kimEntity.setExternalIdentifiers(extIds);

	}

    protected void setupPrivacy(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, KimEntityPrivacyPreferencesImpl origPrivacy) {
		KimEntityPrivacyPreferencesImpl privacyPreferences = new KimEntityPrivacyPreferencesImpl();
		privacyPreferences.setEntityId(identityManagementPersonDocument.getEntityId());
		privacyPreferences.setSuppressAddress(identityManagementPersonDocument.getPrivacy().isSuppressAddress());
		privacyPreferences.setSuppressEmail(identityManagementPersonDocument.getPrivacy().isSuppressEmail());
		privacyPreferences.setSuppressName(identityManagementPersonDocument.getPrivacy().isSuppressName());
		privacyPreferences.setSuppressPhone(identityManagementPersonDocument.getPrivacy().isSuppressPhone());
		privacyPreferences
				.setSuppressPersonal(identityManagementPersonDocument.getPrivacy().isSuppressPersonal());
		if (ObjectUtils.isNotNull(origPrivacy)) {
			privacyPreferences.setVersionNumber(origPrivacy.getVersionNumber());
		}
		kimEntity.setPrivacyPreferences(privacyPreferences);
	}
    protected PersonDocumentPrivacy loadPrivacyReferences(KimEntityPrivacyPreferencesImpl privacyPreferences) {
		PersonDocumentPrivacy docPrivacy = new PersonDocumentPrivacy();
		docPrivacy.setSuppressAddress(privacyPreferences.isSuppressAddress());
		docPrivacy.setSuppressEmail(privacyPreferences.isSuppressEmail());
		docPrivacy.setSuppressName(privacyPreferences.isSuppressName());
		docPrivacy.setSuppressPhone(privacyPreferences.isSuppressPhone());
		docPrivacy.setSuppressPersonal(privacyPreferences.isSuppressPersonal());
		docPrivacy.setEdit(true);
		return docPrivacy;
	}

    protected void setupName(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<KimEntityNameImpl> origNames) {
		List<KimEntityNameImpl> entityNames = new ArrayList<KimEntityNameImpl>();
		for (PersonDocumentName name : identityManagementPersonDocument.getNames()) {
			KimEntityNameImpl entityName = new KimEntityNameImpl();
			entityName.setNameTypeCode(name.getNameTypeCode());
			entityName.setFirstName(name.getFirstName());
			entityName.setLastName(name.getLastName());
			entityName.setMiddleName(name.getMiddleName());
			entityName.setTitle(name.getTitle());
			entityName.setSuffix(name.getSuffix());
			entityName.setActive(name.isActive());
			entityName.setDefault(name.isDflt());
			entityName.setEntityNameId(name.getEntityNameId());
			for (KimEntityNameImpl origName : origNames) {
				if (origName.getEntityNameId().equals(entityName.getEntityNameId())) {
					entityName.setVersionNumber(origName.getVersionNumber());
				}

			}
			entityNames.add(entityName);
		}
		kimEntity.setNames(entityNames);

	}

    protected void setupAffiliation(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity,List<KimEntityAffiliationImpl> origAffiliations, List<KimEntityEmploymentInformationImpl> origEmpInfos) {
		List<KimEntityAffiliationImpl> entityAffiliations = new ArrayList<KimEntityAffiliationImpl>();
		// employment informations
		List<KimEntityEmploymentInformationImpl> entityEmploymentInformations = new ArrayList<KimEntityEmploymentInformationImpl>();
		for (PersonDocumentAffiliation affiliation : identityManagementPersonDocument.getAffiliations()) {
			KimEntityAffiliationImpl entityAffiliation = new KimEntityAffiliationImpl();
			entityAffiliation.setAffiliationTypeCode(affiliation
					.getAffiliationTypeCode());
			entityAffiliation.setCampusCode(affiliation.getCampusCode());
			entityAffiliation.setActive(affiliation.isActive());
			entityAffiliation.setDefault(affiliation.isDflt());
			entityAffiliation.setEntityAffiliationId(affiliation
					.getEntityAffiliationId());
			// EntityAffiliationImpl does not define empinfos as collection
			for (KimEntityAffiliationImpl origAffiliation : origAffiliations) {
				if (origAffiliation.getEntityAffiliationId().equals(entityAffiliation.getEntityAffiliationId())) {
					entityAffiliation.setVersionNumber(origAffiliation.getVersionNumber());
				}
			}
			entityAffiliations.add(entityAffiliation);

			for (PersonDocumentEmploymentInfo empInfo : affiliation
					.getEmpInfos()) {
				KimEntityEmploymentInformationImpl entityEmpInfo = new KimEntityEmploymentInformationImpl();
				entityEmpInfo.setEntityEmploymentId(empInfo
						.getEntityEmploymentId());
				entityEmpInfo.setEmployeeId(empInfo.getEmployeeId());
				entityEmpInfo.setEmploymentRecordId(empInfo
						.getEmploymentRecordId());
				entityEmpInfo.setBaseSalaryAmount(empInfo
						.getBaseSalaryAmount());
				entityEmpInfo.setPrimaryDepartmentCode(empInfo
						.getPrimaryDepartmentCode());
				entityEmpInfo.setEmployeeStatusCode(empInfo
						.getEmployeeStatusCode());
				entityEmpInfo.setEmployeeTypeCode(empInfo
						.getEmployeeTypeCode());
				entityEmpInfo.setActive(empInfo.isActive());
				entityEmpInfo.setPrimary(empInfo.isPrimary());
				entityEmpInfo.setEntityId(identityManagementPersonDocument.getEntityId());
				entityEmpInfo.setEntityAffiliationId(empInfo
						.getEntityAffiliationId());
				for (KimEntityEmploymentInformationImpl origEmpInfo : origEmpInfos) {
					if (origEmpInfo.getEntityEmploymentId().equals(entityEmpInfo.getEntityEmploymentId())) {
						entityEmpInfo.setVersionNumber(origEmpInfo.getVersionNumber());
					}
				}
				entityEmploymentInformations.add(entityEmpInfo);
			}

		}
		kimEntity.setEmploymentInformation(entityEmploymentInformations);
		kimEntity.setAffiliations(entityAffiliations);
	}

    protected void setupPhone(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityEntityTypeImpl entityType, List<KimEntityPhone> origPhones) {
		List<KimEntityPhone> entityPhones = new ArrayList<KimEntityPhone>();
		for (PersonDocumentPhone phone : identityManagementPersonDocument.getPhones()) {
			KimEntityPhoneImpl entityPhone = new KimEntityPhoneImpl();
			entityPhone.setPhoneTypeCode(phone.getPhoneTypeCode());
			entityPhone.setEntityId(identityManagementPersonDocument.getEntityId());
			entityPhone.setEntityPhoneId(phone.getEntityPhoneId());
			entityPhone.setEntityTypeCode(entityType.getEntityTypeCode());
			entityPhone.setPhoneNumber(phone.getPhoneNumber());
			entityPhone.setCountryCode(phone.getCountryCode());
			entityPhone.setExtension(phone.getExtension());
			entityPhone.setExtensionNumber(phone.getExtensionNumber());
			entityPhone.setActive(phone.isActive());
			entityPhone.setDefault(phone.isDflt());
			for (KimEntityPhone origPhone : origPhones) {
				if (origPhone.getEntityPhoneId().equals(entityPhone.getEntityPhoneId())) {
					entityPhone.setVersionNumber(((KimEntityPhoneImpl)origPhone).getVersionNumber());
				}
			}
			entityPhone.setEntityPhoneId(phone.getEntityPhoneId());
			entityPhones.add(entityPhone);
		}
		entityType.setPhoneNumbers(entityPhones);

	}

    protected List<PersonDocumentPhone> loadPhones(List<KimEntityPhone> entityPhones) {
		List<PersonDocumentPhone> docPhones = new ArrayList<PersonDocumentPhone>();
		for (KimEntityPhone phone : entityPhones) {
			PersonDocumentPhone docPhone = new PersonDocumentPhone();
			docPhone.setPhoneTypeCode(phone.getPhoneTypeCode());
			docPhone.setPhoneType(((KimEntityPhoneImpl)phone).getPhoneType());
			docPhone.setEntityTypeCode(phone.getEntityTypeCode());
			docPhone.setPhoneNumber(phone.getPhoneNumber());
			docPhone.setCountryCode(phone.getCountryCode());
			docPhone.setExtensionNumber(phone.getExtensionNumber());
			docPhone.setActive(phone.isActive());
			docPhone.setDflt(phone.isDefault());
			docPhone.setEntityPhoneId(phone.getEntityPhoneId());
			docPhone.setEdit(true);
			docPhones.add(docPhone);
		}
		return  docPhones;

	}

    protected void setupEmail(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			KimEntityEntityTypeImpl entityType, List<KimEntityEmail> origEmails) {
		List<KimEntityEmail> entityEmails = new ArrayList<KimEntityEmail>();
		for (PersonDocumentEmail email : identityManagementPersonDocument
				.getEmails()) {
			KimEntityEmailImpl entityEmail = new KimEntityEmailImpl();
			entityEmail.setEntityId(identityManagementPersonDocument
					.getEntityId());
			entityEmail.setEntityTypeCode(entityType.getEntityTypeCode());
			entityEmail.setEmailTypeCode(email.getEmailTypeCode());
			entityEmail.setEmailAddress(email.getEmailAddress());
			entityEmail.setActive(email.isActive());
			entityEmail.setDefault(email.isDflt());
			entityEmail.setEntityEmailId(email.getEntityEmailId());
			for (KimEntityEmail origEmail : origEmails) {
				if (origEmail.getEntityEmailId().equals(entityEmail.getEntityEmailId())) {
					entityEmail.setVersionNumber(((KimEntityEmailImpl)origEmail).getVersionNumber());
				}
			}
			entityEmails.add(entityEmail);
		}
		entityType.setEmailAddresses(entityEmails);
	}
    protected List<PersonDocumentEmail> loadEmails(List<KimEntityEmail> entityEmais) {
		List<PersonDocumentEmail> emails = new ArrayList<PersonDocumentEmail>();
		for (KimEntityEmail email : entityEmais) {
			PersonDocumentEmail docEmail = new PersonDocumentEmail();
			//docEmail.setEntityId(email.getEntityId());
			docEmail.setEntityTypeCode(email.getEntityTypeCode());
			docEmail.setEmailTypeCode(email.getEmailTypeCode());
			docEmail.setEmailType(((KimEntityEmailImpl)email).getEmailType());
			docEmail.setEmailAddress(email.getEmailAddress());
			docEmail.setActive(email.isActive());
			docEmail.setDflt(email.isDefault());
			docEmail.setEntityEmailId(email.getEntityEmailId());
			docEmail.setEdit(true);
			emails.add(docEmail);
		}
		return emails;
	}

    protected void setupAddress(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			KimEntityEntityTypeImpl entityType, List<KimEntityAddress> origAddresses) {
		List<KimEntityAddress> entityAddresses = new ArrayList<KimEntityAddress>();
		for (PersonDocumentAddress address : identityManagementPersonDocument
				.getAddrs()) {
			KimEntityAddressImpl entityAddress = new KimEntityAddressImpl();
			entityAddress.setEntityId(identityManagementPersonDocument
					.getEntityId());
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
			entityAddress.setDefault(address.isDflt());
			entityAddress.setEntityAddressId(address.getEntityAddressId());
			for (KimEntityAddress origAddress : origAddresses) {
				if (origAddress.getEntityAddressId().equals(entityAddress.getEntityAddressId())) {
					entityAddress.setVersionNumber(((KimEntityAddressImpl)origAddress).getVersionNumber());
				}
			}
			entityAddresses.add(entityAddress);
		}
		entityType.setAddresses(entityAddresses);
	}

    protected List<PersonDocumentAddress> loadAddresses(List<KimEntityAddress> entityAddresses) {
		List<PersonDocumentAddress> docAddresses = new ArrayList<PersonDocumentAddress>();
		for (KimEntityAddress address : entityAddresses) {
			PersonDocumentAddress docAddress = new PersonDocumentAddress();
			docAddress.setEntityTypeCode(address.getEntityTypeCode());
			docAddress.setAddressTypeCode(address.getAddressTypeCode());
			docAddress.setAddressType(((KimEntityAddressImpl)address).getAddressType());
			docAddress.setLine1(address.getLine1());
			docAddress.setLine2(address.getLine2());
			docAddress.setLine3(address.getLine3());
			docAddress.setStateCode(address.getStateCode());
			docAddress.setPostalCode(address.getPostalCode());
			docAddress.setCountryCode(address.getCountryCode());
			docAddress.setCityName(address.getCityName());
			docAddress.setActive(address.isActive());
			docAddress.setDflt(address.isDefault());
			docAddress.setEntityAddressId(address.getEntityAddressId());
			docAddress.setEdit(true);
			docAddresses.add(docAddress);
		}
		return docAddresses;
	}

    protected List <GroupMemberImpl> populateGroups(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List <GroupMemberImpl>  groupPrincipals = new ArrayList<GroupMemberImpl>();
//		List<? extends KimGroup> origGroups = getGroupService().getGroupsForPrincipal(identityManagementPersonDocument.getPrincipalId());
		for (PersonDocumentGroup group : identityManagementPersonDocument.getGroups()) {
			GroupMemberImpl groupPrincipalImpl = new GroupMemberImpl();
			groupPrincipalImpl.setGroupId(group.getGroupId());
			groupPrincipalImpl.setActiveFromDate(group.getActiveFromDate());
			groupPrincipalImpl.setActiveToDate(group.getActiveToDate());
			groupPrincipalImpl.setGroupMemberId(group.getGroupMemberId());
			// TODO : principalId is not ready here yet ?
			groupPrincipalImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
			groupPrincipalImpl.setMemberTypeCode(KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
			List<String> groupIds = new ArrayList<String>();
			groupIds.add(group.getGroupId());
			for (GroupMembershipInfo groupMember : getGroupService().getGroupMembers(groupIds)) {
				if (groupMember.getMemberId().equals(identityManagementPersonDocument.getPrincipalId()) && groupMember.getMemberTypeCode().equals(KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE)) {
					groupPrincipalImpl.setVersionNumber(groupMember.getVersionNumber());
				}
			}

			groupPrincipals.add(groupPrincipalImpl);

		}
		return groupPrincipals;
	}

    protected List <RoleMemberImpl> populateRoles(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List<KimRoleImpl> origRoles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());

		List <RoleMemberImpl>  rolePrincipals = new ArrayList<RoleMemberImpl>();
		for (PersonDocumentRole role : identityManagementPersonDocument.getRoles()) {
			List<RoleMemberImpl> origRoleMembers = new ArrayList<RoleMemberImpl>();
			for (KimRoleImpl origRole : origRoles) {
				if (origRole.getRoleId().equals(role.getRoleId())) {
					origRoleMembers = origRole.getMembers();
					break;
				}
			}
			if (role.getRolePrncpls().isEmpty()) {
				if (!role.getDefinitions().isEmpty()) {
					RoleMemberImpl rolePrincipalImpl = new RoleMemberImpl();
					rolePrincipalImpl.setRoleId(role.getRoleId());
					rolePrincipalImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
					rolePrincipalImpl.setMemberTypeCode(KimRoleImpl.PRINCIPAL_MEMBER_TYPE);
					rolePrincipals.add(rolePrincipalImpl);
				}
			} else {
				for (KimDocumentRoleMember principal : role.getRolePrncpls()) {
					RoleMemberImpl rolePrincipalImpl = new RoleMemberImpl();
					rolePrincipalImpl.setRoleId(role.getRoleId());
					// TODO : principalId is not ready here yet ?
					rolePrincipalImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
					rolePrincipalImpl.setMemberTypeCode(KimRoleImpl.PRINCIPAL_MEMBER_TYPE);
					rolePrincipalImpl.setRoleMemberId(principal.getRoleMemberId());
					rolePrincipalImpl.setActiveFromDate(principal.getActiveFromDate());
					rolePrincipalImpl.setActiveToDate(principal.getActiveToDate());
					List<RoleMemberAttributeDataImpl> origAttributes = new ArrayList<RoleMemberAttributeDataImpl>();
					for (RoleMemberImpl origMember : origRoleMembers) {
						if (origMember.getRoleMemberId().equals(principal.getRoleMemberId())) {
							origAttributes = origMember.getAttributes();
							rolePrincipalImpl.setVersionNumber(origMember.getVersionNumber());
						}
					}
					List<RoleMemberAttributeDataImpl> attributes = new ArrayList<RoleMemberAttributeDataImpl>();
					for (KimDocumentRoleQualifier qualifier : principal.getQualifiers()) {
						//if (StringUtils.isNotBlank(qualifier.getAttrVal())) {
							RoleMemberAttributeDataImpl attribute = new RoleMemberAttributeDataImpl();
							attribute.setAttributeDataId(qualifier.getAttrDataId());
							attribute.setAttributeValue(qualifier.getAttrVal());
							attribute.setKimAttributeId(qualifier.getKimAttrDefnId());
							attribute.setTargetPrimaryKey(qualifier.getTargetPrimaryKey());
							attribute.setKimTypeId(qualifier.getKimTypId());
							for (RoleMemberAttributeDataImpl origAttribute : origAttributes) {
								if (origAttribute.getAttributeDataId().equals(qualifier.getAttrDataId())) {
									attribute.setVersionNumber(origAttribute.getVersionNumber());
								}
							}
							if (attribute.getVersionNumber() != null || StringUtils.isNotBlank(qualifier.getAttrVal())) {
								attributes.add(attribute);
							}
						//}
					}
					rolePrincipalImpl.setAttributes(attributes);
					rolePrincipals.add(rolePrincipalImpl);
				}
			}
		}
		return rolePrincipals;

	}


    protected List <RoleMemberAttributeDataImpl> getBlankRoleMemberAttrs(List <RoleMemberImpl> rolePrncpls) {

		List <RoleMemberAttributeDataImpl>  blankRoleMemberAttrs = new ArrayList<RoleMemberAttributeDataImpl>();
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


		return blankRoleMemberAttrs;

	}

    protected List <RoleResponsibilityActionImpl> populateRoleRspActions(IdentityManagementPersonDocument identityManagementPersonDocument) {
//		List<KimRoleImpl> origRoles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());

		List <RoleResponsibilityActionImpl>  roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		for (PersonDocumentRole role : identityManagementPersonDocument.getRoles()) {
			for (KimDocumentRoleMember roleMbr : role.getRolePrncpls()) {
				for (KimDocumentRoleResponsibilityAction roleRspAction : roleMbr.getRoleRspActions()) {
					RoleResponsibilityActionImpl entRoleRspAction = new RoleResponsibilityActionImpl();
					entRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
					entRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
					entRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
					entRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
					entRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
					entRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
					entRoleRspAction.setRoleResponsibilityId(roleRspAction.getRoleResponsibilityId());
					List<RoleResponsibilityActionImpl> actions = getRoleRspActions(roleMbr.getRoleId(), roleMbr.getRoleMemberId());
					for(RoleResponsibilityActionImpl orgRspAction : actions) {
						if (orgRspAction.getRoleResponsibilityActionId().equals(roleRspAction.getRoleResponsibilityActionId())) {
							entRoleRspAction.setVersionNumber(orgRspAction.getVersionNumber());
						}
					}
					roleRspActions.add(entRoleRspAction);
				}
			}

		}
		return roleRspActions;

	}

	public BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	public IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KIMServiceLocator.getIdentityService();
		}
		return identityService;
	}

	public GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KIMServiceLocator.getGroupService();
		}
		return groupService;
	}

	/**
	 * @return the permissionService
	 */
	public PermissionService getPermissionService() {
	   	if(this.permissionService == null){
	   		this.permissionService = KIMServiceLocator.getPermissionService();
    	}
		return this.permissionService;
	}

	/**
	 * @param permissionService the permissionService to set
	 */
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	/**
	 * @return the roleService
	 */
	public RoleService getRoleService() {
	   	if(this.roleService == null){
	   		this.roleService = KIMServiceLocator.getRoleService();
    	}
		return this.roleService;
	}

	/**
	 * @param roleService the roleService to set
	 */
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public ResponsibilityService getResponsibilityService() {
	   	if ( responsibilityService == null ) {
    		responsibilityService = KIMServiceLocator.getResponsibilityService();
    	}
		return responsibilityService;
	}

	public void setResponsibilityService(ResponsibilityService responsibilityService) {
		this.responsibilityService = responsibilityService;
	}


	/* Role document methods */
	@SuppressWarnings("unchecked")
	public void loadRoleDoc(IdentityManagementRoleDocument identityManagementRoleDocument, KimRole kimRole){
		KimRoleInfo kimRoleInfo = (KimRoleInfo)kimRole;
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("roleId", kimRoleInfo.getRoleId());
		KimRoleImpl kimRoleImpl = (KimRoleImpl)
			KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimRoleImpl.class, criteria);
		identityManagementRoleDocument.setRoleId(kimRoleImpl.getRoleId());
		identityManagementRoleDocument.setKimType(kimRoleImpl.getKimRoleType());
		identityManagementRoleDocument.setRoleTypeName(kimRoleImpl.getKimRoleType().getName());
		identityManagementRoleDocument.setRoleTypeId(kimRoleImpl.getKimRoleType().getKimTypeId());
		identityManagementRoleDocument.setRoleName(kimRoleImpl.getRoleName());
		identityManagementRoleDocument.setActive(kimRoleImpl.isActive());
		identityManagementRoleDocument.setRoleNamespace(kimRoleImpl.getNamespaceCode());
		criteria = new HashMap<String,String>( 2 );
		criteria.put("roleId", kimRoleImpl.getRoleId());

		identityManagementRoleDocument.setPermissions(loadPermissions((List<RolePermissionImpl>)getBusinessObjectService().findMatching(RolePermissionImpl.class, criteria)));
		identityManagementRoleDocument.setResponsibilities(loadResponsibilities((List<RoleResponsibilityImpl>)getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria)));
		loadResponsibilityRoleRspActions(identityManagementRoleDocument);
		identityManagementRoleDocument.setMembers(loadRoleMembers(identityManagementRoleDocument, kimRoleImpl.getMembers()));
		loadMemberRoleRspActions(identityManagementRoleDocument);
		identityManagementRoleDocument.setDelegations(loadRoleDocumentDelegations(getRoleDelegations(kimRoleImpl.getRoleId())));
		//Since delegation members are flattened out on the UI...
		setDelegationMembersInDocument(identityManagementRoleDocument);
		identityManagementRoleDocument.setKimType(kimRoleImpl.getKimRoleType());
	}

	protected void setDelegationMembersInDocument(IdentityManagementRoleDocument identityManagementRoleDocument){
		for(RoleDocumentDelegation delegation: identityManagementRoleDocument.getDelegations()){
			for(RoleDocumentDelegationMember member: delegation.getMembers()){
				member.setDelegationTypeCode(delegation.getDelegationTypeCode());
				identityManagementRoleDocument.getDelegationMembers().add(member);
			}
		}
	}

	protected List<KimDocumentRoleResponsibility> loadResponsibilities(List<RoleResponsibilityImpl> roleResponsibilities){
		List<KimDocumentRoleResponsibility> documentRoleResponsibilities = new ArrayList<KimDocumentRoleResponsibility>();
		KimDocumentRoleResponsibility roleResponsibilityCopy;
		for(RoleResponsibilityImpl roleResponsibility: roleResponsibilities){
			roleResponsibilityCopy = new KimDocumentRoleResponsibility();
			KimCommonUtils.copyProperties(roleResponsibilityCopy, roleResponsibility);
			roleResponsibilityCopy.setEdit(true);
			if(roleResponsibilityCopy.isActive())
				documentRoleResponsibilities.add(roleResponsibilityCopy);
		}
		return documentRoleResponsibilities;
	}

	protected List<KimDocumentRolePermission> loadPermissions(List<RolePermissionImpl> rolePermissions){
		List<KimDocumentRolePermission> documentRolePermissions = new ArrayList<KimDocumentRolePermission>();
		KimDocumentRolePermission rolePermissionCopy;
		for(RolePermissionImpl rolePermission: rolePermissions){
			rolePermissionCopy = new KimDocumentRolePermission();
			KimCommonUtils.copyProperties(rolePermissionCopy, rolePermission);
			rolePermissionCopy.setEdit(true);
			if(rolePermissionCopy.isActive())
				documentRolePermissions.add(rolePermissionCopy);
		}
		return documentRolePermissions;
	}

	protected List<KimDocumentRoleMember> loadRoleMembers(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleMemberImpl> members){
		List<KimDocumentRoleMember> pndMembers = new ArrayList<KimDocumentRoleMember>();
		KimDocumentRoleMember pndMember = new KimDocumentRoleMember();
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
				pndMember.setMemberTypeCode(member.getMemberTypeCode());
				pndMember.setQualifiers(loadRoleMemberQualifiers(identityManagementRoleDocument, member.getAttributes()));
				pndMember.setEdit(true);
				pndMembers.add(pndMember);
			}
		}
		return pndMembers;
	}

	protected void loadResponsibilityRoleRspActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		for(KimDocumentRoleResponsibility responsibility: identityManagementRoleDocument.getResponsibilities()){
			responsibility.getRoleRspActions().addAll(loadKimDocumentRoleRespActions(
					getRoleResponsibilityActionImpls(responsibility.getRoleResponsibilityId()),
					responsibility.getResponsibilityId()));
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
	public List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActionImpls(String roleMemberId, String roleResponsibilityId){
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ID, roleResponsibilityId);
		return (List<RoleResponsibilityActionImpl>)
			getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);

	}

	protected void loadMemberRoleRspActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		for(KimDocumentRoleMember member: identityManagementRoleDocument.getMembers()){
			for(KimDocumentRoleResponsibility responsibility: identityManagementRoleDocument.getResponsibilities()){
				member.getRoleRspActions().addAll(loadKimDocumentRoleRespActions(
					getRoleMemberResponsibilityActionImpls(member.getRoleMemberId(), responsibility.getRoleResponsibilityId()),
					responsibility.getResponsibilityId()));
			}
		}
	}

	protected List<KimDocumentRoleResponsibilityAction> loadKimDocumentRoleRespActions(
			List<RoleResponsibilityActionImpl> roleRespActionImpls, String responsibilityId){
		List<KimDocumentRoleResponsibilityAction> documentRoleRespActions = new ArrayList<KimDocumentRoleResponsibilityAction>();
		KimDocumentRoleResponsibilityAction documentRoleRespAction;
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID, responsibilityId);
		KimResponsibilityImpl responsibilityImpl = (KimResponsibilityImpl)
			getBusinessObjectService().findByPrimaryKey(KimResponsibilityImpl.class, criteria);
		for(RoleResponsibilityActionImpl roleRespActionImpl: roleRespActionImpls){
			documentRoleRespAction = new KimDocumentRoleResponsibilityAction();
			KimCommonUtils.copyProperties(documentRoleRespAction, roleRespActionImpl);
			documentRoleRespAction.setKimResponsibility(responsibilityImpl);
			documentRoleRespActions.add(documentRoleRespAction);
		}
		return documentRoleRespActions;
	}

    public BusinessObject getMember(String memberTypeCode, String memberId){
        Class<? extends BusinessObject> roleMemberTypeClass = null;
        String roleMemberIdName = "";
    	if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberTypeClass = KimPrincipalImpl.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.PRINCIPAL_ID;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	roleMemberTypeClass = KimGroupImpl.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.GROUP_ID;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	roleMemberTypeClass = KimRoleImpl.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.ROLE_ID;
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(roleMemberIdName, memberId);
        return KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(roleMemberTypeClass, criteria);
    }

	public String getMemberName(String memberTypeCode, String memberId){
		BusinessObject member = getMember(memberTypeCode, memberId);
		return getMemberName(memberTypeCode, member);
	}

	public String getMemberNamespaceCode(String memberTypeCode, String memberId){
		BusinessObject member = getMember(memberTypeCode, memberId);
		return getMemberNamespaceCode(memberTypeCode, member);
	}

    public String getMemberName(String memberTypeCode, BusinessObject member){
    	String roleMemberName = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberName = ((KimPrincipalImpl)member).getPrincipalName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	roleMemberName = ((KimGroupImpl)member).getGroupName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	roleMemberName = ((KimRoleImpl)member).getRoleName();
        }
        return roleMemberName;
    }

    public String getMemberNamespaceCode(String memberTypeCode, BusinessObject member){
    	String roleMemberNamespaceCode = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberNamespaceCode = "";
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	roleMemberNamespaceCode = ((KimGroupImpl)member).getNamespaceCode();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	roleMemberNamespaceCode = ((KimRoleImpl)member).getNamespaceCode();
        }
        return roleMemberNamespaceCode;
    }

    protected List<KimDocumentRoleQualifier> loadRoleMemberQualifiers(IdentityManagementRoleDocument identityManagementRoleDocument,
			List<RoleMemberAttributeDataImpl> attributeDataList){
		List<KimDocumentRoleQualifier> pndMemberRoleQualifiers = new ArrayList<KimDocumentRoleQualifier>();
		KimDocumentRoleQualifier pndMemberRoleQualifier = new KimDocumentRoleQualifier();
		AttributeDefinitionMap origAttributes = identityManagementRoleDocument.getDefinitions();
		boolean attributePresent = false;
		String origAttributeId;
		for(String key: origAttributes.keySet()) {
			origAttributeId = identityManagementRoleDocument.getKimAttributeDefnId(origAttributes.get(key));
			for(RoleMemberAttributeDataImpl memberRoleQualifier: attributeDataList){
				if(origAttributeId.equals(memberRoleQualifier.getKimAttribute().getKimAttributeId())){
					pndMemberRoleQualifier = new KimDocumentRoleQualifier();
					pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getAttributeDataId());
					pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
					pndMemberRoleQualifier.setTargetPrimaryKey(memberRoleQualifier.getTargetPrimaryKey());
					pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
					pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
					pndMemberRoleQualifier.setKimAttribute(memberRoleQualifier.getKimAttribute());
					pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
					attributePresent = true;
				}
			}
			if(!attributePresent){
				pndMemberRoleQualifier = new KimDocumentRoleQualifier();
				pndMemberRoleQualifier.setKimAttrDefnId(origAttributeId);
				pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
			}
			attributePresent = false;
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

    protected List<RoleDocumentDelegation> loadRoleDocumentDelegations(List<KimDelegationImpl> delegations){
		List<RoleDocumentDelegation> delList = new ArrayList<RoleDocumentDelegation>();
		RoleDocumentDelegation documentDelegation;
		for(KimDelegationImpl del: delegations){
			documentDelegation = new RoleDocumentDelegation();
			documentDelegation.setActive(del.isActive());
			if(documentDelegation.isActive()){
				documentDelegation.setDelegationId(del.getDelegationId());
				documentDelegation.setDelegationTypeCode(del.getDelegationTypeCode());
				documentDelegation.setKimType(del.getKimType());
				documentDelegation.setKimTypeId(del.getKimTypeId());
				documentDelegation.setMembers(loadDelegationMembers(del.getMembers()));
				documentDelegation.setRoleId(del.getRoleId());
				documentDelegation.setEdit(true);
				delList.add(documentDelegation);
			}
		}
		return delList;
	}

    protected List<RoleDocumentDelegationMember> loadDelegationMembers(List<KimDelegationMemberImpl> members){
		List<RoleDocumentDelegationMember> pndMembers = new ArrayList<RoleDocumentDelegationMember>();
		RoleDocumentDelegationMember pndMember = new RoleDocumentDelegationMember();
		for(KimDelegationMemberImpl member: members){
			pndMember = new RoleDocumentDelegationMember();
			pndMember.setActiveFromDate(member.getActiveFromDate());
			pndMember.setActiveToDate(member.getActiveToDate());
			pndMember.setActive(member.isActive());
			if(pndMember.isActive()){
				KimCommonUtils.copyProperties(pndMember, member);
				pndMember.setMemberNamespaceCode(getMemberNamespaceCode(member.getMemberTypeCode(), member.getMemberId()));
				pndMember.setMemberName(getMemberName(member.getMemberTypeCode(), member.getMemberId()));
				pndMember.setEdit(true);
				pndMember.setQualifiers(loadDelegationMemberQualifiers(member.getAttributes()));
				pndMembers.add(pndMember);
			}
		}
		return pndMembers;
	}

    protected List<RoleDocumentDelegationMemberQualifier> loadDelegationMemberQualifiers(List<KimDelegationMemberAttributeDataImpl> attributeDataList){
		List<RoleDocumentDelegationMemberQualifier> pndMemberRoleQualifiers = new ArrayList<RoleDocumentDelegationMemberQualifier>();
		RoleDocumentDelegationMemberQualifier pndMemberRoleQualifier;
		for(KimDelegationMemberAttributeDataImpl memberRoleQualifier: attributeDataList){
			pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
			pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getAttributeDataId());
			pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
			pndMemberRoleQualifier.setTargetPrimaryKey(memberRoleQualifier.getTargetPrimaryKey());
			pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
			pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
			pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
		}
		return pndMemberRoleQualifiers;
	}


	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	@SuppressWarnings("unchecked")
	public void saveRole(IdentityManagementRoleDocument identityManagementRoleDocument) {
		KimRoleImpl kimRole = new KimRoleImpl();
		Map<String, String> criteria = new HashMap<String, String>();
		String roleId = identityManagementRoleDocument.getRoleId();
		criteria.put("roleId", roleId);
		KimRoleImpl origRole = (KimRoleImpl)getBusinessObjectService().findByPrimaryKey(KimRoleImpl.class, criteria);
		List<RolePermissionImpl> origRolePermissions = new ArrayList<RolePermissionImpl>();
		List<RoleResponsibilityImpl> origRoleResponsibilities = new ArrayList<RoleResponsibilityImpl>();
		List<RoleMemberImpl> origRoleMembers = new ArrayList<RoleMemberImpl>();
		List<KimDelegationImpl> origRoleDelegations = new ArrayList<KimDelegationImpl>();
		if (ObjectUtils.isNull(origRole)) {
			origRole = new KimRoleImpl();
			kimRole.setActive(true);
		} else {
			kimRole.setActive(origRole.isActive());
			kimRole.setVersionNumber(origRole.getVersionNumber());
			origRolePermissions = (List<RolePermissionImpl>)getBusinessObjectService().findMatching(RolePermissionImpl.class, criteria);
			origRoleResponsibilities = (List<RoleResponsibilityImpl>)getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria);
			origRoleMembers = (List<RoleMemberImpl>)getBusinessObjectService().findMatching(RoleMemberImpl.class, criteria);
			origRoleDelegations = (List<KimDelegationImpl>)getBusinessObjectService().findMatching(KimDelegationImpl.class, criteria);
		}

		kimRole.setRoleId(identityManagementRoleDocument.getRoleId());
		criteria = new HashMap<String, String>();
		criteria.put("kimTypeId", identityManagementRoleDocument.getRoleTypeId());
		KimTypeImpl typeImpl = (KimTypeImpl)getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria);
		if(typeImpl==null)
			throw new RuntimeException("Kim type not found for:"+identityManagementRoleDocument.getRoleTypeId());

		kimRole.setKimRoleType(typeImpl);
		kimRole.setKimTypeId(typeImpl.getKimTypeId());
		kimRole.setNamespaceCode(identityManagementRoleDocument.getRoleNamespace());
		kimRole.setRoleName(identityManagementRoleDocument.getRoleName());

		List<BusinessObject> bos = new ArrayList<BusinessObject>();

		bos.add(kimRole);
		bos.addAll(getRolePermissions(identityManagementRoleDocument, origRolePermissions));
		bos.addAll(getRoleResponsibilities(identityManagementRoleDocument, origRoleResponsibilities));
		bos.addAll(getRoleResponsibilitiesActions(identityManagementRoleDocument));
		List<RoleMemberImpl> newRoleMembersList = getRoleMembers(identityManagementRoleDocument, origRoleMembers);
		bos.addAll(newRoleMembersList);
		bos.addAll(getRoleMemberResponsibilityActions(newRoleMembersList));
		//bos.addAll(getRoleMemberResponsibilityActions(identityManagementRoleDocument));
		bos.addAll(getRoleDelegations(identityManagementRoleDocument, origRoleDelegations));

		getBusinessObjectService().save(bos);

		KIMServiceLocator.getResponsibilityInternalService().updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(identityManagementRoleDocument, origRoleResponsibilities));
	}

	protected List<RolePermissionImpl> getRolePermissions(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RolePermissionImpl> origRolePermissions){
		List<RolePermissionImpl> rolePermissions = new ArrayList<RolePermissionImpl>();
		RolePermissionImpl newRolePermission;
		for(KimDocumentRolePermission documentRolePermission: identityManagementRoleDocument.getPermissions()){
			newRolePermission = new RolePermissionImpl();
			KimCommonUtils.copyProperties(newRolePermission, documentRolePermission);
			newRolePermission.setActive(documentRolePermission.isActive());
			newRolePermission.setRoleId(identityManagementRoleDocument.getRoleId());
			for(RolePermissionImpl origPermissionImpl: origRolePermissions){
				if(!origPermissionImpl.getRoleId().equals(newRolePermission.getRoleId()) &&
						origPermissionImpl.getPermissionId().equals(newRolePermission.getPermissionId()) &&
						!origPermissionImpl.isActive() && newRolePermission.isActive()){
					newRolePermission.setRolePermissionId(origPermissionImpl.getRolePermissionId());
				}
				if(origPermissionImpl.getRolePermissionId().equals(newRolePermission.getRolePermissionId())){
					newRolePermission.setVersionNumber(origPermissionImpl.getVersionNumber());
				}
			}
			rolePermissions.add(newRolePermission);
		}
		return rolePermissions;
	}

	protected List<RoleResponsibilityImpl> getRoleResponsibilities(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleResponsibilityImpl> origRoleResponsibilities){
		List<RoleResponsibilityImpl> roleResponsibilities = new ArrayList<RoleResponsibilityImpl>();
		RoleResponsibilityImpl newRoleResponsibility;
		for(KimDocumentRoleResponsibility documentRoleResponsibility: identityManagementRoleDocument.getResponsibilities()){
			newRoleResponsibility = new RoleResponsibilityImpl();
			KimCommonUtils.copyProperties(newRoleResponsibility, documentRoleResponsibility);
			newRoleResponsibility.setActive(documentRoleResponsibility.isActive());
			newRoleResponsibility.setRoleId(identityManagementRoleDocument.getRoleId());
			for(RoleResponsibilityImpl origResponsibilityImpl: origRoleResponsibilities){
				if(!origResponsibilityImpl.getRoleId().equals(newRoleResponsibility.getRoleId()) &&
						origResponsibilityImpl.getResponsibilityId().equals(newRoleResponsibility.getResponsibilityId()) &&
						!origResponsibilityImpl.isActive() && newRoleResponsibility.isActive()){
					newRoleResponsibility.setRoleResponsibilityId(origResponsibilityImpl.getRoleResponsibilityId());
				}
				if(origResponsibilityImpl.getRoleResponsibilityId().equals(newRoleResponsibility.getRoleResponsibilityId()))
					newRoleResponsibility.setVersionNumber(origResponsibilityImpl.getVersionNumber());
			}
			roleResponsibilities.add(newRoleResponsibility);
		}
		return roleResponsibilities;
	}


	protected List <RoleResponsibilityActionImpl> getRoleResponsibilitiesActions(
			IdentityManagementRoleDocument identityManagementRoleDocument){
		List <RoleResponsibilityActionImpl>  roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		List<KimDocumentRoleResponsibilityAction> documentRoleResponsibilityActions;
		RoleResponsibilityActionImpl roleRspAction;
		for(KimDocumentRoleResponsibility roleResponsibility: identityManagementRoleDocument.getResponsibilities()){
			if(!getResponsibilityService().areActionsAtAssignmentLevelById(roleResponsibility.getResponsibilityId())){
				documentRoleResponsibilityActions = roleResponsibility.getRoleRspActions();
				if(ObjectUtils.isNotNull(documentRoleResponsibilityActions) && !documentRoleResponsibilityActions.isEmpty() &&
					StringUtils.isNotBlank(documentRoleResponsibilityActions.get(0).getRoleResponsibilityActionId())){
					roleRspAction = new RoleResponsibilityActionImpl();
					roleRspAction.setRoleResponsibilityActionId(documentRoleResponsibilityActions.get(0).getRoleResponsibilityActionId());
					roleRspAction.setActionPolicyCode(documentRoleResponsibilityActions.get(0).getActionPolicyCode());
					roleRspAction.setActionTypeCode(documentRoleResponsibilityActions.get(0).getActionTypeCode());
					roleRspAction.setPriorityNumber(documentRoleResponsibilityActions.get(0).getPriorityNumber());
					roleRspAction.setIgnorePrevious(documentRoleResponsibilityActions.get(0).isIgnorePrevious());
					roleRspAction.setRoleMemberId(documentRoleResponsibilityActions.get(0).getRoleMemberId());
					roleRspAction.setRoleResponsibilityId(documentRoleResponsibilityActions.get(0).getRoleResponsibilityId());
					updateVersionNumbers(roleRspAction, getRoleResponsibilityActionImpls(roleResponsibility.getRoleResponsibilityId()));
					roleRspActions.add(roleRspAction);
				}
			}
		}
		return roleRspActions;
	}

	protected void updateVersionNumbers(RoleResponsibilityActionImpl newRoleRspAction,
			List<RoleResponsibilityActionImpl> origRoleRespActionImpls){
		for(RoleResponsibilityActionImpl origRoleResponsibilityActionImpl: origRoleRespActionImpls){
			if(origRoleResponsibilityActionImpl.getRoleResponsibilityActionId().equals(
					newRoleRspAction.getRoleResponsibilityActionId()))
				newRoleRspAction.setVersionNumber(origRoleResponsibilityActionImpl.getVersionNumber());
		}

	}

	protected List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActions(List<RoleMemberImpl> newRoleMembersList){
		List<RoleResponsibilityActionImpl> roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		for(RoleMemberImpl roleMember: newRoleMembersList){
			roleRspActions.addAll(roleMember.getRoleRspActions());
		}
		return roleRspActions;
	}

	protected List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		List<RoleResponsibilityActionImpl> roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		for(KimDocumentRoleMember roleMember: identityManagementRoleDocument.getMembers()){
			for(KimDocumentRoleResponsibilityAction roleRspAction : roleMember.getRoleRspActions()){
				RoleResponsibilityActionImpl entRoleRspAction = new RoleResponsibilityActionImpl();
				entRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
				entRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
				entRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
				entRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
				entRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
				entRoleRspAction.setIgnorePrevious(roleRspAction.isIgnorePrevious());
				entRoleRspAction.setRoleResponsibilityId(roleRspAction.getRoleResponsibilityId());
				List<RoleResponsibilityActionImpl> actions = getRoleRspActions(roleMember.getRoleId(), roleMember.getRoleMemberId());
				for(RoleResponsibilityActionImpl orgRspAction : actions) {
					if (orgRspAction.getRoleResponsibilityActionId().equals(roleRspAction.getRoleResponsibilityActionId())) {
						entRoleRspAction.setVersionNumber(orgRspAction.getVersionNumber());
					}
				}
				roleRspActions.add(entRoleRspAction);
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
		for(KimDocumentRoleMember documentRoleMember: identityManagementRoleDocument.getMembers()){
			newRoleMember = new RoleMemberImpl();
			KimCommonUtils.copyProperties(newRoleMember, documentRoleMember);
			newRoleMember.setRoleId(identityManagementRoleDocument.getRoleId());
			for(RoleMemberImpl origRoleMemberImpl: origRoleMembers){
				if(origRoleMemberImpl.getRoleId().equals(newRoleMember.getRoleId()) &&
						origRoleMemberImpl.getMemberId().equals(newRoleMember.getMemberId()) &&
						!origRoleMemberImpl.isActive()){
					//TODO: verify if you want to add  && newRoleMember.isActive() condition to if...
					newRoleMemberIdAssigned = newRoleMember.getRoleMemberId();
					newRoleMember.setRoleMemberId(origRoleMemberImpl.getRoleMemberId());
					activatingInactive = true;
				}
				if(origRoleMemberImpl.getRoleMemberId().equals(newRoleMember.getRoleMemberId())){
					newRoleMember.setVersionNumber(origRoleMemberImpl.getVersionNumber());
					origRoleMemberImplTemp = origRoleMemberImpl;
				}
			}
			origAttributes = (origRoleMemberImplTemp==null || origRoleMemberImplTemp.getAttributes()==null)?
								new ArrayList<RoleMemberAttributeDataImpl>():origRoleMemberImplTemp.getAttributes();
			newRoleMember.setAttributes(getRoleMemberAttributeData(documentRoleMember.getQualifiers(), origAttributes, activatingInactive, newRoleMemberIdAssigned));
			newRoleMember.setRoleRspActions(getRoleMemberResponsibilityActions(documentRoleMember, origRoleMemberImplTemp, activatingInactive, newRoleMemberIdAssigned));
			roleMembers.add(newRoleMember);
			activatingInactive = false;
		}
		return roleMembers;
	}

	protected List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActions(
			KimDocumentRoleMember documentRoleMember, RoleMemberImpl origRoleMemberImplTemp, boolean activatingInactive, String newRoleMemberIdAssigned){
		List<RoleResponsibilityActionImpl> roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
		List<RoleResponsibilityActionImpl> origActions = new ArrayList<RoleResponsibilityActionImpl>();
		if(origRoleMemberImplTemp!=null)
			origActions = getRoleRspActions(origRoleMemberImplTemp.getRoleId(), origRoleMemberImplTemp.getRoleMemberId());
		for(KimDocumentRoleResponsibilityAction roleRspAction : documentRoleMember.getRoleRspActions()){
			RoleResponsibilityActionImpl newRoleRspAction = new RoleResponsibilityActionImpl();
			newRoleRspAction.setRoleResponsibilityActionId(roleRspAction.getRoleResponsibilityActionId());
			newRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
			newRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
			newRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
			newRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
			newRoleRspAction.setIgnorePrevious(roleRspAction.isIgnorePrevious());
			newRoleRspAction.setRoleResponsibilityId(roleRspAction.getRoleResponsibilityId());
			for(RoleResponsibilityActionImpl origRspAction: origActions) {
				if(activatingInactive && origRspAction.getRoleResponsibilityId().equals(newRoleRspAction.getRoleResponsibilityId()) &&
						newRoleRspAction.getRoleMemberId().equals(newRoleMemberIdAssigned)){
					newRoleRspAction.setRoleMemberId(origRspAction.getRoleMemberId());
					newRoleRspAction.setRoleResponsibilityActionId(origRspAction.getRoleResponsibilityActionId());
				}
				if (origRspAction.getRoleResponsibilityActionId().equals(newRoleRspAction.getRoleResponsibilityActionId())) {
					newRoleRspAction.setVersionNumber(origRspAction.getVersionNumber());
				}
			}
			roleRspActions.add(newRoleRspAction);
		}
		return roleRspActions;
	}

	protected List<RoleMemberAttributeDataImpl> getRoleMemberAttributeData(List<KimDocumentRoleQualifier> qualifiers,
			List<RoleMemberAttributeDataImpl> origAttributes, boolean activatingInactive, String newRoleMemberIdAssigned){
		List<RoleMemberAttributeDataImpl> roleMemberAttributeDataList = new ArrayList<RoleMemberAttributeDataImpl>();
		RoleMemberAttributeDataImpl newRoleMemberAttributeData;
		for(KimDocumentRoleQualifier memberRoleQualifier: qualifiers){
			if(StringUtils.isNotBlank(memberRoleQualifier.getAttrVal())){
				newRoleMemberAttributeData = new RoleMemberAttributeDataImpl();
				newRoleMemberAttributeData.setAttributeDataId(memberRoleQualifier.getAttrDataId());
				newRoleMemberAttributeData.setAttributeValue(memberRoleQualifier.getAttrVal());
				newRoleMemberAttributeData.setTargetPrimaryKey(memberRoleQualifier.getTargetPrimaryKey());
				newRoleMemberAttributeData.setKimTypeId(memberRoleQualifier.getKimTypId());
				newRoleMemberAttributeData.setKimAttributeId(memberRoleQualifier.getKimAttrDefnId());
				for(RoleMemberAttributeDataImpl origAttribute: origAttributes){
					if(activatingInactive && origAttribute.getKimAttributeId().equals(newRoleMemberAttributeData.getKimAttributeId()) &&
							newRoleMemberAttributeData.getTargetPrimaryKey().equals(newRoleMemberIdAssigned)){
						newRoleMemberAttributeData.setTargetPrimaryKey(origAttribute.getTargetPrimaryKey());
						newRoleMemberAttributeData.setAttributeDataId(origAttribute.getAttributeDataId());
					}
					if(origAttribute.getAttributeDataId().equals(newRoleMemberAttributeData.getAttributeDataId())){
						newRoleMemberAttributeData.setVersionNumber(origAttribute.getVersionNumber());
					}
				}
				roleMemberAttributeDataList.add(newRoleMemberAttributeData);
			}
		}
		return roleMemberAttributeDataList;
	}

	protected List<KimDelegationImpl> getRoleDelegations(IdentityManagementRoleDocument identityManagementRoleDocument, List<KimDelegationImpl> origDelegations){
		List<KimDelegationImpl> kimDelegations = new ArrayList<KimDelegationImpl>();
		KimDelegationImpl newKimDelegation;
		KimDelegationImpl origDelegationImplTemp = null;
		List<KimDelegationMemberImpl> origMembers = new ArrayList<KimDelegationMemberImpl>();
		boolean activatingInactive = false;
		String newDelegationIdAssigned = "";
		for(RoleDocumentDelegation roleDocumentDelegation: identityManagementRoleDocument.getDelegations()){
			newKimDelegation = new KimDelegationImpl();
			KimCommonUtils.copyProperties(newKimDelegation, roleDocumentDelegation);
			newKimDelegation.setRoleId(identityManagementRoleDocument.getRoleId());
			for(KimDelegationImpl origDelegationImpl: origDelegations){
				if(origDelegationImpl.getRoleId().equals(newKimDelegation.getRoleId()) &&
						origDelegationImpl.getDelegationId().equals(newKimDelegation.getDelegationId())){
					//TODO: verify if you want to add  && newRoleMember.isActive() condition to if...
					newDelegationIdAssigned = newKimDelegation.getDelegationId();
					newKimDelegation.setDelegationId(origDelegationImpl.getDelegationId());
					activatingInactive = true;
				}
				if(origDelegationImpl.getDelegationId().equals(newKimDelegation.getDelegationId())){
					newKimDelegation.setVersionNumber(origDelegationImpl.getVersionNumber());
					origDelegationImplTemp = origDelegationImpl;
				}
			}
			origMembers = (origDelegationImplTemp==null || origDelegationImplTemp.getMembers()==null)?
								new ArrayList<KimDelegationMemberImpl>():origDelegationImplTemp.getMembers();
			newKimDelegation.setMembers(getDelegationMembers(roleDocumentDelegation.getMembers(), origMembers, activatingInactive, newDelegationIdAssigned));
			kimDelegations.add(newKimDelegation);
			activatingInactive = false;
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
		for(RoleDocumentDelegationMember delegationMember: delegationMembers){
			newDelegationMemberImpl = new KimDelegationMemberImpl();
			KimCommonUtils.copyProperties(newDelegationMemberImpl, delegationMember);
			for(KimDelegationMemberImpl origDelegationMember: origDelegationMembers){
				if(activatingInactive && origDelegationMember.getMemberId().equals(newDelegationMemberImpl.getMemberId()) &&
						newDelegationMemberImpl.getDelegationId().equals(newDelegationIdAssigned) &&
						!origDelegationMember.isActive()){
					newDelegationMemberImpl.setDelegationId(origDelegationMember.getDelegationId());
					delegationMemberId = newDelegationMemberImpl.getDelegationMemberId();
					newDelegationMemberImpl.setDelegationMemberId(origDelegationMember.getDelegationMemberId());
				}
				if(origDelegationMember.getDelegationMemberId().equals(newDelegationMemberImpl.getDelegationMemberId())){
					newDelegationMemberImpl.setVersionNumber(origDelegationMember.getVersionNumber());
					origDelegationMemberImplTemp = origDelegationMember;
				}
			}
			origAttributes = (origDelegationMemberImplTemp==null || origDelegationMemberImplTemp.getAttributes()==null)?
					new ArrayList<KimDelegationMemberAttributeDataImpl>():origDelegationMemberImplTemp.getAttributes();
			newDelegationMemberImpl.setAttributes(getDelegationMemberAttributeData(delegationMember.getQualifiers(), origAttributes, activatingInactive, delegationMemberId));
			delegationsMembersList.add(newDelegationMemberImpl);
		}
		return delegationsMembersList;
	}

	//TODO: implement logic same as role members - do not insert qualifiers with blank values
	protected List<KimDelegationMemberAttributeDataImpl> getDelegationMemberAttributeData(
			List<RoleDocumentDelegationMemberQualifier> qualifiers, List<KimDelegationMemberAttributeDataImpl> origAttributes,
			boolean activatingInactive, String delegationMemberId){
		List<KimDelegationMemberAttributeDataImpl> delegationMemberAttributeDataList = new ArrayList<KimDelegationMemberAttributeDataImpl>();
		KimDelegationMemberAttributeDataImpl newDelegationMemberAttributeData;
		for(RoleDocumentDelegationMemberQualifier memberRoleQualifier: qualifiers){
			if(StringUtils.isNotBlank(memberRoleQualifier.getAttrVal())){
				newDelegationMemberAttributeData = new KimDelegationMemberAttributeDataImpl();
				newDelegationMemberAttributeData.setAttributeDataId(memberRoleQualifier.getAttrDataId());
				newDelegationMemberAttributeData.setAttributeValue(memberRoleQualifier.getAttrVal());
				newDelegationMemberAttributeData.setTargetPrimaryKey(memberRoleQualifier.getTargetPrimaryKey());
				newDelegationMemberAttributeData.setKimTypeId(memberRoleQualifier.getKimTypId());
				newDelegationMemberAttributeData.setKimAttributeId(memberRoleQualifier.getKimAttrDefnId());
				for(KimDelegationMemberAttributeDataImpl origAttribute: origAttributes){
					if(activatingInactive && origAttribute.getKimAttributeId().equals(newDelegationMemberAttributeData.getKimAttributeId()) &&
							newDelegationMemberAttributeData.getTargetPrimaryKey().equals(delegationMemberId)){
						newDelegationMemberAttributeData.setTargetPrimaryKey(origAttribute.getTargetPrimaryKey());
						newDelegationMemberAttributeData.setAttributeDataId(origAttribute.getAttributeDataId());
					}
					if(origAttribute.getAttributeDataId().equals(newDelegationMemberAttributeData.getAttributeDataId())){
						newDelegationMemberAttributeData.setVersionNumber(origAttribute.getVersionNumber());
					}
				}
				delegationMemberAttributeDataList.add(newDelegationMemberAttributeData);
			}
		}
		return delegationMemberAttributeDataList;
	}

	/* Group document methods */
	public void loadGroupDoc(IdentityManagementGroupDocument identityManagementGroupDocument, KimGroup kimGroup){
		GroupInfo groupInfo = (GroupInfo)kimGroup;
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("groupId", groupInfo.getGroupId());
		KimGroupImpl kimGroupImpl = (KimGroupImpl)
			KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimGroupImpl.class, criteria);
		identityManagementGroupDocument.setGroupId(kimGroupImpl.getGroupId());
		identityManagementGroupDocument.setKimType(kimGroupImpl.getKimTypeImpl());
		identityManagementGroupDocument.setGroupTypeName(kimGroupImpl.getKimTypeImpl().getName());
		identityManagementGroupDocument.setGroupTypeId(kimGroupImpl.getKimTypeImpl().getKimTypeId());
		identityManagementGroupDocument.setGroupName(kimGroupImpl.getGroupName());
		identityManagementGroupDocument.setActive(kimGroupImpl.isActive());
		identityManagementGroupDocument.setGroupNamespace(kimGroupImpl.getNamespaceCode());
		identityManagementGroupDocument.setMembers(loadGroupMembers(identityManagementGroupDocument, kimGroupImpl.getMembers()));
		identityManagementGroupDocument.setQualifiers(loadGroupQualifiers(identityManagementGroupDocument, kimGroupImpl.getGroupAttributes()));
		identityManagementGroupDocument.setKimType(kimGroupImpl.getKimTypeImpl());
	}

	protected List<GroupDocumentMember> loadGroupMembers(
			IdentityManagementGroupDocument identityManagementGroupDocument, List<GroupMemberImpl> members){
		List<GroupDocumentMember> pndMembers = new ArrayList<GroupDocumentMember>();
		GroupDocumentMember pndMember = new GroupDocumentMember();
		for(GroupMemberImpl member: members){
			pndMember = new GroupDocumentMember();
			pndMember.setActiveFromDate(member.getActiveFromDate());
			pndMember.setActiveToDate(member.getActiveToDate());
			pndMember.setActive(member.isActive());
			if(pndMember.isActive()){
				pndMember.setGroupMemberId(member.getGroupMemberId());
				pndMember.setGroupId(member.getGroupId());
				pndMember.setMemberId(member.getMemberId());
				pndMember.setMemberName(getMemberName(member.getMemberTypeCode(), member.getMemberId()));
				pndMember.setMemberTypeCode(member.getMemberTypeCode());
				pndMember.setEdit(true);
				pndMembers.add(pndMember);
			}
		}
		return pndMembers;
	}

	protected List<GroupDocumentQualifier> loadGroupQualifiers(IdentityManagementGroupDocument IdentityManagementGroupDocument,
			List<GroupAttributeDataImpl> attributeDataList){
		List<GroupDocumentQualifier> pndGroupQualifiers = new ArrayList<GroupDocumentQualifier>();
		GroupDocumentQualifier pndGroupQualifier = new GroupDocumentQualifier();
		AttributeDefinitionMap origAttributes = IdentityManagementGroupDocument.getDefinitions();
		boolean attributePresent = false;
		String origAttributeId;
		for(String key: origAttributes.keySet()) {
			origAttributeId = IdentityManagementGroupDocument.getKimAttributeDefnId(origAttributes.get(key));
			for(GroupAttributeDataImpl groupQualifier: attributeDataList){
				if(origAttributeId.equals(groupQualifier.getKimAttribute().getKimAttributeId())){
					pndGroupQualifier = new GroupDocumentQualifier();
					KimCommonUtils.copyProperties(pndGroupQualifier, groupQualifier);
					pndGroupQualifiers.add(pndGroupQualifier);
					attributePresent = true;
				}
			}
			if(!attributePresent){
				pndGroupQualifier = new GroupDocumentQualifier();
				pndGroupQualifier.setKimAttrDefnId(origAttributeId);
				pndGroupQualifiers.add(pndGroupQualifier);
			}
			attributePresent = false;
		}
		return pndGroupQualifiers;
	}

	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	@SuppressWarnings("unchecked")
	public void saveGroup(IdentityManagementGroupDocument identityManagementGroupDocument) {
		KimGroupImpl kimGroup = new KimGroupImpl();
		Map<String, String> criteria = new HashMap<String, String>();
		String groupId = identityManagementGroupDocument.getGroupId();
		criteria.put("groupId", groupId);
		KimGroupImpl origGroup = (KimGroupImpl)getBusinessObjectService().findByPrimaryKey(KimGroupImpl.class, criteria);
		List<GroupMemberImpl> origGroupMembers = new ArrayList<GroupMemberImpl>();
		List<GroupAttributeDataImpl> origGroupAttributes = new ArrayList<GroupAttributeDataImpl>();
		if (ObjectUtils.isNull(origGroup)) {
			origGroup = new KimGroupImpl();
			kimGroup.setActive(true);
		} else {
			kimGroup.setActive(origGroup.isActive());
			kimGroup.setVersionNumber(origGroup.getVersionNumber());
			origGroupMembers = (List<GroupMemberImpl>)getBusinessObjectService().findMatching(GroupMemberImpl.class, criteria);
			origGroupAttributes = (List<GroupAttributeDataImpl>)getBusinessObjectService().findMatching(GroupAttributeDataImpl.class, criteria);
		}

		kimGroup.setGroupId(identityManagementGroupDocument.getGroupId());
		criteria = new HashMap<String, String>();
		criteria.put("kimTypeId", identityManagementGroupDocument.getGroupTypeId());
		KimTypeImpl typeImpl = (KimTypeImpl)getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria);
		if(typeImpl==null)
			throw new RuntimeException("Kim type not found for:"+identityManagementGroupDocument.getGroupTypeId());

		kimGroup.setKimTypeImpl(typeImpl);
		kimGroup.setKimTypeId(typeImpl.getKimTypeId());
		kimGroup.setNamespaceCode(identityManagementGroupDocument.getGroupNamespace());
		kimGroup.setGroupName(identityManagementGroupDocument.getGroupName());
		kimGroup.setGroupAttributes(getGroupAttributeData(identityManagementGroupDocument, kimGroup.getGroupAttributes()));

		List<String> oldIds = null;
		List<String> newIds = null;
		List<BusinessObject> bos = new ArrayList<BusinessObject>();
		oldIds = getGroupService().getMemberPrincipalIds(kimGroup.getGroupId()); // for the actionList update
		bos.add(kimGroup);
		List<GroupMemberImpl> newGroupMembersList = getGroupMembers(identityManagementGroupDocument, origGroupMembers);
		bos.addAll(newGroupMembersList);

		getBusinessObjectService().save(bos);
		newIds = getGroupService().getMemberPrincipalIds(kimGroup.getGroupId()); // for the action list update

		// Do an async update of the action list for the updated groups
		KIMServiceLocator.getGroupInternalService().updateForWorkgroupChange(kimGroup.getGroupId(), oldIds, newIds);
	}

	protected List<GroupMemberImpl> getGroupMembers(IdentityManagementGroupDocument identityManagementGroupDocument, List<GroupMemberImpl> origGroupMembers){
		List<GroupMemberImpl> groupMembers = new ArrayList<GroupMemberImpl>();
		GroupMemberImpl newGroupMember;
		for(GroupDocumentMember documentGroupMember: identityManagementGroupDocument.getMembers()){
			newGroupMember = new GroupMemberImpl();
			KimCommonUtils.copyProperties(newGroupMember, documentGroupMember);
			newGroupMember.setGroupId(identityManagementGroupDocument.getGroupId());
			for(GroupMemberImpl origGroupMemberImpl: origGroupMembers){
				if(origGroupMemberImpl.getGroupId().equals(newGroupMember.getGroupId()) &&
						origGroupMemberImpl.getMemberId().equals(newGroupMember.getMemberId()) &&
						!origGroupMemberImpl.isActive()){
					//TODO: verify if you want to add  && newGroupMember.isActive() condition to if...
					newGroupMember.setGroupMemberId(origGroupMemberImpl.getGroupMemberId());
				}
				if(origGroupMemberImpl.getGroupMemberId().equals(newGroupMember.getGroupMemberId())){
					newGroupMember.setVersionNumber(origGroupMemberImpl.getVersionNumber());
				}
			}
			groupMembers.add(newGroupMember);
		}
		return groupMembers;
	}

	protected List<GroupAttributeDataImpl> getGroupAttributeData(IdentityManagementGroupDocument identityManagementGroupDocument,
			List<GroupAttributeDataImpl> origAttributes){
		List<GroupAttributeDataImpl> groupAttributeDataList = new ArrayList<GroupAttributeDataImpl>();
		GroupAttributeDataImpl newGroupAttributeData;
		for(GroupDocumentQualifier groupQualifier: identityManagementGroupDocument.getQualifiers()){
			if(StringUtils.isNotBlank(groupQualifier.getAttrVal())){
				newGroupAttributeData = new GroupAttributeDataImpl();
				newGroupAttributeData.setAttributeDataId(groupQualifier.getAttrDataId());
				newGroupAttributeData.setAttributeValue(groupQualifier.getAttrVal());
				newGroupAttributeData.setTargetPrimaryKey(groupQualifier.getTargetPrimaryKey());
				newGroupAttributeData.setKimTypeId(groupQualifier.getKimTypId());
				newGroupAttributeData.setKimAttributeId(groupQualifier.getKimAttrDefnId());
				for(GroupAttributeDataImpl origAttribute: origAttributes){
					if(origAttribute.getKimAttributeId().equals(newGroupAttributeData.getKimAttributeId()) &&
							newGroupAttributeData.getTargetPrimaryKey().equals(origAttribute.getTargetPrimaryKey())){
						newGroupAttributeData.setAttributeDataId(origAttribute.getAttributeDataId());
					}
					if(origAttribute.getAttributeDataId().equals(newGroupAttributeData.getAttributeDataId())){
						newGroupAttributeData.setVersionNumber(origAttribute.getVersionNumber());
					}
				}
				groupAttributeDataList.add(newGroupAttributeData);
			}
		}
		return groupAttributeDataList;
	}

    public KimDocumentRoleMember getKimDocumentRoleMember(String memberTypeCode, String memberId, String roleId){
    	if(StringUtils.isEmpty(memberTypeCode) || StringUtils.isEmpty(memberId) || StringUtils.isEmpty(roleId))
    		return null;
    	KimDocumentRoleMember documentRoleMember = new KimDocumentRoleMember();
    	documentRoleMember.setRoleId(roleId);
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
    	criteria.put("mbr_id", memberId);

    	List matchingRoleMembers = (List)getBusinessObjectService().findMatching(RoleMemberImpl.class, criteria);
    	if(matchingRoleMembers==null || matchingRoleMembers.size()<1) return null;

    	RoleMemberImpl roleMemberImpl = (RoleMemberImpl)matchingRoleMembers.get(0);
    	documentRoleMember.setRoleMemberId(roleMemberImpl.getRoleMemberId());
    	BusinessObject member = getMember(memberTypeCode, memberId);
    	if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
    		documentRoleMember.setMemberId(((KimPrincipalImpl)member).getPrincipalId());
    		documentRoleMember.setMemberName(((KimPrincipalImpl)member).getPrincipalName());
    		documentRoleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	documentRoleMember.setMemberNamespaceCode(((KimGroupImpl)member).getNamespaceCode());
    		documentRoleMember.setMemberId(((KimGroupImpl)member).getGroupId());
    		documentRoleMember.setMemberName(((KimGroupImpl)member).getGroupName());
    		documentRoleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	documentRoleMember.setMemberNamespaceCode(((KimRoleImpl)member).getNamespaceCode());
    		documentRoleMember.setMemberId(((KimRoleImpl)member).getRoleId());
    		documentRoleMember.setMemberName(((KimRoleImpl)member).getRoleName());
    		documentRoleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
        }
    	return documentRoleMember;
    }

    private KimDelegationImpl getDelegationOfType(List<KimDelegationImpl> roleDelegations, String delegationTypeCode){
    	if(roleDelegations==null || StringUtils.isEmpty(delegationTypeCode))
    		return null;
		for(KimDelegationImpl delegation: roleDelegations){
			if(delegation.getDelegationTypeCode().equals(delegationTypeCode))
				return delegation;
		}
		return null;
    }

    public RoleDocumentDelegationMember getRoleDocumentDelegationMember(
    		String memberTypeCode, String memberId, String roleId, String delegationTypeCode){
    	if(StringUtils.isEmpty(memberTypeCode) || StringUtils.isEmpty(memberId) || StringUtils.isEmpty(roleId) || StringUtils.isEmpty(delegationTypeCode))
    		return null;
    	KimDelegationImpl delegation = getDelegationOfType(getRoleDelegations(roleId), delegationTypeCode);
    	if(delegation==null)
    		return null;
    	RoleDocumentDelegationMember delegationMember = new RoleDocumentDelegationMember();
    	delegationMember.setDelegationId(delegation.getDelegationId());

    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegation.getDelegationId());
    	criteria.put(KimConstants.PrimaryKeyConstants.MEMBER_ID, memberId);
    	List matchingDelegationMembers = (List)getBusinessObjectService().findMatching(KimDelegationMemberImpl.class, criteria);
    	if(matchingDelegationMembers==null || matchingDelegationMembers.size()<1) return null;

    	KimDelegationMemberImpl delegationMemberImpl = (KimDelegationMemberImpl)matchingDelegationMembers.get(0);
    	delegationMember.setDelegationMemberId(delegationMemberImpl.getRoleMemberId());
    	BusinessObject member = getMember(memberTypeCode, memberId);
    	if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
    		delegationMember.setMemberId(((KimPrincipalImpl)member).getPrincipalId());
    		delegationMember.setMemberName(((KimPrincipalImpl)member).getPrincipalName());
    		delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
        	delegationMember.setMemberNamespaceCode(((KimRoleImpl)member).getNamespaceCode());
    		delegationMember.setMemberId(((KimRoleImpl)member).getRoleId());
    		delegationMember.setMemberName(((KimRoleImpl)member).getRoleName());
    		delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
        	delegationMember.setMemberNamespaceCode(((KimGroupImpl)member).getNamespaceCode());
    		delegationMember.setMemberId(((KimGroupImpl)member).getGroupId());
    		delegationMember.setMemberName(((KimGroupImpl)member).getGroupName());
    		delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
        }
    	return delegationMember;

    }

    protected Set<String> getChangedRoleResponsibilityIds(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleResponsibilityImpl> origRoleResponsibilities){
		Set<String> lRet = new HashSet<String>();
		List<String> newResp = new ArrayList<String>();
		List<String> oldResp = new ArrayList<String>();

		for(KimDocumentRoleResponsibility documentRoleResponsibility: identityManagementRoleDocument.getResponsibilities()){
			newResp.add(documentRoleResponsibility.getResponsibilityId());
		}
		for(RoleResponsibilityImpl roleResp: origRoleResponsibilities){
			oldResp.add(roleResp.getResponsibilityId());
		}

		lRet.addAll(ListUtils.union(newResp, oldResp));

		return lRet;
	}
}