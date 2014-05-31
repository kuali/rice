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
package org.kuali.rice.krad.schema

import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.framework.config.property.SimpleConfig
import org.kuali.rice.krad.devtools.datadictionary.FactoryExposingDataDictionary

XML_FILE_SUFFIX = ".xml"

FILE_PATH_ARG_PREFIX = "-f"
SCAN_PACKAGES_ARG_PREFIX = "-p"
DICTIONARY_FILE_LISTING_PATH = "-d"
MULTI_VALUE_ARG_REGEX = "\\s*,\\s*"

ConsoleAppender console = new ConsoleAppender()

String PATTERN = "%d [%p|%c|%C{1}] %m%n"
console.setLayout(new PatternLayout(PATTERN))
console.setThreshold(Level.INFO)
console.activateOptions()

Logger.getRootLogger().addAppender(console)

String[] filePaths
String[] scanPackages = null
String dictionaryFileListPath

if (binding.getVariables().containsKey("args")) {
    if (args == null || args.length < 1) {
        throw new RuntimeException("At least one argument, the file path(s) must be given. Pass as -f {path}")
    }

    int i = 0
    while (i < args.length) {
        String arg = args[i].trim()

        if (arg.equals(FILE_PATH_ARG_PREFIX)) {
            filePaths = args[i + 1].trim().split(MULTI_VALUE_ARG_REGEX)
            i++
        } else if (arg.equals(DICTIONARY_FILE_LISTING_PATH)) {
            dictionaryFileListPath = args[i + 1].trim()
            i++
        } else if (arg.equals(SCAN_PACKAGES_ARG_PREFIX)) {
            scanPackages = args[i + 1].trim().split(MULTI_VALUE_ARG_REGEX)
            i++
        }

        i++
    }
} else if (binding.getVariables().containsKey("project")) {
    filePaths = project.properties.outputPath.trim().split(MULTI_VALUE_ARG_REGEX)

    if (project.properties.containsKey("scanPackages")) {
        scanPackages = project.properties.scanPackages.trim().split(MULTI_VALUE_ARG_REGEX)
    }
}

if (filePaths == null) {
    throw new RuntimeException("No file path(s) given. Pass as -f {path}")
}

SimpleConfig config = new SimpleConfig()
config.putProperty("application.version", "null")
config.putProperty("rice.version", "null")

ConfigContext.init(config)

FactoryExposingDataDictionary dataDictionary = new FactoryExposingDataDictionary()

def inputFile = new File(dictionaryFileListPath)
inputFile.eachLine { line ->
    dataDictionary.addConfigFileLocation("", line.trim())
}
dataDictionary.parseDataDictionaryConfigurationFiles(false)

List<File> filesToConvert = new ArrayList<File>()

filePaths.each { filePath ->
    File fileInput = new File(filePath)

    if (fileInput.isDirectory()) {
        filesToConvert.addAll(getXmlFilesToConvert(fileInput))
    } else {
        filesToConvert.add(fileInput)
    }
}

filesToConvert.each { file -> new SchemaConverter(file, scanPackages, dataDictionary) }

/**
 * Gets the xml files within the given directory, recursively.
 *
 * @param directory the directory to start in
 * @return list of found xml files
 */
def List<File> getXmlFilesToConvert(File directory) {
    List<File> files = new ArrayList<File>()

    for (File directoryFile : directory.listFiles()) {
        if (directoryFile.isDirectory()) {
            files.addAll(getXmlFilesToConvert(directoryFile))
        } else if (directoryFile.isFile() && directoryFile.getPath().endsWith(XML_FILE_SUFFIX) &&
                !directoryFile.getName().startsWith(SchemaConverter.CONVERTED_FILE_PREFIX)) {
            files.add(directoryFile)
        }
    }

    return files
}
