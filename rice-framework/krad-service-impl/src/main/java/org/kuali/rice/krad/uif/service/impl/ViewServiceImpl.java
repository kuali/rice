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
package org.kuali.rice.krad.uif.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.krad.datadictionary.validator.ValidationController;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.ViewStatus;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.service.ViewTypeService;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * Implementation of <code>ViewService</code>
 *
 * <p>
 * Provides methods for retrieving View instances and carrying out the View
 * lifecycle methods. Interacts with the configured <code>ViewHelperService</code>
 * during the view lifecycle
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewServiceImpl implements ViewService {
    private static final Logger LOG = Logger.getLogger(ViewServiceImpl.class);

    private DataDictionaryService dataDictionaryService;

    // TODO: remove once we can get beans by type from spring
    private List<ViewTypeService> viewTypeServices;

    /**
     * @see org.kuali.rice.krad.uif.service.ViewService#getViewById(java.lang.String)
     */
    public View getViewById(final String viewId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving view instance for id: " + viewId);
        }
        
        return dataDictionaryService.getViewById(viewId);
    }

    /**
     * Retrieves the <code>ViewTypeService</code> for the given view type, then builds up the index based
     * on the supported view type parameters and queries the dictionary service to retrieve the view
     * based on its type and index
     *
     * @see org.kuali.rice.krad.uif.service.ViewService#getViewByType(org.kuali.rice.krad.uif.UifConstants.ViewType,
     *      java.util.Map<java.lang.String,java.lang.String>)
     */
    public View getViewByType(ViewType viewType, Map<String, String> parameters) {
        ViewTypeService typeService = getViewTypeService(viewType);
        if (typeService == null) {
            throw new RuntimeException("Unable to find view type service for view type name: " + viewType);
        }

        Map<String, String> typeParameters = typeService.getParametersFromRequest(parameters);

        View view = dataDictionaryService.getViewByTypeIndex(viewType, typeParameters);
        if (view == null) {
            LOG.warn("View not found for type: " + viewType);
        }

        return view;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewService#getViewIdForViewType(org.kuali.rice.krad.uif.UifConstants.ViewType,
     * java.util.Map<java.lang.String,java.lang.String>)
     */
    public String getViewIdForViewType(ViewType viewType, Map<String, String> parameters) {
        ViewTypeService typeService = getViewTypeService(viewType);
        if (typeService == null) {
            throw new RuntimeException("Unable to find view type service for view type name: " + viewType);
        }

        Map<String, String> typeParameters = typeService.getParametersFromRequest(parameters);

        return dataDictionaryService.getViewIdByTypeIndex(viewType, typeParameters);
    }

    /**
     * TODO: Consider moving this method to ViewLifecycle
     * @see org.kuali.rice.krad.uif.service.ViewService#buildView(org.kuali.rice.krad.uif.view.View, java.lang.Object,
     *      java.util.Map<java.lang.String,java.lang.String>)
     */
    public View buildView(View view, final Object model, final Map<String, String> parameters) {
        View builtView = ViewLifecycle.encapsulateLifecycle(view, new Runnable(){
			@Override
			public void run() {
				ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
				View view = viewLifecycle.getView();
				
				// populate view from request parameters
				viewLifecycle.populateViewFromRequestParameters(parameters);

				// backup view request parameters on form for recreating lost
				// views (session timeout)
				((UifFormBase) model).setViewRequestParameters(view
						.getViewRequestParameters());

		        // invoke initialize phase on the views helper service
		        if (LOG.isInfoEnabled()) {
		            LOG.info("performing initialize phase for view: " + view.getId());
		        }
		        viewLifecycle.performInitialization(model);

		        // do indexing                               
		        if (LOG.isDebugEnabled()) {
		            LOG.debug("processing indexing for view: " + view.getId());
		        }
		        view.index();

		        // update status on view
		        if (LOG.isDebugEnabled()) {
		            LOG.debug("Updating view status to INITIALIZED for view: " + view.getId());
		        }
		        view.setViewStatus(ViewStatus.INITIALIZED);

		        // Apply Model Phase
		        if (LOG.isInfoEnabled()) {
		            LOG.info("performing apply model phase for view: " + view.getId());
		        }
		        viewLifecycle.performApplyModel(model);

		        // do indexing
		        if (LOG.isInfoEnabled()) {
		            LOG.info("reindexing after apply model for view: " + view.getId());
		        }
		        view.index();

		        // Finalize Phase
		        if (LOG.isInfoEnabled()) {
		            LOG.info("performing finalize phase for view: " + view.getId());
		        }
		        viewLifecycle.performFinalize(model);

		        // do indexing
		        if (LOG.isInfoEnabled()) {
		            LOG.info("processing final indexing for view: " + view.getId());
		        }
		        view.index();

		        // update status on view
		        if (LOG.isDebugEnabled()) {
		            LOG.debug("Updating view status to FINAL for view: " + view.getId());
		        }
		        view.setViewStatus(ViewStatus.FINAL);
			}}).getView();

        // Validation of the page's beans
        if (CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsBoolean(
                UifConstants.VALIDATE_VIEWS_ONBUILD)) {
            ValidationController validator = new ValidationController(true, true, true, true, false);
            Log tempLogger = LogFactory.getLog(ViewServiceImpl.class);
            validator.validate(builtView, tempLogger, false);
        }
        
        return builtView;
    }

    public ViewTypeService getViewTypeService(UifConstants.ViewType viewType) {
        if (viewTypeServices != null) {
            for (ViewTypeService typeService : viewTypeServices) {
                if (viewType.equals(typeService.getViewTypeName())) {
                    return typeService;
                }
            }
        }

        return null;
    }

    public List<ViewTypeService> getViewTypeServices() {
        return this.viewTypeServices;
    }

    public void setViewTypeServices(List<ViewTypeService> viewTypeServices) {
        this.viewTypeServices = viewTypeServices;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
