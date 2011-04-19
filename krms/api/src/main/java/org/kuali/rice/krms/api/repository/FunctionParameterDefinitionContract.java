/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krms.api.repository;

import org.kuali.rice.core.api.mo.Versioned;

/**
 * Defines the contract for a function parameter definition.  A function
 * parameter definition helps to define the "signature" of a
 * {@link FunctionDefinitionContract} by defining the name and type of an
 * expected parameter to the function.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface FunctionParameterDefinitionContract extends Versioned {

	/**
	 * Returns the identifier for this parameter.  Should only return null if
	 * this parameter has not been persisted to the repository yet.  Each
	 * individual function parameter definition should have a unique identifier.
	 * 
	 * @return the id of this function parameter definition, or null if it has
	 * not yet been set
	 */
	String getId();
		
	/**
	 * Returns the name of this parameters.  All parameters have a name and this
	 * value can never be null or blank.  The parameter name must be unique
	 * within a given function definition.
	 * 
	 * @return the name of this function parameter definition
	 */
	String getName();
	
	/**
	 * Returns the description of this parameter.  The description is intended
	 * to provide more information about a parameter and it's appropriate
	 * usage.  The description is optional and will be null if a
	 * description is not defined.
	 * 
	 * @return the description of this function parameter definition, or null
	 * if this parameter has no description
	 */
	String getDescription();
	
	/**
	 * Returns the type of this function parameter.  This can be one of a set
	 * of "built-in" data types or a custom data type represented as a fully
	 * qualified java class name.  All parameters must have a valid type so
	 * this method should never return null or blank.
	 * 
	 * @return the type of this function parameter definition
	 */
	String getType();

}
