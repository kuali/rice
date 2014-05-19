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