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
package org.kuali.rice.kns.datadictionary.mask;

import org.kuali.rice.core.web.format.Formatter;

/**
      The maskLiteral element is used to completely hide the field
      value for unauthorized users.  The specified literal will be
      shown instead of the field value.
 */
public class MaskFormatterLiteral  extends Formatter implements MaskFormatter {
    protected String literal;

    public Object formatObject(Object value) {
        if (value == null)
            return formatNull();
        
        return maskValue(value);
    }
    
    public String maskValue(Object value) {
        return literal;
    }

    /**
     * Gets the literalString attribute.
     * 
     * @return Returns the literal String.
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Specify the string that will be shown instead of the actual value when masked.
     */
    public void setLiteral(String literal) {
        this.literal = literal;
    }

}
