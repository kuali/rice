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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.RecycleUtils;

/**
 * Responsible for creating lifecycle phases.
 *
 * <p>This factory recycles completed phases to reduce object creation during the lifecycle.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class LifecyclePhaseFactory {

    /**
     * Private constructor - utility class only.
     */
    private LifecyclePhaseFactory() {}

    /**
     * Creates a new lifecycle phase processing task for pre-processing a lifecycle element.
     * 
     * @param element The element.
     * @param path Path to the component relative to its parent component.
     * @return lifecycle processing task for processing the initialize phase on the component
     */
    public static PreProcessElementPhase preProcess(LifecycleElement element, String path) {
        return preProcess(element, path, null);
    }
    
    /**
     * Creates a new lifecycle phase processing task for pre-processing a lifecycle element.
     * 
     * @param element The element.
     * @param path Path to the component relative to its parent component.
     * @param parent The parent component.
     * @return lifecycle processing task for processing the initialize phase on the component
     */
    public static PreProcessElementPhase preProcess(LifecycleElement element, String path, Component parent) {
        PreProcessElementPhase preProcessPhase = RecycleUtils.getInstance(PreProcessElementPhase.class);
        preProcessPhase.prepare(element, null, path, null, parent, null);
        return preProcessPhase;
    }
    
    /**
     * Creates a new lifecycle phase processing task for performing initialization on a component.
     *
     * @param component The component.
     * @param model The model
     * @param path Path to the component relative to its parent component.
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @return lifecycle processing task for processing the initialize phase on the component
     */
    public static InitializeComponentPhase initialize(Component component, Object model, String path,
            List<String> refreshPaths) {
        return initialize(component, model, path, refreshPaths, null, null);
    }

    /**
     * Creates a new lifecycle phase processing task for performing initialization on a element.
     *
     * @param element The element
     * @param model The model
     * @param parent The parent element.
     * @param path Path to the component relative to its parent component.
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @param nextPhase The applyModel phase to spawn after the successful completion of the
     * initialize phase.
     * @return lifecycle processing task for processing the initialize phase on the component
     */
    public static InitializeComponentPhase initialize(LifecycleElement element, Object model, String path,
            List<String> refreshPaths, Component parent, ApplyModelComponentPhase nextPhase) {
        InitializeComponentPhase initializePhase = RecycleUtils.getInstance(InitializeComponentPhase.class);
        initializePhase.prepare(element, model, path, refreshPaths, parent, nextPhase);

        return initializePhase;
    }

    /**
     * Creates a new lifecycle phase processing task for applying the model to a component.
     *
     * @param component The component.
     * @param model The model
     * @param path Path to the component relative to its parent component.
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @return lifecycle processing task for processing the apply model phase on the component
     */
    public static ApplyModelComponentPhase applyModel(Component component, Object model, String path,
            List<String> refreshPaths) {
        return applyModel(component, model, path, refreshPaths, null, null, new HashSet<String>());
    }

    /**
     * Creates a new lifecycle phase processing task for applying the model to a component.
     *
     * @param component The component.
     * @param model The model
     * @param parent The component.
     * @param path Path to the component relative to its parent component.
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @return lifecycle processing task for processing the apply model phase on the component
     */
    public static ApplyModelComponentPhase applyModel(Component component, Object model, Component parent, String path,
            List<String> refreshPaths) {
        return applyModel(component, model, path, refreshPaths, parent, null, new HashSet<String>());
    }

    /**
     * Creates a new lifecycle phase processing task for applying the model to a element.
     *
     * @param element The element.
     * @param model The model
     * @param parent The parent component.
     * @param path Path to the component relative to its parent component.
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @param nextPhase The applyModel phase to spawn after the successful completion of the
     * initialize phase.
     * @param visitedIds The set of visited IDs to track while applying model.
     * @return lifecycle processing task for processing the apply model phase on the component
     */
    public static ApplyModelComponentPhase applyModel(LifecycleElement element, Object model, String path,
            List<String> refreshPaths, Component parent, FinalizeComponentPhase nextPhase,
            Set<String> visitedIds) {
        ApplyModelComponentPhase applyModelPhase = RecycleUtils.getInstance(ApplyModelComponentPhase.class);
        applyModelPhase.prepare(element, model, path, refreshPaths, parent, nextPhase, visitedIds);

        return applyModelPhase;
    }

    /**
     * Creates a new lifecycle phase processing task for finalizing a nested component.
     *
     * @param component The component.
     * @param model The model
     * @param path Path to the component relative to its parent component.
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @return lifecycle processing task for processing the finalize phase on the component
     */
    public static FinalizeComponentPhase finalize(Component component, Object model, String path,
            List<String> refreshPaths) {
        return finalize(component, model, path, refreshPaths, null);
    }

    /**
     * Creates a new lifecycle phase processing task for finalizing a nested component.
     *
     * @param element The component.
     * @param model The model
     * @param parent The parent component.
     * @param path Path to the component relative to its parent component.
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @return lifecycle processing task for processing the finalize phase on the component
     */
    public static FinalizeComponentPhase finalize(LifecycleElement element, Object model, String path,
            List<String> refreshPaths, Component parent) {
        FinalizeComponentPhase finalizePhase = RecycleUtils.getInstance(FinalizeComponentPhase.class);
        finalizePhase.prepare(element, model, path, refreshPaths, parent);

        return finalizePhase;
    }

    /**
     * Creates a new lifecycle phase processing task for rendering a component.
     *
     * @param component The component to render.
     * @param model The model associated with the component.
     * @param path Path to the component relative to its parent component.
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @return lifecycle processing task for processing the render phase on the component
     */
    public static RenderComponentPhase render(Component component, Object model, String path,
            List<String> refreshPaths) {
        RenderComponentPhase renderPhase = RecycleUtils.getInstance(RenderComponentPhase.class);
        renderPhase.prepare(component, model, path, refreshPaths, null, null, Collections.<String> emptySet());
        return renderPhase;
    }

    /**
     * Creates a new lifecycle phase processing task for rendering a component.
     *
     * @param finalizePhase The finalize component phase associated with this rendering phase.
     * @param refreshPaths list of paths to run lifecycle on when executing a refresh lifecycle
     * @param renderParent The rendering phase for the parent of the component associated with this phase.
     * @param pendingChildren The number of child phases to expect to be queued with this phase as
     * their rendering parent.
     * @return lifecycle processing task for processing the render phase on the component
     */
    public static RenderComponentPhase render(FinalizeComponentPhase finalizePhase,
            List<String> refreshPaths, RenderComponentPhase renderParent, Set<String> pendingChildren) {
        LifecycleElement element = finalizePhase.getElement();
        RenderComponentPhase renderPhase = RecycleUtils.getInstance(RenderComponentPhase.class);
        renderPhase.prepare(element, finalizePhase.getModel(), finalizePhase.getParentPath(), refreshPaths,
                finalizePhase.getParent(), renderParent, pendingChildren);

        return renderPhase;
    }

    /**
     * Recycles a task instance after processing.
     *
     * @param phase The task to recycle.
     */
    static void recycle(ViewLifecyclePhaseBase phase) {
        phase.recycle();
        RecycleUtils.recycle(phase);
    }

}
