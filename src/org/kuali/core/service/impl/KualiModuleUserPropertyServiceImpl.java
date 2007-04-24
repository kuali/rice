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
package org.kuali.core.service.impl;

import java.util.Collection;

import org.kuali.core.bo.user.KualiModuleUserProperty;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.KualiModuleUserPropertyDao;
import org.kuali.core.service.KualiModuleUserPropertyService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class KualiModuleUserPropertyServiceImpl implements KualiModuleUserPropertyService {

    private KualiModuleUserPropertyDao moduleUserPropertyDao;
    
    public void save(KualiModuleUserProperty prop) {
        moduleUserPropertyDao.save( prop );
    }

    public void save(Collection<KualiModuleUserProperty> properties) {
        moduleUserPropertyDao.save( properties );
    }
    
    public Collection<KualiModuleUserProperty> getPropertiesForUser(UniversalUser user) {
        return moduleUserPropertyDao.getPropertiesForUser( user );
    }

    public Collection<KualiModuleUserProperty> getPropertiesForUser( String personUniversalIdentifier ) {
        return moduleUserPropertyDao.getPropertiesForUser( personUniversalIdentifier );
    }
    
    public KualiModuleUserPropertyDao getModuleUserPropertyDao() {
        return moduleUserPropertyDao;
    }

    public void setModuleUserPropertyDao(KualiModuleUserPropertyDao moduleUserPropertyDao) {
        this.moduleUserPropertyDao = moduleUserPropertyDao;
    }

}
