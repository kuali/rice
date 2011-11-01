package org.kuali.rice.core.api.cache;

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

    /**
     * Gets a cache manager for a given name.  Name cannot be null or blank.
     *
     * @param name the cache manager name
     * @return the CacheManager
     * @throws IllegalArgumentException if the name is null or blank
     */
    CacheManager getCacheManager(String name);

    /**
     * Gets the name of a cache manager.  The cm cannot be null.  Will not return null or blank string.
     *
     * @param cm the cache manager
     * @return the name
     * @throws IllegalArgumentException if the cm is null
     */
    String getCacheManagerName(CacheManager cm);

    /**
     * Gets a cache manager for a given cache name.  Name cannot be null or blank.
     *
     * @param cacheName the  name of a Cache in a CacheManager.
     * @return the CacheManager
     * @throws IllegalArgumentException if the name is null or blank
     */
    CacheManager getCacheManagerByCacheName(String cacheName);
}
