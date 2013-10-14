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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
public abstract class AbstractViewLifecyclePhase implements ViewLifecyclePhase {

    private final Logger LOG = LoggerFactory.getLogger(AbstractViewLifecyclePhase.class);

    private Component component;
    private Object model;
    private List<? extends ViewLifecyclePhase> predecessors;

    private final Queue<ViewLifecycleTask> pendingTasks;
    private final List<? extends ViewLifecycleTask> unmodifiablePendingTasks;
    
    private final Queue<ViewLifecyclePhase> successors;
    private final List<? extends ViewLifecyclePhase> unmodifiableSuccessors;

    private ViewLifecycleTask activeTask;
    private boolean processed;

    /**
     * Default constructor.
     */
    public AbstractViewLifecyclePhase() {
        LinkedList<ViewLifecycleTask> pendingTaskList = new LinkedList<ViewLifecycleTask>();
        this.pendingTasks = pendingTaskList;
        this.unmodifiablePendingTasks = Collections.unmodifiableList(pendingTaskList);

        LinkedList<ViewLifecyclePhase> successorList = new LinkedList<ViewLifecyclePhase>();
        this.successors = successorList;
        this.unmodifiableSuccessors = Collections.unmodifiableList(successorList);
    }
    
    /**
     * Reset this phase for recycling.
     */
    protected void recycle() {
        if (component != null && component.getLastPhase() == this) {
            component.clearLastPhase();
        }
        
        model = null;
        component = null;
        predecessors = null;
        pendingTasks.clear();
        successors.clear();
        activeTask = null;
        processed = false;
    }
    
    /**
     * Prepare this phase for reuse.
     */
    protected void prepare(Component component, Object model, List<? extends ViewLifecyclePhase> predecessors) {
        if (component.getViewStatus().equals(getEndViewStatus())) {
            ViewLifecycle.reportIllegalState(
                    "Component is already in the expected end status " + getEndViewStatus()
                            + " before this phase " + component.getClass() + " " + component.getId() + "\nLast phase: "
                            + component.getLastPhase());
        }

        this.model = model;
        this.component = component;
        this.predecessors = Collections.unmodifiableList(new ArrayList<ViewLifecyclePhase>(predecessors));
    }

    /**
     * Initialize queue of pending tasks phases.
     * 
     * <p>
     * This method will be called before {@link #performLifecyclePhase()} while processing this
     * phase.
     * </p>
     * 
     * @param tasks The queue of tasks to perform.
     */
    protected abstract void initializePendingTasks(Queue<ViewLifecycleTask> tasks);

    /**
     * Initialize queue of successor phases.
     * 
     * <p>
     * This method will be called after {@link #performLifecyclePhase()} while processing this
     * phase.
     * </p>
     * 
     * @param tasks The queue of successor phases.
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
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getActiveTask()
     */
    @Override
    public final ViewLifecycleTask getActiveTask() {
        return activeTask;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getPendingTasks()
     */
    @Override
    public final List<? extends ViewLifecycleTask> getPendingTasks() {
        return unmodifiablePendingTasks;
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
    public boolean isComplete() {
        if (!processed) {
            return false;
        }

        for (ViewLifecyclePhase successor : successors) {
            if (!successor.isComplete()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getPredecessors()
     */
    @Override
    public List<? extends ViewLifecyclePhase> getPredecessors() {
        return predecessors;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#addTask(org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTask)
     */
    @Override
    public void addTask(ViewLifecycleTask task) {
        if (task.getPhase() != null) {
            throw new IllegalArgumentException(task + " is not intended for this phase " + this + ", found "
                    + task.getPhase());
        }

        if (processed) {
            throw new IllegalStateException("Cannot add a task to a phase after processing " + this);
        }

        pendingTasks.offer(task);
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getSuccessors()
     */
    @Override
    public final List<? extends ViewLifecyclePhase> getSuccessors() {
        return unmodifiableSuccessors;
    }

    /**
     * Validate this phase before processing and log activity.
     * @see #run()
     */
    private void validateBeforeProcessing() {
        if (processed) {
            throw new IllegalStateException("Lifecycle phase has already been processed");
        }

        for (ViewLifecyclePhase predecessor : getPredecessors()) {
            if (!predecessor.isProcessed()) {
                throw new IllegalStateException("Predecessor phase has not completely processed");
            }
        }

        if (!ViewLifecycle.isLifecycleActive()) {
            throw new IllegalStateException("No view lifecyle is not active on the current thread");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(getComponent().getClass() + " " + getComponent().getId() + " " +
                    getStartViewStatus() + " -> " + getEndViewStatus());
        }
    }
    
    /**
     * Execute the lifecycle phase.
     * 
     * <p>
     * This method performs state validation and updates component view status. Override
     * {@link #performLifecyclePhase()} to provide phase-specific behavior.
     * </p>
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public final void run() {
        try {
            ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
            
            validateBeforeProcessing();
            
            try {
                if (ProcessLogger.isTraceActive()) {
                    ProcessLogger.ntrace("lc-" + getStartViewStatus() + "-" + getEndViewStatus() + ":", ":"
                            + getComponent().getClass().getSimpleName(), 1000);
                    ProcessLogger.countBegin("lc-" + getStartViewStatus() + "-" + getEndViewStatus());
                }

                viewLifecycle.setActivePhase(this);

                if (!component.getViewStatus().equals(getStartViewStatus())) {
                    ViewLifecycle.reportIllegalState(
                            "Component is not in the expected status " + getStartViewStatus()
                                    + " at the start of this phase, found " + component.getClass() + " "
                                    + component.getId() + " " + component.getViewStatus() +
                                    "\nLast phase: " + component.getLastPhase() + "\nThis phase: " + this);
                }
                
                initializePendingTasks(pendingTasks);

                while (!pendingTasks.isEmpty()) {
                    ViewLifecycleTask task = pendingTasks.poll();
                    activeTask = task;
                    task.run();
                    activeTask = null;
                }

                component.setViewStatus(this);
                processed = true;

                initializeSuccessors(successors);

            } finally {
                viewLifecycle.setActivePhase(null);

                if (ProcessLogger.isTraceActive()) {
                    ProcessLogger.countEnd("lc-" + getStartViewStatus() + "-" + getEndViewStatus(), getComponent()
                            .getClass() + " " + getComponent().getId());
                }
            }

            if (successors.isEmpty()) {
                notifyPredecessors();
            }
            
        } catch (Throwable t) {
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
    private void notifyPredecessors() {
        Iterator<ViewLifecyclePhase> successIterator = successors.iterator();
        
        while (successIterator.hasNext()) {
            ViewLifecyclePhase successor = successIterator.next();

            if (successor.isComplete()) {
                successIterator.remove();
            }
        }
        
        if (!successors.isEmpty() || !isComplete()) {
            return;
        }

        List<? extends ViewLifecyclePhase> predecessors = getPredecessors();
        if (predecessors == null) {
            throw new IllegalStateException();
        }
        
        for (ViewLifecyclePhase predecessor : predecessors) {
            if (predecessor instanceof AbstractViewLifecyclePhase) {
                ((AbstractViewLifecyclePhase) predecessor).notifyPredecessors();
            }
        }
        
        LifecycleEvent event = getEventToNotify();
        if (event != null) {
            ViewLifecycle.getActiveLifecycle().invokeEventListeners(event, ViewLifecycle.getView(), successIterator,
                    component);
        }
        
        notifyCompleted();
        
        synchronized (this) {
            this.notifyAll();
        }

        if (isReadyToRecycle()) {
            LifecyclePhaseFactory.recycle(this);
        }
    }

    /**
     * Override for additional handling when all tasks and successor tasks have been completed for
     * this phase.
     */
    protected void notifyCompleted() {}
    
    /**
     * Determine if this phase was defined internally, has completed all tasks, and is no longer
     * referenced by another phase.
     * 
     * @return
     */
    protected boolean isReadyToRecycle() {
        return successors.isEmpty() && !getPredecessors().isEmpty();
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
            sb.append(tp.getComponent().getClass().getSimpleName());
            sb.append(" ");
            sb.append(tp.getComponent().getId());

            for (ViewLifecycleTask task : tp.getPendingTasks()) {
                sb.append(indent);
                sb.append("  ");
                sb.append(task);
            }
            
            if (tp.getActiveTask() != null) {
                sb.append(indent);
                sb.append("  ");
                sb.append(tp.getActiveTask());
                sb.append(" (active)");
            }

            if (tp == this) {
                sb.append("\nSuccessor Phases:");
                for (ViewLifecyclePhase sp : successors) {
                    sb.append("\n    ");
                    sb.append(sp.getClass());
                    sb.append(" ");
                    
                    if (sp.getComponent() == null) {
                        sb.append(" (recycled)");
                        continue;
                    }
                    
                    sb.append(sp.getComponent().getClass().getSimpleName());
                    sb.append(" ");
                    sb.append(sp.getComponent().getId());
                }
                sb.append("\nPredecessor Phases:");
            }

            toPrint.addAll(tp.getPredecessors());
        }
        return sb.toString();
    }

}
