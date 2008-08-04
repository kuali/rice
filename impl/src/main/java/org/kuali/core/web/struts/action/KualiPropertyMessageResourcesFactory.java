/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.core.web.struts.action;

import java.util.Locale;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.PropertyMessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;
import org.kuali.rice.KNSServiceLocator;

/**
 * A custom MessageResourceFactory that delegates to the KualiConfigurationService's pre-loaded properties. It will first try
 * to get properties the standard struts way, and if a property is not found, it will delegate. This then allows the use of
 * multiple ApplicationResources.properties files to be used in the order specified in configurationServiceData.xml.
 * 
 * This factory can be used in struts-config.xml files by specifying a factory attribute in the <message-resources/> tag.  
 * Example: 
 *   <message-resources 
 *       factory="org.kuali.core.web.struts.action.KualiPropertyMessageResourcesFactory" 
 *       parameter="SampleApplicationResources" />
 */
public class KualiPropertyMessageResourcesFactory extends PropertyMessageResourcesFactory {

    private static final long serialVersionUID = 9045578011738154255L;

    public MessageResources createResources(String config) {
	return new KualiPropertyMessageResources(this, config, this.returnNull);
    }

    private class KualiPropertyMessageResources extends PropertyMessageResources {

	private static final long serialVersionUID = -7712311580595112293L;

	public KualiPropertyMessageResources(MessageResourcesFactory factory, String config) {
	    super(factory, config);
	}

	public KualiPropertyMessageResources(MessageResourcesFactory factory, String config, boolean returnNull) {
	    super(factory, config, returnNull);
	}

	@Override
	public String getMessage(Locale locale, String key) {
	    String value = super.getMessage(locale, key);
	    if (value == null || value.trim().length() == 0) {
		value = KNSServiceLocator.getKualiConfigurationService().getPropertyString(key);
	    }
	    return value;
	}

    }
}
