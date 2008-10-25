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
package org.kuali.rice.kns.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.authorization.AuthorizationType;
import org.kuali.rice.kns.bo.BusinessObject;
import org.springframework.beans.factory.InitializingBean;

/**
 * This interface defines service methods for modules.  
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface ModuleService extends InitializingBean {

	/**
	 * 
	 * This method returns the module configuration.
	 * 
	 * @return
	 */
	public ModuleConfiguration getModuleConfiguration();
	
	/**
	 * 
	 * This method determines whether this service is responsible for the business object class passed in, or not.
	 * 
	 * @param businessObjectClass
	 * @return
	 */
	public boolean isResponsibleFor(Class businessObjectClass);
	
	/**
	 * 
	 * This method determines whether this service is responsible for the given jobname, or not.
	 * 
	 * @param businessObjectClass
	 * @return
	 */
	public boolean isResponsibleForJob(String jobName);
	
	/**
	 * This method returns whether the person can access this module at all.
	 * 
	 * @param user
	 * @return
	 */
	public boolean canAccessModule( Person user );
	
	/**
	 * 
	 * This method determines whether the passed in user is authorized to access this module or not.
	 * 
	 * @param user
	 * @param authType
	 * @return
	 */
	public boolean isAuthorized(Person user, AuthorizationType authType);

	/**
	 * 
	 * This method returns the list of primary keys for the EBO.
	 * 
	 * @param businessObjectInterfaceClass
	 * @return
	 */
	public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass);
	
	/**
	 * 
	 * This method gets the business object dictionary entry for the passed in externalizable business object class.
	 * 
	 * @param businessObjectInterfaceClass
	 * @return
	 */
	public BusinessObjectEntry getExternalizableBusinessObjectDictionaryEntry(Class businessObjectInterfaceClass);
	
	/**
	 * 
	 * This method gets the externalizable business object, given its type and a map of primary keys and values
	 * 
	 * @param businessObjectInterfaceClass
	 * @param fieldValues
	 * @return
	 */
	public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues);

	/**
	 * 
	 * This method gets the list of externalizable business objects, given its type and a map of primary keys and values.
	 * 
	 * @param businessObjectInterfaceClass
	 * @param fieldValues
	 * @return
	 */
	public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(
			Class<T> businessObjectClass, Map<String, Object> fieldValues);
	
	/**
	 * 
	 * This method gets the list of externalizable business objects for lookup, given its type and a map of primary keys and values.
	 * 
	 * @param <T>
	 * @param businessObjectClass
	 * @param fieldValues
	 * @param unbounded
	 * @return
	 */
	public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(
			Class<T> businessObjectClass, Map<String, Object> fieldValues, boolean unbounded);
	
	/**
	 * This method returns a URL so that the inquiry framework may redirect a user to the appropriate (possibly external) website 
	 * at which to view inquiry information.
	 * 
	 * @param inquiryBusinessObjectClass a {@link ExternalizableBusinessObject} managed by this module
	 * @param parameters any inquiry parameters, and the primary key values of the inquiryBusinessObjectClass would be in here
	 * @return a URL where externalizable business object information may be viewed.
	 */
	public String getExternalizableBusinessObjectInquiryUrl(Class inquiryBusinessObjectClass, Map<String, String[]> parameters);

	/**
	 * 
	 * This method gets the lookup url for the given externalizable business object properties.
	 * 
	 * @param parameters
	 * @return
	 */
	public String getExternalizableBusinessObjectLookupUrl(Class inquiryBusinessObjectClass, Map<String, String> parameters);

	/**
	 * 
	 * This method retrieves the externalizable business object, if it is not already populated 
	 * with the matching primary key values.
	 * 
	 * @param businessObject
	 * @param currentInstanceExternalizableBO
	 * @param externalizableRelationshipName
	 * @return
	 */
	public <T extends ExternalizableBusinessObject> T retrieveExternalizableBusinessObjectIfNecessary(
			BusinessObject businessObject, T currentInstanceExternalizableBO, String externalizableRelationshipName);

	/**
	 * 
	 * This method retrieves a list of externalizable business objects given a business object, 
	 * name of the relationship between the business object and the externalizable business object, and
	 * the externalizable business object class.  
	 * 
	 * @param businessObject
	 * @param externalizableRelationshipName
	 * @param externalizableClazz
	 * @return
	 */
	public <T extends ExternalizableBusinessObject> List<T> retrieveExternalizableBusinessObjectsList(
			BusinessObject businessObject, String externalizableRelationshipName, Class<T> externalizableClazz);

	/**
	 * 
	 * This method determines whether or not a bo class is externalizable.
	 * 
	 * @param boClass
	 * @return
	 */
	public boolean isExternalizable(Class boClass);
	
	/**
	 * @param boClass
	 * @return
	 */
	public boolean isExternalizableBusinessObjectLookupable(Class boClass);
	
	/**
	 * @param boClass
	 * @return
	 */
	public boolean isExternalizableBusinessObjectInquirable(Class boClass);
	
	/**
	 * @param <T>
	 * @param boClass
	 * @return
	 */
	public <T extends ExternalizableBusinessObject> T createNewObjectFromExternalizableClass(Class<T> boClass);
	
	/**
	 * For a given ExternalizableBusinessObject interface, return the implementation class provided by this module.
	 */
	public <E extends ExternalizableBusinessObject> Class<E> getExternalizableBusinessObjectImplementation(Class<E> externalizableBusinessObjectInterface);
}

