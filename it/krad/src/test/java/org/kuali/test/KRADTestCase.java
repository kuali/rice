/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.test;

import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.api.lifecycle.Lifecycle;
import org.kuali.rice.core.impl.resourceloader.SpringResourceLoader;
import org.kuali.rice.core.impl.resourceloader.SpringResourceLoader;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.SQLDataLoader;
import org.kuali.rice.test.lifecycles.KEWXmlDataLoaderLifecycle;

import javax.xml.namespace.QName;
import java.util.List;


/**
 * Default test base for a full KNS enabled unit test.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK_CLEAR_DB)
public abstract class KRADTestCase extends BaselineTestCase {

	private static final String SQL_FILE = "classpath:org/kuali/rice/krad/test/DefaultSuiteTestData.sql";
	private static final String XML_FILE = "classpath:org/kuali/rice/krad/test/DefaultSuiteTestData.xml";
    private static final String KRAD_MODULE_NAME = "krad";

    public KRADTestCase() {
		super(KRAD_MODULE_NAME);
	}

    @Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> suiteLifecycles = super.getSuiteLifecycles();
		suiteLifecycles.add(new KEWXmlDataLoaderLifecycle(XML_FILE));
		return suiteLifecycles;
	}

	@Override
	protected void loadSuiteTestData() throws Exception {
		super.loadSuiteTestData();
        new SQLDataLoader(SQL_FILE, ";").runSql();
	}

	@Override
	protected Lifecycle getLoadApplicationLifecycle() {
        SpringResourceLoader springResourceLoader = new SpringResourceLoader(new QName("KRADTestResourceLoader"), "classpath:KradTestSpringBeans.xml", null);
    	springResourceLoader.setParentSpringResourceLoader(getTestHarnessSpringResourceLoader());
    	return springResourceLoader;
	}
}
