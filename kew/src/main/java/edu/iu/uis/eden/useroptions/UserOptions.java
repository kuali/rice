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
package edu.iu.uis.eden.useroptions;

import edu.iu.uis.eden.preferences.Preferences;

/**
 * An option defined for a user.  These are used to store user {@link Preferences}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserOptions implements Comparable {

	private String workflowId;
	private String optionId;
	private String optionVal;
	private Integer lockVerNbr;

	/**
	 * @return
	 */
	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	/**
	 * @return
	 */
	public String getOptionId() {
		return optionId;
	}

	/**
	 * @return
	 */
	public String getOptionVal() {
		return optionVal;
	}

	/**
	 * @return
	 */
	public String getWorkflowId() {
		return workflowId;
	}

	/**
	 * @param integer
	 */
	public void setLockVerNbr(Integer integer) {
		lockVerNbr = integer;
	}

	/**
	 * @param string
	 */
	public void setOptionId(String string) {
		optionId = string;
	}

	/**
	 * @param string
	 */
	public void setOptionVal(String string) {
		optionVal = string;
	}

	/**
	 * @param string
	 */
	public void setWorkflowId(String string) {
	    workflowId = string;
	}

	
    public int compareTo(Object o) {
        if (o instanceof UserOptions) {
            return this.getOptionId().compareTo(((UserOptions)o).getOptionId());
        }
        return 0;
    }
}
