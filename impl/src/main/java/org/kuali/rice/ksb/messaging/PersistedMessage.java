/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.ksb.messaging;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * A message which has been persisted to the data store.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRSB_MSG_QUE_T")
@Sequence(name="KRSB_MSG_QUE_S", property="routeQueueId")
@NamedQueries({
  @NamedQuery(name="PersistedMessage.FindAll", query="select pm from PersistedMessage pm"),
  @NamedQuery(name="PersistedMessage.FindByServiceName", query="select pm from PersistedMessage pm where pm.serviceName = :serviceName and pm.methodName = :methodName"),
  @NamedQuery(name="PersistedMessage.GetNextDocuments", query="select pm from PersistedMessage pm where pm.serviceNamespace = :serviceNamespace and pm.queueStatus <> :queueStatus and pm.ipNumber = :ipNumber order by queuePriority asc, routeQueueId asc, queueDate asc")
})
public class PersistedMessage implements Serializable {

	private static final long serialVersionUID = -7047766894738304195L;

	@Id
	@Column(name="MSG_QUE_ID")
	private Long routeQueueId;
	@Column(name="PRIO")
	private Integer queuePriority;
	@Column(name="STAT_CD")
	private String queueStatus;
	@Column(name="DT")
	private Timestamp queueDate;
	@Column(name="EXP_DT")
	private Timestamp expirationDate;
	@Column(name="RTRY_CNT")
	private Integer retryCount;
	@Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
    @Column(name="IP_NBR")
	private String ipNumber;
    @Column(name="SVC_NM")
	private String serviceName;
    @Column(name="SVC_NMSPC")
	private String serviceNamespace;
    @Column(name="SVC_MTHD_NM")
	private String methodName;
    @Transient
    private AsynchronousCall methodCall;
    @Transient
    private PersistedMessagePayload payload;
    @Column(name="APP_VAL_ONE")
	private String value1;
    @Column(name="APP_VAL_TWO")
	private String value2;
    
    public PersistedMessage() {
        // default constructor
    }
    
    @PrePersist
    public void beforeInsert(){
        OrmUtils.populateAutoIncValue(this, KSBServiceLocator.getMessageEntityManagerFactory().createEntityManager());
    }
    
	public String getServiceNamespace() {
		return this.serviceNamespace;
	}

	public void setServiceNamespace(String ServiceNamespace) {
		this.serviceNamespace = ServiceNamespace;
	}

    public String getIpNumber() {
        return this.ipNumber;
    }
    
    public void setIpNumber(String ipNumber) {
        this.ipNumber = ipNumber;
    }
    
	public Timestamp getQueueDate() {
		return this.queueDate;
	}

	public Integer getQueuePriority() {
		return this.queuePriority;
	}

	public String getQueueStatus() {
		return this.queueStatus;
	}

	public Integer getRetryCount() {
		return this.retryCount;
	}


	public void setQueueDate(Timestamp timestamp) {
	    this.queueDate = timestamp;
	}

	public void setQueuePriority(Integer integer) {
	    this.queuePriority = integer;
	}

	public void setQueueStatus(String string) {
	    this.queueStatus = string;
	}

	public void setRetryCount(Integer integer) {
	    this.retryCount = integer;
	}


    public Integer getLockVerNbr() {
        return this.lockVerNbr;
    }
    
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }
    
    public Long getRouteQueueId() {
        return this.routeQueueId;
    }
    
    public void setRouteQueueId(Long queueSequence) {
        this.routeQueueId = queueSequence;
    }
    
	public String getServiceName() {
		return this.serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

    public String toString() {
	return "[RouteQueue: " + ", routeQueueId=" + this.routeQueueId + ", ipNumber=" + this.ipNumber + ", serviceName="
		+ this.serviceName + ", queueStatus=" + this.queueStatus + ", queuePriority=" + this.queuePriority
		+ ", queueDate=" + this.queueDate + "]";
    }

	public AsynchronousCall getMethodCall() {
		return this.methodCall;
	}

	public void setMethodCall(AsynchronousCall methodCall) {
		this.methodCall = methodCall;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Timestamp getExpirationDate() {
		return this.expirationDate;
	}

	public void setExpirationDate(Timestamp expirationDate) {
		this.expirationDate = expirationDate;
	}

    public PersistedMessagePayload getPayload() {
	if (this.payload == null) {
	    if (this.getRouteQueueId() == null) {
		return null;
}	    this.payload = KSBServiceLocator.getRouteQueueService().findByPersistedMessageByRouteQueueId(this.getRouteQueueId()); 
	}
        return this.payload;
    }

    public void setPayload(PersistedMessagePayload payload) {
        this.payload = payload;
    }

    public String getValue1() {
        return this.value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return this.value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

}
