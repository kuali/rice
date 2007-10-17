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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.ObjectDefinitionResolver;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.user.UserUtils;
import edu.iu.uis.eden.util.ClassLoaderUtils;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * Various static utility methods for helping with Searcha.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocSearchUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocSearchUtils.class);

//    private static final String DATE_REGEX_PASS = "^\\d{2}/\\d{2}/\\d{4}$|^\\d{2}-\\d{2}-\\d{4}$"; // matches MM/dd/yyyy or MM-dd-yyyy
//    private static final String DATE_REGEX_PASS_SPLIT = "(\\d{2})[/|-](\\d{2})[/|-](\\d{4})";
    private static final String DATE_REGEX_SMALL_TWO_DIGIT_YEAR = "^\\d{1,2}/\\d{1,2}/\\d{2}$|^\\d{1,2}-\\d{1,2}-\\d{2}$"; // matches M/d/yy or MM/dd/yy or M-d-yy or MM-dd-yy
    private static final String DATE_REGEX_SMALL_TWO_DIGIT_YEAR_SPLIT = "(\\d{1,2})[/,-](\\d{1,2})[/,-](\\d{2})";
    private static final String DATE_REGEX_SMALL_FOUR_DIGIT_YEAR = "^\\d{1,2}/\\d{1,2}/\\d{4}$|^\\d{1,2}-\\d{1,2}-\\d{4}$"; // matches M/d/yyyy or MM/dd/yyyy or M-d-yyyy or MM-dd-yyyy
    private static final String DATE_REGEX_SMALL_FOUR_DIGIT_YEAR_SPLIT = "(\\d{1,2})[/,-](\\d{1,2})[/,-](\\d{4})";

    private static final String DATE_REGEX_SMALL_FOUR_DIGIT_YEAR_FIRST = "^\\d{4}/\\d{1,2}/\\d{1,2}$|^\\d{4}-\\d{1,2}-\\d{1,2}$"; // matches yyyy/M/d or yyyy/MM/dd or yyyy-M-d or yyyy-MM-dd
    private static final String DATE_REGEX_SMALL_FOUR_DIGIT_YEAR_FIRST_SPLIT = "(\\d{4})[/,-](\\d{1,2})[/,-](\\d{1,2})";

    private static final String DATE_REGEX_WHOLENUM_SMALL = "^\\d{6}$"; // matches MMddyy
    private static final String DATE_REGEX_WHOLENUM_SMALL_SPLIT = "(\\d{2})(\\d{2})(\\d{2})";
    private static final String DATE_REGEX_WHOLENUM_LARGE = "^\\d{8}$"; // matches MMddyyyy
    private static final String DATE_REGEX_WHOLENUM_LARGE_SPLIT = "(\\d{2})(\\d{2})(\\d{4})";

	private static final Map REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION = new HashMap();
	static {
//		REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION.put(DATE_REGEX_PASS, DATE_REGEX_PASS_SPLIT);
		REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION.put(DATE_REGEX_SMALL_TWO_DIGIT_YEAR, DATE_REGEX_SMALL_TWO_DIGIT_YEAR_SPLIT);
        REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION.put(DATE_REGEX_SMALL_FOUR_DIGIT_YEAR, DATE_REGEX_SMALL_FOUR_DIGIT_YEAR_SPLIT);
        REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION.put(DATE_REGEX_SMALL_FOUR_DIGIT_YEAR_FIRST, DATE_REGEX_SMALL_FOUR_DIGIT_YEAR_FIRST_SPLIT);
		REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION.put(DATE_REGEX_WHOLENUM_SMALL, DATE_REGEX_WHOLENUM_SMALL_SPLIT);
		REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION.put(DATE_REGEX_WHOLENUM_LARGE,DATE_REGEX_WHOLENUM_LARGE_SPLIT);
	}

    public static final List DOCUMENT_SEARCH_DATE_VALIDATION_REGEX_EXPRESSIONS = Arrays.asList(new String[] { DATE_REGEX_SMALL_FOUR_DIGIT_YEAR, DATE_REGEX_SMALL_FOUR_DIGIT_YEAR_FIRST });

    public static List<SearchableAttributeValue> getSearchableAttributeValueObjectTypes() {
    	List<SearchableAttributeValue> searchableAttributeValueClasses = new ArrayList<SearchableAttributeValue>();
    	for (Iterator iter = SearchableAttribute.SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST.iterator(); iter.hasNext();) {
			Class searchAttributeValueClass = (Class) iter.next();
	    	ObjectDefinition objDef = new ObjectDefinition(searchAttributeValueClass);
	    	SearchableAttributeValue attributeValue = (SearchableAttributeValue)ObjectDefinitionResolver.createObject(objDef, ClassLoaderUtils.getDefaultClassLoader(), false);
	    	searchableAttributeValueClasses.add(attributeValue);
		}
    	return searchableAttributeValueClasses;
	}

	public static SearchableAttributeValue getSearchableAttributeValueByDataTypeString(String dataType) {
    	SearchableAttributeValue returnableValue = null;
		if (StringUtils.isBlank(dataType)) {
			return returnableValue;
		}
    	for (Iterator iter = getSearchableAttributeValueObjectTypes().iterator(); iter.hasNext();) {
			SearchableAttributeValue attValue = (SearchableAttributeValue) iter.next();
			if (dataType.equalsIgnoreCase(attValue.getAttributeDataType())) {
				if (returnableValue != null) {
					String errorMsg = "Found two SearchableAttributeValue objects with same data type string ('" + dataType + "' while ignoring case):  "
					    + returnableValue.getClass().getName() + " and " + attValue.getClass().getName();
					LOG.error("getSearchableAttributeValueByDataTypeString() " + errorMsg);
					throw new RuntimeException(errorMsg);
				}
				LOG.debug("getSearchableAttributeValueByDataTypeString() SearchableAttributeValue class name is " + attValue.getClass().getName() + "... ojbConcreteClassName is " + attValue.getOjbConcreteClass());
		    	ObjectDefinition objDef = new ObjectDefinition(attValue.getClass());
		    	returnableValue = (SearchableAttributeValue)ObjectDefinitionResolver.createObject(objDef, ClassLoaderUtils.getDefaultClassLoader(), false);
			}
		}
    	//construtorutils
    	return returnableValue;
	}

    /**
     * A method to format any variety of date strings into a common format
     *
     * @param date  A string date in one of a few different formats
     * @return A string representing a date in the format yyyy/MM/dd or null if date is invalid
     */
    public static String getSqlFormattedDate(String date) {
    	DateComponent dc = formatDateToDateComponent(date, Arrays.asList(REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION.keySet().toArray()));
    	if (dc == null) {
    		return null;
    	}
    	return dc.getYear() + "/" + dc.getMonth() + "/" + dc.getDate();
    }

    // TODO Version 2.4 - Remove this and update DocSearchGenerator to use Platform
	public static String getDateSQL(String date, String time) {
        // SQL 92 date literal syntax:
        // http://www.stanford.edu/dept/itss/docs/oracle/9i/java.920/a96654/ref.htm#1005145
        String d = date.replace('/', '-');
        if (time == null) {
            return "{d '" + d + "'}";
        } else {
            return "{ts '" + d + " " + time + "'}";
        }
    }

    /**
     * A method to format any variety of date strings into a common format
     *
     * @param date  A string date in one of a few different formats
     * @return A string representing a date in the format MM/dd/yyyy or null if date is invalid
     */
    public static String getEntryFormattedDate(String date) {
    	DateComponent dc = formatDateToDateComponent(date, DOCUMENT_SEARCH_DATE_VALIDATION_REGEX_EXPRESSIONS);
    	if (dc == null) {
    		return null;
    	}
    	return dc.getMonth() + "/" + dc.getDate() + "/" + dc.getYear();
    }

    private static DateComponent formatDateToDateComponent(String date, List regularExpressionList) {
    	String matchingRegexExpression = null;
    	for (Iterator iter = regularExpressionList.iterator(); iter.hasNext();) {
			String matchRegex = (String) iter.next();
			if (!REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION.containsKey(matchRegex)) {
				String errorMsg = "";
				LOG.error("formatDateToDateComponent(String,List) " + errorMsg);

			}
			Pattern p = Pattern.compile(matchRegex);
			if ((p.matcher(date)).matches()) {
				matchingRegexExpression = matchRegex;
				break;
			}
		}

    	if (matchingRegexExpression == null) {
            String errorMsg = "formatDate(String,List) Date string given '" + date + "' is not valid according to Workflow defaults.  Returning null value.";
            if (StringUtils.isNotBlank(date)) {
                LOG.warn(errorMsg); 
            } else {
                LOG.debug(errorMsg);
            }
            return null;
    	}
    	String regexSplitExpression = (String) REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION.get(matchingRegexExpression);

        // Check date formats and reformat to yyyy/MM/dd
        // well formed MM/dd/yyyy
    	Pattern p = Pattern.compile(regexSplitExpression);
    	Matcher util = p.matcher(date);
    	util.matches();
//        if (regexSplitExpression.equals(DATE_REGEX_PASS_SPLIT)) {
//            return new DateComponent(util.group(3),util.group(1),util.group(2));
//
//            // small date format M/d/yy | 1-2d/1-2m/2y
//        } else
        if (regexSplitExpression.equals(DATE_REGEX_SMALL_TWO_DIGIT_YEAR_SPLIT)) {
        	StringBuffer yearBuf = new StringBuffer();
        	StringBuffer monthBuf = new StringBuffer();
        	StringBuffer dateBuf = new StringBuffer();
            Integer year = new Integer(util.group(3));

            if (year.intValue() <= 50) {
                yearBuf.append("20").append(util.group(3));
            } else if (util.group(3).length() < 3) {
            	yearBuf.append("19").append(util.group(3));
            } else {
            	yearBuf.append(util.group(3));
            }

            if (util.group(1).length() < 2) {
            	monthBuf.append("0").append(util.group(1));
            } else {
            	monthBuf.append(util.group(1));
            }

            if (util.group(2).length() < 2) {
                dateBuf.append("0").append(util.group(2));
            } else {
                dateBuf.append(util.group(2));
            }

            return new DateComponent(yearBuf.toString(),monthBuf.toString(),dateBuf.toString());

            // small date format M/d/yyyy | MM/dd/yyyy | M-d-yyyy | MM-dd-yyyy
        } else if (regexSplitExpression.equals(DATE_REGEX_SMALL_FOUR_DIGIT_YEAR_SPLIT)) {
        	StringBuffer yearBuf = new StringBuffer(util.group(3));
        	StringBuffer monthBuf = new StringBuffer();
        	StringBuffer dateBuf = new StringBuffer();

            if (util.group(1).length() < 2) {
            	monthBuf.append("0").append(util.group(1));
            } else {
            	monthBuf.append(util.group(1));
            }

            if (util.group(2).length() < 2) {
                dateBuf.append("0").append(util.group(2));
            } else {
                dateBuf.append(util.group(2));
            }

            return new DateComponent(yearBuf.toString(),monthBuf.toString(),dateBuf.toString());

            // small date format yyyy/M/d | yyyy/MM/dd | yyyy-M-d | yyyy-MM-dd
        } else if (regexSplitExpression.equals(DATE_REGEX_SMALL_FOUR_DIGIT_YEAR_FIRST_SPLIT)) {
            StringBuffer yearBuf = new StringBuffer(util.group(1));
            StringBuffer monthBuf = new StringBuffer();
            StringBuffer dateBuf = new StringBuffer();

            if (util.group(2).length() < 2) {
                monthBuf.append("0").append(util.group(2));
            } else {
                monthBuf.append(util.group(2));
            }

            if (util.group(3).length() < 2) {
                dateBuf.append("0").append(util.group(3));
            } else {
                dateBuf.append(util.group(3));
            }

            return new DateComponent(yearBuf.toString(),monthBuf.toString(),dateBuf.toString());

            // large number MMddyyyy
        } else if (regexSplitExpression.equals(DATE_REGEX_WHOLENUM_LARGE_SPLIT)) {
            return new DateComponent(util.group(3),util.group(1),util.group(2));

            // small number MMddyy
        } else if (regexSplitExpression.equals(DATE_REGEX_WHOLENUM_SMALL_SPLIT)) {
            StringBuffer yearBuf = new StringBuffer();
            Integer year = new Integer(util.group(3));

            if (year.intValue() < 50) {
            	yearBuf.append("20");
            } else {
            	yearBuf.append("19");
            }
            yearBuf.append(util.group(3));
            return new DateComponent(yearBuf.toString(),util.group(1),util.group(2));
        } else {
        	LOG.warn("formatDate(String,List) Date string given '" + date + "' is not valid according to Workflow defaults.  Returning null value.");
            return null;
        }
    }

    public static String getDisplayValueWithDateOnly(Timestamp value) {
		return EdenConstants.getDefaultDateFormat().format(new Date(value.getTime()));
	}

	public static String getDisplayValueWithDateTime(Timestamp value) {
		return EdenConstants.getDefaultDateAndTimeFormat().format(new Date(value.getTime()));
	}

	public static Timestamp convertStringDateToTimestamp(String dateWithoutTime) {
    	DateComponent formattedDate = formatDateToDateComponent(dateWithoutTime, Arrays.asList(REGEX_EXPRESSION_MAP_TO_REGEX_SPLIT_EXPRESSION.keySet().toArray()));
		if (formattedDate == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.MONTH, Integer.valueOf(formattedDate.getMonth()).intValue() - 1);
		c.set(Calendar.DATE, Integer.valueOf(formattedDate.getDate()).intValue());
		c.set(Calendar.YEAR, Integer.valueOf(formattedDate.getYear()).intValue());
		return Utilities.convertCalendar(c);
	}

	public static class DateComponent {
		protected String month;
		protected String date;
		protected String year;
	    public DateComponent(String year, String month, String date) {
	    	this.month = month;
	    	this.date = date;
	    	this.year = year;
	    }
		public String getDate() {
			return date;
		}
		public String getMonth() {
			return month;
		}
		public String getYear() {
			return year;
		}
	}

	private static final String CURRENT_USER_PREFIX = "CURRENT_USER.";

	/**
	 * Build List of searchable attributes from saved searchable attributes string
	 *
	 * @param searchableAttributeString
	 *            String representation of searchable attributes
	 * @return searchable attributes list
	 */
//	public static List buildSearchableAttributesFromString(String searchableAttributeString) {
//		List searchableAttributes = new ArrayList();
//
//		if ((searchableAttributeString != null) && (searchableAttributeString.trim().length() > 0)) {
//			StringTokenizer tokenizer = new StringTokenizer(searchableAttributeString, ",");
//			while (tokenizer.hasMoreTokens()) {
//				String searchableAttribute = tokenizer.nextToken();
//				int index = searchableAttribute.indexOf(":");
//				if (index != -1) {
//					String key = searchableAttribute.substring(0, index);
//					String label = searchableAttribute.substring(index + 1);
//					if (label.startsWith(CURRENT_USER_PREFIX)) {
//						String idType = label.substring(CURRENT_USER_PREFIX.length());
//						UserSession session = UserSession.getAuthenticatedUser();
//						String idValue = UserUtils.getIdValue(idType, session.getWorkflowUser());
//						if (!StringUtils.isBlank(idValue)) {
//							label = idValue;
//						}
//					}
//					KeyLabelPair propertyField = new KeyLabelPair(key, label);
//					searchableAttributes.add(propertyField);
//				}
//			}
//		}
//
//		return searchableAttributes;
//	}

	/**
	 * Build List of searchable attributes from saved searchable attributes string
	 *
	 * @param searchableAttributeString
	 *            String representation of searchable attributes
	 * @return searchable attributes list
	 */
	public static List<SearchAttributeCriteriaComponent> buildSearchableAttributesFromString(String searchableAttributeString, String documentTypeName) {
		List<SearchAttributeCriteriaComponent> searchableAttributes = new ArrayList<SearchAttributeCriteriaComponent>();
		Map criteriaComponentsByKey = new HashMap();

		if (!Utilities.isEmpty(documentTypeName)) {
			DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
			if (docType == null) {
				String errorMsg = "Cannot find document type for given name '" + documentTypeName + "'";
				LOG.error("buildSearchableAttributesFromString() " + errorMsg);
				throw new RuntimeException(errorMsg);
			}
			for (SearchableAttribute searchableAttribute : docType.getSearchableAttributes()) {
				for (Row row : searchableAttribute.getSearchingRows()) {
					for (Field field : row.getFields()) {
						SearchableAttributeValue searchableAttributeValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType());
						SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(field.getPropertyName(),null,field.getSavablePropertyName(),searchableAttributeValue);
			        	sacc.setRangeSearch(field.isMemberOfRange());
			        	sacc.setAllowWildcards(field.isAllowingWildcards());
			        	sacc.setAutoWildcardBeginning(field.isAutoWildcardAtBeginning());
			        	sacc.setAutoWildcardEnd(field.isAutoWildcardAtEnding());
			        	sacc.setCaseSensitive(field.isCaseSensitive());
			        	sacc.setSearchInclusive(field.isInclusive());
                        sacc.setSearchable(field.isSearchable());
                        sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
			        	criteriaComponentsByKey.put(field.getPropertyName(), sacc);
					}
				}
			}
		}

        Map<String,List<String>> checkForMultiValueSearchableAttributes = new HashMap<String,List<String>>();
		if ((searchableAttributeString != null) && (searchableAttributeString.trim().length() > 0)) {
			StringTokenizer tokenizer = new StringTokenizer(searchableAttributeString, ",");
			while (tokenizer.hasMoreTokens()) {
				String searchableAttribute = tokenizer.nextToken();
				int index = searchableAttribute.indexOf(":");
				if (index != -1) {
					String key = searchableAttribute.substring(0, index);
//					String savedKey = key;
//					if (key.indexOf(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX) == 0) {
//						savedKey = key.substring(SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX.length());
//					} else if (key.indexOf(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX) == 0) {
//						savedKey = key.substring(SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX.length());
//					}
					String value = searchableAttribute.substring(index + 1);
					if (value.startsWith(CURRENT_USER_PREFIX)) {
						String idType = value.substring(CURRENT_USER_PREFIX.length());
						UserSession session = UserSession.getAuthenticatedUser();
						String idValue = UserUtils.getIdValue(idType, session.getWorkflowUser());
						if (!StringUtils.isBlank(idValue)) {
							value = idValue;
						}
					}
					SearchAttributeCriteriaComponent critComponent = (SearchAttributeCriteriaComponent) criteriaComponentsByKey.get(key);
                    if (critComponent == null) {
                        // here we potentially have a change to the searchable attributes dealing with naming or ranges... so we just ignore the values
                        continue;
                    }
                    if (critComponent.getSearchableAttributeValue() == null) {
						String errorMsg = "Cannot find SearchableAttributeValue for given key '" + key + "'";
						LOG.error("buildSearchableAttributesFromString() " + errorMsg);
						throw new RuntimeException(errorMsg);
					}
                    if (critComponent.isCanHoldMultipleValues()) {
                        // should be multivalue
                        if (checkForMultiValueSearchableAttributes.containsKey(key)) {
                            List<String> keyList = checkForMultiValueSearchableAttributes.get(key);
                            keyList.add(value);
                            checkForMultiValueSearchableAttributes.put(key, keyList);
                        } else {
                            List<String> tempList = new ArrayList<String>();
                            tempList.add(value);
//                            tempList.addAll(Arrays.asList(new String[]{value}));
                            checkForMultiValueSearchableAttributes.put(key, tempList);
                            searchableAttributes.add(critComponent);
                        }
                    }
                    else {
                        // should be single value
                        if (checkForMultiValueSearchableAttributes.containsKey(key)) {
                            // attempting to use multiple values in a field that does not support it
                            String error = "Attempting to add multiple values to a search attribute (key: '" + key + "') that does not suppor them";
                            LOG.error("buildSearchableAttributesFromString() " + error);
                            // we don't blow chunks here in case an attribute has been altered from multi-value to non-multi-value
                        }
                        critComponent.setValue(value);
                        searchableAttributes.add(critComponent);
                    }


				}
			}
            for (Iterator iter = searchableAttributes.iterator(); iter.hasNext();) {
                SearchAttributeCriteriaComponent criteriaComponent = (SearchAttributeCriteriaComponent) iter.next();
                if (criteriaComponent.isCanHoldMultipleValues()) {
                    List values =(List)checkForMultiValueSearchableAttributes.get(criteriaComponent.getFormKey());
                    criteriaComponent.setValue(null);
                    criteriaComponent.setValues(values);
                }
            }
		}

		return searchableAttributes;
	}

}
