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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kcb.bo.RecipientDelivererConfig;
import org.kuali.rice.kcb.bo.RecipientPreference;
import org.kuali.rice.kcb.deliverer.MessageDeliverer;
import org.kuali.rice.kcb.exception.ErrorList;
import org.kuali.rice.kcb.service.RecipientPreferenceService;
import org.kuali.rice.krad.data.DataObjectService;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * RecipientPreferenceService implementation 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RecipientPreferenceServiceImpl implements RecipientPreferenceService {

    private DataObjectService dataObjectService;

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#getRecipientPreference(java.lang.String, java.lang.String)
     */
    public RecipientPreference getRecipientPreference(String recipientId, String key) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(
                equal(RecipientPreference.RECIPIENT_FIELD, recipientId),
                equal(RecipientPreference.PROPERTY_FIELD, key)
        );
        List<RecipientPreference> prefs = dataObjectService.findMatching(RecipientPreference.class, criteria.build()).getResults();
        assert(prefs.size() <= 1);

        if (prefs.isEmpty()) {
            return null;
        } else {
            return prefs.get(0);
        }
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#deleteRecipientPreference(org.kuali.rice.kcb.bo.RecipientPreference)
     */
    public void deleteRecipientPreference(RecipientPreference pref) {
        dataObjectService.delete(pref);
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#getRecipientPreferences(java.lang.String)
     */
    public HashMap<String, String> getRecipientPreferences(String recipientId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(RecipientPreference.RECIPIENT_FIELD, recipientId));
        List<RecipientPreference> userPrefs = dataObjectService.findMatching(RecipientPreference.class, criteria.build()).getResults();

        HashMap<String, String> prefs = new HashMap<String,String>();
        for (RecipientPreference p: userPrefs) {
            prefs.put(p.getProperty(), p.getValue());
        }

        return prefs;
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#saveRecipientPreference(org.kuali.rice.kcb.bo.RecipientPreference)
     */
    public RecipientPreference saveRecipientPreference(RecipientPreference pref) {
        return dataObjectService.save(pref);
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#saveRecipientPreferences(java.lang.String, java.util.HashMap, org.kuali.rice.kcb.deliverer.MessageDeliverer)
     */
    public void saveRecipientPreferences(String recipientId, HashMap<String, String> prefs, MessageDeliverer deliverer) throws ErrorList {
        deliverer.validatePreferenceValues(prefs);         
        
        for (Map.Entry<String, String> entry: prefs.entrySet()) {
           String prop = entry.getKey();
           String value = entry.getValue();
               
           // We need to check if this property is already set
           // for the user by checking doing a unique key query...if
           // it already exists, update, otherwise add it 
           RecipientPreference currentPreference = getRecipientPreference(recipientId, prop);
           if (currentPreference != null) {
              currentPreference.setRecipientId(recipientId);
              currentPreference.setProperty(prop);
              currentPreference.setValue(value);
              dataObjectService.save(currentPreference);
           } else {
              RecipientPreference recipientPreference = new RecipientPreference();
              recipientPreference.setRecipientId(recipientId);
              recipientPreference.setProperty(prop);
              recipientPreference.setValue(value);
              dataObjectService.save(recipientPreference);
           }
        }
    }

    // deliverer config
    
    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#removeRecipientDelivererConfigs(java.lang.String)
     */
    public void removeRecipientDelivererConfigs(String recipientId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(RecipientDelivererConfig.RECIPIENT_ID, recipientId));
        dataObjectService.deleteMatching(RecipientDelivererConfig.class, criteria.build());
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#saveRecipientDelivererConfig(java.lang.String, java.lang.String, java.lang.String[])
     */
    public void saveRecipientDelivererConfig(String recipientId, String delivererName, String[] channels) {
        if (channels == null || channels.length == 0) return;
    
        // if selected[0] is 0 we want to remove this deliverer
        // for all channels.  We already did that above.
        for (String channel: channels) {
            RecipientDelivererConfig config = new RecipientDelivererConfig();

            config.setRecipientId(recipientId);
            config.setDelivererName(delivererName);
            config.setChannel(channel);
            
            // first, verify that we aren't trying to insert a duplicate
            Collection<RecipientDelivererConfig> deliverers = getDeliverersForRecipientAndChannel(recipientId, channel);
            if (deliverers != null) {
            	for (RecipientDelivererConfig deliverer : deliverers) {
            		if (deliverer.getDelivererName().equals(delivererName)) {
            			throw new RiceRuntimeException("Attempting to save a duplicate Recipient Deliverer Config.");
            		}
            	}
            }
            dataObjectService.save(config);
        }
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#getDeliverersForRecipient(java.lang.String)
     */
    public Collection<RecipientDelivererConfig> getDeliverersForRecipient(String recipientId) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(RecipientDelivererConfig.RECIPIENT_ID, recipientId));

        return dataObjectService.findMatching(RecipientDelivererConfig.class, criteria.build()).getResults();
    }

    /**
     * @see org.kuali.rice.kcb.service.RecipientPreferenceService#getDeliverersForRecipientAndChannel(java.lang.String, java.lang.String)
     */
    public Collection<RecipientDelivererConfig> getDeliverersForRecipientAndChannel(String recipientId, String channel) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(
                equal(RecipientDelivererConfig.RECIPIENT_ID, recipientId),
                equal(RecipientDelivererConfig.CHANNEL, channel)
        );

        return dataObjectService.findMatching(RecipientDelivererConfig.class, criteria.build()).getResults();
    }

    /**
     * Sets the data object service.
     * @param dataObjectService service to persist data to the datasource.
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
