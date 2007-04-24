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
package org.kuali.core.bo.user;

import java.util.Map;

import org.kuali.core.bo.BusinessObject;

public interface KualiModuleUser extends BusinessObject {
    String getPersonUniversalIdentifier();
    
    boolean isActive();
    
    UniversalUser getUniversalUser();
    
    String getUserProperty( String propKey );
    
    void setUserProperty( String propKey, String value );
    
    Map<String, String> getUserProperties();
    
    String getModuleId();
    
    boolean isModified(UniversalUser oldRecord, UniversalUser newRecord);
}
