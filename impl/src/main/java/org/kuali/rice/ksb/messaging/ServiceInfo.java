/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.ksb.messaging;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * Model bean that represents the definition of a service on the bus.
 * 
 * @see ServiceDefinition
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRSB_SVC_DEF_T")
@Sequence(name="KRSB_SVC_DEF_S", property="messageEntryId")
@NamedQueries({
	@NamedQuery(name="ServiceInfo.FetchAll", query="select s from ServiceInfo s"),
	@NamedQuery(name="ServiceInfo.FetchAllActive",query="select s from ServiceInfo s where s.alive = true"),
	@NamedQuery(name="ServiceInfo.FetchActiveByName",query="select s from ServiceInfo s where s.alive = true AND s.serviceName LIKE :serviceName"),
	@NamedQuery(name="ServiceInfo.FindLocallyPublishedServices",query="select s from ServiceInfo s where s.serverIp = :serverIp AND s.serviceNamespace = :serviceNamespace"),
	@NamedQuery(name="ServiceInfo.DeleteLocallyPublishedServices",query="delete from ServiceInfo s WHERE s.serverIp = :serverIp AND s.serviceNamespace = :serviceNamespace"),
	@NamedQuery(name="ServiceInfo.DeleteByEntry",query="delete from ServiceInfo s where s.messageEntryId = :messageEntryId")			
})
public class ServiceInfo implements Serializable {
 
	private static final long serialVersionUID = -4244884858494208070L;
    @Transient
	private ServiceDefinition serviceDefinition;
	@Id
	@Column(name="SVC_DEF_ID")  
	private Long messageEntryId;
    @Transient
	private QName qname;
	@Column(name="SVC_URL", length=500)
	private String endpointUrl;
    @Transient
	private String endpointAlternateUrl;
	@Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name="SVC_DEF", length=4000)
	private String serializedServiceNamespace;
	@Column(name="SVC_NM")
	private String serviceName;
    @Column(name="SVC_ALIVE")
	private Boolean alive = true; 
	@Column(name="SVC_NMSPC")
	private String serviceNamespace;
	@Column(name="SRVR_IP")
	private String serverIp;
	@Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
	
	@Transient
	private transient ClassLoader serviceClassLoader;
	
	public ServiceInfo() {
	    // default constructor with nothing to do
	}
	
	@PrePersist
    public void beforeInsert(){
        OrmUtils.populateAutoIncValue(this, KSBServiceLocator.getRegistryEntityManagerFactory().createEntityManager());
    }
	
	public ServiceInfo(ServiceDefinition serviceDefinition) {
		this.setServiceDefinition(serviceDefinition);
		this.setQname(serviceDefinition.getServiceName());
		this.setServiceNamespace(ConfigContext.getCurrentContextConfig().getServiceNamespace());
		this.setServerIp(RiceUtilities.getIpNumber());
		this.setEndpointUrl(serviceDefinition.getServiceEndPoint().toString());
		this.setServiceName(this.getQname().toString());
		this.setServiceClassLoader(serviceDefinition.getServiceClassLoader());
	}

	public Long getMessageEntryId() {
		return this.messageEntryId;
	}

	public void setMessageEntryId(Long messageEntryId) {
		this.messageEntryId = messageEntryId;
	}

	public ServiceDefinition getServiceDefinition() {
		return getServiceDefinition(KSBServiceLocator.getMessageHelper());
	}

	public ServiceDefinition getServiceDefinition(MessageHelper enMessageHelper) {
		if (this.serviceDefinition == null && this.serializedServiceNamespace != null) {
		    this.serviceDefinition = (ServiceDefinition)
		    (enMessageHelper==null
		    		?KSBServiceLocator.getMessageHelper().deserializeObject(this.serializedServiceNamespace)
		    		:enMessageHelper.deserializeObject(this.serializedServiceNamespace));
		    this.serviceDefinition.setServiceClassLoader(getServiceClassLoader());
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
	
	/**
     * @return the endpointAlternateUrl
     */
    public String getEndpointAlternateUrl() {
        return this.endpointAlternateUrl;
    }
    
    public String getActualEndpointUrl() {
        if (!StringUtils.isBlank(getEndpointAlternateUrl())) {
            return getEndpointAlternateUrl();
        }
        return getEndpointUrl();
    }

    /**
     * @param endpointAlternateUrl the endpointAlternateUrl to set
     */
    public void setEndpointAlternateUrl(String endpointAlternateUrl) {
        this.endpointAlternateUrl = endpointAlternateUrl;
    }
    
    public ClassLoader getServiceClassLoader() {
        return this.serviceClassLoader;
    }

    public void setServiceClassLoader(ClassLoader serviceClassLoader) {
        this.serviceClassLoader = serviceClassLoader;
    }
    
    public Integer getLockVerNbr() {
		return this.lockVerNbr;
	}
	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
	public String getServiceNamespace() {
		return this.serviceNamespace;
	}

	public void setServiceNamespace(String ServiceNamespace) {
		this.serviceNamespace = ServiceNamespace;
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
	public void setSerializedServiceNamespace(String serializedServiceNamespace) {
		this.serializedServiceNamespace = serializedServiceNamespace;
	}

	public String getSerializedServiceNamespace() {
		return this.serializedServiceNamespace;
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String toString() {
	    return ReflectionToStringBuilder.toString(this);
	}
	

}
