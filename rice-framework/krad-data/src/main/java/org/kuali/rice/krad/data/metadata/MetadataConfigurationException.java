/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.data.metadata;

/**
 * Metadata exception handling
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MetadataConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 3518029933066928419L;

    /**
    * Metadata exception.
    *
    * @return exception
    */
	public MetadataConfigurationException() {
		super();
	}

    /**
    * Metadata config exception.
    *
    * @param message error message
    * @return exception
    */
	public MetadataConfigurationException(String message) {
		super(message);
	}

    /**
    * Metadata config exception.
    *
    * @param message error message
    * @param cause error cause
    * @return exception
    */
	public MetadataConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

    /**
    * Metadata config exception.
    *
    * @param cause error cause
    * @return exception
    */
	public MetadataConfigurationException(Throwable cause) {
		super(cause);
	}
}
