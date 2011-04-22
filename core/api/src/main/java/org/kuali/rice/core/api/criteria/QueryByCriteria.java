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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Arrays;
import java.util.Collection;

/**
 * Defines a criteria-based query.  Consists of a {@link Criteria} definition
 * as well as a set of additional properties which control paging and other
 * aspects of the results which should be returned from the query.
 * 
 * <p>In order to construct a new {@link QueryByCriteria}, the {@link Builder}
 * should be used.  This contains an internal {@link CriteriaBuilder} which
 * can be used to define the {@link Criteria} for use by the query.
 * 
 * <p>This class specifies nothing regarding how the query will be executed.
 * It is expected that an instance will be constructed and then passed to code
 * which understands how to execute the desired query.
 * 
 * <p>This class is mapped for use by JAXB and can therefore be used by clients
 * as part of remotable service definitions.
 * 
 * @param <T> the type of the class which is being queried for based on the
 * specified criteria
 * 
 * @see Criteria
 * @see CriteriaBuilder
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@XmlRootElement(name = QueryByCriteria.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = QueryByCriteria.Constants.TYPE_NAME, propOrder = {
		QueryByCriteria.Elements.PREDICATE,
		QueryByCriteria.Elements.START_AT_INDEX,
		QueryByCriteria.Elements.MAX_RESULTS,
		QueryByCriteria.Elements.COUNT_FLAG,
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public final class QueryByCriteria<T> implements ModelObjectComplete {

	private static final long serialVersionUID = 2210627777648920180L;

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
	private final Predicate predicate;
	
	@XmlElement(name = Elements.START_AT_INDEX, required = false)
	private final Integer startAtIndex;
		
	@XmlElement(name = Elements.MAX_RESULTS, required = false)
	private final Integer maxResults;
	
	@XmlElement(name = Elements.COUNT_FLAG, required = true)
	private final CountFlag countFlag;

    private final String queryClass;

	@SuppressWarnings("unused")
	@XmlAnyElement
	private final Collection<Element> _futureElements = null;

	private QueryByCriteria() {
		this.predicate = null;
		this.startAtIndex = null;
		this.maxResults = null;
		this.countFlag = null;
        this.queryClass = null;
	}

	private QueryByCriteria(Builder<T> builder) {
		final Predicate[] preds = builder.predicates;
        if (preds != null && preds.length > 1) {
            //implicit "and"
            this.predicate = PredicateFactory.and(builder.predicates);
        } else if (preds != null && preds.length == 1) {
            this.predicate = builder.predicates[0];
        } else {
            this.predicate = null;
        }

		this.startAtIndex = builder.getStartAtIndex();
		this.maxResults = builder.getMaxResults();
		this.countFlag = builder.getCountFlag();
        this.queryClass = builder.getQueryClass().getName();
	}

	/**
	 * Returns the {@link Predicate} which will be used to execute the query.
	 * 
	 * @return can be null if no predicate was specified
	 */
	public Predicate getPredicate() {
		return this.predicate;
	}

	/**
	 * Returns the optional zero-based "start" index for rows returned.  When
	 * this query is executed, this property should be read to determine the
	 * first row which should be returned.  If the given index is beyond the
	 * end of the result set, then the resulting query should effectively
	 * return no rows (as opposed to producing an index-based out of bounds
	 * error).  If the value is null, then the results should start with the
	 * first row returned from the query.
	 * 
	 * @return the starting row index requested by this query, or null if
	 * the results should start at the beginning of the result set
	 */
	public Integer getStartAtIndex() {
		return this.startAtIndex;
	}

	/**
	 * Returns the maximum number of results that this query is requesting
	 * to receive.  If null, then the query should return all rows, or as
	 * many as it can.  If the number request is larger than the number of
     * results then all results are returned.
	 * 
	 * @return the maximum number of results to return from the query
	 */
	public Integer getMaxResults() {
		return this.maxResults;
	}

	/**
	 * Indicates whether or not a total row count should be returned with the
	 * query.  See {@link CountFlag} for more information on what each of these
	 * flags means.  This will never return null and defaults to
	 * {@link CountFlag#NONE}.
	 * 
	 * @return the flag specifying whether or not a total row count should be
	 * produced by the query
	 */
	public CountFlag getCountFlag() {
		return this.countFlag;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this,
				Constants.HASH_CODE_EQUALS_EXCLUDE);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(obj, this,
				Constants.HASH_CODE_EQUALS_EXCLUDE);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static final class Builder<T> {

		private Predicate[] predicates;
		private Integer startAtIndex;
		private Integer maxResults;
		private CountFlag countFlag;
        private final Class<T> queryClass;

		private Builder(Class<T> queryClass) {
			this.countFlag = CountFlag.NONE;
            this.queryClass = queryClass;
		}

		public static <T> Builder<T> create(Class<T> queryClass) {
			if (queryClass == null) {
                throw new IllegalArgumentException("the queryClass is null");
            }

            return new Builder<T>(queryClass);
		}

        public Class<T> getQueryClass() {
			return this.queryClass;
		}

		public Integer getStartAtIndex() {
			return this.startAtIndex;
		}

		public void setStartAtIndex(Integer startAtIndex) {
                this.startAtIndex = startAtIndex;
		}

		public Integer getMaxResults() {
			return this.maxResults;
		}

		public void setMaxResults(Integer maxResults) {
			this.maxResults = maxResults;
		}

		public CountFlag getCountFlag() {
			return this.countFlag;
		}

		public void setCountFlag(CountFlag countFlag) {
			if (countFlag == null) {
                throw new IllegalArgumentException("countFlag was null");
            }

            this.countFlag = countFlag;
		}

		public Predicate[] getPredicates() {
			if (this.predicates == null) {
                return null;
            }

			//defensive copies on array
            return Arrays.copyOf(predicates, predicates.length);
		}

        /**
         * Sets the predicats. If multiple predicates are specified then they are wrapped
         * in an "and" predicate. If a null predicate is specified then there will be no
         * constrainsts on the query.
         * @param predicates the predicates to set.
         */
        public void setPredicates(Predicate... predicates) {
            //defensive copies on array
            this.predicates = predicates != null ? Arrays.copyOf(predicates, predicates.length) : null;
		}
	}

	/**
	 * Defines some internal constants used on this class.
	 */
	static class Constants {
		final static String ROOT_ELEMENT_NAME = "queryByCriteria";
		final static String TYPE_NAME = "QueryByCriteriaType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}

	/**
	 * A private class which exposes constants which define the XML element
	 * names to use when this object is marshaled to XML.
	 */
	static class Elements {
		final static String PREDICATE = "predicate";
		final static String START_AT_INDEX = "startAtIndex";
		final static String MAX_RESULTS = "maxResults";
		final static String COUNT_FLAG = "countFlag";
        final static String QUERY_CLASS = "queryClass";
	}

}
