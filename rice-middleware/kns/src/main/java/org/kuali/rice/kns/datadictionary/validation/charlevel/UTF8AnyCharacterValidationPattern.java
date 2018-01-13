/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.validation.charlevel;

import org.kuali.rice.krad.datadictionary.exporter.ExportMap;
import org.kuali.rice.krad.datadictionary.validation.CharacterLevelValidationPattern;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Pattern for matching any UTF-8 character with whitespace option
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated Use {@link org.kuali.rice.krad.datadictionary.validation.constraint.UTF8AnyCharacterPatternConstraint}.
 */
@Deprecated
public class UTF8AnyCharacterValidationPattern extends CharacterLevelValidationPattern{
	
    protected boolean allowWhitespace = false;


    /**
     * @return allowWhitespace
     */
    public boolean getAllowWhitespace() {
        return allowWhitespace;
    }

    /**
     * @param allowWhitespace
     */
    public void setAllowWhitespace(boolean allowWhitespace) {
        this.allowWhitespace = allowWhitespace;
    }

	/**
	 * This overridden method ...
	 * 
     * @see org.kuali.rice.krad.datadictionary.validation.CharacterLevelValidationPattern#extendExportMap(org.kuali.rice.krad.datadictionary.exporter.ExportMap)
	 */
	@Override
	protected String getRegexString() {
		StringBuffer regexString = new StringBuffer("[");
		
		if(!allowWhitespace) {
        regexString.append("[\\u0000-\\uFFFF&&[^\\p{Space}]]");
        } else {
            regexString.append("\\u0000-\\uFFFF");
		}
		
		regexString.append("]");
		return regexString.toString();
	}
	
	/**
	 * This overridden method ...
	 * 
     * @see org.kuali.rice.krad.datadictionary.validation.ValidationPattern#getRegexString()
	 */
	@Override
	public void extendExportMap(ExportMap exportMap) {
        exportMap.set("type", "broaderAnyCharacter");

        if (allowWhitespace) {
            exportMap.set("allowWhitespace", "true");
        }
	}


	
	@Override
	protected String getValidationErrorMessageKeyOptions() {
		if (getAllowWhitespace()) {
			return ".allowWhitespace";
		}
		return KRADConstants.EMPTY_STRING;
	}

}
