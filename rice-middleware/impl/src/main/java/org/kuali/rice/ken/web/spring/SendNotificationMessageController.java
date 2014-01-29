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
package org.kuali.rice.ken.web.spring;

import org.apache.log4j.Logger;
import org.kuali.rice.ken.bo.NotificationBo;
import org.kuali.rice.ken.bo.NotificationContentTypeBo;
import org.kuali.rice.ken.exception.ErrorList;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.ken.util.Util;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * This class is the controller for sending Simple notification messages via an end user interface.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SendNotificationMessageController extends BaseSendNotificationController {

    private static final Logger LOG = Logger.getLogger(SendNotificationMessageController.class);

    /**
     * Handles the display of the form for sending a simple notification message.
     *
     * @param request : a servlet request
     * @param response the servlet response
     *
     * @return the next view to move to
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView sendSimpleNotificationMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = "SendSimpleNotificationMessage";

        LOG.debug("remoteUser: " + request.getRemoteUser());

        Map<String, Object> model = setupModelForSendNotification(request);
        model.put("errors", new ErrorList()); // need an empty one so we don't have an NPE

        return new ModelAndView(view, model);
    }

    /**
     * Handles submitting the actual simple notification message.
     *
     * @param request the servlet request
     * @param response the servlet response
     *
     * @return the next view to move to
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView submitSimpleNotificationMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String routeMessage = "This message was submitted via the simple notification message submission form by user ";
        String viewName = "SendSimpleNotificationMessage";

        return submitNotificationMessage(request, routeMessage, viewName);
    }

    /**
     * {@inheritDoc}
     *
     * Overrides to set the content type to "simple".
     */
    @Override
    protected NotificationBo createNotification(HttpServletRequest request, Map<String, Object> model,
            ErrorList errors) throws ErrorList {
        NotificationBo notification = super.createNotification(request, model, errors);

        String message = getParameter(request, "message", model, errors, "You must fill in a message.");

        // stop processing if there are errors
        if (!errors.getErrors().isEmpty()) {
            throw errors;
        }

        NotificationContentTypeBo contentType = Util.retrieveFieldReference("contentType", "name",
                NotificationConstants.CONTENT_TYPES.SIMPLE_CONTENT_TYPE, NotificationContentTypeBo.class,
                dataObjectService, Boolean.TRUE);
        notification.setContentType(contentType);

        notification.setContent(NotificationConstants.XML_MESSAGE_CONSTANTS.CONTENT_SIMPLE_OPEN
                + NotificationConstants.XML_MESSAGE_CONSTANTS.MESSAGE_OPEN
                + message
                + NotificationConstants.XML_MESSAGE_CONSTANTS.MESSAGE_CLOSE
                + NotificationConstants.XML_MESSAGE_CONSTANTS.CONTENT_CLOSE);

        return notification;
    }
}
