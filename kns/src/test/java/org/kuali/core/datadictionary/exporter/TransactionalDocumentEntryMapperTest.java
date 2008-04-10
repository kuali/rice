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
import org.kuali.rice.kns.test.document.AccountRequestDocument;
import org.kuali.rice.kns.test.document.AccountRequestDocumentAuthorizer;
import org.kuali.rice.kns.test.document.AccountRequestDocumentRule;
import org.kuali.test.KNSTestBase;
import org.kuali.test.KNSWithTestSpringContext;

/**
 * This class is used to test the TransactionalDocumentEntryMapper.
 * 
 * 
 */
@KNSWithTestSpringContext
public class TransactionalDocumentEntryMapperTest extends KNSTestBase {
    private DataDictionaryBuilder builder;
    private DataDictionary dataDictionary;


    @Override
    public void setUp() throws Exception {
        super.setUp();

        builder = new DataDictionaryBuilder(KNSServiceLocator.getValidationCompletionUtils());
        builder.setKualiGroupService(KNSServiceLocator.getKualiGroupService());
        builder.setKualiConfigurationService(KNSServiceLocator.getKualiConfigurationService());

        builder.addUniqueEntries("classpath:org/kuali/rice/kns/test/document/AccountRequestDocument.xml", true);
        builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AttributeReferenceDummy.xml", true);
        builder.parseDocument("Test1", true);

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
        Map entryMap = mapAccountRequest();

        String path = "documentClass";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(AccountRequestDocument.class.getName(), property);
    }


    @Test public final void testExportSimpleProperties_documentTypeName() {
        Map entryMap = mapAccountRequest();

        String path = "documentTypeName";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("AccountRequest", property);
    }


    @Test public final void testExportSimpleProperties_documentTypeCode() {
        Map entryMap = mapAccountRequest();

        String path = "documentTypeCode";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("ARQ", property);
    }

    @Test public final void testExportSimpleProperties_businessRulesClass() {
        Map entryMap = mapAccountRequest();

        String path = "businessRulesClass";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals(AccountRequestDocumentRule.class.getName(), property);
    }

    @Test public final void testExportSimpleProperties_label() {
        Map entryMap = mapAccountRequest();

        String path = "label";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("Account Request", property);
    }

    @Test public final void testExportSimpleProperties_shortLabel() {
        Map entryMap = mapAccountRequest();

        String path = "shortLabel";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("ARQDoc", property);
    }

    @Test public final void testExportSimpleProperties_summary() {
        Map entryMap = mapAccountRequest();

        String path = "summary";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("&nbsp;", property);
    }

    @Test public final void testExportSimpleProperties_description() {
        Map entryMap = mapAccountRequest();

        String path = "description";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertTrue(property.equals("Doing it right"));
    }

    @Test public final void testExportSimpleProperties_allowsCopy() {
        Map entryMap = mapAccountRequest();

        String path = "allowsCopy";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("false", property);
    }

    @Test public final void testExportSimpleProperties_allowsErrorCorrection() {
        Map entryMap = mapAccountRequest();

        String path = "allowsErrorCorrection";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("false", property);
    }

    @Test public final void testExportSimpleProperties_allowsNoteAttachments() {
        Map entryMap = mapAccountRequest();

        String path = "allowsNoteAttachments";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);
        assertEquals("true", property);
    }

    @Test public final void testExportSimpleProperties_documentAuthorizerClass() {
        Map entryMap = mapAccountRequest();

        String path = "documentAuthorizerClass";
        String property = (String) ExporterTestUtils.traverseMap(entryMap, path);

        assertEquals(AccountRequestDocumentAuthorizer.class.getName(), property);
    }


    /* utility methods */
    private Map mapAccountRequest() {
    	
        TransactionalDocumentEntry entry = (TransactionalDocumentEntry)dataDictionary.getDictionaryObjectEntry(AccountRequestDocument.class.getName());
        TransactionalDocumentEntryMapper mapper = new TransactionalDocumentEntryMapper();
        Map map = mapper.mapEntry(entry).getExportData();

        return map;
    }
}
