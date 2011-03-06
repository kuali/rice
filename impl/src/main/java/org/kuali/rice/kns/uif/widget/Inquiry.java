/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.uif.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.BindingInfo;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.UifParameters;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.field.LinkField;
import org.kuali.rice.kns.uif.util.ModelUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * Widget for rendering an Inquiry link on a field's value
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Inquiry extends WidgetBase {
	private static final long serialVersionUID = -2154388007867302901L;

	private String baseInquiryUrl;

	private String objectClassName;
	private String viewId;
	private String viewName;

	private Map<String, String> parameterFieldMapping;

	private LinkField inquiryLinkField;

	public Inquiry() {
		super();
		
		parameterFieldMapping = new HashMap<String, String>();
	}

	/**
	 * @see org.kuali.rice.kns.uif.widget.WidgetBase#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.Component)
	 */
	@Override
	public void performFinalize(View view, Object model, Component parent) {
		super.performFinalize(view, model, parent);

		setRender(false);

		AttributeField field = (AttributeField) parent;
		BindingInfo fieldBindingInfo = (BindingInfo) ObjectUtils.deepCopy(field.getBindingInfo());

		if (StringUtils.isNotBlank(objectClassName)) {
			// build inquiry url
			Properties urlParameters = new Properties();
			
			urlParameters.put(UifParameters.OBJECT_CLASS_NAME, objectClassName);
			urlParameters.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.START);

			// get inquiry parameter values
			for (Entry<String, String> parameterMapping : parameterFieldMapping.entrySet()) {
				fieldBindingInfo.setBindingName(parameterMapping.getValue());
				Object parameterValue = ModelUtils.getPropertyValue(model, fieldBindingInfo.getBindingPath());
				if (parameterValue != null) {
					urlParameters.put(parameterMapping.getKey(), parameterValue);
				}
			}

			String inquiryUrl = UrlFactory.parameterizeUrl(baseInquiryUrl, urlParameters);
			inquiryLinkField.setHrefText(inquiryUrl);

			// get inquiry link text
			Object fieldValue = ModelUtils.getPropertyValue(model, field.getBindingInfo().getBindingPath());
			if (fieldValue != null) {
				inquiryLinkField.setLinkLabel(fieldValue.toString());

				setRender(true);
			}
		}
	}

	public String getBaseInquiryUrl() {
		return this.baseInquiryUrl;
	}

	public void setBaseInquiryUrl(String baseInquiryUrl) {
		this.baseInquiryUrl = baseInquiryUrl;
	}

	public String getObjectClassName() {
		return this.objectClassName;
	}

	public void setObjectClassName(String objectClassName) {
		this.objectClassName = objectClassName;
	}

	public String getViewId() {
		return this.viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getViewName() {
		return this.viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public Map<String, String> getParameterFieldMapping() {
		return this.parameterFieldMapping;
	}

	public void setParameterFieldMapping(Map<String, String> parameterFieldMapping) {
		this.parameterFieldMapping = parameterFieldMapping;
	}
	
	public void setParameterFieldMapping(String parameterFieldMapping) {
		Map<String, String>  parameterMapping = new HashMap<String, String>();
		
		String[] mappings = StringUtils.split(parameterFieldMapping, ",");
		for (int i = 0; i < mappings.length; i++) {
			String[] mapping = StringUtils.split(mappings[i], ":");
			parameterMapping.put(mapping[0], mapping[1]);
		}
		
		this.parameterFieldMapping = parameterMapping;
	}

	public LinkField getInquiryLinkField() {
		return this.inquiryLinkField;
	}

	public void setInquiryLinkField(LinkField inquiryLinkField) {
		this.inquiryLinkField = inquiryLinkField;
	}

}
