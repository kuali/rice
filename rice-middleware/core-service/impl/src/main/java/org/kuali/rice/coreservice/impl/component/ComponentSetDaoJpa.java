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
package org.kuali.rice.coreservice.impl.component;

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.kuali.rice.core.framework.persistence.ojb.DataAccessUtils;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;


/**
 * JDBC-based implementation of the {@code ComponentSetDao}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentSetDaoJpa implements ComponentSetDao {
    private DataObjectService dataObjectService;

    @Override
    public boolean saveIgnoreLockingFailure(ComponentSetBo componentSetBo) {
        try{
            getDataObjectService().save(componentSetBo);
        } catch (RuntimeException e) {
            if (e.getClass().isAssignableFrom(OptimisticLockException.class)) {
                return false;
            }
            throw e;
        }
        return true;
    }
    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }


}
