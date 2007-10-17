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
package org.kuali.notification.bo;

/**
 * This class represents a priority for a notification - i.e. "High", "Medium", "Low", "Emergency", etc.
 * In addition, it describes information about a priority such as its ranking order of priority.  Priority 
 * order within the system is assumed to be ascending.  This by no means impacts the order of delivery 
 * of a notification system message.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationPriority {
    private Long id;
    private String name;
    private String description;
    private Integer order;
    
    /**
     * Constructs a NotificationPriority instance.
     */
    public NotificationPriority() {
    }

    /**
     * Gets the description attribute. 
     * @return Returns the description.
     */
    public String getDescription() {
	return description;
    }

    /**
     * Sets the description attribute value.
     * @param description The description to set.
     */
    public void setDescription(String description) {
	this.description = description;
    }

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
     * Gets the name attribute. 
     * @return Returns the name.
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the name attribute value.
     * @param name The name to set.
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Gets the order attribute. 
     * @return Returns the order.
     */
    public Integer getOrder() {
	return order;
    }

    /**
     * Sets the order attribute value.
     * @param order The order to set.
     */
    public void setOrder(Integer order) {
	this.order = order;
    }
}
