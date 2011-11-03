/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.ken.test;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * Adapted from Rice...we should remove this class and use the Rice one once we merge
 */
public class SQLDataLoader {
    private static final Logger LOG = Logger.getLogger(SQLDataLoader.class);
    public static final String SQL_LINE_COMMENT_PREFIX = "--"; 

    private String fileLoc;
    private String seperatorChar;
    private DataSource dataSource;

    public SQLDataLoader(String fileLoc, String seperatorChar, DataSource dataSource) {
        this.fileLoc = fileLoc;
        this.seperatorChar = seperatorChar;
        this.dataSource = dataSource;
    }

    public void runSql() throws Exception {
        String sqlStatementsContent = getContentsAsString(fileLoc);
        String[] sqlStatements = sqlStatementsContent.split(getSeperatorChar());
        Connection conn = dataSource.getConnection();
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
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        String data = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resourceLoader.getResource(fileLoc).getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                // discard comments...commented single line statements
                // will result in errors when executed because there are no
                // results
                if (!line.trim().startsWith(SQL_LINE_COMMENT_PREFIX)) {
                    data += line + " ";
                }
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

        public String getSeperatorChar() {
                if (this.seperatorChar == null) {
                        return ";";
                }
                return seperatorChar;
        }

}
