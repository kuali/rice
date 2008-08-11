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
package org.kuali.rice.kew.workgroup.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.kew.export.ExportDataSet;
import org.kuali.rice.kew.export.ExportFormat;
import org.kuali.rice.kew.rule.RuleTemplate;
import org.kuali.rice.kew.rule.service.RuleTemplateService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.web.WorkflowAction;
import org.kuali.rice.kew.workgroup.WorkgroupType;
import org.kuali.rice.kew.workgroup.WorkgroupTypeService;


/**
 * A Struts Action for interactig with {@link RuleTemplate}s.
 *
 * @see RuleTemplateService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeAction extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return report(mapping, form, request, response);
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        return null;
    }

    public ActionForward report(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WorkgroupTypeForm workgroupTypeForm = (WorkgroupTypeForm) form;
        if (workgroupTypeForm.getWorkgroupTypeId() != null) {
            WorkgroupType workgroupType = getWorkgroupTypeService().findById(workgroupTypeForm.getWorkgroupTypeId());
            workgroupTypeForm.setWorkgroupType(workgroupType);
        } else if (!StringUtils.isBlank(workgroupTypeForm.getName())) {
        	WorkgroupType workgroupType = getWorkgroupTypeService().findByName(workgroupTypeForm.getName());
            workgroupTypeForm.setWorkgroupType(workgroupType);
        }
        return mapping.findForward("report");
    }

    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WorkgroupTypeForm workgroupTypeForm = (WorkgroupTypeForm) form;
        ExportDataSet dataSet = new ExportDataSet(ExportFormat.XML);
        if (workgroupTypeForm.getWorkgroupTypeId() != null) {
            WorkgroupType workgroupType = getWorkgroupTypeService().findById(workgroupTypeForm.getWorkgroupTypeId());
            dataSet.getWorkgroupTypes().add(workgroupType);
        }
        return exportDataSet(request, dataSet);
    }

    private WorkgroupTypeService getWorkgroupTypeService() {
        return (WorkgroupTypeService) KEWServiceLocator.getWorkgroupTypeService();
    }

}