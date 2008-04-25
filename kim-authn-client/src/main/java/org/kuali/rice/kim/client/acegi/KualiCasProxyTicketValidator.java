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

package org.kuali.rice.kim.client.acegi;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.acegisecurity.AuthenticationServiceException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.providers.cas.TicketResponse;
import org.acegisecurity.providers.cas.ticketvalidator.CasProxyTicketValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.yale.its.tp.cas.client.ProxyTicketValidator;


/**
 * Uses CAS' <code>ProxyTicketValidator</code> to validate a service ticket.
 *  
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KualiCasProxyTicketValidator extends CasProxyTicketValidator {
    //~ Static fields/initializers =====================================================================================

    private static final Log logger = LogFactory.getLog(KualiCasProxyTicketValidator.class);

    //~ Instance fields ================================================================================================

    /**
     * Perform the actual remote invocation. Gets the <code>Authentication Method</code> from the
     * attribute list of the validator response and stores it in the TicketResponse.  Protected to 
     * enable replacement during tests.
     *
     * @param pv the populated <code>ProxyTicketValidator</code>
     *
     * @return the <code>TicketResponse</code> as a <code>KualiTicketResponse</code>
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
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(pv.getResponse()));
			Document				doc     = builder.parse(inStream);
			Element 				head = doc.getDocumentElement();
			NodeList 				attrs = head.getElementsByTagName("cas:attribute");
			for (int i=0; i<attrs.getLength(); i++) {
				logger.warn(("Field name:" + ((Element)attrs.item(i)).getAttribute("name")) + "=" + ((Element)attrs.item(i)).getAttribute("value"));
				if ( ((Element)attrs.item(i)).getAttribute("name").equals("authenticationMethod") ) {
					sAuthenticationSource = ((Element)attrs.item(i)).getAttribute("value");
				}
			}			
        } catch (Exception e) {
        	logger.warn("Error parsing CAS Result", e);
        }
        
        logger.warn("Authentication Method:" + sAuthenticationSource);
        return new KualiTicketResponse(pv.getUser(), pv.getProxyList(), pv.getPgtIou(), sAuthenticationSource);
    }
    

}
