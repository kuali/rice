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
package org.kuali.rice.krad.util;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.resourceloader.ResourceLoader;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.namespace.QName;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests LegacyDetectionAdvice
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class LegacyDetectionAdviceTest {
    @LegacyDataFramework
    public static class LegacyService {
        public boolean performDeprecatedAction() { return true; }
    }
    public static class NonLegacyService {
        public boolean performAction() { return true; }
    }

    @Autowired
    LegacyService legacyService;
    @Autowired
    NonLegacyService nonLegacyService;

    @BeforeClass
    public static void initGrl() throws Exception {

        ConfigContext.init(new SimpleConfig());
        ConfigContext.getCurrentContextConfig().putProperty(CoreConstants.Config.APPLICATION_ID, LegacyUtilsTest.class.getName());

        // have to mock these because LegacyDetectionAdvice is calling LegacyUtils
        MetadataRepository metadataRepository = mock(MetadataRepository.class);
        DataDictionaryService dataDictionaryService = mock(DataDictionaryService.class);
        ResourceLoader resourceLoader = mock(ResourceLoader.class);
        when(resourceLoader.getName()).thenReturn(QName.valueOf(LegacyUtilsTest.class.getName()));
        when(resourceLoader.getService(QName.valueOf("metadataRepository"))).thenReturn(metadataRepository);
        when(resourceLoader.getService(QName.valueOf("dataDictionaryService"))).thenReturn(dataDictionaryService);
        GlobalResourceLoader.addResourceLoader(resourceLoader);
        GlobalResourceLoader.start();
    }

    @AfterClass
    public static void stopGrl() throws Exception {
        GlobalResourceLoader.stop();
    }

    @Before
    public void resetConfiguration() {

        ConfigContext.getCurrentContextConfig().removeProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK);
    }

    @Test
    public void testNonLegacyServiceAlwaysAllowed() {
        assertTrue(nonLegacyService.performAction());
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        assertTrue(nonLegacyService.performAction());
    }

    @Test(expected=IllegalStateException.class)
    public void testLegacyServiceInvocationAllowedWhenEnabled() {
        assertTrue(nonLegacyService.performAction());
        legacyService.performDeprecatedAction();
    }

    @Test // no exception is thrown
    public void testLegacyServiceInvocationDisallowedWhenDisabled() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        assertTrue(nonLegacyService.performAction());
        assertTrue(legacyService.performDeprecatedAction());
    }
}