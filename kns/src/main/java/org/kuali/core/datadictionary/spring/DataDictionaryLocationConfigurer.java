package org.kuali.core.datadictionary.spring;

import java.util.List;

import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.config.ConfigurationException;
import org.springframework.beans.factory.InitializingBean;

/**
 * Puts a data dictionary file location in the data dictionary
 * @author rkirkend
 *
 */
public class DataDictionaryLocationConfigurer implements InitializingBean {

	private List<String> dataDictionaryPackages;
	
	public void afterPropertiesSet() throws Exception {
		if (dataDictionaryPackages == null || dataDictionaryPackages.isEmpty()) {
			throw new ConfigurationException("datatDictionaryPackages empty when initializing DataDictionaryLocation bean.");
		}
		KNSServiceLocator.getDataDictionaryService().addDataDictionaryLocations(getDataDictionaryPackages());
	}

	public List<String> getDataDictionaryPackages() {
		return dataDictionaryPackages;
	}

	public void setDataDictionaryPackages(List<String> dataDictionaryPackages) {
		this.dataDictionaryPackages = dataDictionaryPackages;
	}

}
