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
 * Controller service that provides methods for working with collection groups.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface CollectionControllerService {

    /**
     * Invoked by the add line action to add the add line instance to the model collection.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView addLine(UifFormBase form);

    /**
     * Invoked by the add blank line action to add a new line instance to the model mollection.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView addBlankLine(UifFormBase form);

    /**
     * Invoked by the save line action to save an item within the model collection.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView saveLine(UifFormBase form);

    /**
     * Invoked by the delete line action to delete an item within the model collection.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView deleteLine(UifFormBase form);

    /**
     * Invoked by the table paging widget to retrieve a page for a collection group.
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView retrieveCollectionPage(UifFormBase form);

    /**
     * Get method for getting aaData for jquery datatables which are using sAjaxSource option.
     *
     * <p>This will render the aaData JSON for the displayed page of the table matching the tableId passed in the
     * request parameters.</p>
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView tableJsonRetrieval(UifFormBase form);
}
