/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.web.format;

import java.util.Arrays;
import java.util.List;

import org.kuali.KeyConstants;

/**
 * This class is a formatter for little-b boolean classes, that cannot accept a null.
 */
public class LittleBooleanFormatter extends Formatter {

    static final List TRUE_VALUES = Arrays.asList(new String[] { "yes", "y", "true", "t", "on", "1", "enabled" });
    static final List FALSE_VALUES = Arrays.asList(new String[] { "no", "n", "false", "f", "off", "0", "disabled" });

    protected Object convertToObject(String target) {
        if (Formatter.isEmptyValue(target))
            return Boolean.FALSE;

        String stringValue = target.getClass().isArray() ? unwrapString(target) : (String) target;
        stringValue = stringValue.trim().toLowerCase();

        if (TRUE_VALUES.contains(stringValue))
            return Boolean.TRUE;
        if (FALSE_VALUES.contains(stringValue))
            return Boolean.FALSE;

        throw new FormatException("converting", KeyConstants.ERROR_BOOLEAN, stringValue);
    }

    public Object format(Object target) {
        if (target == null)
            return "No";
        if (target instanceof String) {
            return target;
        }

        boolean isTrue = ((Boolean) target).booleanValue();

        return isTrue ? "Yes" : "No";
    }

    protected Object getNullObjectValue() {
        return Boolean.FALSE;
    }

}
