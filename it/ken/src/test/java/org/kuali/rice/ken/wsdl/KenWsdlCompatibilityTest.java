/*
 * Copyright 2006-2012 The Kuali Foundation
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

package org.kuali.rice.ken.wsdl;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.test.WsdlCompareTestCase;

import java.io.File;

public class KenWsdlCompatibilityTest extends WsdlCompareTestCase {
    private static final Logger LOG = Logger.getLogger(KenWsdlCompatibilityTest.class);
    private static final String MODULE_NAME = "ken";

    public KenWsdlCompatibilityTest() {
        super(MODULE_NAME);
    }

    @Test
    public void compareWsdls() {
        File[] files = new File(getModuleName() + "/api/target/wsdl").listFiles();
        compareWsdlFiles(files);
    }


}
