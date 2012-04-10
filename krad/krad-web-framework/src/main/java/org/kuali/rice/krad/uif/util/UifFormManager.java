/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.view.HistoryEntry;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages Uif form objects for a session
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifFormManager implements Serializable {
    private static final long serialVersionUID = -6323378881342207080L;

    private UifFormBase currentForm;
    private Map<String, UifFormBase> uifForms;

    /**
     * Create a new form manager with an empty list of forms for the session.
     */
    public UifFormManager() {
        this.uifForms = new HashMap<String, UifFormBase>();
    }

    /**
     * Get the current form of the session.
     *
     * @return UifFormBase
     */
    public UifFormBase getCurrentForm() {
        return currentForm;
    }

    /**
     * Sets the current form of the session.
     *
     * @param currentForm
     */
    public void setCurrentForm(UifFormBase currentForm) {
        this.currentForm = currentForm;
        addForm(currentForm);
    }

    /**
     * Add a form to the session.
     *
     * @param form to be added to the session
     */
    public void addForm(UifFormBase form) {
        if (form == null) {
            return;
        }

        uifForms.put(form.getFormKey(), form);
    }

    /**
     * Retrieve a form from the session.
     *
     * @param formKey of the form to retrieve from the session
     * @return UifFormBase
     */
    public UifFormBase getForm(String formKey) {
        if (uifForms.containsKey(formKey)) {
            return uifForms.get(formKey);
        }

        return null;
    }

    /**
     * Removes the stored form data and the forms from the breadcrumb history from the session.
     *
     * @param form to be removed
     */
    public void removeForm(UifFormBase form) {
        if (form == null) {
            return;
        }

        removeFormWithHistoryFormsByKey(form.getFormKey());
    }

    /**
     * Removes the stored form data from the session.
     *
     * @param formKey of the form to be removed
     */
    public void removeFormByKey(String formKey) {
        if (uifForms.containsKey(formKey)) {
            uifForms.remove(formKey);
        }

        if ((currentForm != null) && StringUtils.equals(currentForm.getFormKey(), formKey)) {
            currentForm = null;
        }
    }

    /**
     * Removes the stored form data and the forms from the breadcrumb history from the session.
     *
     * @param formKey of the form to be removed
     */
    public void removeFormWithHistoryFormsByKey(String formKey) {
        if (uifForms.containsKey(formKey)) {
            // Remove forms from breadcrumb history as well
            for (HistoryEntry historyEntry : uifForms.get(formKey).getFormHistory().getHistoryEntries()) {
                uifForms.remove(historyEntry.getFormKey());
            }

            removeFormByKey(formKey);
        }
    }
}
