/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.rice.kns.service;

import java.util.List;

import org.kuali.rice.kns.KualiModule;
import org.kuali.rice.kns.bo.user.KualiModuleUser;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.bo.user.UserId;
import org.kuali.rice.kns.dao.KualiModuleUserDao;
import org.kuali.rice.kns.exception.UserNotFoundException;

public interface KualiModuleUserService<T extends KualiModuleUser> {

    T getModuleUser( UniversalUser universalUser ) throws UserNotFoundException ;
    
    T getModuleUser( String moduleID, UserId userId ) throws UserNotFoundException ;
    T getModuleUser( String moduleID, String personUniversalIdentifier ) throws UserNotFoundException ;
    T getModuleUser( String moduleID, UniversalUser universalUser ) throws UserNotFoundException ;
    
    List<String> getPropertyList();
    
    Class<T> getModuleUserClass();
    
    Object getUserActiveCriteria();
    
    KualiModule getModule();
    
    void setModule( KualiModule module );
    
    KualiModuleUserDao getKualiModuleUserDao();
}
