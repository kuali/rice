README.txt
Kuali Rice 1.1.0
October 29, 2010


Contents
-----
Preface
Licensing and Licenses
Manifests by Distribution
* Source
* Binary
* Server
References and Links


Preface
-----
This file is included in each of the three types of distributed archives
available from the Kuali Rice website (http://rice.kuali.org/download). Its
contents are generic and do not vary between archives.


Licensing and Licenses
-----
The Kuali Rice team has made every effort to acknowledge the contributions of
third-parties whose libraries are used by, or distributed with, Kuali Rice. All
relevant third-party licenses are included in the Rice binary and server 
distributions (in the /licenses directory) and in the NOTICE.txt file. Due
attribution is also given on the Kuali wiki at
https://test.kuali.org/confluence/x/-ILxCw.


Manifests by Distribution
-----

Source
---
The source distribution is compiled and built using two tools: Ant and Maven,
with the former invoking the latter. Dependencies involved in the compilation, 
testing and running of Rice are downloaded and managed by Maven; they are listed
in the /pom.xml file.

To compile and build the binary and server distributions of Rice, invoke ant
from the command-line using the dist-binary or dist-server target, respectively.
For example:

>ant dist-server

For more information about compiling and building Rice from the source
distribution, consult the Rice documentation (http://rice.kuali.org/docs).

Here is a partial, annotated directory structure for the source distribution:

  /build.xml  <-- the Ant build file for Rice
  /pom.xml  <-- the Maven parent POM  
  /.settings  <-- Eclipse-specific configuration files
  /api  <-- Java interfaces for each of the six Rice modules
  /config/edoclite  <-- eDocLite templates
  /config/templates  <-- templates for XML files used in Rice
  /config/examples <-- example configuration files
  /impl/src/main/java  <-- Java implementations for each of the six Rice modules
  /impl/src/main/resources  <-- auxiliary files used for configuration of Spring
      framework, configuration of ORM, as well as XML schemas
  /impl/src/test  <-- test harness code and accompanying configuration files
  /kcb  <-- Kuali Communications Broker unit tests
  /ken  <-- Kuali Enterprise Notification unit tests
  /kew  <-- Kuali Enterprise Workflow unit tests
  /kim  <-- Kuail Identity Management unit tests
  /kns  <-- Kuali Nervous System unit tests
  /ksb  <-- Kuali Service Bus unit tests
  /web  <-- content, configuration (Struts and Spring), and JSPs for the web 
      interface to Rice
  /licenses  <-- licenses governing the use and redistribution of third-party
      code or libraries used by Rice
  /scripts/datasets  <-- SQL and configuration files for creating distributions
  /scripts/ddl  <-- SQL used to setup a unit test database
  /scripts/launch  <-- file for launching a Jetty-based, standalone version of
      Rice within Eclipse 
  /scripts/upgrades  <-- DDL needed for upgrading databases used with previous
      versions of Rice
  /security  <-- SSL-related files

Binary 
--- 
The binary distribution of Rice contains pre-built libraries for use
in Rice client applications. The third-party libraries Rice depends upon are
included in this distribution, so neither Maven (nor Ant) is needed.

Here is a partial, annotated directory structure for the binary distribution:

  /config  <-- files useful for starting a Rice client project from scratch
  /database/bootstrap-client-dataset  <-- Torque data and schema essential to
      getting a Rice client running  
  /database/bootstrap-server-dataset  <-- Torque data and schema essential to
      getting a Rice standalone server running
  /database/database-impex  <-- the Torque-based import/export tool (Impex) that
      Rice uses to initialize a database (in both source and binary form)
  /database/demo-client-dataset  <-- Torque data and schema for sample client 
      application
  /database/demo-server-dataset  <-- Torque data and schema for sample server
      application
  /lib  <-- the third-party libraries Rice uses
  /licenses  <-- licenses governing the use and redistribution of third-party
      code or libraries used by Rice
  /scripts/datasets  <-- SQL and configuration files for creating distributions
  /scripts/ddl  <-- SQL used to setup a unit test database
  /scripts/launch  <-- file for launching a Jetty-based, standalone version of
      Rice within Eclipse 
  /scripts/upgrades  <-- DDL needed for upgrading databases used with previous
      versions of Rice
  /security  <-- SSL-related files
  /webcontent  <-- content, configuration (Struts and Spring), and JSPs for the 
      web interface to Rice

Server
---
The server distribution of Rice is also referred to as the "standalone" 
distribution. In this distribution, Rice is contained within a web application
archive (WAR) that can be placed within a Java servlet container. 

Here is a partial, annotated directory structure for the server distribution:

  /kr-dev.war  <-- the web application archive that contains Rice
  /config  <-- files useful for starting a Rice client project from scratch
  /database/bootstrap-server-dataset  <-- Torque data and schema essential to
      getting a Rice standalone server running
  /database/database-impex  <-- the Torque-based import/export tool (Impex) that
      Rice uses to initialize a database (in both source and binary form)
  /database/demo-server-dataset  <-- Torque data and schema for sample server
      application
  /licenses <-- licenses governing the use and redistribution of third-party
      code or libraries used by Rice
  /scripts/dataset  <-- SQL and configuration files for creating distributions
  /scripts/ddl  <-- SQL used to setup a unit test database
  /scripts/launch  <-- file for launching a Jetty-based, standalone version of
      Rice within Eclipse 
  /scripts/upgrades  <-- DDL needed for upgrading databases used with previous
      versions of Rice
  /security  <-- SSL-related files


References and Links
----- 
Rice documentation: http://rice.kuali.org/docs 
Rice distribution download:  http://rice.kuali.org/download


