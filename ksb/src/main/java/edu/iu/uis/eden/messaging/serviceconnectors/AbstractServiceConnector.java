package edu.iu.uis.eden.messaging.serviceconnectors;

import org.kuali.rice.security.credentials.CredentialsSource;
import org.springframework.util.Assert;

import edu.iu.uis.eden.messaging.BusClientFailureProxy;
import edu.iu.uis.eden.messaging.ServiceInfo;
import edu.iu.uis.eden.messaging.bam.BAMClientProxy;

/**
 * Abstract implementation of the ServiceConnector that holds the ServiceInfo
 * and the CredentialsSource as well as providing convenience proxy methods.
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
 * @since 0.9
 * 
 */
public abstract class AbstractServiceConnector implements ServiceConnector {

	/**
	 * Maintains the information about the service.  This should never be null.
	 */
	private ServiceInfo serviceInfo;

	/**
	 * Maintains the credentials needed by the service.  This may be null.
	 */
	private CredentialsSource credentialsSource;

	public AbstractServiceConnector(final ServiceInfo serviceInfo) {
		Assert.notNull(serviceInfo, "serviceInfo cannot be null");
		this.serviceInfo = serviceInfo;
	}

	public ServiceInfo getServiceInfo() {
		return this.serviceInfo;
	}

	public void setServiceInfo(final ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	public void setCredentialsSource(final CredentialsSource credentialsSource) {
		this.credentialsSource = credentialsSource;
	}

	protected CredentialsSource getCredentialsSource() {
		return this.credentialsSource;
	}

	protected Object getServiceProxyWithFailureMode(final Object service,
			final ServiceInfo serviceInfo) {
		Object bamWrappedClientProxy = BAMClientProxy
				.wrap(service, serviceInfo);
		return BusClientFailureProxy.wrap(bamWrappedClientProxy, serviceInfo);
	}
}
