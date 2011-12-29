/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krms.framework.engine.expression;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.util.jaxb.EnumStringAdapter;
import org.kuali.rice.krms.api.KrmsApiServiceLocator;
import org.kuali.rice.krms.api.engine.expression.ComparisonOperatorService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Operators enumeration for comparing objects.  EQUALS NOT_EQUALS GREATER_THAN GREATER_THAN_EQUAL LESS_THAN LESS_THAN_EQUAL
 */
public enum ComparisonOperator implements Coded {

	EQUALS("="),
	NOT_EQUALS("!="),
	GREATER_THAN(">"),
	GREATER_THAN_EQUAL(">="),
	LESS_THAN("<"),
	LESS_THAN_EQUAL("<=");
	
	private final String code;
	private ComparisonOperator(String code) {
		this.code = code;
	}

    /**
     *
     * @return code representing the type of operator
     */
	public String getCode() {
		return code;
	}

    /**
     *
     * @param code
     * @return a ComparisonOperator created with the given code.
     * @throws IllegalArgumentException if the given code does not exist
     */
	public static ComparisonOperator fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (ComparisonOperator comparisonOperator : values()) {
			if (comparisonOperator.code.equals(code)) {
				return comparisonOperator;
			}
		}
		throw new IllegalArgumentException("Failed to locate the ComparisionOperator with the given code: " + code);
	}

    /**
     *
     * @param lhs
     * @param rhs
     * @return boolean value of comparison results based on the type of operator.
     */
	public boolean compare(Object lhs, Object rhs) {
        EngineComparatorExtension extension = determineComparatorOperator(lhs, rhs);

        int result = extension.compare(lhs, rhs);

        if (this == EQUALS) {
            return result == 0;
        } else if (this == NOT_EQUALS) {
            return result != 0;
        } else if (this == GREATER_THAN) {
            return result > 0;
        } else if (this == GREATER_THAN_EQUAL) {
            return result >= 0;
        } else if (this == LESS_THAN) {
            return result < 0;
        } else if (this == LESS_THAN_EQUAL) {
            return result <= 0;
        }
        throw new IllegalStateException("Invalid unequal operator detected: " + this);
	}

    private EngineComparatorExtension determineComparatorOperator(Object lhs, Object rhs) {
        EngineComparatorExtension extension = null;
        try {
            // If instance is of a registered type, use configured ComparisonOperator
            // KrmsAttributeDefinitionService service = KRMSServiceLocatorInternal.getService("comparisonOperatorRegistration"); // lotta moves
            ComparisonOperatorService service = KrmsApiServiceLocator.getComparisonOperatorService();
            if (service.canCompare(lhs, rhs)) {
                extension = service.findComparatorExtension(lhs, rhs); // maybe better to get result from service?
            }
        } catch (Exception e) {
            e.printStackTrace();   // TODO EGHM
        }
        if (extension == null) {
            extension = new DefaultComparisonOperator();
        }
        return extension;
    }

    /**
     * Operator codes
     */
    public static final Collection<String> OPERATOR_CODES =
        Collections.unmodifiableCollection(Arrays.asList(EQUALS.getCode(), NOT_EQUALS.getCode(), GREATER_THAN.getCode(),
                GREATER_THAN_EQUAL.getCode(), LESS_THAN.getCode(), LESS_THAN_EQUAL.getCode()));

    /**
     * Operator names
     */
    public static final Collection<String> OPERATOR_NAMES =
        Collections.unmodifiableCollection(Arrays.asList(EQUALS.name(), NOT_EQUALS.name(), GREATER_THAN.name(),
                GREATER_THAN_EQUAL.name(), LESS_THAN.name(), LESS_THAN_EQUAL.name()));

    /**
     *
     * @return type code
     */
    @Override
    public String toString(){
        return code;
    }

    static final class Adapter extends EnumStringAdapter<ComparisonOperator> {

        protected Class<ComparisonOperator> getEnumClass() {
            return ComparisonOperator.class;
        }
    }
}
