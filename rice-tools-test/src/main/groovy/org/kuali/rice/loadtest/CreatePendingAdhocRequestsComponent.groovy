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
package org.kuali.rice.loadtest

import groovy.util.ConfigSlurper
import org.apache.commons.cli.ParseException
import java.util.UUID
import java.io.File

import org.kuali.rice.krad.devtools.maintainablexml.EncryptionService

def opt
def config

def user
Integer userCount
Integer userStartSuffix
def actionCode
Integer docCount
def startIndex
def docPrefix
def schemaName
def outfile

EncryptionService encryptService
def CODE
def doc_content_1

/**
 * Handle command line interface.
 * @param args
 */
def parseCommandLine(args){
    def cli = new CliBuilder(usage:'groovy org.kuali.rice.loadtest.CreatePendingAdhocRequestsComponent.groovy -n <number of users>  [options]')
    cli.h( longOpt: 'help', required: false, 'show usage information' )
    cli.n( longOpt: 'numberOfDocs', required: false, 'number of documents to be created', args:1)
    cli.s( longOpt: 'startIndex', required: false, 'starting index number', args:1)
    cli.u( longOpt: 'user', required: false, 'ad hoc recipient principal id', args:1)
    cli.un( longOpt: '# of users', required: false, 'number of users', args:1)
    cli.x( longOpt: 'user start suffix', required: false, 'starting user number suffix', args:1)
    cli.a( longOpt: 'actionCode', required: false, 'requested action code', args:1)
    cli.p( longOpt: 'prefix', required: false, 'prefix', args:1)
    cli.o( longOpt: 'outputfile', required: false, argName:"output file", args:1, 'SQL script output filename' )
    cli.db( longOpt: 'dbschema', required: false, argName:"database schema name", args:1, 'database name')
    cli.c( longOpt: 'config', required: false, argName:"config file", args:1, 'Location of groovy configuration file' )
    cli.v( longOpt: 'version', required: false, argName:"rice version", args:1, 'Rice Version Number (2.4)' )

    opt = cli.parse(args)
    if (!opt) {
        System.exit(-1)
        return
    }
    if (opt.h) {
        cli.usage()
        System.exit(0)
        return
    }

    docCount = opt.n.toInteger() ?: 1
    userCount = opt.un.toInteger() ?: 1
    userStartSuffix = opt.x ?: 0
    startIndex = opt.s ?: 1
    docPrefix = opt.p ?: "LT"
    user = opt.u ?: "fran"
    actionCode = opt.a ?: "A"
    schemaName = opt.db ?: "riceenv17"
    println "ARGS: " + opt.n + "," + opt.s + "," + opt.p + "," + opt.o + "," + opt.u + "," + opt.x + "," + opt.un

    outfile = new File(opt.o)
    if ( outfile.exists() ) {
        println "ERROR: Output file already exits. Aborting."
        System.exit(-1)
    }

//  printheader
    def newline = System.getProperty("line.separator")
    outfile << "-----------------" + newline
    outfile << "-- generated from CreatePendingAdhocRequestsComponent.groovy" + newline
    int endSuffix = Integer.parseInt(userStartSuffix) + userCount - 1
    outfile << "-- USERNAME: " + user + "${userStartSuffix}" + '-'+ "${endSuffix}"+ newline
    outfile << "-- DOCS create for EACH USER: " + opt.n + newline
    outfile << "-- SUMMARY: creates " + opt.n + "Component Documents. Each with a pending action request for user " + user + newline
    outfile << "-----------------" + newline
    outfile << newline
    outfile << newline
}

def getUuid() {
    return UUID.randomUUID() as String;
}

/** create sql insert statements for adding a new principal
 *
 * <p>Currently creates a new component document with pending approve action
 * request
 * @param counter - suffix (per doc) used to create unique identifiers
 * @param jcounter - suffix (per user) used to create unique identifiers
 *
 * @return
 */
def createSubmitComponentWithPendingApproval(int counter, int jcounter){
    int suffix = Integer.parseInt(userStartSuffix) + jcounter
    userid = user + "$suffix"
    println "user: " + userid

    Integer userIndex = startIndex.toInteger() + counter
    def newline = System.getProperty("line.separator")
    outfile << "-- Adding Component Doc " + docPrefix + userIndex + newline + newline

    def DOC_HDR_ID_1 = "DH_" + docPrefix + userIndex + "x" + suffix
    def RTE_NODE_ID_1 = "RN1_" + docPrefix + userIndex + "x" + suffix
    def RTE_NODE_ID_2 = "RN2_" + docPrefix + userIndex + "x" + suffix
    def MAINT_LOCK_1 = "ML_" + docPrefix + userIndex + "x" + suffix
    def ACTN_RQST_1 = "AR_" + docPrefix + userIndex + "x" + suffix
    def ACTN_ITM_1 = "AI_" + docPrefix + userIndex + "x" + suffix
    def ACTN_TKN_1 = "AT_" + docPrefix + userIndex + "x" + suffix
    def DESCRIPTION = "Blah "+ docPrefix + userIndex + "x" + suffix
    def OBJ_ID_1 = getUuid();
    def OBJ_ID_2 = getUuid();
    def OBJ_ID_3 = getUuid();
    def OBJ_ID_4 = getUuid();

    CODE = docPrefix + userIndex + "x" + suffix


/*
    def sql = "INSERT INTO `$schemaName`.`krew_doc_hdr_s` SET id='$DOC_HDR_ID_1'"
    outfile << sql + newline + ";" + newline
    
    sql = "INSERT INTO `$schemaName`.`krew_rte_node_s` SET id='$RTE_NODE_ID_1'"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_rte_node_s` SET id='$RTE_NODE_ID_1'"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_rte_node_s` SET id='$RTE_NODE_ID_2'"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_rte_node_s` SET id='$RTE_NODE_ID_1'"
    outfile << sql + newline + ";" + newline
*/

    sql = "INSERT INTO `$schemaName`.`krew_doc_hdr_t` SET DOC_HDR_ID='$DOC_HDR_ID_1', DOC_TYP_ID='3007', DOC_HDR_STAT_CD='I', RTE_LVL=000000000, STAT_MDFN_DT='2014-04-17 14:01:20', CRTE_DT='2014-04-17 14:01:20', DOC_VER_NBR=000000001, INITR_PRNCPL_ID='admin', VER_NBR=000000001, OBJ_ID='$OBJ_ID_1'"
    outfile << sql + newline + ";" + newline

    sql = "UPDATE `$schemaName`.`krew_doc_hdr_t`  SET VER_NBR=000000002 WHERE DOC_HDR_ID='$DOC_HDR_ID_1'"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_rte_brch_t` SET RTE_BRCH_ID='$RTE_NODE_ID_2', NM='PRIMARY', VER_NBR=000000001"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_rte_node_instn_t` SET RTE_NODE_INSTN_ID='$RTE_NODE_ID_1', DOC_HDR_ID='$DOC_HDR_ID_1', RTE_NODE_ID='2917', BRCH_ID='$RTE_NODE_ID_2', ACTV_IND=000000001, CMPLT_IND=000000000, INIT_IND=000000001, VER_NBR=000000001"
    outfile << sql + newline + ";" + newline

    sql = "UPDATE `$schemaName`.`krew_rte_brch_t`  SET  INIT_RTE_NODE_INSTN_ID='$RTE_NODE_ID_1' WHERE RTE_BRCH_ID='$RTE_NODE_ID_2'"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_doc_hdr_cntnt_t` SET DOC_HDR_ID='$DOC_HDR_ID_1', DOC_CNTNT_TXT='wNv24Qx7w+COB64lDK02+nNsjNd/+2LZ'"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_init_rte_node_instn_t` SET DOC_HDR_ID='$DOC_HDR_ID_1', RTE_NODE_INSTN_ID='$RTE_NODE_ID_1'"
    outfile << sql + newline + ";" + newline

/*
    sql = "INSERT INTO `$schemaName`.`krns_maint_lock_s` SET id=$MAINT_LOCK_1"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_actn_rqst_s` SET id=$ACTN_RQST_1"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_actn_tkn_s` SET id=$ACTN_TKN_1"
    outfile << sql + newline + ";" + newline
*/

    sql = "INSERT INTO `$schemaName`.`krns_doc_hdr_t` SET DOC_HDR_ID='$DOC_HDR_ID_1', OBJ_ID='$OBJ_ID_2', VER_NBR=000000001, FDOC_DESC='$DESCRIPTION'"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krns_maint_doc_t` SET DOC_HDR_ID='$DOC_HDR_ID_1', OBJ_ID='$OBJ_ID_3', VER_NBR=000000001, DOC_CNTNT='QyubAlGJNqixjeNwrY00/9f2We7ofrJSQW0cV45RaxAR43KyuBSRikXF3KMX7owDnI91yLmrylFhOMxGFszjsYmmClr4HjkENqRIzkfwvGY9leBqNxuGfUIS24vlYNYMgo4nSC3Dg1htJ0QBefE15I0sLyRpqurfYLFb767wnifAmmc6tTaYdsUirK6+yFeKy+ylKzSSxeTGnpKNL7jC8UPtedIn8OCnfcYWOM0fpTLzIc792QExYREvQLo93mOdztU1MawAvN3238AvKJ/2xizBUZbhMiCNWS6AJzuKXTlgzkYV+3OlN+h7FkZ/lSM3SJEf1xKfIugxsA+/JGugo1A6GT3uk9U+srZ29nv7KwRf8isjaaJAw2+ZkbwU0e3OSpKt9fuqGeMGFR8Dgh5Kou0V+/G10cG6l/a2v1KSVrPll6tHTstxIoFcbkr5oKANCefEhyyFyIIQS3lcVhOzhtCWhyDHEcSNFp2o0S6GZXGo2ziztkOEe5Uxc9XhyATtXoLSIIttYIhOoksqLWMeuroQ62tJlNOF+63zojtTcH1Kkq31+6oZ46yliRu1J5or6IqWaErJOyWRuven8UxQsCtVDV0poSYHhwX0GqJ0/LbvTW8WSaI6mgpbCk+7eMlcaV2OHV445bHf3nD3R8trKgEwjG1xVJ51FFuxl5PXxSfaSfOH0b832biCnU0N9fjyZHq6KwHPjF2Iyk8prFTmO6iUCiuayyqsOE0frvbsaZd78u8kDUZB4jakSM5H8LxmWPoW5vSo3w47C82AFP6QjMcvCbT4N44DvwWLzDet+1bixmZo4SCFUo0sLyRpqurfYLFb767wniePjQ3Ypx/BoBHjcrK4FJGKi+26OcxM9qirRw4pyH6z6IA55e/Q6aZjJSEYtp0NyYQ2VtgJLdm7dj4NeflFPWTX9okYR8dx5vh0dhK2xTb+1Xwxg/mcI26n8ZxfKsNLt8hiJB2nsUHfbrj7mrt12tWjW5JglDQKhRx5MCnm0ckKQQCmxbFXoQv1HZPz9SeaZRRux1gZXg0RKxnD6HRFr12JDwcqMPs+QvbCrkcSnBfuJb8jkw5ZCVmyXSTg5F4p6f0dtibFPfBFQ+pApHYiTXumrogywGzgkICWHxNOEd/IEuHqdmjN16hkVDEDbNPKejSsOk1JrBnId1kugCc7il05RgPwEC78gws0Iau4xQfnSG02TWhQd0jih1tYyFtBjTQbNHLbRNcScGBeH/K5mM89AiJxabQjc+QLLsTIMOrPqqT92yLUWjxGqYU2w+Aq/C7Wzlgua4QLvq3GWBQN7v1xNGNawCe8jYq3/Romck0UCCuktOe1ct0MsfLG9WTyHezAoOT9lBxK0tcfluls8dncbTZNaFB3SOJswiPB5P7tKnp1hnQQTjJhgWtONeb4wV8ySXrLK2Cf0LwB0WdE9qhoOFYj+sLROJH5CpuYmG/G/mZ93Ir1g0VrecquIh62s66f+QpDJ6ZGAxSIDWlsYpZCtQx0b0a/jRjFIqyuvshXisvspSs0ksXkxp6SjS+4wvFD7XnSJ/Dgp33GFjjNH6Uy8yHO/dkBMWG/7F/Uu4uLbCYW5o//YDpKXuKn5NWE6gVeIw9tZ5eQCNcwYM4we4b6jhTEf16UFn/DSy6tqnuNrYmHQOnDVxvbXmOWugM9A9cR43KyuBSRiskd09Wjzwiq5GL10ZwDtoFydqEidv6IrQ=='"
    outfile << sql + newline + ";" + newline

    sql = "UPDATE `$schemaName`.`krew_doc_hdr_t`  SET TTL='New ComponentBo - $DESCRIPTION ', VER_NBR=000000003 WHERE DOC_HDR_ID='$DOC_HDR_ID_1'"
    outfile << sql + newline + ";" + newline

    sql = "UPDATE `$schemaName`.`krew_doc_hdr_cntnt_t`  SET DOC_HDR_ID='$DOC_HDR_ID_1', DOC_CNTNT_TXT='wNv24Qx7w+COB64lDK02+pbzf2kF6b8Vof4euR54BksXQw4bDff0lXLm1sK7Jt0PgDnl79DppmPl0u1+qwmly6hXdjgxsjBzLInioiEmoqX3Tp4bPi0EdJy5jZV/wxh5k+bzsbkCHNqlNH5BZoPYNtsbZuynuPgvLCcH5WcYZyBQmP4pk5mZ+fcdF2nLvi34XmUaJQHImtc=' WHERE DOC_HDR_ID='$DOC_HDR_ID_1'"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_actn_rqst_t` SET ACTN_RQST_ID='$ACTN_RQST_1', ACTN_RQST_CD='$actionCode', DOC_HDR_ID='$DOC_HDR_ID_1', STAT_CD='I', RSP_ID='-1', PRNCPL_ID='$userid', RECIP_TYP_CD='U', PRIO_NBR=000000000, RTE_LVL_NBR=000000000, RTE_NODE_INSTN_ID='$RTE_NODE_ID_1', DOC_VER_NBR=000000001, CRTE_DT='2014-04-17 14:03:08', RSP_DESC_TXT='', FRC_ACTN=000000001, ACTN_RQST_ANNOTN_TXT='Ad Hoc Routed by admin', APPR_PLCY='F', CUR_IND=000000001, VER_NBR=000000000"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krew_actn_tkn_t` SET ACTN_TKN_ID='$ACTN_TKN_1', DOC_HDR_ID='$DOC_HDR_ID_1', PRNCPL_ID='admin', ACTN_CD='C', ACTN_DT='2014-04-17 14:03:09', DOC_VER_NBR=000000001, ANNOTN='', CUR_IND=000000001, VER_NBR=000000001"
    outfile << sql + newline + ";" + newline

    sql = "INSERT INTO `$schemaName`.`krns_maint_lock_t` SET MAINT_LOCK_REP_TXT='org.kuali.rice.coreservice.impl.component.ComponentBo!!namespaceCode^^KR-SAP::code^^${ writer -> writer << CODE }', OBJ_ID='$OBJ_ID_4', VER_NBR=000000001, DOC_HDR_ID='$DOC_HDR_ID_1', MAINT_LOCK_ID='$MAINT_LOCK_1'"
    outfile << sql + newline + ";" + newline

    clear_content = doc_content_1.toString()
//    println "=========== CLEAR: "+ clear_content
    crypt_content = encryptService.encrypt(clear_content)
    sql = "UPDATE `$schemaName`.`krns_maint_doc_t`  SET VER_NBR=000000002, DOC_CNTNT='$crypt_content' WHERE DOC_HDR_ID='$DOC_HDR_ID_1'"
    outfile << sql + newline + ";" + newline

    sql = "UPDATE `$schemaName`.`krew_doc_hdr_t`  SET DOC_HDR_STAT_CD='R', RTE_LVL=000000000, STAT_MDFN_DT='2014-04-17 14:03:09', CRTE_DT='2014-04-17 14:01:20', RTE_STAT_MDFN_DT='2014-04-17 14:03:09', VER_NBR=000000005, RTE_PRNCPL_ID='admin' WHERE DOC_HDR_ID='$DOC_HDR_ID_1'"
    outfile << sql + newline + ";" + newline

/*
    sql = "INSERT INTO `$schemaName`.`krew_actn_itm_s` SET id=$ACTN_ITM_1"
    outfile << sql + newline + ";" + newline
*/
    sql = "INSERT INTO `$schemaName`.`krew_actn_itm_t` SET ACTN_ITM_ID='$ACTN_ITM_1', PRNCPL_ID='$userid', ASND_DT='2014-04-17 14:03:14', RQST_CD='A', ACTN_RQST_ID='$ACTN_RQST_1', DOC_HDR_ID='$DOC_HDR_ID_1', DOC_HDR_TTL='New ComponentBo - $DESCRIPTION ', DOC_TYP_LBL='Component Maintenance Document', DOC_HDLR_URL='http://localhost:8080/kr-dev/kr/maintenance.do?methodToCall=docHandler', DOC_TYP_NM='ComponentMaintenanceDocument', RSP_ID='-1', VER_NBR=000000000"
    outfile << sql + newline + ";" + newline

    sql = "UPDATE `$schemaName`.`krew_rte_node_instn_t`  SET RTE_NODE_INSTN_ID='$RTE_NODE_ID_1', DOC_HDR_ID='$DOC_HDR_ID_1', RTE_NODE_ID='2917', BRCH_ID='$RTE_NODE_ID_2', ACTV_IND=000000001, CMPLT_IND=000000000, INIT_IND=000000000, VER_NBR=000000002 WHERE RTE_NODE_INSTN_ID='$RTE_NODE_ID_1'"
    outfile << sql + newline + ";" + newline

    sql = "UPDATE `$schemaName`.`krew_actn_rqst_t`  SET ACTN_RQST_ID='$ACTN_RQST_1', ACTN_RQST_CD='A', DOC_HDR_ID='$DOC_HDR_ID_1', STAT_CD='A', RSP_ID='-1', PRNCPL_ID='$userid', RECIP_TYP_CD='U', PRIO_NBR=000000000, RTE_LVL_NBR=000000000, RTE_NODE_INSTN_ID='$RTE_NODE_ID_1', DOC_VER_NBR=000000001, CRTE_DT='2014-04-17 14:03:08', RSP_DESC_TXT='', FRC_ACTN=000000001, ACTN_RQST_ANNOTN_TXT='Ad Hoc Routed by admin', APPR_PLCY='F', CUR_IND=000000001, VER_NBR=000000000 WHERE ACTN_RQST_ID='$ACTN_RQST_1'"
    outfile << sql + newline + ";" + newline + newline + newline
}

public String parseDocContent(docCntnt){
    println "DocContent Before: "+docCntnt
    String oldXml = docCntnt;
    if (encryptService.isEnabled()) {
        println "Decyrpting..."
        oldXml = encryptService.decrypt(docCntnt);
    }
    println "DocContent After:"+oldXml
    return oldXml
}

def init(){
    String key = "7IC64w6ksLU";
    encryptService = new EncryptionService(key);

//    config = LoadTestUtils.slurpConfig(opt.c,"loadtest.script.properties")

    doc_content_1 = "<maintainableDocumentContents maintainableImplClass=\"org.kuali.rice.kns.maintenance.KualiMaintainableImpl\"><oldMaintainableObject><org.kuali.rice.coreservice.impl.component.ComponentBo>\
  <active>true</active>\
  <__persistence__namespace__vh class=\"org.eclipse.persistence.indirection.ValueHolder\">\
    <isCoordinatedWithProperty>false</isCoordinatedWithProperty>\
    <isNewlyWeavedValueHolder>true</isNewlyWeavedValueHolder>\
  </__persistence__namespace__vh>\
  <newCollectionRecord>false</newCollectionRecord>\
</org.kuali.rice.coreservice.impl.component.ComponentBo><maintenanceAction>New</maintenanceAction>\
</oldMaintainableObject><newMaintainableObject><org.kuali.rice.coreservice.impl.component.ComponentBo>\
  <namespaceCode>KR-SAP</namespaceCode>\
  <code>${writer -> writer << CODE}</code>\
  <name>TestMe1 Name</name>\
  <active>true</active>\
  <__persistence__namespace__vh class=\"org.eclipse.persistence.indirection.ValueHolder\">\
    <isCoordinatedWithProperty>false</isCoordinatedWithProperty>\
    <isNewlyWeavedValueHolder>true</isNewlyWeavedValueHolder>\
  </__persistence__namespace__vh>\
  <newCollectionRecord>false</newCollectionRecord>\
</org.kuali.rice.coreservice.impl.component.ComponentBo><maintenanceAction>New</maintenanceAction>\
</newMaintainableObject></maintainableDocumentContents>"


}

def play() {
}

def runScript(args) {

    parseCommandLine(args)
    init()
    for (int j=0; j< userCount; j++){
        for (int i= 0; i< docCount; i++){
            createSubmitComponentWithPendingApproval(i,j)
        }
    }
    println "**************************************************"
    println "*                  Done                          *"
    println "**************************************************"
}

runScript(args)
