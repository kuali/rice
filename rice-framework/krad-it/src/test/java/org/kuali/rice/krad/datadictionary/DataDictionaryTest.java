/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.krad.datadictionary;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.impl.DictionaryValidationServiceImpl;
import org.kuali.rice.krad.test.KRADTestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class DataDictionaryTest extends KRADTestCase {
    private DataDictionaryService ddService;
    private DocumentTypeService dtService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ddService = KRADServiceLocatorWeb.getDataDictionaryService();
        dtService = KEWServiceLocator.getDocumentTypeService();
    }

    @Test
    public void testDocumentTypeEBO() throws Exception {
        DataDictionary dataDictionary = ddService.getDataDictionary();
        Map<String, DocumentEntry> documentEntry = dataDictionary.getDocumentEntries();
        DocumentEntry entry = documentEntry.get("DocumentTypeEBO");
        DocumentTypeEBO documentType = dtService.findByName(entry.getDocumentTypeName());
        assertNotNull(documentType);
        assertNotNull(documentType.getDocTypeParentId());
        assertNotNull(documentType.getDocumentTypeId());
        assertEquals("Travel Authorization Document",documentType.getLabel());
        assertEquals("DocumentTypeEBO",documentType.getName());
        assertEquals("Create a New Travel Authorization Document",documentType.getDescription());
        assertTrue(documentType.isActive());
    }
}
