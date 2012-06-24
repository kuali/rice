/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kew.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * A filter which prevents phishing in the Rice portal by passing in arbitrary
 * "channelUrl" query parameters.
 * 
 * This filter should be mapped to the Rice portal url only!
 * 
 * @author ewestfal
 */
public class PortalPhishingFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(PortalPhishingFilter.class);
	private static final String URL_PATTERN = "^(https?|http|ftp)://.+$";
	private static final String CHANNEL_URL_PARAM = "channelUrl";
	
	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		detectPhishing(req);
		chain.doFilter(req, res);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	protected void detectPhishing(ServletRequest request) {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			String channelUrl = httpRequest.getParameter(CHANNEL_URL_PARAM);
			if (!StringUtils.isBlank(channelUrl) && channelUrl.matches(URL_PATTERN)) {
				String applicationUrl = ConfigContext.getCurrentContextConfig().getProperties().getProperty(KRADConstants.APPLICATION_URL_KEY);
				if (StringUtils.isBlank(applicationUrl)) {
					// this should never happen if the system is configured properly
					throw new RiceRuntimeException("applicationUrl was blank");
				}
				if (!channelUrl.toUpperCase().startsWith(applicationUrl.toUpperCase())) {
					LOG.warn("Phishing attempt encountered, given channel URL was invalid: " + channelUrl);
					throw new RiceRuntimeException("Attempting to embedd a url in the portal which is not allowed.  Given url was:\n" + channelUrl + "\nHowever, channelUrl parameter must start with:\n" + applicationUrl);
				}
				
			}
		}
	}
	
}
