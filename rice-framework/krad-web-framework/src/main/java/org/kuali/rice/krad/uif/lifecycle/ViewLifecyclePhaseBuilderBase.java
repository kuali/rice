/**
 * Copyright 2005-2018 The Kuali Foundation
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

import static org.kuali.rice.krad.uif.UifConstants.ViewPhases.APPLY_MODEL;
import static org.kuali.rice.krad.uif.UifConstants.ViewPhases.FINALIZE;
import static org.kuali.rice.krad.uif.UifConstants.ViewPhases.INITIALIZE;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.freemarker.RenderComponentTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.AddFocusAndJumpDataAttributesTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.AddViewTemplatesTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.ComponentDefaultFinalizeTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.FinalizeViewTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.HelperCustomFinalizeTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.InvokeFinalizerTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.RegisterPropertyEditorTask;
import org.kuali.rice.krad.uif.lifecycle.finalize.SetReadOnlyOnDataBindingTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.AssignIdsTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.ComponentDefaultInitializeTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.HelperCustomInitializeTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.InitializeContainerFromHelperTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.InitializeDataFieldFromDictionaryTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.PopulateComponentFromExpressionGraphTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.PopulatePathTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.PopulateReplacersAndModifiersFromExpressionGraphTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.PrepareForCacheTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.ProcessRemoteFieldsHolderTask;
import org.kuali.rice.krad.uif.lifecycle.initialize.SortContainerTask;
import org.kuali.rice.krad.uif.lifecycle.model.AfterEvaluateExpressionTask;
import org.kuali.rice.krad.uif.lifecycle.model.ApplyAuthAndPresentationLogicTask;
import org.kuali.rice.krad.uif.lifecycle.model.ComponentDefaultApplyModelTask;
import org.kuali.rice.krad.uif.lifecycle.model.EvaluateExpressionsTask;
import org.kuali.rice.krad.uif.lifecycle.model.HelperCustomApplyModelTask;
import org.kuali.rice.krad.uif.lifecycle.model.PopulateComponentContextTask;
import org.kuali.rice.krad.uif.lifecycle.model.RefreshStateModifyTask;
import org.kuali.rice.krad.uif.lifecycle.model.SuffixIdFromContainerTask;
import org.kuali.rice.krad.uif.lifecycle.model.SyncClientSideStateTask;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.RecycleUtils;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Default phase builder implementation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewLifecyclePhaseBuilderBase implements ViewLifecyclePhaseBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewLifecyclePhase buildPhase(View view, String viewPhase, List<String> refreshPaths) {
        return buildPhase(viewPhase, view, null, "", refreshPaths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewLifecyclePhase buildPhase(String viewPhase, LifecycleElement element, Component parent,
            String parentPath, List<String> refreshPaths) {
        ViewLifecyclePhase phase = getPhaseInstance(viewPhase);
        phase.prepare(element, parent, parentPath, refreshPaths);

        return finishBuildPhase(phase);
    }

    /**
     * Determines if the previous phases have been run for the given element and if not backs up
     * until it finds the first phase we need to run.
     *
     * @param phase phase being requested
     * @return phase that should be run
     */
    protected ViewLifecyclePhase finishBuildPhase(ViewLifecyclePhase phase) {
        String previousViewPhase = getPreviousViewPhase(phase);

        while (previousViewPhase != null) {
            ViewLifecyclePhase prevPhase = getPhaseInstance(previousViewPhase);
            prevPhase.prepare(phase.getElement(), phase.getParent(), phase.getParentPath(), phase.getRefreshPaths());

            prevPhase.setNextPhase(phase);
            phase = prevPhase;

            previousViewPhase = getPreviousViewPhase(phase);
        }

        return phase;
    }

    /**
     * Return the previous view phase, for automatic phase spawning.
     *
     * @return phase view phase
     */
    protected static String getPreviousViewPhase(ViewLifecyclePhase phase) {
        String viewPhase = phase.getViewPhase();
        if (FINALIZE.equals(viewPhase) && !phase.getElement().isModelApplied()) {
            return APPLY_MODEL;
        }

        if (APPLY_MODEL.equals(viewPhase) && !phase.getElement().isInitialized()) {
            return INITIALIZE;
        }

        return null;
    }

    /**
     * Build a phase instance for processing the given view phase.
     *
     * @param viewPhase name of the view phase to return
     * @return phase instance
     */
    protected ViewLifecyclePhase getPhaseInstance(String viewPhase) {
        ViewLifecyclePhase phase = RecycleUtils.getRecycledInstance(viewPhase, ViewLifecyclePhase.class);

        if (phase != null) {
            return phase;
        }

        if (UifConstants.ViewPhases.PRE_PROCESS.equals(viewPhase)) {
            phase = buildPreProcessPhase();
        } else if (UifConstants.ViewPhases.INITIALIZE.equals(viewPhase)) {
            phase = buildInitializePhase();
        } else if (UifConstants.ViewPhases.APPLY_MODEL.equals(viewPhase)) {
            phase = buildApplyModelPhase();
        } else if (UifConstants.ViewPhases.FINALIZE.equals(viewPhase)) {
            phase = buildFinalizePhase();
        } else if (UifConstants.ViewPhases.RENDER.equals(viewPhase)) {
            phase = buildRenderPhase();
        }

        if (phase == null) {
            throw new RuntimeException("Cannnot create phase instance for view phase name: " + viewPhase);
        }

        return phase;
    }

    /**
     * Creates an instance of the {@link PreProcessElementPhase} phase with configured tasks.
     *
     * @return view lifecycle phase instance
     */
    protected ViewLifecyclePhase buildPreProcessPhase() {
        PreProcessElementPhase phase = new PreProcessElementPhase();

        List<ViewLifecycleTask<?>> tasks = new ArrayList<ViewLifecycleTask<?>>();

        tasks.add(new AssignIdsTask());
        tasks.add(new PopulatePathTask());
        tasks.add(new SortContainerTask());
        tasks.add(new PrepareForCacheTask());

        phase.setTasks(tasks);
        phase.setSkipLifecycleTasks(new ArrayList<ViewLifecycleTask<?>>());

        return phase;
    }

    /**
     * Creates an instance of the {@link InitializeComponentPhase} phase with configured tasks.
     *
     * @return view lifecycle phase instance
     */
    protected ViewLifecyclePhase buildInitializePhase() {
        InitializeComponentPhase phase = new InitializeComponentPhase();

        List<ViewLifecycleTask<?>> tasks = new ArrayList<ViewLifecycleTask<?>>();

        tasks.add(new AssignIdsTask());
        tasks.add(new PopulatePathTask());
        tasks.add(new PopulateComponentFromExpressionGraphTask());
        tasks.add(new ComponentDefaultInitializeTask());
        tasks.add(new InitializeDataFieldFromDictionaryTask());
        tasks.add(new PopulateReplacersAndModifiersFromExpressionGraphTask());
        tasks.add(new InitializeContainerFromHelperTask());
        tasks.add(new ProcessRemoteFieldsHolderTask());
        tasks.add(new InitializeDataFieldFromDictionaryTask());
        tasks.add(new RunComponentModifiersTask());
        tasks.add(new HelperCustomInitializeTask());

        phase.setTasks(tasks);

        List<ViewLifecycleTask<?>> skipLifecycleTasks = new ArrayList<ViewLifecycleTask<?>>();

        skipLifecycleTasks.add(new AssignIdsTask());

        phase.setSkipLifecycleTasks(skipLifecycleTasks);

        return phase;
    }

    /**
     * Creates an instance of the {@link ApplyModelComponentPhase} phase with configured tasks.
     *
     * @return view lifecycle phase instance
     */
    protected ViewLifecyclePhase buildApplyModelPhase() {
        ApplyModelComponentPhase phase = new ApplyModelComponentPhase();

        List<ViewLifecycleTask<?>> tasks = new ArrayList<ViewLifecycleTask<?>>();

        tasks.add(new SuffixIdFromContainerTask());
        tasks.add(new PopulateComponentContextTask());
        tasks.add(new EvaluateExpressionsTask());
        tasks.add(new AfterEvaluateExpressionTask());
        tasks.add(new SyncClientSideStateTask());
        tasks.add(new ApplyAuthAndPresentationLogicTask());
        tasks.add(new RefreshStateModifyTask());
        tasks.add(new ComponentDefaultApplyModelTask());
        tasks.add(new RunComponentModifiersTask());
        tasks.add(new HelperCustomApplyModelTask());
        tasks.add(new SetReadOnlyOnDataBindingTask());

        phase.setTasks(tasks);

        List<ViewLifecycleTask<?>> skipLifecycleTasks = new ArrayList<ViewLifecycleTask<?>>();

        skipLifecycleTasks.add(new SuffixIdFromContainerTask());

        phase.setSkipLifecycleTasks(skipLifecycleTasks);

        return phase;
    }

    /**
     * Creates an instance of the {@link FinalizeComponentPhase} phase with configured tasks.
     *
     * @return view lifecycle phase instance
     */
    protected ViewLifecyclePhase buildFinalizePhase() {
        FinalizeComponentPhase phase = new FinalizeComponentPhase();

        List<ViewLifecycleTask<?>> tasks = new ArrayList<ViewLifecycleTask<?>>();

        tasks.add(new InvokeFinalizerTask());
        tasks.add(new ComponentDefaultFinalizeTask());
        tasks.add(new AddViewTemplatesTask());
        tasks.add(new FinalizeViewTask());
        tasks.add(new RunComponentModifiersTask());
        tasks.add(new HelperCustomFinalizeTask());
        tasks.add(new RegisterPropertyEditorTask());
        tasks.add(new AddFocusAndJumpDataAttributesTask());

        phase.setTasks(tasks);
        phase.setSkipLifecycleTasks(new ArrayList<ViewLifecycleTask<?>>());

        return phase;
    }

    /**
     * Creates an instance of the {@link RenderComponentPhase} phase with configured tasks.
     *
     * @return view lifecycle phase instance
     */
    protected ViewLifecyclePhase buildRenderPhase() {
        RenderComponentPhase phase = new RenderComponentPhase();

        List<ViewLifecycleTask<?>> tasks = new ArrayList<ViewLifecycleTask<?>>();

        tasks.add(new RenderComponentTask());

        phase.setTasks(tasks);
        phase.setSkipLifecycleTasks(new ArrayList<ViewLifecycleTask<?>>());

        return phase;
    }
}
