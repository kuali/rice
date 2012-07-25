/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.validation.constraint;

import java.util.List;

/**
 * CaseConstraint is imposed only when a certain condition is met
 *
 * <p>For example, if the country attribute value is "USA",
 * then a prerequisite constraint may be imposed that the 'State' attribute is non-null.</p>
 *
 * <p>
 * This class is a direct copy of one that was in Kuali Student.</p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
public class CaseConstraint extends BaseConstraint {

	protected String propertyName;
	protected String operator;
	protected boolean caseSensitive;

    protected List<WhenConstraint> whenConstraint;

    /**
     * get the {@code WhenConstraint}'s defined by this case constraint
     *
     * @return a list of constraints, null if not initialized
     */
	public List<WhenConstraint> getWhenConstraint() {
		return whenConstraint;
	}

    /**
     * sets the {@code WhenConstraint}'s defined by this case constraint
     *
     * @param whenConstraint - the list of constraints
     */
	public void setWhenConstraint(List<WhenConstraint> whenConstraint) {
		this.whenConstraint = whenConstraint;
	}

    /**
     * gets the property name for the attribute to which the case constraint is applied to
     *
     * @return the property name
     */
	public String getPropertyName() {
		return propertyName;
	}

    /**
     * setter for property name
     *
     * @param propertyName a valid property name
     */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

    /**
     * specifies the kind of relationship to be checked between the actual value and the ones defined in the {@link #getWhenConstraint()}
     *
     * @see org.kuali.rice.krad.uif.UifConstants.CaseConstraintOperators
     * @return an operator name
     */
	public String getOperator() {
		return operator;
	}

    /**
     * setter for the operator
     *
     * @see org.kuali.rice.krad.uif.UifConstants.CaseConstraintOperators
     * @param operator
     */
	public void setOperator(String operator) {
		this.operator = operator;
	}

    /**
     * checks whether string comparison will be carried out in a case sensitive fashion
     *
     * @return true if string comparison is case sensitive, false if not
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * setter for case sensitive
     *
     * @param caseSensitive - the case sensitive value to set
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
}