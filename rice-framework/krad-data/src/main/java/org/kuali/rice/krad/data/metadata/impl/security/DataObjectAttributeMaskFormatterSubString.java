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
package org.kuali.rice.krad.data.metadata.impl.security;

import org.apache.commons.lang.StringUtils;

/**
 * The maskTo element is to used hide the beginning part of the value for
 * unauthorized users. The number of leading characters to hide and the
 * replacement character can be specified.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectAttributeMaskFormatterSubString implements DataObjectAttributeMaskFormatter {
    private static final long serialVersionUID = -876112522775686636L;

    protected String maskCharacter = "*";
    protected int maskLength;

    @Override
	public String maskValue(Object value) {
        if (value == null) {
            return null;
        }

        // If, for whatever reason, this was left empty, set it to an asterisk
        if ( StringUtils.isEmpty(maskCharacter) ) {
        	maskCharacter = "*";
        }

        String strValue = value.toString();
        if (strValue.length() < maskLength) {
            return StringUtils.repeat(maskCharacter, maskLength);
        }
        if (maskLength > 0) {
            return StringUtils.repeat(maskCharacter, maskLength) + strValue.substring(maskLength);
        } else {
            return strValue;
        }
    }

    public String getMaskCharacter() {
        return maskCharacter;
    }

    /**
     * Specify the character with which to mask the original value.
     *
     * @param maskCharacter for masking values
     */
    public void setMaskCharacter(String maskCharacter) {
        this.maskCharacter = maskCharacter;
    }

    public int getMaskLength() {
        return maskLength;
    }

    /**
     * Set the number of characters to mask at the beginning of the string.
     *
     * @param maskLength The maskLength to set.
     */
    public void setMaskLength(int maskLength) {
        this.maskLength = maskLength;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MaskFormatterSubString [");
		if (maskCharacter != null) {
			builder.append("maskCharacter=").append(maskCharacter).append(", ");
		}
		builder.append("maskLength=").append(maskLength).append("]");
		return builder.toString();
	}

}
