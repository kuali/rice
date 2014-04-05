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
package org.kuali.rice.testtools.loadtest

import groovy.util.ConfigSlurper

def opt
def config

File inputBinLog
def statements
def outFile

def defaultConfigFilePath
def newline

def init() {
    defaultConfigFilePath = "binlog.conversion.properties"
    def defaultConfigFile = new File(BinLogConverter.getClassLoader().getResource(defaultConfigFilePath).file)
    config = new ConfigSlurper().parse(defaultConfigFile.toURL());

    if (opt.c){
        File configFile = new File(opt.c)
        println "Loading configuration file: $configFile.canonicalPath"
        if ( !configFile.exists() ) {
            println "ERROR: Configuration file does not exist - aborting."
            return
        }
        def altConfig = new ConfigSlurper().parse(configFile.text);
        config.merge(altConfig);
    }

    newline = System.getProperty("line.separator")
}

/**
 * We only want the lines that start with ###
 * Separate the good stuff with delimiter line
 */
def stripUninterestingLines(){
    boolean consecutiveTrash = false
    File workFile = File.createTempFile('work','.scrap')
    inputBinLog.eachLine{
            if (it.startsWith('###')){
                it = it.replaceAll("###","")
                workFile << it
                consecutiveTrash = false
            } else if (!consecutiveTrash){
                workFile << newline + config.sql.statement.delimiter + newline
                consecutiveTrash = true
            }
    }
    statements = workFile.readLines();
}

def findTableName(line){
    for (tableName in config.list.tablenames){
        if (line.contains(tableName)){
            return config.map.tablemaps.get(tableName)
        }
    }
    return null
}

def replaceColumnHoldersWithNames(){
    def tableMap
    statements.eachWithIndex { statement, index ->
        tableMap = findTableName(statement)
        if (tableMap != null){
            println tableMap
            for (column in tableMap){
                if (statement.contains(column.key)){
                    println "FOUND " + column.key + " in " + statement
                    statements[index] = statement.replace(column.key,column.value)
                    println "New Statement: "+statements[index]
                }
            }
        }
    }
}

public void performConversion(){
    stripUninterestingLines()
    replaceColumnHoldersWithNames()
}

def parseCommandLine(args){
    def cli = new CliBuilder(usage:'groovy BinLogConverter.groovyoovy -i <input binlog file> -o <output sql file> [options]')
    cli.h( longOpt: 'help', required: false, 'show usage information' )
    cli.i( longOpt: 'inputfile', required: true, argName:"input file", args:1, 'binary log filename' )
    cli.o( longOpt: 'outputfile', required: true, argName:"output file", args:1, 'SQL script output filename' )
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
    println ""
    inputBinLog = new File(opt.i)
    println "Using binlog file: " + opt.i

    if ( !inputBinLog.exists()  ) {
        println "ERROR: Can't fine binlog file: " + opt.i + " Aborting."
        System.exit(-1)
    }

    outFile = new File(opt.o)
    if ( outFile.exists() ) {
        println "ERROR: Output file already exits. Aborting."
        System.exit(-1)
    }
}

def createOutput(){
    outFile.createNewFile()
    statements.each{
        outFile << it + newline
    }
}

def runScript(args) {

    parseCommandLine(args)
    init()
    performConversion()
    createOutput()

    println "**************************************************"
    println "*                  Done                          *"
    println "**************************************************"
}

runScript(args);
