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

import java.util.UUID
import groovy.util.ConfigSlurper
import java.io.File
import org.apache.commons.cli.ParseException


/*
"INSERT INTO `ricedev24`.`krim_prncpl_id_s` SET id=10001"
"INSERT INTO `ricedev24`.`krim_entity_id_s` SET id=10001"
"INSERT INTO `ricedev24`.`krim_entity_afltn_id_s` SET id=10000"
"INSERT INTO `ricedev24`.`krim_entity_nm_id_s` SET id=10000"
*/

def opt

def ENTITY_ID
def OBJ_ID
def AFFILIATION_ID
def ENT_NAME_ID
def PRINCIPAL_ID
def USER_NAME


Integer userCount
def startIndex
def userPrefix
def outfile

/**
 * Handle command line interface.
 * @param args
 */
def parseCommandLine(args){
    def cli = new CliBuilder(usage:'groovy org.kuali.rice.loadtest.CreatePrincipals.groovy -n <number of users>  [options]')
    cli.h( longOpt: 'help', required: false, 'show usage information' )
    cli.n( longOpt: 'numberOfUsers', required: false, 'number of users', args:1)
    cli.s( longOpt: 'startIndex', required: false, 'starting index number', args:1)
    cli.p( longOpt: 'prefix', required: false, 'username prefix', args:1)
    cli.o( longOpt: 'outputfile', required: false, argName:"output file", args:1, 'SQL script output filename' )
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

    userCount = opt.n.toInteger() ?: 1
    startIndex = opt.s ?: 1
    userPrefix = opt.p ?: "LT"

    println "ARGS: " + opt.n + "," + opt.s + "," + opt.p + "," + opt.o

    outfile = new File(opt.o)
    if ( outfile.exists() ) {
        println "ERROR: Output file already exits. Aborting."
        System.exit(-1)
    }
}

def getUuid() {
    return UUID.randomUUID() as String;
}

/** create sql insert statements for adding a new principal
 *
 * <p>Currently creates a new principal with a minimal set of properties:
 *    - name: userPrefix + counter
 *    - first name: userPrefix + counter
 *    - last name: hardcoded to "Testuser"+++++++++++++++++
 *    - affiliation set to "Affiliate", Campus:"KO"
 * @param counter
 * @return
 */
def createPrincipalAndEntity(int counter){
    Integer userIndex = startIndex.toInteger() + counter
    def PRINCIPAL_ID = userPrefix + userIndex
    def ENTITY_ID = "E_" + userPrefix + userIndex
    def AFFILIATION_ID = "A_" + userPrefix + userIndex
    def ENT_NAME_ID = "EN_" + userPrefix + userIndex
    def USER_NAME = userPrefix + userIndex
    def newline = System.getProperty("line.separator")
    outfile << "-- Adding user " + userPrefix + userIndex + newline + newline

    def OBJ_ID = getUuid();
    def insert_entity = "INSERT INTO krim_entity_t SET ENTITY_ID='$ENTITY_ID', OBJ_ID='$OBJ_ID', VER_NBR=000000001, ACTV_IND='Y'"
    outfile << insert_entity + newline + ";" + newline

    OBJ_ID = getUuid();
    def insert_entity_priv = "INSERT INTO krim_entity_priv_pref_t SET ENTITY_ID='$ENTITY_ID', OBJ_ID='$OBJ_ID', VER_NBR=000000001, SUPPRESS_NM_IND='N', SUPPRESS_EMAIL_IND='N', SUPPRESS_ADDR_IND='N', SUPPRESS_PHONE_IND='N', SUPPRESS_PRSNL_IND='N'"
    outfile << insert_entity_priv + newline + ";" + newline

    OBJ_ID = getUuid();
    String insert_entity_type = "INSERT INTO krim_entity_ent_typ_t SET ENT_TYP_CD='PERSON', ENTITY_ID='$ENTITY_ID', OBJ_ID='$OBJ_ID', VER_NBR=000000001, ACTV_IND='Y'"
    outfile << insert_entity_type + newline + ";" + newline

    OBJ_ID = getUuid();
    String insert_entity_affiliation = "INSERT INTO krim_entity_afltn_t SET ENTITY_AFLTN_ID='$AFFILIATION_ID', OBJ_ID='$OBJ_ID', VER_NBR=000000001, ENTITY_ID='$ENTITY_ID', AFLTN_TYP_CD='AFLT', CAMPUS_CD='KO', DFLT_IND='Y', ACTV_IND='Y'"
    outfile << insert_entity_affiliation + newline + ";" + newline

    OBJ_ID = getUuid();
    String insert_entity_name = "INSERT INTO krim_entity_nm_t SET ENTITY_NM_ID='$ENT_NAME_ID', OBJ_ID='$OBJ_ID', VER_NBR=000000001, ENTITY_ID='$ENTITY_ID', NM_TYP_CD='OTH', FIRST_NM='$USER_NAME', LAST_NM='Testuser', DFLT_IND='Y', ACTV_IND='Y'"
    outfile<< insert_entity_name + newline + ";" + newline

    OBJ_ID = getUuid();
    String insert_principal = "INSERT INTO krim_prncpl_t SET PRNCPL_ID='$PRINCIPAL_ID', OBJ_ID='$OBJ_ID', VER_NBR=000000001, PRNCPL_NM='$USER_NAME', ENTITY_ID='$ENTITY_ID', ACTV_IND='Y'"
    outfile << insert_principal + newline + ";" + newline + newline
}

def runScript(args) {
    println "RUNNING w/ args:"+args
    parseCommandLine(args)

    for (int i= 0; i< userCount; i++){
        createPrincipalAndEntity(i)
    }
    println "**************************************************"
    println "*                  Done                          *"
    println "**************************************************"
}

runScript(args)
