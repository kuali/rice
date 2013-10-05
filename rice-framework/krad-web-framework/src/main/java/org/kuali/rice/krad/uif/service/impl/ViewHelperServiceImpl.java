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
package org.kuali.rice.krad.uif.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewDictionaryService;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.DefaultExpressionEvaluator;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;

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
    private transient ExpressionEvaluator expressionEvaluator;
    private transient LegacyDataAdapter legacyDataAdapter;

    private transient ViewDictionaryService viewDictionaryService;

    /**
     * Helper function to determine whether if column should be displayed. Used to help extract
     * columns used in screen format such as action or select that is not needed for export.
     * 
     * @param layoutManager
     * @param collectionGroup
     * @return
     */
    private List<Integer> findIgnoredColumns(TableLayoutManager layoutManager, CollectionGroup collectionGroup) {
        List<Integer> ignoreColumns = new ArrayList<Integer>();
        int actionColumnIndex = layoutManager.getActionColumnIndex();
        int numberOfColumns = layoutManager.getNumberOfColumns();
        boolean renderActions = collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly();
        boolean renderSelectField = collectionGroup.isIncludeLineSelectionField();
        boolean renderSequenceField = layoutManager.isRenderSequenceField();

        if (renderActions || renderSelectField || renderSequenceField) {
            int shiftColumn = 0;

            if (renderSelectField) {
                ignoreColumns.add(shiftColumn);
                shiftColumn++;
            }
            if (renderSequenceField) {
                ignoreColumns.add(shiftColumn);
                shiftColumn++;
            }
            if (renderActions) {
                if (actionColumnIndex == 1) {
                    ignoreColumns.add(shiftColumn);
                } else if (actionColumnIndex == -1) {
                    ignoreColumns.add(numberOfColumns - 1);
                } else if (actionColumnIndex > 1) {
                    ignoreColumns.add(actionColumnIndex);
                }
            }
        }
        return ignoreColumns;
    }

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
    @Override
    public void addCustomContainerComponents(Object model, Container container) {

    }

    /**
     * Generates formatted table data based on the posted view results and format type
     * 
     * @param view view instance where the table is located
     * @param model top level object containing the data
     * @param tableId id of the table being generated
     * @param formatType format which the table should be generated in
     * @return
     */
    public String buildExportTableData(View view, Object model, String tableId, String formatType) {
        // load table format elements used for generated particular style
        Map<String, String> exportTableFormatOptions = getExportTableFormatOptions(formatType);
        String startTable = exportTableFormatOptions.get("startTable");
        String endTable = exportTableFormatOptions.get("endTable");

        Component component = view.getViewIndex().getComponentById(tableId);
        StringBuilder tableRows = new StringBuilder("");

        // table layout manager is needed for header and gathering field data
        if (component instanceof CollectionGroup && ((CollectionGroup) component)
                .getLayoutManager() instanceof TableLayoutManager) {

            CollectionGroup collectionGroup = (CollectionGroup) component;
            TableLayoutManager layoutManager = (TableLayoutManager) collectionGroup.getLayoutManager();
            List<Label> headerLabels = layoutManager.getHeaderLabels();
            List<Field> rowFields = layoutManager.getAllRowFields();
            int numberOfColumns = layoutManager.getNumberOfColumns();
            List<Integer> ignoredColumns = findIgnoredColumns(layoutManager, collectionGroup);

            // append table header data as first row
            if (headerLabels.size() > 0) {
                List<String> labels = new ArrayList<String>();
                for (Label label : headerLabels) {
                    labels.add(label.getLabelText());
                }

                tableRows.append(buildExportTableRow(labels, exportTableFormatOptions, ignoredColumns));
            }

            // load all subsequent rows to the table
            if (rowFields.size() > 0) {
                List<String> columnData = new ArrayList<String>();
                for (Field field : rowFields) {
                    columnData.add(KRADUtils.getSimpleFieldValue(model, field));
                    if (columnData.size() >= numberOfColumns) {
                        tableRows.append(buildExportTableRow(columnData, exportTableFormatOptions, ignoredColumns));
                        columnData.clear();
                    }
                }
            }
        }

        return startTable + tableRows.toString() + endTable;
    }

    /**
     * Finds the <code>Inquirable</code> configured for the given data object class and delegates to
     * it for building the inquiry URL
     * 
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#buildInquiryLink(java.lang.Object,
     *      java.lang.String, org.kuali.rice.krad.uif.widget.Inquiry)
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
     * Gets the expression evaluator service
     * 
     * @return expression evaluator service
     */
    public ExpressionEvaluator getExpressionEvaluator() {
        if (this.expressionEvaluator == null) {
            this.expressionEvaluator = new DefaultExpressionEvaluator();
        }

        return this.expressionEvaluator;
    }

    /**
     * Hook for service overrides to perform custom apply model logic on the component
     * 
     * @param view view instance containing the component
     * @param component component instance to apply model to
     * @param model Top level object containing the data (could be the model or a top level business
     *        object, dto)
     */
    @Override
    public void performCustomApplyModel(Component component, Object model) {

    }

    /**
     * Hook for service overrides to perform custom component finalization
     * 
     * @param view view instance containing the component
     * @param component component instance to update
     * @param model Top level object containing the data
     * @param parent Parent component for the component being finalized
     */
    @Override
    public void performCustomFinalize(Component component, Object model, Component parent) {

    }

    /**
     * Hook for service overrides to perform custom initialization on the component
     * 
     * @param view view instance containing the component
     * @param component component instance to initialize
     */
    @Override
    public void performCustomInitialization(Component component) {

    }

    /**
     * Hook for service overrides to perform custom component finalization.
     * 
     * @param model Top level object containing the data
     */
    @Override
    public void performCustomViewFinalize(Object model) {
        setExpressionEvaluator(null);
    }

    /**
     * Hook for service overrides to perform custom initialization prior to view initialization.
     * 
     * @param model The model.
     */
    @Override
    public void performCustomViewInitialization(Object model) {
        
    }

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
    @Override
    public void processAfterAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine,
            boolean isValidLine) {

    }

    /**
     * Hook for service overrides to process the collection line after it has been deleted
     * 
     * @param view view instance that is being presented (the action was taken on)
     * @param collectionGroup collection group component for the collection the line that was added
     * @param model object instance that contains the views data
     * @param lineIndex index of the line that was deleted
     */
    @Override
    public void processAfterDeleteLine(View view, CollectionGroup collectionGroup, Object model, int lineIndex) {

    }

    /**
     * Hook for service overrides to process the save collection line after it has been validated
     * 
     * @param view view instance that is being presented (the action was taken on)
     * @param collectionGroup collection group component for the collection
     * @param model object instance that contains the views data
     * @param addLine the new line that was added
     */
    @Override
    public void processAfterSaveLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {

    }

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
    @Override
    public void processBeforeAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {

    }

    /**
     * Hook for service overrides to process the save collection line before it is validated
     * 
     * @param view view instance that is being presented (the action was taken on)
     * @param collectionGroup collection group component for the collection
     * @param model object instance that contain's the views data
     * @param addLine the new line instance to be processed
     */
    @Override
    public void processBeforeSaveLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {

    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#processCollectionAddBlankLine(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void processCollectionAddBlankLine(View view, Object model, String collectionPath) {
        // get the collection group from the view
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
        }

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        Object newLine = DataObjectUtils.newInstance(collectionGroup.getCollectionObjectClass());
        ViewLifecycle.getActiveLifecycle()
                .applyDefaultValuesForCollectionLine(view, model, collectionGroup, newLine);
        addLine(collection, newLine, collectionGroup.getAddLinePlacement().equals("TOP"));

        ((UifFormBase) model).getAddedCollectionItems().add(newLine);
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#processCollectionAddLine(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void processCollectionAddLine(View view, Object model, String collectionPath) {
        // get the collection group from the view
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
        }

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // now get the new line we need to add
        String addLinePath = collectionGroup.getAddLineBindingInfo().getBindingPath();
        Object addLine = ObjectPropertyUtils.getPropertyValue(model, addLinePath);
        if (addLine == null) {
            logAndThrowRuntime("Add line instance not found for path: " + addLinePath);
        }

        processBeforeAddLine(view, collectionGroup, model, addLine);

        // validate the line to make sure it is ok to add
        boolean isValidLine = performAddLineValidation(view, collectionGroup, model, addLine);
        if (isValidLine) {
            // TODO: should check to see if there is an add line method on the
            // collection parent and if so call that instead of just adding to
            // the collection (so that sequence can be set)
            addLine(collection, addLine, collectionGroup.getAddLinePlacement().equals(
                    UifConstants.Position.TOP.name()));

            // make a new instance for the add line
            collectionGroup.initializeNewCollectionLine(view, model, collectionGroup, true);
        }

        ((UifFormBase) model).getAddedCollectionItems().add(addLine);

        processAfterAddLine(view, collectionGroup, model, addLine, isValidLine);
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#processCollectionDeleteLine(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, java.lang.String, int)
     */
    public void processCollectionDeleteLine(View view, Object model, String collectionPath, int lineIndex) {
        // get the collection group from the view
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
        }

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // TODO: look into other ways of identifying a line so we can deal with
        // unordered collections
        if (collection instanceof List) {
            Object deleteLine = ((List<Object>) collection).get(lineIndex);

            // validate the delete action is allowed for this line
            boolean isValid = performDeleteLineValidation(view, collectionGroup, deleteLine);
            if (isValid) {
                ((List<Object>) collection).remove(lineIndex);
                processAfterDeleteLine(view, collectionGroup, model, lineIndex);
            }
        } else {
            logAndThrowRuntime("Only List collection implementations are supported for the delete by index method");
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#processCollectionSaveLine(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, java.lang.String, int)
     */
    @Override
    public void processCollectionSaveLine(View view, Object model, String collectionPath, int selectedLineIndex) {
        // get the collection group from the view
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
        }

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // TODO: look into other ways of identifying a line so we can deal with
        // unordered collections
        if (collection instanceof List) {
            Object saveLine = ((List<Object>) collection).get(selectedLineIndex);

            processBeforeSaveLine(view, collectionGroup, model, saveLine);

            ((UifFormBase) model).getAddedCollectionItems().remove(saveLine);

            processAfterSaveLine(view, collectionGroup, model, saveLine);

        } else {
            logAndThrowRuntime("Only List collection implementations are supported for the delete by index method");
        }

    }

    /**
     * @see org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl#processMultipleValueLookupResults
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    public void processMultipleValueLookupResults(View view, Object model, String collectionPath,
            String lookupResultValues) {
        // if no line values returned, no population is needed
        if (StringUtils.isBlank(lookupResultValues)) {
            return;
        }

        // retrieve the collection group so we can get the collection class and collection lookup
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            throw new RuntimeException("Unable to find collection group for path: " + collectionPath);
        }

        Class<?> collectionObjectClass = collectionGroup.getCollectionObjectClass();
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model,
                collectionGroup.getBindingInfo().getBindingPath());
        if (collection == null) {
            Class<?> collectionClass = ObjectPropertyUtils.getPropertyType(model,
                    collectionGroup.getBindingInfo().getBindingPath());
            collection = (Collection<Object>) DataObjectUtils.newInstance(collectionClass);
            ObjectPropertyUtils.setPropertyValue(model, collectionGroup.getBindingInfo().getBindingPath(), collection);
        }

        Map<String, String> fieldConversions = collectionGroup.getCollectionLookup().getFieldConversions();
        List<String> toFieldNamesColl = new ArrayList<String>(fieldConversions.values());
        Collections.sort(toFieldNamesColl);
        String[] toFieldNames = new String[toFieldNamesColl.size()];
        toFieldNamesColl.toArray(toFieldNames);

        // first split to get the line value sets
        String[] lineValues = StringUtils.split(lookupResultValues, ",");

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
                lineDataObject = DataObjectUtils.newInstance(collectionObjectClass);
            }

            // apply default values to new line
            ViewLifecycle.getActiveLifecycle()
                    .applyDefaultValuesForCollectionLine(view, model, collectionGroup, lineDataObject);

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

            // TODO: duplicate identifier check

            collection.add(lineDataObject);
        }
    }

    /**
     * Add addLine to collection while giving derived classes an opportunity to override for things
     * like sorting.
     * 
     * @param collection the Collection to add the given addLine to
     * @param addLine the line to add to the given collection
     * @param insertFirst indicates if the item should be inserted as the first item
     */
    protected void addLine(Collection<Object> collection, Object addLine, boolean insertFirst) {
        if (insertFirst && (collection instanceof List)) {
            ((List<Object>) collection).add(0, addLine);
        } else {
            collection.add(addLine);
        }
    }

    /**
     * Helper method used to build formatted table row data for export
     * 
     * @return
     */
    protected String buildExportTableRow(List<String> columnData, Map<String, String> tableFormatOptions,
            List<Integer> ignoredColumns) {
        String startRow = tableFormatOptions.get("startRow");
        String endRow = tableFormatOptions.get("endRow");
        String startColumn = tableFormatOptions.get("startColumn");
        String endColumn = tableFormatOptions.get("endColumn");
        boolean appendLastColumn = Boolean.valueOf(tableFormatOptions.get("appendLastColumn"));
        int columnIndex = 0;

        StringBuilder builder = new StringBuilder();
        for (String columnItem : columnData) {
            boolean displayColumn = !ignoredColumns.contains(columnIndex);
            if (displayColumn) {
                builder.append(startColumn + columnItem + endColumn);
            }
            if (columnIndex >= columnData.size() - 1 && !appendLastColumn) {
                builder.delete(builder.length() - endColumn.length(), builder.length());
            }
            columnIndex++;
        }

        return startRow + builder.toString() + endRow;
    }

    /**
     * Identify table formatting elements based on formatType. Defaults to txt format if not found
     * 
     * @param formatType
     * @return
     */
    protected Map<String, String> getExportTableFormatOptions(String formatType) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("contentType", "text/plain");
        map.put("formatType", "txt");
        map.put("startTable", "");
        map.put("endTable", "");
        map.put("startRow", "");
        map.put("endRow", "\n");
        map.put("startColumn", "");
        map.put("endColumn", ", ");
        map.put("appendLastColumn", "false");

        if ("csv".equals(formatType)) {
            map.put("contentType", "text/csv");
            map.put("formatType", "csv");
            map.put("startTable", "");
            map.put("endTable", "");
            map.put("startRow", "");
            map.put("endRow", "\n");
            map.put("startColumn", "");
            map.put("endColumn", ", ");
            map.put("appendLastColumn", "false");

        } else if ("xls".equals(formatType)) {
            map.put("contentType", "application/vnd.ms-excel");
            map.put("formatType", "xls");
            map.put("startTable", "");
            map.put("endTable", "");
            map.put("startRow", "");
            map.put("endRow", "\n");
            map.put("startColumn", "\"");
            map.put("endColumn", "\"\t");
            map.put("appendLastColumn", "true");

        } else if ("xml".equals(formatType)) {
            map.put("contentType", "application/xml");
            map.put("formatType", "xml");
            map.put("startTable", "<table>\n");
            map.put("endTable", "</table>\n");
            map.put("startRow", "  <row>\n");
            map.put("endRow", "  </row>\n");
            map.put("startColumn", "    <column>");
            map.put("endColumn", "</column>\n");
            map.put("appendLastColumn", "true");

        }

        return map;
    }

    /**
     * Performs validation on the new collection line before it is added to the corresponding
     * collection
     * 
     * @param view view instance that the action was taken on
     * @param collectionGroup collection group component for the collection
     * @param addLine new line instance to validate
     * @param model object instance that contain's the views data
     * @return true if the line is valid and it should be added to the collection, false if it was
     *         not valid and should not be added to the collection
     */
    protected boolean performAddLineValidation(View view, CollectionGroup collectionGroup, Object model,
            Object addLine) {
        boolean isValid = true;

        // TODO: this should invoke rules, subclasses like the document view
        // should create the document add line event

        return isValid;
    }

    /**
     * Performs validation on the collection line before it is removed from the corresponding
     * collection
     * 
     * @param view view instance that the action was taken on
     * @param collectionGroup collection group component for the collection
     * @param deleteLine line that will be removed
     * @return true if the action is allowed and the line should be removed, false if the line
     *         should not be removed
     */
    protected boolean performDeleteLineValidation(View view, CollectionGroup collectionGroup, Object deleteLine) {
        boolean isValid = true;

        // TODO: this should invoke rules, sublclasses like the document view
        // should create the document delete line event

        return isValid;
    }


    /**
     * Sets the expression evaluator service
     * 
     * @param expressionEvaluator
     */
    public void setExpressionEvaluator(ExpressionEvaluator expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
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
     * @param configurationService
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
     * @param dataDictionaryService
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
     * @param viewDictionaryService
     */
    public void setViewDictionaryService(ViewDictionaryService viewDictionaryService) {
        this.viewDictionaryService = viewDictionaryService;
    }

    protected LegacyDataAdapter getLegacyDataAdapter() {
        if (legacyDataAdapter == null) {
            return KRADServiceLocatorWeb.getLegacyDataAdapter();
        }
        return legacyDataAdapter;
    }

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
