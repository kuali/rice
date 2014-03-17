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
package org.kuali.rice.krad.service.impl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoaderTestUtils;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.service.impl.BusinessObjectSerializerServiceImpl;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test to verify that KRADs serialization related annotations don't impact maintenance document serialization in KNS
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.service.impl.MaintenanceDocumentSerializationTest
 */
//public class KnsMaintenanceDocumentSerializationTest extends MaintenanceDocumentSerializationTest {
//
//    @BeforeClass
//    public static void setupKnsServices() {
//        // wire up additional services that are needed to test KNS maintenance document serialization
//
//        BusinessObjectSerializerServiceImpl businessObjectSerializerService = new BusinessObjectSerializerServiceImpl();
//        businessObjectSerializerService.setXmlObjectSerializerService(xmlObjectSerializerServiceImpl);
//
//        xmlObjectSerializerServiceImpl.setLegacyDataAdapter(mockLegacyDataAdapter);
//
//        DocumentDictionaryService mockDocumentDictionaryService = mock(DocumentDictionaryService.class);
//        when(mockDocumentDictionaryService.getMaintenanceDocumentTypeName(any(Class.class))).thenReturn("bogusDoc");
//        when(mockDocumentDictionaryService.getMaintenanceDocumentEntry(anyString())).thenReturn(null);
//
//        // put needed mock and hand wired services into the GRL
//        GlobalResourceLoaderTestUtils.addMockService(KRADServiceLocator.KNS_SERIALIZER_SERVICE, businessObjectSerializerService);
//        GlobalResourceLoaderTestUtils.addMockService(KRADServiceLocatorWeb.DOCUMENT_DICTIONARY_SERVICE, mockDocumentDictionaryService);
//    }
//
//    /**
//     * Verify that serialization and deserialization for KNS maintenance documents ignore KRAD- and JPA-specific
//     * annotations.
//     */
//    @Test
//    public void knsSerializationIgnoresAnnotationsTest() {
//        KualiMaintainableImpl maintainable = new KualiMaintainableImpl(dataObject);
//
//        org.kuali.rice.kns.document.MaintenanceDocumentBase knsMaintenanceDoc =
//                new org.kuali.rice.kns.document.MaintenanceDocumentBase();
//
//        knsMaintenanceDoc.setNewMaintainableObject(maintainable);
//        knsMaintenanceDoc.populateXmlDocumentContentsFromMaintainables();
//        knsMaintenanceDoc.populateMaintainablesFromXmlDocumentContents();
//
//        TestKradDataObj reconstitutedBO = (TestKradDataObj) knsMaintenanceDoc.getNewMaintainableObject().getDataObject();
//
//        // verify that all fields on the business object are serialized regardless of KRAD- & JPA-specific annotations
//        Assert.assertNotNull(reconstitutedBO.getName());
//        Assert.assertNotNull(reconstitutedBO.getChild1());
//        Assert.assertNotNull(reconstitutedBO.getChild2());
//        Assert.assertNotNull(reconstitutedBO.getChild3());
//        Assert.assertNotNull(reconstitutedBO.getChild4());
//        Assert.assertNotNull(reconstitutedBO.getChild5());
//        Assert.assertNotNull(reconstitutedBO.getChild6());
//
//        // verify on the child object too
//        Assert.assertNotNull(reconstitutedBO.getChild1().getName());
//        Assert.assertNotNull(reconstitutedBO.getChild1().getContent1());
//        Assert.assertNotNull(reconstitutedBO.getChild1().getContent2());
//        Assert.assertNotNull(reconstitutedBO.getChild1().getContent3());
//        Assert.assertNotNull(reconstitutedBO.getChild1().getContent4());
//        Assert.assertNotNull(reconstitutedBO.getChild1().getContent5());
//        Assert.assertNotNull(reconstitutedBO.getChild1().getContent6());
//    }
//}


