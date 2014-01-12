/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.data.platform;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.incrementer.AbstractColumnMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link MaxValueIncrementerFactory}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class MaxValueIncrementerFactoryTest {

    @Mock private DataSource mysql;
    @Mock private DataSource oracle;
    @Mock private DataSource bad;

    @Before
    public void setUp() throws Exception {
        setUpMetaData(mysql, DatabasePlatforms.MYSQL, 5);
        setUpMetaData(oracle, DatabasePlatforms.ORACLE, 11);
        setUpMetaData(bad, "BAD!!!!!", 1);
    }

    private void setUpMetaData(DataSource dataSource, String platformName, int version) throws SQLException {
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(metaData.getDatabaseProductName()).thenReturn(platformName);
        when(metaData.getDatabaseMajorVersion()).thenReturn(version);
        Connection connection = mock(Connection.class);
        when(connection.getMetaData()).thenReturn(metaData);
        when(dataSource.getConnection()).thenReturn(connection);
    }


    @Test
    public void testGetIncrementer_Oracle() throws Exception {
        DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(oracle, "MY_SEQUENCE");
        assertTrue(incrementer instanceof OracleSequenceMaxValueIncrementer);
        OracleSequenceMaxValueIncrementer oracleIncrementer = (OracleSequenceMaxValueIncrementer)incrementer;
        assertEquals("MY_SEQUENCE", oracleIncrementer.getIncrementerName());

        // ensure that it's caching the incrementer
        assertSame(incrementer, MaxValueIncrementerFactory.getIncrementer(oracle, "MY_SEQUENCE"));
        // ensure that different sequence gives a different incrementer
        assertNotSame(incrementer, MaxValueIncrementerFactory.getIncrementer(oracle, "MY_SEQUENCE_2"));

    }

    @Test
    public void testGetIncrementer_MySQL() throws Exception {
        DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(mysql, "MY_SEQUENCE");
        assertTrue(incrementer instanceof MaxValueIncrementerFactory.EnhancedMySQLMaxValueIncrementer);
        MaxValueIncrementerFactory.EnhancedMySQLMaxValueIncrementer mysqlIncrementer =
                (MaxValueIncrementerFactory.EnhancedMySQLMaxValueIncrementer)incrementer;
        assertEquals("MY_SEQUENCE", mysqlIncrementer.getIncrementerName());
        assertEquals("ID", mysqlIncrementer.getColumnName());

        // ensure that it's caching the incrementer
        assertSame(incrementer, MaxValueIncrementerFactory.getIncrementer(mysql, "MY_SEQUENCE"));
        // ensure that different sequence gives a different incrementer
        assertNotSame(incrementer, MaxValueIncrementerFactory.getIncrementer(mysql, "MY_SEQUENCE_2"));

    }

    @Test(expected = UnsupportedDatabasePlatformException.class)
    public void testGetIncrementer_Bad() throws Exception {
        MaxValueIncrementerFactory.getIncrementer(bad, "MY_SEQUENCE");
    }

    @Test
    public void testGetIncrementer_CaseInsensitive() throws Exception {
        DataFieldMaxValueIncrementer incrementer1 = MaxValueIncrementerFactory.getIncrementer(mysql, "MY_SEQUENCE");
        DataFieldMaxValueIncrementer incrementer2 = MaxValueIncrementerFactory.getIncrementer(mysql, "MY_sequence");
        DataFieldMaxValueIncrementer incrementer3 = MaxValueIncrementerFactory.getIncrementer(mysql, "my_sequence");
        assertSame(incrementer1, incrementer2);
        assertSame(incrementer2, incrementer3);
    }

    @Test
    public void testGetIncrementer_CacheByDataSource() throws Exception {
        DataFieldMaxValueIncrementer incrementer1 = MaxValueIncrementerFactory.getIncrementer(mysql, "MY_SEQUENCE");
        DataFieldMaxValueIncrementer incrementer2 = MaxValueIncrementerFactory.getIncrementer(oracle, "MY_SEQUENCE");
        assertNotSame(incrementer1, incrementer2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncrementer_NullDataSource() throws Exception {
        MaxValueIncrementerFactory.getIncrementer(null, "MY_SEQUENCE");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncrementer_NullIncrementerName() throws Exception {
        MaxValueIncrementerFactory.getIncrementer(mysql, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncrementer_BlankIncrementerName() throws Exception {
        MaxValueIncrementerFactory.getIncrementer(mysql, "");
    }

    @Test
    public void testCustomIncrementerDatasourceVersion() throws Exception {
        SimpleConfig config = new SimpleConfig();
        config.putProperty("rice.krad.data.platform.incrementer.mysql.5",
                "org.kuali.rice.krad.data.platform.testincrementers.CustomIncrementerMySQLVersion5");
        config.putProperty("rice.krad.data.platform.incrementer.oracle.11",
                "org.kuali.rice.krad.data.platform.testincrementers.CustomIncrementerOracleVersion11");
        ConfigContext.init(config);

        DataFieldMaxValueIncrementer mySQLMaxVal = MaxValueIncrementerFactory.getIncrementer(mysql,"test_mySQL");
        assertTrue("Found MySQL custom incrementer",mySQLMaxVal != null);
        assertTrue("Custom incrementer for MySQL should be mySQL5 for String val",
                        StringUtils.equals(mySQLMaxVal.nextStringValue(),"mySQL5"));

        DataFieldMaxValueIncrementer oracleMaxVal = MaxValueIncrementerFactory.getIncrementer(oracle,"test_oracle");
        assertTrue("Found Oracle custom incrementer", oracleMaxVal != null);
    }

    @Test
    public void testCustomIncrementerDatasourceNoVersion() throws Exception {
        SimpleConfig config = new SimpleConfig();
        config.putProperty("rice.krad.data.platform.incrementer.mysql",
                "org.kuali.rice.krad.data.platform.testincrementers.CustomIncrementerMySQLVersion5");
        config.putProperty("rice.krad.data.platform.incrementer.oracle",
                "org.kuali.rice.krad.data.platform.testincrementers.CustomIncrementerOracleVersion11");
        ConfigContext.init(config);

        DataFieldMaxValueIncrementer mySQLMaxVal = MaxValueIncrementerFactory.getIncrementer(mysql,"test_mySQL");
        assertTrue("Found MySQL custom incrementer",mySQLMaxVal != null);
        assertTrue("Custom incrementer for MySQL should be mySQL5 for String val",
                StringUtils.equals(mySQLMaxVal.nextStringValue(),"mySQL5"));

        DataFieldMaxValueIncrementer oracleMaxVal = MaxValueIncrementerFactory.getIncrementer(oracle,"test_oracle");
        assertTrue("Found Oracle custom incrementer", oracleMaxVal != null);
    }

    @Test(expected = InstantiationError.class)
    public void testCustomIncrementerDatasourceInvalidClass() throws Exception {
        SimpleConfig config = new SimpleConfig();
        config.putProperty("rice.krad.data.platform.incrementer.mysql",
                "org.kuali.rice.krad.data.platform.testincrementers.NonExistent");
        ConfigContext.init(config);

        DataFieldMaxValueIncrementer mySQLMaxVal = MaxValueIncrementerFactory.getIncrementer(mysql,"test_mySQL");
        assertTrue("Cannot create incrementer", mySQLMaxVal == null);
    }

    @After
    public void clearContext(){
        ConfigContext.destroy();
    }
}
