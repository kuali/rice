/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.BreadcrumbItem;
import org.kuali.rice.krad.uif.util.BreadcrumbOptions;
import org.kuali.rice.krad.uif.util.PageBreadcrumbOptions;
import org.kuali.rice.krad.uif.view.FormView;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "page-bean", parent = "Uif-Page"),
        @BeanTag(name = "disclosure-page-bean", parent = "Uif-Disclosure-Page"),
        @BeanTag(name = "documentPage-bean", parent = "Uif-DocumentPage"),
        @BeanTag(name = "inquiryPage-bean", parent = "Uif-InquiryPage"),
        @BeanTag(name = "lookupPage-bean", parent = "Uif-LookupPage"),
        @BeanTag(name = "maintenancePage-bean", parent = "Uif-MaintenancePage")})
public class PageGroup extends Group {
    private static final long serialVersionUID = 7571981300587270274L;

    private boolean autoFocus = false;

    private PageBreadcrumbOptions breadcrumbOptions;
    private BreadcrumbItem breadcrumbItem;
    private boolean stickyFooter;

    /**
     * Setup various breadcrumbOptions inherited from view if not explicitly set.
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        setupBreadcrumbs(view, model);
    }

    /**
     * Perform finalize here adds to its document ready script the
     * setupValidator js function for setting up the validator for this view.  Also setup various breadcrumb related
     * settings for the page.
     *
     * @see ContainerBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        this.addDataAttribute(UifConstants.DataAttributes.TYPE, "Page");

        String prefixScript = "";
        if (this.getOnDocumentReadyScript() != null) {
            prefixScript = this.getOnDocumentReadyScript();
        }

        if (view instanceof FormView && ((FormView) view).isValidateClientSide()) {
            this.setOnDocumentReadyScript(prefixScript + "\nsetupPage(true);");
        } else {
            this.setOnDocumentReadyScript(prefixScript + "\nsetupPage(false);");
        }

        finalizeBreadcrumbs(view, model);
    }

    /**
     * Setup the BreadcrumbOptions and BreadcrumbItem for a PageGroup.  To be called from performInitialization.
     *
     * @param view the page's View
     * @param model the model
     */
    protected void setupBreadcrumbs(View view, Object model) {
        BreadcrumbOptions viewBreadcrumbOptions = view.getBreadcrumbOptions();

        //inherit prePageBreadcrumbs, preViewBreadcrumbs, and overrides from the view if not set
        if (breadcrumbOptions.getHomewardPathBreadcrumbs() == null
                && viewBreadcrumbOptions != null
                && viewBreadcrumbOptions.getHomewardPathBreadcrumbs() != null) {
            breadcrumbOptions.setHomewardPathBreadcrumbs(viewBreadcrumbOptions.getHomewardPathBreadcrumbs());

            for (BreadcrumbItem item: breadcrumbOptions.getHomewardPathBreadcrumbs()){
                view.assignComponentIds(item);
            }
        }

        if (breadcrumbOptions.getPrePageBreadcrumbs() == null
                && viewBreadcrumbOptions != null
                && viewBreadcrumbOptions.getPrePageBreadcrumbs() != null) {
            breadcrumbOptions.setPrePageBreadcrumbs(viewBreadcrumbOptions.getPrePageBreadcrumbs());

            for (BreadcrumbItem item: breadcrumbOptions.getPrePageBreadcrumbs()){
                view.assignComponentIds(item);
            }
        }

        if (breadcrumbOptions.getPreViewBreadcrumbs() == null
                && viewBreadcrumbOptions != null
                && viewBreadcrumbOptions.getPreViewBreadcrumbs() != null) {
            breadcrumbOptions.setPreViewBreadcrumbs(viewBreadcrumbOptions.getPreViewBreadcrumbs());

            for (BreadcrumbItem item: breadcrumbOptions.getPreViewBreadcrumbs()){
                view.assignComponentIds(item);
            }
        }

        if (breadcrumbOptions.getBreadcrumbOverrides() == null
                && viewBreadcrumbOptions != null
                && viewBreadcrumbOptions.getBreadcrumbOverrides() != null) {
            breadcrumbOptions.setBreadcrumbOverrides(viewBreadcrumbOptions.getBreadcrumbOverrides());

            for (BreadcrumbItem item: breadcrumbOptions.getBreadcrumbOverrides()){
                view.assignComponentIds(item);
            }
        }
    }

    /**
     * Finalize the setup of the BreadcrumbOptions and the BreadcrumbItem for the PageGroup.  To be called from the
     * performFinalize method.
     *
     * @param view the page's View
     * @param model the model
     */
    protected void finalizeBreadcrumbs(View view, Object model) {
        //set breadcrumbItem label same as the header, if not set
        if (StringUtils.isBlank(breadcrumbItem.getLabel()) && this.getHeader() != null && StringUtils.isNotBlank(
                this.getHeader().getHeaderText())) {
            breadcrumbItem.setLabel(this.getHeader().getHeaderText());
        }

        //if label still blank, dont render
        if (StringUtils.isBlank(breadcrumbItem.getLabel())) {
            breadcrumbItem.setRender(false);
        }

        //special breadcrumb request param handling
        if (breadcrumbItem.getUrl().getControllerMapping() == null
                && breadcrumbItem.getUrl().getViewId() == null
                && model instanceof UifFormBase
                && breadcrumbItem.getUrl().getRequestParameters() == null
                && ((UifFormBase) model).getInitialRequestParameters() != null) {
            //add the current request parameters if controllerMapping, viewId, and requestParams are null
            //(this means that no explicit breadcrumbItem customization was set)
            Map<String, String> requestParameters = ((UifFormBase) model).getInitialRequestParameters();

            //remove ajax properties because breadcrumb should always be a full view request
            requestParameters.remove("ajaxReturnType");
            requestParameters.remove("ajaxRequest");

            //remove pageId because this should be set by the BreadcrumbItem setting
            requestParameters.remove("pageId");

            breadcrumbItem.getUrl().setRequestParameters(requestParameters);
        }

        //form key handling
        if (breadcrumbItem.getUrl().getFormKey() == null
                && model instanceof UifFormBase
                && ((UifFormBase) model).getFormKey() != null) {
            breadcrumbItem.getUrl().setFormKey(((UifFormBase) model).getFormKey());
        }

        //automatically set breadcrumbItem UifUrl properties below, if not set
        if (breadcrumbItem.getUrl().getControllerMapping() == null && model instanceof UifFormBase) {
            breadcrumbItem.getUrl().setControllerMapping(((UifFormBase) model).getControllerMapping());
        }

        if (breadcrumbItem.getUrl().getViewId() == null) {
            breadcrumbItem.getUrl().setViewId(view.getId());
        }

        if (breadcrumbItem.getUrl().getPageId() == null) {
            breadcrumbItem.getUrl().setPageId(this.getId());
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = new ArrayList<Component>();

        components.add(breadcrumbItem);

        if (breadcrumbOptions != null) {
            if (breadcrumbOptions.getHomewardPathBreadcrumbs() != null) {
                components.addAll(breadcrumbOptions.getHomewardPathBreadcrumbs());
            }

            if (breadcrumbOptions.getPreViewBreadcrumbs() != null) {
                components.addAll(breadcrumbOptions.getPreViewBreadcrumbs());
            }

            if (breadcrumbOptions.getPrePageBreadcrumbs() != null) {
                components.addAll(breadcrumbOptions.getPrePageBreadcrumbs());
            }

            if (breadcrumbOptions.getBreadcrumbOverrides() != null) {
                components.addAll(breadcrumbOptions.getBreadcrumbOverrides());
            }
        }

        components.addAll(super.getComponentsForLifecycle());

        return components;
    }

    /**
     * When this is true, the first field of the kualiForm will be focused by
     * default, unless the parameter focusId is set on the form (by an
     * actionField), then that field will be focused instead. When this setting
     * if false, no field will be focused.
     *
     * @return the autoFocus
     */
    @BeanTagAttribute(name = "autoFocus")
    public boolean isAutoFocus() {
        return this.autoFocus;
    }

    /**
     * @param autoFocus the autoFocus to set
     */
    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that no invalid items are present
        for (int i = 0; i < getItems().size(); i++) {
            if (getItems().get(i).getClass() == PageGroup.class || getItems().get(i).getClass()
                    == NavigationGroup.class) {
                String currentValues[] = {"item(" + i + ").class =" + getItems().get(i).getClass()};
                tracer.createError("Items in PageGroup cannot be PageGroup or NaviagtionGroup", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }

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
     * @return the breadcrumbOptions
     */
    @BeanTagAttribute(name = "breadcrumbOptions", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public PageBreadcrumbOptions getBreadcrumbOptions() {
        return breadcrumbOptions;
    }

    /**
     * Set the breadcrumbOptions
     *
     * @param breadcrumbOptions
     */
    public void setBreadcrumbOptions(PageBreadcrumbOptions breadcrumbOptions) {
        this.breadcrumbOptions = breadcrumbOptions;
    }

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
    @BeanTagAttribute(name = "breadcrumbItem", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public BreadcrumbItem getBreadcrumbItem() {
        return breadcrumbItem;
    }

    /**
     * Set the breadcrumbItem for this PageGroup
     *
     * @param breadcrumbItem
     */
    public void setBreadcrumbItem(BreadcrumbItem breadcrumbItem) {
        this.breadcrumbItem = breadcrumbItem;
    }

    /**
     * When true, this page's footer will become sticky (fixed) at the bottom of the window.
     *
     * @return true if the page footer is sticky, false otherwise
     */
    @BeanTagAttribute(name = "stickyFooter")
    public boolean isStickyFooter() {
        return stickyFooter;
    }

    /**
     * Set to true to make this page's footer sticky
     *
     * @param stickyFooter
     */
    public void setStickyFooter(boolean stickyFooter) {
        this.stickyFooter = stickyFooter;
        if (this.getFooter() != null) {
            this.getFooter().addDataAttribute(UifConstants.DataAttributes.STICKY_FOOTER, Boolean.toString(
                    stickyFooter));
        }
    }
}
