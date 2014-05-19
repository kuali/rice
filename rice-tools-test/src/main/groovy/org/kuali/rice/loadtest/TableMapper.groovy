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
import groovy.sql.Sql

/*
@GrabConfig(systemClassLoader=true)
@Grab(group='mysql', module='mysql-connector-java', version='5.1.24')
*/

def opt
def config

// db connection parameters
def username
def password
def url
String platform = ""
def driver
def schema

def sql
def tablename


def init() {
    // get database connection config properties
    username = "RICE"
    password = "RICE"
    url = "jdbc:mysql://localhost/ricedev24"
    driver = "com.mysql.jdbc.Driver"
    platform = "MySQL"
    schema = "ricedev24"

    newline = System.getProperty("line.separator")
}

/**
 * creates a SQL connection to the rice database
 */
public void connectToDb() {
    sql = Sql.newInstance(url, username, password, driver)
}

def getColumnNames(){
    columnNames = new ArrayList();
    sql("use "+schema);
    def columnRows = sql.eachRow("SHOW COLUMNS FROM "+tablename) { row ->
        columnNames.add(row.Field)
        println "COLUMN: " + row.Field
    }
}

def parseCommandLine(args){
    def cli = new CliBuilder(usage:'groovy BinLogConverter.groovyoovy -t <db table name> -o <output sql file> [options]')
    cli.h( longOpt: 'help', required: false, 'show usage information' )
    cli.t( longOpt: 'table', required: true, argName:"table name", args:1, 'table name' )
    cli.o( longOpt: 'outputfile', required: true, argName:"output file", args:1, 'script output filename' )
    cli.c( longOpt: 'config', required: false, argName:"config file", args:1, 'Location of groovy configuration file' )

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
    println ""
    tablename = opt.t
    println "Table Name: " + tablename

    outFile = new File(opt.o)
    if ( outFile.exists() ) {
        println "ERROR: Output file already exits. Aborting."
        System.exit(-1)
    }
}

def createOutput(){
    outFile.createNewFile()
    outFile << "# Column Names for table: "+ tablename + newline
    outFile << "["
    columnNames.eachWithIndex{ col, index ->
        outFile << "'@" + (index+1) + "':'" + col + "'"
        if (index+1 < columnNames.size()) {
            outFile << ","
        }
        outFile << newline
    }
    outFile << "]"
}

def runScript(args) {

    parseCommandLine(args)
    init()
    connectToDb()
    getColumnNames()
    createOutput()

    println "**************************************************"
    println "*                  Done                          *"
    println "**************************************************"
}

runScript(args)
