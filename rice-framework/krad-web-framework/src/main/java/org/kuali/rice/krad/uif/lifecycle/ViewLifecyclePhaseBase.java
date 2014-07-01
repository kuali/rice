/**
 * Copyright 2005-2014 The Kuali Foundation
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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
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

    private LifecycleElement element;
    private Component parent;
    private String viewPath;
    private String path;
    private int depth;
    
    private List<String> refreshPaths;

    private ViewLifecyclePhase predecessor;
    private ViewLifecyclePhase nextPhase;

    private boolean processed;
    private boolean completed;

    private Set<String> pendingSuccessors = new LinkedHashSet<String>();

    private ViewLifecycleTask<?> currentTask;
    
    private List<ViewLifecycleTask<?>> tasks;
    private List<ViewLifecycleTask<?>> skipLifecycleTasks;
    
    /**
     * Resets this phase for recycling.
     */
    public void recycle() {
        trace("recycle");
        element = null;
        path = null;
        viewPath = null;
        depth = 0;
        predecessor = null;
        nextPhase = null;
        processed = false;
        completed = false;
        refreshPaths = null;
        pendingSuccessors.clear();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void prepareView() {
      assert element == null : "already populated " + element;
      element = ViewLifecycle.getView();
             if (element.getViewStatus().equals(getEndViewStatus())) {
                 ViewLifecycle.reportIllegalState(
                  "View is already in the expected end status " + getEndViewStatus() + " before this phase " +
                                 element.getClass() + " " + element.getId());
             }
     
      this.path = "";
      this.viewPath = "";
      this.parent = null;
      afterPrepare();
      trace("prepare-view");
  }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareElement(LifecycleElement element, Component parent, String parentPath) {
        this.path = parentPath;

        String parentViewPath = parent == null ? null : parent.getViewPath();
        if (StringUtils.isEmpty(parentViewPath)) {
            this.viewPath = path;
        } else {
            this.viewPath = parentViewPath + '.' + path;
        }

        this.element = CopyUtils.unwrap((LifecycleElement) element);
        this.parent = parent;
        afterPrepare();
        trace("prepare-element");
    }

    /**
     * Override to continue preparing the phase for processing after setting the element and parent.
     */
    protected void afterPrepare() {
    }

    /**
     * Executes the lifecycle phase.
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
            ViewLifecycleProcessorBase processor = (ViewLifecycleProcessorBase) ViewLifecycle.getProcessor();

            validateBeforeProcessing();

            boolean skipLifecycle = shouldSkipLifecycle();

            String ntracePrefix = null;
            String ntraceSuffix = null;
            try {
                if (ViewLifecycle.isTrace() && ProcessLogger.isTraceActive()) {
                    ntracePrefix = "lc-" + getStartViewStatus() + "-" + getEndViewStatus() + ":";
                    ntraceSuffix = ":" + getElement().getClass().getSimpleName() + (getElement().isRender()?":render":":no-render");

                    ProcessLogger.ntrace(ntracePrefix, ntraceSuffix, 1000);
                    ProcessLogger.countBegin(ntracePrefix + ntraceSuffix);
                }

                String viewStatus = element.getViewStatus();
                if (viewStatus != null &&
                        !viewStatus.equals(getStartViewStatus())) {
                    trace("dup " + getStartViewStatus() + " " + getEndViewStatus() + " " + viewStatus);
                }

                processor.setActivePhase(this);

                trace("path-update " + element.getViewPath());
                
                element.setViewPath(getViewPath());
                element.getPhasePathMapping().put(getViewPhase(), getViewPath());

                List<ViewLifecycleTask<?>> pendingTasks = skipLifecycle ? skipLifecycleTasks : tasks;

                StringBuilder trace;
                if (ViewLifecycle.isTrace() && LOG.isDebugEnabled()) {
                    trace = new StringBuilder("Tasks");
                } else {
                    trace = null;
                }

                for (ViewLifecycleTask<?> task : pendingTasks) {
                    if (trace != null) {
                        trace.append("\n  ").append(task);
                    }

                    if (!task.getElementType().isInstance(element)) {
                        if (trace != null) {
                            trace.append(" skip");
                        }
                        continue;
                    }

                    if (trace != null) {
                        trace.append(" run");
                    }
                    currentTask = task;
                    task.run();
                    currentTask = null;
                }

                if (trace != null) {
                    LOG.debug(trace.toString());
                }

                element.setViewStatus(getEndViewStatus());
                processed = true;

            } finally {
                processor.setActivePhase(null);

                if (ViewLifecycle.isTrace() && ProcessLogger.isTraceActive()) {
                    ProcessLogger.countEnd(ntracePrefix + ntraceSuffix, 
                            getElement().getClass() + " " + getElement().getId());
                }
            }

            if (skipLifecycle) {
                notifyCompleted();
            } else {
                assert pendingSuccessors.isEmpty() : pendingSuccessors;

                Queue<ViewLifecyclePhase> successors = new LinkedList<ViewLifecyclePhase>();

                initializeSuccessors(successors);
                processSuccessors(successors);
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
     * Indicates whether the lifecycle should be skipped for the current component.
     *
     * <p>Elements are always processed in the pre process phase, or in the case of the element or one
     * of its childs being refreshed. If these conditions are false, the element method
     * {@link org.kuali.rice.krad.uif.util.LifecycleElement#skipLifecycle()} is invoked to determine if
     * the lifecycle can be skipped.</p>
     *
     * @return boolean true if the lifecycle should be skipped, false if not
     * @see org.kuali.rice.krad.uif.util.LifecycleElement#skipLifecycle()
     */
    protected boolean shouldSkipLifecycle() {
        // we always want to run the preprocess phase so ids are assigned
        boolean isPreProcessPhase = getViewPhase().equals(UifConstants.ViewPhases.PRE_PROCESS);

        // if the component is being refreshed its lifecycle should not be skipped
        boolean isRefreshComponent = ViewLifecycle.isRefreshComponent(getViewPhase(), getViewPath());

        // if a child of this component is being refresh its lifecycle should not be skipped
        boolean includesRefreshComponent = false;
        if (StringUtils.isNotBlank(ViewLifecycle.getRefreshComponentPhasePath(getViewPhase()))) {
            includesRefreshComponent = ViewLifecycle.getRefreshComponentPhasePath(getViewPhase()).startsWith(getViewPath());
        }

        boolean skipLifecycle = false;
        if (!(isPreProcessPhase || isRefreshComponent || includesRefreshComponent)) {
            // delegate to the component to determine whether skipping lifecycle is ok
            skipLifecycle = element.skipLifecycle();
        }

        return skipLifecycle;
    }

    /**
     * Validates this phase and thread state before processing and logs activity.
     *
     * @see #run()
     */
    protected void validateBeforeProcessing() {
        if (processed) {
            throw new IllegalStateException("Lifecycle phase has already been processed " + this);
        }

        if (predecessor != null && !predecessor.isProcessed()) {
            throw new IllegalStateException("Predecessor phase has not completely processed " + this);
        }

        if (!ViewLifecycle.isActive()) {
            throw new IllegalStateException("No view lifecyle is not active on the current thread");
        }

        if (LOG.isDebugEnabled()) {
            trace("ready " + getStartViewStatus() + " -> " + getEndViewStatus());
        }
    }

    /**
     * Adds phases added as successors to the processor, or if there are no pending successors invokes
     * the complete notification step.
     *
     * @param successors phases to process
     */
    protected void processSuccessors(Queue<ViewLifecyclePhase> successors) {
        for (ViewLifecyclePhase successor : successors) {
            if (!pendingSuccessors.add(successor.getParentPath())) {
                ViewLifecycle.reportIllegalState("Already pending " + successor + "\n" + this);
            }
        }

        trace("processed " + pendingSuccessors);

        if (pendingSuccessors.isEmpty()) {
            notifyCompleted();
        } else {
            for (ViewLifecyclePhase successor : successors) {
                assert successor.getPredecessor() == null : this + " " + successor;
                successor.setPredecessor(this);
                
                if (successor instanceof ViewLifecyclePhaseBase) {
                    ((ViewLifecyclePhaseBase) successor).trace("succ-pend");
                }

                ViewLifecycle.getProcessor().offerPendingPhase(successor);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNextPhase(ViewLifecyclePhase nextPhase) {
        if (this.nextPhase != null) {
            throw new IllegalStateException("Next phase is already set " + nextPhase + "\n" + this);
        }

        if (nextPhase == null || !getEndViewStatus().equals(nextPhase.getStartViewStatus())) {
            throw new IllegalStateException("Next phase is invalid for end phase " + getEndViewStatus() + " found "
                    + nextPhase.getStartViewStatus());
        }

        this.nextPhase = nextPhase;
        trace("next-phase");
    }

    /**
     * Sets the tasks to process at this phase.
     * 
     * @param tasks list of tasks
     */
    public void setTasks(List<ViewLifecycleTask<?>> tasks) {
        for (ViewLifecycleTask<?> task : tasks) {
            assert task.getElementState() == null : task.getElementState() + "\n" + this;
            task.setElementState(this);
        }

        this.tasks = tasks;
    }

    /**
     * Sets the tasks to process at this phase when the lifecycle is skipped.
     * 
     * @param tasks list of tasks
     */
    public void setSkipLifecycleTasks(List<ViewLifecycleTask<?>> skipLifecycleTasks) {
        for (ViewLifecycleTask<?> task : skipLifecycleTasks) {
            assert task.getElementState() == null : task.getElementState() + "\n" + this;
            task.setElementState(this);
        }

        this.skipLifecycleTasks = skipLifecycleTasks;
    }

    /**
     * Initializes queue of successor phases.
     *
     * <p>This method will be called while processing this phase after all tasks have been performed,
     * to determine phases to queue for successor processing. This phase will not be considered
     * complete until all successors queued by this method, and all subsequent successor phases,
     * have completed processing.</p>
     *
     * @param successors The queue of successor phases
     */
    protected void initializeSuccessors(Queue<ViewLifecyclePhase> successors) {
        if (ViewLifecycle.isRefreshLifecycle() && (refreshPaths != null)) {
            String currentPath = getViewPath();

            boolean withinRefreshComponent = currentPath.startsWith(ViewLifecycle.getRefreshComponentPhasePath(
                    getViewPhase()));
            if (withinRefreshComponent) {
                initializeAllLifecycleSuccessors(successors);
            } else if (refreshPaths.contains(currentPath) || StringUtils.isBlank(currentPath)) {
                initializeRefreshPathSuccessors(successors);
            }

            return;
        }

        initializeAllLifecycleSuccessors(successors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRefreshPaths(List<String> refreshPaths) {
        this.refreshPaths = refreshPaths;
    }

    /**
     * Initializes only the lifecycle successors referenced by paths within {@link #getRefreshPaths()}.
     *
     * @param successors the successor queue
     */
    protected void initializeRefreshPathSuccessors(Queue<ViewLifecyclePhase> successors) {
        LifecycleElement element = getElement();

        String nestedPathPrefix;
        Component nestedParent;
        if (element instanceof Component) {
            nestedParent = (Component) element;
            nestedPathPrefix = "";
        } else {
            nestedParent = getParent();
            nestedPathPrefix = getParentPath() + ".";
        }

        List<String> nestedProperties = getNestedPropertiesForRefreshPath();

        for (String nestedProperty : nestedProperties) {
            String nestedPath = nestedPathPrefix + nestedProperty;

            LifecycleElement nestedElement = ObjectPropertyUtils.getPropertyValue(element, nestedProperty);
            if (nestedElement != null) {
                ViewLifecyclePhase nestedPhase = initializeSuccessor(nestedElement, nestedPath, nestedParent);
                successors.add(nestedPhase);
            }
        }
    }

    /**
     * Determines the list of child properties for the current phase component that are in the refresh
     * paths and should be processed next.
     *
     * @return list of property names relative to the component the phase is currently processing
     */
    protected List<String> getNestedPropertiesForRefreshPath() {
        List<String> nestedProperties = new ArrayList<String>();

        String currentPath = getViewPath();
        if (currentPath == null) {
            currentPath = "";
        }

        if (StringUtils.isNotBlank(currentPath)) {
            currentPath += ".";
        }

        // to get the list of children, the refresh path must start with the path of the component being
        // processed. If the child path is nested, we get the top most property first
        for (String refreshPath : refreshPaths) {
            if (!refreshPath.startsWith(currentPath)) {
                continue;
            }

            String nestedProperty = StringUtils.substringAfter(refreshPath, currentPath);

            if (StringUtils.isBlank(nestedProperty)) {
                continue;
            }

            if (StringUtils.contains(nestedProperty, ".")) {
                nestedProperty = StringUtils.substringBefore(nestedProperty, ".");
            }

            if (!nestedProperties.contains(nestedProperty)) {
                nestedProperties.add(nestedProperty);
            }
        }
        
        return nestedProperties;
    }

    /**
     * Initializes all lifecycle phase successors.
     *
     * @param successors The successor queue.
     */
    protected void initializeAllLifecycleSuccessors(Queue<ViewLifecyclePhase> successors) {
        LifecycleElement element = getElement();

        String nestedPathPrefix;
        Component nestedParent;
        if (element instanceof Component) {
            nestedParent = (Component) element;
            nestedPathPrefix = "";
        } else {
            nestedParent = getParent();
            nestedPathPrefix = getParentPath() + ".";
        }

        for (Map.Entry<String, LifecycleElement> nestedElementEntry : ViewLifecycleUtils.getElementsForLifecycle(
                element, getViewPhase()).entrySet()) {
            String nestedPath = nestedPathPrefix + nestedElementEntry.getKey();
            LifecycleElement nestedElement = nestedElementEntry.getValue();

            if (nestedElement != null && !getEndViewStatus().equals(nestedElement.getViewStatus())) {
                ViewLifecyclePhase nestedPhase = initializeSuccessor(nestedElement, nestedPath, nestedParent);
                successors.offer(nestedPhase);
            }
        }
    }

    /**
     * May be overridden in order to check for illegal state based on more concrete assumptions than
     * can be made here.
     * 
     * @throws IllegalStateException If the conditions for completing the lifecycle phase have not been met.
     */
    protected void verifyCompleted() {
    }

    /**
     * Initializes a successor of this phase for a given nested element.
     * 
     * @param nestedElement The lifecycle element.
     * @param nestedPath The path, relative to the parent element.
     * @param nestedParent The parent component of the nested element.
     * @return successor phase
     */
    protected ViewLifecyclePhase initializeSuccessor(LifecycleElement nestedElement, String nestedPath,
            Component nestedParent) {
        ViewLifecyclePhase successorPhase = KRADServiceLocatorWeb.getViewLifecyclePhaseBuilder()
                .buildPhase(getViewPhase(), nestedElement, nestedParent, nestedPath);
        return successorPhase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPendingSuccessors() {
        return !pendingSuccessors.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePendingSuccessor(String parentPath) {
        if (!pendingSuccessors.remove(parentPath)) {
            throw new IllegalStateException("Not a pending successor: " + parentPath);
        }
    }

    /**
     * Notifies predecessors that this task has completed.
     */
    @Override
    public final void notifyCompleted() {
        trace("complete");

        completed = true;

        LifecycleEvent event = getEventToNotify();
        if (event != null) {
            ViewLifecycle.getActiveLifecycle().invokeEventListeners(event, ViewLifecycle.getView(),
                    ViewLifecycle.getModel(), element);
        }

        element.notifyCompleted(this);

        if (nextPhase != null) {
            assert nextPhase.getPredecessor() == null : this + " " + nextPhase;

            // Assign a predecessor to the next phase, to defer notification until
            // after all phases in the chain have completed processing.
            if (predecessor != null) {
                // Common case: "catch up" phase automatically spawned to bring
                // a component up to the right status before phase processing.
                // Swap the next phase in for this phase in the graph.
                nextPhase.setPredecessor(predecessor);
            } else {
                // Initial phase chain:  treat the next phase as a successor so that
                // this phase (and therefore the controlling thread) will be notified
                nextPhase.setPredecessor(this);
                synchronized (pendingSuccessors) {
                    pendingSuccessors.add(nextPhase.getParentPath());
                }
            }

            ViewLifecycle.getProcessor().pushPendingPhase(nextPhase);
            return;
        }

        synchronized (this) {
            if (predecessor != null) {
                synchronized (predecessor) {
                    predecessor.removePendingSuccessor(getParentPath());
                    if (!predecessor.hasPendingSuccessors()) {
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
     * {@inheritDoc}
     */
    @Override
    public final LifecycleElement getElement() {
        return element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Component getParent() {
        return this.parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParentPath() {
        return this.path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewPath() {
        return this.viewPath;
    }

    /**
     * @param viewPath the viewPath to set
     */
    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDepth() {
        return this.depth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isProcessed() {
        return processed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isComplete() {
        return completed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewLifecyclePhase getPredecessor() {
        return predecessor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPredecessor(ViewLifecyclePhase phase) {
        if (this.predecessor != null) {
            throw new IllegalStateException("Predecessor phase is already defined");
        }
        
        this.predecessor = phase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewLifecycleTask<?> getCurrentTask() {
        return this.currentTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Queue<ViewLifecyclePhase> toPrint = new LinkedList<ViewLifecyclePhase>();
        toPrint.offer(this);
        while (!toPrint.isEmpty()) {
            ViewLifecyclePhase tp = toPrint.poll();

            if (tp.getElement() == null) {
                sb.append("\n      ");
                sb.append(tp.getClass().getSimpleName());
                sb.append(" (recycled)");
                continue;
            }

            String indent;
            if (tp == this) {
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
            sb.append(tp.getViewPath());
            sb.append(" ");
            sb.append(tp.getElement().getClass().getSimpleName());
            sb.append(" ");
            sb.append(tp.getElement().getId());
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

    /**
     * Logs a trace message related to processing this lifecycle, when tracing is active and
     * debugging is enabled.
     *
     * @param step The step in processing the phase that has been reached.
     * @see ViewLifecycle#isTrace()
     */
    protected void trace(String step) {
        if (ViewLifecycle.isTrace() && LOG.isDebugEnabled()) {
            String msg = System.identityHashCode(this) + " " + getClass() + " " + step + " " + path + " " +
                    (element == null ? "(recycled)" : element.getClass() + " " + element.getId());
            LOG.debug(msg);
        }
    }

}
