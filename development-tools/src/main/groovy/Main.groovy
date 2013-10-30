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

import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.ojb.broker.metadata.DescriptorRepository
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.visitor.EntityVisitor

import groovy.util.ConfigSlurper

println "Looking for config.groovy at ${this.class.classLoader.getResource("config.groovy")}"

def config = new ConfigSlurper().parse(this.class.classLoader.getResource("config.groovy"))

println config

BasicConfigurator.configure()

/*
 * NOTE: When running from within Eclipse/IDE, you need to add groovy to the --classpath of the script being run so that
 * Groovy can install its RootLoader classloader - needed for the URL additions below.
 * 
 * In Eclipse (and Groovy 2.1.5), this will be: ${groovy_home}/lib/groovy-all-2.1.5.jar
 */
def rootLoader = this.class.classLoader.getRootLoader()
if ( !rootLoader ) {
    println "ERROR!  RootLoader is null - unable to add additional classes to the classpath.  This happens when running Groovy from within Eclipse."
} else {
    //Thread.currentThread().setContextClassLoader(rootLoader);
}
println "RootLoader: $rootLoader"

// add additional items to the classpath
for ( classpathDir in config.project.classpathDirectories ) {
    def classpathUrl = new URL("file://"+new File( config.project.homeDirectory + "/" + classpathDir ).canonicalPath + "/")
    println "Adding Classpath URL: $classpathUrl"
    rootLoader.addURL( classpathUrl )
}

println rootLoader.getURLs()

//def jars   = jardir.listFiles().findAll { it.name.endsWith('.jar') } 
//jars.each { loader.addURL(it.toURI().toURL()) }
fullSourcePaths = config.project.sourceDirectories.collect { config.project.homeDirectory + "/" + it }
fullOjbPaths = config.ojb.repositoryFiles.collect { config.project.homeDirectory + "/" + it }

println "\n\nSource Directories: \n${fullSourcePaths.join( '\n' )}"

fullSourcePaths.each {
    if ( !new File( it ).exists() ) {
        throw new RuntimeException( "ERROR: $it does not exist.  Aborting.")
    }
}

println "\n\nScanning OJB Files: \n${fullOjbPaths.join( '\n' )}"

fullOjbPaths.each {
    if ( !new File( it ).exists() ) {
        throw new RuntimeException( "ERROR: $it does not exist.  Aborting.")
    }
}


Collection<DescriptorRepository> drs = OjbUtil.getDescriptorRepositories( fullOjbPaths );
Collection<String> ojbMappedClasses = OjbUtil.mappedClasses(drs);

//println "\n\nOJB Mapped Classes: \n${ojbMappedClasses.join( '\n' )}"
println "\n\n"

def mappedJavaFiles = convertClassesToJavaFiles(ojbMappedClasses)

println "\n\nJava Files: \n${mappedJavaFiles.join( '\n' )}"

for (File ojbMappedFile : mappedJavaFiles) {
    println "Processing File: $ojbMappedFile"
	final CompilationUnit unit = JavaParser.parse(ojbMappedFile)
	def entityVisitor = new EntityVisitor(drs, config.converterMappings, config.project.removeExistingAnnotations )
    entityVisitor.visit(unit, null)
    if ( config.project.dryRun ) {
        println unit.toString()
    } else {
        ojbMappedFile.delete()
        ojbMappedFile << unit.toString()
    }
}

//2: handle all the classes that are super classes of ojb mapped files but not residing in rice
//final Collection<String> superClasses = OjbUtil.getSuperClasses(ojbMappedClasses, "org.kuali.rice");
//
//for (String superClassFile : toFilePaths(ConversionConfig.getInstance(), superClasses)) {
//	if (superClassFile.endsWith("KraPersistableBusinessObjectBase.java")) {
//		final CompilationUnit unit = JavaParser.parse(new File(superClassFile));
//		new MappedSuperClassVisitor(drs).visit(unit, null);
//		//LOG.info(unit.toString());
//	}
//}


def Collection<File> convertClassesToJavaFiles(Collection<String> mappedClasses) {
    def javaFiles = []
    for ( className in mappedClasses ) {
        println "Looking for source file for $className"
        for ( String dir in fullSourcePaths ) {
            def fileNameWithinSource = className.replace('.', '/') + ".java"
            def javaFile = new File( dir + "/" + fileNameWithinSource)
            //println "Looking for: $javaFile.canonicalPath"
            if ( javaFile.exists() ) {
                println "Found: $javaFile.canonicalPath"
                javaFiles += javaFile
                break
            }
        }
    }
    return javaFiles
}

