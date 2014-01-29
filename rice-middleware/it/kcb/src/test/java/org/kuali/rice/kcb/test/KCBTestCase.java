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
package org.kuali.rice.kcb.test;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.kcb.service.GlobalKCBServiceLocator;
import org.kuali.rice.kcb.service.KCBServiceLocator;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.runners.BootstrapTest;
import org.kuali.rice.test.runners.LoadTimeWeavableTestRunner;

/**
 * Base KCBTestCase
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK_CLEAR_DB)
@RunWith(LoadTimeWeavableTestRunner.class)
@BootstrapTest(KCBTestCase.BootstrapTest.class)
public abstract class KCBTestCase extends BaselineTestCase {
    protected KCBServiceLocator services;

    public KCBTestCase() {
        super("kcb");
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        services = GlobalKCBServiceLocator.getInstance();
    }

    @Override
    protected Lifecycle getLoadApplicationLifecycle() {
        SpringResourceLoader springResourceLoader = new SpringResourceLoader(new QName("KCBTestHarnessApplicationResourceLoader"), "classpath:KCBTestHarnessSpringBeans.xml", null);
        springResourceLoader.setParentSpringResourceLoader(getTestHarnessSpringResourceLoader());
        return springResourceLoader;
    }

    public static final class BootstrapTest extends KCBTestCase {
        @Test
        public void bootstrapTest() {};
    }

}
