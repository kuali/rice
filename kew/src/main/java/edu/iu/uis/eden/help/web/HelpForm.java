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
package edu.iu.uis.eden.help.web;

import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.help.HelpEntry;

/**
 * Struts ActionForm for {@link HelpAction}. 
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class HelpForm extends ActionForm {
    
	private static final long serialVersionUID = 6517665417435951210L;
	private String methodToCall = "";
    private HelpEntry helpEntry;
    private String showEdit;
    private boolean isAdmin;
    private String showDelete;
    
    public HelpForm(){
        helpEntry = new HelpEntry();
    }
    
    public HelpEntry getHelpEntry() {
        return helpEntry;
    }
    public void setHelpEntry(HelpEntry helpEntry) {
        this.helpEntry = helpEntry;
    }
    public String getMethodToCall() {
        return methodToCall;
    }
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }
   
    public String getShowEdit() {
        return showEdit;
    }
    public void setShowEdit(String showEdit) {
        this.showEdit = showEdit;
    }
    
    public boolean getIsAdmin(){
    	return isAdmin;
    }
    
    public void setIsAdmin(boolean isAdmin){
    	this.isAdmin = isAdmin;
    }
    
    public String getShowDelete(){
    	return showDelete;
    }
    
    public void setShowDelete(String showDelete){
    	this.showDelete=showDelete;
    }
    
}
