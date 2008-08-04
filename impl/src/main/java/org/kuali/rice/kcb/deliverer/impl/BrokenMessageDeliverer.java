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
package org.kuali.rice.kcb.deliverer.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.deliverer.MessageDeliverer;
import org.kuali.rice.kcb.exception.ErrorList;
import org.kuali.rice.kcb.exception.MessageDeliveryException;
import org.kuali.rice.kcb.exception.MessageDismissalException;

/**
 * A message deliverer that just throws runtime exceptions 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BrokenMessageDeliverer implements MessageDeliverer {

    /**
     * @see org.kuali.rice.kcb.deliverer.MessageDeliverer#deliver(org.kuali.rice.kcb.bo.MessageDelivery)
     */
    public void deliver(MessageDelivery messageDelivery) throws MessageDeliveryException {
        throw new RuntimeException("Intentional exception on deliver");
    }

    /**
     * @see org.kuali.rice.kcb.deliverer.MessageDeliverer#dismiss(org.kuali.rice.kcb.bo.MessageDelivery, java.lang.String, java.lang.String)
     */
    public void dismiss(MessageDelivery messageDelivery, String user, String cause) throws MessageDismissalException {
        throw new RuntimeException("Intentional exception on dismiss");
    }

    /**
     * @see org.kuali.rice.kcb.deliverer.MessageDeliverer#getDescription()
     */
    public String getDescription() {
        // TODO arh14 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * @see org.kuali.rice.kcb.deliverer.MessageDeliverer#getName()
     */
    public String getName() {
        return "BrokenMessageDeliverer";
    }

    /**
     * @see org.kuali.rice.kcb.deliverer.MessageDeliverer#getPreferenceKeys()
     */
    public LinkedHashMap<String, String> getPreferenceKeys() {
        return new LinkedHashMap<String, String>(0);
    }

    /**
     * @see org.kuali.rice.kcb.deliverer.MessageDeliverer#getTitle()
     */
    public String getTitle() {
        return "Broken Message Deliverer";
    }

    /**
     * @see org.kuali.rice.kcb.deliverer.MessageDeliverer#validatePreferenceValues(java.util.HashMap)
     */
    public void validatePreferenceValues(HashMap<String, String> prefs) throws ErrorList {
    }
}