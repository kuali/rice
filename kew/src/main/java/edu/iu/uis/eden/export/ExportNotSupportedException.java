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
package edu.iu.uis.eden.export;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * Exception thrown when an invalid export is attempted.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ExportNotSupportedException extends WorkflowRuntimeException {

	private static final long serialVersionUID = 1450235230768995894L;

	public ExportNotSupportedException(String message) {
        super(message);
    }

    public ExportNotSupportedException() {
        super();
    }

    public ExportNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportNotSupportedException(Throwable cause) {
        super(cause);
    }

}
