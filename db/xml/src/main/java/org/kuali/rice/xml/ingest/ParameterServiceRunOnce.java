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

/**
 * Locates workflow XML documents available on the classpath and ingests them.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ParameterServiceRunOnce implements RunOnce {

	private static final Logger logger = LoggerUtils.make();

    private static final String CONFIGURATION_PARAMETER_TYPE = "CONFG";
    private static final String YES = "Y";

    private ParameterService parameterService;
	private final String applicationId;
	private final String namespace;
	private final String component;
	private final String name;
	private final Optional<String> description;

    private final boolean runOnMissingParameter;

	private boolean initialized;
	private boolean runonce;

    /**
     * {@inheritDoc}
     */
	@Override
	public synchronized void initialize() {
		checkState(!initialized, "Already initialized");

		parameterService = CoreFrameworkServiceLocator.getParameterService();

		Optional<Parameter> parameter = Optional.fromNullable(parameterService.getParameter(namespace, component, name));
		if (!parameter.isPresent() && runOnMissingParameter) {
			parameter = Optional.of(createParameter());
		}
		runonce = isRunOnce(parameter);
		showConfig(parameter);

		initialized = true;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public synchronized boolean isTrue() {
		checkState(initialized, "Not initialized");

        return runonce;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public synchronized void changeState(RunOnceState state) {
		// Ensure things are as they should be
		checkState(initialized, "Not initialized");
		checkNotNull(state, "'state' cannot be null");

		// Get the existing parameter
		Parameter existingParameter = parameterService.getParameter(namespace, component, name);

		// Can't change the state of a non-existent parameter
		// The isRunOnce() method called during initialization cannot return true unless a parameter exists and it's value is set to 'Y'
		checkNotNull(existingParameter, "'existingParameter' cannot be null");

		// Update the parameter
		logger.info("Updating parameter: [{}]", name);
		Parameter.Builder builder = Parameter.Builder.create(existingParameter);
		builder.setValue(state.name());
		Parameter updatedParameter = parameterService.updateParameter(builder.build());

		// This must always return false here
		runonce = isRunOnce(updatedParameter);
		checkState(!isTrue(), "isTrue() must return false");

		// Emit a log message indicating the change in state
		logger.info("Transitioned RunOnce to - [{}]", updatedParameter.getValue());
	}

	private boolean isRunOnce(Optional<Parameter> parameter) {
		return parameter.isPresent() && isRunOnce(parameter.get());
	}

	private boolean isRunOnce(Parameter parameter) {
		return Truth.strToBooleanIgnoreCase(parameter.getValue(), Boolean.FALSE).booleanValue();
	}

	private Parameter createParameter() {
		logger.info("Creating parameter: [{}]=[{}]", name, YES);
        ParameterType.Builder parameterTypeBuilder = ParameterType.Builder.create(CONFIGURATION_PARAMETER_TYPE);
		Parameter.Builder parameterBuilder = Parameter.Builder.create(applicationId, namespace, component, name, parameterTypeBuilder);
        parameterBuilder.setValue(YES);
		if (description.isPresent()) {
            parameterBuilder.setDescription(description.get());
		}

		return parameterService.createParameter(parameterBuilder.build());
	}

	private void showConfig(Optional<Parameter> optional) {
		logger.info(String.format("Parameter Metadata: [%s:%s:%s]", applicationId, namespace, component));
		if (optional.isPresent()) {
			Parameter parameter = optional.get();
			logger.info("Parameter: [{}]=[{}]", name, parameter.getValue());
		} else {
			logger.info("Parameter [{}] does not exist", name);
		}
		logger.info("RunOnce: [{}]", Boolean.valueOf(runonce));
	}

    /**
     * Returns the application identifier of the parameter.
     *
     * @return the application identifier of the parameter
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Returns the namespace of the parameter.
     *
     * @return the namespace of the parameter
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Returns the component of the parameter.
     *
     * @return the component of the parameter
     */
    public String getComponent() {
        return component;
    }

    /**
     * Returns the name of the parameter.
     *
     * @return the name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the optional description.
     *
     * @return the optional description
     */
    public Optional<String> getDescription() {
        return description;
    }

	private ParameterServiceRunOnce(Builder builder) {
		this.applicationId = builder.applicationId;
		this.namespace = builder.namespace;
		this.component = builder.component;
		this.name = builder.name;
		this.description = builder.description;
		this.runOnMissingParameter = builder.runOnMissingParameter;
	}

    /**
     * Returns the builder for this {@code ParameterServiceRunOnce}.
     *
     * @param applicationId the application identifier of the parameter
     * @param namespace namespace of the parameter
     * @param component component of the parameter
     * @param name name of the parameter
     *
     * @return the builder for this {@code ParameterServiceRunOnce}
     */
    public static Builder builder(String applicationId, String namespace, String component, String name) {
        return new Builder(applicationId, namespace, component, name);
    }

    /**
     * Builds this {@link ParameterServiceRunOnce}.
     */
	public static class Builder {

        // Required
		private String applicationId;
		private String namespace;
		private String component;
		private String name;

        // Optional
		private Optional<String> description = Optional.absent();
		private boolean runOnMissingParameter;

        /**
         * Builds the {@link ParameterServiceRunOnce}.
         *
         * @param applicationId the application identifier of the parameter
         * @param namespace namespace of the parameter
         * @param component component of the parameter
         * @param name name of the parameter
         */
        public Builder(String applicationId, String namespace, String component, String name) {
            this.applicationId = applicationId;
            this.namespace = namespace;
            this.component = component;
            this.name = name;
        }

        /**
         * Sets the description of the parameter.
         *
         * @param description the description to set
         *
         * @return this {@code Builder}
         */
		public Builder description(String description) {
			this.description = Optional.fromNullable(description);
			return this;
		}

        /**
         * Sets whether or not to add the parameter if it is missing.
         *
         * @param runOnMissingParameter whether or not to add the parameter if it is missing
         *
         * @return this {@code Builder}
         */
        public Builder runOnMissingParameter(boolean runOnMissingParameter) {
            this.runOnMissingParameter = runOnMissingParameter;
            return this;
        }

        /**
         * Builds the {@link ParameterServiceRunOnce}.
         *
         * @return the built {@link ParameterServiceRunOnce}
         */
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
			checkNotNull(instance.getDescription(), "'description' cannot be null");
		}

	}

}