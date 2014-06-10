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
package org.kuali.rice.krad.uif.view;

import org.kuali.rice.krad.file.FileMetaBlob;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.web.form.DialogResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface that must be implemented for classes the provide the backing data (model) for a
 * {@link org.kuali.rice.krad.uif.view.View}.
 *
 * <p>Since the View relies on helper properties from the model it is necessary the backing object implement the
 * ViewModel interface. Note model objects can extend {@link org.kuali.rice.krad.web.form.UifFormBase} which implements
 * the ViewModel interface.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewModel extends Serializable {

    /**
     * Called before Spring binds the request to the form to allow for pre-processing before setting values.
     *
     * @param request request object containing the query parameters
     */
    void preBind(HttpServletRequest request);

    /**
     * Called after Spring binds the request to the form and before the controller method is invoked
     *
     * @param request request object containing the query parameters
     */
    void postBind(HttpServletRequest request);

    /**
     * Called after the controller has finished executing, but before rendering occurs.
     *
     * @param request request object containing the query parameters
     */
    void preRender(HttpServletRequest request);

    /**
     * Unique Id for the <code>View</code> instance. This is specified for a
     * view in its definition by setting the 'id' property.
     *
     * @return String view id
     */
    public String getViewId();

    /**
     * Setter for the unique view id
     *
     * @param viewId
     */
    public void setViewId(String viewId);

    /**
     * Name for the <code>View</code> instance. This is specified for a view in
     * its definition by setting the 'id' property. The name is not necessary
     * unique and cannot be used by itself to retrieve a view. Typically it is
     * used with other parameters to identify a view with a certain type (view
     * type)
     *
     * @return String view name
     */
    public String getViewName();

    /**
     * Setter for the view name
     *
     * @param viewName
     */
    public void setViewName(String viewName);

    /**
     * Name for the type of view being requested. This can be used to find
     * <code>View</code> instances by request parameters (not necessary the
     * unique id)
     *
     * @return String view type name
     */
    public ViewType getViewTypeName();

    /**
     * Setter for the view type name
     *
     * @param viewTypeName
     */
    public void setViewTypeName(ViewType viewTypeName);

    /**
     * View instance associated with the model. Used to render the user interface
     *
     * @return View
     */
    public View getView();

    /**
     * Setter for the view instance
     *
     * @param view
     */
    public void setView(View view);

    /**
     * Returns the view helper service instance that was configured for the current view.
     *
     * @return instance of view helper service, null if view is null
     */
    public ViewHelperService getViewHelperService() throws IllegalAccessException, InstantiationException;

    /**
     * Gets the {@link org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata} that has been built up from processing
     * of a view.
     *
     * <p>The view post metadata is used to read information about the view that was rendered when a post occurs. For
     * example, you might need to check whether a particular flag was enabled for the rendered view when processing
     * the post logic</p>
     *
     * @return ViewPostMetadata instance for the previously processed view
     */
    public ViewPostMetadata getViewPostMetadata();

    /**
     * @see ViewModel#getViewPostMetadata()
     */
    public void setViewPostMetadata(ViewPostMetadata viewPostMetadata);

    /**
     * Id for the current page being displayed within the view
     *
     * @return String page id
     */
    public String getPageId();

    /**
     * Setter for the current page id
     *
     * @param pageId
     */
    public void setPageId(String pageId);

    /**
     * URL the form generated for the view should post to
     *
     * @return String form post URL
     */
    public String getFormPostUrl();

    /**
     * Setter for the form post URL
     *
     * @param formPostUrl
     */
    public void setFormPostUrl(String formPostUrl);

    /**
     * Map of parameters that was used to configured the <code>View</code>.
     * Maintained on the form to rebuild the view on posts and session timeout
     *
     * @return Map<String, String> view parameters
     * @see org.kuali.rice.krad.uif.view.View.getViewRequestParameters()
     */
    public Map<String, String> getViewRequestParameters();

    /**
     * Setter for the view's request parameter map
     *
     * @param viewRequestParameters map of request parameters
     */
    public void setViewRequestParameters(Map<String, String> viewRequestParameters);

    /**
     * List of fields that should be read only on the view
     *
     * <p>
     * If the view being rendered supports request setting of read-only fields, the readOnlyFields request parameter
     * can be sent to mark fields as read only that might not have been otherwise
     * </p>
     *
     * <p>
     * Note the paths specified should be the simple property names (not the full binding path). Therefore if the
     * property name appears multiple times in the view, all instances will be set as read only
     * </p>
     *
     * @return List<String> read only property names
     * @see View#isSupportsRequestOverrideOfReadOnlyFields()
     */
    public List<String> getReadOnlyFieldsList();

    /**
     * Setter for the list of read only fields
     *
     * @param readOnlyFieldsList
     */
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList);

    /**
     * Holds instances for collection add lines. The key of the Map gives the
     * collection name the line instance applies to, the Map value is an
     * instance of the collection object class that holds the new line data
     *
     * @return Map<String, Object> new collection lines
     */
    public Map<String, Object> getNewCollectionLines();

    /**
     * Setter for the new collection lines Map
     *
     * @param newCollectionLines
     */
    public void setNewCollectionLines(Map<String, Object> newCollectionLines);

    /**
     * When the request has been triggered by an action component, gives the id for the action.
     *
     * @return String action id, or null if request was not triggered by an action component
     */
    String getTriggerActionId();

    /**
     * @see ViewModel#getTriggerActionId()
     */
    void setTriggerActionId(String triggerActionId);

    /**
     * Map of parameters sent for the invoked action
     *
     * <p>
     * Many times besides just setting the method to call actions need to send
     * additional parameters. For instance the method being called might do a
     * redirect, in which case the action needs to send parameters for the
     * redirect URL. An example of this is redirecting to a <code>Lookup</code>
     * view. In some cases the parameters that need to be sent conflict with
     * properties already on the form, and putting all the action parameters as
     * form properties would grow massive (in addition to adds an additional
     * step from the XML config). So this general map solves those issues.
     * </p>
     *
     * @return Map<String, String> action parameters
     */
    public Map<String, String> getActionParameters();

    /**
     * Setter for the action parameters map
     *
     * @param actionParameters
     */
    public void setActionParameters(Map<String, String> actionParameters);

    /**
     * Map that is populated from the component state maintained on the client
     *
     * <p>
     * Used when a request is made that refreshes part of the view. The current state for components (which
     * have state that can be changed on the client), is populated into this map which is then used by the
     * <code>ViewHelperService</code> to update the components so that the state is maintained when they render.
     * </p>
     *
     * @return Map<String, Object> map where key is name of property or component id, and value is the property
     *         value or another map of component key/value pairs
     */
    public Map<String, Object> getClientStateForSyncing();

    /**
     * Holds Set of String identifiers for lines that were selected in a collection from a single page.
     * selectedCollectionLines are request level values and get reset with every page request
     *
     * <p>
     * When the select field is enabled for a <code>CollectionGroup</code>, the framework will be
     * default bind the selected identifier strings to this property. The key of the map uniquely identifies the
     * collection by the full binding path to the collection, and the value is a set of Strings for the checked
     * lines.
     * </p>
     *
     * @return Map<String, Set<String>> map of collections and their selected lines
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getDataObjectIdentifierString(java.lang.Object)
     */
    public Map<String, Set<String>> getSelectedCollectionLines();

    /**
     * Setter for the map that holds selected collection lines
     *
     * @param selectedCollectionLines
     */
    public void setSelectedCollectionLines(Map<String, Set<String>> selectedCollectionLines);

    /**
     * Indicates whether default values should be applied.
     *
     * <p>
     * Default field values of a view need to be applied after the view life cycle completes.  Otherwise,
     * they risk getting over written.
     * </p>
     *
     * @return boolean true if the request was an ajax call, false if not
     */
    boolean isApplyDefaultValues();

    /**
     * Set whether default values should be applied to the view
     *
     * @param applyDefaultValues
     */
    void setApplyDefaultValues(boolean applyDefaultValues);

    /**
     * Script that will run on render (view or component) for generating growl messages
     *
     * @return String JS growl script
     */
    public String getGrowlScript();

    /**
     * Setter for the script that generates growls on render
     *
     * @param growlScript
     */
    public void setGrowlScript(String growlScript);

    /**
     * Gets the state.  This is the default location for state on KRAD forms.
     *
     * @return the state
     */
    public String getState();

    /**
     * Set the state
     *
     * @param state
     */
    public void setState(String state);

    /**
     * Id for the component that should be updated for a component refresh process
     *
     * @return String component id
     */
    public String getUpdateComponentId();

    /**
     * Setter for the component id that should be refreshed
     *
     * @param updateComponentId
     */
    public void setUpdateComponentId(String updateComponentId);

    /**
     * Component instance that been built for a refresh/disclosure request.
     *
     * <p>This is generally set by org.kuali.rice.krad.uif.lifecycle.ViewLifecycle#performComponentLifecycle(org.kuali.rice.krad.uif.view.View,
     * java.lang.Object, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata, java.lang.String) after processing the lifecycle. The form
     * property provides access to the rendering layer.</p>
     *
     * @return component instance for updating
     */
    public Component getUpdateComponent();

    /**
     * @see ViewModel#getUpdateComponent()
     */
    public void setUpdateComponent(Component updateComponent);

    /**
     * Indicates whether the request was made by an ajax call
     *
     * <p>
     * Depending on whether the request was made via ajax (versus standard browser submit) the response
     * will be handled different. For example with an ajax request we can send back partial page updates, which
     * cannot be done with standard submits
     * </p>
     *
     * <p>
     * If this indicator is true, {@link #getAjaxReturnType()} will be used to determine how to handling
     * the ajax return
     * </p>
     *
     * @return boolean true if the request was an ajax call, false if not
     */
    boolean isAjaxRequest();

    /**
     * Set the ajaxRequest
     *
     * @param ajaxRequest
     */
    void setAjaxRequest(boolean ajaxRequest);

    /**
     * Gets the return type for the ajax call
     *
     * <p>
     * The ajax return type indicates how the response content will be handled in the client. Typical
     * examples include updating a component, the page, or doing a redirect.
     * </p>
     *
     * @return String return type
     * @see org.kuali.rice.krad.uif.UifConstants.AjaxReturnTypes
     */
    String getAjaxReturnType();

    /**
     * Setter for the type of ajax return
     *
     * @param ajaxReturnType
     */
    void setAjaxReturnType(String ajaxReturnType);

    /**
     * Indicates whether the request is to update a component (only applicable for ajax requests)
     *
     * @return boolean true if the request is for update component, false if not
     */
    boolean isUpdateComponentRequest();

    /**
     * Indicates whether the request is to update a page (only applicable for ajax requests)
     *
     * @return boolean true if the request is for update page, false if not
     */
    boolean isUpdatePageRequest();

    /**
     * Indicates whether the request is to update a dialog (only applicable for ajax requests)
     *
     * @return boolean true if the request is for update dialog, false if not
     */
    boolean isUpdateDialogRequest();

    /**
     * Indicates whether the request is for a non-update of the view (only applicable for ajax requests)
     *
     * <p>
     * Examples of requests that do not update the view are ajax queries or requests that download a file
     * </p>
     *
     * @return boolean true if the request is for non-update, false if not
     */
    boolean isUpdateNoneRequest();

    /**
     * Indicates whether the request should return a JSON string
     *
     * <p>
     * When this indicator is true, the rendering process will invoke the template
     * given by {@link #getRequestJsonTemplate()} which should return a JSON string
     * </p>
     *
     * <p>
     * For JSON requests the view is not built, however a component can be retrieved and
     * exported in the request by setting {@link #getUpdateComponentId()}
     * </p>
     *
     * @return boolean true if request is for JSON, false if not
     */
    boolean isJsonRequest();

    /**
     * Template the will be invoked to return a JSON string
     *
     * <p>
     * Certain templates can be rendered to build JSON for a JSON request. The template
     * set here (by a controller) will be rendered
     * </p>
     *
     * @return path to template
     */
    String getRequestJsonTemplate();

    /**
     * Setter for the template to render for the request
     *
     * @param requestJsonTemplate
     */
    void setRequestJsonTemplate(String requestJsonTemplate);

    /**
     * Indicates whether the request is for paging a collection (or sorting).
     *
     * @return boolean true if a paging request is present, false if not
     */
    boolean isCollectionPagingRequest();

    /**
     * @see ViewModel#isCollectionPagingRequest()
     */
    void setCollectionPagingRequest(boolean collectionPagingRequest);

    /**
     * Contains values for dialog explanation fields present on the page.
     *
     * <p>Since multiple dialogs can be present on the same page using the generic explanation field, the values
     * are maintained in this map using the dialog id as the key. Values are cleared on each request.</p>
     *
     * @return map of dialog explanations, where key is the dialog id and map value is the explanation
     */
    Map<String, String> getDialogExplanations();

    /**
     * @see ViewModel#getDialogExplanations()
     */
    void setDialogExplanations(Map<String, String> dialogExplanations);

    /**
     * Map containing dialog responses for a request 'conversation'.
     *
     * <p>When a controller methods requests a dialog, the response is collected on the return call and placed
     * into this map. The key to the map is the id for the dialog. Since a single controller method can spawn multiple
     * dialogs in a single conversation (these are actually multiple requests/responses, but part of the same action
     * request), the responses are collected in this map. Whenever a request is encountered that is not a return, the
     * map is cleared. This means the responses will be cleared in case the action is triggered action.</p>
     *
     * @return map of dialog responses, where map key is the dialog id and the map value is the dialog response
     * object
     */
    Map<String, DialogResponse> getDialogResponses();

    /**
     * Helper method to get a dialog response for the given dialog id.
     *
     * @param dialogId id of the dialog to get response for
     * @return dialog response object, or null if response does not exist
     * @see ViewModel#getDialogResponses()
     */
    DialogResponse getDialogResponse(String dialogId);

    /**
     * @see ViewModel#getDialogResponses()
     */
    void setDialogResponses(Map<String, DialogResponse> dialogResponses);

    /**
     * A generic map for framework pieces (such as component modifiers) that need to dynamically store
     * data to the form
     *
     * @return Map<String, Object>
     */
    public Map<String, Object> getExtensionData();

    /**
     * Setter for the generic extension data map
     *
     * @param extensionData
     */
    public void setExtensionData(Map<String, Object> extensionData);

}
