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
import org.kuali.rice.loadtest.LoadTestUtils

def opt
def config

File inputBinLog
def statements
def outFile

def defaultConfigFilePath
def newline


/**
 * Build config object, set local parameters
 * @return
 */
def old_init() {
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
    println "*** COMPLETED SLURP ***"
}

def init() {
    config = LoadTestUtils.slurpConfig(opt.c,"binlog.conversion.properties")
    newline = config.newline
}
/**
 * We only want the lines that start with ###
 * Separate the good stuff with delimiter line
 * Then saves all the parsed lines into a List
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

/**
 * find map associated with the table manipulated by the statement
 */
def findTableMap(line){
    for (tableName in config.map.tablemaps.keySet()){
        if (line.contains(tableName)){
            return config.map.tablemaps.get(tableName)
        }
    }
    if (line.size() > 5){
        println "************************"
        println "***** TABLE Not FOUND: "+line
        println "************************"
    }
    return null
}

/**
 * date data fields in binlog are not surrounded by quotes, add the quotes
 */
def stringifyDates(tokens){
    def date_pattern = ~/....-..-../
    def time_pattern = ~/..:..:../
    tokens.eachWithIndex{ token, index ->
        if (token.find(date_pattern) || token.find(time_pattern)){
            tokens[index] = token[0..token.indexOf('=')] + "'" + token[token.indexOf("=")+1..-1]+"'"
        }
    }
}

/**
 * remove NULL assignments from the sql commands
 * binlog statements contain null assignments for all unused fields. we don't need them.
 */
def removeNullColumns(tokens){
    tokens.eachWithIndex{ token, index ->
        if (token.contains("=NULL")){
            tokens[index]=""
        }
    }
}

/**
 * Raw data contains placeholders for column names, replace the name holders with the actual table names
 * @param tokens
 * @param tableMap
 * @return
 */
def transformColumnNames(tokens, tableMap){
    if (tableMap != null){
        println tableMap
        tokens.eachWithIndex{ token, index ->
            if (token.contains("=")){
                def colId = token[0..<token.indexOf("=")]
                if (tableMap.containsKey(colId)){
                    def comma = (tokens[index+1]?.contains("=")) ? "," : ""
                    tokens[index] = tableMap[colId] + token[token.indexOf("=")..-1] + comma
                }
            }
        }
    }
}

/**
 * for update statements, raw data from binlog has the where clause before the set clause.
 * Reverse them to be in the correct order with the where clause at the end
 * @param stmt
 * @return
 */
def moveWhereClauseForUpdates(stmt){
    if (stmt.contains("UPDATE")){
        def wClause = stmt[stmt.indexOf("WHERE ")..<stmt.indexOf(" SET ")]
        stmt = stmt - wClause
        stmt = stmt + wClause
    }
    return stmt
}

/**
 * Break the raw sql statement into tokens for easier manipulation.
 * Data containing spaces will be split into multiple tokens, handle this during tokenization.
 * @param stmt
 * @return
 */
def tokenize(stmt){
    def tokens = stmt.tokenize()
    def lastGoodToken = 0;
    tokens.eachWithIndex{token, index ->
        if (token.startsWith("@") || token.startsWith("INSERT") || token.startsWith("SET") || token.startsWith("UPDATE")
            || token.startsWith("WHERE") || token.startsWith("DELETE")){
            lastGoodToken = index
        } else {
            tokens[lastGoodToken] = tokens[lastGoodToken] + " " + token
            tokens[index] = ""
        }
    }
    return tokens
}

/**
 * Ensure any quoted data is handled appropriately.
 * Use double-quote to surround string. Single quotes for quotes in data
 */
def replaceQuotesWithDouble(tokens)
{
    tokens.eachWithIndex{ token, index ->
        def last = token.lastIndexOf("'");
        token = token.reverse().replaceFirst("'",'"').reverse().replaceFirst("'",'"');
        tokens[index] = token
    }
}

/**
 * Reconstructs a SQL statement from the list of tokens
 * @param tokens
 * @return
 */
def untokenize(tokens)
{
    replaceQuotesWithDouble(tokens)
    StringBuffer sb = new StringBuffer()
    tokens.each{
        sb.append(it+" ")
    }
    return sb.toString()
}

/**
 * tears down the statement into tokens, manipulates them, and then rebuilds the statement
 * 1. turns dates into strings
 * 2. remove and null assignments in sql statements
 * 3. transform the sql to use column names
 * 4. for update commands, move the where clause to the end of the statement
 * @return
 */
def transformStatements(){
    statements.eachWithIndex{ statement, index ->
        def tableMap = findTableMap(statement)
        if (tableMap){
            def tokens = tokenize(statement)
            removeNullColumns(tokens)
            tokens = tokens.minus("")
            stringifyDates(tokens)
            transformColumnNames(tokens, tableMap)
            def reconstructedStatement = untokenize(tokens)
            reconstructedStatement = moveWhereClauseForUpdates(reconstructedStatement)
            statements[index] = reconstructedStatement
        }
    }
}


public void performConversion(){
    stripUninterestingLines()
    transformStatements()
}

/**
 * Handle command line interface.
 * @param args
 */
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

/**
 * write the converted statements to file
 * @return
 */
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
