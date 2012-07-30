/*
 * Copyright 2006-2012 The Kuali Foundation
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

package org.kuali.rice.kns.util

import org.kuali.rice.kns.web.ui.Section
import org.junit.Test
import org.kuali.rice.kns.web.ui.Row
import org.kuali.rice.kns.web.ui.Field

import static org.junit.Assert.assertEquals

/**
 * Unit tests WebUtils
 */
class WebUtilsTest {
    protected Field generateContainerField(String name, String label, String containerName) {
        def f = FieldUtils.constructContainerField(name, label, [
                new Field(fieldLabel: "Field One", propertyName: "field1", propertyValue: "value one"),
                new Field(fieldLabel: "Field Two", propertyName: "field2", propertyValue: "value two"),
                new Field(fieldLabel: "Field Three", propertyName: "field3", propertyValue: "value three"),
                new Field(fieldLabel: "Multi-value Field", propertyName: "multiValueField", propertyValues: [ "value1", "value2", "value3" ])
        ], 50)
        f.setContainerElementName(containerName + "-element")
        f.setContainerName(containerName)
        f.setContainerDisplayFields( [
                new Field(fieldLabel: "Contained Field One", propertyName: "containedField1", propertyValue: "contained value one"),
                new Field(fieldLabel: "Contained Field Two", propertyName: "containedField2", propertyValue: "contained value two"),
                new Field(fieldLabel: "Contained Field Three", propertyName: "containedField3", propertyValue: "contained value three"),
                new Field(fieldLabel: "Contained Multi-value Field", propertyName: "multiValueField", propertyValues: [ "value1", "value2", "value3" ])
        ])
        f
    }

    protected Row generateRow(String sectionNo, String rowNo) {
        new Row([
            generateContainerField("sections[$sectionNo].rows[$rowNo].field0", "field-$sectionNo-$rowNo-0", "field_${sectionNo}_${rowNo}_0"),
            generateContainerField("sections[$sectionNo].rows[$rowNo].field1", "field-$sectionNo-$rowNo-1", "field_${sectionNo}_${rowNo}_1"),
            generateContainerField("sections[$sectionNo].rows[$rowNo].field2", "field-$sectionNo-$rowNo-2", "field_${sectionNo}_${rowNo}_2")
        ])
    }

    protected Section generateSection(String num) {
        new Section([
            generateRow(num, "0"),
            generateRow(num, "Uno"),
            generateRow(num, "Dos")
        ])
    }

    @Test
    void testReopenInactiveRecords() {
        def sections = [
            generateSection("0"),
            generateSection("Un"),
            generateSection("Deux")
        ]
        def tabstates = [:]

        // XXX: FieldUtils.generateCollectionSubTabName strips digits from container element name... is that right?
        WebUtils.reopenInactiveRecords(sections, tabstates, "field_0_0_0")
        assertEquals(["field___elementcontainedvalueonecontainedvaluetwocontainedvaluethree": "OPEN"], tabstates)

        WebUtils.reopenInactiveRecords(sections, tabstates, "field_Un_Uno_1")
        assertEquals(["field___elementcontainedvalueonecontainedvaluetwocontainedvaluethree": "OPEN",
                      "field_Un_Uno_elementcontainedvalueonecontainedvaluetwocontainedvaluethree": "OPEN"], tabstates)

        // should switch existing value
        tabstates = ["field_Deux_Dos_elementcontainedvalueonecontainedvaluetwocontainedvaluethree": "CLOSE"]
        WebUtils.reopenInactiveRecords(sections, tabstates, "field_Deux_Dos_2")
        assertEquals(["field_Deux_Dos_elementcontainedvalueonecontainedvaluetwocontainedvaluethree": "OPEN"], tabstates)
    }
}
