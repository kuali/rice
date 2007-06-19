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
