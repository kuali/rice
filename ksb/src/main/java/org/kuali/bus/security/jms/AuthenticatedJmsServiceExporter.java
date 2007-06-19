package org.kuali.bus.security.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.logicblaze.lingo.jms.JmsServiceExporter;

/**
 * Extension to the JmsServiceExporter that attempts to retrieve an
 * {@link Authentication} object from
 * {@link AuthenticationJmsProxyFactoryBean#CONST_KUALI_JMS_AUTHENTICATION} and
 * place it in an Acegi {@link SecurityContext}.
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:14 $
 * @since 0.9
 * 
 */
public class AuthenticatedJmsServiceExporter extends JmsServiceExporter {

	public void onMessage(final Message message) {

		try {
			final Authentication authentication = (Authentication) message
					.getObjectProperty(AuthenticationJmsProxyFactoryBean.CONST_KUALI_JMS_AUTHENTICATION);
			if (authentication != null) {
				final SecurityContextImpl impl = new SecurityContextImpl();
				impl.setAuthentication(authentication);
				authentication.setAuthenticated(false);
				SecurityContextHolder.setContext(impl);
			}
			super.onMessage(message);

		} catch (final JMSException e) {
			logger.error(e, e);
		} finally {
			SecurityContextHolder.clearContext();
		}
	}
}
