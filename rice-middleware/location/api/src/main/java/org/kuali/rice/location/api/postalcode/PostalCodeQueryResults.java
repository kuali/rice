/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.location.api.postalcode;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = PostalCodeQueryResults.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = PostalCodeQueryResults.Constants.TYPE_NAME, propOrder = {
		PostalCodeQueryResults.Elements.RESULTS,
		PostalCodeQueryResults.Elements.TOTAL_ROW_COUNT,
		PostalCodeQueryResults.Elements.MORE_RESULTS_AVAILALBE,
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public class PostalCodeQueryResults extends AbstractDataTransferObject implements QueryResults<PostalCode> {

	@XmlElementWrapper(name = Elements.RESULTS, required = false)
	@XmlElement(name = Elements.RESULT_ELEM, required = false)
	private final List<PostalCode> results;

	@XmlElement(name = Elements.TOTAL_ROW_COUNT, required = false)
	private final Integer totalRowCount;

	@XmlElement(name = Elements.MORE_RESULTS_AVAILALBE, required = true)
	private final boolean moreResultsAvailable;

	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

	private PostalCodeQueryResults() {
		this.results = null;
		this.totalRowCount = null;
		this.moreResultsAvailable = false;
	}

	private PostalCodeQueryResults(Builder builder) {
		final List<PostalCode> temp = new ArrayList<PostalCode>();
        for (PostalCode.Builder b : builder.getResults()) {
            if (b != null) {
                temp.add(b.build());
            }
        }

        this.results = Collections.unmodifiableList(temp);
		this.totalRowCount = builder.getTotalRowCount();
		this.moreResultsAvailable = builder.isMoreResultsAvailable();
	}

	@Override
	public List<PostalCode> getResults() {
		return results;
	}
	
	@Override
	public Integer getTotalRowCount() {
		return totalRowCount;
	}

	@Override
	public boolean isMoreResultsAvailable() {
		return moreResultsAvailable;
	}

	public static class Builder implements ModelBuilder, QueryResults<PostalCode.Builder> {

		private List<PostalCode.Builder> results;
		private Integer totalRowCount;
		private boolean moreResultsAvailable;

        public static Builder create() {
            return new Builder();
        }

		private Builder() {
			this.results = new ArrayList<PostalCode.Builder>();
			this.moreResultsAvailable = false;
		}

        @Override
		public PostalCodeQueryResults build() {
			return new PostalCodeQueryResults(this);
		}

        @Override
		public List<PostalCode.Builder> getResults() {
			return Collections.unmodifiableList(this.results);
		}

		public void setResults(List<PostalCode.Builder> results) {
			this.results = new ArrayList<PostalCode.Builder>(results);
		}

        @Override
		public Integer getTotalRowCount() {
			return this.totalRowCount;
		}

		public void setTotalRowCount(Integer totalRowCount) {
			this.totalRowCount = totalRowCount;
		}

        @Override
		public boolean isMoreResultsAvailable() {
			return this.moreResultsAvailable;
		}

		public void setMoreResultsAvailable(boolean moreResultsAvailable) {
			this.moreResultsAvailable = moreResultsAvailable;
		}
		
	}
	
	/**
	 * Defines some internal constants used on this class.
	 */
	public static class Constants {
		public final static String ROOT_ELEMENT_NAME = "PostalCodeQueryResults";
		public final static String TYPE_NAME = "PostalCodeQueryResultsType";
	}

	/**
	 * A private class which exposes constants which define the XML element
	 * names to use when this object is marshaled to XML.
	 */
	public static class Elements {
		public final static String RESULTS = "results";
		public final static String RESULT_ELEM = "PostalCode";
		public final static String TOTAL_ROW_COUNT = "totalRowCount";
		public final static String MORE_RESULTS_AVAILALBE = "moreResultsAvailable";
	}
	
}
