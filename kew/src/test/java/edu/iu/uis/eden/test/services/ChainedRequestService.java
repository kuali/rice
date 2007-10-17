/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.test.services;

/**
 * A simple service which we can use to effectively "touch" the servers in a system
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ChainedRequestService {

	/**
	 * Should take the given String value and append the message entity of the current server onto the end
	 * with a comma in between.  Then it should forward the call of to another server.
	 */
	public String sendRequest(String value);
	
}
