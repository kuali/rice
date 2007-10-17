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
 * The exception thrown whenever an ActionTaken can not be procesed for some reason.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class InvalidActionTakenException extends WorkflowException {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1138782665042887434L;

public InvalidActionTakenException() {
    super();
  }

  public InvalidActionTakenException(String s) {
    super(s);
  }
  
  public InvalidActionTakenException(String message, Throwable throwable) {
      super(message, throwable);
  }
  
  public InvalidActionTakenException(Throwable throwable) {
      super(throwable);
  }
  
}





/*
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 * This file is part of the EDEN software package.
 * For license information, see the LICENSE file in the top level directory
 * of the EDEN source distribution.
 */
