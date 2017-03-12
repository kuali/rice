/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kew.api.util;

import org.apache.commons.lang.StringUtils;
import org.apache.xml.utils.XMLChar;

public class UserTextFilterForXml {
    /**
     * Removes invalid XML 1.0 Unicode characters
     * @param text The String to clean
     * @return The resulting String
     */
    public static String cleanInvalidXmlChars(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        } else {
            StringBuilder sb = new StringBuilder();
            boolean strippedCharacter = false;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (XMLChar.isValid(c)) {
                    sb.append(c);
                } else {
                    strippedCharacter = true;
                }
            }

            if (strippedCharacter) {
                return sb.toString();
            } else {
                return text;
            }
        }
    }
}
