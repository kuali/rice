/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeFactory;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeInteger;
import org.kuali.rice.kew.service.KEWServiceLocator;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@Table(name="KREW_DOC_HDR_EXT_LONG_T")
//@Sequence(name="KREW_SRCH_ATTR_S",property="searchableAttributeValueId")
@NamedQueries({
	@NamedQuery(name="SearchableAttributeLongValue.FindByDocumentId", query="select s from "
            + "SearchableAttributeLongValue as s where s.documentId = :documentId"),
	@NamedQuery(name="SearchableAttributeLongValue.FindByKey", query="select s from "
            + "SearchableAttributeLongValue as s where s.documentId = :documentId and "
            + "s.searchableAttributeKey = :searchableAttributeKey")
})
@AttributeOverrides({
        @AttributeOverride(name="searchableAttributeValueId", column=@Column(name="DOC_HDR_EXT_LONG_ID"))
})
public class SearchableAttributeLongValue extends SearchableAttributeBase implements SearchableAttributeValue, Serializable {

    private static final long serialVersionUID = 5786144436732198346L;

    private static final String ATTRIBUTE_DATABASE_TABLE_NAME = "KREW_DOC_HDR_EXT_LONG_T";
    private static final boolean DEFAULT_WILDCARD_ALLOWANCE_POLICY = false;
    private static final boolean ALLOWS_RANGE_SEARCH = true;
    private static final boolean ALLOWS_CASE_INSENSITIVE_SEARCH = false;
    private static final String DEFAULT_VALIDATION_REGEX_EXPRESSION = "^-?[0-9]+$";
    private static final String ATTRIBUTE_XML_REPRESENTATION = KewApiConstants.SearchableAttributeConstants.DATA_TYPE_LONG;
    private static final String DEFAULT_FORMAT_PATTERN = "#";

    @Id
    @GeneratedValue(generator="KREW_SRCH_ATTR_S")
	@Column(name="DOC_HDR_EXT_LONG_ID")
	private String searchableAttributeValueId;
    @Column(name="VAL")
	private Long searchableAttributeValue;

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
        if (org.apache.commons.lang.StringUtils.isEmpty(value)) {
            return null;
        } else {
            return Long.valueOf(value.trim());
        }
    }

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#setupAttributeValue(java.sql.ResultSet, java.lang.String)
	 */
	public void setupAttributeValue(ResultSet resultSet, String columnName) throws SQLException {
		this.setSearchableAttributeValue(resultSet.getLong(columnName));
	}

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.docsearch.SearchableAttributeValue#getSearchableAttributeDisplayValue(java.util.Map)
     */
    public String getSearchableAttributeDisplayValue() {
        NumberFormat format = DecimalFormat.getInstance();
        ((DecimalFormat)format).applyPattern(DEFAULT_FORMAT_PATTERN);
        return format.format(getSearchableAttributeValue().longValue());
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

    	boolean bRet = true;
    	boolean bSplit = false;

		if (StringUtils.contains(valueEntered, SearchOperator.BETWEEN.op())) {
			List<String> l = Arrays.asList(valueEntered.split("\\.\\."));
			for(String value : l){
				bSplit = true;
				if(!isPassesDefaultValidation(value)){
					bRet = false;
				}
			}
		}
		if (StringUtils.contains(valueEntered, SearchOperator.OR.op())) {
			//splitValueList.addAll(Arrays.asList(StringUtils.split(valueEntered, KRADConstants.OR_LOGICAL_OPERATOR)));
			List<String> l = Arrays.asList(StringUtils.split(valueEntered, SearchOperator.OR.op()));
			for(String value : l){
				bSplit = true;
				if(!isPassesDefaultValidation(value)){
					bRet = false;
				}
			}
		}
		if (StringUtils.contains(valueEntered, SearchOperator.AND.op())) {
			//splitValueList.addAll(Arrays.asList(StringUtils.split(valueEntered, KRADConstants.AND_LOGICAL_OPERATOR)));
			List<String> l = Arrays.asList(StringUtils.split(valueEntered, SearchOperator.AND.op()));
			for(String value : l){
				bSplit = true;
				if(!isPassesDefaultValidation(value)){
					bRet = false;
				}
			}
		}

		if(bSplit){
			return bRet;
		}

		Pattern pattern = Pattern.compile(DEFAULT_VALIDATION_REGEX_EXPRESSION);
		Matcher matcher = pattern.matcher(SQLUtils.cleanNumericOfValidOperators(valueEntered).trim());
		if(!matcher.matches()){
			bRet = false;
		}

		return bRet;

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

    @Override
    public Long getSearchableAttributeValue() {
        return searchableAttributeValue;
    }

    public void setSearchableAttributeValue(Long searchableAttributeValue) {
        this.searchableAttributeValue = searchableAttributeValue;
    }

    public String getSearchableAttributeValueId() {
        return searchableAttributeValueId;
    }

    public void setSearchableAttributeValueId(String searchableAttributeValueId) {
        this.searchableAttributeValueId = searchableAttributeValueId;
    }

	//@PrePersist
	public void beforeInsert(){
		OrmUtils.populateAutoIncValue(this, KEWServiceLocator.getEntityManagerFactory().createEntityManager());
	}

    @Override
    public DocumentAttributeInteger toDocumentAttribute() {
        BigInteger integer = null;
        if (getSearchableAttributeValue() != null) {
            integer = BigInteger.valueOf(getSearchableAttributeValue().longValue());
        }
        return DocumentAttributeFactory.createIntegerAttribute(getSearchableAttributeKey(), integer);
    }

}

