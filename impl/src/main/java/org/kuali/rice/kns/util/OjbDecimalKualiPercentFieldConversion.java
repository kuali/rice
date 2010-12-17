/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.rice.kns.util;

import java.math.BigDecimal;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.kuali.rice.core.util.type.KualiPercent;

/**
 * This class...
 * 
 * 
 */
public class OjbDecimalKualiPercentFieldConversion extends OjbKualiDecimalFieldConversion implements FieldConversion {

    private static BigDecimal oneHundred = new BigDecimal(100.0000);

    /**
     * Convert percentages to decimals for proper storage.
     * 
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {

        // Convert to BigDecimal using existing conversion.
        source = super.javaToSql(source);

        // Check for null, and verify object type.
        // Do conversion if our type is correct (BigDecimal).
        if (source != null && source instanceof BigDecimal) {
            BigDecimal converted = (BigDecimal) source;
            return converted;
        }
        else {
            return null;
        }
    }

    /**
     * Convert database decimals to 'visual' percentages for use in our business objects.
     * 
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {

        // Check for null, and verify object type.
        // Do conversion if our type is correct (BigDecimal).
        if (source != null && source instanceof BigDecimal) {
            BigDecimal converted = (BigDecimal) source;

            // Once we have converted, we need to convert again to KualiPercent.
            KualiPercent percentConverted = new KualiPercent((BigDecimal) converted.multiply(oneHundred));

            return percentConverted;

        }
        else {
            return null;
        }
    }
}
