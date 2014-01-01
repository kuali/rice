/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class for numeric searchable attributes.
 *
 * <p>Contains common logic for validation along with a template method for retrieving a validation Pattern.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class SearchableAttributeNumericBase extends SearchableAttributeBase {

    /**
     * Returns a Pattern object used for validating the format of number Strings.
     *
     * <p>{@link Pattern}s are immutable and thus safe for concurrent use, so it makes sense to return
     * a pre-compiled static instance.</p>
     *
     * <p>The pattern should only match valid String representations of the numeric type</p>
     *
     * @return the Pattern used for validating number Strings.
     */
    abstract protected Pattern getDefaultValidationPattern();

    /**
     * is the given value valid for searching against this attribute?
     *
     * <p>This method detects the binary operators defined by
     * {@link org.kuali.rice.core.api.search.SearchOperator#BETWEEN},
     * {@link org.kuali.rice.core.api.search.SearchOperator#AND}, and
     * {@link org.kuali.rice.core.api.search.SearchOperator#OR} and validates their operands by recursing on them.
     * It also strips off other valid numeric operators before parsing the leaf operands.
     * </p>
     *
     * <p>A Pattern which is provided by the template method {@link #getDefaultValidationPattern()} is used for parsing
     * the numeric strings themselves.</p>
     *
     * <p>Note that the parsing of expressions done here is very rudimentary, this method is mostly focused on
     * validating that any operands are valid numeric strings for the attribute type.</p>
     *
     * @param valueEntered
     * @return true if the valueEntered is considered valid
     */
    @Override
    public boolean isPassesDefaultValidation(String valueEntered) {

        boolean isValid = true;

        if (StringUtils.contains(valueEntered, SearchOperator.AND.op())) {
            isValid = isOperandsValid(valueEntered, SearchOperator.AND);
        } else if (StringUtils.contains(valueEntered, SearchOperator.OR.op())) {
            isValid = isOperandsValid(valueEntered, SearchOperator.OR);
        } else if (StringUtils.contains(valueEntered, SearchOperator.BETWEEN.op())) {
            isValid = isOperandsValid(valueEntered, SearchOperator.BETWEEN);
        } else {
            // default case is a plain old number, no splitting or recursion required

            Pattern pattern = getDefaultValidationPattern();
            Matcher matcher = pattern.matcher(SQLUtils.cleanNumericOfValidOperators(valueEntered).trim());

            isValid = matcher.matches();
        }

        return isValid;
    }

    /**
     * Tests that (if the given binaryOperator is present in the valueEntered) the operands are valid.
     *
     * <p>The operand test is done by calling isPassesDefaultValidation.  If the binaryOperator is not present,
     * true is returned.</p>
     *
     * @param valueEntered the string being validated
     * @param binaryOperator the operator to test
     * @return whether the operands are valid for the given binaryOperator
     */
    private boolean isOperandsValid(String valueEntered, SearchOperator binaryOperator) {
        if (StringUtils.contains(valueEntered, binaryOperator.op())) {
            // using this split method to make sure we test both sides of the operator.  Using String.split would
            // throw away empty strings, so e.g. "&&100".split("&&") would return an array with one element, ["100"].
            String [] l = StringUtils.splitByWholeSeparatorPreserveAllTokens(valueEntered, binaryOperator.op());
            for(String value : l) {
                if (!isPassesDefaultValidation(value)) {
                    return false;
                }
            }
        }

        return true;
    }
}
