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
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.namespace.QName;

import java.util.concurrent.Callable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link LegacyUtils} class. For the most part, functionality of this is tested alredy by the
 * {@link LegacyDetectorTest} since LegacyUtils delegates most of it's calls to the detector. This tests will test
 * code internal to LegacyUtils.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class LegacyUtilsTest {

    @BeforeClass
    public static void initGrl() throws Exception {

        MetadataRepository metadataRepository = mock(MetadataRepository.class);
        DataDictionaryService dataDictionaryService = mock(DataDictionaryService.class);
        ResourceLoader resourceLoader = mock(ResourceLoader.class);

        SimpleConfig config = new SimpleConfig();
        config.putProperty(CoreConstants.Config.APPLICATION_ID, LegacyUtilsTest.class.getName());
        config.putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        ConfigContext.init(config);

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

    @Test
    public void testIsKnsDocumentEntry() {
        assertTrue(LegacyUtils.isKnsDocumentEntry(new org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry()));
        assertTrue(LegacyUtils.isKnsDocumentEntry(new org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry()));
        assertFalse(LegacyUtils.isKnsDocumentEntry(new org.kuali.rice.krad.datadictionary.TransactionalDocumentEntry()));
        assertFalse(LegacyUtils.isKnsDocumentEntry(new org.kuali.rice.krad.datadictionary.MaintenanceDocumentEntry()));
    }

    @Test
    public void testDoInLegacyContext() throws Exception {
        assertFalse(LegacyUtils.isInLegacyContext());
        Boolean evaluated = LegacyUtils.doInLegacyContext(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                assertTrue(LegacyUtils.isInLegacyContext());
                return true;
            }
        });
        assertTrue(evaluated);
        assertFalse(LegacyUtils.isInLegacyContext());
    }

    @Test
    public void testDoInLegacyContext_Exception() {
        final String exceptionMessage = "I failed! Oh the humanity!!!";
        assertFalse(LegacyUtils.isInLegacyContext());
        try {
            Boolean evaluated = LegacyUtils.doInLegacyContext(new Callable<Boolean>() {
               @Override
                public Boolean call() throws Exception {
                    assertTrue(LegacyUtils.isInLegacyContext());
                    throw new Exception(exceptionMessage);
                }
            });
            fail("An exception should have been thrown.");
        } catch (Exception e) {
            assertEquals(exceptionMessage, e.getMessage());
        }
        assertFalse(LegacyUtils.isInLegacyContext());
    }
}
