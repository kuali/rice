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
package org.kuali.rice.krad.web.jsf.struts;

import javax.faces.context.FacesContext;


/**
 * This is a description of what this class does - jkneal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class InquiryBacking extends AbstractBacking {

	public String inquiry() {
        FacesContext context = FacesContext.getCurrentInstance();
        
        StringBuffer url = inquirySubmit(context);
        url.append("?methodToCall=inquiryForward");
//        url.append("?businessObjectClassName=edu.sampleu.travel.bo.TravelAccount");
//        
//        InquirySubmitForm form = (InquirySubmitForm) getActionForm(context);
//        url.append("&number=" + form.getAccount().getNumber());
        forward(context, url.toString());
        
        return (null);
	}
	
	
    protected StringBuffer inquirySubmit(FacesContext context) {
        return (action(context, "/kr/inquirySubmit"));
    }
}
