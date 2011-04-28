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
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.persistence.jdbc.sql.Criteria;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SqlBuilder;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.core.api.services.CoreApiServiceLocator;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.core.util.type.TypeUtils;
import org.kuali.rice.kew.doctype.SecuritySession;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.ObjectUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

    private static List<SearchableAttribute> searchableAttributes;
    private static DocSearchCriteriaDTO criteria;
    private static String searchingUser;

    private boolean isProcessResultSet = true;

    private DatabasePlatform dbPlatform;
    private MessageMap messageMap;

    private SqlBuilder sqlBuilder = null;

    public StandardDocumentSearchGenerator() {
        super();
        searchableAttributes = new ArrayList<SearchableAttribute>();
    }

    /**
     * @param searchableAttributes in a list
     */
    public StandardDocumentSearchGenerator(List<SearchableAttribute> searchableAttributes) {
        this();
        StandardDocumentSearchGenerator.searchableAttributes = searchableAttributes;
    }

    public DocSearchCriteriaDTO getCriteria() {
        return criteria;
    }

    public void setCriteria(DocSearchCriteriaDTO criteria) {
        StandardDocumentSearchGenerator.criteria = criteria;
    }

    public List<SearchableAttribute> getSearchableAttributes() {
        return searchableAttributes;
    }

    public void setSearchableAttributes(List<SearchableAttribute> searchableAttributes) {
        this.searchableAttributes = searchableAttributes;
    }

    public String getSearchingUser() {
        return searchingUser;
    }

    public void setSearchingUser(String searchingUser) {
        StandardDocumentSearchGenerator.searchingUser = searchingUser;
    }

    public DocSearchCriteriaDTO clearSearch(DocSearchCriteriaDTO searchCriteria) {
        return new DocSearchCriteriaDTO();
    }

    public List<WorkflowServiceError> performPreSearchConditions(String principalId, DocSearchCriteriaDTO searchCriteria) {
        setCriteria(searchCriteria);
        return new ArrayList<WorkflowServiceError>();
    }

    public SearchAttributeCriteriaComponent getSearchableAttributeByFieldName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Attempted to find Searchable Attribute with blank Field name '" + name + "'");
        }
        for (SearchAttributeCriteriaComponent critComponent : getCriteria().getSearchableAttributes())
        {

            if (name.equals(critComponent.getFormKey()))
            {
                return critComponent;
            }
        }
        return null;
    }

    public void addErrorMessageToList(List<WorkflowServiceError> errors, String message) {
        errors.add(new WorkflowServiceErrorImpl(message,"general.message",message));
    }

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.docsearch.DocumentSearchGenerator#executeSearch(org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO, org.kuali.rice.core.database.platform.DatabasePlatform)
     */
    public String generateSearchSql(DocSearchCriteriaDTO searchCriteria) {
        setCriteria(searchCriteria);
        return getDocSearchSQL();
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

    /* (non-Javadoc)
     * @see org.kuali.rice.kew.docsearch.DocumentSearchGenerator#validateSearchableAttributes()
     */
    public List<WorkflowServiceError> validateSearchableAttributes(DocSearchCriteriaDTO searchCriteria) {
        setCriteria(searchCriteria);
        List<WorkflowServiceError> errors = new ArrayList<WorkflowServiceError>();
        List<SearchAttributeCriteriaComponent> searchableAttributes = criteria.getSearchableAttributes();
        if (searchableAttributes != null && !searchableAttributes.isEmpty()) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            for (SearchAttributeCriteriaComponent component : searchableAttributes) {
                if (!CollectionUtils.isEmpty(component.getValues())) {
                    paramMap.put(component.getFormKey(),component.getValues());
                } else {
                    paramMap.put(component.getFormKey(),component.getValue());
                }
            }
            DocumentType documentType = getValidDocumentType(criteria.getDocTypeFullName());
            try {
                for (SearchableAttribute searchableAttribute : documentType.getSearchableAttributes()) {
                    List<WorkflowAttributeValidationError> searchableErrors = validateSearchableAttribute(
                            searchableAttribute, paramMap, DocSearchUtils.getDocumentSearchContext("", documentType.getName(), ""));
                    if(!CollectionUtils.isEmpty(searchableErrors)){
                        for (WorkflowAttributeValidationError error : searchableErrors) {
	                    	if (error.getMessageMap() != null && error.getMessageMap().hasErrors()) {
	                    		// In order to pass the map along we've added a member to the WorkflowServiceErrorImpl
	                    		errors.add(new WorkflowServiceErrorImpl(error.getKey(), "routetemplate.xmlattribute.error", 
	                    				error.getMessage(), null, error.getMessageMap()));
	                    	} else {
                            errors.add(new WorkflowServiceErrorImpl(error.getKey(), "routetemplate.xmlattribute.error", error.getMessage()));
                        }
                    }
                }
	            }
            } catch (Exception e) {
                LOG.error("error finding searchable attribute in when validating document search criteria.", e);
            }
        }
        return errors;
    }

    public List<WorkflowAttributeValidationError> validateSearchableAttribute(
            SearchableAttribute searchableAttribute, Map searchAttributesParameterMap, DocumentSearchContext documentSearchContext) {
        return searchableAttribute.validateUserSearchInputs(searchAttributesParameterMap, documentSearchContext);
    }

    private Class getSearchableAttributeClass(SearchableAttributeValue sav){
        if(sav instanceof SearchableAttributeDateTimeValue){
            return Timestamp.class;
        }else if(sav instanceof SearchableAttributeFloatValue){
            return Float.TYPE;
        }else if(sav instanceof SearchableAttributeLongValue){
            return Long.TYPE;
        }else if(sav instanceof SearchableAttributeStringValue){
            return String.class;
        }else{
            return null;
        }
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
            GlobalVariables.getMessageMap().putError(KNSConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_CUSTOM, new String[] { "Invalid Date Input: " + stringDate });
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

        if (StringUtils.contains(propertyValue, KNSConstants.BETWEEN_OPERATOR)) {
            String[] rangeValues = propertyValue.split("\\.\\."); // this translate to the .. operator
            sRet = ObjectUtils.clean(rangeValues[0].trim())+ " .. " + cleanUpperBound(ObjectUtils.clean(rangeValues[1].trim()));
        }  else if (propertyValue.startsWith("<=")) {
            sRet = "<=" + cleanUpperBound(ObjectUtils.clean(propertyValue));
        }  else if (propertyValue.startsWith("<")) {
            sRet = "<" + cleanUpperBound(ObjectUtils.clean(propertyValue));
        }

        return sRet;
    }



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
    private void validateBounds(SearchAttributeCriteriaComponent lowerBound, SearchAttributeCriteriaComponent upperBound){

        Class type = getSearchableAttributeClass(lowerBound.getSearchableAttributeValue());
        Class upperType = getSearchableAttributeClass(upperBound.getSearchableAttributeValue());

        // Make sure they are of the same data type
        if(type.getName().compareTo(upperType.getName()) != 0){
            String err = "Type Mismatch. Must compare two like types";
            LOG.error("validateBounds() " + err);
            throw new RuntimeException(err);
        }

        String errorMsg = "The search attribute range values are out of order. Lower bound must be <= Upper Bound. ["
            + lowerBound.getValue() + ", " + upperBound.getValue() + "] for type " + type.getName();

        if(TypeUtils.isIntegralClass(type) || TypeUtils.isDecimalClass(type)){
            // The clean numeric will work with both integer and float values
            BigDecimal lVal = SqlBuilder.stringToBigDecimal(lowerBound.getValue());
            BigDecimal uVal = SqlBuilder.stringToBigDecimal(upperBound.getValue());

            if(lVal.compareTo(uVal) > 0){
                LOG.error("validateBounds() " + errorMsg);
                throw new RuntimeException(errorMsg);
            }

        }else if(TypeUtils.isTemporalClass(type)){
            java.sql.Timestamp lVal = null;
            java.sql.Timestamp uVal = null;
            try{
                lVal = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(lowerBound.getValue());
                uVal = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(upperBound.getValue());
            }catch(Exception ex){
                LOG.error("validateBounds() " + errorMsg);
                throw new RuntimeException(errorMsg, ex);
            }

            if(lVal.compareTo(uVal) > 0){
                LOG.error("validateBounds() " + errorMsg);
                throw new RuntimeException(errorMsg);
            }

        }else if(TypeUtils.isStringClass(type)){
            // this is a complete edge case that should not be allowed to happen but
            // the XSD states that it's valid.
            if(lowerBound.isCaseSensitive() != upperBound.isCaseSensitive()){
                LOG.warn("validateBounds(): Cannot Validate because mismatch case sensitivity ["
                        + lowerBound.getValue() + ", " + upperBound.getValue() + "] for type " + type.getName());
            }else if(lowerBound.isCaseSensitive()){
                if(lowerBound.getValue().compareTo(upperBound.getValue()) > 0){
                    LOG.error("validateBounds() " + errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            }else{
                if(lowerBound.getValue().compareToIgnoreCase(upperBound.getValue()) > 0){
                    LOG.error("validateBounds() " + errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            }
        }
    }

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
     * @param searchableAttributes the contents of this list can be altered
     *
     */
    private void combineAndFormatSearchableComponents(
            List<SearchAttributeCriteriaComponent> searchableAttributes) {

        Map<String, List<SearchAttributeCriteriaComponent>> searchableAttributeRangeComponents = new HashMap<String, List<SearchAttributeCriteriaComponent>>();

        for (SearchAttributeCriteriaComponent criteriaComponent : searchableAttributes)
        {
            if (!criteriaComponent.isSearchable())
            {
                continue;
            }

            SearchableAttributeValue searchAttribute = criteriaComponent.getSearchableAttributeValue();
            if (searchAttribute == null)
            {
                // key given for propertyField must not be on document
                String errorMsg = "The search attribute value associated with key '"
                        + criteriaComponent.getSavedKey() + "' cannot be found";
                LOG.error("getSearchableAttributeSql() " + errorMsg);
                throw new RuntimeException(errorMsg);
            }

            Class clazz = getSearchableAttributeClass(searchAttribute);

            if (criteriaComponent.isRangeSearch())
            {

                if (searchableAttributeRangeComponents.containsKey(criteriaComponent.getSavedKey()))
                {
                    List<SearchAttributeCriteriaComponent> criteriaComponents = searchableAttributeRangeComponents.get(criteriaComponent.getSavedKey());
                    List<SearchAttributeCriteriaComponent> newCriteriaComponents = new ArrayList<SearchAttributeCriteriaComponent>();
                    newCriteriaComponents.addAll(criteriaComponents);
                    newCriteriaComponents.add(criteriaComponent);
                    searchableAttributeRangeComponents.put(criteriaComponent.getSavedKey(), newCriteriaComponents);
                } else
                {
                    searchableAttributeRangeComponents.put(criteriaComponent.getSavedKey(),
                            Arrays.asList(criteriaComponent)
                    );
                }
                // we need to make sure the dates are converted based on case.
                // for upperbound
                if (TypeUtils.isTemporalClass(clazz) && criteriaComponent.isComponentUpperBoundValue())
                {
                    criteriaComponent.setValue(cleanUpperBound(criteriaComponent.getValue()));
                    criteriaComponent.setValues(cleanUpperBounds(criteriaComponent.getValues()));
                }

            } else
            {
                if (TypeUtils.isTemporalClass(clazz))
                {
                    criteriaComponent.setValue(criteriaComponent.getValue());
                }
            }
        }

        // we combined the attributes above into a map of lists. Now for each
        // key, make one SA.
        for (String keyName : searchableAttributeRangeComponents.keySet()) {
            List<SearchAttributeCriteriaComponent> criteriaComponents = searchableAttributeRangeComponents
                    .get(keyName);

            SearchAttributeCriteriaComponent newComp = null;
            SearchAttributeCriteriaComponent lowerBound = null;
            SearchAttributeCriteriaComponent upperBound = null;

            for (SearchAttributeCriteriaComponent component : criteriaComponents) {
                if (component.isComponentLowerBoundValue()) {
                    lowerBound = component;
                } else if (component.isComponentUpperBoundValue()) {
                    upperBound = component;
                } else {
                    String errorMsg = "The search attribute value associated with key '"
                            + component.getSavedKey()
                            + "' is not upper or lower bound";
                    LOG.error("getSearchableAttributeSql() " + errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            }

            // now we have both the upper and lower if they exist. lets make a
            // new single component.
            if (lowerBound != null && upperBound != null) { // between case

                // we need to check and make sure a < b for range values
                validateBounds(lowerBound, upperBound);

                // we need to do this if the search is NOT inclusive. if
                // that's the case then
                // the between operator does not work.

                lowerBound.setRangeSearch(false);
                upperBound.setRangeSearch(false);
                if (lowerBound.isSearchInclusive()) {
                    lowerBound.setValue(">=" + lowerBound.getValue());
                } else {
                    lowerBound.setValue(">" + lowerBound.getValue());
                }
                if (upperBound.isSearchInclusive()) {
                    upperBound.setValue("<=" + upperBound.getValue());
                } else {
                    upperBound.setValue("<" + upperBound.getValue());
                }

            } else if (lowerBound != null) {
                newComp = new SearchAttributeCriteriaComponent(lowerBound
                        .getFormKey(), null, false);
                if (lowerBound.isSearchInclusive()) {
                    newComp.setValue(">=" + lowerBound.getValue());
                } else {
                    newComp.setValue(">" + lowerBound.getValue());
                }
                newComp.setSearchInclusive(lowerBound.isSearchInclusive());
                newComp.setCaseSensitive(lowerBound.isCaseSensitive());
                newComp.setAllowInlineRange(lowerBound.isAllowInlineRange());
                newComp.setCanHoldMultipleValues(lowerBound
                        .isCanHoldMultipleValues());
                newComp.setLookupableFieldType(lowerBound
                        .getLookupableFieldType());
                newComp.setSearchable(true);
                newComp.setSearchableAttributeValue(lowerBound
                        .getSearchableAttributeValue());
                newComp.setSavedKey(lowerBound.getSavedKey());
                searchableAttributes.add(newComp);
            } else if (upperBound != null) {
                newComp = new SearchAttributeCriteriaComponent(upperBound
                        .getFormKey(), null, false);
                if (upperBound.isSearchInclusive()) {
                    newComp.setValue("<=" + upperBound.getValue());
                } else {
                    newComp.setValue("<" + upperBound.getValue());
                }
                newComp.setSearchInclusive(upperBound.isSearchInclusive());
                newComp.setCaseSensitive(upperBound.isCaseSensitive());
                newComp.setAllowInlineRange(upperBound.isAllowInlineRange());
                newComp.setCanHoldMultipleValues(upperBound.isCanHoldMultipleValues());
                newComp.setLookupableFieldType(upperBound.getLookupableFieldType());
                newComp.setSearchable(true);
                newComp.setSearchableAttributeValue(upperBound.getSearchableAttributeValue());
                newComp.setSavedKey(upperBound.getSavedKey());
                searchableAttributes.add(newComp);
            }

        }

        // last step is to remove all range items from the list because we have
        // just combined them into single elements
        for (Iterator<SearchAttributeCriteriaComponent> iterator = searchableAttributes.iterator(); iterator
                .hasNext();) {
            SearchAttributeCriteriaComponent criteriaComponent = (SearchAttributeCriteriaComponent) iterator
                    .next();
            if (!criteriaComponent.isSearchable()) {
                continue;
            }

            if (criteriaComponent.isRangeSearch()) {
                iterator.remove();
            }

        }
    }

    public QueryComponent getSearchableAttributeSql(List<SearchAttributeCriteriaComponent> searchableAttributes, String whereClausePredicatePrefix) {
        /*
         * This method still isn't complete. It now is a hybrid of the old and new way of generating the sql.
         * It's still using the old way to build the select and from parts of the statement, with the new part generating
         * the where.  The new way allows for operators and should clear up a lot of the date issues.
         */

        // this will massage the data and change all range attributes into std ones.
        combineAndFormatSearchableComponents(searchableAttributes);

        StringBuffer fromSql = new StringBuffer();
        StringBuffer whereSql = new StringBuffer();

        int tableIndex = 1;
        String tableAlias = "EXT" + tableIndex;

        Map<String, List<SearchAttributeCriteriaComponent>> searchableAttributeRangeComponents = new HashMap<String,List<SearchAttributeCriteriaComponent>>();
        Criteria finalCriteria = null;

        for (Iterator<SearchAttributeCriteriaComponent> iterator = searchableAttributes.iterator(); iterator.hasNext(); tableIndex++) {
            SearchAttributeCriteriaComponent criteriaComponent = iterator.next();
            if (!criteriaComponent.isSearchable()) {
                continue;
            }

            SqlBuilder sqlBuild = this.getSqlBuilder();

            SearchableAttributeValue searchAttribute = criteriaComponent.getSearchableAttributeValue();
            if (searchAttribute == null) {
                // key given for propertyField must not be on document
                String errorMsg = "The search attribute value associated with key '" + criteriaComponent.getSavedKey() + "' cannot be found";
                LOG.error("getSearchableAttributeSql() " + errorMsg);
                throw new RuntimeException(errorMsg);
            }

            tableAlias = "EXT" + tableIndex;

            Class c = getSearchableAttributeClass(searchAttribute);

            boolean addCaseInsensitivityForValue = (!criteriaComponent.isCaseSensitive()) && criteriaComponent.getSearchableAttributeValue().allowsCaseInsensitivity();

            Criteria crit = null;
            List<String> searchValues = criteriaComponent.getValues();
            if (searchValues != null && !searchValues.isEmpty()) {
                crit = new Criteria(searchAttribute.getAttributeTableName(), tableAlias);
                crit.setDbPlatform(sqlBuild.getDbPlatform());
                crit.in("VAL", criteriaComponent.getValues(), c);
            } else {
                crit = sqlBuild.createCriteria("VAL", criteriaComponent.getValue() , searchAttribute.getAttributeTableName(), tableAlias, c, addCaseInsensitivityForValue, searchAttribute.allowsWildcards());
            }
            sqlBuild.addCriteria("KEY_CD", criteriaComponent.getSavedKey(), String.class, false, false, crit); // this is always of type string.
            sqlBuild.andCriteria("DOC_HDR_ID", tableAlias + ".DOC_HDR_ID", "KREW_DOC_HDR_T", "DOC_HDR", TypeUtils.JoinType.class, false, false, crit);

            if(finalCriteria == null ){
                finalCriteria = crit;
            }else{
                sqlBuild.andCriteria(finalCriteria, crit);
            }

            // - below is the old code
            // if where clause is empty then use passed in prefix... otherwise generate one
            String whereClausePrefix = (whereSql.length() == 0) ? whereClausePredicatePrefix : getGeneratedPredicatePrefix(whereSql.length());
            QueryComponent qc = generateSearchableAttributeSql(criteriaComponent, whereClausePrefix, tableIndex);
            fromSql.append(qc.getFromSql());

        }

        for (String keyName : searchableAttributeRangeComponents.keySet()) {
            List<SearchAttributeCriteriaComponent> criteriaComponents = searchableAttributeRangeComponents.get(keyName);
            // if where clause is empty then use passed in prefix... otherwise generate one
            String whereClausePrefix = (whereSql.length() == 0) ? whereClausePredicatePrefix : getGeneratedPredicatePrefix(whereSql.length());
            QueryComponent qc = generateSearchableAttributeRangeSql(keyName, criteriaComponents, whereClausePrefix, tableIndex);
            fromSql.append(qc.getFromSql());
        }
        
        if (finalCriteria == null) {
            return new QueryComponent("", "", "");
        }

        String whereClausePrefix = (whereSql.length() == 0) ? whereClausePredicatePrefix : getGeneratedPredicatePrefix(whereSql.length());

        return new QueryComponent("",fromSql.toString(),whereClausePrefix + " "+ finalCriteria.buildWhere());
    }

    public QueryComponent generateSearchableAttributeSql(SearchAttributeCriteriaComponent criteriaComponent,String whereSqlStarter,int tableIndex) {
        String tableIdentifier = "EXT" + tableIndex;
        String queryTableColumnName = tableIdentifier + ".VAL";
        QueryComponent joinSqlComponent = getSearchableAttributeJoinSql(criteriaComponent.getSearchableAttributeValue(), tableIdentifier, whereSqlStarter, criteriaComponent.getSavedKey());
        StringBuffer fromSql = new StringBuffer(joinSqlComponent.getFromSql());
        StringBuffer whereSql = new StringBuffer(joinSqlComponent.getWhereSql());

        // removed because we pull the where from somewhere else now.
        //whereSql.append(generateSearchableAttributeDefaultWhereSql(criteriaComponent, queryTableColumnName));

        return new QueryComponent("",fromSql.toString(),whereSql.toString());
    }

    public QueryComponent generateSearchableAttributeRangeSql(String searchAttributeKeyName, List<SearchAttributeCriteriaComponent> criteriaComponents,String whereSqlStarter,int tableIndex) {
        StringBuffer fromSql = new StringBuffer();
        StringBuffer whereSql = new StringBuffer();
        boolean joinAlreadyPerformed = false;
        String tableIdentifier = "EXT" + tableIndex;
        String queryTableColumnName = tableIdentifier + ".VAL";

        for (SearchAttributeCriteriaComponent criteriaComponent : criteriaComponents) {
            if (!searchAttributeKeyName.equals(criteriaComponent.getSavedKey())) {
                String errorMsg = "Key value of searchable attribute component with savedKey '" + criteriaComponent.getSavedKey() + "' does not match specified savedKey value '" + searchAttributeKeyName + "'";
                LOG.error("generateSearchableAttributeRangeSql() " + errorMsg);
                throw new RuntimeException(errorMsg);
            }
            if (!joinAlreadyPerformed) {
                QueryComponent joinSqlComponent = getSearchableAttributeJoinSql(criteriaComponent.getSearchableAttributeValue(), tableIdentifier, whereSqlStarter, searchAttributeKeyName);
                fromSql.append(joinSqlComponent.getFromSql());
                whereSql.append(joinSqlComponent.getWhereSql());
                joinAlreadyPerformed = true;
            }
            whereSql.append(generateSearchableAttributeDefaultWhereSql(criteriaComponent, queryTableColumnName));
        }

        return new QueryComponent("",fromSql.toString(),whereSql.toString());
    }

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
            StringBuffer prefix = new StringBuffer("");
            StringBuffer suffix = new StringBuffer("");
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
                for (String range : KNSConstants.RANGE_CHARACTERS) {
                        int index = StringUtils.indexOf(attributeValueSearched, range);
                    if(index != -1) {
                            sqlOperand=range;
                            if(!StringUtils.equals(sqlOperand, "..")) {
                                attributeValueSearched = StringUtils.remove(attributeValueSearched, range);

                            } else {
                                String[] rangeValues = StringUtils.split(attributeValueSearched, "..");
                                if(rangeValues!=null && rangeValues.length>1) {
                                    checkNumberFormattingIfNumeric(rangeValues[0], valueIsLong, valueIsFloat);

                                    //append first one here and then set the second one and break
                                    whereSqlTemp.append(constructWhereClauseElement(initialClauseStarter, queryTableColumnName, ">=", getDbPlatform().escapeString(rangeValues[0]), prefixToUse, suffixToUse));
                                    attributeValueSearched = rangeValues[1];
                                    sqlOperand = "<=";
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

    public QueryComponent getSearchableAttributeJoinSql(SearchableAttributeValue attributeValue,String tableIdentifier,String whereSqlStarter,String attributeTableKeyColumnName) {
        return new QueryComponent("",generateSearchableAttributeFromSql(attributeValue, tableIdentifier).toString(),generateSearchableAttributeWhereClauseJoin(whereSqlStarter, tableIdentifier, attributeTableKeyColumnName).toString());
    }

    public StringBuffer generateSearchableAttributeWhereClauseJoin(String whereSqlStarter,String tableIdentifier,String attributeTableKeyColumnName) {
        StringBuffer whereSql = new StringBuffer(constructWhereClauseElement(whereSqlStarter, "DOC_HDR.DOC_HDR_ID", "=", getDbPlatform().escapeString(tableIdentifier + ".DOC_HDR_ID"), null, null));
        whereSql.append(constructWhereClauseElement(" and ", tableIdentifier + ".KEY_CD", "=", getDbPlatform().escapeString(attributeTableKeyColumnName), "'", "'"));
        return whereSql;
    }

    public StringBuffer generateSearchableAttributeFromSql(SearchableAttributeValue attributeValue,String tableIdentifier) {
        StringBuffer fromSql = new StringBuffer();
        String tableName = getDbPlatform().escapeString(attributeValue.getAttributeTableName());
        if (StringUtils.isBlank(tableName)) {
            String errorMsg = "The table name associated with Searchable Attribute with class '" + attributeValue.getClass() + "' returns as '" + tableName + "'";
            LOG.error("getSearchableAttributeSql() " + errorMsg);
            throw new RuntimeException(errorMsg);
        }
        fromSql.append(" ," + tableName + " " + getDbPlatform().escapeString(tableIdentifier) + " ");
        return fromSql;
    }

    public StringBuffer constructWhereClauseDateElement(String clauseStarter,String queryTableColumnName,boolean inclusive,boolean valueIsLowerBound,String dateValueToSearch) {
        return constructWhereClauseDateElement(clauseStarter, queryTableColumnName, inclusive, valueIsLowerBound, dateValueToSearch,false);
    }

    public StringBuffer constructWhereClauseDateElement(String clauseStarter,String queryTableColumnName,boolean inclusive,boolean valueIsLowerBound,String dateValueToSearch, boolean isAllowInlineRange) {
        StringBuffer whereSQLBuffer = new StringBuffer();
        StringBuffer sqlOperand = new StringBuffer(getSqlOperand(true, inclusive, valueIsLowerBound, false));
        String lowerTimeBound = "00:00:00";
        String upperTimeBound = "23:59:59";

        String timeValueToSearch = null;
        if (valueIsLowerBound) {
            timeValueToSearch = lowerTimeBound;
        } else {
            timeValueToSearch = upperTimeBound;
        }

        if(isAllowInlineRange) {
            for (String range : KNSConstants.RANGE_CHARACTERS) {
                int index = StringUtils.indexOf(dateValueToSearch, range);
                if(index != -1) {
                    sqlOperand=new StringBuffer(range);
                    if(!StringUtils.equals(sqlOperand.toString(), "..")) {
                        dateValueToSearch = StringUtils.remove(dateValueToSearch,range);
                        if(StringUtils.equals(range, ">")) {
                            timeValueToSearch = upperTimeBound;
                        } else if(StringUtils.equals(range, "<")){
                            timeValueToSearch = lowerTimeBound;
                        }
                    }  else {
                        String[] rangeValues = StringUtils.split(dateValueToSearch, "..");
                        if(rangeValues!=null && rangeValues.length>1) {
                            //Enhancement Idea - Could possibly use recursion here (would have to set the lower bound and inclusive variables
                            //append first one here and then set the second one and break
                            timeValueToSearch = lowerTimeBound;
							whereSQLBuffer.append(constructWhereClauseElement(clauseStarter, queryTableColumnName, ">=", getDbPlatform().getDateSQL(getDbPlatform().escapeString(SQLUtils.getSqlFormattedDate(rangeValues[0].trim())), timeValueToSearch.trim()), "", ""));

                            dateValueToSearch = rangeValues[1];
                            sqlOperand = new StringBuffer("<=");
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

    public StringBuffer constructWhereClauseElement(String clauseStarter,String queryTableColumnName,String operand,String valueToSearch,String valuePrefix,String valueSuffix) {
        StringBuffer whereSql = new StringBuffer();
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
        StringBuffer sqlOperand = new StringBuffer("=");
        if (rangeSearch) {
            if (valueIsLowerBound) {
                sqlOperand = new StringBuffer(">");
            } else {
                sqlOperand = new StringBuffer("<");
            }
            if (inclusive) {
                sqlOperand.append("=");
            }

        } else if (usingWildcards) {
            sqlOperand = new StringBuffer("like");
        }
        return sqlOperand.toString();
    }

    /**
     * @deprecated Removed as of version 0.9.3.  Use {@link #processResultSet(Statement, ResultSet, DocSearchCriteriaDTO, String)} instead.
     */
    public List<DocSearchDTO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet,DocSearchCriteriaDTO searchCriteria) throws SQLException {
        String principalId = null;
        return processResultSet(searchAttributeStatement, resultSet, searchCriteria, principalId);
    }


    /**
     * @param resultSet
     * @param criteria
     * @return
     * @throws SQLException
     */
    public List<DocSearchDTO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet,DocSearchCriteriaDTO searchCriteria, String principalId) throws SQLException {
        setCriteria(searchCriteria);
        int size = 0;
        List<DocSearchDTO> docList = new ArrayList<DocSearchDTO>();
        Map<Long, DocSearchDTO> resultMap = new HashMap<Long, DocSearchDTO>();
        PerformanceLogger perfLog = new PerformanceLogger();
        int iteration = 0;
        boolean resultSetHasNext = resultSet.next();
        while ( resultSetHasNext &&
                ( (searchCriteria.getThreshold() == null) || (resultMap.size() < searchCriteria.getThreshold().intValue()) ) &&
                ( (searchCriteria.getFetchLimit() == null) || (iteration < searchCriteria.getFetchLimit().intValue()) ) ) {
            iteration++;
            DocSearchDTO docCriteriaDTO = processRow(searchAttributeStatement, resultSet);
            docCriteriaDTO.setSuperUserSearch(getCriteria().getSuperUserSearch());
            if (!resultMap.containsKey(docCriteriaDTO.getRouteHeaderId())) {
                docList.add(docCriteriaDTO);
                resultMap.put(docCriteriaDTO.getRouteHeaderId(), docCriteriaDTO);
                size++;
            } else {
                // handle duplicate rows with different search data
                DocSearchDTO previousEntry = (DocSearchDTO)resultMap.get(docCriteriaDTO.getRouteHeaderId());
                handleMultipleDocumentRows(previousEntry, docCriteriaDTO);
            }
            resultSetHasNext = resultSet.next();
        }
        /**
         * Begin IU Customization
         * 05/01/2010 - Eric Westfall
         * EN-1792
         * 
         * Go through all doc search rows after they have been generated to fetch all names.  Attempting to
         * address some significance performance issues with doc search whenever none of the initiators on
         * the returned documents are cached.
         */
        Set<String> initiatorPrincipalIdSet = new HashSet<String>();
        for (DocSearchDTO docSearchRow : docList) {
        	initiatorPrincipalIdSet.add(docSearchRow.getInitiatorWorkflowId());
        }
        List<String> initiatorPrincipalIds = new ArrayList<String>();
        initiatorPrincipalIds.addAll(initiatorPrincipalIdSet);
        if(initiatorPrincipalIds != null && !initiatorPrincipalIds.isEmpty()){ // don't call the service if the search returned nothing.
	        Map<String, KimEntityNamePrincipalNameInfo> entityNames = KimApiServiceLocator.getIdentityService().getDefaultNamesForPrincipalIds(initiatorPrincipalIds);
	        for (DocSearchDTO docSearchRow : docList) {
	        	KimEntityNamePrincipalNameInfo name = entityNames.get(docSearchRow.getInitiatorWorkflowId());
	        	if (name != null) {
	        		docSearchRow.setInitiatorFirstName(name.getDefaultEntityName().getFirstName());
	        		docSearchRow.setInitiatorLastName(name.getDefaultEntityName().getLastName());
	        		docSearchRow.setInitiatorName(name.getDefaultEntityName().getFormattedName());
	        		docSearchRow.setInitiatorNetworkId(name.getPrincipalName());
					if (StringUtils.isNotBlank(name.getDefaultEntityName().getFormattedName())) {
						docSearchRow.setInitiatorTransposedName(name.getDefaultEntityName().getFormattedName());
					} else if (StringUtils.isNotBlank(name.getPrincipalName())) {
						docSearchRow.setInitiatorTransposedName(name.getPrincipalName());
					} else {
						docSearchRow.setInitiatorTransposedName(docSearchRow.getInitiatorWorkflowId());
					}
	        		// it doesn't look like the doc search code even uses the initiator email address for anything
	        		docSearchRow.setInitiatorEmailAddress("");
	        	}
	        }
        }
        /**
         * End IU Customization
         */
        perfLog.log("Time to read doc search results.", true);
        // if we have threshold+1 results, then we have more results than we are going to display
        criteria.setOverThreshold(resultSetHasNext);

        final UserSession userSession = createUserSession(searchCriteria, principalId);
        if (userSession != null) {
            // TODO do we really want to allow the document search if there is no User Session?
            // This is mainly to allow for the unit tests to run but I wonder if we need to push
            // the concept of the "executing user" into the doc search api in some way...
            perfLog = new PerformanceLogger();
            SecuritySession securitySession = new SecuritySession(userSession);
            for (Iterator<DocSearchDTO> iterator = docList.iterator(); iterator.hasNext();) {
                DocSearchDTO docCriteriaDTO = (DocSearchDTO) iterator.next();
                if (!KEWServiceLocator.getDocumentSecurityService().docSearchAuthorized(userSession, docCriteriaDTO, securitySession)) {
                    iterator.remove();
                    criteria.setSecurityFilteredRows(criteria.getSecurityFilteredRows() + 1);
                }
            }
            perfLog.log("Time to filter document search results for security.", true);
        }

        LOG.debug("Processed "+size+" document search result rows.");
        return docList;
    }

    private static UserSession createUserSession(DocSearchCriteriaDTO searchCriteria, String principalId) {
        UserSession userSession = GlobalVariables.getUserSession();
        if ( (userSession == null) && StringUtils.isNotBlank(principalId)) {
            LOG.info("Authenticated User Session is null... using parameter user: " + principalId);
            Person user = KimApiServiceLocator.getPersonService().getPerson(principalId);
            if (user != null) {
            	userSession = new UserSession(user.getPrincipalName());
            }
        } else if (searchCriteria.isOverridingUserSession()) {
            if (principalId == null) {
                LOG.error("Search Criteria specified UserSession override but given user paramter is null");
                throw new WorkflowRuntimeException("Search criteria specified UserSession override but given user is null.");
            }
            LOG.info("Search Criteria specified UserSession override.  Using user: " + principalId);
            Person user = KimApiServiceLocator.getPersonService().getPerson(principalId);
            if (user != null) {
            	userSession = new UserSession(user.getPrincipalName());
            }
        }
        return userSession;
    }

    /**
     * Handles multiple document rows by collapsing them and their data into the searchable attribute columns.
     *
     * TODO this is currently concatenating strings together with HTML elements, this seems bad in this location,
     * perhaps we should move this to the web layer (and perhaps enhance the searchable attributes
     * portion of the DocSearchDTO data structure?)
     */
    public void handleMultipleDocumentRows(DocSearchDTO existingRow, DocSearchDTO newRow) {

        for (KeyValueSort newData : newRow.getSearchableAttributes()) {
            String newRowValue = newData.getValue();
            boolean foundMatch = false;
            for (KeyValueSort existingData : existingRow.getSearchableAttributes()) {
                if (existingData.getKey().equals(newData.getKey())) {
                    String existingRowValue = existingData.getValue();
                    if (!org.apache.commons.lang.StringUtils.isEmpty(newRowValue)) {
                        String valueToSet = "";
                        if (org.apache.commons.lang.StringUtils.isEmpty(existingRowValue)) {
                            valueToSet = newRowValue;
                        } else {
                            valueToSet = existingRowValue + "<br>" + newRowValue;
                        }
                        existingData.setValue(valueToSet);
                        if ( (existingData.getSortValue() == null) && (newData.getSortValue() != null) ) {
                            existingData.setSortValue(newData.getSortValue());
                        }
                    }
                    foundMatch = true;
                }
            }
            if (!foundMatch) {
                existingRow.addSearchableAttribute(new KeyValueSort(newData));
            }
        }
    }

    public DocSearchDTO processRow(Statement searchAttributeStatement, ResultSet rs) throws SQLException {
        DocSearchDTO docCriteriaDTO = new DocSearchDTO();

        docCriteriaDTO.setRouteHeaderId(new Long(rs.getLong("DOC_HDR_ID")));

        String docTypeLabel = rs.getString("LBL");
        String activeIndicatorCode = rs.getString("ACTV_IND");

        docCriteriaDTO.setDocRouteStatusCode(rs.getString("DOC_HDR_STAT_CD"));
        docCriteriaDTO.setDateCreated(rs.getTimestamp("CRTE_DT"));
        docCriteriaDTO.setDocumentTitle(rs.getString("TTL"));
        docCriteriaDTO.setDocTypeName(rs.getString("DOC_TYP_NM"));
        docCriteriaDTO.setDocTypeLabel(docTypeLabel);
        docCriteriaDTO.setAppDocStatus(rs.getString("APP_DOC_STAT"));

        if ((activeIndicatorCode == null) || (activeIndicatorCode.trim().length() == 0)) {
            docCriteriaDTO.setActiveIndicatorCode(KEWConstants.ACTIVE_CD);
        } else {
            docCriteriaDTO.setActiveIndicatorCode(activeIndicatorCode);
        }

        if ((docTypeLabel == null) || (docTypeLabel.trim().length() == 0)) {
            docCriteriaDTO.setDocTypeHandlerUrl("");
        } else {
            docCriteriaDTO.setDocTypeHandlerUrl(rs.getString("DOC_HDLR_URL"));
        }

        docCriteriaDTO.setInitiatorWorkflowId(rs.getString("INITR_PRNCPL_ID"));

        /**
         * Begin IU Customization
         * 05/01/2010 - Eric Westfall
         * EN-1792
         * 
         * Remove the code to fetch the person and principal from their services.  After all rows
         * have been fetched, we will process the names in one big bunch in the method that calls processRow.
         * So we will basically comment out all of the following code.
         */

        /*
        Person user = KIMServiceLocatorInternal.getPersonService().getPerson(docCriteriaDTO.getInitiatorWorkflowId());

        if (user != null) {
            KimPrincipal principal = KimApiServiceLocator.getIdentityManagementService().getPrincipal(docCriteriaDTO.getInitiatorWorkflowId());

            docCriteriaDTO.setInitiatorNetworkId(user.getPrincipalName());
            docCriteriaDTO.setInitiatorName(user.getName());
            docCriteriaDTO.setInitiatorFirstName(user.getFirstName());
            docCriteriaDTO.setInitiatorLastName(user.getLastName());
            docCriteriaDTO.setInitiatorTransposedName(UserUtils.getTransposedName(GlobalVariables.getUserSession(), principal));
            docCriteriaDTO.setInitiatorEmailAddress(user.getEmailAddress());
        }

        */

        /**
         * End IU Customization
         */
        
        if (isUsingAtLeastOneSearchAttribute()) {
            populateRowSearchableAttributes(docCriteriaDTO,searchAttributeStatement);
        }
        return docCriteriaDTO;
    }

    /**
     * This method performs searches against the search attribute value tables (see classes implementing
     * {@link SearchableAttributeValue}) to get data to fill in search attribute values on the given docCriteriaDTO parameter
     *
     * @param docCriteriaDTO - document search result object getting search attributes added to it
     * @param searchAttributeStatement - statement being used to call the database for queries
     * @throws SQLException
     */
    public void populateRowSearchableAttributes(DocSearchDTO docCriteriaDTO, Statement searchAttributeStatement) throws SQLException {
        searchAttributeStatement.setFetchSize(50);
        Long documentId = docCriteriaDTO.getRouteHeaderId();
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
                        docCriteriaDTO.addSearchableAttribute(new KeyValueSort(searchAttValue.getSearchableAttributeKey(),searchAttValue.getSearchableAttributeDisplayValue(),searchAttValue.getSearchableAttributeValue(),searchAttValue));
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

    /**
     * @deprecated As of version 0.9.3 this method is no longer used. Method
     *             {@link #populateRowSearchableAttributes(DocSearchDTO, Statement)} is being used instead.
     */
    @Deprecated
    public void populateRowSearchableAttributes(DocSearchDTO docCriteriaDTO, Statement searchAttributeStatement, ResultSet rs) throws SQLException {
        List<SearchableAttributeValue> searchAttributeValues = DocSearchUtils.getSearchableAttributeValueObjectTypes();
        for (SearchableAttributeValue searchAttValue : searchAttributeValues) {
            String prefixName = searchAttValue.getAttributeDataType().toUpperCase();
            searchAttValue.setSearchableAttributeKey(rs.getString(prefixName + "_KEY"));
            searchAttValue.setupAttributeValue(rs, prefixName + "_VALUE");
            if ( (!org.apache.commons.lang.StringUtils.isEmpty(searchAttValue.getSearchableAttributeKey())) && (searchAttValue.getSearchableAttributeValue() != null) ) {
            docCriteriaDTO.addSearchableAttribute(new KeyValueSort(searchAttValue.getSearchableAttributeKey(),searchAttValue.getSearchableAttributeDisplayValue(),searchAttValue.getSearchableAttributeValue(),searchAttValue));
            }
        }
    }

    public String getDocSearchSQL() {

        String docTypeTableAlias   = "DOC1";
        String docHeaderTableAlias = "DOC_HDR";

        String sqlPrefix = "Select * from (";
        String sqlSuffix = ") FINAL_SEARCH order by FINAL_SEARCH.DOC_HDR_ID desc";
        // the DISTINCT here is important as it filters out duplicate rows which could occur as the result of doc search extension values...
        StringBuffer selectSQL = new StringBuffer("select DISTINCT("+ docHeaderTableAlias +".DOC_HDR_ID), "+ docHeaderTableAlias +".INITR_PRNCPL_ID, "
                + docHeaderTableAlias +".DOC_HDR_STAT_CD, "+ docHeaderTableAlias +".CRTE_DT, "+ docHeaderTableAlias +".TTL, "+ docHeaderTableAlias +".APP_DOC_STAT, "+ docTypeTableAlias +".DOC_TYP_NM, "
                + docTypeTableAlias +".LBL, "+ docTypeTableAlias +".DOC_HDLR_URL, "+ docTypeTableAlias +".ACTV_IND");
        StringBuffer fromSQL = new StringBuffer(" from KREW_DOC_TYP_T "+ docTypeTableAlias +" ");
        StringBuffer fromSQLForDocHeaderTable = new StringBuffer(", KREW_DOC_HDR_T " + docHeaderTableAlias + " ");

        StringBuffer whereSQL = new StringBuffer();
        whereSQL.append(getRouteHeaderIdSql(criteria.getRouteHeaderId(), getGeneratedPredicatePrefix(whereSQL.length()), docHeaderTableAlias));
        whereSQL.append(getInitiatorSql(criteria.getInitiator(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getAppDocIdSql(criteria.getAppDocId(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateCreatedSql(criteria.getFromDateCreated(), criteria.getToDateCreated(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateLastModifiedSql(criteria.getFromDateLastModified(), criteria.getToDateLastModified(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateApprovedSql(criteria.getFromDateApproved(), criteria.getToDateApproved(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getDateFinalizedSql(criteria.getFromDateFinalized(), criteria.getToDateFinalized(), getGeneratedPredicatePrefix(whereSQL.length())));
        // flags for the table being added to the FROM class of the sql
        if ((!"".equals(getViewerSql(criteria.getViewer(), getGeneratedPredicatePrefix(whereSQL.length())))) || (!"".equals(getWorkgroupViewerSql(criteria.getWorkgroupViewerId(), criteria.getWorkgroupViewerName(), getGeneratedPredicatePrefix(whereSQL.length()))))) {
            whereSQL.append(getViewerSql(criteria.getViewer(), getGeneratedPredicatePrefix(whereSQL.length())));
            whereSQL.append(getWorkgroupViewerSql(criteria.getWorkgroupViewerId(), criteria.getWorkgroupViewerName(), getGeneratedPredicatePrefix(whereSQL.length())));
            fromSQL.append(", KREW_ACTN_RQST_T ");
        }

        if (!("".equals(getApproverSql(criteria.getApprover(), getGeneratedPredicatePrefix(whereSQL.length()))))) {
            whereSQL.append(getApproverSql(criteria.getApprover(), getGeneratedPredicatePrefix(whereSQL.length())));
            fromSQL.append(", KREW_ACTN_TKN_T ");
        }



        String docRouteNodeSql = getDocRouteNodeSql(criteria.getDocTypeFullName(), criteria.getDocRouteNodeId(), criteria.getDocRouteNodeLogic(), getGeneratedPredicatePrefix(whereSQL.length()));
        if (!"".equals(docRouteNodeSql)) {
            whereSQL.append(docRouteNodeSql);
            fromSQL.append(", KREW_RTE_NODE_INSTN_T ");
            fromSQL.append(", KREW_RTE_NODE_T ");
        }

        filterOutNonQueryAttributes();
        if ((criteria.getSearchableAttributes() != null) && (criteria.getSearchableAttributes().size() > 0)) {
            QueryComponent queryComponent = getSearchableAttributeSql(criteria.getSearchableAttributes(), getGeneratedPredicatePrefix(whereSQL.length()));
            selectSQL.append(queryComponent.getSelectSql());
            fromSQL.append(queryComponent.getFromSql());
            whereSQL.append(queryComponent.getWhereSql());
        }

        // at this point we haven't appended doc title to the query, if the document title is the only field
        // which was entered, we want to set the "from" date to be X days ago.  This will allow for a
        // more efficient query
        Integer defaultCreateDateDaysAgoValue = null;
//        whereSQL.append(getDocTitleSql(criteria.getDocTitle(), getGeneratedPredicatePrefix(whereSQL.length())));
        String tempWhereSql = getDocTitleSql(criteria.getDocTitle(), getGeneratedPredicatePrefix(whereSQL.length()));
        if ( ((whereSQL == null) || (StringUtils.isBlank(whereSQL.toString()))) && (StringUtils.isNotBlank(tempWhereSql)) ) {
            // doc title is not blank
            defaultCreateDateDaysAgoValue = KEWConstants.DOCUMENT_SEARCH_DOC_TITLE_CREATE_DATE_DAYS_AGO;
        }
        whereSQL.append(tempWhereSql);
        if ( ((whereSQL == null) || (StringUtils.isBlank(whereSQL.toString()))) && (StringUtils.isBlank(criteria.getDocRouteStatus())) ) {
            // if they haven't set any criteria, default the from created date to today minus days from constant variable
            defaultCreateDateDaysAgoValue = KEWConstants.DOCUMENT_SEARCH_NO_CRITERIA_CREATE_DATE_DAYS_AGO;
        }
        if (defaultCreateDateDaysAgoValue != null) {
            // add a default create date
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, defaultCreateDateDaysAgoValue.intValue());
            criteria.setFromDateCreated(RiceConstants.getDefaultDateFormat().format(calendar.getTime()));
            whereSQL.append(getDateCreatedSql(criteria.getFromDateCreated(), criteria.getToDateCreated(), getGeneratedPredicatePrefix(whereSQL.length())));
        }

        String docTypeFullNameSql = getDocTypeFullNameWhereSql(criteria.getDocTypeFullName(), getGeneratedPredicatePrefix(whereSQL.length()));
        if (!("".equals(docTypeFullNameSql))) {
            whereSQL.append(docTypeFullNameSql);
        }
        whereSQL.append(getDocRouteStatusSql(criteria.getDocRouteStatus(), getGeneratedPredicatePrefix(whereSQL.length())));
        whereSQL.append(getGeneratedPredicatePrefix(whereSQL.length())).append(" DOC_HDR.DOC_TYP_ID = DOC1.DOC_TYP_ID ");
        fromSQL.append(fromSQLForDocHeaderTable);
        
        // App Doc Status Value and Transition clauses
        String statusTransitionWhereClause = getStatusTransitionDateSql(criteria.getFromStatusTransitionDate(), criteria.getToStatusTransitionDate(), getGeneratedPredicatePrefix(whereSQL.length()));
        whereSQL.append(getAppDocStatusSql(criteria.getAppDocStatus(), getGeneratedPredicatePrefix(whereSQL.length()), statusTransitionWhereClause.length() ));        	
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

    /**
     * @deprecated As of version 0.9.3 this method is no longer used. This method had been used to create multiple SQL queries if using searchable attributes
     *             and use the sql UNION function to join the queries. The replacement method
     *             {@link #generateFinalSQL(QueryComponent, String, String)} is now used instead.
     */
    @Deprecated
    public String generateFinalSQL(QueryComponent searchSQL,String docHeaderTableAlias, String standardSqlPrefix, String standardSqlSuffix) {
        StringBuffer finalSql = new StringBuffer();
        List<SearchableAttributeValue> searchableAttributeValues = DocSearchUtils.getSearchableAttributeValueObjectTypes();
        List<String> tableAliasComponentNames = new ArrayList<String>(searchableAttributeValues.size());
        for (SearchableAttributeValue attValue : searchableAttributeValues) {
            tableAliasComponentNames.add(attValue.getAttributeDataType().toUpperCase());
        }
        for (SearchableAttributeValue attributeValue : searchableAttributeValues) {
            QueryComponent qc = generateSqlForSearchableAttributeValue(attributeValue, tableAliasComponentNames, docHeaderTableAlias);
            StringBuffer currentSql = new StringBuffer();
            currentSql.append(searchSQL.getSelectSql() + qc.getSelectSql() + searchSQL.getFromSql() + qc.getFromSql() + searchSQL.getWhereSql() + qc.getWhereSql());
            if (finalSql.length() == 0) {
                finalSql.append(standardSqlPrefix).append(" ( ").append(currentSql);
            } else {
                finalSql.append(" ) UNION ( " + currentSql.toString());
            }
        }
        finalSql.append(" ) " + standardSqlSuffix);
        return finalSql.toString();
    }

    /**
     * @deprecated As of version 0.9.3 this method is no longer used. This method had been used to generate SQL to return searchable attributes using left
     *             outer joins. The new mechanism to get search attributes from the database is to call each search attribute
     *             table individually in the {@link #populateRowSearchableAttributes(DocSearchDTO, Statement, ResultSet)}
     *             method.
     */
    @Deprecated
    public QueryComponent generateSqlForSearchableAttributeValue(SearchableAttributeValue attributeValue, List<String> tableAliasComponentNames, String docHeaderTableAlias) {
        StringBuffer selectSql = new StringBuffer();
        StringBuffer fromSql = new StringBuffer();
        String currentAttributeTableAlias = "SA_" + attributeValue.getAttributeDataType().toUpperCase();
        fromSql.append(" LEFT OUTER JOIN " + attributeValue.getAttributeTableName() + " " + currentAttributeTableAlias + " ON (" + docHeaderTableAlias + ".DOC_HDR_ID = " + currentAttributeTableAlias + ".DOC_HDR_ID)");
        for (String string : tableAliasComponentNames) {
            String aliasComponentName = (String) string;
            if (aliasComponentName.equalsIgnoreCase(attributeValue.getAttributeDataType())) {
                selectSql.append(", " + currentAttributeTableAlias + ".KEY_CD as " + aliasComponentName + "_KEY, " + currentAttributeTableAlias + ".VAL as " + aliasComponentName + "_VALUE");
            } else {
                selectSql.append(", NULL as " + aliasComponentName + "_KEY, NULL as " + aliasComponentName + "_VALUE");
            }
        }
        return new QueryComponent(selectSql.toString(),fromSql.toString(),"");
    }

    public String getRouteHeaderIdSql(String routeHeaderId, String whereClausePredicatePrefix, String tableAlias) {

        if ((routeHeaderId == null) || "".equals(routeHeaderId.trim())) {
            return "";
        } else {
            Criteria crit = getSqlBuilder().createCriteria("DOC_HDR_ID", routeHeaderId, "KREW_DOC_HDR_T", tableAlias,Long.TYPE);
            return new StringBuffer(whereClausePredicatePrefix + crit.buildWhere()).toString();
        }

    }

    public String getInitiatorSql(String initiator, String whereClausePredicatePrefix) {
        String tableAlias = "DOC_HDR";

        if ((initiator == null) || "".equals(initiator.trim())) {
            return "";
        }

        Map<String, String> m = new HashMap<String, String>();
        m.put("principalName", initiator);

        // This will search for people with the ability for the valid operands.
        List<Person> pList = KimApiServiceLocator.getPersonService().findPeople(m, false);

        if(pList == null || pList.isEmpty() ){
            // they entered something that returned nothing... so we should return nothing
             return new StringBuffer(whereClausePredicatePrefix + " 1 = 0 ").toString();
        }

        List<String> principalList = new ArrayList<String>();

        for(Person p: pList){
            principalList.add(p.getPrincipalId());
        }

        Criteria crit = new Criteria("KREW_DOC_HDR_T", tableAlias);
        crit.in("INITR_PRNCPL_ID", principalList, String.class);

        //sqlBuild.addCriteria("INITR_PRNCPL_ID", userWorkflowId, tableAlias, String.class, true, false, crit);
        return new StringBuffer(whereClausePredicatePrefix + crit.buildWhere()).toString();

        //return new StringBuffer(whereClausePredicatePrefix + " DOC_HDR.INITR_PRNCPL_ID = '").append(userWorkflowId).append("'").toString();
    }

    public String getDocTitleSql(String docTitle, String whereClausePredicatePrefix) {
        if (StringUtils.isBlank(docTitle)) {
            return "";
        } else {
            /*
            if (!docTitle.trim().endsWith("*")) {
                docTitle = docTitle.trim().concat("*").replace('*', '%');
            } else {
                docTitle = docTitle.trim().replace('*', '%');
            }
            */
            // quick and dirty ' replacement that isn't the best but should work for all dbs
            docTitle = docTitle.trim().replace("\'", "\'\'");


            SqlBuilder sqlBuild = new SqlBuilder();
            Criteria crit = new Criteria("KREW_DOC_HDR_T", "DOC_HDR");

            sqlBuild.addCriteria("TTL", docTitle, String.class, true, true, crit);
            return new StringBuffer(whereClausePredicatePrefix + crit.buildWhere()).toString();



            //return new StringBuffer(whereClausePredicatePrefix + " upper(DOC_HDR.TTL) like '%").append(getDbPlatform().escapeString(docTitle.toUpperCase())).append("'").toString();
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
            return new StringBuffer(whereClausePredicatePrefix + crit.buildWhere()).toString();
        }
    }

    public String getDateCreatedSql(String fromDateCreated, String toDateCreated, String whereClausePredicatePrefix) {
        return establishDateString(fromDateCreated, toDateCreated, "KREW_DOC_HDR_T", "DOC_HDR", "CRTE_DT", whereClausePredicatePrefix);
    }

    public String getDateApprovedSql(String fromDateApproved, String toDateApproved, String whereClausePredicatePrefix) {
        return establishDateString(fromDateApproved, toDateApproved, "KREW_DOC_HDR_T", "DOC_HDR", "APRV_DT", whereClausePredicatePrefix);
    }

    public String getDateFinalizedSql(String fromDateFinalized, String toDateFinalized, String whereClausePredicatePrefix) {
        return establishDateString(fromDateFinalized, toDateFinalized, "KREW_DOC_HDR_T", "DOC_HDR", "FNL_DT", whereClausePredicatePrefix);

    }

    public String getDateLastModifiedSql(String fromDateLastModified, String toDateLastModified, String whereClausePredicatePrefix) {
        return establishDateString(fromDateLastModified, toDateLastModified, "KREW_DOC_HDR_T", "DOC_HDR", "STAT_MDFN_DT", whereClausePredicatePrefix);
    }

	public String getStatusTransitionDateSql(String fromStatusTransitionDate, String toStatusTransitionDate, String whereClausePredicatePrefix) {
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
                return new StringBuffer(whereClausePredicatePrefix + " 1 = 0 ").toString();
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

    public String getWorkgroupViewerSql(String id, String workgroupName, String whereClausePredicatePrefix) {
        String sql = "";
        if (!org.apache.commons.lang.StringUtils.isEmpty(workgroupName)) {
            Group group = KimApiServiceLocator.getIdentityManagementService().getGroup(id);
            sql = whereClausePredicatePrefix + " DOC_HDR.DOC_HDR_ID = KREW_ACTN_RQST_T.DOC_HDR_ID and KREW_ACTN_RQST_T.GRP_ID = " + group.getId();
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
        StringBuffer returnSql = new StringBuffer("");
        if ((docTypeFullName != null) && (!"".equals(docTypeFullName.trim()))) {
            /*
            DocumentTypeDAOOjbImpl


            Map<String, String> m = new HashMap<String, String>();
            m.put("name", docTypeFullName);

            Collection c = KNSServiceLocatorInternal.getBusinessObjectDao().findMatching(DocumentType.class, m);
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

    public void addChildDocumentTypes(StringBuffer whereSql, Collection<DocumentType> childDocumentTypes) {
        for (DocumentType child : childDocumentTypes) {
            addDocumentTypeNameToSearchOn(whereSql, child.getName());
            addChildDocumentTypes(whereSql, child.getChildrenDocTypes());
        }
    }

    public void addExtraDocumentTypesToSearch(StringBuffer whereSql,DocumentType docType) {}

    public void addDocumentTypeNameToSearchOn(StringBuffer whereSql,String documentTypeName) {
        this.addDocumentTypeNameToSearchOn(whereSql, documentTypeName, " or ");
    }

    public void addDocumentTypeNameToSearchOn(StringBuffer whereSql,String documentTypeName, String clause) {
        whereSql.append(clause).append(" DOC1.DOC_TYP_NM = '" + documentTypeName + "'");
    }
    public void addDocumentTypeLikeNameToSearchOn(StringBuffer whereSql,String documentTypeName, String clause) {
        documentTypeName = documentTypeName.replace('*', '%');
        whereSql.append(clause).append(" DOC1.DOC_TYP_NM LIKE '" + documentTypeName + "'");
    }

    public String getDocRouteNodeSql(String documentTypeFullName, String docRouteLevel, String docRouteLevelLogic, String whereClausePredicatePrefix) {
        // -1 is the default 'blank' choice from the route node drop down a number is used because the ojb RouteNode object is used to
        // render the node choices on the form.
        String returnSql = "";
        if ((docRouteLevel != null) && (!"".equals(docRouteLevel.trim())) && (!docRouteLevel.equals("-1"))) {
        	
            /**
        	 * Begin IU Customization
        	 * 04-14-2010 - Shannon Hess
        	 * 
        	 * Using the docRouteLevel, get the corresponding route node name and use that for the comparison.  EN-1698.
        	 * 
        	 */
    		
        	String searchCriteriaRouteNodeName = "";
        	try {
        		long docRouteLevelLong = Long.parseLong(docRouteLevel);
        		RouteNode searchCriteriaRouteNode = KEWServiceLocator.getRouteNodeService().findRouteNodeById(docRouteLevelLong);
        		
        		if (searchCriteriaRouteNode != null) {
        			searchCriteriaRouteNodeName = searchCriteriaRouteNode.getRouteNodeName();
        		}
        	} catch (java.lang.NumberFormatException e) {
        		searchCriteriaRouteNodeName = docRouteLevel;
        	}
    				
            StringBuffer routeNodeCriteria = new StringBuffer("and " + ROUTE_NODE_TABLE + ".NM ");
            if (KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIER_EXACT.equalsIgnoreCase(docRouteLevelLogic.trim())) {
        		routeNodeCriteria.append("= '" + getDbPlatform().escapeString(searchCriteriaRouteNodeName) + "' ");
            } else {
                routeNodeCriteria.append("in (");
                // below buffer used to facilitate the addition of the string ", " to separate out route node names
                StringBuffer routeNodeInCriteria = new StringBuffer();
                boolean foundSpecifiedNode = false;
                List<RouteNode> routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(getValidDocumentType(documentTypeFullName), true);
                for (RouteNode routeNode : routeNodes) {
                    if (searchCriteriaRouteNodeName.equals(routeNode.getRouteNodeName())) {
              /**
               * End IU Customization
               */
                        // current node is specified node so we ignore it outside of the boolean below
                        foundSpecifiedNode = true;
                        continue;
                    }
                    // below logic should be to add the current node to the criteria if we haven't found the specified node
                    // and the logic qualifier is 'route nodes before specified'... or we have found the specified node and
                    // the logic qualifier is 'route nodes after specified'
                    if ( (!foundSpecifiedNode && (KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIER_BEFORE.equalsIgnoreCase(docRouteLevelLogic.trim()))) ||
                         (foundSpecifiedNode && (KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIER_AFTER.equalsIgnoreCase(docRouteLevelLogic.trim()))) ) {
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

    public String getDocRouteStatusSql(String docRouteStatuses, String whereClausePredicatePrefix) {
        if ((docRouteStatuses == null) || "".equals(docRouteStatuses.trim())) {
            return whereClausePredicatePrefix + "DOC_HDR.DOC_HDR_STAT_CD != '" + KEWConstants.ROUTE_HEADER_INITIATED_CD + "'";
        } else {

            // doc status can now be a comma deliminated list
            List<String> docRouteStatusList = Arrays.asList(docRouteStatuses.split(","));
            String inList = "";

            for(String docRouteStatus : docRouteStatusList){
                if(KEWConstants.DOCUMENT_STATUS_PARENT_TYPES.containsKey(docRouteStatus)){
                    // build the sql
                    for(String docStatusCd : KEWConstants.DOCUMENT_STATUS_PARENT_TYPES.get(docRouteStatus)){
                        inList += "'" + getDbPlatform().escapeString(docStatusCd.trim()) + "',";
                    }
                } else{
                    inList += "'" + getDbPlatform().escapeString(docRouteStatus.trim()) + "',";
                }
            }
            inList = inList.substring(0,inList.length()-1); // remove trailing ','

            return whereClausePredicatePrefix + " DOC_HDR.DOC_HDR_STAT_CD in (" + inList +")";
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


    // ---- utility methods

    /**
     * TODO we should probably clean this up some, but we are going to exclude those KeyValues
     * that have a null label.  This will happen in the case of Quickfinders which don't really
     * represent criteria anyway.  Note however, that it is legal for the label to be the empty string.
     * At some point we will probably need to do some more work to untangle this mess
     */
    public void filterOutNonQueryAttributes() {
        List<SearchAttributeCriteriaComponent> newAttributes = new ArrayList<SearchAttributeCriteriaComponent>();
        for (SearchAttributeCriteriaComponent component : criteria.getSearchableAttributes()) {
            if (component != null) {
                if ( (StringUtils.isNotBlank(component.getValue())) || (!CollectionUtils.isEmpty(component.getValues())) ) {
                    newAttributes.add(component);
                }
            }
        }
        criteria.setSearchableAttributes(newAttributes);
    }

    public String getGeneratedPredicatePrefix(int whereClauseSize) {
        return (whereClauseSize > 0) ? " and " : " where ";
    }

    public String establishDateString(String fromDate, String toDate, String tableName, String tableAlias, String colName, String whereStatementClause) {
  /*
        String[] splitPropVal = StringUtils.split(columnDbName, "\\.");
        String tableAlias = splitPropVal[0];
        String colName = splitPropVal[1];
*/

        String searchVal = "";

    	if(fromDate != null && !"".equals(fromDate)) {
    		try {   			
    			CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(fromDate);
    		} catch (Exception exc) { throw new RiceRuntimeException("Invalid date format", exc); }
    	}
    	
        if(toDate != null && !"".equals(toDate)){
            try{
                java.sql.Timestamp dt = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(toDate);
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

                if("00:00:00".equals(sdfTime.format(dt))){
                    toDate += " 23:59:59";
                }
            }
            catch (Exception exc) { throw new RiceRuntimeException("Invalid date format", exc); }
        }


        if(fromDate != null && toDate != null && !"".equals(fromDate) && !"".equals(toDate)){
            searchVal = fromDate + " .. " + toDate;
        }else{
            if(fromDate != null && !"".equals(fromDate)){
                searchVal = ">= " + fromDate;
            }else if(toDate != null && !"".equals(toDate)){
                searchVal = "<= " + toDate;
            } else {
				searchVal =  "";
			}
        }

        if(searchVal == null || "".equals(searchVal)) {
			return "";
		}

        Criteria crit = getSqlBuilder().createCriteria(colName, searchVal, tableName, tableAlias, java.sql.Date.class, true, true);
        return new StringBuffer(whereStatementClause + crit.buildWhere()).toString();

/*


        DatabasePlatform platform = getDbPlatform();
        StringBuffer dateSqlString = new StringBuffer(whereStatementClause).append(" " + platform.escapeString(columnDbName) + " ");
        if (fromDate != null && DocSearchUtils.getSqlFormattedDate(fromDate) != null && toDate != null && DocSearchUtils.getSqlFormattedDate(toDate) != null) {
            return dateSqlString.append(" >= " + DocSearchUtils.getDateSQL(platform.escapeString(DocSearchUtils.getSqlFormattedDate(fromDate.trim())), null) + " and " + platform.escapeString(columnDbName) + " <= " + DocSearchUtils.getDateSQL(platform.escapeString(DocSearchUtils.getSqlFormattedDate(toDate.trim())), "23:59:59")).toString();
        } else {
            if (fromDate != null && DocSearchUtils.getSqlFormattedDate(fromDate) != null) {
                return dateSqlString.append(" >= " + DocSearchUtils.getDateSQL(platform.escapeString(DocSearchUtils.getSqlFormattedDate(fromDate.trim())), null)).toString();
            } else if (toDate != null && DocSearchUtils.getSqlFormattedDate(toDate) != null) {
                return dateSqlString.append(" <= " + DocSearchUtils.getDateSQL(platform.escapeString(DocSearchUtils.getSqlFormattedDate(toDate.trim())), "23:59:59")).toString();
            } else {
                return "";
            }
        }
*/
    }

    public int getDocumentSearchResultSetLimit() {
        return DEFAULT_SEARCH_RESULT_CAP;
    }

    public boolean isProcessResultSet(){
        return this.isProcessResultSet;
    }
    public void setProcessResultSet(boolean isProcessResultSet){
        this.isProcessResultSet = isProcessResultSet;
    }

    public DatabasePlatform getDbPlatform() {
        if (dbPlatform == null) {
            dbPlatform = (DatabasePlatform) GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
        }
        return dbPlatform;
    }

    public MessageMap getMessageMap(DocSearchCriteriaDTO searchCriteria) {
        setCriteria(searchCriteria);
        return this.messageMap;
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
     * A helper method for determining whether any searchable attributes are in use for the search. Subclasses can override this method to add their
     * own logic for checking searchable attribute existence.
     * 
     * @return True if the search criteria contains at least one searchable attribute or the criteria's doc type name is non-blank; false otherwise.
     */
    protected boolean isUsingAtLeastOneSearchAttribute() {
        return ( (criteria.getSearchableAttributes() != null && criteria.getSearchableAttributes().size() > 0) ||
                StringUtils.isNotBlank(criteria.getDocTypeFullName()) );
    }

}
