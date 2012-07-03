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
package org.kuali.rice.krad.web.form;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.kuali.rice.krad.uif.util.SessionTransient;
import org.kuali.rice.krad.uif.view.HistoryEntry;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages Uif form objects for a session
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifFormManager implements Serializable {
    private static final long serialVersionUID = -6323378881342207080L;

    private Map<String, UifFormBase> sessionForms;

    /**
     * Create a new form manager with an empty list of forms for the session.
     */
    public UifFormManager() {
        this.sessionForms = new HashMap<String, UifFormBase>();
    }

    /**
     * Add a form to the session.
     *
     * @param form to be added to the session
     */
    public void addSessionForm(UifFormBase form) {
        if (form == null) {
            return;
        }

        sessionForms.put(form.getFormKey(), form);
    }

    /**
     * Retrieve a form from the session.
     *
     * @param formKey of the form to retrieve from the session
     * @return UifFormBase
     */
    public UifFormBase getSessionForm(String formKey) {
        if (sessionForms.containsKey(formKey)) {
            return sessionForms.get(formKey);
        }

        return null;
    }

    /**
     * Removes the stored form data and the forms from the breadcrumb history from the session.
     *
     * @param form to be removed
     */
    public void removeSessionForm(UifFormBase form) {
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
    public void removeSessionFormByKey(String formKey) {
        if (sessionForms.containsKey(formKey)) {
            sessionForms.remove(formKey);
        }
    }

    /**
     * Removes the stored form data and the forms from the breadcrumb history from the session.
     *
     * @param formKey of the form to be removed
     */
    public void removeFormWithHistoryFormsByKey(String formKey) {
        if (sessionForms.containsKey(formKey)) {
            // Remove forms from breadcrumb history as well
            for (HistoryEntry historyEntry : sessionForms.get(formKey).getFormHistory().getHistoryEntries()) {
                sessionForms.remove(historyEntry.getFormKey());
            }

            removeSessionFormByKey(formKey);
        }
    }

    /**
     * Retrieves the session form based on the formkey and updates the non session transient
     * variables on the request form from the session form.
     *
     * @param requestForm
     * @param formKey
     * @return UifFormBase the updated form
     */
    public UifFormBase updateFormWithSession(UifFormBase requestForm, String formKey) {
        UifFormBase updatedForm = null;
        Object fieldValue = null;

        UifFormBase sessionForm = sessionForms.get(formKey);
        if (sessionForm == null) {
            updatedForm = requestForm;
        } else {
            List<Field> fields = new ArrayList<Field>();
            for (Field field : ObjectUtils.getAllFields(fields, sessionForm.getClass(), UifFormBase.class)) {
                boolean copyValue = true;
                for (Annotation an : field.getAnnotations()) {
                    if (an instanceof SessionTransient) {
                        copyValue = false;
                    }
                }

                if (copyValue) {
                    try {
                        fieldValue = PropertyUtils.getProperty(sessionForm, field.getName());
                        PropertyUtils.setProperty(requestForm, field.getName(), fieldValue);
                    } catch (NoSuchMethodException e1) {
                        // Eat it
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Error copying values from the session form to request form for " + field.getName());
                    }
                }

            }

            updatedForm = requestForm;
        }

        return updatedForm;
    }

    /**
     * Removes the values that are marked @SessionTransient from the form.
     *
     * @param form
     * @return UifFormBase the form from which the session transient values have been purged
     */
    public UifFormBase purgeForm(UifFormBase form) {
        List<Field> fields = new ArrayList<Field>();
        for (Field field : ObjectUtils.getAllFields(fields, form.getClass(), UifFormBase.class)) {
            boolean purgeValue = false;

            if (!field.getType().isPrimitive()) {
                for (Annotation an : field.getAnnotations()) {
                    if (an instanceof SessionTransient) {
                        purgeValue = true;
                    }
                }
            }
            try {
                if (purgeValue) {
                    PropertyUtils.setProperty(form, field.getName(), null);
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to purge the field " + field.getName());
            }

        }
        return form;
    }

}
