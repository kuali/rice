/**
 * Copyright 2005-2017 The Kuali Foundation
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

/**
 * Performs overrides on properties of fields in a Data Dictionary bean.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface FieldOverride {
    /**
     * Return the property name to perform the override.
     *
     * @return property name
     */
    public String getPropertyName();

    /**
     * perform the override.
     *
     * @param bean data dictionary bean
     * @param property original property value
     * @return overridden property value
     */
    public Object performFieldOverride(Object bean, Object property);
}
