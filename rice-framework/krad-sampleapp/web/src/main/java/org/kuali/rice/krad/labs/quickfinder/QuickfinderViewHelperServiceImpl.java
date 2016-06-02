/**
 * Copyright 2005-2016 The Kuali Foundation
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
 *
 * Cheezy example of using callback methods.  Each method is specified in
 * LabsQuickFinderCallback.xml.
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
    public void doCallbackTable1( UifFormBase form, String quickfinderId, Map<String, String> callbackContext ) {
        QuickfinderForm qForm = (QuickfinderForm)form;
        List<PersonAccount> collection = qForm.getPersonAccounts1();
        conditionallyModAccountName(callbackContext, collection, "Foo Bar");
    }

    public void doCallbackTable2( UifFormBase form, String quickfinderId, Map<String, String> callbackContext ) {
        QuickfinderForm qForm = (QuickfinderForm)form;
        List<PersonAccount> collection = qForm.getPersonAccounts2();
        conditionallyModAccountName(callbackContext, collection, "Yo");
    }

    public void doCallbackTable3( UifFormBase form, String quickfinderId, Map<String, String> callbackContext ) {
        QuickfinderForm qForm = (QuickfinderForm)form;
        List<PersonAccount> collection = qForm.getPersonAccounts3();
        conditionallyModAccountName(callbackContext, collection, "KaBoom");
    }

    public void doCallbackTable4( UifFormBase form, String quickfinderId, Map<String, String> callbackContext ) {
        QuickfinderForm qForm = (QuickfinderForm)form;
        List<PersonAccount> collection = qForm.getPersonAccounts4();
        conditionallyModAccountName(callbackContext, collection, "Far Out");
    }

    public void doCallbackTable5( UifFormBase form, String quickfinderId, Map<String, String> callbackContext ) {
        QuickfinderForm qForm = (QuickfinderForm)form;
        List<PersonAccount> collection = qForm.getPersonAccounts5();
        conditionallyModAccountName(callbackContext, collection, "Yee Haw");
    }

    private void conditionallyModAccountName(Map<String, String> callbackContext, List<PersonAccount> collection,
            String newAccountName) {

        if (callbackContext != null) {
            String lineIndexAsString = callbackContext
                    .get(UifConstants.PostMetadata.QUICKFINDER_CALLBACK_CONTEXT_PROPERTY_LINE_INDEX);

            if (lineIndexAsString != null) {
                int lineIndex = Integer.parseInt(lineIndexAsString);
                if (lineIndex >= 0) {
                    PersonAccount personAccount = collection.get(lineIndex);
                    personAccount.setAccountName(newAccountName);
                }
            }
        }
    }
}
