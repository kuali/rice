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
package org.kuali.rice.krad.web.jsf;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.Formatter;

/**
 * This is a description of what this class does - jkneal don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class JSFTypeConverter implements Converter {

	/**
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
	 *      javax.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		Formatter formatter = getFormatterForComponent(context, component);
		if (formatter == null) {
			throw new ConverterException("unable to find formatter");
		}

		return formatter.convertFromPresentationFormat(value);
	}

	/**
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
	 *      javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		Formatter formatter = getFormatterForComponent(context, component);

		if (formatter == null) {
			return ObjectUtils.formatPropertyValue(value);
		}

		return (String) formatter.format(value);
	}

	protected Formatter getFormatterForComponent(FacesContext context, UIComponent component) throws ConverterException {
		Formatter formatter = null;

		ValueExpression valueExpression = component.getValueExpression("value");
		String expression = valueExpression.getExpressionString();
		expression = StringUtils.substringBetween(expression, "${", "}");

		BusinessObject businessObject = null;
		String propertyName = "";

		Object currentObject = null;
		String[] parts = StringUtils.split(expression, ".");
		for (int i = 0; i < parts.length; i++) {
			String beanName = parts[i];

			if (i == 0) {
				Application app = context.getApplication();
				currentObject = app.evaluateExpressionGet(context, "${" + beanName + "}", Object.class);
			} else {
				Object propertyValue = ObjectUtils.getPropertyValue(currentObject, beanName);
				// check type if null
				if (propertyValue == null) {
					Class beanType = ObjectUtils.getPropertyType(currentObject, beanName,
							KNSServiceLocator.getPersistenceStructureService());
					if (beanType != null && BusinessObject.class.isAssignableFrom(beanType)) {
						try {
							currentObject = beanType.newInstance();
						} catch (Exception e) {
							throw new ConverterException("unable to create new instance for property type "
									+ beanType.getName(), e);
						}
					}
					else {
						currentObject = null;
					}
				}
				else {
					currentObject = propertyValue;
				}
			}

			if (currentObject == null) {
				return null;
			}

			if (currentObject instanceof BusinessObject) {
				businessObject = (BusinessObject) currentObject;
				propertyName = StringUtils.substringAfter(expression, beanName + ".");
				break;
			}
		}

		if (businessObject != null && StringUtils.isNotEmpty(propertyName)) {
			formatter = ObjectUtils.getFormatterWithDataDictionary(businessObject, propertyName);
		}

		return formatter;
	}

}
