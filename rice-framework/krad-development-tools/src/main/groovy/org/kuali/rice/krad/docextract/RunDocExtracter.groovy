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
package org.kuali.rice.krad.docextract

import org.apache.commons.lang.StringUtils
import org.kuali.rice.krad.docextract.DocExtracter

OUTPUT_PATH_ARG_PREFIX = "-o"
SOURCE_PATH_ARG_PREFIX = "-s"
SOURCE_PACKAGES_ARG_PREFIX = "-p"
EXCLUDE_PACKAGES_ARG_PREFIX = "-e"

String outputPath
String sourcePath
String sourcePackages
String excludePackages

if (binding.getVariables().containsKey("args")) {
    if (args == null || args.length < 3) {
        throw new RuntimeException("At least the source and output paths, along with the source packages must be given")
    }

    int i = 0
    while (i < args.length) {
        String arg = args[i].trim()

        if (arg.equals(OUTPUT_PATH_ARG_PREFIX)) {
            outputPath = args[i + 1].trim()
            i++
        } else if (arg.equals(SOURCE_PATH_ARG_PREFIX)) {
            sourcePath = args[i + 1].trim()
            i++
        } else if (arg.equals(SOURCE_PACKAGES_ARG_PREFIX)) {
            sourcePackages = args[i + 1].trim()
            i++
        } else if (arg.equals(EXCLUDE_PACKAGES_ARG_PREFIX)) {
            excludePackages = args[i + 1].trim()
            i++
        }

        i++
    }
} else if (binding.getVariables().containsKey("project")) {
    outputPath = project.properties.outputPath
    sourcePath = project.properties.sourcePath
    sourcePackages = project.properties.sourcePackages
    excludePackages = project.properties.excludePackages
}

if (StringUtils.isBlank(outputPath)) {
    throw new RuntimeException("Output path not given. Pass as " + OUTPUT_PATH_ARG_PREFIX + " path")
}

if (StringUtils.isBlank(sourcePath)) {
    throw new RuntimeException("Source path not given. Pass as " + SOURCE_PATH_ARG_PREFIX + " path")
}

if (StringUtils.isBlank(sourcePackages)) {
    throw new RuntimeException("Source packages not given. Pass as " + SOURCE_PACKAGES_ARG_PREFIX + " packages")
}

DocExtracter.generateDocProperties(outputPath, sourcePath, sourcePackages, excludePackages)



