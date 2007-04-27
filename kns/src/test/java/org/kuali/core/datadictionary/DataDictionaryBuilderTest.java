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

package org.kuali.core.datadictionary;

import org.apache.log4j.Level;
import org.kuali.core.datadictionary.exception.ParseException;
import org.kuali.core.datadictionary.exception.SourceException;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class is used to test the DataDictionaryBuilder.
 * 
 * 
 */
@WithTestSpringContext
public class DataDictionaryBuilderTest extends KualiTestBase {
    static final String PACKAGE_CORE_BO = "org/kuali/core/bo/datadictionary/";
    static final String PACKAGE_CORE_DOCUMENT = "org/kuali/core/document/datadictionary/";
    static final String PACKAGE_KFS = "org/kuali/kfs/datadictionary/";
    static final String PACKAGE_CHART = "org/kuali/module/chart/datadictionary/";
    static final String PACKAGE_CG = "org/kuali/module/cg/datadictionary/";
    static final String PACKAGE_KRA_BUDGET = "org/kuali/module/kra/budget/datadictionary/";
    static final String PACKAGE_KRA_ROUTINGFORM = "org/kuali/module/kra/routingform/datadictionary/";
    static final String TESTPACKAGE_INVALID = "org/kuali/core/datadictionary/test/invalid/";

    DataDictionaryBuilder builder = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        builder = new DataDictionaryBuilder(KNSServiceLocator.getValidationCompletionUtils());
        builder.setKualiGroupService(KNSServiceLocator.getKualiGroupService());
        builder.setKualiConfigurationService(KNSServiceLocator.getKualiConfigurationService());

        // quieten things down a bit
        setLogLevel("org.apache.commons.digester", Level.FATAL);
        setLogLevel("org.kuali.core.datadictionary.XmlErrorHandler", Level.FATAL);
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        builder = null;
    }

    public final void testDataDictionaryBuilder_source_invalid() {
        boolean failedAsExpected = false;

        try {
            builder.addUniqueEntries(null, true);
            builder.completeInitialization();
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testDataDictionaryBuilder_source_unknownFile() {
        String INPUT_FILE = TESTPACKAGE_INVALID + "foo.xml";

        boolean failedAsExpected = false;

        try {
            builder.addUniqueEntries(INPUT_FILE, true);
            builder.completeInitialization();
        }
        catch (SourceException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testDataDictionaryBuilder_source_unknownPackage() throws Exception {
        String UNKNOWN_PACKAGE = TESTPACKAGE_INVALID + "foo/";

        boolean failedAsExpected = false;

        try {
            builder.addUniqueEntries(UNKNOWN_PACKAGE, true);
            builder.completeInitialization();
        }
        catch (SourceException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

//    public final void testDataDictionaryBuilder_source_emptyPackage() throws Exception {
//        String EMPTY_PACKAGE = TESTPACKAGE_EMPTY;
//
//        builder.addUniqueEntries(EMPTY_PACKAGE, true);
//        builder.completeInitialization();
//    }

    public final void testDataDictionaryBuilder_source_knownPackage_invalidFiles() {
        String INPUT_PACKAGE = TESTPACKAGE_INVALID;

        boolean failedAsExpected = false;

        try {
            builder.addUniqueEntries(INPUT_PACKAGE, true);
            builder.completeInitialization();
        }
        catch (DataDictionaryException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

//    public final void testDataDictionaryBuilder_source_knownPackage_validFiles() {
//        String INPUT_PACKAGE = TESTPACKAGE_VALID_SIMPLE;
//
//        builder.addUniqueEntries(INPUT_PACKAGE, true);
//        builder.completeInitialization();
//    }

    public final void testDataDictionaryBuilder_invalidXml() {
        String INPUT_FILE = TESTPACKAGE_INVALID + "InvalidXml.xml";

        boolean failedAsExpected = false;

        try {
            builder.addUniqueEntries(INPUT_FILE, true);
            builder.completeInitialization();
        }
        catch (ParseException e) {
            if (DataDictionaryUtils.saxCaused(e)) {
                failedAsExpected = true;
            }
        }

        assertTrue(failedAsExpected);
    }

    public final void testDataDictionaryBuilder_getInvalidDictionary() {
        String INPUT_FILE = TESTPACKAGE_INVALID + "InvalidXml.xml";

        boolean failedAsExpected = false;

        try {
            builder.addUniqueEntries(INPUT_FILE, true);
            builder.completeInitialization();
        }
        catch (ParseException e) {
            // ignore the first exception, to cause the second one
        }

        try {
            builder.getDataDictionary();
        }
        catch (Throwable t) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testDataDictionaryBuilder_package_kuali() {
        builder.addUniqueEntries(PACKAGE_CORE_BO, true);
        builder.addUniqueEntries(PACKAGE_CORE_DOCUMENT, true);
        builder.addUniqueEntries(PACKAGE_KFS, true);
        builder.addUniqueEntries(PACKAGE_CHART, true);
        builder.addUniqueEntries(PACKAGE_CG, true);
        builder.addUniqueEntries(PACKAGE_KRA_BUDGET, true);
        builder.addUniqueEntries(PACKAGE_KRA_ROUTINGFORM, true);
        builder.completeInitialization();
    }

    public final void testDataDictionaryBuilder_multipleCompleteCalls() {
        builder.addUniqueEntries(PACKAGE_CORE_BO, true);
        builder.addUniqueEntries(PACKAGE_CORE_DOCUMENT, true);
        builder.addUniqueEntries(PACKAGE_KFS, true);
        builder.addUniqueEntries(PACKAGE_CHART, true);
        builder.addUniqueEntries(PACKAGE_CG, true);
        builder.addUniqueEntries(PACKAGE_KRA_BUDGET, true);
        builder.addUniqueEntries(PACKAGE_KRA_ROUTINGFORM, true);
        builder.completeInitialization();
        
//        builder.addOverrideEntries(DataDictionaryBuilderTest.TESTPACKAGE_VALID + "ValidBusinessObject.xml", true);
        builder.completeInitialization();
    }
}
