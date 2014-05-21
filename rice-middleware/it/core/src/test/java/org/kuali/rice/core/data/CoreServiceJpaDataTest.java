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

package org.kuali.rice.core.data;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import org.kuali.rice.core.test.CORETestCase;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.rice.coreservice.api.component.Component;
import org.kuali.rice.coreservice.api.namespace.Namespace;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.ParameterKey;
import org.kuali.rice.coreservice.api.style.Style;
import org.kuali.rice.coreservice.impl.component.ComponentBo;
import org.kuali.rice.coreservice.impl.component.ComponentId;
import org.kuali.rice.coreservice.impl.component.ComponentSetBo;
import org.kuali.rice.coreservice.impl.component.DerivedComponentBo;
import org.kuali.rice.coreservice.impl.namespace.NamespaceBo;
import org.kuali.rice.coreservice.impl.parameter.ParameterBo;
import org.kuali.rice.coreservice.impl.parameter.ParameterTypeBo;
import org.kuali.rice.coreservice.impl.style.StyleBo;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.test.BaselineTestCase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests to confirm JPA mapping for the Core Service module data objects
 */
public class CoreServiceJpaDataTest extends CORETestCase {
    public static final String DERIVED_COMPONENT_SET_ID = "DD:TSTKR";

    public static final String NAMESPACE = "KR-TST";
    public static final String STYLE_ID = "1234";
    public static final String STYLE_NAME = "TestCSS";

    private static final String APP_ID = "KR-TST";

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
    public void testDerivedComponentBoDataObject() throws Exception{
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

        ParameterTypeBo parameterTypeBoFetched = KRADServiceLocator.getDataObjectService().find(ParameterTypeBo.class,"PTBO");
        assertTrue("ParameterTypeBo refetched after save", parameterTypeBoFetched != null &&
                StringUtils.equals(parameterTypeBoFetched.getName(),"Parameter Type"));
    }

    @Test
    public void testStyleBoDataObject() throws Exception{
        assertTrue("StyleBO is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(StyleBo.class));
        setupStyleBoDataObjectAndSave();

        StyleBo styleBo = KRADServiceLocator.getDataObjectService().find(StyleBo.class,"1234");
        assertTrue("StyleBo refetched after save", styleBo != null &&
                StringUtils.equals(styleBo.getName(),"TestCSS"));
    }

    @Test
    public void testComponentServiceImpl() throws Exception{
        setupNameSpaceBoDataObjectAndSave();
        setupComponentBoDataObjectAndSave();
        setupDerivedComponentBoDataObjectAndSave();

        Component component = CoreServiceApiServiceLocator.getComponentService().getComponentByCode("KR-TST","All");
        assertTrue("ComponentBo refetched after save", component != null && StringUtils.equals(component.getCode(),
                "All"));

        List<Component> componentList = CoreServiceApiServiceLocator.getComponentService().
                                    getAllComponentsByNamespaceCode("KR-TST");
        assertTrue("getAllComponentsByNamespaceCode refetched after save", componentList != null && componentList.size() == 2);

        componentList = CoreServiceApiServiceLocator.getComponentService().
                                    getActiveComponentsByNamespaceCode("KR-TST");
        assertTrue("getActiveComponentsByNamespaceCode refetched after save", componentList != null && componentList.size() == 2);
        componentList = CoreServiceApiServiceLocator.getComponentService().
                            getDerivedComponentSet(DERIVED_COMPONENT_SET_ID);
        assertTrue("getDerivedComponentSet refetched after save",componentList != null &&
                            componentList.size() == 1);
        DerivedComponentBo derivedComponentBo = DerivedComponentBo.from(componentList.get(0));
        derivedComponentBo.setComponentSetId(null);
        component = DerivedComponentBo.to(derivedComponentBo);
        componentList = new ArrayList<Component>();
        componentList.add(component);
        CoreServiceApiServiceLocator.getComponentService().publishDerivedComponents("TEST",componentList);
        componentList = CoreServiceApiServiceLocator.getComponentService().getDerivedComponentSet("TEST");
        assertTrue("publishDerivedComponents corrected save",componentList != null &&
                componentList.size() == 1);
    }

    @Test
    public void testNamespaceServiceImpl() throws Exception{
        setupNameSpaceBoDataObjectAndSave();

        Namespace namespace = CoreServiceApiServiceLocator.getNamespaceService().getNamespace(NAMESPACE);
        assertTrue("getNamespace retrieved after save",namespace != null &&
                        StringUtils.equals(namespace.getCode(),NAMESPACE));
        List<Namespace> namespaceList = CoreServiceApiServiceLocator.getNamespaceService().
                                            findAllNamespaces();
        assertTrue("findAllNamespaces retrieved after save",namespaceList != null &&
                namespaceList.size() > 0);
    }

    @Test
    public void testStyleServiceImpl() throws Exception{
        setupStyleBoDataObjectAndSave();

        List<String> styleNames = CoreServiceApiServiceLocator.getStyleService().getAllStyleNames();
        assertTrue("getAllStyleNames retrieved correctly", styleNames != null && styleNames.size() == 1);

        Style style = CoreServiceApiServiceLocator.getStyleService().getStyle(STYLE_NAME);
        assertTrue("getStyle retrieved correctly", style != null && StringUtils.equals(STYLE_NAME,style.getName()));

        StyleBo styleBo = new StyleBo();
        styleBo.setActive(true);
        styleBo.setId(STYLE_ID + "23");
        styleBo.setName(STYLE_NAME + "_NEW");
        styleBo.setXmlContent("<xml>something_new</xml>");
        style = StyleBo.to(styleBo);
        CoreServiceApiServiceLocator.getStyleService().saveStyle(style);
        style = CoreServiceApiServiceLocator.getStyleService().getStyle(STYLE_NAME+"_NEW");
        assertTrue("getStyle retrieved correctly", style != null && StringUtils.equals(STYLE_NAME+"_NEW",style.getName()));
    }

    @Test
    public void testParameterServiceImpl() throws Exception{
        setupParameterBoDataObjectAndSave();
        ParameterKey parameterKey = ParameterKey.create(APP_ID, "TST_NM_SPACE", "TST", "TST_PARAM");

        Parameter parameter = CoreServiceApiServiceLocator.getParameterRepositoryService().
                getParameter(parameterKey);
        assertTrue("Parameter fetched correctly after save", parameter != null &&
                StringUtils.equals(parameter.getName(),"TST_PARAM"));
        String value = CoreServiceApiServiceLocator.getParameterRepositoryService().getParameterValueAsString(parameterKey);
        assertTrue("Parameter value fetched correctly", StringUtils.equals(parameter.getValue(),value));
        ParameterBo modifiedParam = ParameterBo.from(parameter);
        modifiedParam.setValue("new value");
        CoreServiceApiServiceLocator.getParameterRepositoryService().updateParameter(ParameterBo.to(modifiedParam));
        value = CoreServiceApiServiceLocator.getParameterRepositoryService().getParameterValueAsString(parameterKey);
        assertTrue("Parameter value fetched correctly", StringUtils.equals(modifiedParam.getValue(),value));
    }

    private void setupParameterBoDataObjectAndSave(){
        NamespaceBo namespaceBo = new NamespaceBo();
        namespaceBo.setActive(true);
        namespaceBo.setApplicationId("RICE");
        namespaceBo.setCode("TST_NM_SPACE");
        namespaceBo.setName("Another Test Namespace");
        namespaceBo = KRADServiceLocator.getDataObjectService().save(namespaceBo, PersistenceOption.FLUSH);

        ParameterTypeBo parameterTypeBo = new ParameterTypeBo();
        parameterTypeBo.setActive(true);
        parameterTypeBo.setCode("PT");
        parameterTypeBo.setName("Another Test Parameter Type");
        parameterTypeBo = KRADServiceLocator.getDataObjectService().save(parameterTypeBo, PersistenceOption.FLUSH);

        ParameterBo parameterBo = new ParameterBo();
        parameterBo.setApplicationId(APP_ID);
        parameterBo.setValue("blah");
        parameterBo.setDescription("descr");
        parameterBo.setParameterTypeCode("PT");
        parameterBo.setParameterType(parameterTypeBo);
        parameterBo.setComponentCode("TST");
        parameterBo.setNamespaceCode("TST_NM_SPACE");
        parameterBo.setNamespace(namespaceBo);
        parameterBo.setEvaluationOperatorCode("A");
        parameterBo.setName("TST_PARAM");
        CoreServiceApiServiceLocator.getParameterRepositoryService().
                    createParameter(ParameterBo.to(parameterBo));

    }

    private void setupStyleBoDataObjectAndSave(){
        StyleBo styleBo = new StyleBo();
        styleBo.setActive(true);
        styleBo.setId("1234");
        styleBo.setName(STYLE_NAME);
        styleBo.setXmlContent("<xml>something</xml>");

        KRADServiceLocator.getDataObjectService().save(styleBo, PersistenceOption.FLUSH);

    }

    private void setupParameterTypeBoDataObjectAndSave(){
        ParameterTypeBo parameterTypeBo = new ParameterTypeBo();
        parameterTypeBo.setActive(true);
        parameterTypeBo.setCode("PTBO");
        parameterTypeBo.setName("Parameter Type");
        parameterTypeBo.setVersionNumber(null);

        KRADServiceLocator.getDataObjectService().save(parameterTypeBo, PersistenceOption.FLUSH);
    }

    private void setupDerivedComponentBoDataObjectAndSave(){
          DerivedComponentBo derivedComponentBo = new DerivedComponentBo();
          derivedComponentBo.setCode("ComponentBo");
          derivedComponentBo.setComponentSetId(DERIVED_COMPONENT_SET_ID);
          derivedComponentBo.setName("Derived component");
          derivedComponentBo.setNamespaceCode("KR-TST");

        KRADServiceLocator.getDataObjectService().save(derivedComponentBo, PersistenceOption.FLUSH);
    }

    private void setupComponentSetBoDataObjectAndSave(){
        ComponentSetBo componentSetBo = new ComponentSetBo();
        componentSetBo.setChecksum("ASDFQWER");
        componentSetBo.setComponentSetId("DD:RICETST");
        componentSetBo.setLastUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
        componentSetBo.setVersionNumber(null);

        KRADServiceLocator.getDataObjectService().save(componentSetBo, PersistenceOption.FLUSH);
    }

    private void setupComponentBoDataObjectAndSave(){
        ComponentBo componentBo = new ComponentBo();
        componentBo.setActive(true);
        componentBo.setCode("All");
        componentBo.setName("All");
        componentBo.setNamespaceCode("KR-TST");
        componentBo.setNamespace(KRADServiceLocator.getDataObjectService().find(NamespaceBo.class,"KR-TST"));
        KRADServiceLocator.getDataObjectService().save(componentBo, PersistenceOption.FLUSH);
    }

    private void setupNameSpaceBoDataObjectAndSave(){
        NamespaceBo namespaceBo = new NamespaceBo();
        namespaceBo.setActive(true);
        namespaceBo.setApplicationId("RICE");
        namespaceBo.setCode("KR-TST");
        namespaceBo.setName("Kuali Rice Test Namespace");

        KRADServiceLocator.getDataObjectService().save(namespaceBo, PersistenceOption.FLUSH);

        namespaceBo = new NamespaceBo();
        namespaceBo.setActive(true);
        namespaceBo.setApplicationId("OTH");
        namespaceBo.setCode("KR-OTH");
        namespaceBo.setName("Kuali Other");

        KRADServiceLocator.getDataObjectService().save(namespaceBo, PersistenceOption.FLUSH);

    }
}
