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
package org.kuali.rice.krad.uif.service;

import java.util.Map;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.ExpressionEvaluatorFactory;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.widget.Inquiry;

/**
 * Provides customization methods at various points of the view lifecycle.
 *
 * <p>Custom view helpers can be configured with view property
 * {@link org.kuali.rice.krad.uif.view.View#getViewHelperServiceClass()}</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewHelperService {

    /**
     * Uses reflection to find all fields defined on the <code>View</code> instance that have the
     * <code>RequestParameter</code> annotation (which indicates the field may be populated by the
     * request).
     *
     * <p>
     * The <code>View</code> instance is inspected for fields that have the
     * <code>RequestParameter</code> annotation and if corresponding parameters are found in the
     * request parameter map, the request value is used to set the view property. The Map of
     * parameter name/values that match are placed in the view so they can be later retrieved to
     * rebuild the view. Custom <code>ViewServiceHelper</code> implementations can add additional
     * parameter key/value pairs to the returned map if necessary.
     * </p>
     *
     * <p>
     * For each field found, if there is a corresponding key/value pair in the request parameters,
     * the value is used to populate the field. In addition, any conditional properties of
     * <code>PropertyReplacers</code> configured for the field are cleared so that the request
     * parameter value does not get overridden by the dictionary conditional logic
     * </p>
     *
     * @param parameters The request parameters that apply to the view.
     * @see org.kuali.rice.krad.uif.component.RequestParameter
     */
    void populateViewFromRequestParameters(Map<String, String> parameters);
    
    /**
     * Hook for service overrides to perform custom initialization prior to view initialization.
     * 
     * @param model The model.
     */
    void performCustomViewInitialization(Object model);

    /**
     * Hook for service overrides to perform custom initialization on the component
     * 
     * @param component component instance to initialize
     */
    void performCustomInitialization(LifecycleElement component);

    /**
     * Hook for service overrides to perform custom apply model logic on the component
     * 
     * @param component component instance to apply model to
     * @param model Top level object containing the data (could be the model or a top level business
     *        object, dto)
     */
    void performCustomApplyModel(LifecycleElement component, Object model);

    /**
     * Hook for service overrides to perform custom component finalization
     * 
     * @param component component instance to update
     * @param model Top level object containing the data
     * @param parent Parent component for the component being finalized
     */
    void performCustomFinalize(LifecycleElement component, Object model, LifecycleElement parent);

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
     * @param model object instance that contain's the view's data
     * @param addLine the new line instance to be processed
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     */
    void processBeforeAddLine(ViewModel model, Object addLine, String collectionId, String collectionPath);

    /**
     * Hook for service overrides to process the new collection line after it has been added to the
     * collection
     *
     * @param model object instance that contain's the views data
     * @param addLine the new line that was added
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     * @param isValidLine indicates if the line is valid
     */
    void processAfterAddLine(ViewModel model, Object addLine, String collectionId, String collectionPath,
            boolean isValidLine);

    /**
     * Hook for service overrides to process the save collection line before it is validated
     *
     * @param model object instance that contain's the views data
     * @param lineObject the line instance to be processed
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     */
    void processBeforeSaveLine(ViewModel model, Object lineObject, String collectionId, String collectionPath);

    /**
     * Hook for service overrides to process the save collection line after it has been validated
     *
     * @param model object instance that contains the views data
     * @param lineObject the line instance to be processed
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     */
    void processAfterSaveLine(ViewModel model, Object lineObject, String collectionId, String collectionPath);

    /**
     * Hook for service overrides to process the collection line after it has been deleted
     *
     * @param model object instance that contains the views data
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     * @param lineIndex index of the line that was deleted
     */
    void processAfterDeleteLine(ViewModel model, String collectionId, String collectionPath, int lineIndex);

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
     * @param model object containing the view data
     * @param container container instance to add components to
     */
    void addCustomContainerComponents(ViewModel model, Container container);

    /**
     * Invoked when the add line action is chosen for a collection. The
     * collection path gives the full path to the collection that action was
     * selected for. Here validation can be performed on the line as well as
     * further processing on the line such as defaults. If the action is valid
     * the line should be added to the collection, otherwise errors should be
     * added to the global <code>MessageMap</code>
     *
     * @param model Top level object containing the view data including the
     * collection and new line
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     */
    void processCollectionAddLine(ViewModel model, String collectionId, String collectionPath);

    void processAndAddLineObject(ViewModel viewModel, Object newLine, String collectionId,
                String collectionPath);

    /**
     * Adds a blank line to the collection
     *
     * <p>
     * Adds a new collection item to the collection and applies any default values.
     * </p>
     *
     * @param model Top level object containing the view data including the collection and new line
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     */
    void processCollectionAddBlankLine(ViewModel model, String collectionId, String collectionPath);

    /**
     * Invoked when the save line action is chosen for a collection. This method only does server side validation by
     * default but creates hook for client applications to add additional logic like persisting data.
     *
     * @param model Top level object containing the view data including the collection and new line
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     * @param selectedLineIndex The index within the collection of the line to save.
     */
    void processCollectionSaveLine(ViewModel model, String collectionId, String collectionPath, int selectedLineIndex);

    /**
     * Invoked when the delete line action is chosen for a collection. The
     * collection path gives the full path to the collection that action was
     * selected for. Here validation can be performed to make sure the action is
     * allowed. If the action is valid the line should be deleted from the
     * collection, otherwise errors should be added to the global
     * <code>MessageMap</code>
     *
     * @param model Top level object containing the view data including the collection
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     * @param lineIndex index of the collection line that was selected for removal
     */
    void processCollectionDeleteLine(ViewModel model, String collectionId, String collectionPath, int lineIndex);

    /**
     * Process the results returned from a multi-value lookup populating the lines for the collection given
     * by the path
     *
     * @param model object containing the view data
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     * @param multiValueReturnFields String containing the selected line field names
     * @param lookupResultValues String containing the selected line values
     */
    void processMultipleValueLookupResults(ViewModel model, String collectionId, String collectionPath,
            String multiValueReturnFields, String lookupResultValues);

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
    void buildInquiryLink(Object dataObject, String propertyName, Inquiry inquiry);

    /**
     * Sets up the view context which will be available to other components through their context
     * for conditional logic evaluation.
     */
    void setViewContext();

    /**
     * Invoked to set up the context for an element.
     *
     * <p>Context is primarily used for expression evaluation. Any object in the context for a component
     * will be available as a variable within expressions.</p>
     *
     * @param element element to setup context for
     * @param parent parent of the given element
     */
    void setElementContext(LifecycleElement element, LifecycleElement parent);

    /**
     * Invokes the configured <code>PresentationController</code> and </code>Authorizer</code> for
     * the view to get the exported action flags and edit modes that can be used in conditional
     * logic
     */
    void retrieveEditModesAndActionFlags();

    /**
     * Perform a database or data dictionary based refresh of a specific property object
     * 
     * <p>
     * The object needs to be of type PersistableBusinessObject.
     * </p>
     * 
     * @param parentObject parent object that references the object to be refreshed
     * @param referenceObjectName property name of the parent object to be refreshed
     */
    void refreshReference(Object parentObject, String referenceObjectName);

    /**
     * Update the reference objects listed in referencesToRefresh of the model
     * 
     * <p>
     * The the individual references in the referencesToRefresh string are separated by
     * KRADConstants.REFERENCES_TO_REFRESH_SEPARATOR).
     * </p>
     * 
     * @param referencesToRefresh list of references to refresh (
     */
    void refreshReferences(String referencesToRefresh);
    
    /**
     * Retrieves the default value that is configured for the given data field
     * 
     * <p>
     * The field's default value is determined in the following order:
     * 
     * <ol>
     * <li>If default value on field is non-blank</li>
     * <li>If expression is found for default value</li>
     * <li>If default value finder class is configured for field</li>
     * <li>If an expression is found for default values</li>
     * <li>If default values on field is not null</li>
     * </ol>
     * </p>
     * 
     * @param object object that should be populated
     * @param dataField field to retrieve default value for
     * @return Object default value for field or null if value was not found
     */
    Object getDefaultValueForField(Object object, DataField dataField);

    /**
     * Applies the default value configured for the given field (if any) to the line given object
     * property that is determined by the given binding path
     * 
     * @param object object that should be populated
     * @param dataField field to check for configured default value
     * @param bindingPath path to the property on the object that should be populated
     */
    void populateDefaultValueForField(Object object, DataField dataField, String bindingPath);

    /**
     * Builds JS script that will invoke the show growl method to display a growl message when the
     * page is rendered
     * 
     * <p>
     * A growl call will be created for any explicit growl messages added to the message map.
     * </p>
     * 
     * <p>
     * Growls are only generated if @{link
     * org.kuali.rice.krad.uif.view.View#isGrowlMessagingEnabled()} is enabled. If not, the growl
     * messages are set as info messages for the page
     * </p>
     * 
     * @return JS script string for generated growl messages
     */
    String buildGrowlScript();

    /**
     * Iterates through the view components picking up data fields and applying an default value
     * configured
     * 
     * @param component component that should be checked for default values
     */
    void applyDefaultValues(Component component);

    /**
     * Populate default values the model backing a line in a collection group.
     * 
     * @param collectionGroup The collection group.
     * @param line The model object backing the line.
     */
    void applyDefaultValuesForCollectionLine(CollectionGroup collectionGroup, Object line);

    /**
     * Gets an expression evaluator factory for use with the current view.
     *
     * @return expression evaluator factory
     */
    ExpressionEvaluatorFactory getExpressionEvaluatorFactory();

}
