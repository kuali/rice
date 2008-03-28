/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.kim.client.acegi;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationServiceException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.providers.cas.TicketResponse;
import org.acegisecurity.providers.cas.ticketvalidator.AbstractTicketValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.yale.its.tp.cas.client.ProxyTicketValidator;


/**
 * Uses CAS' <code>ProxyTicketValidator</code> to validate a service ticket.
 *
 * @author Ben Alex
 * @version $Id: CasProxyTicketValidator.java 2217 2007-10-27 00:45:30Z luke_t $
 */
public class KualiCasProxyTicketValidator extends AbstractTicketValidator {
    //~ Static fields/initializers =====================================================================================

    private static final Log logger = LogFactory.getLog(KualiCasProxyTicketValidator.class);

    //~ Instance fields ================================================================================================

    private String proxyCallbackUrl;

    //~ Methods ========================================================================================================

    public TicketResponse confirmTicketValid(String serviceTicket)
        throws AuthenticationException {
        // Attempt to validate presented ticket using CAS' ProxyTicketValidator class
        ProxyTicketValidator pv = new ProxyTicketValidator();

        pv.setCasValidateUrl(super.getCasValidate());
        pv.setServiceTicket(serviceTicket);
        pv.setService(super.getServiceProperties().getService());

        if (super.getServiceProperties().isSendRenew()) {
            logger.warn(
                  "The current CAS ProxyTicketValidator does not support the 'renew' property. "
                + "The ticket cannot be validated as having been issued by a 'renew' authentication. "
                + "It is expected this will be corrected in a future version of CAS' ProxyTicketValidator.");
        }

        if ((this.proxyCallbackUrl != null) && (!"".equals(this.proxyCallbackUrl))) {
            pv.setProxyCallbackUrl(proxyCallbackUrl);
        }

        return validateNow(pv);
    }

    /**
     * Optional callback URL to obtain a proxy-granting ticket from CAS.
     * <p>This callback URL belongs to the Spring Security secured application. We suggest you use
     * CAS' <code>ProxyTicketReceptor</code> servlet to receive this callback and manage the proxy-granting ticket list.
     * The callback URL is usually something like
     * <code>https://www.mycompany.com/application/casProxy/receptor</code>.
     * </p>
     * <p>If left <code>null</code>, the <code>CasAuthenticationToken</code> will not have a proxy granting
     * ticket IOU and there will be no proxy-granting ticket callback. Accordingly, the Spring Securty
     * secured application will be unable to obtain a proxy ticket to call another CAS-secured service on
     * behalf of the user. This is not really an issue for most applications.</p>
     *
     * @return the proxy callback URL, or <code>null</code> if not used
     */
    public String getProxyCallbackUrl() {
        return proxyCallbackUrl;
    }

    public void setProxyCallbackUrl(String proxyCallbackUrl) {
        this.proxyCallbackUrl = proxyCallbackUrl;
    }

    /**
     * Perform the actual remote invocation. Protected to enable replacement during tests.
     *
     * @param pv the populated <code>ProxyTicketValidator</code>
     *
     * @return the <code>TicketResponse</code>
     *
     * @throws AuthenticationServiceException if<code>ProxyTicketValidator</code> internally fails
     * @throws BadCredentialsException DOCUMENT ME!
     */
    protected TicketResponse validateNow(ProxyTicketValidator pv)
        throws AuthenticationServiceException, BadCredentialsException {
		String					sAuthenticationSource = "unknown";

        try {
            pv.validate();
        } catch (Exception internalProxyTicketValidatorProblem) {
            throw new AuthenticationServiceException(internalProxyTicketValidatorProblem.getMessage());
        }

        if (!pv.isAuthenticationSuccesful()) {
            throw new BadCredentialsException(pv.getErrorCode() + ": " + pv.getErrorMessage());
        }
        
        logger.warn("PROXY RESPONSE: " + pv.getResponse());
        
        
        try {
			DocumentBuilderFactory	factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder			builder = factory.newDocumentBuilder();
			Document				doc     = builder.parse(pv.getResponse());
			Element 				head = doc.getDocumentElement();
			NodeList 				attrs = head.getElementsByTagName("attribute");
			for (int i=0; i<attrs.getLength(); i++) {
				logger.warn(("Field name:" + ((Element)attrs.item(i)).getAttribute("name")));
				if ( ((Element)attrs.item(i)).getAttribute("name").equals("authenticationMethod") ) {
					sAuthenticationSource = ((Element)attrs.item(i)).getAttribute("value");
				}
			}
        } catch (Exception e) {
        	logger.warn("Error parsing CAS Result" + e);
        }
        
        logger.warn("Authentication Method:" + sAuthenticationSource);
        return new TicketResponse(pv.getUser(), pv.getProxyList(), pv.getPgtIou());
    }
}
