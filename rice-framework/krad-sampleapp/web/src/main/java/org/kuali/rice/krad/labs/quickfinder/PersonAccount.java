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

import org.kuali.rice.kim.api.identity.Person;

import java.io.Serializable;

/**
 * Class to associate a person with a travel account
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PersonAccount implements Serializable {

    private static final long serialVersionUID = -7525378097732916422L;

    private String name;
    private String accountNumber;
    private String accountName;

    public PersonAccount() {}

    public PersonAccount( String name ) {
        this.name = name;
    }

    /**
     * The name of the travel account for this person
     *
     * @return accountName
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * @see PersonAccount#getAccountName()
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * The number of the travel account for this person
     *
     * @return accountNumber
     */
    public String getAccountNumber () {
        return accountNumber;
    }
    /**
     * @see PersonAccount#getAccountNumber()
     */
    public void setAccountNumber(String number) {
        this.accountNumber = number;
    }
    /**
     * The name of this person
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @see PersonAccount#getName()
     */
    public void setName(String name) {
        this.name = name;
    }
}
