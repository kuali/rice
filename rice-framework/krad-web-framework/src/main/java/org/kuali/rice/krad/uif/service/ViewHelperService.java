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
package org.kuali.rice.krad.uif.service;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.Inquiry;

/**
 * Provides methods for implementing the various phases of a <code>View</code>
 *
 * <ul>
 * <li>Initialize Phase: Invoked when the view is first requested to setup
 * necessary state</li>
 * </ul>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewHelperService {

    /**
     * Return an instance of {@link org.kuali.rice.krad.uif.view.ExpressionEvaluator} that can be used for evaluating
     * expressions
     * contained on the view
     *
     * <p>
     * A ExpressionEvaluator must be initialized with a model for expression evaluation. One instance is
     * constructed for the view lifecycle and made available to all components/helpers through this method
     * </p>
     *
     * @return instance of ExpressionEvaluator
     */
    ExpressionEvaluator getExpressionEvaluator();

    /**
     * Hook for service overrides to perform custom initialization prior to view initialization.
     * 
     * @param model The model.
     */
    void performCustomViewInitialization(Object model);

    /**
     * Hook for service overrides to perform custom initialization on the component
     * 
     * @param view view instance containing the component
     * @param component component instance to initialize
     */
    void performCustomInitialization(Component component);

    /**
     * Hook for service overrides to perform custom apply model logic on the component
     * 
     * @param view view instance containing the component
     * @param component component instance to apply model to
     * @param model Top level object containing the data (could be the model or a top level business
     *        object, dto)
     */
    void performCustomApplyModel(Component component, Object model);

    /**
     * Hook for service overrides to perform custom component finalization
     * 
     * @param component component instance to update
     * @param model Top level object containing the data
     * @param parent Parent component for the component being finalized
     */
    void performCustomFinalize(Component component, Object model, Component parent);

    /**
     * Hook for service overrides to perform view component finalization
     * 
     * @param model Top level object containing the data
     */
    void performCustomViewFinalize(Object model);

    /**
     * Hook for service overrides to process the new collection line before it is added to the
     * collection
     * 
     * @param view view instance that is being presented (the action was taken on)
     * @param collectionGroup collection group component for the collection the line will be added
     *        to
     * @param model object instance that contain's the views data
     * @param addLine the new line instance to be processed
     */
    void processBeforeAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine);

    /**
     * Hook for service overrides to process the new collection line after it has been added to the
     * collection
     * 
     * @param view view instance that is being presented (the action was taken on)
     * @param collectionGroup collection group component for the collection the line that was added
     * @param model object instance that contain's the views data
     * @param addLine the new line that was added
     * @param isValidLine indicates if the line is valid
     */
    void processAfterAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine,
            boolean isValidLine);

    /**
     * Hook for service overrides to process the save collection line before it is validated
     * 
     * @param view view instance that is being presented (the action was taken on)
     * @param collectionGroup collection group component for the collection
     * @param model object instance that contain's the views data
     * @param addLine the new line instance to be processed
     */
    void processBeforeSaveLine(View view, CollectionGroup collectionGroup, Object model, Object addLine);

    /**
     * Hook for service overrides to process the save collection line after it has been validated
     * 
     * @param view view instance that is being presented (the action was taken on)
     * @param collectionGroup collection group component for the collection
     * @param model object instance that contains the views data
     * @param addLine the new line that was added
     */
    void processAfterSaveLine(View view, CollectionGroup collectionGroup, Object model, Object addLine);

    /**
     * Hook for service overrides to process the collection line after it has been deleted
     * 
     * @param view view instance that is being presented (the action was taken on)
     * @param collectionGroup collection group component for the collection the line that was added
     * @param model object instance that contains the views data
     * @param lineIndex index of the line that was deleted
     */
    void processAfterDeleteLine(View view, CollectionGroup collectionGroup, Object model, int lineIndex);

    /**
     * Hook for creating new components with code and adding them to a container
     * 
     * <p>
     * Subclasses can override this method to check for one or more containers by id and then adding
     * components created in code. This is invoked before the initialize method on the container
     * component, so the full lifecycle will be run on the components returned.
     * </p>
     * 
     * <p>
     * New components instances can be retrieved using {@link ComponentFactory}
     * </p>
     * 
     * @param view view instance the container belongs to
     * @param model object containing the view data
     * @param container container instance to add components to
     */
    void addCustomContainerComponents(Object model, Container container);

    /**
     * Invoked when the add line action is chosen for a collection. The
     * collection path gives the full path to the collection that action was
     * selected for. Here validation can be performed on the line as well as
     * further processing on the line such as defaults. If the action is valid
     * the line should be added to the collection, otherwise errors should be
     * added to the global <code>MessageMap</code>
     *
     * @param view view instance that is being presented (the action was taken on)
     * @param model Top level object containing the view data including the
     * collection and new line
     * @param collectionPath full path to the collection on the model
     */
    public void processCollectionAddLine(View view, Object model, String collectionPath);

    /**
     * Adds a blank line to the collection
     *
     * <p>
     * Adds a new collection item to the collection and applies any default values.
     * </p>
     *
     * @param view view instance that is being presented (the action was taken on)
     * @param model Top level object containing the view data including the collection and new line
     * @param collectionPath full path to the collection on the model
     */
    public void processCollectionAddBlankLine(View view, Object model, String collectionPath);

    /**
     * Invoked when the save line action is chosen for a collection. This method only does server side validation by
     * default but creates hook for client applications to add additional logic like persisting data.
     *
     * @param view view instance that is being presented (the action was taken on)
     * @param model Top level object containing the view data including the collection and new line
     * @param collectionPath full path to the collection on the model
     */
    public void processCollectionSaveLine(View view, Object model, String collectionPath, int selectedLineIndex);

    /**
     * Invoked when the delete line action is chosen for a collection. The
     * collection path gives the full path to the collection that action was
     * selected for. Here validation can be performed to make sure the action is
     * allowed. If the action is valid the line should be deleted from the
     * collection, otherwise errors should be added to the global
     * <code>MessageMap</code>
     *
     * @param view view instance that is being presented (the action was taken on)
     * @param model Top level object containing the view data including the collection
     * @param collectionPath full path to the collection on the model
     * @param lineIndex index of the collection line that was selected for removal
     */
    public void processCollectionDeleteLine(View view, Object model, String collectionPath, int lineIndex);

    /**
     * Process the results returned from a multi-value lookup populating the lines for the collection given
     * by the path
     *
     * @param view view instance the collection belongs to
     * @param model object containing the view data
     * @param collectionPath binding path to the collection to populated
     * @param lookupResultValues String containing the selected line values
     */
    public void processMultipleValueLookupResults(View view, Object model, String collectionPath, String lookupResultValues);

    /**
     * Generates table formatted data based on data collected from the table model
     *
     * @param view view instance where the table is located
     * @param model top level object containing the data
     * @param tableId id of the table being generated
     * @param formatType format which the table should be generated in
     * @return
     */
    public String buildExportTableData(View view, Object model, String tableId, String formatType);

    /**
     * Invoked by the <code>Inquiry</code> widget to build the inquiry link
     *
     * <p>
     * Note this is used primarily for custom <code>Inquirable</code>
     * implementations to customize the inquiry class or parameters for an
     * inquiry. Instead of building the full inquiry link, implementations can
     * make a callback to
     * org.kuali.rice.krad.uif.widget.Inquiry.buildInquiryLink(Object, String,
     * Class<?>, Map<String, String>) given an inquiry class and parameters to
     * build the link field.
     * </p>
     *
     * @param dataObject parent object for the inquiry property
     * @param propertyName name of the property the inquiry is being built for
     * @param inquiry instance of the inquiry widget being built for the property
     */
    public void buildInquiryLink(Object dataObject, String propertyName, Inquiry inquiry);

}
