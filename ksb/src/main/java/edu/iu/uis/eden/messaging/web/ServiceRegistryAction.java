/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.messaging.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.util.RiceUtilities;

import edu.iu.uis.eden.messaging.RemoteResourceServiceLocator;
import edu.iu.uis.eden.messaging.RemotedServiceHolder;
import edu.iu.uis.eden.messaging.RemotedServiceRegistry;
import edu.iu.uis.eden.messaging.ServerSideRemotedServiceHolder;
import edu.iu.uis.eden.messaging.ServiceInfo;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

/**
 * Struts action for interacting with the queue of messages.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ServiceRegistryAction extends KSBAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	return mapping.findForward("basic");
    }

    public ActionForward refreshServiceRegistry(ActionMapping mapping, ActionForm form, HttpServletRequest request,
    	HttpServletResponse response) throws IOException, ServletException {
	// TODO is this what really constitutes a "refresh" of the service registry?
	KSBServiceLocator.getServiceDeployer().refresh();
	KSBResourceLoaderFactory.getRemoteResourceLocator().refresh();
	return mapping.findForward("basic");
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm actionForm) throws Exception {
	ServiceRegistryForm form = (ServiceRegistryForm)actionForm;
	form.setMyIpAddress(RiceUtilities.getIpNumber());
	form.setMyMessageEntity(Core.getCurrentContextConfig().getProperty(RiceConstants.MESSAGE_ENTITY));
	form.setDevMode(Core.getCurrentContextConfig().getDevMode());
	RemotedServiceRegistry registry = KSBServiceLocator.getServiceDeployer();
	RemoteResourceServiceLocator remoteLocator = KSBResourceLoaderFactory.getRemoteResourceLocator();
	form.setPublishedServices(getPublishedServices(registry));
	form.setPublishedTempServices(getPublishedTempServices(registry));
	form.setGlobalRegistryServices(getGlobalRegistryServices(remoteLocator));

	return null;
    }

    private List<ServiceInfo> getPublishedServices(RemotedServiceRegistry registry) {
	Map<QName, ServerSideRemotedServiceHolder> publishedServiceHolders = registry.getPublishedServices();
	List<ServiceInfo> publishedServices = new ArrayList<ServiceInfo>();
	for (ServerSideRemotedServiceHolder holder : publishedServiceHolders.values()) {
	    publishedServices.add(holder.getServiceInfo());
	}
	return publishedServices;
    }

    private List<ServiceInfo> getPublishedTempServices(RemotedServiceRegistry registry) {
	Map<QName, ServerSideRemotedServiceHolder> publishedTempServiceHolders = registry.getPublishedTempServices();
	List<ServiceInfo> publishedTempServices = new ArrayList<ServiceInfo>();
	for (ServerSideRemotedServiceHolder holder : publishedTempServiceHolders.values()) {
	    publishedTempServices.add(holder.getServiceInfo());
	}
	return publishedTempServices;
    }

    private List<ServiceInfo> getGlobalRegistryServices(RemoteResourceServiceLocator remoteLocator) {
	Map<QName, List<RemotedServiceHolder>> clients = remoteLocator.getClients();
	List<ServiceInfo> globalRegistryServices = new ArrayList<ServiceInfo>();
	for (List<RemotedServiceHolder> client : clients.values()) {
	    for (RemotedServiceHolder holder : client) {
		globalRegistryServices.add(holder.getServiceInfo());
	    }
	}
	return globalRegistryServices;
    }

}
