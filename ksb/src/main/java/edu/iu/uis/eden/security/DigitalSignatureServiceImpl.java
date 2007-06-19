package edu.iu.uis.eden.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.springframework.beans.factory.InitializingBean;

public class DigitalSignatureServiceImpl implements DigitalSignatureService, InitializingBean {

	private static final String SHA_RSA_ALGORITHM = "SHA1withRSA";
	private static final String JKS_TYPE = "JKS";

	private String keyStoreLocation;
	private String keyStoreAlias;
	private String keyStorePassword;

	private KeyStore keyStore;
	private PrivateKey privateKey;

	/**
	 * Load the keystore and private key for this "application"
	 */
	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isEmpty(this.keyStoreLocation)) {
			setKeyStoreLocation(Core.getCurrentContextConfig().getKeystoreFile());
		}
		if (StringUtils.isEmpty(this.keyStoreAlias)) {
			setKeyStoreAlias(Core.getCurrentContextConfig().getKeystoreAlias());
		}
		if (StringUtils.isEmpty(this.keyStorePassword)) {
			setKeyStorePassword(Core.getCurrentContextConfig().getKeystorePassword());
		}
		verifyConfiguration();
		this.keyStore = loadKeyStore();
		this.privateKey = loadPrivateKey();
	}

	/**
	 * Verifies the configuration of this service and throws an exception if it is not configured properly.
	 */
	protected void verifyConfiguration() {
		if (StringUtils.isEmpty(getKeyStoreLocation())) {
			throw new RuntimeException("Value for configuration parameter '" + Config.KEYSTORE_FILE + "' could not be found.  Please ensure that the keystore is configured properly.");
		}
		if (StringUtils.isEmpty(getKeyStoreAlias())) {
			throw new RuntimeException("Value for configuration parameter '" + Config.KEYSTORE_ALIAS + "' could not be found.  Please ensure that the keystore is configured properly.");
		}
		if (StringUtils.isEmpty(getKeyStorePassword())) {
			throw new RuntimeException("Value for configuration parameter '" + Config.KEYSTORE_PASSWORD + "' could not be found.  Please ensure that the keystore is configured properly.");
		}
		File keystoreFile = new File(getKeyStoreLocation());
		if (!keystoreFile.exists()) {
			throw new RuntimeException("Value for configuration parameter '" + Config.KEYSTORE_FILE + "' is invalid.  The file does not exist on the filesystem, location was: '" + getKeyStoreLocation() + "'");
		}
		if (!keystoreFile.canRead()) {
			throw new RuntimeException("Value for configuration parameter '" + Config.KEYSTORE_FILE + "' is invalid.  The file exists but is not readable (please check permissions), location was: '" + getKeyStoreLocation() + "'");
		}
	}

	public Signature getSignatureForSigning() throws IOException, GeneralSecurityException {
		Signature signature = getSignature();
		signature.initSign(this.privateKey);
		return signature;
	}

	public Signature getSignatureForVerification(String verificationAlias) throws IOException, GeneralSecurityException {
		Signature signature = getSignature();
		PublicKey publicKey = getPublicKey(verificationAlias);
		if (publicKey == null) {
			throw new KeyException("Could not find the public key for the given alias: " + verificationAlias + " in keystore " + getKeyStoreLocation());
		}
	    signature.initVerify(publicKey);
	    return signature;
	}

	public void setKeyStoreAlias(String keyStoreAlias) {
		this.keyStoreAlias = keyStoreAlias;
	}

	public void setKeyStoreLocation(String keyStoreLocation) {
		this.keyStoreLocation = keyStoreLocation;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	protected Signature getSignature() throws GeneralSecurityException {
		return Signature.getInstance(getAlgorithm());
	}

	protected KeyStore loadKeyStore() throws GeneralSecurityException, IOException {
		KeyStore keyStore = KeyStore.getInstance(getKeyStoreType());
		keyStore.load(new FileInputStream(getKeyStoreLocation()), getKeyStorePassword().toCharArray());
		return keyStore;
	}

	protected PrivateKey loadPrivateKey() throws GeneralSecurityException {
		return (PrivateKey)this.keyStore.getKey(getKeyStoreAlias(), getKeyStorePassword().toCharArray());
	}

	protected PublicKey getPublicKey(String alias) throws GeneralSecurityException {
		Certificate cert = this.keyStore.getCertificate(alias);
		if (cert == null) {
			return null;
		}
		return cert.getPublicKey();
	}

	protected String getKeyStoreType() {
		return JKS_TYPE;
	}

	protected String getAlgorithm() {
		return SHA_RSA_ALGORITHM;
	}

	protected String getKeyStoreLocation() {
		return this.keyStoreLocation;
	}

	public String getKeyStoreAlias() {
		return this.keyStoreAlias;
	}

	protected String getKeyStorePassword() {
		return this.keyStorePassword;
	}

	protected KeyStore getKeyStore() {
		return this.keyStore;
	}

}
