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

package org.kuali.rice.ksb.impl.registry;

import java.io.Serializable

import javax.persistence.Column
import javax.persistence.Version
import javax.xml.namespace.QName

import org.kuali.rice.ksb.api.registry.ServiceEndpointStatus
import org.kuali.rice.ksb.api.registry.ServiceInfo
import org.kuali.rice.ksb.api.registry.ServiceInfoContract

/**
 * Model bean that represents the definition of a service on the bus.
 * 
 * @see ServiceDefinition
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
//@Entity
//@Table(name="KRSB_SVC_DEF_T")
//@NamedQueries([
//	@NamedQuery(name="ServiceInfo.FetchAll", query="select s from ServiceInfo s"),
//	@NamedQuery(name="ServiceInfo.FetchAllActive",query="select s from ServiceInfo s where s.alive = true"),
//	@NamedQuery(name="ServiceInfo.FetchActiveByName",query="select s from ServiceInfo s where s.alive = true AND s.serviceName LIKE :serviceName"),
//	@NamedQuery(name="ServiceInfo.FindLocallyPublishedServices",query="select s from ServiceInfo s where s.serverIp = :serverIp AND s.serviceNamespace = :serviceNamespace"),
//	@NamedQuery(name="ServiceInfo.DeleteLocallyPublishedServices",query="delete from ServiceInfo s WHERE s.serverIp = :serverIp AND s.serviceNamespace = :serviceNamespace"),
//	@NamedQuery(name="ServiceInfo.DeleteByEntry",query="delete from ServiceInfo s where s.messageEntryId = :messageEntryId")			
//])
public class ServiceInfoBo implements ServiceInfoContract, Serializable {
 
	private static final long serialVersionUID = -4244884858494208070L;

	// TODO for some reason groovy won't compile this so commenting out for now...
//	@Id
//	@GeneratedValue(generator="KRSB_SVC_DEF_S")
//	@GenericGenerator(name="KRSB_SVC_DEF_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters=[
//			@Parameter(name="sequence_name",value="KRSB_SVC_DEF_S"),
//			@Parameter(name="value_column",value="id")
//	])
	@Column(name="SVC_DEF_ID")  
	String serviceId;
	
	@Column(name="SVC_NM")
	String serviceName;
    
	@Column(name="SVC_URL", length=500)
	String endpointUrl;
	
	@Column(name="INSTN_ID")
	String instanceId;

	@Column(name="APPL_NMSPC")
	String applicationNamespace;

	@Column(name="SRVR_IP")
	String serverIpAddress;
	
	@Column(name="TYP_CD")
	String type;
	
	@Column(name="SVC_VER")
	String serviceVersion;
	
	@Column(name="STAT_CD")
	String statusCode;
	
	@Column(name="SVC_DSCRPTR_ID")
	String serviceDescriptorId;
	
	@Column(name="CHKSM", length=30)
	String checksum;
	
	@Version
	@Column(name="VER_NBR")
	Long versionNumber;	
	
	@Override
	public QName getServiceName() {
		if (this.serviceName == null) {
			return null;
		}
		return QName.valueOf(this.serviceName);
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
		bo.applicationNamespace = im.getApplicationNamespace();
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
