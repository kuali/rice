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
	private transient SecurityAttribute delegate;
	
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
	
	protected synchronized SecurityAttribute getSecurityAttribute() {
		if (delegate != null) {
			return delegate;
		}
		ObjectDefinition objDef = new ObjectDefinition(className, serviceNamespace);
		this.delegate = (SecurityAttribute)GlobalResourceLoader.getObject(objDef);
		return delegate;
	}

}
