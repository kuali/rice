/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.core.xml;

import java.util.Map;

/**
 * Represents a Map between ChainedXMLFilter and a set of optional Properties
 */
public class XMLInputFilterEntry {
    private Class<? extends ChainedXMLFilter> filterClass;
    private Map<String, Object> properties;

    /**
     * Returns the Class that will be used to upgrade a Document's content
     * from the previous version to this one.
     *
     * @return The Filter Class
     */
    public Class<? extends ChainedXMLFilter> getFilterClass() {
        return filterClass;
    }

    /**
     * Sets the Class that will be used to upgrade a Document's content
     * from the previous version to this one.
     *
     * @param filterClass The Filter Class
     */    
    public void setFilterClass(Class<? extends ChainedXMLFilter> filterClass) {
        this.filterClass = filterClass;
    }

    /**
     * Returns a Map containing the property settings that will be used to
     * configure the filterClass after it has been instantiated.
     *
     * @return The Filter Properties
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Sets a Map containing the property settings that will be used to
     * configure the filterClass after it has been instantiated.
     *
     * @param properties The Filter Properties
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
