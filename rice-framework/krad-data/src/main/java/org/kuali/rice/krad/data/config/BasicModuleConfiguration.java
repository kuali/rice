package org.kuali.rice.krad.data.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.krad.data.provider.Provider;

/**
 * Entry point for application configuration of KRAD/KNS providers
 */
public class BasicModuleConfiguration {
	@SuppressWarnings("unchecked")
	protected List<Provider> providers = Collections.unmodifiableList(Collections.EMPTY_LIST);

    /**
     * Sets the list of providers for this module
     * @param providers list of providers
     */
    public void setProviders(List<Provider> providers) {
        this.providers = Collections.unmodifiableList(new ArrayList<Provider>(providers));
    }

    public List<Provider> getProviders() {
        return providers;
    }
}
