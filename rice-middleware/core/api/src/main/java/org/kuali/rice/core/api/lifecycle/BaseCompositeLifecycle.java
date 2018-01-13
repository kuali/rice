/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.core.api.lifecycle;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseCompositeLifecycle extends BaseLifecycle {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaseCompositeLifecycle.class);

	private List<Lifecycle> lifecycles;

	protected abstract List<Lifecycle> loadLifecycles() throws Exception;

	@Override
	public void start() throws Exception {
	    this.lifecycles = loadLifecycles();
		for (Lifecycle lifecycle : this.lifecycles) {
			lifecycle.start();
		}
		super.start();
	}

	@Override
	public void stop() throws Exception {
		for (Lifecycle lifecycle : reverseLifecycles()) {
			try {
				lifecycle.stop();
			} catch (Throwable t) {
				LOG.error("Failed to stop Lifecycle: " + lifecycle.getClass().getName(), t);
			}
		}
		super.stop();
	}

	private List<Lifecycle> reverseLifecycles() {
		LinkedList<Lifecycle> reversed = new LinkedList<Lifecycle>();
		for (Lifecycle lifecycle : this.lifecycles) {
			reversed.addFirst(lifecycle);
		}
		return reversed;
	}

}
