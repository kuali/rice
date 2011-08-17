package org.kuali.rice.core.impl.cache;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.cache.CacheService;
import org.kuali.rice.ksb.api.messaging.MessageHelper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Collections;

/**
 * A distributed cache manager that wraps a cache manager and adds distributed cache capabilities
 * through the kuali service bus.
 */
public final class DistributedCacheManager implements CacheManager {

    private final CacheManager cacheManager;
    private final MessageHelper messageHelper;
    private final String serviceName;

    /**
     * Creates an instance.
     *
     * @param cacheManager the cache manager to wrap
     * @param messageHelper the message helper used to interact with the ksb
     * @param serviceName the serviceName of the {@link CacheService}
     * @throws IllegalArgumentException if the cacheManager, messageHelper is null or the serviceName is null or blank
     */
    public DistributedCacheManager(CacheManager cacheManager, MessageHelper messageHelper, String serviceName) {
        if (cacheManager == null) {
            throw new IllegalArgumentException("cacheManager was null");
        }

        if (messageHelper == null) {
            throw new IllegalArgumentException("messageHelper was null");
        }

        if (StringUtils.isBlank(serviceName)) {
            throw new IllegalArgumentException("serviceName was null or blank");
        }

        this.cacheManager = cacheManager;
        this.messageHelper = messageHelper;
        this.serviceName = serviceName;
    }

    @Override
    public Cache getCache(String name) {
        return DistributedCache.wrap(cacheManager.getCache(name), messageHelper, serviceName);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    /**
     * a cache wrapper that adds distributed cache flush capabilities.  Note: that all cache keys are
     * coerced to a String.  This means that all cache keys must have well-behaved toString methods.
     */
    private static final class DistributedCache implements Cache {

        private final Cache cache;
        private final MessageHelper messageHelper;
        private final String serviceName;

        private DistributedCache(Cache cache, MessageHelper messageHelper, String serviceName) {
            this.cache = cache;
            this.messageHelper = messageHelper;
            this.serviceName = serviceName;
        }

        private static Cache wrap(Cache cache, MessageHelper messageHelper, String serviceName) {
            //just in case they are cached do not want to wrap twice. Obviously this only works
            //if the Cache isn't wrapped a second time.
            if (!(cache instanceof DistributedCache)) {
                return new DistributedCache(cache, messageHelper, serviceName);
            }
            return cache;
        }

        @Override
        public String getName() {
            return cache.getName();
        }

        @Override
        public Object getNativeCache() {
            return cache.getNativeCache();
        }

        @Override
        public ValueWrapper get(Object key) {
            final String sKey = coerceStr(key);
            return cache.get(sKey);
        }

        @Override
        public void put(Object key, Object value) {
            final String sKey = coerceStr(key);
            cache.put(sKey, value);
        }

        @Override
        public void evict(Object key) {
            final String sKey = coerceStr(key);
            cache.evict(sKey);

            for (CacheService s : getCacheServices()) {
                s.evict(getName(), sKey);
            }
        }

        @Override
        public void clear() {
            cache.clear();

            for (CacheService s : getCacheServices()) {
                s.clear(getName());
            }
        }

        private static String coerceStr(Object key) {
            return key != null ? key.toString(): (String) key;
        }

        private Collection<CacheService> getCacheServices() {
            final Collection<CacheService> services = messageHelper.getAllRemoteServicesAsynchronously(QName.valueOf(serviceName));
            return services != null ? services : Collections.<CacheService>emptyList();
        }
    }
}
