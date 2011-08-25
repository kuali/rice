package org.kuali.rice.core.impl.cache;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.cache.CacheService;
import org.kuali.rice.core.api.cache.CacheTarget;
import org.kuali.rice.ksb.api.messaging.MessageHelper;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NamedBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A distributed cache manager that wraps a cache manager and adds distributed cache capabilities
 * through the kuali service bus. Distributed cache messages are queued for a max period of time
 * and sent as a single message rather than sending the messages immediately.
 */
public final class DistributedCacheManagerDecorator implements CacheManager, InitializingBean, DisposableBean, BeanNameAware, NamedBean {

    private static final Log LOG = LogFactory.getLog(DistributedCacheManagerDecorator.class);
    private static final long MAX_WAIT_DEFAULT = TimeUnit.SECONDS.toMillis(60);

    private CacheManager cacheManager;
    private MessageHelper messageHelper;
    private String serviceName;
    private String flushQueueMaxWait;
    private String name;

    private final LinkedBlockingQueue<CacheTarget> flushQueue = new LinkedBlockingQueue<CacheTarget>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicReference<ScheduledFuture<?>> flusherFuture = new AtomicReference<ScheduledFuture<?>>();
    private final Runnable flusher = new Runnable() {
        @Override
        public void run() {
            try {
                //this code may not look the prettiest but we do not want to call into the KSB
                //unless it is necessary to do so.  Also, handling most common fail points to avoid
                //exceptions.
                if (!flushQueue.isEmpty()) {
                    final Collection<CacheService> services = getCacheServices();
                    if (services != null) {
                        //exhaustQueue clears out the queue. it cannot be placed in the inner loop
                        final Collection<CacheTarget> targets = exhaustQueue(flushQueue);
                        for (CacheService service : services) {
                            if (service != null) {
                                //wrap the each call in a try block so if one message send fails
                                //we still attempt the others
                                try {
                                    service.flush(targets);
                                } catch (Throwable t) {
                                    LOG.error("failed to flush the queue for specific endpoint for serviceName " + serviceName, t);
                                }
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                LOG.error("failed to flush the queue for serviceName " + serviceName, t);
            }
        }
    };



    @Override
    public Cache getCache(String name) {
        return wrap(cacheManager.getCache(name));
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    private Cache wrap(Cache cache) {
        //just in case they are cached do not want to wrap twice. Obviously this only works
        //if the Cache isn't wrapped a second time.
        if (!(cache instanceof DistributedCacheDecorator)) {
            return new DistributedCacheDecorator(cache);
        }
        return cache;
    }

    private Collection<CacheService> getCacheServices() {
        final Collection<CacheService> services = messageHelper.getAllRemoteServicesAsynchronously(QName.valueOf(serviceName));
        return services != null ? services : Collections.<CacheService>emptyList();
    }

    /**
     * Iterates over the passed in {@link Queue} calling the {@link Queue#poll} for each item.
     *
     * The returned list will also be normalized such that cache targets with keys will not be
     * present in the returned collection if a cache target exists for the same cache but
     * w/o a key (a complete cache flush)
     *
     * @param targets the queue to iterate over and exhaust
     * @return a new collection containing CacheTargets
     */
    private static Collection<CacheTarget> exhaustQueue(Queue<CacheTarget> targets) {
        final List<CacheTarget> normalized = new ArrayList<CacheTarget>();
        final Set<String> completeFlush = new HashSet<String>();

        CacheTarget target;
        while ((target = targets.poll()) != null) {
            normalized.add(target);
            if (!target.containsKey()) {
                completeFlush.add(target.getCache());
            }
        }

        return Collections2.filter(normalized, new Predicate<CacheTarget>() {
            @Override
            public boolean apply(CacheTarget input) {
                return !input.containsKey() || (input.containsKey() && !completeFlush.contains(input.getCache()));
            }
        });
    }

    @Override
    public void afterPropertiesSet() {
        if (cacheManager == null) {
            throw new IllegalStateException("cacheManager was null");
        }

        if (messageHelper == null) {
            throw new IllegalStateException("messageHelper was null");
        }

        if (StringUtils.isBlank(serviceName)) {
            throw new IllegalStateException("serviceName was null or blank");
        }

        if (StringUtils.isBlank(name)) {
            name = "NOT_NAMED";
        }

        final long maxWait = NumberUtils.isNumber(flushQueueMaxWait) ? Long.parseLong(flushQueueMaxWait) : MAX_WAIT_DEFAULT;
        flusherFuture.set(scheduler.scheduleAtFixedRate(flusher, maxWait, maxWait, TimeUnit.MILLISECONDS));
    }

    @Override
    public void destroy() {
        //shutdown the flush task, null out the old reference to the future
        ScheduledFuture<?> f = flusherFuture.get();
        if (f != null) {
            f.cancel(false);
            flusherFuture.set(null);
        }

        //shutdown the scheduler
        scheduler.shutdown();
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setFlushQueueMaxWait(String flushQueueMaxWait) {
        this.flushQueueMaxWait = flushQueueMaxWait;
    }

    @Override
    public String getBeanName() {
        return name;
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    /**
     * a cache wrapper that adds distributed cache flush capabilities.  Note: that all cache keys are
     * coerced to a String.  This means that all cache keys must have well-behaved toString methods.
     */
    private final class DistributedCacheDecorator implements Cache {

        private final Cache cache;

        private DistributedCacheDecorator(Cache cache) {
            this.cache = cache;
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
            putInQueue(CacheTarget.singleEntry(getName(), sKey));
        }

        @Override
        public void clear() {
            cache.clear();
            putInQueue(CacheTarget.entireCache(getName()));
        }

        private void putInQueue(CacheTarget target) throws DistributedCacheException {
            try {
                flushQueue.put(target);
            } catch (InterruptedException e) {
                throw new DistributedCacheException(e);
            }

            //check to see if the cache flush task is done for some reason.  if so restart it.
            if (flusherFuture.get().isDone()) {
                LOG.warn("The distributed cache flush task is no longer running.  This may indicate an error.");
                afterPropertiesSet();
            }
        }

        private String coerceStr(Object key) {
            return key != null ? key.toString(): (String) key;
        }
    }

    private static final class DistributedCacheException extends RuntimeException {
        private DistributedCacheException(Throwable cause) {
            super(cause);
        }
    }
}
