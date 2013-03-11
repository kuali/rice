/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.vc.kew;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.vc.test.WsdlCompareTestCase;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KewWsdlCompatibilityTest extends WsdlCompareTestCase {
    private static final Logger LOG = Logger.getLogger(KewWsdlCompatibilityTest.class);
    private static final String MODULE_NAME = "kew";

    public KewWsdlCompatibilityTest() {
        super(MODULE_NAME);
    }

    @Test
    public void compareKewApiWsdls() {
        File[] files = new File("../../" + getModuleName() + "/api/target/wsdl").listFiles();
        compareWsdlFiles(files);
    }


    @Test
    public void compareKewFrameworkWsdls() {
        File[] files = new File("../../" + getModuleName() + "/framework/target/wsdl").listFiles();
        compareWsdlFiles(files);
    }

    /**
     * WorkflowDocumentService.wsdl had an incompatible change in  2.2.0 that was corrected in 2.2.1
     * @return
     */
    @Override
    protected Map<String, List<MavenVersion>> getWsdlVersionBlacklists() {
        return Collections.singletonMap("WorkflowDocumentService.wsdl", Arrays.asList(new MavenVersion("2.2.0")));
    }
}
