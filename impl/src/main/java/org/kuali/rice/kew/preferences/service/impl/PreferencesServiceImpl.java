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
package org.kuali.rice.kew.preferences.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.preferences.service.PreferencesService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.useroptions.UserOptions;
import org.kuali.rice.kew.useroptions.UserOptionsService;
import org.kuali.rice.kew.util.KEWConstants;


/**
 * An implementation of the {@link PreferencesService}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PreferencesServiceImpl implements PreferencesService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreferencesServiceImpl.class);

    private static final String DISAPPROVED_DOC_COLOR = "DOCUMENT_STATUS_COLOR_D";
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
    private static final String EMAIL_REMINDER_KEY = KEWConstants.EMAIL_RMNDR_KEY;
    private static final String EMAIL_NOTIFY_PRIMARY_KEY = "EMAIL_NOTIFY_PRIMARY";
    private static final String EMAIL_NOTIFY_SECONDARY_KEY = "EMAIL_NOTIFY_SECONDARY";
    private static final String DEFAULT_COLOR = "white";
    private static final String DEFAULT_ACTION_LIST_SIZE = "10";
    private static final String DEFAULT_REFRESH_RATE = "15";
    private static final String ERR_KEY_REFRESH_RATE_WHOLE_NUM = "preferences.preferencesservice.refreshRate.wholenum";
    private static final String ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM = "preferences.preferencesservice.pagesize.wholenum";
    private static final String DELEGATOR_FILTER_KEY = "DELEGATOR_FILTER";
    public static final String USE_OUT_BOX = "USE_OUT_BOX";
    private static final String COLUMN_LAST_APPROVED_DATE_KEY = "LAST_APPROVED_DATE_COL_SHOW_NEW";
    public static final String COLUMN_CURRENT_NODE_KEY = "LAST_APPROVED_DATE_COL_SHOW_NEW";


    public Preferences getPreferences(String principalId) {
        LOG.debug("start preferences fetch user " + principalId);
        Preferences preferences = new Preferences();
        preferences.setColorApproved(getOption(APPROVED_DOC_COLOR, DEFAULT_COLOR, principalId, preferences).getOptionVal());
        preferences.setColorCanceled(getOption(CANCELLED_DOC_COLOR, DEFAULT_COLOR, principalId, preferences).getOptionVal());
        preferences.setColorDissapproveCancel(getOption(DISSAPPROVED_CANCELLED_DOC_COLOR, DEFAULT_COLOR, principalId, preferences).getOptionVal());
        preferences.setColorDissaproved(getOption(DISAPPROVED_DOC_COLOR, DEFAULT_COLOR, principalId, preferences).getOptionVal());
        preferences.setColorEnroute(getOption(ENROUTE_DOC_COLOR, DEFAULT_COLOR, principalId, preferences).getOptionVal());
        preferences.setColorException(getOption(EXCEPTION_DOC_COLOR, DEFAULT_COLOR, principalId, preferences).getOptionVal());
        preferences.setColorFinal(getOption(FINAL_DOC_COLOR, DEFAULT_COLOR, principalId, preferences).getOptionVal());
        preferences.setColorInitiated(getOption(INITIATED_DOC_COLOR, DEFAULT_COLOR, principalId, preferences).getOptionVal());
        preferences.setColorProccessed(getOption(PROCESSED_DOC_COLOR, DEFAULT_COLOR, principalId, preferences).getOptionVal());
        preferences.setColorSaved(getOption(SAVED_DOC_COLOR, DEFAULT_COLOR, principalId, preferences).getOptionVal());
        preferences.setEmailNotification(getOption(EMAIL_REMINDER_KEY, KEWConstants.EMAIL_RMNDR_IMMEDIATE, principalId, preferences).getOptionVal());
        preferences.setNotifyPrimaryDelegation(getOption(EMAIL_NOTIFY_PRIMARY_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setNotifySecondaryDelegation(getOption(EMAIL_NOTIFY_SECONDARY_KEY, KEWConstants.PREFERENCES_NO_VAL, principalId, preferences).getOptionVal());
        preferences.setOpenNewWindow(getOption(OPEN_NEW_WINDOW_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setPageSize(getOption(ACTION_LIST_SIZE_KEY, DEFAULT_ACTION_LIST_SIZE, principalId, preferences).getOptionVal());
        preferences.setRefreshRate(getOption(REFRESH_RATE_KEY, DEFAULT_REFRESH_RATE, principalId, preferences).getOptionVal());
        preferences.setShowActionRequested(getOption(COLUMN_ACTION_REQ_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setShowDateCreated(getOption(COLUMN_DATE_CREATE_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setShowDocType(getOption(COLUMN_DOC_TYPE_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setShowDocumentStatus(getOption(COLUMN_DOCUMENT_STATUS_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setShowInitiator(getOption(COLUMN_INITIATOR_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setShowDelegator(getOption(COLUMN_DELEGATOR_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setShowDocTitle(getOption(COLUMN_TITLE_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setShowWorkgroupRequest(getOption(COLUMN_WORKGROUP_REQUEST_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setShowClearFyi(getOption(COLUMN_CLEAR_FYI_KEY, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        preferences.setDelegatorFilter(getOption(DELEGATOR_FILTER_KEY, KEWConstants.DELEGATORS_ON_ACTION_LIST_PAGE, principalId, preferences).getOptionVal());
        preferences.setShowDateApproved(getOption(COLUMN_LAST_APPROVED_DATE_KEY, KEWConstants.PREFERENCES_NO_VAL, principalId, preferences).getOptionVal());
        preferences.setShowCurrentNode(getOption(COLUMN_CURRENT_NODE_KEY, KEWConstants.PREFERENCES_NO_VAL, principalId, preferences).getOptionVal());

        if (ConfigContext.getCurrentContextConfig().getOutBoxDefaultPreferenceOn()) {
            preferences.setUseOutbox(getOption(USE_OUT_BOX, KEWConstants.PREFERENCES_YES_VAL, principalId, preferences).getOptionVal());
        } else {
            preferences.setUseOutbox(getOption(USE_OUT_BOX, KEWConstants.PREFERENCES_NO_VAL, principalId, preferences).getOptionVal());
        }

        LOG.debug("end preferences fetch user " + principalId);
        return preferences;
    }

    /* @see https://test.kuali.org/jira/browse/KULRICE-1726 */
    //private static ConcurrencyDetector detector = new ConcurrencyDetector("Concurrency in PreferencesServiceImpl", false);

    private UserOptions getOption(String optionKey, String defaultValue, String principalId, Preferences preferences) {
        LOG.debug("start fetch option " + optionKey + " user " + principalId);
        UserOptionsService optionSrv = getUserOptionService();
        UserOptions option =  optionSrv.findByOptionId(optionKey, principalId);
        if (option == null) {
            LOG.debug("User option '" + optionKey + "' on user " + principalId + " has no stored value.  Preferences will require save.");
            option = new UserOptions();
            option.setWorkflowId(principalId);
            option.setOptionId(optionKey);
            option.setOptionVal(defaultValue);
            preferences.setRequiresSave(true);
        }
        LOG.debug("end fetch option " + optionKey + " user " + principalId);
        return option;
    }

    public void savePreferences(String principalId, Preferences preferences) {
    	// NOTE: this previously displayed the principalName.  Now it's just the id
        LOG.debug("saving preferences user " + principalId);

        validate(preferences);
        UserOptionsService optionSrv = getUserOptionService();
        optionSrv.save(principalId, DISAPPROVED_DOC_COLOR, preferences.getColorDissaproved());
        optionSrv.save(principalId, DISSAPPROVED_CANCELLED_DOC_COLOR, preferences.getColorDissapproveCancel());
        optionSrv.save(principalId, APPROVED_DOC_COLOR, preferences.getColorApproved());
        optionSrv.save(principalId, CANCELLED_DOC_COLOR, preferences.getColorCanceled());
        optionSrv.save(principalId, SAVED_DOC_COLOR, preferences.getColorSaved());
        optionSrv.save(principalId, ENROUTE_DOC_COLOR, preferences.getColorEnroute());
        optionSrv.save(principalId, PROCESSED_DOC_COLOR, preferences.getColorProccessed());
        optionSrv.save(principalId, INITIATED_DOC_COLOR, preferences.getColorInitiated());
        optionSrv.save(principalId, FINAL_DOC_COLOR, preferences.getColorFinal());
        optionSrv.save(principalId, EXCEPTION_DOC_COLOR, preferences.getColorException());
        optionSrv.save(principalId, REFRESH_RATE_KEY, preferences.getRefreshRate().trim());
        optionSrv.save(principalId, OPEN_NEW_WINDOW_KEY, preferences.getOpenNewWindow());
        optionSrv.save(principalId, COLUMN_DOC_TYPE_KEY, preferences.getShowDocType());
        optionSrv.save(principalId, COLUMN_TITLE_KEY, preferences.getShowDocTitle());
        optionSrv.save(principalId, COLUMN_ACTION_REQ_KEY, preferences.getShowActionRequested());
        optionSrv.save(principalId, COLUMN_INITIATOR_KEY, preferences.getShowInitiator());
        optionSrv.save(principalId, COLUMN_DELEGATOR_KEY, preferences.getShowDelegator());
        optionSrv.save(principalId, COLUMN_DATE_CREATE_KEY, preferences.getShowDateCreated());
        optionSrv.save(principalId, COLUMN_DOCUMENT_STATUS_KEY, preferences.getShowDocumentStatus());
        optionSrv.save(principalId, COLUMN_WORKGROUP_REQUEST_KEY, preferences.getShowWorkgroupRequest());
        optionSrv.save(principalId, COLUMN_CLEAR_FYI_KEY, preferences.getShowClearFyi());
        optionSrv.save(principalId, ACTION_LIST_SIZE_KEY, preferences.getPageSize().trim());
        optionSrv.save(principalId, EMAIL_REMINDER_KEY, preferences.getEmailNotification());
        optionSrv.save(principalId, EMAIL_NOTIFY_PRIMARY_KEY, preferences.getNotifyPrimaryDelegation());
        optionSrv.save(principalId, EMAIL_NOTIFY_SECONDARY_KEY, preferences.getNotifySecondaryDelegation());
        optionSrv.save(principalId, DELEGATOR_FILTER_KEY, preferences.getDelegatorFilter());
        optionSrv.save(principalId, COLUMN_LAST_APPROVED_DATE_KEY, preferences.getShowDateApproved());
        optionSrv.save(principalId, COLUMN_CURRENT_NODE_KEY, preferences.getShowCurrentNode());
        if (ConfigContext.getCurrentContextConfig().getOutBoxOn()) {
            optionSrv.save(principalId, USE_OUT_BOX, preferences.getUseOutbox());
        }
        LOG.debug("saved preferences user " + principalId);
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