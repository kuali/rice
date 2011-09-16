/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.KIMPropertyConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeContract;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.framework.group.GroupEbo;
import org.kuali.rice.kim.framework.role.RoleEbo;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.service.impl.ModuleServiceBase;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.kuali.rice.core.api.criteria.PredicateFactory.and;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimModuleService extends ModuleServiceBase {

	private PersonService personService;
	private RoleService kimRoleService;
	private GroupService groupService;
	private KimTypeInfoService kimTypeInfoService;

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.krad.service.impl.ModuleServiceBase#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
		if ( Person.class.isAssignableFrom( businessObjectClass ) ) {
			if ( fieldValues.containsKey( KIMPropertyConstants.Person.PRINCIPAL_ID ) ) {
				return (T) getPersonService().getPerson( (String)fieldValues.get( KIMPropertyConstants.Person.PRINCIPAL_ID ) );
			} else if ( fieldValues.containsKey( KIMPropertyConstants.Person.PRINCIPAL_NAME ) ) {
				return (T) getPersonService().getPersonByPrincipalName( (String)fieldValues.get( KIMPropertyConstants.Person.PRINCIPAL_NAME ) );
			}
			// otherwise, fall through since critieria is not known
		} else if(Role.class.isAssignableFrom(businessObjectClass)){
			if(fieldValues.containsKey(KimConstants.PrimaryKeyConstants.ROLE_ID)){
				Role role = getKimRoleService().getRole((String)fieldValues.get(KimConstants.PrimaryKeyConstants.ROLE_ID));
				return (T) RoleEbo.from(role);
			}
		} else if(Group.class.isAssignableFrom(businessObjectClass)){
			if(fieldValues.containsKey(KimConstants.PrimaryKeyConstants.GROUP_ID)) {
                Group group = getGroupService().getGroup((String)fieldValues.get(KimConstants.PrimaryKeyConstants.GROUP_ID));
				return (T) GroupEbo.from(group);
			}
//		} else if(KimType.class.isAssignableFrom(dataObjectClass)){
//			if(fieldValues.containsKey(KimApiConstants.PrimaryKeyConstants.KIM_TYPE_ID)) {
//				return (T) getTypeInfoService().getKimType((String)fieldValues.get(KimApiConstants.PrimaryKeyConstants.KIM_TYPE_ID));
//			}
		}
		// otherwise, use the default implementation
		return super.getExternalizableBusinessObject( businessObjectClass, fieldValues );
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.krad.service.impl.ModuleServiceBase#getExternalizableBusinessObjectsList(java.lang.Class, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(
			Class<T> externalizableBusinessObjectClass, Map<String, Object> fieldValues) {
		// for Person objects (which are not real PersistableBOs) pull them through the person service

		if ( Person.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            //TODO Leave as is until Person is converted
			return (List)getPersonService().findPeople( (Map)fieldValues );
		}
        else if ( Role.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            List<Role> roles = getKimRoleService().getRolesSearchResults((Map)fieldValues);
            List<T> eboList = new ArrayList<T>();
            for (Role role : roles) { eboList.add((T) RoleEbo.from(role));}
			return eboList;
		} else if ( Group.class.isAssignableFrom(externalizableBusinessObjectClass) ) {
            QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();


            Set<Predicate> preds = new HashSet<Predicate>();
            for (String key : fieldValues.keySet()) {
                preds.add(equal(key, fieldValues.get(key)));
            }
            Predicate[] predicates = new Predicate[0];
            predicates = preds.toArray(predicates);
            Predicate p = and(predicates);
            builder.setPredicates(p);
			return (List)getGroupService().findGroups(builder.build());
		}
		// otherwise, use the default implementation
		return super.getExternalizableBusinessObjectsList( externalizableBusinessObjectClass, fieldValues );
	}

	/***
	 * @see org.kuali.rice.krad.service.ModuleService#getExternalizableBusinessObjectsListForLookup(java.lang.Class, java.util.Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(
			Class<T> externalizableBusinessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
		// for Person objects (which are not real PersistableBOs) pull them through the person service
		if ( Person.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
			return (List)getPersonService().findPeople( (Map)fieldValues, unbounded );
		} else if ( Role.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
			// FIXME: needs to send unbounded flag
			return (List)getKimRoleService().getRolesSearchResults((Map)fieldValues );
		}
		// otherwise, use the default implementation
		return super.getExternalizableBusinessObjectsListForLookup(externalizableBusinessObjectClass, fieldValues, unbounded);
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.krad.service.impl.ModuleServiceBase#listPrimaryKeyFieldNames(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
		// for Person objects (which are not real PersistableBOs) pull them through the person service
		if ( Person.class.isAssignableFrom( businessObjectInterfaceClass ) ) {
			return Collections.singletonList( KimConstants.PrimaryKeyConstants.PRINCIPAL_ID );
		} else if ( Role.class.isAssignableFrom( businessObjectInterfaceClass ) ) {
			return Collections.singletonList( KimConstants.PrimaryKeyConstants.ROLE_ID );
		} else if ( Group.class.isAssignableFrom( businessObjectInterfaceClass ) ) {
			return Collections.singletonList( KimConstants.PrimaryKeyConstants.GROUP_ID );
		} else if ( KimType.class.isAssignableFrom( businessObjectInterfaceClass ) ) {
			return Collections.singletonList( KimConstants.PrimaryKeyConstants.KIM_TYPE_ID );
		} else if ( KimTypeContract.class.isAssignableFrom(businessObjectInterfaceClass)) {
			return Collections.singletonList( KimConstants.PrimaryKeyConstants.KIM_TYPE_CODE );
		}

		// otherwise, use the default implementation
		return super.listPrimaryKeyFieldNames( businessObjectInterfaceClass );
	}

	@SuppressWarnings("unchecked")
	protected PersonService getPersonService() {
		if ( personService == null ) {
			personService = KimApiServiceLocator.getPersonService();
		}
		return personService;
	}

	protected RoleService getKimRoleService() {
		if ( kimRoleService == null ) {
			kimRoleService = KimApiServiceLocator.getRoleService();
		}
		return kimRoleService;
	}

	protected GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KimApiServiceLocator.getGroupService();
		}
		return groupService;
	}

	protected KimTypeInfoService getTypeInfoService() {
		if(kimTypeInfoService == null){
			kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
		}
		return kimTypeInfoService;
	}

	protected Properties getUrlParameters(String businessObjectClassAttribute, Map<String, String[]> parameters){
		Properties urlParameters = new Properties();
		for (String paramName : parameters.keySet()) {
			String[] parameterValues = parameters.get(paramName);
			if (parameterValues.length > 0) {
				urlParameters.put(paramName, parameterValues[0]);
			}
		}
		urlParameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObjectClassAttribute);
		try{
			Class inquiryBusinessObjectClass = Class.forName(businessObjectClassAttribute);
			if(Role.class.isAssignableFrom(inquiryBusinessObjectClass) || 
					Group.class.isAssignableFrom(inquiryBusinessObjectClass) || 
					Person.class.isAssignableFrom(inquiryBusinessObjectClass)) {
		        urlParameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.PARAM_MAINTENANCE_VIEW_MODE_INQUIRY);
			} else{
		        urlParameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.CONTINUE_WITH_INQUIRY_METHOD_TO_CALL);
			}
		} catch(Exception eix){
			urlParameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.CONTINUE_WITH_INQUIRY_METHOD_TO_CALL);
		}
        urlParameters.put(KRADConstants.PARAMETER_COMMAND, KEWConstants.INITIATE_COMMAND);
		return urlParameters;
	}

	@Override
	protected String getInquiryUrl(Class inquiryBusinessObjectClass){
		String inquiryUrl = KimCommonUtilsInternal.getKimBasePath();
		if (!inquiryUrl.endsWith("/")) {
			inquiryUrl = inquiryUrl + "/";
		}
		if(Role.class.isAssignableFrom(inquiryBusinessObjectClass)) {
			return inquiryUrl + KimConstants.KimUIConstants.KIM_ROLE_INQUIRY_ACTION;
		} else if(Group.class.isAssignableFrom(inquiryBusinessObjectClass)) {
			return inquiryUrl + KimConstants.KimUIConstants.KIM_GROUP_INQUIRY_ACTION;
		} else if(Person.class.isAssignableFrom(inquiryBusinessObjectClass)) {
			return inquiryUrl + KimConstants.KimUIConstants.KIM_PERSON_INQUIRY_ACTION;
		}
		return super.getInquiryUrl(inquiryBusinessObjectClass);
	}

}
