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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.ClientSideState;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DelayedCopy;
import org.kuali.rice.krad.uif.component.KeepExpression;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.layout.CollectionLayoutManager;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ContextUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.widget.QuickFinder;
import org.kuali.rice.krad.util.KRADUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Group that holds a collection of objects and configuration for presenting the
 * collection in the UI. Supports functionality such as add line, line actions,
 * and nested collections.
 *
 * <p>
 * Note the standard header/footer can be used to give a header to the
 * collection as a whole, or to provide actions that apply to the entire
 * collection
 * </p>
 *
 * <p>
 * For binding purposes the binding path of each row field is indexed. The name
 * property inherited from <code>ComponentBase</code> is used as the collection
 * name. The collectionObjectClass property is used to lookup attributes from
 * the data dictionary.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "collectionGroup", parent = "Uif-CollectionGroupBase"),
        @BeanTag(name = "stacked", parent = "Uif-StackedCollectionGroup"),
        @BeanTag(name = "stackedSection", parent = "Uif-StackedCollectionSection"),
        @BeanTag(name = "stackedSubSection", parent = "Uif-StackedCollectionSubSection"),
        @BeanTag(name = "stackedSubCollection-withinSection", parent = "Uif-StackedSubCollection-WithinSection"),
        @BeanTag(name = "stackedSubCollection-withinSubSection",
                parent = "Uif-StackedSubCollection-WithinSubSection"),
        @BeanTag(name = "disclosureStackedSection", parent = "Uif-Disclosure-StackedCollectionSection"),
        @BeanTag(name = "disclosureStackedSubSection",
                parent = "Uif-Disclosure-StackedCollectionSubSection"),
        @BeanTag(name = "disclosureStackedSubCollection-withinSection",
                parent = "Uif-Disclosure-StackedSubCollection-WithinSection"),
        @BeanTag(name = "disclosureStackedSubCollection-withinSubSection",
                parent = "Uif-Disclosure-StackedSubCollection-WithinSubSection"),
        @BeanTag(name = "table", parent = "Uif-TableCollectionGroup"),
        @BeanTag(name = "tableSection", parent = "Uif-TableCollectionSection"),
        @BeanTag(name = "tableSubSection", parent = "Uif-TableCollectionSubSection"),
        @BeanTag(name = "tableSubCollection-withinSection", parent = "Uif-TableSubCollection-WithinSection"),
        @BeanTag(name = "tableSubCollection-withinSubSection", parent = "Uif-TableSubCollection-WithinSubSection"),
        @BeanTag(name = "disclosureTableSection", parent = "Uif-Disclosure-TableCollectionSection"),
        @BeanTag(name = "disclosureTableSubSection",
                parent = "Uif-Disclosure-TableCollectionSubSection"),
        @BeanTag(name = "disclosureTableSubCollection-withinSection",
                parent = "Uif-Disclosure-TableSubCollection-WithinSection"),
        @BeanTag(name = "disclosureTableSubCollection-withinSubSection",
                parent = "Uif-Disclosure-TableSubCollection-WithinSubSection"),
        @BeanTag(name = "listCollection", parent = "Uif-ListCollectionGroup"),
        @BeanTag(name = "listCollectionSection", parent = "Uif-ListCollectionSection"),
        @BeanTag(name = "listCollectionSubSection", parent = "Uif-ListCollectionSubSection"),
        @BeanTag(name = "maintenanceStackedSection", parent = "Uif-MaintenanceStackedCollectionSection"),
        @BeanTag(name = "maintenanceStackedSubCollection-withinSection",
                parent = "Uif-MaintenanceStackedSubCollection-WithinSection"),
        @BeanTag(name = "maintenanceTableSection", parent = "Uif-MaintenanceTableCollectionSection"),
        @BeanTag(name = "maintenanceTableSubCollection-withinSection",
                parent = "Uif-MaintenanceTableSubCollection-WithinSection")})
public class CollectionGroupBase extends GroupBase implements CollectionGroup {
    private static final long serialVersionUID = -6496712566071542452L;

    private Class<?> collectionObjectClass;

    private String propertyName;
    private BindingInfo bindingInfo;

    private boolean renderAddLine;
    private String addLinePropertyName;
    private BindingInfo addLineBindingInfo;

    private Message addLineLabel;

    private List<? extends Component> addLineItems;
    private List<? extends Component> addLineActions;

    @KeepExpression
    private String addLineEnterKeyAction;

    @KeepExpression
    private String lineEnterKeyAction;

    private boolean renderLineActions;
    private List<? extends Component> lineActions;

    private boolean includeLineSelectionField;
    private String lineSelectPropertyName;

    private QuickFinder collectionLookup;

    private boolean renderInactiveToggleButton;
    @ClientSideState(variableName = "inactive")
    private boolean showInactiveLines;
    private CollectionFilter activeCollectionFilter;
    private List<CollectionFilter> filters;

    private List<String> duplicateLinePropertyNames;

    private List<BindingInfo> unauthorizedLineBindingInfos;

    @DelayedCopy
    private List<CollectionGroup> subCollections;

    private CollectionGroupBuilder collectionGroupBuilder;

    private int displayCollectionSize = -1;

    private boolean highlightNewItems;
    private boolean highlightAddItem;
    private String newItemsCssClass;
    private String addItemCssClass;

    private boolean renderAddBlankLineButton;
    private Action addBlankLineAction;
    private String addLinePlacement;

    private boolean renderSaveLineActions;

    private boolean addWithDialog;
    private Action addWithDialogAction;
    private DialogGroup addLineDialog;

    private boolean useServerPaging = false;
    private int pageSize;
    private int displayStart = -1;
    private int displayLength = -1;
    private int filteredCollectionSize = -1;
    private int totalCollectionSize = -1;

    private List<String> totalColumns;

    public CollectionGroupBase() {
        renderAddLine = true;
        renderLineActions = true;
        renderInactiveToggleButton = true;
        highlightNewItems = true;
        highlightAddItem = true;
        addLinePlacement = "TOP";

        filters = Collections.emptyList();
        duplicateLinePropertyNames = Collections.emptyList();
        lineActions = Collections.emptyList();
        addLineItems = Collections.emptyList();
        addLineActions = Collections.emptyList();
        subCollections = Collections.emptyList();
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        View view = ViewLifecycle.getView();

        setFieldBindingObjectPath(getBindingInfo().getBindingObjectPath());

        super.performInitialization(model);

        if (bindingInfo != null) {
            bindingInfo.setDefaults(view, getPropertyName());
        }

        if (addLineBindingInfo != null) {
            // add line binds to model property
            if (StringUtils.isNotBlank(addLinePropertyName)) {
                addLineBindingInfo.setDefaults(view, getPropertyName());
                addLineBindingInfo.setBindingName(addLinePropertyName);

                if (StringUtils.isNotBlank(getFieldBindByNamePrefix())) {
                    addLineBindingInfo.setBindByNamePrefix(getFieldBindByNamePrefix());
                }
            }
        }

        if ((addLineItems == null) || addLineItems.isEmpty()) {
            addLineItems = getItems();
        }

        if (addWithDialog && (addLineDialog == null)) {
            addLineDialog = (DialogGroup) ComponentFactory.getNewComponentInstance(ComponentFactory.ADD_LINE_DIALOG);

            ((CollectionLayoutManager) getLayoutManager()).setAddLineGroup(addLineDialog);
        }

        // if active collection filter not set use default
        if (activeCollectionFilter == null) {
            activeCollectionFilter = new ActiveCollectionFilter();
        }

        // set static collection path on items
        String collectionPath = "";
        if (StringUtils.isNotBlank(getBindingInfo().getCollectionPath())) {
            collectionPath += getBindingInfo().getCollectionPath() + ".";
        }
        if (StringUtils.isNotBlank(getBindingInfo().getBindByNamePrefix())) {
            collectionPath += getBindingInfo().getBindByNamePrefix() + ".";
        }
        collectionPath += getBindingInfo().getBindingName();

        List<DataField> collectionFields = ComponentUtils.getNestedNonCollectionComponents((List<Component>) getItems(),
                DataField.class);
        List<DataField> addLineCollectionFields = ComponentUtils.getNestedNonCollectionComponents(
                (List<Component>) addLineItems, DataField.class);
        if (addLineCollectionFields != null) {
            collectionFields.addAll(addLineCollectionFields);
        }

        for (DataField collectionField : collectionFields) {
            collectionField.getBindingInfo().setCollectionPath(collectionPath);

            if (StringUtils.isBlank(collectionField.getDictionaryObjectEntry())) {
                collectionField.setDictionaryObjectEntry(collectionObjectClass.getName());
            }
        }

        for (CollectionGroup collectionGroup : getSubCollections()) {
            collectionGroup.getBindingInfo().setCollectionPath(collectionPath);
        }

        // add collection entry to abstract classes
        if (!view.getObjectPathToConcreteClassMapping().containsKey(collectionPath)) {
            view.getObjectPathToConcreteClassMapping().put(collectionPath, getCollectionObjectClass());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        ViewModel viewModel = (ViewModel) model;
        View view = ViewLifecycle.getView();

        // if server paging is enabled get the display start from post data so we build the correct page
        if (this.isUseServerPaging()) {
            Object displayStart = ViewLifecycle.getViewPostMetadata().getComponentPostData(this.getId(),
                    UifConstants.PostMetadata.COLL_DISPLAY_START);
            if (displayStart != null) {
                this.setDisplayStart(((Integer) displayStart).intValue());
            }

            Object displayLength = ViewLifecycle.getViewPostMetadata().getComponentPostData(this.getId(),
                    UifConstants.PostMetadata.COLL_DISPLAY_LENGTH);
            if (displayLength != null) {
                this.setDisplayLength(((Integer) displayLength).intValue());
            }
        }

        // if we are processing a paging request, invoke the layout managers to carry out the paging,
        if (viewModel.isCollectionPagingRequest()) {
            ((CollectionLayoutManager) getLayoutManager()).processPagingRequest(model, this);
        }

        if (StringUtils.isNotBlank(this.getId())
                && viewModel.getViewPostMetadata() != null
                && viewModel.getViewPostMetadata().getAddedCollectionObjects().get(this.getId()) != null) {
            List<Object> newLines = viewModel.getViewPostMetadata().getAddedCollectionObjects().get(this.getId());

            // if newLines is empty this means its an addLine case (no additional processing) so init collection line
            if (newLines.isEmpty()) {
                initializeNewCollectionLine(view, model, this, true);
            }

            for (Object newLine : newLines) {
                ViewLifecycle.getHelper().applyDefaultValuesForCollectionLine(this, newLine);
            }
        }

        // adds the script to the add line buttons to keep collection on the same page
        if (this.renderAddBlankLineButton) {
            if (this.addBlankLineAction == null) {
                this.addBlankLineAction = (Action) ComponentFactory.getNewComponentInstance(
                        ComponentFactory.ADD_BLANK_LINE_ACTION);
            }

            if (addLinePlacement.equals(UifConstants.Position.BOTTOM.name())) {
                this.addBlankLineAction.setOnClickScript("writeCurrentPageToSession(this, 'last');");
            } else {
                this.addBlankLineAction.setOnClickScript("writeCurrentPageToSession(this, 'first');");
            }
        } else if (this.addWithDialog) {
            setupAddLineDialog();
        }

        pushCollectionGroupToReference();

        // if rendering the collection group, build out the lines
        if (isRender()) {
            getCollectionGroupBuilder().build(view, model, this);
        }
    }

    /**
     * When add via dialog is true, initialize the add with dialog action (if necessary) and sets up the
     * action script for opening the dialog.
     */
    protected void setupAddLineDialog() {
        if (addWithDialogAction == null) {
            addWithDialogAction = (Action) ComponentFactory.getNewComponentInstance(
                    ComponentFactory.ADD_WITH_DIALOG_ACTION);
        }

        String sessionPage = "first";
        if (addLinePlacement.equals(UifConstants.Position.BOTTOM.name())) {
            sessionPage = "last";
        }

        String actionScript = UifConstants.JsFunctions.WRITE_CURRENT_PAGE_TO_SESSION +
                "(this, '" + sessionPage + "');";
        actionScript = ScriptUtils.appendScript(addWithDialogAction.getActionScript(), actionScript);

        actionScript = ScriptUtils.appendScript(actionScript, ScriptUtils.buildFunctionCall(
                UifConstants.JsFunctions.SHOW_DIALOG, addLineDialog.getId()));

        addWithDialogAction.setActionScript(actionScript);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        addCollectionPostMetadata();
    }

    /**
     * Add the metadata about this collection to the ViewPostMetadata
     * that is to be kept in memory between posts for use by other methods
     */
    protected void addCollectionPostMetadata() {
        if (this.getCollectionLookup() != null) {
            ViewLifecycle.getViewPostMetadata().addComponentPostData(this,
                    UifConstants.PostMetadata.COLL_LOOKUP_FIELD_CONVERSIONS,
                    this.getCollectionLookup().getFieldConversions());
        }

        ViewLifecycle.getViewPostMetadata().addComponentPostData(this, UifConstants.PostMetadata.COLL_OBJECT_CLASS,
                this.getCollectionObjectClass());

        ViewLifecycle.getViewPostMetadata().addComponentPostData(this, UifConstants.PostMetadata.BINDING_INFO,
                this.getBindingInfo());

        ViewLifecycle.getViewPostMetadata().addComponentPostData(this, UifConstants.PostMetadata.ADD_LINE_BINDING_INFO,
                this.getAddLineBindingInfo());

        ViewLifecycle.getViewPostMetadata().addComponentPostData(this, UifConstants.PostMetadata.ADD_LINE_PLACEMENT,
                this.getAddLinePlacement());

        if (this.getHeader() != null) {
            ViewLifecycle.getViewPostMetadata().addComponentPostData(this, UifConstants.PostMetadata.COLL_LABEL,
                    this.getCollectionLabel());
        }

        if (this.getDuplicateLinePropertyNames() != null) {
            ViewLifecycle.getViewPostMetadata().addComponentPostData(this,
                    UifConstants.PostMetadata.DUPLICATE_LINE_PROPERTY_NAMES, this.getDuplicateLinePropertyNames());
            ViewLifecycle.getViewPostMetadata().addComponentPostData(this,
                    UifConstants.PostMetadata.DUPLICATE_LINE_LABEL_STRING, this.getDuplicateLineLabelString(
                    this.getDuplicateLinePropertyNames()));
        }

        boolean hasBindingPath = getBindingInfo() != null && getBindingInfo().getBindingPath() != null;
        if (hasBindingPath) {
            ViewLifecycle.getViewPostMetadata().addComponentPostData(this, UifConstants.PostMetadata.BINDING_PATH,
                    getBindingInfo().getBindingPath());
        }

        ViewLifecycle.getViewPostMetadata().addComponentPostData(this, UifConstants.PostMetadata.COLL_DISPLAY_START,
                getDisplayStart());
        ViewLifecycle.getViewPostMetadata().addComponentPostData(this, UifConstants.PostMetadata.COLL_DISPLAY_LENGTH,
                getDisplayLength());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushCollectionGroupToReference() {
        Collection<LifecycleElement> components = ViewLifecycleUtils.getElementsForLifecycle(this).values();
        ContextUtils.pushObjectToContextDeep(components, UifConstants.ContextVariableNames.COLLECTION_GROUP, this);

        List<Action> actions = ViewLifecycleUtils.getElementsOfTypeDeep(components, Action.class);
        for (Action action : actions) {
            action.addActionParameter(UifParameters.SELECTED_COLLECTION_PATH, this.getBindingInfo().getBindingPath());
            action.addActionParameter(UifParameters.SELECTED_COLLECTION_ID, this.getId());
        }
    }

    /**
     * Gets the label for the collection in a human-friendly format.
     *
     * @return a human-friendly collection label
     */
    protected String getCollectionLabel() {
        String collectionLabel = this.getHeaderText();

        if (StringUtils.isBlank(collectionLabel)) {
            String propertyName = this.getPropertyName();
            collectionLabel = KRADServiceLocatorWeb.getUifDefaultingService().deriveHumanFriendlyNameFromPropertyName(
                    propertyName);
        }

        return collectionLabel;
    }

    /**
     * Gets a comma-separated list of the data field labels that are keyed a duplicates.
     *
     * @param duplicateLinePropertyNames the property names to check for duplicates
     * @return a comma-separated list of labels
     */
    protected String getDuplicateLineLabelString(List<String> duplicateLinePropertyNames) {
        List<String> duplicateLineLabels = new ArrayList<String>();

        for (Component addLineItem : this.getAddLineItems()) {
            if (addLineItem instanceof DataField) {
                DataField addLineField = (DataField) addLineItem;

                if (duplicateLinePropertyNames.contains(addLineField.getPropertyName())) {
                    String label = addLineField.getLabel();
                    String shortLabel = addLineField.getShortLabel();
                    duplicateLineLabels.add(StringUtils.isNotBlank(label) ? label : shortLabel);
                }
            }

        }

        return StringUtils.join(duplicateLineLabels, ", ");
    }

    /**
     * New collection lines are handled in the framework by maintaining a map on
     * the form. The map contains as a key the collection name, and as value an
     * instance of the collection type. An entry is created here for the
     * collection represented by the <code>CollectionGroup</code> if an instance
     * is not available (clearExistingLine will force a new instance). The given
     * model must be a subclass of <code>UifFormBase</code> in order to find the
     * Map.
     *
     * @param model Model instance that contains the new collection lines Map
     * @param clearExistingLine boolean that indicates whether the line should be set to a
     * new instance if it already exists
     */
    @Override
    public void initializeNewCollectionLine(View view, Object model, CollectionGroup collectionGroup,
            boolean clearExistingLine) {
        getCollectionGroupBuilder().initializeNewCollectionLine(view, model, collectionGroup, clearExistingLine);
    }

    /**
     * Do not process remote field holders for collections. Collection items will be processed as
     * the lines are built.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isProcessRemoteFieldHolders() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute
    public List<? extends Component> getItems() {
        return super.getItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Class<?> getCollectionObjectClass() {
        return this.collectionObjectClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCollectionObjectClass(Class<?> collectionObjectClass) {
        this.collectionObjectClass = collectionObjectClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public BindingInfo getBindingInfo() {
        return this.bindingInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute
    public List<? extends Component> getLineActions() {
        return this.lineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineActions(List<? extends Component> lineActions) {
        this.lineActions = lineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getAddLineEnterKeyAction() {
        return this.addLineEnterKeyAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineEnterKeyAction(String addLineEnterKeyAction) {
        this.addLineEnterKeyAction = addLineEnterKeyAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getLineEnterKeyAction() {
        return this.lineEnterKeyAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineEnterKeyAction(String lineEnterKeyAction) {
        this.lineEnterKeyAction = lineEnterKeyAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderLineActions() {
        return this.renderLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderLineActions(boolean renderLineActions) {
        this.renderLineActions = renderLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderAddLine() {
        return this.renderAddLine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderAddLine(boolean renderAddLine) {
        this.renderAddLine = renderAddLine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getAddLabel() {
        if (getAddLineLabel() != null) {
            return getAddLineLabel().getMessageText();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLabel(String addLabelText) {
        if (getAddLineLabel() != null) {
            getAddLineLabel().setMessageText(addLabelText);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Message getAddLineLabel() {
        return this.addLineLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineLabel(Message addLineLabel) {
        this.addLineLabel = addLineLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getAddLinePropertyName() {
        return this.addLinePropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLinePropertyName(String addLinePropertyName) {
        this.addLinePropertyName = addLinePropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public BindingInfo getAddLineBindingInfo() {
        return this.addLineBindingInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineBindingInfo(BindingInfo addLineBindingInfo) {
        this.addLineBindingInfo = addLineBindingInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute
    public List<? extends Component> getAddLineItems() {
        return this.addLineItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineItems(List<? extends Component> addLineItems) {
        this.addLineItems = addLineItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute
    public List<? extends Component> getAddLineActions() {
        return this.addLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineActions(List<? extends Component> addLineActions) {
        this.addLineActions = addLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isIncludeLineSelectionField() {
        return includeLineSelectionField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIncludeLineSelectionField(boolean includeLineSelectionField) {
        this.includeLineSelectionField = includeLineSelectionField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getLineSelectPropertyName() {
        return lineSelectPropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineSelectPropertyName(String lineSelectPropertyName) {
        this.lineSelectPropertyName = lineSelectPropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.BYTYPE)
    public QuickFinder getCollectionLookup() {
        return collectionLookup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCollectionLookup(QuickFinder collectionLookup) {
        this.collectionLookup = collectionLookup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isShowInactiveLines() {
        return showInactiveLines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShowInactiveLines(boolean showInactiveLines) {
        this.showInactiveLines = showInactiveLines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public CollectionFilter getActiveCollectionFilter() {
        return activeCollectionFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActiveCollectionFilter(CollectionFilter activeCollectionFilter) {
        this.activeCollectionFilter = activeCollectionFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<CollectionFilter> getFilters() {
        return filters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFilters(List<CollectionFilter> filters) {
        this.filters = filters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<String> getDuplicateLinePropertyNames() {
        return this.duplicateLinePropertyNames;
    }

    /**
     * {@inheritDoc}
     */
    public void setDuplicateLinePropertyNames(List<String> duplicateLinePropertyNames) {
        this.duplicateLinePropertyNames = duplicateLinePropertyNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BindingInfo> getUnauthorizedLineBindingInfos() {
        return this.unauthorizedLineBindingInfos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUnauthorizedLineBindingInfos(List<BindingInfo> unauthorizedLineBindingInfos) {
        this.unauthorizedLineBindingInfos = unauthorizedLineBindingInfos;
    }

    /**
     * List of <code>CollectionGroup</code> instances that are sub-collections
     * of the collection represented by this collection group
     *
     * @return sub collections
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute
    public List<CollectionGroup> getSubCollections() {
        return this.subCollections;
    }

    /**
     * Setter for the sub collection list
     *
     * @param subCollections
     */
    @Override
    public void setSubCollections(List<CollectionGroup> subCollections) {
        this.subCollections = subCollections;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionGroupSecurity getCollectionGroupSecurity() {
        return (CollectionGroupSecurity) super.getComponentSecurity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        if ((componentSecurity != null) && !(componentSecurity instanceof CollectionGroupSecurity)) {
            throw new RiceRuntimeException(
                    "Component security for CollectionGroup should be instance of CollectionGroupSecurity");
        }

        super.setComponentSecurity(componentSecurity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeComponentSecurity() {
        if (getComponentSecurity() == null) {
            setComponentSecurity(KRADUtils.createNewObjectFromClass(CollectionGroupSecurity.class));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isEditLineAuthz() {
        initializeComponentSecurity();

        return getCollectionGroupSecurity().isEditLineAuthz();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEditLineAuthz(boolean editLineAuthz) {
        initializeComponentSecurity();

        getCollectionGroupSecurity().setEditLineAuthz(editLineAuthz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isViewLineAuthz() {
        initializeComponentSecurity();

        return getCollectionGroupSecurity().isViewLineAuthz();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setViewLineAuthz(boolean viewLineAuthz) {
        initializeComponentSecurity();

        getCollectionGroupSecurity().setViewLineAuthz(viewLineAuthz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public CollectionGroupBuilder getCollectionGroupBuilder() {
        if (this.collectionGroupBuilder == null) {
            this.collectionGroupBuilder = new CollectionGroupBuilder();
        }
        return this.collectionGroupBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCollectionGroupBuilder(CollectionGroupBuilder collectionGroupBuilder) {
        this.collectionGroupBuilder = collectionGroupBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderInactiveToggleButton() {
        return renderInactiveToggleButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderInactiveToggleButton(boolean renderInactiveToggleButton) {
        this.renderInactiveToggleButton = renderInactiveToggleButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public int getDisplayCollectionSize() {
        return this.displayCollectionSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayCollectionSize(int displayCollectionSize) {
        this.displayCollectionSize = displayCollectionSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isHighlightNewItems() {
        return highlightNewItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHighlightNewItems(boolean highlightNewItems) {
        this.highlightNewItems = highlightNewItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getNewItemsCssClass() {
        return newItemsCssClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNewItemsCssClass(String newItemsCssClass) {
        this.newItemsCssClass = newItemsCssClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getAddItemCssClass() {
        return addItemCssClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddItemCssClass(String addItemCssClass) {
        this.addItemCssClass = addItemCssClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isHighlightAddItem() {
        return highlightAddItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHighlightAddItem(boolean highlightAddItem) {
        this.highlightAddItem = highlightAddItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderAddBlankLineButton() {
        return renderAddBlankLineButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderAddBlankLineButton(boolean renderAddBlankLineButton) {
        this.renderAddBlankLineButton = renderAddBlankLineButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Action getAddBlankLineAction() {
        return addBlankLineAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddBlankLineAction(Action addBlankLineAction) {
        this.addBlankLineAction = addBlankLineAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getAddLinePlacement() {
        return addLinePlacement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLinePlacement(String addLinePlacement) {
        this.addLinePlacement = addLinePlacement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderSaveLineActions() {
        return renderSaveLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderSaveLineActions(boolean renderSaveLineActions) {
        this.renderSaveLineActions = renderSaveLineActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isAddWithDialog() {
        return addWithDialog;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddWithDialog(boolean addWithDialog) {
        this.addWithDialog = addWithDialog;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Action getAddWithDialogAction() {
        return addWithDialogAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddWithDialogAction(Action addWithDialogAction) {
        this.addWithDialogAction = addWithDialogAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public DialogGroup getAddLineDialog() {
        return addLineDialog;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddLineDialog(DialogGroup addLineDialog) {
        this.addLineDialog = addLineDialog;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isUseServerPaging() {
        return useServerPaging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUseServerPaging(boolean useServerPaging) {
        this.useServerPaging = useServerPaging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPageSize() {
        return pageSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDisplayStart() {
        return displayStart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayStart(int displayStart) {
        this.displayStart = displayStart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDisplayLength() {
        return displayLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayLength(int displayLength) {
        this.displayLength = displayLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFilteredCollectionSize() {
        return filteredCollectionSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFilteredCollectionSize(int filteredCollectionSize) {
        this.filteredCollectionSize = filteredCollectionSize;
    }

    public int getTotalCollectionSize() {
        return totalCollectionSize;
    }

    public void setTotalCollectionSize(int totalCollectionSize) {
        this.totalCollectionSize = totalCollectionSize;
    }

    /**
     * @return list of total columns
     */
    @BeanTagAttribute
    protected List<String> getTotalColumns() {
        return totalColumns;
    }

    /**
     * Setter for the total columns
     *
     * @param totalColumns
     */
    protected void setTotalColumns(List<String> totalColumns) {
        this.totalColumns = totalColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checking if collectionObjectClass is set
        if (getCollectionObjectClass() == null) {
            if (Validator.checkExpressions(this, UifConstants.PostMetadata.COLL_OBJECT_CLASS)) {
                String currentValues[] = {"collectionObjectClass = " + getCollectionObjectClass()};
                tracer.createWarning("CollectionObjectClass is not set (disregard if part of an abstract)",
                        currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }

}
