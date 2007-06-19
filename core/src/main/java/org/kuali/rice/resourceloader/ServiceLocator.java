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
package org.kuali.rice.resourceloader;

import javax.xml.namespace.QName;

import org.kuali.rice.lifecycle.Lifecycle;

/**
 * Uses to locate services for the given service name.
 *
 * @author ewestfal
 */
public interface ServiceLocator extends Lifecycle {

	/**
	 * Fetches the service with the given name.
	 */
	public Object getService(QName qname);

	public String getContents(String indent, boolean servicePerLine);

}
