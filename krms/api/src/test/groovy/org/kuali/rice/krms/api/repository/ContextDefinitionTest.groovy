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

import java.util.List

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller

import org.junit.Assert
import org.junit.Test
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;


/**
 * This is a description of what this class does - dseibert don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class ContextDefinitionTest {
	
	private static final String NAMESPACE = "KRMS_TEST"
	private static final String CONTEXT_ID_1 = "CONTEXTID001"
	private static final String CONTEXT_NAME = "Context1"
	private static final String TYPE_ID = "1234XYZ"

	private static final String AGENDA_ID = "500Agenda"
	private static final String AGENDA_ITEM_ID_1 = "AgendaItem1"

	private static final String SMALL_CONTEXT = """
<context xmlns:="http://rice.kuali.org/krms">
    <id>CONTEXTID001</id>
    <namespace>Context1</namespace>
    <name>KRMS_TEST</name>
    <typeId>1234XYZ</typeId>
    <agendas/>
</context>	"""

	private static final String FULL_CONTEXT = """
		<context xmlns="http://rice.kuali.org/krms">
			<id>CONTEXTID001</id>
			<name>Context1</name>
			<namespace>KRMS_TEST</namespace>
			<typeId>1234XYZ</typeId>
			<agendas>
    		</agendas>
		</context>	"""


	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_all_null() {
		ContextDefinition.Builder.create(null, null)
	}	

	@Test
	void test_ContextDefinition_Builder_create_name_namespace() {
		ContextDefinition.Builder.create(CONTEXT_NAME, NAMESPACE)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_null_context_name() {
		ContextDefinition.Builder.create(null, NAMESPACE)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_empty_context_name() {
		ContextDefinition.Builder.create("", NAMESPACE)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_whitespace_context_name() {
		ContextDefinition.Builder.create("  	", NAMESPACE)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_null_context_namespace() {
		ContextDefinition.Builder.create(CONTEXT_NAME, null)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_empty_context_namespace() {
		ContextDefinition.Builder.create(CONTEXT_NAME,"")
	}

	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_whitespace_context_namespace() {
		ContextDefinition.Builder.create(CONTEXT_NAME, "  	")
	}

	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_blank_id() {
		ContextDefinition.Builder builder = ContextDefinition.Builder.create(CONTEXT_NAME, NAMESPACE)
		builder.setId("")
	}

	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_whitespace_id() {
		ContextDefinition.Builder builder = ContextDefinition.Builder.create(CONTEXT_NAME, NAMESPACE)
		builder.setId("      ")
	}

	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_empty_type_id() {
		ContextDefinition.Builder builder = ContextDefinition.Builder.create(CONTEXT_NAME, NAMESPACE)
		builder.setTypeId("")
	}

	@Test(expected=IllegalArgumentException.class)
	void test_ContextDefinition_Builder_create_fail_whitespace_type_id() {
		ContextDefinition.Builder builder = ContextDefinition.Builder.create(CONTEXT_NAME, NAMESPACE)
		builder.setTypeId("      ")
	}

	@Test
	void test_ContextDefinition_Builder_create() {
		ContextDefinition.Builder builder = ContextDefinition.Builder.create(CONTEXT_NAME, NAMESPACE)
		builder.setId(CONTEXT_ID_1)
		builder.setTypeId(TYPE_ID)
	}
	
	@Test
	void test_ContextDefinition_Builder_create_and_build() {
		ContextDefinition.Builder builder = ContextDefinition.Builder.create(CONTEXT_NAME, NAMESPACE)
		builder.setId(CONTEXT_ID_1)
		builder.setTypeId(TYPE_ID)
		builder.build()
	}
	
	@Test
	public void testXmlMarshaling_small_ContextDefinition() {
//		ContextDefinition.Builder builder = ContextDefinition.Builder.create(CONTEXT_NAME, NAMESPACE)
//		builder.setId(CONTEXT_ID_1)
//		builder.setTypeId(TYPE_ID)
//		ContextDefinition myContext = builder.build()
//
//		JAXBContext jc = JAXBContext.newInstance(ContextDefinition.class)
//		Marshaller marshaller = jc.createMarshaller()
//		StringWriter sw = new StringWriter()
//		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
//		marshaller.marshal(myContext, sw)
//		String xml = sw.toString()
//		print xml
//		
//		Unmarshaller unmarshaller = jc.createUnmarshaller();
//		Object actual = unmarshaller.unmarshal(new StringReader(xml))
//		Object expected = unmarshaller.unmarshal(new StringReader(SMALL_CONTEXT))
//		Assert.assertEquals(expected, actual)
	}

	@Test
	public void testXmlUnmarshal_small_ContextDefinition() {
//	  JAXBContext jc = JAXBContext.newInstance(ContextDefinition.class)
//	  Unmarshaller unmarshaller = jc.createUnmarshaller()
//	  ContextDefinition myContext = (ContextDefinition) unmarshaller.unmarshal(new StringReader(SMALL_CONTEXT))
//	  Assert.assertEquals(CONTEXT_ID_1, myContext.getId())
//	  Assert.assertEquals(myContext.proposition.description, "is Campus Bloomington")
	}

  
}
