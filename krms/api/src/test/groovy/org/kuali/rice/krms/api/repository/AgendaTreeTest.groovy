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


/**
 * This is a description of what this class does - dseibert don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class AgendaTreeTest {
	
	private static final String AGENDA_ID = "500Agenda"
	
	private static final String TINY_AGENDA_TREE = """
		<agendaTreeDefinition xmlns="http://rice.kuali.org/krms">
			<agendaId>500Agenda</agendaId>
		</agendaTreeDefinition>		"""

		private static final String SMALL_RULE_AGENDA_TREE = """
		<agendaTreeDefinition xmlns="http://rice.kuali.org/krms">
			<agendaId>500Agenda</agendaId>
		</agendaTreeDefinition>		"""

	@Test
	void test_AgendaTreeDefinition_Builder_create() {
		AgendaTreeDefinition.Builder.create()
	}	
	
	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeDefinition_Builder_fail_null_agendaId() {
		AgendaTreeDefinition.Builder myBuilder = AgendaTreeDefinition.Builder.create()
		myBuilder.setAgendaId(null);
	}

	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeDefinition_Builder_fail_blank_agendaId() {
		AgendaTreeDefinition.Builder myBuilder = AgendaTreeDefinition.Builder.create()
		myBuilder.setAgendaId("");
	}

	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeDefinition_Builder_fail_whitespace_agendaId() {
		AgendaTreeDefinition.Builder myBuilder = AgendaTreeDefinition.Builder.create()
		myBuilder.setAgendaId("     ");
	}

	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeDefinition_Builder_fail_null_ruleEntry() {
		AgendaTreeDefinition.Builder myBuilder = AgendaTreeDefinition.Builder.create()
		myBuilder.addRuleEntry(null);
	}

	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeDefinition_Builder_fail_null_subAgendaEntry() {
		AgendaTreeDefinition.Builder myBuilder = AgendaTreeDefinition.Builder.create()
		myBuilder.addSubAgendaEntry(null);
	}

	@Test
	void test_AgendaTreeDefinition_Builder_create_and_build_tiny_success() {
		AgendaTreeDefinition.Builder myBuilder = AgendaTreeDefinition.Builder.create()
		myBuilder.build()
	}

	@Test
	public void testXmlMarshaling_tiny_AgendaTreeDefinition() {
		AgendaTreeDefinition.Builder myBuilder  = AgendaTreeDefinition.Builder.create()
		myBuilder.setAgendaId AGENDA_ID
		AgendaTreeDefinition myTiny = myBuilder.build()
		JAXBContext jc = JAXBContext.newInstance(AgendaTreeRuleEntry.class, AgendaTreeDefinition.class, AgendaTreeSubAgendaEntry.class)
		Marshaller marshaller = jc.createMarshaller()
		StringWriter sw = new StringWriter()
		marshaller.marshal(myTiny, sw)
		String xml = sw.toString()
  
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object actual = unmarshaller.unmarshal(new StringReader(xml))
		Object expected = unmarshaller.unmarshal(new StringReader(TINY_AGENDA_TREE))
		Assert.assertEquals(expected, actual)
	}

	@Test
	public void testXmlUnmarshal_tiny_AgendaTreeDefinition() {
	  JAXBContext jc = JAXBContext.newInstance(AgendaTreeRuleEntry.class, AgendaTreeDefinition.class, AgendaTreeSubAgendaEntry.class)
	  Unmarshaller unmarshaller = jc.createUnmarshaller()
	  AgendaTreeDefinition myTiny = (AgendaTreeDefinition) unmarshaller.unmarshal(new StringReader(TINY_AGENDA_TREE))
	  Assert.assertEquals(AGENDA_ID, myTiny.agendaId)
	  Assert.assertTrue(myTiny.entries.size() == 0)
	}



}
