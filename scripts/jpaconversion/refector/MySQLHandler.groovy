def generateMySQLSequence(classes, schemaName, path, file1, file2){
	
	def orm = ""
	def sequences = []
	def scriptText = ""
	def ormText = ""
	def conversion_util = new ConversionUtils()

	  classes.values().each {
	      c ->     
	          c.fields.values().each {
	              f ->
	                  if (f.autoincrement && !sequences.contains(f.sequenceName)) {
	
	 scriptText += """CREATE TABLE ${f.sequenceName} (
		a INT NOT NULL AUTO_INCREMENT,
		PRIMARY KEY (a)
		) AUTO_INCREMENT=1000, ENGINE=MyISAM
		/
		"""
	                      sequences.add(f.sequenceName)
	                  }
	                  if (f.autoincrement) {
	                      def name = c.className[c.className.lastIndexOf('.')+1 .. -1]
	orm += """    <entity class=\"${c.className}\" name=\"${name}\">
	      <attributes>
	          <id name=\"${f.name}\">
	              <column name=\"${f.column}\"/>
	              <generated-value strategy=\"IDENTITY\"/>
	          </id>
	      </attributes>
	  </entity>
	"""                        
	                  }
	          }
	  }
	
	conversion_util.generateFile(path + file1,   scriptText);
	
	def orm_text = """<?xml version=\"1.0\" encoding=\"UTF-8\"?>
		<entity-mappings version=\"1.0\" xmlns=\"http://java.sun.com/xml/ns/persistence/orm\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence/orm orm_1_0.xsd\">
		  <persistence-unit-metadata>
		      <persistence-unit-defaults>
		          <schema>${schemaName}</schema>
		      </persistence-unit-defaults>
		  </persistence-unit-metadata>
		${orm}</entity-mappings>
		"""
	//println (orm_text)
	conversion_util.generateFile(path + file2,  orm_text);
}
