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
package org.kuali.rice.krad.uif.util;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.control.CheckboxControl;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.RadioGroupControl;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.spring.form.UifFormBase;

/**
 * Class for utility methods that pertain to UIF Lookup processing
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupInquiryUtils {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupInquiryUtils.class);

	public static void initializeAttributeFieldFromAttributeDefinition(AttributeField attributeField,
			AttributeDefinition attributeDefinition) {
		// label
		if (StringUtils.isEmpty(attributeField.getLabel())) {
			attributeField.setLabel(attributeDefinition.getLabel());
		}

		// short label
		if (StringUtils.isEmpty(attributeField.getShortLabel())) {
			attributeField.setShortLabel(attributeDefinition.getShortLabel());
		}

		// control
		setupControlFromAttributeDefinition(attributeField, attributeDefinition);

		// do not inherit requiredness
		// if (attributeField.getRequired() == null) {
		// attributeField.setRequired(attributeDefinition.isRequired());
		// }

		// do not inherit summary
		// if (StringUtils.isEmpty(attributeField.getSummary())) {
		// attributeField.setSummary(attributeDefinition.getSummary());
		// }

		// do not inherit description
		// if (StringUtils.isEmpty(attributeField.getDescription())) {
		// attributeField.setDescription(attributeDefinition.getDescription());
		// }

		// do not inherit constraint
		// if (StringUtils.isEmpty(attributeField.getConstraint())) {
		// attributeField.setConstraint(attributeDefinition.getConstraint());
		// }

		// max length
		// if (attributeField.getMaxLength() == null) {
		// attributeField.setMaxLength(attributeDefinition.getMaxLength());
		// }

		// min length
		// if (attributeField.getMinLength() == null) {
		// attributeField.setMinLength(attributeDefinition.getMinLength());
		// }

	}

	protected static void setupControlFromAttributeDefinition(AttributeField attributeField,
			AttributeDefinition attributeDefinition) {
		if (attributeField.getControl() == null) {
			attributeField.setControl(convertControlToLookupControl(attributeDefinition));
		}
	}

	protected static Control convertControlToLookupControl(AttributeDefinition attributeDefinition) {
		Control newControl = null;
		if (attributeDefinition.getControlField() == null) {
		    return null;
		}
		
		if (CheckboxControl.class.isAssignableFrom(attributeDefinition.getControlField().getClass())) {
			newControl = ComponentFactory.getRadioGroupControl();
            List<KeyValue> options = new ArrayList<KeyValue>();
            options.add(new ConcreteKeyValue("Y", "Yes"));
            options.add(new ConcreteKeyValue("N", "No"));
            options.add(new ConcreteKeyValue("", "Both"));

            ((RadioGroupControl) newControl).setOptions(options);
		}
		else if (TextAreaControl.class.isAssignableFrom(attributeDefinition.getControlField().getClass())) {
			newControl = ComponentFactory.getTextControl();
		}
		else {
			newControl = ComponentUtils.copy(attributeDefinition.getControlField(), "");
		}

		return newControl;
	}

	public static Control generateCustomLookupControlFromExisting(Class<? extends Control> controlClass,
			Control existingControl) {
		try {
			Control newControl = controlClass.newInstance();
			for (String propertyPath : ComponentUtils.getComponentPropertyNames(existingControl.getClass())) {
				Object propValue = ObjectPropertyUtils.getPropertyValue(existingControl, propertyPath);
				if (propValue != null) {
					ObjectPropertyUtils.setPropertyValue(newControl, propertyPath, propValue, true);
				}
			}
			return newControl;
		}
		catch (InstantiationException e) {
			LOG.error("Exception instantiating new instance of class '" + controlClass.getClass() + "'", e);
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			LOG.error("Exception instantiating new instance of class '" + controlClass.getClass() + "'", e);
			throw new RuntimeException(e);
		}
	}

	public static String retrieveLookupParameterValue(UifFormBase form, HttpServletRequest request,
			Class<?> lookupObjectClass, String propertyName, String propertyValueName) {
		String parameterValue = "";

		// get literal parameter values first
		if (StringUtils.contains(propertyValueName, "'")) {
			parameterValue = StringUtils.replace(propertyValueName, "'", "");
		}
		else if (parameterValue.startsWith(KRADConstants.LOOKUP_PARAMETER_LITERAL_PREFIX
				+ KRADConstants.LOOKUP_PARAMETER_LITERAL_DELIMITER)) {
			parameterValue = StringUtils.removeStart(parameterValue, KRADConstants.LOOKUP_PARAMETER_LITERAL_PREFIX
					+ KRADConstants.LOOKUP_PARAMETER_LITERAL_DELIMITER);
		}
		// check if parameter is in request
		else if (request.getParameterMap().containsKey(propertyValueName)) {
			parameterValue = request.getParameter(propertyValueName);
		}
		// get parameter value from form object
		else {
			Object value = ObjectPropertyUtils.getPropertyValue(form, propertyValueName);
			if (value != null) {
				if (value instanceof String) {
					parameterValue = (String) value;
				}

				Formatter formatter = Formatter.getFormatter(value.getClass());
				parameterValue = (String) formatter.format(value);
			}
		}

		if (parameterValue != null
				&& lookupObjectClass != null
				&& KRADServiceLocatorWeb.getBusinessObjectAuthorizationService()
						.attributeValueNeedsToBeEncryptedOnFormsAndLinks(lookupObjectClass, propertyName)) {
			try {
				parameterValue = CoreApiServiceLocator.getEncryptionService().encrypt(parameterValue)
						+ EncryptionService.ENCRYPTION_POST_PREFIX;
			}
			catch (GeneralSecurityException e) {
				LOG.error("Unable to encrypt value for property name: " + propertyName);
				throw new RuntimeException(e);
			}
		}

		return parameterValue;
	}
	
	/**
	 * Helper method for building the title text for an element and a map of
	 * key/value pairs
	 * 
	 * @param prependText
	 *            - text to prepend to the title
	 * @param element
	 *            - element class the title is being generated for, used to as
	 *            the parent for getting the key labels
	 * @param keyValueMap
	 *            - map of key value pairs to add to the title text
	 * @return String title text
	 */
	public static String getTitleText(String prependText, Class<?> element, Map<String, String> keyValueMap) {
		StringBuffer titleText = new StringBuffer(prependText);
		for (String key : keyValueMap.keySet()) {
			String fieldVal = keyValueMap.get(key).toString();

			titleText.append(KRADServiceLocatorWeb.getDataDictionaryService().getAttributeLabel(element, key) + "="
					+ fieldVal.toString() + " ");
		}

		return titleText.toString();
	}

}
