/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.uif.UifConstants.ViewStatus;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.service.ViewTypeService;

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
	public View getViewById(String viewId) {
		return getView(viewId, new HashMap<String, String>());
	}

	/**
	 * Retrieves the view from the data dictionary and its corresponding
	 * <code>ViewHelperService</code>. The first phase of the view lifecycle
	 * Initialize is then performed
	 * 
	 * @see org.kuali.rice.krad.uif.service.ViewService#getView(java.lang.String,
	 *      java.util.Map)
	 */
	public View getView(String viewId, Map<String, String> parameters) {
		LOG.debug("retrieving view instance for id: " + viewId);

		View view = dataDictionaryService.getViewById(viewId);
		if (view == null) {
			LOG.error("View not found for id: " + viewId);
			throw new RuntimeException("View not found for id: " + viewId);
		}

		// populate view from request parameters
		view.getViewHelperService().populateViewFromRequestParameters(view, parameters);

		// Initialize Phase
		LOG.info("performing initialize phase for view: " + viewId);
		performInitialization(view, parameters);

		return view;
	}

	/**
	 * Initializes a newly created <code>View</code> instance. Each component of
	 * the tree is invoked to perform setup based on its configuration. In
	 * addition helper service methods are invoked to perform custom
	 * initialization
	 * 
	 * @param view
	 *            - view instance to initialize
	 * @param parameters
	 *            - Map of key values pairs that provide configuration for the
	 *            <code>View</code>, this is generally comes from the request
	 *            and can be the request parameter Map itself. Any parameters
	 *            not valid for the View will be filtered out
	 */
	protected void performInitialization(View view, Map<String, String> parameters) {
		// get the configured helper service for the view
		ViewHelperService helperService = view.getViewHelperService();

		// invoke initialize phase on the views helper service
		helperService.performInitialization(view);

		// do indexing
		LOG.info("processing indexing for view: " + view.getId());
		view.index();

		// update status on view
		LOG.debug("Updating view status to INITIALIZED for view: " + view.getId());
		view.setViewStatus(ViewStatus.INITIALIZED);
	}

	/**
	 * @see org.kuali.rice.krad.uif.service.ViewService#buildView(org.kuali.rice.krad.uif.view.View,
	 *      java.lang.Object)
	 */
	public void buildView(View view, Object model) {
		// get the configured helper service for the view
		ViewHelperService helperService = view.getViewHelperService();

		// Apply Model Phase
		LOG.info("performing apply model phase for view: " + view.getId());
		helperService.performApplyModel(view, model);

		// Update State Phase
		LOG.info("performing finalize phase for view: " + view.getId());
		helperService.performFinalize(view, model);

		// do indexing
		LOG.info("processing indexing for view: " + view.getId());
		view.index();

		// update status on view
		LOG.debug("Updating view status to UPDATED for view: " + view.getId());
		view.setViewStatus(ViewStatus.FINAL);
	}

	/**
	 * @see org.kuali.rice.krad.uif.service.ViewService#rebuildView(java.lang.String,
	 *      java.lang.Object, java.util.Map)
	 */
	public View rebuildView(String viewId, Object model, Map<String, String> parameters) {
		View view = getView(viewId, parameters);
		buildView(view, model);

		return view;
	}

	/**
	 * @see org.kuali.rice.krad.uif.service.ViewService#getViewByType(java.lang.String,
	 *      java.util.Map)
	 */
	public View getViewByType(String viewType, Map<String, String> parameters) {
		View view = getViewForType(viewType, parameters);

		if (view != null) {
			// populate view from request parameters
			view.getViewHelperService().populateViewFromRequestParameters(view, parameters);

			LOG.debug("performing initialize phase for view: " + view.getId());
			performInitialization(view, parameters);
		}

		return view;
	}

	/**
	 * Retrieves the <code>ViewTypeService</code> for the given view type, then
	 * builds up the index based on the supported view type parameters and
	 * queries the dictionary service to retrieve the view based on its type and
	 * index
	 * 
	 * @param viewTypeName
	 *            - name of the view type
	 * @param parameters
	 *            - Map of parameters that were given on request
	 * @return View instance or Null if a matching view was not found
	 */
	protected View getViewForType(String viewTypeName, Map<String, String> parameters) {
		ViewTypeService typeService = getViewTypeService(viewTypeName);
		if (typeService == null) {
			throw new RuntimeException("Unable to find view type service for view type name: " + viewTypeName);
		}

		Map<String, String> typeParameters = typeService.getParametersFromRequest(parameters);

		Map<String, String> indexKey = new HashMap<String, String>();
		for (Map.Entry<String, String> parameter : typeParameters.entrySet()) {
			indexKey.put(parameter.getKey(), parameter.getValue());
		}

		View view = dataDictionaryService.getViewByTypeIndex(viewTypeName, indexKey);

		return view;
	}

	public ViewTypeService getViewTypeService(String viewType) {
		if (viewTypeServices != null) {
			for (ViewTypeService typeService : viewTypeServices) {
				if (StringUtils.equals(viewType, typeService.getViewTypeName())) {
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
