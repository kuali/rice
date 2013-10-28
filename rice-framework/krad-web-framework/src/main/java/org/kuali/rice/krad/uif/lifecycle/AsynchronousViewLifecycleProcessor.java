/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.lifecycle;

import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.freemarker.LifecycleRenderingContext;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.util.RecycleUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Static utility class for handling executor configuration and spreading {@link ViewLifecycle}
 * across multiple threads.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class AsynchronousViewLifecycleProcessor extends ViewLifecycleProcessorBase {

    private static final Logger LOG = Logger.getLogger(AsynchronousViewLifecycleProcessor.class);

    private static final ThreadFactory LIFECYCLE_THREAD_FACTORY = new LifecycleThreadFactory();

    private static final ThreadPoolExecutor LIFECYCLE_EXECUTOR = new ThreadPoolExecutor(
            getMinThreads(), getMaxThreads(), getTimeout(), TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<Runnable>(), LIFECYCLE_THREAD_FACTORY);

    private static final Deque<AsynchronousLifecyclePhase> PENDING_PHASE_QUEUE =
            new LinkedList<AsynchronousLifecyclePhase>();

    private static final ThreadLocal<AsynchronousLifecyclePhase> ACTIVE_PHASE =
            new ThreadLocal<AsynchronousLifecyclePhase>();
    
    private static final Map<Component, AsynchronousLifecyclePhase> BUSY_COMPONENTS =
            new IdentityHashMap<Component, AsynchronousLifecyclePhase>();

    private static Integer minThreads;
    private static Integer maxThreads;
    private static Long timeout;

    private final Queue<LifecycleRenderingContext> renderingContextPool =
            ViewLifecycle.isRenderInLifecycle() ? new ConcurrentLinkedQueue<LifecycleRenderingContext>() : null;

    private Throwable error;

    /**
     * Get the minimum number of lifecycle worker threads to maintain.
     * 
     * <p>
     * This value is controlled by the configuration parameter
     * &quot;krad.uif.lifecycle.asynchronous.minThreads&quot;.
     * </p>
     * 
     * @return The minimum number of worker threads to maintain.
     */
    public static int getMinThreads() {
        if (minThreads == null) {
            String propStr = ConfigContext.getCurrentContextConfig().getProperty(
                    KRADConstants.ConfigParameters.KRAD_VIEW_LIFECYCLE_MINTHREADS);
            minThreads = propStr == null ? 4 : Integer.parseInt(propStr);
        }

        return minThreads;
    }

    /**
     * Get the maximum number of lifecycle worker threads to maintain.
     * 
     * <p>
     * This value is controlled by the configuration parameter
     * &quot;krad.uif.lifecycle.asynchronous.maxThreads&quot;.
     * </p>
     * 
     * @return The maximum number of worker threads to maintain.
     */
    public static int getMaxThreads() {
        if (maxThreads == null) {
            String propStr = ConfigContext.getCurrentContextConfig().getProperty(
                    KRADConstants.ConfigParameters.KRAD_VIEW_LIFECYCLE_MAXTHREADS);
            maxThreads = propStr == null ? 48 : Integer.parseInt(propStr);
        }

        return maxThreads;
    }

    /**
     * Get the time, in milliseconds, to wait for a initial phase to process.
     * 
     * <p>
     * This value is controlled by the configuration parameter
     * &quot;krad.uif.lifecycle.asynchronous.timeout&quot;.
     * </p>
     * 
     * @return The time, in milliseconds, to wait for the initial phase to process.
     */
    public static long getTimeout() {
        if (timeout == null) {
            String propStr = ConfigContext.getCurrentContextConfig().getProperty(
                    KRADConstants.ConfigParameters.KRAD_VIEW_LIFECYCLE_TIMEOUT);
            timeout = propStr == null ? 30000 : Long.parseLong(propStr);
        }

        return timeout;
    }

    /**
     * Constructor.
     * 
     * @param lifecycle The lifecycle to process.
     */
    AsynchronousViewLifecycleProcessor(ViewLifecycle lifecycle) {
        super(lifecycle);
    }

    /**
     * Thread factory for lifecycle processing.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private static class LifecycleThreadFactory implements ThreadFactory {

        private static final ThreadGroup GROUP = new ThreadGroup("krad-lifecycle-group");

        private int sequenceNumber = 0;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(GROUP, r, "krad-lifecycle("
                    + Integer.toString(++sequenceNumber) + ")");
        }
    }

    /**
     * @see ViewLifecycleProcessor#getActivePhase()
     */
    @Override
    public ViewLifecyclePhase getActivePhase() {
        AsynchronousLifecyclePhase aphase = ACTIVE_PHASE.get();

        if (aphase == null) {
            throw new IllegalStateException("No phase worker is active on this thread");
        }

        ViewLifecyclePhase phase = aphase.phase;
        if (phase == null) {
            throw new IllegalStateException("No lifecycle phase is active on this thread");
        }

        return phase;
    }

    /**
     * @see ViewLifecycleProcessorBase#setActivePhase(ViewLifecyclePhase)
     */
    @Override
    void setActivePhase(ViewLifecyclePhase phase) {
        AsynchronousLifecyclePhase aphase = ACTIVE_PHASE.get();

        if (aphase == null) {
            throw new IllegalStateException("No phase worker is active on this thread");
        }
        
        if (phase == null) {
            // Ingore null setting, asychronous state is controlled by aphase.
            return;
        }

        if (aphase.phase != phase) {
            throw new IllegalStateException(
                    "Another lifecycle phase is already active on this thread "
                            + aphase.phase + ", setting " + phase);
        }

        aphase.phase = phase;
    };

    /**
     * @see ViewLifecycleProcessor#getRenderingContext()
     */
    @Override
    public LifecycleRenderingContext getRenderingContext() {
        if (!ViewLifecycle.isRenderInLifecycle()) {
            return null;
        }

        AsynchronousLifecyclePhase aphase = ACTIVE_PHASE.get();

        if (aphase == null) {
            throw new IllegalStateException("No phase worker is active on this thread");
        }

        LifecycleRenderingContext renderContext = aphase.renderingContext;
        if (renderContext != null) {
            return renderContext;
        }

        renderContext = renderingContextPool.poll();
        if (renderContext == null) {
            ViewLifecycle lifecycle = getLifecycle();
            renderContext = new LifecycleRenderingContext(
                    lifecycle.model, lifecycle.request, lifecycle.response);
        }

        List<String> viewTemplates = ViewLifecycle.getView().getViewTemplates();
        synchronized (viewTemplates) {
            for (String viewTemplate : viewTemplates) {
                renderContext.importTemplate(viewTemplate);
            }
        }
        
        aphase.renderingContext = renderContext;
        return renderContext;
    }

    /**
     * @see ViewLifecycleProcessor#pushPendingPhase(ViewLifecyclePhase)
     */
    @Override
    public void pushPendingPhase(ViewLifecyclePhase phase) {
        AsynchronousLifecyclePhase aphase = getAsynchronousPhase(phase);
        if (phase.getStartViewStatus().equals(phase.getComponent().getViewStatus())) {
            synchronized (BUSY_COMPONENTS) {
                BUSY_COMPONENTS.put(phase.getComponent(), aphase);
            }
        }

        synchronized (PENDING_PHASE_QUEUE) {
            PENDING_PHASE_QUEUE.push(aphase);
            PENDING_PHASE_QUEUE.notify();
        }

        spawnWorkers();
    }

    /**
     * @see ViewLifecycleProcessor#offerPendingPhase(ViewLifecyclePhase)
     */
    @Override
    public void offerPendingPhase(ViewLifecyclePhase phase) {
        AsynchronousLifecyclePhase aphase = getAsynchronousPhase(phase);
        if (phase.getStartViewStatus().equals(phase.getComponent().getViewStatus())) {
            synchronized (BUSY_COMPONENTS) {
                BUSY_COMPONENTS.put(phase.getComponent(), aphase);
            }
        }

        synchronized (PENDING_PHASE_QUEUE) {
            PENDING_PHASE_QUEUE.offer(aphase);
            PENDING_PHASE_QUEUE.notify();
        }

        spawnWorkers();
    }

    /**
     * This method should only be called a single time by the controlling thread in order to wait
     * for all pending phases to be performed, and should not be called by any worker threads.
     * 
     * @see ViewLifecycleProcessor#performPhase(ViewLifecyclePhase)
     */
    @Override
    public void performPhase(ViewLifecyclePhase initialPhase) {
        if (error != null) {
            throw new RiceRuntimeException("Error performing view lifecycle", error);
        }

        long now = System.currentTimeMillis();
        try {
            AsynchronousLifecyclePhase aphase = getAsynchronousPhase(initialPhase);
            aphase.initial = true;

            synchronized (PENDING_PHASE_QUEUE) {
                PENDING_PHASE_QUEUE.offer(aphase);
                PENDING_PHASE_QUEUE.notify();
            }

            spawnWorkers();

            while (System.currentTimeMillis() - now < getTimeout() &&
                    error == null && !initialPhase.isComplete()) {
                synchronized (initialPhase) {
                    // Double-check lock
                    if (!initialPhase.isComplete()) {
                        LOG.info("Waiting for view lifecycle " + initialPhase);
                        initialPhase.wait(Math.min(5000L, getTimeout()));
                    }
                }
            }

            if (error != null) {
                throw new IllegalStateException("Error in lifecycle", error);
            }

            if (!initialPhase.isComplete()) {
                error = new IllegalStateException("Time out waiting for lifecycle");
                throw (IllegalStateException) error; 
            }

        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted waiting for view lifecycle", e);
        }
    }

    /**
     * Get a new context wrapper for processing a lifecycle phase using the same lifecycle and
     * thread context as the current thread.
     * 
     * @param phase The lifecycle phase.
     * @return A context wrapper for processing the phase.
     */
    private AsynchronousLifecyclePhase getAsynchronousPhase(ViewLifecyclePhase phase) {
        AsynchronousLifecyclePhase rv = RecycleUtils.getRecycledInstance(AsynchronousLifecyclePhase.class);
        if (rv == null) {
            rv = new AsynchronousLifecyclePhase();
        }

        rv.processor = this;
        rv.globalVariables = GlobalVariables.getCurrentGlobalVariables();
        rv.phase = phase;

        return rv;
    }

    /**
     * Recycle a phase context after processing.
     * 
     * @param aphase The phase context, previously acquire using by
     *        {@link #getAsynchronousPhase(ViewLifecyclePhase)}.
     */
    private static void recyclePhase(AsynchronousLifecyclePhase aphase) {
        if (aphase.initial) {
            return;
        }

        assert aphase.renderingContext == null;
        aphase.processor = null;
        aphase.phase = null;
        aphase.globalVariables = null;
        RecycleUtils.recycle(aphase);
    }

    /**
     * Spawn new worker threads, if needed.
     */
    private static void spawnWorkers() {
        int active = LIFECYCLE_EXECUTOR.getActiveCount();
        if (active < LIFECYCLE_EXECUTOR.getCorePoolSize() ||
                (active * 16 < PENDING_PHASE_QUEUE.size() &&
                active < LIFECYCLE_EXECUTOR.getMaximumPoolSize())) {
            LIFECYCLE_EXECUTOR.submit(new AsynchronousLifecycleWorker());
        }
    }

    /**
     * Private context wrapper for forwarding lifecycle state to worker threads.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private static class AsynchronousLifecyclePhase {
        private boolean initial;
        private GlobalVariables globalVariables;
        private AsynchronousViewLifecycleProcessor processor;
        private ViewLifecyclePhase phase;
        private LifecycleRenderingContext renderingContext;
    }

    /**
     * Encapsulates lifecycle phase worker activity.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private static class PhaseWorkerCall implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            while (!PENDING_PHASE_QUEUE.isEmpty()) {
                AsynchronousLifecyclePhase aphase;
                synchronized (PENDING_PHASE_QUEUE) {
                    aphase = PENDING_PHASE_QUEUE.poll();
                }
                
                if (aphase == null) {
                    continue;
                }

                AsynchronousViewLifecycleProcessor processor = aphase.processor;
                ViewLifecyclePhase phase = aphase.phase;

                if (processor.error != null) {
                    synchronized (phase) {
                        phase.notifyAll();
                    }

                    continue;
                }

                Component comp = phase.getComponent();
                AsynchronousLifecyclePhase busyPhase = BUSY_COMPONENTS.get(comp);
                if (busyPhase != null && busyPhase != aphase) {
                    // Another phase is already active on this component, requeue
                    synchronized (PENDING_PHASE_QUEUE) {
                        PENDING_PHASE_QUEUE.offer(aphase);
                    }
                    
                    continue;
                }

                try {
                    assert ACTIVE_PHASE.get() == null;
                    ACTIVE_PHASE.set(aphase);
                    ViewLifecycle.setProcessor(aphase.processor);
                    GlobalVariables.injectGlobalVariables(aphase.globalVariables);

                    phase.run();

                } catch (Throwable t) {
                    processor.error = t;

                    ViewLifecyclePhase topPhase = phase;
                    while (topPhase.getPredecessor() != null) {
                        topPhase = topPhase.getPredecessor();
                    }
                    
                    synchronized (topPhase) {
                        topPhase.notifyAll();
                    }
                } finally {
                    ACTIVE_PHASE.remove();
                    LifecycleRenderingContext renderingContext = aphase.renderingContext;
                    aphase.renderingContext = null;
                    if (renderingContext != null && aphase.processor != null) {
                        aphase.processor.renderingContextPool.offer(renderingContext);
                    }

                    synchronized (BUSY_COMPONENTS) {
                        BUSY_COMPONENTS.remove(comp);
                    }
                    GlobalVariables.popGlobalVariables();
                    ViewLifecycle.setProcessor(null);
                }

                recyclePhase(aphase);
            }
            return null;
        }

    }

    /**
     * Worker process to submit to the executor. Wraps {@link PhaseWorkerCall} in a process logger
     * for tracing activity.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private static class AsynchronousLifecycleWorker implements Runnable {

        @Override
        public void run() {
            try {
                PhaseWorkerCall call = new PhaseWorkerCall();
                do {
                    if (PENDING_PHASE_QUEUE.isEmpty()) {
                        synchronized (PENDING_PHASE_QUEUE) {
                            PENDING_PHASE_QUEUE.wait(15000L);
                        }
                    } else if (ViewLifecycle.isTrace()) {
                        ProcessLogger.follow(
                                "view-lifecycle", "KRAD lifecycle worker", call);
                    } else {
                        call.call();
                    }
                } while (LIFECYCLE_EXECUTOR.getActiveCount() <= getMinThreads());
            } catch (Throwable t) {
                LOG.fatal("Fatal error in View Lifecycle worker", t);
            }
        }

    }

}
