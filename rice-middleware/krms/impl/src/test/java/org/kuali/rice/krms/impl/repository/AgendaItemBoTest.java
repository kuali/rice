/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krms.impl.repository;

import groovy.mock.interceptor.MockFor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.framework.persistence.jta.Jta;
import org.kuali.rice.krad.data.CopyOption;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.api.repository.typerelation.RelationshipType;
import org.kuali.rice.krms.api.repository.typerelation.TypeTypeRelation;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.kuali.rice.krad.data.CopyOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class AgendaItemBoTest {
    private AgendaItemBo testObject;
    @Mock
    KrmsTypeRepositoryService mockKrmsTypeRepositoryService;
    @Mock
    DataObjectService mockDataObjectService;
    @Mock
    RepositoryBoIncrementer mockRepositoryBoIncrementer;
    @Mock
    RuleBo mockRuleBo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testObject = new AgendaItemBo();
        when(mockKrmsTypeRepositoryService.getTypeById(any(String.class))).thenReturn(KrmsTypeDefinition.Builder.create("name","namespace").build());
        when(mockDataObjectService.copyInstance(any(AgendaItemBo.class), any(CopyOption.class), any(CopyOption.class)))
                .thenReturn(getAgendaItemBoFull("A", "B", "C", "D"))
                .thenReturn(getAgendaItemBo("A", "B", "C", getRuleBo("H", "I", "J", "K", null)));
        when(mockRepositoryBoIncrementer.getNewId()).thenReturn("NEW_ID");
        testObject.setKrmsTypeRepositoryService(mockKrmsTypeRepositoryService);
        testObject.setDataObjectService(mockDataObjectService);
        testObject.agendaItemIdIncrementer = mockRepositoryBoIncrementer;
    }

    @Test
    public void testFromNullParameter() {
        assertNull(testObject.from(null));
    }

    @Test
    public void testToNullParameter() {
        assertNull(testObject.to(null));
    }

    @Test
    public void testTo() {
        AgendaItemBo data = new AgendaItemBo();
        data.setId("A");
        data.setAgendaId("B");
        data.setVersionNumber(0L);
        data.setRule(getRuleBo("C", "D", "E", "F", null));
        data.setSubAgendaId("G");
        data.setWhenTrue(getAgendaItemBo("H", "I", "J", getRuleBo("K", "L", "M", "N", null)));
        data.setWhenFalse(getAgendaItemBo("O", "P", "Q", getRuleBo("R", "S", "T", "U", null)));
        data.setAlways(getAgendaItemBo("V", "W", "X", getRuleBo("Y", "Z", "AA", "AB", null)));

        AgendaItemDefinition result = testObject.to(data);

        assertEquals("A",result.getId());
        assertEquals("B",result.getAgendaId());
        assertEquals("C",result.getRuleId());
        assertEquals("G",result.getSubAgendaId());
        assertEquals("J",result.getWhenTrueId());
        assertEquals("Q",result.getWhenFalseId());
        assertEquals("X",result.getAlwaysId());
        assertEquals(0L,(long)result.getVersionNumber());
        assertNotNull(result.getRule());
        assertNotNull(result.getWhenTrue());
        assertNotNull(result.getWhenFalse());
        assertNotNull(result.getAlways());
    }

    @Test
    public void testFrom() {
        AgendaItemBo data = testObject.from(getAgendItemDefinitionBuilder());

        assertEquals("A",data.getId());
        assertEquals("B",data.getAgendaId());
        assertEquals("H",data.getRuleId());
        assertEquals("D",data.getSubAgendaId());
        assertEquals("Q",data.getWhenTrueId());
        assertEquals("S",data.getWhenFalseId());
        assertEquals("U",data.getAlwaysId());
        assertEquals(0L,(long)data.getVersionNumber());
        assertNotNull(data.getRule());
        assertNotNull(data.getWhenTrue());
        assertNotNull(data.getWhenFalse());
        assertNotNull(data.getAlways());
    }

    @Test
    public void testGetUl() {
        AgendaItemBo obj = new AgendaItemBo();
        AgendaItemBo agenda = getAgendaItemBo("A", "B", "C", getRuleBo("H", "I", "J", "K", null));
        testObject.setRule(agenda.getRule());
        testObject.setWhenTrue(agenda.getWhenTrue());
        testObject.setWhenFalse(agenda.getWhenFalse());
        testObject.setAlways(agenda.getAlways());
        String result = testObject.getUl(obj);
        assertEquals("<ul><li>H</li></ul>", result);
    }

    @Test
    public void testGetUlHelper() {
        AgendaItemBo obj = new AgendaItemBo();
        testObject.setAgendaId("B");
        testObject.setId("A");
        testObject.setVersionNumber(0L);
        testObject.setRule(getRuleBo("ID", "NAME", "NAMESPACE", "DESCRIPTION", null));
        testObject.setSubAgendaId("D");
        //Note : for this test to pass these next 3 lines must be commented out or null
        //testObj.setWhenTrue(getAgendaItemBo())
        //testObj.setWhenFalse(getAgendaItemBo())
        //testObj.setAlways(getAgendaItemBo())
        String result = testObject.getUlHelper(obj);
        assertEquals("<li>ID</li>",result);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetRuleTextInvalidParameter(){
         testObject.getRuleText();
    }

    @Test
    public void testGetRuleTextValidParameterUnnamedRule(){
        List<ActionBo> actionList = new ArrayList<ActionBo>();
        ActionBo action = new ActionBo();
        action.setName("A");
        action.setTypeId("B");
        action.setNamespace("C");
        action.setSequenceNumber(1);
        actionList.add(action);
        testObject.setRule(getRuleBo("D","","E","F", actionList));
        String result = testObject.getRuleText();
        assertEquals("- unnamed rule -: F   [name: A]", result);
    }

    @Test
    public void testGetRuleTextValidParameterSingleAction(){
        List<ActionBo> actionList = new ArrayList<ActionBo>();
        ActionBo action = new ActionBo();
        action.setName("A");
        action.setTypeId("B");
        action.setNamespace("C");
        action.setSequenceNumber(1);
        actionList.add(action);

        testObject.setRule(getRuleBo("D","E","F","G", actionList));

        String result = testObject.getRuleText();
        assertEquals("E: G   [name: A]", result);
    }

    @Test
    public void testGetRuleTextValidParameterMultipleAction(){
        List<ActionBo> actionList = new ArrayList<ActionBo>();
        ActionBo action1 = new ActionBo();
        action1.setName("ActionName1");
        action1.setTypeId("ActionType1");
        action1.setNamespace("ActionNamespace1");
        action1.setSequenceNumber(1);
        actionList.add(action1);
        ActionBo action2 = new ActionBo();
        action2.setName("ActionName2");
        action2.setTypeId("ActionType2");
        action2.setNamespace("ActionNamespace2");
        action2.setSequenceNumber(2);
        actionList.add(action2);

        testObject.setRule(getRuleBo("ID","NAME","NAMESPACE","DESCRIPTION", actionList));

        String result = testObject.getRuleText();
        assertEquals("NAME: DESCRIPTION   [name: ActionName1 ... ]", result);
    }

    @Test
    public void testCopyAgendaItemValidParameter1() {
        RuleBo ruleBo = getRuleBo("ID","NAME","NAMESPACE","DESCRIPTION", null);
        ContextBo contextBo = createContext("NAME", "NAMEPSACE");
        KrmsAttributeDefinitionBo eventAttributeDefinition = createEventAttributeDefinition("NAME", "NAMESPACE");
        AgendaBo copiedAgenda = createAgenda(ruleBo, contextBo, eventAttributeDefinition);
        Map<String, RuleBo> oldRuleIdToNew = new HashMap<String, RuleBo>();
        Map<String, AgendaItemBo> oldAgendaItemIdToNew = new HashMap<String, AgendaItemBo>();
        List<AgendaItemBo> copiedAgendaItems = new ArrayList<AgendaItemBo>();
        String dts = "";

        when(mockRuleBo.getId()).thenReturn("ID");
        when(mockRuleBo.copyRule(any(String.class))).thenReturn(getRuleBo("ID","NAME","NAMESPACE","DESCRIPTION", null));
        testObject.setRule(mockRuleBo);
        testObject.setId(mockRuleBo.getId());
        final AgendaItemBo agendaItemBo1 = getAgendaItemBo("A", "B", "C", mockRuleBo);
        final AgendaItemBo agendaItemBo2 = getAgendaItemBo("D", "E", "F", mockRuleBo);
        final AgendaItemBo agendaItemBo3 = getAgendaItemBo("G", "H", "I", mockRuleBo);
        testObject.setWhenFalse(agendaItemBo1);
        testObject.setWhenFalseId(agendaItemBo1.getId());
        testObject.setWhenTrue(agendaItemBo2);
        testObject.setWhenTrueId(agendaItemBo2.getId());
        testObject.setAlways(agendaItemBo3);
        testObject.setAlwaysId(agendaItemBo3.getId());
        testObject.copyAgendaItem(copiedAgenda, oldRuleIdToNew, oldAgendaItemIdToNew, copiedAgendaItems, dts);

        AgendaItemBo result = testObject.copyAgendaItem(copiedAgenda, oldRuleIdToNew, oldAgendaItemIdToNew, copiedAgendaItems, dts);

        assertNotNull(result);
        assertNull(result.getAlways());
        assertNull(result.getWhenTrue());
        assertNull(result.getWhenFalse());
        assertNotNull(result.getRule());
        assertEquals("NEW_ID",result.getId());
        assertEquals("B",result.getSubAgendaId());
    }

    @Test
    public void testCopyAgendaItemValidParameter2() {
        RuleBo ruleBo = getRuleBo("ID","NAME","NAMESPACE","DESCRIPTION", null);
        ContextBo contextBo = createContext("NAME", "NAMEPSACE");
        KrmsAttributeDefinitionBo eventAttributeDefinition = createEventAttributeDefinition("NAME", "NAMESPACE");
        AgendaBo copiedAgenda = createAgenda(ruleBo, contextBo, eventAttributeDefinition);
        Map<String, RuleBo> oldRuleIdToNew = new HashMap<String, RuleBo>();
        Map<String, AgendaItemBo> oldAgendaItemIdToNew = new HashMap<String, AgendaItemBo>();
        List<AgendaItemBo> copiedAgendaItems = new ArrayList<AgendaItemBo>();
        String dts = "";

        when(mockRuleBo.getId()).thenReturn("C");
        when(mockRuleBo.copyRule(any(String.class))).thenReturn(getRuleBo("C","NAME","NAMESPACE","DESCRIPTION", null));
        testObject.setRule(mockRuleBo);
        testObject.setId(mockRuleBo.getId());
        final AgendaItemBo agendaItemBo1 = getAgendaItemBo("A", "B", "C", mockRuleBo);
        final AgendaItemBo agendaItemBo2 = getAgendaItemBo("A", "D", "C", mockRuleBo);
        final AgendaItemBo agendaItemBo3 = getAgendaItemBo("A", "B", "C", mockRuleBo);
        testObject.setWhenFalse(agendaItemBo1);
        testObject.setWhenFalseId(agendaItemBo1.getId());
        testObject.setWhenTrue(agendaItemBo2);
        testObject.setWhenTrueId(agendaItemBo2.getId());
        testObject.setAlways(agendaItemBo3);
        testObject.setAlwaysId(agendaItemBo3.getId());

        AgendaItemBo result = testObject.copyAgendaItem(copiedAgenda, oldRuleIdToNew, oldAgendaItemIdToNew, copiedAgendaItems, dts);

        assertNotNull(result);
        assertNotNull(result.getAlways());
        assertNotNull(result.getWhenTrue());
        assertNotNull(result.getWhenFalse());
        assertNotNull(result.getRule());
        assertEquals("NEW_ID",result.getId());
        assertEquals("NEW_ID",result.getAlwaysId());
        assertEquals("NEW_ID",result.getWhenTrueId());
        assertEquals("NEW_ID",result.getWhenFalseId());
        assertEquals("D",result.getSubAgendaId());
    }

    @Test
    public void testGetAlwaysList() {
        final AgendaItemBo agendaItemBo1 = getAgendaItemBo("A", "B", "C", null);
        final AgendaItemBo agendaItemBo2 = getAgendaItemBo("D", "E", "F", null);
        agendaItemBo2.setAlways(agendaItemBo1);
        final AgendaItemBo agendaItemBo3 = getAgendaItemBo("G", "H", "I", null);
        agendaItemBo3.setAlways(agendaItemBo2);
        testObject.setAlways(agendaItemBo3);

        List<AgendaItemBo> result = testObject.getAlwaysList();

        assertNotNull(result);
        assertEquals(3,result.size());
    }

    private ContextBo createContext(String name, String namespace) {
        KrmsTypeDefinition.Builder typeDefinition = KrmsTypeDefinition.Builder.create(name, namespace);
        typeDefinition.setId("ID");
        KrmsTypeDefinition defaultContextType = typeDefinition.build();

        ContextBo contextBo = new ContextBo();
        contextBo.setNamespace(KrmsConstants.KRMS_NAMESPACE);
        contextBo.setName("MyContext");
        contextBo.setTypeId(defaultContextType.getId());
        return contextBo;
    }

    private KrmsAttributeDefinitionBo createEventAttributeDefinition(String name, String namespace) {
        KrmsAttributeDefinitionBo attributeDefinitionBo = new KrmsAttributeDefinitionBo();
        attributeDefinitionBo.setNamespace(namespace);
        attributeDefinitionBo.setName(name);
        attributeDefinitionBo.setLabel("Event");
        attributeDefinitionBo.setActive(true);
        return attributeDefinitionBo;
    }

    private AgendaBo createAgenda(RuleBo ruleBo, ContextBo contextBo, KrmsAttributeDefinitionBo eventAttributeDefinition) {
        AgendaBo agendaBo = new AgendaBo();
        agendaBo.setActive(true);
        agendaBo.setContextId(contextBo.getId());
        agendaBo.setName("MyAgenda");
        agendaBo.setTypeId(null);

        AgendaItemBo agendaItemBo = getAgendaItemBo("A", "B", "C", getRuleBo("H", "I", "J", "K", null));
        List<AgendaItemBo> agendaItems = new ArrayList<AgendaItemBo>();
        agendaItems.add(agendaItemBo);

        agendaBo.setItems(agendaItems);
        agendaBo.setFirstItemId(agendaItemBo.getId());
        agendaBo.setFirstItem(agendaItemBo);

        Set<AgendaAttributeBo> agendaAttributes = new HashSet<AgendaAttributeBo>();
        agendaBo.setAttributeBos(agendaAttributes);
        AgendaAttributeBo agendaAttribute = new AgendaAttributeBo();
        agendaAttributes.add(agendaAttribute);
        agendaAttribute.setAttributeDefinition(eventAttributeDefinition);
        agendaAttribute.setValue("workflow");
        agendaAttribute.setAgenda(agendaBo);

        contextBo.getAgendas().add(agendaBo);

        return agendaBo;
    }

    private RuleBo getRuleBo(String id, String name, String namespace, String description, List<ActionBo> actionList) {
        RuleBo obj = new RuleBo();
        obj.setId(id);
        obj.setName(name);
        obj.setNamespace(namespace);
        obj.setDescription(description);
        obj.setActions(actionList);
        return obj;
    }

    private AgendaItemBo getAgendaItemBoFull(String id, String name, String namespace, String description) {
        AgendaItemBo obj = new AgendaItemBo();
        obj.setAgendaId("B");
        obj.setId("A");
        obj.setVersionNumber(0L);
        obj.setRule(getRuleBo(id, name, namespace, description, null));
        obj.setSubAgendaId("D");
        obj.setWhenTrue(getAgendaItemBo("A", "B", "C", getRuleBo("H", "I", "J", "K", null)));
        obj.setWhenFalse(getAgendaItemBo("A", "B", "C", getRuleBo("H", "I", "J", "K", null)));
        obj.setAlways(getAgendaItemBo("A", "B", "C", getRuleBo("H", "I", "J", "K", null)));
        return obj;
    }

    private AgendaItemBo getAgendaItemBo(String agendaId, String sunAgendaId, String id, RuleBo rule) {
        AgendaItemBo obj = new AgendaItemBo();
        obj.setAgendaId(agendaId);
        obj.setId(id);
        obj.setVersionNumber(0L);
        obj.setRule(rule);
        obj.setSubAgendaId(sunAgendaId);
        obj.setWhenTrue(null);
        obj.setWhenFalse(null);
        obj.setAlways(null);
        obj.setDataObjectService(mockDataObjectService);
        return obj;
    }

    private AgendaItemDefinition getAgendItemDefinitionBuilder() {
        AgendaItemDefinition.Builder itemDefinition = AgendaItemDefinition.Builder.create("A", "B");
        itemDefinition.setRuleId("C");
        itemDefinition.setSubAgendaId("D");
        itemDefinition.setWhenTrueId("E");
        itemDefinition.setWhenFalseId("F");
        itemDefinition.setAlwaysId("G");
        itemDefinition.setRule(RuleDefinition.Builder.create("H", "I", "J", "K", "L"));
        itemDefinition.setSubAgenda(AgendaDefinition.Builder.create("M", "N", "O", "P"));
        itemDefinition.setWhenTrue(AgendaItemDefinition.Builder.create("Q", "R"));
        itemDefinition.setWhenFalse(AgendaItemDefinition.Builder.create("S", "T"));
        itemDefinition.setAlways(AgendaItemDefinition.Builder.create("U", "V"));
        itemDefinition.setVersionNumber(0L);
        return itemDefinition.build();
    }
}
