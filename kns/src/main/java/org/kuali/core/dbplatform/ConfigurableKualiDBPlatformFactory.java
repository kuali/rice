package org.kuali.core.dbplatform;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ConfigurableKualiDBPlatformFactory {
	private static final Logger LOG = Logger.getLogger( ConfigurableKualiDBPlatformFactory.class );
	
	public static final String DERBY_PLATFORM = "Derby";

	public static final String ORACLE_PLATFORM = "Oracle9i";

	public static final String MY_SQL_PLATFORM = "MySQL";
    private String ojbPlatform;
    private String dataSourcePlatformClass;
	private DataSource dataSource;
    private KualiDBPlatform platform;
	
    public KualiDBPlatform getPlatform() {
	if (platform == null) {
			try {
				// use temp variable to avoid use of incompletely initialized platform Dao
		KualiDBPlatform tempPlatform = null;
		if (StringUtils.isBlank(dataSourcePlatformClass)) {
		    if (ORACLE_PLATFORM.equals(ojbPlatform)) {
			tempPlatform = new KualiDBPlatformOracle();
		    } else if (MY_SQL_PLATFORM.equals(ojbPlatform)) {
			tempPlatform = new KualiDBPlatformMySQL();
		    } else if (DERBY_PLATFORM.equals(ojbPlatform)) {
			tempPlatform = new KualiDBPlatformDerby();
					} else {
			throw new RuntimeException(
				"No dataSourcePlatformClass was provided and specified ojbPlatform is unknown: "
					+ ojbPlatform);
					}
				} else {
		    try {
			tempPlatform = (KualiDBPlatform) Class.forName(dataSourcePlatformClass).newInstance();
		    } catch (Exception e) {
			throw new RuntimeException(
				"Specified dataSourcePlatformClass was invalid - unable to load, instantiate, or cast to KualiDBPlatform: "
					+ dataSourcePlatformClass);
				}
			}
		if (dataSource != null) {
		    tempPlatform.setDataSource(dataSource);
		} else {
		    throw new RuntimeException("Specified dataSource was null");
		}
		platform = tempPlatform;
	    } catch (RuntimeException e) {
		LOG.fatal("Unable to configure KualiDBPlatform", e);
		throw e;
	}
	}
	return platform;
    }

	public DataSource getDataSource() {
		return dataSource;
	}

    public void setOjbPlatform(String ojbPlatform) {
	this.ojbPlatform = ojbPlatform;
	}
	
    public void setDataSourcePlatformClass(String dataSourcePlatformClass) {
	this.dataSourcePlatformClass = dataSourcePlatformClass;
    }
	
    public void setDataSource(DataSource dataSource) {
	this.dataSource = dataSource;
}
}
