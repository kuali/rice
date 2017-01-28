/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.core.framework.persistence.jdbc.datasource;

import java.sql.SQLException;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * StandardXAPoolDataSource subclass that adds some convienance getters and setters and implements our Lifecycle interface.
 * 
 * @deprecated We will be removing this file from a future release in order to get rid of our dependencies on XAPool.  If you
 * desire to continue using JOTM and XAPool, please configure using org.enhyrdra.jdbc.standard.StandardXADataSource directly
 * instead of using this class.
 */
public class XAPoolDataSource extends StandardXAPoolDataSource implements InitializingBean, DisposableBean {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(XAPoolDataSource.class);

    private static final long serialVersionUID = -3698043954102287887L;
    public static final String DRIVER_CLASS_NAME = "driverClassName";
    public static final String URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String MAX_SIZE = "maxSize";
    public static final String MIN_SIZE = "minSize";
    public static final String MAX_WAIT = "maxWait";
    public static final String VALIDATION_QUERY = "validationQuery";

    private RiceXADataSource dataSource = new RiceXADataSource();
    private boolean started = false;

    public XAPoolDataSource() {
        setDataSource(this.dataSource);
        setPreparedStmtCacheSize(0);
        setCheckLevelObject(2);
    }

    public void afterPropertiesSet() throws Exception {
    }

    public void destroy() throws Exception {
        LOG.info("Destroying WorkflowManagedDatasource.");
        shutdown(true);
        this.started = false;
    }

    public boolean isStarted() {
        return this.started;
    }

    public String getDriverClassName() throws SQLException {
        return this.dataSource.getDriverName();
    }

    public long getMaxWait() {
        return super.getDeadLockMaxWait();
    }

    public String getUrl() {
        return this.dataSource.getUrl();
    }

    public String getUsername() {
        return this.dataSource.getUser();
    }

    public String getValidationQuery() {
        return super.getJdbcTestStmt();
    }

    public void setBeanName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public void setDriverClassName(String driverClassName) {
        try {
            this.dataSource.setDriverName(driverClassName);
        } catch (SQLException e) {
            throw new RiceRuntimeException("Problem setting the driver name to: " + driverClassName, e);
        }
    }

    public void setMaxWait(long maxWait) {
        super.setDeadLockMaxWait(maxWait);
    }

    public void setPassword(String password) {
        // passwrd needs to be set in both places or else there will be an error
        this.dataSource.setPassword(password);
        super.setPassword(password);
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.dataSource.setTransactionManager(transactionManager);
    }

    public void setUrl(String url) {
        this.dataSource.setUrl(url);
    }

    public void setUsername(String username) {
        // username needs to be set in both places or else there will be an error
        this.dataSource.setUser(username);
        super.setUser(username);
    }

    public void setValidationQuery(String validationQuery) {
        super.setJdbcTestStmt(validationQuery);
    }

    public void setPreparedStmtCacheSize(int preparedStatementCacheSize) {
    	this.dataSource.setPreparedStatementCacheSize(preparedStatementCacheSize);
    }

    public <T> T  unwrap(Class<T> iface) throws SQLException {
		Assert.notNull(iface, "Interface argument must not be null");
		if (!DataSource.class.equals(iface)) {
			throw new SQLException("DataSource of type [" + getClass().getName() +
					"] can only be unwrapped as [javax.sql.DataSource], not as [" + iface.getName());
		}
		return (T) dataSource;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return DataSource.class.equals(iface);
	}
}
