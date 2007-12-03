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
package edu.iu.uis.eden.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;

import edu.iu.uis.eden.KEWServiceLocator;

public class SQLDataLoader {

    private static final Logger LOG = Logger.getLogger(SQLDataLoader.class);

    private String fileLoc;
    private Class fileLocationDeterminer;

    public SQLDataLoader(String fileLoc, Class fileLocationDeterminer) {
        this.fileLoc = fileLoc;
        this.fileLocationDeterminer = fileLocationDeterminer;
    }

    public void runSql() throws Exception {
        String sqlStatementsContent = getContentsAsString(fileLoc);
        final String[] sqlStatements = sqlStatementsContent.split(";");
        JdbcTemplate template = new JdbcTemplate(KEWServiceLocator.getDataSource());
        template.execute(new StatementCallback() {
            public Object doInStatement(Statement statement) throws SQLException, DataAccessException {
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
                return null;
            }
        });
    }

    private String getContentsAsString(String fileLoc) throws Exception {
        String data = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(fileLocationDeterminer.getResourceAsStream(fileLoc)));
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