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
package org.kuali.rice.kew.preferences.web;

import org.kuali.rice.kew.preferences.Preferences;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiForm;


/**
 * Struts ActionForm for {@link PreferencesAction}.
 *
 * @see PreferencesAction
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PreferencesForm extends KualiForm {

    private static final long serialVersionUID = 4536869031291955777L;
    private static final String ERR_KEY_REFRESH_RATE_WHOLE_NUM = "preferences.refreshRate";
    private static final String ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM = "preferences.pageSize";
	private Preferences preferences;
    private String methodToCall = "";
    private String returnMapping;
    private boolean showOutbox = true;

    public String getReturnMapping() {
        return returnMapping;
    }
    public void setReturnMapping(String returnMapping) {
        this.returnMapping = returnMapping;
    }
    public PreferencesForm() {
        preferences = new Preferences();
    }
    public String getMethodToCall() {
        return methodToCall;
    }
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }
    public Preferences getPreferences() {
        return preferences;
    }
    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
    public boolean isShowOutbox() {
        return this.showOutbox;
    }
    public void setShowOutbox(boolean showOutbox) {
        this.showOutbox = showOutbox;
    }

    public void validatePreferences() {
        try {
            new Integer(preferences.getRefreshRate().trim());
        } catch (NumberFormatException e) {
            GlobalVariables.getErrorMap().putError(ERR_KEY_REFRESH_RATE_WHOLE_NUM, "general.message", "ActionList Refresh Rate must be in whole minutes");
        } catch (NullPointerException e1) {
            GlobalVariables.getErrorMap().putError(ERR_KEY_REFRESH_RATE_WHOLE_NUM, "general.message", "ActionList Refresh Rate must be in whole minutes");
        }

        try {
            new Integer(preferences.getPageSize().trim());
        } catch (NumberFormatException e) {
            GlobalVariables.getErrorMap().putError(ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM, "general.message", "ActionList Refresh Rate must be in whole minutes");
        } catch (NullPointerException e1) {
            GlobalVariables.getErrorMap().putError(ERR_KEY_ACTION_LIST_PAGE_SIZE_WHOLE_NUM, "general.message", "ActionList Refresh Rate must be in whole minutes");
        }
        if (GlobalVariables.getErrorMap().hasErrors()) {
            throw new ValidationException("errors in preferences");
        }
    }
}
