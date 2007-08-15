/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.messaging.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.bus.services.KSBServiceLocator;

import edu.iu.uis.eden.web.WorkflowAction;

/**
 * Struts action for interacting with the queue of messages.
 *
 * @author rkirkend
 */
public class ThreadPoolAction extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	return mapping.findForward("basic");
    }

    public ActionForward update(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	ThreadPoolForm form = (ThreadPoolForm)actionForm;
	form.getThreadPool().setCorePoolSize(form.getCorePoolSize());
	if (form.getMaximumPoolSize() < form.getCorePoolSize()) {
	    form.setMaximumPoolSize(form.getCorePoolSize());
	}
	form.getThreadPool().setMaximumPoolSize(form.getMaximumPoolSize());
	return mapping.findForward("basic");
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm actionForm) throws Exception {
	ThreadPoolForm form = (ThreadPoolForm)actionForm;
	form.setThreadPool(KSBServiceLocator.getThreadPool());
	if (form.getCorePoolSize() == null) {
	    form.setCorePoolSize(form.getThreadPool().getCorePoolSize());
	}
	if (form.getMaximumPoolSize() == null) {
	    form.setMaximumPoolSize(form.getThreadPool().getMaximumPoolSize());
	}
	return null;
    }

}
