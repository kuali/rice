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
package org.kuali.rice.krms.test;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeBoService;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.ContextBoServiceImpl;
import org.kuali.rice.krms.impl.repository.KrmsTypeBoServiceImpl;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.krms.impl.repository.TermBoServiceImpl;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@BaselineMode(Mode.CLEAR_DB)
public class TermRelatedBoTest extends AbstractBoTest {

    private TermBoService termBoService;
    private ContextBoService contextRepository;
    private KrmsTypeBoService krmsTypeBoService;

    @Before
    public void setup() {

        // wire up BO services

        termBoService = new TermBoServiceImpl();

        // TODO: fix
        ((TermBoServiceImpl)termBoService).setDataObjectService(GlobalResourceLoader.<DataObjectService>getService(
                "dataObjectService"));

        contextRepository = new ContextBoServiceImpl();
        ((ContextBoServiceImpl)contextRepository).setDataObjectService(getDataObjectService());

        krmsTypeBoService = new KrmsTypeBoServiceImpl();
        ((KrmsTypeBoServiceImpl)krmsTypeBoService).setDataObjectService(getDataObjectService());
    }

    @Test
    public void creationTest() {

        // create prerequisite objects
        ContextDefinition contextDefinition = createContext();
        KrmsTypeDefinition krmsTermResolverTypeDefinition = createTermResolverType();

        // output TermSpec
        TermSpecificationDefinition outputTermSpec =
                TermSpecificationDefinition.Builder.create(null, "outputTermSpec", contextDefinition.getId(),
                        "java.lang.String").build();
        outputTermSpec = termBoService.createTermSpecification(outputTermSpec);

        // prereq TermSpec
        TermSpecificationDefinition prereqTermSpec =
                TermSpecificationDefinition.Builder.create(null, "prereqTermSpec", contextDefinition.getId(),
                        "java.lang.String").build();
        prereqTermSpec = termBoService.createTermSpecification(prereqTermSpec);

        // TermResolver
        TermResolverDefinition termResolverDef =
                TermResolverDefinition.Builder.create(null, "KRMS", "testResolver", krmsTermResolverTypeDefinition.getId(),
                        TermSpecificationDefinition.Builder.create(outputTermSpec),
                        Collections.singleton(TermSpecificationDefinition.Builder.create(prereqTermSpec)),
                        null,
                        Collections.singleton("testParamName")).build();
        termResolverDef = termBoService.createTermResolver(termResolverDef);

        // Term Param
        TermParameterDefinition.Builder termParamBuilder =
                TermParameterDefinition.Builder.create(null, null, "testParamName", "testParamValue");

        // Term
        TermDefinition termDefinition =
                TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(outputTermSpec), Collections.singletonList(termParamBuilder)).build();
        termBoService.createTerm(termDefinition);
    }

    /**
     * Verify that Terms can be updated (including modification of an existing TermParameter) by the TermBoServiceImpl
     */
    @Test
    public void updateTermChangeParameterTest() {
        TermDefinition termDefinition = createTermForUpdate();

        // Change some things so that we can verify the updates occurred
        TermDefinition.Builder updateTermBuilder = TermDefinition.Builder.create(termDefinition);
        updateTermBuilder.setDescription("updated description");
        assertTrue(updateTermBuilder.getParameters().size() == 1);
        updateTermBuilder.getParameters().get(0).setValue("updatedParamValue");

        termBoService.updateTerm(updateTermBuilder.build());

        // check that the update stuck
        TermDefinition updatedTerm = termBoService.getTerm(termDefinition.getId());

        assertFalse("descriptions should not be equal after update",
                updatedTerm.getDescription().equals(termDefinition.getDescription()));

        assertTrue(updatedTerm.getParameters().size() == 1);

        TermParameterDefinition updatedParam = updatedTerm.getParameters().get(0);
        assertTrue(updatedParam.getValue().equals("updatedParamValue"));
    }

    /**
     * Verify that Terms can be updated (including replacement of a TermParameter) by the TermBoServiceImpl
     */
    @Test
    public void updateTermReplaceParameterTest() {
        TermDefinition termDefinition = createTermForUpdate();

        // Change some things so that we can verify the updates occurred
        TermDefinition.Builder updateTermBuilder = TermDefinition.Builder.create(termDefinition);
        updateTermBuilder.setDescription("updated description");
        assertTrue(updateTermBuilder.getParameters().size() == 1);
        updateTermBuilder.getParameters().clear();
        updateTermBuilder.getParameters().add(TermParameterDefinition.Builder.create(null, termDefinition.getId(), "secondParamName", "secondParamValue"));

        termBoService.updateTerm(updateTermBuilder.build());

        // check that the update stuck
        TermDefinition updatedTerm = termBoService.getTerm(termDefinition.getId());

        assertFalse("descriptions should not be equal after update",
                updatedTerm.getDescription().equals(termDefinition.getDescription()));

        assertTrue(updatedTerm.getParameters().size() == 1);

        TermParameterDefinition secondParam = updatedTerm.getParameters().get(0);
        assertTrue(secondParam.getValue().equals("secondParamValue"));
    }

    private ContextDefinition createContext() {

        // KrmsType for context
        KrmsTypeDefinition krmsContextTypeDefinition = KrmsTypeDefinition.Builder.create("KrmsTestContextType", "KRMS").build();
        krmsContextTypeDefinition = krmsTypeBoService.createKrmsType(krmsContextTypeDefinition);

        // Context
        ContextDefinition.Builder contextBuilder = ContextDefinition.Builder.create("KRMS", "testContext");
        contextBuilder.setTypeId(krmsContextTypeDefinition.getId());
        ContextDefinition contextDefinition = contextBuilder.build();
        contextDefinition = contextRepository.createContext(contextDefinition);

        return contextDefinition;
    }

    private TermDefinition createTermForUpdate() {
        ContextDefinition contextDefinition = createContext();

        // TermSpec -- we need one to create a term
        TermSpecificationDefinition termSpec =
                TermSpecificationDefinition.Builder.create(null, "TermUpdateTestTermSpec", contextDefinition.getId(),
                        "java.lang.String").build();
        termSpec = termBoService.createTermSpecification(termSpec);

        // Term -- create the term that we'll update
        List<TermParameterDefinition.Builder> paramBuilders =
                Arrays.asList(TermParameterDefinition.Builder.create(null, null, "paramName", "paramValue"));
        TermDefinition termDefinition =
                TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(termSpec), paramBuilders).build();
        termDefinition = termBoService.createTerm(termDefinition);
        return termDefinition;
    }

    private KrmsTypeDefinition createTermResolverType() {
        // KrmsType for TermResolver
        KrmsTypeDefinition krmsTermResolverTypeDefinition = KrmsTypeDefinition.Builder.create("KrmsTestResolverType", "KRMS").build();
        krmsTermResolverTypeDefinition = krmsTypeBoService.createKrmsType(krmsTermResolverTypeDefinition);

        return krmsTermResolverTypeDefinition;
    }
}
