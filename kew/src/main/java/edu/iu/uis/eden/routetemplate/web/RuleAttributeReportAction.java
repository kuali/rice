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
package edu.iu.uis.eden.routetemplate.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action for generating a report for {@link RuleAttribute}s.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleAttributeReportAction extends WorkflowAction {
    private static final Logger LOG = Logger.getLogger(RuleAttributeReportAction.class);

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("start");
        return mapping.findForward("basic");
    }

    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RuleAttributeReportForm rarform = (RuleAttributeReportForm) form;
        ExportDataSet dataSet = new ExportDataSet(ExportFormat.XML);
        dataSet.getRuleAttributes().add(rarform.getRuleAttribute());
        return exportDataSet(request, dataSet);
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        LOG.info("establishRequiredState");
        RuleAttributeReportForm rarform = (RuleAttributeReportForm) form;
        Long id = rarform.getRuleAttributeId();
        /* Nice inconsistency observed: org.apache.struts.action.MESSAGE is set regardless of whether there are any 
         * messages (or at least any set here), but org.apache.struts.action.ACTION_MESSAGE IS set when there is a
         * message set here.
         * On the other hand, org.apache.struts.action.ERROR, is only set when and only when there is an error defined here,
         * and no such ACTION_ERROR is ever set.
         * I'm using those in a c:choose block to determine output because I have not found a better way to determine whether
         * there are any messages on the request (at least not and be able to render an alternative, logic:messagesPresent doesn't
         * have any "otherwise" clause).
         */
        if (id == null) {
            LOG.error("id not specified");
            ActionErrors errors = new ActionErrors();
            errors.add(Globals.ERROR_KEY, new ActionMessage("ruleAttributeReport.idNotSpecified"));
            saveErrors(request, errors);
            // impedence mismatch here: have to return ActionMessages so that control flow does not continue
            // but that will result in errors being saved under messages key *shrug*
            return errors;
            //throw new WorkflowException("RuleAttribute id not specified");
        }
        RuleAttribute ra = KEWServiceLocator.getRuleAttributeService().findByRuleAttributeId(id);
        if (ra == null) {
            LOG.error("rule attribute not found");
            ActionErrors errors = new ActionErrors();
            errors.add(Globals.ERROR_KEY, new ActionMessage("ruleAttributeReport.idNotFound", id));
            saveErrors(request, errors);
            // impedence mismatch here: have to return ActionMessages so that control flow does not continue
            // but that will result in errors being saved under messages key *shrug*
            return errors;
            //throw new WorkflowException("RuleAttribute '" + id + "' not found");
        }
        rarform.setRuleAttribute(ra);
        return null;
    }
}