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

package org.kuali.rice.krad.datadictionary.validation;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

/**
 * Inherited from Kuali Student and adapted extensively, this class provides static utility methods for validation processing. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ValidationUtils {

	public static String buildPath(String attributePath, String attributeName) {
		if (StringUtils.isNotBlank(attributeName)) {
			if (StringUtils.isNotBlank(attributePath)) 
				return new StringBuilder(attributePath).append(".").append(attributeName).toString();
		
			return attributeName;
		}
		return attributePath;
	}
	
	/**
	 * Used to get the rightmost index value of an attribute path.
	 *  
	 * @param attributePath
	 * @return the right index of value of attribute path, -1 if path has no index
	 */
	public static int getLastPathIndex(String attributePath){
	    int index = -1;
	    
	    int leftBracket = attributePath.lastIndexOf("[");
	    int rightBracket = attributePath.lastIndexOf("]");
	    
	    if (leftBracket > 0 && rightBracket > leftBracket){
	        String indexString = attributePath.substring(leftBracket +1, rightBracket);
	        try {
                index = Integer.valueOf(indexString).intValue();
            } catch (NumberFormatException e) {
                // Will just return -1
            }
	    }
	    
	    return index;
	}
	
	public static boolean compareValues(Object value1, Object value2,
			DataType dataType, String operator, boolean isCaseSensitive, DateTimeService dateTimeService) {

		boolean result = false;
		Integer compareResult = null;

		if("has_value".equalsIgnoreCase(operator)){
			if(value1==null){
				return "false".equals(value2.toString().toLowerCase());
			}
			if (value1 instanceof String && ((String) value1).isEmpty()){
			    return "false".equals(value2.toString().toLowerCase());			    
			}
			if(value1 instanceof Collection && ((Collection<?>) value1).isEmpty()){
				return "false".equals(value2.toString().toLowerCase());
			}
			return "true".equals(value2.toString().toLowerCase());
		}		
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
				Date v1 = getDate(value1, dateTimeService);
				Date v2 = getDate(value2, dateTimeService);
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

	public static Date getDate(Object o, DateTimeService dateTimeService) throws IllegalArgumentException {
		Date result = null;
		if (o instanceof Date)
			return (Date) o;
		if (o == null)
			return null;
		String s = o.toString();
		if (s != null && s.trim().length() > 0) {
			try {
				result = dateTimeService.convertToDate(s.trim());
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			} 
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
    
    public static boolean isNullOrEmpty(Object value) {
    	return value == null || (value instanceof String && StringUtils.isBlank(((String) value).trim()));
    }
    
	
	public static enum Result { VALID, INVALID, UNDEFINED };
	
	public static Object convertToDataType(Object value, DataType dataType, DateTimeService dateTimeService) throws AttributeValidationException {
		Object returnValue = value;
		
		if (null == value)
			return null;
		
		switch (dataType) {
		case BOOLEAN:
			if (! (value instanceof Boolean)) {
				returnValue = Boolean.valueOf(value.toString());
				
				// Since the Boolean.valueOf is exceptionally loose - it basically takes any string and makes it false
				if (!value.toString().equalsIgnoreCase("TRUE") && !value.toString().equalsIgnoreCase("FALSE"))
					throw new AttributeValidationException("Value " + value.toString() + " is not a boolean!");
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
			if (((Double)returnValue).isNaN())
				throw new AttributeValidationException("Infinite Double values are not valid!");		
			if (((Double)returnValue).isInfinite())
				throw new AttributeValidationException("Infinite Double values are not valid!");
			break;
		case FLOAT:
			if (! (value instanceof Number)) {
				returnValue = Float.valueOf(value.toString());
			}
			if (((Float)returnValue).isNaN())
				throw new AttributeValidationException("NaN Float values are not valid!");
			if (((Float)returnValue).isInfinite())
				throw new AttributeValidationException("Infinite Float values are not valid!");
			break;
		case TRUNCATED_DATE:
		case DATE:
			if (! (value instanceof Date)) {
				try {
					returnValue = dateTimeService.convertToDate(value.toString());
				} catch (ParseException pe) {
					throw new AttributeValidationException("Value " + value.toString() + " is not a date!");
				}
			}
			break;
		case STRING:
		}
		
		return returnValue;
	}
	
	public static <T> Result isGreaterThan(T value, Comparable<T> limit) {
		return limit == null ? Result.UNDEFINED : ( limit.compareTo(value) < 0 ? Result.VALID : Result.INVALID );
	}
	
	public static <T> Result isGreaterThanOrEqual(T value, Comparable<T> limit) {
		return limit == null ? Result.UNDEFINED : ( limit.compareTo(value) <= 0 ? Result.VALID : Result.INVALID );
	}
	
	public static <T> Result isLessThan(T value, Comparable<T> limit) {
		return limit == null ? Result.UNDEFINED : ( limit.compareTo(value) > 0 ? Result.VALID : Result.INVALID );
	}
	
	public static <T> Result isLessThanOrEqual(T value, Comparable<T> limit) {
		return limit == null ? Result.UNDEFINED : ( limit.compareTo(value) >= 0 ? Result.VALID : Result.INVALID );
	}
	
	
    public static String[] getPathTokens(String fieldPath) {
        return (fieldPath != null && fieldPath.contains(".") ? fieldPath.split("\\.") : new String[]{fieldPath});
    }

}

