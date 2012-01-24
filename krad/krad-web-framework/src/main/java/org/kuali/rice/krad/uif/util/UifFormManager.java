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

    public UifFormManager() {
        this.uifForms = new HashMap<String, UifFormBase>();
    }

    public UifFormBase getCurrentForm() {
        return currentForm;
    }

    public void setCurrentForm(UifFormBase currentForm) {
        this.currentForm = currentForm;
    }

    public void addForm(UifFormBase form) {
        if (form == null) {
            return;
        }

        uifForms.put(form.getFormKey(), form);
    }

    public UifFormBase getForm(String formKey) {
        if (uifForms.containsKey(formKey)) {
            return uifForms.get(formKey);
        }

        return null;
    }

    public void removeForm(UifFormBase form) {
        if (form == null) {
            return;
        }

        if (uifForms.containsKey(form.getFormKey())) {
            uifForms.remove(form.getFormKey());
        }

        if ((currentForm != null) && StringUtils.equals(currentForm.getFormKey(), form.getFormKey())) {
            currentForm = null;
        }
    }
}
