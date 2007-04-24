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
package org.kuali.core.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;


/**
 * Holds errors due to validation. Keys of map represent property paths, and value is a TypedArrayList that contains resource string
 * keys (to retrieve the error message).
 * 
 * 
 */
public class ErrorMap implements Map, Serializable {
    private static final long serialVersionUID = -2328635367656516150L;
    private List errorPath = new ArrayList();
    private Map messages = new HashMap();

    /**
     * Adds an error to the map under the given propertyName and adds an array of message parameters. This will fully prepend the
     * error key with any value in the errorPath list. This should be used when you do not want to add the error with the prepend
     * pre-built error path.
     * 
     * @param propertyName name of the property to add error under
     * @param errorKey resource key used to retrieve the error text from the error message resource bundle
     * @param errorParameters zero or more string parameters for the displayed error message
     * @return TypedArrayList
     */
    public TypedArrayList putError(String propertyName, String errorKey, String... errorParameters) {
        return putError(propertyName, errorKey, true, errorParameters);
    }

    /**
     * Adds an error to the map under the given propertyName and adds an array of message parameters. This will fully prepend the
     * error key with any value in the errorPath list.
     * 
     * @param propertyName name of the property to add error under
     * @param errorKey resource key used to retrieve the error text from the error message resource bundle
     * @param errorParameters zero or more string parameters for the displayed error message
     * @return TypedArrayList
     */
    public TypedArrayList putErrorWithoutFullErrorPath(String propertyName, String errorKey, String... errorParameters) {
        return putError(propertyName, errorKey, false, errorParameters);
    }

    /**
     * adds an error to the map under the given propertyName and adds an array of message parameters.
     * 
     * @param propertyName name of the property to add error under
     * @param errorKey resource key used to retrieve the error text from the error message resource bundle
     * @param withFullErrorPath true if you want the whole parent error path appended, false otherwise
     * @param errorParameters zero or more string parameters for the displayed error message
     * @return TypeArrayList
     */
    private TypedArrayList putError(String propertyName, String errorKey, boolean withFullErrorPath, String... errorParameters) {
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }
        if (StringUtils.isBlank(errorKey)) {
            throw new IllegalArgumentException("invalid (blank) errorKey");
        }

        // check if we have previous errors for this property
        TypedArrayList errorList = null;
        String propertyKey = getKeyPath((String) propertyName, withFullErrorPath);
        if (messages.containsKey(propertyKey)) {
            errorList = (TypedArrayList) messages.get(propertyKey);
        }
        else {
            errorList = new TypedArrayList(ErrorMessage.class);
        }

        // add error to list
        ErrorMessage errorMessage = new ErrorMessage(errorKey, errorParameters);
        // check if this error has already been added to the list
        if ( !errorList.contains( errorMessage ) ) {
            errorList.add(errorMessage);
        }

        return (TypedArrayList) messages.put(propertyKey, errorList);
    }

    /**
     * If any error messages with the key targetKey exist in this ErrorMap for the named property, those ErrorMessages will be
     * replaced with a new ErrorMessage with the given replaceKey and replaceParameters.
     * 
     * @param propertyName name of the property where existing error will be replaced
     * @param targetKey error key of message to be replaced
     * @paran replaceKey error key which will replace targetKey
     * @param replaceParameters zero or more string parameters for the replacement error message
     * @return true if the replacement occurred
     */
    public boolean replaceError(String propertyName, String targetKey, String replaceKey, String... replaceParameters) {
        return replaceError(propertyName, targetKey, true, replaceKey, replaceParameters);
    }

    /**
     * If any error messages with the key targetKey exist in this ErrorMap for the named property, those ErrorMessages will be
     * replaced with a new ErrorMessage with the given replaceKey and replaceParameters. The targetKey and replaceKey will be
     * prepended with the current errorPath, if any.
     * 
     * 
     * @param propertyName name of the property where existing error will be replaced
     * @param targetKey error key of message to be replaced
     * @paran replaceKey error key which will replace targetKey
     * @param replaceParameters zero or more string parameters for the replacement error message
     * @return true if the replacement occurred
     */
    public boolean replaceErrorWithoutFullErrorPath(String propertyName, String targetKey, String replaceKey, String... replaceParameters) {
        return replaceError(propertyName, targetKey, false, replaceKey, replaceParameters);
    }


    /**
     * If any error messages with the key targetKey exist in this ErrorMap for the named property, those ErrorMessages will be
     * replaced with a new ErrorMessage with the given replaceKey and replaceParameters.
     * 
     * @param propertyName name of the property to add error under
     * @param errorKey resource key used to retrieve the error text
     * @param withFullErrorPath true if you want the whole parent error path appended, false otherwise
     * @param errorParameters zero or more string parameters for the displayed error message
     * @return true if the replacement occurred
     */
    private boolean replaceError(String propertyName, String targetKey, boolean withFullErrorPath, String replaceKey, String... replaceParameters) {
        boolean replaced = false;

        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }
        if (StringUtils.isBlank(targetKey)) {
            throw new IllegalArgumentException("invalid (blank) targetKey");
        }
        if (StringUtils.isBlank(replaceKey)) {
            throw new IllegalArgumentException("invalid (blank) replaceKey");
        }

        // check if we have previous errors for this property
        TypedArrayList errorList = null;
        String propertyKey = getKeyPath((String) propertyName, withFullErrorPath);
        if (messages.containsKey(propertyKey)) {
            errorList = (TypedArrayList) messages.get(propertyKey);

            // look for the specific targetKey
            for (int i = 0; i < errorList.size(); ++i) {
                ErrorMessage em = (ErrorMessage) errorList.get(i);

                // replace matching messages
                if (em.getErrorKey().equals(targetKey)) {
                    ErrorMessage rm = new ErrorMessage(replaceKey, replaceParameters);
                    errorList.set(i, rm);
                    replaced = true;
                }
            }
        }

        return replaced;
    }


    /**
     * Returns true if the named field has a message with the given errorKey
     * 
     * @param errorKey
     * @param fieldName
     * @return boolean
     */
    public boolean fieldHasMessage(String fieldName, String errorKey) {
        boolean found = false;

        List fieldMessages = (List) messages.get(fieldName);
        if (fieldMessages != null) {
            for (Iterator i = fieldMessages.iterator(); !found && i.hasNext();) {
                ErrorMessage errorMessage = (ErrorMessage) i.next();
                found = errorMessage.getErrorKey().equals(errorKey);
            }
        }

        return found;
    }

    /**
     * Returns the number of messages for the given field
     * 
     * @param fieldName
     * @return int
     */
    public int countFieldMessages(String fieldName) {
        int count = 0;

        List fieldMessages = (List) messages.get(fieldName);
        if (fieldMessages != null) {
            count = fieldMessages.size();
        }

        return count;
    }


    /**
     * @return true if the given messageKey is associated with some property in this ErrorMap
     */
    public boolean containsMessageKey(String messageKey) {
        ErrorMessage foundMessage = null;

        if (!isEmpty()) {
            for (Iterator i = entrySet().iterator(); (foundMessage == null) && i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();
                String entryKey = (String) e.getKey();
                TypedArrayList entryErrorList = (TypedArrayList) e.getValue();
                for (Iterator j = entryErrorList.iterator(); j.hasNext();) {
                    ErrorMessage em = (ErrorMessage) j.next();
                    if (messageKey.equals(em.getErrorKey())) {
                        foundMessage = em;
                    }
                }
            }
        }

        return (foundMessage != null);
    }


    /**
     * Counts the total number of error messages in the map
     * 
     * @return returns an int for the total number of errors
     */
    public int getErrorCount() {
        int errorCount = 0;
        for (Iterator iter = messages.keySet().iterator(); iter.hasNext();) {
            String errorKey = (String) iter.next();
            List errors = (List) messages.get(errorKey);
            errorCount += errors.size();
        }

        return errorCount;
    }

    /**
     * @param path
     * @return Returns a List of ErrorMessages for the given path
     */
    public TypedArrayList getMessages(String path) {
        return (TypedArrayList) messages.get(path);
    }

    /**
     * Adds a string prefix to the error path.
     * 
     * @param parentName
     */
    public void addToErrorPath(String parentName) {
        errorPath.add(parentName);
    }

    /**
     * This method returns the list that holds the error path values.
     * 
     * @return List
     */
    public List getErrorPath() {
        return errorPath;
    }

    /**
     * Removes a string prefix from the error path.
     * 
     * @param parentName
     * @return boolean Returns true if the parentName existed, false otherwise.
     */
    public boolean removeFromErrorPath(String parentName) {
        return errorPath.remove(parentName);
    }

    /**
     * Clears the errorPath.
     */
    public void clearErrorPath() {
        errorPath = new ArrayList();
    }

    /**
     * This is what's prepended to the beginning of the key. This is built by iterating over all of the entries in the errorPath
     * list and concatenating them together witha "."
     * 
     * @return String Returns the keyPath.
     * @param propertyName
     * @param prependFullErrorPath
     */
    public String getKeyPath(String propertyName, boolean prependFullErrorPath) {
        String keyPath = "";

        if (Constants.GLOBAL_ERRORS.equals(propertyName)) {
            return Constants.GLOBAL_ERRORS;
        }

        if (!errorPath.isEmpty() && prependFullErrorPath) {
            keyPath = StringUtils.join(errorPath.iterator(), ".");
            keyPath += "." + propertyName;
        }
        else {
            keyPath = propertyName;
        }

        return keyPath;
    }

    /**
     * @return List of the property names that have errors.
     */
    public List getPropertiesWithErrors() {
        List properties = new ArrayList();

        for (Iterator iter = messages.keySet().iterator(); iter.hasNext();) {
            properties.add(iter.next());
        }

        return properties;
    }

    // methods added to complete the Map interface
    /**
     * Clears the messages list.
     */
    public void clear() {
        messages.clear();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return messages.containsKey(key);
    }

    /**
     * @param pattern comma separated list of keys, optionally ending with * wildcard
     */
    public boolean containsKeyMatchingPattern(String pattern) {
        ArrayList simplePatterns = new ArrayList();
        ArrayList wildcardPatterns = new ArrayList();
        String[] patterns = pattern.split(",");
        for (int i = 0; i < patterns.length; i++) {
            String s = patterns[i];
            if (s.endsWith("*")) {
                wildcardPatterns.add(s.substring(0, s.length() - 1));
            }
            else {
                simplePatterns.add(s);
            }
        }
        for (Iterator keys = messages.keySet().iterator(); keys.hasNext();) {
            String key = (String) keys.next();
            if (simplePatterns.contains(key)) {
                return true;
            }
            for (Iterator wildcardIterator = wildcardPatterns.iterator(); wildcardIterator.hasNext();) {
                String wildcard = (String) wildcardIterator.next();
                if (key.startsWith(wildcard)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        return messages.entrySet();
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object key) {
        return messages.get(key);
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return messages.isEmpty();
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        return messages.keySet();
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        return messages.remove(key);
    }

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        return messages.size();
    }

    // forbidden-but-required operations
    /**
     * Prevent people from adding arbitrary objects to the messages Map
     * 
     * @param key
     * @param value
     * @return Object
     */
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Prevent people from adding arbitrary objects to the messages Map
     * 
     * @param arg0
     */
    public void putAll(Map arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * Prevent people from directly accessing the values, since the input parameter isn't strongly-enough typed
     * 
     * @param value
     * @return boolean
     */
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Prevent people from directly accessing the values, since the input parameter isn't strongly-enough typed
     * 
     * @return Collection
     */
    public Collection values() {
        throw new UnsupportedOperationException();
    }

    /**
     * Renders as a String, to help debug tests.
     * 
     * @return a String, to help debug tests.
     */
    @Override
    public String toString() {
        return "ErrorMap (errorPath = " + errorPath + ", messages = " + messages + ")";
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;

        if (this == obj) {
            equals = true;
        }
        else if (obj instanceof ErrorMap) {
            ErrorMap other = (ErrorMap) obj;

            if (getErrorPath().equals(other.getErrorPath())) {
                if (size() == other.size()) {
                    if (entrySet().equals(other.entrySet())) {
                        equals = true;
                    }
                }
            }
        }

        return equals;
    }

    /**
     * Returns the size, since that meets with the requirements of the hashCode contract, and since I don't expect ErrorMap to be
     * used as the key in a Map.
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return size();
    }
}