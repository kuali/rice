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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SqlBuilder;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeDateTime;
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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@Table(name="KREW_DOC_HDR_EXT_DT_T")
@NamedQueries({
	@NamedQuery(name="SearchableAttributeDateTimeValue.FindByDocumentId", query="select s from "
        + "SearchableAttributeDateTimeValue as s where s.documentId = :documentId"),
@NamedQuery(name="SearchableAttributeDateTimeValue.FindByKey", query="select s from "
        + "SearchableAttributeDateTimeValue as s where s.documentId = :documentId and "
        + "s.searchableAttributeKey = :searchableAttributeKey")
})
@AttributeOverrides({
        @AttributeOverride(name="searchableAttributeValueId", column=@Column(name="DOC_HDR_EXT_DT_ID"))
})
public class SearchableAttributeDateTimeValue extends SearchableAttributeBase implements SearchableAttributeValue, Serializable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SearchableAttributeDateTimeValue.class);

    private static final long serialVersionUID = 3045621112943214772L;

    private static final String ATTRIBUTE_DATABASE_TABLE_NAME = "KREW_DOC_HDR_EXT_DT_T";
    private static final boolean DEFAULT_WILDCARD_ALLOWANCE_POLICY = false;
    private static final boolean ALLOWS_RANGE_SEARCH = true;
    private static final boolean ALLOWS_CASE_INSENSITIVE_SEARCH = false;
    private static final String ATTRIBUTE_XML_REPRESENTATION = KewApiConstants.SearchableAttributeConstants.DATA_TYPE_DATE;

	@Column(name="VAL")
	private Timestamp searchableAttributeValue;

    /**
     * Default constructor.
     */
    public SearchableAttributeDateTimeValue() {
        super();
        this.ojbConcreteClass = this.getClass().getName();
    }

    public void setupAttributeValue(String value) {
        this.setSearchableAttributeValue(convertStringToTimestamp(value));
    }

    private Timestamp convertStringToTimestamp(String value) {
        if (org.apache.commons.lang.StringUtils.isEmpty(value)) {
            return null;
        } else {
            Timestamp t;
            try {
            	t = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(value);
            } catch (ParseException e) {
            	t = null;
            }
            if (t == null) {
                String errorMsg = "Error converting timestamp value '" + value + "' to valid timestamp object.";
                LOG.error("setupAttributeValue() " + errorMsg);
                throw new RuntimeException(errorMsg);
            }
            return t;
        }
    }

    @Override
	public void setupAttributeValue(ResultSet resultSet, String columnName) throws SQLException {
		Calendar c = Calendar.getInstance();
		c.clear(Calendar.HOUR);
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		this.setSearchableAttributeValue(resultSet.getTimestamp(columnName, c));
	}

    @Override
    public String getSearchableAttributeDisplayValue() {
        return formatAttributeValue(null);
    }

    private String formatAttributeValue(String formatPattern) {
        DateFormat df = getDateFormatToUse(formatPattern);
        return df.format(new Date(getSearchableAttributeValue().getTime()));
    }

    private DateFormat getDateFormatToUse(String parameterFormatPattern) {
        if (StringUtils.isNotBlank(parameterFormatPattern)) {
            return new SimpleDateFormat(parameterFormatPattern);
        }
        return RiceConstants.getDefaultDateFormat();
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
    public boolean isPassesDefaultValidation(String valueEntered) {
    	return new SqlBuilder().isValidDate(valueEntered);
        //return (DocSearchUtils.getEntryFormattedDate(valueEntered) != null);
    }

    @Override
    public Boolean isRangeValid(String lowerValue, String upperValue) {
        if (allowsRangeSearches()) {
            Timestamp lowerTime = convertStringToTimestamp(lowerValue);
            Timestamp upperTime = convertStringToTimestamp(upperValue);
            if ( (lowerTime != null) && (upperTime != null) ) {
                return (lowerTime.compareTo(upperTime) <= 0);
            }
            return true;
        }
        return null;
    }

    @Override
    public Timestamp getSearchableAttributeValue() {
        return searchableAttributeValue;
    }

    public void setSearchableAttributeValue(Timestamp searchableAttributeValue) {
        this.searchableAttributeValue = searchableAttributeValue;
    }

    @Override
    public DocumentAttributeDateTime toDocumentAttribute() {
        DateTime dateTime = null;
        if (getSearchableAttributeValue() != null) {
            dateTime = new DateTime(getSearchableAttributeValue().getTime());
        }
        return DocumentAttributeFactory.createDateTimeAttribute(getSearchableAttributeKey(), dateTime);
    }

}

