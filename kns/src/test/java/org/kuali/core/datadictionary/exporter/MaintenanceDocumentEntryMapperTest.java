/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.util.Map;

import org.junit.Test;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DataDictionaryBuilder;
import org.kuali.core.datadictionary.MaintenanceDocumentEntry;
import org.kuali.core.datadictionary.bos.PaymentReasonCode;
import org.kuali.core.impls.RiceTestMaintenanceDocumentRule;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class is used to test the MaintenanceDocumentEntryExporter.
 * 
 * 
 */
@WithTestSpringContext
public class MaintenanceDocumentEntryMapperTest extends KualiTestBase {
    private DataDictionaryBuilder builder;
    private DataDictionary dataDictionary;


    @Override
    public void setUp() throws Exception {
        super.setUp();

        builder = new DataDictionaryBuilder(KNSServiceLocator.getValidationCompletionUtils());
        builder.setKualiGroupService(KNSServiceLocator.getKualiGroupService());
        builder.setKualiConfigurationService(KNSServiceLocator.getKualiConfigurationService());

        builder.addUniqueEntries("classpath:org/kuali/core/datadictionary/test/PaymentReasonCodeMaintenanceDocument.xml", true);
        dataDictionary = builder.getDataDictionary();
    }


    public final void testConstructor_nullEntry() {
        boolean failedAsExpected = false;

        try {
            new MaintenanceDocumentEntryMapper().mapEntry(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testExportSimpleProperties_businessObjectClass() {
        Map entryMap = mapAccountMaintenanceDocument();

        String path = "businessObjectClass";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(PaymentReasonCode.class.getName(), property);
    }

    @Test public final void testExportSimpleProperties_maintainableClass() {
        Map entryMap = mapAccountMaintenanceDocument();

        String path = "maintainableClass";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(org.kuali.core.maintenance.KualiMaintainableImpl.class.getName(), property);
    }

    @Test public final void testExportSimpleProperties_documentTypeName() {
        Map entryMap = mapAccountMaintenanceDocument();

        String path = "documentTypeName";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("KualiPaymentReasonCodeMaintenanceDocument", property);
    }

    @Test public final void testExportSimpleProperties_documentTypeCode() {
        Map entryMap = mapAccountMaintenanceDocument();

        String path = "documentTypeCode";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("DVPR", property);
    }

    @Test public final void testExportSimpleProperties_businessRulesClass() {
        Map entryMap = mapAccountMaintenanceDocument();

        String path = "businessRulesClass";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(RiceTestMaintenanceDocumentRule.class.getName(), property);
    }

    @Test public final void testExportSimpleProperties_label() {
        Map entryMap = mapAccountMaintenanceDocument();

        String path = "label";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("Payment Reason Code Maintenance Document", property);
    }

    @Test public final void testExportSimpleProperties_shortLabel() {
        Map entryMap = mapAccountMaintenanceDocument();

        String path = "shortLabel";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("PayReasonMaintDoc", property);
    }

    @Test public final void testExportSimpleProperties_summary() {
        Map entryMap = mapAccountMaintenanceDocument();

        String path = "summary";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("Payment Reason Code Maintenance Document", property);
    }

    @Test public final void testExportSimpleProperties_description() {
        Map entryMap = mapAccountMaintenanceDocument();

        String path = "description";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertTrue(property.equals("Document used to create or update Payment Reason objects"));
    }

    @Test public final void testExportSimpleProperties_documentAuthorizerClass() {
        Map entryMap = mapAccountMaintenanceDocument();

        String path = "documentAuthorizerClass";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);

        assertEquals("org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase", property);
    }


    @Test public final void testExportSection() {
        Map entryMap = mapAccountMaintenanceDocument();

        String titlePath = "maintainableSections.0.title";
        String title = (String) ExporterTestUtils.traverseMap(entryMap, titlePath);
        assertEquals("Edit Payment Reason", title);

        String itemsPath = "maintainableSections.0.maintainableItems";
        Map items = (Map) ExporterTestUtils.traverseMap(entryMap, itemsPath);
        assertEquals(5, items.size());

        Map item = (Map) items.get("code");
        assertEquals("code", item.get("name"));
        assertEquals("true", item.get("required"));
    }

    @Test public final void testExportSections() {
        Map entryMap = mapAccountMaintenanceDocument();

        String sectionsPath = "maintainableSections";
        Map sections = (Map) ExporterTestUtils.traverseMap(entryMap, sectionsPath);
        assertEquals(2, sections.size());

        // check section metadata
        String[][] expectedSectionsData = { 
        		{ "0.title", "Edit Payment Reason" }, 
        		{ "1.title", "Maintenance Section 2" } 
        };
        ExporterTestUtils.comparePropertyStrings(sections, expectedSectionsData);

        // check section 0 item data
        // ain't nothing but fields, so far
        String[][] expectedItemData0 = { 
        		{ "code.field", "true" }, 
        		{ "code.name", "code" }, 
        		{ "code.required", "true" }, 
        		{ "name.field", "true" }, 
        		{ "name.name", "name" }, 
        		{ "name.required", "false" }, 
        		{ "description.field", "true" }, 
        		{ "description.name", "description" }, 
        		{ "description.required", "false" }, 
        		{ "active.field", "true" }, 
        		{ "active.name", "active" }, 
        		{ "active.required", "false" }, 
        		{ "versionNumber.field", "true" }, 
        		{ "versionNumber.name", "versionNumber" }, 
        		{ "versionNumber.required", "false" }
        };

        Map items0 = (Map) ExporterTestUtils.traverseMap(sections, "0.maintainableItems");
        ExporterTestUtils.comparePropertyStrings(items0, expectedItemData0);
        
        String[][] expectedItemData1 = { 
        		{ "section2Field1.field", "true" }, 
        		{ "section2Field1.name", "section2Field1" }, 
        		{ "section2Field1.required", "true" }, 
        		{ "section2Field2.field", "true" }, 
        		{ "section2Field2.name", "section2Field2" }, 
        		{ "section2Field2.required", "false" }
        };
        
        Map items2 = (Map) ExporterTestUtils.traverseMap(sections, "1.maintainableItems");
        ExporterTestUtils.comparePropertyStrings(items2, expectedItemData1);

    }

    /* utility methods */
    private Map mapAccountMaintenanceDocument() {
        Class entryClass = PaymentReasonCode.class;

        MaintenanceDocumentEntry entry = dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(entryClass);
        MaintenanceDocumentEntryMapper mapper = new MaintenanceDocumentEntryMapper();
        Map map = mapper.mapEntry(entry).getExportData();

        return map;
    }
}
