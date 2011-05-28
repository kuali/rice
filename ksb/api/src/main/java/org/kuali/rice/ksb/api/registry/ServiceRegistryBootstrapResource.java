package org.kuali.rice.ksb.api.registry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * A REST-based service implementation which can be used to bootstrap the
 * registry and bring it online to look up other services.  It essentially
 * provides REST-based access to a list of ServiceConfigurations that can
 * be used to load the registry.
 */
@Path("/")
public interface ServiceRegistryBootstrapResource {

	@GET
    @Produces("application/xml")
    RegistryConfigurations getAllSoapServiceRegistryConfigurations();
	
}
