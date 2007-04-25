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

import java.io.Serializable;

/**
 * This class is a wrapper class that wraps a string payrollId to provide strong typing of this object as it can be used to find a user
 * distinctly within our system
 * 
 * 
 */
public final class PersonPayrollId implements UserId, Serializable {

    private static final long serialVersionUID = -8246941078425120732L;

    public static final PersonPayrollId NOT_FOUND = new PersonPayrollId("not found");

    private String payrollId;

    /**
     * Constructor which takes a string emplid
     * 
     * @param payrollId
     */
    public PersonPayrollId(String emplId) {
        setPayrollId(emplId);
    }

    /**
     * Empty Constructor
     * 
     */
    public PersonPayrollId() {
    }

    /**
     * simple getter for the string emplid
     * 
     * @return
     */
    public String getPayrollId() {
        return payrollId;
    }

    /**
     * simple setter for the string emplid
     * 
     * @param payrollId
     */
    public void setPayrollId(String emplId) {
        this.payrollId = (emplId == null ? null : emplId.trim());
    }

    /**
     * Returns true if this userId has an empty value. Empty userIds can't be used as keys in a Hash, among other things.
     * 
     * @return true if this instance doesn't have a value
     */
    public boolean isEmpty() {
        return (payrollId == null || payrollId.trim().length() == 0);
    }

    /**
     * override equals to allow for comparison of Emplid objects If you make this class non-final, you must rewrite equals to work
     * for subclasses.
     */
    public boolean equals(Object obj) {
        boolean isEqual = false;

        if (obj != null && (obj instanceof PersonPayrollId)) {
            PersonPayrollId a = (PersonPayrollId) obj;

            if (getPayrollId() == null) {
                return false;
            }

            return payrollId.equals(a.payrollId);
        }

        return false;
    }

    /**
     * override of hashCode since we overrode equals
     */
    public int hashCode() {
        return payrollId == null ? 0 : payrollId.hashCode();
    }

    /**
     * override of toString so that we print out the emplid string
     */
    public String toString() {
        return payrollId;
    }
}