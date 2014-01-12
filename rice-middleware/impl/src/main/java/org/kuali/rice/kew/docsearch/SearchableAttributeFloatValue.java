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
package org.kuali.rice.kew.docsearch;

import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeDecimal;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeFactory;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@Table(name="KREW_DOC_HDR_EXT_FLT_T")
@NamedQueries({
	@NamedQuery(name="SearchableAttributeFloatValue.FindByDocumentId", query="select s from "
            + "SearchableAttributeFloatValue as s where s.documentId = :documentId"),
	@NamedQuery(name="SearchableAttributeFloatValue.FindByKey", query="select s from "
            + "SearchableAttributeFloatValue as s where s.documentId = :documentId and "
            + "s.searchableAttributeKey = :searchableAttributeKey")
})
@AttributeOverrides({
        @AttributeOverride(name="searchableAttributeValueId", column=@Column(name="DOC_HDR_EXT_FLT_ID"))
})
public class SearchableAttributeFloatValue extends SearchableAttributeNumericBase implements SearchableAttributeValue, Serializable {

    private static final long serialVersionUID = -6682101853805320760L;

    private static final String ATTRIBUTE_DATABASE_TABLE_NAME = "KREW_DOC_HDR_EXT_FLT_T";
    private static final boolean DEFAULT_WILDCARD_ALLOWANCE_POLICY = false;
    private static final boolean ALLOWS_RANGE_SEARCH = true;
    private static final boolean ALLOWS_CASE_INSENSITIVE_SEARCH = false;
    private static final String ATTRIBUTE_XML_REPRESENTATION = KewApiConstants.SearchableAttributeConstants.DATA_TYPE_FLOAT;
    private static final String DEFAULT_FORMAT_PATTERN = "";

    private static final String DEFAULT_VALIDATION_REGEX_EXPRESSION = "[-+]?[0-9]*\\.?[0-9]+";
    private static final Pattern defaultValidationPattern = Pattern.compile(DEFAULT_VALIDATION_REGEX_EXPRESSION);

    @Column(name="VAL")
	private BigDecimal searchableAttributeValue;

    /**
     * Default constructor.
     */
    public SearchableAttributeFloatValue() {
        super();
        this.ojbConcreteClass = this.getClass().getName();
    }

    @Override
    public void setupAttributeValue(String value) {
        this.setSearchableAttributeValue(convertStringToBigDecimal(value));
    }

    private BigDecimal convertStringToBigDecimal(String value) {
        if (org.apache.commons.lang.StringUtils.isEmpty(value)) {
            return null;
        } else {
            return new BigDecimal(value);
        }
    }

    @Override
	public void setupAttributeValue(ResultSet resultSet, String columnName) throws SQLException {
		this.setSearchableAttributeValue(resultSet.getBigDecimal(columnName));
	}

    @Override
    public String getSearchableAttributeDisplayValue() {
	    NumberFormat format = DecimalFormat.getInstance();
	    ((DecimalFormat)format).toPattern();
	    ((DecimalFormat)format).applyPattern(DEFAULT_FORMAT_PATTERN);
	    return format.format(getSearchableAttributeValue().doubleValue());
	}

    @Override
	public String getAttributeDataType() {
		return ATTRIBUTE_XML_REPRESENTATION;
	}

    @Override
	public String getAttributeTableName() {
		return ATTRIBUTE_DATABASE_TABLE_NAME;
	}

    @Override
	public boolean allowsWildcards() {
		return DEFAULT_WILDCARD_ALLOWANCE_POLICY;
	}

    @Override
	public boolean allowsCaseInsensitivity() {
		return ALLOWS_CASE_INSENSITIVE_SEARCH;
	}

    @Override
	public boolean allowsRangeSearches() {
		return ALLOWS_RANGE_SEARCH;
	}

    @Override
    public Boolean isRangeValid(String lowerValue, String upperValue) {
        if (allowsRangeSearches()) {
            BigDecimal lower = null;
            BigDecimal upper = null;
            try{
            	lower = convertStringToBigDecimal(lowerValue);
            	upper = convertStringToBigDecimal(upperValue);
            }catch(NumberFormatException ex){
            	return false;
            }
            if ( (lower != null) && (upper != null) ) {
                return (lower.compareTo(upper) <= 0);
            }
            return true;
        }
        return null;
    }

    @Override
    public BigDecimal getSearchableAttributeValue() {
        return searchableAttributeValue;
    }

    public void setSearchableAttributeValue(BigDecimal searchableAttributeValue) {
        this.searchableAttributeValue = searchableAttributeValue;
    }

    /**
     * @deprecated USE method setSearchableAttributeValue(BigDecimal) instead
     */
    @Deprecated
    public void setSearchableAttributeValue(Float floatValueToTranslate) {
        this.searchableAttributeValue = null;
        if (floatValueToTranslate != null) {
            this.searchableAttributeValue = new BigDecimal(floatValueToTranslate.toString());
        }
    }

    @Override
    public DocumentAttributeDecimal toDocumentAttribute() {
        return DocumentAttributeFactory.createDecimalAttribute(getSearchableAttributeKey(), getSearchableAttributeValue());
    }

    @Override
    protected Pattern getDefaultValidationPattern() {
        return defaultValidationPattern;
    }
}

