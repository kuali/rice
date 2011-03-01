/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.shareddata.api.campus

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import junit.framework.Assert
import org.junit.Test

class CampusTypeTest {
	def static final String BASE_XML = """
	  <campusType xmlns="http://rice.kuali.org/shareddata/v1_1">
			<code>A</code>
			<name>AWESOME</name>
			<active>true</active>
	  </campusType>
	  """

    @Test
    void test_create_only_required() {
        CampusType.Builder.create(CampusType.Builder.create("A")).build();
    }

	@Test
	public void testCampusTypeBuilderPassedInParams() {
	  //No assertions, just test whether the Builder gives us a Country object
	  CampusType campustype = CampusType.Builder.create("A").build()
	}
	
	@Test
	public void testCampusTypeBuilderPassedInCampusContract() {
	  this.createCampusTypeFromPassedInContract()
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCampusTypeBuilderEmptyCode() {
	  CampusType.Builder.create("")
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCampusTypeBuilderNullCode() {
	  CampusType.Builder.create(null)
	}
	
	@Test
	public void testXmlMarshaling() {
	  JAXBContext jc = JAXBContext.newInstance(CampusType.class)
	  Marshaller marshaller = jc.createMarshaller()
	  StringWriter sw = new StringWriter()
  
	  CampusType campusType = this.createCampusTypeFromPassedInContract()
	  marshaller.marshal(campusType,sw)
	  String xml = sw.toString()
  
	  String expectedCampusTypeElementXml = BASE_XML
  
	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  Object actual = unmarshaller.unmarshal(new StringReader(xml))
	  Object expected = unmarshaller.unmarshal(new StringReader(expectedCampusTypeElementXml))
	  Assert.assertEquals(expected,actual)
	}
  
	@Test
	public void testXmlUnmarshal() {
	  String rawXml = BASE_XML
  
	  JAXBContext jc = JAXBContext.newInstance(Campus.class)
	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  CampusType campusType = (CampusType) unmarshaller.unmarshal(new StringReader(rawXml))
	  Assert.assertEquals("A",campusType.code)
	  Assert.assertEquals("AWESOME",campusType.name)
	  Assert.assertEquals(true,campusType.active)
  
	}
	
	private CampusType createCampusTypeFromPassedInContract() {
		CampusType campusType = CampusType.Builder.create(new CampusTypeContract() {
			String getCode() {"A"}
			String getName() { "AWESOME" }
			boolean isActive() { true }
		  }).build()
	}
}
