/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.web;

import org.apache.commons.beanutils.Converter;

/**
 * A BeanUtils Converter which delegates to an original convertor or a KEW converter depending on
 * what the current {@link ModuleContext} is.  This allows for the KEW struts module to run inside
 * of an ActionServlet that has the "convertNull" init-param set to null.  The KEW module
 * depends on this being set to true in order for the form processing to work properly.
 * When KEW is embedded in a web application where this is not the case, we need to work
 * around the issue by registering custom converters to convert the values properly when inside
 * the context of the KEW struts module but preserve the conversion behavior of the parent
 * struts module.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ContextSensitiveConverter implements Converter {

	private Converter originalConverter;
	private Converter kewConverter;

	public ContextSensitiveConverter(Converter originalConverter, Converter kewConverter) {
		this.originalConverter = originalConverter;
		this.kewConverter = kewConverter;
	}

	public Object convert(Class type, Object object) {
		if (ModuleContext.isKew()) {
			return kewConverter.convert(type, object);
		} else {
			return originalConverter.convert(type, object);
		}
	}

}
