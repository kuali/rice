/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kim.api.permission;

import org.kuali.rice.core.api.mo.GloballyUnique;
import org.kuali.rice.core.api.mo.Versioned;

/**
 * This is the contract for a KimPermission.  
 *  
 */
public interface PermissionContract extends Versioned, GloballyUnique{

    /**
     * The unique identifier for the KIM Permission.
     *
     * @return id
     */
    String getId();
    
    /**
     * The namespace code that this KIM Permission belongs too.
     *
     * @return namespaceCode
     */
    String getNamespaceCode();
    
    /**
     * The name of the KIM Permission.
     *
     * @return name
     */
    String getName();
    
    /**
     * The description of the KIM Permission.
     *
     * @return description
     */
	String getDescription();

    /**
     * The Template ID referenced by the KIM Permission.
     * 
     * @return templateId
     */
	String getTemplateId();    
    
    /**
     * The flag indicating if the KIM Permission is active.
     *
     * @return active
     */
    boolean isActive();    
}
