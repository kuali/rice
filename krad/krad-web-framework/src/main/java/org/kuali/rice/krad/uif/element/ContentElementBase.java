package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.uif.component.ComponentBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ContentElementBase extends ComponentBase implements ContentElement {
    private static final long serialVersionUID = 5816584444025193540L;

    public ContentElementBase() {
        super();
    }

    @Override
    public String getComponentTypeName() {
        return "element";
    }
}
