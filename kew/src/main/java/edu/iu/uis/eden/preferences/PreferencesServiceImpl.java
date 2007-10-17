/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.preferences;

import java.util.ArrayList;
import java.util.Collection;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.useroptions.UserOptions;
import edu.iu.uis.eden.useroptions.UserOptionsService;

/**
 * An implementation of the {@link PreferencesService}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PreferencesServiceImpl implements PreferencesService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreferencesServiceImpl.class);

    private static final String DISAPPROVED_DOC_COLOR = "DOCUMENT_STATUS_COLOR_D";
    // what the hell is this status?
    private static final String DISSAPPROVED_CANCELLED_DOC_COLOR = "DOCUMENT_STATUS_COLOR_C";
    private static final String APPROVED_DOC_COLOR = "DOCUMENT_STATUS_COLOR_A";
    private static final String CANCELLED_DOC_COLOR = "DOCUMENT_STATUS_COLOR_X";
    private static final String SAVED_DOC_COLOR = "DOCUMENT_STATUS_COLOR_S";
    private static final String ENROUTE_DOC_COLOR = "DOCUMENT_STATUS_COLOR_R";
    private static final String PROCESSED_DOC_COLOR = "DOCUMENT_STATUS_COLOR_P";
    private static final String INITIATED_DOC_COLOR = "DOCUMENT_STATUS_COLOR_I";
    private static final String FINAL_DOC_COLOR = "DOCUMENT_STATUS_COLOR_F";
    private static final String EXCEPTION_DOC_COLOR = "DOCUMENT_STATUS_COLOR_E";
    private static final String REFRESH_RATE_KEY = "REFRESH_RATE";
    private static final String OPEN_NEW_WINDOW_KEY = "OPEN_ITEMS_NEW_WINDOW";
    private static final String COLUMN_DOC_TYPE_KEY = "DOC_TYPE_COL_SHOW_NEW";
    private static final String COLUMN_TITLE_KEY = "TITLE_COL_SHOW_NEW";
    private static final String COLUMN_ACTION_REQ_KEY = "ACTION_REQUESTED_COL_SHOW_NEW";
    private static final String COLUMN_INITIATOR_KEY = "INITIATOR_COL_SHOW_NEW";
    private static final String COLUMN_DELEGATOR_KEY = "DELEGATOR_COL_SHOW_NEW";
    private static final String COLUMN_DATE_CREATE_KEY = "DATE_CREATED_COL_SHOW_NEW";
    private static final String COLUMN_DOCUMENT_STATUS_KEY = "DOCUMENT_STATUS_COL_SHOW_NEW";
    private static final String COLUMN_WORKGROUP_REQUEST_KEY = "WORKGROUP_REQUEST_COL_SHOW_NEW";
    private static final String COLUMN_CLEAR_FYI_KEY = "CLEAR_FYI_COL_SHOW_NEW";
    private static final String ACTION_LIST_SIZE_KEY = "ACTION_LIST_SIZE_NEW";
    private static final String EMAIL_REMINDER_KEY = EdenConstants.EMAIL_RMNDR_KEY;
    private static final String EMAIL_NOTIFY_PRIMARY_KEY = "EMAIL_NOTIFY_PRIMARY";
    private static final String EMAIL_NOTIFY_SECONDARY_KEY = "EMAIL_NOTIFY_SECONDARY";
    private static final String DEFAULT_COLOR = "white";
    private static final String DEFAULT_ACTION_LIST_SIZE = "10";
    private static final String DEFAULT_REFRESH_RATE = "15";
    private static final String ERR_KEY_REFRESH_RATE_WHOLE_NUM = "preferences.preferencesservice.refreshRate.wholenum";
    private static final String ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM = "preferences.preferencesservice.pagesize.wholenum";
    private static final String DELEGATOR_FILTER_KEY = "DELEGATOR_FILTER";

    public Preferences getPreferences(WorkflowUser user) {
        LOG.debug("start preferences fetch user " + user);
        Preferences preferences = new Preferences();
        preferences.setColorApproved(getOption(APPROVED_DOC_COLOR, DEFAULT_COLOR, user).getOptionVal());
        preferences.setColorCanceled(getOption(CANCELLED_DOC_COLOR, DEFAULT_COLOR, user).getOptionVal());
        preferences.setColorDissapproveCancel(getOption(DISSAPPROVED_CANCELLED_DOC_COLOR, DEFAULT_COLOR, user).getOptionVal());
        preferences.setColorDissaproved(getOption(DISAPPROVED_DOC_COLOR, DEFAULT_COLOR, user).getOptionVal());
        preferences.setColorEnroute(getOption(ENROUTE_DOC_COLOR, DEFAULT_COLOR, user).getOptionVal());
        preferences.setColorException(getOption(EXCEPTION_DOC_COLOR, DEFAULT_COLOR, user).getOptionVal());
        preferences.setColorFinal(getOption(FINAL_DOC_COLOR, DEFAULT_COLOR, user).getOptionVal());
        preferences.setColorInitiated(getOption(INITIATED_DOC_COLOR, DEFAULT_COLOR, user).getOptionVal());
        preferences.setColorProccessed(getOption(PROCESSED_DOC_COLOR, DEFAULT_COLOR, user).getOptionVal());
        preferences.setColorSaved(getOption(SAVED_DOC_COLOR, DEFAULT_COLOR, user).getOptionVal());
        preferences.setEmailNotification(getOption(EMAIL_REMINDER_KEY, EdenConstants.EMAIL_RMNDR_IMMEDIATE, user).getOptionVal());
        preferences.setNotifyPrimaryDelegation(getOption(EMAIL_NOTIFY_PRIMARY_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
        preferences.setNotifySecondaryDelegation(getOption(EMAIL_NOTIFY_SECONDARY_KEY, EdenConstants.PREFERENCES_NO_VAL, user).getOptionVal());
        preferences.setOpenNewWindow(getOption(OPEN_NEW_WINDOW_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
        preferences.setPageSize(getOption(ACTION_LIST_SIZE_KEY, DEFAULT_ACTION_LIST_SIZE, user).getOptionVal());
        preferences.setRefreshRate(getOption(REFRESH_RATE_KEY, DEFAULT_REFRESH_RATE, user).getOptionVal());
        preferences.setShowActionRequested(getOption(COLUMN_ACTION_REQ_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
        preferences.setShowDateCreated(getOption(COLUMN_DATE_CREATE_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
        preferences.setShowDocType(getOption(COLUMN_DOC_TYPE_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
        preferences.setShowDocumentStatus(getOption(COLUMN_DOCUMENT_STATUS_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
        preferences.setShowInitiator(getOption(COLUMN_INITIATOR_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
        preferences.setShowDelegator(getOption(COLUMN_DELEGATOR_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
        preferences.setShowDocTitle(getOption(COLUMN_TITLE_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
        preferences.setShowWorkgroupRequest(getOption(COLUMN_WORKGROUP_REQUEST_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
        preferences.setShowClearFyi(getOption(COLUMN_CLEAR_FYI_KEY, EdenConstants.PREFERENCES_YES_VAL, user).getOptionVal());
//        preferences.setActionListContentType(getOption(ACTION_LIST_CONTENT_KEY, EdenConstants.ACTION_LIST_ALL_REQUESTS, user).getOptionVal());
        preferences.setDelegatorFilter(getOption(DELEGATOR_FILTER_KEY, EdenConstants.DELEGATORS_ON_ACTION_LIST_PAGE, user).getOptionVal());
        LOG.debug("end preferences fetch user " + user);
        return preferences;
    }

    private UserOptions getOption(String optionKey, String defaultValue, WorkflowUser user) {
        LOG.debug("start fetch option " + optionKey + " user " + user.getWorkflowUserId().getWorkflowId());
        UserOptionsService optionSrv = getUserOptionService();
        UserOptions option =  optionSrv.findByOptionId(optionKey, user);
        if (option == null) {
            option = new UserOptions();
            option.setWorkflowId(user.getWorkflowUserId().getWorkflowId());
            option.setOptionId(optionKey);
            option.setOptionVal(defaultValue);
            optionSrv.save(option);
        }
        LOG.debug("end fetch option " + optionKey + " user " + user.getWorkflowUserId().getWorkflowId());
        return option;
    }

    public void savePreferences(WorkflowUser user, Preferences preferences) {
        LOG.debug("saving prerferences user " + user.getAuthenticationUserId());
        validate(preferences);
        UserOptionsService optionSrv = getUserOptionService();
        optionSrv.save(user, DISAPPROVED_DOC_COLOR, preferences.getColorDissaproved());
        optionSrv.save(user, DISSAPPROVED_CANCELLED_DOC_COLOR, preferences.getColorDissapproveCancel());
        optionSrv.save(user, APPROVED_DOC_COLOR, preferences.getColorApproved());
        optionSrv.save(user, CANCELLED_DOC_COLOR, preferences.getColorCanceled());
        optionSrv.save(user, SAVED_DOC_COLOR, preferences.getColorSaved());
        optionSrv.save(user, ENROUTE_DOC_COLOR, preferences.getColorEnroute());
        optionSrv.save(user, PROCESSED_DOC_COLOR, preferences.getColorProccessed());
        optionSrv.save(user, INITIATED_DOC_COLOR, preferences.getColorInitiated());
        optionSrv.save(user, FINAL_DOC_COLOR, preferences.getColorFinal());
        optionSrv.save(user, EXCEPTION_DOC_COLOR, preferences.getColorException());
        optionSrv.save(user, REFRESH_RATE_KEY, preferences.getRefreshRate().trim());
        optionSrv.save(user, OPEN_NEW_WINDOW_KEY, preferences.getOpenNewWindow());
        optionSrv.save(user, COLUMN_DOC_TYPE_KEY, preferences.getShowDocType());
        optionSrv.save(user, COLUMN_TITLE_KEY, preferences.getShowDocTitle());
        optionSrv.save(user, COLUMN_ACTION_REQ_KEY, preferences.getShowActionRequested());
        optionSrv.save(user, COLUMN_INITIATOR_KEY, preferences.getShowInitiator());
        optionSrv.save(user, COLUMN_DELEGATOR_KEY, preferences.getShowDelegator());
        optionSrv.save(user, COLUMN_DATE_CREATE_KEY, preferences.getShowDateCreated());
        optionSrv.save(user, COLUMN_DOCUMENT_STATUS_KEY, preferences.getShowDocumentStatus());
        optionSrv.save(user, COLUMN_WORKGROUP_REQUEST_KEY, preferences.getShowWorkgroupRequest());
        optionSrv.save(user, COLUMN_CLEAR_FYI_KEY, preferences.getShowClearFyi());
        optionSrv.save(user, ACTION_LIST_SIZE_KEY, preferences.getPageSize().trim());
        optionSrv.save(user, EMAIL_REMINDER_KEY, preferences.getEmailNotification());
        optionSrv.save(user, EMAIL_NOTIFY_PRIMARY_KEY, preferences.getNotifyPrimaryDelegation());
        optionSrv.save(user, EMAIL_NOTIFY_SECONDARY_KEY, preferences.getNotifySecondaryDelegation());
//        optionSrv.save(user, ACTION_LIST_CONTENT_KEY, preferences.getActionListContentType());
        optionSrv.save(user, DELEGATOR_FILTER_KEY, preferences.getDelegatorFilter());
        LOG.debug("saved preferences user " + user.getAuthenticationUserId());
    }

    private void validate(Preferences preferences) {
        LOG.debug("validating preferences");
        Collection errors = new ArrayList();
        try {
            new Integer(preferences.getRefreshRate().trim());
        } catch (NumberFormatException e) {
            errors.add(new WorkflowServiceErrorImpl("ActionList Refresh Rate must be in whole " +
                    "minutes", ERR_KEY_REFRESH_RATE_WHOLE_NUM));
        } catch (NullPointerException e1) {
            errors.add(new WorkflowServiceErrorImpl("ActionList Refresh Rate must be in whole " +
                    "minutes", ERR_KEY_REFRESH_RATE_WHOLE_NUM));
        }

        try {
            new Integer(preferences.getPageSize().trim());
        } catch (NumberFormatException e) {
            errors.add(new WorkflowServiceErrorImpl("ActionList Refresh Rate must be in whole " +
                    "minutes", ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM));
        } catch (NullPointerException e1) {
            errors.add(new WorkflowServiceErrorImpl("ActionList Refresh Rate must be in whole " +
                    "minutes", ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM));
        }
        LOG.debug("end validating preferences");
        if (! errors.isEmpty()) {
            throw new WorkflowServiceErrorException("Preference Validation Error", errors);
        }
    }

    public UserOptionsService getUserOptionService() {
        return (UserOptionsService) KEWServiceLocator.getService(
                KEWServiceLocator.USER_OPTIONS_SRV);
    }
}