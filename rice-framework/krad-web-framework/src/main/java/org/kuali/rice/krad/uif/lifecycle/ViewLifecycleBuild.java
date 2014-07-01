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

import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.web.controller.UifControllerHelper;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perform the lifecycle process for the view or a component.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see ViewLifecycle#encapsulateLifecycle(View, Object, javax.servlet.http.HttpServletRequest,
 * javax.servlet.http.HttpServletResponse, Runnable)
 * @see UifControllerHelper#invokeViewLifecycle(javax.servlet.http.HttpServletRequest,
 * javax.servlet.http.HttpServletResponse, UifFormBase)
 */
public class ViewLifecycleBuild implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ViewLifecycleBuild.class);

    private final Map<String, String> parameters;
    private final Map<String, List<String>> refreshPathMappings;

    /**
     * Constructor.
     *
     * @param parameters Map of key values pairs that provide configuration for the view, this is generally comes from
     * the request and can be the request parameter Map itself. Any parameters not valid for the View will be
     * filtered out
     * @param refreshPathMappings
     */
    public ViewLifecycleBuild(Map<String, String> parameters, Map<String, List<String>> refreshPathMappings) {
        this.parameters = parameters;
        this.refreshPathMappings = refreshPathMappings;
    }

    /**
     * Runs the three lifecycle phases and performs post finalize processing.
     */
    @Override
    public void run() {
        View view = ViewLifecycle.getView();

        ProcessLogger.trace("begin-view-lifecycle:" + view.getId());

        populateViewRequestParameters();

        runInitializePhase();

        runApplyModelPhase();

        runFinalizePhase();

        // remove view so default values are only applied once
        ((ViewModel) ViewLifecycle.getModel()).setApplyDefaultValues(false);

        // build script for generating growl messages
        String growlScript = ViewLifecycle.getHelper().buildGrowlScript();
        ((ViewModel) ViewLifecycle.getModel()).setGrowlScript(growlScript);

        // on component refreshes regenerate server message content for page
        if (ViewLifecycle.isRefreshLifecycle()) {
            PageGroup page = view.getCurrentPage();
            page.getValidationMessages().generateMessages(view, ViewLifecycle.getModel(), page);
        }

        LifecycleRefreshPathBuilder.processLifecycleElements();

        ViewLifecycle.getViewPostMetadata().cleanAfterLifecycle();

        ProcessLogger.trace("finalize:" + view.getId());
    }

    /**
     * Invokes the view helper to populate view attributes from request parameters, then makes a back up of the
     * view request parameters on the form.
     */
    protected void populateViewRequestParameters() {
        View view = ViewLifecycle.getView();
        ViewHelperService helper = ViewLifecycle.getHelper();
        UifFormBase model = (UifFormBase) ViewLifecycle.getModel();

        // populate view from request parameters. In case of refresh, the parameters will be stored on the
        // form from the initial build
        Map<String, String> parametersToPopulate = parameters;
        if (ViewLifecycle.isRefreshLifecycle()) {
            parametersToPopulate = model.getViewRequestParameters();
        }

        helper.populateViewFromRequestParameters(parametersToPopulate);

        // backup view request parameters on form for refreshes
        model.setViewRequestParameters(view.getViewRequestParameters());
    }

    /**
     * Runs the initialize lifecycle phase.
     *
     * <p>First the view helper is invoked to perform any custom processing, then the processor is invoked
     * to perform any tasks for this phase.</p>
     */
    protected void runInitializePhase() {
        ViewLifecycleProcessor processor = ViewLifecycle.getProcessor();
        ViewLifecyclePhase phase = KRADServiceLocatorWeb.getViewLifecyclePhaseBuilder().buildPhase(
                UifConstants.ViewPhases.INITIALIZE);

        View view = ViewLifecycle.getView();
        ViewHelperService helper = ViewLifecycle.getHelper();
        UifFormBase model = (UifFormBase) ViewLifecycle.getModel();

        ViewLifecycle.getExpressionEvaluator().initializeEvaluationContext(model);

        if (LOG.isInfoEnabled()) {
            LOG.info("performing initialize phase for view: " + view.getId());
        }

        helper.performCustomViewInitialization(model);

        if (refreshPathMappings != null) {
            List<String> refreshPaths = refreshPathMappings.get(UifConstants.ViewPhases.INITIALIZE);
            if (refreshPaths != null) {
                phase.setRefreshPaths(refreshPaths);
            }
        }

        processor.performPhase(phase);

        ProcessLogger.trace("initialize:" + view.getId());
    }

    /**
     * Runs the apply model lifecycle phase.
     *
     * <p>Default values are applied and context is setup for expression evaluation. Then the processor is invoked
     * to perform any tasks for this phase. </p>
     */
    protected void runApplyModelPhase() {
        ViewLifecycleProcessor processor = ViewLifecycle.getProcessor();
        ViewLifecyclePhase phase = KRADServiceLocatorWeb.getViewLifecyclePhaseBuilder().buildPhase(
                UifConstants.ViewPhases.APPLY_MODEL);

        View view = ViewLifecycle.getView();
        ViewHelperService helper = ViewLifecycle.getHelper();
        UifFormBase model = (UifFormBase) ViewLifecycle.getModel();

        if (LOG.isInfoEnabled()) {
            LOG.info("performing apply model phase for view: " + view.getId());
        }

        // apply default values if view in list

        if(model.isApplyDefaultValues()) {
            helper.applyDefaultValues(view);

            //ensure default values are only set once
            model.setApplyDefaultValues(false);
        }

        // get action flag and edit modes from authorizer/presentation controller
        helper.retrieveEditModesAndActionFlags();

        // set view context for conditional expressions
        helper.setViewContext();

        if (refreshPathMappings != null) {
            List<String> refreshPaths = refreshPathMappings.get(UifConstants.ViewPhases.APPLY_MODEL);
            if (refreshPaths != null) {
                phase.setRefreshPaths(refreshPaths);
            }
        }

        processor.performPhase(phase);

        ProcessLogger.trace("apply-model:" + view.getId());
    }

    /**
     * Runs the finalize lifecycle phase.
     *
     * <p>Processor is invoked to perform any tasks for this phase.</p>
     */
    protected void runFinalizePhase() {
        ViewLifecycleProcessor processor = ViewLifecycle.getProcessor();
        ViewLifecyclePhase phase = KRADServiceLocatorWeb.getViewLifecyclePhaseBuilder().buildPhase(
                UifConstants.ViewPhases.FINALIZE);

        View view = ViewLifecycle.getView();
        if (LOG.isInfoEnabled()) {
            LOG.info("performing finalize phase for view: " + view.getId());
        }

        if (refreshPathMappings != null) {
            List<String> refreshPaths = refreshPathMappings.get(UifConstants.ViewPhases.FINALIZE);
            if (refreshPaths != null) {
                phase.setRefreshPaths(refreshPaths);
            }
        }

        processor.performPhase(phase);
    }

}
