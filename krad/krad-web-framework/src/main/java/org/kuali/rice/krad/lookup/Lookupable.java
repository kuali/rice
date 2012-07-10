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
package org.kuali.rice.krad.lookup;

import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.LinkField;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.web.form.LookupForm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Provides contract for implementing a lookup within the lookup framework
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Lookupable extends ViewHelperService, java.io.Serializable {

    /**
     * Invoked to carry out the lookup search based on the given map of key/value search
     * values
     *
     * @param form - lookup form instance containing the lookup data
     * @param searchCriteria - map of criteria currently set
     * @param bounded - indicates whether the results should be limited (if necessary) to the max search
     * result limit configured
     * @return the list of result objects, possibly bounded
     */
    public Collection<?> performSearch(LookupForm form, Map<String, String> searchCriteria, boolean bounded);

    /**
     * Invoked when the clear action is requested to result the search fields to
     * their initial default values
     *
     * @param form - lookup form instance containing the lookup data
     * @param searchCriteria - map of criteria currently set
     * @return map of criteria with field values reset to defaults
     */
    public Map<String, String> performClear(LookupForm form, Map<String, String> searchCriteria);

    /**
     * Invoked to perform validation on the search criteria before the search is performed
     *
     * @param form - lookup form instance containing the lookup data
     * @param searchCriteria - map of criteria where key is search property name and value is
     * search value (which can include wildcards)
     * @param boolean true if validation was successful, false if there were errors and the search
     * should not be performed
     */
    public boolean validateSearchParameters(LookupForm form, Map<String, String> searchCriteria);

    /**
     * Sets the class for the data object the lookup will be provided on
     *
     * @param dataObjectClass - data object class for lookup
     */
    public void setDataObjectClass(Class<?> dataObjectClass);

    /**
     * Returns the class for the data object the lookup is configured with
     *
     * @return Class<?> data object class
     */
    public Class<?> getDataObjectClass();

    /**
     * Sets the field conversion map on the lookupable
     *
     * <p>
     * The field conversions map specifies the mappings for return fields. When the
     * user selects a row to return, for each configured field conversion the corresponding value
     * from the result row will be sent back as the value for the field on the calling field.
     * </p>
     *
     * @param fieldConversions - map of field conversions where key is name of the property on result
     * data object to get value for, and map value is the name of the field to send the value back as (name
     * of the field on the calling view)
     */
    public void setFieldConversions(Map<String, String> fieldConversions);

    /**
     * Sets List of fields on the lookupable that should be made read only in the search
     * criteria group
     *
     * @param readOnlyFieldsList - list of read only fields
     */
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList);

    /**
     * Invoked to build the return URL for a result row
     *
     * <p>
     * Based on the line contained in the field context, the URL for returning the role is constructed and
     * set as the href for the link field. If a return link cannot be constructed the field should be set
     * to not render
     * </p>
     *
     * @param returnLinkField - link field that will be used to render the return URL
     * @param model - lookup form containing the data
     */
    public void getReturnUrlForResults(LinkField returnLinkField, Object model);

    /**
     * Invoked to build a maintenance URL for a result row
     *
     * <p>
     * Based on the line contained in the field context and the given maintenance method that should be called a
     * URL is constructed and set as the href on the link field. If a maintenance link cannot be constructed the
     * field should be set to not render
     * </p>
     *
     * @param actionLinkField - link field that will be used to return the maintenance URL
     * @param model - lookup form containing the data
     * @param maintenanceMethodToCall - name of the method that should be invoked in the maintenance controller
     */
    public void getMaintenanceActionLink(LinkField actionLinkField, Object model, String maintenanceMethodToCall);

    public void setMultiValueLookupSelect(InputField selectField, Object model);
}
