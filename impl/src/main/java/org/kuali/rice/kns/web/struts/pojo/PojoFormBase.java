/*
 * Copyright 2004 Jonathan M. Lehr
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
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
 
// begin Kuali Foundation modification
package org.kuali.rice.kns.web.struts.pojo;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.Timer;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.format.EncryptionFormatter;
import org.kuali.rice.kns.web.format.FormatException;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This class is the base form which implements the PojoForm interface.
 * Kuali Foundation modification: javadoc comments changed
 */
// begin Kuali Foundation modification: this class was named SLActionForm
public class PojoFormBase extends ActionForm implements PojoForm {
    private static final long serialVersionUID = 1L;
    
    // begin Kuali Foundation modification
    private static final Logger LOG = Logger.getLogger(PojoFormBase.class);

	// removed member variables: cachedActionErrors, coder, errorInfo, fieldOrder, formConfig, HEADING_KEY, IGNORED_KEYS,
	//     invalidValueKeys, logger, messageResourceKey, messageResources, padNonRequiredFields, valueBinder
	 
    static final String CREATE_ERR_MSG = "Can't create formatter for keypath ";
    static final String CONVERT_ERR_MSG = "Can't convert value for keypath: ";

    static Map classCache = Collections.synchronizedMap(new HashMap());

    private Map unconvertedValues = new HashMap();
    private List unknownKeys = new ArrayList();
    private Map formatterTypes = new HashMap();
    private Map encryptedProperties = new HashMap();
    private List<String> maxUploadFileSizes = new ArrayList<String>();
    private Set<String> editableProperties = new HashSet<String>();
    private transient Set<String> editablePropertiesFromPreviousRequest;
    protected Set<String> requiredNonEditableProperties = new HashSet<String>();
    private String strutsActionMappingScope; 

    // removed methods: PojoFormBase()/SLActionForm(), addFormLevelMessageInfo, addGlobalMessage, addIgnoredKey, addIgnoredKeys, addLengthValidation, addMessageIfAbsent
    //     addPatternValidation, addPropertyValidationRules, addRangeValidation, addRequiredField, addRequiredFields
    //     addUnknownKey, addValidationRule(String, ValidationRule), addValidationRule(ValidationRule), cachedActionErrors, clearIgnoredKeys,
    //     clearUnknownKeys, clearValidationErrors, coalesceMessageArgs, containsKey, convertValue, createActionMessage, createMessageResourcesIfNecessary, fieldOrder, fieldValidationRuleOrder,
    //     formatMessage, formatMessageArgs, formatterSettingsForKeypath, formatterTypeForKeypath, formBeanConfigForKey, formConfig, formValidationRuleOrder,
    //     generateErrorMessages, getActionErrors, getActionMessages, getErrorMessages, getFieldLabel, getFormatterTypes, getGlobalMessages, getIgnoredKeys, getInvalidValueKeys, getLabels, getLengthValidations, getLocale,
    //     getMultipartRequestParameters, getPadNonRequiredFields, 
    //     getPatternValidations, getPropertyConfig, getRangeValidations, getRequiredFields, hasErrorMessageForKey, hasErrors, hasFormatterForKeypath,
    //     hasGlobalMessageForKey, isMultipart, messageForKey, messageForRule, messageInfoForRule, messageResourcesConfigForKey, messageResourcesKey, messageResourcesPath,
    //     messagesForFormLevelRule, messagesForKey, moduleConfigForRequest, removeIgnoredKey, removePropertyConfig,
    //     renderErrorMessages, renderGlobalMessages, renderMessages, setFieldLabel, setFieldOrder, setFormatterType(String, Class, Map)
    //     setFormConfig, setInvalidValueKeys, setLengthValidations, setMessageResourceKey,setPadNonRequiredFields, setPatternValidations, setPropertyConfig, setRangeValidations,
    //     setRequiredFields, setValueBinder, shouldFormat, validate, validateForm, validateLength, validatePattern, validateProperty, validateRange, validateRequestValues, validateRequired, valueBinder

	// end Kuali Foundation modification
	

	// begin Kuali Foundation modification
    /**
     * Method is called after parameters from a multipart request have been made accessible to request.getParameter calls, but
     * before request parameter values are used to instantiate and populate business objects. Important note: parameters in the
     * given Map which were created from a multipart-encoded parameter will, apparently, be stored in the given Map as String[]
     * instead of as String.
     *
     * @param requestParameters
     */

    public void postprocessRequestParameters(Map requestParameters) {
        // do nothing
    }
    // end Kuali Foundation modification

 

    /**
     * Populates the form with values from the current request. Uses instances of Formatter to convert strings to the Java types of
     * the properties to which they are bound. Values that can't be converted are cached in a map of unconverted values. Returns an
     * ActionErrors containing ActionMessage instances for each conversion error that occured, if any.
     */
    public void populate(HttpServletRequest request) {
        Timer t0 = new Timer("PojoFormBase.populate");
        unconvertedValues.clear();
        unknownKeys = new ArrayList();
        addRequiredNonEditableProperties();
        Map params = request.getParameterMap();

        String contentType = request.getContentType();
        String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method) && contentType != null && contentType.startsWith("multipart/form-data")) {
            Map fileElements = (HashMap)request.getAttribute(KNSConstants.UPLOADED_FILE_REQUEST_ATTRIBUTE_KEY);
            Enumeration names = Collections.enumeration(fileElements.keySet());
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                params.put(name, fileElements.get(name));
            }
        }

        postprocessRequestParameters(params);


        /**
         * Iterate through request parameters, if parameter matches a form variable, get the property type, formatter and convert,
         * if not add to the unknowKeys map.
         */
        for (Iterator iter = params.keySet().iterator(); iter.hasNext();) {
            String keypath = (String) iter.next();
            if (shouldPropertyBePopulatedInForm(keypath, request)) {
	            Object param = params.get(keypath);
	            //LOG.debug("(keypath,paramType)=(" + keypath + "," + param.getClass().getName() + ")");
	
	            // get type for property
	            Class type = null;
	            try {
	                // TODO: see KULOWF-194
	                //testForPojoHack(this, keypath);
	                type = getPropertyType(keypath);
	            }
	            catch (Exception e) {
	                // deleted redundant unknownKeys.add(keypath)
	            }
	
	            // keypath does not match anything on form
	            if (type == null) {
	                unknownKeys.add(keypath);
	            }
	            else {
	                Formatter formatter = null;
	                try {
	                    formatter = buildFormatter(keypath, type, params);
	
	                    ObjectUtils.setObjectProperty(formatter, this, keypath, type, param);
	                	}
	                catch (FormatException e1) {
	                    GlobalVariables.getErrorMap().putError(keypath, e1.getErrorKey(), e1.getErrorArgs());
	                    cacheUnconvertedValue(keypath, param);
	                }
	                catch (InvocationTargetException e1) {
	                    if (e1.getTargetException().getClass().equals(FormatException.class)) {
	                        // Handle occasional case where FormatException is wrapped in an InvocationTargetException
	                        FormatException formatException = (FormatException) e1.getTargetException();
	                        GlobalVariables.getErrorMap().putError(keypath, formatException.getErrorKey(), formatException.getErrorArgs());
	                        cacheUnconvertedValue(keypath, param);
	                    }
	                    else {
	                        LOG.error("Error occurred in populate " + e1.getMessage());
	                        throw new RuntimeException(e1.getMessage(), e1);
	                    }
	                }
	                catch (Exception e1) {
	                    LOG.error("Error occurred in populate " + e1.getMessage());
	                    throw new RuntimeException(e1.getMessage(), e1);
	                }
	            }
            }
        }
        t0.log();
    }

	// begin Kuali Foundation modification
    private Formatter buildFormatter(String keypath, Class propertyType, Map requestParams) {
        Formatter formatter = buildFormatterForKeypath(keypath, propertyType, requestParams);
        if (formatter == null) {
            formatter = buildFormatterForType(propertyType);
        }
        return formatter;
    }
    // end Kuali Foundation modification

	// begin Kuali Foundation modification
    private Formatter buildFormatterForKeypath(String keypath, Class propertyType, Map requestParams) {
        Formatter formatter = null;

        // check if keypath was sent in encrypted property map
        Class formatterClass = null;
        if (requestParams.containsKey("encryptedProperties('" + keypath.replace( '.', '_' ) + "')")) {
            formatterClass = EncryptionFormatter.class;
        }
        else {
            formatterClass = formatterClassForKeypath(keypath);
        }

        if (formatterClass != null) {
            try {
                formatter = (Formatter) formatterClass.newInstance();
            }
            catch (InstantiationException e) {
                throw new FormatException("unable to instantiate formatter class '" + formatterClass.getName() + "'", e);
            }
            catch (IllegalAccessException e) {
                throw new FormatException("unable to access formatter class '" + formatterClass.getName() + "'", e);
            }
            formatter.setPropertyType(propertyType);
        }
        return formatter;
    }
    // end Kuali Foundation modification

	// begin Kuali Foundation modification
    private Formatter buildFormatterForType(Class propertyType) {
        Formatter formatter = null;

        if (Formatter.findFormatter(propertyType) != null) {
            formatter = Formatter.getFormatter(propertyType);
        }
        return formatter;
    }
    // end Kuali Foundation modification

	// begin Kuali Foundation modification
    /**
     * <p>
     * Create a <code>Map</code> containing all of the parameters supplied for a multipart request, keyed by parameter name. In
     * addition to text and file elements from the multipart body, query string parameters are included as well.
     * </p>
     *
     * @param request The (wrapped) HTTP request whose parameters are to be added to the map.
     * @param multipartHandler The multipart handler used to parse the request.
     *
     * @return the map containing all parameters for this multipart request.
     */
    private static Map getAllParametersForMultipartRequest(HttpServletRequest request, MultipartRequestHandler multipartHandler) {
        Timer t0 = new Timer("PojoFormBase.getAllParametersForMultipartRequest");

        Map parameters = new HashMap();
        Hashtable elements = multipartHandler.getAllElements();
        Enumeration e = elements.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            parameters.put(key, elements.get(key));
        }

        if (request instanceof MultipartRequestWrapper) {
            request = ((MultipartRequestWrapper) request).getRequest();
            e = request.getParameterNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                parameters.put(key, request.getParameterValues(key));
            }
        }
        else {
            LOG.debug("Gathering multipart parameters for unwrapped request");
        }

        t0.log();
        return parameters;
    }
    // end Kuali Foundation modification

    /**
     * Delegates to {@link PropertyUtils#getPropertyType(Object, String)}to look up the property type for the provided keypath.
     * Caches the resulting class so that subsequent lookups for the same keypath can be satisfied by looking in the cache.
     *
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected Class getPropertyType(String keypath) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map propertyTypes = (Map) classCache.get(getClass());
        if (propertyTypes == null) {
            propertyTypes = new HashMap();
            classCache.put(getClass(), propertyTypes);
        }

        // if type has not been retrieve previousely, use ObjectUtils to get type
        if (!propertyTypes.containsKey(keypath)) {
            Class type = ObjectUtils.easyGetPropertyType(this, keypath);
            propertyTypes.put(keypath, type);
        }

        Class propertyType = (Class) propertyTypes.get(keypath);
        return propertyType;
    }


    /**
     * Retrieves a formatter for the keypath and property type.
     *
     * @param keypath
     * @param propertyType
     * @return
     */
    protected Formatter getFormatter(String keypath, Class propertyType) {
        // check for a formatter associated with the keypath
        Class type = formatterClassForKeypath(keypath);

        Formatter formatter;
        if (type == null) {
            // retrieve formatter based on property type
            formatter = Formatter.getFormatter(propertyType);
        }
        else {
            try {
                formatter = (Formatter) type.newInstance();
                formatter.setPropertyType(propertyType);
            }
            catch (Exception e) {
                throw new ValidationException(CREATE_ERR_MSG, e);
            }
        }
        return formatter;
    }


	// begin Kuali Foundation modification
    /**
     * Retrieves any formatters associated specially with the keypath.
     *
     * @param keypath
     * @return
     */
    protected Class formatterClassForKeypath(String keypath) {
        // remove traces of array and map indices from the incoming keypath
        String indexlessKey = keypath.replaceAll("(\\[[0-9]*+\\]|\\(.*?\\))", "");

        return (Class)formatterTypes.get( indexlessKey );
    }
    // end Kuali Foundation modification

    /**
     * Tries to format the provided value by passing it to a suitable {@link Formatter}. Adds an ActionMessage to the ActionErrors
     * in the request if a FormatException is thrown.
     * <p>
     * Caution should be used when invoking this method. It should never be called prior to {@link #populate(HttpServletRequest)}
     * because the cached request reference could be stale.
     */
    public Object formatValue(Object value, String keypath, Class type) {

        Formatter formatter = getFormatter(keypath, type);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("formatValue (value,keypath,type) = (" + value + "," + keypath + "," + type.getName() + ")");
        }

        try {
            return Formatter.isSupportedType(type) ? formatter.formatForPresentation(value) : value;
        }
        catch (FormatException e) {
            GlobalVariables.getErrorMap().putError(keypath, e.getErrorKey(), e.getErrorArgs());
            return value.toString();
        }
    }

    /**
     * Sets the Formatter class to use for a given keypath. This class will be used by the form instead of the one returned by calls
     * to {@link Formatter#getFormatter(Class)}, which is the default mechanism.
     */
    public void setFormatterType(String keypath, Class type) {
        formatterTypes.put(keypath, type);
    }

    public Map getUnconvertedValues() {
        return unconvertedValues;
    }

    public void setUnconvertedValues(Map unconvertedValues) {
        this.unconvertedValues = unconvertedValues;
    }

    protected List getUnknownKeys() {
        return unknownKeys;
    }

    protected void cacheUnconvertedValue(String key, Object value) {
        Class type = value.getClass();
        if (type.isArray()) {
            value = Formatter.isEmptyValue(value) ? null : ((Object[]) value)[0];
        }

        unconvertedValues.put(key, value);
    }

	// begin Kuali Foundation modification
    public void processValidationFail() {
        // do nothing - subclasses can implement this if they want to.
    }
    // end Kuali Foundation modification


	// begin Kuali Foundation modification
    /**
     * Gets the formatterTypes attribute.
     * 
     * @return Returns the formatterTypes.
     */
    public Map getFormatterTypes() {
        return formatterTypes;
    }
    // end Kuali Foundation modification


	// begin Kuali Foundation modification
    /**
     * Sets the formatterTypes attribute value.
     * @param formatterTypes The formatterTypes to set.
     */
    public void setFormatterTypes(Map formatterTypes) {
        this.formatterTypes = formatterTypes;
    }
    // end Kuali Foundation modification


	// begin Kuali Foundation modification
    /**
     * Gets the encryptedProperties attribute. 
     * @return Returns the encryptedProperties.
     */
    public Map getEncryptedProperties() {
        return encryptedProperties;
    }
    // end Kuali Foundation modification


	// begin Kuali Foundation modification
    /**
     * Sets the encryptedProperties attribute value.
     * @param encryptedProperties The encryptedProperties to set.
     */
    public void setEncryptedProperties(Map encryptedProperties) {
        this.encryptedProperties = encryptedProperties;
    }

    /**
     * Adds the given string as a maximum size to the form.  It will be used if a file upload is used.
     * 
     * @param sizeString
     */
    protected final void addMaxUploadSize( String sizeString ) {
	maxUploadFileSizes.add( sizeString );
    }

    /**
     * Initializes the list of max upload sizes if necessary. 
     *
     */
    protected final void initMaxUploadSizes() {
    	if ( maxUploadFileSizes.isEmpty() ) {
    	    customInitMaxUploadSizes();
    	    // if it's still empty, add the default
    	    if ( maxUploadFileSizes.isEmpty() ) {
    	        addMaxUploadSize(KNSServiceLocator.getKualiConfigurationService().getParameterValue(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.ALL_DETAIL_TYPE, KNSConstants.MAX_UPLOAD_SIZE_PARM_NM));
    	    }
    	}	
    }
    
    /**
     * Subclasses can override this to add their own max upload size to the list.  Only the largest passed will be used.
     *
     */
    protected void customInitMaxUploadSizes() {
	// nothing here
    }
    
    public final List<String> getMaxUploadSizes() {
	initMaxUploadSizes();
	
	return maxUploadFileSizes;
    }
    
    public void registerEditableProperty(String editablePropertyName){
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Registering: " + editablePropertyName);
    	}
    	editableProperties.add(editablePropertyName);
    }
    
    public void registerRequiredNonEditableProperty(String requiredNonEditableProperty) {
    	requiredNonEditableProperties.add(requiredNonEditableProperty);
    }
    
    public void clearEditablePropertyInformation(){
    	editableProperties = new HashSet<String>();
    }
    
    public Set<String> getEditableProperties(){
    	return editableProperties;
    }
 
    public boolean isPropertyEditable(String propertyName) {
        return WebUtils.isPropertyEditable(getEditablePropertiesFromPreviousRequest(), propertyName);
    }
    
    /***
     * @see org.kuali.rice.kns.web.struts.pojo.PojoForm#addRequiredNonEditableProperties()
     */
    public void addRequiredNonEditableProperties(){
    }
    
    public boolean isPropertyNonEditableButRequired(String propertyName) {
        return WebUtils.isPropertyEditable(requiredNonEditableProperties, propertyName);
    }
    
    protected String getParameter(HttpServletRequest request, String parameterName){
    	return request.getParameter(parameterName);
    }
    
    protected String[] getParameterValues(HttpServletRequest request, String parameterName){
    	return request.getParameterValues(parameterName);
    }
    
    public Set<String> getRequiredNonEditableProperties(){
    	return requiredNonEditableProperties;
    }
    
	/**
	 * @see PojoForm#registerStrutsActionMappingScope(String)
	 */
	public void registerStrutsActionMappingScope(String strutsActionMappingScope) {
		this.strutsActionMappingScope = strutsActionMappingScope;
	}
	
	public String getStrutsActionMappingScope() {
		return strutsActionMappingScope;
	}
	
	/**
	 * @see org.kuali.rice.kns.web.struts.pojo.PojoForm#shouldPropertyBePopulatedInForm(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public boolean shouldPropertyBePopulatedInForm(String requestParameterName, HttpServletRequest request) {
		if (StringUtils.equalsIgnoreCase("scope", getStrutsActionMappingScope())) {
		   return isPropertyEditable(requestParameterName) || isPropertyNonEditableButRequired(requestParameterName);
		}
    	return true;
	}
	
	/**
	 * @see org.kuali.rice.kns.web.struts.pojo.PojoForm#switchEditablePropertiesToEditablePropertiesFromPreviousRequest()
	 */
	public void switchEditablePropertyInformationToPreviousRequestInformation() {
		editablePropertiesFromPreviousRequest = editableProperties;
	}
	
	/**
	 * Returns the list of properties that were editable when the webpage for the previous request was rendered.
	 * 
	 * @return
	 */
	public Set<String> getEditablePropertiesFromPreviousRequest() {
		return editablePropertiesFromPreviousRequest;
	}



	/**
	 * Base implementation that returns {@link Collections#emptySet()}.  sub-implementations should not add values to Set instance returned
	 * by this method, and should create its own instance.
	 * 
	 * @see org.kuali.rice.kns.web.struts.pojo.PojoForm#getMethodToCallsToBypassSessionRetrievalForGETRequests()
	 */
	public Set<String> getMethodToCallsToBypassSessionRetrievalForGETRequests() {
		return Collections.emptySet();
	}
	
	
	// end Kuali Foundation modification
}