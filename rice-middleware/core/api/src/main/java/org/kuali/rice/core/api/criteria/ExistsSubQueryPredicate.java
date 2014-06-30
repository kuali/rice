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
package org.kuali.rice.core.api.criteria;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.w3c.dom.Element;

/**
 * An immutable predicate which represents an "WHERE EXISTS" statement.
 * 
 * This implementation assumes that there is a single field which can be related 
 * between the inner and outer queries.  An equality between those fields is
 * automatically added to the predicates of the inner query.  
 *
 * @see PredicateFactory for a convenient way to construct this class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since Rice 2.4.2
 *
 */
@XmlRootElement(name = ExistsSubQueryPredicate.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ExistsSubQueryPredicate.Constants.TYPE_NAME, propOrder = {
    ExistsSubQueryPredicate.Elements.SUB_QUERY_PREDICATE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ExistsSubQueryPredicate extends AbstractPredicate implements SubQueryPredicate {

	private static final long serialVersionUID = 2397296074921454859L;

    @XmlAttribute(name = Elements.SUB_QUERY_TYPE, required = true)
	protected String subQueryType;
    
    @XmlElements(value = {
            @XmlElement(name = AndPredicate.Constants.ROOT_ELEMENT_NAME, type = AndPredicate.class, required = false),
            @XmlElement(name = EqualPredicate.Constants.ROOT_ELEMENT_NAME, type = EqualPredicate.class, required = false),
            @XmlElement(name = EqualIgnoreCasePredicate.Constants.ROOT_ELEMENT_NAME, type = EqualIgnoreCasePredicate.class, required = false),
            @XmlElement(name = ExistsSubQueryPredicate.Constants.ROOT_ELEMENT_NAME, type = ExistsSubQueryPredicate.class, required = false),
            @XmlElement(name = GreaterThanPredicate.Constants.ROOT_ELEMENT_NAME, type = GreaterThanPredicate.class, required = false),
            @XmlElement(name = GreaterThanOrEqualPredicate.Constants.ROOT_ELEMENT_NAME, type = GreaterThanOrEqualPredicate.class, required = false),
            @XmlElement(name = InPredicate.Constants.ROOT_ELEMENT_NAME, type = InPredicate.class, required = false),
            @XmlElement(name = InIgnoreCasePredicate.Constants.ROOT_ELEMENT_NAME, type = InIgnoreCasePredicate.class, required = false),
            @XmlElement(name = LessThanPredicate.Constants.ROOT_ELEMENT_NAME, type = LessThanPredicate.class, required = false),
            @XmlElement(name = LessThanOrEqualPredicate.Constants.ROOT_ELEMENT_NAME, type = LessThanOrEqualPredicate.class, required = false),
            @XmlElement(name = LikePredicate.Constants.ROOT_ELEMENT_NAME, type = LikePredicate.class, required = false),
            @XmlElement(name = LikeIgnoreCasePredicate.Constants.ROOT_ELEMENT_NAME, type = LikeIgnoreCasePredicate.class, required = false),
            @XmlElement(name = NotEqualPredicate.Constants.ROOT_ELEMENT_NAME, type = NotEqualPredicate.class, required = false),
            @XmlElement(name = NotEqualIgnoreCasePredicate.Constants.ROOT_ELEMENT_NAME, type = NotEqualIgnoreCasePredicate.class, required = false),
            @XmlElement(name = NotInPredicate.Constants.ROOT_ELEMENT_NAME, type = NotInPredicate.class, required = false),
            @XmlElement(name = NotInIgnoreCasePredicate.Constants.ROOT_ELEMENT_NAME, type = NotInIgnoreCasePredicate.class, required = false),
            @XmlElement(name = NotLikePredicate.Constants.ROOT_ELEMENT_NAME, type = NotLikePredicate.class, required = false),
            @XmlElement(name = NotNullPredicate.Constants.ROOT_ELEMENT_NAME, type = NotNullPredicate.class, required = false),
            @XmlElement(name = NullPredicate.Constants.ROOT_ELEMENT_NAME, type = NullPredicate.class, required = false),
            @XmlElement(name = OrPredicate.Constants.ROOT_ELEMENT_NAME, type = OrPredicate.class, required = false)
        })
	protected Predicate subQueryPredicate;

    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

	/**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private ExistsSubQueryPredicate() {
    }

    public ExistsSubQueryPredicate(String subQueryType, Predicate subQueryPredicate) {
        super();
        if ( StringUtils.isBlank(subQueryType) ) {
            throw new IllegalArgumentException("subQueryType is required");
        }
        this.subQueryType = subQueryType;
        this.subQueryPredicate = subQueryPredicate;
    }

    @Override
    public String getSubQueryType() {
        return this.subQueryType;
    }

    @Override
    public Predicate getSubQueryPredicate() {
        return this.subQueryPredicate;
    }
    
    @Override
    public String toString() {
        return new StringBuilder(CriteriaSupportUtils.findDynName(this.getClass().getSimpleName()))
                .append("(").append(getSubQueryType())
                .append(" WHERE ").append(getSubQueryPredicate())
                .append(")").toString();
    }
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "existsSubQuery";
        final static String TYPE_NAME = "ExistsSubQueryType";
    }

    /**
     * A private class which exposes constants which define the XML element
     * names to use when this object is marshaled to XML.
     */
    static class Elements {
        final static String SUB_QUERY_TYPE = "subQueryType";
        final static String SUB_QUERY_PREDICATE = "subQueryPredicate";
    }    
}
