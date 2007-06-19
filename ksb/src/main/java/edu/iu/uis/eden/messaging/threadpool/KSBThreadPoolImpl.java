package edu.iu.uis.eden.messaging.threadpool;

import org.apache.log4j.Logger;
import org.kuali.rice.core.Core;

import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledFuture;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.iu.uis.eden.messaging.MessageFetcher;
import edu.iu.uis.eden.messaging.threadpool.dao.KSBThreadPoolDAO;

/**
 * A Thread Pool implementation for the KSB which implements a thread pool
 * backed by a configuration store.
 * 
 * @author Ryan Kirkendall
 * @author Eric Westfall
 */
public class KSBThreadPoolImpl extends ScheduledThreadPoolExecutor implements KSBThreadPool {

	private static final Logger LOG = Logger.getLogger(KSBThreadPoolImpl.class);

	public static final int DEFAULT_POOL_SIZE = 5;

	public static final long DEFAULT_FETCH_INITIAL_DELAY = 10 * 1000;

	public static final long DEFAULT_FETCH_FREQUENCY = 10 * 1000;

	private KSBThreadPoolDAO dao;

	private long initialFetchDelay = -1;

	private long fetchFrequency = -1;

	private ScheduledFuture messageFetcher;

	private boolean started = false;

	private boolean poolSizeSet = false;

	private boolean threadPoolOn = true;

	public KSBThreadPoolImpl() {
		super(DEFAULT_POOL_SIZE, new KSBThreadFactory());
	}

	public void setCorePoolSize(int corePoolSize) {
		LOG.info("Setting core pool size to " + corePoolSize + " threads.");
		super.setCorePoolSize(corePoolSize);
		saveIfPossible();
		this.poolSizeSet = true;
	}

	public void setFetchFrequency(long fetchFrequency) {
		LOG.info("Setting message fetch frequency to " + fetchFrequency + " milliseconds.");
		this.fetchFrequency = fetchFrequency;
		saveIfPossible();
		if (this.messageFetcher != null) {
			rescheduleMessageFetcher();
		}
	}

	public long getInitialFetchDelay() {
		return this.initialFetchDelay;
	}

	public void setInitialFetchDelay(long initialFetchDelay) {
		LOG.info("Setting message initial fetch delay to " + initialFetchDelay + " milliseconds.");
		this.initialFetchDelay = initialFetchDelay;
		saveIfPossible();
	}

	public long getFetchFrequency() {
		return this.fetchFrequency;
	}

	public boolean isStarted() {
		return this.started;
	}

	public void start() throws Exception {
		if (!Core.getCurrentContextConfig().getDevMode()) {
			loadSettings();
			if (isThreadPoolOn()) {
				scheduleMessageFetcher();
			}
			this.started = true;
		} else {
			this.shutdown();
		}
	}

	public void stop() throws Exception {
		if (isStarted()) {
//			saveIfPossible();
//			cancelMessageFetcher();
//			purge();
//			LOG.info("Shutting down all threads...");
//			shutdown();
//			if (!awaitTermination(10, TimeUnit.SECONDS)) {
//				LOG.warn("Failed to shutdown the thread pool within the alloted time of 10 seconds!");
//			}
			this.shutdownNow();
			this.messageFetcher = null;
			this.started = false;
		}
	}

	/**
	 * Turns the thread pool on or off. Setting this property will turn the pool
	 * on or off and persist the setting until it is changed again. This means
	 * that the setting will survive a reboot of the server (provided that
	 * behind-the-scenes data stores are configured properly).
	 * 
	 * <p>
	 * In addition to flipping this flag and persisting it, this method will
	 * also invoke the appropriate Lifecycle method (start or stop) depending on
	 * the value of the boolean which is set.
	 */
	public void setThreadPoolOn(boolean threadPoolOn) throws Exception {
		this.threadPoolOn = threadPoolOn;
		if (!threadPoolOn) {
			cancelMessageFetcher();
		} else if (threadPoolOn) {
			scheduleMessageFetcher();
		}
		saveIfPossible();
	}

	public boolean isThreadPoolOn() {
		return this.threadPoolOn;
	}

	/**
	 * Loads the thread pool settings from the DAO.
	 */
	protected void loadSettings() {
		if (!this.poolSizeSet) {
			int poolSize = this.dao.getPoolSize();
			if (poolSize == -1) {
				poolSize = DEFAULT_POOL_SIZE;
			}
			setCorePoolSize(poolSize);
		}
		if (getInitialFetchDelay() == -1) {
			long initialFetchDelay = this.dao.getInitialFetchDelay();
			if (initialFetchDelay == -1) {
				initialFetchDelay = DEFAULT_FETCH_INITIAL_DELAY;
			}
			setInitialFetchDelay(initialFetchDelay);
		}
		if (getFetchFrequency() == -1) {
			long fetchFrequency = this.dao.getFetchFrequency();
			if (fetchFrequency == -1) {
				fetchFrequency = DEFAULT_FETCH_FREQUENCY;
			}
			setFetchFrequency(fetchFrequency);
		}
		this.threadPoolOn = this.dao.isThreadPoolOn();
	}

	/**
	 * Attempts to persist the configuration state of the thread pool, catching
	 * and logging any errors if they occur. Will only execute the save if the
	 * thread pool is started.
	 */
	protected void saveIfPossible() {
		if (isStarted()) {
			try {
			    this.dao.save(this);
			} catch (Exception e) {
				LOG.error("Failed to persist the Thread Pool settings, continuing with shutdown...", e);
			}
		}
	}

	/**
	 * Schedules the message fetcher to run for this thread pool.
	 */
	protected void scheduleMessageFetcher() {
		LOG.info("Scheduling message fetcher with initial delay of " + getInitialFetchDelay() + " and frequency of " + getFetchFrequency());
		this.messageFetcher = scheduleWithFixedDelay(new MessageFetcher(), getInitialFetchDelay(), getFetchFrequency(), TimeUnit.MILLISECONDS);
	}

	protected void cancelMessageFetcher() {
		LOG.info("Cancelling message fetcher.");
		if (this.messageFetcher != null) {
			if (!this.messageFetcher.isCancelled()) {
				boolean cancelled = this.messageFetcher.cancel(true);
				if (!cancelled) {
					LOG.warn("Failed to cancel the message fetcher task.");
				}
			}
		}
	}

	/**
	 * Reschedules the message fetcher if possible.
	 */
	protected void rescheduleMessageFetcher() {
		LOG.info("Thread pool properties updated, rescheduling message fetcher");
		cancelMessageFetcher();
		if (isThreadPoolOn()) {
			scheduleMessageFetcher();
		}
	}

	public void setDao(KSBThreadPoolDAO dao) {
		this.dao = dao;
	}

	/**
	 * A simple ThreadFactory which names the thread as follows:<br>
	 * <br>
	 * 
	 * <i>messageEntity</i>/KSB-pool-<i>m</i>-thread-<i>n</i><br>
	 * <br>
	 * 
	 * Where <i>messageEntity</i> is the message entity of the application
	 * running the thread pool, <i>m</i> is the sequence number of the factory
	 * and <i>n</i> is the sequence number of the thread within the factory.
	 * 
	 * @author Eric Westfall
	 */
	private static class KSBThreadFactory implements ThreadFactory {

		private static int factorySequence = 0;

		private static int threadSequence = 0;

		private ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

		public KSBThreadFactory() {
			factorySequence++;
		}

		public Thread newThread(Runnable runnable) {
			threadSequence++;
			Thread thread = this.defaultThreadFactory.newThread(runnable);
			thread.setName(Core.getCurrentContextConfig().getMessageEntity() + "/KSB-pool-" + factorySequence + "-thread-" + threadSequence);
			return thread;
		}

	}
}