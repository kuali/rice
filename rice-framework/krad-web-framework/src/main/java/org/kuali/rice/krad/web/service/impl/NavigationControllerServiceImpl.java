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
package org.kuali.rice.krad.web.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.HistoryFlow;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.kuali.rice.krad.web.service.NavigationControllerService;
import org.springframework.web.servlet.ModelAndView;

import java.util.Properties;

/**
 * Default implementation of the navigation controller service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NavigationControllerServiceImpl implements NavigationControllerService {

    private ModelAndViewService modelAndViewService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView back(UifFormBase form) {
        boolean returnToFlowStart = false;

        String returnToStartActionParm = form.getActionParamaterValue(UifConstants.HistoryFlow.RETURN_TO_START);
        if (StringUtils.isNotBlank(returnToStartActionParm)) {
            returnToFlowStart = Boolean.parseBoolean(returnToStartActionParm);
        }

        return returnToHistory(form, true, false, returnToFlowStart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView returnToPrevious(UifFormBase form) {
        return returnToHistory(form, true, false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView returnToHub(UifFormBase form) {
        return returnToHistory(form, false, false, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView returnToHistory(UifFormBase form, boolean returnToPrevious, boolean returnToApplicationHome,
            boolean returnToFlowStart) {
        Properties props = new Properties();
        props.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.REFRESH);

        if (StringUtils.isNotBlank(form.getReturnFormKey())) {
            props.put(UifParameters.FORM_KEY, form.getReturnFormKey());
        }

        GlobalVariables.getUifFormManager().removeSessionForm(form);

        String returnUrl = getReturnUrl(form, returnToPrevious, returnToApplicationHome, returnToFlowStart);

        return getModelAndViewService().performRedirect(form, returnUrl, props);
    }

    /**
     * Gets the URL to return to based form data and the given flags.
     *
     * @param form form instance containing return location (possibly) and history manager
     * @param returnToPrevious whether we should return to the previous view
     * @param returnToApplicationHome whether we should return to the configured application home
     * @param returnToFlowStart when in a flow, whether to return to the flow start point
     * @return String URL
     */
    protected String getReturnUrl(UifFormBase form, boolean returnToPrevious, boolean returnToApplicationHome,
            boolean returnToFlowStart) {
        String returnUrl = null;

        if (returnToPrevious) {
            returnUrl = form.getReturnLocation();
        } else {
            HistoryFlow historyFlow = form.getHistoryManager().getMostRecentFlowByFormKey(form.getFlowKey(),
                    form.getRequestedFormKey());
            if (historyFlow != null) {
                // we are in a flow
                returnUrl = historyFlow.getFlowReturnPoint();

                if (returnToFlowStart && StringUtils.isNotBlank(historyFlow.getFlowStartPoint())) {
                    returnUrl = historyFlow.getFlowStartPoint();
                }
            }
        }

        if (StringUtils.isBlank(returnUrl) || returnToApplicationHome) {
            returnUrl = ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.APPLICATION_URL_KEY);
        }

        return returnUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView navigate(UifFormBase form) {
        form.setDirtyForm(false);

        String pageId = form.getActionParamaterValue(UifParameters.NAVIGATE_TO_PAGE_ID);

        return getModelAndViewService().getModelAndView(form, pageId);
    }

    protected ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }
}
