package org.kuali.rice.core.impl.cache;

import org.springframework.cache.CacheManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A simple class that holds a global registry to the cache managers.
 */
public final class CacheManagerRegistryImpl implements CacheManagerRegistry {
    private final List<CacheManager> cacheManagers = new CopyOnWriteArrayList<CacheManager>();
    public void setCacheManager(CacheManager c) {
        if (c == null) {
            throw new IllegalArgumentException("c is null");
        }

        cacheManagers.add(c);
    }

    @Override
    public List<CacheManager> getCacheManagers() {
        return Collections.unmodifiableList(cacheManagers);
    }
}
