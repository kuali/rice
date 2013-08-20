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

package org.kuali.rice.core.data;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.coreservice.impl.component.ComponentBo;
import org.kuali.rice.coreservice.impl.component.ComponentId;
import org.kuali.rice.coreservice.impl.component.ComponentSetBo;
import org.kuali.rice.coreservice.impl.component.DerivedComponentBo;
import org.kuali.rice.coreservice.impl.namespace.NamespaceBo;
import org.kuali.rice.coreservice.impl.parameter.ParameterTypeBo;
import org.kuali.rice.coreservice.impl.style.StyleBo;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

import java.sql.Timestamp;

import static org.junit.Assert.*;

/**
 * Tests to confirm JPA mapping for the Core Service module data objects
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class CoreServiceJpaDataTest extends KRADTestCase {
    @Test
    public void testNameSpaceBoDataObject() throws Exception{
        assertTrue("NameSpaceBo is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(NamespaceBo.class));
        setupNameSpaceBoDataObjectAndSave();

        NamespaceBo namespaceBoFetched = KRADServiceLocator.getDataObjectService().find(NamespaceBo.class,"KR-TST");
        assertTrue("Namespace BO refetched after save",
                namespaceBoFetched != null && StringUtils.equals(namespaceBoFetched.getName(),
                        "Kuali Rice Test Namespace"));
    }

    @Test
    public void testComponentBoDataObject() throws Exception{
        assertTrue("ComponentBo is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(ComponentBo.class));
        setupNameSpaceBoDataObjectAndSave();
        setupComponentBoDataObjectAndSave();

        ComponentBo componentBoFetched = KRADServiceLocator.getDataObjectService().find(ComponentBo.class,new ComponentId("KR-TST","All"));
        assertTrue("ComponentBo refetched after save", componentBoFetched != null &&
                StringUtils.equals(componentBoFetched.getName(), "All"));
        assertTrue("Campus Type Bo fetched from Campus BO relationship", componentBoFetched.getNamespace() != null
                && StringUtils.equals(componentBoFetched.getNamespace().getName(), "Kuali Rice Test Namespace"));

    }

    @Test
    public void testComponentSetBoDataObject() throws Exception{
        assertTrue("ComponentSetBo is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(ComponentSetBo.class));
        setupComponentSetBoDataObjectAndSave();

        ComponentSetBo componentSetBo = KRADServiceLocator.getDataObjectService().find(ComponentSetBo.class,"DD:RICETST");
        assertTrue("ComponentBo refetched after save", componentSetBo != null &&
                StringUtils.equals(componentSetBo.getChecksum(), "ASDFQWER"));
    }

    @Test
    public void testDerviedComponentBoDataObject() throws Exception{
        assertTrue("DerivedComponentBo is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(DerivedComponentBo.class));
        setupDerivedComponentBoDataObjectAndSave();

        DerivedComponentBo derivedComponentBoFetched = KRADServiceLocator.getDataObjectService().find(DerivedComponentBo.class,new ComponentId("KR-TST","ComponentBo"));
        assertTrue("DerivedComponentBo refetched after save", derivedComponentBoFetched != null &&
                StringUtils.equals(derivedComponentBoFetched.getCode(), "ComponentBo"));
    }

    @Test
    public void testParameterTypeBoDataObject() throws Exception{
        assertTrue("ParameterTypeBo is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(ParameterTypeBo.class));
        setupParameterTypeBoDataObjectAndSave();

        ParameterTypeBo parameterTypeBoFetched = KRADServiceLocator.getDataObjectService().find(ParameterTypeBo.class,"HELP");
        assertTrue("ParameterTypeBo refetched after save", parameterTypeBoFetched != null &&
                StringUtils.equals(parameterTypeBoFetched.getName(),"Help"));
    }

    @Test
    public void testStyleBoDataObject() throws Exception{
        assertTrue("StyleBO is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(StyleBo.class));
        setupStyleBoDataObjectAndSave();

        StyleBo styleBo = KRADServiceLocator.getDataObjectService().find(StyleBo.class,"1234");
        assertTrue("StyleBo refetched after save", styleBo != null &&
                StringUtils.equals(styleBo.getName(),"TestCSS"));
    }

    private void setupStyleBoDataObjectAndSave(){
        StyleBo styleBo = new StyleBo();
        styleBo.setActive(true);
        styleBo.setId("1234");
        styleBo.setName("TestCSS");
        styleBo.setXmlContent("<xml>something</xml>");

        KRADServiceLocator.getDataObjectService().save(styleBo);

    }

    private void setupParameterTypeBoDataObjectAndSave(){
        ParameterTypeBo parameterTypeBo = new ParameterTypeBo();
        parameterTypeBo.setActive(true);
        parameterTypeBo.setCode("HELP");
        parameterTypeBo.setName("Help");

        KRADServiceLocator.getDataObjectService().save(parameterTypeBo);
    }

    private void setupDerivedComponentBoDataObjectAndSave(){
          DerivedComponentBo derivedComponentBo = new DerivedComponentBo();
          derivedComponentBo.setCode("ComponentBo");
          derivedComponentBo.setComponentSetId("DD:TSTKR");
          derivedComponentBo.setName("Derived component");
          derivedComponentBo.setNamespaceCode("KR-TST");

        KRADServiceLocator.getDataObjectService().save(derivedComponentBo);
    }

    private void setupComponentSetBoDataObjectAndSave(){
        ComponentSetBo componentSetBo = new ComponentSetBo();
        componentSetBo.setChecksum("ASDFQWER");
        componentSetBo.setComponentSetId("DD:RICETST");
        componentSetBo.setLastUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
        componentSetBo.setVersionNumber(1L);

        KRADServiceLocator.getDataObjectService().save(componentSetBo);
    }

    private void setupComponentBoDataObjectAndSave(){
        ComponentBo componentBo = new ComponentBo();
        componentBo.setActive(true);
        componentBo.setCode("All");
        componentBo.setName("All");
        componentBo.setNamespaceCode("KR-TST");

        KRADServiceLocator.getDataObjectService().save(componentBo);
    }

    private void setupNameSpaceBoDataObjectAndSave(){
        NamespaceBo namespaceBo = new NamespaceBo();
        namespaceBo.setActive(true);
        namespaceBo.setApplicationId("RICE");
        namespaceBo.setCode("KR-TST");
        namespaceBo.setName("Kuali Rice Test Namespace");

        KRADServiceLocator.getDataObjectService().save(namespaceBo);
    }


}
