/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.impl.peopleflow;

import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.impl.type.KewAttributeDefinitionBo;
import org.kuali.rice.kew.impl.type.KewTypeAttributeBo;
import org.kuali.rice.kew.impl.type.KewTypeBo;
import org.kuali.rice.kew.responsibility.service.ResponsibilityIdService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.PersistenceOption;
import org.springframework.orm.jpa.JpaSystemException;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

import static org.junit.Assert.*;

/**
 * Test the basic persistence of business objects related to PeopleFlows.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PeopleFlowBoTest extends KEWTestCase {

    private DataObjectService dataObjectService;
    private ResponsibilityIdService responsibilityIdService;

    @org.junit.Before
    public void setupBoService() {
        dataObjectService = KradDataServiceLocator.getDataObjectService();
        responsibilityIdService = KEWServiceLocator.getResponsibilityIdService();
    }

    @Test(expected = JpaSystemException.class)
    public void testKewTypeBoBasicPersist() {
        KewTypeBoBuilder builder = new KewTypeBoBuilder("testType", "testNamespace");

        dataObjectService.save(builder.build(), PersistenceOption.FLUSH);
        // try to persist the same information again, it should fail because of the unique constraint on name + namespace
        dataObjectService.save(builder.build(), PersistenceOption.FLUSH);
    }
    @Test
    public void testKewTypeBoFullPersist() {
        KewTypeBoBuilder builder = new KewTypeBoBuilder("testType", "testNamespace").setServiceName("testService");
        KewTypeBo kewTypeBo = builder.build();

        for (int i=1; i<=3; i++) {
            KewAttributeDefinitionBo attributeDefn = new KewAttributeDefinitionBo();
            attributeDefn.setName("attrDef"+i);
            attributeDefn.setDescription("this is a description of attrDef" + i);
            attributeDefn.setComponentName("componentName" + i);
            attributeDefn.setLabel("label" + i);
            attributeDefn.setNamespace(kewTypeBo.getNamespace());

            attributeDefn = dataObjectService.save(attributeDefn);

            KewTypeAttributeBo typeAttribute = new KewTypeAttributeBo();
            typeAttribute.setSequenceNumber(i);
            typeAttribute.setAttributeDefinition(attributeDefn);
            kewTypeBo.getAttributes().add(typeAttribute);
            typeAttribute.setType(kewTypeBo);
        }

        dataObjectService.save(kewTypeBo);
    }

    @Test
    public void testPeopleFlowPersonMembers() {
        PeopleFlowMemberBo peopleFlowMember = new PeopleFlowMemberBo();
        peopleFlowMember.setMemberType(MemberType.PRINCIPAL);
        peopleFlowMember.setMemberId("admin");
        peopleFlowMember.setPriority(1);
        peopleFlowMember.setResponsibilityId(responsibilityIdService.getNewResponsibilityId());
        assertNotNull(peopleFlowMember.getPerson());
        assertEquals("admin", peopleFlowMember.getPerson().getPrincipalName());

        PeopleFlowDelegateBo peopleFlowDelegate = new PeopleFlowDelegateBo();
        peopleFlowDelegate.setMemberType(MemberType.PRINCIPAL);
        peopleFlowDelegate.setMemberId("admin");
        peopleFlowDelegate.setDelegationTypeCode(DelegationType.PRIMARY.getCode());
        peopleFlowDelegate.setResponsibilityId(responsibilityIdService.getNewResponsibilityId());
        assertNotNull(peopleFlowDelegate.getPerson());
        assertEquals("admin", peopleFlowDelegate.getPerson().getPrincipalName());
    }

    @Test
    public void testPeopleFlowBoPersist() {
        testKewTypeBoFullPersist();

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("name", "testType"), equal("namespace", "testNamespace"));

        QueryResults<KewTypeBo> results = dataObjectService.findMatching(KewTypeBo.class, criteria.build());
        assertEquals(1, results.getResults().size());
        KewTypeBo kewTypeBo = results.getResults().get(0);

        // minimal peopleflow
        PeopleFlowBo peopleFlowBo = new PeopleFlowBo();
        peopleFlowBo.setDescription("description of testPeopleFlow");
        peopleFlowBo.setName("testPeopleFlow");
        peopleFlowBo.setNamespaceCode("testNamespace");
        peopleFlowBo.setTypeId(kewTypeBo.getId());
        
        peopleFlowBo = dataObjectService.save(peopleFlowBo);

        // fill out peopleflow
        KewTypeAttributeBo attribute = kewTypeBo.getAttributes().get(0);

        PeopleFlowAttributeBo peopleFlowAttr = new PeopleFlowAttributeBo();
        peopleFlowAttr.setPeopleFlow(peopleFlowBo);
        peopleFlowAttr.setAttributeDefinition(attribute.getAttributeDefinition());
        peopleFlowAttr.setPeopleFlow(peopleFlowBo);
        peopleFlowAttr.setValue("testAttrValue");

        peopleFlowBo.getAttributeBos().add(peopleFlowAttr);

        PeopleFlowMemberBo peopleFlowMember = new PeopleFlowMemberBo();
        peopleFlowMember.setPeopleFlow(peopleFlowBo);
        peopleFlowMember.setMemberType(MemberType.PRINCIPAL);
        peopleFlowMember.setMemberId("admin");
        peopleFlowMember.setPriority(1);
        peopleFlowMember.setResponsibilityId(responsibilityIdService.getNewResponsibilityId());

        peopleFlowBo.getMembers().add(peopleFlowMember);

        PeopleFlowDelegateBo peopleFlowDelegate1 = new PeopleFlowDelegateBo();
        peopleFlowDelegate1.setPeopleFlowMember(peopleFlowMember);
        peopleFlowDelegate1.setMemberType(MemberType.GROUP);
        peopleFlowDelegate1.setMemberId("1");
        peopleFlowDelegate1.setDelegationTypeCode(DelegationType.PRIMARY.getCode());
        peopleFlowDelegate1.setResponsibilityId(responsibilityIdService.getNewResponsibilityId());
        peopleFlowMember.getDelegates().add(peopleFlowDelegate1);

        PeopleFlowDelegateBo peopleFlowDelegate2 = new PeopleFlowDelegateBo();
        peopleFlowDelegate2.setPeopleFlowMember(peopleFlowMember);
        peopleFlowDelegate2.setMemberType(MemberType.ROLE);
        peopleFlowDelegate2.setMemberId("2");
        peopleFlowDelegate2.setActionRequestPolicyCode(ActionRequestPolicy.FIRST.getCode());
        peopleFlowDelegate2.setDelegationTypeCode(DelegationType.SECONDARY.getCode());
        peopleFlowDelegate2.setResponsibilityId(responsibilityIdService.getNewResponsibilityId());
        peopleFlowMember.getDelegates().add(peopleFlowDelegate2);

        peopleFlowBo = dataObjectService.save(peopleFlowBo);

        assertNotNull(peopleFlowBo.getId());
        peopleFlowBo = dataObjectService.find(PeopleFlowBo.class, peopleFlowBo.getId());

        assertNotNull(peopleFlowBo);
        assertNotNull(peopleFlowBo.getId());
        assertTrue(peopleFlowBo.getMembers().size() == 1);

        PeopleFlowMemberBo memberBo = peopleFlowBo.getMembers().get(0);
        assertNotNull(memberBo.getId());
        assertEquals(peopleFlowBo.getId(), memberBo.getPeopleFlow().getId());
        assertEquals("admin", memberBo.getMemberId());
        assertEquals(MemberType.PRINCIPAL, memberBo.getMemberType());
        assertNotNull(memberBo.getPerson());
        assertEquals("admin", memberBo.getPerson().getPrincipalName());
        assertEquals(peopleFlowMember.getResponsibilityId(), memberBo.getResponsibilityId());
        assertSame(1, memberBo.getPriority());
        assertTrue(memberBo.getDelegates().size() == 2);

        PeopleFlowDelegateBo delegateBo1 = memberBo.getDelegates().get(0);
        assertNotNull(delegateBo1.getId());
        assertEquals(memberBo.getId(), delegateBo1.getPeopleFlowMember().getId());
        assertEquals("1", delegateBo1.getMemberId());
        assertEquals(MemberType.GROUP, delegateBo1.getMemberType());
        assertEquals(DelegationType.PRIMARY.getCode(), delegateBo1.getDelegationTypeCode());
        assertEquals(peopleFlowDelegate1.getResponsibilityId(), delegateBo1.getResponsibilityId());
        assertNull(delegateBo1.getActionRequestPolicyCode());

        PeopleFlowDelegateBo delegateBo2 = memberBo.getDelegates().get(1);
        assertNotNull(delegateBo2.getId());
        assertEquals(memberBo.getId(), delegateBo2.getPeopleFlowMember().getId());
        assertEquals("2", delegateBo2.getMemberId());
        assertEquals(MemberType.ROLE, delegateBo2.getMemberType());
        assertEquals(DelegationType.SECONDARY.getCode(), delegateBo2.getDelegationTypeCode());
        assertEquals(peopleFlowDelegate2.getResponsibilityId(), delegateBo2.getResponsibilityId());
        assertEquals(ActionRequestPolicy.FIRST.getCode(), delegateBo2.getActionRequestPolicyCode());
    }

    public static KewTypeBo buildMinimalKewTypeBo() {
        KewTypeBo kewTypeBo = new KewTypeBo();
        kewTypeBo.setName("TestType");
        kewTypeBo.setNamespace("TestNamespace");
        return kewTypeBo;
    }

    private static class KewTypeBoBuilder {

        private boolean active = true;
        private String name;
        private String namespace;
        private String serviceName;

        public KewTypeBoBuilder(String name, String namespace) {
            this.name = name;
            this.namespace = namespace;
        }

        public KewTypeBoBuilder setServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public KewTypeBoBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public KewTypeBoBuilder setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public KewTypeBoBuilder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public KewTypeBo build() {
            KewTypeBo kewTypeBo = new KewTypeBo();
            kewTypeBo.setActive(active);
            kewTypeBo.setName(name);
            kewTypeBo.setNamespace(namespace);
            kewTypeBo.setServiceName(serviceName);
            return kewTypeBo;
        }
    }

}
