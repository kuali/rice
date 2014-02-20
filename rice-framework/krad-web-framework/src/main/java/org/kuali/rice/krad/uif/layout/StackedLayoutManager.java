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

import java.util.List;

import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.widget.Pager;

/**
 * Layout manager interface for stacked collections. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface StackedLayoutManager extends CollectionLayoutManager {

    /**
     * Text to appears in the header for each collection lines Group. Used in
     * conjunction with {@link #getSummaryFields()} to build up the final header
     * text
     *
     * @return summary title text
     */
    String getSummaryTitle();

    /**
     * Setter for the summary title text
     *
     * @param summaryTitle
     */
    void setSummaryTitle(String summaryTitle);

    /**
     * List of attribute names from the collection line class that should be
     * used to build the line summary. To build the summary the value for each
     * attribute is retrieved from the line instance. All the values are then
     * placed together with a separator.
     *
     * @return summary field names
     * @see StackedLayoutManagerBase#buildLineHeaderText(Object, org.kuali.rice.krad.uif.container.Group)
     */
    List<String> getSummaryFields();

    /**
     * Setter for the summary field name list
     *
     * @param summaryFields
     */
    void setSummaryFields(List<String> summaryFields);

    /**
     * Group instance that will be used for the add line
     *
     * <p>
     * Add line fields and actions configured on the
     * {@code CollectionGroup} will be set onto the add line group (if add
     * line is enabled). If the add line group is not configured, a new instance
     * of the line group prototype will be used for the add line.
     * </p>
     *
     * @return add line group instance
     * @see #getAddLineGroup()
     */
    Group getAddLineGroup();

    /**
     * Setter for the add line group
     *
     * @param addLineGroup
     */
    void setAddLineGroup(Group addLineGroup);

    /**
     * Group instance that is used as a prototype for creating the collection
     * line groups. For each line a copy of the prototype is made and then
     * adjusted as necessary
     *
     * @return Group instance to use as prototype
     */
    Group getLineGroupPrototype();

    /**
     * Setter for the line group prototype
     *
     * @param lineGroupPrototype
     */
    void setLineGroupPrototype(Group lineGroupPrototype);

    /**
     * Setter for the sub-collection field group prototype
     *
     * @param subCollectionFieldGroupPrototype
     */
    void setSubCollectionFieldGroupPrototype(FieldGroup subCollectionFieldGroupPrototype);

    /**
     * Field instance that serves as a prototype for creating the select field on each line when
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#isIncludeLineSelectionField()} is true
     *
     * <p>
     * This prototype can be used to set the control used for the select field (generally will be a checkbox control)
     * in addition to styling and other setting. The binding path will be formed with using the
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#getLineSelectPropertyName()} or if not set the
     * framework
     * will use {@link org.kuali.rice.krad.web.form.UifFormBase#getSelectedCollectionLines()}
     * </p>
     *
     * @return select field prototype instance
     */
    Field getSelectFieldPrototype();

    /**
     * Setter for the prototype instance for select fields
     *
     * @param selectFieldPrototype
     */
    void setSelectFieldPrototype(Field selectFieldPrototype);

    /**
     * Group that will 'wrap' the generated collection lines so that they have a different layout from the general
     * stacked layout
     *
     * <p>
     * By default (when the wrapper group is null), each collection line will become a group and the groups are
     * rendered one after another. If the wrapper group is configured, the generated groups will be inserted as the
     * items for the wrapper group, and the layout manager configured for the wrapper group will determine how they
     * are rendered. For example, the layout manager could be a grid layout configured for three columns, which would
     * layout the first three lines horizontally then break to a new row.
     * </p>
     *
     * @return Group instance whose items list should be populated with the generated groups, or null to use the
     *         default layout
     */
    Group getWrapperGroup();

    /**
     * Setter for the wrapper group that will receive the generated line groups
     *
     * @param wrapperGroup
     */
    void setWrapperGroup(Group wrapperGroup);

    /**
     * The pagerWidget used for paging when the StackedLayout is using paging
     *
     * @return the pagerWidget
     */
    Pager getPagerWidget();

    /**
     * Set the pagerWidget used for paging StackedLayouts
     *
     * @param pagerWidget
     */
    void setPagerWidget(Pager pagerWidget);

    /**
     * Final {@code List} of Groups to render for the collection
     *
     * @return collection groups
     */
    List<Group> getStackedGroups();

    /**
     * Used by reflection during the lifecycle to get groups for the lifecycle when not using a wrapper group
     *
     * <p>There are no references to this method in the code, this is intentional.  DO NOT REMOVE.</p>
     *
     * @return the stacked groups, if any
     */
    List<Group> getStackedGroupsNoWrapper();

    /**
     * Setter for the collection groups
     *
     * @param stackedGroups
     */
    void setStackedGroups(List<Group> stackedGroups);

    /**
     * Flag that indicates whether actions will be added in the same group as the line items instead of in the
     * footer of the line group
     *
     * @return boolean
     */
    boolean isActionsInLineGroup();

    /**
     * Set flag to add actions in the same group as the line items
     *
     * @param actionsInLineGroup
     */
    void setActionsInLineGroup(boolean actionsInLineGroup);
    
   /**
    * Get a string representation of all style classes defined by this layout manager.
    * 
    * @return string representing CSS classes
    */
    String getStyleClassesAsString();

}