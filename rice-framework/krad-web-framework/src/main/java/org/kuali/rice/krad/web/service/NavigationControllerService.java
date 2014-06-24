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
 * Controller service that provides methods for navigating within the application and a view.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface NavigationControllerService {

    /**
     * Returns back to a previous URL by looking at various return mechanisms in HistoryFlow and on the form.
     *
     * <p>Method supports the general back action component.</p>
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView back(UifFormBase form);

    /**
     * Invoked to navigate back one page in the user's history.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView returnToPrevious(UifFormBase form);

    /**
     * Invoked to navigate back to the first page in the user's history.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView returnToHub(UifFormBase form);

    /**
     * Invoked to navigate back to a point in the user's history.
     *
     * @param form form instance containing the model data
     * @param returnToApplicationHome indicates whether the user should be returned to the application home URL
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView returnToHistory(UifFormBase form, boolean returnToPrevious, boolean returnToApplicationHome,
                boolean returnToFlowStart);

    /**
     * Invoked to navigate to a new page within the view.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView navigate(UifFormBase form);

}
