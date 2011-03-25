/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.kew.preferences.service.impl;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.preferences.service.PreferencesService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.useroptions.UserOptions;
import org.kuali.rice.kew.useroptions.UserOptionsService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * An implementation of the {@link PreferencesService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
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
    private static final String COLUMN_APP_DOC_STATUS_KEY = "APP_DOC_STATUS_COL_SHOW_NEW";
    private static final String COLUMN_WORKGROUP_REQUEST_KEY = "WORKGROUP_REQUEST_COL_SHOW_NEW";
    private static final String COLUMN_CLEAR_FYI_KEY = "CLEAR_FYI_COL_SHOW_NEW";
    private static final String ACTION_LIST_SIZE_KEY = "ACTION_LIST_SIZE_NEW";
    private static final String EMAIL_REMINDER_KEY = KEWConstants.EMAIL_RMNDR_KEY;
    private static final String EMAIL_NOTIFY_PRIMARY_KEY = "EMAIL_NOTIFY_PRIMARY";
    private static final String EMAIL_NOTIFY_SECONDARY_KEY = "EMAIL_NOTIFY_SECONDARY";
    private static final String DEFAULT_COLOR = "white";
    private static final String DEFAULT_ACTION_LIST_SIZE = "10";
    private static final String DEFAULT_REFRESH_RATE = "15";
    private static final String ERR_KEY_REFRESH_RATE_WHOLE_NUM = "preferences.refreshRate";
    private static final String ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM = "preferences.pageSize";
    private static final String DELEGATOR_FILTER_KEY = "DELEGATOR_FILTER";
    private static final String PRIMARY_DELEGATE_FILTER_KEY = "PRIMARY_DELEGATE_FILTER";
    public static final String USE_OUT_BOX = "USE_OUT_BOX";
    private static final String COLUMN_LAST_APPROVED_DATE_KEY = "LAST_APPROVED_DATE_COL_SHOW_NEW";

    public static final String COLUMN_CURRENT_NODE_KEY = "CURRENT_NODE_COL_SHOW_NEW";

    public Preferences getPreferences(String principalId) {
        if ( LOG.isDebugEnabled() ) {
        LOG.debug("start preferences fetch user " + principalId);
        }
        Collection<UserOptions> options = getUserOptionService().findByWorkflowUser(principalId);
        Map<String,UserOptions> optionMap = new HashMap<String, UserOptions>();
        for ( UserOptions option : options ) {
        	optionMap.put(option.getOptionId(), option);
        }
        
        ConfigurationService kcs = KNSServiceLocator.getKualiConfigurationService();
        
        String stagingDirectory = kcs.getPropertyString("staging.directory");                                                    
                                                                                                    
        String defaultColor =  kcs.getPropertyString("userOptions.default.color");                                                                                                  
        String defaultEmail = kcs.getPropertyString("userOptions.default.email");                                                                                                   
        String defaultNotifyPrimary = kcs.getPropertyString("userOptions.default.notifyPrimary");                                                                                   
        String defaultNotifySecondary = kcs.getPropertyString("userOptions.default.notifySecondary");                                                                               
        String defaultOpenNewWindow = kcs.getPropertyString("userOptions.default.openNewWindow");                                                                                   
        String defaultActionListSize = kcs.getPropertyString("userOptions.default.actionListSize");                                                                                 
        String defaultRefreshRate = kcs.getPropertyString("userOptions.default.refreshRate");                                                                                       
        String defaultShowActionRequired = kcs.getPropertyString("userOptions.default.showActionRequired");                                                                         
        String defaultShowDateCreated = kcs.getPropertyString("userOptions.default.showDateCreated");                                                                               
        String defaultShowDocType = kcs.getPropertyString("userOptions.default.showDocumentType");                                                                                  
        String defaultShowDocStatus = kcs.getPropertyString("userOptions.default.showDocumentStatus");                                                                              
        String defaultShowInitiator = kcs.getPropertyString("userOptions.default.showInitiator");                                                                                   
        String defaultShowDelegator = kcs.getPropertyString("userOptions.default.showDelegator");                                                                                   
        String defaultShowTitle = kcs.getPropertyString("userOptions.default.showTitle");                                                                                           
        String defaultShowWorkgroupRequest = kcs.getPropertyString("userOptions.default.showWorkgroupRequest");                                                                     
        String defaultShowLastApprovedDate = kcs.getPropertyString("userOptions.default.showLastApprovedDate");                                                                     
        String defaultShowClearFYI = kcs.getPropertyString("userOptions.default.showClearFYI");                                                                                     
        String defaultShowCurrentNode = kcs.getPropertyString("userOptions.default.showCurrentNode");                                                                               
        String defaultDelegatorFilterOnActionList = kcs.getPropertyString("userOptions.default.delegatorFilterOnActionList");                                                       
        String defaultPrimaryDelegatorFilterOnActionList = kcs.getPropertyString("userOptions.default.primaryDelegatorFilterOnActionList");                                         

        final String defaultUseOutBox = kcs.getPropertyString(KEWConstants.USER_OPTIONS_DEFAULT_USE_OUTBOX_PARAM);
                                                                                                                                                                                    
        Preferences preferences = new Preferences();                                                                                                                                
        preferences.setColorApproved(getOption(optionMap,APPROVED_DOC_COLOR, defaultColor, principalId, preferences).getOptionVal());                                               
        preferences.setColorCanceled(getOption(optionMap,CANCELLED_DOC_COLOR, defaultColor, principalId, preferences).getOptionVal());                                              
        preferences.setColorDissapproveCancel(getOption(optionMap,DISSAPPROVED_CANCELLED_DOC_COLOR, defaultColor, principalId, preferences).getOptionVal());                        
        preferences.setColorDissaproved(getOption(optionMap,DISAPPROVED_DOC_COLOR, defaultColor, principalId, preferences).getOptionVal());                                         
        preferences.setColorEnroute(getOption(optionMap,ENROUTE_DOC_COLOR, defaultColor, principalId, preferences).getOptionVal());                                                 
        preferences.setColorException(getOption(optionMap,EXCEPTION_DOC_COLOR, defaultColor, principalId, preferences).getOptionVal());                                             
        preferences.setColorFinal(getOption(optionMap,FINAL_DOC_COLOR, defaultColor, principalId, preferences).getOptionVal());                                                     
        preferences.setColorInitiated(getOption(optionMap,INITIATED_DOC_COLOR, defaultColor, principalId, preferences).getOptionVal());                                             
        preferences.setColorProccessed(getOption(optionMap,PROCESSED_DOC_COLOR, defaultColor, principalId, preferences).getOptionVal());                                            
        preferences.setColorSaved(getOption(optionMap,SAVED_DOC_COLOR, defaultColor, principalId, preferences).getOptionVal());                                                     
        preferences.setEmailNotification(getOption(optionMap,EMAIL_REMINDER_KEY, defaultEmail, principalId, preferences).getOptionVal());                                           
        preferences.setNotifyPrimaryDelegation(getOption(optionMap,EMAIL_NOTIFY_PRIMARY_KEY, defaultNotifyPrimary, principalId, preferences).getOptionVal());                       
        preferences.setNotifySecondaryDelegation(getOption(optionMap,EMAIL_NOTIFY_SECONDARY_KEY, defaultNotifySecondary, principalId, preferences).getOptionVal());                 
        preferences.setOpenNewWindow(getOption(optionMap,OPEN_NEW_WINDOW_KEY, defaultOpenNewWindow, principalId, preferences).getOptionVal());                                      
        preferences.setPageSize(getOption(optionMap,ACTION_LIST_SIZE_KEY, defaultActionListSize, principalId, preferences).getOptionVal());                                         
        preferences.setRefreshRate(getOption(optionMap,REFRESH_RATE_KEY, defaultRefreshRate, principalId, preferences).getOptionVal());                                             
        preferences.setShowActionRequested(getOption(optionMap,COLUMN_ACTION_REQ_KEY, defaultShowActionRequired, principalId, preferences).getOptionVal());                         
        preferences.setShowDateCreated(getOption(optionMap,COLUMN_DATE_CREATE_KEY, defaultShowDateCreated, principalId, preferences).getOptionVal());                               
        preferences.setShowDocType(getOption(optionMap,COLUMN_DOC_TYPE_KEY, defaultShowDocType, principalId, preferences).getOptionVal());                                          
        preferences.setShowDocumentStatus(getOption(optionMap,COLUMN_DOCUMENT_STATUS_KEY, defaultShowDocStatus, principalId, preferences).getOptionVal());                          
        preferences.setShowInitiator(getOption(optionMap,COLUMN_INITIATOR_KEY, defaultShowInitiator, principalId, preferences).getOptionVal());                                     
        preferences.setShowDelegator(getOption(optionMap,COLUMN_DELEGATOR_KEY, defaultShowDelegator, principalId, preferences).getOptionVal());                                     
        preferences.setShowDocTitle(getOption(optionMap,COLUMN_TITLE_KEY, defaultShowTitle, principalId, preferences).getOptionVal());                                              
        preferences.setShowWorkgroupRequest(getOption(optionMap,COLUMN_WORKGROUP_REQUEST_KEY, defaultShowWorkgroupRequest, principalId, preferences).getOptionVal());               
        preferences.setShowClearFyi(getOption(optionMap,COLUMN_CLEAR_FYI_KEY, defaultShowClearFYI, principalId, preferences).getOptionVal());                                       
        preferences.setDelegatorFilter(getOption(optionMap,DELEGATOR_FILTER_KEY, defaultDelegatorFilterOnActionList, principalId, preferences).getOptionVal());                     
        preferences.setPrimaryDelegateFilter(getOption(optionMap,PRIMARY_DELEGATE_FILTER_KEY, defaultPrimaryDelegatorFilterOnActionList, principalId, preferences).getOptionVal()); 
        preferences.setShowDateApproved(getOption(optionMap,COLUMN_LAST_APPROVED_DATE_KEY, defaultShowLastApprovedDate, principalId, preferences).getOptionVal());                  
        preferences.setShowCurrentNode(getOption(optionMap,COLUMN_CURRENT_NODE_KEY, defaultShowCurrentNode, principalId, preferences).getOptionVal());                              
        preferences.setUseOutbox(getOption(optionMap,USE_OUT_BOX, defaultUseOutBox, principalId, preferences).getOptionVal());                                                      

        if ( LOG.isDebugEnabled() ) {
        LOG.debug("end preferences fetch user " + principalId);
        }
        return preferences;
    }

    /* @see https://test.kuali.org/jira/browse/KULRICE-1726 */
    //private static ConcurrencyDetector detector = new ConcurrencyDetector("Concurrency in PreferencesServiceImpl", false);

    private UserOptions getOption(Map<String,UserOptions> optionsMap, String optionKey, String defaultValue, String principalId, Preferences preferences) {
    	if ( LOG.isDebugEnabled() ) {
        LOG.debug("start fetch option " + optionKey + " user " + principalId);
    	}
        UserOptions option = optionsMap.get(optionKey);
        if (option == null) {
        	if ( LOG.isDebugEnabled() ) {
            LOG.debug("User option '" + optionKey + "' on user " + principalId + " has no stored value.  Preferences will require save.");
        	}
            option = new UserOptions();
            option.setWorkflowId(principalId);
            option.setOptionId(optionKey);
            option.setOptionVal(defaultValue);
            optionsMap.put(optionKey, option); // just in case referenced a second time
            if ( optionKey.equals(USE_OUT_BOX) && !ConfigContext.getCurrentContextConfig().getOutBoxOn() ) {
            	// don't mark as needing save
            } else {
            preferences.setRequiresSave(true);
        }
        }
        if ( LOG.isDebugEnabled() ) {
        LOG.debug("End fetch option " + optionKey + " user " + principalId);
        }
        return option;
    }

    public void savePreferences(String principalId, Preferences preferences) {
    	// NOTE: this previously displayed the principalName.  Now it's just the id
    	if ( LOG.isDebugEnabled() ) {
        LOG.debug("saving preferences user " + principalId);
    	}

        validate(preferences);
        Map<String,String> optionsMap = new HashMap<String,String>(50);
        
        optionsMap.put(DISSAPPROVED_CANCELLED_DOC_COLOR, preferences.getColorDissapproveCancel());
        optionsMap.put(DISAPPROVED_DOC_COLOR, preferences.getColorDissaproved());
        optionsMap.put(APPROVED_DOC_COLOR, preferences.getColorApproved());
        optionsMap.put(CANCELLED_DOC_COLOR, preferences.getColorCanceled());
        optionsMap.put(SAVED_DOC_COLOR, preferences.getColorSaved());
        optionsMap.put(ENROUTE_DOC_COLOR, preferences.getColorEnroute());
        optionsMap.put(PROCESSED_DOC_COLOR, preferences.getColorProccessed());
        optionsMap.put(INITIATED_DOC_COLOR, preferences.getColorInitiated());
        optionsMap.put(FINAL_DOC_COLOR, preferences.getColorFinal());
        optionsMap.put(EXCEPTION_DOC_COLOR, preferences.getColorException());
        optionsMap.put(REFRESH_RATE_KEY, preferences.getRefreshRate().trim());
        optionsMap.put(OPEN_NEW_WINDOW_KEY, preferences.getOpenNewWindow());
        optionsMap.put(COLUMN_DOC_TYPE_KEY, preferences.getShowDocType());
        optionsMap.put(COLUMN_TITLE_KEY, preferences.getShowDocTitle());
        optionsMap.put(COLUMN_ACTION_REQ_KEY, preferences.getShowActionRequested());
        optionsMap.put(COLUMN_INITIATOR_KEY, preferences.getShowInitiator());
        optionsMap.put(COLUMN_DELEGATOR_KEY, preferences.getShowDelegator());
        optionsMap.put(COLUMN_DATE_CREATE_KEY, preferences.getShowDateCreated());
        optionsMap.put(COLUMN_DOCUMENT_STATUS_KEY, preferences.getShowDocumentStatus());
        optionsMap.put(COLUMN_APP_DOC_STATUS_KEY, preferences.getShowAppDocStatus());
        optionsMap.put(COLUMN_WORKGROUP_REQUEST_KEY, preferences.getShowWorkgroupRequest());
        optionsMap.put(COLUMN_CLEAR_FYI_KEY, preferences.getShowClearFyi());
        optionsMap.put(ACTION_LIST_SIZE_KEY, preferences.getPageSize().trim());
        optionsMap.put(EMAIL_REMINDER_KEY, preferences.getEmailNotification());
        optionsMap.put(EMAIL_NOTIFY_PRIMARY_KEY, preferences.getNotifyPrimaryDelegation());
        optionsMap.put(EMAIL_NOTIFY_SECONDARY_KEY, preferences.getNotifySecondaryDelegation());
        optionsMap.put(DELEGATOR_FILTER_KEY, preferences.getDelegatorFilter());
        optionsMap.put(PRIMARY_DELEGATE_FILTER_KEY, preferences.getPrimaryDelegateFilter());
        optionsMap.put(COLUMN_LAST_APPROVED_DATE_KEY, preferences.getShowDateApproved());
        optionsMap.put(COLUMN_CURRENT_NODE_KEY, preferences.getShowCurrentNode());
        if (ConfigContext.getCurrentContextConfig().getOutBoxOn()) {
            optionsMap.put(USE_OUT_BOX, preferences.getUseOutbox());
        }
        getUserOptionService().save(principalId, optionsMap);
        if ( LOG.isDebugEnabled() ) {
        LOG.debug("saved preferences user " + principalId);
    }
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
            if(new Integer(preferences.getPageSize().trim()) == 0){
            	errors.add(new WorkflowServiceErrorImpl("ActionList Page Size must be non-zero ", ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM));
            }            
        } catch (NumberFormatException e) {
            errors.add(new WorkflowServiceErrorImpl("ActionList Page Size must be in whole " +
                    "minutes", ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM));
        } catch (NullPointerException e1) {
            errors.add(new WorkflowServiceErrorImpl("ActionList Page Size must be in whole " +
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
