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
package org.kuali.rice.krad.uif.util;

import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Mock person implementation.
 */
public class MockPerson implements Person {

    private static final long serialVersionUID = 5330488987382249417L;

    private final String id;

    public MockPerson() {
        id = null;
    }
    
    MockPerson(String id) {
        this.id = id;
    }

    @Override
    public void refresh() {}

    @Override
    public String getPrincipalId() {
        return id;
    }

    @Override
    public String getPrincipalName() {
        return id;
    }

    @Override
    public String getEntityId() {
        return id;
    }

    @Override
    public String getEntityTypeCode() {
        return null;
    }

    @Override
    public String getFirstName() {
        return "Test";
    }

    @Override
    public String getFirstNameUnmasked() {
        return "Test";
    }

    @Override
    public String getMiddleName() {
        return "User";
    }

    @Override
    public String getMiddleNameUnmasked() {
        return "User";
    }

    @Override
    public String getLastName() {
        return id;
    }

    @Override
    public String getLastNameUnmasked() {
        return id;
    }

    @Override
    public String getName() {
        return "Test User " + id;
    }

    @Override
    public String getNameUnmasked() {
        return "Test User " + id;
    }

    @Override
    public String getEmailAddress() {
        return null;
    }

    @Override
    public String getEmailAddressUnmasked() {
        return null;
    }

    @Override
    public String getAddressLine1() {
        return null;
    }

    @Override
    public String getAddressLine1Unmasked() {
        return null;
    }

    @Override
    public String getAddressLine2() {
        return null;
    }

    @Override
    public String getAddressLine2Unmasked() {
        return null;
    }

    @Override
    public String getAddressLine3() {
        return null;
    }

    @Override
    public String getAddressLine3Unmasked() {
        return null;
    }

    @Override
    public String getAddressCity() {
        return null;
    }

    @Override
    public String getAddressCityUnmasked() {
        return null;
    }

    @Override
    public String getAddressStateProvinceCode() {
        return null;
    }

    @Override
    public String getAddressStateProvinceCodeUnmasked() {
        return null;
    }

    @Override
    public String getAddressPostalCode() {
        return null;
    }

    @Override
    public String getAddressPostalCodeUnmasked() {
        return null;
    }

    @Override
    public String getAddressCountryCode() {
        return null;
    }

    @Override
    public String getAddressCountryCodeUnmasked() {
        return null;
    }

    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public String getPhoneNumberUnmasked() {
        return null;
    }

    @Override
    public String getCampusCode() {
        return null;
    }

    @Override
    public Map<String, String> getExternalIdentifiers() {
        return null;
    }

    @Override
    public boolean hasAffiliationOfType(String affiliationTypeCode) {
        return false;
    }

    @Override
    public List<String> getCampusCodesForAffiliationOfType(String affiliationTypeCode) {
        return null;
    }

    @Override
    public String getEmployeeStatusCode() {
        return null;
    }

    @Override
    public String getEmployeeTypeCode() {
        return null;
    }

    @Override
    public KualiDecimal getBaseSalaryAmount() {
        return null;
    }

    @Override
    public String getExternalId(String externalIdentifierTypeCode) {
        return null;
    }

    @Override
    public String getPrimaryDepartmentCode() {
        return null;
    }

    @Override
    public String getEmployeeId() {
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

}