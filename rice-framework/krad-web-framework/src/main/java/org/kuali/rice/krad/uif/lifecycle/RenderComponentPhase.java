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

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.freemarker.RenderComponentTask;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;

/**
 * Lifecycle phase processing task for rendering a component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see ViewLifecycle#isRenderInLifecycle()
 */
public class RenderComponentPhase extends ViewLifecyclePhaseBase {

    private RenderComponentPhase renderParent;
    private int pendingChildren;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void recycle() {
        super.recycle();
        renderParent = null;
        pendingChildren = -1;
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     * @param index The position of the assocaited finalize phase within it's predecessor's
     *        successor queue.
     * @param parent The parent component.
     * @param pendingChildren The number of child rendering phases to expect to be queued for
     *        processing before this phase.
     */
    protected void prepare(Component component, Object model, int index,
            RenderComponentPhase parent, int pendingChildren) {
        super.prepare(component, model, index, parent == null ? null : parent.getComponent(), null);
        this.renderParent = parent;
        this.pendingChildren = pendingChildren;
    }

    /**
     * {@inheritDoc}
     * @return UifConstants.ViewPhases.RENDER
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.RENDER;
    }

    /**
     * {@inheritDoc}
     * @return UifConstants.ViewStatus.FINAL
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.FINAL;
    }

    /**
     * {@inheritDoc}
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
     * Perform rendering on the given component.
     * 
     * {@inheritDoc}
     */
    @Override
    protected void initializePendingTasks(Queue<ViewLifecycleTask> tasks) {
        Component component = getComponent();
        if (component == null || !component.isRender() || component.getTemplate() == null) {
            return;
        }

        tasks.add(LifecycleTaskFactory.getTask(RenderComponentTask.class, this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeSuccessors(Queue<ViewLifecyclePhase> successors) {
        if (renderParent == null) {
            return;
        }

        synchronized (renderParent) {
            // InitializeSuccessors is invoked right after processing.
            // Once the last sibling is processed, then queue the parent phase
            // as a successor.
            if (--renderParent.pendingChildren == 0) {
                successors.add(renderParent);
            }
        }
    }

}
