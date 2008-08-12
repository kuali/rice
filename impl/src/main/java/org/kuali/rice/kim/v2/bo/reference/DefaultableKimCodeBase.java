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
package org.kuali.rice.kim.v2.bo.reference;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@MappedSuperclass
public abstract class DefaultableKimCodeBase extends KimCodeBase implements DefaultableKimCode {

    @Type(type="yes_no")
	@Column(name="DFLT_IND")
	protected boolean dflt;

	/**
	 * @return the dflt
	 */
	public boolean isDefault() {
		return this.dflt;
	}

	/**
	 * @param dflt the dflt to set
	 */
	public void setDefault(boolean dflt) {
		this.dflt = dflt;
	}
}
