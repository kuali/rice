/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.messaging.threadpool;

import org.apache.log4j.Logger;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.util.ClassLoaderUtils;

import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.PriorityBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 * A Thread Pool implementation for the KSB which implements a thread pool backed by a configuration store.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KSBThreadPoolImpl extends ThreadPoolExecutor implements KSBThreadPool {

    private static final Logger LOG = Logger.getLogger(KSBThreadPoolImpl.class);

    public static final int DEFAULT_POOL_SIZE = 5;

    private boolean started;
    private boolean poolSizeSet;

    public KSBThreadPoolImpl() {
	super(DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE, 60, TimeUnit.SECONDS, new PriorityBlockingQueue(1, new PriorityBlockingQueuePersistedMessageComparator()),  new KSBThreadFactory(ClassLoaderUtils.getDefaultClassLoader()), new ThreadPoolExecutor.AbortPolicy());
    }

    public void setCorePoolSize(int corePoolSize) {
	LOG.info("Setting core pool size to " + corePoolSize + " threads.");
	super.setCorePoolSize(corePoolSize);
	this.poolSizeSet = true;
    }

    public long getKeepAliveTime() {
	return super.getKeepAliveTime(TimeUnit.MILLISECONDS);
    }

    public boolean isStarted() {
	return this.started;
    }

    public void start() throws Exception {

    }

    public void stop() throws Exception {
	LOG.info("Shutting down KSB threadpool.");
	if (isStarted()) {
	    this.shutdownNow();
	    this.started = false;
	}
    }

    /**
         * Loads the thread pool settings from the DAO.
         */
    protected void loadSettings() {
	Core.getCurrentContextConfig().getProperty(Config.THREAD_POOL_SIZE);

	if (!this.poolSizeSet) {
	    int poolSize;
	    try {
		poolSize = new Integer(Core.getCurrentContextConfig().getProperty(Config.THREAD_POOL_SIZE));
	    } catch (NumberFormatException nfe) {
		poolSize = -1;
	    }
	    if (poolSize == -1) {
		poolSize = DEFAULT_POOL_SIZE;
	    }
	    setCorePoolSize(poolSize);
	}

    }

    public Object getInstance() {
	return this;
    }

    /**
         * A simple ThreadFactory which names the thread as follows:<br>
         * <br>
         *
         * <i>messageEntity</i>/KSB-pool-<i>m</i>-thread-<i>n</i><br>
         * <br>
         *
         * Where <i>messageEntity</i> is the message entity of the application running the thread pool, <i>m</i> is the
         * sequence number of the factory and <i>n</i> is the sequence number of the thread within the factory.
         *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
         */
    private static class KSBThreadFactory implements ThreadFactory {

	private static int factorySequence = 0;

	private static int threadSequence = 0;

	private ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

	private ClassLoader contextClassLoader;

	public KSBThreadFactory(ClassLoader contextClassLoader) {
	    this.contextClassLoader = contextClassLoader;
	    factorySequence++;
	}

	public Thread newThread(Runnable runnable) {
	    threadSequence++;
	    Thread thread = this.defaultThreadFactory.newThread(runnable);
	    // if the thread ends up getting spawned by an action inside of a workflow plugin or something along those lines, it will inherit the plugin's
	    // classloader as it's ContextClassLoader.  Let's make sure it's set to the same ClassLoader that loaded the KSBConfigurer
	    thread.setContextClassLoader(contextClassLoader);
	    thread.setName(Core.getCurrentContextConfig().getMessageEntity() + "/KSB-pool-" + factorySequence + "-thread-"
		    + threadSequence);
	    return thread;
	}

    }
}