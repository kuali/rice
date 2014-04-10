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
package org.kuali.rice.krad.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.bind.RequestAccessible;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Form class containing backing data for {@link LookupView}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupForm extends UifFormBase {
    private static final long serialVersionUID = -7323484966538685327L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupForm.class);

    @RequestAccessible
    private String dataObjectClassName;

    @RequestAccessible
    private boolean multipleValuesSelect;

    @RequestAccessible
    private boolean redirectedLookup;

    @RequestAccessible
    private boolean returnByScript;

    @RequestAccessible
    private String returnTarget;

    @RequestAccessible
    private String lookupCollectionName;

    @RequestAccessible
    private String lookupCollectionId;

    @RequestAccessible
    private String referencesToRefresh;

    @RequestAccessible
    private String quickfinderId;

    @RequestAccessible
    private Map<String, String> fieldConversions;
    private List<String> multiValueReturnFields;

    @RequestAccessible
    private Map<String, String> lookupCriteria;

    private Collection<?> lookupResults;

    @RequestAccessible
    private boolean displayResults;

    public LookupForm() {
        super();

        setViewTypeName(ViewType.LOOKUP);

        lookupCriteria = new HashMap<String, String>();
        fieldConversions = new HashMap<String, String>();
        multiValueReturnFields = new ArrayList<String>();
    }

    /**
     * Picks out data object name from the request to retrieve a lookupable and for the initial get request
     * populates the {@link #getFieldConversions()} property.
     *
     * {@inheritDoc}
     */
    @Override
    public void postBind(HttpServletRequest request) {
        super.postBind(request);

        if (StringUtils.isBlank(getDataObjectClassName())) {
            setDataObjectClassName(((LookupView) getView()).getDataObjectClass().getName());
        }

        Lookupable lookupable = getLookupable();
        if ((lookupable != null) && (lookupable.getDataObjectClass() == null)) {
            Class<?> dataObjectClass;
            try {
                dataObjectClass = Class.forName(getDataObjectClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Object class " + getDataObjectClassName() + " not found", e);
            }

            lookupable.setDataObjectClass(dataObjectClass);
        }

        // populate field conversions map on initial GET request
        if (request.getMethod().equals(HttpMethod.GET.name()) && (request.getParameter(
                KRADConstants.CONVERSION_FIELDS_PARAMETER) != null)) {
            String conversionFields = request.getParameter(KRADConstants.CONVERSION_FIELDS_PARAMETER);
            setFieldConversions(KRADUtils.convertStringParameterToMap(conversionFields));
        }
    }

    /**
     * Returns an {@link Lookupable} instance associated with the lookup view.
     *
     * @return Lookupable instance or null if one does not exist
     */
    public Lookupable getLookupable() {
        if (getViewHelperService() != null) {
            return (Lookupable) getViewHelperService();
        }

        return null;
    }

    /**
     * Class name for the data object the lookup should be performed against.
     *
     * <p>The object class name is used to pick up a dictionary entry which will feed the attribute field
     * definitions and other configuration. In addition it is to configure the
     * {@link org.kuali.rice.krad.lookup.Lookupable} which will carry out the search action</p>
     *
     * @return lookup data object class
     */
    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    /**
     * Setter for {@link LookupForm#getDataObjectClassName()}
     * 
     * @param dataObjectClassName property value
     */
    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    /**
     * Indicates whether multiple values select should be enabled for the lookup.
     *
     * <p>When set to true, the select field is enabled for the lookup results group that allows the user
     * to select one or more rows for returning</p>
     *
     * @return boolean true if multiple values should be enabled, false otherwise
     */
    public boolean isMultipleValuesSelect() {
        return multipleValuesSelect;
    }

    /**
     * @see LookupForm#isMultipleValuesSelect()
     */
    public void setMultipleValuesSelect(boolean multipleValuesSelect) {
        this.multipleValuesSelect = multipleValuesSelect;
    }

    /**
     * Indicates whether the requested was redirected from the lookup framework due to an external object
     * request.
     *
     * <p>This prevents the framework from performing another redirect check</p>
     *
     * @return boolean true if request was a redirect, false if not
     */
    public boolean isRedirectedLookup() {
        return redirectedLookup;
    }

    /**
     * @see LookupForm#isRedirectedLookup()
     */
    public void setRedirectedLookup(boolean redirectedLookup) {
        this.redirectedLookup = redirectedLookup;
    }

    /**
      * Indicates whether the return value from the lookup should occur through script or a server side
      * post (default is false, server side post).
      *
      * @return boolean true if return should occur though script, false if return should be done through server
      *         side post
      */
     public boolean isReturnByScript() {
         return returnByScript;
     }

     /**
      * @see LookupForm#isReturnByScript()
      */
     public void setReturnByScript(boolean returnByScript) {
         this.returnByScript = returnByScript;
     }

    /**
     * Name of the window the lookup should return to.
     *
     * <p>The lookup can be invoked from several different contexts: new tab, lightbox within top window, lightbox
     * within portal window. When the request is made, this parameter can be sent to specify the target for
     * the return links.</p>
     *
     * @return String return target window name
     */
    public String getReturnTarget() {
        return returnTarget;
    }

    /**
     * org.kuali.rice.krad.lookup.LookupForm#getReturnTarget()
     */
    public void setReturnTarget(String returnTarget) {
        this.returnTarget = returnTarget;
    }

    /**
     * For the case of multi-value lookup, indicates the collection that should be populated with
     * the return results.
     *
     * @return String collection name (must be full binding path)
     */
    public String getLookupCollectionName() {
        return lookupCollectionName;
    }

    /**
     * @see LookupForm#getLookupCollectionName()
     */
    public void setLookupCollectionName(String lookupCollectionName) {
        this.lookupCollectionName = lookupCollectionName;
    }

    public String getLookupCollectionId() {
        return lookupCollectionId;
    }

    public void setLookupCollectionId(String lookupCollectionId) {
        this.lookupCollectionId = lookupCollectionId;
    }

    /**
     * String containing references that should be refreshed when the lookup returns, passed back on the
     * return URL.
     *
     * @return String containing references that should be refreshed on return from lookup
     */
    public String getReferencesToRefresh() {
        return referencesToRefresh;
    }

    /**
     * @see LookupForm#getReferencesToRefresh()
     */
    public void setReferencesToRefresh(String referencesToRefresh) {
        this.referencesToRefresh = referencesToRefresh;
    }

    /**
     * Id for the quickfinder that triggered the lookup action (if any).
     *
     * <p>When the lookup is triggered from a quickfinder, the return URLs will be present on the lookup
     * results. In addition, the quickfinder id is passed back on the return URL so the caller can perform logic
     * based on which quickfinder was invoked.</p>
     *
     * @return String id for quickfinder that invoked the lookup
     */
    public String getQuickfinderId() {
        return quickfinderId;
    }

    /**
     * @see LookupForm#getQuickfinderId()
     */
    public void setQuickfinderId(String quickfinderId) {
        this.quickfinderId = quickfinderId;
    }

    /**
     * Map of conversions that should occur on the lookup return between properties on the lookup data object
     * and properties on the calling view.
     *
     * <p>When a lookup is invoked from a calling view, the purpose is to return one or more values that will
     * populate fields on the calling view. To accomplish this, values for properties on the selected record
     * are passed back on the URL as values for properties on the calling view. This map specifies which properties
     * on the lookup data object should be pulled, and for each one what is the property on the caller to
     * send the value back as.</p>
     *
     * <p>For example, suppose the map contained the entries id:document.bookId and title:document.bookTitle. When the
     * return URL is selected for a record, the value for the id property will be retrieved and added to the return
     * URL query string as 'document.bookId={idValue}'. Likewise the value for the title property will be pulled
     * and added to the return URL query string as 'document.bookTitle={titleValue}'. So the query string will contain
     * something like 'document.bookId=3&document.bookTitle=Animals'</p>
     *
     * @return Map of field conversions, each entry is a conversion between two properties. Key is property name
     *         on the lookup data object, entry value is the property name on the calling view/model
     */
    public Map<String, String> getFieldConversions() {
        return this.fieldConversions;
    }

    /**
     * @see LookupForm#getFieldConversions()
     */
    public void setFieldConversions(Map<String, String> fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    /**
     * Holds the column names for the multi-value lookup selected values
     *
     * Note: as of KULRICE-12125 secure field names will not be stored in this parameter
     * @return a list of column names for the multi-value lookup
     */
    public List<String> getMultiValueReturnFields() {
        return multiValueReturnFields;
    }

    /**
     * @see LookupForm#getMultiValueReturnFields()
     */
    public void setMultiValueReturnFields(List<String> multiValueReturnFields) {
        this.multiValueReturnFields = multiValueReturnFields;
    }

    /**
     * Map containing the criteria to be used for performing the search.
     *
     * <p>Fields that are defined in the {@link org.kuali.rice.krad.lookup.LookupView#getCriteriaGroup()} bind
     * to this map. The key of the map is the property path specified for the field, and the value of the map
     * is the search value (if any) entered by the user. This map is then passed into the {@link Lookupable} to
     * carry out the search.</p>
     *
     * @return Map of search criteria where key is the property the criteria will be applied to and the value is
     *         the search value entered by the user (if any)
     */
    public Map<String, String> getLookupCriteria() {
        return this.lookupCriteria;
    }

    /**
     * @see LookupForm#getLookupCriteria()
     */
    public void setLookupCriteria(Map<String, String> lookupCriteria) {
        this.lookupCriteria = lookupCriteria;
    }

    /**
     * Holds the results of a search action.
     *
     * <p>After the search action is invoked, the results of the search will be held by this property. The
     * {@link org.kuali.rice.krad.lookup.LookupView#getResultsGroup()} binds to this property for displaying
     * the results.</p>
     *
     * @return Collection of data objects that are the result of a search
     */
    public Collection<?> getLookupResults() {
        return this.lookupResults;
    }

    /**
     * @see LookupForm#getLookupResults()
     */
    public void setLookupResults(Collection<?> lookupResults) {
        this.lookupResults = lookupResults;
    }

    public boolean isDisplayResults() {
        return displayResults;
    }

    public void setDisplayResults(boolean displayResults) {
        this.displayResults = displayResults;
    }
}
