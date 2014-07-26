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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.component.DelayedCopy;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ExpressionUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.widget.Disclosure;
import org.kuali.rice.krad.uif.widget.Help;
import org.kuali.rice.krad.uif.widget.Scrollpane;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * Container that holds a list of <code>Field</code> or other <code>Group</code>
 * instances
 *
 * <p>
 * Groups can exist at different levels of the <code>View</code>, providing
 * conceptual groupings such as the page, section, and group. In addition, other
 * group types can be created to add behavior like collection support
 * </p>
 *
 * <p>
 * <code>Group</code> implementation has properties for defaulting the binding
 * information (such as the parent object path and a binding prefix) for the
 * fields it contains. During the phase these properties (if given) are set on
 * the fields contained in the <code>Group</code> that implement
 * <code>DataBinding</code>, unless they have already been set on the field.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "group", parent = "Uif-GroupBase"),
        @BeanTag(name = "boxGroup", parent = "Uif-BoxGroupBase"),
        @BeanTag(name = "verticalGroup", parent = "Uif-VerticalBoxGroup"),
        @BeanTag(name = "verticalSection", parent = "Uif-VerticalBoxSection"),
        @BeanTag(name = "verticalSubSection", parent = "Uif-VerticalBoxSubSection"),
        @BeanTag(name = "disclosureVerticalSection", parent = "Uif-Disclosure-VerticalBoxSection"),
        @BeanTag(name = "disclosureVerticalSubSection", parent = "Uif-Disclosure-VerticalBoxSubSection"),
        @BeanTag(name = "horizontalGroup", parent = "Uif-HorizontalBoxGroup"),
        @BeanTag(name = "horizontalSection", parent = "Uif-HorizontalBoxSection"),
        @BeanTag(name = "horizontalSubSection", parent = "Uif-HorizontalBoxSubSection"),
        @BeanTag(name = "disclosureHorizontalSection", parent = "Uif-Disclosure-HorizontalBoxSection"),
        @BeanTag(name = "disclosureHorizontalSubSection", parent = "Uif-Disclosure-HorizontalBoxSubSection"),
        @BeanTag(name = "grid", parent = "Uif-GridGroup"),
        @BeanTag(name = "gridSection", parent = "Uif-GridSection"),
        @BeanTag(name = "gridSubSection", parent = "Uif-GridSubSection"),
        @BeanTag(name = "disclosureGridSection", parent = "Uif-Disclosure-GridSection"),
        @BeanTag(name = "cssGrid", parent = "Uif-CssGridGroup"),
        @BeanTag(name = "section", parent = "Uif-CssGridSection"),
        @BeanTag(name = "subSection", parent = "Uif-CssGridSubSection"),
        @BeanTag(name = "section1Col", parent = "Uif-CssGridSection-1FieldLabelColumn"),
        @BeanTag(name = "section2Col", parent = "Uif-CssGridSection-2FieldLabelColumn"),
        @BeanTag(name = "section3Col", parent = "Uif-CssGridSection-3FieldLabelColumn"),
        @BeanTag(name = "subSection1Col", parent = "Uif-CssGridSubSection-1FieldLabelColumn"),
        @BeanTag(name = "subSection2Col", parent = "Uif-CssGridSubSection-2FieldLabelColumn"),
        @BeanTag(name = "subSection3Col", parent = "Uif-CssGridSubSection-3FieldLabelColumn"),
        @BeanTag(name = "list", parent = "Uif-ListGroup"),
        @BeanTag(name = "listSection", parent = "Uif-ListSection"),
        @BeanTag(name = "listSubSection", parent = "Uif-ListSubSection"),
        @BeanTag(name = "disclosureListSection", parent = "Uif-Disclosure-ListSection"),
        @BeanTag(name = "disclosureListSubSection", parent = "Uif-Disclosure-ListSubSection"),
        @BeanTag(name = "collectionGridItem", parent = "Uif-CollectionGridItem"),
        @BeanTag(name = "collectionVerticalBoxItem", parent = "Uif-CollectionVerticalBoxItem"),
        @BeanTag(name = "collectionHorizontalBoxItem", parent = "Uif-CollectionHorizontalBoxItem"),
        @BeanTag(name = "headerUpperGroup", parent = "Uif-HeaderUpperGroup"),
        @BeanTag(name = "headerRightGroup", parent = "Uif-HeaderRightGroup"),
        @BeanTag(name = "headerLowerGroup", parent = "Uif-HeaderLowerGroup"),
        @BeanTag(name = "footer", parent = "Uif-FooterBase"),
        @BeanTag(name = "formFooter", parent = "Uif-FormFooter"),
        @BeanTag(name = "maintenanceGrid", parent = "Uif-MaintenanceGridGroup"),
        @BeanTag(name = "maintenanceHorizontalGroup", parent = "Uif-MaintenanceHorizontalBoxGroup"),
        @BeanTag(name = "maintenanceVerticalGroup", parent = "Uif-MaintenanceVerticalBoxGroup"),
        @BeanTag(name = "maintenanceGridSection", parent = "Uif-MaintenanceGridSection"),
        @BeanTag(name = "maintenanceGridSubSection", parent = "Uif-MaintenanceGridSubSection"),
        @BeanTag(name = "maintenanceHorizontalSection", parent = "Uif-MaintenanceHorizontalBoxSection"),
        @BeanTag(name = "maintenanceVerticalSection", parent = "Uif-MaintenanceVerticalBoxSection"),
        @BeanTag(name = "maintenanceHorizontalSubSection", parent = "Uif-MaintenanceHorizontalBoxSubSection"),
        @BeanTag(name = "maintenanceVerticalSubSection", parent = "Uif-MaintenanceVerticalBoxSubSection")})
public class GroupBase extends ContainerBase implements Group {
    private static final long serialVersionUID = 7953641325356535509L;

    private String fieldBindByNamePrefix;
    private String fieldBindingObjectPath;

    @DelayedCopy
    private Disclosure disclosure;
    private Scrollpane scrollpane;

    private List<? extends Component> items;

    private String wrapperTag;

    /**
     * Default Constructor
     */
    public GroupBase() {
        items = Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        if (isAjaxDisclosureGroup()) {
            this.setItems(new ArrayList<Component>());
        }

        super.performInitialization(model);

        Iterator<? extends Component> itemIterator = getItems().iterator();
        while (itemIterator.hasNext()) {
            Component component = itemIterator.next();

            if (component == null) {
                continue;
            }

            String excludeUnless = component.getExcludeUnless();
            if (StringUtils.isNotBlank(excludeUnless) &&
                    !Boolean.TRUE.equals(ObjectPropertyUtils.getPropertyValue(model, excludeUnless))) {
                itemIterator.remove();
                continue;
            }

            String excludeIf = component.getExcludeIf();
            if (StringUtils.isNotBlank(excludeIf) &&
                    Boolean.TRUE.equals(ObjectPropertyUtils.getPropertyValue(model, excludeIf))) {
                itemIterator.remove();
                continue;
            }

            // append group's field bind by name prefix (if set) to each
            // attribute field's binding prefix
            if (component instanceof DataBinding) {
                DataBinding dataBinding = (DataBinding) component;

                if (StringUtils.isNotBlank(getFieldBindByNamePrefix())) {
                    String bindByNamePrefixToSet = getFieldBindByNamePrefix();

                    if (StringUtils.isNotBlank(dataBinding.getBindingInfo().getBindByNamePrefix())) {
                        bindByNamePrefixToSet += "." + dataBinding.getBindingInfo().getBindByNamePrefix();
                    }
                    dataBinding.getBindingInfo().setBindByNamePrefix(bindByNamePrefixToSet);
                }

                if (StringUtils.isNotBlank(fieldBindingObjectPath) && StringUtils.isBlank(
                        dataBinding.getBindingInfo().getBindingObjectPath())) {
                    dataBinding.getBindingInfo().setBindingObjectPath(fieldBindingObjectPath);
                }
            }
            // set on FieldGroup's group to recursively set AttributeFields
            else if (component instanceof FieldGroup) {
                FieldGroup fieldGroup = (FieldGroup) component;

                if (fieldGroup.getGroup() != null) {
                    if (StringUtils.isBlank(fieldGroup.getGroup().getFieldBindByNamePrefix())) {
                        fieldGroup.getGroup().setFieldBindByNamePrefix(fieldBindByNamePrefix);
                    }
                    if (StringUtils.isBlank(fieldGroup.getGroup().getFieldBindingObjectPath())) {
                        fieldGroup.getGroup().setFieldBindingObjectPath(fieldBindingObjectPath);
                    }
                }
            } else if (component instanceof Group) {
                Group subGroup = (Group) component;
                if (StringUtils.isNotBlank(getFieldBindByNamePrefix())) {
                    if (StringUtils.isNotBlank(subGroup.getFieldBindByNamePrefix())) {
                        subGroup.setFieldBindByNamePrefix(
                                getFieldBindByNamePrefix() + "." + subGroup.getFieldBindByNamePrefix());
                    } else {
                        subGroup.setFieldBindByNamePrefix(getFieldBindByNamePrefix());
                    }
                }
                if (StringUtils.isNotBlank(getFieldBindingObjectPath())) {
                    if (StringUtils.isNotBlank(subGroup.getFieldBindingObjectPath())) {
                        subGroup.setFieldBindingObjectPath(
                                getFieldBindingObjectPath() + "." + subGroup.getFieldBindingObjectPath());
                    } else {
                        subGroup.setFieldBindingObjectPath(getFieldBindingObjectPath());
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterEvaluateExpression() {
        super.afterEvaluateExpression();

        if (getReadOnly() == null) {
            Component parent = ViewLifecycle.getPhase().getParent();
            setReadOnly(parent == null ? null : parent.getReadOnly());
        }
    }

    /**
     * Sets the section boolean to true if this group has a rendering header with text
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (StringUtils.isBlank(wrapperTag) && StringUtils.isNotBlank(this.getHeaderText()) && this.getHeader()
                .isRender()) {
            wrapperTag = UifConstants.WrapperTags.SECTION;
        } else if (StringUtils.isBlank(wrapperTag)) {
            wrapperTag = UifConstants.WrapperTags.DIV;
        }

        setNestedComponentId(getInstructionalMessage(), this.getId() + UifConstants.IdSuffixes.INSTRUCTIONAL);
        setNestedComponentId(getHeader(), this.getId() + UifConstants.IdSuffixes.HEADER_WRAPPER);
        setNestedComponentId(getHelp(), this.getId() + UifConstants.IdSuffixes.HELP_WRAPPER);

        if (getHelp() != null && getHelp().getHelpAction() != null) {
            setNestedComponentId(getHelp().getHelpAction(), this.getId() + UifConstants.IdSuffixes.HELP);
        }
    }

    /**
     * Helper method for setting a new ID for the nested components
     *
     * @param component component to adjust ID for
     * @param newId
     */
    protected void setNestedComponentId(Component component, String newId) {
        if (component != null) {
            component.setId(newId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> supportedComponents = new HashSet<Class<? extends Component>>();
        supportedComponents.add(Field.class);
        supportedComponents.add(Group.class);

        return supportedComponents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getComponentTypeName() {
        return "group";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getFieldBindByNamePrefix() {
        return this.fieldBindByNamePrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFieldBindByNamePrefix(String fieldBindByNamePrefix) {
        this.fieldBindByNamePrefix = fieldBindByNamePrefix;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    public String getFieldBindingObjectPath() {
        return this.fieldBindingObjectPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFieldBindingObjectPath(String fieldBindingObjectPath) {
        this.fieldBindingObjectPath = fieldBindingObjectPath;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    public Disclosure getDisclosure() {
        return this.disclosure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisclosure(Disclosure disclosure) {
        this.disclosure = disclosure;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    public Scrollpane getScrollpane() {
        return this.scrollpane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScrollpane(Scrollpane scrollpane) {
        this.scrollpane = scrollpane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<? extends Component> getItems() {
        if (items == Collections.EMPTY_LIST && isMutable(true)) {
            items = new ArrayList<Component>();
        }

        return this.items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setItems(List<? extends Component> items) {
        if (items == null) {
            this.items = Collections.emptyList();
        } else if (items.contains(this)) {
            throw new IllegalArgumentException("Attempted to add group to itself");
        } else {
            this.items = items;
        }
    }

    /**
     * Defines the html tag that will wrap this group, if left blank, this will automatically be set
     * by the framework to the appropriate tag (in most cases section or div)
     *
     * @return the html tag used to wrap this group
     */
    @BeanTagAttribute
    public String getWrapperTag() {
        return wrapperTag;
    }

    /**
     * @see org.kuali.rice.krad.uif.container.GroupBase#getWrapperTag()
     */
    public void setWrapperTag(String wrapperTag) {
        this.wrapperTag = wrapperTag;
    }

    /**
     * Returns true if this group has a Disclosure widget that is currently closed and using ajax disclosure
     *
     * @return true if this group has a Disclosure widget that is currently closed and using ajax disclosure
     */
    protected boolean isAjaxDisclosureGroup() {
        ViewModel model = (ViewModel) ViewLifecycle.getModel();
        View view = ViewLifecycle.getView();

        ExpressionUtils.populatePropertyExpressionsFromGraph(this);
        // Evaluate the disclosure.defaultOpen expression early so that ajax disclosure mechanisms
        // can take its state into account when replacing items with Placeholders in ContainerBase#performInitialization
        if (this.getDisclosure() != null && StringUtils.isNotBlank(this.getDisclosure().getPropertyExpression(
                UifPropertyPaths.DEFAULT_OPEN))){
            ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

            String expression = this.getDisclosure().getPropertyExpression(UifPropertyPaths.DEFAULT_OPEN);
            expression = expressionEvaluator.replaceBindingPrefixes(view, this, expression);

            expression = expressionEvaluator.evaluateExpressionTemplate(this.getDisclosure().getContext(), expression);
            ObjectPropertyUtils.setPropertyValue(this.getDisclosure(), UifPropertyPaths.DEFAULT_OPEN, expression);
        }

        // Ensure that the disclosure has the correct state before evaluate ajax-based placeholder replacement
        if (this.getDisclosure() != null) {
            KRADUtils.syncClientSideStateForComponent(this.getDisclosure(), model.getClientStateForSyncing());
        }

        // This this will be replaced with a PlaceholderDisclosure group if it is not opened and the
        // ajaxRetrievalWhenOpened option is set
        return !this.isRetrieveViaAjax() && this.getDisclosure() != null && this.getDisclosure()
                        .isAjaxRetrievalWhenOpened() && !this.getDisclosure().isDefaultOpen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that no invalid items are present
        for (int i = 0; i < getItems().size(); i++) {
            if (getItems().get(i).getClass() == PageGroup.class || getItems().get(i).getClass()
                    == TabNavigationGroup.class) {
                String currentValues[] = {"item(" + i + ").class =" + getItems().get(i).getClass()};
                tracer.createError("Items in Group cannot be PageGroup or NaviagtionGroup", currentValues);
            }
        }

        // Checks that the layout manager is set
        if (getLayoutManager() == null) {
            if (Validator.checkExpressions(this, "layoutManager")) {
                String currentValues[] = {"layoutManager = " + getLayoutManager()};
                tracer.createError("LayoutManager must be set", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRenderLoading() {
        return disclosure != null && disclosure.isAjaxRetrievalWhenOpened() && (!disclosure.isRender() || !disclosure
                .isDefaultOpen());
    }

}
