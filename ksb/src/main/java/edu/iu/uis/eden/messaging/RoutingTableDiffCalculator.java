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
package edu.iu.uis.eden.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kuali.rice.exceptions.RiceRuntimeException;

/**
 * Takes two lists of ServiceInfo objects.  One from the service def table and one from a piece 
 * of code and diffs the two.
 * 
 * 
 * @author rkirkend
 *
 */
public class RoutingTableDiffCalculator {
	
	private List<ServiceInfo> servicesNeedUpdated = new ArrayList<ServiceInfo>();
	private List<ServiceInfo> servicesNeedRemoved = new ArrayList<ServiceInfo>();
	private List<ServiceInfo> masterServiceList = new ArrayList<ServiceInfo>();

	public boolean calculateClientSideUpdate(Map<QName, List<RemotedServiceHolder>> clients, List<ServiceInfo> fetchedServiceInfos) {
		List<ServiceInfo> clientServiceList = deconstructRemoteServiceLocatorClientMap(clients);
		if (clientServiceList.isEmpty() && ! fetchedServiceInfos.isEmpty()) {
			return true;
		}
		Map<String, ServiceInfo> configuredServices = getRemotedService(clientServiceList);
		Map<String, ServiceInfo> deployedServices = getRemotedService(fetchedServiceInfos);
		for (Map.Entry<String, ServiceInfo> infoEntry : configuredServices.entrySet()) {
			if (deployedServices.containsKey(infoEntry.getKey())) {
				ServiceInfo deployedServiceInfo = deployedServices.get(infoEntry.getKey());
				if (! isSame(infoEntry.getValue(), deployedServiceInfo)) {
					return true;
				}
			} else {
				return true;
			}
		}
		
		//iterate the bus services and make sure the configured services represent the fetched ones
		for (Map.Entry<String, ServiceInfo> info : deployedServices.entrySet()) {
			if (! configuredServices.containsKey(info.getKey())) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<ServiceInfo> deconstructRemoteServiceLocatorClientMap(Map<QName, List<RemotedServiceHolder>> clients) {
		List<ServiceInfo> clientServices = new ArrayList<ServiceInfo>();
		for (List<RemotedServiceHolder> remoteServiceHolders : clients.values()) {
			for (RemotedServiceHolder holder : remoteServiceHolders) {
				clientServices.add(holder.getServiceInfo());
			}
		}
		return clientServices;
	}
	
	public boolean calculateServerSideUpdateLists(List<ServiceInfo> memoryServiceInfos, List<ServiceInfo> fetchedServiceInfos) {
		Map<String, ServiceInfo> configuredServices = getRemotedService(memoryServiceInfos);
		Map<String, ServiceInfo> deployedServices = getRemotedService(fetchedServiceInfos);
		
		//iterate the configured services vs. the deployed services
		for (Map.Entry<String, ServiceInfo> infoEntry : configuredServices.entrySet()) {
			if (deployedServices.containsKey(infoEntry.getKey())) {
				ServiceInfo deployedServiceInfo = deployedServices.get(infoEntry.getKey());
				if (! isSame(infoEntry.getValue(), deployedServiceInfo)) {
				    this.servicesNeedUpdated.add(deployedServiceInfo);
				}
				updateDeployedServiceInfo(infoEntry.getValue(), deployedServiceInfo);
				this.masterServiceList.add(deployedServiceInfo);	
			} else {
			    this.servicesNeedUpdated.add(infoEntry.getValue());
			    this.masterServiceList.add(infoEntry.getValue());
			}
		}
		
		//compare fetched vs. configured and determine if any fetched services need removed
		for (Map.Entry<String, ServiceInfo> infoEntry : deployedServices.entrySet()) {
			if (! configuredServices.containsKey(infoEntry.getKey())) {
			    this.servicesNeedRemoved.add(infoEntry.getValue());
			}
		}
		
		return ! (this.servicesNeedRemoved.isEmpty() && this.servicesNeedUpdated.isEmpty());
	}
	
	public Map<String, ServiceInfo> getRemotedService(List<ServiceInfo> serviceInfos) {
		Map<String, ServiceInfo> serviceMap = new HashMap<String, ServiceInfo>();
		for (ServiceInfo info : serviceInfos) {
			String endpointURL = info.getEndpointUrl();
			if (serviceMap.containsKey(endpointURL)) {
				throw new RiceRuntimeException("Multiple services with same endpoint url declared and saved in routing table.  " +
						"Stopping service registration.  Endpoint " + endpointURL);
			}
			
			serviceMap.put(endpointURL, info);
		}
		return serviceMap;
	}
	
	private void updateDeployedServiceInfo(ServiceInfo configuredServiceInfo, ServiceInfo deployedServiceInfo) {
		deployedServiceInfo.setAlive(configuredServiceInfo.getAlive());
		deployedServiceInfo.setQname(configuredServiceInfo.getQname());
		deployedServiceInfo.setMessageEntity(configuredServiceInfo.getMessageEntity());
		deployedServiceInfo.setServerIp(configuredServiceInfo.getServerIp());
		deployedServiceInfo.setServiceDefinition(configuredServiceInfo.getServiceDefinition());
	}
	
	private boolean isSame(ServiceInfo configured, ServiceInfo deployed) {
		return configured.getAlive().equals(deployed.getAlive()) &&  
				configured.getQname().equals(deployed.getQname()) &&
				configured.getServerIp().equals(deployed.getServerIp()) && 
				configured.getMessageEntity().equals(deployed.getMessageEntity()) &&
				configured.getServiceDefinition().isSame(deployed.getServiceDefinition());
	}

	public List<ServiceInfo> getServicesNeedRemoved() {
		return this.servicesNeedRemoved;
	}

	public void setServicesNeedRemoved(List<ServiceInfo> servicesNeedRemoved) {
		this.servicesNeedRemoved = servicesNeedRemoved;
	}

	public List<ServiceInfo> getServicesNeedUpdated() {
		return this.servicesNeedUpdated;
	}

	public void setServicesNeedUpdated(List<ServiceInfo> servicesNeedUpdated) {
		this.servicesNeedUpdated = servicesNeedUpdated;
	}

	public List<ServiceInfo> getMasterServiceList() {
		return this.masterServiceList;
	}

	public void setMasterServiceList(List<ServiceInfo> masterServiceList) {
		this.masterServiceList = masterServiceList;
	}
}