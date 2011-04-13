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
package org.kuali.rice.krms.impl.provider.repository;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krms.api.engine.EngineResourceUnavailableException;
import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.repository.ActionDefinition;
import org.kuali.rice.krms.api.type.KrmsTypeDefinition;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.type.ActionTypeService;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
final class LazyAction implements Action {

	private final ActionDefinition actionDefinition;
	private final KrmsTypeDefinition typeDefinition;
	
	private final Object mutex = new Object();
	
	// volatile for double-checked locking idiom
	private volatile Action action;
	
	LazyAction(ActionDefinition actionDefinition, KrmsTypeDefinition typeDefinition) {
		this.actionDefinition = actionDefinition;
		this.typeDefinition = typeDefinition;
		this.action = null;
	}
	
	@Override
	public void execute(ExecutionEnvironment environment) {
		getAction().execute(environment);
	}

	@Override
	public void executeSimulation(ExecutionEnvironment environment) {
		getAction().executeSimulation(environment);
	}
	
	/**
	 * Gets the action using a lazy double-checked locking mechanism as documented in Effective Java Item 71.
	 */
	private Action getAction() {
		Action localAction = action;
		if (localAction == null) {
			synchronized (mutex) {
				localAction = action;
				if (localAction == null) {
					action = localAction = constructAction();
				}
			}
		}
		return localAction;
	}
	
	private Action constructAction() {
		QName serviceName = QName.valueOf(typeDefinition.getServiceName());
		Object service = GlobalResourceLoader.getService(serviceName);
		if (service == null) {
			throw new EngineResourceUnavailableException("Failed to locate the ActionTypeService with name: " + serviceName);
		}
		if (!(service instanceof ActionTypeService)) {
			throw new EngineResourceUnavailableException("The service with name '" + serviceName + "' defined on typeId '" + actionDefinition.getTypeId() + "' was not of type ActionTypeService: " + service);
		}
		Action action = ((ActionTypeService)service).loadAction(actionDefinition);
		if (action == null) {
			action = new Action() {
				@Override
				public void execute(ExecutionEnvironment environment) {
				}
				public void executeSimulation(ExecutionEnvironment environment) {
				}
			};
		}
		return action;
	}

	
}
