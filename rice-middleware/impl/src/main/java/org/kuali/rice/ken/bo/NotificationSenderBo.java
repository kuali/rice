/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.kuali.rice.ken.api.notification.NotificationSender;
import org.kuali.rice.ken.api.notification.NotificationSenderContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents the data structure that will house information about the non-system 
 * sender that a notification message is sent on behalf of.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREN_SNDR_T")
public class NotificationSenderBo extends PersistableBusinessObjectBase implements NotificationSenderContract {
    @Id
    @GeneratedValue(generator="KREN_SNDR_S")
    @PortableSequenceGenerator(name="KREN_SNDR_S")
	@Column(name="SNDR_ID")
	private Long id;
    @Column(name="NM", nullable=false)
	private String senderName;

    // Added for JPA uni-directional one-to-many (not yet supported by JPA)
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name="NTFCTN_ID", nullable = false)
    private NotificationBo notification;

    /**
     * Constructs a NotificationSender.java instance.
     */
    public NotificationSenderBo() {
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
     * Gets the senderName attribute. 
     * @return Returns the senderName.
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Sets the senderName attribute value.
     * @param userId The senderName to set.
     */
    public void setSenderName(String userId) {
        this.senderName = userId;
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
    public static NotificationSender to(NotificationSenderBo bo) {
        if (bo == null) {
            return null;
        }

        return NotificationSender.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static NotificationSenderBo from(NotificationSender im) {
        if (im == null) {
            return null;
        }

        NotificationSenderBo bo = new NotificationSenderBo();
        bo.setId(im.getId());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        bo.setSenderName(im.getSenderName());
        if (im.getNotificationId() != null) {
            NotificationBo notification =
                    KradDataServiceLocator.getDataObjectService().find(NotificationBo.class, im.getNotificationId());
            bo.setNotification(notification);
        }
        return bo;
    }
}
