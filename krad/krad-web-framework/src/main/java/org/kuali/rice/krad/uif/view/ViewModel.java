/**
 * Copyright 2005-2011 The Kuali Foundation
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

import org.kuali.rice.krad.uif.UifConstants.ViewType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface that must be implemented for clases the provide the backing data (model) for a {@link View}
 *
 * <p>
 * Since the View relies on helper properties from the model it is necessary the backing object implement this
 * interface. Note model objects can extend {@link org.kuali.rice.krad.web.form.UifFormBase} which implements
 * this interface
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewModel extends Serializable {

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
     * @param viewRequestParameters
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
     * @see View#isSupportsReadOnlyFieldsOverride()
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
     * Holds Set of String identifiers for lines that were selected in a collection
     *
     * <p>
     * When the select field is enabled for a <code>CollectionGroup</code>, the framework will be
     * default bind the selected identifier strings to this property. The key of the map uniquely identifies the
     * collection by the full binding path to the collection, and the value is a set of Strings for the checked
     * lines.
     * </p>
     *
     * @return Map<String, Set<String>> map of collections and their selected lines
     * @see org.kuali.rice.krad.service.DataObjectMetaDataService#getDataObjectIdentifierString(java.lang.Object)
     */
    public Map<String, Set<String>> getSelectedCollectionLines();

    /**
     * Setter for the map that holds selected collection lines
     *
     * @param selectedCollectionLines
     */
    public void setSelectedCollectionLines(Map<String, Set<String>> selectedCollectionLines);

    /**
     * Indicates whether the form has had default values from the configured
     * <code>View</code> applied. This happens only once for each form instance
     *
     * @return boolean true if default values have been applied, false if not
     */
    public boolean isDefaultsApplied();

    /**
     * Setter for the defaults applied indicator
     *
     * @param defaultsApplied
     */
    public void setDefaultsApplied(boolean defaultsApplied);

}
