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

import org.apache.commons.lang.StringUtils;

/**
 * Mask a value using the specified char up to a given length.
 * 
 * 
 */
public class MaskFormatterSubString implements MaskFormatter {
    private String maskCharacter;
    private int maskLength;

    public String maskValue(Object value) {
        if (value == null) {
            return null;
        }

        if (maskCharacter == null) {
            throw new RuntimeException("Mask character not specified. Check DD maskTo attribute.");
        }

        String strValue = value.toString();
        if (strValue.length() < maskLength) {
            throw new RuntimeException("Data value length exceeds mask length defined in field authorization.");
        }

        return StringUtils.repeat(maskCharacter, maskLength) + strValue.substring(maskLength - 1);
    }

    /**
     * Gets the maskCharacter attribute.
     * 
     * @return Returns the maskCharacter.
     */
    public String getMaskCharacter() {
        return maskCharacter;
    }

    /**
     * Sets the maskCharacter attribute value.
     * 
     * @param maskCharacter The maskCharacter to set.
     */
    public void setMaskCharacter(String maskCharacter) {
        this.maskCharacter = maskCharacter;
    }

    /**
     * Gets the maskLength attribute.
     * 
     * @return Returns the maskLength.
     */
    public int getMaskLength() {
        return maskLength;
    }

    /**
     * Sets the maskLength attribute value.
     * 
     * @param maskLength The maskLength to set.
     */
    public void setMaskLength(int maskLength) {
        this.maskLength = maskLength;
    }


}
