/*
 * Copyright 2004 Jonathan M. Lehr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * 
 * MODIFIED BY THE KUALI FOUNDATION
 */
package org.kuali.rice.kns.web.struts.pojo;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * begin Kuali Foundation modification
 * This interface defines methods that Pojo Forms must provide.
 * end Kuali Foundation modification
 */
// Kuali Foundation modification: original name: SLForm
public interface PojoForm {
    public void populate(HttpServletRequest request);

    // begin Kuali Foundation modification
    // cachedActionErrors() method removed
    public void postprocessRequestParameters(Map requestParameters);
    // end Kuali Foundation modification

    public Map getUnconvertedValues();

    public Object formatValue(Object value, String keypath, Class type);

    // begin Kuali Foundation modification
    public void processValidationFail();
    
    Set<String> getRequiredNonEditableProperties();
    
    void registerEditableProperty(String editablePropertyName);
    
    /**
     * Reinitializes the form to allow it to register the editable properties of the currently processing request.
     */
    void clearEditablePropertyInformation();

    Set<String> getEditableProperties();
    
    /**
     * 
     * This method adds the required property names, that are not directly editable by user on the html page, to a list, regardless of the context
     * in which they appear.  Request parameter names corresponding to these properties
     * will be populated into the form. 
     *
     */
    void addRequiredNonEditableProperties();
    
    /**
     * Sets the value of the "scope" attribute for the Struts action mapping corresponding to this form instance.  Note that this
     * method name is NOT in the syntax of the conventional POJO setter; this is to prevent clients from maliciously altering the value
     * of this parameter
     * 
     * @param scope
     */
    public void registerStrutsActionMappingScope(String scope);
    
    /**
	 * Returns whether a request parameter should be populated as a property of the form, assuming that the request parameter name
	 * corresponds to a property on the form.  This method makes no determination whether the request parameter is a property of the form, but rather
	 * from a security perspective, whether the framework should attempt to set the form property with the same name as the request parameter. 
	 * 
	 * @param requestParameterName the name of the request parameter
	 * @param request the HTTP request
	 * @return whether the parameter should be 
	 */
	public boolean shouldPropertyBePopulatedInForm(String requestParameterName, HttpServletRequest request);
	
	/**
	 * Initializes the set of editable properties from the previous request, so that it is possible to determine which properties were
	 * rendered as editable.
	 */
	public void switchEditablePropertyInformationToPreviousRequestInformation();
	
	/**
	 * Returns the list of properties that were editable when the webpage for the previous request was rendered.
	 * 
	 */
	public Set<String> getEditablePropertiesFromPreviousRequest();
    // end Kuali Foundation modification

}
