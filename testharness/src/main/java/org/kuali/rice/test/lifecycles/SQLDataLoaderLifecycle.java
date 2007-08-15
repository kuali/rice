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
package org.kuali.rice.test.lifecycles;

import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.test.SQLDataLoader;

/**
 * A lifecycle for loading SQL datasets.
 * 
 * @author natjohns
 */
public class SQLDataLoaderLifecycle implements Lifecycle {
	private boolean started;

	private SQLDataLoader sqlDataLoader;

	private String filename;

	private String delimiter;

	public SQLDataLoaderLifecycle() {
		this("classpath:DefaultTestData.sql", ";");
	}

	public SQLDataLoaderLifecycle(String filename, String delimiter) {
		this.filename = filename;
		this.delimiter = delimiter;
	}

	public boolean isStarted() {
		return started;
	}

	public void start() throws Exception {
		if (new Boolean(Core.getCurrentContextConfig().getProperty("use.sqlDataLoaderLifecycle"))) {
			sqlDataLoader = new SQLDataLoader(filename, delimiter);
			sqlDataLoader.runSql();
			started = true;
		}
	}

	public void stop() throws Exception {
		// TODO: may way to do something with the dataLoader
		started = false;
	}
}
