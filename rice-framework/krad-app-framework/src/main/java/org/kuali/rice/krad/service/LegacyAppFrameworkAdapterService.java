/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.service;

import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Deprecated
/**
 *  Provides an interface to the legacy adapter for continued use in PersistableBusinessObject
 */
public interface LegacyAppFrameworkAdapterService {
    /**
     * Refresh persistableBusinessObject
     * @param persistableBusinessObjectBase
     */
    public void refresh(PersistableBusinessObjectBase persistableBusinessObjectBase);

    /**
     * Refresh Nonupdateable references
     * @param persistableBusinessObjectBase
     */
    public void refreshNonUpdateableReferences(PersistableBusinessObjectBase persistableBusinessObjectBase);

    /**
     * Retrieve reference object for persistable business object
     * @param persistableBusinessObject
     * @param referenceObjectName
     */
    public void retrieveReferenceObject(PersistableBusinessObject persistableBusinessObject, String referenceObjectName);

    /**
     * Returns if the class is persistable or not
     * @param objectClass
     * @return
     */
    public boolean isPersistable(Class<?> objectClass);


}
