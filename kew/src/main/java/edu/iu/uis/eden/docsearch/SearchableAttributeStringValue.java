/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.docsearch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.WorkflowPersistable;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SearchableAttributeStringValue implements WorkflowPersistable, SearchableAttributeValue {

    private static final long serialVersionUID = 8696089933682052078L;

    private static final String ATTRIBUTE_DATABASE_TABLE_NAME = "EN_DOC_HDR_EXT_T";
    private static final boolean DEFAULT_WILDCARD_ALLOWANCE_POLICY = true;
    private static final boolean ALLOWS_RANGE_SEARCH = false;
    private static final boolean ALLOWS_CASE_INSENSITIVE_SEARCH = true;
    private static final String ATTRIBUTE_XML_REPRESENTATION = SearchableAttribute.DATA_TYPE_STRING;

    private Long searchableAttributeValueId;
    private String searchableAttributeKey;
    private String searchableAttributeValue;
    protected String ojbConcreteClass; // attribute needed for OJB polymorphism - do not alter!

    private Long routeHeaderId;
    private DocumentRouteHeaderValue routeHeader;

    /**
     * Default constructor.
     */
    public SearchableAttributeStringValue() {
    	super();
        this.ojbConcreteClass = this.getClass().getName();
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#setupAttributeValue(java.lang.String)
     */
    public void setupAttributeValue(String value) {
    	this.setSearchableAttributeValue(value);
    }

	/* (non-Javadoc)
	 * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#setupAttributeValue(java.sql.ResultSet, java.lang.String)
	 */
	public void setupAttributeValue(ResultSet resultSet, String columnName) throws SQLException {
		this.setSearchableAttributeValue(resultSet.getString(columnName));
	}

	/* (non-Javadoc)
	 * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#getSearchableAttributeDisplayValue()
	 */
    public String getSearchableAttributeDisplayValue() {
        return getSearchableAttributeValue();
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#getSearchableAttributeDisplayValue(java.util.Map)
     */
    public String getSearchableAttributeDisplayValue(Map<String,String> displayParameters) {
        return getSearchableAttributeDisplayValue();
    }

	/* (non-Javadoc)
	 * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#getAttributeDataType()
	 */
	public String getAttributeDataType() {
		return ATTRIBUTE_XML_REPRESENTATION;
	}

	/* (non-Javadoc)
	 * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#getAttributeTableName()
	 */
	public String getAttributeTableName() {
		return ATTRIBUTE_DATABASE_TABLE_NAME;
	}

    /* (non-Javadoc)
	 * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#allowsWildcardsByDefault()
	 */
	public boolean allowsWildcards() {
		return DEFAULT_WILDCARD_ALLOWANCE_POLICY;
	}

    /* (non-Javadoc)
	 * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#allowsCaseInsensitivity()
	 */
	public boolean allowsCaseInsensitivity() {
		return ALLOWS_CASE_INSENSITIVE_SEARCH;
	}

    /* (non-Javadoc)
	 * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#allowsRangeSearches()
	 */
	public boolean allowsRangeSearches() {
		return ALLOWS_RANGE_SEARCH;
	}

	/**
	 * @return true
	 *
	 * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#isPassesDefaultValidation()
	 */
	public boolean isPassesDefaultValidation(String valueEntered) {
        // TODO delyea - length check needed?
		return true;
	}

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.docsearch.SearchableAttributeValue#isRangeValid(java.lang.String, java.lang.String)
     */
    public Boolean isRangeValid(String lowerValue, String upperValue) {
        if (allowsRangeSearches()) {
            return true;
        }
        return null;
    }

	public String getOjbConcreteClass() {
		return ojbConcreteClass;
	}

	public void setOjbConcreteClass(String ojbConcreteClass) {
		this.ojbConcreteClass = ojbConcreteClass;
	}

	public DocumentRouteHeaderValue getRouteHeader() {
		return routeHeader;
	}

	public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
		this.routeHeader = routeHeader;
	}

	public Long getRouteHeaderId() {
		return routeHeaderId;
	}

	public void setRouteHeaderId(Long routeHeaderId) {
		this.routeHeaderId = routeHeaderId;
	}

	public String getSearchableAttributeKey() {
		return searchableAttributeKey;
	}

	public void setSearchableAttributeKey(String searchableAttributeKey) {
		this.searchableAttributeKey = searchableAttributeKey;
	}

	public String getSearchableAttributeValue() {
		return searchableAttributeValue;
	}

	public void setSearchableAttributeValue(String searchableAttributeValue) {
		this.searchableAttributeValue = searchableAttributeValue;
	}

	public Long getSearchableAttributeValueId() {
		return searchableAttributeValueId;
	}

	public void setSearchableAttributeValueId(Long searchableAttributeValueId) {
		this.searchableAttributeValueId = searchableAttributeValueId;
	}

	/* (non-Javadoc)
     * @see edu.iu.uis.eden.WorkflowPersistable#copy(boolean)
     */
    public Object copy(boolean preserveKeys) {
        return null;
    }
}
