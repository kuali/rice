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
package org.kuali.rice.kns.service.impl;

import java.lang.reflect.Field;
import java.util.Collection;

import org.kuali.rice.kns.datadictionary.BeanOverride;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.service.BeanOverrideService;
import org.kuali.rice.kns.service.KNSServiceLocator;

import org.springframework.beans.factory.ListableBeanFactory;

/**
 * This is the base implementation of the BeanOverrideService. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class BeanOverrideServiceImpl implements BeanOverrideService {

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.BeanOverrideService#performOverrides()
	 */
	public void performOverrides() {
		ListableBeanFactory lbf = null;

		try {
			// DataDictionary does not expose the DefaultListableBeanFactory factory. Access it through reflection.
			Field field = DataDictionary.class.getDeclaredField("ddBeans");
			field.setAccessible(true);
			lbf = (ListableBeanFactory) field.get(KNSServiceLocator.getDataDictionaryService().getDataDictionary());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}

		Collection<BeanOverride> beanOverrides = lbf.getBeansOfType(BeanOverride.class).values();

		for (BeanOverride beanOverride : beanOverrides) {
			Object bean = lbf.getBean(beanOverride.getBeanName());
			beanOverride.performOverride(bean);
		}
	}

}
