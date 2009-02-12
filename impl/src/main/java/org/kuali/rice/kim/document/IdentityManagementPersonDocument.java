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

import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityBioDemographics;
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
	protected KimEntityAffiliation defaultAffiliation;

	protected String campusCode = "";
	// external identifier data
	protected Map<String, String> externalIdentifiers = null;

	protected boolean active;

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
		this.active = true;
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

	public KimEntityAffiliation getDefaultAffiliation() {
		return this.defaultAffiliation;
	}

	public void setDefaultAffiliation(KimEntityAffiliation defaultAffiliation) {
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
		managedLists.add(getPhones());
		managedLists.add(getAddrs());
		managedLists.add(getEmails());
		managedLists.add(getNames());
		managedLists.add(getGroups());
		managedLists.add(getRoles());
		return managedLists;
	}

	/**
	 * @see org.kuali.rice.kns.document.DocumentBase#handleRouteStatusChange()
	 */
	@Override
	public void handleRouteStatusChange() {
		super.handleRouteStatusChange();
		if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
			KIMServiceLocator.getUiDocumentService().saveEntityPerson(this);
		}
	}

}
