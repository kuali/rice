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

import java.util.Queue;
import java.util.Set;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.RecycleUtils;

/**
 * Lifecycle phase processing task for rendering a component.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see ViewLifecycle#isRenderInLifecycle()
 */
public class RenderComponentPhase extends ViewLifecyclePhaseBase {

    private RenderComponentPhase renderParent;
    private Set<String> pendingChildren;

    /**
     * {@inheritDoc}
     */
    @Override
    public void recycle() {
        super.recycle();
        renderParent = null;
        pendingChildren = null;
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     *
     * @param renderParent rendering phase to queue as a successor when all children have processed
     * @param pendingChildren set of paths to child rendering phases to expect to be queued for
     * processing before this phase
     */
    void prepareRenderPhase(RenderComponentPhase renderParent, Set<String> pendingChildren) {
        this.renderParent = renderParent;
        this.pendingChildren = pendingChildren;
    }

    /**
     * {@inheritDoc}
     *
     * @return UifConstants.ViewPhases.RENDER
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.RENDER;
    }

    /**
     * {@inheritDoc}
     *
     * @return UifConstants.ViewStatus.FINAL
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.FINAL;
    }

    /**
     * {@inheritDoc}
     *
     * @return UifConstants.ViewStatus.RENDERED
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.RENDERED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return null;
    }

    /**
     * Verify that the all pending children have completed.
     */
    @Override
    protected void verifyCompleted() {
        if (pendingChildren != null) {
            ViewLifecycle.reportIllegalState("Render phase is not complete, children are still pending "
                    + pendingChildren + "\n" + this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeSuccessors(Queue<ViewLifecyclePhase> successors) {
        if (renderParent == null || renderParent.pendingChildren == null) {
            trace(renderParent == null ? "no-parent" : "no-children");
            return;
        }

        synchronized (renderParent) {
            // InitializeSuccessors is invoked right after processing.
            // Once the last sibling is processed, then queue the parent phase as a successor.
            if (!renderParent.pendingChildren.remove(getParentPath())) {
                ViewLifecycle.reportIllegalState("Render phase isn't a pending child\n"
                        + this + "\nRender Parent: " + renderParent);
            }

            trace("remove-child " + renderParent.getElement().getId() + " " +
                    renderParent.getViewPath() + " " + getParentPath() + " "
                    + renderParent.pendingChildren);

            if (renderParent.pendingChildren.isEmpty()) {
                successors.add(renderParent);
                renderParent.trace("pend-rend");
                
                Set<String> toRecycle = renderParent.pendingChildren;
                renderParent.pendingChildren = null;
                RecycleUtils.recycle(toRecycle);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewLifecyclePhase initializeSuccessor(LifecycleElement nestedElement, String nestedPath,
            Component parent) {
        return null;
    }

}
