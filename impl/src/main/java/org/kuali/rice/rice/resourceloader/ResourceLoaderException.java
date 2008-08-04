/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.resourceloader;

import org.kuali.rice.exceptions.RiceRuntimeException;

public class ResourceLoaderException extends RiceRuntimeException {

	private static final long serialVersionUID = -9089140992612301469L;

	public ResourceLoaderException(String message) {
		super(message);
	}

	public ResourceLoaderException() {
		super();
	}

	public ResourceLoaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceLoaderException(Throwable cause) {
		super(cause);
	}

}
