package org.kuali.core.dbplatform;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.kuali.core.dao.KualiDBPlatformDao;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;

public class ConfigurableKualiDBPlatformFactory {
	private static final Logger LOG = Logger.getLogger( ConfigurableKualiDBPlatformFactory.class );
	
	public static final String DERBY_PLATFORM = "Derby";

	public static final String ORACLE_PLATFORM = "Oracle9i";

	public static final String MY_SQL_PLATFORM = "MySQL";
	
	private KualiDBPlatformDao platformDao;
	
	private DataSource dataSource;
	
	public KualiDBPlatformDao getPlatformDao() {
		if ( platformDao == null ) {
			String platformClassName = Core.getCurrentContextConfig().getProperty("kns.datasource.platform.class");
			String dbPlatform = Core.getCurrentContextConfig().getProperty(Config.OJB_PLATFORM);
			try {
				// use temp variable to avoid use of incompletely initialized platform Dao
				KualiDBPlatformDao dao = null;
				if ( platformClassName == null || platformClassName.equals( "" ) ) {
					if (dbPlatform.equals(ORACLE_PLATFORM)) {
						dao = new KualiDBPlatformOracle();
					} else if (dbPlatform.equals(MY_SQL_PLATFORM)) {
						dao = new KualiDBPlatformMySQL();
					} else if (dbPlatform.equals(DERBY_PLATFORM)) {
						dao = new KualiDBPlatformDerby();
					} else {
						throw new RuntimeException( "Platform DAO class name (kns.datasource.platform.class) was blank and db platform (" + dbPlatform + ") is unknown" );					
					}
					dao.setDataSource( dataSource );
					platformDao = dao;
				} else {
					Class clazz = Class.forName( platformClassName );				
					dao = (KualiDBPlatformDao)clazz.newInstance();
					dao.setDataSource( dataSource );
					platformDao = dao; 
				}
			} catch ( Exception ex ) {
				LOG.fatal( "unable to create DB platform DAO: " + platformClassName, ex );
				throw new RuntimeException( "unable to create DB platform DAO: " + platformClassName, ex );
			}
		}
		return platformDao;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	
}
