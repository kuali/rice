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
package org.kuali.rice.core.api.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A composite expression which implements "and-ing" of multiple expressions together. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = And.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = And.Constants.TYPE_NAME)
public final class And implements CompositeExpression {

	@XmlAnyElement
	private final List<Expression> expressions;
	
	/**
	 * Used by JAXB for construction.
	 */
	And() {
		this.expressions = null;
	}
	
	/**
	 * Construct an "And" expression from the given list of expressions.  The list must contain
	 * at least one expression.
	 * 
	 * @param expressions the List of expressions to set on the And expression
	 * @throws IllegalArgumentException if the given list is null or empty
	 */
	And(List<Expression> expressions) {
		if (expressions == null || expressions.isEmpty()) {
			throw new IllegalArgumentException("Composite expression 'And' must contain at least one expression");
		}
		this.expressions = new ArrayList<Expression>(expressions);
	}
	
	@Override
	public List<Expression> getExpressions() {
		return Collections.unmodifiableList(expressions);
	}
	
	@Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
	
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "and";
        final static String TYPE_NAME = "AndType";
    }

}
