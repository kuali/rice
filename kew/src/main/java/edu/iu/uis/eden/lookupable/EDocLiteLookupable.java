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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.edl.EDocLiteAssociation;
import edu.iu.uis.eden.edl.EDocLiteService;
import edu.iu.uis.eden.edl.components.WorkflowDocumentActions;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.export.Exportable;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;

/**
 * A {@link WorkflowLookupable} for EDoc Lites.
 *
 * @see EDocLiteService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EDocLiteLookupable implements WorkflowLookupable, Exportable {

	private List ROWS;
	private static final List COLUMNS = establishColData();

	private static final String EDOC_LITE_NAME = "eDocLiteName_";
	private static final String EDOC_LITE_STYLE_NAME = "eDocLiteStyleName_";
	private static final String EDOC_LITE_DOC_TYPE_NAME = "eDocLiteDocName_";
	private static final String ACTIVE_IND_FIELD_LABEL = "Active Indicator";
	private static final String ACTIVE_IND_PROPERTY_NAME = "activeIndicator";

	public EDocLiteLookupable() {
		this.ROWS = establishRowData();
	}

	public String getHtmlMenuBar() {
		return "";
	}

	public List getRows() {
		return ROWS;
	}

	public String getTitle() {
		return "EDocLite Lookup";
	}

	public String getReturnLocation() {
		return "Lookup.do";
	}

	public List getColumns() {
		return COLUMNS;
	}

	public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception {
		EDocLiteAssociation edocLite = new EDocLiteAssociation();

		String activeInd = (String)fieldValues.get(ACTIVE_IND_PROPERTY_NAME);
		if (!Utilities.isEmpty(activeInd) && !activeInd.equals("all")) {
			edocLite.setActiveInd(new Boolean(activeInd.trim()));
		}
		String definition = (String)fieldValues.get(EDOC_LITE_NAME);
		if (!Utilities.isEmpty(definition)) {
			edocLite.setDefinition(definition.trim());
		}
		String style = (String)fieldValues.get(EDOC_LITE_STYLE_NAME);
		if (!Utilities.isEmpty(style)) {
			edocLite.setStyle(style.trim());
		}
		String documentType = (String)fieldValues.get(EDOC_LITE_DOC_TYPE_NAME);
		if (!Utilities.isEmpty(documentType)) {
			edocLite.setEdlName(documentType.trim());
		}
		List results = KEWServiceLocator.getEDocLiteService().search(edocLite);
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			EDocLiteAssociation result = (EDocLiteAssociation) iter.next();
			String actionsUrl = "<a href=\"EDocLite?userAction=" + WorkflowDocumentActions.ACTION_CREATE + "&edlName=" + result.getEdlName() + "\">Create Document</a>";
			result.setActionsUrl(actionsUrl);
		}
		return results;
	}

	public String getNoReturnParams(Map fieldConversions) {
		return null;
	}

	public String getLookupInstructions() {
		return "EDocLite Lookup";
	}

	public List getDefaultReturnType() {
        List returnTypes = new ArrayList();
        returnTypes.add(EDOC_LITE_NAME);
        return returnTypes;
	}

	public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
		return false;
	}

	public void changeIdToName(Map fieldValues) throws Exception {
	}

	public ExportDataSet export(ExportFormat format, Object exportCriteria) throws Exception {
		List searchResults = (List)exportCriteria;
        ExportDataSet dataSet = new ExportDataSet(format);
        dataSet.getEdocLites().addAll(searchResults);
        return dataSet;
	}

	public List getSupportedExportFormats() {
		return EdenConstants.STANDARD_FORMATS;
	}

	private static List establishColData() {
		List cols = new ArrayList();
		cols.add(new Column("Definition Name", Column.COLUMN_IS_SORTABLE_VALUE, "definition"));
		cols.add(new Column("Style Name", Column.COLUMN_IS_SORTABLE_VALUE, "style"));
		cols.add(new Column("DocumentType Name", Column.COLUMN_IS_SORTABLE_VALUE, "edlName"));
		cols.add(new Column("Active Indicator", Column.COLUMN_IS_SORTABLE_VALUE, "activeInd"));
		cols.add(new Column("Action", Column.COLUMN_NOT_SORTABLE_VALUE, "actionsUrl"));
		return cols;
	}

	private List establishRowData() {
		List rows = new ArrayList();
		List fields = new ArrayList();
		fields.add(new Field("Definition Name", "", Field.TEXT, false, EDOC_LITE_NAME, "", null, "", null));
		rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field("Style Name", "", Field.TEXT, false, EDOC_LITE_STYLE_NAME, "", null, "", null));
		rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field("Document Type Name", "", Field.TEXT, false, EDOC_LITE_DOC_TYPE_NAME, "", null, "", null));
		rows.add(new Row(fields));

        List options = new ArrayList();
        options.add(new KeyLabelPair("true", "Active"));
        options.add(new KeyLabelPair("false", "Inactive"));
        options.add(new KeyLabelPair("all", "Show All"));

		fields = new ArrayList();
		fields.add(new Field(ACTIVE_IND_FIELD_LABEL, "", Field.RADIO, false, ACTIVE_IND_PROPERTY_NAME, "true", options, null));
		rows.add(new Row(fields));
		return rows;
	}

}
