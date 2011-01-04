/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.mask.MaskFormatter;

/**
 * This class defines a set of restrictions that are possible on an attribute in a 
 * {@link BusinessObjectEntry} or a maintainable field in a {@link MaintenanceDocumentEntry}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class AttributeSecurity extends DataDictionaryDefinitionBase {
	private static final long serialVersionUID = -7923499408946975318L;
	
	boolean readOnly = false;
	boolean hide = false;
	boolean mask = false;
	boolean partialMask = false;
	MaskFormatter partialMaskFormatter;
	MaskFormatter maskFormatter;

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * @param readOnly
	 *            the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return the hide
	 */
	public boolean isHide() {
		return this.hide;
	}

	/**
	 * @param hide
	 *            the hide to set
	 */
	public void setHide(boolean hide) {
		this.hide = hide;
	}

	/**
	 * @return the mask
	 */
	public boolean isMask() {
		return this.mask;
	}

	/**
	 * @param mask
	 *            the mask to set
	 */
	public void setMask(boolean mask) {
		this.mask = mask;
	}

	/**
	 * @return the partialMask
	 */
	public boolean isPartialMask() {
		return this.partialMask;
	}

	/**
	 * @param partialMask
	 *            the partialMask to set
	 */
	public void setPartialMask(boolean partialMask) {
		this.partialMask = partialMask;
	}

	/**
	 * @return the maskFormatter
	 */
	public MaskFormatter getMaskFormatter() {
		return this.maskFormatter;
	}

	/**
	 * @param maskFormatter
	 *            the maskFormatter to set
	 */
	public void setMaskFormatter(MaskFormatter maskFormatter) {
		this.maskFormatter = maskFormatter;
	}

	/**
	 * @return the partialMaskFormatter
	 */
	public MaskFormatter getPartialMaskFormatter() {
		return this.partialMaskFormatter;
	}

	/**
	 * @param partialMaskFormatter
	 *            the partialMaskFormatter to set
	 */
	public void setPartialMaskFormatter(MaskFormatter partialMaskFormatter) {
		this.partialMaskFormatter = partialMaskFormatter;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class,
	 *      java.lang.Class)
	 */
	public void completeValidation(Class rootBusinessObjectClass,
			Class otherBusinessObjectClass) {

		if (mask && maskFormatter == null) {
			throw new AttributeValidationException("MaskFormatter is required");
		}
		if (partialMask && partialMaskFormatter == null) {
			throw new AttributeValidationException(
					"PartialMaskFormatter is required");
		}
	}

	/**
	 * Returns whether any of the restrictions defined in this class are true.
	 */
	public boolean hasAnyRestriction() {
		return readOnly || mask || partialMask || hide;
	}
	
	
	/**
	 * Returns whether any of the restrictions defined in this class indicate that the attribute value potentially needs
	 * to be not shown to the user (i.e. masked, partial mask, hide).  Note that readonly does not fall in this category.
	 * 
	 * @return
	 */
	public boolean hasRestrictionThatRemovesValueFromUI() {
		return mask || partialMask || hide;	
	}
}
