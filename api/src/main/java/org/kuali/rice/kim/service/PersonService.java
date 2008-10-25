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
package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kns.bo.BusinessObject;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface PersonService<T extends Person> {

	/**
	 * Retrieve a single Person object by Principal ID.
	 */
	T getPerson( String principalId );
	
	/**
	 * Retrieve a person by an arbitrary external identifier.  This method could
	 * potentially return multiple results as there is no guarantee of uniqueness
	 * for external identifiers.
	 * 
	 * @param externalIdentifierTypeCode Type of external identifier to search for.
	 * @param externalId The external identifier.
	 * @return List of Person objects.
	 */
	List<T> getPersonByExternalIdentifier( String externalIdentifierTypeCode, String externalId );
	
	/**
	 * Gets a single Person by their principal name (user ID).
	 */
	T getPersonByPrincipalName( String principalName );
	
	/**
	 * Perform an unbounded search for person records.
	 */
	List<? extends Person> findPeople( Map<String, String> criteria );

	/**
	 * Perform a Person lookup.  If bounded, it will follow the configured KNS lookup limit.
	 */
	List<? extends Person> findPeople( Map<String, String> criteria, boolean unbounded );
	
	List<Person> getGroupMembersByGroupName(String namespace, String groupName); 
	
	/**
	 * Check whether the given person belongs to the given group.  This method will do deep inspection
	 * (through the group service) of contained workgroups and return true if the user is in any of them.
	 * 
	 * @return <b>true</b> if the user is in the group or sub groups.  *false* if any parameter
	 * is null or the group does not exist.
	 */
	boolean isMemberOfGroup( Person person, String namespaceCode, String groupName );

	/**
	 * Check whether the given person belongs to the given group.  This method will do deep inspection
	 * (through the group service) of contained workgroups and return true if the user is in any of them.
	 * 
	 * @return <b>true</b> if the user is in the group or sub groups.  *false* if any parameter
	 * is null or the group does not exist.
	 */
	boolean isMemberOfGroup( Person person, String groupId );
	
	/** Get all the groups to which a Person belongs within a given namespace.
	 *  If the namespaceCode is null, then all namespaces are searched.
	 *  
	 *  @return List of KimGroup objects.  List will be empty if the Person object 
	 *  is null or the person does not belong to any groups within the given namespace.
	 */
	List<? extends KimGroup> getPersonGroups( Person person, String namespaceCode );

	/** Get all the groups to which a Person belongs across all namespaces.
	 *  
	 *  @return List of KimGroup objects.  List will be empty if the Person object 
	 *  is null or the person does not belong to any groups.
	 */
	List<? extends KimGroup> getPersonGroups( Person person );
		
	/**
	 * Get the class object which points to the class used by the underlying implementation.
	 * 
	 * This can be used by implementors who may need to construct Person objects without wishing to bind their code
	 * to a specific implementation.
	 */
	Class<? extends Person> getPersonImplementationClass();
	
	/**
	 * Get the entityTypeCode that is associated with a Person.  This will determine
	 * where EntityType-related data is pulled from within the KimEntity object.
	 */
	String getPersonEntityTypeCode();
	
	/**
     * This method takes a map on its way to populate a business object and replaces all 
     * user identifiers with their corresponding universal users
	 */
	Map<String, String> resolvePrincipalNamesToPrincipalIds( BusinessObject businessObject, Map<String, String> fieldValues );
	
    /**
     * Compares the Principal ID passed in with that in the Person object.  If they are the same, it returns the
     * original object.  Otherwise, it pulls the Person from KIM based on the sourcePrincipalId.
     */
	Person updatePersonIfNecessary(String sourcePrincipalId, Person currentPerson );
	
	/**
	 * Checks whether the given set of search criteria contain any non-blank properties which need to be applied against
	 * a related Person object.  This would be used by the lookup service to determine if special steps need
	 * to be taken when performing a search.
	 */
	boolean hasPersonProperty(Class<? extends BusinessObject> businessObjectClass, Map<String,String> fieldValues);
	
	boolean canAccessAnyModule( Person person );
	
}
