/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.bo;

import java.util.LinkedHashMap;



/**
 * 
 */
public class Module extends TransientBusinessObjectBase {

    private static final long serialVersionUID = 787567094298971223L;
    private String moduleCode;
    private String moduleName;
    
    /**
     * Default no-arg constructor.
     */
    public Module() {

    }
    
	/**
     * Gets the moduleCode attribute.
     * 
     * @return Returns the moduleCode
     * 
     */
    public String getModuleCode() {
		return moduleCode;
	}

    /**
     * Sets the moduleCode attribute.
     * 
     * @param moduleCode The campusCode to set.
     * 
     */
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

    /**
     * Gets the moduleName attribute.
     * 
     * @return Returns the moduleName
     * 
     */
	public String getModuleName() {
		return moduleName;
	}

    /**
     * Gets the moduleName attribute.
     * 
     * @return Returns the moduleName
     * 
     */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("moduleCode", this.moduleCode);
        m.put("moduleName", this.moduleName);
        return m;
    }

}
