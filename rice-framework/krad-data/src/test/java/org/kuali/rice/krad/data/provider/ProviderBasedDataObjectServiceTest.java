package org.kuali.rice.krad.data.provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.data.provider.impl.ProviderBasedDataObjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.Serializable;

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
