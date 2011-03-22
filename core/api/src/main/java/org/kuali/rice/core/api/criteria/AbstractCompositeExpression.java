/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.kuali.rice.core.api.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * An abstract implementation of a {@link CompositeExpression}.  This class defines all of the JAXB
 * annotations such that sub-classes should not have to.
 * 
 * <p>If a class subclasses this class then it *MUST* be sure to add itself to the JAXB
 * annotations for {@link #expressions}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AbstractCompositeExpression.Constants.TYPE_NAME)
abstract class AbstractCompositeExpression extends AbstractExpression implements CompositeExpression {

    private static final long serialVersionUID = 6164560054223588779L;
    
    /**
     * Defines the JAXB annotations for the List of expressions.  All supported expressions *MUST* be
     * included in this List in order for them to be supported in the XML schema.
     * 
     * If a new type of expression is created it *MUST* be added to this list.
     */
	@XmlElements(value = {
            @XmlElement(name = AndExpression.Constants.ROOT_ELEMENT_NAME, type = AndExpression.class, required = true),
            @XmlElement(name = OrExpression.Constants.ROOT_ELEMENT_NAME, type = OrExpression.class, required = true),
            @XmlElement(name = EqualExpression.Constants.ROOT_ELEMENT_NAME, type = EqualExpression.class, required = true),
            @XmlElement(name = NotEqualExpression.Constants.ROOT_ELEMENT_NAME, type = NotEqualExpression.class, required = true),
            @XmlElement(name = LikeExpression.Constants.ROOT_ELEMENT_NAME, type = LikeExpression.class, required = true),
            @XmlElement(name = InExpression.Constants.ROOT_ELEMENT_NAME, type = InExpression.class, required = true),
            @XmlElement(name = NotInExpression.Constants.ROOT_ELEMENT_NAME, type = NotInExpression.class, required = true),
            @XmlElement(name = GreaterThanExpression.Constants.ROOT_ELEMENT_NAME, type = GreaterThanExpression.class, required = true),
            @XmlElement(name = GreaterThanOrEqualExpression.Constants.ROOT_ELEMENT_NAME, type = GreaterThanOrEqualExpression.class, required = true),
            @XmlElement(name = LessThanExpression.Constants.ROOT_ELEMENT_NAME, type = LessThanExpression.class, required = true),
            @XmlElement(name = LessThanOrEqualExpression.Constants.ROOT_ELEMENT_NAME, type = LessThanOrEqualExpression.class, required = true),
            @XmlElement(name = NullExpression.Constants.ROOT_ELEMENT_NAME, type = NullExpression.class, required = true),
            @XmlElement(name = NotNullExpression.Constants.ROOT_ELEMENT_NAME, type = NotNullExpression.class, required = true)
    })
    private final List<Expression> expressions;

	/**
	 * This default constructor exists only to be invoked by sub-classes
	 * in their default constructors which is used by JAXB. 
	 */
    AbstractCompositeExpression() {
        this.expressions = null;
    }

    /**
     * When invoked by a subclass, this constructor will set the expressions
     * to the given list. If the list is null then it will be translated
     * internally to an empty list.
     * 
     * @param expressions the list of expressions to set
     */
    AbstractCompositeExpression(final List<Expression> expressions) {
        if (expressions == null) {
            this.expressions = new ArrayList<Expression>();
        } else {
            this.expressions = new ArrayList<Expression>(expressions);
        }
    }

    @Override
    public List<Expression> getExpressions() {
        return Collections.unmodifiableList(expressions);
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String TYPE_NAME = "CompositeExpressionType";
    }

}
