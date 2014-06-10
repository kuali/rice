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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Header;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.element.BreadcrumbItem;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.element.PageBreadcrumbOptions;
import org.kuali.rice.krad.uif.view.FormView;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * A PageGroup represents a page of a View.
 *
 * <p>
 * PageGroups should only be used with a View component.  The contain the main content that will be seen by the
 * user using the View.  Like all other groups, PageGroup can contain items, headers and footers.  Pages also
 * have their own BreadcrumbItem.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "page", parent = "Uif-Page"),
        @BeanTag(name = "documentPage", parent = "Uif-DocumentPage"),
        @BeanTag(name = "inquiryPage", parent = "Uif-InquiryPage"),
        @BeanTag(name = "maintenancePage", parent = "Uif-MaintenancePage")})
public class PageGroupBase extends GroupBase implements PageGroup {
    private static final long serialVersionUID = 7571981300587270274L;

    private boolean autoFocus = false;

    private PageBreadcrumbOptions breadcrumbOptions;
    private BreadcrumbItem breadcrumbItem;
    private boolean stickyFooter;
    private String formPostUrl;


    /**
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        //check to see if one of the items is a page, if so throw an exception
        for (Component item : this.getItems()) {
            if (item != null && item instanceof PageGroup) {
                throw new RuntimeException("The page with id='"
                        + this.getId()
                        + "' contains a page with id='"
                        + item.getId()
                        + "'.  Nesting a page within a page is not allowed since only one "
                        + "page's content can be shown on the View "
                        + "at a time.  This may have been caused by possible misuse of the singlePageView flag (when "
                        + "this flag is true, items set on the View become items of the single page.  Instead use "
                        + "the page property on the View to set the page being used).");
            }
        }

        breadcrumbOptions.setupBreadcrumbs(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        if (StringUtils.isBlank(this.getWrapperTag())) {
            this.setWrapperTag(UifConstants.WrapperTags.MAIN);
        }

        super.performFinalize(model, parent);

        UifFormBase formBase = (UifFormBase) model;

        // If AutoFocus then set the focus_id to FIRST field, unless focus_id is also specified
        if (isAutoFocus() && StringUtils.isNotBlank(formBase.getFocusId())) {
            this.addDataAttribute(UifConstants.ActionDataAttributes.FOCUS_ID, formBase.getFocusId());
        } else if (isAutoFocus()) {
            this.addDataAttribute(UifConstants.ActionDataAttributes.FOCUS_ID, UifConstants.Order.FIRST.name());
        }

        // Add jumpToId as a data attribute
        if (StringUtils.isNotBlank(formBase.getJumpToId())) {
            this.addDataAttribute(UifConstants.ActionDataAttributes.JUMP_TO_ID, formBase.getJumpToId());
        }

        // Add jumpToName as a data attribute
        if (StringUtils.isNotBlank(formBase.getJumpToName())) {
            this.addDataAttribute(UifConstants.ActionDataAttributes.JUMP_TO_NAME, formBase.getJumpToName());
        }

        this.addDataAttribute(UifConstants.DataAttributes.ROLE, UifConstants.RoleTypes.PAGE);

        String prefixScript = "";
        if (this.getOnDocumentReadyScript() != null) {
            prefixScript = this.getOnDocumentReadyScript();
        }

        View view = ViewLifecycle.getView();
        if (view instanceof FormView && ((FormView) view).isValidateClientSide()) {
            this.setOnDocumentReadyScript(prefixScript + "\nsetupPage(true);");
        } else {
            this.setOnDocumentReadyScript(prefixScript + "\nsetupPage(false);");
        }

        breadcrumbOptions.finalizeBreadcrumbs(model, this, breadcrumbItem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAdditionalTemplates() {
        List<String> additionalTemplates = super.getAdditionalTemplates();

        Header viewHeader = ViewLifecycle.getView().getHeader();
        if (viewHeader != null) {
            if (additionalTemplates.isEmpty()) {
                additionalTemplates = new ArrayList<String>();
            }
            additionalTemplates.add(viewHeader.getTemplate());
        }

        return additionalTemplates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "autoFocus")
    public boolean isAutoFocus() {
        return this.autoFocus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public PageBreadcrumbOptions getBreadcrumbOptions() {
        return breadcrumbOptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBreadcrumbOptions(PageBreadcrumbOptions breadcrumbOptions) {
        this.breadcrumbOptions = breadcrumbOptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BreadcrumbItem> getHomewardPathBreadcrumbs() {
        return breadcrumbOptions == null ? null : breadcrumbOptions.getHomewardPathBreadcrumbs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BreadcrumbItem> getPreViewBreadcrumbs() {
        return breadcrumbOptions == null ? null : breadcrumbOptions.getPreViewBreadcrumbs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BreadcrumbItem> getPrePageBreadcrumbs() {
        return breadcrumbOptions == null ? null : breadcrumbOptions.getPrePageBreadcrumbs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BreadcrumbItem> getBreadcrumbOverrides() {
        return breadcrumbOptions == null ? null : breadcrumbOptions.getBreadcrumbOverrides();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public BreadcrumbItem getBreadcrumbItem() {
        return breadcrumbItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBreadcrumbItem(BreadcrumbItem breadcrumbItem) {
        this.breadcrumbItem = breadcrumbItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isStickyFooter() {
        return stickyFooter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStickyFooter(boolean stickyFooter) {
        this.stickyFooter = stickyFooter;

        if (this.getFooter() != null) {
            this.getFooter().addDataAttribute(UifConstants.DataAttributes.STICKY_FOOTER, Boolean.toString(
                    stickyFooter));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getFormPostUrl() {
        return formPostUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormPostUrl(String formPostUrl) {
        this.formPostUrl = formPostUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that no invalid items are present
        for (int i = 0; i < getItems().size(); i++) {
            if (PageGroup.class.isAssignableFrom(getItems().get(i).getClass())
                    || TabNavigationGroup.class.isAssignableFrom(getItems().get(i).getClass())) {
                String currentValues[] = {"item(" + i + ").class =" + getItems().get(i).getClass()};
                tracer.createError("Items in PageGroup cannot be PageGroup or NaviagtionGroup", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }

}
