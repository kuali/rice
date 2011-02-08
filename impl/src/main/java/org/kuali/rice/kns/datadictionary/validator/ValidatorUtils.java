/**
 * Copyright 2010 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.kuali.rice.kns.datadictionary.validator;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.kuali.rice.kns.datadictionary.DataDictionaryEntry;
import org.kuali.rice.kns.dto.Constrained;
import org.kuali.rice.kns.dto.DataType;
import org.kuali.rice.kns.dto.ExistenceConstrained;
import org.kuali.rice.kns.dto.LengthConstrained;
import org.kuali.rice.kns.dto.QuantityConstrained;
import org.kuali.rice.kns.dto.SizeConstrained;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;


public class ValidatorUtils {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ValidatorUtils.class);
	
	
	public static void addResult(List<ValidationResultInfo> results, ValidationResultInfo result) {
    	if (result != null && !result.isOk()) {
    		results.add(result);
    	}
    }
	
	public static boolean compareValues(Object value1, Object value2,
			DataType dataType, String operator, boolean isCaseSensitive, DateParser dateParser) {

		boolean result = false;
		Integer compareResult = null;

		// Convert objects into appropriate data types
		if (null != dataType) {
			if (DataType.STRING.equals(dataType)) {
			    String v1 = getString(value1);
				String v2 = getString(value2);

				if(!isCaseSensitive) {
				    v1 = v1.toUpperCase();
				    v2 = v2.toUpperCase();
				}
				
				compareResult = v1.compareTo(v2);
			} else if (DataType.INTEGER.equals(dataType)) {
				Integer v1 = getInteger(value1);
				Integer v2 = getInteger(value2);
				compareResult = v1.compareTo(v2);
			} else if (DataType.LONG.equals(dataType)) {
				Long v1 = getLong(value1);
				Long v2 = getLong(value2);
				compareResult = v1.compareTo(v2);
			} else if (DataType.DOUBLE.equals(dataType)) {
				Double v1 = getDouble(value1);
				Double v2 = getDouble(value2);
				compareResult = v1.compareTo(v2);
			} else if (DataType.FLOAT.equals(dataType)) {
				Float v1 = getFloat(value1);
				Float v2 = getFloat(value2);
				compareResult = v1.compareTo(v2);
			} else if (DataType.BOOLEAN.equals(dataType)) {
				Boolean v1 = getBoolean(value1);
				Boolean v2 = getBoolean(value2);
				compareResult = v1.compareTo(v2);
			} else if (DataType.DATE.equals(dataType)) {
				Date v1 = getDate(value1, dateParser);
				Date v2 = getDate(value2, dateParser);
				compareResult = v1.compareTo(v2);
			}
		}

		if (null != compareResult) {
			if (("equals".equalsIgnoreCase(operator)
					|| "greater_than_equal".equalsIgnoreCase(operator) || "less_than_equal"
					.equalsIgnoreCase(operator))
					&& 0 == compareResult) {
				result = true;
			}

			if (("not_equal".equalsIgnoreCase (operator)
     || "greater_than".equalsIgnoreCase(operator)) && compareResult >= 1) {
				result = true;
			}

			if (("not_equal".equalsIgnoreCase (operator)
     || "less_than".equalsIgnoreCase(operator)) && compareResult <= -1) {
				result = true;
			}
		}

		return result;
	}

	public static Integer getInteger(Object o) {
		Integer result = null;
		if (o instanceof Integer)
			return (Integer) o;
		if (o == null)
			return null;
		if (o instanceof Number)
			return ((Number) o).intValue();
		String s = o.toString();
		if (s != null && s.trim().length() > 0) {
			result = Integer.valueOf(s.trim());
		}
		return result;
	}

	public static Long getLong(Object o) {
		Long result = null;
		if (o instanceof Long)
			return (Long) o;
		if (o == null)
			return null;
		if (o instanceof Number)
			return ((Number) o).longValue();
		String s = o.toString();
		if (s != null && s.trim().length() > 0) {
			result = Long.valueOf(s.trim());
		}
		return result;
	}

	public static Float getFloat(Object o) {
		Float result = null;
		if (o instanceof Float)
			return (Float) o;
		if (o == null)
			return null;
		if (o instanceof Number)
			return ((Number) o).floatValue();
		String s = o.toString();
		if (s != null && s.trim().length() > 0) {
			result = Float.valueOf(s.trim());
		}
		return result;
	}

	public static Double getDouble(Object o) {
		Double result = null;
		if (o instanceof BigDecimal)
			return ((BigDecimal) o).doubleValue();
		if (o instanceof Double)
			return (Double) o;
		if (o == null)
			return null;
		if (o instanceof Number)
			return ((Number) o).doubleValue();
		String s = o.toString();
		if (s != null && s.trim().length() > 0) {
			result = Double.valueOf(s.trim());
		}
		return result;
	}

	public static Date getDate(Object o, DateParser dateParser) throws IllegalArgumentException {
		Date result = null;
		if (o instanceof Date)
			return (Date) o;
		if (o == null)
			return null;
		String s = o.toString();
		if (s != null && s.trim().length() > 0) {
			result = dateParser.parseDate(s.trim());
		}
		return result;
	}

	public static String getString(Object o) {
		if (o instanceof String)
			return (String) o;
		if (o == null)
			return null;
		return o.toString();
	}

	public static Boolean getBoolean(Object o) {
		Boolean result = null;
		if (o instanceof Boolean)
			return (Boolean) o;
		if (o == null)
			return null;
		String s = o.toString();
		if (s != null && s.trim().length() > 0) {
			result = Boolean.parseBoolean(s.trim());
		}
		return result;
	}	
	
	/**
	 * Traverses the dictionary ObjectStructure to find the field with the match
	 * key, type and state
	 * The key has to relative to the current object structure that is being traversed.
	 * example: current object structure is ActivityInfo and if we want to lookup 
	 * the academicSubjectorgId, then <property name="fieldPath" value="academicSubjectOrgs.orgId"/>
	 * The current object structure starts from the field on which the constraint is applied on.
	 * If we want to address fields outside of this object structure we ll need to pass in the
	 * dictionary context.
	 * @param key
	 * @param type
	 * @param state
	 * @param objStructure
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static AttributeValueReader getDefinition(String key, AttributeValueReader attributeValueReader) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		String[] lookupPathTokens = getPathTokens(key);
		
		AttributeValueReader localAttributeValueReader = attributeValueReader;
		for(int i = 0; i < lookupPathTokens.length; i++) {
			for (Constrained f : localAttributeValueReader.getDefinitions()) {
				String attributeName = f.getName();
				if (attributeName.equals(lookupPathTokens[i])) {
					if(i==lookupPathTokens.length-1){
						localAttributeValueReader.setCurrentName(attributeName);
						return localAttributeValueReader;
					}
					String childEntryName = f.getChildEntryName();
					DataDictionaryEntry entry = attributeValueReader.getEntry(childEntryName);
					Object value = attributeValueReader.getValue(attributeName);
					localAttributeValueReader = new DictionaryObjectAttributeValueReader(value, childEntryName, entry);
					break;
				}
			}
		 }
		return null;
	}
	
	
	
    public static boolean hasText(String string) {

        if (string == null || string.length() < 1) {
            return false;
        }
        int stringLength = string.length();

        for (int i = 0; i < stringLength; i++) {
            char currentChar = string.charAt(i);
            if (' ' != currentChar || '\t' != currentChar || '\n' != currentChar) {
                return true;
            }
        }

        return false;
    }
	
	
	private static enum Result { VALID, INVALID, UNDEFINED };
	
//	private static ComparisonResult isBigger(String biggerNumber, String smallerNumber, boolean isInclusive) {
//		if (biggerNumber == null || smallerNumber == null)
//			return ComparisonResult.INVALID;
//		
//		try {
//			BigDecimal bigger = new BigDecimal(biggerNumber);
//			BigDecimal smaller = new BigDecimal(smallerNumber);
//			
//			
//		} catch (NumberFormatException nfe) {
//			// Assume that any number format exception just means that this validation shouldn't be happening, not that we shouldn't be throwing an exception
//			LOG.warn("Invalid comparison between " + biggerNumber + " and " + smallerNumber, nfe);
//			return ComparisonResult.INVALID;
//		}
//	}
	
	
	public static Object convertToDataType(Object value, DataType dataType) {
		Object returnValue = value;
		
		if (null == value)
			return null;
		
		switch (dataType) {
		case BOOLEAN:
			if (! (value instanceof Boolean)) {
				returnValue = Boolean.valueOf(value.toString());
			}
			break;
		case INTEGER:
			if (! (value instanceof Number)) {
				returnValue = Integer.valueOf(value.toString());
			}
			break;
		case LONG:
			if (! (value instanceof Number)) {
				returnValue = Long.valueOf(value.toString());
			}
			break;
		case DOUBLE:
			if (! (value instanceof Number)) {
				returnValue = Double.valueOf(value.toString());
			}
			break;
		case FLOAT:
			if (! (value instanceof Number)) {
				returnValue = Float.valueOf(value.toString());
			}
			break;
		case TRUNCATED_DATE:
		case DATE:
			if (! (value instanceof Date)) {
				DateParser dateParser = new ServerDateParser();
				returnValue = dateParser.parseDate(value.toString());
			}
			break;
		case STRING:
		case COMPLEX:
			break;
		}
		
		return returnValue;
	}
	
	
	public static ValidationResultInfo validateDataType(Object value, DataType dataType, String entryName, String attributeName) {
		ValidationResultInfo result = new ValidationResultInfo(entryName, attributeName);
		
		try {
			convertToDataType(value, dataType);
		} catch (Exception e) {		
			switch (dataType) {
			case BOOLEAN:
				result.setError(RiceKeyConstants.ERROR_BOOLEAN);
				break;
			case INTEGER:
				result.setError(RiceKeyConstants.ERROR_INTEGER);
				break;
			case LONG:
				result.setError(RiceKeyConstants.ERROR_LONG);
				break;
			case DOUBLE:
				result.setError(RiceKeyConstants.ERROR_BIG_DECIMAL);
				break;
			case FLOAT:
				result.setError(RiceKeyConstants.ERROR_BIG_DECIMAL);
				break;
			case TRUNCATED_DATE:
			case DATE:
				result.setError(RiceKeyConstants.ERROR_BIG_DECIMAL);
				break;
			case STRING:
			case COMPLEX:
				result.setError(RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
				break;
			}
		}
		
		return result;
	}
	
	public static ValidationResultInfo validateQuantity(Collection<?> collection, QuantityConstrained attribute, String entryName, String attributeName) throws IllegalArgumentException {
		
		Integer sizeOfCollection = Integer.valueOf(collection.size());
		
		Integer maxOccurances = attribute.getMaxOccurs();
		Integer minOccurances = attribute.getMinOccurs();
		
		Result lessThanMax = isLessThanOrEqual(sizeOfCollection, maxOccurances);
		Result greaterThanMin = isGreaterThanOrEqual(sizeOfCollection, minOccurances);

		// It's okay for one end of the range to be undefined - that's not an error. It's only an error if one of them is invalid 
        if (lessThanMax != Result.INVALID && greaterThanMin != Result.INVALID) { 
        	// In this case, just get out
        	return null;
        }
        
		String maxErrorParameter = maxOccurances != null ? maxOccurances.toString() : null;
		String minErrorParameter = minOccurances != null ? minOccurances.toString() : null;
        
		ValidationResultInfo result = new ValidationResultInfo(entryName, attributeName);
		
        // If both comparisons happened then if either comparison failed we can show the end user the expected range on both sides.
        if (lessThanMax != Result.UNDEFINED && greaterThanMin != Result.UNDEFINED) 
        	result.setError(RiceKeyConstants.ERROR_QUANTITY_RANGE, minErrorParameter, maxErrorParameter);
        // If it's the max comparison that fails, then just tell the end user what the max can be
        else if (lessThanMax == Result.INVALID)
        	result.setError(RiceKeyConstants.ERROR_MAX_OCCURS, maxErrorParameter);
        // Otherwise, just tell them what the min can be
        else 
        	result.setError(RiceKeyConstants.ERROR_MIN_OCCURS, minErrorParameter);
		
		return result;
	}
	
	public static ValidationResultInfo validateLength(String value, LengthConstrained attribute, String entryName, String attributeName) throws IllegalArgumentException {
		Integer valueLength = Integer.valueOf(value.length());
		
		Integer maxLength = attribute.getMaxLength();
		Integer minLength = attribute.getMinLength();
		
		Result lessThanMax = isLessThan(valueLength, maxLength);
		Result greaterThanMin = isGreaterThan(valueLength, minLength);
		
        // It's okay for one end of the range to be undefined - that's not an error. It's only an error if one of them is invalid 
        if (lessThanMax != Result.INVALID && greaterThanMin != Result.INVALID) { 
        	// In this case, just get out
        	return null;
        }
        
        ValidationResultInfo result = new ValidationResultInfo(entryName, attributeName);
        
		String maxErrorParameter = maxLength != null ? maxLength.toString() : null;
		String minErrorParameter = minLength != null ? minLength.toString() : null;
        
        // If both comparisons happened then if either comparison failed we can show the end user the expected range on both sides.
        if (lessThanMax != Result.UNDEFINED && greaterThanMin != Result.UNDEFINED) 
        	result.setError(RiceKeyConstants.ERROR_OUT_OF_RANGE, minErrorParameter, maxErrorParameter);
        // If it's the max comparison that fails, then just tell the end user what the max can be
        else if (lessThanMax == Result.INVALID)
        	result.setError(RiceKeyConstants.ERROR_INCLUSIVE_MAX, maxErrorParameter);
        // Otherwise, just tell them what the min can be
        else 
        	result.setError(RiceKeyConstants.ERROR_EXCLUSIVE_MIN, minErrorParameter);
		
		return result;
	}
	
	
	public static ValidationResultInfo validateRange(Date value, SizeConstrained attribute, String entryName, String attributeName) throws IllegalArgumentException {	
		DateParser parser = new ServerDateParser();
		
		Date date = value != null ? getDate(value, parser) : null;

        String inclusiveMaxText = attribute.getInclusiveMax();
        String exclusiveMinText = attribute.getExclusiveMin();

        Date inclusiveMax = inclusiveMaxText != null ? getDate(inclusiveMaxText, parser) : null;
        Date exclusiveMin = exclusiveMinText != null ? getDate(exclusiveMinText, parser) : null;
        
		return isInRange(date, inclusiveMax, inclusiveMaxText, exclusiveMin, exclusiveMinText, entryName, attributeName);
	}
	
	public static ValidationResultInfo validateRange(Number value, SizeConstrained attribute, String entryName, String attributeName) throws IllegalArgumentException {		
		return validateRange(value.toString(), attribute, entryName, attributeName);
	}
	
	public static ValidationResultInfo validateRange(String value, SizeConstrained attribute, String entryName, String attributeName) throws IllegalArgumentException {

		// TODO: JLR - need a code review of the conversions below to make sure this is the best way to ensure accuracy across all numerics
        // This will throw NumberFormatException if the value is 'NaN' or infinity... probably shouldn't be a NFE but something more intelligible at a higher level
        BigDecimal number = value != null ? new BigDecimal(value) : null;

        String inclusiveMaxText = attribute.getInclusiveMax();
        String exclusiveMinText = attribute.getExclusiveMin();
        
        BigDecimal inclusiveMax = inclusiveMaxText != null ? new BigDecimal(inclusiveMaxText) : null;
        BigDecimal exclusiveMin = exclusiveMinText != null ? new BigDecimal(exclusiveMinText) : null;
        
		return isInRange(number, inclusiveMax, inclusiveMaxText, exclusiveMin, exclusiveMinText, entryName, attributeName);
	}
	
	public static ValidationResultInfo validateRequired(Object value, ExistenceConstrained attribute, String entryName, String attributeName) {
		ValidationResultInfo result = new ValidationResultInfo(entryName, attributeName);

		if (attribute.isRequired() != null && attribute.isRequired().booleanValue()) {
			if (ObjectUtils.isNull(value))
				result.setError(RiceKeyConstants.ERROR_REQUIRED);
		}
		
		return result;
	}
	
	/*
	 * This method takes any value along with a minimum and maximum that are comparable to its type and checks to see if the value falls 
	 * within that range. If it doesn't, then the method returns a validation result that indicates the range boundaries and can be processed to 
	 * provide feedback to the end user. 
	 */
	private static <T> ValidationResultInfo isInRange(T value, Comparable<T> inclusiveMax, String inclusiveMaxText, Comparable<T> exclusiveMin, String exclusiveMinText, String entryName, String attributeName) {
        // What we want to know is that the maximum value is greater than or equal to the number passed (the number can be equal to the max, i.e. it's 'inclusive')
        Result lessThanMax = isLessThanOrEqual(value, inclusiveMax); 
        // On the other hand, since the minimum is exclusive, we just want to make sure it's less than the number (the number can't be equal to the min, i.e. it's 'exclusive')
        Result greaterThanMin = isGreaterThan(value, exclusiveMin); 
          
        // It's okay for one end of the range to be undefined - that's not an error. It's only an error if one of them is actually invalid. 
        if (lessThanMax != Result.INVALID && greaterThanMin != Result.INVALID) { 
        	// In this case, just get out
        	return null;
        }
        
        ValidationResultInfo result = new ValidationResultInfo(entryName, attributeName);
        
        // If both comparisons happened then if either comparison failed we can show the end user the expected range on both sides.
        if (lessThanMax != Result.UNDEFINED && greaterThanMin != Result.UNDEFINED) 
        	result.setError(RiceKeyConstants.ERROR_OUT_OF_RANGE, exclusiveMinText, inclusiveMaxText);
        // If it's the max comparison that fails, then just tell the end user what the max can be
        else if (lessThanMax == Result.INVALID)
        	result.setError(RiceKeyConstants.ERROR_INCLUSIVE_MAX, inclusiveMaxText);
        // Otherwise, just tell them what the min can be
        else 
        	result.setError(RiceKeyConstants.ERROR_EXCLUSIVE_MIN, exclusiveMinText);
		
        return result;
	}
	
	private static <T> Result isGreaterThan(T value, Comparable<T> limit) {
		return limit == null ? Result.UNDEFINED : ( limit.compareTo(value) < 0 ? Result.VALID : Result.INVALID );
	}
	
	private static <T> Result isGreaterThanOrEqual(T value, Comparable<T> limit) {
		return limit == null ? Result.UNDEFINED : ( limit.compareTo(value) <= 0 ? Result.VALID : Result.INVALID );
	}
	
	private static <T> Result isLessThan(T value, Comparable<T> limit) {
		return limit == null ? Result.UNDEFINED : ( limit.compareTo(value) > 0 ? Result.VALID : Result.INVALID );
	}
	
	private static <T> Result isLessThanOrEqual(T value, Comparable<T> limit) {
		return limit == null ? Result.UNDEFINED : ( limit.compareTo(value) >= 0 ? Result.VALID : Result.INVALID );
	}
	
//	private static ValidationResultInfo processComparisons(String combinedErrorKey, String firstErrorKey, ComparisonResult first, 
//			String firstText, String secondErrorKey, ComparisonResult second, String secondText, ValidationResultInfo validationResult) {
//		ValidationResultInfo result = validationResult != null ? validationResult : new ValidationResultInfo();
//		
//		// It's okay for one end of the range to be undefined - that's not an error. It's only an error if one of them is "no". 
//        if (first != ComparisonResult.NO && second != ComparisonResult.NO) { 
//        	// In this case, just get out
//        	return result;
//        }
//        
//        // If neither comparison is invalid then if either comparison fails we can show the end user the expected range on both sides.
//        if (first != ComparisonResult.UNDEFINED && second != ComparisonResult.UNDEFINED) 
//        	result.setError(combinedErrorKey, secondText, firstText);
//        // If it's the max comparison that fails, then just tell the end user what the max can be
//        else if (first == ComparisonResult.NO)
//        	result.setError(firstErrorKey, firstText);
//        // Otherwise, just tell them what the min can be
//        else 
//        	result.setError(secondErrorKey, secondText);
//        
//        return result;
//	}
	
//	private static ValidationResultInfo resolveComparisons(Comparison first, Comparison second, String combinedErrorKey, ValidationResultInfo validationResult) {
//		ValidationResultInfo result = validationResult != null ? validationResult : new ValidationResultInfo();
//		
//		if (first.worked() && second.worked()) {
//			return result;
//		}
//		
//		if (first.happened() && second.happened()) 
//			result.setError(combinedErrorKey, first.getErrorParameter(), second.getErrorParameter());
//		else if (first.failed()) 
//			result.setError(first.getErrorKey(), first.getErrorParameter());
//		else
//			result.setError(second.getErrorKey(), second.getErrorParameter());
//		
//		return result;
//	}
//	
//	private class Comparison {
//		
//		final private String errorKey;
//		final private String errorParameter;
//		final private Boolean result;
//		
//		public Comparison(String errorKey, String errorParameter) {
//			this.errorKey = errorKey;
//			this.errorParameter = errorParameter;
//			this.result = null;
//		}
//		
//		public Comparison(String errorKey, String errorParameter, boolean result) {
//			this.errorKey = errorKey;
//			this.errorParameter = errorParameter;
//			this.result = Boolean.valueOf(result);
//		}
//		
//		public boolean happened() {
//			return result != null;
//		}
//		
//		public boolean worked() {
//			return result == null || result.booleanValue();
//		}
//		
//		public boolean failed() {
//			return result != null && !result.booleanValue();
//		}
//
//		/**
//		 * @return the errorKey
//		 */
//		public String getErrorKey() {
//			return this.errorKey;
//		}
//
//		/**
//		 * @return the errorParameter
//		 */
//		public String getErrorParameter() {
//			return this.errorParameter;
//		}
//		
//	}
//	
	
    private static String[] getPathTokens(String fieldPath) {
        return (fieldPath != null && fieldPath.contains(".") ? fieldPath.split("\\.") : new String[]{fieldPath});
    }

}

