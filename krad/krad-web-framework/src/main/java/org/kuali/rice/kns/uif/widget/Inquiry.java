/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.uif.widget;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.services.CoreApiServiceLocator;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.UifParameters;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.core.BindingInfo;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.field.ActionField;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.field.LinkField;
import org.kuali.rice.kns.uif.util.LookupInquiryUtils;
import org.kuali.rice.kns.uif.util.ObjectPropertyUtils;
import org.kuali.rice.kns.uif.util.ViewModelUtils;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * Widget for rendering an Inquiry link on a field's value
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Inquiry extends WidgetBase {
    private static final long serialVersionUID = -2154388007867302901L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Inquiry.class);

    public static final String INQUIRY_TITLE_PREFIX = "title.inquiry.url.value.prependtext";

    private String baseInquiryUrl;

    private String dataObjectClassName;
    private String viewName;

    private Map<String, String> inquiryParameters;

    private boolean forceInquiry;

    private LinkField inquiryLinkField;
    
    private ActionField directInquiryActionField;
    
    private String conditionalReadOnly;
    
    private boolean readOnly;
    
    // Binding Info fields used by direct inquiry to access html fields
    private String bindingPrefix;
    
    private boolean bindToMap;

    public Inquiry() {
        super();

        forceInquiry = false;
        inquiryParameters = new HashMap<String, String>();
    }

    /**
     * @see org.kuali.rice.kns.uif.widget.WidgetBase#performFinalize(org.kuali.rice.kns.uif.container.View,
     *      java.lang.Object, org.kuali.rice.kns.uif.core.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // only set inquiry if enabled
        if (!isRender()) {
            return;
        }

        // set render to false until we find an inquiry class
        setRender(false);

        AttributeField field = (AttributeField) parent;
        
        // If this is a direct inquiry (not read only) then set the binding prefix
        // for mapping to the field names used in the script to get values from the form
        if (!isReadOnly()) {
        	BindingInfo bindingInfo = field.getBindingInfo();
        	if (bindingInfo.isBindToForm()) {
        		bindingPrefix = bindingInfo.getBindByNamePrefix();
        	}else if (bindingInfo.isBindToMap()){
        		bindToMap = true;
        		bindingPrefix = bindingInfo.getBindingObjectPath();
        	}else{
        		bindingPrefix = bindingInfo.getBindingObjectPath() + (bindingInfo.getBindByNamePrefix()==null?"":("." + bindingInfo.getBindByNamePrefix()));
        	}
        }

        // check if field value is null, if so no inquiry
        Object propertyValue = ObjectPropertyUtils.getPropertyValue(model, field.getBindingInfo().getBindingPath());
        if (((propertyValue == null) || StringUtils.isBlank(propertyValue.toString())) && isReadOnly()) {
            return;
        }

        // get parent object for inquiry
        Object parentObject = ViewModelUtils.getParentObjectForMetadata(view, model, field);
        String propertyName = field.getBindingInfo().getBindingName();

        // if class and parameters configured, build link from those
        if (StringUtils.isNotBlank(dataObjectClassName) && (inquiryParameters != null) && !inquiryParameters.isEmpty()) {
            Class<?> inquiryObjectClass = null;
            try {
                inquiryObjectClass = Class.forName(dataObjectClassName);
            }
            catch (ClassNotFoundException e) {
                LOG.error("Unable to get class for: " + dataObjectClassName);
                throw new RuntimeException(e);
            }

            buildInquiryLink(parentObject, propertyName, inquiryObjectClass, inquiryParameters);
        }
        // get inquiry class and parameters from view helper
        else {
            view.getViewHelperService().buildInquiryLink(parentObject, propertyName, this);
        }
    }

    /**
     * Builds the inquiry link based on the given inquiry class and parameters
     * 
     * @param dataObject
     *            - parent object that contains the data (used to pull inquiry
     *            parameters)
     * @param propertyName
     *            - name of the property the inquiry is set on
     * @param inquiryObjectClass
     *            - class of the object the inquiry should point to
     * @param inquiryParms
     *            - map of key field mappings for the inquiry
     */
    public void buildInquiryLink(Object dataObject, String propertyName, Class<?> inquiryObjectClass,
            Map<String, String> inquiryParms) {
        Properties urlParameters = new Properties();

        urlParameters.put(UifParameters.DATA_OBJECT_CLASS_NAME, inquiryObjectClass.getName());
        urlParameters.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.START);

        // Do normal inquiry if read only, otherwise do direct inquiry
        if (isReadOnly()) { 
	        for (Entry<String, String> inquiryParameter : inquiryParms.entrySet()) {
	            String parameterName = inquiryParameter.getKey();
	
	            Object parameterValue = ObjectPropertyUtils.getPropertyValue(dataObject, parameterName);
	
	            // TODO: need general format util that uses spring
	            if (parameterValue == null) {
	                parameterValue = "";
	            }
	            else if (parameterValue instanceof java.sql.Date) {
	                if (Formatter.findFormatter(parameterValue.getClass()) != null) {
	                    Formatter formatter = Formatter.getFormatter(parameterValue.getClass());
	                    parameterValue = formatter.format(parameterValue);
	                }
	            }
	            else {
	                parameterValue = parameterValue.toString();
	            }
	
	            // Encrypt value if it is a field that has restriction that prevents
	            // a value from being shown to user, because we don't want the
	            // browser history to store the restricted
	            // attribute's value in the URL
	            if (KNSServiceLocatorWeb.getBusinessObjectAuthorizationService()
	                    .attributeValueNeedsToBeEncryptedOnFormsAndLinks(inquiryObjectClass, inquiryParameter.getValue())) {
	                try {
	                    parameterValue = CoreApiServiceLocator.getEncryptionService().encrypt(parameterValue);
	                }
	                catch (GeneralSecurityException e) {
	                    LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
	                    throw new RuntimeException(e);
	                }
	            }
	
	            // add inquiry parameter to URL
	            urlParameters.put(inquiryParameter.getValue(), parameterValue);
	        }
	        
	        String inquiryUrl = UrlFactory.parameterizeUrl(baseInquiryUrl, urlParameters);
	        inquiryLinkField.setHrefText(inquiryUrl);
	        	        
	        // get inquiry link text
	        // TODO: should we really put the link label here or just wrap the
	        // written value?
	        Object fieldValue = ObjectPropertyUtils.getPropertyValue(dataObject, propertyName);
	        if (fieldValue != null) {
	            inquiryLinkField.setLinkLabel(fieldValue.toString());
	        }

	        // set inquiry title
	        String linkTitle = createTitleText(inquiryObjectClass);
	        linkTitle = LookupInquiryUtils.getTitleText(linkTitle, inquiryObjectClass, inquiryParameters);
	        inquiryLinkField.setTitle(linkTitle);
		} else {
			// Direct inquiry
			String inquiryUrl = UrlFactory.parameterizeUrl(baseInquiryUrl,
					urlParameters);
			StringBuilder paramMapString = new StringBuilder();
			// Check if lightbox is in dd. Get lightbox options. 
			String lightBoxOptions = "";
			boolean lightBoxShow = directInquiryActionField.getLightBox()!= null;
			if (lightBoxShow) {
				lightBoxOptions = directInquiryActionField.getLightBox().getComponentOptionsJSString();
			}
			// Build parameter string using the actual names of the fields as on the html page
			for (Entry<String, String> inquiryParameter : inquiryParms.entrySet()) {
				if (bindToMap) {
					paramMapString.append(bindingPrefix);
					paramMapString.append("['");
					paramMapString.append(inquiryParameter.getKey());
					paramMapString.append("']");
				}else{
					paramMapString.append(bindingPrefix);
					paramMapString.append(".");
					paramMapString.append(inquiryParameter.getKey());
				}
				paramMapString.append(":");
				paramMapString.append(inquiryParameter.getValue());
				paramMapString.append(",");
			}
			paramMapString.deleteCharAt(paramMapString.length()-1);
			// Create onlick script to open the inquiry window on the click event
			// of the direct inquiry
			StringBuilder onClickScript = new StringBuilder(
					"directInquiry(\"");
			onClickScript.append(inquiryUrl);
			onClickScript.append("\", \"");			
			onClickScript.append(paramMapString);
			onClickScript.append("\", ");			
			onClickScript.append(lightBoxShow);
			onClickScript.append(", ");			
			onClickScript.append(lightBoxOptions);
			onClickScript.append(");");
			directInquiryActionField.setOnClickScript(onClickScript.toString());
		}

        setRender(true);
    }

    /**
     * Gets text to prepend to the inquiry link title
     * 
     * @param dataObjectClass
     *            - data object class being inquired into
     * @return String title prepend text
     */
    public String createTitleText(Class<?> dataObjectClass) {
        String titleText = "";

        String titlePrefixProp = KNSServiceLocator.getKualiConfigurationService().getPropertyString(
                INQUIRY_TITLE_PREFIX);
        if (StringUtils.isNotBlank(titlePrefixProp)) {
            titleText += titlePrefixProp + " ";
        }

        String objectLabel = KNSServiceLocatorWeb.getDataDictionaryService().getDataDictionary()
                .getDataObjectEntry(dataObjectClass.getName()).getObjectLabel();
        if (StringUtils.isNotBlank(objectLabel)) {
            titleText += objectLabel + " ";
        }

        return titleText;
    }
    
    /**
     * @see org.kuali.rice.kns.uif.core.ComponentBase#getNestedComponents()
     */
    @Override
    public List<Component> getNestedComponents() {
        List<Component> components = super.getNestedComponents();

        components.add(inquiryLinkField);
        components.add(directInquiryActionField);

        return components;
    }

    public String getBaseInquiryUrl() {
        return this.baseInquiryUrl;
    }

    public void setBaseInquiryUrl(String baseInquiryUrl) {
        this.baseInquiryUrl = baseInquiryUrl;
    }

    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    public String getViewName() {
        return this.viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public boolean isForceInquiry() {
        return this.forceInquiry;
    }

    public void setForceInquiry(boolean forceInquiry) {
        this.forceInquiry = forceInquiry;
    }

    public Map<String, String> getInquiryParameters() {
        return this.inquiryParameters;
    }

    public void setInquiryParameters(Map<String, String> inquiryParameters) {
        this.inquiryParameters = inquiryParameters;
    }

    public void setInquiryParameters(String inquiryParameterString) {
        Map<String, String> inquiryParms = new HashMap<String, String>();

        String[] mappings = StringUtils.split(inquiryParameterString, ",");
        for (int i = 0; i < mappings.length; i++) {
            String[] mapping = StringUtils.split(mappings[i], ":");
            inquiryParms.put(mapping[0], mapping[1]);
        }

        this.inquiryParameters = inquiryParms;
    }

    public LinkField getInquiryLinkField() {
        return this.inquiryLinkField;
    }

    public void setInquiryLinkField(LinkField inquiryLinkField) {
        this.inquiryLinkField = inquiryLinkField;
    }

	/**
	 * @return the directInquiryActionField
	 */
	public ActionField getDirectInquiryActionField() {
		return this.directInquiryActionField;
	}

	/**
	 * @param directInquiryActionField the directInquiryActionField to set
	 */
	public void setDirectInquiryActionField(ActionField directInquiryActionField) {
		this.directInquiryActionField = directInquiryActionField;
	}

	/**
	 * @return the conditionalReadOnly
	 */
	public String getConditionalReadOnly() {
		return this.conditionalReadOnly;
	}

	/**
	 * @param conditionalReadOnly the conditionalReadOnly to set
	 */
	public void setConditionalReadOnly(String conditionalReadOnly) {
		this.conditionalReadOnly = conditionalReadOnly;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return the bindingPrefix
	 */
	public String getBindingPrefix() {
		return this.bindingPrefix;
	}

	/**
	 * @param bindingPrefix the bindingPrefix to set
	 */
	public void setBindingPrefix(String bindingPrefix) {
		this.bindingPrefix = bindingPrefix;
	}

	/**
	 * @param bindToMap the bindToMap to set
	 */
	public void setBindToMap(boolean bindToMap) {
		this.bindToMap = bindToMap;
	}

	/**
	 * @return the bindToMap
	 */
	public boolean isBindToMap() {
		return bindToMap;
	}

}
