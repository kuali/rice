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

import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.ContextClassLoaderBinder;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests LegacyDetectionService
 */
@RunWith(MockitoJUnitRunner.class)
public class LegacyDetectorTest {

    private static class DummyDataObject {}
    private static class DummyDataObjectOjb {}

    private Set<Class<?>> nonLegacyClasses = new HashSet<Class<?>>();

    @Mock private MetadataManager metadataManager;
    @Mock private MetadataRepository metadataRepository;
    @Mock private DataDictionaryService dataDictionaryService;
    @Mock private DataDictionary dataDictionary;

    private MetadataManager existingMetadataManager = null;

    private LegacyDetector detector;

    @Before
    public void setUp() throws Exception {

        DescriptorRepository descriptorRepository = new DescriptorRepository();
        when(metadataManager.getGlobalRepository()).thenReturn(descriptorRepository);
        this.existingMetadataManager = hackOjb(metadataManager);

        when(metadataRepository.contains(any(Class.class))).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Class<?> type = (Class<?>)invocation.getArguments()[0];
                return nonLegacyClasses.contains(type);
            }
        });

        when(dataDictionaryService.getDataDictionary()).thenReturn(dataDictionary);

        nonLegacyClasses.clear();

        ConfigContext.init(new SimpleConfig());
        ConfigContext.getCurrentContextConfig().removeProperty(KRADConstants.Config.KNS_ENABLED);
        ConfigContext.getCurrentContextConfig().removeProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK);

        this.detector = new LegacyDetector(this.metadataRepository, this.dataDictionaryService);

        addOjbClass(DummyDataObjectOjb.class);
    }

    @After
    public void tearDown() throws Exception {
        unhackOjb(this.existingMetadataManager);
    }

    @Test
    public void testKnsDisabledByDefault() {
        assertFalse(detector.isKnsEnabled());
    }

    @Test
    public void testKnsEnabledByConfig() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.KNS_ENABLED, "true");
        assertTrue(detector.isKnsEnabled());
    }

    @Test
    public void testKnsDisabledByConfig() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.KNS_ENABLED, "false");
        assertFalse(detector.isKnsEnabled());
    }

    @Test
    public void testLegacyDataFrameworkDisabledByDefault() {
        assertFalse(detector.isLegacyDataFrameworkEnabled());
    }

    @Test
    public void testLegacyDataFrameworkEnabledByKnsByDefault() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.KNS_ENABLED, "true");
        assertTrue(detector.isLegacyDataFrameworkEnabled());
    }

    @Test
    public void testLegacyDataFrameworkDisabledByKnsByDefault() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.KNS_ENABLED, "false");
        assertFalse(detector.isLegacyDataFrameworkEnabled());
    }

    @Test
    public void testLegacyDataFrameworkEnabledByConfig() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.KNS_ENABLED, "false");
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        assertTrue(detector.isLegacyDataFrameworkEnabled());
    }

    @Test
    public void testLegacyDataFrameworkDisabledByConfig() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.KNS_ENABLED, "true");
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "false");
        assertFalse(detector.isLegacyDataFrameworkEnabled());
    }

    @Test
    public void testDataObjectNotLegacyWhenDisabled() {
        addOjbClass(DummyDataObject.class);
        assertFalse(detector.isLegacyDataFrameworkEnabled());
        assertFalse(detector.useLegacyForObject(new DummyDataObject()));
        assertFalse(detector.useLegacy(DummyDataObject.class));
    }

    @Test
    public void testDataObjectNotLegacyWhenNotLoaded() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        assertTrue(detector.isLegacyDataFrameworkEnabled());
        assertFalse(detector.useLegacyForObject(new DummyDataObject()));
        assertFalse(detector.useLegacy(DummyDataObject.class));
    }

    @Test
    public void testDataObjectIsLegacyWhenEnabledAndLoaded() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        addOjbClass(DummyDataObject.class);
        assertTrue(detector.isLegacyDataFrameworkEnabled());
        assertTrue(detector.useLegacyForObject(new DummyDataObject()));
        assertTrue(detector.useLegacy(DummyDataObject.class));
    }

    @Test
    public void testUseLegacy_InContext() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        detector.beginLegacyContext();
        try {
            // since recent changes, if it's not in OJB, it's not legacy
            assertFalse(detector.useLegacy(DummyDataObject.class));
            assertTrue(detector.useLegacy(DummyDataObjectOjb.class));
        } finally {
            detector.endLegacyContext();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testUseLegacy_LegacyFrameworkDisabled() {
        detector.beginLegacyContext();
        try {
            detector.useLegacy(DummyDataObject.class);
        } finally {
            detector.endLegacyContext();
        }
    }

    /**
     * Verifies that if you pass an instance of java.lang.Class to useLegacyForObject, that it
     * throws an IllegalArgumentException since you should use the useLegacy method for that
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUseLegacyForObject_PassingAClassIsBad() {
        detector.useLegacyForObject(DummyDataObject.class);
    }

    @Test(expected=IllegalStateException.class)
    public void testBeginLegacyContext_LegacyFrameworkDisabled() {
        detector.beginLegacyContext();
    }

    @Test(expected=IllegalStateException.class)
    public void testEndLegacyContext_NoContext() {
        detector.endLegacyContext();
    }

    @Test
    public void testBeginEndLegacyContext() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        assertFalse(detector.isInLegacyContext());
        detector.beginLegacyContext();
        try {
            assertTrue(detector.isInLegacyContext());
        } finally {
            detector.endLegacyContext();
        }
        assertFalse(detector.isInLegacyContext());
    }

    @Test
    public void testNestedLegacyContext() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        assertFalse(detector.isInLegacyContext());
        detector.beginLegacyContext();
        try {
            assertTrue(detector.isInLegacyContext());
            detector.beginLegacyContext();
            try {
                detector.beginLegacyContext();
                try {
                    assertTrue(detector.isInLegacyContext());
                } finally {
                    detector.endLegacyContext();
                }
                assertTrue(detector.isInLegacyContext());
            } finally {
                detector.endLegacyContext();
            }
            assertTrue(detector.isInLegacyContext());
        } finally {
            detector.endLegacyContext();
        }
        assertFalse(detector.isInLegacyContext());
    }

    @Test
    public void testIllegallyNestedLegacyContext() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        assertFalse(detector.isInLegacyContext());
        detector.beginLegacyContext();
        assertTrue(detector.isInLegacyContext());
        detector.endLegacyContext();
        assertFalse(detector.isInLegacyContext());
        try {
            detector.endLegacyContext();
            fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // this should happen!
        }
    }

    @Test
    public void testIsOjbLoadedClass() {
        assertFalse(detector.isOjbLoadedClass(DummyDataObject.class));
        assertTrue(detector.isOjbLoadedClass(DummyDataObjectOjb.class));
    }

    /**
     * Verifies that isLegacyManaged reports that the object is legacy managed if it has a BusinessObjectEntry and
     * nothing else.
     */
    @Test
    public void testIsLegacyManaged_withBusinessObjectEntry() {
        assertFalse(detector.isLegacyManaged(DummyDataObject.class));
        when(dataDictionary.getBusinessObjectEntry(DummyDataObject.class.getName())).thenReturn(
                new BusinessObjectEntry());
        assertTrue(detector.isLegacyManaged(DummyDataObject.class));
        assertFalse(detector.isLegacyManaged(String.class));
        verify(dataDictionary, times(2)).getBusinessObjectEntry(DummyDataObject.class.getName());
        verify(dataDictionary, times(3)).getBusinessObjectEntry(anyString());
    }

    /**
     * Verifies that isLegacyManaged reports that the object is legacy managed if it is mapped in OJB.
     */
    @Test
    public void testIsLegacyManaged_ojbLoaded() {
        assertFalse(detector.isLegacyManaged(DummyDataObject.class));
        // it must be mapped in ojb
        assertTrue(detector.isLegacyManaged(DummyDataObjectOjb.class));
    }

    /**
     * Ensures false is returned form isOjbLoadedClass if OJB is not on the classpath.
     */
    @Test
    public void testIsOjbLoadedClass_NoOjbOnClasspath() throws Exception {
        // set an empty classloader to cause the check to load OJB MetadataManager to fail from inside isOjbLoadedClass
        ContextClassLoaderBinder.doInContextClassLoader(ClassLoader.getSystemClassLoader().getParent(),
                new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        assertFalse(detector.isOjbLoadedClass(DummyDataObjectOjb.class));
                        return null;
                    }
                });
    }

    private void addOjbClass(Class<?> type) {
        ClassDescriptor classDescriptor = new ClassDescriptor(metadataManager.getGlobalRepository());
        classDescriptor.setClassOfObject(type);
        metadataManager.getGlobalRepository().put(type, classDescriptor);
    }

    /**
     * Hacks OJB to put a stubbed MetadataManager into place.
     */
    private MetadataManager hackOjb(MetadataManager mockMetadataManager) throws Exception {
        Field singletonField = MetadataManager.class.getDeclaredField("singleton");
        singletonField.setAccessible(true);
        MetadataManager existingMetadataManager = (MetadataManager)singletonField.get(null);
        singletonField.set(null, mockMetadataManager);
        return existingMetadataManager;
    }

    private void unhackOjb(MetadataManager existingMetadataManager) throws Exception{
        Field singletonField = MetadataManager.class.getDeclaredField("singleton");
        singletonField.setAccessible(true);
        singletonField.set(null, this.existingMetadataManager);

    }

}