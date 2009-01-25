import org.apache.xpath.XPathAPI
import javax.xml.parsers.DocumentBuilderFactory

def dir = '.'
inputDir = '.'
outputFileName = 'document-type-data-extract.sql'
fileNameSuffix = 'Document.xml'

count = 0
for (arg in args) {
	if (arg == '-in') inputDir = args[count + 1]
	if (arg == '-out') outputFileName = args[count + 1]
	if (arg == '-pattern') fileNameSuffix = args[count + 1]
	count++
}	


def processDir( outputFile, dir ) {
    //println "Processing Directory: " + dir
    def files = new File(dir).list()
	    
    files.each {
        String fileName ->
        File file = new File(dir, fileName)
        //println "Processing File: " + file.getAbsolutePath()
        if ( file.isDirectory() ) {
        	processDir(outputFile, file.getAbsolutePath() )
        } else if (fileName.endsWith( fileNameSuffix )){
        	//println "Processing XML "+fileName  
        	def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        	def inputStream = new FileInputStream(file)
        	def elements     = builder.parse(inputStream).documentElement
				
        	XPathAPI.selectNodeList(elements, '/beans/bean').each{ processBean(outputFile, it, file) }
			
        }
    }
}

def processBean(outputFile, bean, file) {
	def parentAttribute = bean.getAttribute('parent')
	if (parentAttribute.endsWith("DocumentEntry")) {
		println 'Processing DocumentEntry for file ' + file.getAbsolutePath()
		def label = getBeanPropertyValue(bean, 'label')
		def documentTypeName = getBeanPropertyValue(bean, 'documentTypeName')
		outputFile.append(generateUpdateStatement(label, documentTypeName))
	}
}

def generateUpdateStatement(label, documentTypeName) {
	// TODO add help url
	return "UPDATE KREW_DOC_TYP_T SET LBL='" + label + "' WHERE DOC_TYP_NM='" + documentTypeName + "'\n\\\n"
}

def getBeanPropertyValue(beanElement, propertyName) {
	def propertyValue = beanElement.getAttribute("p:" + propertyName)
	if (propertyValue == null || propertyValue == '') {
		def propertyElement = XPathAPI.selectSingleNode(beanElement, 'property[@name=\'' + propertyName +'\']')
		propertyValue = propertyElement.getAttribute('value')
		if (propertyValue == null || propertyValue == '') {
			propertyValue = propertyElement.getTextContent()
		}
	}
	return propertyValue
}

def sqlFile = new File (outputFileName)
if (sqlFile.exists()) {
	sqlFile.delete();
}
processDir(sqlFile, inputDir)
