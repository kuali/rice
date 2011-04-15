/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.api.repository

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import org.junit.Test
import org.junit.Assert


/**
 * This class tests out the buiding of a KrmsTypeAttribute object.
 * It also tests XML marshalling / unmarshalling
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class ActionAttributeTest {
	
	private static final String NAMESPACE = "KRMS_UNIT_TEST"
	
	private static final String ID="ORG_ATTR_1"
	private static final String TYPE_ID="1234ABCD"
	private static final String ATTR_DEF_ID = "1001"
	private static final Integer SEQUENCE_NUMBER_1 = new Integer(1)
	
	private static final String ORG_NAME = "ORG"
	private static final String ORG_LABEL = "Organization"
	private static final String COMPONENT = "someOrgComponent"
	
	private static final String EXPECTED_XML = """
    <ActionAttribute xmlns="http://rice.kuali.org/krms">
        <id>ORG_ATTR_1</id>
        <actionId>1234ABCD</actionId>
        <attributeDefinitionId>1001</attributeDefinitionId>
        <value>value</value>
    </ActionAttribute>
	"""
	
	@Test
	public void testXmlMarshaling() {
		ActionAttribute myAttr = ActionAttribute.Builder.create(ID, TYPE_ID, ATTR_DEF_ID, "actionTypeId", "value")
				.build()
		JAXBContext jc = JAXBContext.newInstance(ActionAttribute.class)
		Marshaller marshaller = jc.createMarshaller()
		StringWriter sw = new StringWriter()
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
		marshaller.marshal(myAttr, sw)
		String xml = sw.toString()
		
		print xml;

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object actual = unmarshaller.unmarshal(new StringReader(xml))
		Object expected = unmarshaller.unmarshal(new StringReader(EXPECTED_XML))
		Assert.assertEquals(expected, actual)
	}

	// TODO: test builder validations, etc

}
