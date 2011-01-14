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

import org.apache.log4j.Logger;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.uif.UIFConstants;
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
	 * @see org.kuali.rice.kns.uif.service.ViewService#getViewById(java.lang.String)
	 */
	public View getViewById(String viewId) {
		return getView(viewId, new HashMap<String, String>());
	}

	/**
	 * Retrieves the view from the data dictionary and its corresponding
	 * <code>ViewHelperService</code>. The first phase of the view lifecycle
	 * Initialize is then performed
	 * 
	 * @see org.kuali.rice.kns.uif.service.ViewService#getView(java.lang.String,
	 *      java.util.Map)
	 */
	public View getView(String viewId, Map<String, String> parameters) {
		View view = dataDictionaryService.getViewById(viewId);

		// get the configured helper service for the view
		ViewHelperService helperService = getHelperService(view);

		// get the initial context for the view using the helper service
		Map<String, String> context = helperService.createInitialViewContext(view, parameters);

		// set context on View instance for reference by its components
		view.setContext(context);

		// invoke initialize phase on the views helper service
		helperService.performInitialization(view);

		return view;
	}

	/**
	 * Calls applyModels using the default model name for the given model
	 * instance
	 * 
	 * @see org.kuali.rice.kns.uif.service.ViewService#applyModel(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	public void applyModel(View view, Object model) {
		Map<String, Object> models = new HashMap<String, Object>();
		models.put(UIFConstants.DEFAULT_MODEL_NAME, model);

		applyModels(view, models);
	}

	/**
	 * Calls the <code>ViewHelperService</code> configured for the view to carry
	 * out the ApplyModels phase
	 * 
	 * @see org.kuali.rice.kns.uif.service.ViewService#applyModels(org.kuali.rice.kns.uif.container.View,
	 *      java.util.Map)
	 */
	public void applyModels(View view, Map<String, Object> models) {
		// get the configured helper service for the view
		ViewHelperService helperService = getHelperService(view);

		// invoke helper service to perform conditional logic
		helperService.performConditionalLogic(view, models);
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
