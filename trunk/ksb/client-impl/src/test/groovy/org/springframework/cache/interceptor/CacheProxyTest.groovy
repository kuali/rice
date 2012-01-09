/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.springframework.cache.interceptor

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable

class CacheProxyTest {

    private FooService fooService;
    private FooCacheManager cacheManager;
    private static final CACHE_NAME_FOO = "foo";
    private static final CACHE_NAME_FOO_ANOTHER = "fooAnother";
    private static final Foo FOO1 = new Foo("1")
    private static final Foo FOO2 = new Foo("2")

    @Before
    void setupService() {
        fooService = new FooServiceImpl();
    }

    @Before
    void setupCacheManager() {
        cacheManager = new FooCacheManager();
    }

    @Test(expected = IllegalArgumentException.class)
    void testNullObject() {
        CacheProxy.createCacheProxy(null, {} as CacheManager)
    }

    @Test(expected = IllegalArgumentException.class)
    void testNullCacheManager() {
        CacheProxy.createCacheProxy(new Object(), null)
    }

    @Test
    void testNoErrorOnNonCachedAnnotatedClass() {
        def o = new Object()

        Assert.assertTrue(o.is(CacheProxy.createCacheProxy(o, {} as CacheManager)))
    }

    @Test
    void testNonCachingMethod() {
        def proxy = CacheProxy.createCacheProxy(fooService, cacheManager);
        def foos = proxy.getFoos();
        Assert.assertTrue(foos.isEmpty())
        Assert.assertTrue(cacheManager.getCache(CACHE_NAME_FOO).getNativeCache().isEmpty())
    }

    @Test
    void testCachingMethod() {
        def proxy = CacheProxy.createCacheProxy(fooService, cacheManager);
        def foo = proxy.getFoo(FOO1.id)
        Assert.assertTrue(FOO1.is(foo))
        def nativeCache = cacheManager.getCache(CACHE_NAME_FOO).getNativeCache();
        Assert.assertFalse(nativeCache.isEmpty())
        Assert.assertTrue(nativeCache[FOO1.id].get().is(FOO1))
    }

    @Test
    void testCacheEvicMethod() {
        def proxy = CacheProxy.createCacheProxy(fooService, cacheManager);

        //prime cache
        def cache = cacheManager.getCache(CACHE_NAME_FOO).getNativeCache()
        prime(cache)

        Assert.assertTrue(cache.size() == 2)
        Assert.assertTrue(cache["1"].get().is(FOO1))

        proxy.updateFoo(FOO1);

        Assert.assertTrue(cache.size() == 1)
        Assert.assertTrue(cache["2"].get().is(FOO2))
    }

    @Test
    void testCacheEvicAllMethod() {
        def proxy = CacheProxy.createCacheProxy(fooService, cacheManager);

        //prime cache
        def cache = cacheManager.getCache(CACHE_NAME_FOO).getNativeCache()
        prime(cache)

        Assert.assertTrue(cache.size() == 2)
        Assert.assertTrue(cache["1"].get().is(FOO1))

        proxy.addFoo(new Foo("3"));

        Assert.assertTrue(cache.isEmpty())
    }

    void prime(cache) {
         cache["1"] = new ValueWrapper() {
            Object get() {
                return FOO1
            }
        }

        cache["2"] = new ValueWrapper() {
            Object get() {
                return FOO2
            }
        }
    }

    static interface FooService {

        @Cacheable(value = CacheProxyTest.CACHE_NAME_FOO, key = "#id")
        Foo getFoo(String id)

        //not caching
        List<Foo> getFoos();

        @CacheEvict(value = CacheProxyTest.CACHE_NAME_FOO, key = "#f.id", allEntries = false)
        void updateFoo(Foo f);

        @CacheEvict(value = CacheProxyTest.CACHE_NAME_FOO, allEntries = true)
        void addFoo(Foo f);
    }

    static class FooServiceImpl implements FooService {


        static final def EMPTY = [];

        Foo getFoo(String id) {
            return CacheProxyTest.FOO1;
        }

        List<Foo> getFoos() {
            return EMPTY;
        }

        void updateFoo(Foo f) {

        }

        void addFoo(Foo f) {

        }
    }

    static class Foo {
        String id;
        public Foo(String i) {
            id = i;
        }

        String toString() {
            return "hashCode: ${hashCode()} id: ${id}"
        }
    }

    static class FooCacheManager implements CacheManager {
        Map<String, FooCache> internalCaches = [
                (CacheProxyTest.CACHE_NAME_FOO): new FooCache(CacheProxyTest.CACHE_NAME_FOO),
                (CacheProxyTest.CACHE_NAME_FOO_ANOTHER): new FooCache(CacheProxyTest.CACHE_NAME_FOO_ANOTHER)
        ];

        FooCache getCache(String name) {
            return internalCaches[name]
        }

        Collection<String> getCacheNames() {
            return internalCaches.keySet()
        }
    }

    static class FooCache implements Cache {
        Map<Object, ValueWrapper> internalCache = [:];
        String name;

        FooCache(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        Map<Object, ValueWrapper> getNativeCache() {
            return internalCache
        }

        ValueWrapper get(Object key) {
            return internalCache[key]
        }

        void put(Object key, Object value) {
            internalCache[key] = new ValueWrapper() {
                Object get() { return value }
            }
        }

        void evict(Object key) {
            internalCache.remove(key)
        }

        void clear() {
            internalCache.clear()
        }

    }
}
