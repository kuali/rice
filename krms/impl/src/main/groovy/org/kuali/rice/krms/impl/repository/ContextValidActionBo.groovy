package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

public class ContextValidActionBo extends PersistableBusinessObjectBase {

	String id
	String contextId
	String actionTypeId
	
	Long versionNumber

    KrmsTypeBo actionType
} 