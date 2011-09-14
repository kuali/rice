package org.kuali.rice.core.impl.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NamedBean;
import org.springframework.cache.CacheManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A simple class that holds a global registry to the cache managers.
 */
public final class CacheManagerRegistryImpl implements CacheManagerRegistry {
    private static final String GET_NAME = "getName";
    private static final Log LOG = LogFactory.getLog(CacheManagerRegistryImpl.class);
    private static final String GET_NAME_MSG = "unable to get the getName method on the cache manager";

    private static final List<CacheManager> CACHE_MANAGERS = new CopyOnWriteArrayList<CacheManager>();

    public void setCacheManager(CacheManager c) {
        if (c == null) {
            throw new IllegalArgumentException("c is null");
        }

        CACHE_MANAGERS.add(c);
    }

    @Override
    public List<CacheManager> getCacheManagers() {
        return Collections.unmodifiableList(CACHE_MANAGERS);
    }

    @Override
    public CacheManager getCacheManager(String name) {
        for (CacheManager cm : getCacheManagers()) {
            if (name.equals(getCacheManagerName(cm))) {
                return cm;
            }
        }
        return null;
    }

    @Override
    public String getCacheManagerName(CacheManager cm) {
        if (cm instanceof NamedBean) {
            return ((NamedBean) cm).getBeanName();
        }

        String v = "Unnamed CacheManager " + cm.hashCode();
        try {
            final Method nameMethod = cm.getClass().getMethod(GET_NAME, new Class[] {});
            if (nameMethod != null && nameMethod.getReturnType() == String.class) {
                v = (String) nameMethod.invoke(cm, new Object[] {});
            }
        } catch (NoSuchMethodException e) {
            LOG.warn(GET_NAME_MSG, e);
        } catch (InvocationTargetException e) {
            LOG.warn(GET_NAME_MSG, e);
        } catch (IllegalAccessException e) {
            LOG.warn(GET_NAME_MSG, e);
        }

        return v;
    }
}
