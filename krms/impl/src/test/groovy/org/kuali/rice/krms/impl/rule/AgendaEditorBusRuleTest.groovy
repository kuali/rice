/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.rule

import org.junit.Test
import org.kuali.rice.krms.impl.repository.AgendaItemBo
import org.kuali.rice.krms.impl.repository.AgendaBo
import groovy.mock.interceptor.MockFor
import org.kuali.rice.krms.impl.repository.RuleBoService
import org.kuali.rice.krms.impl.repository.RuleBo
import org.junit.Assert
import org.kuali.rice.krad.document.MaintenanceDocument
import org.kuali.rice.krms.impl.ui.AgendaEditor
import org.kuali.rice.krms.impl.ui.AgendaEditorMaintainable

class AgendaEditorBusRuleTest {

    def agendaEditorBusRule
    def mockRuleBoService = new MockFor(RuleBoService)
    def mockRuleBoServiceInstance

    void setup() {
        mockRuleBoServiceInstance = mockRuleBoService.proxyDelegateInstance()
        agendaEditorBusRule = [getRuleBoService: { mockRuleBoServiceInstance } ] as AgendaEditorBusRule
    }

    /**
     * Check that validation is successful
     */
    @Test
    void test_processAddAgendaItemBusinessRule_validateRuleName() {
        mockRuleBoService.demand.getRuleByNameAndNamespace(1) {name, namespace -> null }
        setup()
        Assert.assertTrue(agendaEditorBusRule.processAgendaItemBusinessRules(getMaintenanceDocument(getAgendaItem(), getAgenda(null), getAgenda(null))))
        mockRuleBoService.verify(agendaEditorBusRule.getRuleBoService())
   }

    /**
     * Check that error is thrown when the rule name already exist in the agenda
     */
    @Test
    void test_processAddAgendaItemBusinessRule_validateRuleName_duplicateInBo() {
        AgendaItemBo agendaItem = getAgendaItem();
        mockRuleBoService.demand.getRuleByNameAndNamespace(0) {name, namespace -> RuleBo.to(agendaItem.getRule()) }
        setup()
        Assert.assertFalse(agendaEditorBusRule.processAgendaItemBusinessRules(getMaintenanceDocument(getAgendaItem(), getAgenda(agendaItem), getAgenda(null))))
        mockRuleBoService.verify(agendaEditorBusRule.getRuleBoService())
    }

    /**
     * Check that error is thrown when the rule name already exist in any other agenda
     */
    @Test
    void test_processAddAgendaItemBusinessRule_validateRuleName_duplicateInDatabase() {
        AgendaItemBo agendaItem = getAgendaItem();
        AgendaItemBo existingAgendaItem = getAgendaItem();
        existingAgendaItem.getRule().setId ("existingRule");
        mockRuleBoService.demand.getRuleByNameAndNamespace(1) {name, namespace -> RuleBo.to(existingAgendaItem.getRule()) }
        setup()
        Assert.assertFalse(agendaEditorBusRule.processAgendaItemBusinessRules(getMaintenanceDocument(agendaItem, getAgenda(null), getAgenda(null))))
        mockRuleBoService.verify(agendaEditorBusRule.getRuleBoService())
    }

    private MaintenanceDocument getMaintenanceDocument(AgendaItemBo newAgendaItem, AgendaBo newAgenda, AgendaBo oldAgenda) {
        MaintenanceDocument document = new AgendaEditorMaintenanceDocumentDummy();

        AgendaEditorMaintainable newMaintainable = new AgendaEditorMaintainable();
        document.setNewMaintainableObject(newMaintainable);
        AgendaEditor newAgendaEditor = new AgendaEditor();
        newAgendaEditor.setAgendaItemLine(newAgendaItem);
        newAgendaEditor.setAgenda(newAgenda);
        document.getNewMaintainableObject().setDataObject(newAgendaEditor);

        AgendaEditorMaintainable oldMaintainable = new AgendaEditorMaintainable();
        document.setOldMaintainableObject(oldMaintainable);
        AgendaEditor oldAgendaEditor = new AgendaEditor();
        oldAgendaEditor.setAgenda(oldAgenda);
        document.getOldMaintainableObject().setDataObject(oldAgendaEditor);
        return document;
    }

    private AgendaItemBo getAgendaItem() {
        AgendaItemBo agendaItem = new AgendaItemBo()
        agendaItem.setRule(new RuleBo())
        agendaItem.getRule().setId("testRule")
        agendaItem.getRule().setName("Test Rule")
        agendaItem.getRule().setNamespace("KRMS_TEST")
        return agendaItem;

    }

    private AgendaBo getAgenda(AgendaItemBo agendaItem) {
        AgendaBo agenda = new AgendaBo()
        agenda.setItems(new ArrayList())
        if (agendaItem != null) {
            agendaItem.getRule().setId("existingRule");
            agenda.getItems().add(agendaItem)
        }

        return agenda
    }

}
