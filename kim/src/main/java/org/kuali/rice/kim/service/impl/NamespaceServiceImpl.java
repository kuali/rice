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
package org.kuali.rice.kim.service.impl;

import java.util.List;

import org.kuali.rice.kim.dto.NamespaceDTO;
import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.service.NamespaceService;

/**
 * This is the default KIM Namespace implementation that is provided by Rice.  This will mature over time as the KIM 
 * component is developed.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NamespaceServiceImpl implements NamespaceService {

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.NamespaceService#getAllNamespaceNames()
     */
    public List<String> getAllNamespaceNames() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.NamespaceService#getAllNamespaces()
     */
    public List<NamespaceDTO> getAllNamespaces() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.NamespaceService#getPermissionNames(java.lang.String)
     */
    public List<String> getPermissionNames(String namespaceName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.NamespaceService#getPermissions(java.lang.String)
     */
    public List<PermissionDTO> getPermissions(String namespaceName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        return null;
    }
    
}
