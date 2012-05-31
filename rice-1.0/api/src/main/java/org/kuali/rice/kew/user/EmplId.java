/*
 * Copyright 2005-2009 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.user;

import org.kuali.rice.kew.identity.EmployeeId;

/**
 * EmplId is an "employee" id that can be used as a foreign key into another,
 * institutional, identity system.  The workflow engine does not depend upon
 * the existence of this ID on a {@link WorkflowUser}.
 * 
 * @deprecated use {@link EmployeeId} instead 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EmplId implements UserId {

	private static final long serialVersionUID = -1335314734556834643L;

	private String emplId;

    public EmplId(String emplId) {
        setEmplId(emplId);
    }

    public EmplId() {
    }

    public String getId() {
        return getEmplId();
    }
    
    public String getEmplId() {
        return emplId;
    }

    public void setEmplId(String emplId) {
        this.emplId = (emplId == null ? null : emplId.trim());
    }

    /**
     * Returns true if this userId has an empty value. Empty userIds can't be used as keys in a Hash, among other things.
     * 
     * @return true if this instance doesn't have a value
     */
    public boolean isEmpty() {
    	return (emplId == null || emplId.trim().length() == 0);
    }

    /**
     * If you make this class non-final, you must rewrite equals to work for subclasses.
     */
    public boolean equals(Object obj) {

        if (obj != null && (obj instanceof EmplId)) {
            EmplId a = (EmplId) obj;

            if (getEmplId() == null) {
                return false;
            }

            return emplId.equals(a.emplId);
        }

        return false;
    }

    public int hashCode() {
        return emplId == null ? 0 : emplId.hashCode();
    }

    public String toString() {
        if (emplId == null) {
            return "emplId: null";
        }
        return "emplId: " + emplId;
    }
}
