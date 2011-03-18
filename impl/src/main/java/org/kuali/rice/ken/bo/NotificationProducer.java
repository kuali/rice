/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.ken.bo;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an instance of who can actually submit notification messages to the system 
 * for processing.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREN_PRODCR_T")
public class NotificationProducer extends PersistableBusinessObjectBase{
    @Id
    @GeneratedValue(generator="KREN_PRODCR_S")
	@GenericGenerator(name="KREN_PRODCR_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREN_PRODCR_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="PRODCR_ID")
	private Long id;
    @Column(name="NM", nullable=false)
	private String name;
    @Column(name="DESC_TXT", nullable=false)
	private String description;
    @Column(name="CNTCT_INFO", nullable=false)
	private String contactInfo;
    
    // List references
    @ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})@JoinTable(name="KREN_CHNL_PRODCR_T", 
	           joinColumns=@JoinColumn(name="PRODCR_ID"), 
	           inverseJoinColumns=@JoinColumn(name="CHNL_ID"))
	@OrderBy("id ASC")
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
}
