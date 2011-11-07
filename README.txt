====
    Copyright 2005-2011 The Kuali Foundation

    Licensed under the Educational Community License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.opensource.org/licenses/ecl2.php

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
====

README.txt
Kuali Rice ${project.version}
${kuali.build.timestamp}


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
The source distribution of Rice is compiled and built using Maven.

Dependencies involved in the compilation, testing and running of Rice 
are downloaded and managed by Maven.  They are listed in the /pom.xml file.

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
  /krad <-- Kuali Development Framework unit tests
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

====
The binary and server distributions of Rice, are assembled using 
Maven assembly descriptors
====

Binary 
--- 
The binary distribution of Rice contains pre-built libraries for use
in Rice client applications. The third-party libraries Rice depends upon are
included in this distribution, so neither Maven (nor Ant) is needed.

Here is a partial, annotated directory structure for the binary distribution:

  /config  <-- files useful for starting a Rice client project from scratch
  /db/impex/client/bootstrap  <-- Torque data and schema essential to
      getting a Rice client running  
  /db/impex/server/bootstrap  <-- Torque data and schema essential to
      getting a Rice standalone server running
  /db/impex/client/demo  <-- Torque data and schema for sample client 
      application
  /db/impex/server/demo  <-- Torque data and schema for sample server
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
  /db/impex/server/bootstrap  <-- Torque data and schema essential to
      getting a Rice standalone server running
  /db/impex/server/demo  <-- Torque data and schema for sample server
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


