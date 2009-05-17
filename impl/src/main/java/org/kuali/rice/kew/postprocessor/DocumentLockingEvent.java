package org.kuali.rice.kew.postprocessor;


/**
 * Event sent to the postprocessor when document locking ids are requested.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentLockingEvent implements IDocumentEvent {

	private static final long serialVersionUID = 1L;
	private Long routeHeaderId;
	private String appDocId;

	public DocumentLockingEvent(Long routeHeaderId, String appDocId) {
		this.routeHeaderId = routeHeaderId;
		this.appDocId = appDocId;
	}
	
	public Long getRouteHeaderId() {
		return routeHeaderId;
	}

	public String getAppDocId() {
		return appDocId;
	}
	
    public String getDocumentEventCode() {
        return IDocumentEvent.AFTER_PROCESS;
    }

}
