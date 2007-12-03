/*
 * Copyright 2007 The Kuali Foundation
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
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version $Revision: 1.4 $ $Date: 2007-12-03 02:51:29 $
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
