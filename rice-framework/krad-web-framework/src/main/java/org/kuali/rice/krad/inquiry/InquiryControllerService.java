/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.inquiry;

import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krad.web.service.ControllerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller service that extends {@link org.kuali.rice.krad.web.service.ControllerService} and adds
 * methods specific to inquiry views.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface InquiryControllerService extends ControllerService {

    /**
     * When the data object is a {@link org.kuali.rice.krad.bo.PersistableAttachment} or
     * {@link org.kuali.rice.krad.bo.PersistableAttachmentList}, streams the selected attachment back to the
     * response.
     *
     * @param form form instance containing the inquiry data
     * @param response Http response for returning the attachment contents
     */
    void downloadDataObjectAttachment(InquiryForm form, HttpServletResponse response);

    /**
     * When the data object is a custom object, streams the requested attachment back to the
     * response.
     *
     * @param form form instance containing the inquiry data
     * @param request Http request for sending the fileName, contentType, and fileContentDataObjField
     * @param response Http response for returning the attachment contents
     */
    void downloadCustomDataObjectAttachment(InquiryForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception;
}
