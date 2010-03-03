The re-factor work break the big scripts into 8 modules, they work fine as scripts, but they have not 
been converted into groovy(plugin 2.0 style) project yet, so if you import them into a groovy project, it may have errors

List of modules:

ojb2jpa.groovy, this is the main script, with all setups(the same as the old script) and global resource locator. 

jpa_makeup.groovy, major help script, for cleaning up existing annotations before generating BOs and adding @transient after generating BOs  

AnnoationHandler.groovy, major script with annotation function, handles all annotation details.

ConversionUtils.groovy, utility functions for conversion. 

MetaDataHandler.groovy, is the function to load OJB meta data into memory

CustomerTypeHanlder.groovy, handles annotation of all type converters  

MySQLHandler.groovy, handles mySQL sequence and ORM file(optional) 

PersistenceFleHandler.groovy: handles creating persistence.xml 

main script: ojb2jpa.groovy
	configuration: 
		1, change "run option properties" to config what you want to do
		2, change "File Path Properties" to locate source files you want to convert
		3, change "persistence detail properties" to config output options
		
    usage: >groovy -cp ./scripts/jpaconversion ./scripts/jpaconversion/ojb2jpa.groovy
    
help script: jpa_makeup.groovy(for clean existing annotations and add @TRANSIENT)
  
    usage: groovy -cp ./scripts/jpaconversion ./scripts/jpaconversion/jpa_makeup.groovy [CLEAN | TRANSIENT]
    
If you want to run it in a batch, the order is:

groovy -cp ./scripts/jpaconversion ./scripts/jpaconversion/jpa_makeup.groovy CLEAN
groovy -cp ./scripts/jpaconversion ./scripts/jpaconversion/ojb2jpa.groovy
groovy -cp ./scripts/jpaconversion ./scripts/jpaconversion/jpa_makeup.groovy TRANSIENT

************************Detailed description about the conversion scripts*************************************
The ojb2jpa.groovy script performs some conversion tasks to help 
convert your Rice client from OJB persistence model to JPA
The scripts perform the following tasks:
- generates a persistence.xml file in the META-INF directory
- generates the JPA annotated BO POJO source files
- generates composite primary key class source files 
- generates mysql sql scripts for creating sequence generation tables
- generates orm.xml files for mysql sequence annotation overrides 
- clean up existing annotations from BOs

Note: The generated files have not been verified to be correct.
They are intended to provide a head start in converting from OJB to JPA

/*** Properties Local to your local environment ***
 ***    YOU NEED TO CHANGE THESE OPTIONS        ***
 ***    GROUPED IN 3 SECTIONS                   ***/
 /**********************************************************************************************/
 /*1,  run option properties , changing these props will decide what tasks to perform          */
  /**********************************************************************************************/
persistenceXml = false,     /*true means the script with create persistence.xml file*/  
mysql = false               /*true means the script will generate MySQL sql scripts for creating sequence generation tables*/
pkClasses = false           /*true means the script will generate composite primary key class source files */
createBOs = true            /*true means the script will generates the JPA annotated BO POJO source files*/
scanForConfigFiles = false  /*true means the script will search the resource repository for the OJB config files*/
                            /*false means you will provide the path of the OJB config file(s) */ 
clean = false               /*true means the script will clean up the backup files 
dry = false                 /*true means performing a dry run, output to the console, do not actually create the files. */
verbose = false             /*true means printing debugging message to console*/  
 
/**********************************************************************************************/
/* 2, File Path Properties, changing them will help to locate the ojb config files and source */
/*    code you want to convert, also it will config the outupt file path and names            */
/**********************************************************************************************/
projHome = '/java/projects/play/rice-1.1.0'      /*project root*/
ojbMappingPattern = ~/.*OJB|ojb.*-.*xml/         /*OJB config name pattern*/											  
srcRootDir = '/impl/src/main/java/'              /*java bo source file dir root*/
resourceDir = '/impl/src/main/resources/'        /*OJB config file root dir*/

/* if you know the path of the OJB config files, it is recommended to put it here, this will save some time 
   from searching them */
   
def repositories = [
		'/java/projects/play/rice-1.1.0/impl/src/main/resources/org/kuali/rice/ken/config/OJB-repository-ken.xml'
		]
def sourceDirectories = [
		'/impl/src/main/java/'
		]
		
metaInf = 'META-INF/'                            /*resourceDir + metaInf is the output dir for*/ 
                                                 /*persistence.xml and orm.xml*/
                                                 
persistenceXmlFilename = 'persistence.xml'	
mysqlScriptFile = '/scripts/upgrades/mysqlSequenceFix.sql'
mysqlOrmFile = 'project-orm.xml'

/**********************************************************************************************/
/* other misc. config properties
/**********************************************************************************************/
def persistenceUnitName = "rice"                     /*persistence unit name*/
def schemaName = "RICE110DEV"                        /*the schema name of rice or client database*/
def backupExtension = ".backup"                      /*backup file ext*/
def logger = new Logger("errors.log")                /*define log file, it contains important information about
                                                       which class you need to convert manually */

/*************  Dependencies *********************/
NOTE:  For the generated code with JPA2.0 features(such as CASCADETYPE.REFRESH) and dependencies on rice-1.1.x(such as composite 
primary key id classes need CompositePrimaryKeyBase from 1.1 ), you will need update the class path before making them compiled.

/************* newly supported features **********/ 
1, modify auto-xxx and proxy translation according to KULRICE-3882
2, handle generated sequence using sequence
3, join column referencing with multi columns 
4, add constructor with parameters for CPK class
5,  





