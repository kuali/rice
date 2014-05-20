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

/**
 * Created by dseibert on 4/18/14.*/

class LoadTestUtils {

    public static def slurpConfig(configFilePath, defaultConfigFilePath) {
        def defaultConfigFile = new File(LoadTestUtils.getClassLoader().getResource(defaultConfigFilePath).file)
        def config = new ConfigSlurper().parse(defaultConfigFile.toURL());
        if (configFilePath){
            File configFile = new File(configFilePath)
            println "Loading configuration file: $configFile.canonicalPath"
            if ( !configFile.exists() ) {
                println "ERROR: Configuration file does not exist - aborting."
                return
            }
            def altConfig = new ConfigSlurper().parse(configFile.text);
            config.merge(altConfig);
        }

        config.newline = System.getProperty("line.separator")
        println "*** COMPLETED CONFIG SLURP ***"
        return config
    }

    public static def getUuid() {
        return UUID.randomUUID() as String;
    }

}