/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.rice.kns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.authorization.KualiModuleAuthorizer;
import org.kuali.rice.kns.datadictionary.spring.DataDictionaryLocationConfigurer;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.rules.PreRulesContinuationBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleUserService;
import org.kuali.rice.kns.service.impl.KualiModuleUserServiceDefaultImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * This class is used for determining which modules are installed
 */
public class KualiModule implements InitializingBean {
//	private static final Logger LOG = Logger.getLogger(KualiModule.class);

	private String moduleId;

	private String moduleName;

	private String moduleCode;

	private KualiModuleUserService moduleUserService;

	private MaintenanceDocumentRuleBase moduleUserRule;

	private PreRulesContinuationBase moduleUserPreRules;

	private KualiModuleAuthorizer moduleAuthorizer;

	private List<String> databaseRepositoryFilePaths;

	private List<String> dataDictionaryPackages;

	private List<String> scriptConfigurationFilePaths;

	private List<String> jobNames;

	private List<String> triggerNames;
	
	private Map<Class, String> externalizableBusinessObjectImplementations;
	
	private boolean initializeDataDictionary;

	public KualiModule() {
		databaseRepositoryFilePaths = new ArrayList<String>();
		dataDictionaryPackages = new ArrayList<String>();
		scriptConfigurationFilePaths = new ArrayList<String>();
		jobNames = new ArrayList<String>();
		triggerNames = new ArrayList<String>();
	}

	public void afterPropertiesSet() throws Exception {
		if (moduleUserService == null) {
			this.moduleUserService = new KualiModuleUserServiceDefaultImpl(this.getModuleId());
		}
		moduleUserService.setModule(this);
		KNSServiceLocator.getKualiModuleService().getInstalledModules().add(this);
		if (isInitializeDataDictionary()) {
			DataDictionaryLocationConfigurer ddl = new DataDictionaryLocationConfigurer();
			ddl.setDataDictionaryPackages(getDataDictionaryPackages());
			ddl.afterPropertiesSet();
		}
		if (getDatabaseRepositoryFilePaths() != null) {
		    for (String repositoryLocation : getDatabaseRepositoryFilePaths()) {
				// Need the OJB persistence service because it is the only one ever using the database repository files
		    	KNSServiceLocator.getPersistenceServiceOjb().loadRepositoryDescriptor(repositoryLocation);
			}
		}
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public KualiModuleUserService getModuleUserService() {
		return moduleUserService;
	}

	public void setModuleUserService(KualiModuleUserService moduleUserService) {
		this.moduleUserService = moduleUserService;
	}

	public KualiModuleAuthorizer getModuleAuthorizer() {
		return moduleAuthorizer;
	}

	public void setModuleAuthorizer(KualiModuleAuthorizer moduleAuthorizer) {
		this.moduleAuthorizer = moduleAuthorizer;
		this.moduleAuthorizer.setModule(this); // link in the module for
		// reference
	}

	public PreRulesContinuationBase getModuleUserPreRules() {
		return moduleUserPreRules;
	}

	public void setModuleUserPreRules(PreRulesContinuationBase moduleUserPreRules) {
		this.moduleUserPreRules = moduleUserPreRules;
	}

	public MaintenanceDocumentRuleBase getModuleUserRule() {
		return moduleUserRule;
	}

	public void setModuleUserRule(MaintenanceDocumentRuleBase moduleUserRule) {
		this.moduleUserRule = moduleUserRule;
	}

	public List<String> getDataDictionaryPackages() {
		return dataDictionaryPackages;
	}

	public void setDataDictionaryPackages(List<String> dataDictionaryPackages) {
		this.dataDictionaryPackages = dataDictionaryPackages;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public List<String> getDatabaseRepositoryFilePaths() {
		return databaseRepositoryFilePaths;
	}

	public void setDatabaseRepositoryFilePaths(List<String> databaseRepositoryFilePaths) {
		this.databaseRepositoryFilePaths = databaseRepositoryFilePaths;
	}

	public List<String> getJobNames() {
		return jobNames;
	}

	public void setJobNames(List<String> jobNames) {
		this.jobNames = jobNames;
	}

	public List<String> getScriptConfigurationFilePaths() {
		return scriptConfigurationFilePaths;
	}

	public void setScriptConfigurationFilePaths(List<String> scriptConfigurationFilePaths) {
		this.scriptConfigurationFilePaths = scriptConfigurationFilePaths;
	}

	public List<String> getTriggerNames() {
		return triggerNames;
	}

	public void setTriggerNames(List<String> triggerNames) {
		this.triggerNames = triggerNames;
	}

	public boolean isInitializeDataDictionary() {
		return initializeDataDictionary;
	}

	public void setInitializeDataDictionary(boolean initializeDataDictionary) {
		this.initializeDataDictionary = initializeDataDictionary;
	}

	/**
	 * @return the externalizableBusinessObjectImplementations
	 */
	public Map<Class, String> getExternalizableBusinessObjectImplementations() {
		return this.externalizableBusinessObjectImplementations;
	}

	/**
	 * @param externalizableBusinessObjectImplementations the externalizableBusinessObjectImplementations to set
	 */
	public void setExternalizableBusinessObjectImplementations(
			Map<Class, String> externalizableBusinessObjectImplementations) {
		this.externalizableBusinessObjectImplementations = externalizableBusinessObjectImplementations;
	}
}