# Kuali Rice

Kuali Rice is an application development framework and middleware suite developed for and by the higher education community.  Kuali Rice is being used by several Kuali universiy enterprise applications, for its build in eDocLite document routing, and as basis for custom applications.

More info about Kuali Rice at http://www.kuali.org/rice.

## Where to find the official version?

End users can download Kuali Rice at http://www.kuali.org/download/rice

Developers can contribute to Kuali Rice at https://github.com/kuali/rice

## Additional Resources

* Wiki:https://wiki.kuali.org/display/KULRICE/Home
* Issue tracking: https://jira.kuali.org/browse/KULRICE
* Technical help and discussions: rice.collab@kuali.org email list 
  * Archive: https://groups.google.com/a/kuali.org/forum/#!forum/rice.collab

## Contributions

Contributions are welcome. The Rice Project strives for a simple and quick contribution process.  However, there are some specific requirements we need to enforce:
* Please report all security issues to security@kuali.org. 
* It is recommended to contact the Kuali Rice development team before starting work on a pull request.  Send the contribution proposal to the collaboration mailing list (rice.collab@kuali.org). The Kuali Rice team will evaluate the proposal, assign a Jira issue, and respond via email.
* Every contributor must have a Contributor License Agreement (CLA on File with Kuali).  To submit the CLA:
  * Create an account at http://kis.kuali.org/ (details: [Create a KIS account] (https://wiki.kuali.org/display/KULFOUND/Create+a+KIS+account)).
  * Sign the [Contributor License Agreement] (https://docs.google.com/a/kuali.org/file/d/0B98dhdqKa_2hNzk5MzhhODgtYzM2Ni00MmUyLWFlYzYtNmJhMDdkZGM2MTI4/edit), scan and send it to help@kuali.org (details: [License Agreement Process (CLA / CCLA)] (https://wiki.kuali.org/pages/viewpage.action?pageId=313879878)).
* Create a separate pull request for each feature.
* Squash pull request into one commit (see [Squashing Change Log] (https://wiki.kuali.org/pages/viewpage.action?pageId=338976534#Git/GitHubWorkflow,PoliciesandUsage-1.4.5SquashingChangeLog)).
* Prefix each commit message with the Jira issue identifier (e.g. KULRICE-1234).
* Contributions should follow the [Policies and Standards] (https://wiki.kuali.org/display/KULRICE/Policies+and+Standards) of Kuali Rice. 

## Running Rice for Development

### Project and Database Setup

* Install the latest 5.7.x version of MySQL Community Edition
  * Be sure to remember your root password!
* Clone this repository 
* Change directory to `db/impex/master` in directory where you cloned the repository
* Run the following to ensure that you can connect to your mysql instance
  * ```mvn validate -Pdb,mysql -Dimpex.dba.password=[root password]```
  * if you have no root passwords use `NONE` for the password
  * if this does not work, make sure your MySQL database is up and running and try again
  * if you want to tweak the schema name, username, or password, see [](https://wiki.kuali.org/display/KULRICE/Load+Impex+Data+via+Maven)
* Execute the following from the root of your project:
```mvn clean install -Pdb,mysql -Dimpex.dba.password=[root password]```
* You will now have a mysql database created with the name, username, and password of "RICE"

### Configuration

* Copy the `rice.keystore` file from `rice-middleware/security/rice.keystore` to your `/usr/local/rice` directory (create this directory if it does not exist)
* For Kuali Rice standalone you will need a file at `/usr/local/rice/rice-config.xml` in order to configure the database
* Create the file at that location using the following template:
```
<config>
    <param name="appserver.url">http://localhost:8080</param>
    <param name="app.context.name">rice-standalone</param>
    
    <param name="keystore.file">/usr/local/rice/rice.keystore</param>
    <param name="keystore.alias">rice</param>
    <param name="keystore.password">r1c3pw</param>

    <param name="datasource.url">jdbc:mysql://localhost:3306/RICE</param>
    <param name="datasource.username">RICE</param>
    <param name="datasource.password">RICE</param>
    <param name="datasource.driver.name">${datasource.driver.name.MySQL}</param>
    <param name="datasource.pool.minSize">3</param>
    <param name="datasource.pool.maxOpenPreparedStatements">500</param>
    <param name="datasource.platform">${datasource.platform.MySQL}</param>
    <param name="datasource.ojb.platform">MySQL</param>

    <param name="rice.cxf.client.connectionTimeout">0</param>
    <param name="rice.cxf.client.receiveTimeout">20000</param>
    
    <param name="filter.login.class">org.kuali.rice.krad.web.filter.DummyLoginFilter</param>
    <param name="filtermapping.login.1">/*</param>

</config>
```

### Development in Eclipse

* Download and install the latest version of Eclipse for J2EE developers
* Increase the max memory used when launching eclipse from 1024 (the default) to at least 2048.
* Install the Groovy eclipse and m2e connector plugins from the following url in the Eclipse Marketplace: http://dist.springsource.org/snapshot/GRECLIPSE/e4.5/
* Disable all validators globally in Eclipse
* Add an Ant property pointing to your Eclipse workspace
  * Eclipse -> Preferences -> Ant -> Runtime -> Properties -> Add Property
  * Set name as "eclipse.workspace" and value as "${workspace_loc}"
* File -> Import.. -> Existing Maven Projects
* Then browse the folder where you cloned the repo and ensure that all of the various pom.xml modules are selected and click "Finish"
* After approximately one year of waiting, the build should complete and hopefully there will be no errors

### Running Rice Standalone in Eclipse

* In the Servers tab, add a new Server (preferably Tomcat 8)
* Drag-and-drop the "rice-standalone" to the new tomcat server in the servers view, this will publish it
* Because of issues with m2e-wtp plugin (maven and web tools integration), the overlays and filtering on the rice-standalone module don't work :( We have created an Ant target in the root of the project that can be run that will deploy the proper files. It is called "m2e-wtp-deploy-standalone", execute that target via Eclipase and it will copy the appropriate files
* Right-click on the server and start it
* Rice Standalone should (hopefully) start up successfully
* Go to http://localhost:8080/rice-standalone and you should see the Kuali Rice "portal"

### Setting up Dev Environment for Running Integration Tests

* Change directory to `db/impex/master` in directory where you cloned the repository
* Run the following to ensure that you can connect to your mysql instance
  * ```mvn validate -Pdb,mysql -Dimpex.dba.password=[root password]```
  * if you have no root passwords use `NONE` for the password
  * if this does not work, make sure your MySQL database is up and running and try again
* Execute the following from the root of your project:
```mvn clean install -Pdb,mysql,integration-test -Dimpex.dba.password=[root password] -Dimpex.username=RICECI```
* You will now have a mysql database for purposes of continuous integration created with the name, username, and password of "RICECI"
* Next, run "prepare-unit-test-environment" from the Ant build.xml file in the root of the project
  * Note that if you already have a unit test config file in place this will fail
* 

 
   
