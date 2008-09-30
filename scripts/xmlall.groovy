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
MODULES = ['kns', 'kew', 'ksb', 'kim', 'ken', 'kcb']
DATA_TYPES = ['ApplicationConstants', 'users', 'workgroups', 'workgroupTypes', 'ruleAttributes',
              'ruleTemplates', 'documentTypes', 'rules', 'helpEntries', 'styles', 'edoclite' ]
DATA_TYPE_ELEMENT = [
    'ApplicationConstants' : '<ApplicationConstants xmlns="ns:workflow/ApplicationConstants" xsi:schemaLocation="ns:workflow/ApplicationConstants resource:ApplicationConstants">',
    'users' : '<users xmlns="ns:workflow/User" xsi:schemaLocation="ns:workflow/User resource:User">',
    'workgroups' : '<workgroups xmlns="ns:workflow/Workgroup" xsi:schemaLocation="ns:workflow/Workgroup resource:Workgroup">',
    'workgroupTypes' : '<workgroupTypes xmlns="ns:workflow/WorkgroupType" xsi:schemaLocation="ns:workflow/WorkgroupType resource:WorkgroupType">',
    'ruleAttributes' : '<ruleAttributes xmlns="ns:workflow/RuleAttribute" xsi:schemaLocation="ns:workflow/RuleAttribute resource:RuleAttribute">',
    'ruleTemplates' : '<ruleTemplates xmlns="ns:workflow/RuleTemplate" xsi:schemaLocation="ns:workflow/RuleTemplate resource:RuleTemplate">',
    'documentTypes' : '<documentTypes xmlns="ns:workflow/DocumentType" xsi:schemaLocation="ns:workflow/DocumentType resource:DocumentType">',
    'rules' : '<rules xmlns="ns:workflow/Rule" xsi:schemaLocation="ns:workflow/Rule resource:Rule">',
    'helpEntries' : '<helpEntries xmlns="ns:workflow/Help" xsi:schemaLocation="ns:workflow/Help resource:Help">',
    'styles' :  '<styles xmlns="ns:workflow/Style" xsi:schemaLocation="ns:workflow/Style resource:Style">',
    'edoclite' : '<edoclite xmlns="ns:workflow/EDocLite" xsi:schemaLocation="ns:workflow/EDocLite resource:EDocLite">' ]


// a map of the regular expressions so we don't have to recompile them each time
REGEXES = [:]
DATA_TYPES.each() {
  dataType ->
    REGEXES[dataType] = "(?s).*<" + dataType + ".*?>(.*)</" + dataType + ">.*"
}

RICE_BOOTSTRAP_XML = PROJECT_DIR + '/kns/src/main/config/xml/RiceBootstrapData.xml' 
RICE_SAMPLEAPP_XML = PROJECT_DIR + '/kns/src/main/config/xml/RiceSampleAppData.xml'
RICE_SAMPLEAPP_MASTER_XML = PROJECT_DIR + '/kns/src/main/config/xml/RiceSampleAppWorkflowBootstrap.xml'

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

bootstrap = getEmptyFile(RICE_BOOTSTRAP_XML)
concat(bootstrap, {
    moduleName ->
        PROJECT_DIR + '/' + moduleName + '/src/main/config/xml/' + moduleName.toUpperCase() + 'Bootstrap.xml'
}, null)

println ""
println "Creating rice sample app  xml: " + RICE_SAMPLEAPP_MASTER_XML
println ""

sampleapp = getEmptyFile(RICE_SAMPLEAPP_MASTER_XML)

concat(sampleapp, {
    moduleName ->
        PROJECT_DIR + '/' + moduleName + '/src/main/config/xml/' + moduleName.toUpperCase() + 'SampleData.xml'
}, [RICE_BOOTSTRAP_XML, RICE_SAMPLEAPP_XML])

println "Done."

System.exit(0)

def getEmptyFile(path) {
    file = new File(path)
    if (file.exists()) {
        file.delete()
    }
    return file
}

def concat(xml, pathClosure, extra) {
    // open tag
    xml << '<?xml version="1.0" encoding="UTF-8"?><data xmlns="ns:workflow" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="ns:workflow resource:WorkflowData">'

    // concatenate all bootstrap
    // element order matters so it has to be our outer loop which unfortunately means we are going to be reading in these files many times
    DATA_TYPES.each() {
        dataType ->
            xml << '\r\n'
            xml << '    ' + DATA_TYPE_ELEMENT[dataType]
            xml << '\r\n'
            MODULES.each() {
                moduleName ->
                path = pathClosure(moduleName)
                extract(xml, path, dataType)
            }
            if (extra != null) {
                for (path in extra) { 
                    extract(xml, path, dataType)
                }
            }
            xml << '    </' + dataType + '>\r\n'
    }

    // close tag
    xml << '</data>'

}

// functions

def extract(xml, file, dataType) {
    f = new File(file)
    if (!f.isFile()) return
    println "Concatenating " + dataType + " from " + file
    m = f.getText() =~ REGEXES[dataType]
    if (m.matches()) {
      xml << '\r\n        <!-- ' + dataType + ' elements from ' + file + ' -->\r\n'
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
