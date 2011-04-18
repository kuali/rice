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
class AgendaTreeRuleEntryTest {
	
	private static final String AGENDA_ITEM_ID_1 = "202AgendaItem"
	private static final String RULE_ID_1 = "101Rule"
	
	private static final String TINY_AGENDA_RULE_ENTRY = """
		<agendaTreeRuleEntry xmlns="http://rice.kuali.org/krms">
			<agendaItemId>202AgendaItem</agendaItemId>
			<ruleId>101Rule</ruleId>
		</agendaTreeRuleEntry>
		"""

		
	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeRule_Builder_create_fail_all_null() {
		AgendaTreeRuleEntry.Builder.create(null, null)
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeRule_Builder_create_fail_null_agenda_id() {
		AgendaTreeRuleEntry.Builder.create(null, RULE_ID_1)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeRule_Builder_create_fail_empty_agenda_id() {
		AgendaTreeRuleEntry.Builder.create("", RULE_ID_1)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeRule_Builder_create_fail_whitespace_agenda_id() {
		AgendaTreeRuleEntry.Builder.create("        ", RULE_ID_1)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeRule_Builder_create_fail_null_rule_id() {
		AgendaTreeRuleEntry.Builder.create(AGENDA_ITEM_ID_1, null)
	}

	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeRule_Builder_create_fail_empty_rule_id() {
		AgendaTreeRuleEntry.Builder.create(AGENDA_ITEM_ID_1, "")
	}

	@Test(expected=IllegalArgumentException.class)
	void test_AgendaTreeRule_Builder_create_fail_whitespace_rule_id() {
		AgendaTreeRuleEntry.Builder.create(AGENDA_ITEM_ID_1, "     ")
	}

	@Test
	void test_AgendaTreeRule_Builder_create_tiny_sucess() {
		AgendaTreeRuleEntry.Builder.create(AGENDA_ITEM_ID_1, RULE_ID_1)
	}

	@Test
	void test_AgendaTreeRule_Builder_create_and_build_tiny_sucess() {
		AgendaTreeRuleEntry.Builder.create(AGENDA_ITEM_ID_1, RULE_ID_1).build()
	}

	@Test
	public void testXmlMarshaling_tiny_AgendaRuleEntry() {
 		AgendaTreeRuleEntry myTiny = AgendaTreeRuleEntry.Builder.create(AGENDA_ITEM_ID_1, RULE_ID_1).build()
        JAXBContext jc = JAXBContext.newInstance(AgendaTreeRuleEntry.class, AgendaTreeDefinition.class)
	    Marshaller marshaller = jc.createMarshaller()
	    StringWriter sw = new StringWriter()
	    marshaller.marshal(myTiny, sw)
	    String xml = sw.toString()
  
	    Unmarshaller unmarshaller = jc.createUnmarshaller();
	    Object actual = unmarshaller.unmarshal(new StringReader(xml))
	    Object expected = unmarshaller.unmarshal(new StringReader(TINY_AGENDA_RULE_ENTRY))
	    Assert.assertEquals(expected, actual)
	}

	@Test
	public void testXmlUnmarshal_tiny_AgendaRuleEntry() {
	  JAXBContext jc = JAXBContext.newInstance(AgendaTreeRuleEntry.class, AgendaTreeDefinition.class)
	  Unmarshaller unmarshaller = jc.createUnmarshaller()
	  AgendaTreeRuleEntry myTiny = (AgendaTreeRuleEntry) unmarshaller.unmarshal(new StringReader(TINY_AGENDA_RULE_ENTRY))
	  Assert.assertEquals(AGENDA_ITEM_ID_1, myTiny.agendaItemId)
	  Assert.assertEquals(RULE_ID_1, myTiny.ruleId)
	  Assert.assertNull(myTiny.ifTrue)
	  Assert.assertNull(myTiny.ifFalse)
	}


}
