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

package org.kuali.rice.kew.doctype;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests to confirm JPA mapping for the Kew module Document type objects
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KewDocumentTypeJpaTest extends KewDocumentTypeBaseTest {

    public static final String TEST_DOC_ID = "1234";

    private DataObjectService dataObjectService;

    @Before
    public void setup() {
        dataObjectService = KRADServiceLocator.getDataObjectService();
    }


    @Test
    public void testDocumentTypeFindByDocumentId() throws Exception{
        DocumentType documentType = setupDocumentType(true);
        String documentTypeId = documentType.getDocumentTypeId();
        setupDocumentRouteHeaderValueWithRouteHeaderAssigned(documentTypeId);

        documentType = KEWServiceLocator.getDocumentTypeService().findByDocumentId(KewDocumentTypeJpaTest.TEST_DOC_ID);

        assertTrue("DocumentType fetched by document id",documentType != null && StringUtils.equals(
                documentType.getDocumentTypeId(),documentTypeId));
    }

    @Test
    public void testDocumentTypeVersionAndSave() throws Exception{
        DocumentType parent = setupDocumentType(false);
        parent.setName("MyParentDocType");
        parent = KEWServiceLocator.getDocumentTypeService().versionAndSave(parent);

        assertNotNull(parent.getDocumentTypeId());
        assertEquals(Integer.valueOf(0), parent.getVersion());

        DocumentType documentType = setupDocumentType(false);
        documentType.setDocTypeParentId(parent.getDocumentTypeId());
        documentType = KEWServiceLocator.getDocumentTypeService().versionAndSave(documentType);

        assertNotNull(documentType);
        assertEquals(Integer.valueOf(0), documentType.getVersion());

        // now modify the doc type and re-save it, we have to be careful here, we can't just set the label on our
        // original doc because it's managed in jpa now and would be saved! Also, we are still in the same transaction
        // so out attempt to find old doc type internally will just return ourselves(!)
        //
        // set this modified version up without a parent, let's make sure that works!
        DocumentType modified = setupDocumentType(false);
        modified.setLabel("a custom label");
        modified = KEWServiceLocator.getDocumentTypeService().versionAndSave(modified);

        assertNotNull(modified.getDocumentTypeId());
        assertEquals(modified.getPreviousVersionId(), documentType.getDocumentTypeId());
        assertEquals(Integer.valueOf(1), modified.getVersion());
        assertTrue(modified.isCurrent());
        assertNull(modified.getParentId());

        // refetch the parent, it should have no children
        DocumentType newParent = KEWServiceLocator.getDocumentTypeService().findByName(parent.getName());
        assertEquals(0, newParent.getChildrenDocTypes().size());
        assertEquals(parent.getDocumentTypeId(), newParent.getDocumentTypeId());

        // get the old doc type, it should no longer be current
        DocumentType oldVersion = KEWServiceLocator.getDocumentTypeService().findById(modified.getPreviousVersionId());
        assertEquals(Integer.valueOf(0), oldVersion.getVersion());
        assertFalse(oldVersion.isCurrent());
    }

    @Test
    public void testDocumentTypeFindAllCurrentRootDocuments() throws Exception{
        setupDocumentType(true);

        List rootDocumentType = KEWServiceLocator.getDocumentTypeService().findAllCurrentRootDocuments();

        assertTrue("Found all root documents", rootDocumentType != null && rootDocumentType.size() == 3);
    }

    @Test
    public void testDocumentTypeFindAllCurrent() throws Exception{
        setupDocumentType(true);

        List currentDocTypes = KEWServiceLocator.getDocumentTypeService().findAllCurrent();
        assertTrue("Found all current documents", currentDocTypes != null && currentDocTypes.size() == 7);
    }

    @Test
    public void testDocumentTypeFindPreviousInstances() throws Exception{
        testDocumentTypeVersionAndSave();

        List<DocumentType> previousInstances = KEWServiceLocator.getDocumentTypeService().
                                findPreviousInstances("gooddoctype");
        assertTrue("Previous instances found correctly", previousInstances != null && previousInstances.size() == 1);
    }

    @Test
    public void testDocumentTypeFindByName() throws Exception{
        DocumentType documentType = setupDocumentType(true);
        String documentTypeName = documentType.getName();

        documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        assertTrue("DocumentType fetched by document id",documentType != null && StringUtils.equals(
                documentType.getName(),documentTypeName));

        String nameCaseInsensitive = "gooDdocType";
        documentType = KEWServiceLocator.getDocumentTypeService().findByNameCaseInsensitive(nameCaseInsensitive);
        assertTrue("DocumentType fetched by document id",documentType != null && StringUtils.equals(
                documentType.getName(),documentTypeName));
    }

    @Test
    public void testDocumentTypeServiceFind() throws Exception{
        DocumentType parentDocType = setupDocumentType(true);
        DocumentType childDocType = setupDocumentType(false);
        childDocType.setDocTypeParentId(parentDocType.getDocumentTypeId());
        childDocType.setName("CoolNewDocType");
        childDocType = KRADServiceLocator.getDataObjectService().save(childDocType, PersistenceOption.FLUSH);
        assertTrue("Child doc type now has a parent doc type",childDocType != null &&
                StringUtils.isNotBlank(childDocType.getDocTypeParentId()));

        List<DocumentType> documentTypes = (List<DocumentType>)
                KEWServiceLocator.getDocumentTypeService().find(childDocType,parentDocType.getName(),true);
        assertTrue("Fetched correct number of documentTypes", documentTypes != null && documentTypes.size() == 1);


    }

    @Override
    protected DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Override
    protected DocumentType fetchDocumentType(DocumentType dt) {
        return dataObjectService.find(DocumentType.class, dt.getDocumentTypeId());
    }

}
