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

    // db values
    String kradNamespaceCode = "KR-KRAD";
    String knsNamespaceCode = "KR-NS";

    String viewInquiryFieldPermTemplateName = "View Inquiry or Maintenance Document Field";
    String viewFieldPermTemplateName = "View Field";

    // canned select statements
    String selectPermissionTemplateByName = "SELECT * FROM KRIM_PERM_TMPL_T WHERE NM = ? AND NMSPC_CD = ? AND ACTV_IND = 'Y'";
    String selectPermissionsByTemplate = "SELECT * FROM KRIM_PERM_T WHERE PERM_TMPL_ID = ?";
    String selectPermissionAttributes = "SELECT * FROM KRIM_PERM_ATTR_DATA_T WHERE PERM_ID = ?";
    String selectRoleMembersByPermId = "SELECT * FROM KRIM_ROLE_PERM_T WHERE PERM_ID = ?";
    String selectAttributeDefinitionIdByNameAndNamespace = "SELECT KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NM = ? AND NMSPC_CD = ? AND ACTV_IND = 'Y'";

    // insert statement help strings
    String insertIntoPermissionTable = "INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND, VER_NBR) VALUES ('";
    String insertIntoPermissionAttributesTable = "INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, VER_NBR) VALUES ('";
    String insertIntoRoleMembershipTable = "INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, ACTV_IND, VER_NBR) VALUES ('";

    String sqlStatementDelimiter = ";\n";

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
    }


    /**
     * searches database for KNS related KIM permissions in a Rice database
     * and generates a SQL script file for creating KRAD equivalents of those permissions found.
     */
    public void convertKimPermissions() {
        // Load Configurable Properties
        log.finer("converting KIM permissions");
        connectToDb();

        outputFile << "-- KRAD KIM Permissions\n";
        transformViewMaintenanceInquiryFieldPermissions();

        // just for debug to know it completed w/o runtime error, remove when completed
        outputFile << "-- Finished\n" ;
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
     * - insert new permissions attributes (viewId, fieldId, propertyName) into KRIM_PERM_ATTR_DATA_T
     * - insert role assigments into  KRIM_ROLE_MBR_T
     *
     * DB Info needed
     * - KIM_TYP_ID of Krad View Field Permission
     * - ATTR_DEFN_ID of 3 KimAttributes related to Krad View Field Permission
     */
    protected void transformViewMaintenanceInquiryFieldPermissions() {

        // get KRAD View Field permission template. we need the KimTypeId and AttrDefininitionIds
        // for creating the new permissions attributes to insert into KRIM_PERM_ATTR_DATA_T table
        def componentAttributeDefId = sql.firstRow(selectAttributeDefinitionIdByNameAndNamespace, ["componentName", knsNamespaceCode]).KIM_ATTR_DEFN_ID;
        def propertyNameAttributeDefId = sql.firstRow(selectAttributeDefinitionIdByNameAndNamespace, ["propertyName", knsNamespaceCode]).KIM_ATTR_DEFN_ID;
        def viewIdAttributeDefId = sql.firstRow(selectAttributeDefinitionIdByNameAndNamespace, ["viewId", kradNamespaceCode]).KIM_ATTR_DEFN_ID;
        def viewFieldTemplate = sql.firstRow(selectPermissionTemplateByName, [viewFieldPermTemplateName, kradNamespaceCode] );

        println "Component Name Attribute Definition Id: " + componentAttributeDefId;

        outputFile << "-- Insert new View Field permission\n";

        // get existing kim permission template(s)."
        // should be only 1 but theoretically could be more.
        sql.eachRow(selectPermissionTemplateByName,[viewInquiryFieldPermTemplateName, knsNamespaceCode]){ tmpl ->
            def tmplId = tmpl.PERM_TMPL_ID;
            println 'Template ID: ' + tmplId;

            // find the existing permissions based off this template
            def perms = sql.rows(selectPermissionsByTemplate, [tmplId]);
            perms.each{ perm ->
                println perm;
                def oldPermId = perm.PERM_ID;
                def newPermId = sequencePrefix + oldPermId;
                println "New Sequence = " + newPermId;

                // create an open view permission
                def createPermString = insertIntoPermissionTable + newPermId + "', '" + newPermId + "', '" + viewFieldTemplate.PERM_TMPL_ID +
                        "', '" + kradNamespaceCode + "', '" + perm.NM + "', '" +  perm.DESC_TXT + "', 'Y', 1)";
                outputFile << createPermString + "\n";
                outputFile << sqlStatementDelimiter + "\n";

                // select existing related permission attributes from KNS permission
                def oldPermAttributes = sql.rows(selectPermissionAttributes, [oldPermId]);
                println "OldAttributes" + oldPermAttributes;

                // create the perm attributes for the new krad  permission
                // viewId, fieldId, and propertyName
                outputFile << "-- Insert new View Field permission attributes\n";
                oldPermAttributes.eachWithIndex{ oldAttr, index ->
                    def newPermAttrId = sequencePrefix + oldAttr.ATTR_DATA_ID;
                    def attrDefnId="";
                    def attrValue="";
                    println "New Sequence = " + newPermAttrId;
                    if (oldAttr.KIM_ATTR_DEFN_ID == componentAttributeDefId){
                        attrDefnId = viewIdAttributeDefId;
                        attrValue = oldAttr.ATTR_VAL + '*';
                    } else if (oldAttr.KIM_ATTR_DEFN_ID == propertyNameAttributeDefId){
                        attrDefnId = propertyNameAttributeDefId;
                        attrValue = oldAttr.ATTR_VAL;
                    }

                    def createAttrString = insertIntoPermissionAttributesTable + newPermAttrId + "', '" + newPermAttrId +
                            "', '" + newPermId + "', '" + viewFieldTemplate.KIM_TYP_ID + "', '" +  attrDefnId + "', '" + attrValue + "', 1)";
                    outputFile << createAttrString + "\n";
                    outputFile << sqlStatementDelimiter;
                }

                // get all of the roles assigned to the KNS permission
                sql.eachRow(selectRoleMembersByPermId, [oldPermId]){ roleMember ->
                    // add role assignments for the new KRAD permission
                    def newRolePermId =  sequencePrefix + roleMember.ROLE_PERM_ID;
                    def createRolePermString = insertIntoRoleMembershipTable + newRolePermId +
                            "', '" + newRolePermId + "', '" + roleMember.ROLE_ID + "', '" + newPermId + "', 'Y', 1)";

                    outputFile << "-- Insert New Role Membership" + "\n"
                    outputFile << createRolePermString + "\n";
                    outputFile << sqlStatementDelimiter;
                }

                println "Done with creating new permissions.";
            }
        }
    }
}