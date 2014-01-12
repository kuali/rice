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
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import org.apache.commons.io.FilenameUtils
import groovy.util.logging.Log
/**
 * A utility class used by the conversion script to perform database related operations.
 * Reads the properties off the config object and establishes a db connection
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class DBConversionUtils{

    // project name
    def projectProps
    //db username   datasource.username config property
    def username
    //db password    datasource.password config property
    def password
    //db url       datasource.url config property
    def url
    //db driver     datasource.driver config property
    def driver

    def outputDir
    //path to the output directory for the sql files.  output.path.db.sql config property
    def outputPath

    /**
     * Initializes the DBConversionUtils object and loads the db drivers
     *
     * @param config - ConfigObject passed in by the conversion script
     */
    public DBConversionUtils(ConfigObject config) {
        init(config.project, config.datasource.username, config.datasource.password, config.datasource.url,
                config.datasource.driver.name, config.output.dir, config.output.path.db.sql)
        loadDrivers();
    }

    def init(projectProps_, username_, password_, url_, driver_, outputDir_, outputPath_) {
        projectProps = projectProps_
        username = username_
        password = password_
        url =  url_
        driver = driver_
        outputDir = FilenameUtils.normalize(outputDir_, true)
        outputPath = outputPath_
    }

    /**
     *  Loads the driver
     *
     */

    public def loadDrivers() {
        try {
            Class.forName(driver);
        } catch (Exception e) {
            println e
        }

    }

    /**
     *  Gets a db connection
     *
     * @return connection
     */
    public def getConnection() {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Executes a select sql
     *
     * @param sql - the sql to execute
     * @param parameterList - list of parameterValues
     * @return results - list of the resultSet values
     */
    public def executeQuery(sql,List parameterList) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List results = new ArrayList();
        try {
            statement = connection.prepareStatement(sql);
            parameterList.eachWithIndex() {obj, i -> statement.setObject(i+1, obj)  };
            resultSet = statement.executeQuery();

            ResultSetMetaData meta = resultSet.getMetaData();
            int colCount = meta.getColumnCount();

            while(resultSet.next()) {
                Map row = new HashMap();
                for(int i = 1 ; i <= colCount ; i++) {
                    row.put( meta.getColumnName(i), resultSet.getObject(i) );
                }

                results.add(row);
            }

            return results;

          } catch (SQLException e) {
            throw new RuntimeException("Error in executing the query.", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    log.info "Failed to close resultSet " + "\n" + e.message + "\n---\n";
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.info "Failed to close statement " + "\n" + e.message + "\n---\n";
                }
            }
        }
    }

    /**
     * Appends to an existing file or creates one if not present and then appends to it. The file is created in the output
     * path specified by the config property 'output.path.db.sql'
     *
     * @param fileName - name of the file to create/append
     * @param sql - text to be appended
     *
     */
    def appendToFile(fileName , sql){
        def outputDbDirPath = outputDir + outputPath
        try{
            File file =new File(outputDbDirPath + fileName);

            //if file doesnt exists, then create it
            if(!file.exists()){
                file.createNewFile();
            }

            //true = append file
            FileWriter fileWriter = new FileWriter(file,true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
            bufferWriter.write(sql);
            bufferWriter.close();

            log.info "Appended sql to file :" + fileName;
        }catch(IOException e){
            log.info "Failed to append sql to file " + fileName + "\n" + e.message + "\n---\n";
        }
    }
}