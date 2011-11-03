/**
 * Copyright 2005-2011 The Kuali Foundation
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
import org.apache.xpath.XPathAPI
import javax.xml.parsers.DocumentBuilderFactory

def dir = '.'
inputDir = '.'
outputFileName = 'document-type-data-extract.sql'
fileNameSuffix = 'Document.xml'

labelUpdates = []
helpUrlUpdates = []

count = 0
for (arg in args) {
	if (arg == '-in') inputDir = args[count + 1]
	if (arg == '-out') outputFileName = args[count + 1]
	if (arg == '-pattern') fileNameSuffix = args[count + 1]
	count++
}	


def processDir( dir ) {
    //println "Processing Directory: " + dir
    def files = new File(dir).list()
	    
    files.each {
        String fileName ->
        File file = new File(dir, fileName)
        //println "Processing File: " + file.getAbsolutePath()
        if ( file.isDirectory() ) {
        	processDir( file.getAbsolutePath() )
        } else if (fileName.endsWith( fileNameSuffix )){
        	//println "Processing XML "+fileName  
        	def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        	def inputStream = new FileInputStream(file)
        	def elements     = builder.parse(inputStream).documentElement
				
        	XPathAPI.selectNodeList(elements, '/beans/bean').each{ processBean(it, file) }
			
        }
    }

}

def processBean(bean, file) {
	def parentAttribute = bean.getAttribute('parent')
	if (parentAttribute.endsWith("DocumentEntry")) {
		//println 'Processing DocumentEntry for file ' + file.getAbsolutePath()
		def label = getBeanPropertyValue(bean, 'label')
		def documentTypeName = getBeanPropertyValue(bean, 'documentTypeName')
		labelUpdates.add(generateLabelUpdateStatement(label, documentTypeName))
		processHelpDefinition(bean, documentTypeName)
	}
}

def processHelpDefinition(beanElement, documentTypeName) {
	def helpDefinitionNode = XPathAPI.selectSingleNode(beanElement, 'property[@name=\'helpDefinition\']')
	if (helpDefinitionNode != null) {
		def helpDefBean = XPathAPI.selectSingleNode(helpDefinitionNode, 'bean')
		def helpParamName = getBeanPropertyValue(helpDefBean, 'parameterName')
		if (helpParamName == null || helpParamName == '') {
			throw new RuntimeException("Failed to find help parameter name for HelpDefinition")
		}
		helpUrlUpdates.add(generateHelpUrlUpdateStatement(helpParamName, documentTypeName))
	} else {
		println 'Did not find HelpDefinition for document type: ' + documentTypeName
	}
}

def generateLabelUpdateStatement(label, documentTypeName) {
	return "UPDATE KREW_DOC_TYP_T SET LBL='" + label + "' WHERE DOC_TYP_NM='" + documentTypeName + "'\n/"
}

def generateHelpUrlUpdateStatement(helpParameterName, documentTypeName) {
	return "UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=" + 
		"(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='" + helpParameterName + "' AND PARM_TYP_CD='HELP') " +
		"WHERE DOC_TYP_NM='" + documentTypeName + "'\n/"
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
processDir(inputDir)
sqlFile.append('-- Start Label Updates\n\n')
labelUpdates.each {
	String statement ->
	sqlFile.append(statement + "\n")
}
sqlFile.append('\n\n\n\n\n-- Start Help URL Updates\n\n')
helpUrlUpdates.each {
	String statement ->
	sqlFile.append(statement + "\n")
}
