/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.config;

import java.util.LinkedList;
import java.util.List;

import org.kuali.core.KualiModule;
import org.kuali.core.authorization.KualiModuleAuthorizerBase;
import org.kuali.core.web.servlet.dwr.GlobalResourceDelegatingSpringCreator;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ModuleConfigurer;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class KNSConfigurer extends ModuleConfigurer implements BeanFactoryAware {

	private List<String> databaseRepositoryFilePaths;

	private List<String> dataDictionaryPackages;

	private boolean suppressAutoModuleConfiguration;
	
	private BeanFactory beanFactory;

	@Override
	public Config loadConfig(Config parentConfig) throws Exception {
		return null;
	}

	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		GlobalResourceDelegatingSpringCreator.APPLICATION_BEAN_FACTORY = beanFactory;
		lifecycles.add(new OJBConfigurer());
		lifecycles.add(KNSResourceLoaderFactory.createRootKNSResourceLoader());
		if (!isSuppressAutoModuleConfiguration()) {
			lifecycles.add(new Lifecycle() {
				boolean started = false;

				public boolean isStarted() {
					return this.started;
				}

				public void start() throws Exception {
					KualiModule kualiModule = new KualiModule();
					kualiModule.setDatabaseRepositoryFilePaths(getDatabaseRepositoryFilePaths());
					kualiModule.setDataDictionaryPackages(getDataDictionaryPackages());
					kualiModule.setInitializeDataDictionary(true);
					kualiModule.setModuleAuthorizer(new KualiModuleAuthorizerBase());
					kualiModule.setModuleCode(Core.getCurrentContextConfig().getMessageEntity());
					kualiModule.setModuleId(Core.getCurrentContextConfig().getMessageEntity());
					kualiModule.setModuleName(Core.getCurrentContextConfig().getMessageEntity());
					kualiModule.afterPropertiesSet();
					this.started = true;
				}

				public void stop() throws Exception {
					this.started = false;
				}
			});
		}
		return lifecycles;
	}

	public List<String> getDatabaseRepositoryFilePaths() {
		return databaseRepositoryFilePaths;
	}

	public void setDatabaseRepositoryFilePaths(List<String> databaseRepositoryFilePaths) {
		this.databaseRepositoryFilePaths = databaseRepositoryFilePaths;
	}

	public List<String> getDataDictionaryPackages() {
		return dataDictionaryPackages;
	}

	public void setDataDictionaryPackages(List<String> dataDictionaryPackages) {
		this.dataDictionaryPackages = dataDictionaryPackages;
	}

	public boolean isSuppressAutoModuleConfiguration() {
		return suppressAutoModuleConfiguration;
	}

	public void setSuppressAutoModuleConfiguration(boolean suppressAutoModuleConfiguration) {
		this.suppressAutoModuleConfiguration = suppressAutoModuleConfiguration;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}