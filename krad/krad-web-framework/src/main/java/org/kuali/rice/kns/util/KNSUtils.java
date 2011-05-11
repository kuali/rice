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
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.type.KualiDecimal;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;

/**
 * Miscellaneous Utility Methods.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class KNSUtils {
	private static KualiModuleService kualiModuleService;
	
	private KNSUtils() {
		throw new UnsupportedOperationException("do not call");
	}
    
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

	public static String joinWithQuotes(List<String> list){
		if(list==null || list.size()==0) return "";

		return KNSConstants.SINGLE_QUOTE+
			StringUtils.join(list.iterator(), KNSConstants.SINGLE_QUOTE+","+KNSConstants.SINGLE_QUOTE)+
			KNSConstants.SINGLE_QUOTE;
	}
	
	private static KualiModuleService getKualiModuleService() {
		if (kualiModuleService == null) {
			kualiModuleService = KNSServiceLocatorWeb.getKualiModuleService();
		}
		return kualiModuleService;
	}
	
	
	/**
	 * TODO this method will probably need to be exposed in a public KNSUtils class as it is used
	 * by several different modules.  That will have to wait until ModuleService and KualiModuleService are moved
	 * to core though.
	 */
	public static String getNamespaceCode(Class<? extends Object> clazz) {
		ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
		if (moduleService == null) {
			return KNSConstants.DEFAULT_NAMESPACE;
		}
		return moduleService.getModuleConfiguration().getNamespaceCode();
	}
	
	public static AttributeSet getNamespaceAndComponentSimpleName( Class<? extends Object> clazz) {
		AttributeSet attributeSet = new AttributeSet();
		attributeSet.put(KNSConstants.NAMESPACE_CODE, getNamespaceCode(clazz));
		attributeSet.put(KNSConstants.COMPONENT_NAME, getComponentSimpleName(clazz));
		return attributeSet;
	}

	public static AttributeSet getNamespaceAndComponentFullName( Class<? extends Object> clazz) {
		AttributeSet attributeSet = new AttributeSet();
		attributeSet.put(KNSConstants.NAMESPACE_CODE, getNamespaceCode(clazz));
		attributeSet.put(KNSConstants.COMPONENT_NAME, getComponentFullName(clazz));
		return attributeSet;
	}

	public static AttributeSet getNamespaceAndActionClass( Class<? extends Object> clazz) {
		AttributeSet attributeSet = new AttributeSet();
		attributeSet.put(KNSConstants.NAMESPACE_CODE, getNamespaceCode(clazz));
		attributeSet.put(KNSConstants.ACTION_CLASS, clazz.getName());
		return attributeSet;
	}
	
	private static String getComponentSimpleName(Class<? extends Object> clazz) {
		return clazz.getSimpleName();
	}

	private static String getComponentFullName(Class<? extends Object> clazz) {
		return clazz.getName();
	}
}
