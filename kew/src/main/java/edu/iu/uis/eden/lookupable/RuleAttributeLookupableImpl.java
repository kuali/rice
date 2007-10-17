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
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.export.Exportable;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleAttributeService;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;

/**
 * A {@link WorkflowLookupable} implementation for {@link RuleAttribute}.
 *
 * @see RuleAttribute
 * @see RuleAttributeService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleAttributeLookupableImpl implements WorkflowLookupable, Exportable {


    private List rows;

    private static List columns = establishColumns();

    private static final String title = "Rule Attribute Lookup";

    private static final String returnLocation = "Lookup.do";

    private static final String NAME_FIELD_LABEL = "Name";

    private static final String CLASSNAME_FIELD_LABEL = "Class Name";

    private static final String TYPE_FIELD_LABEL = "Attribute Type";

    private static final String NAME_FIELD_HELP = "";

    private static final String CLASSNAME_FIELD_HELP = "";

    private static final String TYPE_FIELD_HELP = "";

    private static final String NAME_PROPERTY_NAME = "name";

    private static final String CLASSNAME_PROPERTY_NAME = "className";

    private static final String TYPE_PROPERTY_NAME = "type";

    private static final String RULE_ATTRIBUTE_ID_PROPERTY_NAME = "ruleAttribute.ruleAttributeId";

    private static final String BACK_LOCATION = "backLocation";

    private static final String DOC_FORM_KEY = "docFormKey";

    private static final String LOOKUP_INSTRUCTIONS = "Enter criteria to search for a rule attribute.";

    public RuleAttributeLookupableImpl() {

        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field(NAME_FIELD_LABEL, NAME_FIELD_HELP, Field.TEXT, false, NAME_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field(CLASSNAME_FIELD_LABEL, CLASSNAME_FIELD_HELP, Field.TEXT, false, CLASSNAME_PROPERTY_NAME, "", null, null));
        rows.add(new Row(fields));

        List options = new ArrayList();
        options.add(new KeyLabelPair("", "All"));
        options.add(new KeyLabelPair(EdenConstants.RULE_ATTRIBUTE_TYPE, "Rule Attribute"));
        options.add(new KeyLabelPair(EdenConstants.SEARCHABLE_ATTRIBUTE_TYPE, "Searchable Attribute"));
        options.add(new KeyLabelPair(EdenConstants.RULE_XML_ATTRIBUTE_TYPE, "Rule Xml Attribute"));
        options.add(new KeyLabelPair(EdenConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE, "Searchable Xml Attribute"));
        options.add(new KeyLabelPair(EdenConstants.EXTENSION_ATTRIBUTE_TYPE, "Extension Attribute"));
        options.add(new KeyLabelPair(EdenConstants.EMAIL_ATTRIBUTE_TYPE, "Email Attribute"));
        options.add(new KeyLabelPair(EdenConstants.NOTE_ATTRIBUTE_TYPE, "Note Attribute"));
        options.add(new KeyLabelPair(EdenConstants.ACTION_LIST_ATTRIBUTE_TYPE, "Action List Attribute"));
        options.add(new KeyLabelPair(EdenConstants.RULE_VALIDATION_ATTRIBUTE_TYPE, "Rule Validation Attribute"));
        options.add(new KeyLabelPair(EdenConstants.SEARCH_GENERATOR_ATTRIBUTE_TYPE, "Document Search Generator Attribute"));
        options.add(new KeyLabelPair(EdenConstants.SEARCH_RESULT_PROCESSOR_ATTRIBUTE_TYPE, "Document Search Result Processor Attribute"));
        options.add(new KeyLabelPair(EdenConstants.SEARCH_RESULT_XML_PROCESSOR_ATTRIBUTE_TYPE, "Document Search Result Processor XML Attribute"));

        fields = new ArrayList();
        fields.add(new Field(TYPE_FIELD_LABEL, TYPE_FIELD_HELP, Field.DROPDOWN, false, TYPE_PROPERTY_NAME, "", options, null));
        rows.add(new Row(fields));

        //TODO row for type?

    }

    private static List establishColumns() {
        List columnList = new ArrayList();
        columnList.add(new Column("Rule Attribute Id", Column.COLUMN_IS_SORTABLE_VALUE, "ruleAttributeId"));
        columnList.add(new Column("Rule Attribute Name", Column.COLUMN_IS_SORTABLE_VALUE, "name"));
        columnList.add(new Column("Attribute Type", Column.COLUMN_IS_SORTABLE_VALUE, "type"));
        columnList.add(new Column("Rule Attribute Class", Column.COLUMN_IS_SORTABLE_VALUE, "className"));
        columnList.add(new Column("Rule Attribute Actions", Column.COLUMN_NOT_SORTABLE_VALUE, "ruleAttributeActionsUrl"));
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
        String name = (String) fieldValues.get(NAME_PROPERTY_NAME);
        String className = (String) fieldValues.get(CLASSNAME_PROPERTY_NAME);
        String type = (String) fieldValues.get(TYPE_PROPERTY_NAME);
        String backLocation = (String) fieldValues.get(BACK_LOCATION);
        String docFormKey = (String) fieldValues.get(DOC_FORM_KEY);

        String ruleIdReturn = (String) fieldConversions.get(RULE_ATTRIBUTE_ID_PROPERTY_NAME);

        RuleAttributeService ruleAttributeService = (RuleAttributeService) KEWServiceLocator.getService(KEWServiceLocator.RULE_ATTRIBUTE_SERVICE);
        RuleAttribute ruleAttribute = new RuleAttribute();

        if (name != null && !"".equals(name.trim())) {
            name = name.replace('*', '%');
            ruleAttribute.setName("%"+name.trim()+"%");
        }

        if (className != null && !"".equals(className.trim())) {
            className = className.replace('*', '%');
            ruleAttribute.setClassName("%"+className.trim()+"%");
        }

        if (type != null && !"".equals(type.trim())) {
        	type = type.replace('*', '%');
            ruleAttribute.setType(type.trim());
            //Logger.getLogger(RuleAttributeLookupableImpl.class).info("Setting type: " + ruleAttribute.getType());
        }

        Iterator ruleAttributes = ruleAttributeService.findByRuleAttribute(ruleAttribute).iterator();
        List displayList = new ArrayList();
        while (ruleAttributes.hasNext()) {
            RuleAttribute record = (RuleAttribute) ruleAttributes.next();

            StringBuffer returnUrl = new StringBuffer("<a href=\"");
            returnUrl.append(backLocation).append("?methodToCall=refresh&docFormKey=").append(docFormKey).append("&");
            if (!Utilities.isEmpty(ruleIdReturn)) {
                returnUrl.append(ruleIdReturn);
            } else {
                returnUrl.append(RULE_ATTRIBUTE_ID_PROPERTY_NAME);
            }
            returnUrl.append("=").append(record.getRuleAttributeId()).append("\">return value</a>");
            record.setReturnUrl(returnUrl.toString());
            displayList.add(record);
        }
        return displayList;
    }

    public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
        return false;
    }


    public List getDefaultReturnType(){
        List returnTypes = new ArrayList();
        returnTypes.add(RULE_ATTRIBUTE_ID_PROPERTY_NAME);
        return returnTypes;
    }

    public String getNoReturnParams(Map fieldConversions) {
        String ruleIdReturn = (String) fieldConversions.get(RULE_ATTRIBUTE_ID_PROPERTY_NAME);
        StringBuffer noReturnParams = new StringBuffer("&");
        if (!Utilities.isEmpty(ruleIdReturn)) {
            noReturnParams.append(ruleIdReturn);
        } else {
            noReturnParams.append(RULE_ATTRIBUTE_ID_PROPERTY_NAME);
        }
        noReturnParams.append("=");
        return noReturnParams.toString();
    }

    /**
     * @return Returns the instructions.
     */
    public String getLookupInstructions() {
        return LOOKUP_INSTRUCTIONS;
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
        dataSet.getRuleAttributes().addAll(searchResults);
        return dataSet;
    }

    public List getSupportedExportFormats() {
        return EdenConstants.STANDARD_FORMATS;
    }


}