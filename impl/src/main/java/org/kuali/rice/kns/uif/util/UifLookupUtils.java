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
package org.kuali.rice.kns.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.uif.control.CheckboxControl;
import org.kuali.rice.kns.uif.control.Control;
import org.kuali.rice.kns.uif.control.RadioGroupControl;
import org.kuali.rice.kns.uif.control.TextAreaControl;
import org.kuali.rice.kns.uif.control.TextControl;
import org.kuali.rice.kns.uif.field.AttributeField;

/**
 * Class for utility methods that pertain to UIF Lookup processing
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifLookupUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifLookupUtils.class);

	public static void initializeAttributeFieldFromAttributeDefinition(AttributeField attributeField, AttributeDefinition attributeDefinition) {
		// label
		if (StringUtils.isEmpty(attributeField.getLabel())) {
			attributeField.setLabel(attributeDefinition.getLabel());
		}

		// short label
		if (StringUtils.isEmpty(attributeField.getShortLabel())) {
			attributeField.setShortLabel(attributeDefinition.getShortLabel());
		}

		// security
		if (attributeField.getAttributeSecurity() == null) {
			attributeField.setAttributeSecurity(attributeDefinition.getAttributeSecurity());
		}

		// control
		setupControlFromAttributeDefinition(attributeField, attributeDefinition);

		// do not inherit requiredness
//		if (attributeField.getRequired() == null) {
//			attributeField.setRequired(attributeDefinition.isRequired());
//		}

		// do not inherit summary
//		if (StringUtils.isEmpty(attributeField.getSummary())) {
//			attributeField.setSummary(attributeDefinition.getSummary());
//		}

		// do not inherit description
//		if (StringUtils.isEmpty(attributeField.getDescription())) {
//			attributeField.setDescription(attributeDefinition.getDescription());
//		}

		// do not inherit constraint
//		if (StringUtils.isEmpty(attributeField.getConstraint())) {
//			attributeField.setConstraint(attributeDefinition.getConstraint());
//		}

		// max length
//		if (attributeField.getMaxLength() == null) {
//			attributeField.setMaxLength(attributeDefinition.getMaxLength());
//		}

		// min length
//		if (attributeField.getMinLength() == null) {
//			attributeField.setMinLength(attributeDefinition.getMinLength());
//		}

	}

	protected static void setupControlFromAttributeDefinition(AttributeField attributeField, AttributeDefinition attributeDefinition) {
		if (attributeField.getControl() == null) {
			attributeField.setControl(convertControlToLookupControl(attributeDefinition));
		}
	}

	protected static Control convertControlToLookupControl(AttributeDefinition attributeDefinition) {
		Control newControl = null;
		if (CheckboxControl.class.isAssignableFrom(attributeDefinition.getControlField().getClass())) {
			newControl = generateCustomLookupControlFromExisting(RadioGroupControl.class, attributeDefinition.getControlField());
		} else if (TextAreaControl.class.isAssignableFrom(attributeDefinition.getControlField().getClass())) {
			newControl = generateCustomLookupControlFromExisting(TextControl.class, attributeDefinition.getControlField());
		} else {
			newControl = ComponentUtils.copy(attributeDefinition.getControlField());
		}
		return newControl;
	}

	protected static Control generateCustomLookupControlFromExisting(Class<? extends Control> controlClass, Control existingControl) {
		try {
	        Control newControl = controlClass.newInstance();
	        for (String propertyPath : ComponentUtils.getComponentPropertyNames(existingControl.getClass())) {
				Object propValue = ObjectPropertyUtils.getPropertyValue(existingControl, propertyPath);
				if (propValue != null) {
					ObjectPropertyUtils.setPropertyValue(newControl, propertyPath, propValue, true);
				}
            }
	        return newControl;
        } catch (InstantiationException e) {
        	LOG.error("Exception instantiating new instance of class '" + controlClass.getClass() + "'", e);
	        throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
        	LOG.error("Exception instantiating new instance of class '" + controlClass.getClass() + "'", e);
	        throw new RuntimeException(e);
        }
	}

}
