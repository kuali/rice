/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtension;
import org.kuali.rice.krad.service.LegacyAppFrameworkAdapterService;
import org.kuali.rice.krad.service.LegacyDataAdapter;

/**
 *   @see org.kuali.rice.krad.service.LegacyAppFrameworkAdapterService
 */
@Deprecated
public class LegacyAppFrameworkAdapterServiceImpl implements LegacyAppFrameworkAdapterService{

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LegacyAppFrameworkAdapterServiceImpl.class);

    private LegacyDataAdapter legacyDataAdapter;

    public LegacyDataAdapter getLegacyDataAdapter() {
        return legacyDataAdapter;
    }

    public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
        this.legacyDataAdapter = legacyDataAdapter;
    }
    @Deprecated
    public void refresh(PersistableBusinessObjectBase persistableBusinessObjectBase) {
        getLegacyDataAdapter().retrieveNonKeyFields(persistableBusinessObjectBase);
    }
    @Deprecated
    public void refreshNonUpdateableReferences(PersistableBusinessObjectBase persistableBusinessObjectBase) {
        getLegacyDataAdapter().refreshAllNonUpdatingReferences(persistableBusinessObjectBase);
    }
    @Deprecated
    public void retrieveReferenceObject(PersistableBusinessObject persistableBusinessObject, String referenceObjectName) {
        getLegacyDataAdapter().retrieveReferenceObject(persistableBusinessObject,referenceObjectName);
    }

    @Override
    public boolean isPersistable(Class<?> objectClass) {
        return getLegacyDataAdapter().isPersistable(objectClass);
    }

    @Override
    public PersistableBusinessObjectExtension getExtension(
            Class<? extends PersistableBusinessObject> businessObjectClass) throws InstantiationException, IllegalAccessException {
        return (PersistableBusinessObjectExtension) getLegacyDataAdapter().getExtension(businessObjectClass);
    }

    @Override
    public void refreshReferenceObject(PersistableBusinessObject businessObject, String referenceObjectName) {
        getLegacyDataAdapter().refreshReferenceObject(businessObject, referenceObjectName);
    }

}
