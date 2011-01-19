/*
 * Copyright 2007-2008 The Kuali Foundation
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

/**
 * This class represents an instance of a user's subscription to a specific 
 * notification channel.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREN_CHNL_SUBSCRP_T")
public class UserChannelSubscription extends PersistableBusinessObjectBase{
    @Id
    @GeneratedValue(generator="KREN_CHNL_SUBSCRP_S")
	@GenericGenerator(name="KREN_CHNL_SUBSCRP_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREN_CHNL_SUBSCRP_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="CHNL_SUBSCRP_ID")
	private Long id;
    @Column(name="PRNCPL_ID", nullable=false)
	private String userId;
    
    @OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.REFRESH, CascadeType.MERGE})
	@JoinColumn(name="CHNL_ID")
	private NotificationChannel channel;
    
    /**
     * Constructs a UserChannelSubscription instance.
     */
    public UserChannelSubscription() {
    }

    /**
     * Gets the channel attribute. 
     * @return Returns the channel.
     */
    public NotificationChannel getChannel() {
	return channel;
    }

    /**
     * Sets the channel attribute value.
     * @param channel The channel to set.
     */
    public void setChannel(NotificationChannel channel) {
	this.channel = channel;
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
     * Gets the userId attribute. 
     * @return Returns the userId.
     */
    public String getUserId() {
	return userId;
    }

    /**
     * Sets the userId attribute value.
     * @param userId The userId to set.
     */
    public void setUserId(String userId) {
	this.userId = userId;
    }
}

