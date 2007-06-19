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
package org.kuali.rice.definition;

import java.io.Serializable;

/**
 * Represents a property used on an {@link ObjectDefinition}.
 * 
 * @author ewestfal
 */
public class PropertyDefinition implements Serializable {

	private static final long serialVersionUID = 8066047686689684888L;
	private final String name;
    private final DataDefinition data;
    
    public PropertyDefinition(String name, DataDefinition data) {
        this.name = name;
        this.data = data;
    }
    
    public String getName() {
        return this.name;
    }
    
    public DataDefinition getData() {
        return this.data;
    }
    
}
