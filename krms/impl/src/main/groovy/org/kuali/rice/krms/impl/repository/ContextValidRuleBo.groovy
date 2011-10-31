package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

public class ContextValidRuleBo extends PersistableBusinessObjectBase {

	String id
	String contextId
	String ruleTypeId
	
	Long versionNumber

    KrmsTypeBo ruleType
}