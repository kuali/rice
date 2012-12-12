/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.parse;

/**
 * Data storage class for information related to a single property of a defined custom tag.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BeanTagAttributeInfo {
    private String name;
    private BeanTagAttribute.AttributeType type;

    /**
     * Constructor initializing the global variables
     */
    public BeanTagAttributeInfo() {
        name = null;
        type = null;
    }

    /**
     * Sets the name of the property being defined by the attribute or tag.
     *
     * @param name - The name of the property.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the type of information being stored by the property.
     *
     * @param type - The type of information being stored by the property.
     */
    public void setType(BeanTagAttribute.AttributeType type) {
        this.type = type;
    }

    /**
     * Retrieves the name of the stored property.
     * This is the name of the property being defined by the tag property.
     *
     * @return The name of the property.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the type of information being stored.
     * This is the type of information being stored and is used to decide how to parse the information in the xml.
     *
     * @return The type of information being stored by the tag.
     */
    public BeanTagAttribute.AttributeType getType() {
        return type;
    }
}
