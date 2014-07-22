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
 * Controller service that handles saves.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface SaveControllerService {

    /**
     * Save the form.
     *
     * <p>Save is not implemented by KRAD and must be overriden with an implementation to persist changes
     * to a db.  By default, this will just refresh the View.</p>
     *
     * @param form the form
     * @return ModelAndView with the updated content
     */
    ModelAndView save(UifFormBase form);

    /**
     * Save the field of the form at the propertyName specified by saveFieldPath (in request params sent); the
     * field is already updated on the form at the time of this invocation.
     *
     * <p>SaveField is not implemented by KRAD and must be overriden with an implementation to persist changes
     * to a db.  By default, this will just refresh the field specified by updateComponentId.</p>
     *
     * @param form the form with the updated field
     * @return ModelAndView which contains the updated component
     */
    ModelAndView saveField(UifFormBase form);

}
