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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.service.impl.ModuleServiceBase;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimModuleService extends ModuleServiceBase {

	private PersonService<Person> personService; 
	private RoleService kimRoleService; 
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.impl.ModuleServiceBase#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
		if ( Person.class.isAssignableFrom( businessObjectClass ) ) {
			if ( fieldValues.containsKey( "principalId" ) ) {
				return (T) personService.getPerson( (String)fieldValues.get( "principalId" ) );
			} else if ( fieldValues.containsKey( "principalName" ) ) {
				return (T) personService.getPersonByPrincipalName( (String)fieldValues.get( "principalName" ) );
			}
			// otherwise, fall through since critieria is not known
		} else if ( Role.class.isAssignableFrom( businessObjectClass ) ) {
			System.out.println( "UNHANDLED EBO CALL in KimModuleService.getExternalizableBusinessObject() for Role");
		}
		// otherwise, use the default implementation
		return super.getExternalizableBusinessObject( businessObjectClass, fieldValues );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.impl.ModuleServiceBase#getExternalizableBusinessObjectsList(java.lang.Class, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(
			Class<T> externalizableBusinessObjectClass, Map<String, Object> fieldValues) {
		// for Person objects (which are not real PersistableBOs) pull them through the person service
		if ( Person.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
			return (List)personService.findPeople( (Map)fieldValues );
		} else if ( Role.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
			return (List)kimRoleService.getRolesSearchResults((Map)fieldValues );
		}
		// otherwise, use the default implementation
		return super.getExternalizableBusinessObjectsList( externalizableBusinessObjectClass, fieldValues );
	}

	/***
	 * @see org.kuali.rice.kns.service.ModuleService#getExternalizableBusinessObjectsListForLookup(java.lang.Class, java.util.Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(
			Class<T> externalizableBusinessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
		// for Person objects (which are not real PersistableBOs) pull them through the person service
		if ( Person.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
			return (List)personService.findPeople( (Map)fieldValues, unbounded );
		} else if ( Role.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
			// FIXME: needs to send unbounded flag
			return (List)kimRoleService.getRolesSearchResults((Map)fieldValues );
		}
		// otherwise, use the default implementation
		return super.getExternalizableBusinessObjectsListForLookup(externalizableBusinessObjectClass, fieldValues, unbounded);
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.impl.ModuleServiceBase#listPrimaryKeyFieldNames(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
		// for Person objects (which are not real PersistableBOs) pull them through the person service
		if ( Person.class.isAssignableFrom( businessObjectInterfaceClass ) ) {
			List<String> pkFields = new ArrayList<String>( 1 );
			pkFields.add( "principalId" );
			return pkFields;
		} else if ( Role.class.isAssignableFrom( businessObjectInterfaceClass ) ) {
			List<String> pkFields = new ArrayList<String>( 1 );
			pkFields.add( "roleId" );
			return pkFields;
		} 
		// otherwise, use the default implementation
		return super.listPrimaryKeyFieldNames( businessObjectInterfaceClass );
	}
	
	@SuppressWarnings("unchecked")
	public PersonService<Person> getPersonService() {
		if ( personService == null ) {
			personService = KIMServiceLocator.getPersonService();
		}
		return personService;
	}

	public RoleService getKimRoleService() {
		if ( kimRoleService == null ) {
			kimRoleService = KIMServiceLocator.getRoleManagementService();
		}
		return kimRoleService;
	}
}

