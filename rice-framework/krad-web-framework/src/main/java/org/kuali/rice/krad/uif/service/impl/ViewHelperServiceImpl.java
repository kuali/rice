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
package org.kuali.rice.krad.uif.service.impl;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBaseAdapter;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.ContainerBase;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.service.ViewDictionaryService;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.BooleanMap;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.RecycleUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.ExpressionEvaluatorFactory;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.view.ViewPresentationController;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.GrowlMessage;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.valuefinder.ValueFinder;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.beans.PropertyAccessorUtils;

import com.google.common.collect.Sets;

/**
 * Default Implementation of {@code ViewHelperService}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewHelperServiceImpl implements ViewHelperService, Serializable {
    private static final long serialVersionUID = 1772618197133239852L;
    private static final Logger LOG = Logger.getLogger(ViewHelperServiceImpl.class);

    private transient ConfigurationService configurationService;
    private transient DataDictionaryService dataDictionaryService;
    private transient LegacyDataAdapter legacyDataAdapter;
    private transient DataObjectService dataObjectService;
    private transient ViewDictionaryService viewDictionaryService;
    private transient ExpressionEvaluatorFactory expressionEvaluatorFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCustomContainerComponents(ViewModel model, Container container) {

    }

    /**
     * Finds the <code>Inquirable</code> configured for the given data object class and delegates to
     * it for building the inquiry URL
     *
     * {@inheritDoc}
     */
    public void buildInquiryLink(Object dataObject, String propertyName, Inquiry inquiry) {
        Inquirable inquirable = getViewDictionaryService().getInquirable(dataObject.getClass(), inquiry.getViewName());
        if (inquirable != null) {
            inquirable.buildInquirableLink(dataObject, propertyName, inquiry);
        } else {
            // TODO: should we really not render the inquiry just because the top parent doesn't have an inquirable?
            // it is possible the path is nested and there does exist an inquiry for the property
            // inquirable not found, no inquiry link can be set
            inquiry.setRender(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performCustomApplyModel(LifecycleElement element, Object model) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performCustomFinalize(LifecycleElement element, Object model, LifecycleElement parent) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performCustomInitialization(LifecycleElement element) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performCustomViewFinalize(Object model) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performCustomViewInitialization(Object model) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processAfterAddLine(ViewModel model, Object lineObject, String collectionId, String collectionPath,
            boolean isValidLine) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processAfterDeleteLine(ViewModel model, String collectionId, String collectionPath, int lineIndex) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processAfterSaveLine(ViewModel model, Object lineObject, String collectionId, String collectionPath) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processBeforeAddLine(ViewModel model, Object addLine, String collectionId, String collectionPath) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processBeforeSaveLine(ViewModel model, Object lineObject, String collectionId, String collectionPath) {

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void processCollectionAddBlankLine(ViewModel model, String collectionId, String collectionPath) {
        if (!(model instanceof ViewModel)) {
            return;
        }

        ViewModel viewModel = (ViewModel) model;

        if (collectionId == null) {
            logAndThrowRuntime(
                    "Unable to get collection group component for Id: " + collectionPath + " path: " + collectionPath);
        }

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        Class<?> collectionObjectClass = (Class<?>) viewModel.getViewPostMetadata().getComponentPostData(collectionId,
                UifConstants.PostMetadata.COLL_OBJECT_CLASS);
        Object newLine = KRADUtils.createNewObjectFromClass(collectionObjectClass);

        List<Object> lineDataObjects = new ArrayList<Object>();
        lineDataObjects.add(newLine);
        viewModel.getViewPostMetadata().getAddedCollectionObjects().put(collectionId, lineDataObjects);
        processAndAddLineObject(viewModel, newLine, collectionId, collectionPath);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void processCollectionAddLine(ViewModel model, String collectionId, String collectionPath) {
        // now get the new line we need to add
        BindingInfo addLineBindingInfo = (BindingInfo) model.getViewPostMetadata().getComponentPostData(
                collectionId, UifConstants.PostMetadata.ADD_LINE_BINDING_INFO);
        Object addLine = ObjectPropertyUtils.getPropertyValue(model, addLineBindingInfo.getBindingPath());
        if (addLine == null) {
            logAndThrowRuntime("Add line instance not found for path: " + addLineBindingInfo.getBindingPath());
        }

        // Adding an empty list because this item does not need to be further processed, but needs to init
        // a new add line
        List<Object> lineDataObjects = new ArrayList<Object>();
        model.getViewPostMetadata().getAddedCollectionObjects().put(collectionId, lineDataObjects);

        processAndAddLineObject(model, addLine, collectionId, collectionPath);
    }

    /**
     * Do all processing related to adding a line: calls processBeforeAddLine, performAddLineValidation, addLine,
     * processAfterAddLine
     *
     * @param viewModel object instance that contain's the view's data
     * @param newLine the new line instance to be processed
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     */
    public void processAndAddLineObject(ViewModel viewModel, Object newLine, String collectionId,
            String collectionPath) {
        String addLinePlacement = (String) viewModel.getViewPostMetadata().getComponentPostData(collectionId,
                UifConstants.PostMetadata.ADD_LINE_PLACEMENT);

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(viewModel, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        processBeforeAddLine(viewModel, newLine, collectionId, collectionPath);

        boolean isValidLine = performAddLineValidation(viewModel, newLine, collectionId, collectionPath);
        if (isValidLine) {
            int addedIndex = addLine(collection, newLine, addLinePlacement.equals("TOP"));

            // now link the added line, this is important in situations where perhaps the collection element is
            // bi-directional and needs to point back to it's parent
            linkAddedLine(viewModel, collectionPath, addedIndex);

            KRADServiceLocatorWeb.getLegacyDataAdapter().refreshAllNonUpdatingReferences(newLine);

            if (viewModel instanceof UifFormBase) {
                ((UifFormBase) viewModel).getAddedCollectionItems().add(newLine);
            }
            processAfterAddLine(viewModel, newLine, collectionId, collectionPath, isValidLine);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processCollectionDeleteLine(ViewModel model, String collectionId, String collectionPath,
            int lineIndex) {
        // get the collection instance for deleting the line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // TODO: look into other ways of identifying a line so we can deal with
        // unordered collections
        if (collection instanceof List) {
            Object deleteLine = ((List<Object>) collection).get(lineIndex);

            // validate the delete action is allowed for this line
            boolean isValid = performDeleteLineValidation(model, collectionId, collectionPath, deleteLine);
            if (isValid) {
                ((List<Object>) collection).remove(lineIndex);

                String collectionLabel = (String) model.getViewPostMetadata().getComponentPostData(collectionId,
                        UifConstants.PostMetadata.COLL_LABEL);
                GlobalVariables.getMessageMap().putInfoForSectionId(collectionId,
                        RiceKeyConstants.MESSAGE_COLLECTION_LINE_DELETED, collectionLabel);

                processAfterDeleteLine(model, collectionId, collectionPath, lineIndex);
            }
        } else {
            logAndThrowRuntime("Only List collection implementations are supported for the delete by index method");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processCollectionSaveLine(ViewModel model, String collectionId, String collectionPath,
            int selectedLineIndex) {
        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // TODO: look into other ways of identifying a line so we can deal with
        // unordered collections
        if (collection instanceof List) {
            Object saveLine = ((List<Object>) collection).get(selectedLineIndex);

            processBeforeSaveLine(model, saveLine, collectionId, collectionPath);

            ((UifFormBase) model).getAddedCollectionItems().remove(saveLine);

            processAfterSaveLine(model, saveLine, collectionId, collectionPath);

        } else {
            logAndThrowRuntime("Only List collection implementations are supported for the delete by index method");
        }

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void processMultipleValueLookupResults(ViewModel model, String collectionId, String collectionPath,
            String multiValueReturnFields, String lookupResultValues) {
        // if no line values returned, no population is needed
        if (StringUtils.isBlank(lookupResultValues) || !(model instanceof ViewModel)) {
            return;
        }

        ViewModel viewModel = (ViewModel) model;

        if (StringUtils.isBlank(collectionId)) {
            throw new RuntimeException(
                    "Id is not set for this collection lookup: " + collectionId + ", " + "path: " + collectionPath);
        }

        // retrieve the collection group so we can get the collection class and collection lookup
        Class<?> collectionObjectClass = (Class<?>) viewModel.getViewPostMetadata().getComponentPostData(collectionId,
                UifConstants.PostMetadata.COLL_OBJECT_CLASS);
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            Class<?> collectionClass = ObjectPropertyUtils.getPropertyType(model, collectionPath);
            collection = (Collection<Object>) KRADUtils.createNewObjectFromClass(collectionClass);
            ObjectPropertyUtils.setPropertyValue(model, collectionPath, collection);
        }

        // get the field conversions
        Map<String, String> fieldConversions =
                (Map<String, String>) viewModel.getViewPostMetadata().getComponentPostData(collectionId,
                        UifConstants.PostMetadata.COLL_LOOKUP_FIELD_CONVERSIONS);

        // filter the field conversions by what was returned from the multi value lookup return fields
        Map <String, String> returnedFieldConversions = filterByReturnedFieldConversions(multiValueReturnFields,
                fieldConversions);

        List<String> toFieldNamesColl = new ArrayList<String>(returnedFieldConversions.values());
        Collections.sort(toFieldNamesColl);
        String[] toFieldNames = new String[toFieldNamesColl.size()];
        toFieldNamesColl.toArray(toFieldNames);

        // first split to get the line value sets
        String[] lineValues = StringUtils.split(lookupResultValues, ",");

        List<Object> lineDataObjects = new ArrayList<Object>();
        // for each returned set create a new instance of collection class and populate with returned line values
        for (String lineValue : lineValues) {
            Object lineDataObject = null;

            // TODO: need to put this in data object service so logic can be reused
            ModuleService moduleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(
                    collectionObjectClass);
            if (moduleService != null && moduleService.isExternalizable(collectionObjectClass)) {
                lineDataObject = moduleService.createNewObjectFromExternalizableClass(collectionObjectClass.asSubclass(
                        org.kuali.rice.krad.bo.ExternalizableBusinessObject.class));
            } else {
                lineDataObject = KRADUtils.createNewObjectFromClass(collectionObjectClass);
            }

            String[] fieldValues = StringUtils.splitByWholeSeparatorPreserveAllTokens(lineValue, ":");
            if (fieldValues.length != toFieldNames.length) {
                throw new RuntimeException(
                        "Value count passed back from multi-value lookup does not match field conversion count");
            }

            // set each field value on the line
            for (int i = 0; i < fieldValues.length; i++) {
                String fieldName = toFieldNames[i];
                ObjectPropertyUtils.setPropertyValue(lineDataObject, fieldName, fieldValues[i]);
            }

            lineDataObjects.add(lineDataObject);
            processAndAddLineObject(viewModel, lineDataObject, collectionId, collectionPath);
        }

        viewModel.getViewPostMetadata().getAddedCollectionObjects().put(collectionId, lineDataObjects);
    }

    /**
     * Add addLine to collection while giving derived classes an opportunity to override for things
     * like sorting.
     *
     * @param collection the Collection to add the given addLine to
     * @param addLine the line to add to the given collection
     * @param insertFirst indicates if the item should be inserted as the first item
     *
     * @return the index at which the item was added to the collection, or -1 if it was not added
     */
    protected int addLine(Collection<Object> collection, Object addLine, boolean insertFirst) {
        int index = -1;
        if (insertFirst && (collection instanceof List)) {
            ((List<Object>) collection).add(0, addLine);
            index = 0;
        } else {
            boolean added = collection.add(addLine);
            if (added) {
                index = collection.size() - 1;
            }
        }
        return index;
    }

    protected void linkAddedLine(Object model, String collectionPath, int addedIndex) {
        int lastSepIndex = PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(collectionPath);
        if (lastSepIndex != -1) {
            String collectionParentPath = collectionPath.substring(0, lastSepIndex);
            Object parent = ObjectPropertyUtils.getPropertyValue(model, collectionParentPath);
            if (parent != null && getDataObjectService().supports(parent.getClass())) {
                DataObjectWrapper<?> wrappedParent = getDataObjectService().wrap(parent);
                String collectionName = collectionPath.substring(lastSepIndex + 1);
                wrappedParent.linkChanges(Sets.newHashSet(collectionName + "[" + addedIndex + "]"));
            }
        }
    }

    /**
     * Performs validation on the new collection line before it is added to the corresponding collection.
     *
     * @param viewModel object instance that contain's the view's data
     * @param newLine the new line instance to be processed
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     */
    protected boolean performAddLineValidation(ViewModel viewModel, Object newLine, String collectionId,
            String collectionPath) {
        boolean isValid = true;

        Collection<Object> collectionItems = ObjectPropertyUtils.getPropertyValue(viewModel, collectionPath);

        if (viewModel.getViewPostMetadata().getComponentPostData(collectionId,
                UifConstants.PostMetadata.DUPLICATE_LINE_PROPERTY_NAMES) == null) {
            return isValid;
        }

        List<String> duplicateLinePropertyNames = (List<String>) viewModel.getViewPostMetadata().getComponentPostData(
                collectionId, UifConstants.PostMetadata.DUPLICATE_LINE_PROPERTY_NAMES);

        String collectionLabel = null;
        if (viewModel.getViewPostMetadata().getComponentPostData(collectionId, UifConstants.PostMetadata.COLL_LABEL)
                != null) {
            collectionLabel = (String) viewModel.getViewPostMetadata().getComponentPostData(collectionId,
                    UifConstants.PostMetadata.COLL_LABEL);
        }

        String duplicateLineLabelString = null;
        if (viewModel.getViewPostMetadata().getComponentPostData(collectionId,
                UifConstants.PostMetadata.DUPLICATE_LINE_LABEL_STRING) != null) {
            duplicateLineLabelString = (String) viewModel.getViewPostMetadata().getComponentPostData(collectionId,
                    UifConstants.PostMetadata.DUPLICATE_LINE_LABEL_STRING);
        }

        if (containsDuplicateLine(newLine, collectionItems, duplicateLinePropertyNames)) {
            isValid = false;
            GlobalVariables.getMessageMap().putErrorForSectionId(collectionId, RiceKeyConstants.ERROR_DUPLICATE_ELEMENT,
                    collectionLabel, duplicateLineLabelString);
        }

        return isValid;
    }

    /**
     * Filters the field conversions by the multi value return fields
     * @param multiValueReturnFields the return fields to filter by, as a comma separated string
     * @param fieldConversions the map of field conversions to filter
     * @return a {@link java.util.Map} containing the filtered field conversions
     */
    private Map<String, String> filterByReturnedFieldConversions(String multiValueReturnFields,
            Map<String, String> fieldConversions) {

        Map <String, String> returnedFieldConversions = new HashMap<String, String>();
        returnedFieldConversions.putAll(fieldConversions);

        // parse the multi value return fields string
        String[] returnedFieldsStrArr = StringUtils.split(multiValueReturnFields, ",");
        // iterate over the returned fields and get the conversion values.
        if (returnedFieldsStrArr != null && returnedFieldsStrArr.length > 0) {
            returnedFieldConversions.clear();
            for (String fieldConversion : returnedFieldsStrArr) {
                if (fieldConversions.containsKey(fieldConversion)) {
                    returnedFieldConversions.put(fieldConversion, fieldConversions.get(fieldConversion));
                }
            }
        }

        return returnedFieldConversions;
    }

    /**
     * Determines whether the new line matches one of the entries in the existing collection, based on the
     * {@code duplicateLinePropertyNames}.
     *
     * @param addLine new line instance to validate
     * @param collectionItems items in the collection
     * @param duplicateLinePropertyNames property names to check for duplicates
     * @return true if there is a duplicate line, false otherwise
     */
    private boolean containsDuplicateLine(Object addLine, Collection<Object> collectionItems,
            List<String> duplicateLinePropertyNames) {
        if (collectionItems.isEmpty() || duplicateLinePropertyNames.isEmpty()) {
            return false;
        }

        for (Object collectionItem : collectionItems) {
            if (isDuplicateLine(addLine, collectionItem, duplicateLinePropertyNames)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether the new {@code addLine} is a duplicate of {@code collectionItem}, based on the
     * {@code duplicateLinePropertyNames}.
     *
     * @param addLine new line instance to validate
     * @param collectionItem existing instance to validate
     * @param duplicateLinePropertyNames the property names to check for duplicates
     * @return true if {@code addLine} is a duplicate of {@code collectionItem}, false otherwise
     */
    private boolean isDuplicateLine(Object addLine, Object collectionItem, List<String> duplicateLinePropertyNames) {
        if (duplicateLinePropertyNames.isEmpty()) {
            return false;
        }

        for (String duplicateLinePropertyName : duplicateLinePropertyNames) {
            Object addLinePropertyValue = ObjectPropertyUtils.getPropertyValue(addLine, duplicateLinePropertyName);
            Object duplicateLinePropertyValue = ObjectPropertyUtils.getPropertyValue(collectionItem,
                    duplicateLinePropertyName);

            if (!ObjectUtils.equals(addLinePropertyValue, duplicateLinePropertyValue)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Performs validation on the collection line before it is removed from the corresponding collection.
     *
     * @param model object instance that contain's the view's data
     * @param collectionId the id of the collection being added to
     * @param collectionPath the path to the collection being modified
     * @param deleteLine line that will be removed
     * @return true if the action is allowed and the line should be removed, false if the line should not be removed
     */
    protected boolean performDeleteLineValidation(ViewModel model, String collectionId, String collectionPath,
            Object deleteLine) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyDefaultValuesForCollectionLine(CollectionGroup collectionGroup, Object line) {
        // retrieve all data fields for the collection line
        List<DataField> dataFields = ViewLifecycleUtils.getElementsOfTypeDeep(collectionGroup.getAddLineItems(),
                DataField.class);
        for (DataField dataField : dataFields) {
            String bindingPath = "";
            if (StringUtils.isNotBlank(dataField.getBindingInfo().getBindByNamePrefix())) {
                bindingPath = dataField.getBindingInfo().getBindByNamePrefix() + ".";
            }
            bindingPath += dataField.getBindingInfo().getBindingName();

            populateDefaultValueForField(line, dataField, bindingPath);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyDefaultValues(Component component) {
        if (component == null) {
            return;
        }

        View view = ViewLifecycle.getView();
        Object model = ViewLifecycle.getModel();

        @SuppressWarnings("unchecked") Queue<LifecycleElement> elementQueue = RecycleUtils.getInstance(
                LinkedList.class);
        elementQueue.offer(component);
        try {
            while (!elementQueue.isEmpty()) {
                LifecycleElement currentElement = elementQueue.poll();

                // if component is a data field apply default value
                if (currentElement instanceof DataField) {
                    DataField dataField = ((DataField) currentElement);

                    // need to make sure binding is initialized since this could be on a page we have not initialized yet
                    dataField.getBindingInfo().setDefaults(view, dataField.getPropertyName());

                    populateDefaultValueForField(model, dataField, dataField.getBindingInfo().getBindingPath());
                }

                elementQueue.addAll(ViewLifecycleUtils.getElementsForLifecycle(currentElement).values());
            }
        } finally {
            elementQueue.clear();
            RecycleUtils.recycle(elementQueue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateViewFromRequestParameters(Map<String, String> parameters) {
        View view = ViewLifecycle.getView();

        // build Map of property replacers by property name so that we can remove them
        // if the property was set by a request parameter
        Map<String, Set<PropertyReplacer>> viewPropertyReplacers = new HashMap<String, Set<PropertyReplacer>>();
        List<PropertyReplacer> propertyReplacerSource = view.getPropertyReplacers();
        if (propertyReplacerSource != null) {
            for (PropertyReplacer replacer : propertyReplacerSource) {
                String replacerPropertyName = replacer.getPropertyName();
                Set<PropertyReplacer> propertyReplacers = viewPropertyReplacers.get(replacerPropertyName);

                if (propertyReplacers == null) {
                    viewPropertyReplacers.put(replacerPropertyName,
                            propertyReplacers = new HashSet<PropertyReplacer>());
                }

                propertyReplacers.add(replacer);
            }
        }

        Map<String, Annotation> annotatedFields = CopyUtils.getFieldsWithAnnotation(view.getClass(),
                RequestParameter.class);

        // for each request parameter allowed on the view, if the request contains a value use
        // to set on View, and clear and conditional expressions or property replacers for that field
        Map<String, String> viewRequestParameters = new HashMap<String, String>();
        for (String fieldToPopulate : annotatedFields.keySet()) {
            RequestParameter requestParameter = (RequestParameter) annotatedFields.get(fieldToPopulate);

            // use specified parameter name if given, else use field name to retrieve parameter value
            String requestParameterName = requestParameter.parameterName();
            if (StringUtils.isBlank(requestParameterName)) {
                requestParameterName = fieldToPopulate;
            }

            if (!parameters.containsKey(requestParameterName)) {
                continue;
            }

            String fieldValue = parameters.get(requestParameterName);
            if (StringUtils.isNotBlank(fieldValue)) {
                viewRequestParameters.put(requestParameterName, fieldValue);
                ObjectPropertyUtils.setPropertyValue(view, fieldToPopulate, fieldValue);

                // remove any conditional configuration so value is not
                // overridden later during the apply model phase
                if (view.getPropertyExpressions().containsKey(fieldToPopulate)) {
                    view.getPropertyExpressions().remove(fieldToPopulate);
                }

                if (viewPropertyReplacers.containsKey(fieldToPopulate)) {
                    Set<PropertyReplacer> propertyReplacers = viewPropertyReplacers.get(fieldToPopulate);
                    for (PropertyReplacer replacer : propertyReplacers) {
                        view.getPropertyReplacers().remove(replacer);
                    }
                }
            }
        }

        view.setViewRequestParameters(viewRequestParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildGrowlScript() {
        View view = ViewLifecycle.getView();
        String growlScript = "";

        MessageService messageService = KRADServiceLocatorWeb.getMessageService();

        MessageMap messageMap = GlobalVariables.getMessageMap();
        for (GrowlMessage growl : messageMap.getGrowlMessages()) {
            if (view.isGrowlMessagingEnabled()) {
                String message = messageService.getMessageText(growl.getNamespaceCode(), growl.getComponentCode(),
                        growl.getMessageKey());

                if (StringUtils.isNotBlank(message)) {
                    if (growl.getMessageParameters() != null) {
                        message = message.replace("'", "''");
                        message = MessageFormat.format(message, (Object[]) growl.getMessageParameters());
                    }

                    // escape single quotes in message or title since that will cause problem with plugin
                    message = message.replace("'", "\\'");

                    String title = growl.getTitle();
                    if (StringUtils.isNotBlank(growl.getTitleKey())) {
                        title = messageService.getMessageText(growl.getNamespaceCode(), growl.getComponentCode(),
                                growl.getTitleKey());
                    }
                    title = title.replace("'", "\\'");

                    growlScript =
                            growlScript + "showGrowl('" + message + "', '" + title + "', '" + growl.getTheme() + "');";
                }
            } else {
                ErrorMessage infoMessage = new ErrorMessage(growl.getMessageKey(), growl.getMessageParameters());
                infoMessage.setNamespaceCode(growl.getNamespaceCode());
                infoMessage.setComponentCode(growl.getComponentCode());

                messageMap.putInfoForSectionId(KRADConstants.GLOBAL_INFO, infoMessage);
            }
        }

        return growlScript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateDefaultValueForField(Object object, DataField dataField, String bindingPath) {

        if (!ObjectPropertyUtils.isReadableProperty(object, bindingPath) || !ObjectPropertyUtils.isWritableProperty(
                object, bindingPath)) {
            return;
        }
        Object defaultValue = getDefaultValueForField(object, dataField);

        if (defaultValue != null) {
            ObjectPropertyUtils.setPropertyValue(object, bindingPath, defaultValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDefaultValueForField(Object object, DataField dataField) {
        View view = ViewLifecycle.getView();
        Object defaultValue = null;

        // if dataField.defaultValue is not null and not empty empty string use it
        if (dataField.getDefaultValue() != null && !(dataField.getDefaultValue() instanceof String && StringUtils
                .isBlank((String) dataField.getDefaultValue()))) {
            defaultValue = dataField.getDefaultValue();
        } else if ((dataField.getExpressionGraph() != null) && dataField.getExpressionGraph().containsKey(
                UifConstants.ComponentProperties.DEFAULT_VALUE)) {
            defaultValue = dataField.getExpressionGraph().get(UifConstants.ComponentProperties.DEFAULT_VALUE);
        } else if (dataField.getDefaultValueFinderClass() != null) {
            ValueFinder defaultValueFinder = KRADUtils.createNewObjectFromClass(dataField.getDefaultValueFinderClass());

            defaultValue = defaultValueFinder.getValue();
        } else if ((dataField.getExpressionGraph() != null) && dataField.getExpressionGraph().containsKey(
                UifConstants.ComponentProperties.DEFAULT_VALUES)) {
            defaultValue = dataField.getExpressionGraph().get(UifConstants.ComponentProperties.DEFAULT_VALUES);
        } else if (dataField.getDefaultValues() != null) {
            defaultValue = dataField.getDefaultValues();
        }

        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

        if ((defaultValue != null) && (defaultValue instanceof String) && expressionEvaluator.containsElPlaceholder(
                (String) defaultValue)) {
            Map<String, Object> context = new HashMap<String, Object>(view.getPreModelContext());
            context.putAll(dataField.getContext());

            defaultValue = expressionEvaluator.replaceBindingPrefixes(view, object, (String) defaultValue);
            defaultValue = expressionEvaluator.evaluateExpressionTemplate(context, (String) defaultValue);
        }

        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshReference(Object parentObject, String referenceObjectName) {
        if (!(parentObject instanceof PersistableBusinessObjectBaseAdapter)) {
            LOG.warn("Could not refresh reference " + referenceObjectName + " off class " + parentObject.getClass()
                    .getName() + ". Class not of type PersistableBusinessObject");
            return;
        }

        LegacyDataAdapter legacyDataAdapter = KRADServiceLocatorWeb.getLegacyDataAdapter();
        DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();

        if (legacyDataAdapter.hasReference(parentObject.getClass(), referenceObjectName) || legacyDataAdapter
                .hasCollection(parentObject.getClass(), referenceObjectName)) {
            // refresh via database mapping
            legacyDataAdapter.retrieveReferenceObject(parentObject, referenceObjectName);
        } else if (dataDictionaryService.hasRelationship(parentObject.getClass().getName(), referenceObjectName)) {
            // refresh via data dictionary mapping
            Object referenceObject = KradDataServiceLocator.getDataObjectService().wrap(parentObject).getPropertyValue(
                    referenceObjectName);
            if (!(referenceObject instanceof PersistableBusinessObjectBaseAdapter)) {
                LOG.warn("Could not refresh reference " + referenceObjectName + " off class " + parentObject.getClass()
                        .getName() + ". Class not of type PersistableBusinessObject");
                return;
            }

            referenceObject = legacyDataAdapter.retrieve(referenceObject);
            if (referenceObject == null) {
                LOG.warn("Could not refresh reference " + referenceObjectName + " off class " + parentObject.getClass()
                        .getName() + ".");
                return;
            }

            try {
                KRADUtils.setObjectProperty(parentObject, referenceObjectName, referenceObject);
            } catch (Exception e) {
                LOG.error("Unable to refresh persistable business object: " + referenceObjectName + "\n" + e
                        .getMessage());
                throw new RuntimeException(
                        "Unable to refresh persistable business object: " + referenceObjectName + "\n" + e
                                .getMessage());
            }
        } else {
            LOG.warn("Could not refresh reference " + referenceObjectName + " off class " + parentObject.getClass()
                    .getName() + ".");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshReferences(String referencesToRefresh) {
        Object model = ViewLifecycle.getModel();
        for (String reference : StringUtils.split(referencesToRefresh, KRADConstants.REFERENCES_TO_REFRESH_SEPARATOR)) {
            if (StringUtils.isBlank(reference)) {
                continue;
            }

            //ToDo: handle add line

            if (PropertyAccessorUtils.isNestedOrIndexedProperty(reference)) {
                String parentPath = KRADUtils.getNestedAttributePrefix(reference);
                Object parentObject = ObjectPropertyUtils.getPropertyValue(model, parentPath);
                String referenceObjectName = KRADUtils.getNestedAttributePrimitive(reference);

                if (parentObject == null) {
                    LOG.warn("Unable to refresh references for " + referencesToRefresh +
                            ". Object not found in model. Nothing refreshed.");
                    continue;
                }

                refreshReference(parentObject, referenceObjectName);
            } else {
                refreshReference(model, reference);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void retrieveEditModesAndActionFlags() {
        View view = ViewLifecycle.getView();
        UifFormBase model = (UifFormBase) ViewLifecycle.getModel();
        ViewPresentationController presentationController = view.getPresentationController();
        ViewAuthorizer authorizer = view.getAuthorizer();

        Set<String> actionFlags = presentationController.getActionFlags(view, model);
        Set<String> editModes = presentationController.getEditModes(view, model);

        // if user session is not established cannot invoke authorizer
        if (GlobalVariables.getUserSession() != null) {
            Person user = GlobalVariables.getUserSession().getPerson();

            actionFlags = authorizer.getActionFlags(view, model, user, actionFlags);
            editModes = authorizer.getEditModes(view, model, user, editModes);
        }

        view.setActionFlags(new BooleanMap(actionFlags));
        view.setEditModes(new BooleanMap(editModes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setViewContext() {
        View view = ViewLifecycle.getView();
        view.pushAllToContext(view.getPreModelContext());

        // evaluate view expressions for further context
        for (Entry<String, String> variableExpression : view.getExpressionVariables().entrySet()) {
            String variableName = variableExpression.getKey();
            Object value = ViewLifecycle.getExpressionEvaluator().evaluateExpression(view.getContext(),
                    variableExpression.getValue());
            view.pushObjectToContext(variableName, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setElementContext(LifecycleElement element, LifecycleElement parent) {
        Map<String, Object> context = new HashMap<String, Object>();

        View view = ViewLifecycle.getView();
        Map<String, Object> viewContext = view.getContext();
        if (viewContext != null) {
            context.putAll(viewContext);
        }

        context.put(UifConstants.ContextVariableNames.COMPONENT, element instanceof Component ? element : parent);

        if (parent != null) {
            context.put(UifConstants.ContextVariableNames.PARENT, parent);
        }

        if (element instanceof LayoutManager) {
            context.put(UifConstants.ContextVariableNames.MANAGER, element);
        }

        element.pushAllToContext(context);
    }

    /**
     * Gets the configuration service
     *
     * @return configuration service
     */
    protected ConfigurationService getConfigurationService() {
        if (this.configurationService == null) {
            this.configurationService = CoreApiServiceLocator.getKualiConfigurationService();
        }
        return this.configurationService;
    }

    /**
     * Sets the configuration service
     *
     * @param configurationService The configuration service.
     */
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Gets the data dictionary service
     *
     * @return data dictionary service
     */
    protected DataDictionaryService getDataDictionaryService() {
        if (this.dataDictionaryService == null) {
            this.dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }

        return this.dataDictionaryService;
    }

    /**
     * Sets the data dictionary service
     *
     * @param dataDictionaryService The dictionary service.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the view dictionary service
     *
     * @return view dictionary service
     */
    protected ViewDictionaryService getViewDictionaryService() {
        if (this.viewDictionaryService == null) {
            this.viewDictionaryService = KRADServiceLocatorWeb.getViewDictionaryService();
        }
        return this.viewDictionaryService;
    }

    /**
     * Sets the view dictionary service
     *
     * @param viewDictionaryService The view dictionary service.
     */
    public void setViewDictionaryService(ViewDictionaryService viewDictionaryService) {
        this.viewDictionaryService = viewDictionaryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExpressionEvaluatorFactory getExpressionEvaluatorFactory() {
        if (expressionEvaluatorFactory == null) {
            expressionEvaluatorFactory = KRADServiceLocatorWeb.getExpressionEvaluatorFactory();
        }

        return expressionEvaluatorFactory;
    }

    /**
     * Setter for {@link #getExpressionEvaluatorFactory()}.
     *
     * @param expressionEvaluatorFactory expression evaluator factory
     */
    public void setExpressionEvaluatorFactory(ExpressionEvaluatorFactory expressionEvaluatorFactory) {
        this.expressionEvaluatorFactory = expressionEvaluatorFactory;
    }

    /**
     * Get the legacy data adapter.
     *
     * @return The legacy data adapter.
     */
    protected LegacyDataAdapter getLegacyDataAdapter() {
        if (legacyDataAdapter == null) {
            legacyDataAdapter = KRADServiceLocatorWeb.getLegacyDataAdapter();
        }
        return legacyDataAdapter;
    }

    protected DataObjectService getDataObjectService() {
        if (dataObjectService == null) {
            dataObjectService = KRADServiceLocator.getDataObjectService();
        }
        return dataObjectService;
    }

    protected void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    /**
     * Set the legacy data adapter.
     *
     * @param legacyDataAdapter The legacy data adapter.
     */
    public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
        this.legacyDataAdapter = legacyDataAdapter;
    }

    /**
     * Log an error message using log4j, then throw a runtime exception with the provided message.
     *
     * @param message The error message.
     */
    protected void logAndThrowRuntime(String message) {
        LOG.error(message);
        throw new RuntimeException(message);
    }

}
