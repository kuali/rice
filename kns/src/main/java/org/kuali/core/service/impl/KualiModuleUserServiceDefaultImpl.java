package org.kuali.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.core.bo.user.KualiModuleUserBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;

public class KualiModuleUserServiceDefaultImpl extends KualiModuleUserServiceBaseImpl {
	
	private String moduleId;
	
	public KualiModuleUserServiceDefaultImpl(String moduleId) {
		this.moduleId = moduleId;
		List<String> properties = new ArrayList<String>();
        properties.add(PropertyConstants.ACTIVE);
        setPropertyList(properties);
	}
	
	public KualiModuleUser getModuleUser(UniversalUser universalUser) throws UserNotFoundException {
		KualiModuleUserBase user = new KualiModuleUserBase(this.moduleId, universalUser);
		user.setActive(true);
		return user;
	}

}