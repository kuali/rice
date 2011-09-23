package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

public class ContextValidTermBo extends PersistableBusinessObjectBase {

	String id
	String contextId
	String termSpecificationId
	
	Boolean prereq

    TermSpecificationBo termSpecification
}