/*
 * Copyright 2006-2011 The Kuali Foundation
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.framework.persistence.jdbc.sql.Criteria;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SqlBuilder;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.DocumentStatusCategory;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeFactory;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResult;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResults;
import org.kuali.rice.kew.api.document.lookup.RouteNodeLookupLogic;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StandardDocumentSearchGenerator implements DocumentSearchGenerator {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StandardDocumentSearchGenerator.class);

    private static final String ROUTE_NODE_TABLE = "KREW_RTE_NODE_T";
    private static final String ROUTE_NODE_INST_TABLE = "KREW_RTE_NODE_INSTN_T";
    private static final String DATABASE_WILDCARD_CHARACTER_STRING = "%";
    private static final char DATABASE_WILDCARD_CHARACTER = DATABASE_WILDCARD_CHARACTER_STRING.toCharArray()[0];

    private DatabasePlatform dbPlatform;
    private MessageMap messageMap;

    private SqlBuilder sqlBuilder = null;

    @Override
    public DocumentLookupCriteria clearSearch(DocumentLookupCriteria criteria) {
        return DocumentLookupCriteria.Builder.create().build();
    }

    public DocumentType getValidDocumentType(String documentTypeFullName) {
        if (!org.apache.commons.lang.StringUtils.isEmpty(documentTypeFullName)) {
            DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeFullName);
            if (documentType == null) {
                throw new RuntimeException("No Valid Document Type Found for document type name '" + documentTypeFullName + "'");
            }
            return documentType;
        }
        return null;
    }

    @Override
    public List<RemotableAttributeError> validateSearchableAttributes(DocumentLookupCriteria.Builder criteria) {
        List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
        for (String documentAttributeName : criteria.getDocumentAttributeValues().keySet()) {
            List<String> documentAttributeValues = criteria.getDocumentAttributeValues().get(documentAttributeName);
            if (CollectionUtils.isEmpty(documentAttributeValues)) {
                paramMap.put(documentAttributeName, Collections.<String>emptyList());
            } else {
                paramMap.put(documentAttributeName, documentAttributeValues);
            }
            DocumentType documentType = getValidDocumentType(criteria.getDocumentTypeName());
            if (documentType != null) {
               errors = KEWServiceLocator.getDocumentLookupCustomizationMediator().validateLookupFieldParameters(
                       documentType, paramMap);
            }
        }
        return errors == null ? Collections.<RemotableAttributeError>emptyList() : Collections.unmodifiableList(errors);
    }

    /**
     * Cleans upper bounds on an entire list of values.
     * @param stringDates list
     * @return list of dates
     */
    private static List<String> cleanUpperBounds(List<String> stringDates) {
        List<String> lRet = null;
        if(stringDates != null && !stringDates.isEmpty()){
            lRet = new ArrayList<String>();
            for(String stringDate:stringDates){
                lRet.add(cleanUpperBound(stringDate));
            }
        }
        return lRet;
    }

    /**
     * When dealing with upperbound dates, it is a business requirement that if a timestamp isn't already
     * stated append 23:59:59 to the end of the date.  This ensures that you are searching for the entire
     * day.
     * @param stringDate
     * @return upper bound date
     */
    private static String cleanUpperBound(String stringDate){
        try{
            java.sql.Timestamp dt = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(stringDate);
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

            if("00:00:00".equals(sdfTime.format(dt))){
                stringDate = stringDate + " 23:59:59";
            }
        } catch (Exception ex){
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_CUSTOM, new String[] { "Invalid Date Input: " + stringDate });
        }
        return stringDate;
    }


    /**
     *
     * This method takes in any valid date string, like <12/30/09 and convert
     * it into <12/30/09 23:59:59, but only for upper bound type values.
     *
     *  This method only really cares about .., <, <=. other operators
     *  are not evaluated.
     *
     * In order to do this it has to parse the inline date string
     *
     * @param propertyValue
     * @return
     */
    private static String parseAndConvertDateToRange(String propertyValue) {

        String sRet = propertyValue;

        if (StringUtils.contains(propertyValue, SearchOperator.BETWEEN.op())) {
            String[] rangeValues = propertyValue.split("\\.\\."); // this translate to the .. operator
            sRet = ObjectUtils.clean(rangeValues[0].trim()) + " " + SearchOperator.BETWEEN.op() + " " + cleanUpperBound(ObjectUtils.clean(rangeValues[1].trim()));
        }  else if (propertyValue.startsWith(SearchOperator.LESS_THAN_EQUAL.op())) {
            sRet = SearchOperator.LESS_THAN_EQUAL + cleanUpperBound(ObjectUtils.clean(propertyValue));
        }  else if (propertyValue.startsWith(SearchOperator.LESS_THAN.op())) {
            sRet = SearchOperator.LESS_THAN + cleanUpperBound(ObjectUtils.clean(propertyValue));
        }

        return sRet;
    }



    // TODO - Rice 2.0 - need to resurrect this code when dealing with range searches
    /**
     *
     * This method is intended to validate that the lower bound value is <= the upper bound value.  Although having a lower
     * value would not hurt the actual db query.  Only relevant to be called when you are dealing with Range Searchable Attributes
     *
     * If an error is found, it will throw a RuntimeException.
     *
     * @param lowerBound
     * @param upperBound
     */
//    private void validateBounds(SearchAttributeCriteriaComponent lowerBound, SearchAttributeCriteriaComponent upperBound){
//
//        Class type = getSearchableAttributeClass(lowerBound.getSearchableAttributeValue());
//        Class upperType = getSearchableAttributeClass(upperBound.getSearchableAttributeValue());
//
//        // Make sure they are of the same data type
//        if(type.getName().compareTo(upperType.getName()) != 0){
//            String err = "Type Mismatch. Must compare two like types";
//            LOG.error("validateBounds() " + err);
//            throw new RuntimeException(err);
//        }
//
//        String errorMsg = "The search attribute range values are out of order. Lower bound must be <= Upper Bound. ["
//            + lowerBound.getValue() + ", " + upperBound.getValue() + "] for type " + type.getName();
//
//        if(TypeUtils.isIntegralClass(type) || TypeUtils.isDecimalClass(type)){
//            // The clean numeric will work with both integer and float values
//            BigDecimal lVal = SqlBuilder.stringToBigDecimal(lowerBound.getValue());
//            BigDecimal uVal = SqlBuilder.stringToBigDecimal(upperBound.getValue());
//
//            if(lVal.compareTo(uVal) > 0){
//                LOG.error("validateBounds() " + errorMsg);
//                throw new RuntimeException(errorMsg);
//            }
//
//        }else if(TypeUtils.isTemporalClass(type)){
//            java.sql.Timestamp lVal = null;
//            java.sql.Timestamp uVal = null;
//            try{
//                lVal = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(lowerBound.getValue());
//                uVal = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(upperBound.getValue());
//            }catch(Exception ex){
//                LOG.error("validateBounds() " + errorMsg);
//                throw new RuntimeException(errorMsg, ex);
//            }
//
//            if(lVal.compareTo(uVal) > 0){
//                LOG.error("validateBounds() " + errorMsg);
//                throw new RuntimeException(errorMsg);
//            }
//
//        }else if(TypeUtils.isStringClass(type)){
//            // this is a complete edge case that should not be allowed to happen but
//            // the XSD states that it's valid.
//            if(lowerBound.isCaseSensitive() != upperBound.isCaseSensitive()){
//                LOG.warn("validateBounds(): Cannot Validate because mismatch case sensitivity ["
//                        + lowerBound.getValue() + ", " + upperBound.getValue() + "] for type " + type.getName());
//            }else if(lowerBound.isCaseSensitive()){
//                if(lowerBound.getValue().compareTo(upperBound.getValue()) > 0){
//                    LOG.error("validateBounds() " + errorMsg);
//                    throw new RuntimeException(errorMsg);
//                }
//            }else{
//                if(lowerBound.getValue().compareToIgnoreCase(upperBound.getValue()) > 0){
//                    LOG.error("validateBounds() " + errorMsg);
//                    throw new RuntimeException(errorMsg);
//                }
//            }
//        }
//    }

    // Rice 2.0 - do we need to resurrect this code anywhere or can we just remove it?
    /**
     *
     * This method takes in a list of searchable attributes and pulls out range componets, combines them
     * and adds them back into the original list.
     *
     *  for any upperbound that does not have a timestamp, add one that's 23:59:59
     *
     *  for non range attributes it checks to see if it's a standard no time date and converts
     *  it to a "between".
     *
     * so sa list: a, b, cR1, d, e, cR2
     * returns: a, b, cR,
     * @param documentAttributeValues the contents of this list can be altered
     *
     */
//    private void combineAndFormatDocumentAttributes(Map<String, List<String>> documentAttributeValues, List<RemotableAttributeField> searchFields) {
//
//
//        for (String documentAttributeName : documentAttributeValues) {
//
//        }
//
//        Map<String, List<SearchAttributeCriteriaComponent>> searchableAttributeRangeComponents = new HashMap<String, List<SearchAttributeCriteriaComponent>>();
//
//        for (SearchAttributeCriteriaComponent criteriaComponent : searchableAttributes)
//        {
//            if (!criteriaComponent.isSearchable())
//            {
//                continue;
//            }
//
//            SearchableAttributeValue searchAttribute = criteriaComponent.getSearchableAttributeValue();
//            if (searchAttribute == null)
//            {
//                // key given for propertyField must not be on document
//                String errorMsg = "The search attribute value associated with key '"
//                        + criteriaComponent.getSavedKey() + "' cannot be found";
//                LOG.error("getSearchableAttributeSql() " + errorMsg);
//                throw new RuntimeException(errorMsg);
//            }
//
//            Class clazz = getSearchableAttributeClass(searchAttribute);
//
//            if (criteriaComponent.isRangeSearch())
//            {
//
//                if (searchableAttributeRangeComponents.containsKey(criteriaComponent.getSavedKey()))
//                {
//                    List<SearchAttributeCriteriaComponent> criteriaComponents = searchableAttributeRangeComponents.get(criteriaComponent.getSavedKey());
//                    List<SearchAttributeCriteriaComponent> newCriteriaComponents = new ArrayList<SearchAttributeCriteriaComponent>();
//                    newCriteriaComponents.addAll(criteriaComponents);
//                    newCriteriaComponents.add(criteriaComponent);
//                    searchableAttributeRangeComponents.put(criteriaComponent.getSavedKey(), newCriteriaComponents);
//                } else
//                {
//                    searchableAttributeRangeComponents.put(criteriaComponent.getSavedKey(),
//                            Arrays.asList(criteriaComponent)
//                    );
//                }
//                // we need to make sure the dates are converted based on case.
//                // for upperbound
//                if (TypeUtils.isTemporalClass(clazz) && criteriaComponent.isComponentUpperBoundValue())
//                {
//                    criteriaComponent.setValue(cleanUpperBound(criteriaComponent.getValue()));
//                    criteriaComponent.setValues(cleanUpperBounds(criteriaComponent.getValues()));
//                }
//
//            } else
//            {
//                if (TypeUtils.isTemporalClass(clazz))
//                {
//                    criteriaComponent.setValue(criteriaComponent.getValue());
//                }
//            }
//        }
//
//        // we combined the attributes above into a map of lists. Now for each
//        // key, make one SA.
//        for (String keyName : searchableAttributeRangeComponents.keySet()) {
//            List<SearchAttributeCriteriaComponent> criteriaComponents = searchableAttributeRangeComponents
//                    .get(keyName);
//
//            SearchAttributeCriteriaComponent newComp = null;
//            SearchAttributeCriteriaComponent lowerBound = null;
//            SearchAttributeCriteriaComponent upperBound = null;
//
//            for (SearchAttributeCriteriaComponent component : criteriaComponents) {
//                if (component.isComponentLowerBoundValue()) {
//                    lowerBound = component;
//                } else if (component.isComponentUpperBoundValue()) {
//                    upperBound = component;
//                } else {
//                    String errorMsg = "The search attribute value associated with key '"
//                            + component.getSavedKey()
//                            + "' is not upper or lower bound";
//                    LOG.error("getSearchableAttributeSql() " + errorMsg);
//                    throw new RuntimeException(errorMsg);
//                }
//            }
//
//            // now we have both the upper and lower if they exist. lets make a
//            // new single component.
//            if (lowerBound != null && upperBound != null) { // between case
//
//                // we need to check and make sure a < b for range values
//                validateBounds(lowerBound, upperBound);
//
//                // we need to do this if the search is NOT inclusive. if
//                // that's the case then
//                // the between operator does not work.
//
//                lowerBound.setRangeSearch(false);
//                upperBound.setRangeSearch(false);
//                if (lowerBound.isSearchInclusive()) {
//                    lowerBound.setValue(SearchOperator.GREATER_THAN_EQUAL.op() + lowerBound.getValue());
//                } else {
//                    lowerBound.setValue(SearchOperator.GREATER_THAN.op() + lowerBound.getValue());
//                }
//                if (upperBound.isSearchInclusive()) {
//                    upperBound.setValue(SearchOperator.LESS_THAN_EQUAL.op() + upperBound.getValue());
//                } else {
//                    upperBound.setValue(SearchOperator.LESS_THAN.op() + upperBound.getValue());
//                }
//
//            } else if (lowerBound != null) {
//                newComp = new SearchAttributeCriteriaComponent(lowerBound
//                        .getFormKey(), null, false);
//                if (lowerBound.isSearchInclusive()) {
//                    newComp.setValue(SearchOperator.GREATER_THAN_EQUAL.op() + lowerBound.getValue());
//                } else {
//                    newComp.setValue(SearchOperator.GREATER_THAN.op() + lowerBound.getValue());
//                }
//                newComp.setSearchInclusive(lowerBound.isSearchInclusive());
//                newComp.setCaseSensitive(lowerBound.isCaseSensitive());
//                newComp.setAllowInlineRange(lowerBound.isAllowInlineRange());
//                newComp.setCanHoldMultipleValues(lowerBound
//                        .isCanHoldMultipleValues());
//                newComp.setLookupableFieldType(lowerBound
//                        .getLookupableFieldType());
//                newComp.setSearchable(true);
//                newComp.setSearchableAttributeValue(lowerBound
//                        .getSearchableAttributeValue());
//                newComp.setSavedKey(lowerBound.getSavedKey());
//                searchableAttributes.add(newComp);
//            } else if (upperBound != null) {
//                newComp = new SearchAttributeCriteriaComponent(upperBound
//                        .getFormKey(), null, false);
//                if (upperBound.isSearchInclusive()) {
//                    newComp.setValue(SearchOperator.LESS_THAN_EQUAL.op() + upperBound.getValue());
//                } else {
//                    newComp.setValue(SearchOperator.LESS_THAN.op() + upperBound.getValue());
//                }
//                newComp.setSearchInclusive(upperBound.isSearchInclusive());
//                newComp.setCaseSensitive(upperBound.isCaseSensitive());
//                newComp.setAllowInlineRange(upperBound.isAllowInlineRange());
//                newComp.setCanHoldMultipleValues(upperBound.isCanHoldMultipleValues());
//                newComp.setLookupableFieldType(upperBound.getLookupableFieldType());
//                newComp.setSearchable(true);
//                newComp.setSearchableAttributeValue(upperBound.getSearchableAttributeValue());
//                newComp.setSavedKey(upperBound.getSavedKey());
//                searchableAttributes.add(newComp);
//            }
//
//        }
//
//        // last step is to remove all range items from the list because we have
//        // just combined them into single elements
//        for (Iterator<SearchAttributeCriteriaComponent> iterator = searchableAttributes.iterator(); iterator
//                .hasNext();) {
//            SearchAttributeCriteriaComponent criteriaComponent = (SearchAttributeCriteriaComponent) iterator
//                    .next();
//            if (!criteriaComponent.isSearchable()) {
//                continue;
//            }
//
//            if (criteriaComponent.isRangeSearch()) {
//                iterator.remove();
//            }
//
//        }
//    }

    public QueryComponent getSearchableAttributeSql(Map<String, List<String>> documentAttributeValues, List<RemotableAttributeField> searchFields, String whereClausePredicatePrefix) {
        // this will massage the data and change all range attributes into std ones.
        // TODO - Rice 2.0 - commented out for now, not sure that we need to do this anymore?
        //combineAndFormatSearchableComponents(searchableAttributes);

        StringBuilder fromSql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();

        //Map<String, List<SearchAttributeCriteriaComponent>> searchableAttributeRangeComponents = new HashMap<String,List<SearchAttributeCriteriaComponent>>();
        Criteria finalCriteria = null;
        int tableIndex = 1;
        SqlBuilder sqlBuilder = this.getSqlBuilder();

        for (String documentAttributeName : documentAttributeValues.keySet()) {

            List<String> searchValues = documentAttributeValues.get(documentAttributeName);
            if (CollectionUtils.isEmpty(searchValues)) {
                continue;
            }

            String tableAlias = "EXT" + tableIndex;
            RemotableAttributeField searchField = getSearchFieldByName(documentAttributeName, searchFields);
            String tableName = DocumentLookupInternalUtils.getAttributeTableName(searchField);
            boolean caseSensitive = DocumentLookupInternalUtils.isLookupCaseSensitive(searchField);

            Criteria crit = null;

            Class<?> dataTypeClass = DocumentLookupInternalUtils.getDataTypeClass(searchField);
            if (searchValues.size() > 1) {
                // if there's more than one entry, we need to do an "in"
                crit = new Criteria(tableName, tableAlias);
                crit.setDbPlatform(sqlBuilder.getDbPlatform());
                crit.in("VAL", searchValues, dataTypeClass);
            } else {
                crit = sqlBuilder.createCriteria("VAL", searchValues.get(0) , tableName, tableAlias, dataTypeClass, !caseSensitive);
            }
            sqlBuilder.addCriteria("KEY_CD", documentAttributeName, String.class, false, false, crit); // this is always of type string.
            sqlBuilder.andCriteria("DOC_HDR_ID", tableAlias + ".DOC_HDR_ID", "KREW_DOC_HDR_T", "DOC_HDR", SqlBuilder.JoinType.class, false, false, crit);

            if (finalCriteria == null ){
                finalCriteria = crit;
            } else{
                sqlBuilder.andCriteria(finalCriteria, crit);
            }

            // - below is the old code
            // if where clause is empty then use passed in prefix... otherwise generate one
            String whereClausePrefix = (whereSql.length() == 0) ? whereClausePredicatePrefix : getGeneratedPredicatePrefix(whereSql.length());
            QueryComponent qc = generateSearchableAttributeSql(tableName, documentAttributeName, whereClausePrefix, tableIndex);
            fromSql.append(qc.getFromSql());
            tableIndex++;
        }

        if (finalCriteria == null) {
            return new QueryComponent("", "", "");
        }

        String whereClausePrefix = (whereSql.length() == 0) ? whereClausePredicatePrefix : getGeneratedPredicatePrefix(whereSql.length());

        return new QueryComponent("", fromSql.toString(), whereClausePrefix + " " + finalCriteria.buildWhere());
    }

    private RemotableAttributeField getSearchFieldByName(String fieldName, List<RemotableAttributeField> searchFields) {
        for (RemotableAttributeField searchField : searchFields) {
            if (searchField.getName().equals(fieldName)) {
                return searchField;
            }
        }
        throw new IllegalStateException("Failed to locate a RemotableAttributeField for fieldName=" + fieldName);
    }

    public QueryComponent generateSearchableAttributeSql(String tableName, String documentAttributeName, String whereSqlStarter,int tableIndex) {
        String tableIdentifier = "EXT" + tableIndex;
        String queryTableColumnName = tableIdentifier + ".VAL";
        QueryComponent joinSqlComponent = getSearchableAttributeJoinSql(tableName, tableIdentifier, whereSqlStarter, documentAttributeName);
        return new QueryComponent("", joinSqlComponent.getFromSql(), joinSqlComponent.getWhereSql());
    }

    // TODO - Rice 2.0 - need to resurrect this code when implementing proper support for range searches
//    public QueryComponent generateSearchableAttributeRangeSql(String searchAttributeKeyName, List<SearchAttributeCriteriaComponent> criteriaComponents,String whereSqlStarter,int tableIndex) {
//        StringBuilder fromSql = new StringBuilder();
//        StringBuilder whereSql = new StringBuilder();
//        boolean joinAlreadyPerformed = false;
//        String tableIdentifier = "EXT" + tableIndex;
//        String queryTableColumnName = tableIdentifier + ".VAL";
//
//        for (SearchAttributeCriteriaComponent criteriaComponent : criteriaComponents) {
//            if (!searchAttributeKeyName.equals(criteriaComponent.getSavedKey())) {
//                String errorMsg = "Key value of searchable attribute component with savedKey '" + criteriaComponent.getSavedKey() + "' does not match specified savedKey value '" + searchAttributeKeyName + "'";
//                LOG.error("generateSearchableAttributeRangeSql() " + errorMsg);
//                throw new RuntimeException(errorMsg);
//            }
//            if (!joinAlreadyPerformed) {
//                QueryComponent joinSqlComponent = getSearchableAttributeJoinSql(criteriaComponent.getSearchableAttributeValue(), tableIdentifier, whereSqlStarter, searchAttributeKeyName);
//                fromSql.append(joinSqlComponent.getFromSql());
//                whereSql.append(joinSqlComponent.getWhereSql());
//                joinAlreadyPerformed = true;
//            }
//            whereSql.append(generateSearchableAttributeDefaultWhereSql(criteriaComponent, queryTableColumnName));
//        }
//
//        return new QueryComponent("",fromSql.toString(),whereSql.toString());
//    }

    public StringBuilder generateSearchableAttributeDefaultWhereSql(SearchAttributeCriteriaComponent criteriaComponent,String queryTableColumnName) {
        StringBuilder whereSql = new StringBuilder();
        String initialClauseStarter = "and";
//        whereSql.append(" " + initialClauseStarter + " ");

        boolean valueIsDate = (criteriaComponent.getSearchableAttributeValue() instanceof SearchableAttributeDateTimeValue);
        boolean valueIsString = (criteriaComponent.getSearchableAttributeValue() instanceof SearchableAttributeStringValue);
        boolean valueIsLong = (criteriaComponent.getSearchableAttributeValue() instanceof SearchableAttributeLongValue);
        boolean valueIsFloat = (criteriaComponent.getSearchableAttributeValue() instanceof SearchableAttributeFloatValue);
        boolean addCaseInsensitivityForValue = (!criteriaComponent.isCaseSensitive()) && criteriaComponent.getSearchableAttributeValue().allowsCaseInsensitivity();
        String attributeValueSearched = criteriaComponent.getValue();
        List<String> attributeValuesSearched = criteriaComponent.getValues();

        StringBuilder whereSqlTemp = new StringBuilder();
        if (valueIsDate) {
            if (criteriaComponent.isRangeSearch()) {
                // for a range search just add the criteria
                whereSqlTemp.append(constructWhereClauseDateElement(initialClauseStarter, queryTableColumnName, criteriaComponent.isSearchInclusive(), criteriaComponent.isComponentLowerBoundValue(), attributeValueSearched));
            } else if(criteriaComponent.isAllowInlineRange()) {
                whereSqlTemp.append(constructWhereClauseDateElement(initialClauseStarter, queryTableColumnName, criteriaComponent.isSearchInclusive(), false, attributeValueSearched, criteriaComponent.isAllowInlineRange()));
            } else {
                if (!CollectionUtils.isEmpty(attributeValuesSearched)) {
                    // for a multivalue date search we need multiple ranges entered
                    whereSqlTemp.append(initialClauseStarter).append(" (");
                    boolean firstValue = true;
                    for (String attributeValueEntered : attributeValuesSearched) {
                        whereSqlTemp.append(" ( ");
                        whereSqlTemp.append(constructWhereClauseDateElement("", queryTableColumnName, criteriaComponent.isSearchInclusive(), true, attributeValueEntered));
                        whereSqlTemp.append(constructWhereClauseDateElement("and", queryTableColumnName, criteriaComponent.isSearchInclusive(), false, attributeValueEntered));
                        whereSqlTemp.append(" ) ");
                        String separator = " or ";
                        if (firstValue) {
                            firstValue = false;
                            separator = "";
                        }
                        whereSqlTemp.append(separator);
                    }
                    whereSqlTemp.append(") ");
                } else {
                    // below is a search for a single date field.... must do a range of 'time' so we can find any value regardless of the time associated with it
                    whereSqlTemp.append(constructWhereClauseDateElement(initialClauseStarter, queryTableColumnName, criteriaComponent.isSearchInclusive(), true, attributeValueSearched));
                    whereSqlTemp.append(constructWhereClauseDateElement(initialClauseStarter, queryTableColumnName, criteriaComponent.isSearchInclusive(), false, attributeValueSearched));
                }
            }
        } else {
            boolean usingWildcards = false;
            StringBuilder prefix = new StringBuilder("");
            StringBuilder suffix = new StringBuilder("");
            if (valueIsString) {
                prefix.append("'");
                suffix.insert(0,"'");
            }
            // apply wildcarding if wildcard character is specified
            // after conversion of doc search to lookup, wildcards are always allowed
            if (!CollectionUtils.isEmpty(attributeValuesSearched)) {
                List<String> newList = new ArrayList<String>();
                for (String attributeValueEntered : attributeValuesSearched) {
                    newList.add(attributeValueEntered.trim().replace('*', DATABASE_WILDCARD_CHARACTER));
                    usingWildcards |= (attributeValueEntered.contains(DATABASE_WILDCARD_CHARACTER_STRING));
                }
                attributeValuesSearched = newList;
            } else {
                attributeValueSearched = attributeValueSearched.trim().replace('*', DATABASE_WILDCARD_CHARACTER);
                usingWildcards |= (attributeValueSearched.indexOf(DATABASE_WILDCARD_CHARACTER_STRING) != -1);
            }
            String prefixToUse = prefix.toString();
            String suffixToUse = suffix.toString();

            if (addCaseInsensitivityForValue) {
                queryTableColumnName = "upper(" + queryTableColumnName + ")";
                prefixToUse = "upper(" + prefix.toString();
                suffixToUse = suffix.toString() + ")";
            }

            if (!CollectionUtils.isEmpty(attributeValuesSearched)) {
                // for a multivalue search we need multiple 'or' clause statements entered
                whereSqlTemp.append(initialClauseStarter).append(" (");
                boolean firstValue = true;
                for (String attributeValueEntered : attributeValuesSearched) {
                    checkNumberFormattingIfNumeric(attributeValueEntered, valueIsLong, valueIsFloat);

                    String separator = " or ";
                    if (firstValue) {
                        firstValue = false;
                        separator = "";
                    }
                    String sqlOperand = getSqlOperand(criteriaComponent.isRangeSearch(), criteriaComponent.isSearchInclusive(), (criteriaComponent.isRangeSearch() && criteriaComponent.isComponentLowerBoundValue()), usingWildcards);
                    whereSqlTemp.append(constructWhereClauseElement(separator, queryTableColumnName, sqlOperand, getDbPlatform().escapeString(attributeValueEntered), prefixToUse, suffixToUse));
                }
                whereSqlTemp.append(") ");
            } else {
                String sqlOperand = getSqlOperand(criteriaComponent.isRangeSearch(), criteriaComponent.isSearchInclusive(), (criteriaComponent.isRangeSearch() && criteriaComponent.isComponentLowerBoundValue()), usingWildcards);
              if(criteriaComponent.isAllowInlineRange()) {
                for (SearchOperator range : SearchOperator.RANGE_CHARACTERS) {
                        int index = StringUtils.indexOf(attributeValueSearched, range.op());
                    if(index != -1) {
                            sqlOperand=range.op();
                            if(!StringUtils.equals(sqlOperand, SearchOperator.BETWEEN.op())) {
                                attributeValueSearched = StringUtils.remove(attributeValueSearched, range.op());

                            } else {
                                String[] rangeValues = StringUtils.split(attributeValueSearched, SearchOperator.BETWEEN.op());
                                if(rangeValues!=null && rangeValues.length>1) {
                                    checkNumberFormattingIfNumeric(rangeValues[0], valueIsLong, valueIsFloat);

                                    //append first one here and then set the second one and break
                                    whereSqlTemp.append(constructWhereClauseElement(initialClauseStarter, queryTableColumnName, SearchOperator.GREATER_THAN_EQUAL.op(), getDbPlatform().escapeString(rangeValues[0]), prefixToUse, suffixToUse));
                                    attributeValueSearched = rangeValues[1];
                                    sqlOperand = SearchOperator.LESS_THAN_EQUAL.op();
                                } else {
                                    throw new RuntimeException("What to do here...Range search \"..\" without one element");
                                }
                            }
                            break;
                        }
                    }
              }
                checkNumberFormattingIfNumeric(attributeValueSearched, valueIsLong, valueIsFloat);
                whereSqlTemp.append(constructWhereClauseElement(initialClauseStarter, queryTableColumnName, sqlOperand, getDbPlatform().escapeString(attributeValueSearched), prefixToUse, suffixToUse));
            }
        }
        whereSqlTemp.append(" ");
        return whereSql.append(whereSqlTemp);
    }

    /**
     *  Checks if a particular String value is supposed to be numeric, and, if so, will throw an exception if the String is not numeric.
     *
     * @param testValue The String to test.
     * @param valueIsLong Indicates if the input value should be a Long.
     * @param valueIsFloat Indicates if the input value should be a BigDecimal.
     * @throws RiceRuntimeException if the value is supposed to be numeric, but is not.
     */
    private void checkNumberFormattingIfNumeric(String testValue, boolean valueIsLong, boolean valueIsFloat) {
        if (valueIsLong) {
            try { Long.parseLong(testValue.trim()); }
            catch (Exception exc) { throw new RiceRuntimeException("Invalid number format", exc); }
        }
        if (valueIsFloat) {
            try { new BigDecimal(testValue.trim()); }
            catch (Exception exc) { throw new RiceRuntimeException("Invalid number format", exc); }
        }
    }

    public QueryComponent getSearchableAttributeJoinSql(String tableName, String tableIdentifier, String whereSqlStarter, String attributeTableKeyColumnName) {
        return new QueryComponent("", generateSearchableAttributeFromSql(tableName, tableIdentifier).toString(), generateSearchableAttributeWhereClauseJoin(whereSqlStarter, tableIdentifier, attributeTableKeyColumnName).toString());
    }

    public StringBuilder generateSearchableAttributeWhereClauseJoin(String whereSqlStarter,String tableIdentifier,String attributeTableKeyColumnName) {
        StringBuilder whereSql = new StringBuilder(constructWhereClauseElement(whereSqlStarter, "DOC_HDR.DOC_HDR_ID", "=", getDbPlatform().escapeString(tableIdentifier + ".DOC_HDR_ID"), null, null));
        whereSql.append(constructWhereClauseElement(" and ", tableIdentifier + ".KEY_CD", "=", getDbPlatform().escapeString(attributeTableKeyColumnName), "'", "'"));
        return whereSql;
    }

    public StringBuilder generateSearchableAttributeFromSql(String tableName, String tableIdentifier) {
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalArgumentException("tableName was null or blank");
        }
        if (StringUtils.isBlank(tableIdentifier)) {
            throw new IllegalArgumentException("tableIdentifier was null or blank");
        }
        StringBuilder fromSql = new StringBuilder();
        fromSql.append(" ,").append(tableName).append(" ").append(getDbPlatform().escapeString(tableIdentifier)).append(" ");
        return fromSql;
    }

    public StringBuilder constructWhereClauseDateElement(String clauseStarter,String queryTableColumnName,boolean inclusive,boolean valueIsLowerBound,String dateValueToSearch) {
        return constructWhereClauseDateElement(clauseStarter, queryTableColumnName, inclusive, valueIsLowerBound,
                dateValueToSearch, false);
    }

    public StringBuilder constructWhereClauseDateElement(String clauseStarter,String queryTableColumnName,boolean inclusive,boolean valueIsLowerBound,String dateValueToSearch, boolean isAllowInlineRange) {
        StringBuilder whereSQLBuffer = new StringBuilder();
        StringBuilder sqlOperand = new StringBuilder(getSqlOperand(true, inclusive, valueIsLowerBound, false));
        String lowerTimeBound = "00:00:00";
        String upperTimeBound = "23:59:59";

        String timeValueToSearch = null;
        if (valueIsLowerBound) {
            timeValueToSearch = lowerTimeBound;
        } else {
            timeValueToSearch = upperTimeBound;
        }

        if(isAllowInlineRange) {
            for (SearchOperator range : SearchOperator.RANGE_CHARACTERS) {
                int index = StringUtils.indexOf(dateValueToSearch, range.op());
                if(index != -1) {
                    sqlOperand=new StringBuilder(range.op());
                    if(!StringUtils.equals(sqlOperand.toString(), SearchOperator.BETWEEN.op())) {
                        dateValueToSearch = StringUtils.remove(dateValueToSearch,range.op());
                        if(range == SearchOperator.GREATER_THAN) {
                            timeValueToSearch = upperTimeBound;
                        } else if(range == SearchOperator.LESS_THAN){
                            timeValueToSearch = lowerTimeBound;
                        }
                    }  else {
                        String[] rangeValues = StringUtils.split(dateValueToSearch, SearchOperator.BETWEEN.op());
                        if(rangeValues!=null && rangeValues.length>1) {
                            //Enhancement Idea - Could possibly use recursion here (would have to set the lower bound and inclusive variables
                            //append first one here and then set the second one and break
                            timeValueToSearch = lowerTimeBound;
							whereSQLBuffer.append(constructWhereClauseElement(clauseStarter, queryTableColumnName, SearchOperator.GREATER_THAN_EQUAL.op(), getDbPlatform().getDateSQL(getDbPlatform().escapeString(SQLUtils.getSqlFormattedDate(rangeValues[0].trim())), timeValueToSearch.trim()), "", ""));

                            dateValueToSearch = rangeValues[1];
                            sqlOperand = new StringBuilder(SearchOperator.LESS_THAN_EQUAL.op());
                            timeValueToSearch = upperTimeBound;
                        } else {
                            throw new RuntimeException("What to do here...Range search \"..\" without one element");
                        }
                    }
                    break;
                }
            }
        }
		return whereSQLBuffer.append(constructWhereClauseElement(clauseStarter, queryTableColumnName, sqlOperand.toString(), getDbPlatform().getDateSQL(getDbPlatform().escapeString(SQLUtils.getSqlFormattedDate(dateValueToSearch.trim())), timeValueToSearch.trim()), "", ""));
    }

    public StringBuilder constructWhereClauseElement(String clauseStarter,String queryTableColumnName,String operand,String valueToSearch,String valuePrefix,String valueSuffix) {
        StringBuilder whereSql = new StringBuilder();
        valuePrefix = (valuePrefix != null) ? valuePrefix : "";
        valueSuffix = (valueSuffix != null) ? valueSuffix : "";
        whereSql.append(" " + clauseStarter + " ").append(getDbPlatform().escapeString(queryTableColumnName)).append(" " + operand + " ").append(valuePrefix).append(valueToSearch).append(valueSuffix).append(" ");
        return whereSql;
    }

    /**
     * For the following we first check for a ranged search because a ranged search
     * does not allow for wildcards
     */
    public String getSqlOperand(boolean rangeSearch, boolean inclusive, boolean valueIsLowerBound, boolean usingWildcards) {
        StringBuilder sqlOperand = new StringBuilder("=");
        if (rangeSearch) {
            if (valueIsLowerBound) {
                sqlOperand = new StringBuilder(">");
            } else {
                sqlOperand = new StringBuilder("<");
            }
            if (inclusive) {
                sqlOperand.append("=");
            }

        } else if (usingWildcards) {
            sqlOperand = new StringBuilder("like");
        }
        return sqlOperand.toString();
    }

    @Override
    public DocumentLookupResults.Builder processResultSet(DocumentLookupCriteria criteria, boolean criteriaModified, Statement searchAttributeStatement, ResultSet resultSet, int maxResultCap, int fetchLimit) throws SQLException {
        DocumentLookupCriteria.Builder criteriaBuilder = DocumentLookupCriteria.Builder.create(criteria);
        DocumentLookupResults.Builder results = DocumentLookupResults.Builder.create(criteriaBuilder);
        results.setCriteriaModified(criteriaModified);
        int size = 0;
        List<DocumentLookupResult.Builder> resultList = new ArrayList<DocumentLookupResult.Builder>();
        results.setLookupResults(resultList);
        Map<String, DocumentLookupResult.Builder> resultMap = new HashMap<String, DocumentLookupResult.Builder>();
        PerformanceLogger perfLog = new PerformanceLogger();
        int iteration = 0;
        boolean resultSetHasNext = resultSet.next();
        while ( resultSetHasNext && resultMap.size() < maxResultCap && iteration++ < fetchLimit) {
            DocumentLookupResult.Builder resultBuilder = processRow(criteria, searchAttributeStatement, resultSet);
            String documentId = resultBuilder.getDocument().getDocumentId();
            if (!resultMap.containsKey(documentId)) {
                resultList.add(resultBuilder);
                resultMap.put(documentId, resultBuilder);
                size++;
            } else {
                // handle duplicate rows with different search data
                DocumentLookupResult.Builder previousEntry = resultMap.get(documentId);
                handleMultipleDocumentRows(previousEntry, resultBuilder);
            }
            resultSetHasNext = resultSet.next();
        }
        
        perfLog.log("Time to read doc search results.", true);
        // if we have threshold+1 results, then we have more results than we are going to display
        results.setOverThreshold(resultSetHasNext);

        LOG.debug("Processed "+size+" document search result rows.");
        return results;
    }

    /**
     * Handles multiple document rows by collapsing them into the list of document attributes on the existing row.
     * The two rows must represent the same document.
     *
     * @param existingRow the existing row to combine the new row into
     * @param newRow the new row from which to combine document attributes with the existing row
     */
    private void handleMultipleDocumentRows(DocumentLookupResult.Builder existingRow, DocumentLookupResult.Builder newRow) {
        for (DocumentAttribute.AbstractBuilder<?> newDocumentAttribute : newRow.getDocumentAttributes()) {
            existingRow.getDocumentAttributes().add(newDocumentAttribute);
        }
    }

    protected DocumentLookupResult.Builder processRow(DocumentLookupCriteria criteria, Statement searchAttributeStatement, ResultSet rs) throws SQLException {

        String documentId = rs.getString("DOC_HDR_ID");
        String initiatorPrincipalId = rs.getString("INITR_PRNCPL_ID");
        String documentTypeName = rs.getString("DOC_TYP_NM");
        org.kuali.rice.kew.api.doctype.DocumentType documentType =
                KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeName);
        if (documentType == null) {
            throw new IllegalStateException("Failed to locate a document type with the given name: " + documentTypeName);
        }
        String documentTypeId = documentType.getId();

        Document.Builder documentBuilder = Document.Builder.create(documentId, initiatorPrincipalId, documentTypeName, documentTypeId);
        DocumentLookupResult.Builder resultBuilder = DocumentLookupResult.Builder.create(documentBuilder);

        String statusCode = rs.getString("DOC_HDR_STAT_CD");
        Timestamp createTimestamp = rs.getTimestamp("CRTE_DT");
        String title = rs.getString("TTL");
        String applicationDocumentStatus = rs.getString("APP_DOC_STAT");

        documentBuilder.setStatus(DocumentStatus.fromCode(statusCode));
        documentBuilder.setDateCreated(new DateTime(createTimestamp.getTime()));
        documentBuilder.setTitle(title);
        documentBuilder.setApplicationDocumentStatus(applicationDocumentStatus);

        // TODO - Rice 2.0 - should probably set as many properties on the document as we can

        if (isUsingAtLeastOneSearchAttribute(criteria)) {
            populateDocumentAttributesValues(resultBuilder, searchAttributeStatement);
        }

        return resultBuilder;
    }

    /**
     * This method performs searches against the search attribute value tables (see classes implementing
     * {@link SearchableAttributeValue}) to get data to fill in search attribute values on the given resultBuilder parameter
     *
     * @param resultBuilder - document search result object getting search attributes added to it
     * @param searchAttributeStatement - statement being used to call the database for queries
     * @throws SQLException
     */
    public void populateDocumentAttributesValues(DocumentLookupResult.Builder resultBuilder, Statement searchAttributeStatement) throws SQLException {
        searchAttributeStatement.setFetchSize(50);
        String documentId = resultBuilder.getDocument().getDocumentId();
        List<SearchableAttributeValue> attributeValues = DocSearchUtils.getSearchableAttributeValueObjectTypes();
        PerformanceLogger perfLog = new PerformanceLogger(documentId);
        for (SearchableAttributeValue searchAttValue : attributeValues) {
            String attributeSql = "select KEY_CD, VAL from " + searchAttValue.getAttributeTableName() + " where DOC_HDR_ID = " + documentId;
            ResultSet attributeResultSet = null;
            try {
                attributeResultSet = searchAttributeStatement.executeQuery(attributeSql);
                while (attributeResultSet.next()) {
                    searchAttValue.setSearchableAttributeKey(attributeResultSet.getString("KEY_CD"));
                    searchAttValue.setupAttributeValue(attributeResultSet, "VAL");
                    if ( (!org.apache.commons.lang.StringUtils.isEmpty(searchAttValue.getSearchableAttributeKey())) && (searchAttValue.getSearchableAttributeValue() != null) ) {
                        DocumentAttribute documentAttribute = searchAttValue.toDocumentAttribute();
                        resultBuilder.getDocumentAttributes().add(DocumentAttributeFactory.loadContractIntoBuilder(
                                documentAttribute));
                    }
                }
            } finally {
                if (attributeResultSet != null) {
                    try {
                        attributeResultSet.close();
                    } catch (Exception e) {
                        LOG.warn("Could not close searchable attribute result set for class " + searchAttValue.getClass().getName(),e);
                    }
                }
            }
        }
        perfLog.log("Time to execute doc search search attribute queries.", true);
    }

    public String generateSearchSql(DocumentLookupCriteria criteria, List<RemotableAttributeField> searchFields) {

        String docTypeTableAlias   = "DOC1";
        String docHeaderTableAlias = "DOC_HDR";

        String sqlPrefix = "Select * from (";
        String sqlSuffix = ") FINAL_SEARCH order by FINAL_SEARCH.DOC_HDR_ID desc";
        // the DISTINCT here is important as it filters out duplicate rows which could occur as the result of doc search extension values...
        StringBuilder selectSQL = new StringBuilder("select DISTINCT("+ docHeaderTableAlias +".DOC_HDR_ID), "+ docHeaderTableAlias +".INITR_PRNCPL_ID, "
                + docHeaderTableAlias +".DOC_HDR_STAT_CD, "+ docHeaderTableAlias +".CRTE_DT, "+ docHeaderTableAlias +".TTL, "+ docHeaderTableAlias +".APP_DOC_STAT, "+ docTypeTableAlias +".DOC_TYP_NM, "
                + docTypeTableAlias +".LBL, "+ docTypeTableAlias +".DOC_HDLR_URL, "+ docTypeTableAlias +".ACTV_IND");
        StringBuilder fromSQL = new StringBuilder(" from KREW_DOC_TYP_T "+ docTypeTableAlias +" ");
        StringBuilder fromSQLForDocHeaderTable = new StringBuilder(", KREW_DOC_HDR_T " + docHeaderTableAlias + " ");

        StringBuilder whereSQL = new StringBuilder();
        whereSQL.append(getDocumentIdSql(criteria.getDocumentId(), getGeneratedPredicatePrefix(whereSQL.length()), docHeaderTableAlias));
        whereSQL.append(getInitiatorSql(criteria.getInitiatorPrincipalName(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getAppDocIdSql(criteria.getApplicationDocumentId(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateCreatedSql(criteria.getDateCreatedFrom(), criteria.getDateCreatedTo(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateLastModifiedSql(criteria.getDateLastModifiedFrom(), criteria.getDateLastModifiedTo(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateApprovedSql(criteria.getDateApprovedFrom(), criteria.getDateApprovedTo(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateFinalizedSql(criteria.getDateFinalizedFrom(), criteria.getDateFinalizedTo(), getGeneratedPredicatePrefix(whereSQL.length())));

        // flags for the table being added to the FROM class of the sql
        String principalViewerSql = getViewerSql(criteria.getViewerPrincipalName(), getGeneratedPredicatePrefix(whereSQL.length()));
        String groupViewerSql = getGroupViewerSql(criteria.getViewerGroupId(), getGeneratedPredicatePrefix(whereSQL.length()));
        if (StringUtils.isNotBlank(principalViewerSql) || StringUtils.isNotBlank(groupViewerSql)) {
            whereSQL.append(principalViewerSql);
            whereSQL.append(groupViewerSql);
            fromSQL.append(", KREW_ACTN_RQST_T ");
        }

        if (!("".equals(getApproverSql(criteria.getApproverPrincipalName(), getGeneratedPredicatePrefix(whereSQL.length()))))) {
            whereSQL.append(getApproverSql(criteria.getApproverPrincipalName(), getGeneratedPredicatePrefix(whereSQL.length())));
            fromSQL.append(", KREW_ACTN_TKN_T ");
        }



        String docRouteNodeSql = getDocRouteNodeSql(criteria.getDocumentTypeName(), criteria.getRouteNodeName(), criteria.getRouteNodeLookupLogic(), getGeneratedPredicatePrefix(whereSQL.length()));
        if (StringUtils.isNotBlank(docRouteNodeSql)) {
            whereSQL.append(docRouteNodeSql);
            fromSQL.append(", KREW_RTE_NODE_INSTN_T ");
            fromSQL.append(", KREW_RTE_NODE_T ");
        }

        if (!criteria.getDocumentAttributeValues().isEmpty()) {
            QueryComponent queryComponent = getSearchableAttributeSql(criteria.getDocumentAttributeValues(), searchFields, getGeneratedPredicatePrefix(
                    whereSQL.length()));
            selectSQL.append(queryComponent.getSelectSql());
            fromSQL.append(queryComponent.getFromSql());
            whereSQL.append(queryComponent.getWhereSql());
        }

        String docTypeFullNameSql = getDocTypeFullNameWhereSql(criteria.getDocumentTypeName(), getGeneratedPredicatePrefix(whereSQL.length()));
        if (!("".equals(docTypeFullNameSql))) {
            whereSQL.append(docTypeFullNameSql);
        }
        whereSQL.append(getDocumentStatusSql(criteria.getDocumentStatuses(), criteria.getDocumentStatusCategories(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getGeneratedPredicatePrefix(whereSQL.length())).append(" DOC_HDR.DOC_TYP_ID = DOC1.DOC_TYP_ID ");
        fromSQL.append(fromSQLForDocHeaderTable);

        // App Doc Status Value and Transition clauses
        String statusTransitionWhereClause = getStatusTransitionDateSql(criteria.getDateApplicationDocumentStatusChangedFrom(), criteria.getDateApplicationDocumentStatusChangedTo(), getGeneratedPredicatePrefix(whereSQL.length()));
        whereSQL.append(getAppDocStatusSql(criteria.getApplicationDocumentStatus(), getGeneratedPredicatePrefix(whereSQL.length()), statusTransitionWhereClause.length() ));
        if (statusTransitionWhereClause.length() > 0){
        	whereSQL.append(statusTransitionWhereClause);
            whereSQL.append(getGeneratedPredicatePrefix(whereSQL.length())).append(" DOC_HDR.DOC_HDR_ID = STAT_TRAN.DOC_HDR_ID ");
        	fromSQL.append(", KREW_APP_DOC_STAT_TRAN_T STAT_TRAN ");
        }

        String finalizedSql = sqlPrefix + " " + selectSQL.toString() + " " + fromSQL.toString() + " " + whereSQL.toString() + " " + sqlSuffix;

        LOG.info("*********** SEARCH SQL ***************");
        LOG.info(finalizedSql);
        LOG.info("**************************************");
        return finalizedSql;
    }

    public String getDocumentIdSql(String documentId, String whereClausePredicatePrefix, String tableAlias) {

        if ((documentId == null) || "".equals(documentId.trim())) {
            return "";
        } else {
        	// Using true for caseInsensitive causes bad performance for MYSQL databases since function indexes cannot be added.
        	// Due to this, false is passed for caseInsensitive
            Criteria crit = getSqlBuilder().createCriteria("DOC_HDR_ID", documentId, "KREW_DOC_HDR_T", tableAlias, String.class, false, true);
            return new StringBuilder(whereClausePredicatePrefix + crit.buildWhere()).toString();
        }

    }

    public String getInitiatorSql(String initiatorPrincipalName, String whereClausePredicatePrefix) {

        if (StringUtils.isBlank(initiatorPrincipalName)) {
            return "";
        }

        String tableAlias = "DOC_HDR";

        Map<String, String> m = new HashMap<String, String>();
        m.put("principalName", initiatorPrincipalName);

        // This will search for people with the ability for the valid operands.
        List<Person> pList = KimApiServiceLocator.getPersonService().findPeople(m, false);

        if(pList == null || pList.isEmpty() ){
            // they entered something that returned nothing... so we should return nothing
             return new StringBuilder(whereClausePredicatePrefix + " 1 = 0 ").toString();
        }

        List<String> principalList = new ArrayList<String>();

        for(Person p: pList){
            principalList.add(p.getPrincipalId());
        }

        Criteria crit = new Criteria("KREW_DOC_HDR_T", tableAlias);
        crit.in("INITR_PRNCPL_ID", principalList, String.class);

        return new StringBuilder(whereClausePredicatePrefix + crit.buildWhere()).toString();
    }

    public String getDocTitleSql(String docTitle, String whereClausePredicatePrefix) {
        if (StringUtils.isBlank(docTitle)) {
            return "";
        } else {
            // quick and dirty ' replacement that isn't the best but should work for all dbs
            docTitle = docTitle.trim().replace("\'", "\'\'");


            SqlBuilder sqlBuild = new SqlBuilder();
            Criteria crit = new Criteria("KREW_DOC_HDR_T", "DOC_HDR");

            sqlBuild.addCriteria("TTL", docTitle, String.class, true, true, crit);
            return new StringBuilder(whereClausePredicatePrefix + crit.buildWhere()).toString();
        }
    }

    // special methods that return the sql needed to complete the search
    // or nothing if the field was not filled in
    public String getAppDocIdSql(String appDocId, String whereClausePredicatePrefix) {
        String tableAlias = "DOC_HDR";

        if ((appDocId == null) || "".equals(appDocId.trim())) {
            return "";
        } else {
            Criteria crit = getSqlBuilder().createCriteria("APP_DOC_ID", appDocId, "KREW_DOC_HDR_T", tableAlias,String.class);
            return new StringBuilder(whereClausePredicatePrefix + crit.buildWhere()).toString();
        }
    }

    public String getDateCreatedSql(DateTime fromDateCreated, DateTime toDateCreated, String whereClausePredicatePrefix) {
        return establishDateString(fromDateCreated, toDateCreated, "KREW_DOC_HDR_T", "DOC_HDR", "CRTE_DT", whereClausePredicatePrefix);
    }

    public String getDateApprovedSql(DateTime fromDateApproved, DateTime toDateApproved, String whereClausePredicatePrefix) {
        return establishDateString(fromDateApproved, toDateApproved, "KREW_DOC_HDR_T", "DOC_HDR", "APRV_DT", whereClausePredicatePrefix);
    }

    public String getDateFinalizedSql(DateTime fromDateFinalized, DateTime toDateFinalized, String whereClausePredicatePrefix) {
        return establishDateString(fromDateFinalized, toDateFinalized, "KREW_DOC_HDR_T", "DOC_HDR", "FNL_DT", whereClausePredicatePrefix);

    }

    public String getDateLastModifiedSql(DateTime fromDateLastModified, DateTime toDateLastModified, String whereClausePredicatePrefix) {
        return establishDateString(fromDateLastModified, toDateLastModified, "KREW_DOC_HDR_T", "DOC_HDR", "STAT_MDFN_DT", whereClausePredicatePrefix);
    }

	public String getStatusTransitionDateSql(DateTime fromStatusTransitionDate, DateTime toStatusTransitionDate, String whereClausePredicatePrefix) {
        return establishDateString(fromStatusTransitionDate, toStatusTransitionDate, "KREW_DOC_HDR_T", "DOC_HDR", "APP_DOC_STAT_MDFN_DT", whereClausePredicatePrefix);
    }

    public String getViewerSql(String viewer, String whereClausePredicatePrefix) {
        String returnSql = "";
        if ((viewer != null) && (!"".equals(viewer.trim()))) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("principalName", viewer);

            // This will search for people with the ability for the valid operands.
            List<Person> pList = KimApiServiceLocator.getPersonService().findPeople(m, false);

            if(pList == null || pList.isEmpty() ){
                // they entered something that returned nothing... so we should return nothing
                return new StringBuilder(whereClausePredicatePrefix + " 1 = 0 ").toString();
            }

            List<String> principalList = new ArrayList<String>();

            for(Person p: pList){
                principalList.add(p.getPrincipalId());
            }

            Criteria crit = new Criteria("KREW_ACTN_RQST_T", "KREW_ACTN_RQST_T");

            crit.in("PRNCPL_ID", principalList, String.class);

            //Person person = KIMServiceLocatorInternal.getPersonService().getPersonByPrincipalName(viewer.trim());
            //String principalId = person.getPrincipalId();
            returnSql = whereClausePredicatePrefix + "( (DOC_HDR.DOC_HDR_ID = KREW_ACTN_RQST_T.DOC_HDR_ID and " + crit.buildWhere() + " )";

            Set<String> viewerGroupIds = new TreeSet<String>();

            if(principalList != null && !principalList.isEmpty()){
                for(String principalId: principalList){
                        viewerGroupIds.addAll(KimApiServiceLocator.getGroupService().getGroupIdsForPrincipal(principalId));
                }
            }

            // Documents routed to users as part of a workgoup should be returned.

            // Use Chad's escape stuff
            if (viewerGroupIds != null && !viewerGroupIds.isEmpty()) {

                returnSql += " or ( " +
                    "DOC_HDR.DOC_HDR_ID = KREW_ACTN_RQST_T.DOC_HDR_ID " +
                    "and KREW_ACTN_RQST_T.GRP_ID in (";

                boolean first = true;
                for(String groupId: viewerGroupIds){
                    if(!first){
                        returnSql += ",";
                    }
                    returnSql += "'" + groupId + "'";
                    first = false;
                }
                returnSql += "))";
            }
            returnSql += ")";
        }
        return returnSql;
    }

    public String getGroupViewerSql(String groupId, String whereClausePredicatePrefix) {
        String sql = "";
        if (StringUtils.isNotBlank(groupId)) {
            sql = whereClausePredicatePrefix + " DOC_HDR.DOC_HDR_ID = KREW_ACTN_RQST_T.DOC_HDR_ID and KREW_ACTN_RQST_T.GRP_ID = " + groupId;
        }
        return sql;
    }

    public String getApproverSql(String approver, String whereClausePredicatePrefix) {
        String returnSql = "";
        if ((approver != null) && (!"".equals(approver.trim()))) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("principalName", approver);

            // This will search for people with the ability for the valid operands.
            List<Person> pList = KimApiServiceLocator.getPersonService().findPeople(m, false);

            if(pList == null || pList.isEmpty() ){
                 return "";
            }

            List<String> principalList = new ArrayList<String>();

            for(Person p: pList){
                principalList.add(p.getPrincipalId());
            }

            Criteria crit = new Criteria("KREW_ACTN_TKN_T", "KREW_ACTN_TKN_T");
            crit.in("PRNCPL_ID", principalList, String.class);

            returnSql = whereClausePredicatePrefix +
            " DOC_HDR.DOC_HDR_ID = KREW_ACTN_TKN_T.DOC_HDR_ID and upper(KREW_ACTN_TKN_T.ACTN_CD) in ('" +
            KEWConstants.ACTION_TAKEN_APPROVED_CD + "','" + KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD + "')" +
            " and " + crit.buildWhere();
        }
        return returnSql;
    }

    public String getDocTypeFullNameWhereSql(String docTypeFullName, String whereClausePredicatePrefix) {
        StringBuilder returnSql = new StringBuilder("");
        if ((docTypeFullName != null) && (!"".equals(docTypeFullName.trim()))) {
            /*
            DocumentTypeDAOOjbImpl


            Map<String, String> m = new HashMap<String, String>();
            m.put("name", docTypeFullName);

            Collection c = KRADServiceLocatorInternal.getBusinessObjectDao().findMatching(DocumentType.class, m);
*/
            DocumentTypeService docSrv = (DocumentTypeService) KEWServiceLocator.getDocumentTypeService();
            DocumentType docType = docSrv.findByName(docTypeFullName.trim());
            if (docType != null) {
                returnSql.append(whereClausePredicatePrefix).append("(");
                addDocumentTypeNameToSearchOn(returnSql,docType.getName(), "");
                if (docType.getChildrenDocTypes() != null) {
                    addChildDocumentTypes(returnSql, docType.getChildrenDocTypes());
                }
                addExtraDocumentTypesToSearch(returnSql,docType);
                returnSql.append(")");
            }else{
                returnSql.append(whereClausePredicatePrefix).append("(");
                addDocumentTypeLikeNameToSearchOn(returnSql,docTypeFullName.trim(), "");
                returnSql.append(")");
            }
        }
        return returnSql.toString();
    }

    public void addChildDocumentTypes(StringBuilder whereSql, Collection<DocumentType> childDocumentTypes) {
        for (DocumentType child : childDocumentTypes) {
            addDocumentTypeNameToSearchOn(whereSql, child.getName());
            addChildDocumentTypes(whereSql, child.getChildrenDocTypes());
        }
    }

    public void addExtraDocumentTypesToSearch(StringBuilder whereSql, DocumentType docType) {}

    public void addDocumentTypeNameToSearchOn(StringBuilder whereSql, String documentTypeName) {
        this.addDocumentTypeNameToSearchOn(whereSql, documentTypeName, " or ");
    }

    public void addDocumentTypeNameToSearchOn(StringBuilder whereSql, String documentTypeName, String clause) {
        whereSql.append(clause).append(" DOC1.DOC_TYP_NM = '" + documentTypeName + "'");
    }
    public void addDocumentTypeLikeNameToSearchOn(StringBuilder whereSql, String documentTypeName, String clause) {
        documentTypeName = documentTypeName.replace('*', '%');
        whereSql.append(clause).append(" DOC1.DOC_TYP_NM LIKE '" + documentTypeName + "'");
    }

    public String getDocRouteNodeSql(String documentTypeFullName, String routeNodeName, RouteNodeLookupLogic docRouteLevelLogic, String whereClausePredicatePrefix) {
        // -1 is the default 'blank' choice from the route node drop down a number is used because the ojb RouteNode object is used to
        // render the node choices on the form.
        String returnSql = "";
        if (StringUtils.isNotBlank(routeNodeName)) {

            StringBuilder routeNodeCriteria = new StringBuilder("and " + ROUTE_NODE_TABLE + ".NM ");
            if (RouteNodeLookupLogic.EXACTLY == docRouteLevelLogic) {
        		routeNodeCriteria.append("= '" + getDbPlatform().escapeString(routeNodeName) + "' ");
            } else {
                routeNodeCriteria.append("in (");
                // below buffer used to facilitate the addition of the string ", " to separate out route node names
                StringBuilder routeNodeInCriteria = new StringBuilder();
                boolean foundSpecifiedNode = false;
                List<RouteNode> routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(getValidDocumentType(documentTypeFullName), true);
                for (RouteNode routeNode : routeNodes) {
                    if (routeNodeName.equals(routeNode.getRouteNodeName())) {
                        // current node is specified node so we ignore it outside of the boolean below
                        foundSpecifiedNode = true;
                        continue;
                    }
                    // below logic should be to add the current node to the criteria if we haven't found the specified node
                    // and the logic qualifier is 'route nodes before specified'... or we have found the specified node and
                    // the logic qualifier is 'route nodes after specified'
                    if ( (!foundSpecifiedNode && RouteNodeLookupLogic.BEFORE == docRouteLevelLogic) ||
                         (foundSpecifiedNode && RouteNodeLookupLogic.AFTER == docRouteLevelLogic) ) {
                        if (routeNodeInCriteria.length() > 0) {
                            routeNodeInCriteria.append(", ");
                        }
                        routeNodeInCriteria.append("'" + routeNode.getRouteNodeName() + "'");
                    }
                }
                if (routeNodeInCriteria.length() > 0) {
                    routeNodeCriteria.append(routeNodeInCriteria);
                } else {
                    routeNodeCriteria.append("''");
                }
                routeNodeCriteria.append(") ");
            }
            returnSql = whereClausePredicatePrefix + "DOC_HDR.DOC_HDR_ID = " + ROUTE_NODE_INST_TABLE + ".DOC_HDR_ID and " + ROUTE_NODE_INST_TABLE + ".RTE_NODE_ID = " + ROUTE_NODE_TABLE + ".RTE_NODE_ID and " + ROUTE_NODE_INST_TABLE + ".ACTV_IND = 1 " + routeNodeCriteria.toString() + " ";
        }
        return returnSql;
    }

    public String getDocumentStatusSql(List<DocumentStatus> documentStatuses, List<DocumentStatusCategory> categories, String whereClausePredicatePrefix) {
        if (CollectionUtils.isEmpty(documentStatuses) && CollectionUtils.isEmpty(categories)) {
            return whereClausePredicatePrefix + "DOC_HDR.DOC_HDR_STAT_CD != '" + DocumentStatus.INITIATED.getCode() + "'";
        } else {
            // include all given document statuses
            Set<DocumentStatus> statusesToInclude = new HashSet<DocumentStatus>(documentStatuses);

            // add all statuses from each category
            for (DocumentStatusCategory category : categories) {
                Set<DocumentStatus> categoryStatuses = DocumentStatus.getStatusesForCategory(category);
                statusesToInclude.addAll(categoryStatuses);
            }

            Set<String> statusCodes = new HashSet<String>();
            for (DocumentStatus statusToInclude : statusesToInclude) {
                statusCodes.add("'" + getDbPlatform().escapeString(statusToInclude.getCode()) + "'");
            }
            return whereClausePredicatePrefix + " DOC_HDR.DOC_HDR_STAT_CD in (" + StringUtils.join(statusCodes, ", ") +")";
        }
    }

    /**
     *
     * This method generates the where clause fragment related to Application Document Status.
     * If the Status value only is defined, search for the appDocStatus value in the route header.
     * If either the transition from/to dates are defined, search agains the status transition history.
     *
     * @param appDocStatus
     * @param whereClausePredicatePrefix
     * @param statusTransitionWhereClauseLength
     * @return
     */
    public String getAppDocStatusSql(String appDocStatus, String whereClausePredicatePrefix, int statusTransitionWhereClauseLength) {
        if ((appDocStatus == null) || "".equals(appDocStatus.trim())) {
            return "";
        } else {
        	if (statusTransitionWhereClauseLength > 0){
        		return whereClausePredicatePrefix + " STAT_TRAN.APP_DOC_STAT_TO = '" + getDbPlatform().escapeString(appDocStatus.trim()) + "'";
        	}else{
        		return whereClausePredicatePrefix + " DOC_HDR.APP_DOC_STAT = '" + getDbPlatform().escapeString(appDocStatus.trim()) + "'";
        	}
        }
    }

    public String getGeneratedPredicatePrefix(int whereClauseSize) {
        return (whereClauseSize > 0) ? " and " : " where ";
    }

    public String establishDateString(DateTime fromDate, DateTime toDate, String tableName, String tableAlias, String colName, String whereStatementClause) {

        String fromDateValue = null;
        if (fromDate != null) {
            fromDateValue = CoreApiServiceLocator.getDateTimeService().toDateString(fromDate.toDate());
        }

        String toDateValue = null;
        if (toDate != null) {
            toDateValue = CoreApiServiceLocator.getDateTimeService().toDateString(toDate.toDate());
            toDateValue += " 23:59:59";
        }

        String searchValue = null;
        if (fromDateValue != null && toDateValue != null) {
            searchValue = fromDateValue + " .. " + toDateValue;
        } else if (fromDateValue != null) {
            searchValue = ">= " + fromDateValue;
        } else if (toDateValue != null) {
            searchValue = "<= " + toDateValue;
        } else {
            return "";
        }

        Criteria crit = getSqlBuilder().createCriteria(colName, searchValue, tableName, tableAlias, java.sql.Date.class, true, true);
        return new StringBuilder(whereStatementClause + crit.buildWhere()).toString();

    }

    public DatabasePlatform getDbPlatform() {
        if (dbPlatform == null) {
            dbPlatform = (DatabasePlatform) GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
        }
        return dbPlatform;
    }

    private List<String> tokenizeCriteria(String input){
        List<String> lRet = null;

        lRet = Arrays.asList(input.split("\\|"));

        return lRet;
    }
    private boolean validateNumber(List<String> integers) {
        for(String integer: integers){
        //  if(!this.validateNumber(integer)){
        //      return false;
        //  }
        }
        return true;
    }

    /**
     * @return the sqlBuilder
     */
    public SqlBuilder getSqlBuilder() {
        if(sqlBuilder == null){
            sqlBuilder = new SqlBuilder();
            sqlBuilder.setDbPlatform(getDbPlatform());
            sqlBuilder.setDateTimeService(CoreApiServiceLocator.getDateTimeService());
        }
        return this.sqlBuilder;
    }

    /**
     * @param sqlBuilder the sqlBuilder to set
     */
    public void setSqlBuilder(SqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    /**
     * A helper method for determining whether any searchable attributes are in use for the search.
     *
     * @return True if the search criteria contains at least one searchable attribute or the criteria's doc type name is
     * non-blank; false otherwise.
     */
    protected boolean isUsingAtLeastOneSearchAttribute(DocumentLookupCriteria criteria) {
        return criteria.getDocumentAttributeValues().size() > 0 || StringUtils.isNotBlank(criteria.getDocumentTypeName());
    }

}
