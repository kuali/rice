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

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * ValidationCompletionUtilsTest
 * 
 * 
 */
@WithTestSpringContext
public class ValidationCompletionUtilsTest extends KualiTestBase {
    
    private ValidationCompletionUtils validationCompletionUtils;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        if(null == validationCompletionUtils) {
            validationCompletionUtils = KNSServiceLocator.getValidationCompletionUtils();
        }
    }

    @Test
    public void testIsBusinessObjectClass_nullClass() {
        boolean failedAsExpected = false;

        try {
            validationCompletionUtils.isBusinessObjectClass(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testIsBusinessObjectClass_nonBusinessObjectClass() {
        assertFalse(validationCompletionUtils.isBusinessObjectClass(java.lang.String.class));
    }

    // @Test
//    public void testIsBusinessObjectClass_businessObjectClass() {
//        assertTrue(validationCompletionUtils.isBusinessObjectClass(org.kuali.module.chart.bo.ProjectCode.class));
//    }

    @Test
    public void testIsMaintainableClass_nullClass() {
        boolean failedAsExpected = false;

        try {
            validationCompletionUtils.isMaintainableClass(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testIsMaintainableClass_nonMaintainableClass() {
        assertFalse(validationCompletionUtils.isMaintainableClass(java.lang.String.class));
    }

    @Test
    public void testIsDocumentClass_nullClass() {
        boolean failedAsExpected = false;

        try {
            validationCompletionUtils.isDocumentClass(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testIsDocumentClass_nonDocumentClass() {
        assertFalse(validationCompletionUtils.isDocumentClass(java.lang.String.class));
    }

    // @Test
//    public void testIsDocumentClass_documentClass() {
//        assertTrue(validationCompletionUtils.isDocumentClass(org.kuali.module.financial.document.InternalBillingDocument.class));
//    }


    @Test
    public void testIsDescendentClass_nullDescendentClass() {
        boolean failedAsExpected = false;

        try {
            validationCompletionUtils.isDescendentClass(null, java.lang.String.class);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testIsDescendentClass_nullAncestorClass() {
        boolean failedAsExpected = false;

        try {
            validationCompletionUtils.isDescendentClass(java.lang.String.class, null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testIsDescendentClass_nonDescendentClass() {
        assertFalse(validationCompletionUtils.isDescendentClass(java.lang.Object.class, java.lang.String.class));
    }

    @Test
    public void testIsDescendentClass_descendentClass() {
        assertTrue(validationCompletionUtils.isDescendentClass(java.util.List.class, java.util.Collection.class));
    }


    @Test
    public void testIsPropertyOf_nullClass() {
        boolean failedAsExpected = false;

        try {
            validationCompletionUtils.isPropertyOf(null, "foo");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testIsPropertyOf_blankPropertyName() {
        boolean failedAsExpected = false;

        try {
            validationCompletionUtils.isPropertyOf(java.lang.String.class, "");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testIsPropertyOf_unknownProperty() {
        assertFalse(validationCompletionUtils.isPropertyOf(java.lang.String.class, "foo"));
    }

    // @Test
//    public void testIsPropertyOf_knownProperty() {
//        assertTrue(validationCompletionUtils.isPropertyOf(org.kuali.module.chart.bo.ProjectCode.class, "name"));
//    }

    @Test
    public void testIsCollectionPropertyOf_nullClass() {
        boolean failedAsExpected = false;

        try {
            validationCompletionUtils.isCollectionPropertyOf(null, "foo");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testIsCollectionPropertyOf_blankPropertyName() {
        boolean failedAsExpected = false;

        try {
            validationCompletionUtils.isCollectionPropertyOf(java.lang.String.class, "");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test
    public void testIsCollectionPropertyOf_unknownProperty() {
        assertFalse(validationCompletionUtils.isCollectionPropertyOf(java.lang.String.class, "foo"));
    }

    // @Test
//    public void testIsCollectionPropertyOf_nonCollectionProperty() {
//        assertFalse(validationCompletionUtils.isCollectionPropertyOf(org.kuali.module.financial.document.InternalBillingDocument.class, "nextItemLineNumber"));
//    }
//
    // @Test
//    public void testIsCollectionPropertyOf_collectionProperty() {
//        assertTrue(validationCompletionUtils.isCollectionPropertyOf(org.kuali.module.financial.document.InternalBillingDocument.class, "items"));
//    }
}
