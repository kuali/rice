/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kcb.bo;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents a recipient preference in the system.  This is a generic Key/Value structure 
 * that is used by the system to store preferences that the user has set up. This will be 
 * used by the tickler plugins which will need a generic and dynamic structure for user specific settings.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RecipientPreference {
    private Long id;
    private String recipientId;
    private String property;
    private String value;

    /**
     * Lock column for OJB optimistic locking
     */
    private Integer lockVerNbr;

    /**
     * Gets the id attribute. 
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id attribute value.
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the property attribute. 
     * @return Returns the property.
     */
    public String getProperty() {
        return property;
    }

    /**
     * Sets the property attribute value.
     * @param property The property to set.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Gets the recipientId attribute. 
     * @return Returns the recipientId.
     */
    public String getRecipientId() {
        return recipientId;
    }

    /**
     * Sets the recipientId attribute value.
     * @param recipientId The recipientId to set.
     */
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * Gets the value attribute. 
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value attribute value.
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Return value of lock column for OJB optimistic locking
     * @return value of lock column for OJB optimistic locking
     */
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    /**
     * Set value of lock column for OJB optimistic locking
     * @param lockVerNbr value of lock column for OJB optimistic locking
     */
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
                                        .append("recipientId", recipientId)
                                        .append("property", property)
                                        .append("value", value)
                                        .append("lockVerNbr", lockVerNbr)
                                        .toString();
    }
}