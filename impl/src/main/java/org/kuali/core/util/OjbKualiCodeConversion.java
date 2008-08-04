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

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.kuali.core.bo.KualiCodeBase;

/**
 * 
 * This class crudely maps KualiCode to/from a String. It is intended as a temporary placeholder until a general technique for
 * managing codes is decided upon.
 * 
 * 
 */
public class OjbKualiCodeConversion implements FieldConversion {

    public Object javaToSql(Object source) throws ConversionException {
        if (source instanceof KualiCodeBase) {
            return ((KualiCodeBase) source).getCode();
        }
        return source;
    }

    public Object sqlToJava(Object source) throws ConversionException {
        if (source instanceof String) {
            return new KualiCodeBase((String) source);
        }

        return source;
    }

}
