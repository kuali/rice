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
package org.kuali.rice.krad.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.web.struts.form.InquirySubmitForm;
import org.springframework.util.Assert;

/**
 * This is a description of what this class does - jkneal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class InquirySubmitAction extends KualiAction {

    public ActionForward inquiryForward(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	InquirySubmitForm inquiryForm = (InquirySubmitForm) form;
    	Assert.isTrue(StringUtils.isNotBlank(inquiryForm.getAccount().getNumber()), "Number is null");
    	
    	return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
}
