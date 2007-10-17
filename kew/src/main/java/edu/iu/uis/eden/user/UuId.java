/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.user;

import java.io.Serializable;

/**
 * UuId is a "universal" id that can be used as a foreign key into another,
 * institutional, identity system.  The workflow engine does not depend upon the
 * existence of this Id on a {@link WorkflowUser}.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public final class UuId implements UserId, Serializable {

	private static final long serialVersionUID = 5408293075182063097L;

	private String uuId;

    public UuId(String uuId) {
        setUuId(uuId);
    }

    public UuId() {
    }
    public String getId() {
        return getUuId();
    }

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = (uuId == null ? null : uuId.trim());
    }

    /**
     * Returns true if this userId has an empty value. Empty userIds can't be used as keys in a Hash, among other things.
     * 
     * @return true if this instance doesn't have a value
     */
    public boolean isEmpty() {
    	return (uuId == null || uuId.trim().length() == 0);
    }

    /**
     * If you make this class non-final, you must rewrite equals to work for subclasses.
     */
    public boolean equals(Object obj) {

        if (obj != null && (obj instanceof UuId)) {
            UuId a = (UuId) obj;

            if (getUuId() == null) {
                return false;
            }

            return uuId.equals(a.uuId);
        }

        return false;
    }

    public int hashCode() {
        return uuId == null ? 0 : uuId.hashCode();
    }

    public String toString() {
        if (uuId == null) {
            return "uuId: null";
        }
        return "uuId: " + uuId;
    }
}