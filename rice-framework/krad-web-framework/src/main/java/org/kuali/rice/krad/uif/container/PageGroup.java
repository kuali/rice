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
package org.kuali.rice.krad.uif.container;

import java.util.List;

import org.kuali.rice.krad.uif.element.BreadcrumbItem;
import org.kuali.rice.krad.uif.element.BreadcrumbOptions;
import org.kuali.rice.krad.uif.element.PageBreadcrumbOptions;

/**
 * Interface for top-level page components, to be used as items in a multi-page view. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface PageGroup extends Group {

    /**
     * When this is true, the first field of the kualiForm will be focused by
     * default, unless the parameter focusId is set on the form (by an
     * actionField), then that field will be focused instead. When this setting
     * if false, no field will be focused.
     *
     * @return the autoFocus
     */
    boolean isAutoFocus();

    /**
     * @param autoFocus the autoFocus to set
     */
    void setAutoFocus(boolean autoFocus);

    /**
     * The breadcrumbOptions specific to this page.
     *
     * <p>
     * Important note: breadcrumbOptions for preViewBreadcrumbs, prePageBreadcrumbs, and
     * breadcrumbOverrides are inherited from the View if not explicitly set from the PageGroup level's
     * breadcrumbOptions
     * (if they contain a value at the view level and the property is null at the page level - default behavior).
     * Explicitly providing an empty list or setting these properties at the PageGroup level will
     * override this inheritance.
     * </p>
     *
     * @return the {@link BreadcrumbOptions}
     */
    PageBreadcrumbOptions getBreadcrumbOptions();

    /**
     * Set the breadcrumbOptions
     *
     * @param breadcrumbOptions
     */
    void setBreadcrumbOptions(PageBreadcrumbOptions breadcrumbOptions);

    /**
     * Gets the breadcrumb items indicating a homeward path.
     *
     * @return breadcrumb items
     */
    List<BreadcrumbItem> getHomewardPathBreadcrumbs();

    /**
     * Gets the breadcrumb items leading to the current view.
     *
     * @return breadcrumb items
     */
    List<BreadcrumbItem> getPreViewBreadcrumbs();

    /**
     * Gets the breadcrumb items leading to the current page.
     *
     * @return breadcrumb items
     */
    List<BreadcrumbItem> getPrePageBreadcrumbs();

    /**
     * Gets the breadcrumb items overrides.
     *
     * @return breadcrumb items
     */
    List<BreadcrumbItem> getBreadcrumbOverrides();

    /**
     * The breadcrumbItem for this page.  This is the item that (generally) appears last in the breadcrumb list.
     *
     * <p>
     * If a label is not explicitly defined, the label is retrieved from the headerText of the PageGroup's header.
     * If this is also not defined, the breadcrumbItem is NOT rendered.  The url properties do not need to be provided
     * for this breadcrumbItem because it is automatically determined based on the this PageGroup's pageId, viewId,
     * and controllerMapping retrieved from the initial controller request.
     * </p>
     *
     * @return the breadcrumbItem for this page
     */
    BreadcrumbItem getBreadcrumbItem();

    /**
     * Set the breadcrumbItem for this PageGroup
     *
     * @param breadcrumbItem
     */
    void setBreadcrumbItem(BreadcrumbItem breadcrumbItem);

    /**
     * When true, this page's footer will become sticky (fixed) at the bottom of the window
     *
     * @return true if the page footer is sticky, false otherwise
     */
    boolean isStickyFooter();

    /**
     * Set to true to make this page's footer sticky
     *
     * @param stickyFooter
     */
    void setStickyFooter(boolean stickyFooter);

    /**
     * Specifies the URL the view's form should post to
     *
     * <p>
     * Any valid form post URL (full or relative) can be specified. If left
     * empty, the form will be posted to the same URL of the preceding request
     * URL.
     * </p>
     *
     * @return post URL
     */
    String getFormPostUrl();

    /**
     * Setter for the form post URL
     *
     * @param formPostUrl
     */
    void setFormPostUrl(String formPostUrl);

}