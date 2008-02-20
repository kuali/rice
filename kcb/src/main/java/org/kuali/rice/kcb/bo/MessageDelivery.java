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
 * This class represents an instance of a MessageDelivery.  A Message gets delivered to 
 * recipients, possibly in various ways.  For each delivery type that a recipient gets sent to them, 
 * they have an instance of this entity.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MessageDelivery {
    /**
     * Field names
     */
    public static final String ID_FIELD = "id";
    public static final String SYSTEMID_FIELD = "delivererSystemId";
    public static final String MESSAGEID_FIELD = "message";
    
    private Long id;
    private String delivererTypeName;
    private String delivererSystemId;  // can hold an identifier from the endpoint deliverer mechanism system (i.e. workflow id, SMS id, etc)
    private String deliveryStatus;

    /**
     * This delivery's message
     */
    private Message message;

    /**
     * Lock column for OJB optimistic locking
     */
    private Integer lockVerNbr;
    
    /**
     * Constructs a MessageDelivery instance.
     */
    public MessageDelivery() {
    }

    /**
     * Shallow-copy constructor
     * @param md MessageDelivery to (shallow) copy
     */
    public MessageDelivery(MessageDelivery md) {
        this.id = md.id;
        this.delivererTypeName = md.delivererTypeName;
        this.deliveryStatus = md.deliveryStatus;
        this.delivererSystemId = md.delivererSystemId;
        this.message = md.message;
        this.lockVerNbr = md.lockVerNbr;
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
     * Gets the deliveryStatus attribute. 
     * @return Returns the deliveryStatus.
     */
    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    /**
     * Sets the deliveryStatus attribute value.
     * @param deliveryStatus The deliveryStatus to set.
     */
    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    /**
     * Gets the delivererTypeName attribute. 
     * @return Returns the delivererTypeName.
     */
    public String getDelivererTypeName() {
        return delivererTypeName;
    }

    /**
     * Sets the delivererTypeName attribute value.
     * @param delivererTypeName The delivererTypeName to set.
     */
    public void setDelivererTypeName(String delivererTypeName) {
        this.delivererTypeName = delivererTypeName;
    }

    /**
     * Gets the delivererSystemId attribute. 
     * @return Returns the delivererSystemId.
     */
    public String getDelivererSystemId() {
        return delivererSystemId;
    }

    /**
     * Sets the delivererSystemId attribute value.
     * @param delivererSystemId The delivererSystemId to set.
     */
    public void setDelivererSystemId(String delivererSystemId) {
        this.delivererSystemId = delivererSystemId;
    }

    /**
     * @return this delivery's message
     */
    public Message getMessage() {
        return this.message;
    }

    /**
     * Sets this delivery's message
     * @param message the message to set
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                       .append("id", id)
                       .append("deliveryStatus", deliveryStatus)
                       .append("delivererTypename", delivererTypeName)
                       .append("delivererSystemId", delivererSystemId)
                       .append("message", message == null ? null : message.getId())
                       .toString();
    }
}