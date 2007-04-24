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
package org.kuali.core.datadictionary.mask;

/**
 * Mask a value using a literal string.
 * 
 * 
 */
public class MaskFormatterLiteral implements MaskFormatter {
    private String literal;

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
     * Sets the literalString attribute value.
     * 
     * @param literal The literal String to set.
     */
    public void setLiteral(String literal) {
        this.literal = literal;
    }

}
