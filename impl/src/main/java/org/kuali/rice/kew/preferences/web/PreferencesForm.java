/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.preferences.web;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kew.api.preferences.Preferences;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;


/**
 * Struts ActionForm for {@link PreferencesAction}.
 *
 * @see PreferencesAction
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PreferencesForm extends KualiForm {

    private static final long serialVersionUID = 4536869031291955777L;
    private static final String ERR_KEY_REFRESH_RATE_WHOLE_NUM = "preferences.refreshRate";
    private static final String ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM = "preferences.pageSize";
	private Preferences.Builder preferences;
    private String methodToCall = "";
    private String returnMapping;
    private boolean showOutbox = true;

    // KULRICE-3137: Added a backLocation parameter similar to the one from lookups.
    private String backLocation;
    
	public String getReturnMapping() {
        return returnMapping;
    }
    public void setReturnMapping(String returnMapping) {
        this.returnMapping = returnMapping;
    }
    public PreferencesForm() {
        preferences = Preferences.Builder.create();
    }
    public String getMethodToCall() {
        return methodToCall;
    }
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }
    public Preferences.Builder getPreferences() {
        return preferences;
    }
    public void setPreferences(Preferences.Builder preferences) {
        this.preferences = preferences;
    }
    public boolean isShowOutbox() {
        return this.showOutbox;
    }
    public void setShowOutbox(boolean showOutbox) {
        this.showOutbox = showOutbox;
    }
    
	public String getBackLocation() {
		return this.backLocation;
	}
	public void setBackLocation(String backLocation) {
		this.backLocation = backLocation;
	}
	
	/**
	 * Retrieves the "returnLocation" parameter after calling "populate" on the superclass.
	 * 
	 * @see org.kuali.rice.krad.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void populate(HttpServletRequest request) {
		super.populate(request);
		
        if (getParameter(request, KRADConstants.RETURN_LOCATION_PARAMETER) != null) {
            setBackLocation(getParameter(request, KRADConstants.RETURN_LOCATION_PARAMETER));
        }
	}

    public void validatePreferences() {
     
        try {
            new Integer(preferences.getRefreshRate().trim());
        } catch (NumberFormatException e) {
            GlobalVariables.getMessageMap().putError(ERR_KEY_REFRESH_RATE_WHOLE_NUM, "general.message", "ActionList Refresh Rate must be in whole minutes");
        } catch (NullPointerException e1) {
            GlobalVariables.getMessageMap().putError(ERR_KEY_REFRESH_RATE_WHOLE_NUM, "general.message", "ActionList Refresh Rate must be in whole minutes");
        }

        try {
            if(new Integer(preferences.getPageSize().trim()) == 0){
            	 GlobalVariables.getMessageMap().putError(ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM, "general.message", "ActionList Page Size must be non-zero ");
            }    
        } catch (NumberFormatException e) {
            GlobalVariables.getMessageMap().putError(ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM, "general.message", "ActionList Page Size must be in whole minutes");
        } catch (NullPointerException e1) {
            GlobalVariables.getMessageMap().putError(ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM, "general.message", "ActionList Page Size must be in whole minutes");
        }
      
        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in preferences");
        }
    }
}
