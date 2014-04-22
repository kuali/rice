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
package org.kuali.rice.krad.bo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.resourceloader.ResourceLoader;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.core.framework.resourceloader.BeanFactoryResourceLoader;
import org.kuali.rice.krad.bo.test.TestEBOAbstract;
import org.kuali.rice.krad.bo.test.TestEBOConcrete;
import org.kuali.rice.krad.bo.test.TestEBOInterface;
import org.kuali.rice.krad.bo.test.TestEBOInterfaceSub;
import org.kuali.rice.krad.data.provider.Provider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.support.StaticListableBeanFactory;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Tests ModuleConfiguration base class
 */
@RunWith(MockitoJUnitRunner.class)
public class ModuleConfigurationTest {

    private ModuleConfiguration config = new ModuleConfiguration();
    @Mock
    private DataDictionaryService ddsMock;
    @Mock
    private ProviderRegistry prMock;

    private List<String> ddPackages = Arrays.asList(new String[]{"package1", "package2"});
    private List<Provider> providers = Arrays.asList(new Provider[] { mock(Provider.class), mock(Provider.class) });


    @Before
    public void initGRL() throws Exception {
        GlobalResourceLoader.stop();
        SimpleConfig config = new SimpleConfig();
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID");
        ConfigContext.init(config);
        StaticListableBeanFactory testBf = new StaticListableBeanFactory();
        testBf.addBean(KRADServiceLocator.PROVIDER_REGISTRY, prMock);
        ResourceLoader rl = new BeanFactoryResourceLoader(new QName("moduleconfiguration-unittest"), testBf);
        GlobalResourceLoader.addResourceLoader(rl);
        GlobalResourceLoader.start();
    }

    @Before
    public void setup() {
        config.setDataDictionaryService(ddsMock);
    }

    protected void initModuleConfiguration() {
        config.setNamespaceCode("moduleconfiguration-unittest");
        config.setDataDictionaryPackages(ddPackages);
        config.setProviders(providers);
    }

    @Test
    public void testInitializationWithoutDD() throws Exception {
        initModuleConfiguration();
        config.afterPropertiesSet();
        verify(ddsMock, never()).addDataDictionaryLocations(config.getNamespaceCode(), ddPackages);
    }

    @Test
    public void testInitialization() throws Exception {
        initModuleConfiguration();
        config.setInitializeDataDictionary(true);
        config.afterPropertiesSet();
        verify(ddsMock).addDataDictionaryLocations(config.getNamespaceCode(), ddPackages);
        verify(prMock).registerProvider(providers.get(0));
        verify(prMock).registerProvider(providers.get(1));
    }

    @Test
    public void testConcreteEBOIsValid() {
        Map<Class, Class> ebos = new HashMap<Class, Class>();
        ebos.put(TestEBOInterface.class, TestEBOConcrete.class);
        // OK
        config.setExternalizableBusinessObjectImplementations(ebos);
    }

    @Test(expected=RuntimeException.class)
    public void testAbstractEBOIsInvalid() {
        Map<Class, Class> ebos = new HashMap<Class, Class>();
        ebos.put(TestEBOInterface.class, TestEBOAbstract.class);
        config.setExternalizableBusinessObjectImplementations(ebos);
    }

    @Test(expected=RuntimeException.class)
    public void testInterfaceEBOIsInvalid() {
        Map<Class, Class> ebos = new HashMap<Class, Class>();
        ebos.put(TestEBOInterface.class, TestEBOInterfaceSub.class);
        config.setExternalizableBusinessObjectImplementations(ebos);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testGetEBOsImmutability() {
        Map<Class, Class> ebos = new HashMap<Class, Class>();
        ebos.put(TestEBOInterface.class, TestEBOConcrete.class);
        // OK
        config.setExternalizableBusinessObjectImplementations(ebos);

        Map<Class, Class> returnedEbos = config.getExternalizableBusinessObjectImplementations();
        // Map should be immutable -> UnsupportedOperationException
        returnedEbos.put(TestEBOInterface.class, TestEBOConcrete.class);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testGetProvidersImmutability() {
        initModuleConfiguration();
        List<Provider> providers = config.getProviders();
        // list should be immutable -> UnsupportedOperationException
        providers.add(mock(Provider.class));
    }
}
