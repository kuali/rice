import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.xpath.*


import groovy.xml.dom.DOMUtil
import groovy.xml.dom.DOMCategory
import groovy.xml.DOMBuilder
import javax.xml.parsers.DocumentBuilderFactory

if (args.length != 1) {
    println "Need one argument representing the directory to process"
    System.exit(1)
}

def rootDirectory = new File(args[0])

traverseDirectory(rootDirectory)
// processXmlFile(new File('C:\\java\\projects\\kfs\\work\\src\\org\\kuali\\kfs\\module\\cam\\document\\datadictionary\\AssetFabricationMaintenanceDocument.xml'))

def traverseDirectory(file) {
    def files = file.listFiles()
    files.each {
        fileElement ->
        if (fileElement.isDirectory()) {
            traverseDirectory(fileElement)
        }
        else if (fileElement.getName().endsWith(".xml")) {
            if (isDocumentDataDictionaryFile(fileElement)) {
                processDocumentXmlFile(fileElement)
            } else if (isBODataDictionaryFile(fileElement)) {
                processBOXmlFile(fileElement)
            }
        }
    }
}

def processDocumentXmlFile(file) {
    def xslt = '''
        <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:spring="http://www.springframework.org/schema/beans"
                xmlns:fn="http://www.w3.org/2005/04/xpath-functions"
                xmlns:p="http://www.springframework.org/schema/p">
            <xsl:template match="/spring:beans/spring:bean[@parent = 'MaintenanceDocumentEntry' or @parent = 'TransactionalDocumentEntry' or @parent = 'AccountingDocumentEntry']/spring:property[@name='label' or @name='shortLabel' or @name='helpDefinition' or @name='summary' or @name='description' or @name='documentTypeCode']"></xsl:template>
            <xsl:template match="/spring:beans/spring:bean[@parent = 'MaintenanceDocumentEntry' or @parent = 'TransactionalDocumentEntry' or @parent = 'AccountingDocumentEntry']/@p:label"></xsl:template>
            <xsl:template match="/spring:beans/spring:bean[@parent = 'MaintenanceDocumentEntry' or @parent = 'TransactionalDocumentEntry' or @parent = 'AccountingDocumentEntry']/@p:shortLabel"></xsl:template>
            <xsl:template match="/spring:beans/spring:bean[@parent = 'MaintenanceDocumentEntry' or @parent = 'TransactionalDocumentEntry' or @parent = 'AccountingDocumentEntry']/@p:helpDefinition"></xsl:template>
            <xsl:template match="/spring:beans/spring:bean[@parent = 'MaintenanceDocumentEntry' or @parent = 'TransactionalDocumentEntry' or @parent = 'AccountingDocumentEntry']/@p:summary"></xsl:template>
            <xsl:template match="/spring:beans/spring:bean[@parent = 'MaintenanceDocumentEntry' or @parent = 'TransactionalDocumentEntry' or @parent = 'AccountingDocumentEntry']/@p:description"></xsl:template>
            <xsl:template match="/spring:beans/spring:bean[@parent = 'MaintenanceDocumentEntry' or @parent = 'TransactionalDocumentEntry' or @parent = 'AccountingDocumentEntry']/@p:documentTypeCode"></xsl:template>
            <xsl:template match="//spring:bean[@parent = 'MaintainableFieldDefinition']/spring:property[@name='displayMask' or @name='displayEditMode']"></xsl:template>
            <xsl:template match="//spring:bean[@parent = 'MaintainableFieldDefinition']/@p:displayMask"></xsl:template>
            <xsl:template match="//spring:bean[@parent = 'MaintainableFieldDefinition']/@p:displayEditMode"></xsl:template>
            <xsl:template match="//spring:bean[@parent = 'LookupDefinition']/@p:instructions"></xsl:template>
            <xsl:template match="@*">
                <xsl:if test="not( namespace-uri(.) = 'http://www.springframework.org/schema/p' and (local-name(.) = 'documentTypeCode' or local-name(.) = 'label' or local-name(.) = 'shortLabel' or local-name(.) = 'helpDefinition' or local-name(.) = 'summary' or local-name(.) = 'description') and local-name(..) = 'bean' and substring(../@name, (string-length(../@name) - string-length('Document')) + 1) = 'Document')">
                    <xsl:copy>
                        <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
                </xsl:if>
            </xsl:template>
            <xsl:template match="node()">
                <xsl:if test="not( ( @name = 'label' or @name = 'shortLabel' or @name = 'instructions' or @name = 'helpDefinition' or @name = 'summary' or @name = 'description') and local-name(.) = 'property' and substring(../@name, (string-length(../@name) - string-length('Document')) + 1) = 'Document' )">
                    <xsl:copy>
                        <xsl:apply-templates select="@*|node()"/>
                    </xsl:copy>
                </xsl:if>
            </xsl:template>
        </xsl:stylesheet>
        '''.trim()
    processXmlFile(xslt, file)
}

def processBOXmlFile(file) {
    def xslt = '''
        <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                        xmlns:spring="http://www.springframework.org/schema/beans"
                        xmlns:fn="http://www.w3.org/2005/04/xpath-functions"
                        xmlns:p="http://www.springframework.org/schema/p">
            <xsl:template match="//spring:bean[@parent = 'LookupDefinition']/@p:instructions"></xsl:template>
            <xsl:template match="@*">
                <xsl:if test="not( namespace-uri(.) = 'http://www.springframework.org/schema/p' and (local-name(.) = 'documentTypeCode' or local-name(.) = 'label' or local-name(.) = 'shortLabel' or local-name(.) = 'helpDefinition' or local-name(.) = 'summary' or local-name(.) = 'description') and local-name(..) = 'bean' and substring(../@name, (string-length(../@name) - string-length('Document')) + 1) = 'Document')">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:if>
            </xsl:template>
            <xsl:template match="node()">
            <xsl:if test="not( @name = 'instructions' and local-name(.) = 'property' and substring(../@parent, (string-length(../@parent) - string-length('LookupDefinition')) + 1) = 'LookupDefinition' )">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:if>
            </xsl:template>
        </xsl:stylesheet>
        '''.trim()
    processXmlFile(xslt, file)
}

def processXmlFile(xslt, file) {    
    try {
        def fileName = file.getCanonicalPath()
        def transformedFileName = fileName + '.tmp'
        def transformedOutputStream = new FileOutputStream(transformedFileName)
        
        def factory = TransformerFactory.newInstance()
        def transformer = factory.newTransformer(new StreamSource(new StringReader(xslt)))
        transformer.transform(new StreamSource(new FileReader(file)), new StreamResult(transformedOutputStream))
        transformedOutputStream.close()
        
        def transformedInputStream = new FileInputStream(transformedFileName)
        
        def fileOutputStream = new FileOutputStream(new File(fileName), false)
        while (transformedInputStream.available() > 0) {
            fileOutputStream.write(transformedInputStream.read())
        }
        transformedInputStream.close()
    
        new File(transformedFileName).delete()
    }
    catch (e) {
        def fileName = file.getCanonicalPath()
        println filename
        println e
        return false
    }
}


def isDocumentDataDictionaryFile(file) {
    try {
    
        def xpath = XPathFactory.newInstance().newXPath()
        def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        def inputStream = new FileInputStream(file)
        def records     = builder.parse(inputStream).documentElement
    
        def nodes = xpath.evaluate("/beans/bean", records, XPathConstants.NODESET )
        def matchFound = false
        nodes.each {
            node ->
            def nodeParent = node.getAttribute("parent")
            if ( nodeParent != null && nodeParent.endsWith("DocumentEntry")) {
                matchFound = true
            }
        }
        return matchFound
    }
    catch (e) {
        println "Ignore the error on the previous line"
        return false
    }
}

def isBODataDictionaryFile(file) {
    try {
    
        def xpath = XPathFactory.newInstance().newXPath()
        def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        def inputStream = new FileInputStream(file)
        def records     = builder.parse(inputStream).documentElement
    
        def nodes = xpath.evaluate("/beans/bean", records, XPathConstants.NODESET )
        def matchFound = false
        nodes.each {
            node ->
            def nodeParent = node.getAttribute("parent")
            if ( nodeParent != null && nodeParent.endsWith("BusinessObjectEntry")) {
                matchFound = true
            }
        }
        return matchFound
    }
    catch (e) {
        println "Ignore the error on the previous line"
        return false
    }
}
