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

import java.beans.PropertyEditorSupport;

/**
 * This PropertyEditor for booleans supports y/n which the spring version does not. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class UifBooleanEditor extends PropertyEditorSupport {
	
	private static final String TRUE_VALUES = "/true/yes/y/on/1/";
	private static final String FALSE_VALUES = "/false/no/n/off/0/";
	
	private static final String TRUE_VALUE = "true";
	private static final String FALSE_VALUE = "false";

	@Override
	public String getAsText() {
		if(this.getValue() == null) {
			return "";
		}
		else if(((Boolean)this.getValue()).booleanValue()) {
			return TRUE_VALUE;
		}
		else {
			return FALSE_VALUE;
		}
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		String input = null;
		
		if(text != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("/").append(text.toLowerCase()).append("/");
			input = builder.toString();
			
			if(TRUE_VALUES.contains(input)) {
				this.setValue(Boolean.TRUE);
			}
			else if(FALSE_VALUES.contains(input)) {
				this.setValue(Boolean.FALSE);
			}
			else {
				input = null;
			}
		}

		if(input == null) {
			throw new IllegalArgumentException("Invalid boolean input: " + text);
		}
	}

}
