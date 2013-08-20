/*
 * Copyright 2006-2013 The Kuali Foundation
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
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.test.KRADTestCase;
import static org.junit.Assert.*;

public class LegacyDataAdapterTest extends KRADTestCase{
    @Test
    public void testGetPropertyType() throws Exception {
        //Confirm simple nested property type works
        ParameterBo param = DataObjectUtils.newInstance(ParameterBo.class);
        Class propertyType = KRADServiceLocatorWeb.getLegacyDataAdapter().getPropertyType(param,"namespaceCode");
        assertTrue("PropertyType is String",propertyType.isAssignableFrom(String.class));
        //Confirm simple nested property type works
        propertyType = KRADServiceLocatorWeb.getLegacyDataAdapter().getPropertyType(param,"component.name");
        assertTrue("PropertyType is String",propertyType.isAssignableFrom(String.class));
        //Confirm double nested property type works
        propertyType =  KRADServiceLocatorWeb.getLegacyDataAdapter().getPropertyType(param,"component.namespace.name");
        assertTrue("PropertyType is String",propertyType.isAssignableFrom(String.class));
    }

}
