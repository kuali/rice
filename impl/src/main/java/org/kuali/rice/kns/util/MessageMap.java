/*
 * Copyright 2005-2009 The Kuali Foundation
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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * Holds errors due to validation. Keys of map represent property paths, and value is a TypedArrayList that contains resource string
 * keys (to retrieve the error message).
 *
 * Note, prior to rice 0.9.4, this class implemented {@link java.util.Map}.  The implements has been removed as of rice 0.9.4
 */
public class MessageMap implements Serializable {
    private static final long serialVersionUID = -2328635367656516150L;
    private List<String> errorPath = new ArrayList<String>();
    private Map<String, TypedArrayList> errorMessages = new LinkedHashMap<String, TypedArrayList>();
    private Map<String, TypedArrayList> warningMessages = new LinkedHashMap<String, TypedArrayList>();
    private Map<String, TypedArrayList> infoMessages = new LinkedHashMap<String, TypedArrayList>();

    public MessageMap() {}
    
    public MessageMap(MessageMap messageMap) {
    	this.errorPath = messageMap.errorPath;
    	this.errorMessages = messageMap.errorMessages;
    	this.warningMessages = messageMap.warningMessages;
    	this.infoMessages = messageMap.infoMessages;
    }
    
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
        return putMessageInMap(errorMessages, propertyName, errorKey, true, true, errorParameters);
    }

    public TypedArrayList putWarning(String propertyName, String messageKey, String... messageParameters) {
        return putMessageInMap(warningMessages, propertyName, messageKey, true, true, messageParameters);
    }

    public TypedArrayList putInfo(String propertyName, String messageKey, String... messageParameters) {
        return putMessageInMap(infoMessages, propertyName, messageKey, true, true, messageParameters);
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
        return putMessageInMap(errorMessages, propertyName, errorKey, false, true, errorParameters);
    }

    public TypedArrayList putWarningWithoutFullErrorPath(String propertyName, String messageKey, String... messageParameters) {
        return putMessageInMap(errorMessages, propertyName, messageKey, false, true, messageParameters);
    }

    public TypedArrayList putInfoWithoutFullErrorPath(String propertyName, String messageKey, String... messageParameters) {
        return putMessageInMap(errorMessages, propertyName, messageKey, false, true, messageParameters);
    }

    /**
     * Adds an error related to a particular section identified by its section ID.  For maintenance documents, the section ID is identified
     * by calling {@link org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition#getId()}
     *
     * @param sectionId
     * @param errorKey
     * @param errorParameters
     * @return
     */
    public TypedArrayList putErrorForSectionId(String sectionId, String errorKey, String... errorParameters) {
    	return putErrorWithoutFullErrorPath(sectionId, errorKey, errorParameters);
    }

    public TypedArrayList putWarningForSectionId(String sectionId, String messageKey, String... messageParameters) {
    	return putWarningWithoutFullErrorPath(sectionId, messageKey, messageParameters);
    }

    public TypedArrayList putInfoForSectionId(String sectionId, String messageKey, String... messageParameters) {
    	return putInfoWithoutFullErrorPath(sectionId, messageKey, messageParameters);
    }

    /**
     * adds an error to the map under the given propertyName and adds an array of message parameters.
     *
     * @param propertyName name of the property to add error under
     * @param messageKey resource key used to retrieve the error text from the error message resource bundle
     * @param withFullErrorPath true if you want the whole parent error path appended, false otherwise
     * @param escapeHtmlMessageParameters whether to escape HTML characters in the message parameters, provides protection against XSS attacks
     * @param messageParameters zero or more string parameters for the displayed error message
     * @return TypeArrayList
     */
    private TypedArrayList putMessageInMap(Map<String, TypedArrayList> messagesMap, String propertyName, String messageKey, boolean withFullErrorPath, boolean escapeHtmlMessageParameters, String... messageParameters) {
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }
        if (StringUtils.isBlank(messageKey)) {
            throw new IllegalArgumentException("invalid (blank) errorKey");
        }

        // check if we have previous errors for this property
        TypedArrayList errorList = null;
        String propertyKey = getKeyPath((String) propertyName, withFullErrorPath);
        if (messagesMap.containsKey(propertyKey)) {
            errorList = (TypedArrayList) messagesMap.get(propertyKey);
        }
        else {
            errorList = new TypedArrayList(ErrorMessage.class);
        }

        if (escapeHtmlMessageParameters && messageParameters != null) {
        	String[] filteredMessageParameters = new String[messageParameters.length];
        	for (int i = 0; i < messageParameters.length; i++) {
        		filteredMessageParameters[i] = StringEscapeUtils.escapeHtml(messageParameters[i]);
        	}
        	messageParameters = filteredMessageParameters;
        }

        // add error to list
        ErrorMessage errorMessage = new ErrorMessage(messageKey, messageParameters);
        // check if this error has already been added to the list
        if ( !errorList.contains( errorMessage ) ) {
            errorList.add(errorMessage);
        }

        return (TypedArrayList) messagesMap.put(propertyKey, errorList);
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
        if (errorMessages.containsKey(propertyKey)) {
            errorList = (TypedArrayList) errorMessages.get(propertyKey);

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

        List fieldMessages = (List) errorMessages.get(fieldName);
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

        List fieldMessages = (List) errorMessages.get(fieldName);
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


    private int getMessageCount(Map<String, TypedArrayList> messageMap) {
        int messageCount = 0;
        for (Iterator iter = messageMap.keySet().iterator(); iter.hasNext();) {
            String errorKey = (String) iter.next();
            List errors = (List) messageMap.get(errorKey);
            messageCount += errors.size();
        }

        return messageCount;
    }

    /**
     * Counts the total number of error messages in the map
     *
     * @return returns an int for the total number of errors
     */
    public int getErrorCount() {
    	return getMessageCount(errorMessages);
    }

    /**
     * Counts the total number of warning messages in the map
     *
     * @return returns an int for the total number of warnings
     */
    public int getWarningCount() {
    	return getMessageCount(warningMessages);
    }

    /**
     * Counts the total number of info messages in the map
     *
     * @return returns an int for the total number of info
     */
    public int getInfoCount() {
    	return getMessageCount(infoMessages);
    }

    /**
     * @param path
     * @return Returns a List of ErrorMessages for the given path
     */
    public TypedArrayList getMessages(String path) {
        return (TypedArrayList) errorMessages.get(path);
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
    public List<String> getErrorPath() {
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
        errorPath.clear();
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

        if (KNSConstants.GLOBAL_ERRORS.equals(propertyName)) {
            return KNSConstants.GLOBAL_ERRORS;
        }

        if (!errorPath.isEmpty() && prependFullErrorPath) {
            keyPath = StringUtils.join(errorPath.iterator(), ".");
            keyPath += (keyPath!=null && keyPath.endsWith("."))?propertyName:"." + propertyName;
        }
        else {
            keyPath = propertyName;
        }

        return keyPath;
    }

    /**
     * @return List of the property names that have errors.
     */
    public List<String> getPropertiesWithErrors() {
        List<String> properties = new ArrayList<String>();

        for (Iterator<String> iter = errorMessages.keySet().iterator(); iter.hasNext();) {
            properties.add(iter.next());
        }

        return properties;
    }

    /**
     * @return List of the property names that have warnings.
     */
    public List<String> getPropertiesWithWarnings() {
        List<String> properties = new ArrayList<String>(warningMessages.keySet());
        return properties;
    }

    /**
     * @return List of the property names that have info.
     */
    public List<String> getPropertiesWithInfo() {
        List<String> properties = new ArrayList<String>(infoMessages.keySet());
        return properties;
    }

    // methods added to complete the Map interface
    /**
     * Clears the messages list.
     *
     * @deprecated As of rice 0.9.4, use {@link #clearErrorMessages()} instead
     */
    @Deprecated
    public void clear() {
        clearErrorMessages();
    }

    public void clearErrorMessages() {
    	errorMessages.clear();
    }

    /**
     * @deprecated As of rice 0.9.4, use {@link #doesPropertyHaveError(String)} instead
     */
    @Deprecated
    public boolean containsKey(Object key) {
        return doesPropertyHaveError((String) key);
    }

    public boolean doesPropertyHaveError(String key) {
    	return errorMessages.containsKey(key);
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
        for (Iterator<String> keys = errorMessages.keySet().iterator(); keys.hasNext();) {
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
     * @deprecated As of rice 0.9.4, use {@link #getAllPropertiesAndErrors()} instead
     */
    @Deprecated
    public Set entrySet() {
        return getAllPropertiesAndErrors();
    }

    public Set<Map.Entry<String, TypedArrayList>> getAllPropertiesAndErrors() {
    	return errorMessages.entrySet();
    }

    /**
     * @deprecated As of rice 0.9.4, use {@link #getErrorMessagesForProperty(String)} instead
     */
    @Deprecated
    public Object get(Object key) {
        return getErrorMessagesForProperty((String) key);
    }

    public TypedArrayList getErrorMessagesForProperty(String propertyName) {
    	return errorMessages.get(propertyName);
    }

    public TypedArrayList getWarningMessagesForProperty(String propertyName) {
    	return warningMessages.get(propertyName);
    }

    public TypedArrayList getInfoMessagesForProperty(String propertyName) {
    	return infoMessages.get(propertyName);
    }

    /**
     * @deprecated As of rice 0.9.4, use {@link #hasNoErrors()} instead
     */
    @Deprecated
    public boolean isEmpty() {
        return hasNoErrors();
    }

    public boolean hasErrors() {
    	return !errorMessages.isEmpty();
    }

    public boolean hasNoErrors() {
    	return errorMessages.isEmpty();
    }

    public boolean hasWarnings() {
    	return !warningMessages.isEmpty();
    }

    public boolean hasNoWarnings() {
    	return warningMessages.isEmpty();
    }

    public boolean hasInfo() {
    	return !infoMessages.isEmpty();
    }

    public boolean hasNoInfo() {
    	return infoMessages.isEmpty();
    }

    public boolean hasMessages() {
        if (!errorMessages.isEmpty()
                || !warningMessages.isEmpty()
                || !infoMessages.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean hasNoMessages() {
        if (errorMessages.isEmpty()
                && warningMessages.isEmpty()
                && infoMessages.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * @deprecated As of rice 0.9.4, use {@link #getAllPropertiesWithErrors()} instead
     */
    @Deprecated
    public Set keySet() {
        return getAllPropertiesWithErrors();
    }

    public Set<String> getAllPropertiesWithErrors() {
    	return errorMessages.keySet();
    }

    public Set<String> getAllPropertiesWithWarnings() {
    	return warningMessages.keySet();
    }

    public Set<String> getAllPropertiesWithInfo() {
    	return infoMessages.keySet();
    }

    /**
     * @deprecated as of rice 0.9.4, use {@link #removeAllErrorMessagesForProperty(String)} instead
     */
    @Deprecated
    public Object remove(Object key) {
        return removeAllErrorMessagesForProperty((String) key);
    }

    public TypedArrayList removeAllErrorMessagesForProperty(String property) {
    	return errorMessages.remove(property);
    }

    public TypedArrayList removeAllWarningMessagesForProperty(String property) {
    	return warningMessages.remove(property);
    }

    public TypedArrayList removeAllInfoMessagesForProperty(String property) {
    	return infoMessages.remove(property);
    }

    /**
     * @deprecated As of rice 0.9.4, use {@link #getNumberOfPropertiesWithErrors()} instead
     */
    @Deprecated
    public int size() {
        return getNumberOfPropertiesWithErrors();
    }

    public int getNumberOfPropertiesWithErrors() {
    	return errorMessages.size();
    }

    // forbidden-but-required operations
    /**
     * @deprecated As of rice 0.9.4, deprecated because this method always throws an {@link UnsupportedOperationException}
     */
    @Deprecated
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated As of rice 0.9.4, deprecated because this method always throws an {@link UnsupportedOperationException}
     */
    @Deprecated
    public void putAll(Map arg0) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated As of rice 0.9.4, this method has been deprecated since it always throws a {@link UnsupportedOperationException}
     */
    @Deprecated
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated As of rice 0.9.4, deprecated because this method always throws an {@link UnsupportedOperationException}
     */
    @Deprecated
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
        return "ErrorMap (errorPath = " + errorPath + ", messages = " + errorMessages + ")";
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
        else if (obj instanceof MessageMap) {
            MessageMap other = (MessageMap) obj;

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

    public Map<String, TypedArrayList> getErrorMessages() {
        return this.errorMessages;
    }

    public Map<String, TypedArrayList> getWarningMessages() {
        return this.warningMessages;
    }

    public Map<String, TypedArrayList> getInfoMessages() {
        return this.infoMessages;
    }
}
