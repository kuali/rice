/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kcb.api.exception;

/**
 * Base exception for message delivery and dismissal
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageDeliveryProcessingException extends KCBCheckedException {
    public MessageDeliveryProcessingException() {
        super();
    }

    public MessageDeliveryProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageDeliveryProcessingException(String message) {
         super(message);
     }

    public MessageDeliveryProcessingException(Throwable cause) {
        super(cause);
    }
}
