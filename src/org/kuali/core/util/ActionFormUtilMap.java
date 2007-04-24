/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.util;

import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.lookup.keyvalues.ApcValuesFinder;
import org.kuali.core.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.KNSServiceLocator;

/**
 * Utility map for the action form to provide a way for calling functions through jstl.
 * 
 * 
 */
public class ActionFormUtilMap extends HashMap {
    
    /**
     * This method parses from the key the actual method to run.
     * 
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object key) {
        String[] methodKey = StringUtils.split((String) key, Constants.ACTION_FORM_UTIL_MAP_METHOD_PARM_DELIMITER);
     
        String methodToCall = methodKey[0];
        String methodParm = methodKey[1];
        
        Method method = null;
        try {
            method = ActionFormUtilMap.class.getMethod(methodToCall, new Class[] {Object.class});
        }
        catch (SecurityException e) {
            throw new RuntimeException("Unable to object handle on method given to ActionFormUtilMap: " + e.getMessage());
        }
        catch (NoSuchMethodException e1) {
            throw new RuntimeException("Unable to object handle on method given to ActionFormUtilMap: " + e1.getMessage());
        }
        
        Object methodValue = null;
        try {
            methodValue = method.invoke(this, new Object[] {methodParm});
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to invoke method " + e.getMessage());
        }
        
        return methodValue;
    }

    /* 
     * Will take in a class name parameter and attempt to create a KeyValueFinder instance, then call the finder to return a list of
     * KeyValue pairs. This is used by the htmlControlAttribute.tag to render select options from a given finder class specified in
     * the data dictionary.
    */
    public Object getOptionsMap(Object key) {
        List optionsList = new ArrayList();

        if (StringUtils.isBlank((String) key)) {
            return optionsList;
        }

        /*
         * the class name has . replaced with | in the jsp to prevent struts from treating each part of the class name as a property
         * substitute back here to get the correct name
         */
        key = StringUtils.replace((String) key, "|", ".");

        KeyValuesFinder finder;
        try {
            Class finderClass = Class.forName((String) key);
            finder = (KeyValuesFinder) finderClass.newInstance();
            if (finder instanceof ApcValuesFinder) {
                throw new IllegalArgumentException("Cannot currently use <apcSelect> in this form");
            }
            optionsList = finder.getKeyValues();
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        return optionsList;
    }
    
    /**
     * Encrypts a value passed from the ui.
     * @param value - clear text
     * @return String - encrypted text
     */
    public String encryptValue(Object value) {
        String encrypted = "";
        if (value != null) {
            encrypted = value.toString();
        }
        
        try {
            encrypted = KNSServiceLocator.getEncryptionService().encrypt(value);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to encrypt value in action form: " + e.getMessage());
        }
        
        return encrypted;
    }
    
    
}
