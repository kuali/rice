/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.web.session;

/**
 * Represents a user's Authentication.  An Authentication reprents the granted
 * authority of the user within the Workflow system or the enterprise.  The
 * authority is identified by a String value which should be unique for the
 * particular type of authentication. 
 * 
 * TODO This is a first cut at this interface, perhaps we should look at
 * something like Acegi security for handling some of this authentication
 * stuff?
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface Authentication extends java.io.Serializable {

	public String getAuthority();
	
}
