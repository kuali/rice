/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.krad.util;

import org.junit.Test;
import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiIntegerPercentageFieldConversion;
import org.kuali.test.KNSTestCase;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the OjbKualiIntegerPercentageFieldConversionTest methods.
 */
public class OjbKualiIntegerPercentageFieldConversionTest extends KNSTestCase {
    @Test public void testJavaToSql_checkNull() {
        OjbKualiIntegerPercentageFieldConversion converter = new OjbKualiIntegerPercentageFieldConversion();

        Object object = converter.javaToSql(null);
        assertEquals("when null is passed converter shouldn't explode", object, null);
    }

    @Test public void testSqlToJava_checkNull() {
        OjbKualiIntegerPercentageFieldConversion converter = new OjbKualiIntegerPercentageFieldConversion();

        Object object = converter.sqlToJava(null);
        assertEquals("when null is passed converter shouldn't explode", object, null);
    }
}
