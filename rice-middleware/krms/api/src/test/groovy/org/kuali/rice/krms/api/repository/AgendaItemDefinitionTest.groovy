/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krms.api.repository

import org.junit.Test
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition
import org.kuali.rice.krms.api.repository.rule.RuleDefinition

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * */
class AgendaItemDefinitionTest extends GroovyTestCase {
    @Test
    void test_AgendaItemDefinition_Builder_NullCreate() {
        shouldFail(IllegalArgumentException) {
            AgendaItemDefinition.Builder.create(null, null)
        }
    }

    @Test
    void test_AgendaItemDefinition_Builder_EmptyCreate1() {
        shouldFail(IllegalArgumentException) {
            AgendaItemDefinition.Builder.create("", null)
        }
    }

    @Test
    void test_AgendaItemDefinition_Builder_EmptyCreate2() {
        shouldFail(IllegalArgumentException) {
            AgendaItemDefinition.Builder.create("", "")
        }
    }

    @Test
    void test_AgendaDefinition_Builder_CreateNullAgendaId() {
        shouldFail(IllegalArgumentException) {
            AgendaItemDefinition.Builder.create("ABCDEFG", null)
        }
    }

    @Test
    void test_AgendaDefinition_Builder_CreateNullContract() {
        shouldFail(IllegalArgumentException) {
            AgendaItemDefinition.Builder.create(null)
        }
    }

    @Test
    void test_AgendaDefinition_Builder_Create1() {
        AgendaItemDefinition.Builder itemDefinition = getAgendItemDefinitionBuilder()

        assertEquals(0L, itemDefinition.getVersionNumber())
        assertEquals("A", itemDefinition.getId())
        assertEquals("B", itemDefinition.getAgendaId())
        assertEquals("C", itemDefinition.getRuleId())
        assertEquals("D", itemDefinition.getSubAgendaId())
        assertEquals("Q", itemDefinition.getWhenTrueId())
        assertEquals("S", itemDefinition.getWhenFalseId())
        assertEquals("U", itemDefinition.getAlwaysId())
        assertNotNull(itemDefinition.getRule())
        assertNotNull(itemDefinition.getSubAgenda())
        assertNotNull(itemDefinition.getWhenTrue())
        assertNotNull(itemDefinition.getWhenFalse())
        assertNotNull(itemDefinition.getAlways())
    }

    @Test
    void test_AgendaDefinition_Builder_Create2() {
        AgendaItemDefinition.Builder itemDefinition = getAgendItemDefinitionBuilder_NullOptions()

        assertEquals(0L, itemDefinition.getVersionNumber())
        assertEquals("A", itemDefinition.getId())
        assertEquals("B", itemDefinition.getAgendaId())
        assertEquals("C", itemDefinition.getRuleId())
        assertEquals("D", itemDefinition.getSubAgendaId())
        assertNull(itemDefinition.getWhenTrueId())
        assertNull(itemDefinition.getWhenFalseId())
        assertNull(itemDefinition.getAlwaysId())
        assertNull(itemDefinition.getRule())
        assertNull(itemDefinition.getSubAgenda())
        assertNull(itemDefinition.getWhenTrue())
        assertNull(itemDefinition.getWhenFalse())
        assertNull(itemDefinition.getAlways())
    }

    @Test
    void test_AgendaDefinition_Builder_Create3() {
        AgendaItemDefinition.Builder data = getAgendItemDefinitionBuilder()
        AgendaItemDefinition.Builder itemDefinition = AgendaItemDefinition.Builder.create(data)

        assertEquals(0L, itemDefinition.getVersionNumber())
        assertEquals("A", itemDefinition.getId())
        assertEquals("B", itemDefinition.getAgendaId())
        assertEquals("C", itemDefinition.getRuleId())
        assertEquals("D", itemDefinition.getSubAgendaId())
        assertEquals("Q", itemDefinition.getWhenTrueId())
        assertEquals("S", itemDefinition.getWhenFalseId())
        assertEquals("U", itemDefinition.getAlwaysId())
        assertNotNull(itemDefinition.getRule())
        assertNotNull(itemDefinition.getSubAgenda())
        assertNotNull(itemDefinition.getWhenTrue())
        assertNotNull(itemDefinition.getWhenFalse())
        assertNotNull(itemDefinition.getAlways())
    }

    @Test
    void test_AgendaDefinition_Constructor1() {
        AgendaItemDefinition itemDefinition = new AgendaItemDefinition()

        assertNull(itemDefinition.getVersionNumber())
        assertNull(itemDefinition.getId())
        assertNull(itemDefinition.getAgendaId())
        assertNull(itemDefinition.getRuleId());
        assertNull(itemDefinition.getSubAgendaId());
        assertNull(itemDefinition.getWhenTrueId());
        assertNull(itemDefinition.getWhenFalseId());
        assertNull(itemDefinition.getAlwaysId());
        assertNull(itemDefinition.getRule());
        assertNull(itemDefinition.getSubAgenda());
        assertNull(itemDefinition.getWhenTrue());
        assertNull(itemDefinition.getWhenFalse());
        assertNull(itemDefinition.getAlways());
    }

    @Test
    void test_AgendaDefinition_Constructor2() {
        AgendaItemDefinition.Builder data = getAgendItemDefinitionBuilder()
        AgendaItemDefinition itemDefinition = new AgendaItemDefinition(data)

        assertEquals(0L, itemDefinition.getVersionNumber())
        assertEquals("A", itemDefinition.getId())
        assertEquals("B", itemDefinition.getAgendaId())
        assertEquals("C", itemDefinition.getRuleId())
        assertEquals("D", itemDefinition.getSubAgendaId())
        assertEquals("Q", itemDefinition.getWhenTrueId())
        assertEquals("S", itemDefinition.getWhenFalseId())
        assertEquals("U", itemDefinition.getAlwaysId())
        assertNotNull(itemDefinition.getRule())
        assertNotNull(itemDefinition.getSubAgenda())
        assertNotNull(itemDefinition.getWhenTrue())
        assertNotNull(itemDefinition.getWhenFalse())
        assertNotNull(itemDefinition.getAlways())
    }

    private AgendaItemDefinition.Builder getAgendItemDefinitionBuilder() {
        AgendaItemDefinition.Builder itemDefinition = AgendaItemDefinition.Builder.create("A", "B")
        itemDefinition.setRuleId("C")
        itemDefinition.setSubAgendaId("D")
        itemDefinition.setWhenTrueId("E")
        itemDefinition.setWhenFalseId("F")
        itemDefinition.setAlwaysId("G")
        itemDefinition.setRule(new RuleDefinition.Builder("H", "I", "J", "K", "L"))
        itemDefinition.setSubAgenda(new AgendaDefinition.Builder("M", "N", "O", "P"))
        itemDefinition.setWhenTrue(new AgendaItemDefinition.Builder("Q", "R"))
        itemDefinition.setWhenFalse(new AgendaItemDefinition.Builder("S", "T"))
        itemDefinition.setAlways(new AgendaItemDefinition.Builder("U", "V"))
        itemDefinition.setVersionNumber(0L);
        return itemDefinition;
    }

    private AgendaItemDefinition.Builder getAgendItemDefinitionBuilder_NullOptions() {
        AgendaItemDefinition.Builder itemDefinition = AgendaItemDefinition.Builder.create("A", "B")
        itemDefinition.setRuleId("C")
        itemDefinition.setSubAgendaId("D")
        itemDefinition.setWhenTrueId("E")
        itemDefinition.setWhenFalseId("F")
        itemDefinition.setAlwaysId("G")
        itemDefinition.setRule(null)
        itemDefinition.setSubAgenda(null)
        itemDefinition.setWhenTrue(null)
        itemDefinition.setWhenFalse(null)
        itemDefinition.setAlways(null)
        itemDefinition.setVersionNumber(0L);
        return itemDefinition;
    }
}
