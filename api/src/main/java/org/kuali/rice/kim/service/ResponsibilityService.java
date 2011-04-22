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
package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.jaxb.AttributeSetAdapter;
import org.kuali.rice.core.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityTemplateInfo;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

/**
 * 
 * This service provides operations for determining what responsibility actions
 * a principal has and for querying about responsibility data.
 * 
 * <p>A responsibility represents an action that a principal is requested to
 * take.  This is used for defining workflow actions (such as approve, 
 * acknowledge, fyi) that the principal has the responsibility to take.  The
 * workflow engine integrates with this service to provide
 * responsibility-driven routing.
 * 
 * <p>A responsibility is very similar to a permission in a couple of ways.
 * First of all, responsibilities are always granted to a role, never assigned
 * directly to a principal or group.  Furthermore, in a similar fashion to
 * permissions, a role has the concept of a responsibility template.  The
 * responsibility template specifies what additional responsibility details
 * need to be defined when the responsibility is created.
 * 
 * <p>This service provides read-only operations.  For write operations, see
 * {@link ResponsibilityUpdateService}.
 * 
 * @see ResponsibilityUpdateService
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = KIMWebServiceConstants.ResponsibilityService.WEB_SERVICE_NAME, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ResponsibilityService {

    // --------------------
    // Responsibility Methods
    // --------------------

    /**
     * Get the responsibility object with the given ID.
     */
    KimResponsibilityInfo getResponsibility(@WebParam(name="responsibilityId") String responsibilityId);
    
 	/** 
 	 * Return the responsibility object for the given unique combination of namespace,
 	 * component and responsibility name.
 	 */
    List<KimResponsibilityInfo> getResponsibilitiesByName( @WebParam(name="namespaceCode") String namespaceCode, 
    													   @WebParam(name="responsibilityName") String responsibilityName );
    
    KimResponsibilityTemplateInfo getResponsibilityTemplate( @WebParam(name="responsibilityTemplateId") String responsibilityTemplateId );

    KimResponsibilityTemplateInfo getResponsibilityTemplateByName( 
    		@WebParam(name="namespaceCode") String namespaceCode, 
			@WebParam(name="responsibilityTemplateName") String responsibilityTemplateName );
    
    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibility( @WebParam(name="principalId") String principalId, 
    						   @WebParam(name="namespaceCode") String namespaceCode, 
    						   @WebParam(name="responsibilityName") String responsibilityName, 
    						   @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification, 
    						   @WebParam(name="responsibilityDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet responsibilityDetails );

    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibilityByTemplateName( @WebParam(name="principalId") String principalId, 
    										 @WebParam(name="namespaceCode") String namespaceCode, 
    										 @WebParam(name="responsibilityTemplateName") String responsibilityTemplateName, 
    										 @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification, 
    										 @WebParam(name="responsibilityDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet responsibilityDetails );
    
   	List<ResponsibilityActionInfo> getResponsibilityActions( @WebParam(name="namespaceCode") String namespaceCode, 
   															 @WebParam(name="responsibilityName") String responsibilityName, 
   															 @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification, 
   															 @WebParam(name="responsibilityDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet responsibilityDetails);
   	
   	List<ResponsibilityActionInfo> getResponsibilityActionsByTemplateName( @WebParam(name="namespaceCode") String namespaceCode, 
   																		   @WebParam(name="responsibilityTemplateName") String responsibilityTemplateName,	
   																		   @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification, 
   																		   @WebParam(name="responsibilityDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet responsibilityDetails);
   	
    /**
     * Lets the system know (mainly for UI purposes) whether this responsibility expects RoleResponsibilityAction
     * records to be given at the assignment level or are global to the responsibility.  (I.e., they apply
     * to any member assigned to the responsibility.) 
     */
   	boolean areActionsAtAssignmentLevelById( @WebParam(name="responsibilityId") String responsibilityId );

    /**
     * Lets the system know (mainly for UI purposes) whether this responsibility expects RoleResponsibilityAction
     * records to be given at the assignment level or are global to the responsibility.  (I.e., they apply
     * to any member assigned to the responsibility.) 
     */
   	boolean areActionsAtAssignmentLevel( @WebParam(name="responsibility") KimResponsibilityInfo responsibility );
   	
   	/**
   	 * Lookup responsibility objects.
   	 */
   	List<? extends KimResponsibilityInfo> lookupResponsibilityInfo( @WebParam(name="searchCriteria") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String,String> searchCriteria, 
   																	@WebParam(name="unbounded") boolean unbounded );
   	
   	/**
   	 * Get the role IDs associated with the given responsibility.
   	 */
   	List<String> getRoleIdsForResponsibility( @WebParam(name="responsibility") KimResponsibilityInfo responsibility, 
   											  @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );
}
