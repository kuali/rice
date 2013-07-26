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


/**
 * The maskLiteral element is used to completely hide the field value for
 * unauthorized users. The specified literal will be shown instead of the field
 * value.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectAttributeMaskFormatterLiteral implements DataObjectAttributeMaskFormatter {
    private static final long serialVersionUID = 3368293409242411693L;

	protected String literal = "********";

    @Override
	public String maskValue(Object value) {
        return literal;
    }

    public String getLiteral() {
        return literal;
    }

    /**
     * Specify the string that will be shown instead of the actual value when masked.
     */
    public void setLiteral(String literal) {
        this.literal = literal;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MaskFormatterLiteral [");
		if (literal != null) {
			builder.append("literal=").append(literal);
		}
		builder.append("]");
		return builder.toString();
	}

}
