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

import java.util.LinkedList;
import java.util.Queue;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base abstract implementation for a lifecycle phase.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ViewLifecyclePhaseBase implements ViewLifecyclePhase {

    private final Logger LOG = LoggerFactory.getLogger(ViewLifecyclePhaseBase.class);

    private Component component;
    private Object model;
    private Component parent;
    private int index = -1;
    private ViewLifecyclePhaseBase predecessor;

    private ViewLifecyclePhase nextPhase;
    
    private boolean processed;
    private boolean completed;
  
    private int pendingSuccessors = -1;

    /**
     * Reset this phase for recycling.
     */
    protected void recycle() {
        trace("recycle");
        model = null;
        index = -1;
        component = null;
        predecessor = null;
        nextPhase = null;
        processed = false;
        completed = false;
        pendingSuccessors = -1;
    }

    /**
     * Prepare this phase for reuse.
     * 
     * @param component The component to be processed by this phase.
     * @param model The model associated with the lifecycle at this phase.
     * @param index The position within the predecessor phase's successor list of this phase.
     * @param parent The parent component. For top-down phases, this component will be associated
     *        with the predecssor phase. For bottom-up phases (rendering), this component will be
     *        associated with a successor phases.
     * @param nextPhase The lifecycle phase to queue directly upon completion of this phase, if
     *        applicible.
     *        
     * @see LifecyclePhaseFactory
     */
    protected void prepare(Component component, Object model, int index,
            Component parent, ViewLifecyclePhase nextPhase) {
        if (component.getViewStatus().equals(getEndViewStatus())) {
            ViewLifecycle.reportIllegalState(
                    "Component is already in the expected end status " + getEndViewStatus()
                            + " before this phase " + component.getClass() + " " + component.getId());
        }

        this.model = model;
        this.index = index;
        this.component = component;
        this.parent = parent;
        this.nextPhase = nextPhase;

        trace("prepare");
    }

    /**
     * Initialize queue of pending tasks phases.
     * 
     * <p>
     * This method will be called before during processing to determine which tasks to perform at
     * this phase.
     * </p>
     * 
     * @param tasks The queue of tasks to perform.
     */
    protected abstract void initializePendingTasks(Queue<ViewLifecycleTask> tasks);

    /**
     * Initialize queue of successor phases.
     * 
     * <p>
     * This method will be called while processing this phase after all tasks have been performed,
     * to determine phases to queue for successor processing. This phase will not be considered
     * complete until all successors queued by this method, and all subsequent successor phases,
     * have completed processing.
     * </p>
     * 
     * @param successors The queue of successor phases.
     */
    protected abstract void initializeSuccessors(Queue<ViewLifecyclePhase> successors);

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getComponent()
     */
    @Override
    public final Component getComponent() {
        return component;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getModel()
     */
    @Override
    public final Object getModel() {
        return model;
    }

    /**
     * @return the parent
     */
    @Override
    public final Component getParent() {
        return this.parent;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getIndex()
     */
    @Override
    public final int getIndex() {
        return index;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#isProcessed()
     */
    @Override
    public final boolean isProcessed() {
        return processed;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#isComplete()
     */
    @Override
    public final boolean isComplete() {
        return completed;
    }

    /**
     * @see ViewLifecyclePhase#getPredecessor()
     */
    @Override
    public ViewLifecyclePhase getPredecessor() {
        return predecessor;
    }

    /**
     * Validate this phase before processing and log activity.
     * @see #run()
     */
    private void validateBeforeProcessing() {
        if (processed) {
            throw new IllegalStateException(
                    "Lifecycle phase has already been processed " + this);
        }

        if (predecessor != null && !predecessor.isProcessed()) {
            throw new IllegalStateException(
                    "Predecessor phase has not completely processed " + this);
        }

        if (!ViewLifecycle.isActive()) {
            throw new IllegalStateException("No view lifecyle is not active on the current thread");
        }

        if (LOG.isDebugEnabled()) {
            trace("ready " + getStartViewStatus() + " -> " + getEndViewStatus());
        }
    }

    /**
     * Execute the lifecycle phase.
     * 
     * <p>
     * This method performs state validation and updates component view status. Use
     * {@link #initializePendingTasks(Queue)} to provide phase-specific behavior.
     * </p>
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public final void run() {
        try {
            ViewLifecycleProcessorBase processor =
                    (ViewLifecycleProcessorBase) ViewLifecycle.getProcessor();

            validateBeforeProcessing();

            try {
                if (ViewLifecycle.isTrace() && ProcessLogger.isTraceActive()) {
                    ProcessLogger.ntrace("lc-" + getStartViewStatus() + "-" + getEndViewStatus() + ":", ":"
                            + getComponent().getClass().getSimpleName(), 1000);
                    ProcessLogger.countBegin("lc-" + getStartViewStatus() + "-" + getEndViewStatus());
                }

                processor.setActivePhase(this);

                if (!component.getViewStatus().equals(getStartViewStatus())) {
                    ViewLifecycle.reportIllegalState(
                            "Component is not in the expected status " + getStartViewStatus()
                                    + " at the start of this phase, found " + component.getClass()
                                    + " " + component.getId() + " " + component.getViewStatus() +
                                    "\nThis phase: " + this);
                }

                Queue<ViewLifecycleTask> pendingTasks = new LinkedList<ViewLifecycleTask>();
                initializePendingTasks(pendingTasks);

                while (!pendingTasks.isEmpty()) {
                    ViewLifecycleTask task = pendingTasks.poll();
                    task.run();
                }

                component.setViewStatus(this);
                processed = true;

            } finally {
                processor.setActivePhase(null);

                if (ViewLifecycle.isTrace() && ProcessLogger.isTraceActive()) {
                    ProcessLogger.countEnd("lc-" + getStartViewStatus() + "-" + getEndViewStatus(), getComponent()
                            .getClass() + " " + getComponent().getId());
                }
            }

            assert pendingSuccessors == -1 : this;

            Queue<ViewLifecyclePhase> successors = new LinkedList<ViewLifecyclePhase>();
            initializeSuccessors(successors);
            pendingSuccessors = successors.size();
            trace("processed " + pendingSuccessors);

            if (pendingSuccessors == 0) {
                notifyCompleted();
            } else {
                for (ViewLifecyclePhase successor : successors) {
                    if (successor instanceof ViewLifecyclePhaseBase) {
                        ViewLifecyclePhaseBase successorBase = (ViewLifecyclePhaseBase) successor;
                        assert successorBase.predecessor == null : this + " " + successorBase;
                        successorBase.predecessor = this;
                        successorBase.trace("succ-pend");
                    }

                    ViewLifecycle.getProcessor().offerPendingPhase(successor);
                }
            }

        } catch (Throwable t) {
            trace("error");
            LOG.warn("Error in lifecycle phase " + this, t);

            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new IllegalStateException("Unexpected error in lifecycle phase " + this, t);
            }
        }
    }

    /**
     * Notify predecessors that this task has completed.
     */
    protected final void notifyCompleted() {
        trace("complete");
        assert !completed : this;
        completed = true;
        
        LifecycleEvent event = getEventToNotify();
        if (event != null) {
            ViewLifecycle.getActiveLifecycle().invokeEventListeners(
                    event, ViewLifecycle.getView(), ViewLifecycle.getModel(), component);
        }

        doNotifyCompleted();

        if (nextPhase != null) {
            ViewLifecycle.getProcessor().offerPendingPhase(nextPhase);
        }
        
        synchronized (this) {
            if (predecessor != null) {
                synchronized (predecessor) {
                    predecessor.pendingSuccessors--;
                    if (predecessor.pendingSuccessors == 0) {
                        predecessor.notifyCompleted();
                    }
                    LifecyclePhaseFactory.recycle(this);
                }
            } else {
                trace("notify");
                notifyAll();
            }
        }
    }

    /**
     * Override for additional handling when all tasks and successor tasks have been completed for
     * this phase.
     */
    protected void doNotifyCompleted() {
        component.notifyCompleted(this);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Queue<ViewLifecyclePhase> toPrint = new LinkedList<ViewLifecyclePhase>();
        toPrint.offer(this);
        while (!toPrint.isEmpty()) {
            ViewLifecyclePhase tp = toPrint.poll();

            if (tp.getModel() == null || tp.getComponent() == null) {
                sb.append("\n      ");
                sb.append(tp.getClass().getSimpleName());
                sb.append(" (recycled)");
                continue;
            }

            String indent;
            if (tp == this) {
                sb.append("Model: ");
                sb.append(this.model.getClass().getSimpleName());
                sb.append("\nProcessed? ");
                sb.append(processed);
                indent = "\n";
            } else {
                indent = "\n    ";
            }
            sb.append(indent);

            sb.append(tp.getClass().getSimpleName());
            sb.append(" ");
            sb.append(System.identityHashCode(tp));
            sb.append(" ");
            sb.append(tp.getComponent().getClass().getSimpleName());
            sb.append(" ");
            sb.append(tp.getComponent().getId());
            sb.append(" ");
            sb.append(pendingSuccessors);

            if (tp == this) {
                sb.append("\nPredecessor Phases:");
            }

            ViewLifecyclePhase tpredecessor = tp.getPredecessor();
            if (tpredecessor != null) {
                toPrint.add(tpredecessor);
            }
        }
        return sb.toString();
    }

    private void trace(String step) {
        if (LOG.isDebugEnabled()) {
            String msg = System.identityHashCode(this) + " " + getClass() + " " + step + " " +
                    (component == null ? "(recycled)" : component.getClass() + " " + component.getId());
            LOG.debug(msg);
        }
    }

}
