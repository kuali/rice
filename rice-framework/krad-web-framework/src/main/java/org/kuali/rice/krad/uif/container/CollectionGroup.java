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

import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.QuickFinder;

/**
 * Interface representing an editable collection within a view. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface CollectionGroup extends Group, DataBinding {

    /**
     * Sets a reference in the context map for all nested components in the collection group
     * instance, and sets selected collection path and id data attributes on nested actions of this group.
     */
    void pushCollectionGroupToReference();

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
    void initializeNewCollectionLine(View view, Object model, CollectionGroup collectionGroup,
            boolean clearExistingLine);

    /**
     * Object class the collection maintains. Used to get dictionary information
     * in addition to creating new instances for the collection when necessary
     *
     * @return collection object class
     */
    Class<?> getCollectionObjectClass();

    /**
     * Setter for the collection object class
     *
     * @param collectionObjectClass
     */
    void setCollectionObjectClass(Class<?> collectionObjectClass);

    /**
     * Setter for the collections property name
     *
     * @param propertyName
     */
    void setPropertyName(String propertyName);

    /**
     * Action fields that should be rendered for each collection line. Example
     * line action is the delete action
     *
     * @return line action fields
     */
    List<? extends Component> getLineActions();

    /**
     * Setter for the line action fields list
     *
     * @param lineActions
     */
    void setLineActions(List<? extends Component> lineActions);

    /**
     * Indicates whether the action column for the collection should be rendered
     *
     * @return true if the actions should be rendered, false if not
     * @see #getLineActions()
     */
    boolean isRenderLineActions();

    /**
     * Setter for the render line actions indicator
     *
     * @param renderLineActions
     */
    void setRenderLineActions(boolean renderLineActions);

    /**
     * Indicates whether an add line should be rendered for the collection
     *
     * @return true if add line should be rendered, false if it should not be
     */
    boolean isRenderAddLine();

    /**
     * Setter for the render add line indicator
     *
     * @param renderAddLine
     */
    void setRenderAddLine(boolean renderAddLine);

    /**
     * Get the id of the add line action to invoke when the enter key is pressed.
     *
     * <p>Use '@DEFAULT' if supposed to use first action where 'defaultEnterKeyAction'
     * property is set to true.</p>
     * 
     * @return id or '@DEFAULT'
     */
    String getAddLineEnterKeyAction();

    /**
     * @see #getAddLineEnterKeyAction()
     */
    void setAddLineEnterKeyAction(String addLineEnterKeyAction);

    /**
     * Get the id of the nonAdd line action to invoke when the enter key is pressed.
     *
     * <p>Use '@DEFAULT' if supposed to use first action where 'defaultEnterKeyAction'
     * property is set to true. In a collection may have to use SpringEL to identify
     * line ID values. Here is a sample value: DemoButton@{#lineSuffix}. Notice the use
     * of '@{#lineSuffix}' to help append line suffix information.</p>
     * 
     * @return id or '@DEFAULT'
     */
    String getLineEnterKeyAction();

    /**
     * @see #getLineEnterKeyAction()
     */
    void setLineEnterKeyAction(String lineEnterKeyAction);

    /**
     * Convenience getter for the add line label field text. The text is used to
     * label the add line when rendered and its placement depends on the
     * <code>LayoutManager</code>
     *
     * <p>
     * For the <code>TableLayoutManager</code> the label appears in the sequence
     * column to the left of the add line fields. For the
     * <code>StackedLayoutManager</code> the label is placed into the group
     * header for the line.
     * </p>
     *
     * @return add line label
     */
    String getAddLabel();

    /**
     * Setter for the add line label text
     *
     * @param addLabelText
     */
    void setAddLabel(String addLabelText);

    /**
     * <code>Message</code> instance for the add line label
     *
     * @return add line Message
     * @see #getAddLabel
     */
    Message getAddLineLabel();

    /**
     * Setter for the <code>Message</code> instance for the add line label
     *
     * @param addLineLabel
     * @see #getAddLabel
     */
    void setAddLineLabel(Message addLineLabel);

    /**
     * Name of the property that contains an instance for the add line. If set
     * this is used with the binding info to create the path to the add line.
     * Can be left blank in which case the framework will manage the add line
     * instance in a generic map.
     *
     * @return add line property name
     */
    String getAddLinePropertyName();

    /**
     * Setter for the add line property name
     *
     * @param addLinePropertyName
     */
    void setAddLinePropertyName(String addLinePropertyName);

    /**
     * <code>BindingInfo</code> instance for the add line property used to
     * determine the full binding path. If add line name given
     * {@link #getAddLabel} then it is set as the binding name on the
     * binding info. Add line label and binding info are not required, in which
     * case the framework will manage the new add line instances through a
     * generic map (model must extend UifFormBase)
     *
     * @return BindingInfo add line binding info
     */
    BindingInfo getAddLineBindingInfo();

    /**
     * Setter for the add line binding info
     *
     * @param addLineBindingInfo
     */
    void setAddLineBindingInfo(BindingInfo addLineBindingInfo);

    /**
     * List of <code>Component</code> instances that should be rendered for the
     * collection add line (if enabled). If not set, the default group's items
     * list will be used
     *
     * @return add line field list
     * @see CollectionGroup#performInitialization(Object)
     */
    List<? extends Component> getAddLineItems();

    /**
     * Setter for the add line field list
     *
     * @param addLineItems
     */
    void setAddLineItems(List<? extends Component> addLineItems);

    /**
     * Component fields that should be rendered for the add line.
     *
     * <p>This is generally the add action (button) but can be configured to contain additional
     * components
     * </p>
     *
     * @return add line action fields
     */
    List<? extends Component> getAddLineActions();

    /**
     * Setter for the add line action components fields
     *
     * @param addLineActions
     */
    void setAddLineActions(List<? extends Component> addLineActions);

    /**
     * Indicates whether lines of the collection group should be selected by rendering a
     * field for each line that will allow selection
     *
     * <p>
     * For example, having the select field enabled could allow selecting multiple lines from a search
     * to return (multi-value lookup)
     * </p>
     *
     * @return true if select field should be rendered, false if not
     */
    boolean isIncludeLineSelectionField();

    /**
     * Setter for the render selected field indicator
     *
     * @param includeLineSelectionField
     */
    void setIncludeLineSelectionField(boolean includeLineSelectionField);

    /**
     * When {@link #isIncludeLineSelectionField()} is true, gives the name of the property the select field
     * should bind to
     *
     * <p>
     * Note if no prefix is given in the property name, such as 'form.', it is assumed the property is
     * contained on the collection line. In this case the binding path to the collection line will be
     * appended. In other cases, it is assumed the property is a list or set of String that will hold the
     * selected identifier strings
     * </p>
     *
     * <p>
     * This property is not required. If not the set the framework will use a property contained on
     * <code>UifFormBase</code>
     * </p>
     *
     * @return property name for select field
     */
    String getLineSelectPropertyName();

    /**
     * Setter for the property name that will bind to the select field
     *
     * @param lineSelectPropertyName
     */
    void setLineSelectPropertyName(String lineSelectPropertyName);

    /**
     * Instance of the <code>QuickFinder</code> widget that configures a multi-value lookup for the collection
     *
     * <p>
     * If the collection lookup is enabled (by the render property of the quick finder), {@link
     * #getCollectionObjectClass()} will be used as the data object class for the lookup (if not set). Field
     * conversions need to be set as usual and will be applied for each line returned
     * </p>
     *
     * @return instance configured for the collection lookup
     */
    QuickFinder getCollectionLookup();

    /**
     * Setter for the collection lookup quickfinder instance
     *
     * @param collectionLookup
     */
    void setCollectionLookup(QuickFinder collectionLookup);

    /**
     * Indicates whether inactive collections lines should be displayed
     *
     * <p>
     * Setting only applies when the collection line type implements the
     * <code>Inactivatable</code> interface. If true and showInactive is
     * set to false, the collection will be filtered to remove any items
     * whose active status returns false
     * </p>
     *
     * @return true to show inactive records, false to not render inactive records
     */
    boolean isShowInactiveLines();

    /**
     * Setter for the show inactive indicator
     *
     * @param showInactiveLines boolean show inactive
     */
    void setShowInactiveLines(boolean showInactiveLines);

    /**
     * Collection filter instance for filtering the collection data when the
     * showInactive flag is set to false
     *
     * @return CollectionFilter
     */
    CollectionFilter getActiveCollectionFilter();

    /**
     * Setter for the collection filter to use for filter inactive records from the
     * collection
     *
     * @param activeCollectionFilter CollectionFilter instance
     */
    void setActiveCollectionFilter(CollectionFilter activeCollectionFilter);

    /**
     * List of {@link CollectionFilter} instances that should be invoked to filter the collection before
     * displaying
     *
     * @return List<CollectionFilter>
     */
    List<CollectionFilter> getFilters();

    /**
     * Setter for the List of collection filters for which the collection will be filtered against
     *
     * @param filters
     */
    void setFilters(List<CollectionFilter> filters);

    /**
     * List of property names that should be checked for duplicates in the collection.
     *
     * @return the list of property names that should be checked for duplicates in the collection
     */
    List<String> getDuplicateLinePropertyNames();

    /**
     * @see CollectionGroup#getDuplicateLinePropertyNames()
     */
    void setDuplicateLinePropertyNames(List<String> duplicateLinePropertyNames);

    /**
     *  List of {@link BindingInfo} instances that represent lines not authorized to be viewed or edited by the user.
     */
    List<BindingInfo> getUnauthorizedLineBindingInfos();

    /**
     * @see CollectionGroup#getUnauthorizedLineBindingInfos()
     */
    void setUnauthorizedLineBindingInfos(List<BindingInfo> unauthorizedLineBindingInfos);

    /**
     * List of <code>CollectionGroup</code> instances that are sub-collections
     * of the collection represented by this collection group
     *
     * @return sub collections
     */
    List<CollectionGroup> getSubCollections();

    /**
     * Setter for the sub collection list
     *
     * @param subCollections
     */
    void setSubCollections(List<CollectionGroup> subCollections);

    /**
     * Collection Security object that indicates what authorization (permissions) exist for the collection
     *
     * @return CollectionGroupSecurity instance
     */
    CollectionGroupSecurity getCollectionGroupSecurity();

    /**
     * Override to assert a {@link CollectionGroupSecurity} instance is set
     *
     * @param componentSecurity instance of CollectionGroupSecurity
     */
    void setComponentSecurity(ComponentSecurity componentSecurity);

    /**
     * @see org.kuali.rice.krad.uif.container.CollectionGroupSecurity#isEditLineAuthz()
     */
    boolean isEditLineAuthz();

    /**
     * @see org.kuali.rice.krad.uif.container.CollectionGroupSecurity#setEditLineAuthz(boolean)
     */
    void setEditLineAuthz(boolean editLineAuthz);

    /**
     * @see org.kuali.rice.krad.uif.container.CollectionGroupSecurity#isViewLineAuthz()
     */
    boolean isViewLineAuthz();

    /**
     * @see org.kuali.rice.krad.uif.container.CollectionGroupSecurity#setViewLineAuthz(boolean)
     */
    void setViewLineAuthz(boolean viewLineAuthz);

    /**
     * <code>CollectionGroupBuilder</code> instance that will build the
     * components dynamically for the collection instance
     *
     * @return CollectionGroupBuilder instance
     */
    CollectionGroupBuilder getCollectionGroupBuilder();

    /**
     * Setter for the collection group building instance
     *
     * @param collectionGroupBuilder
     */
    void setCollectionGroupBuilder(CollectionGroupBuilder collectionGroupBuilder);

    /**
     * @param renderInactiveToggleButton the showHideInactiveButton to set
     */
    void setRenderInactiveToggleButton(boolean renderInactiveToggleButton);

    /**
     * @return the showHideInactiveButton
     */
    boolean isRenderInactiveToggleButton();

    /**
     * The number of records to display for a collection
     *
     * @return int
     */
    int getDisplayCollectionSize();

    /**
     * Setter for the display collection size
     *
     * @param displayCollectionSize
     */
    void setDisplayCollectionSize(int displayCollectionSize);

    /**
     * Indicates whether new items should be styled with the #newItemsCssClass
     *
     * @return true if new items must be highlighted
     */
    boolean isHighlightNewItems();

    /**
     * Setter for the flag that allows for different styling of new items
     *
     * @param highlightNewItems
     */
    void setHighlightNewItems(boolean highlightNewItems);

    /**
     * The css style class that will be added on new items
     *
     * @return the new items css style class
     */
    String getNewItemsCssClass();

    /**
     * Setter for the new items css style class
     *
     * @param newItemsCssClass
     */
    void setNewItemsCssClass(String newItemsCssClass);

    /**
     * The css style class that will be added on the add item group or row
     *
     * @return the add item group or row css style class
     */
    String getAddItemCssClass();

    /**
     * Setter for the add item css style class
     *
     * @param addItemCssClass
     */
    void setAddItemCssClass(String addItemCssClass);

    /**
     * Indicates whether the add item group or row should be styled with the #addItemCssClass
     *
     * @return true if add item group or row must be highlighted
     */
    boolean isHighlightAddItem();

    /**
     * Setter for the flag that allows for different styling of the add item group or row
     *
     * @param highlightAddItem
     */
    void setHighlightAddItem(boolean highlightAddItem);

    /**
     * Indicates that a button will be rendered that allows the user to add blank lines to the collection
     *
     * <p>
     * The button will be added separately from the collection items. The default add line wil not be rendered. The
     * action of the button will call the controller, add the blank line to the collection and do a component refresh.
     * </p>
     *
     * @return boolean
     */
    boolean isRenderAddBlankLineButton();

    /**
     * Setter for the flag indicating that the add blank line button must be rendered
     *
     * @param renderAddBlankLineButton
     */
    void setRenderAddBlankLineButton(boolean renderAddBlankLineButton);

    /**
     * The add blank line {@link Action} field rendered when renderAddBlankLineButton is true
     *
     * @return boolean
     */
    Action getAddBlankLineAction();

    /**
     * Setter for the add blank line {@link Action} field
     *
     * @param addBlankLineAction
     */
    void setAddBlankLineAction(Action addBlankLineAction);

    /**
     * Indicates the add line placement
     *
     * <p>
     * Valid values are 'TOP' or 'BOTTOM'. The default is 'TOP'. When the value is 'BOTTOM' the blank line will be
     * added
     * to the end of the collection.
     * </p>
     *
     * @return the add blank line action placement
     */
    String getAddLinePlacement();

    /**
     * Setter for the add line placement
     *
     * @param addLinePlacement add line placement string
     */
    void setAddLinePlacement(String addLinePlacement);

    /**
     * Indicates whether the save line actions should be rendered
     *
     * @return boolean
     */
    boolean isRenderSaveLineActions();

    /**
     * Setter for the flag indicating whether the save actions should be rendered
     *
     * @param renderSaveLineActions
     */
    void setRenderSaveLineActions(boolean renderSaveLineActions);

    /**
     * Indicates that a add action should be rendered and that the add group be displayed in a model dialog.
     *
     * @return boolean true if add should be through model dialog, false if not
     */
    boolean isAddWithDialog();

    /**
     * @see CollectionGroup#isAddWithDialog()
     */
    void setAddWithDialog(boolean addWithDialog);

    /**
     * The {@link Action} that will be displayed that will open the add line group in a dialog.
     *
     * @return Action
     */
    Action getAddWithDialogAction();

    /**
     * @see CollectionGroup#getAddWithDialogAction()
     */
    void setAddWithDialogAction(Action addViaLightBoxAction);

    /**
     * Dialog group to use for the add line when {@link CollectionGroup#isAddWithDialog()} is true.
     *
     * <p>If dialog group is not set by add with dialog is true, a default dialog group will be created.</p>
     *
     * <p>The add line items and actions are still used as usual, unless the items and footer items have been
     * explicity set in the dialog group</p>
     *
     * @return dialog group instance for add line
     */
    DialogGroup getAddLineDialog();

    /**
     * @see CollectionGroup#getAddLineDialog()
     */
    void setAddLineDialog(DialogGroup addLineDialog);

    /**
     * Gets useServerPaging, the flag that indicates whether server side paging is enabled.  Defaults to false.
     *
     * @return true if server side paging is enabled.
     */
    boolean isUseServerPaging();

    /**
     * Sets useServerPaging, the flag indicating whether server side paging is enabled.
     *
     * @param useServerPaging the useServerPaging value to set
     */
    void setUseServerPaging(boolean useServerPaging);

    int getPageSize();

    void setPageSize(int pageSize);

    /**
     * Gets the displayStart, the index of the first item to display on the page (assuming useServerPaging is enabled).
     *
     * <p>if this field has not been set, the returned value will be -1</p>
     *
     * @return the index of the first item to display, or -1 if unset
     */
    int getDisplayStart();

    /**
     * Sets the displayStart, the index of the first item to display on the page (assuming useServerPaging is enabled).
     *
     * @param displayStart the displayStart to set
     */
    void setDisplayStart(int displayStart);

    /**
     * Gets the displayLength, the number of items to display on the page (assuming useServerPaging is enabled).
     *
     * <p>if this field has not been set, the returned value will be -1</p>
     *
     * @return the number of items to display on the page, or -1 if unset
     */
    int getDisplayLength();

    /**
     * Sets the displayLength, the number of items to display on the page (assuming useServerPaging is enabled).
     *
     * @param displayLength the displayLength to set
     */
    void setDisplayLength(int displayLength);

    /**
     * Gets the number of un-filtered elements from the model collection.
     *
     * <p>if this field has not been set, the returned value will be -1</p>
     *
     * @return the filtered collection size, or -1 if unset
     */
    int getFilteredCollectionSize();

    /**
     * Sets the number of un-filtered elements from the model collection.
     *
     * <p>This value is used for display and rendering purposes, it has no effect on the model collection</p>
     *
     * @param filteredCollectionSize the filtered collection size
     */
    void setFilteredCollectionSize(int filteredCollectionSize);

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
    void completeValidation(ValidationTrace tracer);

}
