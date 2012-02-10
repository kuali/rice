/**
 * Copyright 2005-2012 The Kuali Foundation
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
 * createproject.groovy
 *
 * A groovy script which can be used to create a functioning project
 * skeleton for an application wishing to use Kuali Rice.
 *
 * This script produces a project which uses Maven as it's build tool.
 * Additionally, the generated project can be imported into Eclipse.
 */

if (args.length < 2 || args.length > 9) {
	println 'usage: groovy createproject.groovy -name PROJECT_NAME [-pdir PROJECT_DIR] [-rdir RICE_DIR] [-mdir MAVEN_HOME] [-sampleapp] [-standalone]'
	System.exit(1)
}

PROJECT_DIR = '/java/projects'
RICE_DIR = '/java/projects/rice'
MAVEN_HOME = ''
SAMPLEAPP = false
STANDALONE = false

count = 0
for (arg in args) {
	if (arg == '-name') PROJECT_NAME = args[count + 1]
	if (arg == '-pdir') PROJECT_DIR = args[count + 1]
	if (arg == '-rdir') RICE_DIR = args[count + 1]
	if (arg == '-mdir') MAVEN_HOME = args[count + 1]
	if (arg == '-sampleapp') SAMPLEAPP = true
	if (arg == '-standalone') STANDALONE = true
	count++
}

PROJECT_PATH = PROJECT_DIR + '/' + PROJECT_NAME

//get rice version from rice projects pom file
def pom=new XmlSlurper().parse(new File("${RICE_DIR}/pom.xml"))
riceVersion = pom.version.text()
projectNameUpper = PROJECT_NAME.toUpperCase()

if (SAMPLEAPP) {
	projectNameUpper = "TRAVEL"
}
TEMPLATE_BINDING = [
			"\${PROJECT_NAME}":"$PROJECT_NAME",
			"\${APP_NAMESPACE}":projectNameUpper,
			"\${RICE_VERSION}":riceVersion,
			"\${USER_HOME}":System.getProperty('user.home'),
			"\${bootstrap.spring.file}":"classpath:SpringBeans.xml", 
			"\${monitoring.spring.import}":"", 			
			"\${monitoring.filter}":"",
			"\${monitoring.listener}":"",
			"\${monitoring.mapping}":"",
			"sample-app":PROJECT_NAME
		]

println warningtext()

input = new BufferedReader(new InputStreamReader(System.in))
answer = input.readLine()
if (!"yes".equals(answer.trim().toLowerCase())) {
	System.exit(2)
}

def maven = detectMaven(MAVEN_HOME)

if (!maven) {
	println mavenwarningtext()
	input = new BufferedReader(new InputStreamReader(System.in))
	answer = input.readLine()
	if (!"yes".equals(answer.trim().toLowerCase())) {
		System.exit(2)
	}
}

removeFile("${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME}-config.xml")
removeFile("${System.getProperty('user.home')}/kuali/main/dev/rice.keystore")

buildDir("${System.getProperty('user.home')}/kuali/main/dev/")
buildDir("${PROJECT_DIR}")

ant = new AntBuilder()

ant.delete(dir:PROJECT_PATH)

def springTemplateFile = null;

if (SAMPLEAPP) {
	// copy the Sample Application Files
	ant.copy(todir:PROJECT_PATH + '/src/main/java') {
		fileset(dir:RICE_DIR + '/sampleapp/src/main/java')
	}
	/*ant.copy(todir:PROJECT_PATH + '/src/main/resources') {
	 fileset(dir:RICE_DIR + '/web/src/test/resources') 
	 }*/

	ant.copy(todir:PROJECT_PATH + '/src/main/resources') {
		fileset(dir:RICE_DIR + '/sampleapp/src/main/resources', includes:'META-INF/*,edu/sampleu/**/*,OJB-repository-sampleapp.xml')
	}
	
	// Remove if Sample app not required on Homepage
	ant.copy(todir:PROJECT_PATH + '/src/main/webapp/WEB-INF/jsp') {
		fileset(dir:RICE_DIR + '/sampleapp/src/main/webapp/WEB-INF/jsp')
	}
	
	
	// copy main channels from sampleapp to new project
	ant.copy(todir:PROJECT_PATH + '/src/main/webapp/WEB-INF/tags/rice-portal/channel/main') {
		fileset(dir:RICE_DIR + '/sampleapp/src/main/webapp/WEB-INF/tags/rice-portal/channel/main')
	}
	
	//Copy tag to new project to enable sample-app channel
	ant.copy(todir:PROJECT_PATH + '/src/main/webapp/WEB-INF/tags/rice-portal/') {
		fileset(file:RICE_DIR + '/sampleapp/src/main/webapp/WEB-INF/tags/rice-portal/mainTab.tag')
	}	// copy sample-app-config.xml
	/*ant.copy(todir:PROJECT_PATH + '/src/main/resources') { 
	 fileset(dir:RICE_DIR + '/sampleapp/src/main/resources', includes:'META-INF/*') 
	 }*/

	// copy other configuration files
	/*ant.copy(todir:PROJECT_PATH + '/src/main/resources') { 
	 fileset(dir:RICE_DIR + '/web/src/test/resources', includes:'configurationServiceData.xml, *Resources.properties, OJB-*.xml')
	 }*/

	// copy the Sample Application scripts. Like database create sql
	ant.copy(todir:PROJECT_PATH + '/scripts') {
		fileset(dir:RICE_DIR + '/sampleapp/scripts')
	}

	springTemplateFile = new File(RICE_DIR + '/config/templates/createproject.SampleAppBeans.template.xml')
} else {
	// copy an empty java file to /src/main/java
	ant.copy(file:RICE_DIR + "/config/templates/PutJavaCodeHere.java",
			tofile:PROJECT_PATH + "/src/main/java/PutJavaCodeHere.java")

	// copy configuration files
	ant.copy(todir:PROJECT_PATH + '/src/main/resources') {
		fileset(dir:RICE_DIR + '/web/src/main/resources', includes:'configurationServiceData.xml, META-INF/*')
	}
	/*ant.copy(todir:PROJECT_PATH + '/src/main/resources') { 
	 fileset(dir:RICE_DIR + '/web/src/test/resources', includes:'KR-ApplicationResources.properties')
	 }*/
	
	//copies meta-inf folder to empty rice project skeleton
	ant.copy(todir:PROJECT_PATH + '/src/main/resources/META-INF') {
		fileset(dir:RICE_DIR + '/sampleapp/src/main/resources/META-INF')
	}
	
	
	springTemplateFile = new File(RICE_DIR + '/config/templates/createproject.SpringBeans.template.xml')
}

// copy standard Rice Spring configuration files to project and rename

ant.copy(file:RICE_DIR + "/core/impl/src/main/resources/org/kuali/rice/core/RiceJTASpringBeans.xml",
		tofile:PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-RiceJTASpringBeans.xml")
if (STANDALONE) {
	new File(PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-RiceDataSourceSpringBeans.xml") << templateReplace(new File(RICE_DIR + '/core/impl/src/main/resources/org/kuali/rice/core/RiceDataSourceStandaloneClientSpringBeans.xml.template'))
	new File(PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-RiceSpringBeans.xml") << templateReplace(new File(RICE_DIR + '/web/src/main/resources/org/kuali/rice/config/RiceStandaloneClientSpringBeans.xml.template'))
} else {
	new File(PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-RiceDataSourceSpringBeans.xml") << templateReplace(new File(RICE_DIR + '/core/impl/src/main/resources/org/kuali/rice/core/RiceDataSourceSpringBeans.xml'))
	/*ant.copy(file:RICE_DIR + "/core/impl/src/main/resources/org/kuali/rice/core/RiceDataSourceSpringBeans.xml",
	 tofile:PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-RiceDataSourceSpringBeans.xml")*/
	ant.copy(file:RICE_DIR + "/impl/src/main/resources/org/kuali/rice/config/RiceSpringBeans.xml",
			tofile:PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-RiceSpringBeans.xml")
}

if (SAMPLEAPP) {
	// [tbradford] TODO: There are some classes in the web portion of rice that
	// need to be copied in addition, as they no longer seem to be bundled in
	// any of the Maven artifacts.
	ant.copy(file:RICE_DIR + "/sampleapp/src/main/resources/SampleAppModuleBeans.xml",
			tofile:PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-SampleAppModuleBeans.xml")
}

// create SpringBeans.xml based on the defined springTemplateFile
new File(PROJECT_PATH + "/src/main/resources/SpringBeans.xml") << templateReplace(springTemplateFile)

// copy SQL and XML configuration files

// [tbradford] TODO: Removed the config/ copy and need to figure out an
// elegant way of directing people to the impex process for importing the
// sample application data.

// ant.copy(todir:PROJECT_PATH + '/src/main/config/sql') {
//     fileset(dir:RICE_DIR + '/impl/src/main/config/sql', includes:'**/*', excludes:'.svn,CVS*,.cvs*')
// }

// ant.copy(todir:PROJECT_PATH + '/src/main/config/xml') {
//     fileset(dir:RICE_DIR + '/impl/src/main/config/xml', includes:'**/*', excludes:'.svn,CVS*,.cvs*')
// }

// copy web application

ant.copy(todir:PROJECT_PATH + '/src/main/webapp') {
	fileset(dir:RICE_DIR + '/web/src/main/webapp', includes:'**/*', excludes:'.svn,CVS*,.cvs*')
}

// [tbradford] There's already an index.jsp in the portal that redirects to portal.jsp
// if this isn't the sample application, copy the index.jsp which doesn't include the sample app links
// if (!SAMPLEAPP) {
// 	ant.copy(file:RICE_DIR + "/web/src/main/config/index.jsp",
// 		 tofile:PROJECT_PATH + "/src/main/webapp/index.jsp", overwrite:"true")
// }

// copy the keystore files
ant.copy(todir:"${System.getProperty('user.home')}/kuali/main/dev") {
	fileset(dir:RICE_DIR + '/security', includes:'rice.keystore', excludes:'.svn,CVS*,.cvs*')
}

// execute variable replacement on web.xml to give it the correct SpringBeans.xml

webXml = new File(PROJECT_PATH + '/src/main/webapp/WEB-INF/web.xml')
newWebXmlText = templateReplace(webXml)
webXml.delete()
webXml = new File(PROJECT_PATH + '/src/main/webapp/WEB-INF/web.xml')
webXml << newWebXmlText

// create the initial POM file

pom = new File(PROJECT_PATH + '/pom.xml')
pom << pomtext()

// create the Launch script

// TODO this doesn't seem to exist right now...
//launch = new File(PROJECT_PATH + '/Launch Web App.launch')
//launch << launchtext()

// create the configuration file in the user's home directory

config = new File("${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME}-config.xml")
config << userhomeconfigtext()

// execute variable replacement on sample-app-config.xml and rename the file
if (SAMPLEAPP) {
	config = new File(PROJECT_PATH + '/src/main/resources/META-INF/sample-app-config.xml')
	configtext = ""
	config.eachLine { line -> configtext += line.replace('sample-app', "${PROJECT_NAME}") + "\n" }
	config.delete()
	config = new File(PROJECT_PATH + "/src/main/resources/META-INF/${PROJECT_NAME}-config.xml")
	config << configtext
} else {
	//Rename config file and replace module name from config file  
	new File(PROJECT_PATH + "/src/main/resources/META-INF/${PROJECT_NAME}-config.xml") << templateReplace(new File(RICE_DIR + '/sampleapp/src/main/resources/META-INF/sample-app-config.xml'))
}

// fix the links in index.jsp

index = new File(PROJECT_PATH + '/src/main/webapp/index.jsp')
indextext = ""
index.eachLine {  line ->
	line = line.replace('kr-dev', "${PROJECT_NAME}-dev")
	line = line.replace('-dev/portal.do', "-dev/index.jsp")
	indextext += line + "\n"
}
index.delete()
index = new File(PROJECT_PATH + "/src/main/webapp/index.jsp")
index << indextext

// [tbradford] replaced by datasets that come from the master datasources
// execute variable replacement on RiceSampleAppWorkflowBootstrap.xml
// ingest = new File(PROJECT_PATH + '/src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml')
// ingesttext = ""
// ingest.eachLine { line -> ingesttext += line.replace('kr-dev', "${PROJECT_NAME}-dev") + "\n" }
// ingest.delete()
// ingest = new File(PROJECT_PATH + "/src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml")
// ingest << ingesttext

if (maven) {
	// execute the maven command to build eclipse classpath
	println ("\nExecuting Maven...\n")
	if (maven("eclipse:eclipse", new File(PROJECT_PATH)) != 0) {
		println "\nFailed to execute Maven!  See console output above for details."
		System.exit(1)
	}

	println("\nAdjusting Classpath Entries...")
	def facetXml = new File(PROJECT_PATH + '/.classpath')
	def facets = new XmlParser().parse(facetXml)

	facets.classpathentry.each {
		// con here is part of the classpath entry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER found in the .classpath file
		// at this time, it uniquely ids the classpath entry for the jre
		if (it.'@kind' == 'con') {
			def tempNode = it
			facets.remove(it)
			println('\nMoving ' + it.'@path' + 'to the top of the classpath...')
			facets.children().add(0, tempNode)
		}
	}
	println("\nBacking up the .classpath file... \n")
	new File(PROJECT_PATH, '/.classpath.bkp').delete()
	facetXml.renameTo(new File(PROJECT_PATH, '/.classpath.bkp'))
	new XmlNodePrinter(new PrintWriter(new FileWriter(PROJECT_PATH + '/.classpath'))).print(facets)
}

new File(PROJECT_PATH + '/instructions.txt') << instructionstext()

println instructionstext()

System.exit(0)

def removeFile(path) {
	if (new File(path).exists()) {
		new File(path).delete()
	}
}

def buildDir(path) {
	if (! new File(path).exists()) {
		new File(path).mkdirs()
	}
}

// Returns a closure that can be used to execute Maven

def detectMaven(mvnDir) {
	mvnPath = ""
	mvnExec = (System.getProperty("os.name") =~ /^Windows/) ? "/bin/mvn.bat" : "/bin/mvn"

	mvnHome1 = System.getProperty("M2_HOME")
	mvnHome2 = System.getProperty("m2.home")
	mvnHome3 = System.getenv("M2_HOME")

	if (mvnDir) {
		mvnPath = mvnDir + mvnExec
	} else if (mvnHome1) {
		mvnPath = mvnHome1 + mvnExec
	}  else if (mvnHome2) {
		mvnPath = mvnHome2 + mvnExec
	} else if (mvnHome3) {
		mvnPath = mvnHome3 + mvnExec
	} else {
		// attempt to find maven.home.directory in kuali-build.properties
		file = new File(System.getProperty("user.home") + "/kuali-build.properties")
		if (file.exists()) {
			properties = new Properties()
			properties.load(new FileInputStream(file))
			mvnHomeDir = properties.getProperty("maven.home.directory")
			if (mvnHomeDir) {
				mvnPath = mvnHomeDir + mvnExec
			}
		}
	}
	println "Detected Maven executable at: " + mvnPath
	if (!mvnPath) return null
	return { params, workingDir ->
		if (!params) params = ""
		paramArray = params.split(" ")
		finalParams = new String[paramArray.length + 1]
		finalParams[0] = mvnPath
		for (i = 0; i < paramArray.length; i++) {
			finalParams[i+1] = paramArray[i];
		}
		process = Runtime.getRuntime().exec(finalParams, null, workingDir)
		writeProcessOutput(process)
		process.waitFor()
		return process.exitValue()
	}
}

def writeProcessOutput(process) {
	tempReader = new InputStreamReader(new BufferedInputStream(process.getInputStream()))
	reader = new BufferedReader(tempReader)
	while (true) {
		line = reader.readLine()
		if (line == null) break
			println line
	}
}

def pomtext() {
	return templateReplace(new File(RICE_DIR + '/config/templates/createproject.pom.template.xml'))
}

def launchtext() {
	// TODO this doesn't seem to exist
	return templateReplace(new File(RICE_DIR + '/config/templates/createproject.launch.template.xml'))
}

def userhomeconfigtext() {
	if (STANDALONE) {
		return templateReplace(new File(RICE_DIR + '/config/templates/createproject.standalone.config.template.xml'))
	} else {
		return templateReplace(new File(RICE_DIR + '/config/templates/createproject.config.template.xml'))
	}
}

def templateReplace(file) {
	def replaced = ""
	file.eachLine {  line ->
		for ( binding in TEMPLATE_BINDING ) {
			line = line.replace(binding.key, binding.value)
		}
		replaced += line + "\n"
	}
	return replaced
}

def instructionstext() {
	def datasetDirectory = (SAMPLEAPP)? "/database/demo-server-dataset" : "database/bootstrap-server-dataset"
	"""
==================================================================
        Instructions to complete Rice Template Install
==================================================================
1. Import ${PROJECT_PATH} as an 'existing' eclipse project.
2. Configure an M2_REPO classpath variable in Eclipse
3. Update ${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME}-config.xml 
   with application runtime database information.
4. Configure an impex-build.properties file in your home directory
   with application runtime database information, being sure to set
   the torque.schema.dir property to the location of the rice
   ${datasetDirectory} directory. A template for impex-build.properties 
   can be found in database/database-impex/impex-build.properties.sample.
5. Run the impex tool under /database/impex-database.  If you require
   a schema to be created for you, type: 'ant create-schema'.
   To import the demonstration dataset, type: 'ant import'
6. Start the application using the eclipse launch configuration.
   In the eclipse Run menu, choose 'Run...' and select the
   configuration named 'Launch Web App'
7. Open a brower to http://localhost:8080/${PROJECT_NAME}-dev/index.jsp

   
   These instructions can also be found in the instructions.txt file
   in your generated project.
"""
}

def warningtext() {
	"""
==================================================================
                            WARNING 
==================================================================
This program will delete the following directory and replace it 
with a new project:
    ${PROJECT_PATH}

It will also create or replace the following files in USER_HOME:
    1) ${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME}-config.xml
    2) ${System.getProperty('user.home')}/kuali/main/dev/rice.keystore

If this is not what you want, please supply more information:
    usage: groovy createproject -name PROJECT_NAME [-pdir PROJECT_DIR] [-rdir RICE_DIR] [-mdir MAVEN_HOME]

Do you want to continue (yes/no)?"""
}

def mavenwarningtext() {
	"""
==================================================================
                            WARNING 
==================================================================
We could not locate a Maven 2 installation on your machine!
Without this we will not be able to create the .classpath file
which will be used by Eclipse.

In order to allow this script to locate Maven please install
Maven 2 and do one of the following:
	1) Specify the -mdir parameter to this script
	2) Create an M2_HOME environment variable
	3) Create an m2.home environment variable
	4) Create a kuali-build.properties file at
	   ${System.getProperty('user.home')}
	   and add a 'maven.home.directory' property to it

If you don't wish to install Maven you can still continue with
this script but you will have to configure your Eclipse
classpath yourself.

Do you want to continue (yes/no)?"""
}