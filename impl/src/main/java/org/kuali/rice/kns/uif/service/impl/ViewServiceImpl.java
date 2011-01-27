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
package org.kuali.rice.kns.uif.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.UifConstants.ViewType;
import org.kuali.rice.kns.uif.UifConstants.ViewTypeIndexParameterNames;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.service.ViewHelperService;
import org.kuali.rice.kns.uif.service.ViewService;

/**
 * Implementation of <code>ViewService</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewServiceImpl implements ViewService {
	private static final Logger LOG = Logger.getLogger(ViewServiceImpl.class);

	protected DataDictionaryService dataDictionaryService;

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewService#getViewById(java.lang.String,
	 *      java.lang.Object)
	 */
	public View getViewById(String viewId, Object model) {
		return getView(viewId, new HashMap<String, String>(), model);
	}

	/**
	 * Retrieves the view from the data dictionary and its corresponding
	 * <code>ViewHelperService</code>. The first phase of the view lifecycle
	 * Initialize is then performed
	 * 
	 * @see org.kuali.rice.kns.uif.service.ViewService#getView(java.lang.String,
	 *      java.util.Map, java.lang.Object)
	 */
	public View getView(String viewId, Map<String, String> parameters, Object model) {
		View view = dataDictionaryService.getViewById(viewId);

		// Initialize Phase
		performInitialization(view, parameters);
		
		// Apply Model Phase
		performApplyModel(view, model);
		
		// Update State Phase
		performUpdateState(view);
		
		// do indexing
		view.getViewIndex().index();

		return view;
	}

	protected void performInitialization(View view, Map<String, String> parameters) {
		// get the configured helper service for the view
		ViewHelperService helperService = getHelperService(view);

		// get the initial context for the view using the helper service
		Map<String, String> context = helperService.createInitialViewContext(view, parameters);

		// set context on View instance for reference by its components
		view.setContext(context);

		// invoke initialize phase on the views helper service
		helperService.performInitialization(view);
	}

	/**
	 * Performs initialization or updating of the <code>View</code> instance
	 * based on the form (containing models) instance
	 * 
	 * <p>
	 * Part of the view lifecycle that applies the model data to the view.
	 * Should be called after the model has been populated before the view is
	 * rendered. The main things that occur during this phase are:
	 * <ul>
	 * <li>Generation of dynamic fields (such as collection rows)</li>
	 * <li>Execution of conditional logic (hidden, read-only, required settings
	 * based on model values)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param view
	 *            - view instance the model should be applied to
	 * @param model
	 *            - Top level object containing the data (could be the form or a
	 *            top level business object, dto)
	 */
	protected void performApplyModel(View view, Object model) {
		// get the configured helper service for the view
		ViewHelperService helperService = getHelperService(view);

		// invoke helper service to perform conditional logic
		helperService.performApplyModel(view, model);
	}

	/**
	 * 
	 * @param view
	 */
	protected void performUpdateState(View view) {

	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewService#getViewHelperService(java.lang.String)
	 */
	public ViewHelperService getViewHelperService(String viewId) {
		View view = dataDictionaryService.getViewById(viewId);

		return getHelperService(view);
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewService#getInquiryViewId(java.lang.String,
	 *      java.lang.String)
	 */
	public String getInquiryViewId(String name, String modelClassName) {
		String viewName = name;
		if (StringUtils.isBlank(name)) {
			viewName = UifConstants.DEFAULT_VIEW_NAME;
		}

		Map<String, String> indexKey = new HashMap<String, String>();
		indexKey.put(ViewTypeIndexParameterNames.NAME, viewName);
		indexKey.put(ViewTypeIndexParameterNames.MODEL_CLASS, modelClassName);

		View view = dataDictionaryService.getViewByTypeIndex(ViewType.INQUIRY, indexKey);

		if (view != null) {
			return view.getId();
		}

		return null;
	}

	/**
	 * Retrieves the <code>ViewHelperService</code> configured for the view from
	 * the application context. If a service is not found a
	 * <code>RuntimeException</code> will be thrown.
	 * 
	 * @param view
	 *            - view instance with configured service
	 * @return ViewHelperService instance
	 */
	protected ViewHelperService getHelperService(View view) {
		ViewHelperService lifecycleService = KNSServiceLocator.getService(view.getViewHelperServiceBeanId());

		if (lifecycleService == null) {
			LOG.error("Unable to find ViewHelperService for view: " + view.getId());
			throw new RuntimeException("Unable to find ViewHelperService for view: " + view.getId());
		}

		return lifecycleService;
	}

	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

}
