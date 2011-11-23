/**
 * Copyright 2005-2011 The Kuali Foundation
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

import org.junit.Test
import org.kuali.rice.kns.web.ui.Row
import org.kuali.rice.core.api.uif.RemotableAttributeField
import org.kuali.rice.core.api.uif.DataType
import org.kuali.rice.core.api.uif.RemotableCheckboxGroup
import org.kuali.rice.core.api.uif.RemotableSelect
import org.kuali.rice.core.api.uif.RemotableDatepicker
import org.kuali.rice.core.api.uif.RemotableTextInput
import static org.junit.Assert.assertEquals
import org.kuali.rice.kns.web.ui.Field

/**
 * Tests FieldUtils
 */
class FieldUtilsTest {

    /**
     * Performs an as-of-yet very superficial check of remotableattributefield conversion
     */
    @Test
    void testConvertRemotableAttributeFields() {
        def fields = [ ] as ArrayList<RemotableAttributeField>
        def field = RemotableAttributeField.Builder.create("string")
        field.shortLabel = "s"
        field.dataType = DataType.STRING
        field.maxLength = 10

        fields << field.build()

        field = RemotableAttributeField.Builder.create("boolean")
        field.shortLabel = "b"
        field.dataType = DataType.BOOLEAN
        field.defaultValues = [ "true" ] as Collection<String>
        field.control = RemotableCheckboxGroup.Builder.create(["2be": "To Be or Not To Be"])

        fields << field.build()

        field = RemotableAttributeField.Builder.create("long")
        field.shortLabel = "k"
        field.dataType = DataType.LONG
        field.defaultValues = [ "1" ] as Collection<String>
        field.control = RemotableSelect.Builder.create(["one" : "1", "two" : "2"]);

        fields << field.build()

        field = RemotableAttributeField.Builder.create("date")
        field.shortLabel = "d"
        field.dataType = DataType.DATE
        def input = RemotableTextInput.Builder.create()
        input.size = 20
        field.control = input

        fields << field.build()

        List<Row> rows = FieldUtils.convertRemotableAttributeFields(fields)

        //displayRows(rows)

        assertConvertedRows(rows, fields)
    }

    protected assertConvertedRows(List<Row> rows, List<RemotableAttributeField> rafs) {
        assertEquals(rows.size(), rafs.size())
        rows.eachWithIndex {
            it, i ->
            def f = it.fields[0]
            assertEquals(rafs[i].name, f.propertyName)
            assertEquals(rafs[i].shortLabel, f.fieldLabel)
            // XXX: conversion does not set this field ??
            //assertEquals(rafs[i].dataType, f.fieldDataType)
            assertEquals(new Field("","").fieldDataType, f.fieldDataType)
            switch (rafs[i].dataType) {
                case DataType.STRING:
                    assertEquals(Field.TEXT, f.fieldType)
                    break
                case DataType.BOOLEAN:
                    assertEquals(Field.CHECKBOX, f.fieldType)
                    break
                case DataType.LONG:
                    assertEquals(Field.DROPDOWN, f.fieldType)
                    break
                case DataType.DATE:
                    assertEquals(Field.TEXT, f.fieldType)
                    break
            }
        }
    }

//    protected displayRows(List<Row> rows) {
//        rows.each {
//            it.fields.each {
//                println it.propertyName
//                println it.fieldLabel
//                println it.fieldType
//            }
//        }
//    }
}
