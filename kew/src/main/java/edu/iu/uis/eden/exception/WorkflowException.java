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
 * A generic checked exception thrown from KEW.  Acts as the superclass for all checked
 * exceptions in KEW.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkflowException extends Exception {

	private static final long serialVersionUID = 4377433160795253501L;

	public WorkflowException() {
		super();
	}

	public WorkflowException(String message) {
		super(message);
	}

	public WorkflowException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public WorkflowException(Throwable throwable) {
		super(throwable);
	}

}
