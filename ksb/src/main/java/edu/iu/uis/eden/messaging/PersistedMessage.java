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
package edu.iu.uis.eden.messaging;

import java.io.Serializable;
import java.sql.Timestamp;

import org.kuali.bus.services.KSBServiceLocator;

/**
 * A message which has been persisted to the data store.
 *
 * @author rkirkend
 */
public class PersistedMessage implements Serializable {

	private static final long serialVersionUID = -7047766894738304195L;

	private Long routeQueueId;
	private Integer queuePriority;
	private String queueStatus;
	private Timestamp queueDate;
	private Timestamp expirationDate;
	private Integer retryCount;
	private Integer lockVerNbr;
    private String ipNumber;
    private String serviceName;
    private String messageEntity;
    private String methodName;
    private AsynchronousCall methodCall;
    private PersistedMassagePayload payload;
    private String value1;
    private String value2;
    
    public PersistedMessage() {
        // default constructor
    }
    
	public String getMessageEntity() {
		return this.messageEntity;
	}

	public void setMessageEntity(String messageEntity) {
		this.messageEntity = messageEntity;
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

    public PersistedMassagePayload getPayload() {
	if (this.payload == null) {
	    if (this.getRouteQueueId() == null) {
		return null;
}	    this.payload = KSBServiceLocator.getRouteQueueService().findByPersistedMessageByRouteQueueId(this.getRouteQueueId()); 
	}
        return this.payload;
    }

    public void setPayload(PersistedMassagePayload payload) {
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