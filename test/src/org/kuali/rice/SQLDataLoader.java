/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SQLDataLoader {
    
    private static final Logger LOG = Logger.getLogger(SQLDataLoader.class);

    private String fileLoc;

    public SQLDataLoader(String fileLoc) {
        this.fileLoc = fileLoc;
    }

    public void runSql() throws Exception {
        String sqlStatementsContent = getContentsAsString(fileLoc);
        String[] sqlStatements = sqlStatementsContent.split(";");
        Connection conn = ((DataSource) KNSServiceLocator.getService("dataSource")).getConnection();
        Statement statement = conn.createStatement();
        LOG.info("################################");
        LOG.info("#");
        LOG.info("#");
        for (String sqlStatement : sqlStatements) {
            if (StringUtils.isNotBlank(sqlStatement)) {
                LOG.info("# Executing sql statement ->" + sqlStatement + "<-");
                statement.execute(sqlStatement);    
            }
            
        }
        LOG.info("#");
        LOG.info("#");
        LOG.info("################################");
        try {
            statement.close();
        }
        catch (Exception e) {
            LOG.error(e);
        }
        try {
            conn.close();
        }
        catch (Exception e) {
            LOG.error(e);
        }

    }

    private String getContentsAsString(String fileLoc) throws Exception {
        String data = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileLoc)));
            String line = "";
            while ((line = reader.readLine()) != null) {
                data += line + " ";
            }
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception e) {
                    LOG.error(e);
                }
            }

        }
        return data;
    }

}