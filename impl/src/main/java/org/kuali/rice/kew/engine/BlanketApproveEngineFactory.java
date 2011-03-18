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
package org.kuali.rice.kew.engine;


/**
 * A factory which can be used to construct a new BlanketApproveEngine.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface BlanketApproveEngineFactory {

	/**
	 * Constructs a new BlanketApproveEngine using the given orchestration configuration.
	 * Also receives a directive on whether or not post processing should be run during
	 * blanket approval orchestration.
	 * 
	 * <p>The resulting {@link BlanketApproveEngine} is not guaranteed to be thread safe.
	 * 
	 * @param config the orchestration configuration to use for the BlanketApproveEngine
	 * @param runPostProcessor indicates whether or not the document's PostProcessor should
	 * be executed during blanket approval orchestration.
	 * @return the initialized blanket approve engine
	 */
	public BlanketApproveEngine newEngine(OrchestrationConfig config, boolean runPostProcessor);
	
	/**
	 * Constructs a new BlanketApproveEngine using the given orchestration configuration.
	 * Is equivalent to invoking {@link #newEngine(OrchestrationConfig, boolean)} passing
	 * "true" for runPostProcessor.
	 * 
	 * <p>The resulting {@link BlanketApproveEngine} is not guaranteed to be thread safe.
	 * 
	 * @param config the orchestration configuration to use for the BlanketApproveEngine
	 * @return the initialized blanket approve engine
	 */
	public BlanketApproveEngine newEngine(OrchestrationConfig config);
	
}
