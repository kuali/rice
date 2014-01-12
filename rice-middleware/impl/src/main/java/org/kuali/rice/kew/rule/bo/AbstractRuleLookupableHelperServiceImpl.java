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
package org.kuali.rice.kew.rule.bo;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.CollectionFormatter;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.rule.RuleTemplate;
import org.kuali.rice.kew.api.rule.RuleTemplateAttribute;
import org.kuali.rice.kew.lookupable.MyColumns;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegationBo;
import org.kuali.rice.kew.rule.WorkflowRuleAttributeRows;
import org.kuali.rice.kew.rule.service.RuleDelegationService;
import org.kuali.rice.kew.rule.service.RuleServiceInternal;
import org.kuali.rice.kew.rule.service.RuleTemplateService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AbstractRuleLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    protected static final String GROUP_REVIEWER_PROPERTY_NAME = "groupReviewer";
    protected static final String GROUP_REVIEWER_NAME_PROPERTY_NAME = "groupReviewerName";
    protected static final String GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME = "groupReviewerNamespace";
    protected static final String PERSON_REVIEWER_PROPERTY_NAME = "personReviewer";
    protected static final String PERSON_REVIEWER_TYPE_PROPERTY_NAME = "personReviewerType";

    protected static final String BACK_LOCATION = "backLocation";
    protected static final String DOC_FORM_KEY = "docFormKey";
    protected static final String INVALID_WORKGROUP_ERROR = "The Group Reviewer Namespace and Name combination is not valid";
    protected static final String INVALID_PERSON_ERROR = "The Person Reviewer is not valid";

    @Override
    public List<Row> getRows() {
        List<Row> returnRows = new ArrayList<Row>();
        returnRows.addAll(rows);
        return returnRows;
    }

    @Override
    public void validateSearchParameters(Map<String, String> fieldValues) {
        super.validateSearchParameters(fieldValues);

        // make sure that if we have either groupName or Namespace, that both are filled in
        String groupName = (String)fieldValues.get(GROUP_REVIEWER_NAME_PROPERTY_NAME);
        String groupNamespace = (String)fieldValues.get(GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME);
        String principalName = (String)fieldValues.get(PERSON_REVIEWER_PROPERTY_NAME);

        if (StringUtils.isEmpty(groupName) && !StringUtils.isEmpty(groupNamespace)) {
            String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), GROUP_REVIEWER_NAME_PROPERTY_NAME);
            GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAME_PROPERTY_NAME, RiceKeyConstants.ERROR_REQUIRED, attributeLabel);
        }

        if  (!StringUtils.isEmpty(groupName) && StringUtils.isEmpty(groupNamespace)) {
            String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME);
            GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME, RiceKeyConstants.ERROR_REQUIRED, attributeLabel);
        }

        if  (!StringUtils.isEmpty(groupName) && !StringUtils.isEmpty(groupNamespace)) {
            Group group = KimApiServiceLocator.getGroupService().getGroupByNamespaceCodeAndName(groupNamespace,
                    groupName);
            if (group == null) {
                GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAME_PROPERTY_NAME, RiceKeyConstants.ERROR_CUSTOM, INVALID_WORKGROUP_ERROR);
            }
        }

        if  (!StringUtils.isEmpty(principalName)) {
            Person person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName);
            if (person == null) {
                GlobalVariables.getMessageMap().putError(PERSON_REVIEWER_PROPERTY_NAME, RiceKeyConstants.ERROR_CUSTOM, INVALID_PERSON_ERROR);
            }
        }
        if (!GlobalVariables.getMessageMap().hasNoErrors()) {
            throw new ValidationException("errors in search criteria");
        }
    }


    @Override
    public List<Column> getColumns() {
        List<Column> columns = new ArrayList<Column>();

        for (Row row : rows) {
            for (Field field : row.getFields()) {
                Column newColumn = new Column();
                newColumn.setColumnTitle(field.getFieldLabel());
                newColumn.setMaxLength(field.getMaxLength());
                newColumn.setPropertyName(field.getPropertyName());
                columns.add(newColumn);
            }
        }
        return columns;
    }

    protected GroupService getGroupService() {
        return KimApiServiceLocator.getGroupService();
    }

    protected RuleTemplateService getRuleTemplateService() {
        return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
    }

}
