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
package edu.iu.uis.eden.jmx;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.springframework.jmx.export.MBeanExporter;

/**
 * Exports MBeans configured from an Embedded KEW client.  By fetching them
 * from the mBeans Config parameter.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ClientMBeanExporter extends MBeanExporter {

	@Override
	public void afterPropertiesSet() {
		Map beans = (Map)Core.getCurrentContextConfig().getObject(Config.M_BEANS);
		if (beans == null) {
			beans = new HashMap();
		}
		setBeans(beans);
		super.afterPropertiesSet();
	}



}
