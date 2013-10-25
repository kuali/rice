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
def projectSourceBase = new File("$projectHome/rice-middleware/core-service/impl/src/main/java").canonicalPath
def ojbRepoFile = new File("$projectHome/rice-middleware/core-service/impl/src/main/resources/org/kuali/rice/coreservice/config/OJB-repository-core-service.xml").canonicalPath
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
 * Update java files with new annotations
 * 	Skip if already annotated?
 * Generate IdClass if a class with that name does not already exist. 
 */

/*  
	REFERENCES - It's adding the links on the properties and the classes - need to remove from properties
 */

println '\nGenerating Business Object POJOs with JPA Annotations...'
JPAConversionHandlers.annotation_handler.generateJPABO(classes, 
	[projectSourceBase], projectHome, true, true, backupPath, logger, false)


public class JPAConversionHandlers {
	public static metadata_handler = new MetaDataHandler();
	public static type_handler = new TypeConverterHandler();
	public static annotation_handler = new AnnotationHandler();

	public static info_log = new Logger("jpa_info.log");
	public static error_log = new Logger("jpa_error.log");

	public static bo_log = new Logger("jpa_bo.log");
	public static cpk_log = new Logger("jpa_cpk.log");
}