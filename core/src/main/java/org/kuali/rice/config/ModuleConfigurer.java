package org.kuali.rice.config;

import org.kuali.rice.lifecycle.BaseCompositeLifecycle;

public abstract class ModuleConfigurer extends BaseCompositeLifecycle implements Configurer {

	public abstract Config loadConfig(Config parentConfig) throws Exception;

}
