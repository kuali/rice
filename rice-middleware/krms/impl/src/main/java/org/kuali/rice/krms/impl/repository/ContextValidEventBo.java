package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.core.api.mo.common.Versioned;

import java.io.Serializable;

public class ContextValidEventBo implements Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    private String id;
    private String contextId;
    private String eventName;
    private Long versionNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
