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

import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.kuali.rice.krad.web.service.SaveControllerService;
import org.springframework.web.servlet.ModelAndView;

/**
 * Default implementation of the save controller service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SaveControllerServiceImpl implements SaveControllerService{

    private ModelAndViewService modelAndViewService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView save(UifFormBase form) {
        // This is a hook method for persistence of a full form save
        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView saveField(UifFormBase form) {
        // This is a hook method for persistence of a single field save
        return getModelAndViewService().getModelAndView(form);
    }

    public ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }
}
