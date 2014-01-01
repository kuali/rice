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
package org.kuali.rice.krad.data.provider;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.data.provider.impl.ProviderRegistryImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests ProviderRegistryImpl
 */
public class ProviderRegistryImplTest {
    // various Provider interfaces for testing
    private static interface CustomProvider extends Provider {}
    private static interface MultiProvider extends PersistenceProvider, MetadataProvider, CustomProvider {}

    // test types
    private static class A {}
    private static class B {}
    private static Class<A> TYPE_A = A.class;
    private static Class<B> TYPE_B = B.class;

    private ProviderRegistry registry;

    @Before
    public void setup() {
        registry = new ProviderRegistryImpl();
    }

    /**
     * Test empty state
     */
    @Test
    public void testEmpty() {
        assertEquals(0, registry.getProviders().size());
        assertEquals(0, registry.getMetadataProviders().size());
        assertEquals(0, registry.getProvidersForType(MetadataProvider.class).size());
        assertEquals(0, registry.getProvidersForType(PersistenceProvider.class).size());
        assertEquals(0, registry.getProvidersForType(CustomProvider.class).size());
        assertNull(registry.getPersistenceProvider(String.class));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRegisterProviderIllegalArgument() {
        registry.registerProvider(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUnregisterProviderIllegalArgument() {
        registry.unregisterProvider(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetProvidersForTypeIllegalArgument() {
        registry.getProvidersForType(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetPersistenceProviderIllegalArgument() {
        registry.getPersistenceProvider(null);
    }

    /**
     * Registers various Provider implementations and tests getters
     */
    @Test
    public void testRegisterProviders() {
        registry.registerProvider(mock(Provider.class));

        assertEquals(1, registry.getProviders().size());
        assertEquals(0, registry.getMetadataProviders().size());
        assertEquals(0, registry.getProvidersForType(MetadataProvider.class).size());
        assertEquals(0, registry.getProvidersForType(PersistenceProvider.class).size());
        assertEquals(0, registry.getProvidersForType(CustomProvider.class).size());
        assertNull(registry.getPersistenceProvider(String.class));

        // create mock metadataprovider that handles any dataobjecttype
        MetadataProvider mockMetadataProvider = mock(MetadataProvider.class);
        when(mockMetadataProvider.handles(any(Class.class))).thenReturn(true);

        registry.registerProvider(mockMetadataProvider);

        assertEquals(2, registry.getProviders().size());
        assertEquals(1, registry.getMetadataProviders().size());
        assertEquals(1, registry.getProvidersForType(MetadataProvider.class).size());
        assertEquals(0, registry.getProvidersForType(PersistenceProvider.class).size());
        assertEquals(0, registry.getProvidersForType(CustomProvider.class).size());
        assertNull(registry.getPersistenceProvider(String.class));
        assertSame(mockMetadataProvider, registry.getMetadataProvider(String.class));

        assertEquals(2, registry.getProviders().size());
        assertEquals(1, registry.getMetadataProviders().size());
        assertEquals(1, registry.getProvidersForType(MetadataProvider.class).size());
        assertEquals(0, registry.getProvidersForType(PersistenceProvider.class).size());
        assertEquals(0, registry.getProvidersForType(CustomProvider.class).size());
        assertNull(registry.getPersistenceProvider(String.class));
        assertSame(mockMetadataProvider, registry.getMetadataProvider(String.class));

        PersistenceProvider pp = mock(PersistenceProvider.class);
        when(pp.handles(TYPE_B)).thenReturn(true);
        registry.registerProvider(pp);

        assertEquals(3, registry.getProviders().size());
        assertEquals(1, registry.getMetadataProviders().size());
        assertEquals(1, registry.getProvidersForType(MetadataProvider.class).size());
        assertEquals(1, registry.getProvidersForType(PersistenceProvider.class).size());
        assertEquals(0, registry.getProvidersForType(CustomProvider.class).size());
        assertNull(registry.getPersistenceProvider(String.class));
        assertSame(pp, registry.getPersistenceProvider(B.class));
        assertSame(mockMetadataProvider, registry.getMetadataProvider(String.class));

        registry.registerProvider(mock(CustomProvider.class));

        assertEquals(4, registry.getProviders().size());
        assertEquals(1, registry.getMetadataProviders().size());
        assertEquals(1, registry.getProvidersForType(MetadataProvider.class).size());
        assertEquals(1, registry.getProvidersForType(PersistenceProvider.class).size());
        assertEquals(1, registry.getProvidersForType(CustomProvider.class).size());
        assertNull(registry.getPersistenceProvider(String.class));
        assertSame(pp, registry.getPersistenceProvider(B.class));
        assertSame(mockMetadataProvider, registry.getMetadataProvider(String.class));

        registry.registerProvider(mock(MultiProvider.class));

        assertEquals(5, registry.getProviders().size());
        assertEquals(2, registry.getMetadataProviders().size());
        assertEquals(2, registry.getProvidersForType(MetadataProvider.class).size());
        assertEquals(2, registry.getProvidersForType(PersistenceProvider.class).size());
        assertEquals(2, registry.getProvidersForType(CustomProvider.class).size());
        assertNull(registry.getPersistenceProvider(String.class));
        assertSame(pp, registry.getPersistenceProvider(B.class));
        // returns the *first* metadataprovider that handles the given type
        assertSame(mockMetadataProvider, registry.getMetadataProvider(String.class));
    }

    /**
     * Verifies duplicate providers can't be registered
     */
    @Test
    public void testRegisterDuplicateProviders() {
        Provider p = mock(Provider.class);
        MetadataProvider mp1 = mock(MetadataProvider.class);
        MetadataProvider mp2 = mock(MetadataProvider.class);

        registry.registerProvider(p);
        assertEquals(1, registry.getProviders().size());
        registry.registerProvider(p);
        assertEquals(1, registry.getProviders().size());

        registry.registerProvider(mp1);
        assertEquals(2, registry.getProviders().size());
        assertEquals(1, registry.getMetadataProviders().size());
        registry.registerProvider(mp1);
        assertEquals(2, registry.getProviders().size());
        assertEquals(1, registry.getMetadataProviders().size());

        registry.registerProvider(mp2);
        assertEquals(3, registry.getProviders().size());
        assertEquals(2, registry.getMetadataProviders().size());
        registry.registerProvider(mp2);
        assertEquals(3, registry.getProviders().size());
        assertEquals(2, registry.getMetadataProviders().size());
    }

    /**
     * Tests unregistering after registering
     */
    @Test
    public void testRegisterUnregister() {
        Provider a = mock(Provider.class);
        Provider b = mock(Provider.class);
        Provider c = mock(Provider.class);
        Provider d = mock(Provider.class);

        registry.registerProvider(a);
        registry.registerProvider(b);
        registry.registerProvider(c);
        registry.registerProvider(d);

        assertEquals(4, registry.getProviders().size());

        registry.unregisterProvider(c);
        registry.unregisterProvider(b);
        registry.unregisterProvider(d);
        registry.unregisterProvider(a);

        assertEquals(0, registry.getProviders().size());
    }

    /**
     * Verifies ProviderRegistryImpl is threadsafe
     */
    @Test
    public void testConcurrency() throws InterruptedException {
        final Class<? extends Provider>[] TYPES = new Class[] {
            Provider.class, MetadataProvider.class,
            PersistenceProvider.class, CustomProvider.class
        };

        int providers = 50;
        int threads = providers * 2; // just use live threads for all consumers/producers to ensure no consumer deadlock

        final BlockingQueue<Provider> queue = new LinkedBlockingQueue<Provider>();
        ExecutorService threadpool = Executors.newFixedThreadPool(threads);

        Callable<Object>[] producers = new Callable[providers];
        Callable<Object>[] consumers = new Callable[providers];
        Callable<Object> producer = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Provider p = mock(TYPES[RandomUtils.nextInt(5)]);
                registry.registerProvider(p);
                queue.add(p);
                return null;
            }
        };
        Callable<Object> consumer = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Provider p = queue.take();
                registry.unregisterProvider(p);
                return null;
            }
        };

        Arrays.fill(producers, producer);
        Arrays.fill(consumers, consumer);

        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>(providers * 2);
        tasks.addAll(Arrays.asList(producers));
        tasks.addAll(Arrays.asList(consumers));
        Collections.shuffle(tasks);

        System.out.println("Registering and unregistering " + providers + " providers");
        threadpool.invokeAll(tasks, 10, TimeUnit.SECONDS);

        // all producers and consumers should have run, we should be back at 0 providers registered
        assertEquals(0, registry.getProviders().size());
    }

    /**
     * Tests registration and lookup of multiple PersistenceProviders
     */
    @Test
    public void testRegisterPersistenceProviders() {
        PersistenceProvider mockA = mock(PersistenceProvider.class);
        when(mockA.handles(eq(TYPE_A))).thenReturn(true);

        PersistenceProvider mockB = mock(PersistenceProvider.class);
        when(mockB.handles(eq(TYPE_B))).thenReturn(true);

        PersistenceProvider mockAB = mock(PersistenceProvider.class);
        when(mockAB.handles(eq(TYPE_A))).thenReturn(true);
        when(mockAB.handles(eq(TYPE_B))).thenReturn(true);

        registry.registerProvider(mockA);

        assertEquals(1, registry.getProviders().size());
        assertEquals(0, registry.getMetadataProviders().size());
        assertEquals(1, registry.getProvidersForType(PersistenceProvider.class).size());
        assertEquals(0, registry.getProvidersForType(MetadataProvider.class).size());
        assertEquals(0, registry.getProvidersForType(CustomProvider.class).size());
        assertSame(mockA, registry.getPersistenceProvider(A.class));

        registry.registerProvider(mockB);

        assertEquals(2, registry.getProviders().size());
        assertEquals(0, registry.getMetadataProviders().size());
        assertEquals(2, registry.getProvidersForType(PersistenceProvider.class).size());
        assertEquals(0, registry.getProvidersForType(MetadataProvider.class).size());
        assertEquals(0, registry.getProvidersForType(CustomProvider.class).size());
        assertSame(mockA, registry.getPersistenceProvider(A.class));
        assertSame(mockB, registry.getPersistenceProvider(B.class));

        registry.registerProvider(mockAB);

        assertEquals(3, registry.getProviders().size());
        assertEquals(0, registry.getMetadataProviders().size());
        assertEquals(3, registry.getProvidersForType(PersistenceProvider.class).size());
        assertEquals(0, registry.getProvidersForType(MetadataProvider.class).size());
        assertEquals(0, registry.getProvidersForType(CustomProvider.class).size());
        assertSame(mockA, registry.getPersistenceProvider(A.class)); // still returns mockA
        assertSame(mockB, registry.getPersistenceProvider(B.class));
    }
}
