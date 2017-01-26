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
package org.kuali.rice.kcb.service;

import org.kuali.rice.kcb.bo.MessageDelivery;

/**
 * The EmailService class is responsible for actually sending email messages to recipients.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface EmailService {
    /**
     * This service method is responsible for sending a Email notification for the given user.
     * @param messageDelivery
     * @param recipientEmailAddress
     * @param emailFormat
     * @return Long - the id of the email message
     */
    public Long sendEmail(MessageDelivery messageDelivery, String recipientEmailAddress, String emailFormat) throws Exception;
}
