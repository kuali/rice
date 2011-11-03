/**
 * Copyright 2005-2011 The Kuali Foundation
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
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.*;

/**
 * This class houses information pertaining to each recipient for a Notification message.  This 
 * recipient can be either a user or a group - which is specified by the recipient type.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREN_RECIP_T")
public class NotificationRecipient extends PersistableBusinessObjectBase{
    @Id
    @GeneratedValue(generator="KREN_RECIP_S")
	@GenericGenerator(name="KREN_RECIP_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREN_RECIP_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="RECIP_ID")
	private Long id;
    @Column(name="NTFCTN_ID", nullable=false)
	private Long notificationId;
    @Column(name="RECIP_TYP_CD", nullable=false)
	private String recipientType;
    @Column(name="PRNCPL_ID", nullable=false)
	private String recipientId;
    
    // Added for JPA uni-directional one-to-many (not yet supported by JPA)
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name="NTFCTN_ID", insertable=false, updatable=false)
    private Notification notification;
    
    /**
     * Constructs a NotificationRecipient instance.
     */
    public NotificationRecipient() {
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
     * Gets the recipientType attribute. 
     * @return Returns the recipientType.
     */
    public String getRecipientType() {
	return recipientType;
    }

    /**
     * Sets the recipientType attribute value.
     * @param recipientType The recipientType to set.
     */
    public void setRecipientType(String recipientType) {
	this.recipientType = recipientType;
    }
}

