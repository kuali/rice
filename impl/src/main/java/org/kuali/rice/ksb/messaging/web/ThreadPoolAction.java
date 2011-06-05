/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.ksb.messaging.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.bus.ServiceBus;
import org.kuali.rice.ksb.api.bus.services.KsbApiServiceLocator;
import org.kuali.rice.ksb.messaging.service.BusAdminService;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.util.KSBConstants;


/**
 * Struts action for interacting with the queue of messages.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ThreadPoolAction extends KSBAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ThreadPoolAction.class);

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	return mapping.findForward("basic");
    }

    public ActionForward update(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	ThreadPoolForm form = (ThreadPoolForm)actionForm;
	if (form.getTimeIncrement() != null && form.getTimeIncrement().longValue() <= 0) {
	    form.setTimeIncrement(null);
	}
	if (form.getMaxRetryAttempts() != null && form.getMaxRetryAttempts().longValue() < 0) {
	    form.setMaxRetryAttempts(null);
	}
	if (form.isAllServers()) {
	    // if it's all servers, we need to find all of the BusAdmin services
	    QName serviceName = new QName(form.getApplicationId(), "busAdminService");
	    ServiceBus serviceBus = KsbApiServiceLocator.getServiceBus();
	    List<Endpoint> adminServices = serviceBus.getEndpoints(serviceName);
	    for (Endpoint adminEndpoint : adminServices) {
		try {
		    BusAdminService adminService = (BusAdminService)adminEndpoint.getService();
		    adminService.setCorePoolSize(form.getCorePoolSize());
		    adminService.setMaximumPoolSize(form.getMaximumPoolSize());
		    adminService.setConfigProperty(KSBConstants.Config.ROUTE_QUEUE_TIME_INCREMENT_KEY, (form.getTimeIncrement() == null ? null : form.getTimeIncrement().toString()));
		    adminService.setConfigProperty(KSBConstants.Config.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY, (form.getMaxRetryAttempts() == null ? null : form.getMaxRetryAttempts().toString()));
		} catch (Exception e) {
		    LOG.error("Failed to set thread pool sizes for busAdminService at " + adminEndpoint.getServiceConfiguration().getEndpointUrl());
		}
	    }
	} else {
	    form.getThreadPool().setCorePoolSize(form.getCorePoolSize());
	    if (form.getMaximumPoolSize() < form.getCorePoolSize()) {
		form.setMaximumPoolSize(form.getCorePoolSize());
	    }
	    form.getThreadPool().setMaximumPoolSize(form.getMaximumPoolSize());

	    if (form.getTimeIncrement() == null) {
		ConfigContext.getCurrentContextConfig().removeProperty(KSBConstants.Config.ROUTE_QUEUE_TIME_INCREMENT_KEY);
	    } else {
		ConfigContext.getCurrentContextConfig().putProperty(KSBConstants.Config.ROUTE_QUEUE_TIME_INCREMENT_KEY, form.getTimeIncrement().toString());
	    }

	    if (form.getMaxRetryAttempts() == null) {
		ConfigContext.getCurrentContextConfig().removeProperty(KSBConstants.Config.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY);
	    } else {
		ConfigContext.getCurrentContextConfig().putProperty(KSBConstants.Config.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY, form.getMaxRetryAttempts().toString());
	    }
	}
	return mapping.findForward("basic");
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm actionForm) throws Exception {
	ThreadPoolForm form = (ThreadPoolForm)actionForm;
	form.setThreadPool(KSBServiceLocator.getThreadPool());
	if (form.getCorePoolSize() == null) {
	    form.setCorePoolSize(form.getThreadPool().getCorePoolSize());
	}
	if (form.getMaximumPoolSize() == null) {
	    form.setMaximumPoolSize(form.getThreadPool().getMaximumPoolSize());
	}
	if (form.getTimeIncrement() == null) {
	    String timeIncrementValue = ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.Config.ROUTE_QUEUE_TIME_INCREMENT_KEY);
	    if (!StringUtils.isEmpty(timeIncrementValue)) {
		form.setTimeIncrement(Long.parseLong(timeIncrementValue));
	    }
	}
	if (form.getMaxRetryAttempts() == null) {
	    String maxRetryAttemptsValue = ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.Config.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY);
	    if (!StringUtils.isEmpty(maxRetryAttemptsValue)) {
		form.setMaxRetryAttempts(Long.parseLong(maxRetryAttemptsValue));
	    }
	}
	return null;
    }

}
