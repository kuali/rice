/*
 * Copyright 2005-2009 The Kuali Foundation
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
package org.kuali.rice.kew.dto;

/**
 * Signal to the PostProcessor that the routeHeader is being deleted.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DeleteEventDTO extends DocumentEventDTO {

	private static final long serialVersionUID = -3875560393424293103L;

	public DeleteEventDTO() {
        super(DELETE_CHANGE);
    }
    
}
