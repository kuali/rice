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
package org.kuali.rice.krad.web.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.CollectionControllerService;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Default implementation of the collection controller service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionControllerServiceImpl implements CollectionControllerService {

    private ModelAndViewService modelAndViewService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView addLine(final UifFormBase form) {
        final CollectionActionParameters parameters = new CollectionActionParameters(form, false);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ViewLifecycle.getHelper().processCollectionAddLine(form, parameters.selectedCollectionId,
                        parameters.selectedCollectionPath);
            }
        };

        return performHelperLifecycle(form, runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView addBlankLine(final UifFormBase form) {
        final CollectionActionParameters parameters = new CollectionActionParameters(form, false);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ViewLifecycle.getHelper().processCollectionAddBlankLine(form, parameters.selectedCollectionId,
                        parameters.selectedCollectionPath);
            }
        };

        return performHelperLifecycle(form, runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView saveLine(final UifFormBase form) {
        final CollectionActionParameters parameters = new CollectionActionParameters(form, true);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ViewLifecycle.getHelper().processCollectionSaveLine(form, parameters.selectedCollectionId,
                        parameters.selectedCollectionPath, parameters.selectedLineIndex);
            }
        };

        return performHelperLifecycle(form, runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView deleteLine(final UifFormBase form) {
        final CollectionActionParameters parameters = new CollectionActionParameters(form, true);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ViewLifecycle.getHelper().processCollectionDeleteLine(form, parameters.selectedCollectionId,
                        parameters.selectedCollectionPath, parameters.selectedLineIndex);
            }
        };

        return performHelperLifecycle(form, runnable);
    }

    /**
     * Helper method to run a {@link java.lang.Runnable} through the view lifecycle.
     *
     * @param form form instance containing the model data
     * @param runnable code to run in the lifecycle
     * @return ModelAndView instance for rendering the view
     */
    protected ModelAndView performHelperLifecycle(final UifFormBase form, Runnable runnable) {
        ViewLifecycle.encapsulateLifecycle(form.getView(), form, form.getViewPostMetadata(), null, form.getRequest(),
                runnable);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView retrieveCollectionPage(UifFormBase form) {
        form.setCollectionPagingRequest(true);

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView tableJsonRetrieval(UifFormBase form) {
        form.setCollectionPagingRequest(true);

        // set property to trigger special JSON rendering logic
        form.setRequestJsonTemplate(UifConstants.TableToolsValues.JSON_TEMPLATE);

        return getModelAndViewService().getModelAndView(form);
    }

    protected ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }

    /**
     * Helper class for maintaining collection action parameters for a request.
     */
    protected static class CollectionActionParameters {
        private String selectedCollectionPath;
        private String selectedCollectionId;
        private int selectedLineIndex;

        /**
         * Constructs a new CollectionActionParameters pulling the action parameter values from the give form.
         *
         * @param form form instance containing the action parameter values
         * @param requireIndexParam whether to thrown an exception if the selected line value action parameter
         * is not present (or valid)
         * @throws java.lang.RuntimeException if selected collection path is missing, or requireIndexParam is true
         * and selected line index is missing
         */
        public CollectionActionParameters(UifFormBase form, boolean requireIndexParam) {
            selectedCollectionPath = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_PATH);
            if (StringUtils.isBlank(selectedCollectionPath)) {
                throw new RuntimeException("Selected collection path was not set for collection action");
            }

            selectedCollectionId = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_ID);

            String selectedLine = form.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);
            if (StringUtils.isNotBlank(selectedLine)) {
                selectedLineIndex = Integer.parseInt(selectedLine);
            } else {
                selectedLineIndex = -1;
            }

            if (requireIndexParam && (selectedLineIndex == -1)) {
                throw new RuntimeException("Selected line index was not set for collection action");
            }
        }
    }
}
