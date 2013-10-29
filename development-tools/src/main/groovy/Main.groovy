
import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit

import org.apache.ojb.broker.metadata.DescriptorRepository
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.visitor.EntityVisitor

import groovy.util.ConfigSlurper

println "Looking for config.groovy at ${this.class.classLoader.getResource("config.groovy")}"

config = new ConfigSlurper().parse(this.class.classLoader.getResource("config.groovy"))

println config

fullSourcePaths = config.project.sourceDirectories.collect { config.project.homeDirectory + "/" + it }
fullOjbPaths = config.ojb.repositoryFiles.collect { config.project.homeDirectory + "/" + it }

println "\n\nSource Directories: \n${fullSourcePaths.join( '\n' )}"
println "\n\nScanning OJB Files: \n${fullOjbPaths.join( '\n' )}"

Collection<DescriptorRepository> drs = OjbUtil.getDescriptorRepositories( fullOjbPaths );
Collection<String> ojbMappedClasses = OjbUtil.mappedClasses(drs);

//println "\n\nOJB Mapped Classes: \n${ojbMappedClasses.join( '\n' )}"
println "\n\n"

def mappedJavaFiles = convertClassesToJavaFiles(ojbMappedClasses)

println "\n\nJava Files: \n${mappedJavaFiles.join( '\n' )}"

for (String ojbMappedFile : mappedJavaFiles) {
	final CompilationUnit unit = JavaParser.parse(new File(ojbMappedFile))
	new EntityVisitor(drs).visit(unit, null)
	println unit.toString()
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


def Collection<String> convertClassesToJavaFiles(Collection<String> mappedClasses) {
    def javaFileNames = []
    for ( className in mappedClasses ) {
        println "Looking for source file for $className"
        for ( String dir in fullSourcePaths ) {
            def fileNameWithinSource = className.replace('.', '/') + ".java"
            def javaFile = new File( dir + "/" + fileNameWithinSource)
            //println "Looking for: $javaFile.canonicalPath"
            if ( javaFile.exists() ) {
                println "Found: $javaFile.canonicalPath"
                javaFileNames += javaFile.canonicalPath
                break
            }
        }
    }
    return javaFileNames
}

