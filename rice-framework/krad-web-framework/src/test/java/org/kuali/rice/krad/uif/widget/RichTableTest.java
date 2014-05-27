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
package org.kuali.rice.krad.uif.widget;

import com.google.common.collect.Lists;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.datadictionary.validation.Employee;
import org.kuali.rice.krad.lookup.LookupView;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.CollectionGroupBase;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.GroupBase;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.DataFieldBase;
import org.kuali.rice.krad.uif.field.InputFieldBase;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.layout.TableLayoutManagerBase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.UifUnitTestUtils;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * test the RichTable widget
 */

public class RichTableTest {

    public static final String S_TYPE = "{\"bSortable\": false,\"sType\": \"numeric\",\"sSortDataType\": \"dom-text\",\"aTargets\": [0]}";
    public static final String S_SORT_DATA_TARGETS_1 = "{\"sType\": \"string\",\"sSortDataType\": \"dom-text\",\"aTargets\": [1]}";
    public static final String S_SORT_DATA_TARGETS_2 = S_SORT_DATA_TARGETS_1.replace("1", "2");
    public static final String S_SORT_DATA_TARGETS_3 = S_SORT_DATA_TARGETS_1.replace("1", "3");

    public static final String EXPECTED = S_TYPE + "," + S_SORT_DATA_TARGETS_1 + "," + S_SORT_DATA_TARGETS_2 + "," + S_SORT_DATA_TARGETS_3;

    public static final String B_VISIBLE_FALSE_TARGETS_1 = "{\"bVisible\": false,\"aTargets\": [1]}";
    public static final String B_SORTABLE_FALSE_TARGETS_3 = "{\"bSortable\": false,\"aTargets\": [3]}";

    private RichTable richTable;
    private CollectionGroup group;
    private LookupView mockView;

    @BeforeClass
    public static void setUpClass() throws Throwable {
        UifUnitTestUtils.establishMockConfig("KRAD-RichTableTest");
    }

    @AfterClass
    public static void tearDownClass() throws Throwable {
        GlobalResourceLoader.stop();
    }

    //private
    @Before
    public void setup() throws Throwable {
        richTable = new RichTable();

        richTable = spy(richTable);

        ConfigurationService configurationService = mock(ConfigurationService.class);
        doReturn(configurationService).when(richTable).getConfigurationService();

        group = new CollectionGroupBase();
        group.setCollectionObjectClass(Employee.class);

        TableLayoutManager layoutManager = new TableLayoutManagerBase();
        layoutManager.setRenderSequenceField(true);

        List<Component> items = new ArrayList<Component>(1);
        DataField name = new DataFieldBase();
        name.setPropertyName("employeeId");
        items.add(name);
        DataField number = new DataFieldBase();
        number.setPropertyName("positionTitle");
        items.add(number);
        DataField contactEmail = new DataFieldBase();
        contactEmail.setPropertyName("contactEmail");
        items.add(contactEmail);

        layoutManager = spy(layoutManager);
        doReturn(items).when(layoutManager).getFirstRowFields();
        doReturn(layoutManager).when(layoutManager).clone();

        group.setLayoutManager(layoutManager);
        group.setIncludeLineSelectionField(false);
        group.setRenderLineActions(false);

        group.setItems(items);

        mockView = mock(LookupView.class);
        ViewHelperService mockViewHelperService = mock(ViewHelperService.class);
        when(mockView.getViewHelperService()).thenReturn(mockViewHelperService);
        when(mockView.clone()).thenReturn(mockView);
    }

    @Test
    /**
     * test that without aoColumns being set explicitly, the default behaviour continues
     */
    public void testComponentOptionsDefault() throws Exception {
        assertRichTableComponentOptions(null, "[" + EXPECTED + "]", UifConstants.TableToolsKeys.AO_COLUMN_DEFS);
    }

    @Test
    /**
     * test that when aoColumns is explicitly set, it is integrated into the rich table rendering logic
     */
    public void testComponentOptionsAoColumnsJSOptions() throws Exception {
        String innerColValues = "{bVisible: false}, null, null";
        assertRichTableComponentOptions("[" + innerColValues + "]", "[" + EXPECTED + "," + innerColValues + "]",
                UifConstants.TableToolsKeys.AO_COLUMN_DEFS);
    }

    @Test
    /**
     * test whether a hidden column, when marked as sortable is still hidden
     */
    public void testComponentOptionsHideColumnOnRichTable() {
        Set<String> hiddenColumns = new HashSet<String>();
        hiddenColumns.add("employeeId");
        Set<String> sortableColumns = new HashSet<String>();
        sortableColumns.add("positionTitle");
        richTable.setSortableColumns(sortableColumns);
        richTable.setHiddenColumns(hiddenColumns);
        String expected = "[" + S_TYPE + "," +
                B_VISIBLE_FALSE_TARGETS_1 + "," +
                S_SORT_DATA_TARGETS_2 + "," +
                B_SORTABLE_FALSE_TARGETS_3 + "]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AO_COLUMN_DEFS);
    }

    @Test
    /**
     * test that sortableColumns and hiddenColumns, when set on layoutManager, will not override those properties on the richTable
     */
    public void testComponentOptionsHideColumnOnLayoutManager() {
        // set rich table properties
        Set<String> richTableHiddenColumns = new HashSet<String>();
        richTableHiddenColumns.add("employeeId");
        Set<String> sortableColumns = new HashSet<String>();
        sortableColumns.add("positionTitle");
        richTable.setSortableColumns(sortableColumns);
        richTable.setHiddenColumns(richTableHiddenColumns);
        // set layout manager properties
        Set<String> lmHiddenColumns = new HashSet<String>();
        lmHiddenColumns.add("contactEmail");
        Set<String> lmSortableColumns = new HashSet<String>();
        lmSortableColumns.add("employeeId");
        ((TableLayoutManager) group.getLayoutManager()).setSortableColumns(lmSortableColumns);
        ((TableLayoutManager) group.getLayoutManager()).setHiddenColumns(lmHiddenColumns);
        // Watch out for spaces
        String expected = "[" + EXPECTED.replace(S_SORT_DATA_TARGETS_1 + ",", B_VISIBLE_FALSE_TARGETS_1 + ",") + "]";
        expected = expected.replace(S_SORT_DATA_TARGETS_3, B_SORTABLE_FALSE_TARGETS_3);
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AO_COLUMN_DEFS);
    }

    @Test
    /**
     * test that the default sort options, when set on the view, sort in the correct order
     */
    public void testComponentOptionsDefaultSort() {
        when(mockView.isDefaultSortAscending()).thenReturn(false);
        when(mockView.getDefaultSortAttributeNames()).thenReturn(Lists.newArrayList("employeeId", "contactEmail"));

        String expected = "[[1,'desc'],[3,'desc']]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AASORTING);
    }

    @Test
    /**
     * test that the default sort options, when set on the view, sort in the correct order
     */
    public void testComponentOptionsDefaultSortReverse() {
        when(mockView.isDefaultSortAscending()).thenReturn(false);
        when(mockView.getDefaultSortAttributeNames()).thenReturn(Lists.newArrayList("contactEmail", "employeeId"));

        String expected = "[[3,'desc'],[1,'desc']]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AASORTING);
    }

    @Test
    /**
     * test that the default sort options, when set on the view, sort in the correct order and in the correct position
     * when no sequence column is added
     */
    public void testComponentOptionsDefaultSortWithoutSequence() {
        when(mockView.isDefaultSortAscending()).thenReturn(false);
        when(mockView.getDefaultSortAttributeNames()).thenReturn(Lists.newArrayList("employeeId", "contactEmail"));

        ((TableLayoutManager) group.getLayoutManager()).setRenderSequenceField(false);

        String expected = "[[0,'desc'],[2,'desc']]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AASORTING);
    }

    @Test
    /**
     * test that the default sort options, when set on the view, sort in the correct order and in the correct position
     * relative to the details column
     */
    public void testComponentOptionsDefaultSortWithDetails() {
        when(mockView.isDefaultSortAscending()).thenReturn(false);
        when(mockView.getDefaultSortAttributeNames()).thenReturn(Lists.newArrayList("employeeId", "contactEmail"));

        Group rowDetailsGroup = new GroupBase();
        rowDetailsGroup.setItems(Lists.newArrayList(new InputFieldBase()));
        ((TableLayoutManager) group.getLayoutManager()).setRowDetailsGroup(rowDetailsGroup);

        String expected = "[[2,'desc'],[4,'desc']]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AASORTING);
    }

    @Test
    /**
     * test that the default sort options, when set on the view, sort in the correct order and in the correct position
     * relative to the action column on the left
     */
    public void testComponentOptionsDefaultSortWithActionLeft() {
        when(mockView.isDefaultSortAscending()).thenReturn(false);
        when(mockView.getDefaultSortAttributeNames()).thenReturn(Lists.newArrayList("employeeId", "contactEmail"));

        group.setRenderLineActions(true);
        ((TableLayoutManager) group.getLayoutManager()).setActionColumnPlacement("LEFT");

        String expected = "[[2,'desc'],[4,'desc']]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AASORTING);
    }

    @Test
    /**
     * test that the default sort options, when set on the view, sort in the correct order and in the correct position
     * relative to the action column on the right
     */
    public void testComponentOptionsDefaultSortWithActionRight() {
        when(mockView.isDefaultSortAscending()).thenReturn(false);
        when(mockView.getDefaultSortAttributeNames()).thenReturn(Lists.newArrayList("employeeId", "contactEmail"));

        group.setRenderLineActions(true);
        ((TableLayoutManager) group.getLayoutManager()).setActionColumnPlacement("RIGHT");

        String expected = "[[1,'desc'],[3,'desc']]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AASORTING);
    }

    @Test
    /**
     * test that the default sort options, when set on the view, sort in the correct order and in the correct position
     * relative to the multiple columns on the left
     */
    public void testComponentOptionsDefaultSortWithMultipleColumns() {
        when(mockView.isDefaultSortAscending()).thenReturn(false);
        when(mockView.getDefaultSortAttributeNames()).thenReturn(Lists.newArrayList("employeeId", "contactEmail"));

        Group rowDetailsGroup = new GroupBase();
        rowDetailsGroup.setItems(Lists.newArrayList(new InputFieldBase()));
        ((TableLayoutManager) group.getLayoutManager()).setRowDetailsGroup(rowDetailsGroup);

        group.setRenderLineActions(true);
        ((TableLayoutManager) group.getLayoutManager()).setActionColumnPlacement("LEFT");

        String expected = "[[3,'desc'],[5,'desc']]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AASORTING);
    }

    @Test
    /**
     * test that the default sort options, when set on the view, sort in the correct order and in the correct position
     * relative to the action column in the middle
     */
    public void testComponentOptionsDefaultSortWithActionMiddle() {
        when(mockView.isDefaultSortAscending()).thenReturn(false);
        when(mockView.getDefaultSortAttributeNames()).thenReturn(Lists.newArrayList("employeeId", "contactEmail"));

        group.setRenderLineActions(true);
        ((TableLayoutManager) group.getLayoutManager()).setActionColumnPlacement("4");

        String expected = "[[1,'desc'],[4,'desc']]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AASORTING);
    }

    /**
     * a common method to test rich table options
     *
     * @param optionsOnGroup - a string in JSON format of the options set on the collection group
     * @param optionsOnRichTable - a string in JSON format of the options set on the rich table
     * @param optionKey - a string with the rich table option key being tested
     */
    private void assertRichTableComponentOptions(String optionsOnGroup, final String optionsOnRichTable,
            final String optionKey) {

        Map<String, String> templateOptions = richTable.getTemplateOptions();
        if (templateOptions == null) {
            templateOptions = new HashMap<String, String>();
        } else {
            templateOptions = new HashMap<String, String>(templateOptions);
        }

        templateOptions.put(optionKey, optionsOnGroup);
        richTable.setTemplateOptions(templateOptions);

        ViewLifecycle.encapsulateLifecycle(mockView, null, null, new Runnable() {
            @Override
            public void run() {
                RichTable mutableRichTable = richTable.<RichTable>copy();
                mutableRichTable.performFinalize(new UifFormBase(), (Group) CopyUtils.copy(group));
                assertEquals(optionsOnRichTable, mutableRichTable.getTemplateOptions().get(optionKey));
            }
        });
    }
}
