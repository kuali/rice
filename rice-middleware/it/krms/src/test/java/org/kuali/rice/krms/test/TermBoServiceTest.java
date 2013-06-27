/**
 * Copyright 2005-2013 The Kuali Foundation
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
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.test.BaselineTestCase;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class TermBoServiceTest extends AbstractBoTest {
	
	TermBoService termBoService = null;

    @Override
	@Before
	public void setUp() throws Exception {
        super.setUp();
		termBoService = KrmsRepositoryServiceLocator.getTermBoService();
	}

	@Test
	public void testPersistTermSpecificationContextIds() {

        ContextDefinition context1 = createContextDefinition("KR-SAP", "TermBoServiceTest-Context1", Collections.<String,String>emptyMap());
        ContextDefinition context2 = createContextDefinition("KR-SAP", "TermBoServiceTest-Context2", Collections.<String,String>emptyMap());

        termBoService = GlobalResourceLoader.getService("termBoService");

		TermSpecificationDefinition.Builder termSpecBuilder =
			TermSpecificationDefinition.Builder.create(null, "1", "testTermSpec", "java.lang.String");

        termSpecBuilder.getContextIds().add(context1.getId());
        termSpecBuilder.getContextIds().add(context2.getId());

        TermSpecificationDefinition termSpecificationDefinition = termBoService.createTermSpecification(termSpecBuilder.build());

        assertNotNull(termSpecificationDefinition);
        assertTrue(termSpecificationDefinition.getContextIds().size() == 2);
        for (String contextId : Arrays.asList(context1.getId(), context2.getId())) {
            assertTrue(termSpecificationDefinition.getContextIds().contains(contextId));
        }

        TermSpecificationDefinition fetchedTermSpec = termBoService.getTermSpecificationById(termSpecificationDefinition.getId());

        for (String contextId : Arrays.asList(context1.getId(), context2.getId())) {
            assertTrue("https://jira.kuali.org/browse/KULRICE-9850 ", fetchedTermSpec.getContextIds().contains(contextId));
        }
	}
	
}
