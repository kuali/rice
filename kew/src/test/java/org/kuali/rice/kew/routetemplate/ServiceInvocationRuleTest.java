/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.routetemplate;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.clientapp.WorkflowDocument;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.workflow.test.FakeService;
import org.kuali.workflow.test.KEWTestCase;
import org.kuali.workflow.test.FakeServiceImpl.Invocation;


/**
 * Tests that a groovy expression in a rule can invoke an arbitrary service on the KSB 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ServiceInvocationRuleTest extends KEWTestCase {
    protected void loadTestData() throws Exception {
        loadXmlFile("ServiceInvokingRule.xml");
    }

    @Test public void testServiceInvokingRule() throws WorkflowException {
        // test that we can get the service to start with
        FakeService fakeService = (FakeService) GlobalResourceLoader.getService(new QName("fake", "fakeService-remote"));
        assertNotNull(fakeService);
        
        
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("arh14"), "ServiceInvocationRuleTest");
        doc.routeDocument("routing");

        // no requests whatsoever were sent...we're done
        assertTrue(doc.stateIsFinal());
        
        fakeService = (FakeService) GlobalResourceLoader.getService(new QName("fake", "fakeService-remote"));
        
        assertEquals(1, fakeService.getInvocations().size());
        Invocation invocation = fakeService.getInvocations().get(0);
        assertEquals("method2", invocation.methodName);
    }
}
