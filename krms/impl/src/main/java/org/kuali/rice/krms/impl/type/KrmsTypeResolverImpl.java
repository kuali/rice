/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.type;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krms.api.engine.EngineResourceUnavailableException;
import org.kuali.rice.krms.api.repository.ActionDefinition;
import org.kuali.rice.krms.api.repository.PropositionDefinition;
import org.kuali.rice.krms.api.repository.PropositionType;
import org.kuali.rice.krms.api.repository.RepositoryDataException;
import org.kuali.rice.krms.api.repository.TermResolverDefinition;
import org.kuali.rice.krms.api.type.KrmsTypeDefinition;
import org.kuali.rice.krms.framework.type.ActionTypeService;
import org.kuali.rice.krms.framework.type.PropositionTypeService;
import org.kuali.rice.krms.framework.type.TermResolverTypeService;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KrmsTypeResolverImpl implements KrmsTypeResolver {

	private PropositionTypeService defaultCompoundPropositionTypeService;
	private PropositionTypeService defaultSimplePropositionTypeService;
	
	@Override
	public PropositionTypeService getPropositionTypeService(
			PropositionDefinition propositionDefinition,
			KrmsTypeDefinition typeDefinition) {
		if (typeDefinition == null) {
			PropositionType propositionType = PropositionType.fromCode(propositionDefinition.getPropositionTypeCode());
			if (PropositionType.COMPOUND == propositionType) {
				return defaultCompoundPropositionTypeService;
			} else if (PropositionType.SIMPLE == propositionType) {
				return defaultSimplePropositionTypeService;
			}
			throw new RepositoryDataException("Proposition does not have a typeId defined and does not define a valid proposition type code.  Proposition id is: " + propositionDefinition.getPropId());
		}
		QName serviceName = QName.valueOf(typeDefinition.getServiceName());
		Object service = GlobalResourceLoader.getService(serviceName);
		if (service == null) {
			throw new EngineResourceUnavailableException("Failed to locate the PropositionTypeService with name: " + serviceName);
		}
		if (!(service instanceof PropositionTypeService)) {
			throw new EngineResourceUnavailableException("The service with name '" + serviceName + "' defined on typeId '" + propositionDefinition.getTypeId() + "' was not of type PropositionTypeService: " + service);
		}
		return (PropositionTypeService)service;
	}

	@Override
	public ActionTypeService getActionTypeService(ActionDefinition actionDefinition, KrmsTypeDefinition typeDefinition) {
		QName serviceName = QName.valueOf(typeDefinition.getServiceName());
		Object service = GlobalResourceLoader.getService(serviceName);
		if (service == null) {
			throw new EngineResourceUnavailableException("Failed to locate the ActionTypeService with name: " + serviceName);
		}
		if (!(service instanceof ActionTypeService)) {
			throw new EngineResourceUnavailableException("The service with name '" + serviceName + "' defined on typeId '" + actionDefinition.getTypeId() + "' was not of type ActionTypeService: " + service);
		}
		return (ActionTypeService)service;
	}
	
	@Override
	public TermResolverTypeService getTermResolverTypeService(TermResolverDefinition termResolverDefintion, KrmsTypeDefinition typeDefinition) {
		QName serviceName = QName.valueOf(typeDefinition.getServiceName());
		Object service = GlobalResourceLoader.getService(serviceName);
		if (service == null) {
			throw new EngineResourceUnavailableException("Failed to locate the " + TermResolverTypeService.class.getSimpleName() + 
					" with name: " + serviceName);
		}
		if (!(service instanceof TermResolverTypeService)) {
			throw new EngineResourceUnavailableException("The service with name '" + serviceName + "' defined on typeId '" + termResolverDefintion.getTypeId() + 
					"' was not of type " + TermResolverTypeService.class.getSimpleName() + ": " + service);
		}
		return (TermResolverTypeService)service;
	}
	
	public void setDefaultCompoundPropositionTypeService(PropositionTypeService defaultCompoundPropositionTypeService) {
		this.defaultCompoundPropositionTypeService = defaultCompoundPropositionTypeService;
	}

	public void setDefaultSimplePropositionTypeService(PropositionTypeService defaultSimplePropositionTypeService) {
		this.defaultSimplePropositionTypeService = defaultSimplePropositionTypeService;
	}
	
}
