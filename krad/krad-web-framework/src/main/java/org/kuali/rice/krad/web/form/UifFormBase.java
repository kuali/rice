/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.web.form;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.util.SessionTransient;
import org.kuali.rice.krad.uif.view.DialogManager;
import org.kuali.rice.krad.uif.view.History;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.KRADUtils;
import org.springframework.web.multipart.MultipartFile;
import org.kuali.rice.krad.uif.UifConstants.ViewType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

/**
 * Base form class for views within the KRAD User Interface Framework
 *
 * <p>
 * Holds properties necessary to determine the {@code View} instance that
 * will be used to render the UI
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifFormBase implements ViewModel {
    private static final long serialVersionUID = 8432543267099454434L;

    // current view

    protected String viewId;

    protected String viewName;

    protected ViewType viewTypeName;

    protected String pageId;

    protected String methodToCall;

    protected String formKey;

    @SessionTransient
    protected String jumpToId;

    @SessionTransient
    protected String jumpToName;

    @SessionTransient
    protected String focusId;

    protected String formPostUrl;

    protected String state;

    protected boolean defaultsApplied;

    protected boolean validateDirty;

    @SessionTransient
    protected String growlScript;

    @SessionTransient
    protected String lightboxScript;

    protected View view;

    protected View postedView;

    protected Map<String, String> viewRequestParameters;

    protected List<String> readOnlyFieldsList;

    protected Map<String, Object> newCollectionLines;

    @SessionTransient
    protected Map<String, String> actionParameters;

    @SessionTransient
    protected Map<String, Object> clientStateForSyncing;

    @SessionTransient
    protected Map<String, Set<String>> selectedCollectionLines;

    private List addedCollectionItems;

    protected MultipartFile attachmentFile;

    // navigation

    protected String returnLocation;

    protected String returnFormKey;

    @SessionTransient
    protected History formHistory;

    // dialog fields
    @SessionTransient
    protected String dialogExplanation;

    @SessionTransient
    protected String dialogResponse;

    @SessionTransient
    private DialogManager dialogManager;

    @SessionTransient
    protected boolean skipViewInit;

    @SessionTransient
    protected boolean requestRedirect;

    @SessionTransient
    protected String updateComponentId;

    @SessionTransient
    protected boolean renderFullView;

    public UifFormBase() {
        formKey = generateFormKey();
        renderFullView = true;
        defaultsApplied = false;
        skipViewInit = false;
        requestRedirect = false;

        formHistory = new History();

        readOnlyFieldsList = new ArrayList<String>();
        viewRequestParameters = new HashMap<String, String>();
        newCollectionLines = new HashMap<String, Object>();
        actionParameters = new HashMap<String, String>();
        clientStateForSyncing = new HashMap<String, Object>();
        selectedCollectionLines = new HashMap<String, Set<String>>();
        addedCollectionItems = new ArrayList();
        dialogManager = new DialogManager();
    }

    /**
     * Creates the unique id used to store this "conversation" in the session.
     * The default method generates a java UUID.
     *
     * @return
     */
    protected String generateFormKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#postBind(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void postBind(HttpServletRequest request) {
        // default form post URL to request URL
        formPostUrl = request.getRequestURL().toString();

        // get any sent client view state and parse into map
        if (request.getParameterMap().containsKey(UifParameters.CLIENT_VIEW_STATE)) {
            String clientStateJSON = request.getParameter(UifParameters.CLIENT_VIEW_STATE);
            if (StringUtils.isNotBlank(clientStateJSON)) {
                // change single quotes to double quotes (necessary because the reverse was done for sending)
                clientStateJSON = StringUtils.replace(clientStateJSON, "'", "\"");

                ObjectMapper mapper = new ObjectMapper();
                try {
                    clientStateForSyncing = mapper.readValue(clientStateJSON, Map.class);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to decode client side state JSON", e);
                }
            }
        }

        // populate read only fields list
        if (request.getParameter(UifParameters.READ_ONLY_FIELDS) != null) {
            String readOnlyFields = request.getParameter(UifParameters.READ_ONLY_FIELDS);
            setReadOnlyFieldsList(KRADUtils.convertStringParameterToList(readOnlyFields));
        }

        // reset skip view init parameter if not passed
        if (!request.getParameterMap().containsKey(UifParameters.SKIP_VIEW_INIT)) {
            skipViewInit = false;
        }
    }


    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getViewId()
     */

    @Override
    public String getViewId() {
        return this.viewId;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setViewId(java.lang.String)
     */
    @Override
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getViewName()
     */
    @Override
    public String getViewName() {
        return this.viewName;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setViewName(java.lang.String)
     */
    @Override
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getViewTypeName()
     */
    @Override
    public ViewType getViewTypeName() {
        return this.viewTypeName;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setViewTypeName(org.kuali.rice.krad.uif.UifConstants.ViewType)
     */
    @Override
    public void setViewTypeName(ViewType viewTypeName) {
        this.viewTypeName = viewTypeName;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getPageId()
     */
    @Override
    public String getPageId() {
        return this.pageId;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setPageId(java.lang.String)
     */
    @Override
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getFormPostUrl()
     */
    @Override
    public String getFormPostUrl() {
        return this.formPostUrl;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setFormPostUrl(java.lang.String)
     */
    @Override
    public void setFormPostUrl(String formPostUrl) {
        this.formPostUrl = formPostUrl;
    }

    public String getReturnLocation() {
        return this.returnLocation;
    }

    public void setReturnLocation(String returnLocation) {
        this.returnLocation = returnLocation;
    }

    public String getReturnFormKey() {
        return this.returnFormKey;
    }

    public void setReturnFormKey(String returnFormKey) {
        this.returnFormKey = returnFormKey;
    }

    /**
     * Identifies the controller method that should be invoked to fulfill a
     * request. The value will be matched up against the 'params' setting on the
     * {@code RequestMapping} annotation for the controller method
     *
     * @return String method to call
     */
    public String getMethodToCall() {
        return this.methodToCall;
    }

    /**
     * Setter for the method to call
     *
     * @param methodToCall
     */
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getViewRequestParameters()
     */
    @Override
    public Map<String, String> getViewRequestParameters() {
        return this.viewRequestParameters;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setViewRequestParameters(java.util.Map<java.lang.String,java.lang.String>)
     */
    @Override
    public void setViewRequestParameters(Map<String, String> viewRequestParameters) {
        this.viewRequestParameters = viewRequestParameters;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getReadOnlyFieldsList()
     */
    @Override
    public List<String> getReadOnlyFieldsList() {
        return readOnlyFieldsList;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setReadOnlyFieldsList(java.util.List<java.lang.String>)
     */
    @Override
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList) {
        this.readOnlyFieldsList = readOnlyFieldsList;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getNewCollectionLines()
     */
    @Override
    public Map<String, Object> getNewCollectionLines() {
        return this.newCollectionLines;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setNewCollectionLines(java.util.Map<java.lang.String,java.lang.Object>)
     */
    @Override
    public void setNewCollectionLines(Map<String, Object> newCollectionLines) {
        this.newCollectionLines = newCollectionLines;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getActionParameters()
     */
    @Override
    public Map<String, String> getActionParameters() {
        return this.actionParameters;
    }

    /**
     * Returns the action parameters map as a {@code Properties} instance
     *
     * @return Properties action parameters
     */
    public Properties getActionParametersAsProperties() {
        return KRADUtils.convertMapToProperties(actionParameters);
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setActionParameters(java.util.Map<java.lang.String,java.lang.String>)
     */
    @Override
    public void setActionParameters(Map<String, String> actionParameters) {
        this.actionParameters = actionParameters;
    }

    /**
     * Retrieves the value for the given action parameter, or empty string if
     * not found
     *
     * @param actionParameterName - name of the action parameter to retrieve value for
     * @return String parameter value or empty string
     */
    public String getActionParamaterValue(String actionParameterName) {
        if ((actionParameters != null) && actionParameters.containsKey(actionParameterName)) {
            return actionParameters.get(actionParameterName);
        }

        return "";
    }

    /**
     * Returns the action event that was sent in the action parameters (if any)
     *
     * <p>
     * The action event is a special action parameter that can be sent to indicate a type of action being taken. This
     * can be looked at by the view or components to render differently
     * </p>
     *
     * TODO: make sure action parameters are getting reinitialized on each request
     *
     * @return String action event name or blank if action event was not sent
     */
    public String getActionEvent() {
        if ((actionParameters != null) && actionParameters.containsKey(UifConstants.UrlParams.ACTION_EVENT)) {
            return actionParameters.get(UifConstants.UrlParams.ACTION_EVENT);
        }

        return "";
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getClientStateForSyncing()
     */
    @Override
    public Map<String, Object> getClientStateForSyncing() {
        return clientStateForSyncing;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getSelectedCollectionLines()
     */
    @Override
    public Map<String, Set<String>> getSelectedCollectionLines() {
        return selectedCollectionLines;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setSelectedCollectionLines(java.util.Map<java.lang.String,java.util.Set<java.lang.String>>)
     */
    @Override
    public void setSelectedCollectionLines(Map<String, Set<String>> selectedCollectionLines) {
        this.selectedCollectionLines = selectedCollectionLines;
    }

    /**
     * Key string that identifies the form instance in session storage
     *
     * <p>
     * When the view is posted, the previous form instance is retrieved and then
     * populated from the request parameters. This key string is retrieve the
     * session form from the session service
     * </p>
     *
     * @return String form session key
     */
    public String getFormKey() {
        return this.formKey;
    }

    /**
     * Setter for the form's session key
     *
     * @param formKey
     */
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#isDefaultsApplied()
     */
    @Override
    public boolean isDefaultsApplied() {
        return this.defaultsApplied;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setDefaultsApplied(boolean)
     */
    @Override
    public void setDefaultsApplied(boolean defaultsApplied) {
        this.defaultsApplied = defaultsApplied;
    }

    /**
     * Indicates whether a new view is being initialized or the call is refresh (or query) call
     *
     * @return boolean true if view initialization was skipped, false if new view is being created
     */
    public boolean isSkipViewInit() {
        return skipViewInit;
    }

    /**
     * Setter for the skip view initialization flag
     *
     * @param skipViewInit
     */
    public void setSkipViewInit(boolean skipViewInit) {
        this.skipViewInit = skipViewInit;
    }

    /**
     * Indicates whether a redirect has been requested for the view
     *
     * @return boolean true if redirect was requested, false if not
     */
    public boolean isRequestRedirect() {
        return requestRedirect;
    }

    /**
     * Setter for the request redirect indicator
     *
     * @param requestRedirect
     */
    public void setRequestRedirect(boolean requestRedirect) {
        this.requestRedirect = requestRedirect;
    }

    /**
     * Holder for files that are attached through the view
     *
     * @return MultipartFile representing the attachment
     */
    public MultipartFile getAttachmentFile() {
        return this.attachmentFile;
    }

    /**
     * Setter for the form's attachment file
     *
     * @param attachmentFile
     */
    public void setAttachmentFile(MultipartFile attachmentFile) {
        this.attachmentFile = attachmentFile;
    }

    /**
     * Id for the component that should be updated for a component refresh process
     *
     * @return String component id
     */
    public String getUpdateComponentId() {
        return updateComponentId;
    }

    /**
     * Setter for the component id that should be refreshed
     *
     * @param updateComponentId
     */
    public void setUpdateComponentId(String updateComponentId) {
        this.updateComponentId = updateComponentId;
    }

    /**
     * Indicates if the full view is to be rendered or if its just a component that
     * needs to be refreshed
     *
     * @return the renderFullView
     */
    public boolean isRenderFullView() {
        return this.renderFullView;
    }

    /**
     * Setter for renderFullView
     *
     * @param renderFullView
     */
    public void setRenderFullView(boolean renderFullView) {
        this.renderFullView = renderFullView;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getView()
     */
    @Override
    public View getView() {
        return this.view;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setView(org.kuali.rice.krad.uif.view.View)
     */
    @Override
    public void setView(View view) {
        this.view = view;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getPostedView()
     */
    @Override
    public View getPostedView() {
        return this.postedView;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setPostedView(org.kuali.rice.krad.uif.view.View)
     */
    @Override
    public void setPostedView(View postedView) {
        this.postedView = postedView;
    }

    /**
     * Instance of the {@code ViewService} that can be used to retrieve
     * {@code View} instances
     *
     * @return ViewService implementation
     */
    protected ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }

    /**
     * The jumpToId for this form, the element with this id will be jumped to automatically
     * when the form is loaded in the view.
     * Using "TOP" or "BOTTOM" will jump to the top or the bottom of the resulting page.
     * jumpToId always takes precedence over jumpToName, if set.
     *
     * @return the jumpToId
     */
    public String getJumpToId() {
        return this.jumpToId;
    }

    /**
     * @param jumpToId the jumpToId to set
     */
    public void setJumpToId(String jumpToId) {
        this.jumpToId = jumpToId;
    }

    /**
     * The jumpToName for this form, the element with this name will be jumped to automatically
     * when the form is loaded in the view.
     * WARNING: jumpToId always takes precedence over jumpToName, if set.
     *
     * @return the jumpToName
     */
    public String getJumpToName() {
        return this.jumpToName;
    }

    /**
     * @param jumpToName the jumpToName to set
     */
    public void setJumpToName(String jumpToName) {
        this.jumpToName = jumpToName;
    }

    /**
     * Field to place focus on when the page loads
     * An empty focusId will result in focusing on the first visible input element by default.
     *
     * @return the focusId
     */
    public String getFocusId() {
        return this.focusId;
    }

    /**
     * @param focusId the focusId to set
     */
    public void setFocusId(String focusId) {
        this.focusId = focusId;
    }

    /**
     * History parameter representing the History of views that have come before the
     * viewing of the current view
     *
     * <p>
     * Used for breadcrumb widget generation on the view and also for navigating back
     * to previous or hub locations
     * </p>
     *
     * @return History instance giving current history
     */
    public History getFormHistory() {
        return formHistory;
    }

    /**
     * Setter for the current History object
     *
     * @param history the history to set
     */
    public void setFormHistory(History history) {
        this.formHistory = history;
    }

    /**
     * Indicates whether the form should be validated for dirtyness
     *
     * <p>
     * For FormView, it's necessary to validate when the user tries to navigate out of the form. If set, all the
     * InputFields will be validated on refresh, navigate, cancel or close Action or on form
     * unload and if dirty, displays a message and user can decide whether to continue with
     * the action or stay on the form
     * </p>
     *
     * @return boolean true if dirty validation should be enabled
     */
    public boolean isValidateDirty() {
        return this.validateDirty;
    }

    /**
     * Setter for dirty validation indicator
     *
     * @param validateDirty
     */
    public void setValidateDirty(boolean validateDirty) {
        this.validateDirty = validateDirty;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getGrowlScript()
     */
    @Override
    public String getGrowlScript() {
        return growlScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setGrowlScript(java.lang.String)
     */
    @Override
    public void setGrowlScript(String growlScript) {
        this.growlScript = growlScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getState() 
     */
    public String getState() {
        return state;
    }

    /**
     * @see ViewModel#setState(String)
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getLightboxScript()
     */
    @Override
    public String getLightboxScript() {
        return lightboxScript;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setLightboxScript(java.lang.String)
     */
    @Override
    public void setLightboxScript(String lightboxScript) {
        this.lightboxScript = lightboxScript;
    }

    /**
     * Returns the String entered by the user when presented a dialog
     *
     * <p>
     * Field defined here so all forms will be able to bind to a dialog using the same property
     * </p>
     *
     * @return String - the text entered by a user as a reply in a modal dialog.
     */
    public String getDialogExplanation() {
        return dialogExplanation;
    }

    /**
     * Sets the dialogExplanation text value.
     *
     * @param dialogExplanation - text entered by user when replying to a modal dialog
     */
    public void setDialogExplanation(String dialogExplanation) {
        this.dialogExplanation = dialogExplanation;
    }

    /**
     * Represents the option chosen by the user when interacting with a modal dialog
     *
     * <p>
     * This is used to determine which option was chosen by the user. The value is the key in the key/value pair
     * selected in the control.
     * </p>
     *
     * @return - String key selected by the user
     */
    public String getDialogResponse() {
        return dialogResponse;
    }

    /**
     * Sets the response key text selected by the user as a response to a modal dialog
     *
     * @param dialogResponse - the key of the option chosen by the user
     */
    public void setDialogResponse(String dialogResponse) {
        this.dialogResponse = dialogResponse;
    }

    /**
     * Gets the DialogManager for this view/form
     *
     * <p>
     * The DialogManager tracks modal dialog interactions with the user
     * </p>
     * @return
     */
    public DialogManager getDialogManager() {
        return dialogManager;
    }

    /**
     * Sets the DialogManager for this view
     *
     * @param dialogManager - DialogManager instance for this view
     */
    public void setDialogManager(DialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }


    /**
     * The {@code List} that contains all newly added items for the collections on the model
     * 
     * <p>
     * This list contains the new items for all the collections on the model.    
     * </p>
     *
     * @return List of the newly added item lists
     */
    public List getAddedCollectionItems() {
        return addedCollectionItems;
    }

    /**
     * Setter for the newly added item list
     *
     * @param addedCollectionItems
     */
    public void setAddedCollectionItems(List addedCollectionItems) {
        this.addedCollectionItems = addedCollectionItems;
    }

    /**
     * Indicates whether an collection item has been newly added
     *
     * <p>
     * Tests collection items against the list of newly added items on the model. This list gets cleared when the view 
     * is submitted and the items are persisted.
     * </p>
     *
     * @param item - the item to test against list of newly added items
     * @return boolean true if the item has been newly added
     */
    public boolean isAddedCollectionItem(Object item) {
        return addedCollectionItems.contains(item);
    }

}