/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.testharness;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Base module test case that allows overriding of the test harness spring beans
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseModuleTestCase extends RiceTestCase {
    protected final Logger LOG = Logger.getLogger(getClass());

    protected final String moduleName;

    /**
     * @param moduleName module name
     */
    public BaseModuleTestCase(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @see org.kuali.rice.testharness.RiceTestCase#getModuleName()
     */
    @Override
    protected String getModuleName() {
        return moduleName;
    }

// BELOW REMOVED DUE TO RiceTestCase.getTestHarnessSpringBeansLocation() implementation being a duplicate
//    /**
//     * Overrides to allow (enforce) per-module (MODULE)TestHarnessSpringBeans.xml 
//     * @see org.kuali.rice.testharness.RiceTestCase#getTestHarnessSpringBeansLocation()
//     */
//    @Override
//    protected String[] getTestHarnessSpringBeansLocation() {
//        String[] locations = super.getTestHarnessSpringBeansLocation();
//
//        String moduleTestHarnessSpringBeansPath = getModuleName().toUpperCase() + "TestHarnessSpringBeans.xml";
//        Resource resource = new ClassPathResource(moduleTestHarnessSpringBeansPath);
//        if (resource.exists()) {
//            locations = (String[]) ArrayUtils.add(locations, "classpath:" + moduleTestHarnessSpringBeansPath);
//        }
//        
//        return locations;
//    }
}