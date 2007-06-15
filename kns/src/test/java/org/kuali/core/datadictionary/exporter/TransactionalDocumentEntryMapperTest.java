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
import org.kuali.core.datadictionary.TransactionalDocumentEntry;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

import edu.sampleu.travel.document.TravelDocument2;
import edu.sampleu.travel.document.rule.TravelDocumentRule;

/**
 * This class is used to test the TransactionalDocumentEntryMapper.
 * 
 * 
 */
@WithTestSpringContext
public class TransactionalDocumentEntryMapperTest extends KualiTestBase {
    private DataDictionaryBuilder builder;
    private DataDictionary dataDictionary;


    @Override
    public void setUp() throws Exception {
        super.setUp();

        builder = new DataDictionaryBuilder(KNSServiceLocator.getValidationCompletionUtils());
        builder.setKualiGroupService(KNSServiceLocator.getKualiGroupService());
        builder.setKualiConfigurationService(KNSServiceLocator.getKualiConfigurationService());

        builder.addUniqueEntries("classpath:/edu/sampleu/travel/datadictionary/TravelRequestDocument.xml", true);
        builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AttributeReferenceDummy.xml", true);
        builder.parseDocument("TravelRequest", true);

        dataDictionary = builder.getDataDictionary();
    }

    public final void testConstructor_nullEntry() {
        boolean failedAsExpected = false;

        try {
            new TransactionalDocumentEntryMapper().mapEntry(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testExportSimpleProperties_classname() {
        Map entryMap = mapTravelDocument2();

        String path = "documentClass";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(TravelDocument2.class.getName(), property);
    }


    @Test public final void testExportSimpleProperties_documentTypeName() {
        Map entryMap = mapTravelDocument2();

        String path = "documentTypeName";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("TravelRequest", property);
    }


    @Test public final void testExportSimpleProperties_documentTypeCode() {
        Map entryMap = mapTravelDocument2();

        String path = "documentTypeCode";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("TRD2", property);
    }

    @Test public final void testExportSimpleProperties_businessRulesClass() {
        Map entryMap = mapTravelDocument2();

        String path = "businessRulesClass";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(TravelDocumentRule.class.getName(), property);
    }

    @Test public final void testExportSimpleProperties_label() {
        Map entryMap = mapTravelDocument2();

        String path = "label";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("Travel Doc 2", property);
    }

    @Test public final void testExportSimpleProperties_shortLabel() {
        Map entryMap = mapTravelDocument2();

        String path = "shortLabel";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("TRDoc2", property);
    }

    @Test public final void testExportSimpleProperties_summary() {
        Map entryMap = mapTravelDocument2();

        String path = "summary";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("&nbsp;", property);
    }

    @Test public final void testExportSimpleProperties_description() {
        Map entryMap = mapTravelDocument2();

        String path = "description";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertTrue(property.equals("Doing it right"));
    }

    @Test public final void testExportSimpleProperties_allowsCopy() {
        Map entryMap = mapTravelDocument2();

        String path = "allowsCopy";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("false", property);
    }

    @Test public final void testExportSimpleProperties_allowsErrorCorrection() {
        Map entryMap = mapTravelDocument2();

        String path = "allowsErrorCorrection";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("false", property);
    }

    @Test public final void testExportSimpleProperties_documentAuthorizerClass() {
        Map entryMap = mapTravelDocument2();

        String path = "documentAuthorizerClass";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);

        assertEquals("edu.sampleu.travel.document.authorizer.TravelDocumentAuthorizer", property);
    }


    /* utility methods */
    private Map mapTravelDocument2() {
    	
        TransactionalDocumentEntry entry = (TransactionalDocumentEntry)dataDictionary.getDictionaryObjectEntry(TravelDocument2.class.getName());
        TransactionalDocumentEntryMapper mapper = new TransactionalDocumentEntryMapper();
        Map map = mapper.mapEntry(entry).getExportData();

        return map;
    }
}
