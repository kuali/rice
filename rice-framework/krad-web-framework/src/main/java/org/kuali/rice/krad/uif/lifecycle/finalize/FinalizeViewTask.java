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
package org.kuali.rice.krad.uif.lifecycle.finalize;

import java.util.Map;

import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.lifecycle.FinalizeComponentPhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTaskBase;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Perform custom finalize behavior for the component defined by the helper.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FinalizeViewTask extends ViewLifecycleTaskBase<View> {

    /**
     * Default constructor.
     */
    public FinalizeViewTask() {
        super(View.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FinalizeComponentPhase getElementState() {
        return (FinalizeComponentPhase) super.getElementState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performLifecycleTask() {
        View view = (View) getElementState().getElement();
        assert view == ViewLifecycle.getView();
        Object model = ViewLifecycle.getModel();

        view.setPreLoadScript(ScriptUtils.appendScript(
                view.getPreLoadScript(), buildClientSideStateScript(model)));
    }

    /**
     * Builds script that will initialize configuration parameters and component state on the client
     * 
     * <p>
     * Here client side state is initialized along with configuration variables that need exposed to
     * script
     * </p>
     * 
     * @param model model containing the client side state map.
     * @return The client side state script associated with this model.
     */
    protected String buildClientSideStateScript(Object model) {
        Map<String, Object> clientSideState = ((ViewModel) model).getClientStateForSyncing();

        // script for initializing client side state on load
        String clientStateScript = "";
        if (!clientSideState.isEmpty()) {
            clientStateScript = ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.INITIALIZE_VIEW_STATE,
                    clientSideState);
        }

        // add necessary configuration parameters
        String kradImageLocation = CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                UifConstants.ConfigProperties.KRAD_IMAGES_URL);
        clientStateScript += ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.SET_CONFIG_PARM,
                UifConstants.ClientSideVariables.KRAD_IMAGE_LOCATION, kradImageLocation);

        String kradURL = CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                UifConstants.ConfigProperties.KRAD_URL);
        clientStateScript += ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.SET_CONFIG_PARM,
                UifConstants.ClientSideVariables.KRAD_URL, kradURL);

        String applicationURL = CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.ConfigParameters.APPLICATION_URL);
        clientStateScript += ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.SET_CONFIG_PARM,
                UifConstants.ClientSideVariables.APPLICATION_URL, applicationURL);

        String scriptCleanup = CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.ConfigParameters.KRAD_SCRIPT_CLEANUP);
        clientStateScript += ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.SET_CONFIG_PARM,
                UifConstants.ClientSideVariables.KRAD_SCRIPT_CLEANUP, scriptCleanup);

        return clientStateScript;
    }

}
