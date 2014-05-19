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
package org.kuali.rice.core.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.runners.BootstrapTest;
import org.kuali.rice.test.runners.LoadTimeWeavableTestRunner;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Default test base for a full KNS enabled unit test.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK_CLEAR_DB)
@RunWith(LoadTimeWeavableTestRunner.class)
@BootstrapTest(CORETestCase.BootstrapTest.class)
public abstract class CORETestCase extends BaselineTestCase {

    private static final String CORE_MODULE_NAME = "core";

    public CORETestCase() {
		super(CORE_MODULE_NAME);
	}

	@Override
	protected Lifecycle getLoadApplicationLifecycle() {
        SpringResourceLoader springResourceLoader = new SpringResourceLoader(new QName("CORETestResourceLoader"), "CORETestHarnessSpringBeans.xml", null);
    	springResourceLoader.setParentSpringResourceLoader(getTestHarnessSpringResourceLoader());
    	return springResourceLoader;
	}

    /**
     * Returns the List of tables that should be cleared on every test run.
     */
    protected List<String> getPerTestTablesToClear() {
        List<String> tablesToClear = new ArrayList<String>();
        tablesToClear.add("KREW_.*");
        tablesToClear.add("KRSB_.*");
        tablesToClear.add("KREN_.*");
        tablesToClear.add("KRMS_.*");
        return tablesToClear;
    }

    public static final class BootstrapTest extends CORETestCase  {
        @Test
        public void bootstrapTest() {};
    }

}
