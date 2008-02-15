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
package org.kuali.rice.kcb.service.impl;

import java.util.Collection;
import java.util.HashMap;

import org.kuali.rice.kcb.GlobalKCBServiceLocator;
import org.kuali.rice.kcb.bo.RecipientPreference;
import org.kuali.rice.kcb.deliverer.MessageDeliverer;
import org.kuali.rice.kcb.exception.ErrorList;
import org.kuali.rice.kcb.service.RecipientPreferenceService;

/**
 * RecipientPreferenceService implementation 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RecipientPreferenceServiceImpl extends BusinessObjectServiceImpl implements RecipientPreferenceService {
    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#getDeliverersForRecipient(java.lang.String)
     */
    public Collection<String> getDeliverersForRecipient(String recipientId) {
        // TODO: implement for real
        return GlobalKCBServiceLocator.getInstance().getMessageDelivererRegistryService().getAllDelivererTypes();
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#getDeliverersForRecipientAndChannel(java.lang.String, java.lang.String)
     */
    public Collection<String> getDeliverersForRecipientAndChannel(String recipientId, String channel) {
        // TODO: implement for real
        return GlobalKCBServiceLocator.getInstance().getMessageDelivererRegistryService().getAllDelivererTypes();
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#getRecipientPreference(java.lang.String, java.lang.String)
     */
    public RecipientPreference getRecipientPreference(String recipientId, String key) {
        // TODO arh14 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#getRecipientPreferences(java.lang.String)
     */
    public HashMap<String, String> getRecipientPreferences(String recipientId) {
        // TODO arh14 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#saveRecipientPreference(org.kuali.rice.kcb.bo.RecipientPreference)
     */
    public void saveRecipientPreference(RecipientPreference pref) {
    // TODO arh14 - THIS METHOD NEEDS JAVADOCS

    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#saveRecipientPreferences(java.lang.String, java.util.HashMap, org.kuali.rice.kcb.deliverer.MessageDeliverer)
     */
    public void saveRecipientPreferences(String userid, HashMap<String, String> prefs, MessageDeliverer deliverer) throws ErrorList {
    // TODO arh14 - THIS METHOD NEEDS JAVADOCS

    }
}