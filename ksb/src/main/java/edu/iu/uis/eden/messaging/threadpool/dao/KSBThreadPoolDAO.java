package edu.iu.uis.eden.messaging.threadpool.dao;

import edu.iu.uis.eden.messaging.threadpool.KSBThreadPool;

public interface KSBThreadPoolDAO {

	/**
	 * Gets the pool size for the thread pool.
	 * 
	 * @return the pool size of the thread pool or -1 if pool size cannot be determined
	 */
	public int getPoolSize();

	/**
	 * Gets the initial delay (in milliseconds) for the process which fetches message from the queue.
	 * 
	 * @return the number of milliseconds of delay or -1 if the delay cannot be determined
	 */
	public long getInitialFetchDelay();
	
	/**
	 * Gets the freqency (in milliseconds) at which the message fetcher will run to fetch new
	 * messages from the queue for processing.
	 *  
	 * @return the frequency in milliseconds to run the message fetcher
	 */
	public long getFetchFrequency();
	
	public boolean isThreadPoolOn();
	
	/**
	 * Saves the persistent data on the given KSBThreadPool.
	 */
	public void save(KSBThreadPool pool);
	

	
}
