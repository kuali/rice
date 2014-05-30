/**
 * Copyright 2005-2014 The Kuali Foundation
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation flag that the connected method is represented by the declared name in Spring Beans created using the
 * custom schema.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanTagAttribute {

    /*
     * Represents the type of an attribute within the schema (determines how it will be parsed).
     *
     * <ul>
     *     <li>Any - Property holds HTML content (any tags can be nested and populated as a string)</li>
     *     <li>ByType - The property tag itself can be missing, and any beans that match the type of the property
     *     will be used to populate its value. Note, only one attribute per class (and its supers) can have
     *     this setting</li>
     *     <li>Direct - The bean can be populated directly on the property tag. This means there is a bean
     *     with the same tag name as the property (prevent nested tags with the same name).</li>
     *     <li>DirectOrByType - Property can be configured by type or direct</li>
     *     <li>SingleValue - Property is a single standard value (attribute). (DEFAULT)</li>
     *     <li>SingleBean - Property is a single bean object</li>
     *     <li>ListBean - Property is a list consisting of beans</li>
     *     <li>ListValue - Property is a list consisting of standard values (string, int, char, etc)</li>
     *     <li>MapValue - The property is a map that consists of String keys and String values</li>
     *     <li>MapBean - The property is a map that consists of either String or bean keys and bean values</li>
     *     <li>SetValue - The property is a set consisting of standard values</li>
     *     <li>SetBean - The property is a set consisting of beans</li>
     * </ul>
     */
    public enum AttributeType {
        ANY, BYTYPE, DIRECT, DIRECTORBYTYPE, SINGLEVALUE, SINGLEBEAN, LISTBEAN, LISTVALUE, MAPVALUE, MAPBEAN,
        SETVALUE, SETBEAN, NOTSET
    }

    // name to use for the attribute in the custom schema, this will default to the name of the property if not set
    String name() default "";

    // the type of the property defining how it should be parsed, generally this can be derived from the type
    // and does not need to be set. Only in cases where the attribute needs to be set by type, direct, or any
    AttributeType type() default AttributeType.NOTSET;
}
