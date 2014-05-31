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
package org.kuali.rice.krad.uif.container.collections;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.layout.CollectionLayoutManager;
import org.kuali.rice.krad.uif.view.ViewModel;

import java.io.Serializable;
import java.util.List;

/**
 * Holds components and configuration for a line during the build process.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.uif.container.CollectionGroupBuilder
 * @see org.kuali.rice.krad.uif.container.CollectionGroupLineBuilder
 */
public class LineBuilderContext implements Serializable {
    private static final long serialVersionUID = -2025777471407211781L;

    private int lineIndex;
    private Object currentLine;
    private String bindingPath;
    private boolean bindToForm;

    private ViewModel model;
    private CollectionGroup collectionGroup;

    private List<? extends Component> lineActions;
    private List<Field> lineFields;
    private List<FieldGroup> subCollectionFields;

    /**
     * Empty constructor.
     */
    public LineBuilderContext() {

    }

    /**
     * Constructor.
     *
     * @param lineIndex index of line
     * @param currentLine object containing the line data
     * @param bindingPath path to the line in the model
     * @param bindToForm indicates if the line fields bind to the form (not the default object path)
     * @param model object containing the views data
     * @param collectionGroup collection group instance the line is being built for
     * @param lineActions list of components for the lines action column
     */
    public LineBuilderContext(int lineIndex, Object currentLine, String bindingPath, boolean bindToForm, ViewModel model,
            CollectionGroup collectionGroup, List<? extends Component> lineActions) {
        this.lineIndex = lineIndex;
        this.currentLine = currentLine;
        this.bindingPath = bindingPath;
        this.bindToForm = bindToForm;
        this.model = model;
        this.collectionGroup = collectionGroup;
        this.lineActions = lineActions;
    }

    /**
     * Suffix to use for adjusting the ids on components within the line.
     *
     * @return String id suffix
     */
    public String getIdSuffix() {
        String idSuffix;

        if (isAddLine()) {
            idSuffix = UifConstants.IdSuffixes.ADD_LINE;
        } else {
            idSuffix = UifConstants.IdSuffixes.LINE + Integer.toString(lineIndex);
        }

        return idSuffix;
    }

    /**
     * Indicates whether the line is the add line, or an existing collection line.
     *
     * @return boolean true if the line is the add line, false if not
     */
    public boolean isAddLine() {
        return this.lineIndex == -1;
    }

    /**
     * Returns the {@link org.kuali.rice.krad.uif.layout.CollectionLayoutManager} configured on the collection
     * group.
     *
     * @return collection layout manager instance
     */
    public CollectionLayoutManager getLayoutManager() {
        if (this.collectionGroup != null) {
            return (CollectionLayoutManager) this.collectionGroup.getLayoutManager();
        }

        return null;
    }

    /**
     * Index for the line within the collection, or -1 for the add line.
     *
     * @return line index
     */
    public int getLineIndex() {
        return lineIndex;
    }

    /**
     * @see LineBuilderContext#getLineIndex()
     */
    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    /**
     * Object containing the line's data.
     *
     * @return object instance
     */
    public Object getCurrentLine() {
        return currentLine;
    }

    /**
     * @see LineBuilderContext#getCurrentLine()
     */
    public void setCurrentLine(Object currentLine) {
        this.currentLine = currentLine;
    }

    /**
     * Path to the line in the full model.
     *
     * @return binding path
     */
    public String getBindingPath() {
        return bindingPath;
    }

    /**
     * @see LineBuilderContext#getBindingPath()
     */
    public void setBindingPath(String bindingPath) {
        this.bindingPath = bindingPath;
    }

    /**
     * Indicates if the line fields bind to the form (not the default object path).
     *
     * @return boolean true if line fields bindi to the form, false if not
     */
    public boolean isBindToForm() {
        return bindToForm;
    }

    /**
     * @see LineBuilderContext#isBindToForm()
     */
    public void setBindToForm(boolean bindToForm) {
        this.bindToForm = bindToForm;
    }

    /**
     * Object containing the view's data.
     *
     * @return model instance
     */
    public ViewModel getModel() {
        return model;
    }

    /**
     * @see LineBuilderContext#getModel()
     */
    public void setModel(ViewModel model) {
        this.model = model;
    }

    /**
     * Collection group the line is being built for.
     *
     * @return collection group instance
     */
    public CollectionGroup getCollectionGroup() {
        return collectionGroup;
    }

    /**
     * @see LineBuilderContext#getCollectionGroup()
     */
    public void setCollectionGroup(CollectionGroup collectionGroup) {
        this.collectionGroup = collectionGroup;
    }

    /**
     * List of components to render in the lines action column.
     *
     * @return list of component instances
     */
    public List<? extends Component> getLineActions() {
        return lineActions;
    }

    /**
     * @see LineBuilderContext#getLineActions()
     */
    public void setLineActions(List<? extends Component> lineActions) {
        this.lineActions = lineActions;
    }

    /**
     * List of field instances that make up the lines columns.
     *
     * @return list of field instances.
     */
    public List<Field> getLineFields() {
        return lineFields;
    }

    /**
     * @see LineBuilderContext#getLineFields()
     */
    public void setLineFields(List<Field> lineFields) {
        this.lineFields = lineFields;
    }

    /**
     * List of field groups that wrap the sub-collections for the line.
     *
     * @return list of field groups instances
     */
    public List<FieldGroup> getSubCollectionFields() {
        return subCollectionFields;
    }

    /**
     * @see LineBuilderContext#getSubCollectionFields()
     */
    public void setSubCollectionFields(List<FieldGroup> subCollectionFields) {
        this.subCollectionFields = subCollectionFields;
    }
}
