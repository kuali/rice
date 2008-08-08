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
package mocks;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.mail.EmailBody;
import org.kuali.rice.kew.mail.EmailFrom;
import org.kuali.rice.kew.mail.EmailSubject;
import org.kuali.rice.kew.mail.EmailTo;
import org.kuali.rice.kew.mail.service.impl.DefaultEmailService;


/**
 * This class is used to disallow email sending for KEW tests 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MockDefaultEmailServiceImpl extends DefaultEmailService {
    private static final Logger LOG = Logger.getLogger(MockDefaultEmailServiceImpl.class);

    /**
     * MOCK METHOD USED TO OVERRIDE EMAIL DELIVERY
     */
    @Override
    public void sendEmail(EmailFrom from, EmailTo to, EmailSubject subject, EmailBody body, boolean htmlMessage) {
        String toValue = (to == null) ? "" : to.getToAddress();
        String fromValue = (from == null) ? "" : from.getFromAddress();
        String subjectValue = (subject == null) ? "" : subject.getSubject();
        String bodyValue = (body == null) ? "" : body.getBody();
        LOG.debug("WILL NOT send e-mail message with to '" + toValue + "'... from '" + fromValue + "'... subject '" + subjectValue + "'... and body '" + bodyValue);
    }
}
