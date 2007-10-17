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
 * This class represents the different types of Notification content that the system can handle.  
 * For example, and instance of content type could be "Alert" or "Event".
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationContentType {
    private Long id;
    private String name;
    private String description;
    private String namespace;
    private String xsd;
    private String xsl;

    /**
     * Constructs a NotificationContentType instance.
     */
    public NotificationContentType() {
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
     * Gets the namespace attribute. 
     * @return Returns the namespace.
     */
    public String getNamespace() {
	return namespace;
    }

    /**
     * Sets the namespace attribute value.
     * @param namespace The namespace to set.
     */
    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }

    /**
     * Gets the xsd attribute. The value of this field is used to validate a notification's content field dynamically.
     * @return Returns the xsd.
     */
    public String getXsd() {
        return xsd;
    }

    /**
     * Sets the xsd attribute value.  The value of this field is used to validate a notification's content field dynamically.
     * @param xsd The xsd to set.
     */
    public void setXsd(String xsd) {
        this.xsd = xsd;
    }

    /**
     * Gets the xsl attribute. The value of this field is used to render a notification's contents dynamically.
     * @return Returns the xsl.
     */
    public String getXsl() {
        return xsl;
    }

    /**
     * Sets the xsl attribute value.  The value of this field is used to render a notification's contents dynamically.
     * @param xsl The xsl to set.
     */
    public void setXsl(String xsl) {
        this.xsl = xsl;
    }

    /**
     * Returns readable representation of the object
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return "[NotificationContentType: id=" + id +
	                               ", name=" + name +
	                               ", namespace=" + namespace +
	                               ", description=" + description + "]";
    }
}