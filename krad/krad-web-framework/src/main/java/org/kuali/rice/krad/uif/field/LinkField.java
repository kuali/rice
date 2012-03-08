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
package org.kuali.rice.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.LightBox;

import java.util.List;

/**
 * Field that encloses a link element
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LinkField extends FieldBase {
    private static final long serialVersionUID = -1908504471910271148L;

    private Link link;

    private LightBox lightBox;

    public LinkField() {
        super();
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Set the linkLabel if blank to the Field label</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        if (StringUtils.isBlank(getLinkLabel())) {
            setLinkLabel(this.getLabel());
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(link);
        components.add(lightBox);

        return components;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public String getLinkLabel() {
        return link.getLinkLabel();
    }

    public void setLinkLabel(String linkLabel) {
        link.setLinkLabel(linkLabel);
    }

    public String getTarget() {
        return link.getTarget();
    }

    public void setTarget(String target) {
        link.setTarget(target);
    }

    public String getHrefText() {
        return link.getHrefText();
    }

    public void setHrefText(String hrefText) {
        link.setHrefText(hrefText);
    }

    public void setLightBox(LightBox lightBox) {
        this.lightBox = lightBox;
    }

    public LightBox getLightBox() {
        return lightBox;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getSupportsOnClick()
     */
    public boolean getSupportsOnClick() {
        return true;
    }

}
