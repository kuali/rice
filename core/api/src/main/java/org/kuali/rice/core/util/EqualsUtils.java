/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.core.util;

import org.apache.commons.lang.ArrayUtils;
import java.lang.reflect.Field;

public class EqualsUtils {
    public static <T> boolean areObjectsEqualUsingCompareTo(T o1, T o2, String... fieldNames) {
        if (o1 == o2) { return true; }

        boolean isEqual = true;
        Class<?> targetClass = o1.getClass();
        try {
            for (String fieldName : fieldNames) {
                Field field = targetClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                Class<?> fieldClass = field.getType();

                if (ArrayUtils.contains(fieldClass.getInterfaces(), Comparable.class)) {
                    @SuppressWarnings("unchecked") Comparable<Object> c1 = (Comparable) field.get(o1);
                    @SuppressWarnings("unchecked") Comparable<Object> c2 = (Comparable) field.get(o2);
                    isEqual = (c1.compareTo(c2) == 0);
                } else {
                    isEqual = false;
                }

                if (!isEqual) {
                    break;
                }
            }

            return isEqual;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
