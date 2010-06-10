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
package org.kuali.rice.core.xml.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.xml.CoreNamespaceConstants;

@XmlType(name="attributeEntry",namespace=CoreNamespaceConstants.CORE)
public class AttributeEntry {
	@XmlAttribute
    public String value;
	@XmlAttribute
    public String key;

    public AttributeEntry() {
    }

    public AttributeEntry(
        String tKey,
        String tValue) {
        key = tKey;
        value = tValue;
    }

    public String toString() {
        return "key=" + key + "  value=" + value;
    }
}
