/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.kuali.rice.core.api.criteria;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract implementation of a {@link CompositePredicate}.  This class defines all of the JAXB
 * annotations such that sub-classes should not have to.
 * 
 * <p>If a class subclasses this class then it *MUST* be sure to add itself to the JAXB
 * annotations for {@link #predicates}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AbstractCompositePredicate.Constants.TYPE_NAME)
abstract class AbstractCompositePredicate extends AbstractPredicate implements CompositePredicate {

    private static final long serialVersionUID = 6164560054223588779L;
    
    /**
     * Defines the JAXB annotations for the List of predicates.  All supported predicates *MUST* be
     * included in this List in order for them to be supported in the XML schema.
     * 
     * If a new type of predicate is created it *MUST* be added to this list.
     */
	@XmlElements(value = {
            @XmlElement(name = AndPredicate.Constants.ROOT_ELEMENT_NAME, type = AndPredicate.class, required = true),
            @XmlElement(name = EqualPredicate.Constants.ROOT_ELEMENT_NAME, type = EqualPredicate.class, required = true),
            @XmlElement(name = GreaterThanPredicate.Constants.ROOT_ELEMENT_NAME, type = GreaterThanPredicate.class, required = true),
            @XmlElement(name = GreaterThanOrEqualPredicate.Constants.ROOT_ELEMENT_NAME, type = GreaterThanOrEqualPredicate.class, required = true),
            @XmlElement(name = InPredicate.Constants.ROOT_ELEMENT_NAME, type = InPredicate.class, required = true),
            @XmlElement(name = LessThanPredicate.Constants.ROOT_ELEMENT_NAME, type = LessThanPredicate.class, required = true),
            @XmlElement(name = LessThanOrEqualPredicate.Constants.ROOT_ELEMENT_NAME, type = LessThanOrEqualPredicate.class, required = true),
            @XmlElement(name = LikePredicate.Constants.ROOT_ELEMENT_NAME, type = LikePredicate.class, required = true),
            @XmlElement(name = NotEqualPredicate.Constants.ROOT_ELEMENT_NAME, type = NotEqualPredicate.class, required = true),
            @XmlElement(name = NotInPredicate.Constants.ROOT_ELEMENT_NAME, type = NotInPredicate.class, required = true),
            @XmlElement(name = NotLikePredicate.Constants.ROOT_ELEMENT_NAME, type = NotLikePredicate.class, required = true),
            @XmlElement(name = NotNullPredicate.Constants.ROOT_ELEMENT_NAME, type = NotNullPredicate.class, required = true),
            @XmlElement(name = NullPredicate.Constants.ROOT_ELEMENT_NAME, type = NullPredicate.class, required = true),
            @XmlElement(name = OrPredicate.Constants.ROOT_ELEMENT_NAME, type = OrPredicate.class, required = true)
    })
    private final List<Predicate> predicates;

	/**
	 * This default constructor exists only to be invoked by sub-classes
	 * in their default constructors which is used by JAXB. 
	 */
    AbstractCompositePredicate() {
        this.predicates = null;
    }

    /**
     * When invoked by a subclass, this constructor will set the predicates
     * to the given list. If the list is null then it will be translated
     * internally to an empty list.
     * 
     * @param predicates the list of predicates to set
     */
    AbstractCompositePredicate(final List<Predicate> predicates) {
        if (predicates == null) {
            this.predicates = new ArrayList<Predicate>();
        } else {
            this.predicates = new ArrayList<Predicate>(predicates);
        }
    }

    @Override
    public List<Predicate> getPredicates() {
        return Collections.unmodifiableList(predicates);
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String TYPE_NAME = "CompositePredicateType";
    }

}
