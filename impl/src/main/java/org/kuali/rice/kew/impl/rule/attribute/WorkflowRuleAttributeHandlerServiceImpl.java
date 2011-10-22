package org.kuali.rice.kew.impl.rule.attribute;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.api.extension.ExtensionRepositoryService;
import org.kuali.rice.kew.api.extension.ExtensionUtils;
import org.kuali.rice.kew.api.rule.RoleName;
import org.kuali.rice.kew.api.validation.ValidationResults;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.framework.rule.attribute.WorkflowRuleAttributeHandlerService;
import org.kuali.rice.kew.rule.RoleAttribute;
import org.kuali.rice.kew.rule.WorkflowRuleAttribute;
import org.kuali.rice.kew.rule.WorkflowRuleSearchAttribute;
import org.kuali.rice.kew.rule.XmlConfiguredAttribute;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Row;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkflowRuleAttributeHandlerServiceImpl implements WorkflowRuleAttributeHandlerService {
    private static final Logger LOG = Logger.getLogger(WorkflowRuleAttributeHandlerServiceImpl.class);

    private ExtensionRepositoryService extensionRepositoryService;

    @Override
    public List<RemotableAttributeField> getSearchRows(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new RiceIllegalArgumentException("attributeName was null or blank");
        }
        Object searchAttribute = loadAttribute(attributeName);
        List<Row> rows = null;

        if (WorkflowRuleSearchAttribute.class.isAssignableFrom(searchAttribute.getClass())) {
            rows = ((WorkflowRuleSearchAttribute)searchAttribute).getSearchRows();
        } else {
            rows = ((WorkflowRuleAttribute)searchAttribute).getRuleRows();
        }

        return FieldUtils.convertRowsToAttributeFields(rows);
    }

    @Override
    public boolean isWorkflowRuleAttribute(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new RiceIllegalArgumentException("attributeName was null or blank");
        }
        Object workflowRuleAttribute = loadAttribute(attributeName);
        return workflowRuleAttribute instanceof WorkflowRuleAttribute;
    }

    @Override
    public List<RemotableAttributeField> getRuleRows(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new RiceIllegalArgumentException("attributeName was null or blank");
        }
        WorkflowRuleAttribute attribute = loadAttribute(attributeName);
        List<Row> rows = attribute.getRuleRows();

        return FieldUtils.convertRowsToAttributeFields(rows);
    }

    @Override
    public List<RemotableAttributeField> getRoutingDataRows(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new RiceIllegalArgumentException("attributeName was null or blank");
        }
        WorkflowRuleAttribute attribute = loadAttribute(attributeName);
        List<Row> rows = attribute.getRoutingDataRows();

        return FieldUtils.convertRowsToAttributeFields(rows);
    }

    @Override
    public ValidationResults validateRoutingData(@WebParam(name = "attributeName") String attributeName, Map<String, String> paramMap) {
        if (StringUtils.isBlank(attributeName)) {
            throw new RiceIllegalArgumentException("attributeName was null or blank");
        }
        WorkflowRuleAttribute attribute = loadAttribute(attributeName);
        List<WorkflowServiceError> errors = attribute.validateRoutingData(paramMap);
        ValidationResults.Builder builder = ValidationResults.Builder.create();
        for (WorkflowServiceError error : errors) {
            builder.addError(error.getArg1(), error.getMessage());
        }
        return builder.build();
    }

    @Override
    public ValidationResults validateSearchData(@WebParam(name = "attributeName") String attributeName,  Map<String, String> paramMap) {
        if (StringUtils.isBlank(attributeName)) {
            throw new RiceIllegalArgumentException("attributeName was null or blank");
        }
        WorkflowRuleAttribute attribute = loadAttribute(attributeName);
        List<WorkflowServiceError> errors = attribute.validateRoutingData(paramMap);
        ValidationResults.Builder builder = ValidationResults.Builder.create();
        for (WorkflowServiceError error : errors) {
            builder.addError(error.getArg1(), error.getMessage());
        }
        return builder.build();
    }

    @Override
    public ValidationResults validateRuleData(@WebParam(name = "attributeName") String attributeName, Map<String, String> paramMap) {
        if (StringUtils.isBlank(attributeName)) {
            throw new RiceIllegalArgumentException("customizerName was null or blank");
        }
        List<WorkflowServiceError> errors = new ArrayList<WorkflowServiceError>();
        Object attribute = loadAttribute(attributeName);
        if (WorkflowRuleSearchAttribute.class.isAssignableFrom(attribute.getClass())) {
            errors = ((WorkflowRuleSearchAttribute)attribute).validateSearchData(paramMap);
        } else {
            errors = ((WorkflowRuleAttribute)attribute).validateRoutingData(paramMap);
        }
        ValidationResults.Builder builder = ValidationResults.Builder.create();
        for (WorkflowServiceError error : errors) {
            builder.addError(error.getArg1(), error.getMessage());
        }
        return builder.build();
    }

    @Override
    public List<RoleName> getRoleNames(String attributeName) {
        Object roleAttribute = loadAttribute(attributeName);
        if (!RoleAttribute.class.isAssignableFrom(roleAttribute.getClass())) {
            throw new RiceIllegalArgumentException("Failed to locate a RoleAttribute with the given name: " + attributeName);
        }
        return ((RoleAttribute)roleAttribute).getRoleNames();
    }

    private WorkflowRuleAttribute loadAttribute(String attributeName) {
        ExtensionDefinition extensionDefinition = getExtensionRepositoryService().getExtensionByName(attributeName);
        if (extensionDefinition == null) {
            throw new RiceIllegalArgumentException("Failed to locate a WorkflowRuleAttribute with the given name: " + attributeName);
        }
        Object attribute = ExtensionUtils.loadExtension(extensionDefinition);
        if (attribute == null) {
            throw new RiceIllegalArgumentException("Failed to load WorkflowRuleAttribute for: " + extensionDefinition);
        }
        if (!WorkflowRuleAttribute.class.isAssignableFrom(attribute.getClass())) {
            throw new RiceIllegalArgumentException("Failed to locate a WorkflowRuleAttribute with the given name: " + attributeName);
        }
        if (attribute instanceof XmlConfiguredAttribute) {
            ((XmlConfiguredAttribute)attribute).setExtensionDefinition(extensionDefinition);
        }

        return (WorkflowRuleAttribute)attribute;
    }


    protected ExtensionRepositoryService getExtensionRepositoryService() {
        return extensionRepositoryService;
    }

    public void setExtensionRepositoryService(ExtensionRepositoryService extensionRepositoryService) {
        this.extensionRepositoryService = extensionRepositoryService;
    }
}
