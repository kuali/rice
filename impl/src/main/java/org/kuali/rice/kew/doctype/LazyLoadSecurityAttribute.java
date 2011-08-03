/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.kew.doctype;

import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.identity.Person;

/**
 * This implementation of SecurityAttribute allows for us to lazy load the underlying
 * SecurityAttribute implementation from the service bus.  This is to address an issue
 * with DocumentType XML import where it was attempting to load the security attribute
 * class during ingestion which can cause problems if that application isn't online and
 * available. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class LazyLoadSecurityAttribute implements SecurityAttribute {

	private static final long serialVersionUID = 8194757786570696656L;

	private String className;
	private String applicationId;
	
	public LazyLoadSecurityAttribute(String className, String applicationId) {
		this.className = className;
		this.applicationId = applicationId;
	}

	public Boolean docSearchAuthorized(Person currentUser, String docTypeName,
			String documentId, String initiatorPrincipalId) {
		return getSecurityAttribute().docSearchAuthorized(currentUser, docTypeName, documentId, initiatorPrincipalId);
	}

	public Boolean routeLogAuthorized(Person currentUser, String docTypeName,
			String documentId, String initiatorPrincipalId) {
		return getSecurityAttribute().routeLogAuthorized(currentUser, docTypeName, documentId, initiatorPrincipalId);
	}
	
	protected SecurityAttribute getSecurityAttribute() {
		ObjectDefinition objDef = new ObjectDefinition(className, applicationId);
		return (SecurityAttribute)GlobalResourceLoader.getObject(objDef);
	}

}
