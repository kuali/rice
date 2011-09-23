package org.kuali.rice.kew.mail.service.impl;

import org.kuali.rice.kew.api.action.ActionItem;
import org.kuali.rice.kew.api.mail.ImmediateEmailReminderService;
import org.kuali.rice.kew.mail.service.ActionListEmailService;
import org.kuali.rice.kew.service.KEWServiceLocator;

/**
 * Implementation of the {@link ImmediateEmailReminderService}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ImmediateEmailReminderServiceImpl implements ImmediateEmailReminderService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ImmediateEmailReminderServiceImpl.class);

    private ActionListEmailService actionListEmailService;

    public void sendReminder(ActionItem actionItem, Boolean skipOnApprovals) {
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
