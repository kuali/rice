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

import org.kuali.rice.core.api.CoreConstants;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.List;

/**
 * Defines a criteria statement which will form part of a query.  The Criteria
 * is itself a composite expression which represents an implicit "And" on it's
 * list of expressions.
 * 
 * <p>To construct a new Criteria instance, the {@link CriteriaBuilder} should
 * be used. Once all expressions have been established on the builder, the
 * {@link CriteriaBuilder#build()} method can be invoked to construct the
 * Criteria. The resulting Criteria object is both immutable and thread-safe,
 * along with all expressions contained therein.
 * 
 * <p>A criteria itself could be empty.  In which case it is expected that any
 * query performed using that criteria should produce all records of the target
 * data being queried.
 *
 * @see CriteriaBuilder
 * @see QueryByCriteria
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = Criteria.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Criteria.Constants.TYPE_NAME, propOrder = {
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class Criteria extends AbstractCompositeExpression {
		
	private static final long serialVersionUID = -8662772314094715831L;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

	/**
	 * Used only by JAXB.
	 */
	@SuppressWarnings("unused")
	private Criteria() {
		super();
	}
	
	/**
	 * Constructor meant to be used only by the {@link CriteriaBuilder}
	 * 
	 * @param expressions the expressions to use when constructing this criteria, can be null or empty
	 * 
	 * @see AbstractCompositeExpression#AbstractCompositeExpression(List)
	 */
	Criteria(List<Expression> expressions) {
	    //don't worry about defensive copy of list here - super class takes care of it.
        super(expressions);
	}
		
	/**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "criteria";
        final static String TYPE_NAME = "CriteriaType";
    }
	
}
