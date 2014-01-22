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
package org.kuali.rice.core.api.parameter

import org.junit.Test
import org.kuali.rice.core.test.CORETestCase
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator
import org.kuali.rice.coreservice.api.parameter.Parameter
import org.kuali.rice.krad.util.KRADConstants
import org.kuali.rice.kew.api.KewApiConstants
import org.kuali.rice.test.data.PerSuiteUnitTestData
import org.kuali.rice.test.data.UnitTestData
import org.kuali.rice.test.data.UnitTestSql

import static org.junit.Assert.assertNotNull
import org.kuali.rice.coreservice.framework.parameter.ParameterService
import org.apache.commons.lang.time.StopWatch

@PerSuiteUnitTestData(
    value = [
        @UnitTestData(
            sqlStatements = [
                @UnitTestSql("INSERT INTO KRCR_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_ID) VALUES('KR-CACHE-TST', '5685fbb89b054a24b6174ee3213b8f91', 1, 'Test Namespace', 'Y', 'RICE')"),
                @UnitTestSql("INSERT INTO KRCR_PARM_TYP_T(PARM_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('CACHE', '4b424baa4f51429ab43f6816c300230e', 1, 'Cache Test Type', 'Y')"),
                @UnitTestSql("INSERT INTO KRCR_CMPNT_T(NMSPC_CD, CMPNT_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('KR-CACHE-TST', 'CMPNT-FOR-CACHE-TEST', '015a6f78a85548ac84011646e3866b6c', 1, 'CACHE-TEST', 'Y')"),
                @UnitTestSql("INSERT INTO KRCR_PARM_T(NMSPC_CD, CMPNT_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, VAL, PARM_DESC_TXT, EVAL_OPRTR_CD, APPL_ID) VALUES('KR-CACHE-TST', 'CMPNT-FOR-CACHE-TEST', 'PARM-NM-FOR-CACHE-TEST', '41e65ffd34cc49a49fb2dea99266bfe2', 1, 'CACHE', 'Y', 'Parameter for cache test.', 'A', 'RICE')")
            ]
        )
    ]
)

class ParameterUpdateTest extends CORETestCase {

    public static final String KUALI_RICE_TEST_NAMESPACE = "KR-CACHE-TST";
    public static final String KUALI_RICE_TEST_COMPONENT_CD = "CMPNT-FOR-CACHE-TEST";
    public static final String KUALI_RICE_TEST_PARAMETER = "PARM-NM-FOR-CACHE-TEST";

    @Test
    void parameterCachingTest ( ){

        ParameterService parameterService = CoreFrameworkServiceLocator.getParameterService();
        Parameter parameter = parameterService.getParameter(KUALI_RICE_TEST_NAMESPACE,
                KUALI_RICE_TEST_COMPONENT_CD, KUALI_RICE_TEST_PARAMETER);


        String value = parameterService.getParameterValueAsString(KUALI_RICE_TEST_NAMESPACE,
                KUALI_RICE_TEST_COMPONENT_CD, KUALI_RICE_TEST_PARAMETER);
        assertNotNull("parameter should not be Null", parameter)

        //loop and get the same parameter to test caching
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        0.step(10000, 1) {
            parameter = parameterService.getParameter(KUALI_RICE_TEST_NAMESPACE,
                    KUALI_RICE_TEST_COMPONENT_CD, KUALI_RICE_TEST_PARAMETER);
            assertNotNull("parameter should not be Null", parameter)
        }
        stopWatch.stop()
        LOG.info("loop time: " + stopWatch.getTime() + "ms");
    }
}
