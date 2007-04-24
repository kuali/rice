/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.bo.user;

/**
 * This class wraps a social security number.
 * 
 * 
 */
public class PersonTaxId implements UserId {

    public static final PersonTaxId NOT_FOUND = new PersonTaxId("not found");

    private String taxId;

    /**
     * Empty constructor, available to support standard bean activity
     */
    public PersonTaxId() {
    }

    /**
     * Constructor that takes in a string taxId
     * 
     * @param taxId
     */
    public PersonTaxId(String socialSecurityNumber) {
        this();
        setTaxId(socialSecurityNumber);
    }

    /**
     * getter which returns the string taxId
     * 
     * @return taxId
     */
    public String getTaxId() {
        return taxId;
    }

    /**
     * setter which takes the string taxId
     * 
     * @param taxId
     */
    public void setTaxId(String socialSecurityNumber) {
        this.taxId = (socialSecurityNumber == null ? null : socialSecurityNumber.trim());
    }

    /**
     * Returns true if this userId has an empty value. Empty userIds can't be used as keys in a Hash, among other things.
     * 
     * @return true if this instance doesn't have a value
     */
    public boolean isEmpty() {
        return (taxId == null || taxId.trim().length() == 0);
    }

    /**
     * override equals so that we can compare socialSecurityNumbers If you make this class non-final, you must rewrite equals to
     * work for subclasses.
     */
    public boolean equals(Object obj) {
        boolean isEqual = false;

        if (obj != null && (obj instanceof PersonTaxId)) {
            PersonTaxId a = (PersonTaxId) obj;

            if (getTaxId() == null) {
                return false;
            }

            return taxId.equals(a.taxId);
        }

        return false;
    }

    /**
     * override hashCode because we overrode equals
     */
    public int hashCode() {
        return taxId == null ? 0 : taxId.hashCode();
    }

    /**
     * override toString so that it prints out the taxId String
     */
    public String toString() {
        return taxId;
    }
}
