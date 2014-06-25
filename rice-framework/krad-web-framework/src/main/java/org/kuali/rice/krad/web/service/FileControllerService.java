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

import javax.servlet.http.HttpServletResponse;

/**
 * Controller service that provides methods for working with the multi-file upload component.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface FileControllerService {

    /**
     * Invoked by the multiFile upload element to add a file object to the collection it controls.
     *
     * @param form form instance containing the file data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView addFileUploadLine(UifFormBase form);

    /**
     * Invoked by the multiFile upload widget to delete a file; Inform the model of file to delete.
     *
     * @param form form instance containing the file data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView deleteFileUploadLine(UifFormBase form);

    /**
     * Invoked by the multiFile upload widget to get the file contents for a file upload line.
     *
     * @param form form instance containing the file request data
     * @param response Http response for streaming back the file contents
     */
    void getFileFromLine(UifFormBase form, HttpServletResponse response);
}
