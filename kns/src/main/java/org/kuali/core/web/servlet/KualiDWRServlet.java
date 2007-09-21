/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.web.servlet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.KualiModule;
import org.kuali.core.util.spring.NamedOrderedListBean;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.core.io.DefaultResourceLoader;

import uk.ltd.getahead.dwr.Configuration;
import uk.ltd.getahead.dwr.DWRServlet;

import edu.iu.uis.eden.util.ClassLoaderUtils;

public class KualiDWRServlet extends DWRServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3903455224197903186L;

	private static final String CLASSPATH_RESOURCE_PREFIX = "WEB-INF/classes/";
	
	public static List<String> HACK_ADDITIONAL_FILES = new ArrayList<String>();

	private Boolean springBasedConfigPath;

	@Override
	public void init(ServletConfig config) throws ServletException {
		setSpringBasedConfigPath(new Boolean(config.getInitParameter("springpath")));
		super.init(config);
	}

	/**
	 * This method calls the super version then loads the dwr config file
	 * specified in the loaded module definitions.
	 * 
	 * @see uk.ltd.getahead.dwr.DWRServlet#configure(javax.servlet.ServletConfig,
	 *      uk.ltd.getahead.dwr.Configuration)
	 */
	@Override
	public void configure(ServletConfig servletConfig, Configuration configuration) throws ServletException {
		for (NamedOrderedListBean namedOrderedListBean : KNSServiceLocator.getNamedOrderedListBeans(RiceConstants.SCRIPT_CONFIGURATION_FILES_LIST_NAME)) {
			for (String scriptConfigurationFilePath : namedOrderedListBean.getList()) {
				if (getSpringBasedConfigPath()) {
					DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
					try {
						InputStream is = resourceLoader.getResource(scriptConfigurationFilePath).getInputStream();
						configuration.addConfig(is);
					} catch (Exception e) {
						throw new ServletException(e);
					}
				} else {
					super.readFile(CLASSPATH_RESOURCE_PREFIX + scriptConfigurationFilePath, configuration);
				}
			}
		}
		for (KualiModule module : KNSServiceLocator.getKualiModuleService().getInstalledModules()) {
			for (String scriptConfigurationFilePath : module.getScriptConfigurationFilePaths()) {
				if (!StringUtils.isBlank(scriptConfigurationFilePath))
					super.readFile(CLASSPATH_RESOURCE_PREFIX + scriptConfigurationFilePath, configuration);
			}	
		}
		
		for (String configFile : HACK_ADDITIONAL_FILES) {
			super.readFile(CLASSPATH_RESOURCE_PREFIX + configFile, configuration);
		}
	}

	public Boolean getSpringBasedConfigPath() {
		return springBasedConfigPath;
	}

	public void setSpringBasedConfigPath(Boolean springBasedConfigPath) {
		this.springBasedConfigPath = springBasedConfigPath;
	}
}
