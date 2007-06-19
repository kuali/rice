/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package org.kuali.rice.config.xfire;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.Merlin;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.security.wss4j.WSS4JInHandler;
import org.kuali.rice.config.wss4j.CryptoPasswordCallbackHandler;
import org.kuali.rice.core.Core;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.kuali.rice.util.ClassLoaderUtils;

/**
 *
 * @author rkirkend
 * @author natjohns
 */
public class WorkflowXFireWSS4JInHandler extends WSS4JInHandler {
	
	private static final Logger LOG = Logger.getLogger(WorkflowXFireWSS4JInHandler.class);

	public WorkflowXFireWSS4JInHandler() {
		this.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE);
		this.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, CryptoPasswordCallbackHandler.class.getName());
		this.setProperty(WSHandlerConstants.SIG_KEY_ID, "IssuerSerial");
		this.setProperty(WSHandlerConstants.USER, Core.getCurrentContextConfig().getKeystoreAlias());
	}

	@Override
	public Crypto loadSignatureCrypto(RequestData reqData) {		
		try {
			return new Merlin(getMerlinProperties(), ClassLoaderUtils.getDefaultClassLoader());
		} catch (Exception e) {
			throw new RiceRuntimeException(e);
		} 		
	}

	@Override
	public Crypto loadDecryptionCrypto(RequestData reqData) {
		return loadSignatureCrypto(reqData);
	}

	protected Properties getMerlinProperties() {
		Properties props = new Properties();
		props.put("org.apache.ws.security.crypto.merlin.keystore.type", "jks");
		props.put("org.apache.ws.security.crypto.merlin.keystore.password", Core.getCurrentContextConfig().getKeystorePassword());
		props.put("org.apache.ws.security.crypto.merlin.alias.password", Core.getCurrentContextConfig().getKeystorePassword());
		props.put("org.apache.ws.security.crypto.merlin.keystore.alias", Core.getCurrentContextConfig().getKeystoreAlias());
		props.put("org.apache.ws.security.crypto.merlin.file", Core.getCurrentContextConfig().getKeystoreFile());
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Using keystore location " + Core.getCurrentContextConfig().getKeystoreFile());
		}
		return props;
	}
	
}
