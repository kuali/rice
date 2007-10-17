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
package edu.iu.uis.eden.web.backdoor;

import org.apache.struts.action.ActionForm;

/**
 * A Struts ActionForm for the {@link BackdoorAction}.
 *
 * @see BackdoorAction
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BackdoorForm extends ActionForm {

	private static final long serialVersionUID = -2720178686804392055L;

	private String methodToCall = "";
    private String backdoorId;
    private Boolean showBackdoorLogin;
    private Boolean isWorkflowAdmin;
    private String linkTarget;
    private String targetName;
    //determines whether to show the backdoor login textbox in the backdoor links page
    private String backdoorLinksBackdoorLogin;

    private String graphic="yes";

    public String getBackdoorId() {
        return backdoorId;
    }
    public void setBackdoorId(String backdoorId) {
        this.backdoorId = backdoorId;
    }
    public String getMethodToCall() {
        return methodToCall;
    }
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }
    public Boolean getIsWorkflowAdmin() {
        return isWorkflowAdmin;
    }
    public void setIsWorkflowAdmin(Boolean isWorkflowAdmin) {
        this.isWorkflowAdmin = isWorkflowAdmin;
    }
    public Boolean getShowBackdoorLogin() {
        return showBackdoorLogin;
    }
    public void setShowBackdoorLogin(Boolean showBackdoorLogin) {
        this.showBackdoorLogin = showBackdoorLogin;
    }
    public String getLinkTarget() {
        return linkTarget;
    }
    public void setLinkTarget(String linkTarget) {
        this.linkTarget = linkTarget;
    }
    public String getTargetName() {
        return targetName;
    }
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
    public String getGraphic(){
    	return this.graphic;
    }
    public void setGraphic(String choice){
    	if(!edu.iu.uis.eden.util.Utilities.isEmpty(choice)&&choice.trim().equals("no")){
    		this.graphic="no";
    	}else{
    		this.graphic="yes";
    	}
    }
	public String getBackdoorLinksBackdoorLogin() {
		return backdoorLinksBackdoorLogin;
	}
	public void setBackdoorLinksBackdoorLogin(String backdoorLinksBackdoorLogin) {
		this.backdoorLinksBackdoorLogin = backdoorLinksBackdoorLogin;
	}
}
