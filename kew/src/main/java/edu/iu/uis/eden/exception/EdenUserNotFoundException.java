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
package edu.iu.uis.eden.exception;

/**
 * An exception which is thrown when a user cannot be found.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EdenUserNotFoundException extends WorkflowException {
	
	private static final long serialVersionUID = -7171012642086191459L;

	public EdenUserNotFoundException() {
		super();
	}

	public EdenUserNotFoundException(Throwable cause) {
		super(cause);
	}

	public EdenUserNotFoundException(String s) {
		super(s);
	}
  
	public EdenUserNotFoundException(String s, Throwable cause) {
		super(s, cause);
	}
  
}