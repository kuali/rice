/*
 * Copyright 2006-2007 The Kuali Foundation
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
package org.kuali.rice.core.impl.namespace;

import java.util.Comparator;

public class NamespaceComparator implements Comparator<NamespaceBo> {

    public static final Comparator<NamespaceBo> INSTANCE = new NamespaceComparator();

    private NamespaceComparator() {

    }

    public int compare(NamespaceBo o1, NamespaceBo o2) {
        return o1.getCode().compareTo( o2.getCode() );
    }
}
