package org.kuali.rice.core.impl.cache;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.cache.CacheService;
import org.kuali.rice.core.api.cache.CacheTarget;
import org.kuali.rice.ksb.api.messaging.MessageHelper;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NamedBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A distributed cache manager that wraps a cache manager and adds distributed cache capabilities
 * through the kuali service bus.
 *
 * <p>
 * If in a transaction, distributed cache messages are queued until a transaction completes successfully.
 * They are then sent as a single message rather than sending individual messages.
 * If the transaction does not complete successfully then all messages are discarded.
 * </p>
 *
 * <p>
 * If not in a transaction, distributed messages are sent immediately.  This should be avoided and is likely
 * the result of a programming error.
 * </p>
 */
public final class DistributedCacheManagerDecorator implements CacheManager, InitializingBean, BeanNameAware, NamedBean {
    private static final Log LOG = LogFactory.getLog(DistributedCacheManagerDecorator.class);

    private CacheManager cacheManager;
    private MessageHelper messageHelper;
    private String serviceName;
    private String name;

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
        //if the Cache isn't wrapped a second time. Don't want to wrap a null cache!
        if (!(cache instanceof DistributedCacheDecorator) && cache != null) {
            return new DistributedCacheDecorator(cache);
        }
        return cache;
    }

    private Collection<CacheService> getCacheServices() {
        final Collection<CacheService> services = messageHelper.getAllRemoteServicesAsynchronously(QName.valueOf(serviceName));
        return services != null ? services : Collections.<CacheService>emptyList();
    }

    private void flushCache(Collection<CacheTarget> cacheTargets) {
        try {
            //this code may not look the prettiest but we do not want to call into the KSB
            //unless it is necessary to do so.  Also, handling most common fail points to avoid
            //exceptions.
            if (!cacheTargets.isEmpty()) {
                final Collection<CacheService> services = getCacheServices();
                if (services != null) {
                    for (CacheService service : services) {
                        if (service != null) {
                            //wrap the each call in a try block so if one message send fails
                            //we still attempt the others
                            try {
                                service.flush(cacheTargets);
                            } catch (Throwable t) {
                                LOG.error("failed to flush the queue for specific endpoint for serviceName "
                                        + serviceName, t);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            LOG.error("failed to flush the queue for serviceName " + serviceName, t);
        }
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
            doDistributed(CacheTarget.singleEntry(getName(), sKey));
        }

        @Override
        public void clear() {
            cache.clear();
            doDistributed(CacheTarget.entireCache(getName()));
        }

        private String coerceStr(Object key) {
            return key != null ? key.toString() : (String) key;
        }

        /**
         * Sends a cache target message to distributed endpoints.  It will either send it in a delayed fashion when
         * bound to a transaction or immediately if no transaction is present.
         * @param target the cache target.  cannot be null.
         */
        private void doDistributed(CacheTarget target) {
            if (doTransactionalFlush()) {
                final CacheMessageSendingTransactionSynchronization ts = getCacheMessageSendingTransactionSynchronization();
                //adding to internal queue.  the Synchronization is already registered at this point
                ts.add(target);
            } else {
                flushCache(Collections.singleton(target));
            }
        }

        /**
         * Gets the {@link CacheMessageSendingTransactionSynchronization} that is registered with the transaction.  If no
         * synchronization is currently registered, then one is created and registered.
         * @return the synchronization. will no return null.
         */
        private CacheMessageSendingTransactionSynchronization getCacheMessageSendingTransactionSynchronization() {
            final Collection<TransactionSynchronization> sycs = TransactionSynchronizationManager.getSynchronizations();
            if (sycs != null) {
                for (final TransactionSynchronization ts : sycs) {
                    if (ts instanceof CacheMessageSendingTransactionSynchronization) {
                        return (CacheMessageSendingTransactionSynchronization) ts;
                    }
                }
            }
            final CacheMessageSendingTransactionSynchronization ts = new CacheMessageSendingTransactionSynchronization();
            TransactionSynchronizationManager.registerSynchronization(ts);
            return ts;
        }

        /**
         * Should a transaction bound flush be performed?
         * @return true for transaction based flushing
         */
        private boolean doTransactionalFlush() {
            return TransactionSynchronizationManager.isSynchronizationActive() && TransactionSynchronizationManager.isActualTransactionActive();
        }
    }

    /**
     * A TransactionSynchronizer that contains a queue of pending messages.  After the initial creation of this
     * synchronizer and when in the same transaction, this instance should be retrieved from the Spring Transaction
     * registry and messages should be added to the internal queue.  This way messages can be "bundled" into a single
     * message send.
     *
     * <p>
     *     This synchronizer will only send cache messages when the transaction complete successfully.
     * </p>
     */
    private final class CacheMessageSendingTransactionSynchronization extends TransactionSynchronizationAdapter {

        private final LinkedBlockingQueue<CacheTarget> flushQueue = new LinkedBlockingQueue<CacheTarget>();

        private void add(CacheTarget target) throws DistributedCacheException {
            try {
                flushQueue.put(target);
            } catch (InterruptedException e) {
                throw new DistributedCacheException(e);
            }
        }

        @Override
        public void afterCompletion(int status) {
            if (status == STATUS_COMMITTED) {
                flushCache(exhaustQueue(flushQueue));
            } else {
                //clear the queue. destroys all messages
                flushQueue.clear();
            }
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
        private Collection<CacheTarget> exhaustQueue(Queue<CacheTarget> targets) {
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
    }

    private static final class DistributedCacheException extends RuntimeException {
        private DistributedCacheException(Throwable cause) {
            super(cause);
        }
    }
}
