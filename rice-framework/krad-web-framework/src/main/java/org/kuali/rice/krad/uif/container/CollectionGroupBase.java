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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.ClientSideState;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DelayedCopy;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTask;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.QuickFinder;
import org.kuali.rice.krad.web.form.UifFormBase;

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
@BeanTags({@BeanTag(name = "collectionGroup-bean", parent = "Uif-CollectionGroupBase"),
        @BeanTag(name = "stackedCollectionGroup-bean", parent = "Uif-StackedCollectionGroup"),
        @BeanTag(name = "stackedCollectionSection-bean", parent = "Uif-StackedCollectionSection"),
        @BeanTag(name = "stackedCollectionSubSection-bean", parent = "Uif-StackedCollectionSubSection"),
        @BeanTag(name = "stackedSubCollection-withinSection-bean", parent = "Uif-StackedSubCollection-WithinSection"),
        @BeanTag(name = "stackedSubCollection-withinSubSection-bean",
                parent = "Uif-StackedSubCollection-WithinSubSection"),
        @BeanTag(name = "disclosure-stackedCollectionSection-bean", parent = "Uif-Disclosure-StackedCollectionSection"),
        @BeanTag(name = "disclosure-stackedCollectionSubSection-bean",
                parent = "Uif-Disclosure-StackedCollectionSubSection"),
        @BeanTag(name = "disclosure-stackedSubCollection-withinSection-bean",
                parent = "Uif-Disclosure-StackedSubCollection-WithinSection"),
        @BeanTag(name = "disclosure-stackedSubCollection-withinSubSection-bean",
                parent = "Uif-Disclosure-StackedSubCollection-WithinSubSection"),
        @BeanTag(name = "tableCollectionGroup-bean", parent = "Uif-TableCollectionGroup"),
        @BeanTag(name = "tableCollectionSection-bean", parent = "Uif-TableCollectionSection"),
        @BeanTag(name = "tableCollectionSubSection-bean", parent = "Uif-TableCollectionSubSection"),
        @BeanTag(name = "tableSubCollection-withinSection-bean", parent = "Uif-TableSubCollection-WithinSection"),
        @BeanTag(name = "tableSubCollection-withinSubSection-bean", parent = "Uif-TableSubCollection-WithinSubSection"),
        @BeanTag(name = "disclosure-tableCollectionSection-bean", parent = "Uif-Disclosure-TableCollectionSection"),
        @BeanTag(name = "disclosure-tableCollectionSubSection-bean",
                parent = "Uif-Disclosure-TableCollectionSubSection"),
        @BeanTag(name = "disclosure-tableSubCollection-withinSection-bean",
                parent = "Uif-Disclosure-TableSubCollection-WithinSection"),
        @BeanTag(name = "disclosure-tableSubCollection-withinSubSection-bean",
                parent = "Uif-Disclosure-TableSubCollection-WithinSubSection"),
        @BeanTag(name = "listCollectionGroup-bean", parent = "Uif-ListCollectionGroup"),
        @BeanTag(name = "listCollectionSection-bean", parent = "Uif-ListCollectionSection"),
        @BeanTag(name = "listCollectionSubSection-bean", parent = "Uif-ListCollectionSubSection"),
        @BeanTag(name = "documentNotesSection-bean", parent = "Uif-DocumentNotesSection"),
        @BeanTag(name = "lookupResultsCollectionSection-bean", parent = "Uif-LookupResultsCollectionSection"),
        @BeanTag(name = "maintenanceStackedCollectionSection-bean", parent = "Uif-MaintenanceStackedCollectionSection"),
        @BeanTag(name = "maintenanceStackedSubCollection-withinSection-bean",
                parent = "Uif-MaintenanceStackedSubCollection-WithinSection"),
        @BeanTag(name = "maintenanceTableCollectionSection-bean", parent = "Uif-MaintenanceTableCollectionSection"),
        @BeanTag(name = "maintenanceTableSubCollection-withinSection-bean",
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
    @DelayedCopy
    private List<? extends Component> addLineItems;
    private List<Action> addLineActions;

    private boolean renderLineActions;
    private List<Action> lineActions;

    private boolean includeLineSelectionField;
    private String lineSelectPropertyName;

    private QuickFinder collectionLookup;

    private boolean renderInactiveToggleButton;
    @ClientSideState(variableName = "inactive")
    private boolean showInactiveLines;
    private CollectionFilter activeCollectionFilter;
    private List<CollectionFilter> filters;

    private List<BindingInfo> unauthorizedLineBindingInfos;

    @DelayedCopy
    private List<CollectionGroup> subCollections;
    private String subCollectionSuffix;

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
    private boolean addViaLightBox;
    private Action addViaLightBoxAction;

    private boolean useServerPaging = false;
    private int pageSize;
    private int displayStart = -1;
    private int displayLength = -1;
    private int filteredCollectionSize = -1;

    private List<String> totalColumns;

    public CollectionGroupBase() {
        renderAddLine = true;
        renderLineActions = true;
        renderInactiveToggleButton = true;
        highlightNewItems = true;
        highlightAddItem = true;
        addLinePlacement = "TOP";

        filters = Collections.emptyList();
        lineActions = Collections.emptyList();
        addLineItems = Collections.emptyList();
        addLineActions = Collections.emptyList();
        subCollections = Collections.emptyList();
    }

    /**
     * Do not process remote field holders for collections. Collection items will be processed as
     * the lines are built
     * 
     * @see org.kuali.rice.krad.uif.container.ContainerBase#isProcessRemoteFieldHolders()
     */
    @Override
    protected boolean isProcessRemoteFieldHolders() {
        return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#performInitialization(java.lang.Object)
     */
    @Override
    public void performInitialization(Object model) {
        setFieldBindingObjectPath(getBindingInfo().getBindingObjectPath());

        super.performInitialization(model);

        View view = ViewLifecycle.getView();
        
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

        for (Component item : getItems()) {
            if (item instanceof DataField) {
                DataField field = (DataField) item;

                if (StringUtils.isBlank(field.getDictionaryObjectEntry())) {
                    field.setDictionaryObjectEntry(collectionObjectClass.getName());
                }
            }
        }

        if ((addLineItems == null) || addLineItems.isEmpty()) {
            addLineItems = getItems();
        } else {
            for (Component addLineField : addLineItems) {
                if (!(addLineField instanceof DataField)) {
                    continue;
                }

                DataField field = (DataField) addLineField;

                if (StringUtils.isBlank(field.getDictionaryObjectEntry())) {
                    field.setDictionaryObjectEntry(collectionObjectClass.getName());
                }
            }
        }

        // if active collection filter not set use default
        if (this.activeCollectionFilter == null) {
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

        List<DataField> collectionFields = ComponentUtils.getComponentsOfTypeDeep(getItems(), DataField.class);
        for (DataField collectionField : collectionFields) {
            collectionField.getBindingInfo().setCollectionPath(collectionPath);
        }

        List<DataField> addLineCollectionFields = ComponentUtils.getComponentsOfTypeDeep(addLineItems, DataField.class);
        for (DataField collectionField : addLineCollectionFields) {
            collectionField.getBindingInfo().setCollectionPath(collectionPath);
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
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#performApplyModel(java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performApplyModel(Object model, Component parent) {
        super.performApplyModel(model, parent);

        // If we are using server paging, determine if a displayStart value has been set for this collection
        // and used that value as the displayStart
        if (model instanceof UifFormBase && this.isUseServerPaging()) {
            Object displayStart = ((UifFormBase) model).getExtensionData().get(
                    this.getId() + UifConstants.PageRequest.DISPLAY_START_PROP);

            if (displayStart != null) {
                this.setDisplayStart(((Integer) displayStart).intValue());
            }
        }

        View view = ViewLifecycle.getView();
        
        // adds the script to the add line buttons to keep collection on the same page
        if (this.renderAddBlankLineButton) {
            if (this.addBlankLineAction == null) {
                this.addBlankLineAction = (Action) ComponentFactory.getNewComponentInstance(
                        ComponentFactory.ADD_BLANK_LINE_ACTION);
                ViewLifecycle.spawnSubLifecyle(model, this.addBlankLineAction, this);
            }

            if (addLinePlacement.equals(UifConstants.Position.BOTTOM.name())) {
                this.addBlankLineAction.setOnClickScript("writeCurrentPageToSession(this, 'last');");
            } else {
                this.addBlankLineAction.setOnClickScript("writeCurrentPageToSession(this, 'first');");
            }
        } else if (this.addViaLightBox) {
            if (this.addViaLightBoxAction == null) {
                this.addViaLightBoxAction = (Action) ComponentFactory.getNewComponentInstance(
                        ComponentFactory.ADD_VIA_LIGHTBOX_ACTION);
                ViewLifecycle.spawnSubLifecyle(model, this.addViaLightBoxAction, this);
            }

            if (this.addLinePlacement.equals(UifConstants.Position.BOTTOM.name())) {
                this.addViaLightBoxAction.setOnClickScript("writeCurrentPageToSession(this, 'last');");
            } else {
                this.addViaLightBoxAction.setOnClickScript("writeCurrentPageToSession(this, 'first');");
            }
        }

        pushCollectionGroupToReference();

        // if rendering the collection group, build out the lines
        if (isRender()) {
            getCollectionGroupBuilder().build(view, model, this);
        }

        // TODO: is this necessary to call again?
        // This may be necessary to call in case getCollectionGroupBuilder().build resets the context map
        pushCollectionGroupToReference();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#initializePendingTasks(org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase, java.util.Queue)
     */
    @Override
    public void initializePendingTasks(ViewLifecyclePhase phase, Queue<ViewLifecycleTask> pendingTasks) {
        super.initializePendingTasks(phase, pendingTasks);

        // TODO (moved from ViewHelperServiceImpl): Add task to initialize from dictionary
    }

    /**
     * Sets a reference in the context map for all nested components to the collection group
     * instance, and sets name as parameter for an action fields in the group
     */
    public void pushCollectionGroupToReference() {
        Collection<Component> components;
        synchronized (this) {
            resetComponentsForLifecycle();
            components = getComponentsForLifecycle().values();
        }
        ComponentUtils.pushObjectToContext(components,
                UifConstants.ContextVariableNames.COLLECTION_GROUP, this);

        List<Action> actions = ComponentUtils.getComponentsOfTypeDeep(components, Action.class);
        for (Action action : actions) {
            action.addActionParameter(UifParameters.SELLECTED_COLLECTION_PATH, this.getBindingInfo().getBindingPath());
        }
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
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getItems()
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    public List<? extends Component> getItems() {
        return super.getItems();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getCollectionObjectClass()
     */
    @Override
    @BeanTagAttribute(name = "collectionObjectClass")
    public Class<?> getCollectionObjectClass() {
        return this.collectionObjectClass;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setCollectionObjectClass(java.lang.Class)
     */
    @Override
    public void setCollectionObjectClass(Class<?> collectionObjectClass) {
        this.collectionObjectClass = collectionObjectClass;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getPropertyName()
     */
    @Override
    @BeanTagAttribute(name = "propertyName")
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setPropertyName(java.lang.String)
     */
    @Override
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getBindingInfo()
     */
    @Override
    @BeanTagAttribute(name = "bindingInfo", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public BindingInfo getBindingInfo() {
        return this.bindingInfo;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setBindingInfo(org.kuali.rice.krad.uif.component.BindingInfo)
     */
    @Override
    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getLineActions()
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute(name = "lineActions", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<Action> getLineActions() {
        return this.lineActions;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setLineActions(java.util.List)
     */
    @Override
    public void setLineActions(List<Action> lineActions) {
        this.lineActions = lineActions;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isRenderLineActions()
     */
    @Override
    @BeanTagAttribute(name = "renderLineActions")
    public boolean isRenderLineActions() {
        return this.renderLineActions;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setRenderLineActions(boolean)
     */
    @Override
    public void setRenderLineActions(boolean renderLineActions) {
        this.renderLineActions = renderLineActions;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isRenderAddLine()
     */
    @Override
    @BeanTagAttribute(name = "renderAddLine")
    public boolean isRenderAddLine() {
        return this.renderAddLine;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setRenderAddLine(boolean)
     */
    @Override
    public void setRenderAddLine(boolean renderAddLine) {
        this.renderAddLine = renderAddLine;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getAddLabel()
     */
    @Override
    public String getAddLabel() {
        if (getAddLineLabel() != null) {
            return getAddLineLabel().getMessageText();
        }

        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddLabel(java.lang.String)
     */
    @Override
    public void setAddLabel(String addLabelText) {
        if (getAddLineLabel() != null) {
            getAddLineLabel().setMessageText(addLabelText);
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getAddLineLabel()
     */
    @Override
    @BeanTagAttribute(name = "addLineLabel", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Message getAddLineLabel() {
        return this.addLineLabel;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddLineLabel(org.kuali.rice.krad.uif.element.Message)
     */
    @Override
    public void setAddLineLabel(Message addLineLabel) {
        this.addLineLabel = addLineLabel;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getAddLinePropertyName()
     */
    @Override
    @BeanTagAttribute(name = "addLinePropertyName")
    public String getAddLinePropertyName() {
        return this.addLinePropertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddLinePropertyName(java.lang.String)
     */
    @Override
    public void setAddLinePropertyName(String addLinePropertyName) {
        this.addLinePropertyName = addLinePropertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getAddLineBindingInfo()
     */
    @Override
    @BeanTagAttribute(name = "addLineBindingInfo", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public BindingInfo getAddLineBindingInfo() {
        return this.addLineBindingInfo;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddLineBindingInfo(org.kuali.rice.krad.uif.component.BindingInfo)
     */
    @Override
    public void setAddLineBindingInfo(BindingInfo addLineBindingInfo) {
        this.addLineBindingInfo = addLineBindingInfo;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getAddLineItems()
     */
    @Override
    @ViewLifecycleRestriction
    @BeanTagAttribute(name = "addLineItems", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<? extends Component> getAddLineItems() {
        return this.addLineItems;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddLineItems(java.util.List)
     */
    @Override
    public void setAddLineItems(List<? extends Component> addLineItems) {
        this.addLineItems = addLineItems;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getAddLineActions()
     */
    @Override
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    @BeanTagAttribute(name = "addLineActions", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<Action> getAddLineActions() {
        return this.addLineActions;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddLineActions(java.util.List)
     */
    @Override
    public void setAddLineActions(List<Action> addLineActions) {
        this.addLineActions = addLineActions;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isIncludeLineSelectionField()
     */
    @Override
    @BeanTagAttribute(name = "includeLineSelectionField")
    public boolean isIncludeLineSelectionField() {
        return includeLineSelectionField;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setIncludeLineSelectionField(boolean)
     */
    @Override
    public void setIncludeLineSelectionField(boolean includeLineSelectionField) {
        this.includeLineSelectionField = includeLineSelectionField;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getLineSelectPropertyName()
     */
    @Override
    @BeanTagAttribute(name = "lineSelectPropertyName")
    public String getLineSelectPropertyName() {
        return lineSelectPropertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setLineSelectPropertyName(java.lang.String)
     */
    @Override
    public void setLineSelectPropertyName(String lineSelectPropertyName) {
        this.lineSelectPropertyName = lineSelectPropertyName;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getCollectionLookup()
     */
    @Override
    @BeanTagAttribute(name = "collectionLookup", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public QuickFinder getCollectionLookup() {
        return collectionLookup;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setCollectionLookup(org.kuali.rice.krad.uif.widget.QuickFinder)
     */
    @Override
    public void setCollectionLookup(QuickFinder collectionLookup) {
        this.collectionLookup = collectionLookup;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isShowInactiveLines()
     */
    @Override
    @BeanTagAttribute(name = "showInactiveLines")
    public boolean isShowInactiveLines() {
        return showInactiveLines;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setShowInactiveLines(boolean)
     */
    @Override
    public void setShowInactiveLines(boolean showInactiveLines) {
        this.showInactiveLines = showInactiveLines;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getActiveCollectionFilter()
     */
    @Override
    @BeanTagAttribute(name = "activeCollectionFilter", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public CollectionFilter getActiveCollectionFilter() {
        return activeCollectionFilter;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setActiveCollectionFilter(org.kuali.rice.krad.uif.container.CollectionFilter)
     */
    @Override
    public void setActiveCollectionFilter(CollectionFilter activeCollectionFilter) {
        this.activeCollectionFilter = activeCollectionFilter;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getFilters()
     */
    @Override
    @BeanTagAttribute(name = "filters", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<CollectionFilter> getFilters() {
        return filters;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setFilters(java.util.List)
     */
    @Override
    public void setFilters(List<CollectionFilter> filters) {
        this.filters = filters;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getUnauthorizedLineBindingInfos()
     */
    @Override
    public List<BindingInfo> getUnauthorizedLineBindingInfos() {
        return this.unauthorizedLineBindingInfos;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setUnauthorizedLineBindingInfos(java.util.List)
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
    @BeanTagAttribute(name = "subCollections", type = BeanTagAttribute.AttributeType.LISTBEAN)
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
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getSubCollectionSuffix()
     */
    @Override
    public String getSubCollectionSuffix() {
        return subCollectionSuffix;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setSubCollectionSuffix(java.lang.String)
     */
    @Override
    public void setSubCollectionSuffix(String subCollectionSuffix) {
        this.subCollectionSuffix = subCollectionSuffix;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getCollectionGroupSecurity()
     */
    @Override
    public CollectionGroupSecurity getCollectionGroupSecurity() {
        return (CollectionGroupSecurity) super.getComponentSecurity();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setComponentSecurity(org.kuali.rice.krad.uif.component.ComponentSecurity)
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
     * @see org.kuali.rice.krad.uif.component.ComponentBase#initializeComponentSecurity()
     */
    @Override
    protected void initializeComponentSecurity() {
        if (getComponentSecurity() == null) {
            setComponentSecurity(DataObjectUtils.newInstance(CollectionGroupSecurity.class));
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isEditLineAuthz()
     */
    @Override
    public boolean isEditLineAuthz() {
        initializeComponentSecurity();

        return getCollectionGroupSecurity().isEditLineAuthz();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setEditLineAuthz(boolean)
     */
    @Override
    public void setEditLineAuthz(boolean editLineAuthz) {
        initializeComponentSecurity();

        getCollectionGroupSecurity().setEditLineAuthz(editLineAuthz);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isViewLineAuthz()
     */
    @Override
    public boolean isViewLineAuthz() {
        initializeComponentSecurity();

        return getCollectionGroupSecurity().isViewLineAuthz();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setViewLineAuthz(boolean)
     */
    @Override
    public void setViewLineAuthz(boolean viewLineAuthz) {
        initializeComponentSecurity();

        getCollectionGroupSecurity().setViewLineAuthz(viewLineAuthz);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getCollectionGroupBuilder()
     */
    @Override
    @BeanTagAttribute(name = "collectionGroupBuilder", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public CollectionGroupBuilder getCollectionGroupBuilder() {
        if (this.collectionGroupBuilder == null) {
            this.collectionGroupBuilder = new CollectionGroupBuilder();
        }
        return this.collectionGroupBuilder;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setCollectionGroupBuilder(org.kuali.rice.krad.uif.container.CollectionGroupBuilder)
     */
    @Override
    public void setCollectionGroupBuilder(CollectionGroupBuilder collectionGroupBuilder) {
        this.collectionGroupBuilder = collectionGroupBuilder;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setRenderInactiveToggleButton(boolean)
     */
    @Override
    public void setRenderInactiveToggleButton(boolean renderInactiveToggleButton) {
        this.renderInactiveToggleButton = renderInactiveToggleButton;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isRenderInactiveToggleButton()
     */
    @Override
    @BeanTagAttribute(name = "renderInactiveToggleButton")
    public boolean isRenderInactiveToggleButton() {
        return renderInactiveToggleButton;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getDisplayCollectionSize()
     */
    @Override
    @BeanTagAttribute(name = "displayCollectionSize")
    public int getDisplayCollectionSize() {
        return this.displayCollectionSize;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setDisplayCollectionSize(int)
     */
    @Override
    public void setDisplayCollectionSize(int displayCollectionSize) {
        this.displayCollectionSize = displayCollectionSize;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isHighlightNewItems()
     */
    @Override
    @BeanTagAttribute(name = "highlightNewItems")
    public boolean isHighlightNewItems() {
        return highlightNewItems;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setHighlightNewItems(boolean)
     */
    @Override
    public void setHighlightNewItems(boolean highlightNewItems) {
        this.highlightNewItems = highlightNewItems;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getNewItemsCssClass()
     */
    @Override
    @BeanTagAttribute(name = "newItemsCssClass")
    public String getNewItemsCssClass() {
        return newItemsCssClass;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setNewItemsCssClass(java.lang.String)
     */
    @Override
    public void setNewItemsCssClass(String newItemsCssClass) {
        this.newItemsCssClass = newItemsCssClass;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getAddItemCssClass()
     */
    @Override
    @BeanTagAttribute(name = "addItemCssClass")
    public String getAddItemCssClass() {
        return addItemCssClass;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddItemCssClass(java.lang.String)
     */
    @Override
    public void setAddItemCssClass(String addItemCssClass) {
        this.addItemCssClass = addItemCssClass;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isHighlightAddItem()
     */
    @Override
    @BeanTagAttribute(name = "highlightAddItem")
    public boolean isHighlightAddItem() {
        return highlightAddItem;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setHighlightAddItem(boolean)
     */
    @Override
    public void setHighlightAddItem(boolean highlightAddItem) {
        this.highlightAddItem = highlightAddItem;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isRenderAddBlankLineButton()
     */
    @Override
    @BeanTagAttribute(name = "renderAddBlankLineButton")
    public boolean isRenderAddBlankLineButton() {
        return renderAddBlankLineButton;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setRenderAddBlankLineButton(boolean)
     */
    @Override
    public void setRenderAddBlankLineButton(boolean renderAddBlankLineButton) {
        this.renderAddBlankLineButton = renderAddBlankLineButton;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getAddBlankLineAction()
     */
    @Override
    @BeanTagAttribute(name = "addBlankLineAction", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Action getAddBlankLineAction() {
        return addBlankLineAction;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddBlankLineAction(org.kuali.rice.krad.uif.element.Action)
     */
    @Override
    public void setAddBlankLineAction(Action addBlankLineAction) {
        this.addBlankLineAction = addBlankLineAction;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getAddLinePlacement()
     */
    @Override
    @BeanTagAttribute(name = "addLinePlacement")
    public String getAddLinePlacement() {
        return addLinePlacement;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddLinePlacement(java.lang.String)
     */
    @Override
    public void setAddLinePlacement(String addLinePlacement) {
        this.addLinePlacement = addLinePlacement;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isRenderSaveLineActions()
     */
    @Override
    @BeanTagAttribute(name = "renderSaveLineActions")
    public boolean isRenderSaveLineActions() {
        return renderSaveLineActions;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setRenderSaveLineActions(boolean)
     */
    @Override
    public void setRenderSaveLineActions(boolean renderSaveLineActions) {
        this.renderSaveLineActions = renderSaveLineActions;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isAddViaLightBox()
     */
    @Override
    @BeanTagAttribute(name = "addViaLightBox")
    public boolean isAddViaLightBox() {
        return addViaLightBox;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddViaLightBox(boolean)
     */
    @Override
    public void setAddViaLightBox(boolean addViaLightBox) {
        this.addViaLightBox = addViaLightBox;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getAddViaLightBoxAction()
     */
    @Override
    @BeanTagAttribute(name = "addViaLightBoxAction", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Action getAddViaLightBoxAction() {
        return addViaLightBoxAction;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setAddViaLightBoxAction(org.kuali.rice.krad.uif.element.Action)
     */
    @Override
    public void setAddViaLightBoxAction(Action addViaLightBoxAction) {
        this.addViaLightBoxAction = addViaLightBoxAction;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#isUseServerPaging()
     */
    @Override
    @BeanTagAttribute(name = "useServerPaging")
    public boolean isUseServerPaging() {
        return useServerPaging;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setUseServerPaging(boolean)
     */
    @Override
    public void setUseServerPaging(boolean useServerPaging) {
        this.useServerPaging = useServerPaging;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getPageSize()
     */
    @Override
    public int getPageSize() {
        return pageSize;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setPageSize(int)
     */
    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getDisplayStart()
     */
    @Override
    public int getDisplayStart() {
        return displayStart;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setDisplayStart(int)
     */
    @Override
    public void setDisplayStart(int displayStart) {
        this.displayStart = displayStart;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getDisplayLength()
     */
    @Override
    public int getDisplayLength() {
        return displayLength;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setDisplayLength(int)
     */
    @Override
    public void setDisplayLength(int displayLength) {
        this.displayLength = displayLength;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#getFilteredCollectionSize()
     */
    @Override
    public int getFilteredCollectionSize() {
        return filteredCollectionSize;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#setFilteredCollectionSize(int)
     */
    @Override
    public void setFilteredCollectionSize(int filteredCollectionSize) {
        this.filteredCollectionSize = filteredCollectionSize;
    }

    /**
     * @return list of total columns
     */
    @BeanTagAttribute(name = "addTotalColumns")
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
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        CollectionGroupBase collectionGroupCopy = (CollectionGroupBase) component;

        collectionGroupCopy.setDisplayCollectionSize(this.displayCollectionSize);
        collectionGroupCopy.setActiveCollectionFilter(this.activeCollectionFilter);

        if (this.addBlankLineAction != null) {
            collectionGroupCopy.setAddBlankLineAction((Action) this.addBlankLineAction.copy());
        }

        collectionGroupCopy.setAddItemCssClass(this.addItemCssClass);

        if (addLineItems != null && !addLineItems.isEmpty()) {
            List<Component> addLineItemsCopy = ComponentUtils.copy(new ArrayList<Component>(addLineItems));
            collectionGroupCopy.setAddLineItems(addLineItemsCopy);
        }

        if (addLineActions != null && !addLineActions.isEmpty()) {
            List<Action> addLineActionsCopy = ComponentUtils.copy(addLineActions);
            collectionGroupCopy.setAddLineActions(addLineActionsCopy);
        }

        if (this.addLineBindingInfo != null) {
            collectionGroupCopy.setAddLineBindingInfo((BindingInfo) this.addLineBindingInfo.copy());
        }

        if (this.addLineLabel != null) {
            collectionGroupCopy.setAddLineLabel((Message) this.addLineLabel.copy());
        }

        collectionGroupCopy.setAddLinePlacement(this.addLinePlacement);
        collectionGroupCopy.setAddLinePropertyName(this.addLinePropertyName);
        collectionGroupCopy.setAddViaLightBox(this.addViaLightBox);

        if (this.addViaLightBoxAction != null) {
            collectionGroupCopy.setAddViaLightBoxAction((Action) this.addViaLightBoxAction.copy());
        }

        if (this.bindingInfo != null) {
            collectionGroupCopy.setBindingInfo((BindingInfo) this.bindingInfo.copy());
        }

        if (this.collectionLookup != null) {
            collectionGroupCopy.setCollectionLookup((QuickFinder) this.collectionLookup.copy());
        }

        collectionGroupCopy.setCollectionObjectClass(this.collectionObjectClass);
        
        if (this.filters != null && !this.filters.isEmpty()) {
            collectionGroupCopy.setFilters(new ArrayList<CollectionFilter>(this.filters));
        }
        
        collectionGroupCopy.setHighlightAddItem(this.highlightAddItem);
        collectionGroupCopy.setHighlightNewItems(this.highlightNewItems);
        collectionGroupCopy.setIncludeLineSelectionField(this.includeLineSelectionField);
        collectionGroupCopy.setUseServerPaging(this.useServerPaging);
        collectionGroupCopy.setPageSize(this.pageSize);
        collectionGroupCopy.setDisplayStart(this.displayStart);
        collectionGroupCopy.setDisplayLength(this.displayLength);

        if (lineActions != null && !lineActions.isEmpty()) {
            List<Action> lineActionsCopy = ComponentUtils.copy(lineActions);
            collectionGroupCopy.setLineActions(lineActionsCopy);
        }
        
        collectionGroupCopy.setLineSelectPropertyName(this.lineSelectPropertyName);
        collectionGroupCopy.setNewItemsCssClass(this.newItemsCssClass);
        collectionGroupCopy.setPropertyName(this.propertyName);
        collectionGroupCopy.setRenderAddBlankLineButton(this.renderAddBlankLineButton);
        collectionGroupCopy.setRenderAddLine(this.renderAddLine);
        collectionGroupCopy.setRenderInactiveToggleButton(this.renderInactiveToggleButton);
        collectionGroupCopy.setActiveCollectionFilter(this.activeCollectionFilter);
        collectionGroupCopy.setFilters(this.filters);

        collectionGroupCopy.setRenderLineActions(this.renderLineActions);
        collectionGroupCopy.setRenderSaveLineActions(this.renderSaveLineActions);
        collectionGroupCopy.setShowInactiveLines(this.showInactiveLines);

        if (this.unauthorizedLineBindingInfos != null && !this.unauthorizedLineBindingInfos.isEmpty()) {
            List<BindingInfo> unauthorizedLineBindingInfosCopy = new ArrayList<BindingInfo>();

            for (BindingInfo bindingInfo : this.unauthorizedLineBindingInfos) {
                unauthorizedLineBindingInfosCopy.add((BindingInfo) bindingInfo.copy());
            }

            collectionGroupCopy.setUnauthorizedLineBindingInfos(unauthorizedLineBindingInfosCopy);
        }

        if (subCollections != null && !subCollections.isEmpty()) {
            List<CollectionGroup> subCollectionsCopy = ComponentUtils.copy(subCollections);
            collectionGroupCopy.setSubCollections(subCollectionsCopy);
        }
        collectionGroupCopy.setSubCollectionSuffix(this.subCollectionSuffix);

        if (this.totalColumns != null) {
            collectionGroupCopy.setTotalColumns(new ArrayList<String>(this.totalColumns));
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.container.CollectionGroup#completeValidation(org.kuali.rice.krad.datadictionary.validator.ValidationTrace)
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checking if collectionObjectClass is set
        if (getCollectionObjectClass() == null) {
            if (Validator.checkExpressions(this, "collectionObjectClass")) {
                String currentValues[] = {"collectionObjectClass = " + getCollectionObjectClass()};
                tracer.createWarning("CollectionObjectClass is not set (disregard if part of an abstract)",
                        currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }
    
}
