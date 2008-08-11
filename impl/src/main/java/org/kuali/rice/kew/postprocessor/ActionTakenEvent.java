package org.kuali.rice.kew.postprocessor;

import org.kuali.rice.kew.actiontaken.ActionTakenValue;

/**
 * Event sent to the postprocessor when an action has been taken
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionTakenEvent implements IDocumentEvent {

	private static final long serialVersionUID = 2945081851810845704L;
	private Long routeHeaderId;
	private String appDocId;
	private ActionTakenValue actionTaken;

	public ActionTakenEvent(Long routeHeaderId, String appDocId, ActionTakenValue actionTaken) {
		this.routeHeaderId = routeHeaderId;
		this.appDocId = appDocId;
		this.actionTaken = actionTaken;
	}

	public String getDocumentEventCode() {
		return ACTION_TAKEN;
	}

	public Long getRouteHeaderId() {
		return routeHeaderId;
	}

	public ActionTakenValue getActionTaken() {
		return actionTaken;
	}

	public String getAppDocId() {
		return appDocId;
	}

}
