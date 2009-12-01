/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.core.jdbc;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.database.platform.DatabasePlatform;
import org.kuali.rice.core.jdbc.criteria.Criteria;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.TypeUtils;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * This is a description of what this class does - Garey don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SqlBuilder {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SqlBuilder.class);

	private DateTimeService dateTimeService;
	private DatabasePlatform dbPlatform;

	public Criteria createCriteria(String columnName, String searchValue, String tableName, String tableAlias, Class propertyType) {
		return createCriteria(columnName, searchValue, tableName, tableAlias, propertyType, false, true);
	}

	public Criteria createCriteria(String columnName, String searchValue, String tableName, String tableAlias, Class propertyType, boolean caseInsensitive, boolean allowWildcards) {

		if (propertyType == null) {
			return null;
		}

		Criteria criteria = new Criteria(tableName, tableAlias);
		criteria.setDbPlatform(this.getDbPlatform());

		// build criteria
		addCriteria(columnName, searchValue, propertyType, caseInsensitive, allowWildcards, criteria);
		return criteria;
	}

	public void andCriteria(String columnName, String searchValue, String tableName, String tableAlias, Class propertyType, boolean caseInsensitive, boolean allowWildcards, Criteria addToThisCriteria) {
		Criteria crit = createCriteria(columnName,searchValue, tableName, tableAlias, propertyType, caseInsensitive, allowWildcards);

		addToThisCriteria.and(crit);
	}
	public void andCriteria(Criteria addToThisCriteria, Criteria newCriteria) {
		addToThisCriteria.and(newCriteria);
	}
	public void orCriteria(String columnName, String searchValue, String tableName, String tableAlias, Class propertyType, boolean caseInsensitive, boolean allowWildcards, Criteria addToThisCriteria) {
		Criteria crit = createCriteria(columnName, searchValue,tableName, tableAlias, propertyType, caseInsensitive, allowWildcards);

		addToThisCriteria.or(crit);
	}

	/**
	 * @see org.kuali.rice.kns.dao.LookupDao#findObjectByMap(java.lang.Object,
	 *      java.util.Map)
	 *
	public Object findObjectByMap(Object example, Map formProps) {
		Criteria jpaCriteria = new Criteria(example.getClass().getName());

		Iterator propsIter = formProps.keySet().iterator();
		while (propsIter.hasNext()) {
			String propertyName = (String) propsIter.next();
			String searchValue = "";
			if (formProps.get(propertyName) != null) {
				searchValue = (formProps.get(propertyName)).toString();
			}

			if (StringUtils.isNotBlank(searchValue) & PropertyUtils.isWriteable(example, propertyName)) {
				Class propertyType = ObjectUtils.getPropertyType(example, propertyName, persistenceStructureService);
				if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType)) {
					if (propertyType.equals(Long.class)) {
						jpaCriteria.eq(propertyName, new Long(searchValue));
					} else {
						jpaCriteria.eq(propertyName, new Integer(searchValue));
					}
				} else if (TypeUtils.isTemporalClass(propertyType)) {
					jpaCriteria.eq(propertyName, parseDate(ObjectUtils.clean(searchValue)));
				} else {
					jpaCriteria.eq(propertyName, searchValue);
				}
			}
		}

		return new QueryByCriteria(entityManager, jpaCriteria).toQuery().getSingleResult();
	}
*/
	/*
	public void addCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, boolean treatWildcardsAndOperatorsAsLiteral, Criteria criteria) {

		String tableAlias = "__JPA_ALIAS__";

		if(criteria != null && criteria.getAlias() != null && !"".equals(criteria.getAlias())){
			tableAlias = criteria.getAlias();
		}
		addCriteria(propertyName, propertyValue, tableAlias,propertyType, caseInsensitive, treatWildcardsAndOperatorsAsLiteral, criteria);
	}
*/

	public void addCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, boolean allowWildcards, Criteria criteria) {

		if(TypeUtils.isJoinClass(propertyType)){ // treat this as a join table.
			String temp = ObjectUtils.clean(propertyValue);
			criteria.eq(propertyName, temp, propertyType);
			return;
		}

		if (StringUtils.contains(propertyValue, KNSConstants.OR_LOGICAL_OPERATOR)) {
			addOrCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria, allowWildcards);
			return;
		}

		if ( StringUtils.contains(propertyValue, KNSConstants.AND_LOGICAL_OPERATOR)) {
			addAndCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria, allowWildcards);
			return;
		}

		if (TypeUtils.isStringClass(propertyType)) {
			if (StringUtils.contains(propertyValue,
					KNSConstants.NOT_LOGICAL_OPERATOR)) {
				addNotCriteria(propertyName, propertyValue, propertyType,
						caseInsensitive, criteria, allowWildcards);
            } else if (propertyValue != null && (
            				StringUtils.contains(propertyValue, KNSConstants.BETWEEN_OPERATOR)
            				|| propertyValue.startsWith(">")
            				|| propertyValue.startsWith("<") ) ) {
				addStringRangeCriteria(propertyName, propertyValue, criteria, propertyType, caseInsensitive, allowWildcards);
			} else {
				//if (!allowWildcards) {
				//	propertyValue = StringUtils.replace(propertyValue, "*", "\\*");
				//}
				// KULRICE-85 : made string searches case insensitive - used new
				// DBPlatform function to force strings to upper case
				if (caseInsensitive) {
					// TODO: What to do here now that the JPA version does not extend platform aware?
					propertyName = getDbPlatform().getUpperCaseFunction() + "(__JPA_ALIAS__." + propertyName + ")";
					//propertyName = "UPPER("+ tableAlias + "." + propertyName + ")";
					propertyValue = propertyValue.toUpperCase();
				}
				criteria.like(propertyName, propertyValue,propertyType, allowWildcards);
			}
		} else if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType)) {
			addNumericRangeCriteria(propertyName, propertyValue, criteria, propertyType);
		} else if (TypeUtils.isTemporalClass(propertyType)) {
			addDateRangeCriteria(propertyName, propertyValue, criteria, propertyType);
		} else if (TypeUtils.isBooleanClass(propertyType)) {
			String temp = ObjectUtils.clean(propertyValue);
			criteria.eq(propertyName, ("Y".equalsIgnoreCase(temp) || "T".equalsIgnoreCase(temp) || "1".equalsIgnoreCase(temp) || "true".equalsIgnoreCase(temp)) ? true : false, propertyType);
		} else {
			LOG.error("not adding criterion for: " + propertyName + "," + propertyType + "," + propertyValue);
		}
	}

	private void addOrCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria, boolean allowWildcards) {
		addLogicalOperatorCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria, KNSConstants.OR_LOGICAL_OPERATOR, allowWildcards);
	}

	private void addAndCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria, boolean allowWildcards) {
		addLogicalOperatorCriteria(propertyName, propertyValue, propertyType, caseInsensitive, criteria, KNSConstants.AND_LOGICAL_OPERATOR, allowWildcards);
	}

	private void addNotCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria, boolean allowWildcards) {
		String[] splitPropVal = StringUtils.split(propertyValue, KNSConstants.NOT_LOGICAL_OPERATOR);

		int strLength = splitPropVal.length;
		// if more than one NOT operator assume an implicit and (i.e. !a!b = !a&!b)
		if (strLength > 1) {
			String expandedNot = "!" + StringUtils.join(splitPropVal, KNSConstants.AND_LOGICAL_OPERATOR + KNSConstants.NOT_LOGICAL_OPERATOR);
			// we know that since this method is called, treatWildcardsAndOperatorsAsLiteral is false
			addCriteria(propertyName, expandedNot, propertyType, caseInsensitive, allowWildcards, criteria);
		} else {
			// only one so add a not like
			criteria.notLike(propertyName, splitPropVal[0], propertyType, allowWildcards);
		}
	}

	private void addLogicalOperatorCriteria(String propertyName, String propertyValue, Class propertyType, boolean caseInsensitive, Criteria criteria, String splitValue, boolean allowWildcards) {
		String[] splitPropVal = StringUtils.split(propertyValue, splitValue);

		Criteria subCriteria = new Criteria("N/A");
		for (int i = 0; i < splitPropVal.length; i++) {
			Criteria predicate = new Criteria("N/A", criteria.getAlias());
			// we know that since this method is called, treatWildcardsAndOperatorsAsLiteral is false
			addCriteria(propertyName, splitPropVal[i], propertyType, caseInsensitive, allowWildcards, predicate);
			if (splitValue == KNSConstants.OR_LOGICAL_OPERATOR) {
				subCriteria.or(predicate);
			}
			if (splitValue == KNSConstants.AND_LOGICAL_OPERATOR) {
				subCriteria.and(predicate);
			}
		}

		criteria.and(subCriteria);
	}

	private Timestamp parseDate(String dateString) {
		dateString = dateString.trim();
		try {
			Timestamp dt =  this.getDateTimeService().convertToSqlTimestamp(dateString);
			return dt;
		} catch (ParseException ex) {
			return null;
		}
	}
	public boolean isValidDate(String dateString){
		dateString = dateString.trim();
		try {
			this.createCriteria("date", dateString, "validation", "test", Date.class);
			//Timestamp dt =  this.getDateTimeService().convertToSqlTimestamp(cleanDate(dateString));
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static String cleanDate(String string) {
        for (int i = 0; i < KNSConstants.RANGE_CHARACTERS.length; i++) {
            string = StringUtils.replace(string, KNSConstants.RANGE_CHARACTERS[i], KNSConstants.EMPTY_STRING);
        }
        return string;
    }

	public static boolean containsRangeCharacters(String string){
		boolean bRet = false;
		for (int i = 0; i < KNSConstants.RANGE_CHARACTERS.length; i++) {
            if(StringUtils.contains(string, KNSConstants.RANGE_CHARACTERS[i])){
            	bRet = true;
            }
        }
		return bRet;
	}

	private void addDateRangeCriteria(String propertyName, String propertyValue, Criteria criteria, Class propertyType) {

		if (StringUtils.contains(propertyValue, KNSConstants.BETWEEN_OPERATOR)) {
			String[] rangeValues = propertyValue.split("\\.\\."); // this translate to the .. operator
			criteria.between(propertyName, parseDate(cleanDate(rangeValues[0])), parseDate(cleanUpperBound(cleanDate(rangeValues[1]))), propertyType);
		} else if (propertyValue.startsWith(">=")) {
			criteria.gte(propertyName, parseDate(cleanDate(propertyValue)), propertyType);
		} else if (propertyValue.startsWith("<=")) {
			criteria.lte(propertyName, parseDate(cleanUpperBound(cleanDate(propertyValue))),propertyType);
		} else if (propertyValue.startsWith(">")) {
			// we clean the upper bound here because if you say >12/22/09, it translates greater than
			// the date... as in whole date. ie. the next day on.
			criteria.gt(propertyName, parseDate(cleanUpperBound(cleanDate(propertyValue))), propertyType);
		} else if (propertyValue.startsWith("<")) {
			criteria.lt(propertyName, parseDate(cleanDate(propertyValue)), propertyType);
		} else {
			String sDate = convertSimpleDateToDateRange(cleanDate(propertyValue));
			if(sDate.indexOf(KNSConstants.BETWEEN_OPERATOR) != -1){
				addDateRangeCriteria(propertyName, sDate, criteria, propertyType);
			}else{
				criteria.eq(propertyName, parseDate(sDate), propertyType);
			}
		}
	}

	public static boolean isValidNumber(String value){
		try{
		BigDecimal bd = stringToBigDecimal(value);
			return true;
		}catch(Exception ex){
			return false;
		}
	}

	public static String cleanNumericOfValidOperators(String string){
		for (int i = 0; i < KNSConstants.RANGE_CHARACTERS.length; i++) {
            string = StringUtils.replace(string, KNSConstants.RANGE_CHARACTERS[i], KNSConstants.EMPTY_STRING);
        }
		string = StringUtils.replace(string, KNSConstants.OR_LOGICAL_OPERATOR, KNSConstants.EMPTY_STRING);
		string = StringUtils.replace(string, KNSConstants.AND_LOGICAL_OPERATOR, KNSConstants.EMPTY_STRING);
		string = StringUtils.replace(string, KNSConstants.NOT_LOGICAL_OPERATOR, KNSConstants.EMPTY_STRING);

		return string;
	}

	public static String cleanNumeric(String value){
		String cleanedValue = value.replaceAll("[^-0-9.]", "");
		// ensure only one "minus" at the beginning, if any
		if (cleanedValue.lastIndexOf('-') > 0) {
			if (cleanedValue.charAt(0) == '-') {
				cleanedValue = "-" + cleanedValue.replaceAll("-", "");
			} else {
				cleanedValue = cleanedValue.replaceAll("-", "");
			}
		}
		// ensure only one decimal in the string
		int decimalLoc = cleanedValue.lastIndexOf('.');
		if (cleanedValue.indexOf('.') != decimalLoc) {
			cleanedValue = cleanedValue.substring(0, decimalLoc).replaceAll("\\.", "") + cleanedValue.substring(decimalLoc);
		}
		return cleanedValue;
	}

	public static BigDecimal stringToBigDecimal(String value) {

		//try {
			return new BigDecimal(cleanNumeric(value));
		/*
		} catch (NumberFormatException ex) {
			GlobalVariables.getMessageMap().putError(KNSConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_CUSTOM, new String[] { "Invalid Numeric Input: " + value });
			return null;
		}*/
	}

	private void addNumericRangeCriteria(String propertyName, String propertyValue, Criteria criteria, Class propertyType) {

		if (StringUtils.contains(propertyValue, KNSConstants.BETWEEN_OPERATOR)) {
			String[] rangeValues = propertyValue.split("\\.\\."); // this translate to the .. operator
			criteria.between(propertyName, stringToBigDecimal(rangeValues[0]), stringToBigDecimal(rangeValues[1]), propertyType);
		} else if (propertyValue.startsWith(">=")) {
			criteria.gte(propertyName, stringToBigDecimal(propertyValue), propertyType);
		} else if (propertyValue.startsWith("<=")) {
			criteria.lte(propertyName, stringToBigDecimal(propertyValue), propertyType);
		} else if (propertyValue.startsWith(">")) {
			criteria.gt(propertyName, stringToBigDecimal(propertyValue), propertyType);
		} else if (propertyValue.startsWith("<")) {
			criteria.lt(propertyName, stringToBigDecimal(propertyValue), propertyType);
		} else {
			criteria.eq(propertyName, stringToBigDecimal(propertyValue), propertyType);
		}
	}

	private void addStringRangeCriteria(String propertyName, String propertyValue, Criteria criteria, Class propertyType, boolean caseInsensitive, boolean allowWildcards) {

		if (StringUtils.contains(propertyValue, KNSConstants.BETWEEN_OPERATOR)) {
			String[] rangeValues = propertyValue.split("\\.\\."); // this translate to the .. operator
			propertyName = this.getCaseAndLiteralPropertyName(propertyName, caseInsensitive);
			String val1 = this.getCaseAndLiteralPropertyValue(rangeValues[0], caseInsensitive, allowWildcards);
			String val2 = this.getCaseAndLiteralPropertyValue(rangeValues[1], caseInsensitive, allowWildcards);
			criteria.between(propertyName, val1, val2, propertyType);
		} else{
			propertyName = this.getCaseAndLiteralPropertyName(propertyName, caseInsensitive);
			String value = this.getCaseAndLiteralPropertyValue(ObjectUtils.clean(propertyValue), caseInsensitive, allowWildcards);

			if (propertyValue.startsWith(">=")) {
				criteria.gte(propertyName, value, propertyType);
			} else if (propertyValue.startsWith("<=")) {
				criteria.lte(propertyName, value, propertyType);
			} else if (propertyValue.startsWith(">")) {
				criteria.gt(propertyName, value, propertyType);
			} else if (propertyValue.startsWith("<")) {
				criteria.lt(propertyName, value, propertyType);
			}
		}
	}

	private String getCaseAndLiteralPropertyName(String propertyName, boolean caseInsensitive){
		// KULRICE-85 : made string searches case insensitive - used new
		// DBPlatform function to force strings to upper case
		if (caseInsensitive) {
			// TODO: What to do here now that the JPA version does not extend platform aware?
			propertyName = getDbPlatform().getUpperCaseFunction() + "(__JPA_ALIAS__." + propertyName + ")";

		}
		return propertyName;
	}
	private String getCaseAndLiteralPropertyValue(String propertyValue, boolean caseInsensitive, boolean allowWildcards){
		//if (!allowWildcards) {
		//	propertyValue = StringUtils.replace(propertyValue, "*", "\\*");
		//}
		// KULRICE-85 : made string searches case insensitive - used new
		// DBPlatform function to force strings to upper case
		if (caseInsensitive) {
			//propertyName = "UPPER("+ tableAlias + "." + propertyName + ")";
			propertyValue = propertyValue.toUpperCase();
		}
		return propertyValue;
	}


	protected DateTimeService getDateTimeService(){
		if (dateTimeService == null) {
			dateTimeService = KNSServiceLocator.getDateTimeService();
    	}
    	return dateTimeService;
	}

	/**
	 * @param dateTimeService
	 *            the dateTimeService to set
	 */
	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	public DatabasePlatform getDbPlatform() {
    	if (dbPlatform == null) {
    		dbPlatform = (DatabasePlatform) GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
    	}
    	return dbPlatform;
    }

	public void setDbPlatform(DatabasePlatform dbPlatform){
		this.dbPlatform = dbPlatform;
	}

	 /**
     * When dealing with upperbound dates, it is a business requirement that if a timestamp isn't already
     * stated append 23:59:59 to the end of the date.  This ensures that you are searching for the entire
     * day.
     */
    private static String cleanUpperBound(String stringDate){
    	try{
    		java.sql.Timestamp dt = KNSServiceLocator.getDateTimeService().convertToSqlTimestamp(stringDate);
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
    * This method will take a whole date like 03/02/2009 and convert it into
    * 03/02/2009 .. 03/02/20009 00:00:00
    *
    * This is used for non-range searchable attributes
    *
    * @param stringDate
    * @return
    */
   private static String convertSimpleDateToDateRange(String stringDate){
   	try{
   		java.sql.Timestamp dt = KNSServiceLocator.getDateTimeService().convertToSqlTimestamp(stringDate);
   		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

			if("00:00:00".equals(sdfTime.format(dt))){
				stringDate = stringDate + " .. " + stringDate + " 23:59:59";
			}
		} catch (Exception ex){
			GlobalVariables.getMessageMap().putError(KNSConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_CUSTOM, new String[] { "Invalid Date Input: " + stringDate });
		}
		return stringDate;
   }

   /**
   *
   * This method splits the values then cleans them of any other query characters like *?!><...
   *
   * @param valueEntered
   * @return
   */
  public static List<String> getCleanedSearchableValues(String valueEntered) {
	   List<String> lRet = null;
	   List<String> lTemp = getSearchableValues(valueEntered);
	   if(lTemp != null && !lTemp.isEmpty()){
		   lRet = new ArrayList<String>();
		   for(String val: lTemp){
			   lRet.add(ObjectUtils.clean(val));
		   }
	   }
	   return lRet;
  }

   /**
    *
    * This method splits the valueEntered on locical operators and, or, and between
    *
    * @param valueEntered
    * @return
    */
   public static List<String> getSearchableValues(String valueEntered) {
		List<String> lRet = new ArrayList<String>();

		getSearchableValueRecursive(valueEntered, lRet);

		return lRet;
	}

	protected static void getSearchableValueRecursive(String valueEntered, List lRet) {
		if(valueEntered == null)return;

		valueEntered = valueEntered.trim();

		if(lRet == null){
			throw new NullPointerException("The list passed in is by reference and should never be null.");
		}

		if (StringUtils.contains(valueEntered, KNSConstants.BETWEEN_OPERATOR)) {
			List<String> l = Arrays.asList(valueEntered.split("\\.\\."));
			for(String value : l){
				getSearchableValueRecursive(value,lRet);
			}
			return;
		}
		if (StringUtils.contains(valueEntered, KNSConstants.OR_LOGICAL_OPERATOR)) {
			List<String> l = Arrays.asList(StringUtils.split(valueEntered, KNSConstants.OR_LOGICAL_OPERATOR));
			for(String value : l){
				getSearchableValueRecursive(value,lRet);
			}
			return;
		}
		if (StringUtils.contains(valueEntered, KNSConstants.AND_LOGICAL_OPERATOR)) {
			//splitValueList.addAll(Arrays.asList(StringUtils.split(valueEntered, KNSConstants.AND_LOGICAL_OPERATOR)));
			List<String> l = Arrays.asList(StringUtils.split(valueEntered, KNSConstants.AND_LOGICAL_OPERATOR));
			for(String value : l){
				getSearchableValueRecursive(value,lRet);
			}
			return;
		}

		// lRet is pass by ref and should NEVER be null
		lRet.add(valueEntered);
   }


}
