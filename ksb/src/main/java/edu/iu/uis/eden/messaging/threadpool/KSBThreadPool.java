/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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


import org.kuali.rice.lifecycle.Lifecycle;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;

/**
 * A thread pool which can be used to schedule asynchronous tasks.
 *
 * @author rkirkend
 */
public interface KSBThreadPool extends ExecutorService, Lifecycle {

	public boolean remove(Runnable task);

	public int getActiveCount();

	public void setCorePoolSize(int corePoolSize);

	public int getCorePoolSize();
	
	public int getMaximumPoolSize();

	public void setMaximumPoolSize(int maxPoolSize);

	public int getPoolSize();

	public int getLargestPoolSize();

	public long getKeepAliveTime();

	public long getTaskCount();

	public long getCompletedTaskCount();

	public BlockingQueue getQueue();

	public Object getInstance();
}

