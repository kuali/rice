package edu.yale.its.tp.cas.proxy;

import java.io.IOException;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:15 $
 * @since 0.9
 *
 */
public class ExtendedProxyGrantingTicket extends ProxyGrantingTicket {

	public ExtendedProxyGrantingTicket(String arg0, String arg1) {
		super(arg0, arg1);
	}

	@Override
	public String getProxyTicket(final String target) throws IOException {
		return "PT";
	}
}
