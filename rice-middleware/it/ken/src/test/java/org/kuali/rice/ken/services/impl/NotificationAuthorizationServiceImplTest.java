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
package org.kuali.rice.ken.services.impl;

import org.junit.Test;
import org.kuali.rice.ken.bo.NotificationChannelBo;
import org.kuali.rice.ken.bo.NotificationProducerBo;
import org.kuali.rice.ken.test.KENTestCase;
import org.kuali.rice.ken.test.TestConstants;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the authz aspects of KEN
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineMode(Mode.CLEAR_DB)
public class NotificationAuthorizationServiceImplTest extends KENTestCase {

    public NotificationAuthorizationServiceImplTest() {
    }

    @Test
    public void testIsProducerAuthorizedForNotificationChannel_validInput() {

        NotificationChannelBo channel = KRADServiceLocator.getDataObjectService().find(NotificationChannelBo.class,
                TestConstants.CHANNEL_ID_1);
        NotificationProducerBo producer = KRADServiceLocator.getDataObjectService().find(NotificationProducerBo.class,
                TestConstants.PRODUCER_3.getId());

	    assertTrue(services.getNotificationAuthorizationService().isProducerAuthorizedToSendNotificationForChannel(producer, channel));
    }

    @Test
    public void testIsProducerAuthorizedForNotificationChannel_invalidInput() {

        NotificationChannelBo channel = KRADServiceLocator.getDataObjectService().find(NotificationChannelBo.class, TestConstants.CHANNEL_ID_1);
        NotificationProducerBo producer = KRADServiceLocator.getDataObjectService().find(NotificationProducerBo.class, TestConstants.PRODUCER_4.getId());

	    assertFalse(services.getNotificationAuthorizationService().isProducerAuthorizedToSendNotificationForChannel(producer, channel));
    }

    @Test
    public void testIsUserAdministrator_validAdmin() {
	    assertTrue(services.getNotificationAuthorizationService().isUserAdministrator(TestConstants.ADMIN_USER_1));
    }

    @Test
    public void testIsUserAdministrator_nonAdmin() {
	    assertFalse(services.getNotificationAuthorizationService().isUserAdministrator(TestConstants.NON_ADMIN_USER_1));
    }

    @Test
    public void testIsUserAdministrator_invalidUser() {
	    assertFalse(services.getNotificationAuthorizationService().isUserAdministrator(TestConstants.INVALID_USER_1));
    }
}
