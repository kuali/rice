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
package org.kuali.rice.kim.bo.role;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.types.KimTypeService;

/**
 * This is a service interface that must be used for a service related to a role type.
 * 
 * Is it used to interpret the qualifiers which may be attached.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimRoleTypeService extends KimTypeService {

                
    /** Return whether a role assignment with the given qualifier is applicable for the given qualification. 
     * 
     * For example, the qualifier for a role could be as follows:
     *   chartOfAccountsCode = BL
     *   organizationCode = ARSC
     *   descendsHierarchy = true
     *   
     * The qualification could be:
     *   chartOfAccountsCode = BL
     *   organizationCode = PSY    (reports to BL-ARSC)
     *   
     * This method would return true for this set of arguments.  This would require a query of 
     * the KFS organization hierarchy, so an implementation of this sort must be done by
     * a service which lives within KFS and will be called remotely by KIM.
     */
    boolean doesRoleQualifierMatchQualification( Map<String,String> qualification, Map<String,String> roleQualifier );

    /** Same as {@link #doesRoleQualifierMatchQualification(Map, Map)} except that it takes a list of qualifiers to check.
     */
    boolean doRoleQualifiersMatchQualification( Map<String,String> qualification, List<Map<String,String>> roleQualifierList );

    /**
     * This method would return all qualifications that the given qualification implies. (down)
     */
    List<Map<String,String>> getAllImpliedQualifications( Map<String,String> qualification );

    /**
     * This method would return all qualifications that imply this qualification. (up)
     * 
     * TODO: 
     * Allowing?
     * Allowed?
     * Granting?
     */
    List<Map<String,String>> getAllImplyingQualifications( Map<String,String> qualification );
    // TODO: need list versions of the implyed/ing methods?
    
    /** 
     * Return a list of attribute names that will be accepted by this role type.  They
     * are either understood directly, or can be translated by this service into that
     * required. 
     */
    List<String> getAcceptedQualificationAttributeNames();
    
    /**
     * Given a list of attribute names, determine whether this service can convert that set of parameters.
     */
    boolean supportsQualificationAttributes( List<String> attributeNames );
    
    /**
     * Convert a set of attributes that need to be converted.  For example,
     * this method could take [chart=BL,org=PSY] and return [campus=BLOOMINGTON]
     * if this role was based on the campus and the role assigned to it was based 
     * on organization.
     */
    Map<String,String> convertQualificationAttributesToRequired( Map<String,String> qualificationAttributes );
    
}
