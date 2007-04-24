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
package org.kuali.core.util;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * 
 */
public class OjbCharBooleanConversion implements FieldConversion {

    /**
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        if (source instanceof Boolean) {
            if (source != null) {
                Boolean b = (Boolean) source;
                return b.booleanValue() ? "Y" : "N";
            }
            else {
                return null;
            }
        }
        else if (source instanceof String) {
            if ("true".equals(source)) {
                return "Y";
            }
            else if ("false".equals(source)) {
                return "N";
            }
        }
        return source;
    }

    /**
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        try {
            if (source instanceof String) {
                if (source != null) {
                    String s = (String) source;
                    return Boolean.valueOf("YT1".indexOf(s) >= 0);
                }
                else {
                    return null;
                }
            }
            return source;
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("I have exploded converting types", t);
        }
    }

}
