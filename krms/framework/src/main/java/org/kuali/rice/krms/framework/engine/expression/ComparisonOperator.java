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
package org.kuali.rice.krms.framework.engine.expression;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.util.jaxb.EnumStringAdapter;
import org.kuali.rice.krms.api.KrmsApiServiceLocator;
import org.kuali.rice.krms.api.engine.expression.ComparisonOperatorService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Operators enumeration for comparing objects.  EQUALS NOT_EQUALS GREATER_THAN GREATER_THAN_EQUAL LESS_THAN LESS_THAN_EQUAL.
 * Uses registered {@link EngineComparatorExtension} for the given objects or the {@link DefaultComparisonOperator}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum ComparisonOperator implements Coded {

    /**
     * use this flag with the static factory to get a {@link ComparisonOperator} EQUALS
     */
	EQUALS("="),

    /**
     * use this flag with the static factory to get a {@link ComparisonOperator} NOT_EQUALS
     */
	NOT_EQUALS("!="),

    /**
     * use this flag with the static factory to get a {@link ComparisonOperator} GREATER_THAN
     */
	GREATER_THAN(">"),

    /**
     * use this flag with the static factory to get a {@link ComparisonOperator} GREATER_THAN_EQUAL
     */
	GREATER_THAN_EQUAL(">="),

    /**
     * use this flag with the static factory to get a {@link ComparisonOperator} LESS_THAN
     */
	LESS_THAN("<"),

    /**
     * use this flag with the static factory to get a {@link ComparisonOperator} LESS_THAN_EQUAL
     */
	LESS_THAN_EQUAL("<=");
	
	private final String code;

    /**
     * Create a ComparisonOperator from the given code
     * @param code code the ComparisonOperator should be of.
     */
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
     * Create a ComparisonOperator from the given code
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
     * Compare the given objects
     * @param lhs left hand side object
     * @param rhs right hand side object
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
        throw new IllegalStateException("Invalid comparison operator detected: " + this);
	}

    /**
     * Return registered {@link EngineComparatorExtension} for the given objects or the {@link DefaultComparisonOperator}.
     * @param lhs left hand side object
     * @param rhs right hand side object
     * @return EngineComparatorExtension
     */
    // TODO EGHM move to utility class, or service if new possible breakage is okay. AgendaEditorController has similar code with different extension
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
            e.printStackTrace();   // TODO EGHM log
        }
        if (extension == null) {
            // It's tempting to have findStringCoercionExtension return this default, but if the Service lookup fails we
            // will be broken in a new way.  Would that be okay? - EGHM
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

        @Override
        protected Class<ComparisonOperator> getEnumClass() {
            return ComparisonOperator.class;
        }
    }
}
