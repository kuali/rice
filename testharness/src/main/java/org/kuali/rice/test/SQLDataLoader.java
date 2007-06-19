package org.kuali.rice.test;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;


public class SQLDataLoader {
    
    private static final Logger LOG = Logger.getLogger(SQLDataLoader.class);

    private String fileLoc;
    private String seperatorChar;

    public SQLDataLoader(String fileLoc, String seperatorChar) {
        this.fileLoc = fileLoc;
        this.seperatorChar = seperatorChar;
    }

    public void runSql() throws Exception {
        String sqlStatementsContent = getContentsAsString(fileLoc);
        String[] sqlStatements = sqlStatementsContent.split(getSeperatorChar());
        Connection conn = ((DataSource) TestHarnessServiceLocator.getDataSource()).getConnection();
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

	public String getSeperatorChar() {
		if (this.seperatorChar == null) {
			return ";";
		}
		return seperatorChar;
	}

}