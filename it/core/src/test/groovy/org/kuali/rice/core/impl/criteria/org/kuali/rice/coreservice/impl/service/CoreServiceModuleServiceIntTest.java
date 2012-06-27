/*
 * Copyright 2006-2012 The Kuali Foundation
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

package org.kuali.rice.core.impl.criteria.org.kuali.rice.coreservice.impl.service;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.test.CORETestCase;
import org.kuali.rice.coreservice.impl.namespace.NamespaceBo;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration test for the CoreServiceModuleService
 */
public class CoreServiceModuleServiceIntTest extends CORETestCase {

    private static final String KUALI_NAMESPACE_CODE = "KUALI";

    private ModuleService coreServiceModuleService;

    @Before
    public void setupServiceUnderTest() {
        KualiModuleService kualiModuleService = GlobalResourceLoader.getService("kualiModuleService");

        coreServiceModuleService = kualiModuleService.getResponsibleModuleService(NamespaceBo.class);
    }

    /**
     * Test that we can load the Namespace EBO which this ModuleService is responsible for
     */
    @Test public void testGetNamespaceEbo() {
        assertNotNull("coreServiceModuleService wasn't successfully configured", coreServiceModuleService);

        NamespaceBo kualiNamespace = coreServiceModuleService.getExternalizableBusinessObject(NamespaceBo.class,
                Collections.<String,Object>singletonMap("code", KUALI_NAMESPACE_CODE));

        assertNotNull("kualiNamespace wasn't successfully loaded", kualiNamespace);
        assertEquals("kualiNamespace doesn't have the requested namespace code",
                kualiNamespace.getCode(),KUALI_NAMESPACE_CODE);

        assertNull("non-null result for bogus query",
                coreServiceModuleService.getExternalizableBusinessObject(NamespaceBo.class,
                        Collections.<String,Object>singletonMap("code", "Kwisatz Haderach")));

        List<NamespaceBo> namespaces = coreServiceModuleService.getExternalizableBusinessObjectsList(NamespaceBo.class,
                Collections.<String,Object>emptyMap());

        assertFalse("namespaces weren't successfully retrieved", CollectionUtils.isEmpty(namespaces));
        assertTrue(namespaces.size() > 1);
        assertTrue(namespaces.contains(kualiNamespace));

        namespaces = coreServiceModuleService.getExternalizableBusinessObjectsList(NamespaceBo.class,
                Collections.<String,Object>singletonMap("code", KUALI_NAMESPACE_CODE));

        assertFalse("namespaces weren't successfully retrieved", CollectionUtils.isEmpty(namespaces));
        assertTrue(namespaces.size() == 1);
        assertTrue(namespaces.contains(kualiNamespace));

        namespaces = coreServiceModuleService.getExternalizableBusinessObjectsList(NamespaceBo.class,
                Collections.<String,Object>singletonMap("code", "Kwisatz Haderach"));

        assertTrue("no namespaces should have been returned", CollectionUtils.isEmpty(namespaces));
    }

}
