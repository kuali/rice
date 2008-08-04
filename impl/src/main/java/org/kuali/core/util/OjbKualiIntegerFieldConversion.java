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
package org.kuali.core.util;

import java.math.BigInteger;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

public class OjbKualiIntegerFieldConversion implements FieldConversion {

    /**
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        Object converted = source;

        if (source instanceof KualiInteger) {
            converted = new Long(((KualiInteger) source).longValue());
        }

        return converted;
    }

    /**
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        Object converted = source;

        if (source instanceof Long) {
            converted = new KualiInteger(((Long) source).longValue());
        }
        else if (source instanceof BigInteger) {
            converted = new KualiInteger((BigInteger) source);
        }

        return converted;
    }
}
