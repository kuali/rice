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
package org.kuali.rice.krad.data.provider.impl;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests basic ProviderBasedDataObjectService implementation
 */
@RunWith(MockitoJUnitRunner.class)
public class ProviderBasedDataObjectServiceTest {
    @Mock PersistenceProvider mockProvider;

    @Mock ProviderRegistry providerRegistry;
    @Mock
    MetadataRepository metadataRepository;

    ProviderBasedDataObjectService service = new ProviderBasedDataObjectService();

    @Before
    public void setup() {
        when(providerRegistry.getPersistenceProvider(any(Class.class))).thenReturn(mockProvider);

        service.setMetadataRepository(metadataRepository);
        service.setProviderRegistry(providerRegistry);
    }

    @Test
    public void testGetMetadataRepository() {
        assertSame(metadataRepository, service.getMetadataRepository());
    }

    @Test
    public void testFind() {
        Object findResult = new Object();
        when(mockProvider.find(any(Class.class), any())).thenReturn(findResult);

        assertSame(findResult, service.find(Object.class, "id"));

        verify(mockProvider).find(any(Class.class), eq("id"));
    }

    @Test
    public void testFindUnique_NoResults() {
        QueryByCriteria criteria = QueryByCriteria.Builder.create().build();
        QueryResults<Object> emptyResults = GenericQueryResults.Builder.<Object>create().build();
        when(mockProvider.findMatching(Object.class, criteria)).thenReturn(emptyResults);

        Object singleResult = service.findUnique(Object.class, criteria);
        assertNull(singleResult);
    }

    @Test
    public void testFindUnique_OneResult() {
        QueryByCriteria criteria = QueryByCriteria.Builder.create().build();

        // create results that contain a single object
        Object theResult = new Object();
        GenericQueryResults.Builder<Object> resultsBuilder =
                GenericQueryResults.Builder.<Object>create();
        resultsBuilder.setResults(Lists.newArrayList(theResult));
        QueryResults<Object> results = resultsBuilder.build();
        when(mockProvider.findMatching(Object.class, criteria)).thenReturn(results);

        // now we should just get the one result back
        Object singleResult = service.findUnique(Object.class, criteria);
        assertNotNull(singleResult);
        assertEquals(theResult, singleResult);
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void testFindUnique_TooManyResults() {
        QueryByCriteria criteria = QueryByCriteria.Builder.create().build();

        // create results that contains multiple objects
        Object result1 = new Object();
        Object result2 = new Object();
        GenericQueryResults.Builder<Object> resultsBuilder =
                GenericQueryResults.Builder.<Object>create();
        resultsBuilder.setResults(Lists.newArrayList(result1, result2));
        QueryResults<Object> results = resultsBuilder.build();
        when(mockProvider.findMatching(Object.class, criteria)).thenReturn(results);

        // now when we invoke this, we should get the data access exception
        // (see the "expected" exception on the @Test annotation)
        service.findUnique(Object.class, criteria);
    }

    @Test
    public void testReduceCompoundKey_Null() {
        // test null, should just return null
        assertNull(service.reduceCompoundKey(null));
    }

    @Test
    public void testReduceCompoundKey_NonCompoundKey() {
        assertEquals("1234", service.reduceCompoundKey("1234"));
    }

    @Test
    public void testReduceCompoundKey_CompoundKeyOneValue() {
        Map<String, Object> singleKeyMap = new HashMap<String, Object>();
        singleKeyMap.put("myAwesomeId", 123456L);
        CompoundKey singleKey = new CompoundKey(singleKeyMap);
        assertEquals(123456L, service.reduceCompoundKey(singleKey));
    }

    @Test
    public void testReduceCompoundKey_CompoutKeyMultiValue() {
        Map<String, Object> multiKeyMap = new HashMap<String, Object>();
        multiKeyMap.put("myAwesomeId", 123456L);
        multiKeyMap.put("myOtherAwesomeId", "abcdefg");
        CompoundKey multiKey = new CompoundKey(multiKeyMap);
        assertEquals(multiKey, service.reduceCompoundKey(multiKey));
    }

    @Test
    public void testFindClass() {
        Object findResult = new Object();
        when(mockProvider.find(any(Class.class), any())).thenReturn(findResult);

        assertSame(findResult, service.find(Object.class, "id"));

        verify(mockProvider).find(any(Class.class), eq("id"));
    }

    @Test
    public void testFindMatching() {
        QueryResults findMatchingResult = mock(QueryResults.class);
        QueryByCriteria query = QueryByCriteria.Builder.create().build();
        when(mockProvider.findMatching(any(Class.class), any(QueryByCriteria.class))).thenReturn(findMatchingResult);

        assertSame(findMatchingResult, service.findMatching(Object.class, query));

        verify(mockProvider).findMatching(any(Class.class), eq(query));
    }

    @Test
    public void testFindMatchingClass() {
        QueryResults findMatchingResult = mock(QueryResults.class);
        QueryByCriteria query = QueryByCriteria.Builder.create().build();
        when(mockProvider.findMatching(any(Class.class), any(QueryByCriteria.class))).thenReturn(findMatchingResult);

        assertSame(findMatchingResult, service.findMatching(Object.class, query));

        verify(mockProvider).findMatching(any(Class.class), eq(query));
    }

    @Test
    public void testFindAll() {
        QueryResults allResults = mock(QueryResults.class);
        QueryByCriteria query = QueryByCriteria.Builder.create().build();
        when(mockProvider.findAll(any(Class.class))).thenReturn(allResults);

        assertSame(allResults, service.findAll(Object.class));

        verify(mockProvider).findAll(any(Class.class));
    }

    @Test
    public void testDelete() {
        Object dataObject = new Object();
        service.delete(dataObject);

        verify(mockProvider).delete(eq(dataObject));
    }

    @Test
    public void testDeleteMatching() {
        QueryByCriteria query = QueryByCriteria.Builder.create().build();
        service.deleteMatching(Object.class, query);

        verify(mockProvider).deleteMatching(any(Class.class), eq(query));
    }

    @Test
    public void testDeleteAll() {
        service.deleteAll(Object.class);

        verify(mockProvider).deleteAll(any(Class.class));
    }

    @Test
    public void testSave() {
        Serializable dataObject = new Serializable() {};
        service.save(dataObject);

        verify(mockProvider).save(eq(dataObject));
    }

    @Test
    public void testSupportsSupportedType() {
        // should support a type our mock is configured to support
        assertTrue(service.supports(String.class));
    }

    @Test
    public void testDoesNotSupportUnsupportedType() {
        // disavow any persistenceprovider mapping for this type
        when(providerRegistry.getPersistenceProvider(String.class)).thenReturn(null);
        assertFalse(service.supports(String.class));
    }
}
