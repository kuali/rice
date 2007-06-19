package edu.iu.uis.eden.messaging.threadpool.dao;

import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigDAOSupport;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.messaging.threadpool.KSBThreadPool;

/**
 * An implementation of the KSBThreadPoolDAO which pulls it's data from the configuration system.
 *
 * @author Eric Westfall
 * 
 * Commented code out because it was causing weird issues.  Talk to Ryan before reimplementing this code.
 * 
 */
public class KSBThreadPoolDAOConfigImpl extends ConfigDAOSupport implements KSBThreadPoolDAO {

	public int getPoolSize() {
		String poolSize = Core.getCurrentContextConfig().getProperty(Config.THREAD_POOL_SIZE);//getIntProperty(Config.THREAD_POOL_SIZE);
		if (poolSize == null) {
			return new Integer(-1);
		}
		return new Integer(poolSize);
	}
	
	public long getFetchFrequency() {
		String fetchFrequency = Core.getCurrentContextConfig().getProperty(Config.THREAD_POOL_FETCH_FREQUENCY);//getLongProperty(Config.THREAD_POOL_FETCH_FREQUENCY);
		if (fetchFrequency == null) {
			return new Long(-1);
		}
		return new Long(fetchFrequency);
	}

	public long getInitialFetchDelay() {
		String initialFetchDelay = Core.getCurrentContextConfig().getProperty(Config.THREAD_POOL_INITIAL_FETCH_DELAY);//getLongProperty(Config.THREAD_POOL_INITIAL_FETCH_DELAY);
		if (initialFetchDelay == null) {
			return new Long(-1);
		}
		return new Long(initialFetchDelay);
	}
	
	public boolean isThreadPoolOn() {
		String on = Core.getCurrentContextConfig().getProperty(Config.THREAD_POOL_ON);
		if (on == null) {
			return true;
		}
		return new Boolean(on);
	}

	public void save(KSBThreadPool pool) {
//		getNodeSettings().setSetting(Config.THREAD_POOL_SIZE, String.valueOf(pool.getCorePoolSize()));
//		getNodeSettings().setSetting(Config.THREAD_POOL_INITIAL_FETCH_DELAY, String.valueOf(pool.getInitialFetchDelay()));
//		getNodeSettings().setSetting(Config.THREAD_POOL_FETCH_FREQUENCY, String.valueOf(pool.getFetchFrequency()));
//		getNodeSettings().setSetting(Config.THREAD_POOL_ON, String.valueOf(pool.isThreadPoolOn()));
	}

}
