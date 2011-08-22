package org.kuali.rice.core.impl.cache;

import org.kuali.rice.core.api.cache.CacheService;
import org.kuali.rice.core.api.cache.CacheTarget;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;

public class CacheServiceImpl implements CacheService, InitializingBean {

    private CacheManager cacheManager;

    @Override
    public void flush(Collection<CacheTarget> cacheTargets) throws RiceIllegalArgumentException {
        if (cacheTargets == null) {
            throw new RiceIllegalArgumentException("cacheTargets is null");
        }
        for (CacheTarget cacheTarget : cacheTargets) {
            if (cacheTarget == null) {
                throw new RiceIllegalArgumentException("cacheTarget is null");
            }
            final Cache c = getCache(cacheTarget.getCache());
            if (c != null) {
                if (cacheTarget.containsKey()) {
                    c.evict(cacheTarget.getKey());
                } else {
                    c.clear();
                }
            }
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
