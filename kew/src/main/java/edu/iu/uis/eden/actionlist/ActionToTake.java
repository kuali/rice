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
package edu.iu.uis.eden.actionlist;

/**
 * Represents a mass action taken from the action list
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionToTake {

	private Long actionItemId;
    private String actionTakenCd;
    
    public Long getActionItemId() {
		return actionItemId;
	}

	public void setActionItemId(Long actionItemId) {
		this.actionItemId = actionItemId;
	}

	public String getActionTakenCd() {
        return actionTakenCd;
    }

    public void setActionTakenCd(String actionTakenCd) {
        this.actionTakenCd = actionTakenCd;
    }


}
