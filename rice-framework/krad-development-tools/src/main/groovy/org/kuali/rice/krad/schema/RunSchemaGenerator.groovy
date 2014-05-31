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

import org.apache.commons.lang.StringUtils
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.apache.log4j.Level

ConsoleAppender console = new ConsoleAppender()

String PATTERN = "%d [%p|%c|%C{1}] %m%n"
console.setLayout(new PatternLayout(PATTERN))
console.setThreshold(Level.INFO)
console.activateOptions()

Logger.getRootLogger().addAppender(console)

OUTPUT_PATH_ARG_PREFIX = "-o"
SCAN_PACKAGES_ARG_PREFIX = "-p"
ADDITIONAL_SCHEMA_TAGS_PROPERTIES_PATH = "-t"
SCHEMA_NAME_ARG_PREFIX = "-s"
OTHER_SCHEMA_PACKAGES = "-x"

ResourceBundle doc = ResourceBundle.getBundle("org.kuali.rice.krad.ComponentJavaDocs")

String outputPath
String[] scanPackages
String additionalSchemaTagsPropertiesPath
String schemaName = "krad"
Map<String, List<String>> otherSchemaPackages

if (binding.getVariables().containsKey("args")) {
    if (args == null || args.length < 1) {
        throw new RuntimeException("At least one argument, the output path must be given. Pass as -o {path}")
    }

    int i = 0
    while (i < args.length) {
        String arg = args[i].trim()

        if (arg.equals(OUTPUT_PATH_ARG_PREFIX)) {
            outputPath = args[i + 1].trim()
            i++
        } else if (arg.equals(SCAN_PACKAGES_ARG_PREFIX)) {
            scanPackages = args[i + 1].trim().split("\\s*,\\s*")
            i++
        } else if (arg.equals(ADDITIONAL_SCHEMA_TAGS_PROPERTIES_PATH)) {
            additionalSchemaTagsPropertiesPath = args[i + 1].trim()
            i++
        } else if (arg.equals(SCHEMA_NAME_ARG_PREFIX)) {
            schemaName = args[i + 1].trim()
            i++
        } else if (arg.equals(OTHER_SCHEMA_PACKAGES)) {
            schemaPackagesMappings = args[i + 1].trim().split("\\s*;\\s*")

            otherSchemaPackages = new HashMap<String, List<String>>()
            schemaPackagesMappings.each { schemaPackageMapping ->
                String otherSchemaName = StringUtils.substringBefore(schemaPackageMapping, ":")
                String[] otherPackages = StringUtils.substringAfter(schemaPackageMapping, ":").split("\\s*,\\s*")

                otherSchemaPackages.put(otherSchemaName, Arrays.asList(otherPackages))
            }

            i++
        }

        i++
    }
} else if (binding.getVariables().containsKey("project")) {
    outputPath = project.properties.outputPath.trim()

    if (project.properties.containsKey("scanPackages")) {
        scanPackages = project.properties.scanPackages.trim().split("\\s*,\\s*")
    }
}

if (outputPath == null || outputPath.isEmpty()) {
    throw new RuntimeException("Output path not given. Pass as -o {path}")
}

if (scanPackages == null) {
    throw new RuntimeException("Scan packages not given. Pass as -p {packages}")
}

ResourceBundle additionalSchemaTagsProperties = null
if (additionalSchemaTagsPropertiesPath != null) {
    additionalSchemaTagsProperties = ResourceBundle.getBundle(additionalSchemaTagsPropertiesPath)
}

new SchemaGenerator().
        generateSchema(doc, scanPackages, outputPath, additionalSchemaTagsProperties, schemaName, otherSchemaPackages)



