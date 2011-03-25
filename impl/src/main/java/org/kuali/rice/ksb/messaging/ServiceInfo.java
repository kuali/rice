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

package org.kuali.rice.ksb.messaging;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.ksb.service.KSBServiceLocator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * Model bean that represents the definition of a service on the bus.
 * 
 * @see ServiceDefinition
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRSB_SVC_DEF_T")
//@Sequence(name="KRSB_SVC_DEF_S", property="messageEntryId")
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
	@GeneratedValue(generator="KRSB_SVC_DEF_S")
	@GenericGenerator(name="KRSB_SVC_DEF_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KRSB_SVC_DEF_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="SVC_DEF_ID")  
	private Long messageEntryId;
    @Transient
	private QName qname;
	@Column(name="SVC_URL", length=500)
	private String endpointUrl;
    @Transient
	private String endpointAlternateUrl;
    @Column(name="FLT_SVC_DEF_ID")
    private Long flattenedServiceDefinitionId;
    @Transient
    private FlattenedServiceDefinition serializedServiceNamespace;
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
	@Column(name="SVC_DEF_CHKSM", length=30)
	private String checksum;
	
	@Transient
	private transient ClassLoader serviceClassLoader;
	
	public ServiceInfo() {
	    // default constructor with nothing to do
	}
	
	//@PrePersist
    public void beforeInsert(){
        OrmUtils.populateAutoIncValue(this, KSBServiceLocator.getRegistryEntityManagerFactory().createEntityManager());
    }
	
	public ServiceInfo(ServiceDefinition serviceDefinition, String serverIp, String checksum) {
		this.setServiceDefinition(serviceDefinition);
		this.setQname(serviceDefinition.getServiceName());
		this.setServiceNamespace(ConfigContext.getCurrentContextConfig().getServiceNamespace());
		this.setServerIp(serverIp);
		this.setEndpointUrl(serviceDefinition.getServiceEndPoint().toString());
		this.setServiceName(this.getQname().toString());
		this.setServiceClassLoader(serviceDefinition.getServiceClassLoader());
		this.setChecksum((checksum != null) ? checksum : this.objectToChecksum(serviceDefinition));
	}
	
	public ServiceInfo(ServiceDefinition serviceDefinition) {
		this.setServiceDefinition(serviceDefinition);
		this.setQname(serviceDefinition.getServiceName());
		this.setServiceNamespace(ConfigContext.getCurrentContextConfig().getServiceNamespace());
		this.setServerIp(RiceUtilities.getIpNumber());
		this.setEndpointUrl(serviceDefinition.getServiceEndPoint().toString());
		this.setServiceName(this.getQname().toString());
		this.setServiceClassLoader(serviceDefinition.getServiceClassLoader());
		this.setChecksum(this.objectToChecksum(serviceDefinition));
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
		if (this.serviceDefinition == null) {
		    this.serviceDefinition = (ServiceDefinition)
		    		(enMessageHelper==null
		    				?KSBServiceLocator.getMessageHelper().deserializeObject(this.getSerializedServiceNamespace().getFlattenedServiceDefinitionData())
		    						:enMessageHelper.deserializeObject(this.getSerializedServiceNamespace().getFlattenedServiceDefinitionData()));
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
	public void setSerializedServiceNamespace(FlattenedServiceDefinition serializedServiceNamespace) {
		this.serializedServiceNamespace = serializedServiceNamespace;
	}

	public FlattenedServiceDefinition getSerializedServiceNamespace() {
		if (this.serializedServiceNamespace == null && this.getFlattenedServiceDefinitionId() != null) {
			this.serializedServiceNamespace = KSBServiceLocator.getServiceRegistry().getFlattenedServiceDefinition(this.getFlattenedServiceDefinitionId());
		}
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

	public String getChecksum() {
		return this.checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public Long getFlattenedServiceDefinitionId() {
		return this.flattenedServiceDefinitionId;
	}

	public void setFlattenedServiceDefinitionId(Long flattenedServiceDefinitionId) {
		this.flattenedServiceDefinitionId = flattenedServiceDefinitionId;
	}
	
	/**
	 * Creates a checksum for the given serializable object (usually a ServiceDefinition in this case).
	 * 
	 * @param object The object (possibly a ServiceDefinition) to serialize.
	 * @return A checksum for the object.
	 */
	public String objectToChecksum(Serializable object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
        } catch (IOException e) {
            throw new RiceRuntimeException(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {}
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return new String( Base64.encodeBase64( md.digest( bos.toByteArray() ) ), "UTF-8");
        } catch( GeneralSecurityException ex ) {
        	throw new RiceRuntimeException(ex);
        } catch( UnsupportedEncodingException ex ) {
        	throw new RiceRuntimeException(ex);
        }
	}
}
