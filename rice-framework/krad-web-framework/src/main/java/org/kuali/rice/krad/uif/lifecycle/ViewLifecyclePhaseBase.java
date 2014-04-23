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
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.lifecycle.initialize.AssignIdsTask;
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
    private Object model;
    private Component parent;
    private String viewPath;
    private String path;
    private int depth;

    private List<String> refreshPaths;

    private ViewLifecyclePhaseBase predecessor;
    private ViewLifecyclePhaseBase nextPhase;

    private boolean processed;
    private boolean completed;

    private Set<String> pendingSuccessors = new LinkedHashSet<String>();

    private ViewLifecycleTask<?> currentTask;

    /**
     * Resets this phase for recycling.
     */
    protected void recycle() {
        trace("recycle");
        element = null;
        model = null;
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
     * Prepares this phase for reuse.
     *
     * @param element The element to be processed by this phase
     * @param model The model associated with the lifecycle at this phase
     * @param path Path to the component relative to the active view
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @param parent The parent element. For top-down phases, this component will be associated
     * with the predecessor phase. For bottom-up phases (rendering), this element will be
     * associated with a successor phases
     * @param nextPhase The lifecycle phase to queue directly upon completion of this phase, if
     * applicable
     * @see LifecyclePhaseFactory
     */
    protected void prepare(LifecycleElement element, Object model, String path, List<String> refreshPaths,
            Component parent, ViewLifecyclePhaseBase nextPhase) {
        if (element.getViewStatus().equals(getEndViewStatus())) {
            ViewLifecycle.reportIllegalState(
                    "Component is already in the expected end status " + getEndViewStatus() + " before this phase " +
                            element.getClass() + " " + element.getId());
        }

        this.model = model;
        this.path = path;

        String parentViewPath = parent == null ? null : parent.getViewPath();
        if (StringUtils.isEmpty(parentViewPath)) {
            this.viewPath = path;
        } else {
            this.viewPath = parentViewPath + "." + path;
        }

        this.element = (LifecycleElement) element.unwrap();
        this.refreshPaths = refreshPaths;
        this.parent = parent;
        this.nextPhase = nextPhase;

        trace("prepare");
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
                    // TODO: Consider a warning here instead of the "dup" trace, or short-circuit to
                    // prevent duplicate processing of components.  Either way, this is not an illegal
                    // state in that the lifecycle can most likely complete without further issue
//                    ViewLifecycle.reportIllegalState("Component is not in the expected status " + getStartViewStatus() +
//                            " at the start of this phase, found " + element.getClass() + " " + element.getId() + " " +
//                            viewStatus + "\nThis phase: " + this);
                }

                processor.setActivePhase(this);

                trace("path-update " + element.getViewPath());
                
                // TODO: this cannot be enforced currently due to help tooltip getting pushed to header
//                if (ViewLifecycle.isStrict()) {
//                    if (element == view) {
//                        if (!StringUtils.isEmpty(viewPath)) {
//                            ViewLifecycle.reportIllegalState("View path is not empty " + viewPath);
//                        }
//                    } else {
//                        LifecycleElement referredElement = ObjectPropertyUtils.getPropertyValue(view, viewPath);
//                        if (referredElement != null) {
//                            referredElement = (LifecycleElement) referredElement.unwrap();
//                            if (element != referredElement) {
//                                ViewLifecycle.reportIllegalState(
//                                        "View path " + viewPath + " refers to an element other than " +
//                                                element.getClass() + " " + element.getId() + " " +
//                                                element.getViewPath() + (referredElement == null ? "" :
//                                                " " + referredElement.getClass() + " " + referredElement.getId() + " " +
//                                                        referredElement.getViewPath()));
//                            }
//                        }
//                    }
//                }

                element.setViewPath(getViewPath());
                element.getPhasePathMapping().put(getViewPhase(), getViewPath());

                // if skipping lifecycle we need to make sure the element has an id
                if (skipLifecycle) {
                    if (StringUtils.isBlank(element.getId())) {
                        String elementId = AssignIdsTask.generateId(element, ViewLifecycle.getView());
                        element.setId(elementId);
                    }
                } else {
                    Queue<ViewLifecycleTask<?>> pendingTasks = new LinkedList<ViewLifecycleTask<?>>();
                    initializePendingTasks(pendingTasks);

                    while (!pendingTasks.isEmpty()) {
                        ViewLifecycleTask<?> task = pendingTasks.poll();

                        currentTask = task;
                        task.run();
                        currentTask = null;
                    }
                }

                element.setViewStatus(this);
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
                if (successor instanceof ViewLifecyclePhaseBase) {
                    ViewLifecyclePhaseBase successorBase = (ViewLifecyclePhaseBase) successor;
                    assert successorBase.predecessor == null : this + " " + successorBase;

                    successorBase.predecessor = this;
                    successorBase.depth = this.depth + 1;
                    successorBase.trace("succ-pend");
                }

                ViewLifecycle.getProcessor().offerPendingPhase(successor);
            }
        }
    }

    /**
     * Initializes queue of pending tasks phases.
     *
     * <p>This method will be called before during processing to determine which tasks to perform at
     * this phase.</p>
     *
     * @param tasks The queue of tasks to perform.
     */
    protected abstract void initializePendingTasks(Queue<ViewLifecycleTask<?>> tasks);

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
     * Initializes a successor of this phase for a given nested element.
     *
     * @param nestedElement The lifecycle element.
     * @param nestedPath The path, relative to the parent element.
     * @param nestedParent The parent component of the nested element.
     * @return successor phase
     */
    protected abstract ViewLifecyclePhase initializeSuccessor(LifecycleElement nestedElement, String nestedPath,
            Component nestedParent);

    /**
     * May be overridden in order to check for illegal state based on more concrete assumptions than
     * can be made here.
     * 
     * @throws IllegalStateException If the conditions for completing the lifecycle phase have not been met.
     */
    protected void verifyCompleted() {
    }

    /**
     * Notifies predecessors that this task has completed.
     */
    protected final void notifyCompleted() {
        trace("complete");

        completed = true;

        LifecycleEvent event = getEventToNotify();
        if (event != null) {
            ViewLifecycle.getActiveLifecycle().invokeEventListeners(event, ViewLifecycle.getView(),
                    ViewLifecycle.getModel(), element);
        }

        element.notifyCompleted(this);

        if (nextPhase != null) {
            assert nextPhase.predecessor == null : this + " " + nextPhase;

            // Assign a predecessor to the next phase, to defer notification until
            // after all phases in the chain have completed processing.
            if (predecessor != null) {
                // Common case: "catch up" phase automatically spawned to bring
                // a component up to the right status before phase processing.
                // Swap the next phase in for this phase in the graph.
                nextPhase.predecessor = predecessor;
                nextPhase.depth = predecessor.depth + 1;
            } else {
                // Initial phase chain:  treat the next phase as a successor so that
                // this phase (and therefore the controlling thread) will be notified
                nextPhase.predecessor = this;
                nextPhase.depth = this.depth + 1;
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
                    predecessor.pendingSuccessors.remove(getParentPath());
                    if (predecessor.pendingSuccessors.isEmpty()) {
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
    public final Object getModel() {
        return model;
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
    public List<String> getRefreshPaths() {
        return refreshPaths;
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
                if (this.model != null) {
                    sb.append("Model: ");
                    sb.append(this.model.getClass().getSimpleName());
                }
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
