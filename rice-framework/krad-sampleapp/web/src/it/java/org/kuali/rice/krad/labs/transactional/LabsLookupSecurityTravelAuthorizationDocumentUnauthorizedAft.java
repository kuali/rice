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
package org.kuali.rice.krad.labs.transactional;

import org.junit.Test;

/**
 * Tests lookup security for an unauthorized user (admin).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupSecurityTravelAuthorizationDocumentUnauthorizedAft extends LabsLookupSecurityTravelAuthorizationDocumentBase {

    @Test
    public void testTransactionalLookupSecurityUnauthorizedBookmark() throws Exception {
        testTransactionalLookupSecurity();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityUnauthorizedNav() throws Exception {
        testTransactionalLookupSecurity();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddDataDictionaryConversionFieldUnauthorizedBookmark() throws Exception {
        testTransactionalLookupSecurityAddDataDictionaryConversionField();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddDataDictionaryConversionFieldUnauthorizedNav() throws Exception {
        testTransactionalLookupSecurityAddDataDictionaryConversionField();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddUifConversionFieldUnauthorizedBookmark() throws Exception {
        testTransactionalLookupSecurityAddUifConversionField();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddUifConversionFieldUnauthorizedNav() throws Exception {
        testTransactionalLookupSecurityAddUifConversionField();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddHiddenConversionFieldUnauthorizedBookmark() throws Exception {
        testTransactionalLookupSecurityAddHiddenConversionField();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddHiddenConversionFieldUnauthorizedNav() throws Exception {
        testTransactionalLookupSecurityAddHiddenConversionField();
        passed();
    }

}
