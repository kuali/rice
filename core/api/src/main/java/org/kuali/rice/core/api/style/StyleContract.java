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
package org.kuali.rice.core.api.style;

import org.kuali.rice.core.api.mo.GloballyUnique;
import org.kuali.rice.core.api.mo.Versioned;

/**
 * This is the contract for a Style.  A style represents a stylesheet that is used for transforming data from
 * one format to another (currently on XSL is supported).
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface StyleContract extends Versioned, GloballyUnique {

	/**
	 * Returns the identifier of this style.  Should only return null if this
	 * style has not been persisted to a data repository yet.  Each
	 * individual style should have a unique identifier.
	 * 
	 * @return the id of this style, or null if it has not yet been set
	 */
	Long getStyleId();
	
	/**
	 * Returns the name of this style.  All styles have a name and this value
	 * can never be null or blank.  The name must be unique within the entire
	 * repository of existing styles.
	 * 
	 * @return the name of this style
	 */
	String getName();
	
	/**
	 * Returns the XML definition of this style as a String.
	 * 
	 * @return the xml definition of this style
	 */
	String getXmlContent();
	
	/**
	 * Returns whether or not this style is active.
	 * 
	 * @return true if this style is active, false otherwise
	 */
	boolean isActive();
	
		
}
