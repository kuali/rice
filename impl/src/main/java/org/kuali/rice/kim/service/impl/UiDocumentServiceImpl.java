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

import org.kuali.rice.kim.bo.entity.EntityAddress;
import org.kuali.rice.kim.bo.entity.EntityEmail;
import org.kuali.rice.kim.bo.entity.EntityPhone;
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
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentPrivacy;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.bo.ui.PersonDocumentRolePrncpl;
import org.kuali.rice.kim.bo.ui.PersonDocumentRoleQualifier;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.control.TextControlDefinition;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class UiDocumentServiceImpl implements UiDocumentService {

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.UiDocumentService#getKimEntity(org.kuali.rice.kim.document.IdentityManagementPersonDocument)
	 */
	public void saveEntityPerson(
			IdentityManagementPersonDocument identityManagementPersonDocument) {
		KimEntityImpl kimEntity = new KimEntityImpl();
		kimEntity.setActive(identityManagementPersonDocument.isActive());
		kimEntity.setEntityId(identityManagementPersonDocument.getEntityId());

		// principal
		setupPrincipal(identityManagementPersonDocument, kimEntity);
		setupExtId(identityManagementPersonDocument, kimEntity);
		
		// privacy references
		setupPrivacy(identityManagementPersonDocument, kimEntity);

		// affiliations
		setupAffiliation(identityManagementPersonDocument, kimEntity);

		// names
		setupName(identityManagementPersonDocument, kimEntity);
		// entitytype
		List<EntityEntityTypeImpl> entityTypes = new ArrayList<EntityEntityTypeImpl>();
		EntityEntityTypeImpl entityType = new EntityEntityTypeImpl();
		entityType.setEntityId(identityManagementPersonDocument.getEntityId());
		entityType.setEntityTypeCode("PERSON");
		entityType.setActive(identityManagementPersonDocument.isActive());
		entityTypes.add(entityType);
		kimEntity.setEntityTypes(entityTypes);

		// phones
		setupPhone(identityManagementPersonDocument, entityType);

		// emails
		setupEmail(identityManagementPersonDocument, entityType);

		// address
		setupAddress(identityManagementPersonDocument, entityType);

		// group memeber
		List <GroupMemberImpl>  groupPrincipals = new ArrayList<GroupMemberImpl>();
		for (PersonDocumentGroup group : identityManagementPersonDocument.getGroups()) {
			GroupMemberImpl groupPrincipalImpl = new GroupMemberImpl();
			groupPrincipalImpl.setGroupId(group.getGroupId());
			// TODO : principalId is not ready here yet ?
			groupPrincipalImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
			groupPrincipalImpl.setMemberTypeCode("P");
			groupPrincipals.add(groupPrincipalImpl);
			
		}
		
		List <RoleMemberImpl>  rolePrincipals = new ArrayList<RoleMemberImpl>();
		for (PersonDocumentRole role : identityManagementPersonDocument.getRoles()) {
			for (PersonDocumentRolePrncpl principal : role.getRolePrncpls()) {
				RoleMemberImpl rolePrincipalImpl = new RoleMemberImpl();
				rolePrincipalImpl.setRoleId(role.getRoleId());
				// TODO : principalId is not ready here yet ?
				rolePrincipalImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
				rolePrincipalImpl.setMemberTypeCode("P");
				rolePrincipalImpl.setRoleMemberId(principal.getRoleMemberId());
				List<RoleMemberAttributeDataImpl> attributes = new ArrayList<RoleMemberAttributeDataImpl>();
				for (PersonDocumentRoleQualifier qualifier : principal.getQualifiers()) {
					RoleMemberAttributeDataImpl attribute = new RoleMemberAttributeDataImpl();
					attribute.setAttributeDataId(qualifier.getAttrDataId());
					attribute.setAttributeValue(qualifier.getAttrVal());
					attribute.setKimAttributeId(qualifier.getKimAttrDefnId());
					attribute.setTargetPrimaryKey(qualifier.getTargetPrimaryKey());
					attribute.setKimTypeId(qualifier.getKimTypId());
					attributes.add(attribute);
				}
				rolePrincipalImpl.setAttributes(attributes);
				rolePrincipals.add(rolePrincipalImpl);
			}
		}
		List <BusinessObject> bos = new ArrayList<BusinessObject>();
		bos.add(kimEntity);
		bos.add(kimEntity.getPrivacyPreferences());
		bos.addAll(groupPrincipals);
		bos.addAll(rolePrincipals);
		KNSServiceLocator.getBusinessObjectService().save(bos);

	}

	public void setAttributeEntry(PersonDocumentRole personDocRole) {
        for (String key : personDocRole.getDefinitions().keySet()) {
			AttributeDefinition attrDefinition = personDocRole.getDefinitions().get(key);
			Map attribute = new HashMap();
			if (attrDefinition instanceof KimDataDictionaryAttributeDefinition) {
				AttributeDefinition definition = ((KimDataDictionaryAttributeDefinition) attrDefinition)
						.getDataDictionaryAttributeDefinition();
				attribute.put("control", definition.getControl());
				attribute.put("label", definition.getLabel());
				attribute.put("shortLabel", definition.getShortLabel());
				attribute.put("maxLength", definition.getMaxLength());
				attribute.put("required", definition.isRequired());
				personDocRole.getAttributeEntry().put(definition.getName(),attribute);
			} else {
				TextControlDefinition control = new TextControlDefinition();
				control.setSize(10);
				attribute.put("control", control);
				attribute.put("label", attrDefinition.getLabel());
				attribute.put("maxLength", 20);
				attribute.put("required", true);
				attribute.put("shortLabel", attrDefinition.getLabel());
				personDocRole.getAttributeEntry().put(attrDefinition.getName(),attribute);
			}
		}
	}


	public void loadEntityToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity) {
		identityManagementPersonDocument.setEntityId(kimEntity.getEntityId());
		
		identityManagementPersonDocument.setAffiliations(loadAffiliations(kimEntity.getAffiliations(),kimEntity.getEmploymentInformation()));
		identityManagementPersonDocument.setNames(loadNames(kimEntity.getNames()));
		EntityEntityTypeImpl entityType = null;
		for (EntityEntityTypeImpl type : kimEntity.getEntityTypes()) {
			if (type.getEntityTypeCode().equals("PERSON")) {
				entityType = type;
			}
		}
		for (EntityExternalIdentifierImpl extId : kimEntity.getExternalIdentifiers()){
			if (extId.getExternalIdentifierTypeCode().equals("TAX")) {
				identityManagementPersonDocument.setTaxId(extId.getExternalId());				
			} else if (extId.getExternalIdentifierTypeCode().equals("LOGON")) {
				identityManagementPersonDocument.setUnivId(extId.getExternalId());				
			}
		}
		identityManagementPersonDocument.setEmails(loadEmails(entityType.getEmailAddresses()));
		identityManagementPersonDocument.setPhones(loadPhones(entityType.getPhoneNumbers()));
		identityManagementPersonDocument.setAddrs(loadAddresses(entityType.getAddresses()));
		if (kimEntity.getPrivacyPreferences() != null) {
			identityManagementPersonDocument.setPrivacy(loadPrivacyReferences(kimEntity.getPrivacyPreferences()));
		}
	}
	
	public void loadGroupToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, List<? extends KimGroup> groups) {
		List <PersonDocumentGroup> docGroups = new ArrayList <PersonDocumentGroup>();
		for (KimGroup group : groups) {
			for (String memberId : KIMServiceLocator.getGroupService().getDirectMemberPrincipalIds(group.getGroupId())) {
				// other more direct methods for this ?
				if (memberId.equals(identityManagementPersonDocument.getPrincipalId())) {
					PersonDocumentGroup docGroup = new PersonDocumentGroup();
					docGroup.setGroupId(group.getGroupId());
					docGroup.setGroupName(group.getGroupName());
					docGroup.setPrincipalId(memberId);
					docGroups.add(docGroup);
				}
			}
		}
		identityManagementPersonDocument.setGroups(docGroups);
	}
	
	private List<PersonDocumentName> loadNames(List <EntityNameImpl> names) {
		List<PersonDocumentName> docNames = new ArrayList<PersonDocumentName>();
		for (EntityNameImpl name : names) {
			PersonDocumentName docName = new PersonDocumentName();
			docName.setNameTypeCode(name.getNameTypeCode());
			docName.setFirstName(name.getFirstName());
			docName.setLastName(name.getLastName());
			docName.setMiddleName(name.getMiddleName());
			docName.setActive(name.isActive());
			docName.setDflt(name.isDefault());
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
			// EntityAffiliationImpl does not define empinfos as collection
			docAffiliations.add(docAffiliation);

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
				docEmpInfo.setEmployeeStatusCode(empInfo
						.getEmployeeStatusCode());
				docEmpInfo.setEmployeeTypeCode(empInfo
						.getEmployeeTypeCode());
				docEmpInfo.setActive(empInfo.isActive());
				docEmpInfo.setEntityAffiliationId(empInfo
						.getEntityAffiliationId());
				docEmpInfo.setVersionNumber(empInfo.getVersionNumber());
				docEmploymentInformations.add(docEmpInfo);
				}
			}
			docAffiliation.setEmpInfos(docEmploymentInformations);
		}
		return docAffiliations;

	}
	
	private void setupPrincipal(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity) {
		List<KimPrincipalImpl> principals = new ArrayList<KimPrincipalImpl>();
		KimPrincipalImpl principal = new KimPrincipalImpl();
		principal.setPrincipalId(identityManagementPersonDocument.getPrincipalId());
		principal.setPrincipalName(identityManagementPersonDocument.getPrincipalName());
		principal.setPassword(identityManagementPersonDocument.getPassword());
		principal.setActive(identityManagementPersonDocument.isActive());
		principals.add(principal);
		kimEntity.setPrincipals(principals);

	}
	
	private void setupExtId(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity) {
		List<EntityExternalIdentifierImpl> extIds = new ArrayList<EntityExternalIdentifierImpl>();
		EntityExternalIdentifierImpl extId = new EntityExternalIdentifierImpl();
		extId.setEntityId(identityManagementPersonDocument.getEntityId());
		extId.setExternalId(identityManagementPersonDocument.getTaxId());
		extId.setExternalIdentifierTypeCode("TAX");
		extIds.add(extId);
		extId = new EntityExternalIdentifierImpl();
		extId.setEntityId(identityManagementPersonDocument.getEntityId());
		extId.setExternalId(identityManagementPersonDocument.getUnivId());
		extId.setExternalIdentifierTypeCode("LOGON");
		extIds.add(extId);
		kimEntity.setExternalIdentifiers(extIds);

	}
	
	private void setupPrivacy(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity) {
		EntityPrivacyPreferencesImpl privacyPreferences = new EntityPrivacyPreferencesImpl();
		privacyPreferences.setEntityId(identityManagementPersonDocument.getEntityId());
		privacyPreferences.setSuppressAddress(identityManagementPersonDocument.getPrivacy().isSuppressAddress());
		privacyPreferences.setSuppressEmail(identityManagementPersonDocument.getPrivacy().isSuppressEmail());
		privacyPreferences.setSuppressName(identityManagementPersonDocument.getPrivacy().isSuppressName());
		privacyPreferences.setSuppressPhone(identityManagementPersonDocument.getPrivacy().isSuppressPhone());
		privacyPreferences
				.setSuppressPersonal(identityManagementPersonDocument.getPrivacy().isSuppressPersonal());
		kimEntity.setPrivacyPreferences(privacyPreferences);
	}
	private PersonDocumentPrivacy loadPrivacyReferences(EntityPrivacyPreferencesImpl privacyPreferences) {
		PersonDocumentPrivacy docPrivacy = new PersonDocumentPrivacy();
		docPrivacy.setSuppressAddress(privacyPreferences.isSuppressAddress());
		docPrivacy.setSuppressEmail(privacyPreferences.isSuppressEmail());
		docPrivacy.setSuppressName(privacyPreferences.isSuppressName());
		docPrivacy.setSuppressPhone(privacyPreferences.isSuppressPhone());
		docPrivacy.setSuppressPersonal(privacyPreferences.isSuppressPersonal());
		return docPrivacy;
	}
	
	private void setupName(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity) {
		List<EntityNameImpl> entityNames = new ArrayList<EntityNameImpl>();
		for (PersonDocumentName name : identityManagementPersonDocument.getNames()) {
			EntityNameImpl entityName = new EntityNameImpl();
			entityName.setNameTypeCode(name.getNameTypeCode());
			entityName.setFirstName(name.getFirstName());
			entityName.setLastName(name.getLastName());
			entityName.setMiddleName(name.getMiddleName());
			entityName.setActive(name.isActive());
			entityName.setDefault(name.isDflt());
			entityName.setEntityNameId(name.getEntityNameId());
			entityNames.add(entityName);
		}
		kimEntity.setNames(entityNames);

	}
	
	private void setupAffiliation(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity) {
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
				entityEmpInfo.setEmployeeStatusCode(empInfo
						.getEmployeeStatusCode());
				entityEmpInfo.setEmployeeTypeCode(empInfo
						.getEmployeeTypeCode());
				entityEmpInfo.setActive(empInfo.isActive());
				entityEmpInfo.setEntityId(identityManagementPersonDocument.getEntityId());
				entityEmpInfo.setEntityAffiliationId(empInfo
						.getEntityAffiliationId());
				entityEmploymentInformations.add(entityEmpInfo);
			}
			kimEntity
					.setEmploymentInformation(entityEmploymentInformations);

		}
		kimEntity.setAffiliations(entityAffiliations);
	}
	
	private void setupPhone(IdentityManagementPersonDocument identityManagementPersonDocument, EntityEntityTypeImpl entityType) {
		List<EntityPhone> entityPhones = new ArrayList<EntityPhone>();
		for (PersonDocumentPhone phone : identityManagementPersonDocument.getPhones()) {
			EntityPhoneImpl entityPhone = new EntityPhoneImpl();
			entityPhone.setPhoneTypeCode(phone.getPhoneTypeCode());
			entityPhone.setEntityId(identityManagementPersonDocument.getEntityId());
			entityPhone.setEntityTypeCode(entityType.getEntityTypeCode());
			entityPhone.setPhoneNumber(phone.getPhoneNumber());
			entityPhone.setExtension(phone.getExtension());
			entityPhone.setExtensionNumber(phone.getExtensionNumber());
			entityPhone.setActive(phone.isActive());
			entityPhone.setDefault(phone.isDflt());
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
			docPhones.add(docPhone);
		}
		return  docPhones;

	}

	private void setupEmail(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			EntityEntityTypeImpl entityType) {
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
			emails.add(docEmail);
		}
		return emails;
	}
	
	private void setupAddress(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			EntityEntityTypeImpl entityType) {
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
			docAddresses.add(docAddress);
		}
		return docAddresses;
	}

}
