/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kew.engine;

import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.kew.engine.node.service.RouteNodeService;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.springframework.beans.factory.InitializingBean;

/**
 * A simple implementation of a {@link BlanketApproveEngineFactory}.  Intended to be wired up in Spring
 * and initialized as part of the Spring lifecycle.
 * 
 * <p>In order for this factory to be properly constructed, all three service dependencies must be
 * injected into it.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BlanketApproveEngineFactoryImpl implements BlanketApproveEngineFactory, InitializingBean {

	private RouteNodeService routeNodeService;
    private RouteHeaderService routeHeaderService;
    private ParameterService parameterService;
    
	/**
	 * Ensures that all dependencies were injected into this factory.
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 * @throws IllegalStateException if any of the required services are null
	 */
	@Override
	public void afterPropertiesSet() {
		if (routeNodeService == null) {
			throw new IllegalStateException("routeNodeService not properly injected, was null.");
		}
		if (routeHeaderService == null) {
			throw new IllegalStateException("routeHeaderService not properly injected, was null.");
		}
		if (parameterService == null) {
			throw new IllegalStateException("parameterService not properly injected, was null.");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.kuali.rice.kew.engine.BlanketApproveEngineFactory#newEngine(org.kuali.rice.kew.engine.OrchestrationConfig)
	 */
	@Override
	public BlanketApproveEngine newEngine(final OrchestrationConfig config, final boolean runPostProcessor) {
		BlanketApproveEngine engine = new BlanketApproveEngine(config);
		engine.setRouteNodeService(getRouteNodeService());
		engine.setRouteHeaderService(getRouteHeaderService());
		engine.setParameterService(getParameterService());
		engine.setRunPostProcessorLogic(runPostProcessor);
		return engine;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.kuali.rice.kew.engine.BlanketApproveEngineFactory#newEngine(org.kuali.rice.kew.engine.OrchestrationConfig)
	 */
	@Override
	public BlanketApproveEngine newEngine(final OrchestrationConfig config) {
		return newEngine(config, true);
	}

	public void setRouteNodeService(RouteNodeService routeNodeService) {
		this.routeNodeService = routeNodeService;
	}

	public void setRouteHeaderService(RouteHeaderService routeHeaderService) {
		this.routeHeaderService = routeHeaderService;
	}

	public void setParameterService(
            ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	protected final RouteNodeService getRouteNodeService() {
		return this.routeNodeService;
	}

	protected final RouteHeaderService getRouteHeaderService() {
		return this.routeHeaderService;
	}

	protected final ParameterService getParameterService() {
		return this.parameterService;
	}

}
