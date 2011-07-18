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

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.api.common.assignee.Assignee;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.util.KimConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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

    /**
     * This will create a {@link Permission} exactly like the permission passed in.
     *
     * @param permission the permission to create
     * @return the id of the newly created object.  will never be null.
     * @throws IllegalArgumentException if the permission is null
     * @throws IllegalStateException if the permission is already existing in the system
     */
    @WebMethod(operationName="createPermission")
    @WebResult(name = "id")
    String createPermission(@WebParam(name = "permission") Permission permission)
            throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
     * This will update a {@link Permission}.
     *
     * @param permission the permission to update
     * @throws IllegalArgumentException if the permission is null
     * @throws IllegalStateException if the permission does not exist in the system
     */
    @WebMethod(operationName="updatePermission")
    void updatePermission(@WebParam(name = "permission") Permission permission)
            throws RiceIllegalArgumentException, RiceIllegalStateException;

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
    					   @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
                           @WebParam(name="permissionDetails") Map<String, String> permissionDetails );


    /**
     * Checks whether the given qualified permission is granted to the principal given
     * the passed roleQualification.  If no roleQualification is passed (null or empty)
     * then this method behaves the same as {@link #hasPermission(String, String, String, Map<String, String>)}.
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
    @WebMethod(operationName = "isAuthorized")
    @WebResult(name = "isAuthorized")
    boolean isAuthorized( @WebParam(name="principalId") String principalId,
    					  @WebParam(name="namespaceCode") String namespaceCode,
    					  @WebParam(name="permissionName") String permissionName,
                          @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    					  @WebParam(name="permissionDetails") Map<String, String> permissionDetails,
                          @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    					  @WebParam(name="qualification") Map<String, String> qualification  );

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
                                         @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    									 @WebParam(name="permissionDetails") Map<String, String> permissionDetails );
    
    /**
     * Checks whether the given qualified permission is granted to the principal given
     * the passed roleQualification.  If no roleQualification is passed (null or empty)
     * then this method behaves the same as {@link #hasPermission(String, String, String, Map<String, String>)}.
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
                                        @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    									@WebParam(name="permissionDetails") Map<String, String> permissionDetails,
                                        @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    									@WebParam(name="qualification") Map<String, String> qualification  );
    
    
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
    @XmlElementWrapper(name = "assignees", required = true)
    @XmlElement(name = "assignee", required = false)
    @WebResult(name = "assignees")
    List<Assignee> getPermissionAssignees( @WebParam(name="namespaceCode") String namespaceCode,
    													 @WebParam(name="permissionName") String permissionName,
                                                         @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    													 @WebParam(name="permissionDetails") Map<String, String> permissionDetails,
                                                         @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    													 @WebParam(name="qualification") Map<String, String> qualification );

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
    @XmlElementWrapper(name = "assignees", required = true)
    @XmlElement(name = "assignee", required = false)
    @WebResult(name = "assignees")
    List<Assignee> getPermissionAssigneesForTemplateName( @WebParam(name="namespaceCode") String namespaceCode,
    																	@WebParam(name="permissionTemplateName") String permissionTemplateName,
                                                                        @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    																	@WebParam(name="permissionDetails") Map<String, String> permissionDetails,
                                                                        @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    																	@WebParam(name="qualification") Map<String, String> qualification );
    
    /**
     * Returns true if the given permission is defined on any Roles.
     */
    @WebMethod(operationName = "isPermissionDefined")
    @WebResult(name = "isPermissionDefined")
    boolean isPermissionDefined( @WebParam(name="namespaceCode") String namespaceCode,
    							 @WebParam(name="permissionName") String permissionName,
                                 @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    							 @WebParam(name="permissionDetails") Map<String, String> permissionDetails );
    
    /**
     * Returns true if the given permission template is defined on any Roles.
     */
    @WebMethod(operationName = "isPermissionDefinedForTemplateName")
    @WebResult(name = "isPermissionDefinedForTemplateName")
    boolean isPermissionDefinedForTemplateName( @WebParam(name="namespaceCode") String namespaceCode,
    											@WebParam(name="permissionTemplateName") String permissionTemplateName,
                                                @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    											@WebParam(name="permissionDetails") Map<String, String> permissionDetails );
    
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
    @XmlElementWrapper(name = "permissions", required = true)
    @XmlElement(name = "permission", required = false)
    @WebResult(name = "permissions")
    List<Permission> getAuthorizedPermissions( @WebParam(name="principalId") String principalId,
    												  @WebParam(name="namespaceCode") String namespaceCode,
    												  @WebParam(name="permissionName") String permissionName,
                                                      @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    												  @WebParam(name="permissionDetails") Map<String, String> permissionDetails,
                                                      @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    												  @WebParam(name="qualification") Map<String, String> qualification );

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
    @XmlElementWrapper(name = "permissions", required = true)
    @XmlElement(name = "permission", required = false)
    @WebResult(name = "permissions")
    List<Permission> getAuthorizedPermissionsByTemplateName( @WebParam(name="principalId") String principalId,
    																@WebParam(name="namespaceCode") String namespaceCode,
    																@WebParam(name="permissionTemplateName") String permissionTemplateName,
                                                                    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    																@WebParam(name="permissionDetails") Map<String, String> permissionDetails,
                                                                    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    																@WebParam(name="qualification") Map<String, String> qualification );

    // --------------------
    // Permission Data
    // --------------------

    /**
     * Get the permission object with the given ID.
     */
	@WebMethod(operationName = "getPermission")
    @WebResult(name = "permission")
    Permission getPermission( @WebParam(name="permissionId") String permissionId );
   
	/** 
	 * Return the permission object for the given unique combination of namespace,
	 * component and permission template name.
	 */
	@WebMethod(operationName = "getPermissionsByTemplateName")
    @WebResult(name = "permission")
    Permission getPermissionsByTemplateName( @WebParam(name="namespaceCode") String namespaceCode,
    													  @WebParam(name="permissionTemplateName") String permissionTemplateName );

	/** 
	 * Return the permission object for the given unique combination of namespace,
	 * component and permission name.
	 */
	@WebMethod(operationName = "getPermissionsByName")
    @WebResult(name = "permissions")
    Permission getPermissionsByName( @WebParam(name="namespaceCode") String namespaceCode,
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
    @XmlElementWrapper(name = "templates", required = true)
    @XmlElement(name = "template", required = false)
    @WebResult(name = "templates")
    List<Template> getAllTemplates();
	
    /**
     * Search for permissions using arbitrary search criteria.  JavaBeans property syntax 
     * should be used to reference the properties.
     * 
     * If the searchCriteria parameter is null or empty, an empty list will be returned.
     */
	@WebMethod(operationName = "lookupPermissions")
    @XmlElementWrapper(name = "permissions", required = true)
    @XmlElement(name = "permission", required = false)
    @WebResult(name = "permissions")
    List<Permission> lookupPermissions( @WebParam(name="searchCriteria") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String,String> searchCriteria,
    									@WebParam(name="unbounded") boolean unbounded);
    
    /**
     * Get the role IDs for the given permission.
     */
	@WebMethod(operationName = "getRoleIdsForPermission")
    @XmlElementWrapper(name = "roleIds", required = true)
    @XmlElement(name = "roleId", required = false)
    @WebResult(name = "roleIds")
    List<String> getRoleIdsForPermission( @WebParam(name="namespaceCode") String namespaceCode,
    									  @WebParam(name="permissionName") String permissionName,
                                          @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    									  @WebParam(name="permissionDetails") Map<String, String> permissionDetails);
    
    /**
     * Get the role IDs for the given list of permissions.
     */
	@WebMethod(operationName = "getRoleIdsForPermissions")
    @XmlElementWrapper(name = "roleIds", required = true)
    @XmlElement(name = "roleId", required = false)
    @WebResult(name = "roleIds")
    List<String> getRoleIdsForPermissions( @WebParam(name="permissions") List<Permission> permissions );
    
    /**
     * Returns the label of the permission detail for the given permissionId, kimType and attributeName. 
     */
	@WebMethod(operationName = "getPermissionDetailLabel")
    @WebResult(name = "permissionDetailLabel")
    String getPermissionDetailLabel( String permissionId, String kimTypeId, String attributeName);

    /**
     * Get the role IDs for the given permission.
     */
	@WebMethod(operationName = "getRoleIdsForPermissionId")
    @XmlElementWrapper(name = "roleIds", required = true)
    @XmlElement(name = "roleId", required = false)
    @WebResult(name = "roleIds")
    List<String> getRoleIdsForPermissionId(@WebParam(name = "permissionId") String permissionId);

    /**
     * Return the permission object for the given unique combination of namespace, component and permission name. Inactive
     * permissions are also returned
     */
	@WebMethod(operationName = "getPermissionsByNameIncludingInactive")
    @WebResult(name = "permissionsIncludingInactive")
    Permission getPermissionsByNameIncludingInactive(@WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "permissionName") String permissionName);
}
