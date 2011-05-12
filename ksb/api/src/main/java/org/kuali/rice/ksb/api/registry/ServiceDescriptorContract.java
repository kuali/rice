package org.kuali.rice.ksb.api.registry;

import org.kuali.rice.core.api.mo.common.Versioned;

public interface ServiceDescriptorContract extends Versioned {

	Long getId();
	
	String getDescriptor();
	
}
