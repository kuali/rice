package org.kuali.rice.ksb.messaging;

import org.kuali.rice.ksb.api.messaging.AsynchronousCall;

import java.io.Serializable;
import java.sql.Timestamp;

public interface PersistedMessage extends Serializable {
    String getServiceNamespace();

    String getIpNumber();

    Timestamp getQueueDate();

    Integer getQueuePriority();

    String getQueueStatus();

    Integer getRetryCount();

    Long getRouteQueueId();

    String getServiceName();

    AsynchronousCall getMethodCall();

    String getMethodName();

    Timestamp getExpirationDate();

    PersistedMessagePayload getPayload();

    String getValue1();

    String getValue2();
}
