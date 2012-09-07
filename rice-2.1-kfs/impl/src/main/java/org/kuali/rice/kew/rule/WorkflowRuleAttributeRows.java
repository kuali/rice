package org.kuali.rice.kew.rule;

import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.framework.rule.attribute.WorkflowRuleAttributeFields;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Row;

import java.util.List;
import java.util.Map;

/**
 * This class wraps a {@link WorkflowRuleAttributeFields} object and provides a KNS-compatible view to the data
 * contained therein. Primarily, this means that RemotableAttributeField objects are transformed to KNS Row objects.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkflowRuleAttributeRows {

    private final WorkflowRuleAttributeFields fields;
    private final List<Row> rows;

    public WorkflowRuleAttributeRows(WorkflowRuleAttributeFields fields) {
        this.fields = fields;
        this.rows = convertToRows(fields.getAttributeFields());
    }

    private static List<Row> convertToRows(List<RemotableAttributeField> attributeFields) {
        return FieldUtils.convertRemotableAttributeFields(attributeFields);
    }

    public List<Row> getRows() {
        return rows;
    }

    public List<RemotableAttributeError> getValidationErrors() {
        return fields.getValidationErrors();
    }

    public Map<String, String> getRuleExtensionValues() {
        return fields.getRuleExtensionValues();
    }

}
