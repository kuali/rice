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
package org.kuali.rice.kns.ui.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.ui.container.View;
import org.kuali.rice.kns.ui.service.ViewHelperService;
import org.kuali.rice.kns.ui.service.ViewService;

/**
 * Implementation of <code>ViewService</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewServiceImpl implements ViewService {
	private static final Logger LOG = Logger.getLogger(ViewServiceImpl.class);

	protected DataDictionaryService dataDictionaryService;

	/**
	 * @see org.kuali.rice.kns.ui.service.ViewService#getViewById(java.lang.String)
	 */
	public View getViewById(String viewId) {
		return getView(viewId, new HashMap<String, String>());
	}

	/**
	 * @see org.kuali.rice.kns.ui.service.ViewService#getView(java.lang.String,
	 *      java.util.Map)
	 */
	public View getView(String viewId, Map<String, String> options) {
		View view = dataDictionaryService.getViewById(viewId);

		// invoke initialize phase on the views helper service
		ViewHelperService lifecycleService = getLifecycleService(view);
		lifecycleService.performInitialization(view, options);

		return view;
	}

	/**
	 * Retrieves the <code>ViewHelperService</code> configured for the view
	 * from the application context. If a service is not found a
	 * <code>RuntimeException</code> will be thrown.
	 * 
	 * @param view
	 *            - view instance with configured service
	 * @return ViewHelperService instance
	 */
	protected ViewHelperService getLifecycleService(View view) {
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
