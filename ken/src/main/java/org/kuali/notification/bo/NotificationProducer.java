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

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents an instance of who can actually submit notification messages to the system 
 * for processing.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="NOTIFICATION_PRODUCERS")
public class NotificationProducer {
    @Id
	@Column(name="ID")
	private Long id;
    @Column(name="NAME", nullable=false)
	private String name;
    @Column(name="DESCRIPTION", nullable=false)
	private String description;
    @Column(name="CONTACT_INFO", nullable=false)
	private String contactInfo;
    
    // List references
    @ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})@JoinTable(name="NOTIFICATION_CHANNEL_PRODUCERS", 
	           joinColumns=@JoinColumn(name="PRODUCER_ID"), 
	           inverseJoinColumns=@JoinColumn(name="CHANNEL_ID"))
	private List<NotificationChannel> channels;
    
    /**
     * Constructs a NotificationProducer instance.
     */
    public NotificationProducer() {
	channels = new ArrayList<NotificationChannel>();
    }

    /**
     * Gets the contactInfo attribute. 
     * @return Returns the contactInfo.
     */
    public String getContactInfo() {
	return contactInfo;
    }

    /**
     * Sets the contactInfo attribute value.
     * @param contactInfo The contactInfo to set.
     */
    public void setContactInfo(String contactInfo) {
	this.contactInfo = contactInfo;
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
     * Gets the channels attribute. 
     * @return Returns the channels.
     */
    public List<NotificationChannel> getChannels() {
        return channels;
    }

    /**
     * Sets the channels attribute value.
     * @param channels The channels to set.
     */
    public void setChannels(List<NotificationChannel> channels) {
        this.channels = channels;
    }
    
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
                                        .append("name", name)
                                        .append("description", description)
                                        .append("contactInfo", contactInfo)
                                        .toString();
    }
}
