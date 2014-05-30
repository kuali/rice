/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif.view;

import java.util.List;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Iframe;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.UrlInfo;

/**
 * IframeView is a View component that shows another website's content in an iframe.
 *
 * <p>This View will always have one page itself and will always contain an iframe component.  The location
 * property allows ease of setting the url for the iframe.  If the site being shown in the iframe is a KRAD View
 * itself, the default bean for this class will attempt to pass a url parameter notifying the View that it is being
 * shown in an iframe; this can be used in SpringEL to invoke special logic (such as not rendering some components,
 * like the app header)</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "iframeView", parent = "Uif-IframeView")
public class IframeView extends FormView {
    private UrlInfo location;
    private Iframe iframe;

    /**
     * Forces this view to be only one page, and sets the iframe as one of its items
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        super.setSinglePageView(true);

        List<Component> modifiedItems = (List<Component>) this.getPage().getItems();
        modifiedItems.add(iframe);
        this.getPage().setItems(modifiedItems);
    }

    /**
     * Evaluates expressions that may appear in location properties and sets the source of iframe automatically
     *
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        if (location != null) {
            ViewLifecycle.getExpressionEvaluator().populatePropertyExpressionsFromGraph(location, false);
            ViewLifecycle.getExpressionEvaluator().evaluateExpressionsOnConfigurable(this, location, this.getContext());

            iframe.setSource(location.getHref());
        }
    }

    /**
     * Get the url object representing the location
     *
     * @return the url location object
     */
    @BeanTagAttribute
    public UrlInfo getLocation() {
        return location;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.IframeView#getLocation()
     */
    public void setLocation(UrlInfo location) {
        this.location = location;
    }

    /**
     * @see org.kuali.rice.krad.uif.util.UrlInfo#getHref()
     */
    @BeanTagAttribute
    public String getHref() {
        if (this.location != null) {
            return this.location.getHref();
        }

        return null;
    }

    /**
     * @see IframeView#getHref()
     */
    public void setHref(String href) {
        if (this.location == null) {
            this.location = ComponentFactory.getUrlInfo();
        }

        this.location.setHref(href);
    }

    /**
     * The iframe component to be used as the content of this view, nothing needs to be set on this directly if
     * using the default bean for this View
     *
     * @return the iframe component
     */
    @BeanTagAttribute
    public Iframe getIframe() {
        return iframe;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.IframeView#getIframe()
     */
    public void setIframe(Iframe iframe) {
        this.iframe = iframe;
    }
}
