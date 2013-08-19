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
package org.kuali.rice.krad.data.provider.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    public void testDelete() {
        Object dataObject = new Object();
        service.delete(dataObject);

        verify(mockProvider).delete(eq(dataObject));
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
