/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.exception;


/**
 * Thrown whenever a problem with a workgroup exists, like one is execpected but not found.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InvalidWorkgroupException extends WorkflowException {
  /**
	 * 
	 */
	private static final long serialVersionUID = -2670612945753672923L;

public InvalidWorkgroupException() {
    super();
  }

  public InvalidWorkgroupException(String s) {
    super(s);
  }
}
