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