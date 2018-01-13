/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kew.useroptions;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Version;

import org.kuali.rice.kew.api.preferences.Preferences;


/**
 * An option defined for a user.  These are used to store user {@link Preferences}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@IdClass(UserOptionsId.class)
@Entity
@Table(name="KREW_USR_OPTN_T")
@Cacheable(false)
public class UserOptions implements Comparable<UserOptions> {

	@Id
	@Column(name="PRNCPL_ID")
	private String workflowId;

	@Id
	@Column(name="PRSN_OPTN_ID")
	private String optionId;

	@Column(name="VAL")
	private String optionVal;

	@Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public String getOptionId() {
		return optionId;
	}

	public String getOptionVal() {
		return optionVal;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setLockVerNbr(Integer integer) {
		lockVerNbr = integer;
	}

	public void setOptionId(String string) {
		optionId = string;
	}

	public void setOptionVal(String string) {
		optionVal = string;
	}

	public void setWorkflowId(String string) {
	    workflowId = string;
	}

    /**
     * Compares the given object is an instance of this class, then determines comparison based on the option id.
     * @param o the object to compare with
     * @return The value 0 if the option ID is equal; a value less than 0 if
     * the Option ID is greater than this object's; and a value greater than 0 if the argument's
     * Option ID less than this object's.
     */
    @Override
    public int compareTo(UserOptions o) {
        return getOptionId().compareTo(o.getOptionId());
    }

}

