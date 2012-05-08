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

package org.kuali.rice.core.wsdl;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.test.WsdlCompareTestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CoreWsdlCompatibilityTest extends WsdlCompareTestCase {
    private static final Logger LOG = Logger.getLogger(CoreWsdlCompatibilityTest.class);
    private static final String MODULE_NAME = "core";

    public CoreWsdlCompatibilityTest() {
        super(MODULE_NAME);
    }

    @Test
    public void compareCoreServiceWsdls() {
        File[] files = new File(getModuleName() + "-service/api/target/wsdl").listFiles();
        compareWsdlFiles(files);
    }

    @Ignore
    @Test
    public void compareCoreWsdls() {
        File[] files = new File(getModuleName() + "/api/target/wsdl").listFiles();

        try {
            FileReader file = new FileReader(files[0].getAbsolutePath());
            BufferedReader bufferedReader = new BufferedReader(file);

            String line = bufferedReader.readLine();
            while (line != null) {
                System.out.println(line);
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        compareWsdlFiles(files);
    }


}
