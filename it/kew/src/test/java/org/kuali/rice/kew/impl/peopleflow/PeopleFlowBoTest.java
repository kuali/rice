package org.kuali.rice.kew.impl.peopleflow;

import org.junit.Test;
import org.kuali.rice.core.api.namespace.Namespace;
import org.kuali.rice.kew.impl.type.KewAttributeDefinitionBo;
import org.kuali.rice.kew.impl.type.KewTypeAttributeBo;
import org.kuali.rice.kew.impl.type.KewTypeBo;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import sun.security.krb5.ServiceName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import static org.junit.Assert.fail;

/**
 * Test the basic persistence of business objects related to PeopleFlows
 */
public class PeopleFlowBoTest extends KEWTestCase {

    private BusinessObjectService boService;

    @org.junit.Before
    public void setupBoService() {
        boService = KRADServiceLocator.getBusinessObjectService();
    }

    @Test
    public void testKewTypeBoBasicPersist() {
        KewTypeBoBuilder builder = new KewTypeBoBuilder("testType", "testNamespace");

        boService.save(builder.build());
        try {
            // same info again should be a no go
            boService.save(builder.build());
            fail("this shoould violate unique constraints");
        } catch (Exception e) {
            // good
        }
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

            boService.save(attributeDefn);

            KewTypeAttributeBo typeAttribute = new KewTypeAttributeBo();
            typeAttribute.setSequenceNumber(i);
            typeAttribute.setAttributeDefinition(attributeDefn);
            kewTypeBo.getAttributes().add(typeAttribute);
        }

        boService.save(kewTypeBo);
    }

    @Test
    public void testPeopleFlowBoPersist() {
        testKewTypeBoFullPersist();

        Map<String,String> keysMap = new HashMap<String, String>();
        keysMap.put("name", "testType");
        keysMap.put("namespace", "testNamespace");

        KewTypeBo kewTypeBo = boService.findByPrimaryKey(KewTypeBo.class, keysMap);

        // minimal peopleflow
        PeopleFlowBo peopleFlowBo = new PeopleFlowBo();
        peopleFlowBo.setDescription("description of testPeopleFlow");
        peopleFlowBo.setName("testPeopleFlow");
        peopleFlowBo.setNamespace("testNamespace");
        peopleFlowBo.setTypeId(kewTypeBo.getId());

        boService.save(peopleFlowBo);

        // fill out peopleflow
        KewTypeAttributeBo attribute = kewTypeBo.getAttributes().get(0);

        PeopleFlowAttributeBo peopleFlowAttr = new PeopleFlowAttributeBo();
        peopleFlowAttr.setAttributeDefinition(attribute.getAttributeDefinition());
        peopleFlowAttr.setPeopleFlowId(peopleFlowBo.getId());
        peopleFlowAttr.setValue("testAttrValue");

        peopleFlowBo.getAttributeBos().add(peopleFlowAttr);

        PeopleFlowMemberBo peopleFlowMember = new PeopleFlowMemberBo();
        peopleFlowMember.setMemberTypeCode("P");
        peopleFlowMember.setMemberId("admin");
        peopleFlowMember.setPriority(1);

        peopleFlowBo.getMembers().add(peopleFlowMember);

        boService.save(peopleFlowBo);
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
