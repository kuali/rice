/*
 * Copyright 2007-2009 The Kuali Foundation
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.RiceConfigurer;
import org.kuali.rice.core.config.event.AfterStartEvent;
import org.kuali.rice.core.config.event.BeforeStopEvent;
import org.kuali.rice.core.config.event.RiceConfigEvent;
import org.kuali.rice.core.config.event.RiceConfigEventListener;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.impl.KimEntityDefaultInfoCacheImpl;
import org.kuali.rice.kim.service.IdentityArchiveService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 * This is the default implementation for the IdentityArchiveService. 
 * @see IdentityArchiveService
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class IdentityArchiveServiceImpl implements IdentityArchiveService, RiceConfigEventListener {
	private static final Logger LOG = Logger.getLogger( IdentityArchiveServiceImpl.class );
	
	private BusinessObjectService businessObjectService;
	
	private static final String EXEC_INTERVAL_SECS = "kim.identityArchiveServiceImpl.executionIntervalSeconds";
	private static final String MAX_WRITE_QUEUE_SIZE = "kim.identityArchiveServiceImpl.maxWriteQueueSize";
	
	private int executionIntervalSeconds = 600; // by default, flush the write queue this often
	private int maxWriteQueueSize = 300; // cache this many KEDI's before forcing write
	private final WriteQueue writeQueue = new WriteQueue();
	private final EntityArchiveWriter writer = new EntityArchiveWriter();
	
	// all this ceremony just decorates the writer so it logs a message first, and converts the Callable to Runnable
	private final Runnable maxQueueSizeExceededWriter = 
		new CallableAdapter(new PreLogCallableWrapper<Boolean>(writer, Level.DEBUG, "max size exceeded, flushing write queue"));

	// ditto
	private final Runnable scheduledWriter = 
		new CallableAdapter(new PreLogCallableWrapper<Boolean>(writer, Level.DEBUG, "scheduled write out, flushing write queue"));

	// ditto
	private final Runnable shutdownWriter = 
		new CallableAdapter(new PreLogCallableWrapper<Boolean>(writer, Level.DEBUG, "rice is shutting down, flushing write queue"));

	private final edu.emory.mathcs.backport.java.util.concurrent.Callable missRetryWriter = 
		new PreLogCallableWrapper<Boolean>(writer, Level.DEBUG, "missed in the archive, flushing write queue");

	
	public IdentityArchiveServiceImpl(Integer executionIntervalSeconds, Integer maxWriteQueueSize) {
		// register for RiceConfigEventS
		RiceConfigurer rice = 
			(RiceConfigurer)ConfigContext.getCurrentContextConfig().getObject( RiceConstants.RICE_CONFIGURER_CONFIG_NAME );
		LOG.debug("registering for events...");
		rice.getKimConfigurer().registerConfigEventListener(this);

		if (executionIntervalSeconds != null) {
			this.executionIntervalSeconds = executionIntervalSeconds; 
		}

		if (maxWriteQueueSize != null) {
			this.maxWriteQueueSize = maxWriteQueueSize;
		}
	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
	public KimEntityDefaultInfo getEntityDefaultInfoFromArchive( String entityId ) {
    	Map<String,String> criteria = new HashMap<String, String>(1);
    	criteria.put(KimConstants.PrimaryKeyConstants.ENTITY_ID, entityId);
    	KimEntityDefaultInfoCacheImpl cachedValue = (KimEntityDefaultInfoCacheImpl)getBusinessObjectService().findByPrimaryKey(KimEntityDefaultInfoCacheImpl.class, criteria);
    	if ( cachedValue == null ) {
    		flushOnMiss(); // flush and retry
    		cachedValue = (KimEntityDefaultInfoCacheImpl)getBusinessObjectService().findByPrimaryKey(KimEntityDefaultInfoCacheImpl.class, criteria);
    	}
    	return (cachedValue == null) ? null : cachedValue.convertCacheToEntityDefaultInfo();
    }

    public KimEntityDefaultInfo getEntityDefaultInfoFromArchiveByPrincipalId( String principalId ) {
    	Map<String,String> criteria = new HashMap<String, String>(1);
    	criteria.put("principalId", principalId);
    	KimEntityDefaultInfoCacheImpl cachedValue = (KimEntityDefaultInfoCacheImpl)getBusinessObjectService().findByPrimaryKey(KimEntityDefaultInfoCacheImpl.class, criteria);
    	if ( cachedValue == null ) {
    		flushOnMiss(); // flush and retry
    		cachedValue = (KimEntityDefaultInfoCacheImpl)getBusinessObjectService().findByPrimaryKey(KimEntityDefaultInfoCacheImpl.class, criteria);
    	}
    	return (cachedValue == null) ? null : cachedValue.convertCacheToEntityDefaultInfo();
    }

    @SuppressWarnings("unchecked")
	public KimEntityDefaultInfo getEntityDefaultInfoFromArchiveByPrincipalName( String principalName ) {
    	Map<String,String> criteria = new HashMap<String, String>(1);
    	criteria.put("principalName", principalName);
    	Collection<KimEntityDefaultInfoCacheImpl> entities = getBusinessObjectService().findMatching(KimEntityDefaultInfoCacheImpl.class, criteria);
    	if ( entities.isEmpty()  ) {
    		flushOnMiss();
    		entities = getBusinessObjectService().findMatching(KimEntityDefaultInfoCacheImpl.class, criteria);
    	}
    	return (entities == null || entities.size() == 0) ? null : entities.iterator().next().convertCacheToEntityDefaultInfo();
    }

    public void saveDefaultInfoToArchive( KimEntityDefaultInfo entity ) {
    	// if the max size has been exceeded, schedule now
    	if (maxWriteQueueSize < writeQueue.offerAndGetSize(entity) /* <- this enqueues the KEDI */ &&
    			!writer.isRunning()) {
    		KSBServiceLocator.getThreadPool().execute(maxQueueSizeExceededWriter);
    	}
    }
    
    /**
     * <h4>On events:</h4>
     * <p>{@link AfterStartEvent}: schedule the writer on the KSB scheduled pool
     * <p>{@link BeforeStopEvent}: flush the write queue immediately
     * 
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    public void onEvent(final RiceConfigEvent event) {
    	if (event instanceof AfterStartEvent) {
    		// on startup, schedule this to run
    		LOG.info("scheduling writer...");
    		KSBServiceLocator.getScheduledPool().scheduleAtFixedRate(scheduledWriter, 
    				executionIntervalSeconds, executionIntervalSeconds, TimeUnit.SECONDS);
    	} else if (event instanceof BeforeStopEvent) {
    		if (!writer.isRunning()) { 
    			KSBServiceLocator.getThreadPool().execute(shutdownWriter);
    		}
    	}
    }
    
    /**
     * Utility method to force a flush of the write queue.  It's intended to be called on a miss in the archive.
     */
	private void flushOnMiss() {
		try {
			int sleepDuration = 100;
			Boolean executed = null;
			
			for (int retries = 5; retries > 0; retries--) {
				// retry until we get a TRUE back, meaning it actually executed a flush
				executed = (Boolean)KSBServiceLocator.getThreadPool().submit(missRetryWriter).get(); 
				if (executed == null || !executed) {
					// sleep and retry
					try {
						Thread.sleep(sleepDuration); // a medium sized sleep
					} catch (InterruptedException e) {
						// restore the interrupted status
						Thread.currentThread().interrupt();
					}
					sleepDuration *= 2; // back off by doubling sleep duration 
				} else {
					break;
				}
			}
			
			if (executed == null || !executed) {
				LOG.error("failed to flush on miss");
			}
		} catch (ExecutionException e) {
			LOG.error("failed to flush on miss", e);
		} catch (InterruptedException e) {
			LOG.error("failed to flush on miss", e);
			// restore the interrupted status
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * store the person to the database, but do this an alternate thread to 
	 * prevent transaction issues since this service is non-transactional 
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	private class EntityArchiveWriter implements Callable {
		
		// flag used to prevent multiple processes from running at once
		AtomicBoolean currentlyRunning = new AtomicBoolean(false);
		
		public boolean isRunning() {
			return currentlyRunning.get();
		}
		
		/**
		 * Call that tries to flush the write queue.  
		 * @return true if it executes, false if it doesn't (due to another job currently running) 
		 * @see Callable#call()
		 */
		public Object call() {
			Boolean result = Boolean.FALSE;
			// only run one at a time using the AtomicBoolean as a lightweight mutex
			if (currentlyRunning.compareAndSet(false, true)) {
				result = Boolean.TRUE;
				try {
					PlatformTransactionManager transactionManager = KNSServiceLocator.getTransactionManager();
					TransactionTemplate template = new TransactionTemplate(transactionManager);
					template.execute(new TransactionCallback() {
						public Object doInTransaction(TransactionStatus status) {
							KimEntityDefaultInfo entity = null;
							while (null != (entity = writeQueue.poll())) {
								getBusinessObjectService().save( new KimEntityDefaultInfoCacheImpl( entity ) );
							}
							return null;
						}
					});
				} finally { // make sure our running flag is unset, otherwise we'll never run again
					currentlyRunning.compareAndSet(true, false);
				}
			}
			return result;
		}
	}
	
	/**
	 * A class encapsulating a {@link ConcurrentLinkedQueue} and an {@link AtomicInteger} to
	 * provide fast offer(enqueue)/poll(dequeue) and size checking.  Size may be approximate due to concurrent
	 * behavior, but for our purposes that is fine.
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	private static class WriteQueue {
		AtomicInteger writeQueueSize = new AtomicInteger(0);
		ConcurrentLinkedQueue<KimEntityDefaultInfo> queue = new ConcurrentLinkedQueue<KimEntityDefaultInfo>();
		
		public int offerAndGetSize(KimEntityDefaultInfo entity) {
			queue.add(entity);
			return writeQueueSize.incrementAndGet();
		}
		
		private KimEntityDefaultInfo poll() {
			KimEntityDefaultInfo result = queue.poll();
			if (result != null) { writeQueueSize.decrementAndGet(); }
			return result;
		}
		
		private int getSize() {
			return writeQueueSize.get();
		}
	}
	
	/**
	 * decorator for a callable to log a message before it is executed
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	private static class PreLogCallableWrapper<A> implements Callable<A>, edu.emory.mathcs.backport.java.util.concurrent.Callable {
		
		private final Callable inner;
		private final Level level;
		private final String message;
		
		public PreLogCallableWrapper(Callable inner, Level level, String message) {
			this.inner = inner;
			this.level = level;
			this.message = message;
		}
		
		/**
		 * logs the message then calls the inner Callable
		 * 
		 * @see edu.emory.mathcs.backport.java.util.concurrent.Callable#call()
		 */
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
	private static class CallableAdapter implements Runnable {  

		private final Callable callable;
		
		public CallableAdapter(Callable callable) {
			this.callable = callable;
		}  
		
		public void run() {
			try {  
				callable.call();  
			} catch (Exception e) {  
				throw new RuntimeException(e);  
			}  
		}
	}
}
