package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition
import org.junit.Test
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition
import org.kuali.rice.krms.api.repository.term.TermDefinition
import org.junit.Assert
import org.kuali.rice.krms.api.repository.context.ContextDefinition
import org.kuali.rice.krms.api.repository.TermSpecificationDefinitionTest
import org.kuali.rice.krms.api.repository.ContextDefinitionTest
import org.kuali.rice.krms.api.repository.TermDefinitionTest
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition
import org.kuali.rice.krms.api.repository.KrmsAttributeDefinitionTest

/**
 * Test translation from BO to model object and back again
 */
class BoTranslationTest {

    @Test
    public void testTermBoTranslation() {
        TermDefinition termDef = TermDefinitionTest.buildFullTermDefinition();

        Assert.assertEquals(termDef, TermBo.to(TermBo.from(termDef)));
    }

    @Test
    public void testContextBoTranslation() {
       ContextDefinition myContext = ContextDefinitionTest.buildFullContextDefinition();

        Assert.assertEquals(myContext, ContextBo.to(ContextBo.from(myContext)));
    }

    @Test
    void testTermSpecificationBoTranslation() {
        TermSpecificationDefinition termSpecDef = TermSpecificationDefinitionTest.buildFullTermSpecificationDefinition();

        Assert.assertEquals(termSpecDef, TermSpecificationBo.to(TermSpecificationBo.from(termSpecDef)));
    }

    @Test
    void testKrmsAttributeBoTranslation() {
        KrmsAttributeDefinition attr = KrmsAttributeDefinitionTest.buildFullKrmsAttributeDefinition();

        Assert.assertEquals(attr, KrmsAttributeDefinitionBo.to(KrmsAttributeDefinitionBo.from(attr)));
    }
}
