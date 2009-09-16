/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.util.ExternalizableBusinessObjectUtils;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KNSModuleService extends ModuleServiceBase {

    protected List<String> businessObjects;

    @Override
    public boolean isResponsibleFor(Class businessObjectClass) {
        if (businessObjects != null) {
            if (businessObjects.contains(businessObjectClass.getName())) {
                return true;
            }
        }
        if (ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass)) {
            Class externalizableBusinessObjectInterface = ExternalizableBusinessObjectUtils.determineExternalizableBusinessObjectSubInterface(businessObjectClass);
            if (externalizableBusinessObjectInterface != null) {
                Map<Class, Class> validEBOs = getModuleConfiguration().getExternalizableBusinessObjectImplementations();
                if (validEBOs != null) {
                    if (validEBOs.get(externalizableBusinessObjectInterface) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<String> getBusinessObjects() {
        return this.businessObjects;
    }

    public void setBusinessObjects(List<String> businessObjects) {
        this.businessObjects = businessObjects;
    }
}
