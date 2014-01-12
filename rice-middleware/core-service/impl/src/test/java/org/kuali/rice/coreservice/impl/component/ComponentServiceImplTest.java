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
package org.kuali.rice.coreservice.impl.component;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.coreservice.api.component.Component;
import org.kuali.rice.coreservice.api.component.ComponentService;
import org.kuali.rice.coreservice.api.namespace.Namespace;
import org.kuali.rice.coreservice.api.namespace.NamespaceService;
import org.kuali.rice.krad.data.DataObjectService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link org.kuali.rice.coreservice.impl.component.ComponentServiceImpl}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class ComponentServiceImplTest {
    @Mock private DataObjectService dataObjectService;
    @Mock private NamespaceService namespaceService;
    @Mock private ComponentSetDaoJpa componentSetDao;

    @InjectMocks private ComponentServiceImpl componentService = new ComponentServiceImpl();


    private static final String NAMESPACE_CODE = "MyNamespaceCode";
    private static final String CODE = "MyComponentCode";
    private static final String NAME = "This is my component!";


    private static final String DERIVED_CODE = "MyDerivedComponentCode";
    private static final String DERIVED_NAME = "This is my derived component!";
    private static final String DERIVED_COMPONENT_SET_ID = "derivedComponentSetId";

    private static final String COMPONENT_SET_ID = "componentSetId";

    final Component component = createComponent();
    final org.kuali.rice.coreservice.impl.component.ComponentBo componentBo = convertComponent(component);

    private ComponentService compService = componentService;
    final Component derivedComponent = createDerivedComponent();
    final DerivedComponentBo derivedComponentBo = DerivedComponentBo.from(derivedComponent);

    final ComponentSetBo componentSetBo = createComponentSet();

    private Component createDerivedComponent() {
        Component.Builder builder = Component.Builder.create(NAMESPACE_CODE, DERIVED_CODE, DERIVED_NAME);
        builder.setComponentSetId(DERIVED_COMPONENT_SET_ID);

        return builder.build();
    }

    private ComponentSetBo createComponentSet(){
        ComponentSetBo compSetBo = new ComponentSetBo();
        compSetBo.setComponentSetId(COMPONENT_SET_ID);
        compSetBo.setLastUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
        compSetBo.setChecksum("test");

        return compSetBo;
    }

    public void setComponentService(ComponentService componentService){
         this.compService = componentService;
    }

    public ComponentService getComponentService(){
        return compService;
    }

    private Component createComponent() {
        Component.Builder builder = Component.Builder.create(NAMESPACE_CODE, CODE, NAME);
        return builder.build();
    }

    private ComponentBo convertComponent(Component component) {
        Namespace namespace = Namespace.Builder.create(NAMESPACE_CODE).build();

        NamespaceService nmService = mock(NamespaceService.class);
        when(nmService.getNamespace(NAMESPACE_CODE)).thenReturn(namespace);
        ComponentBo.setNamespaceService(nmService);
        return ComponentBo.from(component);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getComponentByCode_null_namespaceCode() throws Exception{
        getComponentService().getComponentByCode(null, "myComponentCode");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getComponentByCode_empty_namespaceCode() throws Exception{
        getComponentService().getComponentByCode("", "myComponentCode");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getComponentByCode_blank_namespaceCode() throws Exception{
        getComponentService().getComponentByCode("  ", "myComponentCode");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getComponentByCode_null_componentCode() throws Exception{
        getComponentService().getComponentByCode("myNamespaceCode", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getComponentByCode_empty_componentCode() throws Exception{
        getComponentService().getComponentByCode("myNamespaceCode", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getComponentByCode_blank_componentCode() throws Exception{
        getComponentService().getComponentByCode("myNamespaceCode", "  ");
    }

    @Test
    public void test_getComponentByCode_exists() throws Exception{
        when(dataObjectService.find(any(Class.class),anyObject())).thenReturn(componentBo);
        assertTrue("Component was returned", StringUtils.equals(component.getCode(),
                getComponentService().getComponentByCode(NAMESPACE_CODE, CODE).getCode()));
    }

    @Test
    public void test_getComponentsByCode_not_exists() throws Exception{
        when(dataObjectService.find(any(Class.class),anyObject())).thenReturn(null);
        assertTrue("Component was returned",getComponentService().getComponentByCode("blah", "blah")==null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getAllComponentsByNamespaceCode_null_namespaceCode() throws Exception {
        getComponentService().getAllComponentsByNamespaceCode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getAllComponentsByNamespaceCode_empty_namespaceCode() throws Exception{
        getComponentService().getAllComponentsByNamespaceCode("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getAllComponentsByNamespaceCode_blank_namespaceCode() throws Exception{
        getComponentService().getAllComponentsByNamespaceCode("  ");
    }

    @Test
    public void test_getAllComponentsByNamespaceCode_exists() throws Exception{
        setupDataObjectServiceFetchComponent();
        when(dataObjectService.findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<DerivedComponentBo>(DerivedComponentBo.class)),
                any(QueryByCriteria.class))).thenReturn(null);
        List<Component> components = getComponentService().getAllComponentsByNamespaceCode(NAMESPACE_CODE);
        assertTrue("getAllComponentsByNamespaceCode retrieved correctly", components != null && components.size() == 1);
        assertTrue("Component was returned", StringUtils.equals(component.getCode(), components.get(0).getCode()));
        assertImmutableList(components);
        verify(dataObjectService,times(1)).findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<ComponentBo>(ComponentBo.class)), any(QueryByCriteria.class));
        verify(dataObjectService,times(1)).findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<DerivedComponentBo>(DerivedComponentBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_getAllComponentsByNamespaceCode_not_exists() throws Exception{
        when(dataObjectService.findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<ComponentBo>(ComponentBo.class)),
                any(QueryByCriteria.class))).thenReturn(null);
        when(dataObjectService.findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<DerivedComponentBo>(DerivedComponentBo.class)),
                any(QueryByCriteria.class))).thenReturn(null);
        List<Component> components = getComponentService().getAllComponentsByNamespaceCode("blah");
        assertTrue("getAllComponentsByNamespaceCode not retrieved", components != null && components.isEmpty());
        verify(dataObjectService,times(1)).findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<ComponentBo>(ComponentBo.class)), any(QueryByCriteria.class));
        verify(dataObjectService,times(1)).findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<DerivedComponentBo>(DerivedComponentBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_getAllComponentsByNamespaceCode_with_derived() throws Exception{
        setupDataObjectServiceFetchComponent();
        setupDataObjectServiceFetchDerivedComponent();

        List<Component> components = getComponentService().getAllComponentsByNamespaceCode(NAMESPACE_CODE);
        assertTrue("getAllComponentsByNamespaceCode retrieved correctly",
                                    components != null && components.size() == 2);
        assertTrue("Component was returned", StringUtils.equals(component.getCode(), components.get(0).getCode()));
        assertTrue("Component was returned", StringUtils.equals(derivedComponent.getCode(),
                                components.get(1).getCode()));
        assertImmutableList(components);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getActiveComponentsByNamespaceCode_null_namespaceCode() throws Exception{
        getComponentService().getActiveComponentsByNamespaceCode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getActiveComponentsByNamespaceCode_empty_namespaceCode() throws Exception{
        getComponentService().getActiveComponentsByNamespaceCode("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getActiveComponentsByNamespaceCode_blank_namespaceCode() throws Exception{
        getComponentService().getActiveComponentsByNamespaceCode("  ");
    }

    @Test
    public void test_getActiveComponentsByNamespaceCode_exists() throws Exception{
        ArgumentCaptor<QueryByCriteria> argument = setupDataObjectServiceFetchComponent();
        List <Component> components = getComponentService().getActiveComponentsByNamespaceCode(NAMESPACE_CODE);
        QueryByCriteria queryByCriteria = argument.getValue();
        assertTrue("Active passed as criteria for findMatching",StringUtils.contains(queryByCriteria.toString(),
                "active, true"));
        assertTrue("getActiveComponentsByNamespaceCode retrieved correctly", components != null && components.size() == 1);
        assertTrue("Component was returned", StringUtils.equals(component.getCode(), components.get(0).getCode()));
        assertImmutableList(components);
        verify(dataObjectService,times(1)).findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<ComponentBo>(ComponentBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_getActiveComponentsByNamespaceCode_not_exists() throws Exception{
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        when(dataObjectService.findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<ComponentBo>(ComponentBo.class)), any(QueryByCriteria.class))).thenReturn(
                builder.build());
        List<Component> components = getComponentService().getActiveComponentsByNamespaceCode("blah");
        assertTrue("getActiveComponentsByNamespaceCode retrieved correctly",
                                    components != null && components.size() == 0);
        assertImmutableList(components);
    }

    @Test
    public void test_getActiveComponentsByNamespaceCode_with_derived() throws Exception{
        ArgumentCaptor<QueryByCriteria> argument = setupDataObjectServiceFetchComponent();
        setupDataObjectServiceFetchDerivedComponent();

        List<Component> components = getComponentService().getActiveComponentsByNamespaceCode(NAMESPACE_CODE);
        QueryByCriteria queryByCriteria = argument.getValue();
        assertTrue("Active passed as criteria for findMatching",StringUtils.contains(
                queryByCriteria.toString(),"active, true"));
        assertTrue("getAllComponentsByNamespaceCode retrieved correctly",
                components != null && components.size() == 2);
        assertTrue("Component was returned", StringUtils.equals(component.getCode(), components.get(0).getCode()));
        assertTrue("Component was returned", StringUtils.equals(derivedComponent.getCode(),
                components.get(1).getCode()));
        assertImmutableList(components);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getDerivedComponentSet_null_componentSetId() throws Exception{
        getComponentService().getDerivedComponentSet(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getDerivedComponentSet_empty_componentSetId() throws Exception{
        getComponentService().getDerivedComponentSet("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getDerivedComponentSet_blank_componentSetId() throws Exception{
        getComponentService().getDerivedComponentSet("  ");
    }

    @Test
    public void test_getDerivedComponentSet_not_exists() throws Exception{
        when(dataObjectService.findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<DerivedComponentBo>(DerivedComponentBo.class)),
                any(QueryByCriteria.class))).thenReturn(null);
        List<Component> components = getComponentService().getDerivedComponentSet("blah");
        assertTrue("getDerivedComponentSet is empty",components != null && components.isEmpty());
        assertImmutableList(components);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_publishDerivedComponents_null_componentSetId() throws Exception{
        List<Component> components = new ArrayList<Component>();
        components.add(component);
        getComponentService().publishDerivedComponents(null, components);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_publishDerivedComponents_empty_componentSetId() throws Exception{
        List<Component> components = new ArrayList<Component>();
        components.add(derivedComponent);
        getComponentService().publishDerivedComponents("", components);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_publishDerivedComponents_blank_componentSetId() throws Exception{
        List<Component> components = new ArrayList<Component>();
        components.add(derivedComponent);
        getComponentService().publishDerivedComponents("  ", components);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_publishDerivedComponents_invalidComponentSetId_onComponents() throws Exception{
        Component.Builder builder = Component.Builder.create(component);
        builder.setComponentSetId("myComponentSet");
        List<Component> components = new ArrayList<Component>();
        components.add(builder.build());
        getComponentService().publishDerivedComponents("blah", components);
    }

    @Test
    public void test_publishDerivedComponents_null_components() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ComponentSetBo>(
                ComponentSetBo.class)), any(Object.class))).thenReturn(null);
        ArgumentCaptor<ComponentSetBo> boArgumentCaptor = ArgumentCaptor.forClass(ComponentSetBo.class);
        when(componentSetDao.saveIgnoreLockingFailure(boArgumentCaptor.capture())).thenReturn(true);

        when(dataObjectService.findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<ComponentBo>(ComponentBo.class)),
                any(QueryByCriteria.class))).thenReturn(null);

        getComponentService().publishDerivedComponents("myComponentSet", null);
        getComponentService().getDerivedComponentSet("myComponentSet").isEmpty();

        ArgumentCaptor<QueryByCriteria> argument = ArgumentCaptor.forClass(QueryByCriteria.class);
        verify(dataObjectService).deleteMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<DerivedComponentBo>(DerivedComponentBo.class)),
                argument.capture());
        ComponentSetBo compSetCaptured = boArgumentCaptor.getValue();
        QueryByCriteria queryByCriteria = argument.getValue();
        assertTrue("QueryByCriteria contained componentSetId",queryByCriteria!= null &&
                StringUtils.contains(queryByCriteria.getPredicate().toString(),"componentSetId"));
        assertTrue(compSetCaptured != null);
        assertTrue(compSetCaptured.getChecksum() != null);
        assertTrue(StringUtils.equals(compSetCaptured.getComponentSetId(),"myComponentSet"));
        assertTrue(compSetCaptured.getLastUpdateTimestamp() != null);
    }

    /**
     * Tests attempting to publish an empty list of components in a situation where there are already components for
     * the component set.
     */
    @Test
    public void test_publishDerivedComponents_empty_components_withExisting_componentSet() throws Exception{
        ComponentSetBo componentSetBo = new ComponentSetBo();
        componentSetBo.setComponentSetId("myComponentSet");
        componentSetBo.setChecksum("blah");
        componentSetBo.setLastUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
        componentSetBo.setVersionNumber(500L);

        ComponentSetBo savedComponentSet = null;
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ComponentSetBo>(ComponentSetBo.class))
                ,any(Object.class))).thenReturn(componentSetBo);

        ArgumentCaptor<ComponentSetBo> boArgumentCaptor = ArgumentCaptor.forClass(ComponentSetBo.class);
        when(componentSetDao.saveIgnoreLockingFailure(boArgumentCaptor.capture())).thenReturn(true);
        when(dataObjectService.findMatching(Matchers.argThat(new ClassOrSubclassMatcher<ComponentSetBo>(
                ComponentSetBo.class)), any(QueryByCriteria.class))).thenReturn(null);

        getComponentService().publishDerivedComponents("myComponentSet", new ArrayList<Component>());
        assertTrue(getComponentService().getDerivedComponentSet("myComponentSet").isEmpty());

        ArgumentCaptor<QueryByCriteria> argument = ArgumentCaptor.forClass(QueryByCriteria.class);
        verify(dataObjectService).deleteMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<DerivedComponentBo>(DerivedComponentBo.class)),
                argument.capture());
        ComponentSetBo compSetCaptured = boArgumentCaptor.getValue();
        QueryByCriteria queryByCriteria = argument.getValue();
        assertTrue("QueryByCriteria contained componentSetId",queryByCriteria!= null &&
                StringUtils.contains(queryByCriteria.getPredicate().toString(),"componentSetId"));
        assertTrue(compSetCaptured != null);
        assertTrue(!StringUtils.equals(compSetCaptured.getChecksum(),"blah"));
    }

    @Test
    public void test_publishDerivedComponents() throws Exception{

        List<ComponentBo> publishedComponentBos = new ArrayList<ComponentBo>();
        ComponentSetBo componentSet = null;


        setupDataObjectServiceFetchComponentSetEmptyList();

        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ComponentSetBo>(ComponentSetBo.class)),
                any(Object.class))).thenReturn(null);

        ArgumentCaptor<ComponentSetBo> boArgumentCaptor = ArgumentCaptor.forClass(ComponentSetBo.class);
        when(componentSetDao.saveIgnoreLockingFailure(boArgumentCaptor.capture())).thenReturn(true);

        List<Component> components = getComponentService().getDerivedComponentSet("myComponentSet");
        assertTrue(components.isEmpty());


        getComponentService().publishDerivedComponents("myComponentSet", Arrays.asList(component));
        verify(dataObjectService, times(1)).save(any(ComponentBo.class));
        derivedComponentBo.setComponentSetId("myComponentSet");
        setupDataObjectServiceFetchDerivedComponent();
        components = getComponentService().getDerivedComponentSet("myComponentSet");
        assertTrue(components.size() == 1);
        assertTrue(StringUtils.equals(derivedComponent.getNamespaceCode(),components.get(0).getNamespaceCode()));
        assertTrue(StringUtils.equals(derivedComponent.getCode(),components.get(0).getCode()));
    }


    private void assertImmutableList(List<Component> components){
        try{
            components.add(null);
            fail("Should not be able to add to immutable list");
        } catch(UnsupportedOperationException e){

        }
    }

    private ArgumentCaptor<QueryByCriteria> setupDataObjectServiceFetchComponent(){
        List<ComponentBo> componentBoList = new ArrayList<ComponentBo>();
        componentBoList.add(componentBo);
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();

        builder.setResults(componentBoList);
        ArgumentCaptor<QueryByCriteria> argument = ArgumentCaptor.forClass(QueryByCriteria.class);
        when(dataObjectService.findMatching(Matchers.argThat(
                new ClassOrSubclassMatcher<ComponentBo>(ComponentBo.class)), argument.capture())).thenReturn(
                builder.build());
        return argument;
    }

    private void setupDataObjectServiceFetchDerivedComponent(){
        List<DerivedComponentBo> derivedComponentBoList = new ArrayList<DerivedComponentBo>();
        derivedComponentBoList.add(derivedComponentBo);
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        builder.setResults(derivedComponentBoList);

        when(dataObjectService.findMatching(Matchers.argThat(new ClassOrSubclassMatcher<DerivedComponentBo>(
                DerivedComponentBo.class)), any(QueryByCriteria.class))).thenReturn(builder.build());
    }

    private void setupDataObjectServiceFetchComponentSet(){
        List<ComponentSetBo> componentSetBoList = new ArrayList<ComponentSetBo>();
        componentSetBoList.add(componentSetBo);
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        builder.setResults(componentSetBoList);

        when(dataObjectService.findMatching(Matchers.argThat(new ClassOrSubclassMatcher<ComponentSetBo>(
                ComponentSetBo.class)), any(QueryByCriteria.class))).thenReturn(builder.build());
    }

    private void setupDataObjectServiceFetchComponentSetEmptyList(){
        List<ComponentSetBo> componentSetBoList = new ArrayList<ComponentSetBo>();
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        builder.setResults(componentSetBoList);

        when(dataObjectService.findMatching(Matchers.argThat(new ClassOrSubclassMatcher<ComponentSetBo>(
                ComponentSetBo.class)), any(QueryByCriteria.class))).thenReturn(builder.build());
    }


    /**
     * Tests that the calculateChecksum method returns the same checksum regardless of the order of the elements given
     * to it.
     */
    @Test
    public void test_calculateChecksum_orderIndependent() throws Exception{
        Component component2 = Component.Builder.create("a", "b", "name2").build();
        Component component3 = Component.Builder.create("b", "a", "name3").build();
        Component component4 = Component.Builder.create("c", "c", "name4").build();

        List<Component> components1 = new ArrayList<Component>();
        components1.add(component);
        components1.add(component2);
        components1.add(component3);
        components1.add(component4);

        String checksum1 = componentService.calculateChecksum(components1);
        assert checksum1 != null;


        List<Component> components2 = new ArrayList<Component>();
        components2.add(component3);
        components2.add(component2);
        components2.add(component);
        components2.add(component4);

        String checksum2 = componentService.calculateChecksum(components2);
        assert checksum2 != null;

        assertTrue("Checksums match",StringUtils.equals(checksum1,checksum2));
    }

    @Test
    public void test_calculateChecksum_emptyList() throws Exception{
        String checksum1 = componentService.calculateChecksum(new ArrayList<Component>());
        String checksum2 = componentService.calculateChecksum(new ArrayList<Component>());
        assert checksum1 != null;
        assert checksum2 != null;
        assertTrue("Checksums match",StringUtils.equals(checksum1,checksum2));
    }

    @Test
    public void test_translateCollections_nullList() throws Exception{
        List<Component> components = componentService.translateCollections(null, null);
        assert components != null;
        assert components.isEmpty();
        assertImmutableList(components);
    }

    @Test
    public void test_translateCollections_emptyList() throws Exception{
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        GenericQueryResults.Builder builder2 = GenericQueryResults.Builder.create();
        List<Component> components = componentService.translateCollections(builder.build(), builder2.build());
        assert components != null;
        assert components.isEmpty();
        assertImmutableList(components);
    }

    @Test
    public void test_translateCollections_components() throws Exception{
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        List<ComponentBo> results = new ArrayList<ComponentBo>();
        results.add(componentBo);
        builder.setResults(results);
        List<Component> components = componentService.translateCollections(builder.build(), null);
        assert components != null;
        assert components.size() == 1;
        assertTrue("Component fetched correctly",
                StringUtils.equals(components.get(0).getCode(),component.getCode()));
        assertImmutableList(components);
    }

    @Test
    public void test_translateCollections_derivedComponents() throws Exception{
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        List<DerivedComponentBo> results = new ArrayList<DerivedComponentBo>();
        results.add(derivedComponentBo);
        builder.setResults(results);
        List<Component> components = componentService.translateCollections(null, builder.build());
        assert components != null;
        assert components.size() == 1;
        assertTrue("Component fetched correctly",
                StringUtils.equals(components.get(0).getCode(),derivedComponent.getCode()));
        assertImmutableList(components);
    }

    @Test
    public void test_translateCollections_both() throws Exception{
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create();
        GenericQueryResults.Builder builder2 = GenericQueryResults.Builder.create();

        List<DerivedComponentBo> derivedComponentBoList = new ArrayList<DerivedComponentBo>();
        derivedComponentBoList.add(derivedComponentBo);
        builder2.setResults(derivedComponentBoList);

        List<ComponentBo> componentBoList = new ArrayList<ComponentBo>();
        componentBoList.add(componentBo);
        builder.setResults(componentBoList);

        List<Component> components = componentService.translateCollections(builder.build(), builder2.build());
        assert components != null;
        assert components.size() == 2;
        assertTrue("Component fetched correctly",
                StringUtils.equals(components.get(0).getCode(),component.getCode()));
        assertTrue("Component fetched correctly",
                StringUtils.equals(components.get(1).getCode(),derivedComponent.getCode()));
        assertImmutableList(components);
    }

    class ClassOrSubclassMatcher<T> extends BaseMatcher<Class<T>> {

        private final Class<T> targetClass;

        public ClassOrSubclassMatcher(Class<T> targetClass) {
            this.targetClass = targetClass;
        }

        @SuppressWarnings("unchecked")
        public boolean matches(Object obj) {
            if (obj != null) {
                if (obj instanceof Class) {
                    return targetClass.isAssignableFrom((Class<T>) obj);
                }
            }
            return false;
        }

        public void describeTo(Description desc) {
            desc.appendText("Matches a class or subclass");
        }
    }





}
