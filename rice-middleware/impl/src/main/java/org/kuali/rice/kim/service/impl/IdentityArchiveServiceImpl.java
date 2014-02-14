/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.impl.identity.EntityDefaultInfoCacheBo;
import org.kuali.rice.kim.impl.identity.IdentityArchiveService;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This is the default implementation for the IdentityArchiveService.
 * @see IdentityArchiveService
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityArchiveServiceImpl implements IdentityArchiveService, InitializingBean, DisposableBean {
	private static final Logger LOG = Logger.getLogger( IdentityArchiveServiceImpl.class );

	protected DataObjectService dataObjectService;
	protected ConfigurationService kualiConfigurationService;
	protected PlatformTransactionManager transactionManager;

	protected static final String EXEC_INTERVAL_SECS = "kim.identityArchiveServiceImpl.executionIntervalSeconds";
	protected static final String MAX_WRITE_QUEUE_SIZE = "kim.identityArchiveServiceImpl.maxWriteQueueSize";
	protected static final int EXECUTION_INTERVAL_SECONDS_DEFAULT = 600; // by default, flush the write queue this often
	protected static final int MAX_WRITE_QUEUE_SIZE_DEFAULT = 300; // cache this many KEDI's before forcing write

	protected final WriteQueue writeQueue = new WriteQueue();
	protected final EntityArchiveWriter writer = new EntityArchiveWriter();

	// all this ceremony just decorates the writer so it logs a message first, and converts the Callable to Runnable
	protected final Runnable maxQueueSizeExceededWriter =
		new CallableAdapter(new PreLogCallableWrapper<Boolean>(writer, Level.DEBUG, "max size exceeded, flushing write queue"));

	// ditto
	protected final Runnable scheduledWriter =
		new CallableAdapter(new PreLogCallableWrapper<Boolean>(writer, Level.DEBUG, "scheduled write out, flushing write queue"));

	// ditto
	protected final Runnable shutdownWriter =
		new CallableAdapter(new PreLogCallableWrapper<Boolean>(writer, Level.DEBUG, "rice is shutting down, flushing write queue"));

	protected int getExecutionIntervalSeconds() {
		final String prop = kualiConfigurationService.getPropertyValueAsString(EXEC_INTERVAL_SECS);
		try {
			return Integer.valueOf(prop).intValue();
		} catch (NumberFormatException e) {
			return EXECUTION_INTERVAL_SECONDS_DEFAULT;
		}
	}

	protected int getMaxWriteQueueSize() {
		final String prop = kualiConfigurationService.getPropertyValueAsString(MAX_WRITE_QUEUE_SIZE);
		try {
			return Integer.valueOf(prop).intValue();
		} catch (NumberFormatException e) {
			return MAX_WRITE_QUEUE_SIZE_DEFAULT;
		}
	}

	@Override
	public EntityDefault getEntityDefaultFromArchive( String entityId ) {
    	if (StringUtils.isBlank(entityId)) {
            throw new IllegalArgumentException("entityId is blank");
        }

    	List<EntityDefaultInfoCacheBo> results = dataObjectService.findMatching(EntityDefaultInfoCacheBo.class,
    	        QueryByCriteria.Builder.forAttribute(KimConstants.PrimaryKeyConstants.SUB_ENTITY_ID, entityId).build() ).getResults();
        EntityDefaultInfoCacheBo cachedValue = null;
        if ( !results.isEmpty() ) {
            cachedValue = results.get(0);
        }

    	return (cachedValue == null) ? null : cachedValue.convertCacheToEntityDefaultInfo();
    }

    @Override
	public EntityDefault getEntityDefaultFromArchiveByPrincipalId(String principalId) {
    	if (StringUtils.isBlank(principalId)) {
            throw new IllegalArgumentException("principalId is blank");
        }

    	EntityDefaultInfoCacheBo cachedValue = dataObjectService.find(EntityDefaultInfoCacheBo.class, principalId);
    	return (cachedValue == null) ? null : cachedValue.convertCacheToEntityDefaultInfo();
    }

    @Override
	public EntityDefault getEntityDefaultFromArchiveByPrincipalName(String principalName) {
    	if (StringUtils.isBlank(principalName)) {
            throw new IllegalArgumentException("principalName is blank");
        }

    	List<EntityDefaultInfoCacheBo> entities = dataObjectService.findMatching(EntityDefaultInfoCacheBo.class,
    	        QueryByCriteria.Builder.forAttribute("principalName", principalName).build()).getResults();
    	return entities.isEmpty() ? null : entities.get(0).convertCacheToEntityDefaultInfo();
    }

    @Override
    public EntityDefault getEntityDefaultFromArchiveByEmployeeId(String employeeId) {
        if (StringUtils.isBlank(employeeId)) {
            throw new IllegalArgumentException("employeeId is blank");
        }
        List<EntityDefaultInfoCacheBo> entities = dataObjectService.findMatching(EntityDefaultInfoCacheBo.class,
                QueryByCriteria.Builder.forAttribute("employeeId", employeeId).build()).getResults();
        return entities.isEmpty() ? null : entities.get(0).convertCacheToEntityDefaultInfo();
    }

    @Override
	public void saveEntityDefaultToArchive(EntityDefault entity) {
    	if (entity == null) {
            throw new IllegalArgumentException("entity is blank");
        }

    	// if the max size has been reached, schedule now
    	if (getMaxWriteQueueSize() <= writeQueue.offerAndGetSize(entity) /* <- this enqueues the KEDI */ &&
    			writer.requestSubmit()) {
    		KSBServiceLocator.getThreadPool().execute(maxQueueSizeExceededWriter);
    	}
    }

    @Override
    public void flushToArchive() {
        writer.call();
    }

	public void setKualiConfigurationService(
			ConfigurationService kualiConfigurationService) {
		this.kualiConfigurationService = kualiConfigurationService;
	}

    public void setTransactionManager(PlatformTransactionManager txMgr) {
        this.transactionManager = txMgr;
    }

    /** schedule the writer on the KSB scheduled pool. */
	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("scheduling writer...");
		KSBServiceLocator.getScheduledPool().scheduleAtFixedRate(scheduledWriter,
				getExecutionIntervalSeconds(), getExecutionIntervalSeconds(), TimeUnit.SECONDS);
	}

	/** flush the write queue immediately. */
	@Override
	public void destroy() throws Exception {
		shutdownWriter.run();
	}

	/**
	 * store the person to the database, but do this an alternate thread to
	 * prevent transaction issues since this service is non-transactional
	 *
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	protected class EntityArchiveWriter implements Callable {

		// flag used to prevent multiple processes from being submitted at once
		AtomicBoolean currentlySubmitted = new AtomicBoolean(false);

		private final Comparator<Comparable> nullSafeComparator = new Comparator<Comparable>() {
			@Override
			public int compare(Comparable i1, Comparable i2) {
				if (i1 != null && i2 != null) {
					return i1.compareTo(i2);
				} else if (i1 == null) {
					if (i2 == null) {
						return 0;
					} else {
						return -1;
					}
				} else { // if (entityId2 == null) {
					return 1;
				}
			};
		};

		/**
		 * Comparator that attempts to impose a total ordering on EntityDefault instances
		 */
		protected final Comparator<EntityDefault> kediComparator = new Comparator<EntityDefault>() {
			/**
			 * compares by entityId value
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(EntityDefault o1, EntityDefault o2) {
				String entityId1 = (o1 == null) ? null : o1.getEntityId();
				String entityId2 = (o2 == null) ? null : o2.getEntityId();

				int result = nullSafeComparator.compare(entityId1, entityId2);

				if (result == 0) {
					result = getPrincipalIdsString(o1).compareTo(getPrincipalIdsString(o2));
				}

				return result;
			}

			/**
			 * This method builds a newline delimited String containing the identity's principal IDs in sorted order
			 *
			 * @param entity
			 * @return
			 */
			private String getPrincipalIdsString(EntityDefault entity) {
				String result = "";
				if (entity != null) {
					List<Principal> principals = entity.getPrincipals();
					if (principals != null) {
						if (principals.size() == 1) { // one
							result = principals.get(0).getPrincipalId();
						} else { // multiple
							String [] ids = new String [principals.size()];
							int insertIndex = 0;
							for (Principal principal : principals) {
								ids[insertIndex++] = principal.getPrincipalId();
							}
							Arrays.sort(ids);
							result = StringUtils.join(ids, "\n");
						}
					}
				}
				return result;
			}
		};

		public boolean requestSubmit() {
			return currentlySubmitted.compareAndSet(false, true);
		}

		/**
		 * Call that tries to flush the write queue.
		 * @see Callable#call()
		 */
		@Override
		public Object call() {
			try {
				// the strategy is to grab chunks of entities, dedupe & sort them, and insert them in a big
				// batch to reduce transaction overhead.  Sorting is done so insertion order is guaranteed, which
				// prevents deadlocks between concurrent writers to the database.
				TransactionTemplate template = new TransactionTemplate(transactionManager);
				template.execute(new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus status) {
						EntityDefault entity = null;
						ArrayList<EntityDefault> entitiesToInsert = new ArrayList<EntityDefault>(getMaxWriteQueueSize());
						Set<String> deduper = new HashSet<String>(getMaxWriteQueueSize());

						// order is important in this conditional so that elements aren't dequeued and then ignored
						while (entitiesToInsert.size() < getMaxWriteQueueSize() && null != (entity = writeQueue.poll())) {
							if (deduper.add(entity.getEntityId())) {
								entitiesToInsert.add(entity);
							}
						}

						Collections.sort(entitiesToInsert, kediComparator);
						for (EntityDefault entityToInsert : entitiesToInsert) {
							dataObjectService.save( new EntityDefaultInfoCacheBo( entityToInsert ) );
						}
						return null;
					}
				});
			} finally { // make sure our running flag is unset, otherwise we'll never run again
				currentlySubmitted.compareAndSet(true, false);
			}

			return Boolean.TRUE;
		}
	}

	/**
	 * A class encapsulating a {@link ConcurrentLinkedQueue} and an {@link AtomicInteger} to
	 * provide fast offer(enqueue)/poll(dequeue) and size checking.  Size may be approximate due to concurrent
	 * activity, but for our purposes that is fine.
	 *
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	protected static class WriteQueue {
		AtomicInteger writeQueueSize = new AtomicInteger(0);
		ConcurrentLinkedQueue<EntityDefault> queue = new ConcurrentLinkedQueue<EntityDefault>();

		public int offerAndGetSize(EntityDefault entity) {
			queue.add(entity);
			return writeQueueSize.incrementAndGet();
		}

		protected EntityDefault poll() {
			EntityDefault result = queue.poll();
			if (result != null) { writeQueueSize.decrementAndGet(); }
			return result;
		}
	}

	/**
	 * decorator for a callable to log a message before it is executed
	 *
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	protected static class PreLogCallableWrapper<A> implements Callable<A> {

	    protected final Callable inner;
	    protected final Level level;
	    protected final String message;

		public PreLogCallableWrapper(Callable inner, Level level, String message) {
			this.inner = inner;
			this.level = level;
			this.message = message;
		}

		/**
		 * logs the message then calls the inner Callable
		 *
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		@SuppressWarnings("unchecked")
		public A call() throws Exception {
			LOG.log(level, message);
			return (A)inner.call();
		}
	}

	/**
	 * Adapts a Callable to be Runnable
	 *
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	protected static class CallableAdapter implements Runnable {

		private final Callable callable;

		public CallableAdapter(Callable callable) {
			this.callable = callable;
		}

		@Override
		public void run() {
			try {
				callable.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

    /**
     * @param dataObjectService the dataObjectService to set
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
