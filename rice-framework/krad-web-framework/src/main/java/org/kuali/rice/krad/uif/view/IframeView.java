/*
 * Copyright 2006-2014 The Kuali Foundation
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

import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Iframe;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.UrlInfo;

import java.util.List;

/**
 * IframeView is a View component that shows another website's content in an iframe.
 *
 * <p>This View will always have one page itself and will always contain an iframe component.  The location
 * property allows ease of setting the url for the iframe.  If the site being shown in the iframe is a KRAD View
 * itself, the default bean for this class will attempt to pass a url parameter notifying the View that it is being
 * shown in an iframe; this can be used in SpringEL to invoke special logic (such as not rendering some components,
 * like the app header)</p>
 */
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
    public void performApplyModel(Object model, Component parent) {
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
    @BeanTagAttribute(name = "location", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
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
     * Convenience setter for setting the href (full URL) of the location object
     *
     * @param href URL for location option
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
    public Iframe getIframe() {
        return iframe;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.IframeView#getIframe()
     */
    public void setIframe(Iframe iframe) {
        this.iframe = iframe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(iframe);
        return components;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        IframeView viewCopy = (IframeView) component;

        viewCopy.setIframe((Iframe) iframe.copy());
        viewCopy.setLocation((UrlInfo) location.copy());
    }
}
