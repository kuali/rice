package org.kuali.rice.test;

import java.io.File;
import java.sql.DriverManager;

import org.apache.log4j.Logger;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;

public class DerbyDBCreationLifecycle implements Lifecycle {
	
	private static final Logger LOG = Logger.getLogger(DerbyDBCreationLifecycle.class);
	
	private String sqlFile;
	
	public DerbyDBCreationLifecycle(String sqlFile) {
		this.setSqlFile(sqlFile);
	}

	public boolean isStarted() {
		return false;
	}

	public void start() throws Exception {
		if (! isDoingDerby()) {
			LOG.info("Not using the Derby database for testing or no ddl file found");
			return;
		}
	
		//just checking that the driver's on the classpath and the url is valid
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        DriverManager.getConnection(Core.getCurrentContextConfig().getProperty("datasource.url")).close();
		
		String dbLocation = Core.getCurrentContextConfig().getProperty("db.location");
		File db = new File(dbLocation);
		if (! db.exists()) {
			throw new ConfigurationException("Can't find db file " + dbLocation);
		}
		
		if (isDerbyDBReadyForTests()) {
			LOG.info("Derby ready for testing");
			return;
		}
		
		LOG.info("Setting up Derby for testing");
		LOG.info("Derby connection string: " + Core.getCurrentContextConfig().getProperty("datasource.url"));
		SQLDataLoader dataLoader = new SQLDataLoader(this.getSqlFile(), ";");
		dataLoader.runSql();
	}

	public void stop() throws Exception {
		
	}
	
	private boolean isDerbyDBReadyForTests() {
		return new ClearDatabaseLifecycle().isTestTableInSchema(TestHarnessServiceLocator.getDataSource());
	}
	
	protected boolean isDoingDerby() {
		if (sqlFile == null) {
			return false;
		}
		String dbDriverName = Core.getCurrentContextConfig().getProperty("datasource.driver.name");
		if (dbDriverName == null) {
			throw new ConfigurationException("No property 'datasource.driver.name' found");
		}
		return dbDriverName.toLowerCase().indexOf("derby") > -1;
	}

	public String getSqlFile() {
		return sqlFile;
	}

	public void setSqlFile(String sqlFile) {
		this.sqlFile = sqlFile;
	}
}