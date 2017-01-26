/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging.threadpool;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.kuali.rice.core.api.lifecycle.Lifecycle;

/**
 * A thread pool which can be used to schedule asynchronous tasks.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
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

