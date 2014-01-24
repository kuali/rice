/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.ksb.impl.registry;

import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.ksb.api.registry.ServiceEndpointStatus;
import org.kuali.rice.ksb.api.registry.ServiceInfo;
import org.kuali.rice.ksb.api.registry.ServiceInfoContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * Model bean that represents the definition of a service on the bus.
 * 
 * @see ServiceInfo
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

@Entity
@Table(name="KRSB_SVC_DEF_T")
public class ServiceInfoBo implements ServiceInfoContract, Serializable {
 
	private static final long serialVersionUID = -4244884858494208070L;

    @Id
    @GeneratedValue(generator = "KRSB_SVC_DEF_S")
    @PortableSequenceGenerator(name = "KRSB_SVC_DEF_S")
	@Column(name = "SVC_DEF_ID")
	private String serviceId;
	
	@Column(name = "SVC_NM")
	private String serviceName;
    
	@Column(name = "SVC_URL", length = 500)
	private String endpointUrl;
	
	@Column(name = "INSTN_ID")
	private String instanceId;

	@Column(name = "APPL_ID")
	private String applicationId;

	@Column(name = "SRVR_IP")
	private String serverIpAddress;
	
	@Column(name = "TYP_CD")
	private String type;
	
	@Column(name = "SVC_VER")
	private String serviceVersion;
	
	@Column(name = "STAT_CD")
	private String statusCode;
	
	@Column(name = "SVC_DSCRPTR_ID")
	private String serviceDescriptorId;
	
	@Column(name = "CHKSM", length = 30)
	private String checksum;

    @Deprecated
	@Column(name = "VER_NBR")
	private Long versionNumber;

    public ServiceInfoBo() {
        this.versionNumber = Long.valueOf(1);
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public QName getServiceName() {
        if (this.serviceName == null) {
            return null;
        }
        return QName.valueOf(this.serviceName);
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getServiceDescriptorId() {
        return serviceDescriptorId;
    }

    public void setServiceDescriptorId(String serviceDescriptorId) {
        this.serviceDescriptorId = serviceDescriptorId;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    /**
     * Version number is deprecated, so this method does nothing.
     *
     * @deprecated version number is no longer used
     */
    @Deprecated
    public void setVersionNumber(Long versionNumber) {
        // no longer does anything
    }

    @Override
	public ServiceEndpointStatus getStatus() {
		if (getStatusCode() == null) {
			return null;
		}
		return ServiceEndpointStatus.fromCode(getStatusCode());
	}

	static ServiceInfo to(ServiceInfoBo bo) {
		if (bo == null) {
			return null;
		}
		return ServiceInfo.Builder.create(bo).build();
	}
	
	static ServiceInfoBo from(ServiceInfo im) {
		if (im == null) {
			return null;
		}
		ServiceInfoBo bo = new ServiceInfoBo();
		bo.serviceId = im.getServiceId();
		bo.serviceName = im.getServiceName().toString();
		bo.endpointUrl = im.getEndpointUrl();
		bo.instanceId = im.getInstanceId();
		bo.applicationId = im.getApplicationId();
		bo.serverIpAddress = im.getServerIpAddress();
		bo.type = im.getType();
		bo.serviceVersion = im.getServiceVersion();
		bo.statusCode = im.getStatus().getCode();
		bo.serviceDescriptorId = im.getServiceDescriptorId();
		bo.checksum = im.getChecksum();
		bo.versionNumber = im.getVersionNumber();
		return bo;
	}
	
	
	
	
}
