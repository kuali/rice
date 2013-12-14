/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.scripts

import groovy.util.logging.Log
import groovy.sql.Sql

/**
 * KimPermissionConverter.groovy
 *
 * A groovy class which can be used to update KNS to KRAD.
 * Examines a Rice database looking for KNS related KIM permissions and creates a SQL script file
 * which creates equivalent KRAD permissions.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class KimPermissionConverter {

    def config;

    // db connection parameters
    def username;
    def password;
    def url;
    def platform;
    def driver;

    // directory and path structure
    def outputFile;

    // sql processing vars
    def sql;
    def sequencePrefix = "KRADCONV";
    def sequenceRandom;
    def sequenceValue = 0;

    // db values
    String kradNamespaceCode = "KR-KRAD";
    String knsNamespaceCode = "KR-NS";

    String viewInquiryFieldPermTemplateName = "View Inquiry or Maintenance Document Field";
    String viewFieldPermTemplateName = "View Field";

    // canned select statements
    String selectPermissionTemplateByName = "select * from KRIM_PERM_TMPL_T where NMSPC_CD = ? and NM = ? and ACTV_IND = 'Y'";
    String selectPermissionsByTemplate = "select * from KRIM_PERM_T where PERM_TMPL_ID = ?";

    String selectInquiryViewFieldPerm = "select * from KRIM_PERM_TMPL_T where NMSPC_CD='KR-NS' and NM = 'View Inquiry or Maintenance Document Field' and ACTV_IND = 'Y'";

    String insertIntoPermissionTable = "INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, VER_NBR, ACTV_IND,) values ('";
    String insertIntoPermissionAttributesTable = "INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, VER_NBR) VALUES ('";
    String insertIntoRoleMembershipTable = "INSERT INTO KRIM_ROLE_PERM_T (ROLD_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, VER_NBR, ACTV_IND) VALUES ('";

    String selectPermTemplateAttributeDefinitionIds = "SELECT A.KIM_TYP_ID, C.KIM_ATTR_DEFN_ID FROM KRIM_PERM_TMPL_T AS A, KRIM_TYP_ATTR_T AS B, KRIM_ATTR_DEFN_T AS C" +
      " WHERE A.NMSPC_CD = ? AND A.NM= ? and A.ACTV_IND = 'Y' AND A.KIM_TYP_ID = B.KIM_TYP_ID AND B.KIM_ATTR_DEFN_ID = C.KIM_ATTR_DEFN_ID"

    String sqlStatementDelimiter = "/";

    public KimPermissionConverter(config) {
        init(config);
    }


    def init(config) {
        username = config.datasource.username;
        password = config.datasource.password;
        url = config.datasource.url;
        platform = config.datasource.platform;
        driver = config.datasource.driver.name;

        def dir = config.output.dir + config.output.path.db.sql;
        outputFile = new File(dir, 'KradKimPermissions.sql');
        sequenceRandom = System.currentTimeMillis().toString();
    }


    /**
     * searches database for KNS related KIM permissions in a Rice database
     * and generates a SQL script file for creating KRAD equivalents of those permissions found.
     */
    public void convertKimPermissions() {
        // Load Configurable Properties
        log.finer("converting KIM permissions");
        connectToDb();

        outputFile << "-- KRAD KIM Permissions";
        transformViewMaintenanceInquiryFieldPermissions();
//        generateSqlScriptFile()
    }

    /**
     * creates a SQL connextion to the rice database
     */
    public void connectToDb() {
        log.finer("connecting to database: " + url + " as user: " + username);
        sql = Sql.newInstance(url, username, password, driver)
    }

    /**
     * Transforms KNS permission used to override the hide field security attribute into the KRAD equivalent.
     * KNS version uses VIEW_MAINTENANCE_INQUIRY_FIELD permission template.
     * KRAD equivalent uses VIEW_FIELD permission template.
     *
     * Ouput:
     * - insert new permission into KRIM_PERM_T
     * - insert 3 new permissions attributes (viewId, fieldId, propertyName) into KRIM_PERM_ATTR_DATA_T
     * - insert role assigments into  KRIM_ROLE_MBR_T
     *
     * DB Info needed
     * - KIM_TYP_ID of Krad View Field Permission
     * - ATTR_DEFN_ID of 3 KimAttributes related to Krad View Field Permission
     */
    protected void transformViewMaintenanceInquiryFieldPermissions() {

        // get KRAD View Field permission template. we need the Id and KimType
        def permAttributeDefs = sql.rows(selectPermTemplateAttributeDefinitionIds,[kradNamespaceCode, viewFieldPermTemplateName]);
        println "PERM ATTRIBUTE DEFS: " + permAttributeDefs;

        // get existing kim permission template(s)."
        // should be only 1 but theoretically could be more.
        sql.eachRow(selectPermissionTemplateByName,[knsNamespaceCode, viewInquiryFieldPermTemplateName]){ tmpl ->
            def tmplId = tmpl.PERM_TMPL_ID;
            def permTmplKimType = tmpl.KIM_TYP_ID;
            println 'Template ID: ' + tmplId;

            // find the existing permissions based off this template
            def perms = sql.rows(selectPermissionsByTemplate, [tmplId]);
            perms.each{ perm ->
                println perm;
                def oldPermId = perm.PERM_ID;
                def newPermId = getNextSequence();
                println "New Sequence = " + newPermId

                // select related permission attributes
                def oldPermAttributes = sql.rows(selectPermissionAttributes, [oldPermId]);

                // create an open view permission
                def createPermString = insertIntoPermissionTable + newPermId + "', '" + newPermId + "', '" + tmplId
                 + "', 'KR-KRAD', 'View Field', 'Allow user to view hidden field', 'Y', 1);"
                outputFile << createPermString;

                // create the perm attribute data value for viewId
                newPermAttrId = getNextSequence();
/*
                def createAttr1String = insertIntoPermissionAttributesTable + newPermAttrId + "', '" + newPermAttrId
                + "', '"+ newPermId+ "', '"+ newPermId+ "', '"+ newPermId+ "', '"+ newPermId);
                + "'"
*/
                // get all of the roles assigned to the KNS permission

                // add role assignments for the new KRAD permission

            }
        }
    }


    /**
     * Creates a SQL script file contains statements to create KRAD KIM permissions
     *
     * @param path
     * @param filename
     */
    private void generateSqlScriptFile(path, filename) {
        try {
            def writer = new StringWriter();
            XmlUtil.serialize(rootBean, writer);
            def result = writer.toString();
            result = addBlankLinesBetweenMajorBeans(result);
            result = fixComments(result);
            result = modifyBeanSchema(result);
            ConversionUtils.buildFile(path, filename, result);
        } catch (FileNotFoundException ex) {
            log.info "unable to generate output for " + outputFile.name;
            errorText();
        }
    }

    private String getNextSequence(){
        sequencePrefix + sequenceRandom + sequenceValue++
    }

   /**
     * @deprecated
     */
    def errorText() {
        log.info("=====================\nFatal Error in Script\n=====================\n")
        System.exit(2)
    }

}