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
 * This class represents the data structure that will house information about the non-system 
 * sender that a notification message is sent on behalf of.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREN_SNDR_T")
public class NotificationSender extends PersistableBusinessObjectBase{
    @Id
    @GeneratedValue(generator="KREN_SNDR_S")
	@GenericGenerator(name="KREN_SNDR_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREN_SNDR_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="SNDR_ID")
	private Long id;
    @Column(name="NTFCTN_ID", nullable=false)
	private Long notificationId;
    @Column(name="NM", nullable=false)
	private String senderName;

    // Added for JPA uni-directional one-to-many (not yet supported by JPA)
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name="NTFCTN_ID", insertable=false, updatable=false)
    private Notification notification;

    /**
     * Constructs a NotificationSender.java instance.
     */
    public NotificationSender() {
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
     * Gets the notificationId attribute. 
     * @return Returns the notificationId.
     */
    public Long getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the notificationId attribute value.
     * @param notificationId The notificationId to set.
     */
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Gets the senderName attribute. 
     * @return Returns the senderName.
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Sets the senderName attribute value.
     * @param senderName The senderName to set.
     */
    public void setSenderName(String userId) {
        this.senderName = userId;
    }
}
