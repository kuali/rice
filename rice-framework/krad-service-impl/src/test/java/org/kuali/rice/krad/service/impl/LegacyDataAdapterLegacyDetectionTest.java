/**
 * Copyright 2005-2013 The Kuali Foundation
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.namespace.QName;

import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.resourceloader.ResourceLoader;
import org.kuali.rice.core.api.util.ClasspathOrFileResourceLoader;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.core.framework.resourceloader.BeanFactoryResourceLoader;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.identity.PersonServiceImpl;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.dao.LookupDao;
import org.kuali.rice.krad.dao.MaintenanceDocumentDao;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.messages.Message;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DataObjectMetaDataService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.LegacyUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.core.io.Resource;

/**
 * Tests that the LegacyDataAdapter is correctly calling either the DataObjectService or appropriate legacy service
 */
@RunWith(MockitoJUnitRunner.class)
public class LegacyDataAdapterLegacyDetectionTest {
    private static final String TEST_ID = LegacyDataAdapterLegacyDetectionTest.class.getName();

    @Mock private DataObjectService dataObjectService;
    @Mock private MetadataRepository metadataRepository;
    @Mock private BusinessObjectService businessObjectService;
    @Mock private BusinessObjectMetaDataService businessObjectMetaDataService;
    @Mock private DataObjectMetaDataService dataObjectMetaDataService;
    @Mock private PersistenceService persistenceService;
    @Mock private LookupDao lookupDao;
    @Mock private LookupCriteriaGenerator lookupCriteriaGenerator;
    @Mock private DateTimeService dateTimeService;
    @Mock private DatabasePlatform databasePlatform;
    @Mock private PersistenceStructureService persistenceStructureService;

    @Mock private MaintenanceDocumentDao maintenanceDocumentDaoOjb;
    @Mock private DataObjectWrapper wrap;

    @InjectMocks private LegacyDataAdapterImpl lda = new LegacyDataAdapterImpl();
    @InjectMocks private KNSLegacyDataAdapterImpl knsLegacyDataAdapter = new KNSLegacyDataAdapterImpl();
    @InjectMocks private KRADLegacyDataAdapterImpl kradLegacyDataAdapter = new KRADLegacyDataAdapterImpl();

    @Before
    public void setup() throws Exception {
        GlobalResourceLoader.stop();

        SimpleConfig config = new SimpleConfig();
        config.putProperty(CoreConstants.Config.APPLICATION_ID, TEST_ID);
        ConfigContext.init(config);
        ConfigContext.getCurrentContextConfig().removeProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK);

        StaticListableBeanFactory testBf = new StaticListableBeanFactory();
        // required for search limits check
        testBf.addBean(CoreFrameworkServiceLocator.PARAMETER_SERVICE, mock(ParameterService.class));
        // AdHocRoutePerson invokes service bus in constructor D8
        // person = (Person) KimApiServiceLocator.getPersonService().getPersonImplementationClass().newInstance();
        // fake it out :(
        testBf.addBean(KimApiServiceLocator.KIM_PERSON_SERVICE, new PersonServiceImpl());
        testBf.addBean("kd-metadataRepository", mock(MetadataRepository.class));
        testBf.addBean("dataDictionaryService", mock(DataDictionaryService.class));
        testBf.addBean("knsLegacyDataAdapter", knsLegacyDataAdapter);
        testBf.addBean("kradLegacyDataAdapter",kradLegacyDataAdapter);
        lda.setKradLegacyDataAdapter(kradLegacyDataAdapter);
        lda.setKnsLegacyDataAdapter(knsLegacyDataAdapter);

        ResourceLoader rl = new BeanFactoryResourceLoader(new QName(TEST_ID), testBf);
        GlobalResourceLoader.addResourceLoader(rl);
        GlobalResourceLoader.start();

        // load up some OJB meta data
        Resource resource = new ClasspathOrFileResourceLoader().getResource("classpath:org/kuali/rice/krad/service/impl/OJB-repository-LegacyDataAdapterLegacyDetectionTest.xml");
        InputStream is = resource.getInputStream();
        MetadataManager mm = MetadataManager.getInstance();
        DescriptorRepository dr = mm.readDescriptorRepository(is);
        is.close();
        mm.mergeDescriptorRepository(dr);

        // make sure these calls return something because they are inevitably followed by deferences
        when(dataObjectService.findMatching(any(Class.class), any(QueryByCriteria.class))).thenReturn(mock(QueryResults.class));
        when(dataObjectService.wrap(any(Class.class))).thenReturn(wrap);
        when(metadataRepository.getMetadata(any(Class.class))).thenReturn(mock(DataObjectMetadata.class));
        when(lookupCriteriaGenerator.generateCriteria(any(Class.class), anyMap(), anyBoolean())).thenReturn(
                QueryByCriteria.Builder.create());
        when(lookupCriteriaGenerator.createObjectCriteriaFromMap(anyObject(), anyMap())).thenReturn(QueryByCriteria.Builder.create());
    }

    protected void enableLegacyFramework() {
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.ENABLE_LEGACY_DATA_FRAMEWORK, "true");
        ConfigContext.getCurrentContextConfig().putProperty(KRADConstants.Config.KNS_ENABLED, "true");
    }

    protected Serializable newDataObject() {
        // an arbitrary Serializable
        return new String("test");
    }

    protected PersistableBusinessObject newPersistableBusinessObject() {
        // an arbitrary PBO
        return new Message();
    }

    @Test
    public void testSave() {
        Serializable obj = newDataObject();
        lda.save(obj);
        verify(dataObjectService).save(obj);
        verify(businessObjectService, never()).save(any(PersistableBusinessObject.class));
    }

    @Test
    public void testLegacySave() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.save(obj);
        verify(businessObjectService).save(obj);
        verify(dataObjectService, never()).save(any(Serializable.class), any(PersistenceOption.class));
    }

    @Test
    public void testLinkAndSave() {
        Serializable obj = newDataObject();
        lda.linkAndSave(obj);
        verify(dataObjectService).save(obj, PersistenceOption.LINK);
        verify(businessObjectService, never()).linkAndSave(any(PersistableBusinessObject.class));
    }

    @Test
    public void testLegacyLinkAndSave() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.linkAndSave(obj);
        verify(businessObjectService).linkAndSave(obj);
        verify(dataObjectService, never()).save(any(PersistableBusinessObject.class), any(PersistenceOption.class));
    }

    @Test
    public void testFindByPrimaryKey() {
        Map<String, Object> keys = new HashMap<String, Object>();
        // must have at least one key value!
        keys.put("a", "b");
        lda.findByPrimaryKey(String.class, keys);
        verify(dataObjectService).find(eq(String.class), any(CompoundKey.class));
        verify(businessObjectService, never()).findByPrimaryKey(any(Class.class), anyMap());
    }

    @Test
    public void testLegacyFindByPrimaryKey() {
        enableLegacyFramework();
        Map<String, ?> keys = new HashMap<String, Object>();
        lda.findByPrimaryKey(Message.class, keys);
        verify(businessObjectService).findByPrimaryKey(Message.class, keys);
        verify(dataObjectService, never()).find(any(Class.class), any(CompoundKey.class));
    }

    @Test
    public void testFindBySinglePrimaryKey() {
        Object key = new String();
        lda.findBySinglePrimaryKey(String.class, key);
        verify(dataObjectService).find(String.class, key);
        verify(businessObjectService, never()).findBySinglePrimaryKey(any(Class.class), any());
    }

    @Test
    public void testLegacyFindBySinglePrimaryKey() {
        enableLegacyFramework();
        Object key = new String();
        lda.findBySinglePrimaryKey(Message.class, key);
        verify(businessObjectService).findBySinglePrimaryKey(Message.class, key);
        verify(dataObjectService, never()).find(any(Class.class), any());
    }

    @Test
    public void testDelete() {
        Serializable obj = newDataObject();
        lda.delete(obj);
        verify(dataObjectService).delete(obj);
        verify(businessObjectService, never()).delete(any(PersistableBusinessObject.class));
    }


    @Test
    public void testLegacyDelete() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.delete(obj);
        verify(businessObjectService).delete(obj);
        verify(dataObjectService, never()).delete(any());
    }

    @Test
    public void testDeleteMatching() {
        Map<String, ?> keys = new HashMap<String, Object>();
        lda.deleteMatching(String.class, keys);
        verify(dataObjectService).deleteMatching(eq(String.class), any(QueryByCriteria.class));
        verify(businessObjectService, never()).deleteMatching(any(Class.class), anyMap());
    }

    @Test
    public void testLegacyDeleteMatching() {
        enableLegacyFramework();
        Map<String, ?> keys = new HashMap<String, Object>();
        lda.deleteMatching(Message.class, keys);
        verify(businessObjectService).deleteMatching(Message.class, keys);
        verify(dataObjectService, never()).deleteMatching(any(Class.class), any(QueryByCriteria.class));
    }

    @Test
    public void testRetrieve() {
        Serializable obj = newDataObject();
        try {
            lda.retrieve(obj);
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            // throws this because it can't determine primary keys
        }
        verify(dataObjectService).wrap(any(Object.class));
        verify(wrap).getPrimaryKeyValues();
        verify(businessObjectService, never()).retrieve(any(PersistableBusinessObject.class));
    }

    @Test
    public void testLegacyRetrieve() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.retrieve(obj);
        verify(businessObjectService).retrieve(obj);
        verify(dataObjectService, never()).find(any(Class.class), any(CompoundKey.class));
    }

    @Test
    public void testFindMatching() {
        Map<String, ?> fields = new HashMap<String, Object>();
        lda.findMatching(String.class, fields);
        verify(dataObjectService).findMatching(eq(String.class), any(QueryByCriteria.class));
        verify(businessObjectService, never()).findMatching(any(Class.class), anyMap());
    }

    @Test
    public void testLegacyFindMatching() {
        enableLegacyFramework();
        Map<String, ?> fields = new HashMap<String, Object>();
        lda.findMatching(Message.class, fields);
        verify(businessObjectService).findMatching(Message.class, fields);
        verify(dataObjectService, never()).findMatching(any(Class.class), any(QueryByCriteria.class));
    }

    @Test
    public void testGetPrimaryKeyFieldValuesPS() {
        Object obj = new Object();
        lda.getPrimaryKeyFieldValues(obj);
        verify(dataObjectService).wrap(obj);
        verify(wrap).getPrimaryKeyValues();
        verify(persistenceService, never()).getPrimaryKeyFieldValues(any());
    }

    @Test
    public void testLegacyGetPrimaryKeyFieldValuesPS() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.getPrimaryKeyFieldValues(obj);
        verify(persistenceService).getPrimaryKeyFieldValues(obj);
        verify(dataObjectService, never()).wrap(obj);
    }


    @Test
    public void testRetrieveNonKeyFields() {
        Object obj = new Object();
        try{
            lda.retrieveNonKeyFields(obj);
            Assert.fail("Retrieve non key fields should not be called in non legacy contexts");
        } catch(UnsupportedOperationException e){
            verify(persistenceService, never()).retrieveNonKeyFields(any());
        }
    }
    @Test
    public void testLegacyRetrieveNonKeyFields() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.retrieveNonKeyFields(obj);
        verify(persistenceService).retrieveNonKeyFields(obj);
        // TODO: implement
    }

    @Test
    public void testRetrieveReferenceObject() {
        Object obj = new Object();
        String name = "";
        try{
            lda.retrieveReferenceObject(obj, name);
            Assert.fail("Retrieve reference object is not supported in non legacy context");
        } catch(UnsupportedOperationException e){
            verify(persistenceService, never()).retrieveReferenceObject(any(), anyString());
        }
    }
    @Test
    public void testLegacyRetrieveReferenceObject() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        String name = "";
        lda.retrieveReferenceObject(obj, name);
        verify(persistenceService).retrieveReferenceObject(obj, name);
        // TODO: implement
    }

    @Test
    public void testRefreshAllNonUpdatingReferences() {
        Object obj = new Object();
        try{
            lda.refreshAllNonUpdatingReferences(obj);
            Assert.fail("Refresh all non updating references should fall in non legacy context");
        } catch(UnsupportedOperationException e){
            verify(persistenceService, never()).refreshAllNonUpdatingReferences(any(PersistableBusinessObject.class));
        }

    }
    @Test
    public void testLegacyRefreshAllNonUpdatingReferences() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.refreshAllNonUpdatingReferences(obj);
        verify(persistenceService).refreshAllNonUpdatingReferences(obj);
        // TODO: implement
    }

    @Test
    public void testIsProxied() {
        Object obj = new Object();
        lda.isProxied(obj);
        // TODO: implement
        verify(persistenceService, never()).isProxied(any());
    }
    @Test
    public void testLegacyIsProxied() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.isProxied(obj);
        verify(persistenceService).isProxied(obj);
        // TODO: implement
    }

    @Test
    public void testResolveProxy() {
        Object obj = new Object();
        lda.resolveProxy(obj);
        // TODO: implement
        verify(persistenceService, never()).resolveProxy(any());
    }
    @Test
    public void testLegacyResolveProxy() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.resolveProxy(obj);
        verify(persistenceService).resolveProxy(obj);
        // TODO: implement
    }

    // Lookup methods

    @Test
    public void testFindCollectionBySearchHelper() {
        Map<String, String> fields = new HashMap<String, String>();
        lda.findCollectionBySearchHelper(Message.class, fields, true, false, 1);
        verify(lookupCriteriaGenerator).generateCriteria(Message.class, fields, false);
        verify(dataObjectService).findMatching(eq(Message.class), any(QueryByCriteria.class));
        verify(lookupDao, never()).findCollectionBySearchHelper(any(Class.class), anyMap(), anyBoolean(), anyBoolean(), anyInt());
    }
    @Test
    public void testLegacyFindCollectionBySearchHelper() {
        enableLegacyFramework();
        Map<String, String> fields = new HashMap<String, String>();
        lda.findCollectionBySearchHelper(Message.class, fields, true, false, 1);
        verify(lookupDao).findCollectionBySearchHelper(Message.class, fields, true, false, 1);
        verify(dataObjectService, never()).findMatching(any(Class.class), any(QueryByCriteria.class));
    }

    @Test
    public void testFindObjectBySearch() {
        Map<String, String> fields = new HashMap<String, String>();
        lda.findObjectBySearch(Message.class, fields);
        verify(dataObjectService).findMatching(eq(Message.class), any(QueryByCriteria.class));
        verify(lookupDao, never()).findObjectByMap(any(Class.class), anyMap());
    }
    @Test
    public void testLegacyFindObjectBySearch() {
        enableLegacyFramework();
        Map<String, String> fields = new HashMap<String, String>();
        lda.findObjectBySearch(Message.class, fields);
        verify(lookupDao).findObjectByMap(Message.class, fields);
        verify(dataObjectService, never()).findMatching(any(Class.class), any(QueryByCriteria.class));
    }

    @Test
    public void testGetAttachmentByNoteId() {
        lda.getAttachmentByNoteId(1l);
        verify(dataObjectService).find(Attachment.class, 1l);
        verify(businessObjectService, never()).findBySinglePrimaryKey(eq(Attachment.class),anyLong());
    }
    @Test
    public void testLegacyGetAttachmentByNoteId() throws Exception {
        enableLegacyFramework();
        LegacyUtils.doInLegacyContext(new Callable() {
            @Override
			public Object call() throws Exception {
                lda.getAttachmentByNoteId(1l);
                verify(businessObjectService).findBySinglePrimaryKey(eq(Attachment.class),anyLong());
                verify(dataObjectService, never()).find(any(Class.class), any());
                return null;
            }
        });
    }

//    @Test
//    public void testGetByDocumentHeaderId() {
//        String id = "";
//        lda.getByDocumentHeaderId(id);
//        verify(dataObjectService).find(DocumentHeader.class, id);
//        verify(documentHeaderDaoOjb, never()).getByDocumentHeaderId(id);
//    }
//    @Test
//    public void testLegacyGetByDocumentHeaderId() throws Exception {
//        enableLegacyFramework();
//        final String id = "";
//        LegacyUtils.doInLegacyContext(new Callable() {
//            @Override
//			public Object call() throws Exception {
//                lda.getByDocumentHeaderId(id);
////                verify(documentHeaderDaoOjb).getByDocumentHeaderId(id);
////                verify(dataObjectService, never()).find(any(Class.class), any());
//                verify(dataObjectService).find(DocumentHeader.class, id);
//                verify(documentHeaderDaoOjb, never()).getByDocumentHeaderId(id);
//                return null;
//            }
//        });
//
//    }
//
//    @Test
//    public void testGetDocumentHeaderBaseClass() {
//        lda.getDocumentHeaderBaseClass();
//        verify(documentHeaderDaoOjb, never()).getDocumentHeaderBaseClass();
//    }
//
//    @Test
//    public void testLegacyGetDocumentHeaderBaseClass() throws Exception {
//        enableLegacyFramework();
//        LegacyUtils.doInLegacyContext(new Callable() {
//            @Override
//			public Object call() throws Exception {
//                lda.getDocumentHeaderBaseClass();
//                verify(documentHeaderDaoOjb).getDocumentHeaderBaseClass();
//                return null;
//            }
//        });
//    }

    @Test
    public void testDeleteLocks() {
        String docid = "";
        QueryResults mockResults = mock(QueryResults.class);
        when(mockResults.getResults()).thenReturn(Arrays.asList(new MaintenanceLock[]{new MaintenanceLock(), new MaintenanceLock()}));
        when(dataObjectService.findMatching(eq(MaintenanceLock.class), any(QueryByCriteria.class))).thenReturn(mockResults);

        lda.deleteLocks(docid);

        verify(dataObjectService).deleteMatching(eq(MaintenanceLock.class), any(QueryByCriteria.class));
        verify(businessObjectService, never()).deleteMatching(eq(MaintenanceLock.class), anyMap());
    }
    @Test
    public void testLegacyDeleteLocks() throws Exception {
        enableLegacyFramework();
        final String docid = "";
        LegacyUtils.doInLegacyContext(new Callable() {
            @Override
			public Object call() throws Exception {
                lda.deleteLocks(docid);
                verify(businessObjectService).deleteMatching(eq(MaintenanceLock.class), anyMap());
                verify(dataObjectService, never()).findMatching(eq(MaintenanceLock.class), any(QueryByCriteria.class));
                return null;
            }
        });
    }

    @Test
    public void testGetLockingDocumentNumber() {
        String rep = "rep";
        String docNum = "docNum";
        lda.getLockingDocumentNumber(rep, docNum);
        verify(dataObjectService).findMatching(eq(MaintenanceLock.class), any(QueryByCriteria.class));
        verify(maintenanceDocumentDaoOjb, never()).getLockingDocumentNumber(anyString(), anyString());
    }
    @Test
    public void testLegacyGetLockingDocumentNumber() throws Exception {
        enableLegacyFramework();
        final String rep = "rep";
        final String docNum = "docNum";
        LegacyUtils.doInLegacyContext(new Callable() {
            @Override
			public Object call() throws Exception {
                lda.getLockingDocumentNumber(rep, docNum);
                verify(maintenanceDocumentDaoOjb).getLockingDocumentNumber(rep, docNum);
                verify(dataObjectService, never()).findMatching(any(Class.class), any(QueryByCriteria.class));
                return null;
            }
        });

    }

    @Test
    public void testStoreLocks() {
        List<MaintenanceLock> locks = Arrays.asList(new MaintenanceLock[]{new MaintenanceLock(), new MaintenanceLock()});
        lda.storeLocks(locks);
        verify(dataObjectService).save(locks.get(0));
        verify(dataObjectService).save(locks.get(1));
        verify(businessObjectService, never()).save(any(List.class));
    }
    @Test
    public void testLegacyStoreLocks() {
        enableLegacyFramework();
        List<MaintenanceLock> locks = Arrays.asList(new MaintenanceLock[]{new MaintenanceLock(), new MaintenanceLock()});
        lda.storeLocks(locks);
        verify(businessObjectService).save(any(List.class));
        verify(dataObjectService, never()).save(any(MaintenanceLock.class));
    }


    @Test
    public void testListPrimaryKeyFieldNames() {
        when(metadataRepository.contains(String.class)).thenReturn(true);
        when(lda.isPersistable(String.class)).thenReturn(true);

        lda.listPrimaryKeyFieldNames(String.class);
        verify(metadataRepository).getMetadata(String.class);
        verify(persistenceStructureService, never()).listPrimaryKeyFieldNames(any(Class.class));
    }
    @Test
    public void testLegacyListPrimaryKeyFieldNames() {
        enableLegacyFramework();
        when(metadataRepository.contains(Message.class)).thenReturn(true);
        when(lda.isPersistable(Message.class)).thenReturn(true);

        lda.listPrimaryKeyFieldNames(Message.class);

        verify(persistenceStructureService).listPrimaryKeyFieldNames(Message.class);
        verify(metadataRepository, never()).getMetadata(any(Class.class));
    }


    @Test
    public void testDetermineCollectionObjectType() {
        when(metadataRepository.contains(Message.class)).thenReturn(true);
        // return a valid DataObjectCollection so we don't throw IllegalArgumentException
        DataObjectMetadata mockMetaData = mock(DataObjectMetadata.class);
        DataObjectCollection mockCollection = mock(DataObjectCollection.class);
        when(mockCollection.getRelatedType()).thenReturn(any(Class.class));
        when(mockMetaData.getCollection("collectionName")).thenReturn(mockCollection);
        when(metadataRepository.getMetadata(Message.class)).thenReturn(mockMetaData);

        when(lda.isPersistable(DocumentType.class)).thenReturn(true);

        lda.determineCollectionObjectType(Message.class, "collectionName");

        verify(metadataRepository).getMetadata(Message.class);
        verify(persistenceStructureService, never()).listCollectionObjectTypes(any(Class.class));
    }
    @Test
    public void testLegacyDetermineCollectionObjectType() {
        enableLegacyFramework();
        when(metadataRepository.contains(Message.class)).thenReturn(true);
        when(lda.isPersistable(Message.class)).thenReturn(true);

        lda.determineCollectionObjectType(Message.class, "namespaceCode");

        verify(persistenceStructureService).listCollectionObjectTypes(Message.class);
        verify(metadataRepository, never()).getMetadata(any(Class.class));
    }

    @Test
    public void testIsLockable() {
        lda.isLockable("foo");
        verify(persistenceStructureService, never()).isPersistable(any(Class.class));
    }
    @Test
    public void testLegacyIsLockable() {
        enableLegacyFramework();
        lda.isLockable(new Message());
        verify(persistenceStructureService).isPersistable(Message.class);
    }

    @Test
    public void testVerifyVersionNumber() {
        Serializable obj = newDataObject();
        lda.verifyVersionNumber(obj);
        // TODO: implement
        verify(persistenceStructureService, never()).isPersistable(any(Class.class));
    }
    @Test
    public void testLegacyVerifyVersionNumber() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.verifyVersionNumber(obj);
        verify(persistenceStructureService).isPersistable(obj.getClass());
        // TODO: implement
    }

    @Test
    public void testGetPrimaryKeyFieldValuesDOMDS() {
        Object obj = new Object();
        lda.getPrimaryKeyFieldValuesDOMDS(obj);
        verify(dataObjectService).wrap(obj);
        verify(wrap).getPrimaryKeyValues();
        verify(dataObjectMetaDataService, never()).getPrimaryKeyFieldValues(any());
    }

    @Test
    public void testLegacyGetPrimaryKeyFieldValuesDOMDS() {
        enableLegacyFramework();
        PersistableBusinessObject obj = newPersistableBusinessObject();
        lda.getPrimaryKeyFieldValuesDOMDS(obj);
        verify(dataObjectMetaDataService).getPrimaryKeyFieldValues(obj);
        verify(dataObjectService, never()).wrap(obj);
    }

//    @Override
//    public boolean equalsByPrimaryKeys(Object do1, Object do2) {
//        if (LegacyUtils.useLegacyForObject(do1)) {
//            return dataObjectMetaDataService.equalsByPrimaryKeys(do1, do2);
//        }
//        return dataObjectService.wrap(do1).equalsByPrimaryKey(do2);
//    }


    @Test
    public void testEqualsByPrimaryKeys() {
        Object object1 = new Object();
        Object object2 = new Object();
        lda.equalsByPrimaryKeys(object1, object2);
        verify(dataObjectService).wrap(object1);
        verify(wrap).equalsByPrimaryKey(object2);
        verifyZeroInteractions(dataObjectMetaDataService);
    }

    @Test
    public void testEqualsByPrimaryKeysLegacy() {
        enableLegacyFramework();
        PersistableBusinessObject object1 = newPersistableBusinessObject();
        PersistableBusinessObject object2 = newPersistableBusinessObject();
        lda.equalsByPrimaryKeys(object1, object2);
        verify(dataObjectMetaDataService).equalsByPrimaryKeys(object1, object2);
        verifyZeroInteractions(dataObjectService);
    }



}
