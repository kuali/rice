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
	println 'usage: groovy createproject.groovy -name PROJECT_NAME [-pdir PROJECT_DIR] [-rdir RICE_DIR] [-mdir MAVEN_HOME] [-sampleapp]'
	System.exit(1)	
}

PROJECT_DIR = '/java/projects'
RICE_DIR = '/java/projects/rice'
MAVEN_HOME = ''
SAMPLEAPP = false

count = 0
for (arg in args) {
	if (arg == '-name') PROJECT_NAME = args[count + 1]
   	if (arg == '-pdir') PROJECT_DIR = args[count + 1]
	if (arg == '-rdir') RICE_DIR = args[count + 1]
	if (arg == '-mdir') MAVEN_HOME = args[count + 1]
	if (arg == '-sampleapp') SAMPLEAPP = true
	count++
}	

PROJECT_PATH = PROJECT_DIR + '/' + PROJECT_NAME

TEMPLATE_BINDING = [
	"\${PROJECT_NAME}":"$PROJECT_NAME",
	"\${RICE_VERSION}":"0.9.4-SNAPSHOT",
	"\${USER_HOME}":System.getProperty('user.home'),
	"\${bootstrap.spring.file}":"SpringBeans.xml"
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
	ant.copy(todir:PROJECT_PATH + '/src/main/java/org') { 
    	fileset(dir:RICE_DIR + '/web/src/test/java/org')
	}
	ant.copy(todir:PROJECT_PATH + '/src/main/resources/org') {
		fileset(dir:RICE_DIR + '/web/src/test/resources/org') 
	}
	
	// copy sample-app-config.xml
	ant.copy(todir:PROJECT_PATH + '/src/main/resources') { 
    	fileset(dir:RICE_DIR + '/web/src/main/resources', includes:'META-INF/*') 
	}
	
	// copy other configuration files
	ant.copy(todir:PROJECT_PATH + '/src/main/resources') { 
    	fileset(dir:RICE_DIR + '/web/src/test/resources', includes:'configurationServiceData.xml, *Resources.properties, OJB-*.xml')
    }
    
	springTemplateFile = new File(RICE_DIR + '/scripts/templates/createproject.SampleAppBeans.template.xml')
} else {
	// copy an empty java file to /src/main/java
	ant.copy(file:RICE_DIR + "/scripts/templates/PutJavaCodeHere.java",
		 tofile:PROJECT_PATH + "/src/main/java/PutJavaCodeHere.java")
		 
	// copy configuration files
	ant.copy(todir:PROJECT_PATH + '/src/main/resources') { 
    	fileset(dir:RICE_DIR + '/web/src/main/resources', includes:'configurationServiceData.xml, META-INF/*') 
	}
	ant.copy(todir:PROJECT_PATH + '/src/main/resources') { 
    	fileset(dir:RICE_DIR + '/web/src/test/resources', includes:'KR-ApplicationResources.properties')
    }
    
	springTemplateFile = new File(RICE_DIR + '/scripts/templates/createproject.SpringBeans.template.xml')
}

// copy standard Rice Spring configuration files to project and rename
 
ant.copy(file:RICE_DIR + "/web/src/main/resources/org/kuali/rice/config/RiceJTASpringBeans.xml",
		 tofile:PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-RiceJTASpringBeans.xml")
ant.copy(file:RICE_DIR + "/web/src/main/resources/org/kuali/rice/config/RiceDataSourceSpringBeans.xml",
		 tofile:PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-RiceDataSourceSpringBeans.xml")
ant.copy(file:RICE_DIR + "/web/src/main/resources/org/kuali/rice/config/RiceSpringBeans.xml",
		 tofile:PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-RiceSpringBeans.xml")

if (SAMPLEAPP) {
	ant.copy(file:RICE_DIR + "/web/src/main/resources/SampleAppModuleBeans.xml",
			 tofile:PROJECT_PATH + "/src/main/resources/${PROJECT_NAME}-SampleAppModuleBeans.xml")
}

// create SpringBeans.xml based on the defined springTemplateFile
new File(PROJECT_PATH + "/src/main/resources/SpringBeans.xml") << templateReplace(springTemplateFile)

// copy SQL and XML configuration files

ant.copy(todir:PROJECT_PATH + '/src/main/config/sql') { 
    fileset(dir:RICE_DIR + '/impl/src/main/config/sql', includes:'**/*', excludes:'.svn,CVS*,.cvs*') 
}
ant.copy(todir:PROJECT_PATH + '/src/main/config/xml') { 
    fileset(dir:RICE_DIR + '/impl/src/main/config/xml', includes:'**/*', excludes:'.svn,CVS*,.cvs*') 
}

// copy web application

ant.copy(todir:PROJECT_PATH + '/src/main/webapp') { 
    fileset(dir:RICE_DIR + '/web/src/main/webapp', includes:'**/*', excludes:'.svn,CVS*,.cvs*') 
}

// if this isn't the sample application, copy the index.html which doesn't include the sample app links
if (!SAMPLEAPP) {
	ant.copy(file:RICE_DIR + "/web/src/main/config/index.html",
		 tofile:PROJECT_PATH + "/src/main/webapp/index.html", overwrite:"true")
}

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

launch = new File(PROJECT_PATH + '/Launch Web App.launch')
launch << launchtext()

// create the configuration file in the user's home directory

config = new File("${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME}-config.xml")
config << userhomeconfigtext()

// execute variable replacement on sample-app-config.xml and rename the file

config = new File(PROJECT_PATH + '/src/main/resources/META-INF/sample-app-config.xml')
configtext = ""
config.eachLine { line -> configtext += line.replace('sample-app', "${PROJECT_NAME}") + "\n" }
config.delete()
config = new File(PROJECT_PATH + "/src/main/resources/META-INF/${PROJECT_NAME}-config.xml")
config << configtext

// fix the links in index.html

index = new File(PROJECT_PATH + '/src/main/webapp/index.html')
indextext = ""
index.eachLine { 
    line -> 
	    line = line.replace('kr-dev', "${PROJECT_NAME}-dev")
	    line = line.replace('-dev/portal.do', "-dev/index.html")
    	indextext += line + "\n" 
}
index.delete()
index = new File(PROJECT_PATH + "/src/main/webapp/index.html")
index << indextext

// execute variable replacement on RiceSampleAppWorkflowBootstrap.xml

ingest = new File(PROJECT_PATH + '/src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml')
ingesttext = ""
ingest.eachLine { line -> ingesttext += line.replace('kr-dev', "${PROJECT_NAME}-dev") + "\n" }
ingest.delete()
ingest = new File(PROJECT_PATH + "/src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml")
ingest << ingesttext

if (maven) {
	// execute the maven command to build eclipse classpath
	println ("\nExecuting Maven...\n")
	if (maven("eclipse:eclipse", new File(PROJECT_PATH)) != 0) {
		println "\nFailed to execute Maven!  See console output above for details."
		System.exit(1)
	}
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
	mvnExec = "/bin/mvn"
	
	mvnHome1 = System.getProperty("M2_HOME")
	mvnHome2 = System.getProperty("m2.home")
	
	if (mvnDir) {
		mvnPath = mvnDir + mvnExec
	} else if (mvnHome1) {
		mvnPath = mvnHome1 + mvnExec
	}  else if (mvnHome2) {
		mvnPath = mvnHome2 + mvnExec
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
	return templateReplace(new File(RICE_DIR + '/scripts/templates/createproject.pom.template.xml'))
}

def launchtext() {
	return templateReplace(new File(RICE_DIR + '/scripts/templates/createproject.launch.template.xml'))
}

def userhomeconfigtext() {
	return templateReplace(new File(RICE_DIR + '/scripts/templates/createproject.config.template.xml'))
}

def templateReplace(file) {
	def replaced = ""
	file.eachLine { 
    line -> 
    	for ( binding in TEMPLATE_BINDING ) {
          line = line.replace(binding.key, binding.value)
        }
    	replaced += line + "\n" 
	}
	return replaced
}

def instructionstext() {
"""
==================================================================
        Instructions to complete Rice Template Install
==================================================================
1. Import ${PROJECT_PATH} as an 'existing' eclipse project.
2. Configure an M2_REPO classpath variable in Eclipse
3. Update ${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME}-config.xml 
   with application runtime database information.
4. Run the /src/main/config/sql/rice_db_bootstrap.sql script if
   you have a fresh database.  If you need to drop all tables first,
   run the /src/main/config/sql/rice_db_destroy.sql script 
   followed by the former.
5. If you generated the project with the -sampleapp option, run 
   the /src/main/config/sql/rice_sample_app.sql script.
6. Start the application using the eclipse launch configuration.
   In the eclipse Run menu, choose 'Run...' and select the the
   configuration named 'Launch Web App'
7. Open a brower to http://localhost:8080/${PROJECT_NAME}-dev/index.html
8. Finish bootstrapping by ingesting the workflow xml.  From the 
   start screen, go to the workflow portal and log in as quickstart.
   Go to the 'XML Ingester' link and ingest the following file: 
   /src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml
   
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