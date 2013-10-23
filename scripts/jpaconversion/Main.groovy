// Main driver script for the OJB to JPA conversion


def projectHome = "${System.getenv()['PROJECT_HOME']}/rice-20"
def projectSourceBase = "$projectHome/rice-middleware/impl/src/main/java"
def ojbRepoFile = "$projectHome/rice-middleware/impl/src/main/resources/org/kuali/rice/kcb/config/OJB-repository-kcb.xml"

println "Project Home: $projectHome"
println "Source Base:  $projectSourceBase"
println "OJB File: $ojbRepoFile"

def logger = JPAConversionHandlers.bo_log
def classes = [:]
JPAConversionHandlers.metadata_handler.loadMetaData([ojbRepoFile], classes, logger)
println '\nFirst pass completed, metadata captured.'

println "Class Metadata Extracted: \n$classes"






public class JPAConversionHandlers {
	public static conversion_util = new ConversionUtils();
	public static metadata_handler = new MetaDataHandler();
	public static persistence_handler = new PersistenceFileHandler();
	public static mysql_handler = new MySQLHandler();
	public static type_handler = new CustomerTypeHandler();
	public static annotation_handler = new AnnotationHandler();

	public static info_log = new Logger("jpa_info.log");
	public static error_log = new Logger("jpa_error.log");

	public static bo_log = new Logger("jpa_bo.log");
	public static cpk_log = new Logger("jpa_cpk.log");

	public static SQL_DATE_PATTERN = ~/Date|Timestamp|(java\.sql\.Date)|(java\.sql\.Timestamp)/;
}