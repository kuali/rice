package edu.sampleu.demo.kitchensink;

import org.apache.commons.lang.StringUtils;
import java.beans.PropertyEditorSupport;
import java.io.Serializable;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UITestPropertyEditor extends PropertyEditorSupport implements Serializable {
    private static final long serialVersionUID = -4113846709722954737L;

    /**
     * @see java.beans.PropertyEditorSupport#getAsText()
     */
    @Override
    public String getAsText() {
        Object obj = this.getValue();

        if (obj == null) {
            return null;
        }

        String displayValue = obj.toString();
        if (displayValue.length() > 3) {
            displayValue = StringUtils.substring(displayValue, 0, 3) + "-" + StringUtils.substring(displayValue, 3);
        }

        return displayValue;
    }

    /**
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(String text) {
        String value = text;
        if (StringUtils.contains(value, "-")) {
            value = StringUtils.replaceOnce(value, "-", "");
        }

        this.setValue(value);
    }

}
