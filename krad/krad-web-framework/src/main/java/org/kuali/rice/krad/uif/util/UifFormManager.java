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
