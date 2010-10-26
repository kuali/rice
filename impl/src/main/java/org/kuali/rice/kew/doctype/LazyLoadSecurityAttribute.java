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

import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.bo.Person;

/**
 * This implementation of SecurityAttribute allows for us to lazy load the underlying
 * SecurityAttribute implementation from the service bus.  This is to address an issue
 * with DocumentType XML import where it was attempting to load the security attribute
 * class during ingestion which can cause problems if that application isn't online and
 * available. 
 * 
 * @author Eric Westfall
 *
 */
public class LazyLoadSecurityAttribute implements SecurityAttribute {

	private static final long serialVersionUID = 8194757786570696656L;

	private String className;
	private String serviceNamespace;
	
	public LazyLoadSecurityAttribute(String className, String serviceNamespace) {
		this.className = className;
		this.serviceNamespace = serviceNamespace;
	}

	public Boolean docSearchAuthorized(Person currentUser, String docTypeName,
			Long documentId, String initiatorPrincipalId) {
		return getSecurityAttribute().docSearchAuthorized(currentUser, docTypeName, documentId, initiatorPrincipalId);
	}

	public Boolean routeLogAuthorized(Person currentUser, String docTypeName,
			Long documentId, String initiatorPrincipalId) {
		return getSecurityAttribute().routeLogAuthorized(currentUser, docTypeName, documentId, initiatorPrincipalId);
	}
	
	protected SecurityAttribute getSecurityAttribute() {
		ObjectDefinition objDef = new ObjectDefinition(className, serviceNamespace);
		return (SecurityAttribute)GlobalResourceLoader.getObject(objDef);
	}

}
