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
package org.kuali.rice.kim.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kim.bo.entity.EntityAffiliation;
import org.kuali.rice.kim.bo.entity.EntityBioDemographics;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentCitizenship;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentPrivacy;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.bo.ui.PersonDocumentRolePrncpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.TransactionalDocumentBase;

/**
 * This is a description of what this class does - shyu don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class IdentityManagementPersonDocument extends TransactionalDocumentBase {

	// principal data
	protected String principalId;
	protected String principalName;
	protected String entityId;
	protected String password;
	// ext id - now hard coded for "tax id" & "univ id"
	protected String taxId = "";
	protected String univId = "";
	// affiliation data
	protected List<PersonDocumentAffiliation> affiliations;
	protected EntityAffiliation defaultAffiliation;

	protected String campusCode = "";
	// external identifier data
	protected Map<String, String> externalIdentifiers = null;

	protected boolean active;

	// bio
	protected List<? extends EntityBioDemographics> bios;
	// citizenship
	protected List<PersonDocumentCitizenship> citizenships;
	// protected List<DocEmploymentInfo> employmentInformations;
	protected List<PersonDocumentName> names;
	protected List<PersonDocumentAddress> addrs;
	protected List<PersonDocumentPhone> phones;
	protected List<PersonDocumentEmail> emails;
	protected List<PersonDocumentGroup> groups;
	protected List<PersonDocumentRole> roles;
	protected PersonDocumentPrivacy privacy;

	public IdentityManagementPersonDocument() {
		affiliations = new ArrayList<PersonDocumentAffiliation>();
		citizenships = new ArrayList<PersonDocumentCitizenship>();
		// employmentInformations = new ArrayList<DocEmploymentInfo>();
		names = new ArrayList<PersonDocumentName>();
		addrs = new ArrayList<PersonDocumentAddress>();
		phones = new ArrayList<PersonDocumentPhone>();
		emails = new ArrayList<PersonDocumentEmail>();
		groups = new ArrayList<PersonDocumentGroup>();
		roles = new ArrayList<PersonDocumentRole>();
		privacy = new PersonDocumentPrivacy();
		// privacy.setDocumentNumber(documentNumber);
	}

	public String getPrincipalId() {
		return this.principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	public String getPrincipalName() {
		return this.principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public List<PersonDocumentAffiliation> getAffiliations() {
		return this.affiliations;
	}

	public void setAffiliations(List<PersonDocumentAffiliation> affiliations) {
		this.affiliations = affiliations;
	}

	public EntityAffiliation getDefaultAffiliation() {
		return this.defaultAffiliation;
	}

	public void setDefaultAffiliation(EntityAffiliation defaultAffiliation) {
		this.defaultAffiliation = defaultAffiliation;
	}

	public String getCampusCode() {
		return this.campusCode;
	}

	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}

	public Map<String, String> getExternalIdentifiers() {
		return this.externalIdentifiers;
	}

	public void setExternalIdentifiers(Map<String, String> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}

	public List<? extends EntityBioDemographics> getBios() {
		return this.bios;
	}

	public void setBios(List<? extends EntityBioDemographics> bios) {
		this.bios = bios;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	// public List<DocEmploymentInfo> getEmploymentInformations() {
	// return this.employmentInformations;
	// }
	//
	// public void setEmploymentInformations(
	// List<DocEmploymentInfo> employmentInformations) {
	// this.employmentInformations = employmentInformations;
	// }

	public List<PersonDocumentCitizenship> getCitizenships() {
		return this.citizenships;
	}

	public void setCitizenships(List<PersonDocumentCitizenship> citizenships) {
		this.citizenships = citizenships;
	}

	public List<PersonDocumentName> getNames() {
		return this.names;
	}

	public void setNames(List<PersonDocumentName> names) {
		this.names = names;
	}

	public List<PersonDocumentAddress> getAddrs() {
		return this.addrs;
	}

	public void setAddrs(List<PersonDocumentAddress> addrs) {
		this.addrs = addrs;
	}

	public List<PersonDocumentPhone> getPhones() {
		return this.phones;
	}

	public void setPhones(List<PersonDocumentPhone> phones) {
		this.phones = phones;
	}

	public List<PersonDocumentEmail> getEmails() {
		return this.emails;
	}

	public void setEmails(List<PersonDocumentEmail> emails) {
		this.emails = emails;
	}

	public void setGroups(List<PersonDocumentGroup> groups) {
		this.groups = groups;
	}

	public List<PersonDocumentRole> getRoles() {
		return this.roles;
	}

	public void setRoles(List<PersonDocumentRole> roles) {
		this.roles = roles;
	}

	public List<PersonDocumentGroup> getGroups() {
		return this.groups;
	}

	public String getTaxId() {
		return this.taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public String getUnivId() {
		return this.univId;
	}

	public void setUnivId(String univId) {
		this.univId = univId;
	}

	public PersonDocumentPrivacy getPrivacy() {
		return this.privacy;
	}

	public void setPrivacy(PersonDocumentPrivacy privacy) {
		this.privacy = privacy;
	}

	@Override
	public List buildListOfDeletionAwareLists() {
		List managedLists = super.buildListOfDeletionAwareLists();
		List<PersonDocumentEmploymentInfo> empInfos = new ArrayList<PersonDocumentEmploymentInfo>();
		for (PersonDocumentAffiliation affiliation : getAffiliations()) {
			empInfos.addAll(affiliation.getEmpInfos());
		}

		managedLists.add(empInfos);
		managedLists.add(getAffiliations());
		managedLists.add(getCitizenships());
		return managedLists;
	}

	@Override
	public void doRouteStatusChange(
			DocumentRouteStatusChangeDTO statusChangeEvent) throws Exception {
		// TODO shyu - THIS METHOD NEEDS JAVADOCS
		super.doRouteStatusChange(statusChangeEvent);
		// if
		// (statusChangeEvent.getNewRouteStatus().equals(KEWConstants.ROUTE_HEADER_APPROVED_CD))
		// {
		if (statusChangeEvent.getNewRouteStatus().equals("R")) {
			KIMServiceLocator.getUiDocumentService().saveEntityPerson(this);
			
//			// save to KIM tables
//			// should call service for this
//			// test here for now
//			KimEntityImpl kimEntity = new KimEntityImpl();
//			kimEntity.setActive(isActive());
//			kimEntity.setEntityId(getEntityId());
//
//			// principal
//			List<KimPrincipalImpl> principals = new ArrayList<KimPrincipalImpl>();
//			KimPrincipalImpl principal = new KimPrincipalImpl();
//			principal.setPrincipalId(principalId);
//			principal.setPrincipalName(principalName);
//			principal.setPassword(password);
//			principals.add(principal);
//			kimEntity.setPrincipals(principals);
//
//			// privacy references
//			EntityPrivacyPreferencesImpl privacyPreferences = new EntityPrivacyPreferencesImpl();
//			privacyPreferences.setEntityId(entityId);
//			privacyPreferences.setSuppressAddress(privacy.isSuppressAddress());
//			privacyPreferences.setSuppressEmail(privacy.isSuppressEmail());
//			privacyPreferences.setSuppressName(privacy.isSuppressName());
//			privacyPreferences.setSuppressPhone(privacy.isSuppressPhone());
//			privacyPreferences
//					.setSuppressPersonal(privacy.isSuppressPersonal());
//			kimEntity.setPrivacyPreferences(privacyPreferences);
//
//			// affiliations
//			List<EntityAffiliationImpl> entityAffiliations = new ArrayList<EntityAffiliationImpl>();
//			for (PersonDocumentAffiliation affiliation : affiliations) {
//				EntityAffiliationImpl entityAffiliation = new EntityAffiliationImpl();
//				entityAffiliation.setAffiliationTypeCode(affiliation
//						.getAffiliationTypeCode());
//				entityAffiliation.setCampusCode(affiliation.getCampusCode());
//				entityAffiliation.setActive(affiliation.isActive());
//				entityAffiliation.setDefault(affiliation.isDflt());
//				entityAffiliation.setEntityAffiliationId(affiliation
//						.getEntityAffiliationId());
//				// EntityAffiliationImpl does not define empinfos as collection
//				entityAffiliations.add(entityAffiliation);
//
//				// employment informations
//				List<EntityEmploymentInformationImpl> entityEmploymentInformations = new ArrayList<EntityEmploymentInformationImpl>();
//				for (PersonDocumentEmploymentInfo empInfo : affiliation
//						.getEmpInfos()) {
//					EntityEmploymentInformationImpl entityEmpInfo = new EntityEmploymentInformationImpl();
//					entityEmpInfo.setEntityEmploymentId(empInfo
//							.getEntityEmploymentId());
//					entityEmpInfo.setEmployeeId(empInfo.getEmployeeId());
//					entityEmpInfo.setEmploymentRecordId(empInfo
//							.getEmploymentRecordId());
//					entityEmpInfo.setBaseSalaryAmount(empInfo
//							.getBaseSalaryAmount());
//					entityEmpInfo.setEmployeeStatusCode(empInfo
//							.getEmployeeStatusCode());
//					entityEmpInfo.setEmployeeTypeCode(empInfo
//							.getEmployeeTypeCode());
//					entityEmpInfo.setActive(empInfo.isActive());
//					entityEmpInfo.setEntityId(entityId);
//					entityEmpInfo.setEntityAffiliationId(empInfo
//							.getEntityAffiliationId());
//					entityEmploymentInformations.add(entityEmpInfo);
//				}
//				kimEntity
//						.setEmploymentInformation(entityEmploymentInformations);
//
//			}
//			kimEntity.setAffiliations(entityAffiliations);
//
//			// names
//			List<EntityNameImpl> entityNames = new ArrayList<EntityNameImpl>();
//			for (PersonDocumentName name : names) {
//				EntityNameImpl entityName = new EntityNameImpl();
//				entityName.setNameTypeCode(name.getNameTypeCode());
//				entityName.setFirstName(name.getFirstName());
//				entityName.setLastName(name.getLastName());
//				entityName.setMiddleName(name.getMiddleName());
//				entityName.setActive(name.isActive());
//				entityName.setDefault(name.isDflt());
//				entityName.setEntityNameId(name.getEntityNameId());
//				entityNames.add(entityName);
//			}
//			kimEntity.setNames(entityNames);
//
//			// entitytype
//			List<EntityEntityTypeImpl> entityTypes = new ArrayList<EntityEntityTypeImpl>();
//			EntityEntityTypeImpl entityType = new EntityEntityTypeImpl();
//			entityType.setEntityId(entityId);
//			entityType.setEntityTypeCode("PERSON");
//			entityTypes.add(entityType);
//			kimEntity.setEntityTypes(entityTypes);
//
//			// phones
//			List<EntityPhone> entityPhones = new ArrayList<EntityPhone>();
//			for (PersonDocumentPhone phone : phones) {
//				EntityPhoneImpl entityPhone = new EntityPhoneImpl();
//				entityPhone.setPhoneTypeCode(phone.getPhoneTypeCode());
//				entityPhone.setEntityId(entityId);
//				entityPhone.setEntityTypeCode(entityType.getEntityTypeCode());
//				entityPhone.setPhoneNumber(phone.getPhoneNumber());
//				entityPhone.setExtension(phone.getExtension());
//				entityPhone.setExtensionNumber(phone.getExtensionNumber());
//				entityPhone.setActive(phone.isActive());
//				entityPhone.setDefault(phone.isDflt());
//				entityPhone.setEntityPhoneId(phone.getEntityPhoneId());
//				entityPhones.add(entityPhone);
//			}
//			entityType.setPhoneNumbers(entityPhones);
//
//			// emails
//			List<EntityEmail> entityEmails = new ArrayList<EntityEmail>();
//			for (PersonDocumentEmail email : emails) {
//				EntityEmailImpl entityEmail = new EntityEmailImpl();
//				entityEmail.setEntityId(entityId);
//				entityEmail.setEntityTypeCode(entityType.getEntityTypeCode());
//				entityEmail.setEmailTypeCode(email.getEmailTypeCode());
//				entityEmail.setEmailAddress(email.getEmailAddress());
//				entityEmail.setActive(email.isActive());
//				entityEmail.setDefault(email.isDflt());
//				entityEmail.setEntityEmailId(email.getEntityEmailId());
//				entityEmails.add(entityEmail);
//			}
//			entityType.setEmailAddresses(entityEmails);
//
//			// address
//			List<EntityAddress> entityAddresses = new ArrayList<EntityAddress>();
//			for (PersonDocumentAddress address : addrs) {
//				EntityAddressImpl entityAddress = new EntityAddressImpl();
//				entityAddress.setEntityId(entityId);
//				entityAddress.setEntityTypeCode(entityType.getEntityTypeCode());
//				entityAddress.setAddressTypeCode(address.getAddressTypeCode());
//				entityAddress.setLine1(address.getLine1());
//				entityAddress.setLine2(address.getLine2());
//				entityAddress.setLine3(address.getLine3());
//				entityAddress.setStateCode(address.getStateCode());
//				entityAddress.setPostalCode(address.getPostalCode());
//				entityAddress.setCountryCode(address.getCountryCode());
//				entityAddress.setCityName(address.getCityName());
//				entityAddress.setActive(address.isActive());
//				entityAddress.setDefault(address.isDflt());
//				entityAddress.setEntityAddressId(address.getEntityAddressId());
//				entityAddresses.add(entityAddress);
//			}
//			entityType.setAddresses(entityAddresses);
//
//			// group memeber
//			List <GroupPrincipalImpl>  groupPrincipals = new ArrayList<GroupPrincipalImpl>();
//			for (PersonDocumentGroup group : groups) {
//				GroupPrincipalImpl groupPrincipalImpl = new GroupPrincipalImpl();
//				groupPrincipalImpl.setGroupId(group.getGroupId());
//				// TODO : principalId is not ready here yet ?
//				groupPrincipalImpl.setMemberPrincipalId(principalId);
//				groupPrincipals.add(groupPrincipalImpl);
//				
//			}
//			
//			List <RolePrincipalImpl>  rolePrincipals = new ArrayList<RolePrincipalImpl>();
//			for (PersonDocumentRole role : roles) {
//				RolePrincipalImpl rolePrincipalImpl = new RolePrincipalImpl();
//				rolePrincipalImpl.setRoleId(role.getRoleId());
//				// TODO : principalId is not ready here yet ?
//				rolePrincipalImpl.setPrincipalId(principalId);
//				rolePrincipals.add(rolePrincipalImpl);
//				
//			}
//			
//			KNSServiceLocator.getBusinessObjectService().save(kimEntity);

		}
	}

}
