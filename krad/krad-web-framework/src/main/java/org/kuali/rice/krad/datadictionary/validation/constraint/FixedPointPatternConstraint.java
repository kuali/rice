/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krad.datadictionary.validation.constraint;

/**
 * TODO delyea don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FixedPointPatternConstraint extends ValidDataPatternConstraint {

    protected boolean allowNegative;
    protected int precision;
    protected int scale;

    /**
     * Overriding retrieval of 
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersPatternConstraint#getRegexString()
     */
    @Override
    protected String getRegexString() {
        StringBuilder regex = new StringBuilder();

        if (isAllowNegative()) {
            regex.append("-?");
        }
        // final patter will be: -?([0-9]{0,p-s}\.[0-9]{1,s}|[0-9]{1,p-s}) where p = precision, s=scale
        regex.append("(");
        regex.append("[0-9]{0," + (getPrecision() - getScale()) + "}");
        regex.append("\\.");
        regex.append("[0-9]{1," + getScale() + "}");
        regex.append("|[0-9]{1," + (getPrecision() - getScale()) + "}");
        regex.append(")");
        return regex.toString();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint#getLabelKey()
     */
    @Override
    public String getLabelKey() {
        StringBuffer buf = new StringBuffer("validation.format." + getPatternTypeKey());
        if (isAllowNegative()) {
            buf.append(".allowNegative");
        }
        return buf.toString();
    }

    /**
     * @return the allowNegative
     */
    public boolean isAllowNegative() {
        return this.allowNegative;
    }

    /**
     * @param allowNegative the allowNegative to set
     */
    public void setAllowNegative(boolean allowNegative) {
        this.allowNegative = allowNegative;
    }

    /**
     * @return the precision
     */
    public int getPrecision() {
        return this.precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * @return the scale
     */
    public int getScale() {
        return this.scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    /*******************************************************************************/

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.ValidationPattern#getValidationErrorMessageParameters(java.lang.String)
     */
//    @Override
//    public String[] getValidationErrorMessageParameters(String attributeLabel) {
//        return new String[]{attributeLabel, String.valueOf(precision), String.valueOf(scale)};
//    }

}
