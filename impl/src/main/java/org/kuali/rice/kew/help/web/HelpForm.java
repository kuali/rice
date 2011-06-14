/*
 * Copyright 2005-2007 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.help.web;

import org.kuali.rice.kew.help.HelpEntry;
import org.kuali.rice.krad.web.struts.form.KualiForm;


/**
 * Struts ActionForm for {@link HelpAction}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HelpForm extends KualiForm {

	private static final long serialVersionUID = 6517665417435951210L;
    private HelpEntry helpEntry;
    private String helpId;
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

    public String getHelpId() {
        return this.helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

}
