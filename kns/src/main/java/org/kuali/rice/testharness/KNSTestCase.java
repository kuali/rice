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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.rice.config.spring.ConfigFactoryBean;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.kuali.rice.test.lifecycles.SQLDataLoaderLifecycle;

import edu.iu.uis.eden.batch.KEWXmlDataLoaderLifecycle;

/**
 * Default test base for a full KNS enabled unit test.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KNSTestCase extends RiceTestCase {

	private String contextName = "/SampleRiceClient";
	private String relativeWebappRoot = "/src/test/webapp";
	private String sqlFilename = "classpath:DefaultTestData.sql";
	private String sqlDelimiter = ";";
	private String xmlFilename = "classpath:DefaultTestData.xml";
	private String testConfigFilename = "classpath:META-INF/sample-app-test-config.xml";
	
	@Override
	public List<Lifecycle> getPerTestLifecycles() {
		return new ArrayList<Lifecycle>();
	}

	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> lifeCycles = super.getPerTestLifecycles();
		lifeCycles.add(new Lifecycle() {

			boolean started = false;

			public boolean isStarted() {
				return this.started;
			}

			public void start() throws Exception {
				ConfigFactoryBean.CONFIG_OVERRIDE_LOCATION = getTestConfigFilename();
				new SQLDataLoaderLifecycle(getSqlFilename(), getSqlDelimiter()).start();
				new JettyServerLifecycle(getPort(), getContextName(), getRelativeWebappRoot()).start();
				new KEWXmlDataLoaderLifecycle(getXmlFilename()).start();

				this.started = true;
			}

			public void stop() throws Exception {
				this.started = false;
			}

		});
		return lifeCycles;
	}

	@Override
	protected List<String> getConfigLocations() {
		return Arrays.asList(new String[] { getTestConfigFilename() });
	}

	@Override
	protected String getDerbySQLFileLocation() {
		return "classpath:db/derby/kns.sql";
	}

	@Override
	protected String getModuleName() {
		return "kns";
	}
	
	protected String getTestConfigFilename() {
		return testConfigFilename;
	}

	protected void setTestConfigFilename(String testConfigFilename) {
		this.testConfigFilename = testConfigFilename;
	}

	protected String getContextName() {
		return contextName;
	}

	protected void setContextName(String contextName) {
		this.contextName = contextName;
	}

	protected int getPort() {
		return HtmlUnitUtil.getPort();
	}

	protected String getRelativeWebappRoot() {
		return relativeWebappRoot;
	}

	protected void setRelativeWebappRoot(String relativeWebappRoot) {
		this.relativeWebappRoot = relativeWebappRoot;
	}

	protected String getSqlFilename() {
		return sqlFilename;
	}

	protected void setSqlFilename(String sqlFilename) {
		this.sqlFilename = sqlFilename;
	}

	protected String getXmlFilename() {
		return xmlFilename;
	}

	protected void setXmlFilename(String xmlFilename) {
		this.xmlFilename = xmlFilename;
	}

	protected String getSqlDelimiter() {
		return sqlDelimiter;
	}

	protected void setSqlDelimiter(String sqlDelimiter) {
		this.sqlDelimiter = sqlDelimiter;
	}

}