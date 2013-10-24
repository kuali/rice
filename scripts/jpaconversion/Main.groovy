import org.apache.commons.io.FileUtils;


/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Main driver script for the OJB to JPA conversion


def projectHome = new File("${System.getenv()['PROJECT_HOME']}/rice-20").canonicalPath
def projectSourceBase = new File("$projectHome/rice-middleware/impl/src/main/java").canonicalPath
def ojbRepoFile = new File("$projectHome/rice-middleware/impl/src/main/resources/org/kuali/rice/kcb/config/OJB-repository-kcb.xml").canonicalPath
def backupPath = new File("$projectSourceBase/../backup").canonicalPath

println "Project Home:        $projectHome"
println "Source Base:         $projectSourceBase"
println "Source Backup Path:  $backupPath"
println "OJB File:            $ojbRepoFile"
println ""

FileUtils.forceMkdir( new File( backupPath ) )

def logger = JPAConversionHandlers.bo_log
def classes = [:]
JPAConversionHandlers.metadata_handler.loadMetaData([ojbRepoFile], classes, logger)
println '\nFirst pass completed, metadata captured.'

println "Class Metadata Extracted: \n$classes"


/* TODO:
 * back up files being modified - create bak directory at same location as src
 * Update java files with new annotations
 * 	Skip if already annotated?
 * Generate IdClass if a class with that name does not already exist. 
 */

/*  
	TYPE CONVERTERS - need to update those to current

EXISTING GENERATES THESE - ADJUST TO NEW ANNOTATIONS
	@GeneratedValue(generator="KREN_MSG_S")
	@GenericGenerator(name="KREN_MSG_S", strategy="org.hibernate.id.enhanced.SequenceStyleGenerator", 
		 parameters={@Parameter(name="sequence_name",value="KREN_MSG_S"), 
				 @Parameter(name="value_column",value="id")})

 */

println '\nGenerating Business Object POJOs with JPA Annotations...'
JPAConversionHandlers.annotation_handler.generateJPABO(classes, 
	[projectSourceBase], projectHome, true, true, backupPath, logger, false)


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