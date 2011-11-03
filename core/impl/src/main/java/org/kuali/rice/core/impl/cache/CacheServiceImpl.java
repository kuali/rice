/**
 * Copyright 2005-2011 The Kuali Foundation
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
