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

import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.UifConstants.ViewType;
import org.kuali.rice.kns.uif.UifConstants.ViewTypeParameterNames;
import org.kuali.rice.kns.uif.container.LookupView;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.service.ViewTypeService;

/**
 * Type service implementation for Lookup views
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupViewTypeServiceImpl implements ViewTypeService {

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewTypeService#getViewTypeName()
	 */
	public String getViewTypeName() {
		return ViewType.LOOKUP;
	}
	
	/**
	 * @see org.kuali.rice.kns.uif.service.ViewTypeService#getParametersFromView(org.kuali.rice.kns.uif.container.View)
	 */
	public Map<String, String> getParametersFromView(View view) {
		Map<String, String> parameters = new HashMap<String, String>();

		LookupView lookupView = (LookupView) view;

		parameters.put(ViewTypeParameterNames.NAME, lookupView.getViewName());
		parameters.put(ViewTypeParameterNames.DATA_OBJECT_CLASS_NAME, lookupView.getDataObjectClassName().getName());

		return parameters;
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewTypeService#getParametersFromRequest(java.util.Map)
	 */
	public Map<String, String> getParametersFromRequest(Map<String, String> requestParameters) {
		Map<String, String> parameters = new HashMap<String, String>();

		if (requestParameters.containsKey(ViewTypeParameterNames.NAME)) {
			parameters.put(ViewTypeParameterNames.NAME, requestParameters.get(ViewTypeParameterNames.NAME));
		}
		else {
			parameters.put(ViewTypeParameterNames.NAME, UifConstants.DEFAULT_VIEW_NAME);
		}

		if (requestParameters.containsKey(ViewTypeParameterNames.DATA_OBJECT_CLASS_NAME)) {
			parameters.put(ViewTypeParameterNames.DATA_OBJECT_CLASS_NAME,
					requestParameters.get(ViewTypeParameterNames.DATA_OBJECT_CLASS_NAME));
		}

		return parameters;
	}

}
