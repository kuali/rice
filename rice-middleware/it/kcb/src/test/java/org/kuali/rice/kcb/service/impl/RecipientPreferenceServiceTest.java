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
package org.kuali.rice.kcb.service.impl;

import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kcb.bo.RecipientPreference;
import org.kuali.rice.kcb.deliverer.MessageDeliverer;
import org.kuali.rice.kcb.deliverer.impl.EmailMessageDeliverer;
import org.kuali.rice.kcb.exception.ErrorList;
import org.kuali.rice.kcb.service.MessageDelivererRegistryService;
import org.kuali.rice.kcb.service.RecipientPreferenceService;
import org.kuali.rice.kcb.test.KCBTestCase;
import org.kuali.rice.kcb.test.TestConstants;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * Tests {@link RecipientPreferenceService}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineMode(Mode.ROLLBACK_CLEAR_DB)
public class RecipientPreferenceServiceTest extends KCBTestCase {

    public static final String VALID_DELIVERER_NAME = EmailMessageDeliverer.NAME;
    public static final String VALID_PROPERTY = EmailMessageDeliverer.NAME + "." + EmailMessageDeliverer.EMAIL_ADDR_PREF_KEY;
    public static final String VALID_VALUE = TestConstants.EMAIL_DELIVERER_PROPERTY_VALUE;
    public static final String VALID_USER_ID = "user1"; // any user will do as KCB has no referential integrity in this regard

    @Test
    public void saveRecipientPreferences() throws ErrorList {
        RecipientPreferenceService impl = services.getRecipientPreferenceService();
        MessageDelivererRegistryService delivererService = services.getMessageDelivererRegistryService();
        MessageDeliverer deliverer = delivererService.getDelivererByName(VALID_DELIVERER_NAME);
        if (deliverer == null) {
            throw new RuntimeException("Message deliverer could not be obtained");
        }
            
        HashMap<String, String> userprefs = new HashMap<String, String>();
        userprefs.put(VALID_PROPERTY, VALID_VALUE);
        userprefs.put("Email.email_delivery_format", "text");

        impl.saveRecipientPreferences(VALID_USER_ID, userprefs, deliverer);

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("recipientId", VALID_USER_ID));
        List<RecipientPreference> prefs =
                KRADServiceLocator.getDataObjectService().findMatching(RecipientPreference.class, criteria.build()).getResults();
        assertEquals(2, prefs.size());
    }
}
