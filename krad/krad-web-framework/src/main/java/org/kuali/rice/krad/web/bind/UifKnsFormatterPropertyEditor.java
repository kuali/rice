/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.web.bind;

import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.core.web.format.Formatter;

import java.beans.PropertyEditorSupport;


/**
 * Used to convert KNS style Formatter classes to Property Editors that can be used by Spring 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifKnsFormatterPropertyEditor extends PropertyEditorSupport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifKnsFormatterPropertyEditor.class);

	Formatter formatter;

	/**
     * This constructs a ...
     * 
     * @param formatter
     */
    public UifKnsFormatterPropertyEditor(Class<? extends Formatter> formatterClass) {
	    super();
        try {
            formatter = formatterClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new FormatException("Couldn't create an instance of class " + formatterClass, e);
        }
        catch (IllegalAccessException e) {
            throw new FormatException("Couldn't create an instance of class " + formatterClass, e);
        }

//        if (settings != null)
//            formatter.setSettings(Collections.unmodifiableMap(settings));
        formatter.setPropertyType(formatterClass);
    }

	@Override
	public String getAsText() {
		try {
			return (String) formatter.formatForPresentation(getValue());
		} catch (FormatException e) {
			LOG.error("FormatException: " + e.getLocalizedMessage(), e);
			throw e;
		}
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		try {
			this.setValue(formatter.convertFromPresentationFormat(text));
		} catch (FormatException e) {
			LOG.error("FormatException: " + e.getLocalizedMessage(), e);
			throw e;
		}
//		String input = null;
//		
//		if(text != null) {
//			StringBuilder builder = new StringBuilder();
//			builder.append("/").append(text.toLowerCase()).append("/");
//			input = builder.toString();
//			
//			if(TRUE_VALUES.contains(input)) {
//				this.setValue(Boolean.TRUE);
//			}
//			else if(FALSE_VALUES.contains(input)) {
//				this.setValue(Boolean.FALSE);
//			}
//			else {
//				input = null;
//			}
//		}
//
//		if(input == null) {
//			throw new IllegalArgumentException("Invalid boolean input: " + text);
//		}
	}


}
