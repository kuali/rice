package org.kuali.rice.core.impl.cache;

import org.springframework.cache.CacheManager;

import java.util.List;

/**
 *
 */
public interface CacheManagerRegistry {
    /**
     * Will return a list of registered cache managers.  Will not return null.
     * @return a list of cache managers
     */
    List<CacheManager> getCacheManagers();
}
