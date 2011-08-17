package org.kuali.rice.core.impl.cache;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.cache.CacheService;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CacheServiceImpl implements CacheService, InitializingBean {

    private CacheManager cacheManager;

    @Override
    public void evict(String cache, String key) throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(cache)) {
            throw new RiceIllegalArgumentException("cache is blank or null");
        }

        if (StringUtils.isBlank(key)) {
            throw new RiceIllegalArgumentException("key is blank or null");
        }

        final Cache c = getCache(cache);
        if (c != null) {
            c.evict(key);
        }
    }

    @Override
    public void clear(String cache) throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(cache)) {
            throw new RiceIllegalArgumentException("cache is blank or null");
        }

        final Cache c = getCache(cache);
        if (c != null) {
            c.clear();
        }
    }

    private Cache getCache(String cache) {
        return cacheManager.getCache(cache);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (cacheManager == null) {
            throw new IllegalStateException("the cacheManager must be set");
        }
    }
}
