/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.krms.impl.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.web.spring.controller.UifControllerBase;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/krmsEditor")
public class EditorController extends UifControllerBase {

	@Override
    protected EditorForm createInitialForm(HttpServletRequest request) {
        return new EditorForm();
    }

	@RequestMapping(params = "methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") EditorForm editorForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
		
		// populate model for testing
		ContextBo context = new ContextBo();
		AgendaBo agenda = new AgendaBo();
		
		String[] agendaIds = request.getParameterValues("agendaId");
        if (agendaIds == null || agendaIds.length != 1) { 
            //throw new RiceRuntimeException("one and only one agendaId request parameter may be passed");
        } else {
            // TODO: throw out this hacky junk
            String agendaId = agendaIds[0];

            agenda = getBoService().findBySinglePrimaryKey(AgendaBo.class, agendaId);
            String contextId = agenda.getContextId();

            context = getBoService().findBySinglePrimaryKey(ContextBo.class, contextId);
        }

		editorForm.setContext(context);
		editorForm.setAgenda(agenda);

		return getUIFModelAndView(editorForm, editorForm.getViewId());
	}

	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=save")
	public ModelAndView save(@ModelAttribute("KualiForm") EditorForm editorForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
//		//For testing server side errors:
//		if(editorForm.getField2().equals("server_error")){
//			GlobalVariables.getMessageMap().putError("field2", "serverTestError");
//			GlobalVariables.getMessageMap().putError("field2", "serverTestError2");
//			GlobalVariables.getMessageMap().putWarning("field2", "serverTestWarning");
//			GlobalVariables.getMessageMap().putInfo("field2", "serverTestInfo");
//
//			//GlobalVariables.getMessageMap().clearErrorMessages();
//			return getUIFModelAndView(editorForm, editorForm.getViewId(), editorForm.getPageId());
//		}
		return getUIFModelAndView(editorForm, editorForm.getViewId());
	}
	
	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=close")
	public ModelAndView close(@ModelAttribute("KualiForm") EditorForm editorForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		return getUIFModelAndView(editorForm, editorForm.getViewId());
	}
	
	private BusinessObjectService getBoService() {
	    return KRADServiceLocator.getBusinessObjectService();
	}

}
