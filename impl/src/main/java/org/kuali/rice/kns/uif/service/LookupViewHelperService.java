/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.uif.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.BusinessObject;

/**
 * A ViewHelperService that is specifically used for Lookups
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LookupViewHelperService extends ViewHelperService {

	/**
	 * This method ...
	 * 
	 * @param criteriaFieldsForLookup
	 * @param bounded
	 * @return the list of result BOs, possibly bounded
	 */
	public Collection<? extends BusinessObject> performSearch(Map<String, String> criteriaFieldsForLookup, boolean bounded);

	/**
	 * 
	 * This method allows for customization of the lookup clear
	 * 
	 */
	public Map<String, String> performClear(Map<String, String> fieldsForLookup);

	/**
	 * Validates the values filled in as search criteria, also checks for required field values.
	 * 
	 * @param fieldValues - Map of property/value pairs
	 */
	public void validateSearchParameters(Map<String, String> fieldValues);

	/**
	 * Gets the readOnlyFieldsList attribute.
	 * 
	 * @return Returns the readOnlyFieldsList.
	 */
	public List<String> getReadOnlyFieldsList();

	/**
	 * Sets the readOnlyFieldsList attribute value.
	 * 
	 * @param readOnlyFieldsList The readOnlyFieldsList to set.
	 */
	public void setReadOnlyFieldsList(List<String> readOnlyFieldsList);

	public void setDataObjectClass(Class dataObjectClass);

	public void setFieldConversions(Map<String, String> fieldConversions);

}
