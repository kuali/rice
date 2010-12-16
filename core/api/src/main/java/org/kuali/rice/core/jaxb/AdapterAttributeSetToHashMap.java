/*
 * Copyright 2007 Sun Microsystems, Inc.
 * All rights reserved.  You may not modify, use,
 * reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * http://developer.sun.com/berkeley_license.html
 */


/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * https://jwsdp.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 */
package org.kuali.rice.core.jaxb;

import java.util.TreeMap;
import java.util.Iterator;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kuali.rice.core.xml.dto.AttributeEntry;
import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.core.xml.dto.AttributeSetList;


/*
 *
 *  PurchaseList - ValueType
 *  HashMap - BoundType
 */
public class AdapterAttributeSetToHashMap extends XmlAdapter<AttributeSetList, AttributeSet> {
    public AdapterAttributeSetToHashMap() {
    }

    // Convert a value type to a bound type.
    // read xml content and put into Java class.
    public AttributeSet unmarshal(AttributeSetList v) {
        int cnt = v.entry.size();
        AttributeSet aHashMap = new AttributeSet(cnt);

        for (int i = 0; i < cnt; i++) {
            AttributeEntry pe = v.entry.get(i);
            aHashMap.put(pe.key, pe.value);
        }

        return aHashMap;
    }

    // Convert a bound type to a value type.
    // write Java content into class that generates desired XML 
    public AttributeSetList marshal(AttributeSet v) {
        AttributeSetList pList = new AttributeSetList();

        // For QA consistency order the output. 
        TreeMap<String, String> tMap = new TreeMap<String, String>(v);

        Iterator it = tMap.keySet().iterator();
        while (it.hasNext()){
        	String key = (String) it.next();
            pList.entry.add(
                    new AttributeEntry(
                        key,
                        tMap.get(key)));
        }
        return pList;
    }
}
