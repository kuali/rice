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
import org.kuali.rice.krad.bo.test.TestEBOInterface;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.provider.Provider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.service.impl.ModuleServiceBase;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.support.StaticListableBeanFactory;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests ModulesService base implementation
 */
@RunWith(MockitoJUnitRunner.class)
public class ModuleServiceBaseTest {
    // concrete EBO class that is NOT in the package prefix
    // but the interface it implements IS
    private static class ModuleServiceBaseTestEBO implements TestEBOInterface {
        @Override public void refresh() {}
    }

    // local EBO interface outside of package prefixes
    private static interface ModuleServiceBaseTestEBOInterface extends TestEBOInterface {
    }

    private static class DataObject {}

    private ModuleServiceBase module = new ModuleServiceBase();
    private ModuleConfiguration config = new ModuleConfiguration();
    @Mock
    private KualiModuleService kualiModuleService;
    private List<ModuleService> installedModuleServices = new ArrayList<ModuleService>();

    @Before
    public void initGRL() throws Exception {
        GlobalResourceLoader.stop();
        SimpleConfig config = new SimpleConfig();
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID");
        ConfigContext.init(config);

        StaticListableBeanFactory testBf = new StaticListableBeanFactory();
        when(kualiModuleService.getInstalledModuleServices()).thenReturn(installedModuleServices);

        testBf.addBean(KRADServiceLocator.PROVIDER_REGISTRY, mock(ProviderRegistry.class));
        testBf.addBean(KRADServiceLocatorWeb.KUALI_MODULE_SERVICE, kualiModuleService);

        ResourceLoader rl = new BeanFactoryResourceLoader(new QName("moduleservicebase-unittest"), testBf);
        GlobalResourceLoader.addResourceLoader(rl);
        GlobalResourceLoader.start();
    }

    @Before
    public void setup() {
        config.setNamespaceCode("moduleconfiguration-unittest");
        config.setDatabaseRepositoryFilePaths(Arrays.asList(new String[]{"path1", "path2"}));
        config.setDataDictionaryPackages(Arrays.asList(new String[]{"ddpackage1", "ddpackage2"}));

        // set package prefix to a nested package
        config.setPackagePrefixes(Arrays.asList(new String[]{ TestEBOInterface.class.getPackage().getName(), getClass().getPackage().getName() }));

        PersistenceProvider pp = mock(PersistenceProvider.class);
        when(pp.handles(DataObject.class)).thenReturn(true);
        config.setProviders(Arrays.asList(new Provider[] { pp }));

        module.setModuleConfiguration(config);
    }

    @Test
    public void testRegistersWithKualiModuleService() throws Exception {
        module.afterPropertiesSet();
        assertTrue(installedModuleServices.contains(module));
    }

    @Test
    public void testNotResponsibleForNullClass() {
        assertFalse(module.isResponsibleFor(null));
    }

    @Test
    public void testIsResponsibleForClassByPackagePrefix() {
        assertTrue(module.isResponsibleFor(TestEBOInterface.class));
    }

    @Test
    public void testIsResponsibleForClassByProvider() {
        assertTrue(module.isResponsibleFor(DataObject.class));
    }

    @Test
    public void testIsResponsibleForEBOSubclass() {
        assertTrue(module.isResponsibleFor(ModuleServiceBaseTestEBO.class));
    }

    @Test
    public void testIsResponsibleForEBOInterface() {
        assertTrue(module.isResponsibleFor(ModuleServiceBaseTestEBOInterface.class));
    }
}
