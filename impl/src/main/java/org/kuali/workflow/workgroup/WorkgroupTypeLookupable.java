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
package org.kuali.workflow.workgroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.export.Exportable;
import edu.iu.uis.eden.lookupable.Column;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;

/**
 * A {@link WorkflowLookupable} implementation for {@link WorkgroupType}.
 *
 * @see WorkgroupType
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeLookupable implements WorkflowLookupable, Exportable {


    private List rows;
    private static List columns = establishColumns();
    private static final String title = "Workgroup Type Lookup";
    private static final String returnLocation = "Lookup.do";

    private static final String NAME_FIELD_LABEL = "Name";
    private static final String ID_FIELD_LABEL = "Workgroup Type Id";
    private static final String LABEL_FIELD_LABEL = "Label";
    private static final String DESCRIPTION_FIELD_LABEL = "Description";
    private static final String ACTIVE_IND_FIELD_LABEL = "Active";

    private static final String NAME_FIELD_HELP = "";
    private static final String ID_FIELD_HELP = "";
    private static final String LABEL_FIELD_HELP = "";
    private static final String DESCRIPTION_FIELD_HELP = "";
    private static final String ACTIVE_IND_FIELD_HELP = "";

    private static final String NAME_PROPERTY_NAME = "name";
    private static final String ID_PROPERTY_NAME = "workgroupTypeId";
    private static final String LABEL_PROPERTY_NAME = "label";
    private static final String DESCRIPTION_PROPERTY_NAME = "description";
    private static final String ACTIVE_IND_PROPERTY_NAME = "active";

    private static final String BACK_LOCATION = "backLocation";
    private static final String DOC_FORM_KEY = "docFormKey";

    public WorkgroupTypeLookupable() {
        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field(NAME_FIELD_LABEL, NAME_FIELD_HELP, Field.TEXT, false, NAME_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field(ID_FIELD_LABEL, ID_FIELD_HELP, Field.TEXT, false, ID_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field(LABEL_FIELD_LABEL, LABEL_FIELD_HELP, Field.TEXT, false, LABEL_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field(DESCRIPTION_FIELD_LABEL, DESCRIPTION_FIELD_HELP, Field.TEXT, false, DESCRIPTION_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        List options = new ArrayList();
		options.add(new KeyLabelPair("true", "Active"));
		options.add(new KeyLabelPair("false", "Inactive"));
		options.add(new KeyLabelPair("ALL", "Both"));

		fields = new ArrayList();
		fields.add(new Field(ACTIVE_IND_FIELD_LABEL, ACTIVE_IND_FIELD_HELP, Field.RADIO, false, ACTIVE_IND_PROPERTY_NAME, "true", options, null));
		rows.add(new Row(fields));
    }

    private static List establishColumns() {
        List columnList = new ArrayList();
        columnList.add(new Column("Id", Column.COLUMN_IS_SORTABLE_VALUE, "workgroupType.workgroupTypeId"));
        columnList.add(new Column("Name", Column.COLUMN_IS_SORTABLE_VALUE, "workgroupType.name"));
        columnList.add(new Column("Label", Column.COLUMN_IS_SORTABLE_VALUE, "workgroupType.label"));
        columnList.add(new Column("Description", Column.COLUMN_IS_SORTABLE_VALUE, "workgroupType.description"));
        columnList.add(new Column("Active", Column.COLUMN_IS_SORTABLE_VALUE, "workgroupType.active"));
        columnList.add(new Column("Actions", Column.COLUMN_NOT_SORTABLE_VALUE, "actionsUrl"));
        return columnList;
    }

    public void changeIdToName(Map fieldValues) {

    }

    /**
     * getSearchResults - searches for a fiscal organization information based on the criteria passed in by the map.
     *
     * @return Returns a list of FiscalOrganization objects that match the result.
     */
    public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception {
    	String idValue = (String) fieldValues.get(ID_PROPERTY_NAME);
    	String name = wildcard((String) fieldValues.get(NAME_PROPERTY_NAME));
    	String label = wildcard((String) fieldValues.get(LABEL_PROPERTY_NAME));
        String description = wildcard((String) fieldValues.get(DESCRIPTION_PROPERTY_NAME));
        String activeValue = (String) fieldValues.get(ACTIVE_IND_PROPERTY_NAME);
        String backLocation = (String) fieldValues.get(BACK_LOCATION);
        String docFormKey = (String) fieldValues.get(DOC_FORM_KEY);

        List errors = new ArrayList();
        Long id = null;
        if (!StringUtils.isBlank(idValue)) {
        	try {
        		id = Long.valueOf(idValue);
        	} catch (NumberFormatException e) {
        		errors.add(new WorkflowServiceErrorImpl("Workgroup Type Id Invalid", "workgrouptype.id.invalidNumber"));
        	}
        }
        Boolean active = null;
        if (!StringUtils.isBlank(activeValue) && !"ALL".equals(activeValue)) {
        	active = new Boolean(activeValue);
        }

        String workgroupTypeNameReturn = (String) fieldConversions.get(NAME_PROPERTY_NAME);

        WorkgroupTypeService workgroupTypeService = KEWServiceLocator.getWorkgroupTypeService();
        List<WorkgroupType> results = workgroupTypeService.search(id, name, label, description, active);
        List displayList = new ArrayList();
        for (WorkgroupType workgroupType : results) {
        	WorkgroupTypeLookupResult result = new WorkgroupTypeLookupResult(workgroupType);
        	StringBuffer returnUrl = new StringBuffer("<a href=\"");
            returnUrl.append(backLocation).append("?methodToCall=refresh&docFormKey=").append(docFormKey).append("&");
            if (!Utilities.isEmpty(workgroupTypeNameReturn)) {
                returnUrl.append(workgroupTypeNameReturn);
            } else {
                returnUrl.append(NAME_PROPERTY_NAME);
            }
            returnUrl.append("=").append(result.getWorkgroupType().getWorkgroupTypeId()).append("\">return value</a>");
            result.setReturnUrl(returnUrl.toString());
            displayList.add(result);
        }
        return displayList;
    }

    private String wildcard(String string) {
    	if (string == null) {
    		string = "";
    	}
    	return string.trim().replace("*", "%");
    }

    public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
        return false;
    }

    public List getDefaultReturnType(){
        List returnTypes = new ArrayList();
        returnTypes.add(NAME_PROPERTY_NAME);
        return returnTypes;
    }

    public String getNoReturnParams(Map fieldConversions) {
        String nameReturn = (String) fieldConversions.get(NAME_PROPERTY_NAME);
        StringBuffer noReturnParams = new StringBuffer("&");
        if (!Utilities.isEmpty(nameReturn)) {
            noReturnParams.append(nameReturn);
        } else {
            noReturnParams.append(NAME_PROPERTY_NAME);
        }
        noReturnParams.append("=&").append(NAME_PROPERTY_NAME).append("=");
        return noReturnParams.toString();
    }

    public String getLookupInstructions() {
        return "Enter criteria to search for a Workgroup Type";
    }

    public String getTitle() {
        return title;
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public List getColumns() {
        return columns;
    }

    public String getHtmlMenuBar() {
        return "";
    }

    public List getRows() {
        return rows;
    }

    public ExportDataSet export(ExportFormat format, Object exportCriteria) throws Exception {
        List searchResults = (List)exportCriteria;
        ExportDataSet dataSet = new ExportDataSet(format);
        for (Iterator iterator = searchResults.iterator(); iterator.hasNext();) {
			WorkgroupTypeLookupResult result = (WorkgroupTypeLookupResult) iterator.next();
			dataSet.getWorkgroupTypes().add(result.getWorkgroupType());
		}
        return dataSet;
    }

    public List getSupportedExportFormats() {
        return EdenConstants.STANDARD_FORMATS;
    }

    public static class WorkgroupTypeLookupResult {

    	private WorkgroupType workgroupType;
    	private String returnUrl;

    	public WorkgroupTypeLookupResult(WorkgroupType workgroupType) {
    		this.workgroupType = workgroupType;
    	}

    	public WorkgroupType getWorkgroupType() {
    		return workgroupType;
    	}

    	public String getActionsUrl() {
    		return "<a href=\"WorkgroupType.do?methodToCall=report&workgroupTypeId=" + getWorkgroupType().getWorkgroupTypeId() + "\" >report</a>";
    	}

		public String getReturnUrl() {
			return returnUrl;
		}

		public void setReturnUrl(String returnUrl) {
			this.returnUrl = returnUrl;
		}

    }

}
