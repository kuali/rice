package edu.iu.uis.eden.messaging;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import org.junit.Test;
import org.kuali.bus.test.KSBTestCase;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;

public class DigitalSignatureTest extends KSBTestCase {


	@Test public void testSigning() throws Exception {
		
		Config config = Core.getCurrentContextConfig();
//		config.parseConfig(); 
//		
		Signature rsa = Signature.getInstance("SHA1withRSA");
		String keystoreLocation = config.getKeystoreFile();
		String keystoreAlias = config.getKeystoreAlias();
		String keystorePassword = config.getKeystorePassword();
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(keystoreLocation), keystorePassword.toCharArray());
		PrivateKey privateKey = (PrivateKey)keystore.getKey(keystoreAlias, keystorePassword.toCharArray());
        
		rsa.initSign(privateKey);
		
		String imLovinIt = "Ba-da-ba-ba-baa, I'm lovin' it!";
		rsa.update(imLovinIt.getBytes());
		
		byte[] sigToVerify = rsa.sign();
		
		
		PublicKey publicKey = keystore.getCertificate(keystoreAlias).getPublicKey();
	    Signature verifySig = Signature.getInstance("SHA1withRSA");
	    verifySig.initVerify(publicKey);
	    verifySig.update(imLovinIt.getBytes());
	    boolean verifies = verifySig.verify(sigToVerify);
	    System.out.println("signature verifies: " + verifies);
		
	}
	
}
