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
package org.kuali.rice.krad.lookup;

import org.kuali.rice.krad.web.service.ControllerService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller service that provides methods for supporting a lookup view.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LookupControllerService extends ControllerService {

    /**
     * Executes a search using the provided criteria and builds a list of results to return
     * to the user.
     *
     * @param lookupForm form instance containing the lookup data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView search(LookupForm lookupForm);

    /**
     * Performs a reset (or clear) on the lookup criteria values.
     *
     * @param lookupForm form instance containing the lookup data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView clearValues(LookupForm lookupForm);

    /**
     * Handles the select all pages action on the lookup results.
     *
     * @param lookupForm form instance containing the lookup data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView selectAllPages(LookupForm lookupForm);

    /**
     * Handles the deselect all pages action on the lookup results.
     *
     * @param lookupForm form instance containing the lookup data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView deselectAllPages(LookupForm lookupForm);

    /**
     * Invoked from the UI to return the selected lookup results lines back to the calling view.
     *
     * @param lookupForm form instance containing the lookup data
     * @param redirectAttributes spring provided redirect attributes
     * @return String url for redirecting back to the lookup caller
     */
    String returnSelected(LookupForm lookupForm, RedirectAttributes redirectAttributes);
}
