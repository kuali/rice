/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.web;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * A resolver for URLs for the user, user report, workgroup and workgroup
 * report screens.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UrlResolver {

	private static final UrlResolver INSTANCE = new UrlResolver();
	
	public static final String USER_URL = "user.url";  
	public static final String USER_REPORT_URL = "user.report.url";
	public static final String WORKGROUP_URL = "workgroup.url";
	public static final String WORKGROUP_REPORT_URL = "workgroup.report.url";	
	
	public static UrlResolver getInstance() {
		return INSTANCE;
	}
	
	public String getUserUrl() {
		return getUrl(USER_URL);
	}
	
	public String getUserReportUrl() {
		return getUrl(USER_REPORT_URL);
	}
	
	public String getWorkgroupUrl() {
		return getUrl(WORKGROUP_URL);
	}
	
	public String getWorkgroupReportUrl() {
		return getUrl(WORKGROUP_REPORT_URL);
	}

	protected String getUrl(String urlName) {
		String url = Core.getCurrentContextConfig().getProperty(urlName);
		if (StringUtils.isEmpty(url)) {
			throw new WorkflowRuntimeException("Could not locate the url value for '" + urlName + "'.  Please be sure to configure it properly in your workflow.xml.");
		}
		return url;
	}
	
}
