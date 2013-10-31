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
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewDictionaryService;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.BooleanMap;
import org.kuali.rice.krad.uif.util.CloneUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizer;
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

/**
 * Default Implementation of {@code ViewHelperService}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@SuppressWarnings("deprecation")
public class ViewHelperServiceImpl implements ViewHelperService, Serializable {
    
    private static final long serialVersionUID = 1772618197133239852L;
    
    private static final Logger LOG = Logger.getLogger(ViewHelperServiceImpl.class);
    
    private transient ConfigurationService configurationService;
    private transient DataDictionaryService dataDictionaryService;
    private transient LegacyDataAdapter legacyDataAdapter;

    private transient ViewDictionaryService viewDictionaryService;

    /**
     * Helper function to determine whether if column should be displayed. Used to help extract
     * columns used in screen format such as action or select that is not needed for export.
     * 
     * @param layoutManager The layout manager.
     * @param collectionGroup The collection group.
     * @return Index numbers for all columns that should be ignored.
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
     * @return The generated table data.
     */
    @Override
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
     * Hook for service overrides to perform custom apply model logic on the component
     * 
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
        applyDefaultValuesForCollectionLine(collectionGroup, newLine);
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
    @SuppressWarnings("unchecked")
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
            applyDefaultValuesForCollectionLine(collectionGroup, lineDataObject);

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
     * @param columnData Formatted column data.
     * @param tableFormatOptions Format options: startRow and endRow are added to the row,
     *        startColumn and endColumn are added to each column.
     * @param ignoredColumns Index numbers of columns to ignore.
     * 
     * @return Formatted table data for one row.
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
     * @param formatType The format type: csv, xls, or xml.
     * @return The format options for to use with the indicated format type.
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
     * Populate default values the model backing a line in a collection group.
     * 
     * @param collectionGroup The collection group.
     * @param line The model object backing the line.
     */
    @Override
    public void applyDefaultValuesForCollectionLine(CollectionGroup collectionGroup, Object line) {
        // retrieve all data fields for the collection line
        List<DataField> dataFields = ComponentUtils.getComponentsOfTypeDeep(collectionGroup.getAddLineItems(),
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
     * Iterates through the view components picking up data fields and applying an default value
     * configured
     * 
     * @param component component that should be checked for default values
     */
    @Override
    public void applyDefaultValues(Component component) {
        if (component == null) {
            return;
        }

        View view = ViewLifecycle.getView();
        Object model = ViewLifecycle.getModel();
        
        // if component is a data field apply default value
        if (component instanceof DataField) {
            DataField dataField = ((DataField) component);

            // need to make sure binding is initialized since this could be on a page we have not initialized yet
            dataField.getBindingInfo().setDefaults(view, dataField.getPropertyName());

            populateDefaultValueForField(model, dataField, dataField.getBindingInfo().getBindingPath());
        }

        for (Component nested : component.getComponentsForLifecycle()) {
            applyDefaultValues(nested);
        }

        // if view, need to add all pages since only one will be on the lifecycle
        if (component instanceof View) {
            for (Component nested : ((View) component).getItems()) {
                applyDefaultValues(nested);
            }
        }

    }

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

        Map<String, Annotation> annotatedFields = CloneUtils.getFieldsWithAnnotation(view.getClass(),
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
     * Applies the default value configured for the given field (if any) to the line given object
     * property that is determined by the given binding path
     * 
     * @param object object that should be populated
     * @param dataField field to check for configured default value
     * @param bindingPath path to the property on the object that should be populated
     */
    @Override
    public void populateDefaultValueForField(Object object, DataField dataField, String bindingPath) {
        if (!ObjectPropertyUtils.isReadableProperty(object, bindingPath)
                || !ObjectPropertyUtils.isWritableProperty(object, bindingPath)) {
            return;
        }

        Object currentValue = ObjectPropertyUtils.getPropertyValue(object, bindingPath);

        // Default value only applies when the value being set is null (has no value on the form)
        if (currentValue != null) {
            return;
        }

        Object defaultValue = getDefaultValueForField(object, dataField);

        ObjectPropertyUtils.setPropertyValue(object, bindingPath, defaultValue);
    }

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
    @Override
    public Object getDefaultValueForField(Object object, DataField dataField) {
        View view = ViewLifecycle.getView();
        Object defaultValue = null;

        if (StringUtils.isNotBlank(dataField.getDefaultValue())) {
            defaultValue = dataField.getDefaultValue();
        } else if ((dataField.getExpressionGraph() != null) && dataField.getExpressionGraph().containsKey(
                UifConstants.ComponentProperties.DEFAULT_VALUE)) {
            defaultValue = dataField.getExpressionGraph().get(UifConstants.ComponentProperties.DEFAULT_VALUE);
        } else if (dataField.getDefaultValueFinderClass() != null) {
            ValueFinder defaultValueFinder = DataObjectUtils.newInstance(dataField.getDefaultValueFinderClass());

            defaultValue = defaultValueFinder.getValue();
        } else if ((dataField.getExpressionGraph() != null) && dataField.getExpressionGraph().containsKey(
                UifConstants.ComponentProperties.DEFAULT_VALUES)) {
            defaultValue = dataField.getExpressionGraph().get(UifConstants.ComponentProperties.DEFAULT_VALUES);
        } else if (dataField.getDefaultValues() != null) {
            defaultValue = dataField.getDefaultValues();
        }

        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

        if ((defaultValue != null) && (defaultValue instanceof String) && expressionEvaluator
                .containsElPlaceholder((String) defaultValue)) {
            Map<String, Object> context = new HashMap<String, Object>(view.getPreModelContext());
            context.putAll(dataField.getContext());

            defaultValue = expressionEvaluator.replaceBindingPrefixes(view, object, (String) defaultValue);
            defaultValue = expressionEvaluator.evaluateExpressionTemplate(context, (String) defaultValue);
        }

        return defaultValue;
    }

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
    @Override
    public void refreshReference(Object parentObject, String referenceObjectName) {
        if (!(parentObject instanceof PersistableBusinessObject)) {
            LOG.warn("Could not refresh reference " + referenceObjectName + " off class " + parentObject.getClass()
                    .getName() + ". Class not of type PersistableBusinessObject");
            return;
        }

        LegacyDataAdapter legacyDataAdapter = KRADServiceLocatorWeb.getLegacyDataAdapter();
        DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();

        if (legacyDataAdapter.hasReference(parentObject.getClass(), referenceObjectName)
                || legacyDataAdapter.hasCollection(parentObject.getClass(), referenceObjectName)) {
            // refresh via database mapping
            legacyDataAdapter.retrieveReferenceObject(parentObject, referenceObjectName);
        } else if (dataDictionaryService.hasRelationship(parentObject.getClass().getName(), referenceObjectName)) {
            // refresh via data dictionary mapping
            Object referenceObject = DataObjectUtils.getPropertyValue(parentObject, referenceObjectName);
            if (!(referenceObject instanceof PersistableBusinessObject)) {
                LOG.warn("Could not refresh reference " + referenceObjectName + " off class " + parentObject.getClass()
                        .getName() + ". Class not of type PersistableBusinessObject");
                return;
            }

            referenceObject = legacyDataAdapter.retrieve((PersistableBusinessObject) referenceObject);
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
     * Update the reference objects listed in referencesToRefresh of the model
     * 
     * <p>
     * The the individual references in the referencesToRefresh string are separated by
     * KRADConstants.REFERENCES_TO_REFRESH_SEPARATOR).
     * </p>
     * 
     * @param referencesToRefresh list of references to refresh (
     */
    @Override
    public void refreshReferences(String referencesToRefresh) {
        Object model = ViewLifecycle.getModel();
        for (String reference : StringUtils.split(referencesToRefresh, KRADConstants.REFERENCES_TO_REFRESH_SEPARATOR)) {
            if (StringUtils.isBlank(reference)) {
                continue;
            }

            //ToDo: handle add line

            if (DataObjectUtils.isNestedAttribute(reference)) {
                String parentPath = DataObjectUtils.getNestedAttributePrefix(reference);
                Object parentObject = ObjectPropertyUtils.getPropertyValue(model, parentPath);
                String referenceObjectName = DataObjectUtils.getNestedAttributePrimitive(reference);

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
     * Invokes the configured <code>PresentationController</code> and </code>Authorizer</code> for
     * the view to get the exported action flags and edit modes that can be used in conditional
     * logic
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
     * Sets up the view context which will be available to other components through their context
     * for conditional logic evaluation.
     */
    @Override
    public void setViewContext() {
        View view = ViewLifecycle.getView();
        view.pushAllToContext(view.getPreModelContext());

        // evaluate view expressions for further context
        for (Entry<String, String> variableExpression : view.getExpressionVariables().entrySet()) {
            String variableName = variableExpression.getKey();
            Object value = ViewLifecycle.getExpressionEvaluator().evaluateExpression(
                    view.getContext(), variableExpression.getValue());
            view.pushObjectToContext(variableName, value);
        }
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
     * Get the legacy data adapter.
     * 
     * @return The legacy data adapter.
     */
    protected LegacyDataAdapter getLegacyDataAdapter() {
        if (legacyDataAdapter == null) {
            return KRADServiceLocatorWeb.getLegacyDataAdapter();
        }
        return legacyDataAdapter;
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
