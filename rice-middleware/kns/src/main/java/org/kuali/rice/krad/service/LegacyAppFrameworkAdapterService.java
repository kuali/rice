/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtension;

@Deprecated
/**
 *  Provides an interface to the legacy adapter for continued use in PersistableBusinessObject
 *
 *  @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LegacyAppFrameworkAdapterService {

    /**
     * Refresh persistableBusinessObject
     * @param persistableBusinessObjectBase
     */
    void refresh(PersistableBusinessObjectBase persistableBusinessObjectBase);

    /**
     * Refresh Nonupdateable references
     * @param persistableBusinessObjectBase
     */
    void refreshNonUpdateableReferences(PersistableBusinessObjectBase persistableBusinessObjectBase);

    /**
     * Retrieve reference object for persistable business object
     * @param persistableBusinessObject
     * @param referenceObjectName
     */
    void retrieveReferenceObject(PersistableBusinessObject persistableBusinessObject, String referenceObjectName);

    /**
     * Returns if the class is persistable or not
     * @param objectClass
     * @return
     */
    boolean isPersistable(Class<?> objectClass);

    /**
     * Creates an instance of the extension for the given business object class.
     */
    PersistableBusinessObjectExtension getExtension(Class<? extends PersistableBusinessObject> businessObjectClass)
            throws InstantiationException, IllegalAccessException;

    /**
     * Refreshes the specified reference object on the given business object.
     */
    void refreshReferenceObject(PersistableBusinessObject businessObject, String referenceObjectName);

}
