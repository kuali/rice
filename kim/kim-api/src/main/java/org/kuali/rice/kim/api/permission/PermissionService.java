/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.api.permission;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.jaxb.AttributeSetAdapter;
import org.kuali.rice.core.util.jaxb.ImmutableListAdapter;
import org.kuali.rice.core.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.util.KimConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

/**
 * This service provides operations for evaluating permissions and querying for permission data.
 * 
 * <p>A permission is the ability to perform an action.  All permissions have a permission template.
 * Both permissions and permission templates are uniquely identified by a namespace code plus a name.
 * The permission template defines the course-grained permission and specifies what additional
 * permission details need to be collected on permissions that use that template.  For example, a
 * permission template might have a name of "Initiate Document" which requires a permission detail
 * specifying the document type that can be initiated.  A permission created from the "Initiate Document"
 * template would define the name of the specific Document Type that can be initiated as a permission
 * detail.
 * 
 * <p>The isAuthorized and isAuthorizedByTemplateName operations
 * on this service are used to execute authorization checks for a principal against a
 * permission.  Permissions are always assigned to roles (never directly to a principal or
 * group).  A particular principal will be authorized for a given permission if the permission
 * evaluates to true (according to the permission evaluation logic and based on any supplied
 * permission details) and that principal is assigned to a role which has been granted the permission.
 * 
 * <p>The actual logic for how permission evaluation logic is defined and executed is dependent upon
 * the permission service implementation.  However, it will typically be associated with the permission
 * template used on the permission. 
 * 
 * <p>This service provides read-only operations.  For write operations, see
 * {@link PermissionUpdateService}.
 * 
 * @see PermissionUpdateService
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "PermissionService", targetNamespace = KimConstants.Namespaces.KIM_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface PermissionService {
            
    // --------------------
    // Authorization Checks
    // --------------------
	
    /**
     * Checks whether the principal has been granted a permission matching the given details
     * without taking role qualifiers into account.
     * 
	 * This method should not be used for true authorization checks since a principal
	 * may only have this permission within a given context.  It could be used to
	 * identify that the user would have some permissions within a certain area.
	 * Later checks would identify exactly what permissions were granted.
	 * 
	 * It can also be used when the client application KNOWS that this is a role which
	 * is never qualified.
     */
    @WebMethod(operationName = "hasPermission")
    @WebResult(name = "hasPermission")
    boolean hasPermission( @WebParam(name="principalId") String principalId,
    					   @WebParam(name="namespaceCode") String namespaceCode,
    					   @WebParam(name="permissionName") String permissionName,
    					   @WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails );

    // TODO Deal with KimPermissionTypeService -> PermissionTypeService
    /**
     * Checks whether the given qualified permission is granted to the principal given
     * the passed roleQualification.  If no roleQualification is passed (null or empty)
     * then this method behaves the same as {@link #hasPermission(String, String, String, AttributeSet)}.
     * 
     * Each role assigned to the principal is checked for qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked first through
     * the role's type service.  Once it is determined that the principal has the role
     * in the given context (qualification), the permissions are examined.
     * 
     * Each permission is checked against the permissionDetails.  The PermissionTypeService
     * is called for each permission with the given permissionName to see if the 
     * permissionDetails matches its details.
     */
    // TODO Do AttributeSets still get passed in? Should PermissionAttributes be passed in instead?
    // AttributeSet permissionDetails -> PermissionAttribute permissionAttributes
    @WebMethod(operationName = "isAuthorized")
    @WebResult(name = "isAuthorized")
    boolean isAuthorized( @WebParam(name="principalId") String principalId,
    					  @WebParam(name="namespaceCode") String namespaceCode,
    					  @WebParam(name="permissionName") String permissionName,
    					  @WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
    					  @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification  );

    /**
     * Checks whether the principal has been granted a permission matching the given details
     * without taking role qualifiers into account.
     * 
	 * This method should not be used for true authorization checks since a principal
	 * may only have this permission within a given context.  It could be used to
	 * identify that the user would have some permissions within a certain area.
	 * Later checks would identify exactly what permissions were granted.
	 * 
	 * It can also be used when the client application KNOWS that this is a role which
	 * is never qualified.
     */
    @WebMethod(operationName = "hasPermissionByTemplateName")
    @WebResult(name = "hasPermission")
    boolean hasPermissionByTemplateName( @WebParam(name="principalId") String principalId,
    									 @WebParam(name="namespaceCode") String namespaceCode,
    									 @WebParam(name="permissionTemplateName") String permissionTemplateName,
    									 @WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails );
    
    /**
     * Checks whether the given qualified permission is granted to the principal given
     * the passed roleQualification.  If no roleQualification is passed (null or empty)
     * then this method behaves the same as {@link #hasPermission(String, String, String, AttributeSet)}.
     * 
     * Each role assigned to the principal is checked for qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked first through
     * the role's type service.  Once it is determined that the principal has the role
     * in the given context (qualification), the permissions are examined.
     * 
     * Each permission is checked against the permissionDetails.  The PermissionTypeService
     * is called for each permission with the given permissionName to see if the 
     * permissionDetails matches its details.
     */
    @WebMethod(operationName = "isAuthorizedByTemplateName")
    @WebResult(name = "isAuthorized")
    boolean isAuthorizedByTemplateName( @WebParam(name="principalId") String principalId,
    									@WebParam(name="namespaceCode") String namespaceCode,
    									@WebParam(name="permissionTemplateName") String permissionTemplateName,
    									@WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
    									@WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification  );
    
    
    /**
     * Get the list of principals/groups who have a given permission.  This also returns delegates
     * for the given principals/groups who also have this permission given the context in the
     * qualification parameter.
     * 
     * Each role assigned to the principal is checked for qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked first through
     * the role's type service.  Once it is determined that the principal has the role
     * in the given context (qualification), the permissions are examined.
     * 
     */
	@WebMethod(operationName = "getPermissionAssignees")
    @WebResult(name = "permissionAssignees")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    // TODO Modelize PermissionAssigneeInfo
    List<PermissionAssigneeInfo> getPermissionAssignees( @WebParam(name="namespaceCode") String namespaceCode,
    													 @WebParam(name="permissionName") String permissionName,
    													 @WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
    													 @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );

    /**
     * Get the list of principals/groups who have a given permission that match the given 
     * permission template and permission details.  This also returns delegates
     * for the given principals/groups who also have this permission given the context in the
     * qualification parameter.
     * 
     * Each role assigned to the principal is checked for qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked first through
     * the role's type service.  Once it is determined that the principal has the role
     * in the given context (qualification), the permissions are examined.
     * 
     */
	@WebMethod(operationName = "getPermissionAssigneesForTemplateName")
    @WebResult(name = "permissionAssigneesForTemplateName")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    // TODO Modelize PermissionAssigneeInfo
    List<PermissionAssigneeInfo> getPermissionAssigneesForTemplateName( @WebParam(name="namespaceCode") String namespaceCode,
    																	@WebParam(name="permissionTemplateName") String permissionTemplateName,
    																	@WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
    																	@WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );
    
    /**
     * Returns true if the given permission is defined on any Roles.
     */
    @WebMethod(operationName = "isPermissionDefined")
    @WebResult(name = "isPermissionDefined")
    boolean isPermissionDefined( @WebParam(name="namespaceCode") String namespaceCode,
    							 @WebParam(name="permissionName") String permissionName,
    							 @WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails );
    
    /**
     * Returns true if the given permission template is defined on any Roles.
     */
    @WebMethod(operationName = "isPermissionDefinedForTemplateName")
    @WebResult(name = "isPermissionDefinedForTemplateName")
    boolean isPermissionDefinedForTemplateName( @WebParam(name="namespaceCode") String namespaceCode,
    											@WebParam(name="permissionTemplateName") String permissionTemplateName,
    											@WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails );
    
    /**
     * Returns permissions (with their details) that are granted to the principal given
     * the passed qualification.  If no qualification is passed (null or empty)
     * then this method does not check any qualifications on the roles.
     * 
     * All permissions with the given name are checked against the permissionDetails.  
     * The PermissionTypeService is called for each permission to see if the 
     * permissionDetails matches its details.
     * 
     * An asterisk (*) as a value in any permissionDetails key-value pair will match any value.
     * This forms a way to provide a wildcard to obtain multiple permissions in one call.
     * 
     * After the permissions are determined, the roles that hold those permissions are determined.
     * Each role that matches between the principal and the permission objects is checked for 
     * qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked through
     * the role's type service. 
     * 
     */
	@WebMethod(operationName = "getAuthorizedPermissions")
    @WebResult(name = "authorizedPermissions")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    List<Permission> getAuthorizedPermissions( @WebParam(name="principalId") String principalId,
    												  @WebParam(name="namespaceCode") String namespaceCode,
    												  @WebParam(name="permissionName") String permissionName,
    												  @WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
    												  @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );

    /**
     * Returns permissions (with their details) that are granted to the principal given
     * the passed qualification.  If no qualification is passed (null or empty)
     * then this method does not check any qualifications on the roles.
     * 
     * All permissions with the given name are checked against the permissionDetails.  
     * The PermissionTypeService is called for each permission to see if the 
     * permissionDetails matches its details.
     * 
     * An asterisk (*) as a value in any permissionDetails key-value pair will match any value.
     * This forms a way to provide a wildcard to obtain multiple permissions in one call.
     * 
     * After the permissions are determined, the roles that hold those permissions are determined.
     * Each role that matches between the principal and the permission objects is checked for 
     * qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked through
     * the role's type service. 
     * 
     */
	@WebMethod(operationName = "getAuthorizedPermissionsByTemplateName")
    @WebResult(name = "authorizedPermissions")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    List<Permission> getAuthorizedPermissionsByTemplateName( @WebParam(name="principalId") String principalId,
    																@WebParam(name="namespaceCode") String namespaceCode,
    																@WebParam(name="permissionTemplateName") String permissionTemplateName,
    																@WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
    																@WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );

    // --------------------
    // Permission Data
    // --------------------

    /**
     * Get the permission object with the given ID.
     */
	@WebMethod(operationName = "getPermission")
    @WebResult(name = "permission")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    Permission getPermission( @WebParam(name="permissionId") String permissionId );
   
	/** 
	 * Return the permission object for the given unique combination of namespace,
	 * component and permission template name.
	 */
	@WebMethod(operationName = "getPermissionsByTemplateName")
    @WebResult(name = "permissions")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    List<Permission> getPermissionsByTemplateName( @WebParam(name="namespaceCode") String namespaceCode,
    													  @WebParam(name="permissionTemplateName") String permissionTemplateName );

	/** 
	 * Return the permission object for the given unique combination of namespace,
	 * component and permission name.
	 */
	@WebMethod(operationName = "getPermissionsByName")
    @WebResult(name = "permissions")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    List<Permission> getPermissionsByName( @WebParam(name="namespaceCode") String namespaceCode,
			    											  @WebParam(name="permissionName") String permissionName );
    
	/**
	 * 
	 * Return the Permission Template given the Template ID.
	 * 
	 * @param permissionTemplateId
	 * @return PermissionTemplate
	 */
	@WebMethod(operationName = "getPermissionTemplate")
    @WebResult(name = "permissionTemplate")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    Template getPermissionTemplate( @WebParam(name="permissionTemplateId") String permissionTemplateId );

	/**
	 * 
	 * Return the Permission Template given the Template Name and Namespace Code.
	 * 
	 * @param namespaceCode, permissionTemplateName
	 * @return PermissionTemplate
	 */
	@WebMethod(operationName = "getPermissionTemplateByName")
    @WebResult(name = "permissionTemplate")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    Template getPermissionTemplateByName( @WebParam(name="namespaceCode") String namespaceCode,
			  										@WebParam(name="permissionTemplateName") String permissionTemplateName );

	/**
	 * 
	 * Return all Permission Templates.
	 * 
	 * @param namespaceCode, permissionTemplateName
	 * @return PermissionTemplate
	 */
	@WebMethod(operationName = "getAllTemplates")
    @WebResult(name = "permissionTemplates")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    public List<Template> getAllTemplates();
	
    /**
     * Search for permissions using arbitrary search criteria.  JavaBeans property syntax 
     * should be used to reference the properties.
     * 
     * If the searchCriteria parameter is null or empty, an empty list will be returned.
     */
	@WebMethod(operationName = "lookupPermissions")
    @WebResult(name = "lookupPermissions")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)		
    List<Permission> lookupPermissions( @WebParam(name="searchCriteria") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String,String> searchCriteria,
    									@WebParam(name="unbounded") boolean unbounded);
    
    /**
     * Get the role IDs for the given permission.
     */
	@WebMethod(operationName = "getRoleIdsForPermission")
    @WebResult(name = "roleIdsForPermission")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    List<String> getRoleIdsForPermission( @WebParam(name="namespaceCode") String namespaceCode,
    									  @WebParam(name="permissionName") String permissionName,
    									  @WebParam(name="permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails);
    
    /**
     * Get the role IDs for the given list of permissions.
     */
	@WebMethod(operationName = "getRoleIdsForPermissions")
    @WebResult(name = "roleIdsForPermissions")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    // TODO Replace KimPermissionInfo with  List<Permission> as parameter?
    List<String> getRoleIdsForPermissions( @WebParam(name="permissions") List<KimPermissionInfo> permissions );
    
    /**
     * Returns the label of the permission detail for the given permissionId, kimType and attributeName. 
     */
	@WebMethod(operationName = "getPermissionDetailLabel")
    @WebResult(name = "permissionDetailLabel")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    public String getPermissionDetailLabel( String permissionId, String kimTypeId, String attributeName);

    /**
     * Get the role IDs for the given permission.
     */
	@WebMethod(operationName = "getRoleIdsForPermissionId")
    @WebResult(name = "roleIdsForPermissionId")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)
    List<String> getRoleIdsForPermissionId(@WebParam(name = "permissionId") String permissionId);

    /**
     * Return the permission object for the given unique combination of namespace, component and permission name. Inactive
     * permissions are also returned
     */
	@WebMethod(operationName = "getPermissionsByNameIncludingInactive")
    @WebResult(name = "permissionsIncludingInactive")
    @XmlJavaTypeAdapter(value = ImmutableListAdapter.class)	
    List<Permission> getPermissionsByNameIncludingInactive(@WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "permissionName") String permissionName);
}
