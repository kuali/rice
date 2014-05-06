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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.resourceloader.ResourceLoader;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.core.framework.resourceloader.BeanFactoryResourceLoader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentBase;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.util.KRADConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.support.StaticListableBeanFactory;

/**
 * Unit test for the {@link LegacyDataAdapterImpl}. Tests that the various methods delegate to KNS or KRAD under the
 * appropriate circumstances. Also tests some of the internal code in this class is functioning properly.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@RunWith(MockitoJUnitRunner.class)
public class LegacyDataAdapterImplTest {

    @Mock private LegacyDataAdapter knsLegacyDataAdapter;
    @Mock private LegacyDataAdapter kradLegacyDataAdapter;
    @Mock private DataDictionaryService dataDictionaryService;
    @Mock private MetadataRepository metadataRepository;

    @InjectMocks private LegacyDataAdapterImpl lda = new LegacyDataAdapterImpl();

    @Before
    public void setup() throws Exception {
        GlobalResourceLoader.stop();

        SimpleConfig config = new SimpleConfig();
        config.putProperty(CoreConstants.Config.APPLICATION_ID, getClass().getName());
        ConfigContext.init(config);
        ConfigContext.getCurrentContextConfig().removeProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK);
        ConfigContext.getCurrentContextConfig().removeProperty(KRADConstants.Config.KNS_ENABLED);

        StaticListableBeanFactory testBf = new StaticListableBeanFactory();
        testBf.addBean("metadataRepository", metadataRepository);
        testBf.addBean("dataDictionaryService", dataDictionaryService);
        testBf.addBean("knsLegacyDataAdapter", knsLegacyDataAdapter);
        testBf.addBean("kradLegacyDataAdapter", kradLegacyDataAdapter);

        ResourceLoader rl = new BeanFactoryResourceLoader(new QName(getClass().getName()), testBf);
        GlobalResourceLoader.addResourceLoader(rl);
        GlobalResourceLoader.start();

        MetadataManager mm = MetadataManager.getInstance();

        // register Legacy object
        ClassDescriptor legacyDescriptor = new ClassDescriptor(mm.getGlobalRepository());
        legacyDescriptor.setClassOfObject(Legacy.class);
        mm.getGlobalRepository().put(Legacy.class, legacyDescriptor);

        // register LegacyDocument object
        ClassDescriptor legacyDocumentDescriptor = new ClassDescriptor(mm.getGlobalRepository());
        legacyDocumentDescriptor.setClassOfObject(LegacyDocument.class);
        mm.getGlobalRepository().put(LegacyDocument.class, legacyDocumentDescriptor);
    }

    protected void enableLegacy() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.KNS_ENABLED, "true");
    }

    protected NonLegacy newNonLegacyObject() {
        return new NonLegacy();
    }

    protected NonLegacyPersistableBusinessObject newNonLegacyPersistableBusinessObject() {
        return new NonLegacyPersistableBusinessObject();
    }

    protected Legacy newLegacyObject() {
        return new Legacy();
    }

    protected NonLegacyDocument newNonLegacyDocument() {
        return new NonLegacyDocument();
    }

    protected LegacyDocument newLegacyDocument() {
        return new LegacyDocument();
    }

    @Test
    public void testSave() throws Exception {
        Serializable object = newNonLegacyObject();
        lda.save(object);
        verify(kradLegacyDataAdapter).save(object);
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testSave_Legacy() throws Exception {
        enableLegacy();
        PersistableBusinessObject object = newLegacyObject();
        lda.save(object);
        verify(knsLegacyDataAdapter).save(object);
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testLinkAndSave() throws Exception {
        Serializable object = newNonLegacyObject();
        lda.linkAndSave(object);
        verify(kradLegacyDataAdapter).linkAndSave(object);
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testLinkAndSave_Legacy() throws Exception {
        enableLegacy();
        Serializable object = newLegacyObject();
        lda.linkAndSave(object);
        verify(knsLegacyDataAdapter).linkAndSave(object);
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testSaveDocument() throws Exception {
        Document document = newNonLegacyDocument();
        lda.saveDocument(document);
        verify(kradLegacyDataAdapter).saveDocument(document);
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testSaveDocument_Legacy() throws Exception {
        enableLegacy();
        Document document = newLegacyDocument();
        lda.saveDocument(document);
        verify(knsLegacyDataAdapter).saveDocument(document);
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testFindByPrimaryKey() throws Exception {
        lda.findByPrimaryKey(NonLegacy.class, new HashMap<String, Object>());
        verify(kradLegacyDataAdapter).findByPrimaryKey(eq(NonLegacy.class), anyMapOf(String.class, Object.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testFindByPrimaryKey_Legacy() throws Exception {
        enableLegacy();
        lda.findByPrimaryKey(Legacy.class, new HashMap<String, Object>());
        verify(knsLegacyDataAdapter).findByPrimaryKey(eq(Legacy.class), anyMapOf(String.class, Object.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testFindBySinglePrimaryKey() throws Exception {
        lda.findBySinglePrimaryKey(NonLegacy.class, new Object());
        verify(kradLegacyDataAdapter).findBySinglePrimaryKey(eq(NonLegacy.class), anyObject());
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testFindBySinglePrimaryKey_Legacy() throws Exception {
        enableLegacy();
        lda.findBySinglePrimaryKey(Legacy.class, new Object());
        verify(knsLegacyDataAdapter).findBySinglePrimaryKey(eq(Legacy.class), anyObject());
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testDelete() throws Exception {
        Object object = newNonLegacyObject();
        lda.delete(object);
        verify(kradLegacyDataAdapter).delete(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testDelete_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.delete(object);
        verify(knsLegacyDataAdapter).delete(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testDeleteMatching() throws Exception {
        lda.deleteMatching(NonLegacy.class, new HashMap<String, String>());
        verify(kradLegacyDataAdapter).deleteMatching(eq(NonLegacy.class), anyMapOf(String.class, String.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testDeleteMatching_Legacy() throws Exception {
        enableLegacy();
        lda.deleteMatching(Legacy.class, new HashMap<String, String>());
        verify(knsLegacyDataAdapter).deleteMatching(eq(Legacy.class), anyMapOf(String.class, String.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testRetrieve() throws Exception {
        Object object = newNonLegacyObject();
        lda.retrieve(object);
        verify(kradLegacyDataAdapter).retrieve(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testRetrieve_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.retrieve(object);
        verify(knsLegacyDataAdapter).retrieve(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testFindAll() throws Exception {
        lda.findAll(NonLegacy.class);
        verify(kradLegacyDataAdapter).findAll(eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testFindAll_Legacy() throws Exception {
        enableLegacy();
        lda.findAll(Legacy.class);
        verify(knsLegacyDataAdapter).findAll(eq(Legacy.class));;
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testFindMatching() throws Exception {
        lda.findMatching(NonLegacy.class, new HashMap<String, String>());
        verify(kradLegacyDataAdapter).findMatching(eq(NonLegacy.class), anyMapOf(String.class, String.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testFindMatching_Legacy() throws Exception {
        enableLegacy();
        lda.findMatching(Legacy.class, new HashMap<String, String>());
        verify(knsLegacyDataAdapter).findMatching(eq(Legacy.class), anyMapOf(String.class, String.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }


    @Test
    public void testFindMatchingOrderBy() throws Exception {
        lda.findMatchingOrderBy(NonLegacy.class, new HashMap<String, String>(), "a", true);
        verify(kradLegacyDataAdapter).findMatchingOrderBy(eq(NonLegacy.class), anyMapOf(String.class, String.class), eq(
                "a"), eq(Boolean.TRUE));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testFindMatchingOrderBy_Legacy() throws Exception {
        enableLegacy();
        lda.findMatchingOrderBy(Legacy.class, new HashMap<String, String>(), "a", true);
        verify(knsLegacyDataAdapter).findMatchingOrderBy(eq(Legacy.class), anyMapOf(String.class, String.class), eq(
                "a"), eq(Boolean.TRUE));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }


    @Test
    public void testGetPrimaryKeyFieldValues() throws Exception {
        Object object = newNonLegacyObject();
        lda.getPrimaryKeyFieldValues(object);
        verify(kradLegacyDataAdapter).getPrimaryKeyFieldValues(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetPrimaryKeyFieldValues_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.getPrimaryKeyFieldValues(object);
        verify(knsLegacyDataAdapter).getPrimaryKeyFieldValues(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testRetrieveNonKeyFields() throws Exception {
        Object object = newNonLegacyObject();
        lda.retrieveNonKeyFields(object);
        verify(kradLegacyDataAdapter).retrieveNonKeyFields(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testRetrieveNonKeyFields_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.retrieveNonKeyFields(object);
        verify(knsLegacyDataAdapter).retrieveNonKeyFields(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testRetrieveReferenceObject() throws Exception {
        Object object = newNonLegacyObject();
        lda.retrieveReferenceObject(object, "blah");
        verify(kradLegacyDataAdapter).retrieveReferenceObject(eq(object), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testRetrieveReferenceObject_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.retrieveReferenceObject(object, "blah");
        verify(knsLegacyDataAdapter).retrieveReferenceObject(eq(object), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testRefreshAllNonUpdatingReferences() throws Exception {
        Object object = newNonLegacyObject();
        lda.refreshAllNonUpdatingReferences(object);
        verify(kradLegacyDataAdapter).refreshAllNonUpdatingReferences(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testRefreshAllNonUpdatingReferences_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.refreshAllNonUpdatingReferences(object);
        verify(knsLegacyDataAdapter).refreshAllNonUpdatingReferences(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }


    @Test
    public void testIsProxied() throws Exception {
        Object object = newNonLegacyObject();
        lda.isProxied(object);
        verify(kradLegacyDataAdapter).isProxied(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testIsProxied_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.isProxied(object);
        verify(knsLegacyDataAdapter).isProxied(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }


    @Test
    public void testResolveProxy() throws Exception {
        Object object = newNonLegacyObject();
        lda.resolveProxy(object);
        verify(kradLegacyDataAdapter).resolveProxy(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testResolveProxy_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.resolveProxy(object);
        verify(knsLegacyDataAdapter).resolveProxy(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testFindCollectionBySearchHelper() throws Exception {
        lda.findCollectionBySearchHelper(NonLegacy.class, new HashMap<String, String>(), true, true, 50);
        verify(kradLegacyDataAdapter).findCollectionBySearchHelper(eq(NonLegacy.class), anyMapOf(String.class,
                String.class), eq(true), eq(true), eq(50));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testFindCollectionBySearchHelper_Legacy() throws Exception {
        enableLegacy();
        lda.findCollectionBySearchHelper(Legacy.class, new HashMap<String, String>(), true, true, 50);
        verify(knsLegacyDataAdapter).findCollectionBySearchHelper(eq(Legacy.class), anyMapOf(String.class,
                String.class), eq(true), eq(true), eq(50));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testFindObjectBySearch() throws Exception {
        lda.findObjectBySearch(NonLegacy.class, new HashMap<String, String>());
        verify(kradLegacyDataAdapter).findObjectBySearch(eq(NonLegacy.class), anyMapOf(String.class, String.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testFindObjectBySearch_Legacy() throws Exception {
        enableLegacy();
        lda.findObjectBySearch(Legacy.class, new HashMap<String, String>());
        verify(knsLegacyDataAdapter).findObjectBySearch(eq(Legacy.class), anyMapOf(String.class, String.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testAllPrimaryKeyValuesPresentAndNotWildcard() throws Exception {
        lda.allPrimaryKeyValuesPresentAndNotWildcard(NonLegacy.class, new HashMap<String, String>());
        verify(kradLegacyDataAdapter).allPrimaryKeyValuesPresentAndNotWildcard(eq(NonLegacy.class), anyMapOf(
                String.class, String.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testAllPrimaryKeyValuesPresentAndNotWildcard_Legacy() throws Exception {
        enableLegacy();
        lda.allPrimaryKeyValuesPresentAndNotWildcard(Legacy.class, new HashMap<String, String>());
        verify(knsLegacyDataAdapter).allPrimaryKeyValuesPresentAndNotWildcard(eq(Legacy.class), anyMapOf(String.class, String.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testListPrimaryKeyFieldNames() throws Exception {
        lda.listPrimaryKeyFieldNames(NonLegacy.class);
        verify(kradLegacyDataAdapter).listPrimaryKeyFieldNames(eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testListPrimaryKeyFieldNames_Legacy() throws Exception {
        enableLegacy();
        lda.listPrimaryKeyFieldNames(Legacy.class);
        verify(knsLegacyDataAdapter).listPrimaryKeyFieldNames(eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testDetermineCollectionObjectType() throws Exception {
        lda.determineCollectionObjectType(NonLegacy.class, "blah");
        verify(kradLegacyDataAdapter).determineCollectionObjectType(eq(NonLegacy.class), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testDetermineCollectionObjectType_Legacy() throws Exception {
        enableLegacy();
        lda.determineCollectionObjectType(Legacy.class, "blah");
        verify(knsLegacyDataAdapter).determineCollectionObjectType(eq(Legacy.class), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testHasReference() throws Exception {
        lda.hasReference(NonLegacy.class, "blah");
        verify(kradLegacyDataAdapter).hasReference(eq(NonLegacy.class), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testHasReference_Legacy() throws Exception {
        enableLegacy();
        lda.hasReference(Legacy.class, "blah");
        verify(knsLegacyDataAdapter).hasReference(eq(Legacy.class), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testHasCollection() throws Exception {
        lda.hasCollection(NonLegacy.class, "blah");
        verify(kradLegacyDataAdapter).hasCollection(eq(NonLegacy.class), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testHasCollection_Legacy() throws Exception {
        enableLegacy();
        lda.hasCollection(Legacy.class, "blah");
        verify(knsLegacyDataAdapter).hasCollection(eq(Legacy.class), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testIsExtensionAttribute() throws Exception {
        lda.isExtensionAttribute(NonLegacy.class, "blah", NonLegacy.class);
        verify(kradLegacyDataAdapter).isExtensionAttribute(eq(NonLegacy.class), eq("blah"), eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testIsExtensionAttribute_Legacy() throws Exception {
        enableLegacy();
        lda.isExtensionAttribute(Legacy.class, "blah", Legacy.class);
        verify(knsLegacyDataAdapter).isExtensionAttribute(eq(Legacy.class), eq("blah"), eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetExtensionAttributeClass() throws Exception {
        lda.getExtensionAttributeClass(NonLegacy.class, "blah");
        verify(kradLegacyDataAdapter).getExtensionAttributeClass(eq(NonLegacy.class), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetExtensionAttributeClass_Legacy() throws Exception {
        enableLegacy();
        lda.getExtensionAttributeClass(Legacy.class, "blah");
        verify(knsLegacyDataAdapter).getExtensionAttributeClass(eq(Legacy.class), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetPrimaryKeyFieldValuesDOMDS() throws Exception {
        Object object = newNonLegacyObject();
        lda.getPrimaryKeyFieldValuesDOMDS(object);
        verify(kradLegacyDataAdapter).getPrimaryKeyFieldValuesDOMDS(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetPrimaryKeyFieldValuesDOMDS_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.getPrimaryKeyFieldValuesDOMDS(object);
        verify(knsLegacyDataAdapter).getPrimaryKeyFieldValuesDOMDS(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testEqualsByPrimaryKeys() throws Exception {
        Object object1 = newNonLegacyObject();
        Object object2 = newNonLegacyObject();
        lda.equalsByPrimaryKeys(object1, object2);
        verify(kradLegacyDataAdapter).equalsByPrimaryKeys(eq(object1), eq(object2));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testEqualsByPrimaryKeys_Legacy() throws Exception {
        enableLegacy();
        Object object1 = newLegacyObject();
        Object object2 = newLegacyObject();
        lda.equalsByPrimaryKeys(object1, object2);
        verify(knsLegacyDataAdapter).equalsByPrimaryKeys(eq(object1), eq(object2));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

//    @Test
//    public void testToPersistableBusinessObject() throws Exception {
//        Object object = newNonLegacyObject();
//        lda.toPersistableBusinessObject(object);
//        verify(kradLegacyDataAdapter).toPersistableBusinessObject(eq(object));
//        verifyZeroInteractions(knsLegacyDataAdapter);
//    }
//
//    @Test
//    public void testToPersistableBusinessObject_Legacy() throws Exception {
//        enableLegacy();
//        Object object = newLegacyObject();
//        lda.toPersistableBusinessObject(object);
//        verify(knsLegacyDataAdapter).toPersistableBusinessObject(eq(object));
//        verifyZeroInteractions(kradLegacyDataAdapter);
//    }

    @Test
    public void testMaterializeAllSubObjects() throws Exception {
        Object object = newNonLegacyObject();
        lda.materializeAllSubObjects(object);
        verify(kradLegacyDataAdapter).materializeAllSubObjects(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testMaterializeAllSubObjects_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.materializeAllSubObjects(object);
        verify(knsLegacyDataAdapter).materializeAllSubObjects(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetPropertyType() throws Exception {
        Object object = newNonLegacyObject();
        lda.getPropertyType(object, "blah");
        verify(kradLegacyDataAdapter).getPropertyType(eq(object), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetPropertyType_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.getPropertyType(object, "blah");
        verify(knsLegacyDataAdapter).getPropertyType(eq(object), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetExtension() throws Exception {
        lda.getExtension(NonLegacyPersistableBusinessObject.class);
        verify(kradLegacyDataAdapter).getExtension(eq(NonLegacyPersistableBusinessObject.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetExtension_Legacy() throws Exception {
        enableLegacy();
        lda.getExtension(Legacy.class);
        verify(knsLegacyDataAdapter).getExtension(eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testRefreshReferenceObject() throws Exception {
        PersistableBusinessObject object = newNonLegacyPersistableBusinessObject();
        lda.refreshReferenceObject(object, "blah");
        verify(kradLegacyDataAdapter).refreshReferenceObject(eq(object), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testRefreshReferenceObject_Legacy() throws Exception {
        enableLegacy();
        PersistableBusinessObject object = newLegacyObject();
        lda.refreshReferenceObject(object, "blah");
        verify(knsLegacyDataAdapter).refreshReferenceObject(eq(object), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testIsLockable() throws Exception {
        Object object = newNonLegacyObject();
        lda.isLockable(object);
        verify(kradLegacyDataAdapter).isLockable(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testIsLockable_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.isLockable(object);
        verify(knsLegacyDataAdapter).isLockable(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testVerifyVersionNumber() throws Exception {
        Object object = newNonLegacyObject();
        lda.verifyVersionNumber(object);
        verify(kradLegacyDataAdapter).verifyVersionNumber(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testVerifyVersionNumber_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.verifyVersionNumber(object);
        verify(knsLegacyDataAdapter).verifyVersionNumber(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testCreateQuickFinder() throws Exception {
        lda.createQuickFinder(NonLegacy.class, "blah");
        verify(kradLegacyDataAdapter).createQuickFinder(eq(NonLegacy.class), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testCreateQuickFinder_Legacy() throws Exception {
        enableLegacy();
        lda.createQuickFinder(Legacy.class, "blah");
        verify(knsLegacyDataAdapter).createQuickFinder(eq(Legacy.class), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testIsReferenceUpdatable() throws Exception {
        lda.isReferenceUpdatable(NonLegacy.class, "blah");
        verify(kradLegacyDataAdapter).isReferenceUpdatable(eq(NonLegacy.class), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testIsReferenceUpdatable_Legacy() throws Exception {
        enableLegacy();
        lda.isReferenceUpdatable(Legacy.class, "blah");
        verify(knsLegacyDataAdapter).isReferenceUpdatable(eq(Legacy.class), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testListReferenceObjectFields() throws Exception {
        lda.listReferenceObjectFields(NonLegacy.class);
        verify(kradLegacyDataAdapter).listReferenceObjectFields(eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testListReferenceObjectFields_Legacy() throws Exception {
        enableLegacy();
        lda.listReferenceObjectFields(Legacy.class);
        verify(knsLegacyDataAdapter).listReferenceObjectFields(eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testIsCollectionUpdatable() throws Exception {
        lda.isCollectionUpdatable(NonLegacy.class, "blah");
        verify(kradLegacyDataAdapter).isCollectionUpdatable(eq(NonLegacy.class), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testIsCollectionUpdatable_Legacy() throws Exception {
        enableLegacy();
        lda.isCollectionUpdatable(Legacy.class, "blah");
        verify(knsLegacyDataAdapter).isCollectionUpdatable(eq(Legacy.class), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testListCollectionObjectTypes() throws Exception {
        lda.listCollectionObjectTypes(NonLegacy.class);
        verify(kradLegacyDataAdapter).listCollectionObjectTypes(eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testListCollectionObjectTypes_Legacy() throws Exception {
        enableLegacy();
        lda.listCollectionObjectTypes(Legacy.class);
        verify(knsLegacyDataAdapter).listCollectionObjectTypes(eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetReferenceIfExists() throws Exception {
        PersistableBusinessObject object = newNonLegacyPersistableBusinessObject();
        lda.getReferenceIfExists(object, "blah");
        verify(kradLegacyDataAdapter).getReferenceIfExists(eq(object), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetReferenceIfExists_Legacy() throws Exception {
        enableLegacy();
        PersistableBusinessObject object = newLegacyObject();
        lda.getReferenceIfExists(object, "blah");
        verify(knsLegacyDataAdapter).getReferenceIfExists(eq(object), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testAllForeignKeyValuesPopulatedForReference() throws Exception {
        PersistableBusinessObject object = newNonLegacyPersistableBusinessObject();
        lda.allForeignKeyValuesPopulatedForReference(object, "blah");
        verify(kradLegacyDataAdapter).allForeignKeyValuesPopulatedForReference(eq(object), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testAllForeignKeyValuesPopulatedForReference_Legacy() throws Exception {
        enableLegacy();
        PersistableBusinessObject object = newLegacyObject();
        lda.allForeignKeyValuesPopulatedForReference(object, "blah");
        verify(knsLegacyDataAdapter).allForeignKeyValuesPopulatedForReference(eq(object), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetDictionaryRelationship() throws Exception {
        lda.getDictionaryRelationship(NonLegacy.class, "blah");
        verify(kradLegacyDataAdapter).getDictionaryRelationship(eq(NonLegacy.class), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetDictionaryRelationship_Legacy() throws Exception {
        enableLegacy();
        lda.getDictionaryRelationship(Legacy.class, "blah");
        verify(knsLegacyDataAdapter).getDictionaryRelationship(eq(Legacy.class), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetTitleAttribute() throws Exception {
        lda.getTitleAttribute(NonLegacy.class);
        verify(kradLegacyDataAdapter).getTitleAttribute(eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetTitleAttribute_Legacy() throws Exception {
        enableLegacy();
        lda.getTitleAttribute(Legacy.class);
        verify(knsLegacyDataAdapter).getTitleAttribute(eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testAreNotesSupported() throws Exception {
        lda.areNotesSupported(NonLegacy.class);
        verify(kradLegacyDataAdapter).areNotesSupported(eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testAreNotesSupported_Legacy() throws Exception {
        enableLegacy();
        lda.areNotesSupported(Legacy.class);
        verify(knsLegacyDataAdapter).areNotesSupported(eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetDataObjectIdentifierString() throws Exception {
        Object object = newNonLegacyObject();
        lda.getDataObjectIdentifierString(object);
        verify(kradLegacyDataAdapter).getDataObjectIdentifierString(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetDataObjectIdentifierString_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.getDataObjectIdentifierString(object);
        verify(knsLegacyDataAdapter).getDataObjectIdentifierString(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetInquiryObjectClassIfNotTitle() throws Exception {
        Object object = newNonLegacyObject();
        lda.getInquiryObjectClassIfNotTitle(object, "blah");
        verify(kradLegacyDataAdapter).getInquiryObjectClassIfNotTitle(eq(object), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetInquiryObjectClassIfNotTitle_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.getInquiryObjectClassIfNotTitle(object, "blah");
        verify(knsLegacyDataAdapter).getInquiryObjectClassIfNotTitle(eq(object), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetInquiryParameters() throws Exception {
        Object object = newNonLegacyObject();
        lda.getInquiryParameters(object, new ArrayList<String>(), "blah");
        verify(kradLegacyDataAdapter).getInquiryParameters(eq(object), anyListOf(String.class), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetInquiryParameters_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.getInquiryParameters(object, new ArrayList<String>(), "blah");
        verify(knsLegacyDataAdapter).getInquiryParameters(eq(object), anyListOf(String.class), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testHasLocalLookup() throws Exception {
        lda.hasLocalLookup(NonLegacy.class);
        verify(kradLegacyDataAdapter).hasLocalLookup(eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testHasLocalLookup_Legacy() throws Exception {
        enableLegacy();
        lda.hasLocalLookup(Legacy.class);
        verify(knsLegacyDataAdapter).hasLocalLookup(eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testHasLocalInquiry() throws Exception {
        lda.hasLocalInquiry(NonLegacy.class);
        verify(kradLegacyDataAdapter).hasLocalInquiry(eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testHasLocalInquiry_Legacy() throws Exception {
        enableLegacy();
        lda.hasLocalInquiry(Legacy.class);
        verify(knsLegacyDataAdapter).hasLocalInquiry(eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetDataObjectRelationship() throws Exception {
        Object object = newNonLegacyObject();
        lda.getDataObjectRelationship(object, NonLegacy.class, "blah", "prefix", true, true, true);
        verify(kradLegacyDataAdapter).getDataObjectRelationship(eq(object), eq(NonLegacy.class), eq("blah"), eq(
                "prefix"), eq(true), eq(true), eq(true));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetDataObjectRelationship_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.getDataObjectRelationship(object, Legacy.class, "blah", "prefix", true, true, true);
        verify(knsLegacyDataAdapter).getDataObjectRelationship(eq(object), eq(Legacy.class), eq("blah"), eq(
                "prefix"), eq(true), eq(true), eq(true));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testIsPersistable() throws Exception {
        lda.isPersistable(NonLegacy.class);
        verify(kradLegacyDataAdapter).isPersistable(eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testIsPersistable_Legacy() throws Exception {
        enableLegacy();
        lda.isPersistable(Legacy.class);
        verify(knsLegacyDataAdapter).isPersistable(eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetForeignKeyFieldsPopulationState() throws Exception {
        Object object = newNonLegacyObject();
        lda.getForeignKeyFieldsPopulationState(object, "blah");
        verify(kradLegacyDataAdapter).getForeignKeyFieldsPopulationState(eq(object), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetForeignKeyFieldsPopulationState_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.getForeignKeyFieldsPopulationState(object, "blah");
        verify(knsLegacyDataAdapter).getForeignKeyFieldsPopulationState(eq(object), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetForeignKeysForReference() throws Exception {
        lda.getForeignKeysForReference(NonLegacy.class, "blah");
        verify(kradLegacyDataAdapter).getForeignKeysForReference(eq(NonLegacy.class), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetForeignKeysForReference_Legacy() throws Exception {
        enableLegacy();
        lda.getForeignKeysForReference(Legacy.class, "blah");
        verify(knsLegacyDataAdapter).getForeignKeysForReference(eq(Legacy.class), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testHasPrimaryKeyFieldValues() throws Exception {
        Object object = newNonLegacyObject();
        lda.hasPrimaryKeyFieldValues(object);
        verify(kradLegacyDataAdapter).hasPrimaryKeyFieldValues(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testHasPrimaryKeyFieldValues_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.hasPrimaryKeyFieldValues(object);
        verify(knsLegacyDataAdapter).hasPrimaryKeyFieldValues(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testSetObjectPropertyDeep() throws Exception {
        Object object = newNonLegacyObject();
        lda.setObjectPropertyDeep(object, "blahName", NonLegacy.class, "blahValue");
        verify(kradLegacyDataAdapter).setObjectPropertyDeep(eq(object), eq("blahName"), eq(NonLegacy.class), eq(
                "blahValue"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testSetObjectPropertyDeep_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.setObjectPropertyDeep(object, "blahName", Legacy.class, "blahValue");
        verify(knsLegacyDataAdapter).setObjectPropertyDeep(eq(object), eq("blahName"), eq(Legacy.class), eq(
                "blahValue"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testMaterializeClassForProxiedObject() throws Exception {
        Object object = newNonLegacyObject();
        lda.materializeClassForProxiedObject(object);
        verify(kradLegacyDataAdapter).materializeClassForProxiedObject(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testMaterializeClassForProxiedObject_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.materializeClassForProxiedObject(object);
        verify(knsLegacyDataAdapter).materializeClassForProxiedObject(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetNestedValue() throws Exception {
        Object object = newNonLegacyObject();
        lda.getNestedValue(object, "blah");
        verify(kradLegacyDataAdapter).getNestedValue(eq(object), eq("blah"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testGetNestedValue_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.getNestedValue(object, "blah");
        verify(knsLegacyDataAdapter).getNestedValue(eq(object), eq("blah"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testCreateNewObjectFromClass() throws Exception {
        lda.createNewObjectFromClass(NonLegacy.class);
        verify(kradLegacyDataAdapter).createNewObjectFromClass(eq(NonLegacy.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testCreateNewObjectFromClass_Legacy() throws Exception {
        enableLegacy();
        lda.createNewObjectFromClass(Legacy.class);
        verify(knsLegacyDataAdapter).createNewObjectFromClass(eq(Legacy.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testIsNull() throws Exception {
        Object object = newNonLegacyObject();
        lda.isNull(object);
        verify(kradLegacyDataAdapter).isNull(eq(object));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testIsNull_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.isNull(object);
        verify(knsLegacyDataAdapter).isNull(eq(object));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testSetObjectProperty() throws Exception {
        Object object = newNonLegacyObject();
        lda.setObjectProperty(object, "blahName", NonLegacy.class, "blahValue");
        verify(kradLegacyDataAdapter).setObjectProperty(eq(object), eq("blahName"), eq(NonLegacy.class),
                eq("blahValue"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testSetObjectProperty_Legacy() throws Exception {
        enableLegacy();
        Object object = newLegacyObject();
        lda.setObjectProperty(object, "blahName", Legacy.class, "blahValue");
        verify(knsLegacyDataAdapter).setObjectProperty(eq(object), eq("blahName"), eq(Legacy.class), eq("blahValue"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }


    @Test
    public void testFindByDocumentHeaderId() throws Exception {
        lda.findByDocumentHeaderId(NonLegacyDocument.class, "1234");
        verify(kradLegacyDataAdapter).findByDocumentHeaderId(eq(NonLegacyDocument.class), eq("1234"));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testFindByDocumentHeaderId_Legacy() throws Exception {
        enableLegacy();
        lda.findByDocumentHeaderId(LegacyDocument.class, "1234");
        verify(knsLegacyDataAdapter).findByDocumentHeaderId(eq(LegacyDocument.class), eq("1234"));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testFindByDocumentHeaderIds() throws Exception {
        lda.findByDocumentHeaderIds(NonLegacyDocument.class, new ArrayList<String>());
        verify(kradLegacyDataAdapter).findByDocumentHeaderIds(eq(NonLegacyDocument.class), anyListOf(String.class));
        verifyZeroInteractions(knsLegacyDataAdapter);
    }

    @Test
    public void testFindByDocumentHeaderIds_Legacy() throws Exception {
        enableLegacy();
        lda.findByDocumentHeaderIds(LegacyDocument.class, new ArrayList<String>());
        verify(knsLegacyDataAdapter).findByDocumentHeaderIds(eq(LegacyDocument.class), anyListOf(String.class));
        verifyZeroInteractions(kradLegacyDataAdapter);
    }

    @Test
    public void testGetKnsLegacyDataAdapter() throws Exception {
        assertEquals(knsLegacyDataAdapter, lda.getKnsLegacyDataAdapter());
    }

    @Test
    public void testSetKnsLegacyDataAdapter() throws Exception {
        lda.setKnsLegacyDataAdapter(null);
        assertNull(lda.getKnsLegacyDataAdapter());
    }


    @Test
    public void testGetKradLegacyDataAdapter() throws Exception {
        assertEquals(kradLegacyDataAdapter, lda.getKradLegacyDataAdapter());
    }

    @Test
    public void testSetKradLegacyDataAdapter() throws Exception {
        lda.setKradLegacyDataAdapter(null);
        assertNull(lda.getKradLegacyDataAdapter());
    }

    @Test
    public void testSelectAdapter() throws Exception {

        // Scenario 1: KNS is not enabled, in this case it will always default to KRAD adapter
        assertEquals(kradLegacyDataAdapter, lda.selectAdapter(NonLegacy.class));
        assertEquals(kradLegacyDataAdapter, lda.selectAdapter(newNonLegacyObject()));
        assertEquals(kradLegacyDataAdapter, lda.selectAdapter(Legacy.class));
        assertEquals(kradLegacyDataAdapter, lda.selectAdapter(newLegacyObject()));

        // Now let's enable the KNS
        enableLegacy();

        // Scenario 2: Using a Class which is a valid legacy Class, should use KNS adapter
        assertEquals(knsLegacyDataAdapter, lda.selectAdapter(Legacy.class));

        // Scenario 3: Using an Object which is a valid legacy Object, should use KNS adapter
        assertEquals(knsLegacyDataAdapter, lda.selectAdapter(newLegacyObject()));

        // Scenario 4: Using a Class which is a not a legacy Class, should fall back to KRAD adapter even though legacy is enabled
        assertEquals(kradLegacyDataAdapter, lda.selectAdapter(NonLegacy.class));

        // Scenario 5: Using an Object which is a not a legacy Object, should fall back to KRAD adapter even though legacy is enabled
        assertEquals(kradLegacyDataAdapter, lda.selectAdapter(newNonLegacyObject()));

    }

    public static final class NonLegacy implements Serializable {}
    public static final class Legacy extends PersistableBusinessObjectBase {}
    public static final class NonLegacyPersistableBusinessObject extends PersistableBusinessObjectBase {}

    public static final class NonLegacyDocument extends DocumentBase {}
    public static final class LegacyDocument extends DocumentBase {}

}
