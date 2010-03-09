
/* Begin User Configurable Fields */

/* run option properties */
def persistenceXml = false
def mysql = false
def pkClasses = true
def clean = false
def dry = false
def verbose = false
def createBOs = true
def scanForConfigFiles = false

/* File Path Properties */
//for KFS
//def ojbMappingPattern = ~/.*OJB|ojb.*-.*xml/
//def projHome = '/java/projects/kfs-jpa-ref'
//def resourceDir = '/work/src/'

//for rice
def ojbMappingPattern = ~/.*OJB.*repository.*xml/
def projHome = '/java/projects/rice-1.1.0'
def resourceDir = '/impl/src/main/resources/'

def repositories = [
        //for KFS
        //'/java/projects/kfs-jpa-ref/work/src/org/kuali/kfs/coa/ojb-coa.xml',
        //'/java/projects/kfs-jpa-ref/work/src/org/kuali/kfs/fp/ojb-fp.xml'
        //for rice
		'/java/projects/rice-1.1.0/impl/src/main/resources/org/kuali/rice/ken/config/OJB-repository-ken.xml'
        //,'/java/projects/play/rice-1.1.0/impl/src/main/resources/org/kuali/rice/kns/config/OJB-repository-kns.xml'
		]

def sourceDirectories = [
       //for KFS
       //'/work/src/' 
       //for rice                     
	   '/impl/src/main/java/'
		]
def metaInf = 'META-INF/'
def persistenceXmlFilename = 'persistence.xml'	
def mysqlScriptFile = 'mysqlSequenceFix.sql'
def mysqlOrmFile = 'orm.xml'

/*  other misc. config properties */
def persistenceUnitName = "rice"
def schemaName = "RICE110DEV"
def backupExtension = ".backup"
def logger = new Logger("errors.log")
def classes = [:]

//   Search for OJB Mapping Files
if (scanForConfigFiles){
	println 'Scanning for files'
    JPAConversionHandlers.conversion_util.getOJBConfigFiles(projHome, resourceDir, ojbMappingPattern, repositories)
}
println 'Found '+repositories.size().toString()+' OJB mapping files:'
repositories.each {println it}
println 'Convering the following '+sourceDirectories.size().toString()+' Source Directories:'
sourceDirectories.each {println it}

// give the user the opportunity to review and abort.
def actions = 'The script is set to: '
def something
if (dry) actions = 'The script will perform a dry run to: '
if (persistenceXml) {
	actions+=' Generate persistence.xml files.'
}
if (mysql) {
	actions+=' Generate MySQL sequence annotations.'
} 
if (clean) {
	actions+=' Clean up backup file.'
} 
if (createBOs){
	actions+=' Generate JPA annotated BO files.'
}
// Primary Key Classes need to be created AFTER the BO classes
if (pkClasses){
	actions+=' Generate Composite Primary Key Classes.'
}

System.in.withReader { reader ->
println()
println actions
print "Would you like to continue? (Y/N): "
something = reader.readLine()
println()
}


if (something.toString().toUpperCase().equals( 'Y'))
{
	/*
	The first pass over the OJB XML captures all of the metadata in groovy datastructures.
	*/

	JPAConversionHandlers.metadata_handler.loadMetaData(repositories, classes, logger)
	println '\nFirst pass completed, metadata captured.'

	//for persistence.xml
	if (persistenceXml) {
		println '\nGenerating persistence.xml...'
		JPAConversionHandlers.persistence_handler.generatePersistenceXML(classes, persistenceUnitName, persistenceXmlFilename, projHome+resourceDir+metaInf)
	} 

	//for handling sequence in mysql
	if (mysql) {
		println '\nGenerating MySQL sequence scripts and orm files...'
		//JPAConversionHandlers.mysql_handler.generateMySQLSequence(classes, schemaName, projHome, mysqlScriptFile, resourceDir+metaInf+mysqlOrmFile)
		repositories.each {
			repository ->
			    def moduleName = JPAConversionHandlers.conversion_util.stripeModuleName(repository.toString())
				def filePath = resourceDir+metaInf+moduleName+'-'
				def this_classes = [:]
                def this_repository = []
			    this_repository.add(repository)     
			    JPAConversionHandlers.metadata_handler.loadMetaData(this_repository, this_classes, logger)
			    JPAConversionHandlers.mysql_handler.generateMySQLSequence(this_classes, schemaName, projHome, filePath+mysqlScriptFile, filePath+mysqlOrmFile)	
			}
	}

	//clean back up
	if (clean) {
		println '\nCleaning up backup files...'
		JPAConversionHandlers.conversion_util.cleanBackupFiles(classes, sourceDirectories, projHome, backupExtension, logger, verbose)
	} 

	//generate  sounre code for bo in JPA style
	if (createBOs)	{
		println '\nGenerating Business Object POJOs with JPA Annotations...'
		JPAConversionHandlers.annotation_handler.generateJPABO(classes, sourceDirectories, projHome, dry, verbose, backupExtension, logger, false)
	}
	// generate composite primary key classes
	if (pkClasses){
		println '\nGenerating Composite Primary Key Classes...'
		JPAConversionHandlers.annotation_handler.generateJPABO(classes, sourceDirectories, projHome, dry, verbose, backupExtension, logger, pkClasses)
	}
}
else
{
	println 'Aborting script.'
}

/*
This class is simply for logging basic messages.
*/
class Logger {
    def logFile
    def lineNumber = 1

    def Logger() {
        this("errors.log")
    }
    
    def Logger(file) {
        logFile = new File(file)
        if (logFile.exists()) {
            logFile.delete()
        }  
    }
    
    def log(message) {
        logFile << "${lineNumber}) ${message}" 
        logFile << "\n"
        lineNumber++
    }
}

/* 
Below are the class definitions responsible for holding the OJB metedata. 
*/

class Field {
    def id
    def name
    def column
    def jdbcType
    def primarykey
    def nullable
    def indexed
    def autoincrement
    def sequenceName
    def locking
    def conversion    
    def access
}

class Key {
    def fieldRef
    def fieldIdRef
}

class ReferenceDescriptor {
    def name
    def classRef
    def proxy
    def autoRetrieve
    def autoUpdate
    def autoDelete
    def foreignKeys = []
}

class CollectionDescriptor {
    def name
    def collectionClass
    def elementClassRef
    def orderBy
    def sort
    def indirectionTable
    def proxy
    def autoRetrieve
    def autoUpdate
    def autoDelete
    def fkPointingToThisClassColumn
    def fkPointingToElementClassColumn
    def inverseForeignKeys = []    
    def manyToMany
}

class ClassDescriptor {
    def compoundPrimaryKey = false
    def pkClassIdText = ""
    def cpkFilename = ""
    def tableName
    def className
    def primaryKeys = []
    def fields = [:]
    def referenceDescriptors = [:]
    def collectionDescriptors = [:]
}

public class JPAConversionHandlers{
	public static conversion_util = new ConversionUtils();
	public static metadata_handler = new MetaDataHandler();
	public static persistence_handler = new PersistenceFileHandler();
	public static mysql_handler = new MySQLHandler();
	public static type_handler = new CustomerTypeHandler();
	public static annotation_handler = new AnnotationHandler();
	
	public static info_logger = new Logger("info.log");
}