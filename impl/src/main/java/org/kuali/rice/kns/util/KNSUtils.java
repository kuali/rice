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
package org.kuali.rice.kns.util;

import java.lang.reflect.Constructor;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Miscellaneous Utility Methods.
 */
public class KNSUtils {
    
    public final static String getBusinessTitleForClass(Class<? extends Object> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("The getBusinessTitleForClass method of KNSUtils requires a non-null class");
        }
        String className = clazz.getSimpleName();
    
        StringBuffer label = new StringBuffer(className.substring(0, 1));
        for (int i = 1; i < className.length(); i++) {
            if (Character.isLowerCase(className.charAt(i))) {
                label.append(className.charAt(i));
            } else {
                label.append(" ").append(className.charAt(i));
            }
        }
        return label.toString().trim();
    }

    /**
     * Picks off the filename from the full path. Takes care of different OS seperators.
     */
    public final static List<String> getFileNameFromPath(List<String> fullFileNames) {
        List<String> fileNameList = new ArrayList<String>();

        for (String fullFileName : fullFileNames) {
            if (StringUtils.contains(fullFileName, "/")) {
                fileNameList.add(StringUtils.substringAfterLast(fullFileName, "/"));
            }
            else {
                fileNameList.add(StringUtils.substringAfterLast(fullFileName, "\\"));
            }
        }

        return fileNameList;
    }

    private static final KualiDecimal ONE_HUNDRED = new KualiDecimal("100.00");
    /**
     * Convert the given monney amount into an interger string. Since the return string cannot have decimal point, multiplies the
     * amount by 100 so the decimal places are not lost, for example, 320.15 is converted into 32015. 
     * 
     * @return an integer string of the given money amount through multiplicating by 100 and removing the fraction portion.
     */
    public final static String convertDecimalIntoInteger(KualiDecimal decimalNumber) {
        KualiDecimal decimalAmount = decimalNumber.multiply(ONE_HUNDRED);
        NumberFormat formatter = NumberFormat.getIntegerInstance();
        String formattedAmount = formatter.format(decimalAmount);

        return StringUtils.replace(formattedAmount, ",", "");
    }
    
	public static Integer getIntegerValue(String numberStr){
		Integer numberInt = null;
		try{
			numberInt = new Integer(numberStr);
		} catch(NumberFormatException nfe){
			Double numberDbl = new Double(numberStr);
			numberInt = new Integer(numberDbl.intValue());
		}
		return numberInt;
	}

	public static Object createObject(Class<?> clazz, Class<?>[] argumentClasses, Object[] argumentValues) {
		if(clazz==null)
			return null;
		try {
			Constructor<?> constructor = clazz.getConstructor(argumentClasses);
			return constructor.newInstance(argumentValues);
	    } catch (Exception e) {
	      	return null;
	    }
	}

}