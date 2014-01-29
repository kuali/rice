/**
 * Copyright 2005-2014 The Kuali Foundation
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
project {
    
    // Directories which will be added to the classpath for the purpose of finding the OJB-mapped classes 
    classpathDirectories = [
          "<your app exploded war>/WEB-INF/classes"
        ]
    
    // Directories which contain jar files.  Each jar file will be added to the classpath
    // This should be set to the lib directory of your project so that all referenced libraries
    // will be present on the classpath.
    classpathJarDirectories = [
        "<your app exploded war>/WEB-INF/lib"
        ]

    // Set this to true to make the application dump the resulting class files to the console instead of updating the existing files
    // This can be set to true if you want to make sure that you are not updating anything until this file is changed.
    // This option is set to true if you use the -d/--dryrun parameter of the script. 
    dryrun = false    
    
    // If this property is set, it will skip all file updates and only report the messages which would appear 
    // during the processing of the files.  Useful for pre-testing the conversion to make any needed changes 
    // to the OJB descriptors or class files before running the conversion.
    // Also can alert you to places where you may need to make manual modifications after the conversion.    
    errorsonly = false
    
    // Set this property to true if you want to wipe out all existing JPA annotations on target classes
    // Can also be set to true by the --replace script parameter.
    replaceExistingAnnotations = false

    // Set this property to true if you want to automatically upper case all database names such as column, table, and sequence names
    upperCaseDbArtifactNames = true
    
    // Project source directories.  These directories will be scanned in order to find the source
    // files for the classes which need JPA annotation.  The first matching java file found in these 
    // directories will be the one updated.
    sourceDirectories = [
          "<your app project>/src/main/java"
        ]
}
ojb {
    repositoryFiles = [
          "<your app project>/src/main/resouces/<path to OJB mapping file>"
        ]
    // Mappings between OJB and JPA Type converters.  This contains the base ones known to the project teams.
    // If you have any additional ones you want auto-converted, add them to this map.
    // If the JPA converter value is blank, no converter will be added.  This assumes that there is
    // a default converter in place.
    converterMappings = [
          "OjbCharBooleanConversion" : "org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter"
        , "OjbCharBooleanConversionTF" : "org.kuali.rice.krad.data.jpa.converters.BooleanTFConverter"
        , "OjbKualiDecimalFieldConversion" : ""
        , "OjbKualiIntegerFieldConversion" : ""
        , "OjbKualiPercentFieldConversion" : ""
        , "OjbKualiEncryptDecryptFieldConversion" : "org.kuali.rice.krad.data.jpa.converters.EncryptionConverter"
        , "OjbKualiHashFieldConversion" : "org.kuali.rice.krad.data.jpa.converters.HashConverter"
        , "OjbAccountActiveIndicatorConversion" : "org.kuali.rice.krad.data.jpa.converters.InverseBooleanYNConverter"
        , "OjbPendingBCAppointmentFundingActiveIndicatorConversion" : "org.kuali.rice.krad.data.jpa.converters.InverseBooleanYNConverter"
        , "OjbCharBooleanFieldInverseConversion" : "org.kuali.rice.krad.data.jpa.converters.InverseBooleanYNConverter"
        , "OjbBCPositionActiveIndicatorConversion" : "org.kuali.rice.krad.data.jpa.converters.BooleanAIConverter"
        , "OjbCharBooleanFieldAIConversion" : "org.kuali.rice.krad.data.jpa.converters.BooleanAIConverter"

        ]
}
