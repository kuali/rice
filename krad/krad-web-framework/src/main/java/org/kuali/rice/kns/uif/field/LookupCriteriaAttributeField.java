/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.uif.control.MultiValueControlBase;
import org.kuali.rice.kns.uif.control.RadioGroupControl;
import org.kuali.rice.kns.uif.control.TextControl;
import org.kuali.rice.kns.uif.util.LookupInquiryUtils;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupCriteriaAttributeField extends AttributeField {

	protected boolean treatWildcardsAndOperatorsAsLiteral = false;

	/**
     * @return the treatWildcardsAndOperatorsAsLiteral
     */
    public boolean isTreatWildcardsAndOperatorsAsLiteral() {
    	return this.treatWildcardsAndOperatorsAsLiteral;
    }

	/**
     * @param treatWildcardsAndOperatorsAsLiteral the treatWildcardsAndOperatorsAsLiteral to set
     */
    public void setTreatWildcardsAndOperatorsAsLiteral(boolean treatWildcardsAndOperatorsAsLiteral) {
    	this.treatWildcardsAndOperatorsAsLiteral = treatWildcardsAndOperatorsAsLiteral;
    }

	/**
	 * Defaults the properties of the <code>AttributeField</code> to the corresponding properties of its <code>AttributeDefinition</code> retrieved from the dictionary (if such an entry exists). If
	 * the field already contains a value for a property, the definitions value is not used.
	 * 
	 * @param attributeDefinition
	 *            - AttributeDefinition instance the property values should be copied from
	 */
	@Override
	public void copyFromAttributeDefinition(AttributeDefinition attributeDefinition) {
		LookupInquiryUtils.initializeAttributeFieldFromAttributeDefinition(this, attributeDefinition);
		
		if (getControl() == null) {
		    return;
		}

		// set field size to 30 if not already set
		if ( (TextControl.class.isAssignableFrom(getControl().getClass())) && (((TextControl)getControl()).getSize() <= 0) ) {
			((TextControl)getControl()).setSize(30);
		}

		// overwrite maxLength to allow for wildcards and ranges in the select, but only if it's not a mulitselect box, because maxLength determines the # of entries
		if ((getMaxLength() == null) && (!MultiValueControlBase.class.isAssignableFrom(getControl().getClass()))) {
			setMaxLength(100);
		}

		if (StringUtils.isEmpty(getDefaultValue())) {
			// if the attrib name is "active", and BO is Inactivatable, then set the default value to Y
			// TODO delyea: this used to take into account if the class was Inactivateable:
			// Inactivateable.class.isAssignableFrom(businessObjectClass)
			// TODO delyea: check to see if the propertyName needs to be checked for instances where getPropertyName() returns "bo.active"
			if ((StringUtils.equals(getPropertyName(), KNSPropertyConstants.ACTIVE)) && (RadioGroupControl.class.isAssignableFrom(getControl().getClass()))) {
				setDefaultValue(KNSConstants.YES_INDICATOR_VALUE);
			}
		}

		/*
		 * TODO delyea: FieldUtils.createAndPopulateFieldsForLookup used to allow for a set of property names to be passed in via the URL 
		 * parameters of the lookup url to set fields as 'read only'
		 */

	}

}
