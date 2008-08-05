/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.config.ModuleConfigurer;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.config.logging.Log4jLifeCycle;
import org.kuali.rice.core.Core;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.workflow.ojb.OjbConfigurer;


/**
 * Configures the KEW Rice module.  KEW module initiation proceeds as follows:
 *
 * <ol>
 *   <li>Parse and load configuration for:</li>
 *     <ul>
 *       <li>Client Protocol</li>
 *       <li>Database</li>
 *	   </ul>
 *   </li>
 *   <li>Configure and startup KEW for "Thin Client" mode OR</li>
 *   <li>Configure and startup KEW for "Embedded Mode"</li>
 * </ol>
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KEWConfigurer extends ModuleConfigurer {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KEWConfigurer.class);

	public static final String KEW_DATASOURCE_OBJ = "org.kuali.workflow.datasource";
	public static final String KEW_DATASOURCE_JNDI = "org.kuali.workflow.datasource.jndi.location";

	private String clientProtocol;

	private DataSource dataSource;
	private String dataSourceJndiName;

	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		if (KEWConstants.WEBSERVICE_CLIENT_PROTOCOL.equals(Core.getCurrentContextConfig().getClientProtocol())) {
			lifecycles.add(createThinClientLifecycle());
		} else {
			if (isStandaloneServer()) {
				lifecycles.add(new Log4jLifeCycle());
			}
			lifecycles.add(new OjbConfigurer());
			lifecycles.add(createTempEmbeddedLifeCycle());
		}
		return lifecycles;
	}

	protected boolean isStandaloneServer() {
		return new Boolean(Core.getCurrentContextConfig().getProperty("kew.standalone.server")).booleanValue();
	}

	/**
	 * TODO Because a lot of our lifecycles live behind the embedded plugin and the KEWConfigurer does not, this is a simple
	 * measure to load these without having to deal with the removal of the embedded plugin right away.
	 */
	protected Lifecycle createTempEmbeddedLifeCycle() throws Exception {
		return (Lifecycle)Class.forName("edu.iu.uis.eden.core.TempEmbeddedLifeCycle").newInstance();
	}

	protected Lifecycle createThinClientLifecycle() throws Exception {
		return (Lifecycle)Class.forName("org.kuali.workflow.config.ThinClientLifecycle").newInstance();
	}

	public Config loadConfig(Config parentConfig) throws Exception {
		LOG.info("Starting configuration of KEW for message entity " + getMessageEntity(parentConfig));
		Config currentConfig = parseConfig(parentConfig);
		configureClientProtocol(currentConfig);
		configureDataSource(currentConfig);
		return currentConfig;
	}

	protected Config parseConfig(Config parentConfig) throws Exception {
		List<String> defaultConfigLocations = new ArrayList<String>();
		defaultConfigLocations.add(KEWConstants.DEFAULT_GLOBAL_CONFIG_LOCATION);
		defaultConfigLocations.add(KEWConstants.DEFAULT_APPLICATION_CONFIG_LOCATION);
		Config kewConfig = new SimpleConfig(defaultConfigLocations, parentConfig.getProperties());
		kewConfig.parseConfig();
		mergeDefaultsIntoParentConfig(parentConfig, kewConfig);
		return parentConfig;
	}

	/**
	 * Merges any default configuration into the parent config.  If a property appears in both
	 * places, precedence is given to the parentConfig.  This allows for our defaults to not
	 * override any property which has already been defined.
	 */
	protected void mergeDefaultsIntoParentConfig(Config parentConfig, Config defaultConfig) {
		for (Object keyObj : defaultConfig.getProperties().keySet()) {
			String key = (String)keyObj;
			if (!parentConfig.getProperties().containsKey(key)) {
				parentConfig.getProperties().put(key, defaultConfig.getProperty(key));
			}
		}
	}

	protected String getMessageEntity(Config config) {
		if (StringUtils.isBlank(config.getMessageEntity())) {
			throw new ConfigurationException("The 'message.entity' property was not properly configured.");
		}
		return config.getMessageEntity();
	}

	protected void configureClientProtocol(Config config) {
		if (StringUtils.isBlank(clientProtocol)) {
			clientProtocol = config.getClientProtocol();
			if (clientProtocol == null) {
				clientProtocol = KEWConstants.WEBSERVICE_CLIENT_PROTOCOL;
			}
		}
		// from a client, LOCAL protocol is equivalent to EMBEDDED
		// TODO this was messing up the tests were LOCAL was actually being used
		/*if (KEWConstants.LOCAL_CLIENT_PROTOCOL.equals(clientProtocol)) {
			clientProtocol = KEWConstants.EMBEDDED_CLIENT_PROTOCOL;
		}*/
		if (!KEWConstants.CLIENT_PROTOCOLS.contains(clientProtocol)) {
			throw new ConfigurationException("Invalid client protocol specified '" + clientProtocol + "'.");
		}
		config.getProperties().put(Config.CLIENT_PROTOCOL, clientProtocol);
	}

	protected void configureDataSource(Config config) {
		if (getDataSource() != null) {
			config.getObjects().put(KEW_DATASOURCE_OBJ, getDataSource());
		} else if (!StringUtils.isBlank(getDataSourceJndiName())) {
			config.getProperties().put(KEW_DATASOURCE_JNDI, getDataSourceJndiName());
		}
	}

	public String getClientProtocol() {
		return clientProtocol;
	}

	public void setClientProtocol(String clientProtocol) {
		this.clientProtocol = clientProtocol;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getDataSourceJndiName() {
		return dataSourceJndiName;
	}

	public void setDataSourceJndiName(String jndiDatasourceLocation) {
		this.dataSourceJndiName = jndiDatasourceLocation;
	}

}
