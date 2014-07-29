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

import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;

import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;
import org.kuali.rice.krad.uif.util.RecycleUtils;

/**
 * Lifecycle phase processing task for finalizing a component.
 *
 * <p>
 * The finalize phase is the last phase before the view is rendered. Here final preparations can be
 * made based on the updated view state.
 * </p>
 *
 * <p>
 * The finalize phase runs after the apply model phase and can be called multiple times for the
 * view's lifecylce (however typically only once per request)
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FinalizeComponentPhase extends ViewLifecyclePhaseBase {

    /**
     * Lifecycle phase to render this component after finalization, if in-lifecycle rendering is
     * enabled.
     *
     * @see ViewLifecycle#isRenderInLifecycle()
     */
    private RenderComponentPhase renderPhase;

    /**
     * {@inheritDoc}
     */
    @Override
    public void recycle() {
        super.recycle();
        renderPhase = null;
    }

    /**
     * {@inheritDoc}
     *
     * @return UifConstants.ViewPhases.FINALIZE
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.FINALIZE;
    }

    /**
     * {@inheritDoc}
     *
     * @return UifConstants.ViewStatus.MODEL_APPLIED
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.MODEL_APPLIED;
    }

    /**
     * {@inheritDoc}
     *
     * @return UifConstants.ViewStatus.FINAL
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.FINAL;
    }

    /**
     * {@inheritDoc}
     *
     * @return LifecycleEvent.LIFECYCLE_COMPLETE
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return LifecycleEvent.LIFECYCLE_COMPLETE;
    }

    /**
     * Verify that the render phase has no pending children.
     */
    @Override
    protected void verifyCompleted() {
        super.verifyCompleted();

        if (renderPhase != null) {
            renderPhase.verifyCompleted();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeSuccessors(Queue<ViewLifecyclePhase> successors) {
        super.initializeSuccessors(successors);

        if (ViewLifecycle.isRenderInLifecycle()) {
            RenderComponentPhase parentRenderPhase = null;

            ViewLifecyclePhase predecessor = getPredecessor();
            if (predecessor instanceof FinalizeComponentPhase) {
                parentRenderPhase = ((FinalizeComponentPhase) predecessor).renderPhase;
            }

            @SuppressWarnings("unchecked") Set<String> pendingChildren = RecycleUtils.getInstance(LinkedHashSet.class);
            for (ViewLifecyclePhase successor : successors) {
                boolean skipSuccessor;
                if (successor instanceof ViewLifecyclePhaseBase) {
                    skipSuccessor = ((ViewLifecyclePhaseBase) successor).shouldSkipLifecycle();
                } else {
                    // TODO: consider moving shouldSkipLifecycle to public interface
                    skipSuccessor = successor.getElement().skipLifecycle();
                }

                // Don't queue successors that will be skipped.
                // Doing so would cause notification issues for the render phase.
                if (skipSuccessor) {
                    continue;
                }

                // Queue the successor, with strict validation that it hasn't already been queued
                if (!pendingChildren.add(successor.getParentPath())) {
                    ViewLifecycle.reportIllegalState(
                            "Successor is already pending " + pendingChildren + "\n" + successor + "\n" + this);
                }
            }

            renderPhase = (RenderComponentPhase) KRADServiceLocatorWeb.getViewLifecyclePhaseBuilder().buildPhase(
                    UifConstants.ViewPhases.RENDER, getElement(), getParent(), getParentPath(), getRefreshPaths());
            renderPhase.prepareRenderPhase(parentRenderPhase, pendingChildren);

            trace("create-render " + getElement().getId() + " " + pendingChildren);
        }

        if (successors.isEmpty() && renderPhase != null) {
            successors.add(renderPhase);
        }
    }

}
