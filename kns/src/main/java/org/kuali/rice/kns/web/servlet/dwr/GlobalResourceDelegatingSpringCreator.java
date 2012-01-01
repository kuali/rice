/**
 * Copyright 2005-2012 The Kuali Foundation
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

package org.kuali.rice.kns.web.servlet.dwr;

import org.directwebremoting.spring.SpringCreator;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.springframework.beans.factory.BeanFactory;


/**
 * A {@link SpringCreator} that checks the {@link GlobalResourceLoader} for the
 * bean name in question if the default {@link BeanFactory} (the applications)
 * does not have the bean in question.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class GlobalResourceDelegatingSpringCreator extends SpringCreator {
	
	@Override
	public Object getInstance() throws InstantiationException {
		Object bean = GlobalResourceLoader.getService(this.getBeanName());
		if (bean == null) {
			//weird DWR passes the scriptName rather than beanName - so trying the script name.
			bean = GlobalResourceLoader.getService(this.getJavascript());
			if (bean == null) {
				throw new InstantiationException("Unable to find bean " + this.getBeanName() + " in Rice Global Resource Loader");
			}
		}
		return bean;
	}

}
