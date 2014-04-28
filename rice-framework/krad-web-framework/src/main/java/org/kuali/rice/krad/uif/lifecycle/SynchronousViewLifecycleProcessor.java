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

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.freemarker.LifecycleRenderingContext;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.view.DefaultExpressionEvaluator;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.ExpressionEvaluatorFactory;
import org.springframework.util.StringUtils;

/**
 * Single-threaded view lifecycle processor implementation.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SynchronousViewLifecycleProcessor extends ViewLifecycleProcessorBase {
    private static final Logger LOG = Logger.getLogger(SynchronousViewLifecycleProcessor.class);

    // pending lifecycle phases.
    private final Deque<ViewLifecyclePhase> pendingPhases = new LinkedList<ViewLifecyclePhase>();

    // the phase currently active on this lifecycle.
    private ViewLifecyclePhase activePhase;

    // the rendering context.
    private LifecycleRenderingContext renderingContext;

    // the expression evaluator to use with this lifecycle.
    private final ExpressionEvaluator expressionEvaluator;

    private static String getTracePath(ViewLifecyclePhase phase) {
        Component parent = phase.getParent();
        if (parent == null) {
            return "";
        } else {
            return phase.getParent().getViewPath();
        }
    }
    
    private static final class TraceNode {
        private final String path;
        private StringBuilder buffer = new StringBuilder();
        private Set<String> childPaths = new LinkedHashSet<String>();

        private TraceNode(ViewLifecyclePhase phase) {
            path = getTracePath(phase);
        }
        
        private void startTrace(ViewLifecyclePhase phase) {
            try {
                LifecycleElement element = phase.getElement();
                
                String parentPath = phase.getParentPath();
                if (StringUtils.hasLength(parentPath)) {
                    childPaths.add(phase.getParentPath());
                }
                
                buffer.append('\n');
                for (int i = 0; i < phase.getDepth(); i++) {
                    buffer.append("  ");
                }
                buffer.append(phase.getViewPath());
                buffer.append(' ');
                buffer.append(phase.getEndViewStatus());
                buffer.append(' ');
                buffer.append(element.getViewStatus());
                buffer.append(' ');
                buffer.append(element.isRender());
                buffer.append(' ');
                buffer.append(element.getClass().getSimpleName());
                buffer.append(' ');
                buffer.append(element.getId());
                buffer.append(' ');
            } catch (Throwable e) {
                LOG.warn("Error tracing lifecycle", e);
            }
        }
        
        private void finishTrace(long phaseStartTime, Throwable error) {
            if (error == null) {
                buffer.append(" done ");
            } else {
                buffer.append(" ERROR ");
            }
            buffer.append(ProcessLogger.intervalToString(System.currentTimeMillis() - phaseStartTime));
        }
    }

    private final Map<String, TraceNode> trace = ViewLifecycle.isTrace() ? new HashMap<String, TraceNode>() : null;
    
    private TraceNode getTraceNode(ViewLifecyclePhase phase) {
        if (trace == null) {
            return null;
        }
        
        String tracePath = getTracePath(phase);
        TraceNode traceNode = trace.get(tracePath);
        
        if (traceNode == null) {
            traceNode = new TraceNode(phase);
            trace.put(tracePath, traceNode);
        }
        
        return traceNode;
    }

    /**
     * Creates a new synchronous processor for a lifecycle.
     * 
     * @param lifecycle The lifecycle to process.
     */
    public SynchronousViewLifecycleProcessor(ViewLifecycle lifecycle) {
        super(lifecycle);

        // The null conditions noted here should not happen in full configured environments
        // Conditional fallback support is in place primary for unit testing.
        ExpressionEvaluatorFactory expressionEvaluatorFactory;
        if (lifecycle.helper == null) {
            LOG.warn("No helper is defined for the view lifecycle, using global expression evaluation factory");
            expressionEvaluatorFactory = KRADServiceLocatorWeb.getExpressionEvaluatorFactory();
        } else {
            expressionEvaluatorFactory = lifecycle.helper.getExpressionEvaluatorFactory();
        }

        if (expressionEvaluatorFactory == null) {
            LOG.warn("No global expression evaluation factory is defined, using DefaultExpressionEvaluator");
            expressionEvaluator = new DefaultExpressionEvaluator();
        } else {
            expressionEvaluator = expressionEvaluatorFactory.createExpressionEvaluator();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void offerPendingPhase(ViewLifecyclePhase pendingPhase) {
        pendingPhases.offer(pendingPhase);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushPendingPhase(ViewLifecyclePhase phase) {
        pendingPhases.push(phase);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performPhase(ViewLifecyclePhase initialPhase) {
        long startTime = System.currentTimeMillis();
        TraceNode initialNode = getTraceNode(initialPhase);

        offerPendingPhase(initialPhase);
        while (!pendingPhases.isEmpty()) {
            ViewLifecyclePhase pendingPhase = pendingPhases.poll();
            long phaseStartTime = System.currentTimeMillis();

            try {
                if (trace != null) {
                    getTraceNode(pendingPhase).startTrace(pendingPhase);
                }

                pendingPhase.run();

                if (trace != null) {
                    getTraceNode(pendingPhase).finishTrace(phaseStartTime, null);
                }

            } catch (Throwable e) {
                if (trace != null) {
                    getTraceNode(pendingPhase).finishTrace(phaseStartTime, e);
                }

                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else if (e instanceof Error) {
                    throw (Error) e;
                } else {
                    throw new IllegalStateException(e);
                }
            }
        }

        if (trace != null) {
            assert initialNode != null : initialPhase;
            Deque<TraceNode> msgQueue = new LinkedList<TraceNode>();
            StringBuilder msg = new StringBuilder();

            msgQueue.push(initialNode);
            while (!msgQueue.isEmpty()) {
                TraceNode traceNode = msgQueue.pop();
                assert traceNode != null : msg + " " + trace.keySet();
                assert traceNode.buffer != null : traceNode.path;

                msg.append(traceNode.buffer);

                for (String childPath : traceNode.childPaths) {
                    TraceNode child = trace.get(traceNode.path + (traceNode.path.equals("") ? "" : ".") + childPath);
                    if (child != null) {
                        msgQueue.push(child);
                    }
                }
            }

            LOG.info("Lifecycle phase processing completed in "
                    + ProcessLogger.intervalToString(System.currentTimeMillis() - startTime) + msg);
            trace.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewLifecyclePhase getActivePhase() {
        return activePhase;
    }

    /**
     * {@inheritDoc}
     */
    public LifecycleRenderingContext getRenderingContext() {
        if (renderingContext == null && ViewLifecycle.isRenderInLifecycle()) {
            ViewLifecycle lifecycle = getLifecycle();
            this.renderingContext = new LifecycleRenderingContext(lifecycle.model, lifecycle.request);
        }

        return this.renderingContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExpressionEvaluator getExpressionEvaluator() {
        return this.expressionEvaluator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setActivePhase(ViewLifecyclePhase phase) {
        if (activePhase != null && phase != null) {
            throw new IllegalStateException("Another phase is already active on this lifecycle thread " + activePhase);
        }

        activePhase = phase;
    }

}
