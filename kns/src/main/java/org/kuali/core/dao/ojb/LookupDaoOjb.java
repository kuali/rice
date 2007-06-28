/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.dao.ojb;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.LookupDao;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypeUtils;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springmodules.orm.ojb.OjbOperationException;


/**
 * This class is the OJB implementation of the LookupDao interface.
 */
public class LookupDaoOjb extends PlatformAwareDaoBaseOjb implements LookupDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupDaoOjb.class);
    
    private PersistenceStructureService persistenceStructureService;
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    // TODO WARNING: this does not support nested joins, because i don't have a test case
    public Collection findCollectionBySearchHelperWithUniversalUserJoin(Class businessObjectClass, Map nonUniversalUserSearchCriteria, Map universalUserSearchCriteria, boolean unbounded, boolean usePrimaryKeyValuesOnly) {
        PersistableBusinessObject businessObject = checkBusinessObjectClass(businessObjectClass);
        Criteria criteria;
        if (usePrimaryKeyValuesOnly) {
            criteria = getCollectionCriteriaFromMapUsingPrimaryKeysOnly(businessObjectClass, nonUniversalUserSearchCriteria);
        }
        else {
            criteria = getCollectionCriteriaFromMap(businessObject, nonUniversalUserSearchCriteria);
            Iterator universalUserReferenceItr = universalUserSearchCriteria.keySet().iterator();
            UniversalUser universalUserExample = new UniversalUser();
            while (universalUserReferenceItr.hasNext()) {
                String institutionalIdSourcePrimitivePropertyName = (String)universalUserReferenceItr.next();
                Map universalUserReferenceSearchCriteria = (Map)universalUserSearchCriteria.get(institutionalIdSourcePrimitivePropertyName);
                Iterator universalUserReferenceSearchCriterionItr = universalUserReferenceSearchCriteria.keySet().iterator();
                Criteria universalUserSubCriteria = new Criteria();
                while (universalUserReferenceSearchCriterionItr.hasNext()) {
                    String universalUserSearchFieldName = (String)universalUserReferenceSearchCriterionItr.next();
                	Boolean caseInsensitive = Boolean.FALSE;
                	if ( KNSServiceLocator.getDataDictionaryService().isAttributeDefined( businessObjectClass, universalUserSearchFieldName )) {
                		caseInsensitive = !KNSServiceLocator.getDataDictionaryService().getAttributeForceUppercase( UniversalUser.class, universalUserSearchFieldName );
                	}
                	if ( caseInsensitive == null ) { caseInsensitive = Boolean.FALSE; }
                    createCriteria(universalUserExample, (String)universalUserReferenceSearchCriteria.get(universalUserSearchFieldName), universalUserSearchFieldName, caseInsensitive, universalUserSubCriteria);
                }
                ReportQueryByCriteria universalUserSubQuery = QueryFactory.newReportQuery(UniversalUser.class, universalUserSubCriteria);
                universalUserSubQuery.setAttributes(new String[] { "personUniversalIdentifier" });
                criteria.addIn(institutionalIdSourcePrimitivePropertyName, universalUserSubQuery);
            }
        }
        return executeSearch(businessObjectClass, criteria, unbounded);
    }
    
    public Collection findCollectionBySearchHelper(Class businessObjectClass, Map formProps, boolean unbounded, boolean usePrimaryKeyValuesOnly) {
        return findCollectionBySearchHelper( businessObjectClass, formProps, unbounded, usePrimaryKeyValuesOnly, null );
    }

    public Collection findCollectionBySearchHelper(Class businessObjectClass, Map formProps, boolean unbounded, boolean usePrimaryKeyValuesOnly, Object additionalCriteria ) {
        PersistableBusinessObject businessObject = checkBusinessObjectClass(businessObjectClass);
        if (usePrimaryKeyValuesOnly) {
            return executeSearch(businessObjectClass, getCollectionCriteriaFromMapUsingPrimaryKeysOnly(businessObjectClass, formProps), unbounded);
        }
        else {
            Criteria crit = getCollectionCriteriaFromMap(businessObject, formProps);
            if ( additionalCriteria != null && additionalCriteria instanceof Criteria ) {
                crit.addAndCriteria( (Criteria)additionalCriteria );
            }
            return executeSearch(businessObjectClass, crit, unbounded);
        }
    }
    
    /**
     * Builds up criteria object based on the object and map.
     */
    public Criteria getCollectionCriteriaFromMap(PersistableBusinessObject example, Map formProps) {
        Criteria criteria = new Criteria();
        Iterator propsIter = formProps.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            if (formProps.get(propertyName) instanceof Collection) {
                Iterator iter = ((Collection) formProps.get(propertyName)).iterator();
                while (iter.hasNext()) {
                	Boolean caseInsensitive = Boolean.FALSE;
                	if ( KNSServiceLocator.getDataDictionaryService().isAttributeDefined( example.getClass(), propertyName )) {
                		caseInsensitive = !KNSServiceLocator.getDataDictionaryService().getAttributeForceUppercase( example.getClass(), propertyName );
                	}
                	if ( caseInsensitive == null ) { caseInsensitive = Boolean.FALSE; }
                    if (!createCriteria(example, (String) iter.next(), propertyName, caseInsensitive.booleanValue(), criteria )) {
                        throw new RuntimeException("Invalid value in Collection");
                    }
                }
            }
            else {
            	Boolean caseInsensitive = Boolean.FALSE;
            	if ( KNSServiceLocator.getDataDictionaryService().isAttributeDefined( example.getClass(), propertyName )) {
            		caseInsensitive = !KNSServiceLocator.getDataDictionaryService().getAttributeForceUppercase( example.getClass(), propertyName );
            	}
            	if ( caseInsensitive == null ) { caseInsensitive = Boolean.FALSE; }
                if (!createCriteria(example, (String) formProps.get(propertyName), propertyName, caseInsensitive.booleanValue(), criteria)) {
                    continue;
                }
            }
        }
        return criteria;
    }
    
    public Criteria getCollectionCriteriaFromMapUsingPrimaryKeysOnly(Class businessObjectClass, Map formProps) {
        PersistableBusinessObject businessObject = checkBusinessObjectClass(businessObjectClass);
        Criteria criteria = new Criteria();
        List pkFields = persistenceStructureService.listPrimaryKeyFieldNames(businessObjectClass);
        Iterator pkIter = pkFields.iterator();
        while (pkIter.hasNext()) {
            String pkFieldName = (String) pkIter.next();
            String pkValue = (String) formProps.get(pkFieldName);
            
            if (StringUtils.isBlank(pkValue)) {
                throw new RuntimeException("Missing pk value for field " + pkFieldName + " when a search based on PK values only is performed.");
            }
            else if (StringUtils.indexOfAny(pkValue, Constants.QUERY_CHARACTERS) != -1) {
                throw new RuntimeException("Value \"" + pkValue + "\" for PK field " + pkFieldName + " contains wildcard/operator characters.");
            }
            createCriteria(businessObject, pkValue, pkFieldName, false, criteria);
        }
        return criteria;
    }
    
    private PersistableBusinessObject checkBusinessObjectClass(Class businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("BusinessObject class passed to LookupDaoOjb findCollectionBySearchHelper... method was null");
        }
        PersistableBusinessObject businessObject = null;
        try {
            businessObject = (PersistableBusinessObject) businessObjectClass.newInstance();
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("LookupDaoOjb could not get instance of " + businessObjectClass.getName(), e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("LookupDaoOjb could not get instance of " + businessObjectClass.getName(), e);
        }
        return businessObject;
    }
    
    private Collection executeSearch(Class businessObjectClass, Criteria criteria, boolean unbounded) {
        Collection searchResults = new ArrayList();
        Long matchingResultsCount = null;
         try {
            Integer searchResultsLimit = LookupUtils.getApplicationSearchResultsLimit();
            if (!unbounded && (searchResultsLimit != null)) {
                matchingResultsCount = new Long(getPersistenceBrokerTemplate().getCount(QueryFactory.newQuery(businessObjectClass, criteria)));
                LookupUtils.applySearchResultsLimit(criteria, getDbPlatform());
                    }
            if ((matchingResultsCount == null) || (matchingResultsCount.intValue() <= searchResultsLimit.intValue())) {
                matchingResultsCount = new Long(0);
                }
            searchResults = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(businessObjectClass, criteria));
            // populate UniversalUser objects in business objects
            List bos = new ArrayList();
            bos.addAll(searchResults);
            searchResults = bos;
        }
        catch (OjbOperationException e) {
            // TODO when KULRNE-4326 is done, replace the 2 lines below with this: throw new RuntimeException("LookupDaoOjb encountered exception during executeSearch", e);
            LOG.debug("Caught OjbOperationException while executing search: " + businessObjectClass, e);
            GlobalVariables.getErrorMap().putError(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_CUSTOM, new String[] { "Invalid Input" });
        }
        catch (DataIntegrityViolationException e) {
            // TODO when KULRNE-4326 is done, replace the 2 lines below with this: throw new RuntimeException("LookupDaoOjb encountered exception during executeSearch", e);
            LOG.debug("Caught DataIntegrityViolationException while executing search: " + businessObjectClass, e);
            GlobalVariables.getErrorMap().putError(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_CUSTOM, new String[] { "Invalid Input" });
        }
        return new CollectionIncomplete(searchResults, matchingResultsCount);   
    }

    /**
     * Return whether or not an attribute is writeable. This method is aware that that Collections
     * may be involved and handles them consistently with the way in which OJB handles specifying
     * the attributes of elements of a Collection.
     * 
     * @param o
     * @param p
     * @return
     * @throws IllegalArgumentException
     */
    private boolean isWriteable(Object o, String p) throws IllegalArgumentException {
        
        if(null == o || null == p) {
            
            throw new IllegalArgumentException("Cannot check writeable status with null arguments.");
            
        }
        
        boolean b = false;
        
        // Try the easy way.
        if(!(PropertyUtils.isWriteable(o, p))) {
            
            // If that fails lets try to be a bit smarter, understanding that Collections may be involved.
            if(-1 != p.indexOf('.')) {
                
                String[] parts = p.split("\\.");
                
                // Get the type of the attribute.
                Class c = ObjectUtils.getPropertyType(o, parts[0], persistenceStructureService);
                
                Object i = null;
                
                // If the next level is a Collection, look into the collection, to find out what type its elements are.
                if(Collection.class.isAssignableFrom(c)) {
                    
                    Map<String, Class> m = persistenceStructureService.listCollectionObjectTypes(o.getClass());
                    c = m.get(parts[0]);
                    
                }
                
                // Look into the attribute class to see if it is writeable.
                try {
                    
                    i = c.newInstance();
                    
                    StringBuffer sb = new StringBuffer();
                    for(int x = 1; x < parts.length; x++) {
                        sb.append(1 == x ? "" : ".").append(parts[x]);
                    }
                    b = isWriteable(i, sb.toString());
                    
                } catch(InstantiationException ie) {
                    LOG.info(ie);
                } catch(IllegalAccessException iae) {
                    LOG.info(iae);
                }
                
            }
            
        } else {
            
            b = true;
            
        }
        
        return b;
        
    }

    public boolean createCriteria(Object example, String searchValue, String propertyName, Criteria criteria) {
    	return createCriteria( example, searchValue, propertyName, false, criteria );
    }
    
    public boolean createCriteria(Object example, String searchValue, String propertyName, boolean caseInsensitive, Criteria criteria) {
        
        // if searchValue is empty and the key is not a valid property ignore
        if (StringUtils.isBlank(searchValue) || !isWriteable(example, propertyName)) {
            
            return false;
            
        }

        // get property type which is used to determine type of criteria
        Class propertyType = ObjectUtils.getPropertyType(example, propertyName, persistenceStructureService);
        if (propertyType == null) {
            return false;
        }

        // build criteria
        addCriteria(propertyName, searchValue, propertyType, caseInsensitive, criteria);
        return true;
    }


    /**
     * Find count of records meeting criteria based on the object and map.
     */
    public Long findCountByMap(Object example, Map formProps) {
        Criteria criteria = new Criteria();
        // iterate through the parameter map for key values search criteria
        Iterator propsIter = formProps.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String searchValue = (String) formProps.get(propertyName);

            // if searchValue is empty and the key is not a valid property ignore
            if (StringUtils.isBlank(searchValue) || !(PropertyUtils.isWriteable(example, propertyName))) {
                continue;
            }

            // get property type which is used to determine type of criteria
            Class propertyType = ObjectUtils.getPropertyType(example, propertyName, persistenceStructureService);
            if (propertyType == null) {
                continue;
            }
        	Boolean caseInsensitive = Boolean.FALSE;
        	if ( KNSServiceLocator.getDataDictionaryService().isAttributeDefined( example.getClass(), propertyName )) {
        		caseInsensitive = !KNSServiceLocator.getDataDictionaryService().getAttributeForceUppercase( example.getClass(), propertyName );
        	}
        	if ( caseInsensitive == null ) { caseInsensitive = Boolean.FALSE; }

            // build criteria
            addCriteria(propertyName, searchValue, propertyType, caseInsensitive, criteria);
        }

        // execute query and return result list
        Query query = QueryFactory.newQuery(example.getClass(), criteria);

        return new Long(getPersistenceBrokerTemplate().getCount(query));
    }

    /**
     * @see org.kuali.core.dao.LookupDao#findObjectByMap(java.lang.Object, java.util.Map)
     */
    public Object findObjectByMap(Object example, Map formProps) {
        Criteria criteria = new Criteria();

        // iterate through the parameter map for key values search criteria
        Iterator propsIter = formProps.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            String searchValue = "";
            if (formProps.get(propertyName) != null) {
                searchValue = (formProps.get(propertyName)).toString();
            }

            if (StringUtils.isNotBlank(searchValue) & PropertyUtils.isWriteable(example, propertyName)) {
                criteria.addEqualTo(propertyName, searchValue);
            }
        }

        // execute query and return result list
        Query query = QueryFactory.newQuery(example.getClass(), criteria);
        return getPersistenceBrokerTemplate().getObjectByQuery(query);
    }


    /**
     * Adds to the criteria object based on the property type and any query characters given.
     */
    private void addCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria) {
        
        if (StringUtils.contains(propertyValue, Constants.OR_LOGICAL_OPERATOR)) {
            addOrCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria);
            return;
        }

        if (StringUtils.contains(propertyValue, Constants.AND_LOGICAL_OPERATOR)) {
            addAndCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria);
            return;
        }
        
        if (TypeUtils.isStringClass(propertyType)) {
        	// KULRICE-85 : made string searches case insensitive - used new DBPlatform function to force strings to upper case
        	if ( caseInsensitive ) {
        		propertyName = getDbPlatform().getUpperCaseFunction() + "(" + propertyName + ")";
        		propertyValue = propertyValue.toUpperCase();
        	}
            if (StringUtils.contains(propertyValue, Constants.NOT_LOGICAL_OPERATOR)) {
                addNotCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria);
            } else if (StringUtils.contains(propertyValue, "..") || StringUtils.contains(propertyValue, ">") 
                    || StringUtils.contains(propertyValue, "<")  || StringUtils.contains(propertyValue, ">=") 
                    || StringUtils.contains(propertyValue, "<=")){
                addStringRangeCriteria(propertyName, propertyValue, criteria);
            }
            else {
                criteria.addLike(propertyName, propertyValue);
            }
        } else if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) ) {
            addNumericRangeCriteria(propertyName, propertyValue, criteria);
        } else if (TypeUtils.isTemporalClass(propertyType)) {
            addDateRangeCriteria(propertyName, propertyValue, criteria);
        } else if (TypeUtils.isBooleanClass(propertyType)) {
            criteria.addEqualTo(propertyName, ObjectUtils.clean(propertyValue));
        } else {
            LOG.error("not adding criterion for: " + propertyName + "," + propertyType + "," + propertyValue);
        }
    }


    /**
     * @param propertyName
     * @param propertyValue
     * @param propertyType
     * @param criteria
     */
    private void addOrCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria) {
        addLogicalOperatorCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria, Constants.OR_LOGICAL_OPERATOR);
    }

    /**
     * @param propertyName
     * @param propertyValue
     * @param propertyType
     * @param criteria
     */
    private void addAndCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria) {
        addLogicalOperatorCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria, Constants.AND_LOGICAL_OPERATOR);
    }

    private void addNotCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria) {

        String[] splitPropVal = StringUtils.split(propertyValue, Constants.NOT_LOGICAL_OPERATOR);

        int strLength = splitPropVal.length;
        // if more than one NOT operator assume an implicit and (i.e. !a!b = !a&!b)
        if (strLength > 1) {
            String expandedNot = "!" + StringUtils.join(splitPropVal, Constants.AND_LOGICAL_OPERATOR + Constants.NOT_LOGICAL_OPERATOR);
            addCriteria(propertyName, expandedNot, propertyType, caseInsensitive, criteria);
        }
        else {
            // only one so add a not like
            criteria.addNotLike(propertyName, splitPropVal[0]);
        }
    }


    /**
     * Builds a sub criteria object joined with an 'AND' or 'OR' (depending on splitValue) using the split values of propertyValue. Then joins back the
     * sub criteria to the main criteria using an 'AND'.
     */
    private void addLogicalOperatorCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria, String splitValue) {
        String[] splitPropVal = StringUtils.split(propertyValue, splitValue);
      
        Criteria subCriteria = new Criteria();
        for (int i = 0; i < splitPropVal.length; i++) {
        	Criteria predicate = new Criteria();

            addCriteria(propertyName, splitPropVal[i], propertyType, caseInsensitive, predicate);
            if (splitValue == Constants.OR_LOGICAL_OPERATOR) {
            	subCriteria.addOrCriteria(predicate);
            }
            if (splitValue == Constants.AND_LOGICAL_OPERATOR) {
            	subCriteria.addAndCriteria(predicate);
            }
        }
       
        criteria.addAndCriteria(subCriteria);
    }


    private static final String[] dateFormats = new String[] {
        "MM/dd/yy",        
        "MM/dd/yyyy",
        "MM-dd-yy",        
        "MM-dd-yyyy"
    };
    
    public static java.sql.Date parseDate( String dateString ) {
        dateString = dateString.trim();
        for ( String format : dateFormats ) {
            try {
                return new java.sql.Date( new SimpleDateFormat( format ).parse( dateString ).getTime() );
            } catch ( ParseException ex ) {
                // do nothing, just skip to the next item
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "parsing of " + dateString + " failed using pattern " + format );
                }
            }
        }
        GlobalVariables.getErrorMap().putError(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_CUSTOM, new String[] { "Invalid Date Input: " + dateString });
        return null;
    }
    
    /**
     * Adds to the criteria object based on query characters given
     */
    private void addDateRangeCriteria(String propertyName, String propertyValue, Criteria criteria) {

        if (StringUtils.contains(propertyValue, "..")) {
            String[] rangeValues = StringUtils.split(propertyValue, "..");
            criteria.addBetween(propertyName, parseDate( ObjectUtils.clean(rangeValues[0] ) ), parseDate( ObjectUtils.clean(rangeValues[1] ) ) );
        }
        else if (propertyValue.startsWith(">")) {
            criteria.addGreaterThan(propertyName, parseDate( ObjectUtils.clean(propertyValue) ) );
        }
        else if (propertyValue.startsWith("<")) {
            criteria.addLessThan(propertyName, parseDate( ObjectUtils.clean(propertyValue) ) );
        }
        else if (propertyValue.startsWith(">=")) {
            criteria.addGreaterOrEqualThan(propertyName, parseDate( ObjectUtils.clean(propertyValue) ) );
        }
        else if (propertyValue.startsWith("<=")) {
            criteria.addLessOrEqualThan(propertyName, parseDate( ObjectUtils.clean(propertyValue) ) );
        }
        else {
            criteria.addEqualTo(propertyName, parseDate( ObjectUtils.clean(propertyValue) ) );
        }
    }

    private BigDecimal cleanNumeric( String value ) {
        String cleanedValue = value.replaceAll( "[^-0-9.]", "" );
        // ensure only one "minus" at the beginning, if any 
        if ( cleanedValue.lastIndexOf( '-' ) > 0 ) {
            if ( cleanedValue.charAt( 0 ) == '-' ) {
                cleanedValue = "-" + cleanedValue.replaceAll( "-", "" );
            } else {
                cleanedValue = cleanedValue.replaceAll( "-", "" );
            }
        }
        // ensure only one decimal in the string
        int decimalLoc = cleanedValue.lastIndexOf( '.' );
        if ( cleanedValue.indexOf( '.' ) != decimalLoc ) {
            cleanedValue = cleanedValue.substring( 0, decimalLoc ).replaceAll( "\\.", "" ) + cleanedValue.substring( decimalLoc );
        }
        try {
            return new BigDecimal( cleanedValue );
        } catch ( NumberFormatException ex ) {
            GlobalVariables.getErrorMap().putError(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_CUSTOM, new String[] { "Invalid Numeric Input: " + value });
            return null;
        }
    }
    
    /**
     * Adds to the criteria object based on query characters given
     */
    private void addNumericRangeCriteria(String propertyName, String propertyValue, Criteria criteria) {

        if (StringUtils.contains(propertyValue, "..")) {
            String[] rangeValues = StringUtils.split(propertyValue, "..");
            criteria.addBetween(propertyName, cleanNumeric( rangeValues[0] ), cleanNumeric( rangeValues[1] ));
        }
        else if (propertyValue.startsWith(">")) {
            criteria.addGreaterThan(propertyName, cleanNumeric( propertyValue ) );
        }
        else if (propertyValue.startsWith("<")) {
            criteria.addLessThan(propertyName, cleanNumeric(propertyValue));
        }
        else if (propertyValue.startsWith(">=")) {
            criteria.addGreaterOrEqualThan(propertyName, cleanNumeric(propertyValue));
        }
        else if (propertyValue.startsWith("<=")) {
            criteria.addLessOrEqualThan(propertyName, cleanNumeric(propertyValue));
        }
        else {
            criteria.addEqualTo(propertyName, cleanNumeric(propertyValue));
        }
    }

    /**
     * Adds to the criteria object based on query characters given
     */
    private void addStringRangeCriteria(String propertyName, String propertyValue, Criteria criteria) {

        if (StringUtils.contains(propertyValue, "..")) {
            String[] rangeValues = StringUtils.split(propertyValue, "..");
            criteria.addBetween(propertyName, rangeValues[0], rangeValues[1]);
            
            //To fix a bug related on number of digits issues for searching String field with range operator
            Criteria orCriteria = new Criteria();
            orCriteria.addGreaterThan(propertyName, rangeValues[0].length());
            criteria.addOrCriteria(orCriteria);
            criteria.addLessOrEqualThan(propertyName, rangeValues[1].length());
            
        }
        else if (propertyValue.startsWith(">")) {
            criteria.addGreaterThan(propertyName, ObjectUtils.clean(propertyValue));
            
            //To fix a bug related on number of digits issues for searching String field with range operator
            Criteria orCriteria = new Criteria();
            orCriteria.addGreaterThan(propertyName, ObjectUtils.clean(propertyValue).length());
            criteria.addOrCriteria(orCriteria);
            
        }
        else if (propertyValue.startsWith("<")) {
            criteria.addLessThan(propertyName, ObjectUtils.clean(propertyValue));
            
            //To fix a bug related on number of digits issues for searching String field with range operator
            criteria.addLessOrEqualThan(propertyName, ObjectUtils.clean(propertyValue).length());
        }
        else if (propertyValue.startsWith(">=")) {
            criteria.addGreaterOrEqualThan(propertyName, ObjectUtils.clean(propertyValue));
            
            //To fix a bug related on number of digits issues for searching String field with range operator
            criteria.addGreaterOrEqualThan(propertyName, ObjectUtils.clean(propertyValue).length());
        }
        else if (propertyValue.startsWith("<=")) {
            criteria.addLessOrEqualThan(propertyName, ObjectUtils.clean(propertyValue));
            
            //To fix a bug related on number of digits issues for searching String field with range operator
            criteria.addLessOrEqualThan(propertyName, ObjectUtils.clean(propertyValue).length());
        }
    }
}