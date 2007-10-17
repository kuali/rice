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
package edu.iu.uis.eden.clientapp.vo;



/**
 * Super class for all user id transport objects.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object 
 */
public abstract class UserIdVO extends RecipientIdVO {
    
    private String id;
    
    public UserIdVO() {}
    
    public UserIdVO(String id) {
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
            String objectId = ((UserIdVO)object).getId();
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
