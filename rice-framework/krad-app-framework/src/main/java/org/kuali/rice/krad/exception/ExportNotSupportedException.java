/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.exception;

import org.kuali.rice.kew.api.WorkflowRuntimeException;

/**
 * Exception thrown when an invalid export is attempted.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
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
