/**
 * Copyright 2005-2017 The Kuali Foundation
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

import org.kuali.rice.ken.api.notification.NotificationRecipient;
import org.kuali.rice.ken.api.notification.NotificationRecipientContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.*;

/**
 * This class houses information pertaining to each recipient for a Notification message.  This 
 * recipient can be either a user or a group - which is specified by the recipient type.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREN_RECIP_T")
public class NotificationRecipientBo extends PersistableBusinessObjectBase implements NotificationRecipientContract {
    @Id
    @GeneratedValue(generator="KREN_RECIP_S")
    @PortableSequenceGenerator(name="KREN_RECIP_S")
	@Column(name="RECIP_ID")
	private Long id;
    @Column(name="RECIP_TYP_CD", nullable=false)
	private String recipientType;
    @Column(name="PRNCPL_ID", nullable=false)
	private String recipientId;

    // Added for JPA uni-directional one-to-many (not yet supported by JPA)
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name="NTFCTN_ID", nullable = false)
    private NotificationBo notification;

    /**
     * Constructs a NotificationRecipient instance.
     */
    public NotificationRecipientBo() {
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
	    return (notification == null) ? null : notification.getId();
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

    public NotificationBo getNotification() {
        return notification;
    }

    public void setNotification(NotificationBo notification) {
        this.notification = notification;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static NotificationRecipient to(NotificationRecipientBo bo) {
        if (bo == null) {
            return null;
        }

        return NotificationRecipient.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static NotificationRecipientBo from(NotificationRecipient im) {
        if (im == null) {
            return null;
        }

        NotificationRecipientBo bo = new NotificationRecipientBo();
        bo.setId(im.getId());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());

        bo.setRecipientType(im.getRecipientType());
        bo.setRecipientId(im.getRecipientId());
        if (im.getNotificationId() != null) {
            NotificationBo notification =
                    KradDataServiceLocator.getDataObjectService().find(NotificationBo.class, im.getNotificationId());
            bo.setNotification(notification);
        }
        return bo;
    }
}

