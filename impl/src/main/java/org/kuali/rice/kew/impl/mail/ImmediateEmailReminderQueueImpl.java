package org.kuali.rice.kew.impl.mail;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.action.ActionItem;
import org.kuali.rice.kew.api.mail.ImmediateEmailReminderQueue;
import org.kuali.rice.kew.mail.service.ActionListEmailService;
import org.kuali.rice.kew.service.KEWServiceLocator;

/**
 * Reference implementation of an {@code ImmediateEmailReminderQueue}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ImmediateEmailReminderQueueImpl implements ImmediateEmailReminderQueue {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ImmediateEmailReminderQueueImpl.class);

    private ActionListEmailService actionListEmailService;

    public void sendReminder(ActionItem actionItem, Boolean skipOnApprovals) {
        if (actionItem == null) {
			throw new RiceIllegalArgumentException("actionItem was null");
		}

        if (skipOnApprovals == null) {
			throw new RiceIllegalArgumentException("skipOnApprovals was null");
		}

        getActionListEmailService().sendImmediateReminder(actionItem, skipOnApprovals);
    }

    private ActionListEmailService getActionListEmailService() {
        if (actionListEmailService == null)
            actionListEmailService = (ActionListEmailService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_LIST_EMAIL_SERVICE);
    
        return actionListEmailService;
    }
    
    /**
     * @param actionListEmailService the actionListEmailService to set
     */
    public void setActionListEmailService(ActionListEmailService actionListEmailService) {
        this.actionListEmailService = actionListEmailService;
    }
    
}
