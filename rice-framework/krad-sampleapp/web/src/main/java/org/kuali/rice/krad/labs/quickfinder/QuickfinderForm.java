/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.labs.quickfinder;

import org.kuali.rice.krad.labs.LabsRoleMember;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Form to contain quickfinder's travel account fields.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class QuickfinderForm extends UifFormBase {

    private List<PersonAccount> peopleAccounts;

    private String nameField;
    private String accountNumberField;
    private String accountNameField;
    private LabsRoleMember labsRoleMember;

    public QuickfinderForm() {
        super();
        peopleAccounts = new ArrayList<PersonAccount>();
        peopleAccounts.add( new PersonAccount( "Abra" ) );
        peopleAccounts.add( new PersonAccount( "Ca" ) );
        peopleAccounts.add( new PersonAccount( "Dabra" ) );
    }

    public String getAccountNameField() {
        return accountNameField;
    }

    public void setAccountNameField(String accountNameField) {
        this.accountNameField = accountNameField;
    }

    public String getAccountNumberField() {
        return accountNumberField;
    }

    public void setAccountNumberField(String accountNumberField) {
        this.accountNumberField = accountNumberField;
    }

    public String getNameField() {
        return nameField;
    }

    public void setNameField(String nameField) {
        this.nameField = nameField;
    }

    public List<PersonAccount> getPeopleAccounts() {
        return peopleAccounts;
    }

    public void setPeopleAccounts(List<PersonAccount> peopleAccounts) {
        this.peopleAccounts = peopleAccounts;
    }

    public LabsRoleMember getLabsRoleMember() { return labsRoleMember; }

    public void setLabsRoleMember(LabsRoleMember labsRoleMember) { this.labsRoleMember = labsRoleMember; }
}
