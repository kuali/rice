/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;

/**
 * Common base for all objects that can be configured in the dictionary
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DictionaryBeanBase implements DictionaryBean {

    private String namespaceCode;
    private String componentCode;

    public DictionaryBeanBase() {}

    /**
     * @see DictionaryBean#getNamespaceCode()
     */
    @Override
    @BeanTagAttribute(name = "namespaceCode")
    public String getNamespaceCode() {
        return namespaceCode;
    }

    /**
     * Setter for the bean's associated namespace code
     *
     * @param namespaceCode
     */
    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    /**
     * @see DictionaryBean#getComponentCode()
     */
    @Override
    @BeanTagAttribute(name = "componentCode")
    public String getComponentCode() {
        return componentCode;
    }

    /**
     * Setter for the bean's associated component code
     *
     * @param componentCode
     */
    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    /**
     * Helper method to allow reasonable names to be defaulted from class or property names
     */
    protected String getLabelFromCamelCasedName(String name) {
        // We only want to include the component after the last property separator
        if (name.contains(".")) {
            name = StringUtils.substringAfterLast(name, ".");
        }
        StringBuilder label = new StringBuilder(name);
        // upper case the 1st letter
        label.replace(0, 1, label.substring(0, 1).toUpperCase());
        // loop through, inserting spaces when cap
        for (int i = 0; i < label.length(); i++) {
            if (Character.isUpperCase(label.charAt(i))) {
                label.insert(i, ' ');
                i++;
            }
        }

        return label.toString().trim();
    }

    /**
     * Copies object by value
     *
     * @return copiedClass
     */
    public <T> T copy() {
        T copiedClass = null;
        try {
            copiedClass = (T)this.getClass().newInstance();
        }
        catch(Exception exception) {
            throw new RuntimeException(exception);
        }

        copyProperties(copiedClass);

        return copiedClass;
    }

    /**
     * Copies properties for copy()
     *
     * @param dictionaryBeanBase base bean
     */
    protected <T> void copyProperties(T dictionaryBeanBase) {
        DictionaryBeanBase dictionaryBeanBaseCopy = (DictionaryBeanBase) dictionaryBeanBase;
        dictionaryBeanBaseCopy.setComponentCode(this.componentCode);
        dictionaryBeanBaseCopy.setNamespaceCode(this.namespaceCode);
    }

    @Override
    public void dataDictionaryPostProcessing() {
        // Do nothing here - will be implemented by subclasses
    }
}
