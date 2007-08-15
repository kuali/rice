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
// Created on Feb 27, 2006

package org.kuali.rice.config.wss4j;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;


/**
 * Workflow CryptoPasswordCallbackHandler which retrieves the keystore password
 * from the workflow Config.
 * 
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
public class CryptoPasswordCallbackHandler implements CallbackHandler {

    /**
     * The actual CallBackHandler implementation.
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                String password = Core.getCurrentContextConfig().getKeystorePassword();
                if (password == null) {
                	throw new ConfigurationException("Could not locate the webservice password.  Should be configured as the '" + Config.KEYSTORE_PASSWORD + "' property.");
                }
                pc.setPassword(password);
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
    }
}