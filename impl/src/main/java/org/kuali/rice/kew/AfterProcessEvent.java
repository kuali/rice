package org.kuali.rice.kew;

/**
 * Event sent to the postprocessor when the processor is ended
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AfterProcessEvent implements IDocumentEvent {

	private static final long serialVersionUID = 2945081851810845704L;
	private Long routeHeaderId;
	private Long nodeInstanceId;
	private String appDocId;
	private boolean successfullyProcessed;

	public AfterProcessEvent(Long routeHeaderId, String appDocId, Long nodeInstanceId, boolean successfullyProcessed) {
		this.routeHeaderId = routeHeaderId;
		this.appDocId = appDocId;
		this.nodeInstanceId = nodeInstanceId;
		this.successfullyProcessed = successfullyProcessed;
	}
	
	public Long getNodeInstanceId() {
	    return nodeInstanceId;
	}

	public Long getRouteHeaderId() {
		return routeHeaderId;
	}

	public String getAppDocId() {
		return appDocId;
	}
	
    public boolean isSuccessfullyProcessed() {
        return this.successfullyProcessed;
    }

    public String getDocumentEventCode() {
        return IDocumentEvent.AFTER_PROCESS;
    }

}
