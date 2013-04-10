/*
 * Copyright 2006-2013 The Kuali Foundation
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

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;

/**
 * BreadcrumbOptions specific to page.  Render options are only available at the page level.
 */
@BeanTag(name = "pageBreadcrumbOptions-bean", parent = "Uif-PageBreadcrumbOptions")
public class PageBreadcrumbOptions extends BreadcrumbOptions {
    private static final long serialVersionUID = -5666730356781875858L;

    //render options
    private boolean renderViewBreadcrumb;
    private boolean renderPreViewBreadcrumbs;
    private boolean renderPrePageBreadcrumbs;
    private boolean renderParentLocations;

    /**
     * Whether or not to render the view breadcrumb at this level
     *
     * @return true if rendering the view breadcrumb, false otherwise
     */
    @BeanTagAttribute(name = "renderViewBreadcrumb")
    public boolean isRenderViewBreadcrumb() {
        return renderViewBreadcrumb;
    }

    /**
     * Set renderViewBreadcrumb
     *
     * @param renderViewBreadcrumb
     */
    public void setRenderViewBreadcrumb(boolean renderViewBreadcrumb) {
        this.renderViewBreadcrumb = renderViewBreadcrumb;
    }

    /**
     * If true, render the preViewBreadcrumbs (if any are set), otherwise do not render them
     *
     * @return true if rendering preViewBreadcrumbs, false otherwise
     */
    @BeanTagAttribute(name = "renderPreViewBreadcrumbs")
    public boolean isRenderPreViewBreadcrumbs() {
        return renderPreViewBreadcrumbs;
    }

    /**
     * Set renderPreViewBreadcrumbs
     *
     * @param renderPreViewBreadcrumbs
     */
    public void setRenderPreViewBreadcrumbs(boolean renderPreViewBreadcrumbs) {
        this.renderPreViewBreadcrumbs = renderPreViewBreadcrumbs;
    }

    /**
     * If true, render the prePageBreadcrumbs (if any are set), otherwise do not render them
     *
     * @return true if rendering prePageBreadcrumbs, false otherwise
     */
    @BeanTagAttribute(name = "renderPrePageBreadcrumbs")
    public boolean isRenderPrePageBreadcrumbs() {
        return renderPrePageBreadcrumbs;
    }

    /**
     * Set renderPrePageBreadcrumbs
     *
     * @param renderPrePageBreadcrumbs
     */
    public void setRenderPrePageBreadcrumbs(boolean renderPrePageBreadcrumbs) {
        this.renderPrePageBreadcrumbs = renderPrePageBreadcrumbs;
    }

    /**
     * If true, render the parent location breadcrumbs.  These BreadcrumbItems are automatically generated based on the
     * view's parentLocation property settings by traversing parent views/pages or based on a history path.
     *
     * @return true if rendering the parent location breadcrumbs, false otherwise
     */
    @BeanTagAttribute(name = "renderParentLocations")
    public boolean isRenderParentLocations() {
        return renderParentLocations;
    }

    /**
     * Set renderParentLocations
     *
     * @param renderParentLocations
     */
    public void setRenderParentLocations(boolean renderParentLocations) {
        this.renderParentLocations = renderParentLocations;
    }
}
