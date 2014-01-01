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
package org.kuali.rice.krad.datadictionary;

import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;

/**
 * A complex attribute definition in the DataDictictionary. This can be be used to define
 * an attribute for a DataObjectEntry's attribute list which is represented by another
 * object entry definition. It will
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComplexAttributeDefinition extends AttributeDefinitionBase {
    private static final long serialVersionUID = 3977923609388434447L;

    protected DataDictionaryEntry dataObjectEntry;

    /**
     * @return the dataObjectEntry
     */
    public DataDictionaryEntry getDataObjectEntry() {
        return this.dataObjectEntry;
    }

    /**
     * @param dataObjectEntry the dataObjectEntry to set
     */
    public void setDataObjectEntry(DataDictionaryEntry dataObjectEntry) {
        this.dataObjectEntry = dataObjectEntry;
    }

    public void completeValidation(Class<?> rootObjectClass, Class<?> otherObjectClass, ValidationTrace tracer) {
        tracer.addBean(this.getClass().getSimpleName(), "id: " + getId());
        if (getDataObjectEntry() == null) {
            String currentValues[] = {"id = " + getId(), "class = " + rootObjectClass.getName()};
            tracer.createError("ComplexAttributeDefinition missing dataObjectClass", currentValues);
        }
    }
}
