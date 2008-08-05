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
package org.kuali.rice.kew.services;

import org.kuali.rice.kew.exception.WorkflowException;


/**
 * <p><Title> InconsistentDocElementStateException</p>
 * <p><Description> Mark when a DocElement is being set/loaded improperly</p>
 * <p><p><p>Copyright: Copyright (c) 2002</p>
 * <p><p>Company: UIS - Indiana University</p>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class InconsistentDocElementStateException extends WorkflowException {
  /**
	 * 
	 */
	private static final long serialVersionUID = 7558402317855310723L;

public InconsistentDocElementStateException(String s) {
    super(s);
  }
}
