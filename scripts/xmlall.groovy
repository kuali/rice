// generates master bootstrap and sample app xml
import java.util.regex.*

PROJECT_DIR = '/java/projects/rice'

if (args.length > 2) { 
    println 'usage: groovy xmlall.groovy [-pdir PROJECT_DIR]'
    println '       PROJECT_DIR defaults to ' + PROJECT_DIR
    System.exit(1)  
}

count = 0
for (arg in args) {
    if (arg == '-pdir') PROJECT_DIR = args[count + 1]
    count++
}   

// set up variables based on PROJECT_DIR
MODULES = ['kns', 'kew', 'ksb', 'kim', 'ken', 'kom']
DATA_TYPES = ['ApplicationConstants', 'users', 'workgroups', 'workgroupTypes', 'ruleAttributes',
              'ruleTemplates', 'documentTypes', 'rules', 'helpEntries', 'styles', 'edoclite' ]
// a map of the regular expressions so we don't have to recompile them each time
REGEXES = [:]
DATA_TYPES.each() {
  dataType ->
    REGEXES[dataType] = "(?s).*(<" + dataType + ".*</" + dataType + ">).*"
}

RICE_BOOTSTRAP_XML = PROJECT_DIR + '/kns/src/main/config/xml/RiceBootstrapData.xml' 
RICE_SAMPLEAPP_XML = PROJECT_DIR + '/kns/src/main/config/xml/RiceSampleData.xml'

// prompt and read user input
println warningtext()
input = new BufferedReader(new InputStreamReader(System.in))
answer = input.readLine()
if (!"yes".equals(answer.trim().toLowerCase())) {
    System.exit(2)
}

println ""
println "Creating rice bootstrap xml: " + RICE_BOOTSTRAP_XML
println ""

concat(RICE_BOOTSTRAP_XML, {
    moduleName ->
        PROJECT_DIR + '/' + moduleName + '/src/main/config/xml/' + moduleName.toUpperCase() + 'Bootstrap.xml'
})

println ""
println "Creating rice sample app  xml: " + RICE_SAMPLEAPP_XML
println ""

concat(RICE_SAMPLEAPP_XML, {
    moduleName ->
        PROJECT_DIR + '/' + moduleName + '/src/main/config/xml/' + moduleName.toUpperCase() + 'SampleData.xml'
})

println "Done."

System.exit(0)

def concat(target, pathClosure) {
    xml = new File(target)
    if (xml.exists()) {
        xml.delete()
    }

    // open tag
    xml << '<?xml version="1.0" encoding="UTF-8"?><data xmlns="ns:workflow" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="ns:workflow resource:WorkflowData">'

    // concatenate all bootstrap
    // element order matters so it has to be our outer loop which unfortunately means we are going to be reading in these files many times
    DATA_TYPES.each() {
        dataType ->
            MODULES.each() {
                moduleName ->
                path = pathClosure(moduleName)
                println "Concatenating " + dataType + " for module " + moduleName.toUpperCase() + ": " + path
                extract(xml, path, dataType)
            }
    }

    // close tag
    xml << '</data>'

}

// functions

def extract(xml, file, dataType) {
    f = new File(file)
    if (!f.isFile()) return
    m = f.getText() =~ REGEXES[dataType]
    if (m.matches()) {
      xml << '\r\n<!-- ' + dataType + ' elements from ' + path + ' -->\r\n\r\n'
      for (i in 1..m.groupCount()) {
            xml << m.group(i)
            xml << '\r\n\r\n'
      }
    }
}

def warningtext() {
"""
==================================================================
                            WARNING 
==================================================================
It will create or replace the following files:
    1) ${RICE_BOOTSTRAP_XML}
    2) ${RICE_SAMPLEAPP_XML}

If this is not what you want, please supply more information:
    usage: groovy dball.groovy [-pdir PROJECT_DIR]
           PROJECT_DIR defaults to /java/projects/rice

Do you want to continue (yes/no)?"""
}
