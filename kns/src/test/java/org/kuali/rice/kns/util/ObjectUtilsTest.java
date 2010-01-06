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
package org.kuali.rice.kns.util;

import org.junit.Test;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.test.KNSTestCase;

/**
 * ObjectUtilsTest
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ObjectUtilsTest extends KNSTestCase {
    @Test
    public void testObjectUtils_equalsByKey() throws Exception {
        Parameter parameterInDB = new Parameter();
        parameterInDB.setParameterNamespaceCode("KR-NS");
        parameterInDB.setParameterName("OBJ_UTIL_TEST");
        
        Parameter parameterNew = new Parameter();
        parameterNew.setParameterNamespaceCode("KR-NS");
        parameterInDB.setParameterName(null);
        
        boolean equalsResult = false;
        equalsResult = ObjectUtils.equalByKeys(parameterInDB, parameterNew);
        assertFalse(equalsResult);
    }
}
