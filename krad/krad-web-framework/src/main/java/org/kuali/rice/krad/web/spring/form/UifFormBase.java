/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kuali.rice.krad.web.spring.form;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.history.History;
import org.kuali.rice.krad.uif.service.ViewService;
import org.springframework.web.multipart.MultipartFile;

/**
 * Base form class for views within the KRAD User Interface Framework
 * 
 * <p>
 * Holds properties necessary to determine the <code>View</code> instance that
 * will be used to render the UI
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifFormBase implements Serializable {
    private static final long serialVersionUID = 8432543267099454434L;
    
    private History formHistory;

    // current view
    protected String viewId;
    protected String viewName;
    protected String viewTypeName;
    protected String pageId;
    protected String methodToCall;
    protected String formKey;
    protected String jumpToId;
    protected String jumpToName;
    protected String focusId;
    protected String formPostUrl;
    
    protected boolean defaultsApplied;

    protected View view;
    protected View previousView;
    protected Map<String, String> viewRequestParameters;

    protected Map<String, Object> newCollectionLines;
    protected Map<String, String> actionParameters;

    protected MultipartFile attachmentFile;

    // navigation
    protected String returnLocation;
    protected String returnFormKey;
    protected String hubLocation;
    protected String hubFormKey;
    protected String homeLocation;

    protected boolean renderFullView;
    protected boolean validateDirty;

    public UifFormBase() {
        formKey = generateFormKey();
        renderFullView = true;
        defaultsApplied = false;

        viewRequestParameters = new HashMap<String, String>();
        newCollectionLines = new HashMap<String, Object>();
        actionParameters = new HashMap<String, String>();
        //formHistory = new History();
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
     * Called after Spring binds the request to the form and before the
     * controller method is invoked.
     * 
     * @param request
     *            - request object containing the query parameters
     */
    public void postBind(HttpServletRequest request) {
        // default form post URL to request URL
        formPostUrl = request.getRequestURL().toString();

        //history.pushToHistory(viewId, pageId, view.getTitle(), formPostUrl, formKey);
    }

    /**
     * Unique Id for the <code>View</code> instance. This is specified for a
     * view in its definition by setting the 'id' property.
     * 
     * @return String view id
     */
    public String getViewId() {
        return this.viewId;
    }

    /**
     * Setter for the unique view id
     * 
     * @param viewId
     */
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    /**
     * Name for the <code>View</code> instance. This is specified for a view in
     * its definition by setting the 'id' property. The name is not necessary
     * unique and cannot be used by itself to retrieve a view. Typically it is
     * used with other parameters to identify a view with a certain type (view
     * type)
     * 
     * @return String view name
     */
    public String getViewName() {
        return this.viewName;
    }

    /**
     * Setter for the view name
     * 
     * @param viewName
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    /**
     * Name for the type of view being requested. This can be used to find
     * <code>View</code> instances by request parameters (not necessary the
     * unique id)
     * 
     * @return String view type name
     */
    public String getViewTypeName() {
        return this.viewTypeName;
    }

    /**
     * Setter for the view type name
     * 
     * @param viewTypeName
     */
    public void setViewTypeName(String viewTypeName) {
        this.viewTypeName = viewTypeName;
    }

    /**
     * Id for the current page being displayed within the view
     * 
     * @return String page id
     */
    public String getPageId() {
        return this.pageId;
    }

    /**
     * Setter for the current page id
     * 
     * @param pageId
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getFormPostUrl() {
        return this.formPostUrl;
    }

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

    public String getHubLocation() {
        return this.hubLocation;
    }

    public void setHubLocation(String hubLocation) {
        this.hubLocation = hubLocation;
    }

    public String getHubFormKey() {
        return this.hubFormKey;
    }

    public void setHubFormKey(String hubFormKey) {
        this.hubFormKey = hubFormKey;
    }

    public String getHomeLocation() {
        return this.homeLocation;
    }

    public void setHomeLocation(String homeLocation) {
        this.homeLocation = homeLocation;
    }

    /**
     * Identifies the controller method that should be invoked to fulfill a
     * request. The value will be matched up against the 'params' setting on the
     * <code>RequestMapping</code> annotation for the controller method
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
     * Map of parameters that was used to configured the <code>View</code>.
     * Maintained on the form to rebuild the view on posts and session timeout
     * 
     * @return Map<String, String> view parameters
     * @see org.kuali.rice.krad.uif.container.View.getViewRequestParameters()
     */
    public Map<String, String> getViewRequestParameters() {
        return this.viewRequestParameters;
    }

    /**
     * Setter for the view's request parameter map
     * 
     * @param viewRequestParameters
     */
    public void setViewRequestParameters(Map<String, String> viewRequestParameters) {
        this.viewRequestParameters = viewRequestParameters;
    }

    /**
     * Holds instances for collection add lines. The key of the Map gives the
     * collection name the line instance applies to, the Map value is an
     * instance of the collection object class that holds the new line data
     * 
     * @return Map<String, Object> new collection lines
     */
    public Map<String, Object> getNewCollectionLines() {
        return this.newCollectionLines;
    }

    /**
     * Setter for the new collection lines Map
     * 
     * @param newCollectionLines
     */
    public void setNewCollectionLines(Map<String, Object> newCollectionLines) {
        this.newCollectionLines = newCollectionLines;
    }

    /**
     * Map of parameters sent for the invoked action
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
    public Map<String, String> getActionParameters() {
        return this.actionParameters;
    }

    /**
     * Returns the action parameters map as a <code>Properties</code> instance
     * 
     * @return Properties action parameters
     */
    public Properties getActionParametersAsProperties() {
        Properties actionProperties = new Properties();

        if (actionParameters != null) {
            for (Map.Entry<String, String> actionParameter : actionParameters.entrySet()) {
                actionProperties.put(actionParameter.getKey(), actionParameter.getValue());
            }
        }

        return actionProperties;
    }

    /**
     * Setter for the action parameters map
     * 
     * @param actionParameters
     */
    public void setActionParameters(Map<String, String> actionParameters) {
        this.actionParameters = actionParameters;
    }

    /**
     * Retrieves the value for the given action parameter, or empty string if
     * not found
     * 
     * @param actionParameterName
     *            - name of the action parameter to retrieve value for
     * @return String parameter value or empty string
     */
    public String getActionParamaterValue(String actionParameterName) {
        if ((actionParameters != null) && actionParameters.containsKey(actionParameterName)) {
            return actionParameters.get(actionParameterName);
        }

        return "";
    }

    /**
     * Key string that identifies the form instance in session storage
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
     * Indicates whether the form has had default values from the configured
     * <code>View</code> applied. This happens only once for each form instance
     * 
     * @return boolean true if default values have been applied, false if not
     */
    public boolean isDefaultsApplied() {
        return this.defaultsApplied;
    }

    /**
     * Setter for the defaults applied indicator
     * 
     * @param defaultsApplied
     */
    public void setDefaultsApplied(boolean defaultsApplied) {
        this.defaultsApplied = defaultsApplied;
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
     * @return the renderFullView
     */
    public boolean isRenderFullView() {
        return this.renderFullView;
    }

    /**
     * @param renderFullView
     */
    public void setRenderFullView(boolean renderFullView) {
        this.renderFullView = renderFullView;
    }

    /**
     * View instance associated with the form. Used to render the user interface
     * 
     * @return View
     */
    public View getView() {
        return this.view;
    }

    /**
     * Setter for the view instance
     * 
     * @param view
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * View instance for the page that made a request. Since a new view instance
     * gets initialized for each request before the controller logic is invoked,
     * any state about the previous view is lost. This could be needed to read
     * metadata from the view for such things as collection processing. When
     * this is necessary the previous view instance can be retrieved
     * 
     * @return View instance
     */
    public View getPreviousView() {
        return this.previousView;
    }

    /**
     * Setter for the previous view instance
     * 
     * @param previousView
     */
    public void setPreviousView(View previousView) {
        this.previousView = previousView;
    }

    /**
     * Instance of the <code>ViewService</code> that can be used to retrieve
     * <code>View</code> instances
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
     * viewing of the current view.  Used for breadcrumb widget generation on the view.
     *
     * @param history the history to set
     */
    public void setFormHistory(History history) {
        this.formHistory = history;
    }

    /**
     * @return the history
     */
    public History getFormHistory() {
        return formHistory;
    }
    
    public boolean isValidateDirty() {
		return this.validateDirty;
	}

	/**
	 * Setter for dirty validation. 
	 */
	public void setValidateDirty(boolean validateDirty) {
		this.validateDirty = validateDirty;
	}    

}
