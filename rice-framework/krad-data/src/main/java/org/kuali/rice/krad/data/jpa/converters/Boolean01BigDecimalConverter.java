/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.data.jpa.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;

/**
*  Used to convert 0/1 boolean where the field is stored as a Decimal numeric type
 */
@Converter
public class Boolean01BigDecimalConverter implements AttributeConverter<Boolean, BigDecimal> {
    @Override
    public BigDecimal convertToDatabaseColumn(Boolean objectValue) {
        if (objectValue == null) {
            return BigDecimal.ZERO;
        }
        return objectValue ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    @Override
    public Boolean convertToEntityAttribute(BigDecimal dataValue){
        if(dataValue == null){
            return false;
        }
        return dataValue.compareTo(BigDecimal.ONE) == 0;
    }

}
