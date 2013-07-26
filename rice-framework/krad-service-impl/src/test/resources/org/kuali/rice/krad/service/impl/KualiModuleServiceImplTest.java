package org.kuali.rice.krad.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.resourceloader.ResourceLoader;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.core.framework.resourceloader.BeanFactoryResourceLoader;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.springframework.beans.factory.support.StaticListableBeanFactory;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class KualiModuleServiceImplTest {
    private static List<ModuleService> riceModuleServices;
    static {
        List<ModuleService> moduleServices = new ArrayList<ModuleService>();
        moduleServices.add(mock(ModuleService.class));
        moduleServices.add(mock(ModuleService.class));
        moduleServices.add(mock(ModuleService.class));
        riceModuleServices = Collections.unmodifiableList(moduleServices);
    }

    private KualiModuleServiceImpl kualiModuleService = new KualiModuleServiceImpl();

    @Before
    public void installRiceKualiModuleService() throws Exception {
        GlobalResourceLoader.stop();
        SimpleConfig config = new SimpleConfig();
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID");
        ConfigContext.init(config);

        StaticListableBeanFactory testBf = new StaticListableBeanFactory();

        KualiModuleService riceKualiModuleService = mock(KualiModuleService.class);
        when(riceKualiModuleService.getInstalledModuleServices()).thenReturn(riceModuleServices);

        testBf.addBean(KRADServiceLocatorWeb.KUALI_MODULE_SERVICE, riceKualiModuleService);

        ResourceLoader rl = new BeanFactoryResourceLoader(new QName("moduleservicebase-unittest"), testBf);
        GlobalResourceLoader.addResourceLoader(rl);
        GlobalResourceLoader.start();
    }

    @Test
    public void testLoadsRiceInstalledModuleServices() throws Exception {
        kualiModuleService.afterPropertiesSet();
        assertEquals(0, kualiModuleService.getInstalledModuleServices().size());

        // when set to true, adds services from the global rice kualimoduleservice
        kualiModuleService.setLoadRiceInstalledModuleServices(true);
        // add our local service
        ModuleService localModuleService = mock(ModuleService.class);
        kualiModuleService.getInstalledModuleServices().add(localModuleService);
        kualiModuleService.afterPropertiesSet();

        // should contain our service + rice module services
        int installedModuleServices = kualiModuleService.getInstalledModuleServices().size();
        assertEquals(riceModuleServices.size() + 1, installedModuleServices);
        assertEquals(localModuleService, kualiModuleService.getInstalledModuleServices().get(0));
        assertEquals(riceModuleServices, kualiModuleService.getInstalledModuleServices().subList(1, installedModuleServices));
    }
}
