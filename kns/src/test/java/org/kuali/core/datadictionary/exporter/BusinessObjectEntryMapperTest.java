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
import org.kuali.KeyConstants.ObjectCode;
import org.kuali.core.bo.BusinessObjectAttributeEntry;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DataDictionaryBuilder;
import org.kuali.core.datadictionary.bos.PaymentReasonCode;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;


/**
 * This class is used to test the BusinessObjectEntryMapper.
 * 
 * 
 */
@WithTestSpringContext
public class BusinessObjectEntryMapperTest extends KualiTestBase {
    private DataDictionaryBuilder builder;
    private DataDictionary dataDictionary;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        if(null == builder) {
            builder = new DataDictionaryBuilder(KNSServiceLocator.getValidationCompletionUtils());
            
            builder.setKualiGroupService(KNSServiceLocator.getKualiGroupService());
            builder.setKualiConfigurationService(KNSServiceLocator.getKualiConfigurationService());
            
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AdHocRoutePerson.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AdHocRouteWorkgroup.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/ApplicationConstant.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/Attachment.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AttributeReferenceDummy.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/BusinessObjectAttributeEntry.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/BusinessRule.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/BusinessRuleSecurity.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/Campus.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/CampusType.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentGroup.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentHeader.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentStatus.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentType.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/EmployeeStatus.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/EmployeeType.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/FinancialSystemParameter.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/FinancialSystemParameterSecurity.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/Note.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/NoteType.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/UniversalUser.xml", true);
            
            builder.addUniqueEntries("classpath:org/kuali/core/datadictionary/test/PaymentReasonCode.xml", true);
            builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/BusinessObjectAttributeEntry.xml", true);
//            builder.addUniqueEntries("classpath:org/kuali/module/chart/datadictionary/", true);
//            builder.addUniqueEntries("classpath:org/kuali/module/financial/datadictionary/", true);
//            builder.addUniqueEntries("classpath:org/kuali/module/cg/datadictionary/", true);
//            builder.addUniqueEntries("classpath:org/kuali/module/kra/budget/datadictionary/",true);
//            builder.addUniqueEntries("classpath:org/kuali/module/kra/routingform/datadictionary/",true);
            
            dataDictionary = builder.getDataDictionary();
        }
    }
    
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        
        // free these up to prevent heap overflows
        builder = null;
        dataDictionary = null;
    }

    @Test public final void testConstructor_nullEntry() {
        boolean failedAsExpected = false;

        try {
            new BusinessObjectEntryMapper().mapEntry(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testExportInquiryProperties_title() {
        Map entryMap = map(PaymentReasonCode.class);

        String path = "inquiry.title";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("Payment Reason Code inquiry", property);
    }

    @Test public final void testExportInquiryProperties_inquiryField() {
        Map entryMap = map(PaymentReasonCode.class);

        String path = "inquiry.inquiryFields.code.attributeName";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("code", property);
    }

    @Test public final void testExportInquiryProperties_inquiryFields() {
        Map entryMap = map(PaymentReasonCode.class);

        // test retrieval, order and value for all fields
        String path = "inquiry.inquiryFields";
        String[][] expectedFieldValues = { { "code.attributeName", "code" }, { "name.attributeName", "name" }, { "description.attributeName", "description" }, { "active.attributeName", "active" } };

        Map fields = (Map) ExporterTestUtils.traverseMap(entryMap, path);
        ExporterTestUtils.comparePropertyStrings(fields, expectedFieldValues);
    }

    @Test public final void testExportLookupProperties_title() {
        Map entryMap = map(BusinessObjectAttributeEntry.class);

        String path = "lookup.title";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("Data Dictionary Attribute Lookup", property);
    }

    @Test public final void testExportLookupProperties_noMenubar() {
        Map entryMap = map(BusinessObjectAttributeEntry.class);

        String path = "lookup.menubar";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertNull(property);
    }

    @Test public final void testExportLookupProperties_menubar() {
        Map entryMap = map(PaymentReasonCode.class);

        String path = "lookup.menubar";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertTrue(property.equals("payment reason code menu bar"));
    }

    @Test public final void testExportLookupProperties_instructions() {
        Map entryMap = map(BusinessObjectAttributeEntry.class);

        String path = "lookup.instructions";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("Lookup attributes for a Business Object.", property);
    }


    @Test public final void testExportLookupProperties_defaultSort_sortAscending() {
        Map entryMap = map(BusinessObjectAttributeEntry.class);

        String path = "lookup.defaultSort.sortAscending";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("false", property);
    }

    @Test public final void testExportLookupProperties_defaultSort_field() {
        Map entryMap = map(BusinessObjectAttributeEntry.class);

        String path = "lookup.defaultSort.sortAttributes.attributeName.attributeName";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("attributeName", property);
    }

    @Test public final void testExportLookupProperties_defaultSort_fields() {
        // ValidBusinessObject.xml defines ObjectCode businessObjectEntry
        Map entryMap = map(PaymentReasonCode.class);

        // test retrieval, order, and value for all fields
        String path = "lookup.defaultSort.sortAttributes";
        String[][] expectedValues = { { "code.attributeName", "code" } };

        Map sortAttributes = (Map) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(1, sortAttributes.size());
        ExporterTestUtils.comparePropertyStrings(sortAttributes, expectedValues);
    }

    @Test public final void testExportLookupProperties_lookupField() {
        Map entryMap = map(PaymentReasonCode.class);

        String path = "lookup.lookupFields.code.attributeName";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(property, "code");
    }

    @Test public final void testExportLookupProperties_lookupFields() {
        // ValidBusinessObject.xml defines entry for ObjectCode.class
        Map entryMap = map(PaymentReasonCode.class);

        // test retrieval, order, and value for all fields
        String path = "lookup.lookupFields";
        String[][] expectedValues = { 
        		{ "code.required", "true" }, 
        		{ "name.attributeName", "name" }, 
        		{ "name.required", "false" }, 
        		{ "description.attributeName", "description" }, 
        		{ "description.required", "false" }, 
        		{ "active.attributeName", "active" }, 
        		{ "active.required", "false" }

        };

        Map lookupFields = (Map) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(4, lookupFields.size());
        ExporterTestUtils.comparePropertyStrings(lookupFields, expectedValues);
    }

    @Test public final void testExportLookupProperties_resultField() {
        Map entryMap = map(BusinessObjectAttributeEntry.class);

        String path = "lookup.resultFields.attributeMaxLength.attributeName";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(property, "attributeMaxLength");
    }

    @Test public final void testExportLookupProperties_resultFields() {
        Map entryMap = map(BusinessObjectAttributeEntry.class);

        // test retrieval, order, and value for all fields
        String path = "lookup.resultFields";
        String[][] expectedValues = { { "attributeName.attributeName", "attributeName" }, { "attributeLabel.attributeName", "attributeLabel" }, { "attributeSummary.attributeName", "attributeSummary" }, { "attributeMaxLength.attributeName", "attributeMaxLength", }, { "attributeValidatingExpression.attributeName", "attributeValidatingExpression" }, { "attributeControlType.attributeName", "attributeControlType" }, { "attributeFormatterClassName.attributeName", "attributeFormatterClassName" }, };


        Map resultFields = (Map) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(7, resultFields.size());
        ExporterTestUtils.comparePropertyStrings(resultFields, expectedValues);
    }


    @Test public final void testExportAttribute_simpleProperties() {
        Map entryMap = map(PaymentReasonCode.class);

        String path = "attributes.description";
        String[][] expectedValues = { { "forceUppercase", "false" }, { "label", "Payment Reason Description" }, { "shortLabel", "Description" }, { "maxLength", "2500" }, { "summary", "&nbsp;" }, { "*description", ".*meaningful.*codes.*" }, { "formatterClass", null }, };

        Map attribute = (Map) ExporterTestUtils.traverseMap(entryMap, path);
        ExporterTestUtils.comparePropertyStrings(attribute, expectedValues);
    }

    @Test public final void testExportAttributes_simpleProperties() {
        Map entryMap = map(PaymentReasonCode.class);

        String path = "attributes";
        String[][] expectedValues = { { "code.name", "code" }, { "code.maxLength", "1" }, { "code.forceUppercase", "true" }, { "name.name", "name" }, { "name.maxLength", "40" }, { "name.forceUppercase", "false" }, { "description.name", "description" }, { "description.maxLength", "2500" }, { "description.forceUppercase", "false" }, { "active.name", "active" }, { "active.maxLength", "1" }, { "active.forceUppercase", "false" }, { "versionNumber.name", "versionNumber" }, { "versionNumber.maxLength", "8" }, { "versionNumber.forceUppercase", "true" } };

        Map attributes = (Map) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(5, attributes.size());
        ExporterTestUtils.comparePropertyStrings(attributes, expectedValues);

        // testing additional attribute-properties
        Map anotherEntryMap = map(PaymentReasonCode.class);

        String[][] moreExpectedValues = {
        // default to false
                { "code.name", "code" }, 
                { "code.required", "true" },
                // explicitly set to false
                { "name.name", "name" }, 
                { "name.required", "false" } };

        ExporterTestUtils.comparePropertyStrings((Map) ExporterTestUtils.traverseMap(anotherEntryMap, path), moreExpectedValues);

    }


    @Test public final void testExportAttribute_control() {
        Map entryMap = map(PaymentReasonCode.class);

        // test several attributes of differing types
        String codePath = "attributes.code.control";
        String[][] codeExpectedValues = { { "text", "true" }, { "size", "1" } };
        ExporterTestUtils.comparePropertyStrings((Map) ExporterTestUtils.traverseMap(entryMap, codePath), codeExpectedValues);

        String namePath = "attributes.name.control";
        String[][] nameExpectedValues = { { "text", "true" }, { "size", "40" } };
        ExporterTestUtils.comparePropertyStrings((Map) ExporterTestUtils.traverseMap(entryMap, namePath), nameExpectedValues);

        String descrciptionPath = "attributes.description.control";
        String[][] descriptionExpectedValues = { { "textarea", "true" }, { "rows", "10" }, { "cols", "50" } };
        ExporterTestUtils.comparePropertyStrings((Map) ExporterTestUtils.traverseMap(entryMap, descrciptionPath), descriptionExpectedValues);

        String activePath = "attributes.active.control";
        String[][] activeExpectedValues = { { "checkbox", "true" } };
        ExporterTestUtils.comparePropertyStrings((Map) ExporterTestUtils.traverseMap(entryMap, activePath), activeExpectedValues);

        String versionNumberPath = "attributes.versionNumber.control";
        String[][] versionNumberExpectedValues = { { "hidden", "true" } };
        ExporterTestUtils.comparePropertyStrings((Map) ExporterTestUtils.traverseMap(entryMap, versionNumberPath), versionNumberExpectedValues);
    }


    /* utility methods */
    private Map map(Class entryClass) {
        Map map = null;

        BusinessObjectEntry entry = dataDictionary.getBusinessObjectEntry(entryClass.getName());
        BusinessObjectEntryMapper mapper = new BusinessObjectEntryMapper();
        map = mapper.mapEntry(entry).getExportData();

        return map;
    }
}
