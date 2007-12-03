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
package edu.iu.uis.eden.web;

import java.io.Serializable;
import java.util.Map;

import edu.iu.uis.eden.util.CodeTranslator;

/**
 * A bean for the web-tier when represents the recipient of an Ad Hoc request.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AppSpecificRouteRecipient implements Serializable {

	private static final long serialVersionUID = 6587140192756322878L;

	private static Map actionRequestCds = CodeTranslator.arLabels;
    
    protected String type;
    protected String actionRequested;
    protected String id;  //can be networkId or workgroupname
   
    public String getActionRequested() {
        return actionRequested;
    }
    public void setActionRequested(String actionRequested) {
        this.actionRequested = actionRequested;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    public String getActionRequestedValue(){
        if(getActionRequested() != null && !getActionRequested().trim().equals("")){
            return (String) actionRequestCds.get(getActionRequested());
        } 
        return null;
    }
   
}
