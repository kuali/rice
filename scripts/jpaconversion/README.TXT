The re-factor work break the big scripts into 8 modules, they work fine as scripts, but they have not 
been converted into groovy project yet,  so if you import them into a groovy project, it may have errors

main script: ojb2jpa.groovy
	configuration: 
		1, change "run option properties" to do what you want to do
		2, change "File Path Properties" to decide source files you want to convert
		3, change "persistence detail properties" to decide output
		4, for detail description of configuration, please see ./acchives/ojb2jpa.readme
		
    usage: >groovy -cp ./scripts/jpaconversion ./scripts/jpaconversion/ojb2jpa.groovy
    
help script: jpa_makeup.groovy(for clean existing annotations and add @TRANSIENT)
  
    usage: groovy -cp ./scripts/jpaconversion ./scripts/jpaconversion/jpa_makeup.groovy [CLEAN | TRANSIENT]

************************

The ojb2jpa.groovy script performs some conversion tasks to help 
convert your Rice client from OJB persistence model to JPA
The script performs the following tasks:
- generates a persistence.xml file in the META-INF directory
- generates the JPA annotated BO POJO source files
- generates composite primary key class source files 
- generates mysql sql scripts for creating sequence generation tables
- generates orm.xml files for mysql sequence annotation overrides (not yet implemented)
configuration properties control which tasks to perform. 

Note: The generated files have not been verified to be correct.
They are intended to provide a head start in converting from OJB to JPA


The script can search your project for your existing OJB repository config files,
or you can specify the repository files


/*** Properties Local to your local environment ***
 ***    YOU NEED TO CHANGE THESE                ***
 ***                                            ***
/* project root from which to search for files (all subdirectories will be searched)*/
projHome = 'c:/java/rice/projects'   

// an array of directories under the project to locate java files
def sourceDirectories = ['/impl/src/main/java/']

// the directory where to place the META-INF/persistence.xml file
def resourceDir = '/impl/src/main/resources/'

// the schema name of your rice client database
def schemaName = "RICE110DEV"

persistenceUnitName = "rice"

// a regular expression used to auto-locate your old OJB repository config files
// the default behavior searches for  *OJB*repository.xml  files
// this is only required if scanning for files
ojbMappingPattern = ~/.*OJB.*repository.*xml/   /* regular expression to match OJB mapping files */


***  Script task control parameters ***
***  These properties configure which tasks to perform
***

persistenceXml = true		/* create persistenceXml files */
createBOs = true
pkClasses = false
mysql = false
def scanForConfigFiles = true
dry = true   /* perform a dry run, output to the console, do not actually create the files. */


// other misc. config properties
def verbose = false
def backupExtension = ".backup"
def metainf = 'META-INF/'
verbose = true

/*************  Dependencies *********************/

NOTE:  For the generated composite primary key id classes to compile, you will need the Rice version 1.1 .jar files in your build path.
This is because the generated classes inherit from CompositePrimaryKeyBase which is a new class beginning in version 1.1
