/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.krad.data;

import org.junit.Test;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.TestDictionaryConfig;

import static org.junit.Assert.assertEquals;

/**
 * Created by nigupta on 5/8/2014.
 */
@TestDictionaryConfig( namespaceCode="KRAD",
        dataDictionaryFiles="classpath:org/kuali/rice/krad/test/datadictionary/TestDocumentEntry.xml" )
public class DataDictionaryTravelAccountDocumentEntryTest extends KRADTestCase {

    protected static final String MAIN_DATA_OBJECT_FOR_TESTING = "org.kuali.rice.krad.test.document.bo.Account";

    @Test
    public void verifyDocumentEntry() {
        DocumentEntry entry = dd.getDocumentEntry( "AccountMaintenanceDocument" );
        assertEquals( "The business rule class does not match.",
                "org.kuali.rice.krad.test.document.BusinessRuleImpl",
                entry.getBusinessRulesClass().getName() );
    }
}
