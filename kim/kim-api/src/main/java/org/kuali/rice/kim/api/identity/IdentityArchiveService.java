/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.api.identity;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * 
 * This service archives EntityDefault.  It's purpose is to provide long term 
 * storage for basic identity data that may be removed from the IdentityService implementation's
 * backing store.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = "IdentityArchiveServiceSoap", targetNamespace = KimConstants.Namespaces.KIM_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface IdentityArchiveService {

    /**
     * Gets a {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} with an id from the archive.
     * {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} is a condensed version of {@link org.kuali.rice.kim.api.identity.entity.Entity} that contains
     * default values of its subclasses
     *
     * <p>
     *   This method will return null if the Entity does not exist.
     * </p>
     *
     * @param id the unique id to retrieve the entity by. cannot be null.
     * @return a {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} or null
     * @throws IllegalArgumentException if the id is blank
     */
    @WebMethod(operationName = "getEntityDefaultFromArchive")
    @WebResult(name = "entityDefault")
    @Cacheable(value= EntityDefault.Cache.NAME + "{Archive}", key="'id=' + #id")
    EntityDefault getEntityDefaultFromArchive(@WebParam(name = "id") String id ) throws RiceIllegalArgumentException;

	/**
     * Gets a {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} with an principalId from the archive.
     * {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} is a condensed version of {@link org.kuali.rice.kim.api.identity.entity.Entity} that contains
     * default values of its subclasses
     *
     * <p>
     *   This method will return null if the Entity does not exist.
     * </p>
     *
     * @param principalId the unique principalId to retrieve the entity by. cannot be null.
     * @return a {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} or null
     * @throws IllegalArgumentException if the principalId is blank
     */
    @WebMethod(operationName = "getEntityDefaultFromArchiveByPrincipalId")
    @WebResult(name = "entityDefault")
    @Cacheable(value= EntityDefault.Cache.NAME + "{Archive}", key="'principalId=' + #principalId")
    EntityDefault getEntityDefaultFromArchiveByPrincipalId(@WebParam(name = "principalId") String principalId) throws RiceIllegalArgumentException;

	/**
     * Gets a {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} with an principalName from the archive.
     * {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} is a condensed version of {@link org.kuali.rice.kim.api.identity.entity.Entity} that contains
     * default values of its subclasses
     *
     * <p>
     *   This method will return null if the Entity does not exist.
     * </p>
     *
     * @param principalName the unique principalName to retrieve the entity by. cannot be null.
     * @return a {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} or null
     * @throws IllegalArgumentException if the principalName is blank
     */
    @WebMethod(operationName = "getEntityDefaultFromArchiveByPrincipalName")
    @WebResult(name = "entityDefault")
    @Cacheable(value= EntityDefault.Cache.NAME + "{Archive}", key="'principalName=' + #principalName")
	EntityDefault getEntityDefaultFromArchiveByPrincipalName(@WebParam(name = "principalName") String principalName) throws RiceIllegalArgumentException;
	
	/**
     * Saves a {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} to the archive.
     * {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} is a condensed version of {@link org.kuali.rice.kim.api.identity.entity.Entity} that contains
     * default values of its subclasses
     *
     * <p>
     *   This method will return the saved EntityDefault object
     * </p>
     *
     * @param entityDefault the unique principalName to retrieve the entity by. cannot be null.
     * @return a {@link org.kuali.rice.kim.api.identity.entity.EntityDefault} or null
     * @throws IllegalArgumentException if the entityDefault is null
     */
    //TODO: this should probably return some kind of Future<EntityDefault> if we can find a way to remote that
    @WebMethod(operationName = "saveEntityDefaultToArchive")
    @CacheEvict(value= EntityDefault.Cache.NAME + "{Archive}", allEntries = true)
	void saveEntityDefaultToArchive(@WebParam(name = "entityDefault") EntityDefault entityDefault) throws RiceIllegalArgumentException;
	
}
