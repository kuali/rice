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
import org.kuali.rice.kns.uif.UifParameters;
import org.kuali.rice.kns.uif.container.InquiryView;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.service.ViewTypeService;

/**
 * Type service implementation for Inquiry views
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryViewTypeServiceImpl implements ViewTypeService {

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewTypeService#getViewTypeName()
	 */
	public String getViewTypeName() {
		return ViewType.INQUIRY;
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewTypeService#getParametersFromView(org.kuali.rice.kns.uif.container.View)
	 */
	public Map<String, String> getParametersFromView(View view) {
		Map<String, String> parameters = new HashMap<String, String>();

		InquiryView inquiryView = (InquiryView) view;

		parameters.put(UifParameters.VIEW_NAME, inquiryView.getViewName());
		parameters.put(UifParameters.DATA_OBJECT_CLASS_NAME, inquiryView.getDataObjectClassName().getName());

		return parameters;
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.ViewTypeService#getParametersFromRequest(java.util.Map)
	 */
	public Map<String, String> getParametersFromRequest(Map<String, String> requestParameters) {
		Map<String, String> parameters = new HashMap<String, String>();

		if (requestParameters.containsKey(UifParameters.VIEW_NAME)) {
			parameters.put(UifParameters.VIEW_NAME, requestParameters.get(UifParameters.VIEW_NAME));
		}
		else {
			parameters.put(UifParameters.VIEW_NAME, UifConstants.DEFAULT_VIEW_NAME);
		}

		if (requestParameters.containsKey(UifParameters.DATA_OBJECT_CLASS_NAME)) {
			parameters.put(UifParameters.DATA_OBJECT_CLASS_NAME,
					requestParameters.get(UifParameters.DATA_OBJECT_CLASS_NAME));
		}
		else {
			throw new RuntimeException("Parameter '" + UifParameters.DATA_OBJECT_CLASS_NAME
					+ "' must be given to find views of type: " + getViewTypeName());
		}

		return parameters;
	}

}
