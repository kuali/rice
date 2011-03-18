/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.test;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A TestCase superclass to be used internally by Rice for tests that need to
 * load all of the Rice suite-level test data.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class RiceInternalSuiteDataTestCase extends RiceTestCase {

	/**
	 * Loads the suite test data from the shared DefaultSuiteTestData.sql
	 */
	@Override
	protected void loadSuiteTestData() throws Exception {
		new SQLDataLoader(getKNSDefaultSuiteTestData(), "/").runSql();
		
		//ClassPathResource cpr = new ClassPathResource(getKIMDataLoadOrderFile());
		DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
		//BufferedReader reader = new BufferedReader(new FileReader(cpr.getFile()));
		//BufferedReader reader = new BufferedReader(new FileReader(cpr.getFile()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(resourceLoader.getResource(getKIMDataLoadOrderFile()).getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (!StringUtils.isBlank(line)) {
				new SQLDataLoader(getKIMSqlFileBaseLocation() + "/" + line, "/").runSql();
			}
		}
	}
	
	protected String getKNSDefaultSuiteTestData() {
	    //return "file:" + getBaseDir() + "/../src/test/config/data/DefaultSuiteTestDataKNS.sql";
		return "classpath:/config/data/DefaultSuiteTestDataKNS.sql";
	}

	protected String getKIMDataLoadOrderFile() {
	    return "classpath:/config/data/KIMDataLoadOrder.txt";
	}

	protected String getKIMSqlFileBaseLocation() {
	    return "classpath:/config/data";
	}
}
