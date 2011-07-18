/*
 * Copyright 2005-2009 The Kuali Foundation
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
package org.kuali.rice.kew.dto;

import org.kuali.rice.core.api.util.AbstractKeyValue;

/**
 * Transport object for State.  Essentially an empty "marker" subclass of state to use as a VO
 * Inherits all functionality.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StateDTO extends AbstractKeyValue {
    
	private static final long serialVersionUID = 4787443469039295347L;
	
	private String stateId;

    public String getStateId() {
		return this.stateId;
	}

    public void setStateId(String stateId) {
		this.stateId = stateId;
	}
    
    public void setKey(String key) {
		this.key = key;
	}
    
    public void setValue(String value) {
		this.value = value;
	}
}
