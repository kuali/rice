/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kew.dto;

/**
 * Super class for all user id transport objects.
 * 
 * @deprecated UserIdDTO should no longer be used.  Instead, there should be alternate API methods
 * which reference Strings containing principal ID
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class UserIdDTO extends RecipientIdDTO {
    
    private String id;
    
    public UserIdDTO() {}
    
    public UserIdDTO(String id) {
        this.id = id;
    }
    
    protected String getId() {
        return id;
    }
    
    protected void setId(String id) {
        this.id = id;
    }
    
    public boolean equals(Object object) {
        if (getClass().isInstance(object)) {
            String objectId = ((UserIdDTO)object).getId();
            return ((getId() == null && objectId == null) || (getId() != null && getId().equals(objectId)));
        }
        return false;
    }
    
    public int hashCode() {
        return (getId() == null ? 0 : getId().hashCode());
    }
    
    public String toString() {
        return (getId() == null ? "null" : getId());
    }
    
}
