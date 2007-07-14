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
package edu.iu.uis.eden.messaging;

import java.io.Serializable;

import javax.xml.namespace.QName;

import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.core.Core;
import org.kuali.rice.util.RiceUtilities;

/**
 * Model bean that represents the definition of a service on the bus.
 * 
 * @see ServiceDefinition
 *
 * @author rkirkend
 */
public class ServiceInfo implements Serializable {
	
	private static final long serialVersionUID = -4244884858494208070L;
	private ServiceDefinition serviceDefinition;
	private Long messageEntryId;
	private QName qname;
	private String endpointUrl;
	private String serializedMessageEntity;
	private String serviceName;
	private Boolean alive = true; 
	private String messageEntity;
	private String serverIp;
	private Integer lockVerNbr;
	
	public ServiceInfo() {
	    // default constructor with nothing to do
	}
	
	public ServiceInfo(ServiceDefinition serviceDefinition) {
		this.setServiceDefinition(serviceDefinition);
		this.setQname(serviceDefinition.getServiceName());
		this.setMessageEntity(Core.getCurrentContextConfig().getMessageEntity());
		this.setServerIp(RiceUtilities.getIpNumber());
		this.setEndpointUrl(serviceDefinition.getServiceEndPoint().toString());
		this.setServiceName(this.getQname().toString());
	}

	public Long getMessageEntryId() {
		return this.messageEntryId;
	}

	public void setMessageEntryId(Long messageEntryId) {
		this.messageEntryId = messageEntryId;
	}

	public ServiceDefinition getServiceDefinition() {
		if (this.serviceDefinition == null && this.serializedMessageEntity != null) {
		    this.serviceDefinition = (ServiceDefinition)KSBServiceLocator.getMessageHelper().deserializeObject(this.serializedMessageEntity);
		}
		return this.serviceDefinition;
	}

	public void setServiceDefinition(ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	public QName getQname() {
		if (this.qname == null) {
		    this.qname = QName.valueOf(this.getServiceName());
		}
		return this.qname;
	}

	public void setQname(QName serviceName) {
		this.qname = serviceName;
	}
	public String getEndpointUrl() {
		return this.endpointUrl;
	}
	public void setEndpointUrl(String ipNumber) {
		this.endpointUrl = ipNumber;
	}
	public Integer getLockVerNbr() {
		return this.lockVerNbr;
	}
	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
	public String getMessageEntity() {
		return this.messageEntity;
	}

	public void setMessageEntity(String messageEntity) {
		this.messageEntity = messageEntity;
	}
	public String getServerIp() {
		return this.serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public Boolean getAlive() {
		return this.alive;
	}

	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	public void setSerializedMessageEntity(String serializedMessageEntity) {
		this.serializedMessageEntity = serializedMessageEntity;
	}

	public String getSerializedMessageEntity() {
		return this.serializedMessageEntity;
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	
}