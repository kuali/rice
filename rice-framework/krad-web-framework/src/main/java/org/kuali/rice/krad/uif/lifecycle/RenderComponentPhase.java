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
import java.util.List;
import java.util.Queue;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.freemarker.RenderComponentTask;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent;

/**
 * Lifecycle phase processing task for applying the model to a component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RenderComponentPhase extends AbstractViewLifecyclePhase {

    private RenderComponentPhase parent;
    private List<RenderComponentPhase> siblings;
    private List<ViewLifecyclePhase> predecessors;
    
    /**
     * Assert that all siblings have the same parent object.
     * 
     * <p>
     * This method will only execute when assertions are enabled for this class.
     * </p>
     * 
     * @return True if all siblings have the same parent.
     */
    private boolean testSameParent() {
        for (RenderComponentPhase sibling : siblings) {
            assert parent == sibling.parent;
        }
        return true;
    }
    
    /**
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#recycle()
     */
    @Override
    protected void recycle() {
        super.recycle();
        parent = null;
        siblings = null;
        predecessors = null;
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     */
    protected void prepare(Component component, Object model, FinalizeComponentPhase finalizer,
            RenderComponentPhase parent, List<RenderComponentPhase> siblings) {
        super.prepare(component, model, finalizer == null
                ? Collections.<ViewLifecyclePhase> emptyList()
                : Collections.<ViewLifecyclePhase> singletonList(finalizer));
        this.parent = parent;
        this.siblings = siblings;
        assert testSameParent();
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getViewPhase()
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.RENDER;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getStartViewStatus()
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.FINAL;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEndViewStatus()
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.RENDERED;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEventToNotify()
     */
    @Override
    public LifecycleEvent getEventToNotify() {
        return null;
    }
    
    /**
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#getPredecessors()
     */
    @Override
    public List<? extends ViewLifecyclePhase> getPredecessors() {
        if (predecessors == null) {
            return super.getPredecessors();
        } else {
            return predecessors;
        }
    }

    /**
     * Perform rendering on the given component.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#initializePendingTasks(java.util.Queue)
     */
    @Override
    protected void initializePendingTasks(Queue<ViewLifecycleTask> tasks) {
        Component component = getComponent();
        if (component == null || !component.isRender() || component.getTemplate() == null) {
            return;
        }
        
        tasks.add(LifecycleTaskFactory.getTask(RenderComponentTask.class,this));
    }

    /**
     * Define all nested lifecycle components, and component prototypes, as successors.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#initializeSuccessors(java.util.List)
     */
    @Override
    protected void initializeSuccessors(Queue<ViewLifecyclePhase> successors) {
        if (parent == null) {
            return;
        }

        siblings.remove(this);
        for (RenderComponentPhase sibling : siblings) {
            if (!sibling.isProcessed()) {
                return;
            }
        }
        assert siblings.isEmpty() : siblings;

        List<ViewLifecyclePhase> parentPredecessors = new ArrayList<ViewLifecyclePhase>(parent.getPredecessors());
        parentPredecessors.add(this);
        parent.predecessors = Collections.unmodifiableList(parentPredecessors);
        
        successors.add(parent);
    }

}
