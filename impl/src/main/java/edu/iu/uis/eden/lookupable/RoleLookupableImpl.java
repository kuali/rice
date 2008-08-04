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

import org.apache.log4j.Logger;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.plugin.attributes.RoleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;
import edu.iu.uis.eden.routetemplate.RuleTemplateService;
import edu.iu.uis.eden.util.Utilities;

/**
 * A {@link WorkflowLookupable} implementation which is used to lookup roles.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleLookupableImpl implements WorkflowLookupable {

	private static final Logger LOG = Logger.getLogger(RoleLookupableImpl.class);

    private List rows;

    private static List columns = establishColumns();

    private static final String TITLE = "Role Lookup";
    private static final String RETURNLOCATION = "Lookup.do";
    private static final String RULE_TEMPLATE_FIELD_LABEL = "Rule Template";
    private static final String RULE_TEMPLATE_FIELD_HELP = "";
    private static final String RULE_TEMPLATE_PROPERTY_NAME = "ruleTemplateName";
    private static final String RULE_TEMPLATE_ID_PROPERTY_NAME = "ruleTemplate.ruleTemplateId";

    private static final String RULE_TEMPLATE_LOOKUPABLE = "RuleTemplateLookupableImplService";
    private static final String BACK_LOCATION_KEY_NAME = "backLocation";
    private static final String DOC_FORM_KEY_NAME = "docFormKey";
    private static final String ROLE_NAME = "roleName";

    public RoleLookupableImpl() {
        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field(RULE_TEMPLATE_FIELD_LABEL, RULE_TEMPLATE_FIELD_HELP, Field.TEXT, true, RULE_TEMPLATE_PROPERTY_NAME, "", null, RULE_TEMPLATE_LOOKUPABLE));
        fields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, RULE_TEMPLATE_LOOKUPABLE));
        fields.add(new Field("", "", Field.LOOKUP_RESULT_ONLY, true, RULE_TEMPLATE_ID_PROPERTY_NAME, "", null, RULE_TEMPLATE_LOOKUPABLE));
        rows.add(new Row(fields));
    }

    private static List establishColumns() {
        List columnList = new ArrayList();
        columnList.add(new Column("Role Label", Column.COLUMN_IS_SORTABLE_VALUE, "label"));
        columnList.add(new Column("Role Name", Column.COLUMN_IS_SORTABLE_VALUE, "name"));
        return columnList;
    }

    public List getFieldConversions() {
        return new ArrayList();
    }

    public void changeIdToName(Map fieldValues) {
        String ruleTemplateIdParam = (String) fieldValues.get(RULE_TEMPLATE_ID_PROPERTY_NAME);
        String ruleTemplateNameParam = (String) fieldValues.get(RULE_TEMPLATE_PROPERTY_NAME);

        if (ruleTemplateNameParam != null && !ruleTemplateNameParam.trim().equals("") || ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam) && !"null".equals(ruleTemplateIdParam)) {
            RuleTemplate ruleTemplate = null;
            if (ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam)) {
                ruleTemplate = getRuleTemplateService().findByRuleTemplateId(new Long(ruleTemplateIdParam));
            } else {
                ruleTemplate = getRuleTemplateService().findByRuleTemplateName(ruleTemplateNameParam.trim());
            }
            if (ruleTemplate != null) {
                setFieldValue(RULE_TEMPLATE_PROPERTY_NAME, ruleTemplate.getName());
            }
        } else {
            setFieldValue(RULE_TEMPLATE_PROPERTY_NAME, "");
        }

    }

    public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception {
        String ruleTemplateIdParam = (String) fieldValues.get(RULE_TEMPLATE_ID_PROPERTY_NAME);
        String ruleTemplateNameParam = (String) fieldValues.get(RULE_TEMPLATE_PROPERTY_NAME);
        String backLocation = (String) fieldValues.get(BACK_LOCATION_KEY_NAME);
        String docFormKey = (String) fieldValues.get(DOC_FORM_KEY_NAME);

        String roleReturn = (String) fieldConversions.get(ROLE_NAME);

        Long ruleTemplateId = null;

        List displayList = new ArrayList();

        if (ruleTemplateNameParam != null && !ruleTemplateNameParam.trim().equals("") || ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam)) {
            RuleTemplate ruleTemplate = null;
            if (ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam)) {
                ruleTemplateId = new Long(ruleTemplateIdParam);
                ruleTemplate = getRuleTemplateService().findByRuleTemplateId(ruleTemplateId);
            } else {
                ruleTemplate = getRuleTemplateService().findByRuleTemplateName(ruleTemplateNameParam.trim());
            }
            if (ruleTemplate != null) {
                for (Iterator iter = ruleTemplate.getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {
                    RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
                    try {
                        WorkflowAttribute attribute = (WorkflowAttribute)GlobalResourceLoader.getResourceLoader().getObject(new ObjectDefinition(ruleTemplateAttribute.getRuleAttribute().getClassName()));
                        if (attribute instanceof RoleAttribute) {
                            RoleAttribute roleAttribute = (RoleAttribute) attribute;

                            for (Iterator iterator = roleAttribute.getRoleNames().iterator(); iterator.hasNext();) {
                                Role role = (Role) iterator.next();
                                StringBuffer returnUrl = new StringBuffer("<a href=\"");
                                returnUrl.append(backLocation).append("?methodToCall=refresh&docFormKey=").append(docFormKey).append("&");
                                if (!Utilities.isEmpty(roleReturn)) {
                                    returnUrl.append(roleReturn);
                                } else {
                                    returnUrl.append(ROLE_NAME);
                                }
                                returnUrl.append("=").append(role.getName()).append(" \">return value</a>");
                                role.setReturnUrl(returnUrl.toString());
                                displayList.add(role);
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("Exception caught looking up Role Attribute", e);
                    }
                }
            }
        }

        return displayList;
    }

    public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
        return false;
    }

    public List getDefaultReturnType() {
        List returnTypes = new ArrayList();
        returnTypes.add(ROLE_NAME);
        return returnTypes;
    }

    public List getColumns() {
        return columns;
    }

    public String getHtmlMenuBar() {
        return "";
    }

    public String getLookupInstructions() {
        return "Use fields below to search for roles.";
    }

    public String getNoReturnParams(Map fieldConversions) {
        String roleReturn = (String) fieldConversions.get(ROLE_NAME);
        StringBuffer noReturnParams = new StringBuffer("&");
        if (!Utilities.isEmpty(roleReturn)) {
            noReturnParams.append(roleReturn);
        } else {
            noReturnParams.append(ROLE_NAME);
        }
        noReturnParams.append("=");

        return noReturnParams.toString();
    }

    public String getReturnLocation() {
        return RETURNLOCATION;
    }

    public List getRows() {
        return rows;
    }

    public String getTitle() {
        return TITLE;
    }

    private void setFieldValue(String name, String value) {
        for (Iterator iter = getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                    if (name.equals(field.getPropertyName())) {
                        field.setPropertyValue(value);
                    }
                }
            }
        }
    }

    private RuleTemplateService getRuleTemplateService() {
        return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
    }

}