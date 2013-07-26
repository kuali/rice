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

import org.kuali.rice.krad.data.metadata.DataObjectAttributeSecurity;

/**
 * Defines a set of restrictions that are possible on an attribute
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataObjectAttributeSecurityImpl implements DataObjectAttributeSecurity {
    private static final long serialVersionUID = -7923499408946975318L;

    protected boolean readOnly = false;
    protected boolean hide = false;
    protected boolean mask = false;
    protected boolean partialMask = false;

    protected DataObjectAttributeMaskFormatter partialMaskFormatter;
    protected DataObjectAttributeMaskFormatter maskFormatter;

    @Override
	public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
	public boolean isHide() {
        return this.hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    @Override
	public boolean isMask() {
        return this.mask;
    }

    public void setMask(boolean mask) {
        this.mask = mask;
    }

    @Override
	public boolean isPartialMask() {
        return this.partialMask;
    }

    public void setPartialMask(boolean partialMask) {
        this.partialMask = partialMask;
    }

    @Override
	public DataObjectAttributeMaskFormatter getMaskFormatter() {
		if (maskFormatter == null) {
			maskFormatter = new DataObjectAttributeMaskFormatterLiteral();
		}
        return this.maskFormatter;
    }

    public void setMaskFormatter(DataObjectAttributeMaskFormatter maskFormatter) {
        this.maskFormatter = maskFormatter;
    }

    @Override
	public DataObjectAttributeMaskFormatter getPartialMaskFormatter() {
        return this.partialMaskFormatter;
    }

    public void setPartialMaskFormatter(DataObjectAttributeMaskFormatter partialMaskFormatter) {
        this.partialMaskFormatter = partialMaskFormatter;
    }

    /**
     * Returns whether any of the restrictions defined in this class are true.
     */
    @Override
	public boolean hasAnyRestriction() {
        return readOnly || mask || partialMask || hide;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataObjectAttributeSecurityBase [readOnly=").append(readOnly).append(", hide=").append(hide)
				.append(", mask=").append(mask).append(", partialMask=").append(partialMask).append(", ");
		if (maskFormatter != null) {
			builder.append("maskFormatter=").append(maskFormatter).append(", ");
		}
		if (partialMaskFormatter != null) {
			builder.append("partialMaskFormatter=").append(partialMaskFormatter);
		}
		builder.append("]");
		return builder.toString();
	}

}
