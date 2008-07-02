// apparently groovy doesn't like dashes in file names...maybe because it's trying to turn the file
// name into a class name

if (args.length < 1) {
    PROJECT_DIR = "."
} else {
    PROJECT_DIR = args[0]
}

delete = true
if (args.length > 1) {
    delete = !"nodelete".equals(args[1])
}

EOL = '\r\n'
CLASSPATH_FILENAME = PROJECT_DIR + "/.classpath"

ENTRIES = [] as Set

println "Target .classpath file: " + CLASSPATH_FILENAME

new File(PROJECT_DIR).eachDir() {
    classpathFile = new File(it, ".classpath")
    if (classpathFile.isFile()) {
        println "Processing " + classpathFile

        processClasspathFile(classpathFile)

        if (delete) {
            // clean up the carnage eclipse:eclipse left
            cleanUp(classpathFile)
        }
    }
}

if (ENTRIES.size() == 0) {
    println "ERROR: no classpath entries where generated.  Aborting " + CLASSPATH_FILENAME + " file regeneration."
    return
} else {
    println ENTRIES.size() + " unique classpath entries found.  Regenerating " + CLASSPATH_FILENAME + " file."
}

getEmptyFile(CLASSPATH_FILENAME).withWriterAppend("UTF-8") { output ->
    output << '<?xml version="1.0" encoding="UTF-8"?>' << EOL
    output << "<classpath>" << EOL

    // sort so changes are stable
    ENTRIES.sort().each() {
        output << "    " << it << EOL
    }

    output << EOL
    output << "</classpath>"
}

def processClasspathFile(cpFile) {
    module = cpFile.parentFile.name

    text = (cpFile.text =~ /\s*[\r\n]\s*+/).replaceAll("")
    text = (text =~ /<classpath>|<\/classpath>/).replaceAll("")
    text = (text =~ /<classpathentry/).replaceAll("\r\n<classpathentry")

    new StringReader(text).eachLine() {
        line->
        
        trimmed = line.trim();
        
        if (!trimmed.startsWith("<classpathentry")) {
            return
        } else if (trimmed =~ /kind="src"/) {
            if (trimmed =~ /path="\//) {
                // don't want those module project references eclipse:eclipse puts in
                //println "Discarding project reference: " + trimmed
                return
            } else {
            	outputExpr = /output="/
            	javaPathExpr = /path="src\/main\/java"/
            	resPathExpr = /path="src\/main\/resources"/
            	pathExpr = /path="/
            	
            	hasOutput = trimmed =~ outputExpr
            	hasSrcJava = trimmed =~ javaPathExpr
            	hasSrcRes = trimmed =~ resPathExpr
            	if (hasOutput) {
            		trimmed = hasOutput.replaceFirst(outputExpr + module + "/")
            	} else if (hasSrcJava) {
            		trimmed = hasSrcJava.replaceFirst(javaPathExpr + " output=\"" + module + "/target/classes\"")
            	}  else if (hasSrcRes) {
            		trimmed = hasSrcRes.replaceFirst(resPathExpr + " output=\"" + module + "/target/classes\"")
            	}
            	
            	hasPath = trimmed =~ pathExpr
            	if (hasPath) {
            		trimmed = hasPath.replaceFirst(pathExpr + module + "/")
            	}
            	
                // omit the includes and excludes that get propagated from the parent POM
                // they are only there to support CI
                [ /including=".*"/, /excluding=".*"/ ].each() {
                    m = trimmed =~ it
                    if (m) {
                        trimmed = m.replaceFirst("")
                    }
                }
            }
        }
        
        ENTRIES.add(trimmed)
    }
}

def cleanUp(classpathFile) {
    module = classpathFile.parentFile.name
    
    // delete the .classpath file
    classpathFile.delete()

    // delete the .project file
    projectFile = new File(module, ".project")
    if (projectFile.isFile()) {
        projectFile.delete();
    }

    // delete the module prefs and .settings dir if empty
    moduleSettingsDir = new File(module, ".settings")
    modulePrefsFile = new File(moduleSettingsDir, "org.eclipse.jdt.core.prefs")
    if (modulePrefsFile.isFile()) {
        modulePrefsFile.delete()
    }
    
    // if settings dir is now empty, delete it
    if (moduleSettingsDir.list().size() == 0) {
        moduleSettingsDir.delete()
    }
}

def getEmptyFile(path) {
    File file = new File(path)
    if (file.exists()) {
        file.delete()
    }
    return file
}