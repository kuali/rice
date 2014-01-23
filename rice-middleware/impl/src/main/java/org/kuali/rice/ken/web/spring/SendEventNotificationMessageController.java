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
import java.util.Date;
import java.util.Map;

/**
 * This class is the controller for sending Event notification messages via an end user interface.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SendEventNotificationMessageController extends BaseSendNotificationController {

    private static final Logger LOG = Logger.getLogger(SendEventNotificationMessageController.class);

    /**
     * Handles the display of the form for sending a event notification message.
     *
     * @param request : a servlet request
     * @param response the servlet response
     *
     * @return the next view to move to
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView sendEventNotificationMessage(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String view = "SendEventNotificationMessage";

        LOG.debug("remoteUser: " + request.getRemoteUser());

        Map<String, Object> model = setupModelForSendNotification(request);
        model.put("errors", new ErrorList()); // need an empty one so we don't have an NPE

        return new ModelAndView(view, model);
    }

    /**
     * Handles submitting the actual event notification message.
     *
     * @param request the servlet request
     * @param response the servlet response
     *
     * @return the next view to move to
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView submitEventNotificationMessage(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String routeMessage = "This message was submitted via the event notification message submission form by user ";
        String viewName = "SendEventNotificationMessage";

        return submitNotificationMessage(request, routeMessage, viewName);
    }

    /**
     * {@inheritDoc}
     *
     * Populates values pertaining to an event notification message.
     */
    @Override
    protected Map<String, Object> setupModelForSendNotification(HttpServletRequest request) {
        Map<String, Object> model = super.setupModelForSendNotification(request);

        model.put("summary", request.getParameter("summary"));
        model.put("description", request.getParameter("description"));
        model.put("location", request.getParameter("location"));
        model.put("startDateTime", request.getParameter("startDateTime"));
        model.put("stopDateTime", request.getParameter("stopDateTime"));

        return model;
    }

    /**
     * {@inheritDoc}
     *
     * Overrides to set the content type to "event" and add extra attributes.
     */
    @Override
    protected NotificationBo createNotification(HttpServletRequest request, Map<String, Object> model,
            ErrorList errors) throws ErrorList {
        NotificationBo notification = super.createNotification(request, model, errors);

        String message = getParameter(request, "message", model, errors, "You must fill in a message.");

        String summary = getParameter(request, "summary", model, errors, "You must fill in a summary.");
        String description = getParameter(request, "description", model, errors, "You must fill in a description.");
        String location = getParameter(request, "location", model, errors, "You must fill in a location.");

        String startDateTime = request.getParameter("startDateTime");
        Date startDate = getDate(startDateTime, errors,
                "You specified an invalid start date and time.  Please use the calendar picker.");
        if (startDate != null) {
            model.put("startDateTime", startDateTime);
        }

        String stopDateTime = request.getParameter("stopDateTime");
        Date stopDate = getDate(stopDateTime, errors,
                "You specified an invalid start date and time.  Please use the calendar picker.");
        if (stopDate != null) {
            model.put("stopDateTime", stopDateTime);
        } else {

        }

        if (stopDate != null && startDate != null) {
            if (stopDate.before(startDate)) {
                errors.addError("Event Stop Date/Time cannot be before Event Start Date/Time.");
            }
        }

        // stop processing if there are errors
        if (!errors.getErrors().isEmpty()) {
            throw errors;
        }

        NotificationContentTypeBo contentType = Util.retrieveFieldReference("contentType", "name",
                NotificationConstants.CONTENT_TYPES.EVENT_CONTENT_TYPE, NotificationContentTypeBo.class,
                dataObjectService, Boolean.TRUE);
        notification.setContentType(contentType);

        notification.setContent(NotificationConstants.XML_MESSAGE_CONSTANTS.CONTENT_EVENT_OPEN
                + NotificationConstants.XML_MESSAGE_CONSTANTS.MESSAGE_OPEN
                + message
                + NotificationConstants.XML_MESSAGE_CONSTANTS.MESSAGE_CLOSE
                + "<event>\n"
                + "  <summary>"
                + summary
                + "</summary>\n"
                + "  <description>"
                + description
                + "</description>\n"
                + "  <location>"
                + location
                + "</location>\n"
                + "  <startDateTime>"
                + Util.toUIDateTimeString(startDate)
                + "</startDateTime>\n"
                + "  <stopDateTime>"
                + Util.toUIDateTimeString(stopDate)
                + "</stopDateTime>\n"
                + "</event>"
                + NotificationConstants.XML_MESSAGE_CONSTANTS.CONTENT_CLOSE);

        return notification;
    }
}
