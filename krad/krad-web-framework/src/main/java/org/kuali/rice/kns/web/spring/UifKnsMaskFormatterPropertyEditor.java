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
package org.kuali.rice.kns.web.spring;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.core.web.format.Formatter;


/**
 * Used to convert KNS style Formatter classes to Property Editors that can be used by Spring 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifKnsMaskFormatterPropertyEditor extends PropertyEditorSupport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifKnsMaskFormatterPropertyEditor.class);

	Formatter maskFormatter;
	Formatter propertyFormatter;

	public UifKnsMaskFormatterPropertyEditor(Formatter maskFormatter) {
		this(maskFormatter,null);
	}
	
    public UifKnsMaskFormatterPropertyEditor(Formatter maskFormatter,Class<? extends Formatter> formatterClass) {
    	super();
	    
        try {
        	this.maskFormatter = maskFormatter;
        	if (formatterClass != null){
        		propertyFormatter = formatterClass.newInstance();
        		propertyFormatter.setPropertyType(formatterClass);
        	}
        }
        catch (InstantiationException e) {
            throw new FormatException("Couldn't create an instance of class " + formatterClass, e);
        }
        catch (IllegalAccessException e) {
            throw new FormatException("Couldn't create an instance of class " + formatterClass, e);
        }
        
    }
    
	@Override
	public String getAsText() {
		try {
			Object value = getValue();
			if (propertyFormatter != null){
				value = (String) propertyFormatter.formatForPresentation(getValue());
			}
			return (String)maskFormatter.formatForPresentation(value);
		} catch (FormatException e) {
			LOG.error("FormatException: " + e.getLocalizedMessage(), e);
			throw e;
		}
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		try {
			if (propertyFormatter != null){
				this.setValue(propertyFormatter.convertFromPresentationFormat(text));
			}else{
				this.setValue(text);
			}
		} catch (FormatException e) {
			LOG.error("FormatException: " + e.getLocalizedMessage(), e);
			throw e;
		}
	}

}
