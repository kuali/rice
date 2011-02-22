package org.kuali.rice.shareddata.api.campus

import org.junit.Test
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import junit.framework.Assert

class CampusTest {
	def static final String BASE_XML = """
	  <campus xmlns="http://rice.kuali.org/shareddata/campus/v1_1">
		<code>AMES</code>
		<name>Iowa State University - Ames</name>
		<shortName>ISU - Ames</shortName>
		<campusType>
			<code>B</code>
			<name>BOTH</name>
			<active>true</active>
		</campusType>
		<active>true</active>
	  </campus>
	  """
	
	@Test
	public void testCampusBuilderPassedInParams() {
	  //No assertions, just test whether the Builder gives us a Country object
	  Campus campus = Campus.Builder.create("AMES").build()
	}
	
	@Test
	public void testCampusBuilderPassedInCampusContract() {
	  this.createCampusFromPassedInContract()
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCampusBuilderEmptyCode() {
	  Campus.Builder.create("")
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCampusBuilderNullCode() {
	  Campus.Builder.create(null)
	}
	
	@Test
	public void testXmlMarshaling() {
	  JAXBContext jc = JAXBContext.newInstance(Campus.class)
	  Marshaller marshaller = jc.createMarshaller()
	  StringWriter sw = new StringWriter()
	  //marshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
	  //marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8")
	  //m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", mapper)
  
	  Campus campus = this.createCampusFromPassedInContract()
	  marshaller.marshal(campus,sw)
	  String xml = sw.toString()
  
	  String expectedCampusElementXml = BASE_XML
  
	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  Object actual = unmarshaller.unmarshal(new StringReader(xml))
	  Object expected = unmarshaller.unmarshal(new StringReader(expectedCampusElementXml))
	  Assert.assertEquals(expected,actual)
	}
  
	@Test
	public void testXmlUnmarshal() {
	  String rawXml = BASE_XML
  
	  JAXBContext jc = JAXBContext.newInstance(Campus.class)
	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  Campus campus = (Campus) unmarshaller.unmarshal(new StringReader(rawXml))
	  Assert.assertEquals("AMES",campus.code)
	  Assert.assertEquals("Iowa State University - Ames",campus.name)
	  Assert.assertEquals("ISU - Ames",campus.shortName)
	  Assert.assertEquals(true,campus.active)
	  Assert.assertEquals("B",campus.campusType.code)
	  Assert.assertEquals("BOTH", campus.campusType.name)
	  Assert.assertEquals(true, campus.campusType.active)
  
	}
	
	private Campus createCampusFromPassedInContract() {
		Campus campus = Campus.Builder.create(new CampusContract() {
			String getCode() {"AMES"}
			String getName() { "Iowa State University - Ames" }
			String getShortName() { "ISU - Ames" }
			CampusType getCampusType() { CampusType.Builder.create(new CampusTypeContract() {
				String getCode() {"B"}
				String getName() {"BOTH"}
				boolean isActive() { true }
			}).build()}
			boolean isActive() { true }
		  }).build()
	}
}
