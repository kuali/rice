/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.core.util;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.kew.test.KEWTestCase;

/**
 * Tests {@link RiceService} annotation and the {@link GRLServiceInjectionPostProcessor}.
 * For now this is a KEW-based test case living in the KEW unit tests because the
 * KEW unit test suite happens to exercise Rice startup and config pretty well.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Ignore("This test only works if it's the only test run in the suite so it always fails in CI")
public class RiceServiceOverrideTest extends KEWTestCase {
    /**
     * Overridden to introduce our own client-side beans 
     * @see org.kuali.rice.kew.test.KEWTestCase#getKEWBootstrapSpringFile()
     */
    @Override
    protected String getKEWBootstrapSpringFile() {
        return "classpath:/org/kuali/rice/core/util/RiceServiceOverrideTestSpringBeans.xml";
    }

    @Test public void testServiceInjection() {
        RiceServiceInjectedObject o = RiceServiceInjectedObject.beans.get("riceServiceInjectedObject");
        assertNotNull(o.als);
        assertNotNull(o.ales);
        assertNotNull(o.ars);
        assertEquals(o.wireMeInSpring, "A Spring-wired value");
    }

    @Test public void testServiceInjectionWithInheritance() {
        RiceServiceInjectedDescendent o = (RiceServiceInjectedDescendent) RiceServiceInjectedDescendent.beans.get("riceServiceInjectedDescendent");
        
        // all of the ancestor members should be set
        
        assertNotNull(o.als);
        assertNotNull(o.ales);
        assertNotNull(o.ars);
        assertEquals(o.wireMeInSpring, "A Spring-wired value in the descendent");
        
        // as well as the locally declared members
        
        assertNotNull(o.ats);
        assertNotNull(o.messageHelper);
        assertNotNull(o.ns);
    }
    
    // we subclassed and overrode the KNS document service; see if the lazy initialization worked
    @Test public void testDocumentServiceOverriding() {
        ClientDocumentServiceImpl d = ClientDocumentServiceImpl.me;
        assertNotNull(d);
        assertNotNull(d.getBusinessObjectService());
        assertNotNull(d.getDocumentDao());
        assertNotNull(d.getWorkflowDocumentService());
    }
    
    // we subclassed and overrode the KNS document service; see if the lazy initialization worked
    @Test public void testDocumentDaoOverriding() {
        ClientDocumentDaoOjb d = ClientDocumentDaoOjb.me;
        assertNotNull(d);
        assertNotNull(d.getBusinessObjectDao());
        assertNotNull(d.getDbPlatform());
    }
}
