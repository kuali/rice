package org.kuali.rice.spring.config;

import javax.sql.DataSource;

import org.kuali.core.dao.jdbc.AbstractDBPlatformDaoJdbc;
import org.kuali.core.dbplatform.KualiDBPlatformDerby;
import org.kuali.core.dbplatform.KualiDBPlatformMySQL;
import org.kuali.core.dbplatform.KualiDBPlatformOracle;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.springframework.beans.factory.FactoryBean;

public class KNSDBPlatformFactoryBean implements FactoryBean {

	public static final String DERBY_PLATFORM = "Derby";

	public static final String ORACLE_PLATFORM = "Oracle9i";

	public static final String MY_SQL_PLATFORM = "MySQL";

	private boolean singleton = true;

	private AbstractDBPlatformDaoJdbc knsDBPlatformDao;

	private DataSource dataSource;

	public Object getObject() throws Exception {
		if (this.knsDBPlatformDao != null && ! singleton) {
			return this.knsDBPlatformDao;
		}
		String dbPlatform = Core.getCurrentContextConfig().getProperty(Config.OJB_PLATFORM);
		if (dbPlatform == null) {
			throw new ConfigurationException("No property " + Config.OJB_PLATFORM + " is not set.");
		}
		if (this.getDataSource() == null) {
			throw new ConfigurationException("No datasource set.");
		}
		if (dbPlatform.equals(DERBY_PLATFORM)) {
			this.knsDBPlatformDao = new KualiDBPlatformDerby();
			this.knsDBPlatformDao.setDataSource(this.getDataSource());
		} else if (dbPlatform.equals(ORACLE_PLATFORM)) {
			this.knsDBPlatformDao = new KualiDBPlatformOracle();
			this.knsDBPlatformDao.setDataSource(this.getDataSource());
		} else if (dbPlatform.equals(MY_SQL_PLATFORM)) {
			this.knsDBPlatformDao = new KualiDBPlatformMySQL();
			this.knsDBPlatformDao.setDataSource(this.getDataSource());
		}
		return this.knsDBPlatformDao;
	}

	public Class getObjectType() {
		if (this.knsDBPlatformDao == null) {
			return null;
		} else {
			return this.knsDBPlatformDao.getClass();
		}
	}

	public boolean isSingleton() {
		return singleton;
	}

	public AbstractDBPlatformDaoJdbc getKnsDBPlatformDao() {
		return knsDBPlatformDao;
	}

	public void setKnsDBPlatformDao(AbstractDBPlatformDaoJdbc knsDBPlatformDao) {
		this.knsDBPlatformDao = knsDBPlatformDao;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
}