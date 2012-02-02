package org.kuali.rice.krms.impl.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.InquiryForm;
import org.kuali.rice.krad.web.form.MaintenanceForm;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.framework.type.ActionTypeService;
import org.kuali.rice.krms.framework.type.AgendaTypeService;
import org.kuali.rice.krms.framework.type.RuleTypeService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.type.ActionTypeServiceBase;
import org.kuali.rice.krms.impl.type.AgendaTypeServiceBase;
import org.kuali.rice.krms.impl.type.RuleTypeServiceBase;
import org.kuali.rice.krms.impl.ui.AgendaEditor;

import java.util.ArrayList;
import java.util.List;

/**
 * KRMS utilility class for retrieving KRMS objects for {@link org.kuali.rice.krms.impl.ui.AgendaEditorMaintainable} and
 * {@link org.kuali.rice.krms.impl.repository.AgendaInquiryHelperServiceImpl}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KrmsRetriever {

    private static final long serialVersionUID = 1L;

    /**
     * This only supports a single action within a rule.
     * @param agendaEditor
     * @return List<RemotableAttributeField>
     */
    public List<RemotableAttributeField> retrieveRuleActionCustomAttributes(AgendaEditor agendaEditor) {
        List<RemotableAttributeField> results = new ArrayList<RemotableAttributeField>();
        // if we have an rule action w/ a typeId set on it
        if (!StringUtils.isBlank(agendaEditor.getAgendaItemLineRuleAction().getTypeId())) {
            ActionTypeService actionTypeService = getActionTypeService(agendaEditor.getAgendaItemLineRuleAction().getTypeId());
            results.addAll(actionTypeService.getAttributeFields(agendaEditor.getAgendaItemLineRuleAction().getTypeId()));
        }

        return results;
    }

    /**
     * Get the AgendaEditor out of the MaintenanceForm's newMaintainableObject
     * @param model the MaintenanceForm
     * @return the AgendaEditor
     */
    private AgendaEditor getAgendaEditor(Object model) {
        MaintenanceForm maintenanceForm = (MaintenanceForm)model;
        return (AgendaEditor)maintenanceForm.getDocument().getNewMaintainableObject().getDataObject();
    }

    private ActionTypeService getActionTypeService(String krmsTypeId) {
        //
        // Get the ActionTypeService by hook or by crook
        //

        KrmsTypeDefinition krmsType = KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService().getTypeById(krmsTypeId);

        ActionTypeService actionTypeService = null;

        if (!StringUtils.isBlank(krmsTypeId)) {
            String serviceName = krmsType.getServiceName();

            if (!StringUtils.isBlank(serviceName)) {
                actionTypeService = KrmsRepositoryServiceLocator.getService(serviceName);
            }
        }
        if (actionTypeService == null) {
            actionTypeService = ActionTypeServiceBase.defaultActionTypeService;
        }

        //        if (actionTypeService == null) { actionTypeService = AgendaTypeServiceBase.defaultAgendaTypeService; }

        return actionTypeService;
    }

    /**
     *
     * @param agendaEditor
     * @return List<RemotableAttributeField>
     */
    public List<RemotableAttributeField> retrieveAgendaCustomAttributes(AgendaEditor agendaEditor) {
        List<RemotableAttributeField> results = new ArrayList<RemotableAttributeField>();
        // if we have an agenda w/ a typeId set on it
        if (agendaEditor.getAgenda() != null && !StringUtils.isBlank(agendaEditor.getAgenda().getTypeId())) {

            String krmsTypeId = agendaEditor.getAgenda().getTypeId();

            AgendaTypeService agendaTypeService = getAgendaTypeService(krmsTypeId);
            results.addAll(agendaTypeService.getAttributeFields(krmsTypeId));
        }

        return results;
    }

    private AgendaTypeService getAgendaTypeService(String krmsTypeId) {
        //
        // Get the AgendaTypeService by hook or by crook
        //

        KrmsTypeDefinition krmsType =
                KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService().
                        getTypeById(krmsTypeId);

        AgendaTypeService agendaTypeService = null;

        if (!StringUtils.isBlank(krmsTypeId)) {
            String serviceName = krmsType.getServiceName();

            if (!StringUtils.isBlank(serviceName)) {
                agendaTypeService = KrmsRepositoryServiceLocator.getService(serviceName);
            }
        }

        if (agendaTypeService == null) { agendaTypeService = AgendaTypeServiceBase.defaultAgendaTypeService; }

        return agendaTypeService;
    }

    public List<RemotableAttributeField> retrieveRuleCustomAttributes(AgendaEditor agendaEditor) {
        List<RemotableAttributeField> results = new ArrayList<RemotableAttributeField>();
        // if we have an rule w/ a typeId set on it
        if (agendaEditor.getAgendaItemLine() != null && agendaEditor.getAgendaItemLine().getRule() != null
                && !StringUtils.isBlank(agendaEditor.getAgendaItemLine().getRule().getTypeId())) {

            String krmsTypeId = agendaEditor.getAgendaItemLine().getRule().getTypeId();

            RuleTypeService ruleTypeService = getRuleTypeService(krmsTypeId);
            results.addAll(ruleTypeService.getAttributeFields(krmsTypeId));
        }

        return results;
    }


    private RuleTypeService getRuleTypeService(String krmsTypeId) {
        RuleTypeService ruleTypeService = null;
        String serviceName = getRuleTypeServiceName(krmsTypeId);

        if (!StringUtils.isBlank(serviceName)) {
            ruleTypeService = KrmsRepositoryServiceLocator.getService(serviceName);
        }
        if (ruleTypeService == null) {
            ruleTypeService = RuleTypeServiceBase.defaultRuleTypeService;
        }
        return ruleTypeService;
    }

    private String getRuleTypeServiceName(String krmsTypeId) {
        String serviceName = null;
        KrmsTypeDefinition krmsType =
                KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService().
                        getTypeById(krmsTypeId);

        if (!StringUtils.isBlank(krmsTypeId)) {
            serviceName = krmsType.getServiceName();
        }
        return serviceName;
    }
}
