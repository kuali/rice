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
package org.kuali.rice.coreservice.impl.style;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * JPA-based implementation of the {@code StyleDao}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StyleDaoJpa implements StyleDao {

    private EntityManager entityManager;

    @Override
    public List<String> getAllStyleNames() {
        return (List<String>)getEntityManager().createNamedQuery("StyleBo.findAllStyleNames").
                getResultList();
    }

    public EntityManager getEntityManager(){
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager){
        this.entityManager = entityManager;
    }

}
