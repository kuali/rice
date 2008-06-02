if (args.length < 2 || args.length > 6) { 
	println 'usage: groovy createproject.groovy -name PROJECT_NAME [-pdir PROJECT_DIR] [-rdir RICE_DIR]'
	System.exit(1)	
}

PROJECT_DIR = '/java/projects'
RICE_DIR = '/java/projects/rice'

count = 0
for (arg in args) {
	if (arg == '-name') PROJECT_NAME = args[count + 1]
   	if (arg == '-pdir') PROJECT_DIR = args[count + 1]
	if (arg == '-rdir') RICE_DIR = args[count + 1]
	count++
}	

PROJECT_PATH = PROJECT_DIR + '/' + PROJECT_NAME

TEMPLATE_BINDING = ["\${PROJECT_NAME}":"$PROJECT_NAME", "\${RICE_VERSION}":"0.9.3-SNAPSHOT", "\${USER_HOME}":System.getProperty('user.home')] 

println warningtext()

input = new BufferedReader(new InputStreamReader(System.in))
answer = input.readLine()
if (!"yes".equals(answer.trim().toLowerCase())) {
	System.exit(2)
}

removeFile("${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME}-config.xml")
removeFile("${System.getProperty('user.home')}/kuali/test/dev/${PROJECT_NAME}-test-config.xml")
removeFile("${System.getProperty('user.home')}/kuali/main/dev/rice.keystore")
removeFile("${System.getProperty('user.home')}/kuali/test/dev/rice.keystore")

buildDir("${System.getProperty('user.home')}/kuali/main/dev/")
buildDir("${System.getProperty('user.home')}/kuali/test/dev/")
buildDir("${PROJECT_DIR}")

ant = new AntBuilder() 

ant.delete(dir:PROJECT_PATH) 

// Copy the Sample Application Files

ant.copy(todir:PROJECT_PATH + '/src/main/java/edu') { 
    fileset(dir:RICE_DIR + '/server/src/test/java/edu')
}
ant.copy(todir:PROJECT_PATH + '/src/main/resources') { 
    fileset(dir:RICE_DIR + '/server/src/test/resources', includes:'**/*', excludes:'.svn,CVS*,.cvs*,db/, **/standalone/**/*, **/SampleAppBeans-test.xml, **/ServerDefault*, **/sample-app-test-config.xml')
    fileset(dir:RICE_DIR + '/server/src/main/resources', includes:'**/*', excludes:'.svn,CVS*,.cvs*,db/, **/standalone/**/*, **/SampleAppBeans-test.xml, **/ServerDefault*, **/sample-app-test-config.xml') 
}

ant.copy(todir:PROJECT_PATH + '/src/test/resources') { 
    fileset(dir:RICE_DIR + '/server/src/test/resources', includes:'**/sample-app-test-config.xml') 
}

ant.copy(todir:PROJECT_PATH + '/src/main/config/ddl') { 
    fileset(dir:RICE_DIR + '/kns/src/main/config/ddl', includes:'**/*', excludes:'.svn,CVS*,.cvs*') 
}
ant.copy(todir:PROJECT_PATH + '/src/main/config/sql') { 
    fileset(dir:RICE_DIR + '/kns/src/main/config/sql', includes:'**/*', excludes:'.svn,CVS*,.cvs*') 
}
ant.copy(todir:PROJECT_PATH + '/src/main/config/sql') { 
    fileset(dir:RICE_DIR + '/kew/src/main/config/sql', includes:'**/*', excludes:'.svn,CVS*,.cvs*') 
}
ant.copy(todir:PROJECT_PATH + '/src/main/config/xml') { 
    fileset(dir:RICE_DIR + '/kns/src/main/config/xml', includes:'**/*', excludes:'.svn,CVS*,.cvs*') 
}

ant.copy(todir:PROJECT_PATH + '/src/main/webapp') { 
    fileset(dir:RICE_DIR + '/server/src/main/webapp', includes:'**/*', excludes:'.svn,CVS*,.cvs*') 
}

ant.copy(todir:"${System.getProperty('user.home')}/kuali/main/dev") { 
    fileset(dir:RICE_DIR + '/security', includes:'rice.keystore', excludes:'.svn,CVS*,.cvs*') 
}
ant.copy(todir:"${System.getProperty('user.home')}/kuali/test/dev") { 
    fileset(dir:RICE_DIR + '/security', includes:'rice.keystore', excludes:'.svn,CVS*,.cvs*') 
}
ant.delete(dir:PROJECT_PATH + '/src/main/resources/org')

// create the initial POM file

pom = new File(PROJECT_PATH + '/pom.xml')
pom << pomtext()

// TODO generate .classpath file using eclipse:eclipse from maven somehow, this old way won't cut it anymore after getting rid of maven plugin
// classpath = new File(PROJECT_PATH + '/.classpath')
// classpath << classpathtext()
// TODO generate .project file using eclipse:eclipse from maven showhow
//project = new File(PROJECT_PATH + '/.project')
//project << projecttext()

launch = new File(PROJECT_PATH + '/Launch Web App.launch')
launch << launchtext()

config = new File("${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME}-config.xml")
config << userhomeconfigtext()

config = new File(PROJECT_PATH + '/src/main/resources/META-INF/sample-app-config.xml')
configtext = ""
config.eachLine { line -> configtext += line.replace('sample-app', "${PROJECT_NAME}") + "\n" }
config.delete()
config = new File(PROJECT_PATH + "/src/main/resources/META-INF/${PROJECT_NAME}-config.xml")
config << configtext
	
config = new File(PROJECT_PATH + '/src/test/resources/META-INF/sample-app-test-config.xml')
configtext = ""
config.eachLine { line -> configtext += line.replace('sample-app', "${PROJECT_NAME}") + "\n" }
config.delete()
config = new File(PROJECT_PATH + "/src/test/resources/META-INF/${PROJECT_NAME}-test-config.xml")
config << configtext

// Rename SampleAppSpringBeans.xml to SpringBeans.xml
spring = new File(PROJECT_PATH + '/src/main/resources/SpringBeans.xml')
sampleAppSpring = new File(PROJECT_PATH + '/src/main/resources/SampleAppBeans.xml')
if ( !sampleAppSpring.renameTo( spring ) ) {
	println 'Failed to rename SampleAppSpringBeans.xml to SpringBeans.xml'
	System.exit(1)
}
	
springtext = ""
spring.eachLine { line -> springtext += line.replace('sample-app', "${PROJECT_NAME}") + "\n" }
spring.delete()
spring = new File(PROJECT_PATH + "/src/main/resources/SpringBeans.xml")
spring << springtext

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

ingest = new File(PROJECT_PATH + '/src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml')
ingesttext = ""
ingest.eachLine { line -> ingesttext += line.replace('kr-dev', "${PROJECT_NAME}-dev") + "\n" }
ingest.delete()
ingest = new File(PROJECT_PATH + "/src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml")
ingest << ingesttext

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
2. Update ${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME}-config.xml 
   with application runtime database information.
3. Update ${System.getProperty('user.home')}/kuali/test/dev/${PROJECT_NAME}-test-config.xml 
   with unit testing database information.
4. Run the /src/main/config/sql/rice_db_bootstrap.sql file if you 
   have a fresh database.  If you need to drop all tables first, run
   the /src/main/config/sql/rice_db_destroy.sql followed by the former.
5. Start the application using the eclipse launch configuration.
   In the eclipse Run menu, choose 'Run...' and select the the
   configuration named 'Launch Web App'
6. Open a brower to http://localhost:8080/${PROJECT_NAME}-dev/index.html
7. Finish bootstrapping by ingesting the workflow xml.  From the 
   start screen, go to the workflow portal and log in as quickstart.
   Go to the 'XML Ingester' link and ingest the following file: 
   /src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml
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
    2) ${System.getProperty('user.home')}/kuali/test/dev/${PROJECT_NAME}-test-config.xml
    3) ${System.getProperty('user.home')}/kuali/main/dev/rice.keystore
    4) ${System.getProperty('user.home')}/kuali/test/dev/rice.keystore

If this is not what you want, please supply more information:
    usage: groovy createproject -name PROJECT_NAME [-pdir PROJECT_DIR] [-rdir RICE_DIR]

Do you want to continue (yes/no)?"""
}
