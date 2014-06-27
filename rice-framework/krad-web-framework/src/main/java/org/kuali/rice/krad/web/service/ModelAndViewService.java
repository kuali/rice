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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Properties;

/**
 * Service that provides helper methods for building {@link org.springframework.web.servlet.ModelAndView} instances
 * and also other controller helper methods such as showing a dialog, a message view, or redirecting.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ModelAndViewService {

    /**
     * Handles the check form action to validate the given form data.
     *
     * @param form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView checkForm(UifFormBase form);

    /**
     * Invoked by controller methods to show a dialog to the user.
     *
     * <p>This will return back to the view and display the dialog to the user. When the users chooses a response,
     * the initial action that triggered the controller method (which called showDialog) will be triggered again. The
     * response will be captured in the form property
     * {@link org.kuali.rice.krad.web.form.UifFormBase#getDialogResponses()}. In the case of a confirmation, if 'false'
     * (typically labeled cancel) is choosen the initial action will simply not be triggered again.</p>
     *
     * @param dialogId id for the dialog group to show
     * @param confirmation whether the dialog should be shown as a confirmation, in this case it is expected the
     * options are true (continue) or false (stop). In addition, if the user selects the false option, the dialog
     * answer will not be resubmitted to the server, instead the user will remain on the view
     * @param form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView showDialog(String dialogId, boolean confirmation, UifFormBase form);

    /**
     * Builds an URL from the given base URL and parameters, then builds a model and view instance
     * configured to redirect to the built URL.
     *
     * @param form form instance containing the model data
     * @param baseUrl base URL to redirect to
     * @param urlParameters parameters to add to the base URL
     * @return ModelAndView instance configured to redirect to the built URL
     */
    ModelAndView performRedirect(UifFormBase form, String baseUrl, Properties urlParameters);

    /**
     * Builds a model and view instance configured to redirect to the given URL.
     *
     * @param form instance containing the model data
     * @param redirectUrl URL to redirect to
     * @return ModelAndView instance configured to redirect to the given URL
     */
    ModelAndView performRedirect(UifFormBase form, String redirectUrl);

    /**
     * Builds a message view from the given header and message text then forwards the UIF model and view.
     *
     * <p>If an error or other type of interruption occurs during the request processing the controller can
     * invoke this message to display the message to the user. This will abandon the view that was requested
     * and display a view with just the message</p>
     *
     * @param form instance containing the model data
     * @param headerText header text for the message view (can be blank)
     * @param messageText text for the message to display
     * @return instance for rendering the view
     */
    ModelAndView getMessageView(UifFormBase form, String headerText, String messageText);

    /**
     * Configures the Spring model and view instance containing the form data and pointing to the
     * generic Uif view.
     *
     * <p>The view and page to render is assumed to be set in the given form object.</p>
     *
     * @param form form instance containing the model data
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView getModelAndView(UifFormBase form);

    /**
     * Configures the Spring model and view instance containing the form data and pointing to the
     * generic Uif view, and also changes the current page to the given page id.
     *
     * <p>The view to render is assumed to be set in the given form object.</p>
     *
     * @param form form instance containing the model data
     * @param pageId page id within the view to render
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView getModelAndView(UifFormBase form, String pageId);

    /**
     * Configures the Spring model and view instance containing the form data and pointing to the
     * generic Uif view, and also adds the given attributes (which can be referenced in the view templates).
     *
     * <p>The view and page to render is assumed to be set in the given form object.</p>
     *
     * @param form form instance containing the model data
     * @param additionalViewAttributes additional attributes to add to the returned model and view
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView getModelAndView(UifFormBase form, Map<String, Object> additionalViewAttributes);

    /**
     * Initialize a new view instance for the given view id, then configures the Spring model and view
     * instance containing the form data and pointing to the generic Uif view.
     *
     * <p>This can be used by controllers to render a different view from the one initially requested (if any)</p>
     *
     * @param form form instance containing the model data
     * @param viewId id for the view to initialize
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView getModelAndViewWithInit(UifFormBase form, String viewId);

    /**
     * Initialize a new view instance for the given view id, then configures the Spring model and view
     * instance containing the form data and pointing to the generic Uif view.
     *
     * <p>This can be used by controllers to render a different view from the one initially requested (if any)</p>
     *
     * @param form form instance containing the model data
     * @param viewId id for the view to initialize
     * @param pageId page id within the view to render
     * @return ModelAndView instance for rendering the view
     */
    ModelAndView getModelAndViewWithInit(UifFormBase form, String viewId, String pageId);

    /**
     * After the controller logic is executed, the form is placed into session and the corresponding view
     * is prepared for rendering.
     *
     * @param request servlet request
     * @param modelAndView model and view
     */
    void prepareView(HttpServletRequest request, ModelAndView modelAndView);
}
