// apparently groovy doesn't like dashes in file names...maybe because it's trying to turn the file
// name into a class name

if (args.length < 1) {
    PROJECT_DIR = "."
} else {
    PROJECT_DIR = args[0]
}

EOL = '\r\n'
CLASSPATH_FILENAME = PROJECT_DIR + "/.classpath"

ENTRIES = [] as Set

println "Writing to " + CLASSPATH_FILENAME

new File(PROJECT_DIR).eachDir() {
    it.eachFileMatch(~/.classpath/) {
        println "Processing " + it

        processClasspathFile(it)            
    }
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
            
                // , /including="/, /excluding="/
            
                [ /path="/, /output="/ ].each() {
                    m = trimmed =~ it
                    if (m) {
                        trimmed = m.replaceFirst(it + module + "/")
                    }
                }
            }
        }
        
        ENTRIES.add(trimmed)
    }
}

def getEmptyFile(path) {
    File file = new File(path)
    if (file.exists()) {
        file.delete()
    }
    return file
}