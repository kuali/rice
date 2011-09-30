package org.kuali.rice.krms.impl.rule

import org.junit.Test
import org.kuali.rice.krms.impl.repository.AgendaItemBo
import org.kuali.rice.krms.impl.repository.AgendaBo
import groovy.mock.interceptor.MockFor
import org.kuali.rice.krms.impl.repository.RuleBoService
import org.kuali.rice.krms.impl.repository.RuleBo
import org.junit.Assert

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
        AgendaItemBo agendaItem = getAgendaItem();
        mockRuleBoService.demand.getRuleByNameAndNamespace(1) {name, namespace -> null }
        setup()
        Assert.assertTrue(agendaEditorBusRule.processAddAgendaItemBusinessRules(agendaItem, getAgenda(null)))
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
        Assert.assertFalse(agendaEditorBusRule.processAddAgendaItemBusinessRules(getAgendaItem(), getAgenda(agendaItem)))
        mockRuleBoService.verify(agendaEditorBusRule.getRuleBoService())
    }

    /**
     * Check that error is thrown when the rule name already exist in any other agenda
     */
    @Test
    void test_processAddAgendaItemBusinessRule_validateRuleName_duplicateInDatabase() {
        AgendaItemBo agendaItem = getAgendaItem();
        mockRuleBoService.demand.getRuleByNameAndNamespace(1) {name, namespace -> RuleBo.to(agendaItem.getRule()) }
        setup()
        Assert.assertFalse(agendaEditorBusRule.processAddAgendaItemBusinessRules(agendaItem, getAgenda(null)))
        mockRuleBoService.verify(agendaEditorBusRule.getRuleBoService())
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
            agenda.getItems().add(agendaItem)
        }

        return agenda
    }

}
