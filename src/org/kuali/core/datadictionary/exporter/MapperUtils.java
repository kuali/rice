/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.datadictionary.exporter;

import org.kuali.core.datadictionary.FieldDefinition;
import org.kuali.core.datadictionary.FieldPairDefinition;

/**
 * Defines utility methods associated with mapping Entries et al
 * 
 * 
 */
public class MapperUtils {
    /**
     * @param attributeName
     * @return ExportMap containing the standard entries associated with an attribute name
     */
    public static ExportMap buildAttributeMap(String attributeName) {
        ExportMap attributeMap = new ExportMap(attributeName);

        attributeMap.set("attributeName", attributeName);

        return attributeMap;
    }

    /**
     * @param fieldDefinition
     * @return ExportMap containing the standard entries associated with a FieldDefinition
     */
    public static ExportMap buildFieldMap(FieldDefinition field) {
        return buildAttributeMap(field.getAttributeName());
    }

    /**
     * @param index
     * @param fieldPair
     * @return ExportMap containing the standard entries associated with an indexed FieldPairDefinition
     */
    public static ExportMap buildFieldPairMap(FieldPairDefinition fieldPair, int index) {
        ExportMap fieldPairMap = new ExportMap(Integer.toString(index));

        fieldPairMap.set("index", Integer.toString(index));
        fieldPairMap.set("fieldTo", fieldPair.getFieldTo());
        fieldPairMap.set("fieldFrom", fieldPair.getFieldFrom());

        return fieldPairMap;
    }
}