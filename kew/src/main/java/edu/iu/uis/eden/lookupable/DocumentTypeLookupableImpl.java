/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.lookupable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.export.Exportable;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.util.ClassLoaderUtils;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;

/**
 * A {@link WorkflowLookupable} implementation for lookup of DocumentTypes.
 *
 * @see DocumentType
 * @see DocumentTypeService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeLookupableImpl implements WorkflowLookupable, Exportable {
    private static final Logger LOG = Logger.getLogger(DocumentTypeLookupableImpl.class);

    private static final String title = "Lookup a Document type";
    private static final String returnLocation = "Lookup.do";

    private static final String PARENT_DOC_TYP_FIELD_LABEL_KEY = "documentTypeLookupable.field.label.parentDocumentType";
    private static final String CLIMB_HIERARCHY_FIELD_LABEL_KEY = "documentTypeLookupable.field.label.climbHierarchy";
    private static final String DOC_TYP_NAME_FIELD_LABEL_KEY = "documentTypeLookupable.field.label.documentTypeName";
    private static final String DOC_TYP_FIELD_LABEL_KEY = "documentTypeLookupable.field.label.documentTypeLabel";
    private static final String ACTIVE_IND_FIELD_LABEL_KEY = "documentTypeLookupable.field.label.activeIndicator";
    private static final String DOCUMENT_TYPE_ID_FIELD_LABEL_KEY = "documentTypeLookupable.field.label.documentTypeId";

    private static final String DOC_TYP_ID_COLUMN_LABEL_KEY = "documentTypeLookupable.column.label.documentTypeId";
    private static final String DOC_TYP_NAME_COLUMN_LABEL_KEY = "documentTypeLookupable.column.label.documentTypeName";
    private static final String DOC_TYP_PARENT_COLUMN_LABEL_KEY = "documentTypeLookupable.column.label.documentTypeParent";
    private static final String DOC_TYP_COLUMN_LABEL_KEY = "documentTypeLookupable.column.label.documentTypeLabel";
    private static final String ACTIVE_IND_COLUMN_LABEL_KEY = "documentTypeLookupable.column.label.activeIndicator";
    private static final String ACTION_COLUMN_LABEL_KEY = "documentTypeLookupable.column.label.action";

    private static final String PARENT_DOC_TYP_FIELD_HELP = "";
    private static final String CLIMB_HIERARCHY_FIELD_HELP = "";
    private static final String DOC_TYP_NAME_FIELD_HELP = "";
    private static final String DOC_TYP_FIELD_HELP = "";
    private static final String ACTIVE_IND_FIELD_HELP = "";
    private static final String DOCUMENT_TYPE_ID_FIELD_HELP = "";

    public static final String CLIMB_HIERARCHY_PROPERTY_NAME = "climbHierarchy";
    public static final String DOC_TYP_PROPERTY_NAME = "docTypeLabel";
    public static final String DOC_TYP_NAME_PROPERTY_NAME = "docTypeName";
    public static final String ACTIVE_IND_PROPERTY_NAME = "activeIndicator";
    public static final String DOCUMENT_TYPE_ID_PROPERTY_NAME = "documentTypeId";

    public static final String DOC_TYP_LOOKUPABLE = "DocumentTypeLookupableImplService";
    public static final String DOC_TYP_FULL_NAME = "docTypeFullName";
    public static final String BACK_LOCATION_KEY_NAME = "backLocation";
    public static final String DOC_FORM_KEY_NAME = "docFormKey";

    private static String PARENT_DOC_TYP_FIELD_LABEL = "Parent Document Type";
    private static String CLIMB_HIERARCHY_FIELD_LABEL = "Traverse Down Hierarchy";
    private static String DOC_TYP_NAME_FIELD_LABEL = "Document Type Name";
    private static String DOC_TYP_FIELD_LABEL = "Document Type Label";
    private static String ACTIVE_IND_FIELD_LABEL = "Active Indicator";
    private static String DOCUMENT_TYPE_ID_FIELD_LABEL = "Document Type Id";

    private static String DOC_TYP_ID_COLUMN_LABEL = "Document Type Id";
    private static String DOC_TYP_NAME_COLUMN_LABEL = "Document Type Name";
    private static String DOC_TYP_PARENT_COLUMN_LABEL = "Document Type Parent";
    private static String DOC_TYP_COLUMN_LABEL = "Document Type Label";
    private static String ACTIVE_IND_COLUMN_LABEL = "Active";
    private static String ACTION_COLUMN_LABEL = "Action";

    // load field/column labels from properties file
    // needs to be done prior to establishing columns
    static {
    	loadProperties();
    }

    private List rows;
    private static List columns = establishColumns();

    /**
     * DocumentTypeLookupableImpl - constructor that sets up the values of what the form on the jsp will look like.
     */
    public DocumentTypeLookupableImpl() {
    	this.rows = establishRows();
    }

    /**
     * This must be done non-statically (see {@link WorkflowLookupable}).
     *
	 * @return
	 */
	private List establishRows() {
        List rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field("", "", Field.HIDDEN, true, DOC_TYP_FULL_NAME, "", null, DOC_TYP_LOOKUPABLE));
        fields.add(new Field(PARENT_DOC_TYP_FIELD_LABEL, PARENT_DOC_TYP_FIELD_HELP, Field.QUICKFINDER, false, DOC_TYP_FULL_NAME, "", null, DOC_TYP_LOOKUPABLE));
        rows.add(new Row(fields));

        List options = new ArrayList();
        options.add(new KeyLabelPair("true", "Yes"));
        options.add(new KeyLabelPair("false", "No"));

		fields = new ArrayList();
		fields.add(new Field(CLIMB_HIERARCHY_FIELD_LABEL, CLIMB_HIERARCHY_FIELD_HELP, Field.RADIO, false, CLIMB_HIERARCHY_PROPERTY_NAME, "false", options, null));
		rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field(DOC_TYP_NAME_FIELD_LABEL, DOC_TYP_NAME_FIELD_HELP, Field.TEXT, false, DOC_TYP_NAME_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field(DOC_TYP_FIELD_LABEL, DOC_TYP_FIELD_HELP, Field.TEXT, false, DOC_TYP_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field(DOCUMENT_TYPE_ID_FIELD_LABEL, DOCUMENT_TYPE_ID_FIELD_HELP, Field.TEXT, false, DOCUMENT_TYPE_ID_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        options = new ArrayList();
        options.add(new KeyLabelPair("Y", "Active"));
        options.add(new KeyLabelPair("N", "Inactive"));
        options.add(new KeyLabelPair("ALL", "Show All"));

        fields = new ArrayList();
        fields.add(new Field(ACTIVE_IND_FIELD_LABEL, ACTIVE_IND_FIELD_HELP, Field.RADIO, false, ACTIVE_IND_PROPERTY_NAME, "ALL", options, null));
        rows.add(new Row(fields));

        return rows;
	}

	/**
     * Load the properties from struts ApplicationResources.properties.
     */
    private static void loadProperties() {
        Properties properties = new Properties();

        InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream("edu/iu/uis/eden/ApplicationResources.properties");
        try {
            if (in == null) {
            	LOG.warn("edu.iu.uis.eden.ApplicationResources.properties not found in CLASSPATH, using defaults.");
            } else {
                properties.load(in);
                if (properties.getProperty(PARENT_DOC_TYP_FIELD_LABEL_KEY) != null) {
                    PARENT_DOC_TYP_FIELD_LABEL = properties.getProperty(PARENT_DOC_TYP_FIELD_LABEL_KEY);
                }
                if (properties.getProperty(CLIMB_HIERARCHY_FIELD_LABEL_KEY) != null) {
                    CLIMB_HIERARCHY_FIELD_LABEL = properties.getProperty(CLIMB_HIERARCHY_FIELD_LABEL_KEY);
                }
                if (properties.getProperty(DOC_TYP_NAME_FIELD_LABEL_KEY) != null) {
                    DOC_TYP_NAME_FIELD_LABEL = properties.getProperty(DOC_TYP_NAME_FIELD_LABEL_KEY);
                }
                if (properties.getProperty(DOC_TYP_FIELD_LABEL_KEY) != null) {
                    DOC_TYP_FIELD_LABEL = properties.getProperty(DOC_TYP_FIELD_LABEL_KEY);
                }
                if (properties.getProperty(ACTIVE_IND_FIELD_LABEL_KEY) != null) {
                    ACTIVE_IND_FIELD_LABEL = properties.getProperty(ACTIVE_IND_FIELD_LABEL_KEY);
                }
                if (properties.getProperty(DOCUMENT_TYPE_ID_FIELD_LABEL_KEY) != null) {
                    DOCUMENT_TYPE_ID_FIELD_LABEL = properties.getProperty(DOCUMENT_TYPE_ID_FIELD_LABEL_KEY);
                }

                if (properties.getProperty(DOC_TYP_ID_COLUMN_LABEL_KEY) != null) {
                    DOC_TYP_ID_COLUMN_LABEL = properties.getProperty(DOC_TYP_ID_COLUMN_LABEL_KEY);
                }
                if (properties.getProperty(DOC_TYP_NAME_COLUMN_LABEL_KEY) != null) {
                    DOC_TYP_NAME_COLUMN_LABEL = properties.getProperty(DOC_TYP_NAME_COLUMN_LABEL_KEY);
                }
                if (properties.getProperty(DOC_TYP_PARENT_COLUMN_LABEL_KEY) != null) {
                    DOC_TYP_PARENT_COLUMN_LABEL = properties.getProperty(DOC_TYP_PARENT_COLUMN_LABEL_KEY);
                }
                if (properties.getProperty(DOC_TYP_COLUMN_LABEL_KEY) != null) {
                    DOC_TYP_COLUMN_LABEL = properties.getProperty(DOC_TYP_COLUMN_LABEL_KEY);
                }
                if (properties.getProperty(ACTIVE_IND_COLUMN_LABEL_KEY) != null) {
                    ACTIVE_IND_COLUMN_LABEL = properties.getProperty(ACTIVE_IND_COLUMN_LABEL_KEY);
                }
                if (properties.getProperty(ACTION_COLUMN_LABEL_KEY) != null) {
                    ACTION_COLUMN_LABEL = properties.getProperty(ACTION_COLUMN_LABEL_KEY);
                }

            }
        } catch (IOException e) {
            LOG.error("Error loading edu.iu.uis.eden.ApplicationResources.properties, using defaults.", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Ignore IOException at this point
                }
            }
        }

    }

    private static List establishColumns() {
        List columns = new ArrayList();

        Column column = new Column(DOC_TYP_ID_COLUMN_LABEL, "true", "documentTypeId");
        column.setType(Column.INTEGER);
        columns.add(column);
        columns.add(new Column(DOC_TYP_NAME_COLUMN_LABEL, Column.COLUMN_IS_SORTABLE_VALUE, "name"));
        columns.add(new Column(DOC_TYP_PARENT_COLUMN_LABEL, Column.COLUMN_IS_SORTABLE_VALUE, "lookupParentName"));
        columns.add(new Column(DOC_TYP_COLUMN_LABEL, Column.COLUMN_IS_SORTABLE_VALUE, "label"));
        columns.add(new Column(ACTIVE_IND_COLUMN_LABEL, Column.COLUMN_IS_SORTABLE_VALUE, "docTypeActiveIndicatorDisplayValue"));
        columns.add(new Column(ACTION_COLUMN_LABEL, Column.COLUMN_NOT_SORTABLE_VALUE, "actionsUrl"));
        return columns;
    }

    public void changeIdToName(Map fieldValues) {

    }
    /**
     * getSearchResults - searches for document type information based on the criteria passed in by the map.
     *
     * @return Returns a list of DocumentType objects that match the result.
     */
    public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception {
        DocumentType documentType = new DocumentType();
        LOG.debug("fieldValues: " + fieldValues);
        LOG.debug("fieldConversions: " + fieldConversions);
        String activeIndicator = (String) fieldValues.get(ACTIVE_IND_PROPERTY_NAME);
        String docTypeLabel = (String) fieldValues.get(DOC_TYP_PROPERTY_NAME);
        String parentDocTypeName = (String) fieldValues.get(DOC_TYP_FULL_NAME);
        Boolean climbHierarchy = Boolean.valueOf((String)fieldValues.get(CLIMB_HIERARCHY_PROPERTY_NAME));
        String documentTypeId = (String) fieldValues.get(DOCUMENT_TYPE_ID_PROPERTY_NAME);
        String docTypeName = (String) fieldValues.get(DOC_TYP_NAME_PROPERTY_NAME);
        String backLocation = (String) fieldValues.get(BACK_LOCATION_KEY_NAME);
        String docFormKey = (String) fieldValues.get(DOC_FORM_KEY_NAME);

        String docTypReturn = (String) fieldConversions.get(DOC_TYP_FULL_NAME);

        if (activeIndicator == null) {
            activeIndicator = "ALL";
        } else if (activeIndicator.equals("Y")) {
            documentType.setActiveInd(new Boolean(true));
        } else if (activeIndicator.equals("N")) {
            documentType.setActiveInd(new Boolean(false));
        }
        if (docTypeLabel != null && !"".equals(docTypeLabel.trim())) {
            docTypeLabel = docTypeLabel.replace('*', '%');
            documentType.setLabel("%"+docTypeLabel.trim()+"%");
        }
        if (docTypeName != null && !"".equals(docTypeName.trim())) {

            documentType.setName(docTypeName.trim());
        }

        if (documentTypeId != null && !"".equals(documentTypeId.trim())) {
            try {
                documentType.setDocumentTypeId(new Long(documentTypeId.trim()));
            } catch (NumberFormatException e) {
                documentType.setDocumentTypeId(new Long(-1));
            }
        }

        LOG.debug("finding...: " + documentType + " " + parentDocTypeName);
        Collection docTypesFound = KEWServiceLocator.getDocumentTypeService().find(documentType, parentDocTypeName, climbHierarchy.booleanValue());
        Iterator docTypes = docTypesFound.iterator();
        List displayList = new ArrayList();
        while (docTypes.hasNext()) {
            DocumentType type = (DocumentType) docTypes.next();
            StringBuffer returnUrl = new StringBuffer("<a href=\"");
            returnUrl.append(backLocation).append("?methodToCall=refresh&docFormKey=").append(docFormKey).append("&");
            if (!Utilities.isEmpty(docTypReturn)) {
                returnUrl.append(docTypReturn);
            } else {
                returnUrl.append(DOC_TYP_FULL_NAME);
            }
            returnUrl.append("=").append(type.getName()).append("\">return value</a>");
            type.setReturnUrl(returnUrl.toString());

            StringBuffer actions = new StringBuffer("<a href=\"");
            actions.append("DocumentType.do?methodToCall=report");
            actions.append("&docTypeId=").append(type.getDocumentTypeId()).append("\" >report</a>");
            //actions.append("&nbsp;|&nbsp;<a href=\"").append("DocumentType.do?methodToCall=edit").append("&documentType.documentTypeId=");
            //actions.append(type.getDocumentTypeId()).append("\" >edit</a>");
            type.setActionsUrl(actions.toString());
            displayList.add(type);
        }
        return displayList;
    }
    public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
        return false;
    }


    public List getDefaultReturnType(){
        List returnTypes = new ArrayList();
        returnTypes.add(DOC_TYP_FULL_NAME);
        return returnTypes;
    }

    public String getNoReturnParams(Map fieldConversions) {
        String docTypReturn = (String) fieldConversions.get(DOC_TYP_FULL_NAME);

        StringBuffer noReturnParams = new StringBuffer("&");
        if(!Utilities.isEmpty(docTypReturn)){
            noReturnParams.append(docTypReturn);
        } else {
            noReturnParams.append(DOC_TYP_FULL_NAME);
        }
        noReturnParams.append("=");
        return noReturnParams.toString();
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return Returns the instructions.
     */
    public String getLookupInstructions() {
        return Utilities.getApplicationConstant(EdenConstants.DOCUMENT_TYPE_SEARCH_INSTRUCTION_KEY);
    }

    /**
     * @return Returns the returnLocation.
     */
    public String getReturnLocation() {
        return returnLocation;
    }

    /**
     * @return Returns the columns.
     */
    public List getColumns() {
        return columns;
    }

    public String getHtmlMenuBar() {
        return "";//"<a href=\"DocumentType.do\" >Create new document type</a>";
    }

    public List getRows() {
        return rows;
    }

    public ExportDataSet export(ExportFormat format, Object exportCriteria) throws Exception {
        List searchResults = (List)exportCriteria;
        ExportDataSet dataSet = new ExportDataSet(format);
        dataSet.getDocumentTypes().addAll(searchResults);
        return dataSet;
    }

    public List getSupportedExportFormats() {
        return EdenConstants.STANDARD_FORMATS;
    }

}