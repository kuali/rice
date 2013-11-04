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

import java.util.Map;

import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.web.controller.UifControllerHelper;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perform the full view lifecycle.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see ViewLifecycle#encapsulateLifecycle(View, Object, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Runnable)
 * @see UifControllerHelper#prepareViewForRendering(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, UifFormBase)
 */
public class ViewLifecycleFullBuild implements Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(ViewLifecycleFullBuild.class);

    private final Map<String, String> parameters;

    /**
     * Constructor.
     * 
     * @param parameters Map of key values pairs that provide configuration for the
     *        <code>View</code>, this is generally comes from the request and can be the request
     *        parameter Map itself. Any parameters not valid for the View will be filtered out
     */
    public ViewLifecycleFullBuild(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void run() {
        ViewLifecycleProcessor processor = ViewLifecycle.getProcessor();
        View view = ViewLifecycle.getView();
        ViewHelperService helper = ViewLifecycle.getHelper();
        UifFormBase model = (UifFormBase) ViewLifecycle.getModel();

        if (ViewLifecycle.isTrace()) {
            ProcessLogger.trace("begin-view-lifecycle:" + view.getId());
        }
        
        // populate view from request parameters
        helper.populateViewFromRequestParameters(parameters);

        // backup view request parameters on form for recreating lost
        // views (session timeout)
        model.setViewRequestParameters(view.getViewRequestParameters());

        // invoke initialize phase on the views helper service
        if (LOG.isInfoEnabled()) {
            LOG.info("performing initialize phase for view: " + view.getId());
        }

        helper.performCustomViewInitialization(model);

        processor.performPhase(LifecyclePhaseFactory.initialize(view, model, 0, null, null));

        if (ViewLifecycle.isTrace()) {
            ProcessLogger.trace("initialize:" + view.getId());
        }
        
        // Apply Model Phase
        if (LOG.isInfoEnabled()) {
            LOG.info("performing apply model phase for view: " + view.getId());
        }
        
        // apply default values if they have not been applied yet
        if (!model.isDefaultsApplied()) {
            helper.applyDefaultValues(view);
            model.setDefaultsApplied(true);
        }

        // get action flag and edit modes from authorizer/presentation controller
        helper.retrieveEditModesAndActionFlags();

        // set view context for conditional expressions
        helper.setViewContext();

        processor.performPhase(LifecyclePhaseFactory.applyModel(view, model));

        if (ViewLifecycle.isTrace()) {
            ProcessLogger.trace("apply-model:" + view.getId());
        }

        // Finalize Phase
        if (LOG.isInfoEnabled()) {
            LOG.info("performing finalize phase for view: " + view.getId());
        }

        // get script for generating growl messages
        String growlScript = helper.buildGrowlScript();
        ((ViewModel) model).setGrowlScript(growlScript);

        processor.performPhase(LifecyclePhaseFactory.finalize(view, model, 0, null));
        
        if (ViewLifecycle.isTrace()) {
            ProcessLogger.trace("finalize:" + view.getId());
        }
    }
    
}
