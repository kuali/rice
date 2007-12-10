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

println warningtext()
input = new BufferedReader(new InputStreamReader(System.in))
answer = input.readLine()
if (!"yes".equals(answer.trim().toLowerCase())) {
	System.exit(2)
}

removeFile("${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME.toLowerCase()}-config.xml")
removeFile("${System.getProperty('user.home')}/kuali/test/dev/${PROJECT_NAME.toLowerCase()}-test-config.xml")
removeFile("${System.getProperty('user.home')}/kuali/main/dev/ricekeystore")
removeFile("${System.getProperty('user.home')}/kuali/test/dev/ricekeystore")

buildDir("${System.getProperty('user.home')}/kuali/main/dev/")
buildDir("${System.getProperty('user.home')}/kuali/test/dev/")
buildDir("${PROJECT_DIR}")

ant = new AntBuilder() 

ant.delete(dir:PROJECT_PATH) 
ant.copy(todir:PROJECT_PATH + '/src/main/java/edu') { 
    fileset(dir:RICE_DIR + '/kns/src/test/java/edu', includes:'**/*', excludes:'CVS*,.cvs*') 
}
ant.copy(todir:PROJECT_PATH + '/src/test/java/org/kuali/rice') { 
    fileset(dir:RICE_DIR + '/kns/src/test/java/org/kuali/rice', includes:'**/*', excludes:'CVS*,.cvs*,**/KualiMaintainableTest*') 
}
ant.copy(todir:PROJECT_PATH + '/src/main/resources') { 
    fileset(dir:RICE_DIR + '/kns/src/test/resources', includes:'**/*', excludes:'CVS*,.cvs*,db/, **/sample-app-test-config.xml') 
}
ant.copy(todir:PROJECT_PATH + '/src/test/resources') { 
    fileset(dir:RICE_DIR + '/kns/src/test/resources', includes:'**/sample-app-test-config.xml') 
}
ant.copy(todir:PROJECT_PATH + '/src/main/config/ddl') { 
    fileset(dir:RICE_DIR + '/kns/src/main/config/ddl', includes:'**/*', excludes:'CVS*,.cvs*') 
}
ant.copy(todir:PROJECT_PATH + '/src/main/config/sql') { 
    fileset(dir:RICE_DIR + '/kns/src/main/config/sql', includes:'**/*', excludes:'CVS*,.cvs*') 
}
ant.copy(todir:PROJECT_PATH + '/src/main/config/sql') { 
    fileset(dir:RICE_DIR + '/kew/src/main/config/sql', includes:'**/*', excludes:'CVS*,.cvs*') 
}
ant.copy(todir:PROJECT_PATH + '/src/main/config/xml') { 
    fileset(dir:RICE_DIR + '/kns/src/main/config/xml', includes:'**/*', excludes:'CVS*,.cvs*') 
}
ant.copy(todir:PROJECT_PATH + '/src/main/webapp') { 
    fileset(dir:RICE_DIR + '/kns/src/test/webapp', includes:'**/*', excludes:'CVS*,.cvs*') 
}
ant.copy(todir:"${System.getProperty('user.home')}/kuali/main/dev") { 
    fileset(dir:RICE_DIR + '/security', includes:'ricekeystore', excludes:'CVS*,.cvs*') 
}
ant.copy(todir:"${System.getProperty('user.home')}/kuali/test/dev") { 
    fileset(dir:RICE_DIR + '/security', includes:'ricekeystore', excludes:'CVS*,.cvs*') 
}
ant.delete(dir:PROJECT_PATH + '/src/main/resources/org')

pom = new File(PROJECT_PATH + '/pom.xml')
pom << pomtext()

classpath = new File(PROJECT_PATH + '/.classpath')
classpath << classpathtext()

project = new File(PROJECT_PATH + '/.project')
project << projecttext()

launch = new File(PROJECT_PATH + '/Launch Web App.launch')
launch << launchtext()

config = new File("${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME.toLowerCase()}-config.xml")
config << userhomeconfigtext()
config = new File("${System.getProperty('user.home')}/kuali/test/dev/${PROJECT_NAME.toLowerCase()}-test-config.xml")
config << userhometestconfigtext()

config = new File(PROJECT_PATH + '/src/main/resources/META-INF/sample-app-config.xml')
configtext = ""
config.eachLine { line -> configtext += line.replace('sample-app', "${PROJECT_NAME.toLowerCase()}") + "\n" }
config.delete()
config = new File(PROJECT_PATH + "/src/main/resources/META-INF/${PROJECT_NAME.toLowerCase()}-config.xml")
config << configtext
	
config = new File(PROJECT_PATH + '/src/test/resources/META-INF/sample-app-test-config.xml')
configtext = ""
config.eachLine { line -> configtext += line.replace('sample-app', "${PROJECT_NAME.toLowerCase()}") + "\n" }
config.delete()
config = new File(PROJECT_PATH + "/src/test/resources/META-INF/${PROJECT_NAME.toLowerCase()}-test-config.xml")
config << configtext

spring = new File(PROJECT_PATH + '/src/main/resources/SpringBeans.xml')
springtext = ""
spring.eachLine { line -> springtext += line.replace('sample-app', "${PROJECT_NAME.toLowerCase()}") + "\n" }
spring.delete()
spring = new File(PROJECT_PATH + "/src/main/resources/SpringBeans.xml")
spring << springtext

index = new File(PROJECT_PATH + '/src/main/webapp/index.html')
indextext = ""
index.eachLine { 
    line -> 
	    line = line.replace('kr-dev', "${PROJECT_NAME.toLowerCase()}-dev")
	    line = line.replace('-dev/portal.do', "-dev/index.html")
    	indextext += line + "\n" 
}
index.delete()
index = new File(PROJECT_PATH + "/src/main/webapp/index.html")
index << indextext

ingest = new File(PROJECT_PATH + '/src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml')
ingesttext = ""
ingest.eachLine { line -> ingesttext += line.replace('kr-dev', "${PROJECT_NAME.toLowerCase()}-dev") + "\n" }
ingest.delete()
ingest = new File(PROJECT_PATH + "/src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml")
ingest << ingesttext

testbase = new File(PROJECT_PATH + '/src/test/java/org/kuali/rice/TestBase.java')
testbasetext = ""
testbase.eachLine { 
    line -> 
		line = line.replace('return "kns"', 'return ""')
		line = line.replace('"/src/test/webapp"', '"/src/main/webapp"')
		line = line.replace('sample-app', "${PROJECT_NAME.toLowerCase()}")
    	testbasetext += line + "\n" 
}
testbase.delete()
testbase = new File(PROJECT_PATH + "/src/test/java/org/kuali/rice/TestBase.java")
testbase << testbasetext

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


def launchtext() {
"""<?xml version="1.0" encoding="UTF-8"?>
<launchConfiguration type="org.eclipse.jdt.launching.localJavaApplication">
<stringAttribute key="org.eclipse.jdt.launching.MAIN_TYPE" value="org.kuali.rice.web.jetty.JettyServer"/>
<stringAttribute key="org.eclipse.jdt.launching.PROGRAM_ARGUMENTS" value="8080 &quot;/${PROJECT_NAME.toLowerCase()}-dev&quot; &quot;/src/main/webapp&quot;"/>
<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_TYPES">
<listEntry value="4"/>
</listAttribute>
<stringAttribute key="org.eclipse.jdt.launching.PROJECT_ATTR" value="${PROJECT_NAME}"/>
<stringAttribute key="org.eclipse.jdt.launching.VM_ARGUMENTS" value="-Xmx256M"/>
<listAttribute key="org.eclipse.debug.ui.favoriteGroups">
<listEntry value="org.eclipse.debug.ui.launchGroup.run"/>
<listEntry value="org.eclipse.debug.ui.launchGroup.debug"/>
</listAttribute>
<listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_PATHS">
<listEntry value="/${PROJECT_NAME}"/>
</listAttribute>
<booleanAttribute key="org.eclipse.debug.core.appendEnvironmentVariables" value="true"/>
</launchConfiguration>"""
}

def userhomeconfigtext() {
'''<config>
	<!-- App specific parameters -->
	<param name="app.code">''' + PROJECT_NAME.toLowerCase() + '''</param>
	<param name="app.context.name">${app.code}-${environment}</param>
	<param name="production.environment.code">PRD</param>
	<param name="externalizable.help.url">/${app.context.name}/kr/static/help/</param>
	<param name="externalizable.images.url">/${app.context.name}/kr/static/images/</param>
	<param name="kr.externalizable.images.url">/${app.context.name}/kr/static/images/</param>
	<param name="travel.externalizable.images.url">/${app.context.name}/static/images/</param>
	<param name="kr.url">/${app.context.name}/kr</param>
	<param name="attachments.pending.directory">${java.io.tmpdir}</param>
	<param name="attachments.directory">${java.io.tmpdir}</param>

	<!-- Misc parameters -->
	<param name="institution">iu</param>
	<param name="version">03/19/2007 01:59 PM</param>
	<param name="security.directory">/usr/local/rice/</param>
	<param name="settings.directory">/usr/local/rice/</param>

	<param name="transaction.timeout">3600</param>
	<param name="memory.monitor.threshold">.85</param>
	<param name="production.environment.code">prd</param>
	<param name="maintain.users.locally">true</param>

    <param name="rice.user">true</param>

	<param name="log4j.reload.minutes">5</param>
	<param name="log4j.settings.path">classpath:log4j.properties</param>

	<param name="workflow.url">http://localhost:8080/${app.context.name}/en</param>
	<param name="application.url">http://localhost:8080/${app.context.name}</param>
	<param name="serviceServletUrl">http://localhost:8080/${app.context.name}/remoting/</param>

	<param name="datasource.url"></param>
	<param name="datasource.username"></param>
	<param name="datasource.password"></param>
	<param name="datasource.ojb.platform">Oracle9i</param>
	<param name="datasource.platform">edu.iu.uis.eden.database.platform.OraclePlatform</param>
	<param name="datasource.driver.name">oracle.jdbc.driver.OracleDriver</param>
	<param name="datasource.pool.validationQuery">select 1 from dual</param>
	<param name="datasource.pool.maxWait">30000</param>
	<param name="datasource.pool.minSize">0</param>
	<param name="datasource.pool.maxSize">50</param>

	<param name="keystore.alias">rice</param>
	<param name="keystore.location">''' + System.getProperty('user.home') + '''/kuali/main/dev/ricekeystore</param>
	<param name="keystore.password">r1c3pw</param>

	<param name="dev.mode">true</param>
	<param name="message.persistence">false</param>
	<param name="message.delivery">asynchronous</param>
	<param name="useQuartzDatabase">false</param>
	<param name="Routing.ImmediateExceptionRouting">true</param>
    <param name="bam.enabled">false</param>

	<!-- XML ingester directories -->
	<param name="data.xml.root.location">/opt/ears/${environment}/en/xml</param>
	<param name="data.xml.pending.location">${data.xml.root.location}pending</param>
	<param name="data.xml.loaded.location">${data.xml.root.location}loaded</param>
	<param name="data.xml.problem.location">${data.xml.root.location}problem</param>
	<param name="attachment.dir.location">/opt/ears/</param>
	<param name="data.xml.pollIntervalSecs">30</param>
	<param name="initialDelaySecs">10</param>
	<param name="workgroup.url">Workgroup.do</param>
	<param name="workgroup.report.url">Workgroup.do</param>
	<param name="user.url">WorkflowUser.do</param>
	<param name="user.report.url">WorkflowUserReport.do</param>

	<!-- Kuali parameters -->
	<param name="mail.relay.server">mail.relay.server</param>
	<param name="mailing.list.batch">mailing.list.batch</param>
	<param name="encryption.key">7IC64w6ksLU</param>
	<param name="kfsLocator.useAppContext">true</param>
	<param name="production.environment.code">prd</param>

    <!-- Ken stuffs... -->
    <!-- stock notification system configuration properties -->

    <!-- Quartz Jobs (MS = Milli Seconds) -->
    <param name="notification.resolveMessageDeliveriesJob.startDelayMS">5000</param>
    <param name="notification.resolveMessageDeliveriesJob.intervalMS">10000</param>

    <param name="notification.processUndeliveredJob.startDelayMS">10000</param>
    <param name="notification.processUndeliveredJob.intervalMS">10000</param>

    <param name="notification.processAutoRemovalJob.startDelayMS">60000</param>
    <param name="notification.processAutoRemovalJob.intervalMS">60000</param>

    <param name="notification.quartz.autostartup">true</param>
    <param name="notification.concurrent.jobs">true</param>

    <param name="notification.basewebappurl">http://localhost:8080/kr-dev/ken</param>
    
    <param name="notification.ojb.platform">Oracle</param>
    
    <!-- Email Plugin Properties -->
    <param name="emailDeliverer.smtp.host">no_such_smtp_host</param>
    

    <param name="css.files">kr/css/kuali.css</param>
    <param name="javascript.files">kr/scripts/core.js,kr/scripts/dhtml.js,kr/scripts/documents.js,kr/scripts/my_common.js,kr/scripts/objectInfo.js</param>
</config>'''	
}

def userhometestconfigtext() {
'''<config>
	<!-- App specific parameters -->
	<param name="app.code">''' + PROJECT_NAME.toLowerCase() + '''</param>
	<param name="app.context.name">${app.code}-${environment}</param>
	<param name="production.environment.code">PRD</param>
	<param name="externalizable.help.url">/${app.context.name}/kr/static/help/</param>
	<param name="externalizable.images.url">/${app.context.name}/kr/static/images/</param>
	<param name="kr.externalizable.images.url">/${app.context.name}/kr/static/images/</param>
	<param name="travel.externalizable.images.url">/${app.context.name}/static/images/</param>
	<param name="kr.url">/${app.context.name}/kr</param>
	<param name="use.clearDatabaseLifecycle">true</param>
	<param name="use.kewXmlmlDataLoaderLifecycle">true</param>
	<param name="use.sqlDataLoaderLifecycle">true</param>
	<param name="test.mode">true</param>
	<param name="attachments.pending.directory">${java.io.tmpdir}</param>
	<param name="attachments.directory">${java.io.tmpdir}</param>

	<!-- Misc parameters -->
	<param name="institution">iu</param>
	<param name="version">03/19/2007 01:59 PM</param>
	<param name="security.directory">/usr/local/rice/</param>
	<param name="settings.directory">/usr/local/rice/</param>

	<param name="transaction.timeout">3600</param>
	<param name="memory.monitor.threshold">.85</param>
	<param name="production.environment.code">prd</param>
	<param name="maintain.users.locally">true</param>

    <param name="kns.test.port">9916</param>

    <param name="rice.user">true</param>

	<param name="log4j.reload.minutes">5</param>
	<param name="log4j.settings.path">classpath:log4j.properties</param>

	<param name="workflow.url">http://localhost:9912/${app.context.name}/en</param>
	<param name="application.url">http://localhost:9912/${app.context.name}</param>
	<param name="serviceServletUrl">http://localhost:9912/${app.context.name}/remoting/</param>

	<param name="datasource.url"></param>
	<param name="datasource.username"></param>
	<param name="datasource.password"></param>
	<param name="datasource.ojb.platform">Oracle9i</param>
	<param name="datasource.platform">edu.iu.uis.eden.database.platform.OraclePlatform</param>
	<param name="datasource.driver.name">oracle.jdbc.driver.OracleDriver</param>
	<param name="datasource.pool.validationQuery">select 1 from dual</param>
	<param name="datasource.pool.maxWait">30000</param>
	<param name="datasource.pool.minSize">0</param>
	<param name="datasource.pool.maxSize">50</param>

	<param name="keystore.alias">rice</param>
	<param name="keystore.location">''' + System.getProperty('user.home') + '''/kuali/test/dev/ricekeystore</param>
	<param name="keystore.password">r1c3pw</param>

	<param name="dev.mode">true</param>
	<param name="message.persistence">false</param>
	<param name="message.delivery">asynchronous</param>
	<param name="useQuartzDatabase">false</param>
	<param name="Routing.ImmediateExceptionRouting">true</param>
    <param name="bam.enabled">false</param>

	<!-- XML ingester directories -->
	<param name="data.xml.root.location">/opt/ears/${environment}/en/xml</param>
	<param name="data.xml.pending.location">${data.xml.root.location}pending</param>
	<param name="data.xml.loaded.location">${data.xml.root.location}loaded</param>
	<param name="data.xml.problem.location">${data.xml.root.location}problem</param>
	<param name="attachment.dir.location">/opt/ears/</param>
	<param name="data.xml.pollIntervalSecs">30</param>
	<param name="initialDelaySecs">10</param>
	<param name="workgroup.url">Workgroup.do</param>
	<param name="workgroup.report.url">Workgroup.do</param>
	<param name="user.url">WorkflowUser.do</param>
	<param name="user.report.url">WorkflowUserReport.do</param>

	<!-- Kuali parameters -->
	<param name="mail.relay.server">mail.relay.server</param>
	<param name="mailing.list.batch">mailing.list.batch</param>
	<param name="encryption.key">7IC64w6ksLU</param>
	<param name="kfsLocator.useAppContext">true</param>
	<param name="production.environment.code">prd</param>

    <!-- Ken stuffs... -->
    <!-- stock notification system configuration properties -->

    <!-- Quartz Jobs (MS = Milli Seconds) -->
    <param name="notification.resolveMessageDeliveriesJob.startDelayMS">5000</param>
    <param name="notification.resolveMessageDeliveriesJob.intervalMS">10000</param>

    <param name="notification.processUndeliveredJob.startDelayMS">10000</param>
    <param name="notification.processUndeliveredJob.intervalMS">10000</param>

    <param name="notification.processAutoRemovalJob.startDelayMS">60000</param>
    <param name="notification.processAutoRemovalJob.intervalMS">60000</param>

    <param name="notification.quartz.autostartup">true</param>
    <param name="notification.concurrent.jobs">true</param>

    <param name="notification.basewebappurl">http://localhost:8080/kr-dev/ken</param>
    
    <param name="notification.ojb.platform">Oracle</param>
    
    <!-- Email Plugin Properties -->
    <param name="emailDeliverer.smtp.host">no_such_smtp_host</param>
    

    <param name="css.files">kr/css/kuali.css</param>
    <param name="javascript.files">kr/scripts/core.js,kr/scripts/dhtml.js,kr/scripts/documents.js,kr/scripts/my_common.js,kr/scripts/objectInfo.js</param>
</config>'''	
}

def instructionstext() {
"""
==================================================================
        Instructions to complete Rice Template Install
==================================================================
1. Import ${PROJECT_PATH} as an 'existing' eclipse project.
2. Update ${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME.toLowerCase()}-config.xml 
   with application runtime database information.
3. Update ${System.getProperty('user.home')}/kuali/test/dev/${PROJECT_NAME.toLowerCase()}-test-config.xml 
   with unit testing database information.
4. Run the /src/main/config/sql/rice_db_bootstrap.sql file if you 
   have a fresh database.  If you need to drop all tables first, run
   the /src/main/config/sql/rice_db_destroy.sql followed by the former.
5. Start the application using the eclipse launch configuration.
   In the eclipse Run menu, choose 'Run...' and select the the
   configuration named 'Launch Web App'
6. Open a brower to http://localhost:8080/${PROJECT_NAME.toLowerCase()}-dev/index.html
7. Finish bootstrapping by ingesting the workflow xml.  From the 
   start screen, go to the workflow portal and log in as quickstart.
   Go to the 'XML Ingester' link and ingest the following file: 
   /src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml
"""
}

def projecttext() {
"""<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
	<name>${PROJECT_NAME}</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
		<buildCommand>
			<name>org.eclipse.jdt.core.javabuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
		<buildCommand>
			<name>com.ibm.sse.model.structuredbuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
		<buildCommand>
			<name>org.maven.ide.eclipse.maven2Builder</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
		<nature>org.eclipse.jdt.core.javanature</nature>
		<nature>org.maven.ide.eclipse.maven2Nature</nature>
	</natures>
</projectDescription>"""	
}

def classpathtext() {
"""<?xml version="1.0" encoding="UTF-8"?>
<classpath>
	<classpathentry kind="src" path="src/main/java"/>
	<classpathentry kind="src" path="src/main/resources"/>
	<classpathentry kind="src" path="src/test/java"/>
	<classpathentry kind="src" path="src/test/resources"/>
	<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
	<classpathentry kind="con" path="org.maven.ide.eclipse.MAVEN2_CLASSPATH_CONTAINER"/>
	<classpathentry kind="output" path="target/classes"/>
</classpath>"""	
}

def pomtext() {
"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  	<modelVersion>4.0.0</modelVersion>
	<groupId>${PROJECT_NAME.toLowerCase()}-group-id</groupId>
	<artifactId>${PROJECT_NAME.toLowerCase()}-artifact-id</artifactId>
	<packaging>war</packaging>
	<name>${PROJECT_NAME.toLowerCase()}</name>
	<version>SNAPSHOT-0.1</version>
	<url>http://kuali.org</url>
	<profiles>
		<profile>
			<id>war</id>
			<activation>
				<property>
					<name>war</name>
					<value>true</value>
				</property>
			</activation>
	<build>
		<plugins>
			<plugin>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<source>1.5</source>
					<target>1.5</target>
				</configuration>
			</plugin>
				</plugins>
			</build>
			<dependencies>
				<dependency>
					<groupId>javax.servlet</groupId>
					<artifactId>servlet-api</artifactId>
					<version>2.4</version>
					<scope>provided</scope>
				</dependency>
				<dependency>
					<groupId>javax.servlet</groupId>
					<artifactId>servlet-api</artifactId>
					<version>2.3</version>
					<scope>provided</scope>
				</dependency>
				<dependency>
					<groupId>org.mortbay.jetty</groupId>
					<artifactId>jetty</artifactId>
					<version>6.1.1</version>
					<scope>provided</scope>
				</dependency>
				<dependency>
					<groupId>tomcat</groupId>
					<artifactId>jasper-compiler</artifactId>
					<version>5.5.15</version>
					<scope>provided</scope>
				</dependency>
				<dependency>
					<groupId>tomcat</groupId>
					<artifactId>jasper-runtime</artifactId>
					<version>5.5.15</version>
					<scope>provided</scope>
				</dependency>
				<dependency>
					<groupId>tomcat</groupId>
					<artifactId>jasper-compiler-jdt</artifactId>
					<version>5.5.15</version>
					<scope>provided</scope>
				</dependency>
				<dependency>
					<groupId>org.apache.activemq</groupId>
					<artifactId>activemq-core</artifactId>
					<version>4.1.1</version>
					<scope>provided</scope>
				</dependency>
				<dependency>
					<groupId>org.apache.derby</groupId>
					<artifactId>derby</artifactId>
					<version>10.2.2.0</version>
					<scope>provided</scope>
				</dependency>
				<dependency>
					<groupId>org.kuali.rice</groupId>
					<artifactId>rice-kns</artifactId>
					<version>0.9.1</version>
					<exclusions>
						<exclusion>
							<groupId>junit</groupId>
							<artifactId>junit</artifactId>
						</exclusion>
						<exclusion>
							<groupId>htmlunit</groupId>
							<artifactId>htmlunit</artifactId>
						</exclusion>
						<exclusion>
							<groupId>jmock</groupId>
							<artifactId>jmock</artifactId>
						</exclusion>
						<exclusion>
							<groupId>nekohtml</groupId>
							<artifactId>nekohtml</artifactId>
						</exclusion>
						<exclusion>
							<groupId>org.apache.activemq</groupId>
							<artifactId>activemq-core</artifactId>
						</exclusion>
						<exclusion>
							<groupId>org.apache.derby</groupId>
							<artifactId>derby</artifactId>
						</exclusion>
						<exclusion>
							<groupId>org.springframework</groupId>
							<artifactId>spring-mock</artifactId>
						</exclusion>
					</exclusions>
				</dependency>
			</dependencies>
		</profile>
		<profile>
			<id>default</id>
			<activation>
				<activeByDefault>true</activeByDefault>
			</activation>
			<build>
				<plugins>
			<plugin>
						<artifactId>maven-compiler-plugin</artifactId>
						<configuration>
							<source>1.5</source>
							<target>1.5</target>
						</configuration>
					</plugin>
					<plugin>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>2.3</version>
				<configuration>
					<useFile>false</useFile>
					<testFailureIgnore>true</testFailureIgnore>
					<includes>
						<include>**/*Test.java</include>
					</includes>
					<forkMode>once</forkMode>
					<reportFormat>plain</reportFormat>
				</configuration>
			</plugin>
		</plugins>
	</build>
			<dependencies>
				<dependency>
					<groupId>org.kuali.rice</groupId>
					<artifactId>rice-kns</artifactId>
					<version>0.9.1</version>
				</dependency>
			</dependencies>
		</profile>
	</profiles>
	<repositories>
		<repository>
			<id>connector</id>
			<name>Connector</name>
			<url>http://julien.dubois.free.fr/maven2/</url>
		</repository>
		<repository>
			<id>kuali</id>
			<name>Kuali Repository</name>
			<url>https://test.kuali.org/maven</url>
		</repository>
	</repositories>
	<reporting>
		<plugins>
			<plugin>
				<artifactId>maven-surefire-report-plugin</artifactId>
			</plugin>
		</plugins>
	</reporting>
</project>"""
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
    1) ${System.getProperty('user.home')}/kuali/main/dev/${PROJECT_NAME.toLowerCase()}-config.xml
    2) ${System.getProperty('user.home')}/kuali/test/dev/${PROJECT_NAME.toLowerCase()}-test-config.xml
    3) ${System.getProperty('user.home')}/kuali/main/dev/ricekeystore
    4) ${System.getProperty('user.home')}/kuali/test/dev/ricekeystore

If this is not what you want, please supply more information:
    usage: groovy createproject -name PROJECT_NAME [-pdir PROJECT_DIR] [-rdir RICE_DIR]

Do you want to continue (yes/no)?"""
}
