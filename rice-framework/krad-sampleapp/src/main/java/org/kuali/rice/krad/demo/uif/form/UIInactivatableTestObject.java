package org.kuali.rice.krad.demo.uif.form;

import org.kuali.rice.core.api.mo.common.active.Inactivatable;

/**
 * Test data object that implements Inactivatable
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UIInactivatableTestObject extends UITestObject implements Inactivatable {
    private static final long serialVersionUID = 5546600234913206443L;

    private boolean active;

    public UIInactivatableTestObject() {
        super();
    }

    public UIInactivatableTestObject(String field1, String field2, String field3, String field4, boolean active) {
        super(field1, field2, field3, field4);

        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
