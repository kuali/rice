/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.rules;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for rules classes.
 * 
 * 
 */
public class RulesUtils {

    /**
     * 
     * This method is a null-safe wrapper around Set.contains().
     * 
     * @param set - methods returns false if the Set is null
     * @param value to seek
     * @return true iff Set exists and contains given value
     */
    public static boolean permitted(Set set, Object value) {
        if (set != null) {
            return set.contains(value);
        }
        return false;
    }

    public static boolean denied(Set set, Object value) {
        if (set != null) {
            return !set.contains(value);
        }
        return true;
    }

    public static Set makeSet(String elementString) {
        Set result = new HashSet();
        String[] elements = elementString.split(",");
        for (int i = 0; i < elements.length; i++) {
            result.add(elements[i]);
        }
        return result;
    }

    public static Set makeSet(String[] elements) {
        Set result = new HashSet();
        for (int i = 0; i < elements.length; i++) {
            result.add(elements[i]);
        }
        return result;
    }

}
