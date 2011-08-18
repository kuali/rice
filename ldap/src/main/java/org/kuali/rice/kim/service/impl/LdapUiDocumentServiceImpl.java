/*
 * Copyright 2010 The Kuali Foundation
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

import static org.kuali.rice.core.util.BufferedLogger.*;
import static org.kuali.rice.kim.util.KimConstants.EntityTypes.PERSON;
import static org.kuali.rice.kim.util.KimConstants.PersonExternalIdentifierTypes.TAX;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.parameter.Parameter;
import org.kuali.rice.core.api.uif.RemotableCheckboxGroup;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.IdentityManagementNotificationService;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.address.EntityAddressContract;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.email.EntityEmailContract;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.api.identity.phone.EntityPhoneContract;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
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
import org.kuali.rice.kim.framework.services.KimFrameworkServiceLocator;
import org.kuali.rice.kim.framework.type.KimTypeService;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberAttributeDataBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.group.GroupAttributeBo;
import org.kuali.rice.kim.impl.group.GroupBo;
import org.kuali.rice.kim.impl.group.GroupMemberBo;
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo;
import org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationTypeBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentTypeBo;
import org.kuali.rice.kim.impl.identity.entity.EntityBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameTypeBo;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneBo;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneTypeBo;
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo;
import org.kuali.rice.kim.impl.identity.privacy.EntityPrivacyPreferencesBo;
import org.kuali.rice.kim.impl.identity.type.EntityTypeContactInfoBo;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityInternalService;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RoleMemberAttributeDataBo;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.kim.impl.role.RolePermissionBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityActionBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.kim.impl.services.KIMServiceLocatorInternal;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.datadictionary.exporter.AttributesMapBuilder;
import org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceHelper;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.exporter.ExportMap;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentHelperService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;
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
 * Customized version of the UiDocumentServiceImpl to support LDAP communcation
 *
 * @author Leo Przybylski (przybyls@arizona.edu)
 */
public class LdapUiDocumentServiceImpl extends org.kuali.rice.kim.service.impl.UiDocumentServiceImpl {

	/**
	 *
	 * @see org.kuali.rice.kim.service.UiDocumentService#loadEntityToPersonDoc(IdentityManagementPersonDocument, String)
	 */
	public void loadEntityToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId) {
		Principal principal = this.getIdentityService().getPrincipal(principalId);
        if(principal==null) {
        	throw new RuntimeException("Principal does not exist for principal id:"+principalId);
        }

        identityManagementPersonDocument.setPrincipalId(principal.getPrincipalId());
        identityManagementPersonDocument.setPrincipalName(principal.getPrincipalName());
        identityManagementPersonDocument.setPassword(principal.getPassword());
        identityManagementPersonDocument.setActive(principal.isActive());
        Entity kimEntity = this.getIdentityService().getEntity(principal.getEntityId());
		identityManagementPersonDocument.setEntityId(kimEntity.getId());
		if ( ObjectUtils.isNotNull( kimEntity.getPrivacyPreferences() ) ) {
			identityManagementPersonDocument.setPrivacy(loadPrivacyReferences(kimEntity.getPrivacyPreferences()));
		}
		//identityManagementPersonDocument.setActive(kimEntity.isActive());
		identityManagementPersonDocument.setAffiliations(loadAffiliations(kimEntity.getAffiliations(),kimEntity.getEmploymentInformation()));
		identityManagementPersonDocument.setNames(loadNames( identityManagementPersonDocument, principalId, kimEntity.getNames(), identityManagementPersonDocument.getPrivacy().isSuppressName() ));
		EntityTypeContactInfo entityType = null;
		for (EntityTypeContactInfo type : kimEntity.getEntityTypeContactInfos()) {
			if (KimConstants.EntityTypes.PERSON.equals(type.getEntityTypeCode())) {
				entityType = EntityTypeContactInfo.Builder.create(type).build();
			}
		}

		if(entityType!=null){
			identityManagementPersonDocument.setEmails(loadEmails(identityManagementPersonDocument, principalId, entityType.getEmailAddresses(), identityManagementPersonDocument.getPrivacy().isSuppressEmail()));
			identityManagementPersonDocument.setPhones(loadPhones(identityManagementPersonDocument, principalId, entityType.getPhoneNumbers(), identityManagementPersonDocument.getPrivacy().isSuppressPhone()));
			identityManagementPersonDocument.setAddrs(loadAddresses(identityManagementPersonDocument, principalId, entityType.getAddresses(), identityManagementPersonDocument.getPrivacy().isSuppressAddress()));
		}

		List<Group> groups = getGroupService().getGroups(getGroupService().getDirectGroupIdsForPrincipal(identityManagementPersonDocument.getPrincipalId()));
		loadGroupToPersonDoc(identityManagementPersonDocument, groups);
		loadRoleToPersonDoc(identityManagementPersonDocument);
		loadDelegationsToPersonDoc(identityManagementPersonDocument);
	}

	protected String getInitiatorPrincipalId(Document document){
		try{
			return document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
		} catch(Exception ex){
			return null;
		}
	}

	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
    public void saveEntityPerson(IdentityManagementPersonDocument identityManagementPersonDocument) {
		EntityBo kimEntity = new EntityBo();
		EntityBo origEntity = getEntityBo(identityManagementPersonDocument.getEntityId());
		boolean creatingNew = true;
		if (origEntity == null) {
			origEntity = new EntityBo();
			kimEntity.setActive(true);
		} else {
			// TODO : in order to resolve optimistic locking issue. has to get identity and set the version number if identity records matched
			// Need to look into this.
			//kimEntity = origEntity;
			kimEntity.setActive(origEntity.isActive());
			kimEntity.setVersionNumber(origEntity.getVersionNumber());
			creatingNew = false;
		}

		kimEntity.setId(identityManagementPersonDocument.getEntityId());
		String initiatorPrincipalId = getInitiatorPrincipalId(identityManagementPersonDocument);
		boolean inactivatingPrincipal = false;
		if(canModifyEntity(initiatorPrincipalId, identityManagementPersonDocument.getPrincipalId())){
			inactivatingPrincipal = setupPrincipal(identityManagementPersonDocument, kimEntity, origEntity.getPrincipals());
			setupAffiliation(identityManagementPersonDocument, kimEntity, origEntity.getAffiliations(), origEntity.getEmploymentInformation());
			setupName(identityManagementPersonDocument, kimEntity, origEntity.getNames());
		// entitytype
			List<EntityTypeContactInfoBo> entityTypes = new ArrayList<EntityTypeContactInfoBo>();
			EntityTypeContactInfoBo entityType = new EntityTypeContactInfoBo();
			entityType.setEntityId(identityManagementPersonDocument.getEntityId());
			entityType.setEntityTypeCode(KimConstants.EntityTypes.PERSON);
			entityType.setActive(true);
			entityTypes.add(entityType);
			EntityTypeContactInfoBo origEntityType = new EntityTypeContactInfoBo();
			for (EntityTypeContactInfoBo type : origEntity.getEntityTypeContactInfos()) {
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
            kimEntity.setEntityTypeContactInfos(entityTypes);
		} else{
			if(ObjectUtils.isNotNull(origEntity.getExternalIdentifiers())) {
                kimEntity.setExternalIdentifiers(origEntity.getExternalIdentifiers());
            }
			if(ObjectUtils.isNotNull(origEntity.getEmploymentInformation())) {
                kimEntity.setEmploymentInformation(origEntity.getEmploymentInformation());
            }
			if(ObjectUtils.isNotNull(origEntity.getAffiliations())) {
                kimEntity.setAffiliations(origEntity.getAffiliations());
            }
			if(ObjectUtils.isNotNull(origEntity.getNames())) {
                kimEntity.setNames(origEntity.getNames());
            }
			if(ObjectUtils.isNotNull(origEntity.getEntityTypeContactInfos())) {
                kimEntity.setEntityTypeContactInfos(origEntity.getEntityTypeContactInfos());
            }
		}
		if(creatingNew || canOverrideEntityPrivacyPreferences(getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId())) {
			setupPrivacy(identityManagementPersonDocument, kimEntity, origEntity.getPrivacyPreferences());
		} else {
			if(ObjectUtils.isNotNull(origEntity.getPrivacyPreferences())) {
				kimEntity.setPrivacyPreferences(origEntity.getPrivacyPreferences());
			}
		}
		List <GroupMemberBo>  groupPrincipals = populateGroupMembers(identityManagementPersonDocument);
		List <RoleMemberBo>  rolePrincipals = populateRoleMembers(identityManagementPersonDocument);
		List <DelegateBo> personDelegations = populateDelegations(identityManagementPersonDocument);
		List <PersistableBusinessObject> bos = new ArrayList<PersistableBusinessObject>();
		List <RoleResponsibilityActionBo> roleRspActions = populateRoleRspActions(identityManagementPersonDocument);
		List <RoleMemberAttributeDataBo> blankRoleMemberAttrs = getBlankRoleMemberAttrs(rolePrincipals);
		bos.add(kimEntity);
		//if(ObjectUtils.isNotNull(kimEntity.getPrivacyPreferences()))
		//	bos.add(kimEntity.getPrivacyPreferences());
		bos.addAll(groupPrincipals);
		bos.addAll(rolePrincipals);
		bos.addAll(roleRspActions);
		bos.addAll(personDelegations);
		// boservice.save(bos) does not handle deleteawarelist
		getBusinessObjectService().save(bos);

		//KimApiServiceLocator.getIdentityService().flushEntityPrincipalCaches();
		IdentityManagementNotificationService service = (IdentityManagementNotificationService) KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(new QName(
                KimApiConstants.Namespaces.KIM_NAMESPACE_2_0, "identityManagementNotificationServiceSoap"));
		service.principalUpdated();

		if (!blankRoleMemberAttrs.isEmpty()) {
			getBusinessObjectService().delete(blankRoleMemberAttrs);
		}
		if ( inactivatingPrincipal ) {
			//when a person is inactivated, inactivate their group, role, and delegation memberships
			KimApiServiceLocator.getRoleService().principalInactivated(identityManagementPersonDocument.getPrincipalId());
		}
	}

    protected boolean setupPrincipal(IdentityManagementPersonDocument identityManagementPersonDocument,EntityBo kimEntity, List<PrincipalBo> origPrincipals) {
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

	protected List<PersonDocumentAffiliation> loadAffiliations(List <EntityAffiliation> affiliations, List<EntityEmployment> empInfos) {
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
						for (EntityEmployment empInfo: empInfos) {
							if (empInfo.isActive()
                                    && StringUtils.equals(docAffiliation.getEntityAffiliationId(),
                                                          (empInfo.getEntityAffiliation() != null ? empInfo.getEntityAffiliation().getId() : null))) {
								PersonDocumentEmploymentInfo docEmpInfo = new PersonDocumentEmploymentInfo();
								docEmpInfo.setEntityEmploymentId(empInfo.getEmployeeId());
								docEmpInfo.setEmployeeId(empInfo.getEmployeeId());
								docEmpInfo.setEmploymentRecordId(empInfo.getEmploymentRecordId());
								docEmpInfo.setBaseSalaryAmount(empInfo.getBaseSalaryAmount());
								docEmpInfo.setPrimaryDepartmentCode(empInfo.getPrimaryDepartmentCode());
								docEmpInfo.setEmploymentStatusCode(empInfo.getEmployeeStatus() != null ? empInfo.getEmployeeStatus().getCode() : null);
								docEmpInfo.setEmploymentTypeCode(empInfo.getEmployeeType() != null ? empInfo.getEmployeeType().getCode() : null);
								docEmpInfo.setActive(empInfo.isActive());
								docEmpInfo.setPrimary(empInfo.isPrimary());
								docEmpInfo.setEntityAffiliationId(empInfo.getEntityAffiliation() != null ? empInfo.getEntityAffiliation().getId() : null);
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

    public BusinessObject getMember(String memberTypeCode, String memberId){
        Class<? extends BusinessObject> roleMemberTypeClass = null;
        String roleMemberIdName = "";
    	if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
        	roleMemberTypeClass = PrincipalBo.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.PRINCIPAL_ID;
	 	 	Principal principalInfo = getIdentityService().getPrincipal(memberId);
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
        	roleMemberTypeClass = RoleBo.class;
        	roleMemberIdName = KimConstants.PrimaryKeyConstants.ROLE_ID;
	 	 	Role role = getRoleService().getRole(memberId);
	 	 	if (role != null) {
	 	 		
	 	 	}
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(roleMemberIdName, memberId);
        return getBusinessObjectService().findByPrimaryKey(roleMemberTypeClass, criteria);
    }

    /**
     * Overridden to only check permission - users should not be able to edit themselves.
     * 
     * KFSI-974/KITT-662
     * @see org.kuali.rice.kim.service.impl.UiDocumentServiceImpl#canModifyEntity(java.lang.String, java.lang.String)
     */
    @Override
	public boolean canModifyEntity( String currentUserPrincipalId, String toModifyPrincipalId ){
		return (StringUtils.isNotBlank(currentUserPrincipalId) && StringUtils.isNotBlank(toModifyPrincipalId) &&
				currentUserPrincipalId.equals(toModifyPrincipalId)) ||
				getPermissionService().isAuthorized(
						currentUserPrincipalId,
						KimConstants.NAMESPACE_CODE,
						KimConstants.PermissionNames.MODIFY_ENTITY,
						null,
						Collections.singletonMap(KimConstants.AttributeConstants.PRINCIPAL_ID, currentUserPrincipalId));
	}
    
    protected List<RoleMemberBo> getRoleMembers(IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleMemberBo> origRoleMembers){
        List<RoleMemberBo> roleMembers = new ArrayList<RoleMemberBo>();
        RoleMemberBo newRoleMember;
        RoleMemberBo origRoleMemberImplTemp;
        List<RoleMemberAttributeDataBo> origAttributes;
        boolean activatingInactive = false;
        String newRoleMemberIdAssigned = "";

        identityManagementRoleDocument.setKimType(KimApiServiceLocator.getKimTypeInfoService().getKimType(identityManagementRoleDocument.getRoleTypeId()));
        KimTypeService kimTypeService = KimFrameworkServiceLocator.getKimTypeService(identityManagementRoleDocument.getKimType());

        if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getMembers())){
            for(KimDocumentRoleMember documentRoleMember: identityManagementRoleDocument.getMembers()){
                origRoleMemberImplTemp = null;

                newRoleMember = new RoleMemberBo();
                KimCommonUtilsInternal.copyProperties(newRoleMember, documentRoleMember);
                newRoleMember.setRoleId(identityManagementRoleDocument.getRoleId());
                if(ObjectUtils.isNotNull(origRoleMembers)){
                    for(RoleMemberBo origRoleMemberImpl: origRoleMembers){
                        if((origRoleMemberImpl.getRoleId()!=null && StringUtils.equals(origRoleMemberImpl.getRoleId(), newRoleMember.getRoleId())) &&
                            (origRoleMemberImpl.getMemberId()!=null && StringUtils.equals(origRoleMemberImpl.getMemberId(), newRoleMember.getMemberId())) &&
                            (origRoleMemberImpl.getMemberTypeCode()!=null && StringUtils.equals(origRoleMemberImpl.getMemberTypeCode(), newRoleMember.getMemberTypeCode())) &&
                            !origRoleMemberImpl.isActive(new Timestamp(System.currentTimeMillis())) &&
                            !kimTypeService.validateAttributesAgainstExisting(identityManagementRoleDocument.getKimType().getId(),
                                    documentRoleMember.getQualifierAsMap(), origRoleMemberImpl.getAttributes()).isEmpty()) {

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
                                    new ArrayList<RoleMemberAttributeDataBo>():origRoleMemberImplTemp.getAttributeDetails();
                newRoleMember.setActiveFromDateValue(documentRoleMember.getActiveFromDate());
                newRoleMember.setActiveToDateValue(documentRoleMember.getActiveToDate());
                newRoleMember.setAttributeDetails(getRoleMemberAttributeData(documentRoleMember.getQualifiers(), origAttributes, activatingInactive, newRoleMemberIdAssigned));
                newRoleMember.setRoleRspActions(getRoleMemberResponsibilityActions(documentRoleMember, origRoleMemberImplTemp, activatingInactive, newRoleMemberIdAssigned));
                roleMembers.add(newRoleMember);
                activatingInactive = false;
            }
        }
        return roleMembers;
    }
}
