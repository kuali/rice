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
 * This is a description of what this class does - ewestfal don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AbstractCompositeExpression.Constants.TYPE_NAME)
abstract class AbstractCompositeExpression extends AbstractExpression implements CompositeExpression {

    private static final long serialVersionUID = 6164560054223588779L;
    
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

    AbstractCompositeExpression() {
        this(null);
    }

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
