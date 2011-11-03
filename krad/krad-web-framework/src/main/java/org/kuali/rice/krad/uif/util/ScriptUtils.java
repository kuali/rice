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
package org.kuali.rice.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.TypeUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for generating JavaScript
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ScriptUtils {

    /**
     * Translates an Object to a String for representing the given Object as
     * a JavaScript value
     *
     * <p>
     * Handles null, List, Map, and Set collections, along with non quoting for numeric and
     * boolean types. Complex types are treated as a String value using toString
     * </p>
     *
     * @param value - Object instance to translate
     * @return String JS value
     */
    public static String translateValue(Object value) {
        String jsValue = "";

        if (value == null) {
            jsValue = "null";
            return jsValue;
        }

        if (value instanceof List) {
            jsValue = "[";

            List<Object> list = (List<Object>) value;
            for (Object listItem : list) {
                jsValue += translateValue(listItem);
                jsValue += ",";
            }
            jsValue = StringUtils.removeEnd(jsValue, ",");

            jsValue += "]";
        } else if (value instanceof Set) {
            jsValue = "[";

            Set<Object> set = (Set<Object>) value;
            for (Object setItem : set) {
                jsValue += translateValue(setItem);
                jsValue += ",";
            }
            jsValue = StringUtils.removeEnd(jsValue, ",");

            jsValue += "]";
        } else if (value instanceof Map) {
            jsValue = "{";

            Map<Object, Object> map = (Map<Object, Object>) value;
            for (Map.Entry<Object, Object> mapEntry : map.entrySet()) {
                jsValue += mapEntry.getKey().toString() + ":";
                jsValue += translateValue(mapEntry.getValue());
                jsValue += ",";
            }
            jsValue = StringUtils.removeEnd(jsValue, ",");

            jsValue += "}";
        } else {
            boolean quoteValue = true;

            Class<?> valueClass = value.getClass();
            if (TypeUtils.isBooleanClass(valueClass) || TypeUtils.isDecimalClass(valueClass) || TypeUtils
                    .isIntegralClass(valueClass)) {
                quoteValue = false;
            }

            if (quoteValue) {
                jsValue = "\"";
            }

            // TODO: should this go through property editors?
            jsValue += value.toString();

            if (quoteValue) {
                jsValue += "\"";
            }
        }

        return jsValue;
    }
}
