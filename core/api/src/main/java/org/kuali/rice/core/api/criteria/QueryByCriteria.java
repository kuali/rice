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

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

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
		QueryByCriteria.Elements.CRITERIA,
		QueryByCriteria.Elements.START_AT_INDEX,
		QueryByCriteria.Elements.MAX_RESULTS,
		QueryByCriteria.Elements.COUNT_FLAG,
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public final class QueryByCriteria<T> implements ModelObjectComplete {

	private static final long serialVersionUID = 2210627777648920180L;

	@XmlElement(name = Elements.CRITERIA, required = true)
	private final Criteria criteria;
	
	@XmlElement(name = Elements.START_AT_INDEX, required = false)
	private final Integer startAtIndex;
		
	@XmlElement(name = Elements.MAX_RESULTS, required = false)
	private final Integer maxResults;
	
	@XmlElement(name = Elements.COUNT_FLAG, required = true)
	private final CountFlag countFlag;

	@SuppressWarnings("unused")
	@XmlAnyElement
	private final Collection<Element> _futureElements = null;

	private QueryByCriteria() {
		this.criteria = null;
		this.startAtIndex = null;
		this.maxResults = null;
		this.countFlag = null;
	}

	private QueryByCriteria(Builder<T> builder) {
		this.criteria = builder.getCriteriaBuilder().build();
		this.startAtIndex = builder.getStartAtIndex();
		this.maxResults = builder.getMaxResults();
		this.countFlag = builder.getCountFlag();
	}

	/**
	 * Returns the {@link Criteria} which will be used to execute the query.
	 * 
	 * @return the criteria defined on the query, should never be null
	 */
	public Criteria getCriteria() {
		return this.criteria;
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
	 * many as it can.
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

		private final CriteriaBuilder<T> criteriaBuilder;
		private Integer startAtIndex;
		private Integer maxResults;
		private boolean includeCount;
		private CountFlag countFlag;

		private Builder(Class<T> queryClass) {
			this.criteriaBuilder = CriteriaBuilder.newCriteriaBuilder(queryClass);
			this.countFlag = CountFlag.NONE;
		}

		public static <T> Builder<T> create(Class<T> queryClass) {
			return new Builder<T>(queryClass);
		}

		public QueryByCriteria<T> build() {
			return new QueryByCriteria<T>(this);
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

		public boolean isIncludeCount() {
			return this.includeCount;
		}

		public void setIncludeCount(boolean includeCount) {
			this.includeCount = includeCount;
		}

		public CountFlag getCountFlag() {
			return this.countFlag;
		}

		public void setCountFlag(CountFlag countFlag) {
			this.countFlag = countFlag;
		}

		public CriteriaBuilder<T> getCriteriaBuilder() {
			return this.criteriaBuilder;
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
		final static String CRITERIA = "criteria";
		final static String START_AT_INDEX = "startAtIndex";
		final static String MAX_RESULTS = "maxResults";
		final static String COUNT_FLAG = "countFlag";
	}

}
