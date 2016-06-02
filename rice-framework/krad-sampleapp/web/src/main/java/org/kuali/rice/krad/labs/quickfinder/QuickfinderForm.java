/**
 * Copyright 2005-2016 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.List;

/**
 * Form to contain quickfinder's travel account fields.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class QuickfinderForm extends UifFormBase {

    private List<PersonAccount> personAccounts1;
    private List<PersonAccount> personAccounts2;
    private List<PersonAccount> personAccounts3;
    private List<PersonAccount> personAccounts4;
    private List<PersonAccount> personAccounts5;

    private String nameField;
    private String accountNumberField;
    private String accountNameField;
    private LabsRoleMember labsRoleMember;

    public QuickfinderForm() {
        super();
        personAccounts1 = cheapInit();
        personAccounts2 = cheapInit();
        personAccounts3 = cheapInit();
        personAccounts4 = cheapInit();
        personAccounts5 = cheapInit();
    }

    public String getAccountNameField() { return accountNameField; }

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

    public LabsRoleMember getLabsRoleMember() {
        return labsRoleMember;
    }

    public void setLabsRoleMember(LabsRoleMember labsRoleMember) {
        this.labsRoleMember = labsRoleMember;
    }

    public List<PersonAccount> getPersonAccounts1() {
        return personAccounts1;
    }

    public void setPersonAccounts1(List<PersonAccount> personAccounts1) {
        this.personAccounts1 = personAccounts1;
    }

    public List<PersonAccount> getPersonAccounts2() {
        return personAccounts2;
    }

    public void setPersonAccounts2(List<PersonAccount> personAccounts2) {
        this.personAccounts2 = personAccounts2;
    }

    public List<PersonAccount> getPersonAccounts3() {
        return personAccounts3;
    }

    public void setPersonAccounts3(List<PersonAccount> personAccounts3) {
        this.personAccounts3 = personAccounts3;
    }

    public List<PersonAccount> getPersonAccounts4() {
        return personAccounts4;
    }

    public void setPersonAccounts4(List<PersonAccount> personAccounts4) {
        this.personAccounts4 = personAccounts4;
    }

    public List<PersonAccount> getPersonAccounts5() {
        return personAccounts5;
    }

    public void setPersonAccounts5(List<PersonAccount> personAccounts5) {
        this.personAccounts5 = personAccounts5;
    }

    private List<PersonAccount> cheapInit () {
        List<PersonAccount> peopleAccountsToInit = new ArrayList<PersonAccount>();
        peopleAccountsToInit.add(new PersonAccount("Abra"));
        peopleAccountsToInit.add(new PersonAccount("Ca"));
        peopleAccountsToInit.add(new PersonAccount("Dabra"));
        return (peopleAccountsToInit);
    }
}
