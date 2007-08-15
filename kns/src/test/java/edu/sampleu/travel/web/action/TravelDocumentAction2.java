/*
 * Copyright 2007 The Kuali Foundation.
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
package edu.sampleu.travel.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RiceConstants;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.rice.KNSServiceLocator;

import edu.sampleu.travel.bo.TravelAccount;
import edu.sampleu.travel.document.TravelDocument2;
import edu.sampleu.travel.web.form.TravelDocumentForm2;

public class TravelDocumentAction2 extends KualiDocumentActionBase {

    public ActionForward insertAccount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelDocumentForm2 travelForm = (TravelDocumentForm2) form;
        TravelAccount travAcct = (TravelAccount) KNSServiceLocator.getBusinessObjectService().retrieve(travelForm.getTravelAccount());
        ((TravelDocument2) travelForm.getDocument()).getTravelAccounts().add(travAcct);
        travelForm.setTravelAccount(new TravelAccount());
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

}
