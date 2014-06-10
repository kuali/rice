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
package org.kuali.rice.krad.labs.quickfinder;

import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.List;
import java.util.Map;

/**
 * Quickfinder's view service implementation class that handles callbacks.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class QuickfinderViewHelperServiceImpl extends LookupableImpl {

    /**
     * Retrieves the collection from the form, and depending on the context parameters given, it
     * manipulates the object in the collection.
     *
     * @param form - the form that has the collection
     * @param quickfinderId - the quickfinder id of the lookup component
     * @param callbackContext - a map of parameters to manipulate the collection
     */
    public void doCallback( UifFormBase form, String quickfinderId, Map<String, String> callbackContext ) {
        QuickfinderForm qForm = ( QuickfinderForm ) form;

        List<PersonAccount> collection = qForm.getPeopleAccounts();

        if( callbackContext != null ) {
            String lineIndexAsString = callbackContext
                    .get( UifConstants.PostMetadata.QUICKFINDER_CALLBACK_CONTEXT_PROPERTY_LINE_INDEX );

            if( lineIndexAsString != null ) {
                int lineIndex = Integer.parseInt( lineIndexAsString );
                if( lineIndex >= 0 ) {
                    PersonAccount personAccount = collection.get(lineIndex);
                    personAccount.setAccountName("Foo Bar");
                }
            }
        }
    }
}
