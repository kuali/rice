/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");} else {throw new RuntimeException("Attempted to call PojoFormBase");}
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
package org.kuali.rice.kns.web.spring.form;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.ActionServletWrapper;
import org.apache.struts.upload.MultipartRequestHandler;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.struts.pojo.PojoFormBase;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KualiSpringInterceptorForm extends PojoFormBase {
	
	private boolean isUsingSpring = false;

	public boolean isUsingSpring() {
    	return this.isUsingSpring;
    }

	public void setUsingSpring(boolean isUsingSpring) {
    	this.isUsingSpring = isUsingSpring;
    }

	/*
	 * THIS IS FROM KUALI FORM
	 */
	@Override
    public MultipartRequestHandler getMultipartRequestHandler() {
	    if (!isUsingSpring) {
	    return super.getMultipartRequestHandler();} else {throw new RuntimeException("Attempted to call ActionForm");}
    }

	@Override
    protected ActionServlet getServlet() {
	    if (!isUsingSpring) {
	    return super.getServlet();} else {throw new RuntimeException("Attempted to call ActionForm");}
    }

	@Override
    public ActionServletWrapper getServletWrapper() {
	    if (!isUsingSpring) {
	    return super.getServletWrapper();} else {throw new RuntimeException("Attempted to call ActionForm");}
    }

	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
	    if (!isUsingSpring) {
	    super.reset(mapping, request);} else {throw new RuntimeException("Attempted to call ActionForm");}
    }

	@Override
    public void reset(ActionMapping mapping, ServletRequest request) {
	    if (!isUsingSpring) {
	    super.reset(mapping, request);} else {throw new RuntimeException("Attempted to call ActionForm");}
    }

	@Override
    public void setMultipartRequestHandler(MultipartRequestHandler multipartRequestHandler) {
	    if (!isUsingSpring) {
	    super.setMultipartRequestHandler(multipartRequestHandler);} else {throw new RuntimeException("Attempted to call ActionForm");}
    }

	@Override
    public void setServlet(ActionServlet servlet) {
	    if (!isUsingSpring) {
	    super.setServlet(servlet);} else {throw new RuntimeException("Attempted to call ActionForm");}
    }

	@Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
	    if (!isUsingSpring) {
	    return super.validate(mapping, request);} else {throw new RuntimeException("Attempted to call ActionForm");}
    }

	@Override
    public ActionErrors validate(ActionMapping mapping, ServletRequest request) {
	    if (!isUsingSpring) {
	    return super.validate(mapping, request);} else {throw new RuntimeException("Attempted to call ActionForm");}
    }

	/*
	 * THIS IS FROM POJO FORM
	 */
	@Override
    public void addRequiredNonEditableProperties() {
	    if (!isUsingSpring) {
	    super.addRequiredNonEditableProperties();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    protected void cacheUnconvertedValue(String key, Object value) {
	    if (!isUsingSpring) {
	    super.cacheUnconvertedValue(key, value);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void clearEditablePropertyInformation() {
	    if (!isUsingSpring) {
	    super.clearEditablePropertyInformation();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void copyPopulateEditablePropertiesToActionEditableProperties() {
	    if (!isUsingSpring) {
	    super.copyPopulateEditablePropertiesToActionEditableProperties();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    protected void customInitMaxUploadSizes() {
	    if (!isUsingSpring) {
	    super.customInitMaxUploadSizes();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    protected Class formatterClassForKeypath(String keypath) {
	    if (!isUsingSpring) {
	    return super.formatterClassForKeypath(keypath);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public Object formatValue(Object value, String keypath, Class type) {
	    if (!isUsingSpring) {
	    return super.formatValue(value, keypath, type);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public String getActionEditablePropertiesGuid() {
	    if (!isUsingSpring) {
	    return super.getActionEditablePropertiesGuid();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public Set<String> getEditableProperties() {
	    if (!isUsingSpring) {
	    return super.getEditableProperties();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    protected Formatter getFormatter(String keypath, Class propertyType) {
	    if (!isUsingSpring) {
	    return super.getFormatter(keypath, propertyType);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public Map getFormatterTypes() {
	    if (!isUsingSpring) {
	    return super.getFormatterTypes();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public boolean getIsNewForm() {
	    if (!isUsingSpring) {
	    return super.getIsNewForm();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public Set<String> getMethodToCallsToBypassSessionRetrievalForGETRequests() {
	    if (!isUsingSpring) {
	    return super.getMethodToCallsToBypassSessionRetrievalForGETRequests();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    protected String getParameter(HttpServletRequest request, String parameterName) {
	    if (!isUsingSpring) {
	    return super.getParameter(request, parameterName);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    protected String[] getParameterValues(HttpServletRequest request, String parameterName) {
	    if (!isUsingSpring) {
	    return super.getParameterValues(request, parameterName);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public Set<String> getPopulateEditableProperties() {
	    if (!isUsingSpring) {
	    return super.getPopulateEditableProperties();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public String getPopulateEditablePropertiesGuid() {
	    if (!isUsingSpring) {
	    return super.getPopulateEditablePropertiesGuid();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    protected Class getPropertyType(String keypath) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	    if (!isUsingSpring) {
	    return super.getPropertyType(keypath);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public Set<String> getRequiredNonEditableProperties() {
	    if (!isUsingSpring) {
	    return super.getRequiredNonEditableProperties();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public String getStrutsActionMappingScope() {
	    if (!isUsingSpring) {
	    return super.getStrutsActionMappingScope();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public Map getUnconvertedValues() {
	    if (!isUsingSpring) {
	    return super.getUnconvertedValues();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    protected List getUnknownKeys() {
	    if (!isUsingSpring) {
	    return super.getUnknownKeys();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public boolean isPropertyEditable(String propertyName) {
	    if (!isUsingSpring) {
	    return super.isPropertyEditable(propertyName);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public boolean isPropertyNonEditableButRequired(String propertyName) {
	    if (!isUsingSpring) {
	    return super.isPropertyNonEditableButRequired(propertyName);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void populate(HttpServletRequest request) {
	    if (!isUsingSpring) {
	    super.populate(request);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    protected void populateForProperty(String paramPath, Object paramValue, Map params) {
	    if (!isUsingSpring) {
	    super.populateForProperty(paramPath, paramValue, params);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void postprocessRequestParameters(Map requestParameters) {
	    if (!isUsingSpring) {
	    super.postprocessRequestParameters(requestParameters);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void processValidationFail() {
	    if (!isUsingSpring) {
	    super.processValidationFail();} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void registerEditableProperty(String editablePropertyName) {
	    if (!isUsingSpring) {
	    super.registerEditableProperty(editablePropertyName);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void registerIsNewForm(boolean isNewForm) {
	    if (!isUsingSpring) {
	    super.registerIsNewForm(isNewForm);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void registerRequiredNonEditableProperty(String requiredNonEditableProperty) {
	    if (!isUsingSpring) {
	    super.registerRequiredNonEditableProperty(requiredNonEditableProperty);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void registerStrutsActionMappingScope(String strutsActionMappingScope) {
	    if (!isUsingSpring) {
	    super.registerStrutsActionMappingScope(strutsActionMappingScope);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void setActionEditablePropertiesGuid(String guid) {
	    if (!isUsingSpring) {
	    super.setActionEditablePropertiesGuid(guid);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void setFormatterType(String keypath, Class type) {
	    if (!isUsingSpring) {
	    super.setFormatterType(keypath, type);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void setFormatterTypes(Map formatterTypes) {
	    if (!isUsingSpring) {
	    super.setFormatterTypes(formatterTypes);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void setPopulateEditablePropertiesGuid(String guid) {
	    if (!isUsingSpring) {
	    super.setPopulateEditablePropertiesGuid(guid);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public void setUnconvertedValues(Map unconvertedValues) {
	    if (!isUsingSpring) {
	    super.setUnconvertedValues(unconvertedValues);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

	@Override
    public boolean shouldPropertyBePopulatedInForm(String requestParameterName, HttpServletRequest request) {
	    if (!isUsingSpring) {
	    return super.shouldPropertyBePopulatedInForm(requestParameterName, request);} else {throw new RuntimeException("Attempted to call PojoFormBase");}
    }

}
