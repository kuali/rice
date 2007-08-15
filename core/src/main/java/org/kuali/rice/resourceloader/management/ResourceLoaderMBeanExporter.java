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
package org.kuali.rice.resourceloader.management;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.MBeanExporter;


public class ResourceLoaderMBeanExporter implements BeanFactoryAware, InitializingBean, DisposableBean {

	private String objectNamePrefix;
	private MBeanExporter exporter = new MBeanExporter();

	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isEmpty(this.objectNamePrefix)) {
			throw new IllegalArgumentException("The objectNamePrefix must be set.");
		}
		this.exporter.setAutodetect(false);
		ResourceLoader rootResourceLoader = GlobalResourceLoader.getResourceLoader();
		Map<String, Object> beans = new HashMap<String, Object>();

		String loaderNamePrefix = getObjectNamePrefix() + ",type=ResourceLoaders,name=";
		addResourceLoader(loaderNamePrefix, rootResourceLoader, beans);
		this.exporter.setBeans(beans);
		this.exporter.afterPropertiesSet();
	}

	protected void addResourceLoader(String namePrefix, ResourceLoader resourceLoader, Map<String, Object> beans) {
		String beanName = namePrefix + resourceLoader.getName().toString();
		beans.put(fixBeanName(beanName, beanName, beans, 0), new ResourceLoaderWrapper(resourceLoader));
		for (ResourceLoader childLoader : resourceLoader.getResourceLoaders()) {
			addResourceLoader(namePrefix, childLoader, beans);
		}
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
	    this.exporter.setBeanFactory(beanFactory);
	}

	public void destroy() throws Exception {
	    this.exporter.destroy();
	}

	public String getObjectNamePrefix() {
		return this.objectNamePrefix;
	}

	public void setObjectNamePrefix(String objectNamePrefix) {
		this.objectNamePrefix = objectNamePrefix;
	}

	protected String fixBeanName(String generatedBeanName, String actualBeanName, Map beans, int counter) {
		if (!(beans.containsKey(actualBeanName))) {
			return actualBeanName;
		}
		counter++;
		return fixBeanName(generatedBeanName, generatedBeanName+"("+counter+")", beans, counter);
	}

}
