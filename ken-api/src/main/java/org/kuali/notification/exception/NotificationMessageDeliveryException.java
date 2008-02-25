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
package org.kuali.notification.exception;

/**
 * This class represents a delivery exception - when notifications are not properly delivered 
 * to their target audiences.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationMessageDeliveryException extends Exception {
    /**
     * Constructs a NotificationMessageDeliveryException instance.
     */
    public NotificationMessageDeliveryException() {
        super();
    }

    /**
     * Constructs a NotificationMessageDeliveryException instance.
     * @param message
     * @param cause
     */
    public NotificationMessageDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a NotificationMessageDeliveryException instance.
     * @param message
     */
    public NotificationMessageDeliveryException(String message) {
        super(message);
    }

    /**
     * Constructs a NotificationMessageDeliveryException instance.
     * @param cause
     */
    public NotificationMessageDeliveryException(Throwable cause) {
        super(cause);
    }
}