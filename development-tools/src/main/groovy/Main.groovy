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

c = new ConfigSlurper().parse(this.class.classLoader.getResource("config.groovy"))

println c

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
for ( classpathDir in c.project.classpathDirectories ) {
    // NOTE: That trailing slash is absolutely required.  Otherwise it just adds the directory to the path as if it's a file and does not scan inside
    def classpathUrl = new URL("file://"+new File( c.project.homeDirectory + "/" + classpathDir ).canonicalPath + "/")
    println "Adding Classpath URL: $classpathUrl"
    rootLoader.addURL( classpathUrl )
}

for ( jarDir in c.project.classpathJarDirectories ) {
    def jars   = jarDir.listFiles().findAll { it.name.endsWith('.jar') } 
    jars.each { 
        println "Adding Classpath URL: ${it.toURI().toURL()}"
        rootLoader.addURL(it.toURI().toURL()) 
    }
    rootLoader.addURL( classpathUrl )
}


println rootLoader.getURLs()


fullSourcePaths = c.project.sourceDirectories.collect { c.project.homeDirectory + "/" + it }
fullOjbPaths = c.ojb.repositoryFiles.collect { c.project.homeDirectory + "/" + it }

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

println "\n\nJava Files: \n${mappedJavaFiles.values().join( '\n' )}"

entityVisitor = new EntityVisitor(drs, c.converterMappings, c.project.removeExistingAnnotations )

for (String className : mappedJavaFiles.keySet()) {
    File ojbMappedFile = mappedJavaFiles[className]
	processJavaFile(ojbMappedFile, className)
    
    Collection<String> superClasses = OjbUtil.getSuperClasses(className, "org.kuali.rice");
    for (String superClass : superClasses) {
        processJavaFile(convertClassToFile(superClass), className)
    }

}

def void processJavaFile( File ojbMappedFile, String subclassName ) {
    println "Processing File: $ojbMappedFile"
    if ( ojbMappedFile == null ) return;
    
    def unit = JavaParser.parse(ojbMappedFile)
    entityVisitor.visit(unit, subclassName)

    if ( c.project.dryRun ) {
        println unit.toString()
    } else {
        ojbMappedFile.delete()
        ojbMappedFile << unit.toString()
    }
}

def File convertClassToFile( String className ) {
    println "Looking for source file for $className"
    for ( String dir in fullSourcePaths ) {
        def fileNameWithinSource = className.replace('.', '/') + ".java"
        def javaFile = new File( dir + "/" + fileNameWithinSource)
        //println "Looking for: $javaFile.canonicalPath"
        if ( javaFile.exists() ) {
            println "Found: $javaFile.canonicalPath"
            return javaFile
        }
    }
    println "No source file found.  Ignoring."
    return null
}

def Map<String,File> convertClassesToJavaFiles(Collection<String> mappedClasses) {
    def javaFiles = [:]
    for ( className in mappedClasses ) {
        def javaFile = convertClassToFile(className)
        if ( javaFile != null ) {
            javaFiles[className] = javaFile
        }
    }
    return javaFiles
}

