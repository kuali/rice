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
/**
 *  uses properties file to load settings, builds scaffolding for project and runs related conversion
 *
 *  script pulls an input and target directory
 *  target directory is wiped and a structure is setup based on a web application maven project
 *  using the struts-config.xml the file is parsed and processed into creating a basic web-overlay project
 *  so the generated code can be tested without mixing with existing source*/
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.StringUtils
import org.kuali.rice.scripts.ConversionUtils
import org.kuali.rice.scripts.DictionaryConverter
import org.kuali.rice.scripts.ScaffoldGenerator
import org.kuali.rice.scripts.StrutsConverter
import org.kuali.rice.scripts.KimPermissionConverter

def configFilePath;
def projectConfigFilePath;
def config;

def inputDir;
def outputDir;
def outputPathList;

def outputResourceDir;
def projectApp;
def templateDir;
def projectArtifact;
def isWarProject;
def projectParent;
def dependencies;

def performDictionaryConversion;
def performStrutsConversion;
def performKimPermissionConversion;
def copyWebXml;
def copyPortalTags;
def includeRiceValidationTest;
def coreXmlFilePathList;
def strutsSearchDirPath;
def ignoreStrutsPattern;

ScaffoldGenerator scaffold;
StrutsConverter struts;
DictionaryConverter dictionary;
KimPermissionConverter kimConverter;

public void init() {

    // load configuration file(s)
    defaultConfigFilePath = "krad.conversion.properties";
    projectConfigFilePath = System.getProperty("alt.config.location");
    config = ConversionUtils.getConfig(defaultConfigFilePath, projectConfigFilePath);

    // load any config-specific members
    inputDir = FilenameUtils.normalize(config.input.dir, true);
    outputDir = FilenameUtils.normalize(config.output.dir, true);
    outputPathList = config.output.path.list;

    outputResourceDir = outputDir + config.output.path.src.resources;
    projectApp = config.project.app;
    templateDir = config.script.path.template;
    projectArtifact = config.project.artifact;
    isWarProject = "war".equals(config.project.artifact.war);
    projectParent = config.project.parent;
    dependencies = config.project.dependencies;

    performDictionaryConversion = config.bool.script.performDictionaryConversion;
    performStrutsConversion = config.bool.script.performStrutsConversion;
    performKimPermissionConversion = config.bool.script.performKimPermissionConversion;
    copyWebXml = config.bool.script.copyWebXml;
    copyPortalTags = config.bool.script.copyPortalTags;
    includeRiceValidationTest = config.bool.script.includeRiceValidationTest;
    coreXmlFilePathList = config.map.scaffold.rdvconfig.additionalCorefiles;
    strutsSearchDirPath = config.input.dir + config.input.path.src.webapp
    ignoreStrutsPattern = config.pattern.script.ignoreStruts;

    // setup all necessary classes
    scaffold = new ScaffoldGenerator(config);
    struts = new StrutsConverter(config);
    dictionary = new DictionaryConverter(config);
    kimConverter = new KimPermissionConverter(config);

}

public void validateEnvironment() {
    if (StringUtils.isBlank(inputDir) || StringUtils.isBlank(outputDir)) {
        System.out.println "Error:\nplease configure your input and output directories before continuing\n\n";
        System.exit(1);
    }

    if (!performDictionaryConversion && !(performStrutsConversion && isWarProject) && !performKimPermissionConversion) {
        System.out.println "Error:\nall conversion bypassed; exiting\n\n";
        System.exit(1);
    }
}

public void performConversion() {

    ConversionUtils.buildDirectoryStructure(outputDir, outputPathList, true);
    ScaffoldGenerator.buildOverlayPom(outputDir, projectApp, projectArtifact, projectParent, dependencies, []);

    if (isWarProject && copyWebXml) {
        System.out.println "Copy web.xml";
        ScaffoldGenerator.copyWebXml(inputDir, outputDir);
    }

    if (isWarProject && copyPortalTags) {
        System.out.println "Copy portal tags";
        ScaffoldGenerator.copyPortalTags(inputDir, outputDir, projectApp);
    }

    if (performDictionaryConversion) {
        System.out.println "Perform dictionary conversion";
        dictionary.convertDataDictionaryFiles();
        def springBeansFileList = ConversionUtils.findFilesByPattern(outputResourceDir, ~/\.xml$/, ~/META-INF/);
        // TODO: build portal tag for all views generated by dictionary conversion
    }

    if (performKimPermissionConversion) {
        System.out.println "Permform KIM Permission conversion";
        kimConverter.convertKimPermissions();
    }

    if (performStrutsConversion) {
        // confirm existence of strut-config files and begin processing
        def strutsConfigFiles;
        if (ignoreStrutsPattern) {
            strutsConfigFiles = ConversionUtils.
                    findFilesByPattern(strutsSearchDirPath, /struts-.*?\.xml$/, ignoreStrutsPattern);
        } else {
            strutsConfigFiles = ConversionUtils.findFilesByPattern(strutsSearchDirPath, /struts-.*?\.xml$/);
        }
        System.out.println "Load struts-config.xml files for processing - dir: " + strutsSearchDirPath +
                " " + strutsConfigFiles?.size();
        // generate spring controllers and other classes
        System.out.println "Generating all necessary spring components (controllers, forms, views) from strutsConverter information"
        strutsConfigFiles.each { strutsConfigFile ->
            def strutsConfig = StrutsConverter.parseStrutsConfig(strutsConfigFile.path);
            struts.generateSpringComponents(strutsConfig);
            // TODO: change portal tag builder to build for all new controllers
            // scaffold.buildPortalTag(strutsConfig)
        }
    }


    // find all spring files and add to a rice validation test (good precursor test)
    def springBeansFileList = ConversionUtils.findFilesByPattern(outputResourceDir, ~/\.xml$/, ~/META-INF/);
    def springBeansFilePathList = [];
    springBeansFileList.each { file -> springBeansFilePathList << file.path }

    // includes a spring validation test to allow for testing before running the server application
    if (includeRiceValidationTest) {
        System.out.println "Generating spring validation test based on resulting output from conversion";
        scaffold.buildSpringBeansValidationTest(outputDir, springBeansFilePathList, coreXmlFilePathList);
    }
}

public void notifyResults() {
    System.out.println " -- Script Complete";
    System.out.println " -- open directory " + outputDir;
    System.out.println " -- prep project -- mvn eclipse:clean eclipse:eclipse generate-resources ";
    System.out.println " -- if using eclipse add target/generate-resources directory as a referenced library " +
            " (Configure -> Build Path -> Library -> Add Class Folder ";
}

public void runScript() {
    init();
    validateEnvironment();
    performConversion();
    notifyResults();
}

runScript();