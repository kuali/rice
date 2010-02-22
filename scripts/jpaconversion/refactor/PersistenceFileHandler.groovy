def generatePersistenceXML(classes, persistenceUnitName, persistenceXmlFilename, path) {

	def classesXml = ""
	classes.values().each {
		c ->     
		classesXml += "    <class>${c.className}</class>\n"
	}
	
	def persistXml = """<?xml version="1.0" encoding="UTF-8"?>
			
	<persistence 
	    version=\"1.0\" 
	    xmlns=\"http://java.sun.com/xml/ns/persistence\" 
	    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" 
	    xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd\">
	  
	  <persistence-unit name=\"${persistenceUnitName}\" transaction-type=\"RESOURCE_LOCAL\">
	${classesXml}  </persistence-unit>
	
	</persistence>
	"""
	
	new ConversionUtils().generateFile(path+persistenceXmlFilename, persistXml);
}