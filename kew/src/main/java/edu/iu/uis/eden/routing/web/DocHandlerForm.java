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
package edu.iu.uis.eden.routing.web;

import edu.iu.uis.eden.web.WorkflowRoutingForm;

/**
 * A Struts ActionForm for the {@link WorkflowDocHandlerAction}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocHandlerForm extends WorkflowRoutingForm {

	private static final long serialVersionUID = 3054059006090336396L;
	private String methodToCall = "";
    private String lookupableImplServiceName;
    private String docHandlerRedirectUrl;
    
    public String getMethodToCall() {
        return methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    public String getLookupableImplServiceName() {
        return lookupableImplServiceName;
    }
    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
    }
   
    public String getDocHandlerRedirectUrl() {
        return docHandlerRedirectUrl;
    }
    public void setDocHandlerRedirectUrl(String docHandlerRedirectUrl) {
        this.docHandlerRedirectUrl = docHandlerRedirectUrl;
    }


}