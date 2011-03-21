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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.mo.ModelObjectComplete;
import org.w3c.dom.Element;

/**
 * TODO
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@XmlRootElement(name = QueryByCriteria.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = QueryByCriteria.Constants.TYPE_NAME, propOrder = {
		QueryByCriteria.Elements.CRITERIA,
		QueryByCriteria.Elements.START_AT_INDEX,
		QueryByCriteria.Elements.END_AT_INDEX,
		QueryByCriteria.Elements.MAX_RESULTS,
		QueryByCriteria.Elements.COUNT_FLAG,
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public final class QueryByCriteria<T> implements ModelObjectComplete {

	private static final long serialVersionUID = 2210627777648920180L;

	private final Criteria criteria;
	private final Integer startAtIndex;
	private final Integer endAtIndex;
	private final Integer maxResults;
	private final CountFlag countFlag;

	@SuppressWarnings("unused")
	@XmlAnyElement
	private final Collection<Element> _futureElements = null;

	private QueryByCriteria() {
		this.criteria = null;
		this.startAtIndex = null;
		this.endAtIndex = null;
		this.maxResults = null;
		this.countFlag = null;
	}

	private QueryByCriteria(Builder<T> builder) {
		this.criteria = builder.getCriteriaBuilder().build();
		this.startAtIndex = builder.getStartAtIndex();
		this.endAtIndex = builder.getEndAtIndex();
		this.maxResults = builder.getMaxResults();
		this.countFlag = builder.getCountFlag();
	}

	public Criteria getCriteria() {
		return this.criteria;
	}

	public Integer getStartAtIndex() {
		return this.startAtIndex;
	}

	public Integer getEndAtIndex() {
		return this.endAtIndex;
	}

	public Integer getMaxResults() {
		return this.maxResults;
	}

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
		private Integer endAtIndex;
		private Integer maxResults;
		private boolean includeCount;
		private CountFlag countFlag;

		private Builder(Class<T> queryClass) {
			this.criteriaBuilder = CriteriaBuilder
					.newCriteriaBuilder(queryClass);
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

		public Integer getEndAtIndex() {
			return this.endAtIndex;
		}

		public void setEndAtIndex(Integer endAtIndex) {
			this.endAtIndex = endAtIndex;
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
		final static String END_AT_INDEX = "endAtIndex";
		final static String MAX_RESULTS = "maxResults";
		final static String COUNT_FLAG = "countFlag";
	}

}
