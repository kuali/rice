package org.kuali.bus.security.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import org.kuali.bus.security.credentials.SecurityUtils;
import org.kuali.rice.security.credentials.Credentials;
import org.kuali.rice.security.credentials.CredentialsSource;
import org.logicblaze.lingo.jms.JmsProxyFactoryBean;
import org.springframework.util.Assert;

import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 * Extension to JmsProxyFactoryBean that takes Authentication information from
 * the CredentialsSource and passes it along with the JMS message via the object
 * property <code>CONST_KUALI_JMS_AUTHENTICATION</code>.
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:14 $
 * @since 0.9
 */
public class AuthenticationJmsProxyFactoryBean extends JmsProxyFactoryBean {

    /**
     * Public constant defining the location of the Kuali authentication
     * information within the JMS message.
     */
    public static final String CONST_KUALI_JMS_AUTHENTICATION = "kuali.authentication";

    /** Source of the credentials to send with the JMS message. */
    private final CredentialsSource credentialsSource;

    /** Information about the service the message is being sent to. */
    private final ServiceInfo serviceInfo;

    public AuthenticationJmsProxyFactoryBean(
        final CredentialsSource credentialsSource, final ServiceInfo serviceInfo) {
        Assert.notNull(credentialsSource, "credentialsSource cannot be null.");
        Assert.notNull(serviceInfo, "serviceInfo cannot be null.");
        this.credentialsSource = credentialsSource;
        this.serviceInfo = serviceInfo;
    }

    /**
     * Utilizing the CredentialsSource, will add the Authentication information
     * as the property "authentication".
     */
    protected void populateHeaders(final Message requestMessage)
        throws JMSException {
        super.populateHeaders(requestMessage);
        final Credentials credentials = this.credentialsSource
            .getCredentials(this.serviceInfo.getEndpointUrl());

        requestMessage.setObjectProperty(CONST_KUALI_JMS_AUTHENTICATION,
            SecurityUtils.convertCredentialsToSecurityContext(credentials));
    }
}
