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

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.ControllerService;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.kuali.rice.krad.web.service.NavigationControllerService;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ControllerServiceImpl implements ControllerService {

    private ModelAndViewService modelAndViewService;
    private NavigationControllerService navigationControllerService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView start(UifFormBase form) {
        checkViewAuthorization(form);

        if (form.getView() != null) {
            form.setApplyDefaultValues(true);
        }

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkViewAuthorization(UifFormBase form) throws AuthorizationException {
        // if user session or view not established we cannnot authorize the view request
        View view = form.getView();
        if ((GlobalVariables.getUserSession() == null) || view == null) {
            return;
        }

        Person user = GlobalVariables.getUserSession().getPerson();
        boolean viewAuthorized = view.getAuthorizer().canOpenView(view, form, user);

        if (!viewAuthorized) {
            throw new AuthorizationException(user.getPrincipalName(), "open", view.getId(),
                    "User '" + user.getPrincipalName() + "' is not authorized to open view ID: " +
                            view.getId(), null);
        }
    }

    /**
     * Default impl does nothing but render the view.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView sessionTimeout(UifFormBase form) {
        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * Navigates back to a previous point (depending on how the view was requested).
     *
     * {@inheritDoc}
     * org.kuali.rice.krad.web.service.impl.NavigationControllerServiceImpl#back(org.kuali.rice.krad.web.form.UifFormBase,
     * boolean)
     */
    @Override
    public ModelAndView cancel(UifFormBase form) {
        GlobalVariables.getUifFormManager().removeSessionForm(form);

        return getNavigationControllerService().returnToHistory(form, false, false, true);
    }

    protected ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }

    protected NavigationControllerService getNavigationControllerService() {
        return navigationControllerService;
    }

    public void setNavigationControllerService(NavigationControllerService navigationControllerService) {
        this.navigationControllerService = navigationControllerService;
    }
}
