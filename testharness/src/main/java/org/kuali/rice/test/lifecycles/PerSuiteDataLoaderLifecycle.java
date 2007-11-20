/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.test.lifecycles;

import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.SQLDataLoader;
import org.kuali.rice.test.data.PerSuiteUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;
import org.kuali.rice.test.data.UnitTestSql;

/**
 * A lifecycle for loading SQL datasets based on the PerSuiteUnitTestData annotation. The individual SQL statements are
 * loaded first, followed by the statements inside the files (files are loaded sequentially in the order listed in the
 * annotation).
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PerSuiteDataLoaderLifecycle implements Lifecycle {
	private boolean started;

	private SQLDataLoader sqlDataLoader;
	private Class<? extends RiceTestCase> annotatedClass;

	public PerSuiteDataLoaderLifecycle(Class<? extends RiceTestCase> annotatedClass) {
		this.annotatedClass = annotatedClass;
	}

	public boolean isStarted() {
		return started;
	}

	public void start() throws Exception {
		if (annotatedClass.isAnnotationPresent(PerSuiteUnitTestData.class)) {
			UnitTestData data = annotatedClass.getAnnotation(PerSuiteUnitTestData.class).value();
			for (UnitTestSql statement : data.sqlStatements()) {
				sqlDataLoader = new SQLDataLoader(statement.value());
				sqlDataLoader.runSql();
			}
			for (UnitTestFile file : data.sqlFiles()) {
				sqlDataLoader = new SQLDataLoader(file.filename(), file.delimiter());
				sqlDataLoader.runSql();
			}
		}
		started = true;
	}

	public void stop() throws Exception {
		started = false;
	}

}
