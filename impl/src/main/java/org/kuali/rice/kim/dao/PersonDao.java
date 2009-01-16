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
package org.kuali.rice.kim.dao;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface PersonDao<T extends Person> {

	List<T> findPeople( Map<String,String> criteria, boolean unbounded );
	
	/**
	 * Get the class object which points to the class used by the underlying implementation.
	 * 
	 * This can be used by implementors who may need to construct Person objects without wishing to bind their code
	 * to a specific implementation.
	 */
	Class<? extends Person> getPersonImplementationClass();
	
	Map<String,String> convertPersonPropertiesToEntityProperties( Map<String,String> criteria );
	
	T convertEntityToPerson( KimEntity entity, KimPrincipal principal );
	
	void savePersonToCache( Person p );
	T getPersonFromCache( String principalId );
}
