package org.kuali.rice.lifecycle;

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
