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
package org.kuali.rice.krad.uif.widget;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.datadictionary.validation.Account;
import org.kuali.rice.krad.datadictionary.validation.Employee;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * test the RichTable widget
 */

public class RichTableTest {

    private RichTable richTable;
    private CollectionGroup group;

    //private
    @Before
    public void setup(){

        richTable = new RichTable();

        group = new CollectionGroup();
        group.setCollectionObjectClass(Employee.class);
        TableLayoutManager layoutManager = new TableLayoutManager();
        layoutManager.setRenderSequenceField(true);
        group.setLayoutManager(layoutManager);
        group.setRenderSelectField(false);
        group.setRenderLineActions(false);

        List<Component> items = new ArrayList<Component>(1);
        DataField name = new DataField();
        name.setPropertyName("employeeId");
        items.add(name);
        DataField number = new DataField();
        number.setPropertyName("positionTitle");
        items.add(number);
        DataField contactEmail = new DataField();
        contactEmail.setPropertyName("contactEmail");
        items.add(contactEmail);

        group.setItems(items);
    }

    @Test
    /**
     * test that without aoColumns being set explicitly, the default behaviour continues
     */
    public void testComponentOptionsDefault() throws Exception {
        String expected = "[ null ,{\"sSortDataType\" : \"dom-text\" , \"sType\" : \"string\"} , "
                + "{\"sSortDataType\" : \"dom-text\" , \"sType\" : \"string\"} , "
                + "{\"sSortDataType\" : \"dom-text\" , \"sType\" : \"string\"} ]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AO_COLUMNS);
    }


    @Test
    /**
     * test that when aoColumns is explicitly set, it is integrated into the rich table rendering logic
     */
    public void testComponentOptionsAoColumnsJSOptions() throws Exception {
        String innerColValues = "{bVisible: false}, null, null";
        String options = "[" + innerColValues + "]";
        String expected = "[ null ," + innerColValues + " ]";
        assertRichTableComponentOptions(options, expected, UifConstants.TableToolsKeys.AO_COLUMNS);
    }

    @Test
    /**
     * test whether a hidden column, when marked as sortable is still hidden
     * @throws Exception
     */
     public void testComponentOptionsHideColumnOnRichTable() {
        Set<String> hiddenColumns = new HashSet<String>();
        hiddenColumns.add("employeeId");
        Set<String> sortableColumns = new HashSet<String>();
        //sortableColumns.add("employeeId");
        sortableColumns.add("positionTitle");
        richTable.setSortableColumns(sortableColumns);
        richTable.setHiddenColumns(hiddenColumns);
        String expected = "[ null ,{bVisible: false}, {\"sSortDataType\" : \"dom-text\" , \"sType\" : \"string\"}, {'bSortable': false}]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AO_COLUMNS);
    }

    @Test
    /**
     * test that sortableColumns and hiddenColumns, when set on layoutManager, override those properties on the richTable
     * @throws Exception
     */
     public void testComponentOptionsHideColumnOnLayoutManager() {
        //set rich table properties
        Set<String> richTableHiddenColumns = new HashSet<String>();
        richTableHiddenColumns.add("employeeId");
        Set<String> sortableColumns = new HashSet<String>();
        sortableColumns.add("positionTitle");
        richTable.setSortableColumns(sortableColumns);
        richTable.setHiddenColumns(richTableHiddenColumns);
        //set layout manager properties
        Set<String> lmHiddenColumns = new HashSet<String>();
        lmHiddenColumns.add("contactEmail");
        Set<String> lmSortableColumns = new HashSet<String>();
        lmSortableColumns.add("employeeId");
        ((TableLayoutManager)group.getLayoutManager()).setSortableColumns(lmSortableColumns);
        ((TableLayoutManager)group.getLayoutManager()).setHiddenColumns(lmHiddenColumns);

        String expected = "[ null ,{\"sSortDataType\" : \"dom-text\" , \"sType\" : \"string\"}, {'bSortable': false}, {bVisible: false}]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AO_COLUMNS);
    }

    private void assertRichTableComponentOptions(String optionsOnGroup, String optionsOnRichTable, String optionKey) {
        richTable.getComponentOptions().put(optionKey, optionsOnGroup);
        richTable.performFinalize(new View(), new UifFormBase(), group);
        //assertNotNull(richTable.getComponentOptions().get(optionKey));
        assertEquals(optionsOnRichTable,richTable.getComponentOptions().get(optionKey));
    }
}
