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
package edu.iu.uis.eden.help;

/**
 * Model bean representing a piece of help information which adds extra information about fields and
 * data in the user interface.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class HelpEntry {
    private Long helpId;
	private String helpName;
	private String helpText;
	private String helpKey;
	private Integer lockVerNbr;
	
    public Long getHelpId() {
        return helpId;
    }
    public void setHelpId(Long helpId) {
        this.helpId = helpId;
    }
    public String getHelpName() {
        return helpName;
    }
    public void setHelpName(String helpName) {
        this.helpName = helpName;
    }
    public String getHelpText() {
    	return helpText;
    }
    public void setHelpText(String helpText) {
    	this.helpText = helpText;
    }
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }
    public String getHelpKey() {
        return helpKey;
    }
    public void setHelpKey(String helpKey) {
        this.helpKey = helpKey;
    }
}
