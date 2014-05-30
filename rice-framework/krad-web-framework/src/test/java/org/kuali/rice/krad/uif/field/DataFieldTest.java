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
package org.kuali.rice.krad.uif.field;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DataFieldTest {
    @Test
    /**
     * Tests setting and retrieving default value
     */
    public void testSetDefaultValueSucceeds() {

        // create mock objects for view, model, and component
        String defaultValue = "default";

        DataField dataField = new DataFieldBase();
        dataField.setDefaultValue(defaultValue);
        assertEquals(defaultValue, dataField.getDefaultValue());
    }

    @Test
    /**
     * Tests setting and retrieving default values
     */
    public void testSetDefaultValuesSucceeds() {
        // create mock objects for view, model, and component
        List<Object> defaultValues = new ArrayList<Object>();
        defaultValues.add("A");
        defaultValues.add("B");

        DataField dataField = new DataFieldBase();
        dataField.setDefaultValues(defaultValues);
        assertEquals(defaultValues, dataField.getDefaultValues());
    }
}
