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
package org.kuali.rice.kew.docsearch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.bo.WorkflowPersistable;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.util.Utilities;


/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_DOC_HDR_EXT_LONG_T")
public class SearchableAttributeLongValue implements WorkflowPersistable, SearchableAttributeValue {

    private static final long serialVersionUID = 5786144436732198346L;

    private static final String ATTRIBUTE_DATABASE_TABLE_NAME = "KREW_DOC_HDR_EXT_LONG_T";
    private static final boolean DEFAULT_WILDCARD_ALLOWANCE_POLICY = false;
    private static final boolean ALLOWS_RANGE_SEARCH = true;
    private static final boolean ALLOWS_CASE_INSENSITIVE_SEARCH = false;
    private static final String DEFAULT_VALIDATION_REGEX_EXPRESSION = "^[0-9]+$";
    private static final String ATTRIBUTE_XML_REPRESENTATION = SearchableAttribute.DATA_TYPE_LONG;
    private static final String DEFAULT_FORMAT_PATTERN = "#";

    @Id
	@Column(name="DOC_HDR_EXT_LONG_ID")
	private Long searchableAttributeValueId;
    @Column(name="KEY_CD")
	private String searchableAttributeKey;
    @Column(name="VAL")
	private Long searchableAttributeValue;
    @Transient
    protected String ojbConcreteClass; // attribute needed for OJB polymorphism - do not alter!

    @Column(name="DOC_HDR_ID")
	private Long routeHeaderId;
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="DOC_HDR_ID", insertable=false, updatable=false)
	private DocumentRouteHeaderValue routeHeader;

    /**
     * Default constructor.
     */
    public SearchableAttributeLongValue() {
        super();
        this.ojbConcreteClass = this.getClass().getName();
    }

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#setupAttributeValue(java.lang.String)
     */
    public void setupAttributeValue(String value) {
        this.setSearchableAttributeValue(convertStringToLong(value));
    }

    private Long convertStringToLong(String value) {
        if (Utilities.isEmpty(value)) {
            return null;
        } else {
            return Long.valueOf(value);
        }
    }

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#setupAttributeValue(java.sql.ResultSet, java.lang.String)
	 */
	public void setupAttributeValue(ResultSet resultSet, String columnName) throws SQLException {
		this.setSearchableAttributeValue(resultSet.getLong(columnName));
	}

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#getSearchableAttributeDisplayValue()
	 */
    public String getSearchableAttributeDisplayValue() {
        return getSearchableAttributeDisplayValue(new HashMap<String,String>());
    }

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#getSearchableAttributeDisplayValue(java.util.Map)
     */
    public String getSearchableAttributeDisplayValue(Map<String,String> displayParameters) {
        NumberFormat format = DecimalFormat.getInstance();
        ((DecimalFormat)format).applyPattern(getFormatPatternToUse(displayParameters.get(DISPLAY_FORMAT_PATTERN_MAP_KEY)));
        return format.format(getSearchableAttributeValue().longValue());
    }

    private String getFormatPatternToUse(String parameterFormatPattern) {
        if (StringUtils.isNotBlank(parameterFormatPattern)) {
            return parameterFormatPattern;
        }
        return DEFAULT_FORMAT_PATTERN;
    }

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#getAttributeDataType()
	 */
	public String getAttributeDataType() {
		return ATTRIBUTE_XML_REPRESENTATION;
	}

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#getAttributeTableName()
	 */
	public String getAttributeTableName() {
		return ATTRIBUTE_DATABASE_TABLE_NAME;
	}

    /* (non-Javadoc)
	 * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#allowsWildcardsByDefault()
	 */
	public boolean allowsWildcards() {
		return DEFAULT_WILDCARD_ALLOWANCE_POLICY;
	}

    /* (non-Javadoc)
	 * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#allowsCaseInsensitivity()
	 */
	public boolean allowsCaseInsensitivity() {
		return ALLOWS_CASE_INSENSITIVE_SEARCH;
	}

    /* (non-Javadoc)
	 * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#allowsRangeSearches()
	 */
	public boolean allowsRangeSearches() {
		return ALLOWS_RANGE_SEARCH;
	}

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#isPassesDefaultValidation()
	 */
	public boolean isPassesDefaultValidation(String valueEntered) {
		Pattern pattern = Pattern.compile(DEFAULT_VALIDATION_REGEX_EXPRESSION);
		Matcher matcher = pattern.matcher(valueEntered);
		return (matcher.matches());
	}

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#isRangeValid(java.lang.String, java.lang.String)
     */
    public Boolean isRangeValid(String lowerValue, String upperValue) {
        if (allowsRangeSearches()) {
            Long lower = convertStringToLong(lowerValue);
            Long upper = convertStringToLong(upperValue);
            if ( (lower != null) && (upper != null) ) {
                return (lower.compareTo(upper) <= 0);
            }
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

    public Long getSearchableAttributeValue() {
        return searchableAttributeValue;
    }

    public void setSearchableAttributeValue(Long searchableAttributeValue) {
        this.searchableAttributeValue = searchableAttributeValue;
    }

    public Long getSearchableAttributeValueId() {
        return searchableAttributeValueId;
    }

    public void setSearchableAttributeValueId(Long searchableAttributeValueId) {
        this.searchableAttributeValueId = searchableAttributeValueId;
    }

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.WorkflowPersistable#copy(boolean)
     */
    public Object copy(boolean preserveKeys) {
        return null;
    }
}

