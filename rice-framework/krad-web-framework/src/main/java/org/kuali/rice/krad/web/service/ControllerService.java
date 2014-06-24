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
package org.kuali.rice.krad.web.service;

import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller service that provides the basic entry and exit methods.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ControllerService {

    /**
     * Initial method called when requesting a new view instance.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView start(UifFormBase form);

    /**
     * Invoked when a session timeout occurs.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView sessionTimeout(UifFormBase form);

    /**
     * Invoked when the cancel action is invoked on a view.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView cancel(UifFormBase form);
}
