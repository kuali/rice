package org.kuali.rice.kew.postprocessor;


/**
 * Event sent to the postprocessor when the processor is started
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BeforeProcessEvent implements IDocumentEvent {

	private static final long serialVersionUID = 2945081851810845704L;
	private Long routeHeaderId;
	private Long nodeInstanceId;
	private String appDocId;

	public BeforeProcessEvent(Long routeHeaderId, String appDocId, Long nodeInstanceId) {
		this.routeHeaderId = routeHeaderId;
		this.appDocId = appDocId;
		this.nodeInstanceId = nodeInstanceId;
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

    public String getDocumentEventCode() {
        return IDocumentEvent.BEFORE_PROCESS;
    }

}
