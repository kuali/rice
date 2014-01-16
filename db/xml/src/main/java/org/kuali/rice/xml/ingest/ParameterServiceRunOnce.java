/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.xml.ingest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.apache.commons.lang3.StringUtils;
import org.kuali.common.util.log.LoggerUtils;
import org.kuali.common.util.runonce.smart.RunOnce;
import org.kuali.common.util.runonce.smart.RunOnceState;
import org.kuali.rice.core.api.util.Truth;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.ParameterType;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.slf4j.Logger;

import com.google.common.base.Optional;

public final class ParameterServiceRunOnce implements RunOnce {

	private static final Logger logger = LoggerUtils.make();

	private final String applicationId;
	private final String namespace;
	private final String component;
	private final String name;
	private final Optional<String> description;
	private final boolean runOnMissingParameter;

	//
	private boolean initialized = false;
	private boolean runonce = false;
	private ParameterService service;

	private static final String CONFIGURATION_PARAMETER_TYPE = "CONFG";
	private static final String YES = "Y";

	@Override
	public synchronized void initialize() {
		checkState(!initialized, "Already initialized");
		this.service = CoreFrameworkServiceLocator.getParameterService();
		Optional<Parameter> parameter = Optional.fromNullable(service.getParameter(namespace, component, name));
		if (!parameter.isPresent() && runOnMissingParameter) {
			parameter = Optional.of(createParameter());
		}
		this.runonce = isRunOnce(parameter);
		showConfig(parameter);
		this.initialized = true;
	}

	@Override
	public synchronized boolean isTrue() {
		checkState(initialized, "Not initialized");
		return runonce;
	}

	@Override
	public synchronized void changeState(RunOnceState state) {
		// Ensure things are as they should be
		checkState(initialized, "Not initialized");
		checkNotNull(state, "'state' cannot be null");

		// Get the existing parameter
		Parameter existingParameter = service.getParameter(namespace, component, name);

		// Can't change the state of a non-existent parameter
		// The isRunOnce() method called during initialization cannot return true unless a parameter exists and it's value is set to 'Y'
		checkNotNull(existingParameter, "'existingParameter' cannot be null");

		// Update the parameter
		logger.info("Updating parameter: [{}]", name);
		Parameter.Builder builder = Parameter.Builder.create(existingParameter);
		builder.setValue(state.name());
		Parameter updatedParameter = service.updateParameter(builder.build());

		// This must always return false here
		this.runonce = isRunOnce(updatedParameter);
		checkState(!isTrue(), "isTrue() must return false");

		// Emit a log message indicating the change in state
		logger.info("Transitioned RunOnce to - [{}]", updatedParameter.getValue());
	}

	protected boolean isRunOnce(Optional<Parameter> parameter) {
		if (parameter.isPresent()) {
			return isRunOnce(parameter.get());
		} else {
			return false;
		}
	}

	protected boolean isRunOnce(Parameter parameter) {
		return Boolean.parseBoolean(Truth.strToBooleanIgnoreCase(parameter.getValue()) + "");
	}

	protected Parameter createParameter() {
		logger.info("Creating parameter: [{}]=[{}]", name, YES);
		Parameter.Builder builder = Parameter.Builder.create(applicationId, namespace, component, name, ParameterType.Builder.create(CONFIGURATION_PARAMETER_TYPE));
		builder.setValue(YES);
		if (description.isPresent()) {
			builder.setDescription(description.get());
		}
		return service.createParameter(builder.build());
	}

	protected void showConfig(Optional<Parameter> optional) {
		logger.info(String.format("Parameter Metadata: [%s:%s:%s]", applicationId, namespace, component));
		if (optional.isPresent()) {
			Parameter parameter = optional.get();
			logger.info("Parameter: [{}]=[{}]", name, parameter.getValue());
		} else {
			logger.info("Parameter [{}] does not exist", name);
		}
		logger.info("RunOnce: [{}]", runonce);
	}

	private ParameterServiceRunOnce(Builder builder) {
		this.applicationId = builder.applicationId;
		this.namespace = builder.namespace;
		this.component = builder.component;
		this.name = builder.name;
		this.description = builder.description;
		this.runOnMissingParameter = builder.runOnMissingParameter;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String applicationId;
		private String namespace;
		private String component;
		private String name;
		private Optional<String> description;
		private boolean runOnMissingParameter;

		public Builder runOnMissingParameter(boolean runOnMissingParameter) {
			this.runOnMissingParameter = runOnMissingParameter;
			return this;
		}

		public Builder applicationId(String applicationId) {
			this.applicationId = applicationId;
			return this;
		}

		public Builder namespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		public Builder component(String component) {
			this.component = component;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder description(String name) {
			this.description = Optional.fromNullable(name);
			return this;
		}

		public ParameterServiceRunOnce build() {
			ParameterServiceRunOnce instance = new ParameterServiceRunOnce(this);
			validate(instance);
			return instance;
		}

		private static void validate(ParameterServiceRunOnce instance) {
			checkArgument(!StringUtils.isBlank(instance.getApplicationId()), "'application' id cannot be null");
			checkArgument(!StringUtils.isBlank(instance.getNamespace()), "'namespace' cannot be null");
			checkArgument(!StringUtils.isBlank(instance.getComponent()), "'component' cannot be null");
			checkArgument(!StringUtils.isBlank(instance.getName()), "'name' cannot be null");
			checkNotNull(instance.description, "'description' cannot be null");
		}

		public String getApplicationId() {
			return applicationId;
		}

		public void setApplicationId(String applicationId) {
			this.applicationId = applicationId;
		}

		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}

		public String getComponent() {
			return component;
		}

		public void setComponent(String component) {
			this.component = component;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Optional<String> getDescription() {
			return description;
		}

		public void setDescription(Optional<String> description) {
			this.description = description;
		}

	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getComponent() {
		return component;
	}

	public String getName() {
		return name;
	}

	public Optional<String> getDescription() {
		return description;
	}

	public boolean isRunOnMissingParameter() {
		return runOnMissingParameter;
	}

}
