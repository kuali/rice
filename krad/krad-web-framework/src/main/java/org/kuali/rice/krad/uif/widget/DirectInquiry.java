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
package org.kuali.rice.krad.uif.widget;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.BindingInfo;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Widget for rendering an Direct Inquiry link icon next to a input field
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DirectInquiry extends Inquiry {
    
    private static final long serialVersionUID = -2490979579285984314L;
    
    private ActionField directInquiryActionField;
    
    // Binding Info fields used by direct inquiry to access html fields
    private String bindingPrefix;
    
    private boolean bindToMap;

    public DirectInquiry() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.WidgetBase#performFinalize(org.kuali.rice.krad.uif.container.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.core.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // only set inquiry if enabled
        if (!isRender() || isReadOnly()) {
            return;
        }

        // set render to false until we find an inquiry class
        setRender(false);

        AttributeField field = (AttributeField) parent;
        
        // If this is a direct inquiry (not read only) then set the binding prefix
        // for mapping to the field names used in the script to get values from the form
    	BindingInfo bindingInfo = field.getBindingInfo();
    	if (bindingInfo.isBindToForm()) {
    		bindingPrefix = bindingInfo.getBindByNamePrefix();
    	}else if (bindingInfo.isBindToMap()){
    		bindToMap = true;
    		bindingPrefix = bindingInfo.getBindingObjectPath();
    	}else{
    		bindingPrefix = bindingInfo.getBindingObjectPath() + (bindingInfo.getBindByNamePrefix()==null?"":("." + bindingInfo.getBindByNamePrefix()));
    	}

    	setupLink(view, model, field);
    }

    /**
     * Builds the inquiry link and onclick script based on the given inquiry class and parameters
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
	public void buildInquiryLink(Object dataObject, String propertyName,
			Class<?> inquiryObjectClass, Map<String, String> inquiryParms) {
		Properties urlParameters = new Properties();

		urlParameters.put(UifParameters.DATA_OBJECT_CLASS_NAME,
				inquiryObjectClass.getName());
		urlParameters.put(UifParameters.METHOD_TO_CALL,
				UifConstants.MethodToCallNames.START);

		// Direct inquiry
		String inquiryUrl = UrlFactory.parameterizeUrl(getBaseInquiryUrl(),
				urlParameters);
		StringBuilder paramMapString = new StringBuilder();
		// Check if lightbox is set. Get lightbox options.
		String lightBoxOptions = "";
		boolean lightBoxShow = directInquiryActionField.getLightBox() != null;
		if (lightBoxShow) {
			lightBoxOptions = directInquiryActionField.getLightBox()
					.getComponentOptionsJSString();
		}

		// Build parameter string using the actual names of the fields as on the
		// html page
		for (Entry<String, String> inquiryParameter : inquiryParms.entrySet()) {
			if (bindToMap) {
				paramMapString.append(bindingPrefix);
				paramMapString.append("['");
				paramMapString.append(inquiryParameter.getKey());
				paramMapString.append("']");
			} else {
				paramMapString.append(bindingPrefix);
				paramMapString.append(".");
				paramMapString.append(inquiryParameter.getKey());
			}
			paramMapString.append(":");
			paramMapString.append(inquiryParameter.getValue());
			paramMapString.append(",");
		}
		paramMapString.deleteCharAt(paramMapString.length() - 1);

		// Create onlick script to open the inquiry window on the click event
		// of the direct inquiry
		StringBuilder onClickScript = new StringBuilder("showDirectInquiry(\"");
		onClickScript.append(inquiryUrl);
		onClickScript.append("\", \"");
		onClickScript.append(paramMapString);
		onClickScript.append("\", ");
		onClickScript.append(lightBoxShow);
		onClickScript.append(", ");
		onClickScript.append(lightBoxOptions);
		onClickScript.append(");");
		directInquiryActionField.setBlockValidateDirty(true);
		directInquiryActionField.setClientSideJs(onClickScript.toString());

		setRender(true);
	}

    /**
     * @see org.kuali.rice.krad.uif.core.ComponentBase#getNestedComponents()
     */
    @Override
    public List<Component> getNestedComponents() {
        List<Component> components = super.getNestedComponents();

        components.add(directInquiryActionField);

        return components;
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
