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
package org.kuali.rice.kns.web.comparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @deprecated KNS Struts deprecated, use KRAD and the Spring MVC framework.
 */
@Deprecated
public class NullValueComparator implements Comparator, Serializable {
    private static final NullValueComparator theInstance = new NullValueComparator();
    
    public NullValueComparator() {
    }

    public static NullValueComparator getInstance() {
        return theInstance;
    }
    
    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }
        else {
            // probably won't go into this code segment, but doing it just in case
            return CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(o1.getClass()).compare(o1, o2);
        }
    }
}
