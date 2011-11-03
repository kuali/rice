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
package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.util.jaxb.EnumStringAdapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public enum ComparisonOperator implements Coded {

	EQUALS ("="),
	NOT_EQUALS ("!="),
	GREATER_THAN (">"),
	GREATER_THAN_EQUAL (">="),
	LESS_THAN ("<"),
	LESS_THAN_EQUAL ("<=");
	
    private final String code;
    
    private ComparisonOperator(String code){
        this.code = code;
    }
    
    @Override
    public String getCode(){
        return code;
    }
    
	public <T> boolean compare(Comparable<T> lhs, T rhs) {
		int result = lhs.compareTo(rhs);
		if (this == EQUALS) {
			return result == 0;
		}
		if (this == NOT_EQUALS) {
			return result != 0;
		}
		if (this == GREATER_THAN) {
			return result > 0;
		}
		if (this == GREATER_THAN_EQUAL) {
			return result >= 0;
		}
		if (this == LESS_THAN) {
			return result < 0;
		}
		if (this == LESS_THAN_EQUAL) {
			return result <= 0;
		}
		throw new IllegalStateException("Invalid operator detected: " + this);
	}
	
    public static final Collection<String> OPERATOR_CODES =
        Collections.unmodifiableCollection(Arrays.asList(EQUALS.getCode(), NOT_EQUALS.getCode(), GREATER_THAN.getCode(),
                GREATER_THAN_EQUAL.getCode(), LESS_THAN.getCode(), LESS_THAN_EQUAL.getCode()));
		
    public static final Collection<String> OPERATOR_NAMES =
        Collections.unmodifiableCollection(Arrays.asList(EQUALS.name(), NOT_EQUALS.name(), GREATER_THAN.name(),
                GREATER_THAN_EQUAL.name(), LESS_THAN.name(), LESS_THAN_EQUAL.name()));
        
    public static ComparisonOperator fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ComparisonOperator operator : values()) {
            if (operator.code.equals(code)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("Failed to locate the ComparisonOperator with the given code: " + code);
    }
	
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
