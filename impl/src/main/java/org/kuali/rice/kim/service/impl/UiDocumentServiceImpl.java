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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.EntityAddress;
import org.kuali.rice.kim.bo.entity.EntityEmail;
import org.kuali.rice.kim.bo.entity.EntityPhone;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.impl.EntityAddressImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityAffiliationImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityEmailImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityEmploymentInformationImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityEntityTypeImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityExternalIdentifierImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityPhoneImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityPrivacyPreferencesImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
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
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.service.support.impl.KimTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
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

	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	public void saveEntityPerson(
			IdentityManagementPersonDocument identityManagementPersonDocument) {
		KimEntityImpl kimEntity = new KimEntityImpl();
		KimEntityImpl origEntity = (KimEntityImpl)getIdentityService().getEntity(identityManagementPersonDocument.getEntityId());
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

		setupPrincipal(identityManagementPersonDocument, kimEntity, origEntity.getPrincipals());
		setupExtId(identityManagementPersonDocument, kimEntity, origEntity.getExternalIdentifiers());
		setupPrivacy(identityManagementPersonDocument, kimEntity, origEntity.getPrivacyPreferences());
		setupAffiliation(identityManagementPersonDocument, kimEntity, origEntity.getAffiliations(), origEntity.getEmploymentInformation());
		setupName(identityManagementPersonDocument, kimEntity, origEntity.getNames());
		// entitytype
		List<EntityEntityTypeImpl> entityTypes = new ArrayList<EntityEntityTypeImpl>();
		EntityEntityTypeImpl entityType = new EntityEntityTypeImpl();
		entityType.setEntityId(identityManagementPersonDocument.getEntityId());
		entityType.setEntityTypeCode(KimConstants.EntityTypes.PERSON);
		entityType.setActive(true);
		entityTypes.add(entityType);
		EntityEntityTypeImpl origEntityType = new EntityEntityTypeImpl();
		for (EntityEntityTypeImpl type : origEntity.getEntityTypes()) {
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
		bos.add(kimEntity);
		bos.add(kimEntity.getPrivacyPreferences());
		bos.addAll(groupPrincipals);
		bos.addAll(rolePrincipals);
		getBusinessObjectService().save(bos);

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
	 * @see org.kuali.rice.kim.service.UiDocumentService#loadEntityToPersonDoc(org.kuali.rice.kim.document.IdentityManagementPersonDocument, org.kuali.rice.kim.bo.entity.impl.KimEntityImpl)
	 */
	public void loadEntityToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId) {
        KimPrincipal principal = getIdentityService().getPrincipal(principalId);
        identityManagementPersonDocument.setPrincipalId(principal.getPrincipalId());
        identityManagementPersonDocument.setPrincipalName(principal.getPrincipalName());
        identityManagementPersonDocument.setPassword(principal.getPassword());
        identityManagementPersonDocument.setActive(principal.isActive());
		KimEntityImpl kimEntity = (KimEntityImpl)getIdentityService().getEntity(principal.getEntityId());
		identityManagementPersonDocument.setEntityId(kimEntity.getEntityId());
		//identityManagementPersonDocument.setActive(kimEntity.isActive());
		identityManagementPersonDocument.setAffiliations(loadAffiliations(kimEntity.getAffiliations(),kimEntity.getEmploymentInformation()));
		identityManagementPersonDocument.setNames(loadNames(kimEntity.getNames()));
		EntityEntityTypeImpl entityType = null;
		for (EntityEntityTypeImpl type : kimEntity.getEntityTypes()) {
			if (type.getEntityTypeCode().equals(KimConstants.EntityTypes.PERSON)) {
				entityType = type;
			}
		}

		for (EntityExternalIdentifierImpl extId : kimEntity.getExternalIdentifiers()){
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
	private void loadGroupToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, List<? extends KimGroup> groups) {
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
						if (groupMember.getMemberId().equals(identityManagementPersonDocument.getPrincipalId()) && groupMember.getMemberTypeCode().equals(KimGroupImpl.PRINCIPAL_MEMBER_TYPE)) {
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
	
	private void loadRoleToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument) {
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
	        	docRole.setRoleName(role.getKimRoleType().getName());
	        	docRole.setRolePrncpls(populateDocRolePrncpl(role.getMembers(), identityManagementPersonDocument.getPrincipalId()));
	        	docRoles.add(docRole);
	        	roleIds.add(role.getRoleId());
        	}
        }
        
        // TODO : this is odd way to get attributedefid hooked.  need to rework
		for (PersonDocumentRole role : docRoles) {
		    	String serviceName = role.getKimRoleType().getKimTypeServiceName();
		    	if (StringUtils.isBlank(serviceName)) {
		    		serviceName = "kimTypeService";
		    	}

	        KimTypeService kimTypeService = (KimTypeServiceBase)KIMServiceLocator.getService(serviceName);
			role.setDefinitions(kimTypeService.getAttributeDefinitions(role.getKimRoleType()));
			// TODO : refactor qualifier key to connect between defn & qualifier
//        	for (KimDocumentRoleMember principal : role.getRolePrncpls()) {
//        		for (KimDocumentRoleQualifier qualifier : principal.getQualifiers()) {
//    		        for (KimTypeAttributeImpl attrDef : role.getKimRoleType().getAttributeDefinitions()) {
//    		        	if (qualifier.getKimAttrDefnId().equals(attrDef.getKimAttributeId())) {
//    		        		qualifier.setQualifierKey(attrDef.getSortCode());
//    		        	}
//    		        }
//        			
//        		}
//        	}
        	// when post again, it will need this during populate
            role.setNewRolePrncpl(new KimDocumentRoleMember());
            for (String key : role.getDefinitions().keySet()) {
            	KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
            	//qualifier.setQualifierKey(key);
            	setAttrDefnIdForQualifier(qualifier,role.getDefinitions().get(key));
            	role.getNewRolePrncpl().getQualifiers().add(qualifier);
            }
            role.setAttributeEntry( getAttributeEntries( role.getDefinitions() ) );
		}
        //
        
        identityManagementPersonDocument.setRoles(docRoles);
	}
		
    private void setAttrDefnIdForQualifier(KimDocumentRoleQualifier qualifier,AttributeDefinition definition) {
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		qualifier.setKimAttrDefnId(((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId());
    		qualifier.refreshReferenceObject("kimAttribute");
    	} else {
    		qualifier.setKimAttrDefnId(((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId());
    		qualifier.refreshReferenceObject("kimAttribute");    		
    	}
    }

    @SuppressWarnings("unchecked")
	private List<KimRoleImpl> getRolesForPrincipal(String principalId) {
		if ( principalId == null ) {
			return new ArrayList<KimRoleImpl>();
		}
		Map<String,String> criteria = new HashMap<String,String>( 2 );
		criteria.put("members.memberId", principalId);
		criteria.put("members.memberTypeCode", KimRoleImpl.PRINCIPAL_MEMBER_TYPE);
		return (List<KimRoleImpl>)getBusinessObjectService().findMatching(KimRoleImpl.class, criteria);
	}

    private List<KimDocumentRoleMember> populateDocRolePrncpl(List <RoleMemberImpl> roleMembers, String principalId) {
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
         		docRolePrncpl.setQualifiers(populateDocRoleQualifier(rolePrincipal.getAttributes()));
         		docRolePrncpl.setEdit(true);
        		docRoleMembers.add(docRolePrncpl);
    		 }
    	}
    	return docRoleMembers;
    }
    
    // UI layout for rolequalifier is a little different from kimroleattribute set up.
    // each principal may have member with same role multiple times with different qualifier, but the role
    // only displayed once, and the qualifier displayed multiple times.
    private List<KimDocumentRoleQualifier> populateDocRoleQualifier(List <RoleMemberAttributeDataImpl> qualifiers) {
		List <KimDocumentRoleQualifier> docRoleQualifiers = new ArrayList <KimDocumentRoleQualifier>();
		for (RoleMemberAttributeDataImpl qualifier : qualifiers) {
    		KimDocumentRoleQualifier docRoleQualifier = new KimDocumentRoleQualifier();
    		docRoleQualifier.setAttrDataId(qualifier.getAttributeDataId());
    		docRoleQualifier.setAttrVal(qualifier.getAttributeValue());
    		docRoleQualifier.setKimAttrDefnId(qualifier.getKimAttributeId());
    		docRoleQualifier.setKimAttribute(qualifier.getKimAttribute());
    		docRoleQualifier.setKimTypId(qualifier.getKimTypeId());
    		docRoleQualifier.setTargetPrimaryKey(qualifier.getTargetPrimaryKey());
    		docRoleQualifier.setEdit(true);
    		docRoleQualifiers.add(docRoleQualifier);
		}
    	return docRoleQualifiers;
    }

	private List<PersonDocumentName> loadNames(List <EntityNameImpl> names) {
		List<PersonDocumentName> docNames = new ArrayList<PersonDocumentName>();
		for (EntityNameImpl name : names) {
			PersonDocumentName docName = new PersonDocumentName();
			docName.setNameTypeCode(name.getNameTypeCode());
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
	
	private List<PersonDocumentAffiliation> loadAffiliations(List <EntityAffiliationImpl> affiliations, List<EntityEmploymentInformationImpl> empInfos) {
		List<PersonDocumentAffiliation> docAffiliations = new ArrayList<PersonDocumentAffiliation>();
		for (EntityAffiliationImpl affiliation : affiliations) {
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
			for (EntityEmploymentInformationImpl empInfo : empInfos) {
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
				docEmploymentInformations.add(docEmpInfo);
				}
			}
			docAffiliation.setEmpInfos(docEmploymentInformations);
		}
		return docAffiliations;

	}
	
	private void setupPrincipal(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<KimPrincipalImpl> origPrincipals) {
		List<KimPrincipalImpl> principals = new ArrayList<KimPrincipalImpl>();
		KimPrincipalImpl principal = new KimPrincipalImpl();
		principal.setPrincipalId(identityManagementPersonDocument.getPrincipalId());
		principal.setPrincipalName(identityManagementPersonDocument.getPrincipalName());
		principal.setPassword(identityManagementPersonDocument.getPassword());
		principal.setActive(identityManagementPersonDocument.isActive());
		for (KimPrincipalImpl prncpl : origPrincipals) {
			if (prncpl.getPrincipalId().equals(principal.getPrincipalId())) {
				principal.setVersionNumber(prncpl.getVersionNumber());
			}
		}
		principals.add(principal);
		
		kimEntity.setPrincipals(principals);

	}
	
	private void setupExtId(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<EntityExternalIdentifierImpl> origExtIds) {
		List<EntityExternalIdentifierImpl> extIds = new ArrayList<EntityExternalIdentifierImpl>();
		EntityExternalIdentifierImpl extId = new EntityExternalIdentifierImpl();
		extId.setEntityId(identityManagementPersonDocument.getEntityId());
		extId.setExternalId(identityManagementPersonDocument.getTaxId());
		extId.setExternalIdentifierTypeCode(KimConstants.PersonExternalIdentifierTypes.TAX);
		for (EntityExternalIdentifierImpl origExtId : origExtIds) {
			if (origExtId.getExternalIdentifierTypeCode().equals(extId.getExternalIdentifierTypeCode())) {
				extId.setVersionNumber(origExtId.getVersionNumber());
			}
		}
		extIds.add(extId);
		kimEntity.setExternalIdentifiers(extIds);

	}
	
	private void setupPrivacy(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, EntityPrivacyPreferencesImpl origPrivacy) {
		EntityPrivacyPreferencesImpl privacyPreferences = new EntityPrivacyPreferencesImpl();
		privacyPreferences.setEntityId(identityManagementPersonDocument.getEntityId());
		privacyPreferences.setSuppressAddress(identityManagementPersonDocument.getPrivacy().isSuppressAddress());
		privacyPreferences.setSuppressEmail(identityManagementPersonDocument.getPrivacy().isSuppressEmail());
		privacyPreferences.setSuppressName(identityManagementPersonDocument.getPrivacy().isSuppressName());
		privacyPreferences.setSuppressPhone(identityManagementPersonDocument.getPrivacy().isSuppressPhone());
		privacyPreferences
				.setSuppressPersonal(identityManagementPersonDocument.getPrivacy().isSuppressPersonal());
		if (origPrivacy != null) {
			privacyPreferences.setVersionNumber(origPrivacy.getVersionNumber());
		}
		kimEntity.setPrivacyPreferences(privacyPreferences);
	}
	private PersonDocumentPrivacy loadPrivacyReferences(EntityPrivacyPreferencesImpl privacyPreferences) {
		PersonDocumentPrivacy docPrivacy = new PersonDocumentPrivacy();
		docPrivacy.setSuppressAddress(privacyPreferences.isSuppressAddress());
		docPrivacy.setSuppressEmail(privacyPreferences.isSuppressEmail());
		docPrivacy.setSuppressName(privacyPreferences.isSuppressName());
		docPrivacy.setSuppressPhone(privacyPreferences.isSuppressPhone());
		docPrivacy.setSuppressPersonal(privacyPreferences.isSuppressPersonal());
		docPrivacy.setEdit(true);
		return docPrivacy;
	}
	
	private void setupName(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity, List<EntityNameImpl> origNames) {
		List<EntityNameImpl> entityNames = new ArrayList<EntityNameImpl>();
		for (PersonDocumentName name : identityManagementPersonDocument.getNames()) {
			EntityNameImpl entityName = new EntityNameImpl();
			entityName.setNameTypeCode(name.getNameTypeCode());
			entityName.setFirstName(name.getFirstName());
			entityName.setLastName(name.getLastName());
			entityName.setMiddleName(name.getMiddleName());
			entityName.setTitle(name.getTitle());
			entityName.setSuffix(name.getSuffix());
			entityName.setActive(name.isActive());
			entityName.setDefault(name.isDflt());
			entityName.setEntityNameId(name.getEntityNameId());
			for (EntityNameImpl origName : origNames) {
				if (origName.getEntityNameId().equals(entityName.getEntityNameId())) {
					entityName.setVersionNumber(origName.getVersionNumber());
				}
				
			}
			entityNames.add(entityName);
		}
		kimEntity.setNames(entityNames);

	}
	
	private void setupAffiliation(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity,List<EntityAffiliationImpl> origAffiliations, List<EntityEmploymentInformationImpl> origEmpInfos) {
		List<EntityAffiliationImpl> entityAffiliations = new ArrayList<EntityAffiliationImpl>();
		for (PersonDocumentAffiliation affiliation : identityManagementPersonDocument.getAffiliations()) {
			EntityAffiliationImpl entityAffiliation = new EntityAffiliationImpl();
			entityAffiliation.setAffiliationTypeCode(affiliation
					.getAffiliationTypeCode());
			entityAffiliation.setCampusCode(affiliation.getCampusCode());
			entityAffiliation.setActive(affiliation.isActive());
			entityAffiliation.setDefault(affiliation.isDflt());
			entityAffiliation.setEntityAffiliationId(affiliation
					.getEntityAffiliationId());
			// EntityAffiliationImpl does not define empinfos as collection
			for (EntityAffiliationImpl origAffiliation : origAffiliations) {
				if (origAffiliation.getEntityAffiliationId().equals(entityAffiliation.getEntityAffiliationId())) {
					entityAffiliation.setVersionNumber(origAffiliation.getVersionNumber());
				}
			}
			entityAffiliations.add(entityAffiliation);

			// employment informations
			List<EntityEmploymentInformationImpl> entityEmploymentInformations = new ArrayList<EntityEmploymentInformationImpl>();
			for (PersonDocumentEmploymentInfo empInfo : affiliation
					.getEmpInfos()) {
				EntityEmploymentInformationImpl entityEmpInfo = new EntityEmploymentInformationImpl();
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
				for (EntityEmploymentInformationImpl origEmpInfo : origEmpInfos) {
					if (origEmpInfo.getEntityEmploymentId().equals(entityEmpInfo.getEntityEmploymentId())) {
						entityEmpInfo.setVersionNumber(origEmpInfo.getVersionNumber());
					}
				}
				entityEmploymentInformations.add(entityEmpInfo);
			}
			kimEntity
					.setEmploymentInformation(entityEmploymentInformations);

		}
		kimEntity.setAffiliations(entityAffiliations);
	}
	
	private void setupPhone(IdentityManagementPersonDocument identityManagementPersonDocument, EntityEntityTypeImpl entityType, List<EntityPhone> origPhones) {
		List<EntityPhone> entityPhones = new ArrayList<EntityPhone>();
		for (PersonDocumentPhone phone : identityManagementPersonDocument.getPhones()) {
			EntityPhoneImpl entityPhone = new EntityPhoneImpl();
			entityPhone.setPhoneTypeCode(phone.getPhoneTypeCode());
			entityPhone.setEntityId(identityManagementPersonDocument.getEntityId());
			entityPhone.setEntityPhoneId(phone.getEntityPhoneId());
			entityPhone.setEntityTypeCode(entityType.getEntityTypeCode());
			entityPhone.setPhoneNumber(phone.getPhoneNumber());
			entityPhone.setExtension(phone.getExtension());
			entityPhone.setExtensionNumber(phone.getExtensionNumber());
			entityPhone.setActive(phone.isActive());
			entityPhone.setDefault(phone.isDflt());
			for (EntityPhone origPhone : origPhones) {
				if (origPhone.getEntityPhoneId().equals(entityPhone.getEntityPhoneId())) {
					entityPhone.setVersionNumber(((EntityPhoneImpl)origPhone).getVersionNumber());
				}
			}
			entityPhone.setEntityPhoneId(phone.getEntityPhoneId());
			entityPhones.add(entityPhone);
		}
		entityType.setPhoneNumbers(entityPhones);

	}

	private List<PersonDocumentPhone> loadPhones(List<EntityPhone> entityPhones) {
		List<PersonDocumentPhone> docPhones = new ArrayList<PersonDocumentPhone>();
		for (EntityPhone phone : entityPhones) {
			PersonDocumentPhone docPhone = new PersonDocumentPhone();
			docPhone.setPhoneTypeCode(phone.getPhoneTypeCode());
			docPhone.setEntityTypeCode(phone.getEntityTypeCode());
			docPhone.setPhoneNumber(phone.getPhoneNumber());
			docPhone.setExtensionNumber(phone.getExtensionNumber());
			docPhone.setActive(phone.isActive());
			docPhone.setDflt(phone.isDefault());
			docPhone.setEntityPhoneId(phone.getEntityPhoneId());
			docPhone.setEdit(true);
			docPhones.add(docPhone);
		}
		return  docPhones;

	}

	private void setupEmail(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			EntityEntityTypeImpl entityType, List<EntityEmail> origEmails) {
		List<EntityEmail> entityEmails = new ArrayList<EntityEmail>();
		for (PersonDocumentEmail email : identityManagementPersonDocument
				.getEmails()) {
			EntityEmailImpl entityEmail = new EntityEmailImpl();
			entityEmail.setEntityId(identityManagementPersonDocument
					.getEntityId());
			entityEmail.setEntityTypeCode(entityType.getEntityTypeCode());
			entityEmail.setEmailTypeCode(email.getEmailTypeCode());
			entityEmail.setEmailAddress(email.getEmailAddress());
			entityEmail.setActive(email.isActive());
			entityEmail.setDefault(email.isDflt());
			entityEmail.setEntityEmailId(email.getEntityEmailId());
			for (EntityEmail origEmail : origEmails) {
				if (origEmail.getEntityEmailId().equals(entityEmail.getEntityEmailId())) {
					entityEmail.setVersionNumber(((EntityEmailImpl)origEmail).getVersionNumber());
				}
			}
			entityEmails.add(entityEmail);
		}
		entityType.setEmailAddresses(entityEmails);
	}
	private List<PersonDocumentEmail> loadEmails(List<EntityEmail> entityEmais) {
		List<PersonDocumentEmail> emails = new ArrayList<PersonDocumentEmail>();
		for (EntityEmail email : entityEmais) {
			PersonDocumentEmail docEmail = new PersonDocumentEmail();
			//docEmail.setEntityId(email.getEntityId());
			docEmail.setEntityTypeCode(email.getEntityTypeCode());
			docEmail.setEmailTypeCode(email.getEmailTypeCode());
			docEmail.setEmailAddress(email.getEmailAddress());
			docEmail.setActive(email.isActive());
			docEmail.setDflt(email.isDefault());
			docEmail.setEntityEmailId(email.getEntityEmailId());
			docEmail.setEdit(true);
			emails.add(docEmail);
		}
		return emails;
	}
	
	private void setupAddress(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			EntityEntityTypeImpl entityType, List<EntityAddress> origAddresses) {
		List<EntityAddress> entityAddresses = new ArrayList<EntityAddress>();
		for (PersonDocumentAddress address : identityManagementPersonDocument
				.getAddrs()) {
			EntityAddressImpl entityAddress = new EntityAddressImpl();
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
			for (EntityAddress origAddress : origAddresses) {
				if (origAddress.getEntityAddressId().equals(entityAddress.getEntityAddressId())) {
					entityAddress.setVersionNumber(((EntityAddressImpl)origAddress).getVersionNumber());
				}
			}
			entityAddresses.add(entityAddress);
		}
		entityType.setAddresses(entityAddresses);
	}
	
	private  List<PersonDocumentAddress> loadAddresses(List<EntityAddress> entityAddresses) {
		List<PersonDocumentAddress> docAddresses = new ArrayList<PersonDocumentAddress>();
		for (EntityAddress address : entityAddresses) {
			PersonDocumentAddress docAddress = new PersonDocumentAddress();
			docAddress.setEntityTypeCode(address.getEntityTypeCode());
			docAddress.setAddressTypeCode(address.getAddressTypeCode());
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

	private List <GroupMemberImpl> populateGroups(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List <GroupMemberImpl>  groupPrincipals = new ArrayList<GroupMemberImpl>();
		List<? extends KimGroup> origGroups = getGroupService().getGroupsForPrincipal(identityManagementPersonDocument.getPrincipalId());
		for (PersonDocumentGroup group : identityManagementPersonDocument.getGroups()) {
			GroupMemberImpl groupPrincipalImpl = new GroupMemberImpl();
			groupPrincipalImpl.setGroupId(group.getGroupId());
			groupPrincipalImpl.setActiveFromDate(group.getActiveFromDate());
			groupPrincipalImpl.setActiveToDate(group.getActiveToDate());
			groupPrincipalImpl.setGroupMemberId(group.getGroupMemberId());
			// TODO : principalId is not ready here yet ?
			groupPrincipalImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
			groupPrincipalImpl.setMemberTypeCode(KimGroupImpl.PRINCIPAL_MEMBER_TYPE);
			List<String> groupIds = new ArrayList<String>();
			groupIds.add(group.getGroupId());
			for (GroupMembershipInfo groupMember : getGroupService().getGroupMembers(groupIds)) {
				if (groupMember.getMemberId().equals(identityManagementPersonDocument.getPrincipalId()) && groupMember.getMemberTypeCode().equals(KimGroupImpl.PRINCIPAL_MEMBER_TYPE)) {
					groupPrincipalImpl.setVersionNumber(groupMember.getVersionNumber());
				}
			}

			groupPrincipals.add(groupPrincipalImpl);
			
		}
		return groupPrincipals;
	}

	private List <RoleMemberImpl> populateRoles(IdentityManagementPersonDocument identityManagementPersonDocument) {
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
	
						attributes.add(attribute);
					}
					rolePrincipalImpl.setAttributes(attributes);
					rolePrincipals.add(rolePrincipalImpl);
				}
			}
		}
		return rolePrincipals;

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


	/* Role document methods */
	public void loadRoleDoc(IdentityManagementRoleDocument identityManagementRoleDocument, KimRole kimRole){
		KimRoleInfo kimRoleInfo = (KimRoleInfo)kimRole;
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("roleId", kimRoleInfo.getRoleId());
		KimRoleImpl kimRoleImpl = (KimRoleImpl)
			KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimRoleImpl.class, criteria);
		identityManagementRoleDocument.setRoleId(kimRoleImpl.getRoleId());
		identityManagementRoleDocument.setRoleName(kimRoleImpl.getRoleName());
		identityManagementRoleDocument.setActive(kimRoleImpl.isActive());
		identityManagementRoleDocument.setRoleNamespace(kimRoleImpl.getNamespaceCode());
		identityManagementRoleDocument.setMembers(loadMembers(identityManagementRoleDocument, kimRoleImpl.getMembers()));
		identityManagementRoleDocument.setPermissions(kimRoleImpl.getAssignedPermissions());
		identityManagementRoleDocument.setResponsibilities(kimRoleImpl.getAssignedResponsibilities());
		identityManagementRoleDocument.setDelegations(loadRoleDocumentDelegations(getRoleDelegations(kimRoleImpl.getRoleId())));
		identityManagementRoleDocument.setKimType(kimRoleImpl.getKimRoleType());
		//Delegations
	}

	private List<KimDocumentRoleMember> loadMembers(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleMemberImpl> members){
		List<KimDocumentRoleMember> pndMembers = new ArrayList<KimDocumentRoleMember>();
		KimDocumentRoleMember pndMember = new KimDocumentRoleMember();
		for(RoleMemberImpl member: members){
			pndMember = new KimDocumentRoleMember();
			pndMember.setRoleMemberId(member.getMemberId());
			pndMember.setRoleId(member.getRoleId());
			pndMember.setMemberId(member.getMemberId());
			pndMember.setMemberName(getMemberName(member));
			pndMember.setMemberTypeCode(member.getMemberTypeCode());
			pndMember.setActiveFromDate(member.getActiveFromDate());
			pndMember.setActiveToDate(member.getActiveToDate());
			pndMember.setActive(member.isActive());
			pndMember.setQualifiers(loadRoleMemberQualifiers(member.getAttributes()));
			pndMembers.add(pndMember);
		}
		return pndMembers;
	}

	private List loadRoleMemberQualifiers(List<RoleMemberAttributeDataImpl> attributeDataList){
		List<KimDocumentRoleQualifier> pndMemberRoleQualifiers = new ArrayList<KimDocumentRoleQualifier>();
		KimDocumentRoleQualifier pndMemberRoleQualifier = new KimDocumentRoleQualifier();
		for(RoleMemberAttributeDataImpl memberRoleQualifier: attributeDataList){
			pndMemberRoleQualifier = new KimDocumentRoleQualifier();
			pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getAttributeDataId());
			pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
			pndMemberRoleQualifier.setTargetPrimaryKey(memberRoleQualifier.getTargetPrimaryKey());
			pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
			pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
			pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
		}
		return pndMemberRoleQualifiers;
	}

	private List<KimDocumentRoleQualifier> loadDelegationQualifiers(List<KimDelegationAttributeDataImpl> attributeDataList){
		List<KimDocumentRoleQualifier> pndDelegationQualifiers = new ArrayList<KimDocumentRoleQualifier>();
		KimDocumentRoleQualifier pndDelegationQualifier = new KimDocumentRoleQualifier();
		for(KimDelegationAttributeDataImpl delegationQualifier: attributeDataList){
			pndDelegationQualifier = new KimDocumentRoleQualifier();
			pndDelegationQualifier.setAttrDataId(delegationQualifier.getAttributeDataId());
			pndDelegationQualifier.setAttrVal(delegationQualifier.getAttributeValue());
			pndDelegationQualifier.setTargetPrimaryKey(delegationQualifier.getTargetPrimaryKey());
			pndDelegationQualifier.setKimTypId(delegationQualifier.getKimTypeId());
			pndDelegationQualifier.setKimAttrDefnId(delegationQualifier.getKimAttributeId());
			pndDelegationQualifiers.add(pndDelegationQualifier);
		}
		return pndDelegationQualifiers;
	}

	private List<KimDelegationImpl> getRoleDelegations(String roleId){
		if(roleId==null)
			return new ArrayList<KimDelegationImpl>();
		Map<String,String> criteria = new HashMap<String,String>(1);
		criteria.put("roleId", roleId);
		return (List<KimDelegationImpl>)getBusinessObjectService().findMatching(KimDelegationImpl.class, criteria);	
	}
	
	private List<RoleDocumentDelegation> loadRoleDocumentDelegations(List<KimDelegationImpl> delegations){
		List<RoleDocumentDelegation> delList = new ArrayList<RoleDocumentDelegation>();
		RoleDocumentDelegation documentDelegation;
		for(KimDelegationImpl del: delegations){
			documentDelegation = new RoleDocumentDelegation();
			documentDelegation.setActive(del.isActive());
			documentDelegation.setDelegationId(del.getDelegationId());
			documentDelegation.setDelegationTypeCode(del.getDelegationTypeCode());
			documentDelegation.setKimType(del.getKimType());
			documentDelegation.setKimTypeId(del.getKimTypeId());
			documentDelegation.setQualifiers(loadDelegationQualifiers(del.getAttributes()));
			documentDelegation.setRoleId(del.getRoleId());
			delList.add(documentDelegation);
		}
		return delList;
	}
	
	//TODO: remove this and find a better way to do this. Should be done by next week with role doc task
	//Also, inject services
	private String getMemberName(RoleMemberImpl member){
		if("P".equals(member.getMemberTypeCode())){
			KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipal(member.getMemberId());
			return principal==null?"":principal.getPrincipalName();
		} else if("G".equals(member.getMemberTypeCode())){
			KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroup(member.getMemberId());
			return group==null?"":group.getGroupName();
		} else if("R".equals(member.getMemberTypeCode())){
			KimRoleInfo role = KIMServiceLocator.getRoleService().getRole(member.getMemberId());
			return role==null?"":role.getRoleName();
		}
		return "";
	}
	
	/**
	 * @return the permissionService
	 */
	public PermissionService getPermissionService() {
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
		return this.roleService;
	}

	/**
	 * @param roleService the roleService to set
	 */
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

}
