/*
 * Copyright 2008 The Kuali Foundation
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
 * See the License for the specific language governing responsibilitys and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.role.dto;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.bo.role.ResponsibilityDetails;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ResponsibilityDetailsInfo implements ResponsibilityDetails {

	protected String responsibilityId;

	protected AttributeSet details;

	public String getResponsibilityId() {
		return this.responsibilityId;
	}

	public void setResponsibilityId(String responsibilityId) {
		this.responsibilityId = responsibilityId;
	}

	public AttributeSet getDetails() {
		return this.details;
	}

	public void setDetails(AttributeSet responsibilityDetails) {
		this.details = responsibilityDetails;
	}
	
	public boolean hasDetails() {
		return !details.isEmpty();
	}
	
}
