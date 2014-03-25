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
 * Tests lookup security for an authorized user (dev1).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookupSecurityTravelAuthorizationDocumentAuthorizedAft extends LabsLookupSecurityTravelAuthorizationDocumentBase {

    @Override
    public String getUserName() {
        return "dev1";
    }

    @Test
    public void testTransactionalLookupSecurityAuthorizedBookmark() throws Exception {
        testTransactionalLookupSecurity();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAuthorizedNav() throws Exception {
        testTransactionalLookupSecurity();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddDataDictionaryConversionFieldAuthorizedBookmark() throws Exception {
        testTransactionalLookupSecurityAddDataDictionaryConversionField();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddDataDictionaryConversionFieldAuthorizedNav() throws Exception {
        testTransactionalLookupSecurityAddDataDictionaryConversionField();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddUifConversionFieldAuthorizedBookmark() throws Exception {
        testTransactionalLookupSecurityAddUifConversionField();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddUifConversionFieldAuthorizedNav() throws Exception {
        testTransactionalLookupSecurityAddUifConversionField();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddHiddenConversionFieldAuthorizedBookmark() throws Exception {
        testTransactionalLookupSecurityAddHiddenConversionField();
        passed();
    }

    @Test
    public void testTransactionalLookupSecurityAddHiddenConversionFieldAuthorizedNav() throws Exception {
        testTransactionalLookupSecurityAddHiddenConversionField();
        passed();
    }

}
