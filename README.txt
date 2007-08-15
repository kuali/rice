-- Libraries --

Note that this project DOES NOT contain a "/lib" directory.  This is 
because this project is built and structured using Apache Maven2.  
Apache Maven2 allows for all libraries to be housed remotely in a Maven2 
repository where they are downloaded by local installations of the software 
at build time.  To view a list of all library dependencies that this 
project has, view the <dependencies> section of all "pom.xml" files found
in the project.  Note that not all dependencies are downloaded automatically 
by the project at build time (i.e. Oracle JDBC driver).  These libraries 
must be obtained and manually installed by the local builder. 