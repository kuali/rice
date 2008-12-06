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
package org.kuali.rice.kim.web.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentCitizenship;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementPersonDocumentForm extends KualiDocumentFormBase {

    private PersonDocumentAffiliation newAffln;
    private PersonDocumentEmploymentInfo newEmpInfo;
    private PersonDocumentCitizenship newCitizenship;
    private PersonDocumentName newName;
    private PersonDocumentAddress newAddress;
    private PersonDocumentPhone newPhone;
    private PersonDocumentEmail newEmail;
    private PersonDocumentGroup newGroup;
    private PersonDocumentRole newRole;

    
    public IdentityManagementPersonDocumentForm() {
        super();
        this.setDocument(new IdentityManagementPersonDocument());
    }

    /*
     * Reset method - reset attributes of form retrieved from session otherwise
     * we will always call docHandler action
     * @param mapping
     * @param request
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.setMethodToCall(null);
        this.setRefreshCaller(null);
        this.setAnchor(null);
        this.setCurrentTabIndex(0);
    }

    public IdentityManagementPersonDocument getPersonDocument() {
        return (IdentityManagementPersonDocument) this.getDocument();
    }

	public PersonDocumentAffiliation getNewAffln() {
		return this.newAffln;
	}

	public void setNewAffln(PersonDocumentAffiliation newAffln) {
		this.newAffln = newAffln;
	}

	public PersonDocumentEmploymentInfo getNewEmpInfo() {
		return this.newEmpInfo;
	}

	public void setNewEmpInfo(PersonDocumentEmploymentInfo newEmpInfo) {
		this.newEmpInfo = newEmpInfo;
	}

	public PersonDocumentCitizenship getNewCitizenship() {
		return this.newCitizenship;
	}

	public void setNewCitizenship(PersonDocumentCitizenship newCitizenship) {
		this.newCitizenship = newCitizenship;
	}

	public PersonDocumentName getNewName() {
		return this.newName;
	}

	public void setNewName(PersonDocumentName newName) {
		this.newName = newName;
	}

	public PersonDocumentAddress getNewAddress() {
		return this.newAddress;
	}

	public void setNewAddress(PersonDocumentAddress newAddress) {
		this.newAddress = newAddress;
	}

	public PersonDocumentPhone getNewPhone() {
		return this.newPhone;
	}

	public void setNewPhone(PersonDocumentPhone newPhone) {
		this.newPhone = newPhone;
	}

	public PersonDocumentEmail getNewEmail() {
		return this.newEmail;
	}

	public void setNewEmail(PersonDocumentEmail newEmail) {
		this.newEmail = newEmail;
	}

	public PersonDocumentGroup getNewGroup() {
		return this.newGroup;
	}

	public void setNewGroup(PersonDocumentGroup newGroup) {
		this.newGroup = newGroup;
	}

	public PersonDocumentRole getNewRole() {
		return this.newRole;
	}

	public void setNewRole(PersonDocumentRole newRole) {
		this.newRole = newRole;
	}


}
