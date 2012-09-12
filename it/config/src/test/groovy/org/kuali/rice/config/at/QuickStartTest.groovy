/*
 * Copyright 2006-2012 The Kuali Foundation
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

package org.kuali.rice.config.at

import org.junit.Test
import org.junit.Before
import org.junit.After
import com.google.common.io.Files

import static org.junit.Assert.*
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl
import org.apache.commons.lang.SystemUtils
import org.junit.Ignore

/**
 * These test call maven commands.  They require that the MAVEN_HOME environment variable is set.
 */
class QuickStartTest {
    //FIXME: how should I get the rice version out of this.
    private static final BASE_MVN_CMD = """mvn org.apache.maven.plugins:maven-archetype-plugin:2.2:generate -DinteractiveMode=false -DarchetypeGroupId=org.kuali.rice -DarchetypeArtifactId=rice-archetype-quickstart -Dmaven.failsafe.skip=false -DgroupId=org.kuali.rice -DartifactId=qstest -Dversion=1.0-SNAPSHOT -Dpackage=org.kuali.rice.qstest """

    private File tempBaseDir;
    private String mvnCommandPath;

    JAXBConfigImpl config;

    @Before
    void createBaseDir() {
        tempBaseDir = Files.createTempDir();
    }

    @Before
    void createMavenPath() {
        def mvnHome = System.env['MAVEN_HOME']

        if (!mvnHome) {
            fail("MAVEN_HOME not set");
        }

        mvnCommandPath = mvnHome + "/bin/"
    }

    @Before
    void setConfig() {
        config = new JAXBConfigImpl("classpath:META-INF/core-test-config.xml");
    }

    @After
    void removeBaseDir() {
        def recursiveDel;
        recursiveDel = {
            it.eachDir( recursiveDel )
            it.eachFile {
                it.delete()
            }
            it.delete()
        }

        if (tempBaseDir != null) {
            recursiveDel( tempBaseDir )
        }
    }

    @After
    void clearMavenPath() {
        mvnCommandPath = null;
    }

    @Before
    void clearConfig() {
        config = null;
    }

    def getDBArgs() {
        def args = "";

        def platform = config.getProperty("datasource.ojb.platform");
        def url = config.getProperty("datasource.url");
        def username = config.getProperty("datasource.username");
        def pass = config.getProperty("datasource.password");

        if (platform) {
            args += "-Ddatasource_ojb_platform=" + platform;
        }

        if (url) {
            args += "-Ddatasource_url=" + url;
        }

        if (username) {
            args += "-Ddatasource_username=" + username;
        }

        if (pass) {
            args += "-Ddatasource_password=" + pass;
        }
        return args;
    }

    def getPort() {
        def port = config.getProperty("kns.test.port");

        if (port) {
            return port;
        }
        return "8080";
    }

    def getPortArg() {
        def args = "";
        def port = getPort();

        if (port) {
            args += "-Djetty.port=" + port;
        }

        return args;
    }

    def getVersionArg() {
        def arg = "";

        def version = config.getProperty("rice.version");
        if (version) {
            arg = "-DarchetypeVersion=" + version;
        }

        return arg;
    }

    private ProcessBuilder getProcessBuilderForPlatform(String command) {
        def processBuilder;
        if (SystemUtils.IS_OS_WINDOWS) {
            processBuilder = new ProcessBuilder("""cmd """, "/c", command)
        } else {
            processBuilder = new ProcessBuilder(command)
        }

        return processBuilder;
    }

    /**
     * This test generates a new project in a temp directory using the maven archetype plugin.
     */
    @Test
    void test_quickstart_gen() {

        def processBuilder = getProcessBuilderForPlatform(mvnCommandPath + BASE_MVN_CMD + getDBArgs() + getVersionArg())
        //println processBuilder.command();
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(tempBaseDir)
        def output = "";
        def process = null
        try {
            process = processBuilder.start()
            output = process.text;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        //println output;

        if (output.count("BUILD SUCCESS") != 1) {
            fail("the output did not contain two occurances of BUILD SUCCESS \n ${output}")
        }
    }

    /**
     * This test generates a new project in a temp directory using the maven archetype plugin. It then executes a clean install on the project.
     * This tests that the sample project's the application successfully generates, it compiles, and the unit and integration tests pass.
     */
    @Test
    void test_quickstart_gen_clean_install() {

        def processBuilder = getProcessBuilderForPlatform(mvnCommandPath + BASE_MVN_CMD + getDBArgs() + getVersionArg() + "-Dgoals=\"clean install\"")
        //println processBuilder.command();
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(tempBaseDir)
        def output = "";
        def process = null
        try {
            process = processBuilder.start()
            output = process.text;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        //println output;

        if (output.count("BUILD SUCCESS") != 2) {
            fail("the output did not contain two occurances of BUILD SUCCESS \n ${output}")
        }
    }

    /**
     * This test generates a new project in a temp directory using the maven archetype plugin. It then executes a clean install on the project.
     * This tests that the sample project's the application successfully generates, it compiles, and the unit and integration tests pass and jetty starts up.
     */
    @Test @Ignore("http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4770092")
    void test_quickstart_gen_clean_install_jetty_run() {
        //process isn't being destroyed.  see:
        //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4770092
        //maybe want to use jetty:start & jetty:stop?
        def process = null;
        def processBuilder = getProcessBuilderForPlatform(mvnCommandPath + BASE_MVN_CMD + getDBArgs() + getVersionArg() + """-Dgoals="clean install jetty:run ${getPortArg()}" """)
        println processBuilder.command();
        processBuilder.redirectErrorStream(true)
        processBuilder.directory(tempBaseDir)
        StringBuilder out = new StringBuilder("");
        StringBuilder err = new StringBuilder("");

        try {
            process = processBuilder.start()
            process.consumeProcessOutput(out, err)
            //probably could be more intelligent here
            Thread.sleep(1000*60*2); //two minutes

            def url = new URL("http://localhost:" + getPort() + "/qstest")
            println url
            def connection = url.openConnection()


            println out;
            println err;

            assertEquals(200, connection.responseCode)
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        //println out;
        //println err;

        if (!out.contains("Started Jetty Server")) {
            fail("the output did not contain Started Jetty Server \n ${out} \n ${err}")
        }
    }
}
