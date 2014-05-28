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
package org.kuali.rice.krad.uif.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.component.KeepExpression;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.collections.LineBuilderContext;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.layout.collections.CollectionPagingHelper;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.widget.Pager;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * Layout manager that works with {@code CollectionGroup} containers and
 * renders the collection lines in a vertical row
 *
 * <p>
 * For each line of the collection, a {@code Group} instance is created.
 * The group header contains a label for the line (summary information), the
 * group fields are the collection line fields, and the group footer contains
 * the line actions. All the groups are rendered using the
 * {@code BoxLayoutManager} with vertical orientation.
 * </p>
 *
 * <p>
 * Modify the lineGroupPrototype to change header/footer styles or any other
 * customization for the line groups
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "stackedCollectionLayout-bean", parent = "Uif-StackedCollectionLayoutBase"),
        @BeanTag(name = "stackedCollectionLayout-withGridItems-bean",
                parent = "Uif-StackedCollectionLayout-WithGridItems"),
        @BeanTag(name = "stackedCollectionLayout-withBoxItems-bean",
                parent = "Uif-StackedCollectionLayout-WithBoxItems"),
        @BeanTag(name = "stackedCollectionLayout-list-bean", parent = "Uif-StackedCollectionLayout-List")})
public class StackedLayoutManagerBase extends LayoutManagerBase implements StackedLayoutManager {
    private static final long serialVersionUID = 4602368505430238846L;

    @KeepExpression
    private String summaryTitle;
    private List<String> summaryFields;

    private Group addLineGroup;
    private Group lineGroupPrototype;
    private FieldGroup subCollectionFieldGroupPrototype;
    private Field selectFieldPrototype;
    private Group wrapperGroup;
    private Pager pagerWidget;

    private List<Group> stackedGroups;

    private boolean actionsInLineGroup;

    public StackedLayoutManagerBase() {
        super();

        summaryFields = new ArrayList<String>();
        stackedGroups = new ArrayList<Group>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        stackedGroups = new ArrayList<Group>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement component) {
        super.performApplyModel(model, component);

        if (wrapperGroup != null) {
            wrapperGroup.setItems(stackedGroups);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement element) {
        super.performFinalize(model, element);

        boolean serverPagingEnabled =
                (element instanceof CollectionGroup) && ((CollectionGroup) element).isUseServerPaging();

        // set the appropriate page, total pages, and link script into the Pager
        if (serverPagingEnabled && this.getPagerWidget() != null) {
            CollectionLayoutUtils.setupPagerWidget(pagerWidget, (CollectionGroup) element, model);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildLine(LineBuilderContext lineBuilderContext) {
        List<Field> lineFields = lineBuilderContext.getLineFields();
        CollectionGroup collectionGroup = lineBuilderContext.getCollectionGroup();
        int lineIndex = lineBuilderContext.getLineIndex();
        String idSuffix = lineBuilderContext.getIdSuffix();
        Object currentLine = lineBuilderContext.getCurrentLine();
        List<? extends Component> actions = lineBuilderContext.getLineActions();
        String bindingPath = lineBuilderContext.getBindingPath();

        // construct new group
        Group lineGroup = null;
        if (lineBuilderContext.isAddLine()) {
            stackedGroups = new ArrayList<Group>();

            if (addLineGroup == null) {
                lineGroup = ComponentUtils.copy(lineGroupPrototype, idSuffix);
            } else {
                lineGroup = ComponentUtils.copy(getAddLineGroup(), idSuffix);
                lineGroup.addStyleClass(collectionGroup.getAddItemCssClass());
            }

            if (collectionGroup.isAddViaLightBox()) {
                String addLineGroupId = lineGroup.getId();
                if (StringUtils.isNotBlank(collectionGroup.getContainerIdSuffix())) {
                    addLineGroupId = addLineGroupId + collectionGroup.getContainerIdSuffix();
                }

                String actionScript = "showLightboxComponent('" + addLineGroupId + "');";
                if (StringUtils.isNotBlank(collectionGroup.getAddViaLightBoxAction().getActionScript())) {
                    actionScript = collectionGroup.getAddViaLightBoxAction().getActionScript() + actionScript;
                }
                collectionGroup.getAddViaLightBoxAction().setActionScript(actionScript);

                lineGroup.setStyle("display: none");
            }
        } else {
            lineGroup = ComponentUtils.copy(lineGroupPrototype, idSuffix);
        }

        if (((UifFormBase) lineBuilderContext.getModel()).isAddedCollectionItem(currentLine)) {
            lineGroup.addStyleClass(collectionGroup.getNewItemsCssClass());
        }

        // any actions that are attached to the group prototype (like the header) need to get action parameters
        // and context set for the collection line
        List<Action> lineGroupActions = ViewLifecycleUtils.getElementsOfTypeDeep(lineGroup, Action.class);
        if (lineGroupActions != null) {
            collectionGroup.getCollectionGroupBuilder().initializeActions(lineGroupActions, collectionGroup, lineIndex);
            ComponentUtils.updateContextsForLine(lineGroupActions, collectionGroup, currentLine, lineIndex, idSuffix);
        }

        ComponentUtils.updateContextForLine(lineGroup, collectionGroup, currentLine, lineIndex, idSuffix);

        // build header for the group
        if (lineBuilderContext.isAddLine()) {
            if (lineGroup.getHeader() != null) {
                Message headerMessage = ComponentUtils.copy(collectionGroup.getAddLineLabel());
                lineGroup.getHeader().setRichHeaderMessage(headerMessage);
            }
        } else {
            // get the collection for this group from the model
            List<Object> modelCollection = ObjectPropertyUtils.getPropertyValue(lineBuilderContext.getModel(),
                    ((DataBinding) collectionGroup).getBindingInfo().getBindingPath());

            String headerText = buildLineHeaderText(modelCollection.get(lineIndex), lineGroup);

            // don't set header if text is blank (could already be set by other means)
            if (StringUtils.isNotBlank(headerText) && lineGroup.getHeader() != null) {
                lineGroup.getHeader().setHeaderText(headerText);
            }
        }

        // stack all fields (including sub-collections) for the group
        List<Component> groupFields = new ArrayList<Component>();
        groupFields.addAll(lineFields);

        if (lineBuilderContext.getSubCollectionFields() != null) {
            groupFields.addAll(lineBuilderContext.getSubCollectionFields());
        }

        // set line actions on group footer
        if (collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly() && (lineGroup.getFooter() != null)) {
            // add the actions to the line group if isActionsInLineGroup flag is true
            if (isActionsInLineGroup()) {
                groupFields.addAll(actions);
                lineGroup.setRenderFooter(false);
            } else {
                lineGroup.getFooter().setItems(actions);
            }
        }

        lineGroup.setItems(groupFields);
        
        // Must evaluate the client-side state on the lineGroup's disclosure for PlaceholderDisclosureGroup processing
        if (lineBuilderContext.getModel() instanceof ViewModel){
            KRADUtils.syncClientSideStateForComponent(lineGroup.getDisclosure(),
                    ((ViewModel) lineBuilderContext.getModel()).getClientStateForSyncing());
        }

        stackedGroups.add(lineGroup);
    }

    /**
     * Builds the header text for the collection line
     *
     * <p>
     * Header text is built up by first the collection label, either specified
     * on the collection definition or retrieved from the dictionary. Then for
     * each summary field defined, the value from the model is retrieved and
     * added to the header.
     * </p>
     *
     * <p>
     * Note the {@link #getSummaryTitle()} field may have expressions defined, in which cause it will be copied to the
     * property expressions map to set the title for the line group (which will have the item context variable set)
     * </p>
     *
     * @param line Collection line containing data
     * @param lineGroup Group instance for rendering the line and whose title should be built
     * @return header text for line
     */
    protected String buildLineHeaderText(Object line, Group lineGroup) {
        // check for expression on summary title
        if (ViewLifecycle.getExpressionEvaluator().containsElPlaceholder(summaryTitle)) {
            lineGroup.getPropertyExpressions().put(UifPropertyPaths.HEADER_TEXT, summaryTitle);
            return null;
        }

        // build up line summary from declared field values and fixed title
        String summaryFieldString = "";
        for (String summaryField : summaryFields) {
            Object summaryFieldValue = ObjectPropertyUtils.getPropertyValue(line, summaryField);
            if (StringUtils.isNotBlank(summaryFieldString)) {
                summaryFieldString += " - ";
            }

            if (summaryFieldValue != null) {
                summaryFieldString += summaryFieldValue;
            } else {
                summaryFieldString += "Null";
            }
        }

        String headerText = summaryTitle;
        if (StringUtils.isNotBlank(summaryFieldString)) {
            headerText += " ( " + summaryFieldString + " )";
        }

        return headerText;
    }

    /**
     * Invokes {@link org.kuali.rice.krad.uif.layout.collections.CollectionPagingHelper} to carry out the
     * paging request.
     *
     * {@inheritDoc}
     */
    @Override
    public void processPagingRequest(Object model, CollectionGroup collectionGroup) {
        String pageNumber = ViewLifecycle.getRequest().getParameter(UifConstants.PageRequest.PAGE_NUMBER);

        CollectionPagingHelper pagingHelper = new CollectionPagingHelper();
        pagingHelper.processPagingRequest(ViewLifecycle.getView(), collectionGroup, (UifFormBase) model, pageNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Container> getSupportedContainer() {
        return CollectionGroup.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "summaryTitle")
    public String getSummaryTitle() {
        return this.summaryTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSummaryTitle(String summaryTitle) {
        this.summaryTitle = summaryTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "summaryFields", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getSummaryFields() {
        return this.summaryFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSummaryFields(List<String> summaryFields) {
        this.summaryFields = summaryFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute(name = "addLineGroup", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Group getAddLineGroup() {
        return this.addLineGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineGroup(Group addLineGroup) {
        this.addLineGroup = addLineGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute(name = "lineGroupPrototype", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Group getLineGroupPrototype() {
        return this.lineGroupPrototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineGroupPrototype(Group lineGroupPrototype) {
        this.lineGroupPrototype = lineGroupPrototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute(name = "subCollectionFieldGroupPrototype", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public FieldGroup getSubCollectionFieldGroupPrototype() {
        return this.subCollectionFieldGroupPrototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSubCollectionFieldGroupPrototype(FieldGroup subCollectionFieldGroupPrototype) {
        this.subCollectionFieldGroupPrototype = subCollectionFieldGroupPrototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute(name = "selectFieldPrototype", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Field getSelectFieldPrototype() {
        return selectFieldPrototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectFieldPrototype(Field selectFieldPrototype) {
        this.selectFieldPrototype = selectFieldPrototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "wrapperGroup", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Group getWrapperGroup() {
        return wrapperGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWrapperGroup(Group wrapperGroup) {
        this.wrapperGroup = wrapperGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pager getPagerWidget() {
        return pagerWidget;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPagerWidget(Pager pagerWidget) {
        this.pagerWidget = pagerWidget;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction
    @BeanTagAttribute(name = "stackedGroups", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<Group> getStackedGroups() {
        return this.stackedGroups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Group> getStackedGroupsNoWrapper() {
        return wrapperGroup != null ? null : this.stackedGroups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStackedGroups(List<Group> stackedGroups) {
        this.stackedGroups = stackedGroups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActionsInLineGroup() {
        return actionsInLineGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActionsInLineGroup(boolean actionsInLineGroup) {
        this.actionsInLineGroup = actionsInLineGroup;
    }
}
