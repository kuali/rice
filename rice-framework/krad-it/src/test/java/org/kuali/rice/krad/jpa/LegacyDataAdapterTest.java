/*
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

package org.kuali.rice.krad.jpa;

import org.junit.Test;
import org.kuali.rice.coreservice.impl.parameter.ParameterBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtension;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.bo.Account;
import org.kuali.rice.krad.test.document.bo.AccountExtension;
import org.kuali.rice.krad.util.KRADUtils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LegacyDataAdapterTest extends KRADTestCase {

    private LegacyDataAdapter legacyDataAdapter;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        legacyDataAdapter = KRADServiceLocatorWeb.getLegacyDataAdapter();
    }

    @Test
    public void testGetPropertyType() throws Exception {
        //Confirm simple nested property type works
        ParameterBo param = KRADUtils.createNewObjectFromClass(ParameterBo.class);
        Class propertyType = legacyDataAdapter.getPropertyType(param, "namespaceCode");
        assertTrue("PropertyType is String",propertyType.isAssignableFrom(String.class));
        //Confirm simple nested property type works
        propertyType = legacyDataAdapter.getPropertyType(param, "component.name");
        assertTrue("PropertyType is String",propertyType.isAssignableFrom(String.class));
        //Confirm double nested property type works
        propertyType =  legacyDataAdapter.getPropertyType(param, "component.namespace.name");
        assertTrue("PropertyType is String",propertyType.isAssignableFrom(String.class));
    }

    /**
     * Verifies that new instances of the extension object are created properly when in Legacy mode.
     * The {@link org.kuali.rice.krad.test.document.bo.Account} object has it's extension mapped both using the
     * legacy KNS and non-Legacy KRAD approaches and it extends from PersistableBusinessObjectBase.
     */
    @Test
    @Legacy
    public void testGetExtension_Legacy() throws Exception {
        testGetExtension();
    }

    /**
     * Verifies that new instances of the extension object are created properly when using KRAD.
     * The {@link org.kuali.rice.krad.test.document.bo.Account} object has it's extension mapped both using the
     * legacy KNS and non-Legacy KRAD approaches and it extends from PersistableBusinessObjectBase.
     */
    @Test
    public void testGetExtension() throws Exception {
        Account account = new Account();
        Object extension = account.getExtension();
        assertNotNull(extension);
        assertTrue(extension instanceof AccountExtension);

        extension = legacyDataAdapter.getExtension(Account.class);
        assertNotNull(extension);
        assertTrue(extension instanceof AccountExtension);
    }

}
